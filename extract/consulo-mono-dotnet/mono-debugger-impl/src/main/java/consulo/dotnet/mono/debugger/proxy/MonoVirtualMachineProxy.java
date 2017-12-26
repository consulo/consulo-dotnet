/*
 * Copyright 2013-2015 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consulo.dotnet.mono.debugger.proxy;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SmartList;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import consulo.dotnet.debugger.DotNetDebuggerUtil;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.DotNetVirtualMachineProxy;
import consulo.dotnet.debugger.proxy.value.DotNetBooleanValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetCharValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNullValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNumberValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;
import consulo.dotnet.module.extension.DotNetModuleLangExtension;
import consulo.dotnet.mono.debugger.TypeMirrorUnloadedException;
import consulo.vfs.util.ArchiveVfsUtil;
import mono.debugger.*;
import mono.debugger.request.EventRequest;
import mono.debugger.request.EventRequestManager;
import mono.debugger.request.StepRequest;

/**
 * @author VISTALL
 * @since 16.04.2015
 */
public class MonoVirtualMachineProxy implements DotNetVirtualMachineProxy
{
	private static final Logger LOGGER = Logger.getInstance(MonoVirtualMachineProxy.class);

	private final Map<Integer, AppDomainMirror> myLoadedAppDomains = ContainerUtil.newConcurrentMap();
	private final Set<StepRequest> myStepRequests = ContainerUtil.newLinkedHashSet();
	private final MultiMap<XBreakpoint, EventRequest> myBreakpointEventRequests = MultiMap.create();

	private final VirtualMachine myVirtualMachine;

	private final boolean mySupportSearchTypesBySourcePaths;
	private final boolean mySupportSearchTypesByQualifiedName;
	private final boolean mySupportSystemThreadId;

	private final ExecutorService myExecutor = AppExecutorUtil.createBoundedApplicationPoolExecutor("mono vm invoker", 1);

	public MonoVirtualMachineProxy(@NotNull VirtualMachine virtualMachine)
	{
		myVirtualMachine = virtualMachine;
		mySupportSearchTypesByQualifiedName = myVirtualMachine.isAtLeastVersion(2, 9);
		mySupportSearchTypesBySourcePaths = myVirtualMachine.isAtLeastVersion(2, 7);
		mySupportSystemThreadId = myVirtualMachine.isAtLeastVersion(2, 2);
	}

	@Override
	public void invoke(@NotNull Runnable runnable)
	{
		myExecutor.execute(() ->
		{
			try
			{
				runnable.run();
			}
			catch(VMDisconnectedException ignored)
			{
			}
			catch(Throwable e)
			{
				LOGGER.error(e);
			}
		});
	}

	@Nullable
	@Override
	public DotNetTypeProxy findType(@NotNull Project project, @NotNull String vmQName, @NotNull VirtualFile virtualFile)
	{
		try
		{
			TypeMirror typeMirror = findTypeMirror(project, virtualFile, vmQName);
			return MonoTypeProxy.of(typeMirror);
		}
		catch(TypeMirrorUnloadedException ignored)
		{
		}
		return null;
	}

	@NotNull
	@Override
	public List<DotNetThreadProxy> getThreads()
	{
		return ContainerUtil.map(myVirtualMachine.allThreads(), threadMirror -> new MonoThreadProxy(MonoVirtualMachineProxy.this, threadMirror));
	}

	@NotNull
	@Override
	public DotNetStringValueProxy createStringValue(@NotNull String value)
	{
		return MonoValueProxyUtil.wrap(myVirtualMachine.rootAppDomain().createString(value));
	}

	@NotNull
	@Override
	public DotNetCharValueProxy createCharValue(char value)
	{
		return MonoValueProxyUtil.wrap(new CharValueMirror(myVirtualMachine, value));
	}

	@NotNull
	@Override
	public DotNetBooleanValueProxy createBooleanValue(boolean value)
	{
		return MonoValueProxyUtil.wrap(new BooleanValueMirror(myVirtualMachine, value));
	}

	@NotNull
	@Override
	public DotNetNumberValueProxy createNumberValue(int tag, @NotNull Number value)
	{
		return MonoValueProxyUtil.wrap(new NumberValueMirror(myVirtualMachine, tag, value));
	}

	@NotNull
	@Override
	public DotNetNullValueProxy createNullValue()
	{
		return MonoValueProxyUtil.wrap(new NoObjectValueMirror(myVirtualMachine));
	}

	public boolean isSupportSystemThreadId()
	{
		return mySupportSystemThreadId;
	}

	public void dispose()
	{
		myExecutor.shutdown();
		myStepRequests.clear();
		myVirtualMachine.dispose();
		myBreakpointEventRequests.clear();
	}

	public void addStepRequest(@NotNull StepRequest stepRequest)
	{
		myStepRequests.add(stepRequest);
	}

	public void stopStepRequest(@NotNull StepRequest stepRequest)
	{
		stepRequest.disable();
		myStepRequests.remove(stepRequest);
	}

	public void putRequest(@NotNull XBreakpoint<?> breakpoint, @NotNull EventRequest request)
	{
		myBreakpointEventRequests.putValue(breakpoint, request);
	}

	@Nullable
	public XBreakpoint<?> findBreakpointByRequest(@NotNull EventRequest eventRequest)
	{
		for(Map.Entry<XBreakpoint, Collection<EventRequest>> entry : myBreakpointEventRequests.entrySet())
		{
			if(entry.getValue().contains(eventRequest))
			{
				return entry.getKey();
			}
		}
		return null;
	}

	public void stopBreakpointRequests(XBreakpoint<?> breakpoint)
	{
		Collection<EventRequest> eventRequests = myBreakpointEventRequests.remove(breakpoint);
		if(eventRequests == null)
		{
			return;
		}
		for(EventRequest eventRequest : eventRequests)
		{
			eventRequest.disable();
		}
		myVirtualMachine.eventRequestManager().deleteEventRequests(eventRequests);
	}

	public void stopStepRequests()
	{
		for(StepRequest stepRequest : myStepRequests)
		{
			stepRequest.disable();
		}
		myStepRequests.clear();
	}

	public EventRequestManager eventRequestManager()
	{
		return myVirtualMachine.eventRequestManager();
	}

	@NotNull
	public VirtualMachine getDelegate()
	{
		return myVirtualMachine;
	}

	@Nullable
	public TypeMirror findTypeMirror(@NotNull Project project, @NotNull final VirtualFile virtualFile, @NotNull final String vmQualifiedName) throws TypeMirrorUnloadedException
	{
		List<TypeMirror> typeMirrors = findTypeMirrors(project, virtualFile, vmQualifiedName);
		return ContainerUtil.getFirstItem(typeMirrors);
	}

	@Nullable
	private List<TypeMirror> findTypeMirrors(@NotNull Project project, @NotNull final VirtualFile virtualFile, @NotNull final String vmQualifiedName) throws TypeMirrorUnloadedException
	{
		try
		{
			List<TypeMirror> list = new SmartList<>();
			if(mySupportSearchTypesByQualifiedName)
			{
				TypeMirror[] typesByQualifiedName = myVirtualMachine.findTypesByQualifiedName(vmQualifiedName, false);
				Collections.addAll(list, typesByQualifiedName);
			}

			if(mySupportSearchTypesBySourcePaths)
			{
				TypeMirror[] typesBySourcePath = myVirtualMachine.findTypesBySourcePath(virtualFile.getPath(), SystemInfo.isFileSystemCaseSensitive);
				for(TypeMirror typeMirror : typesBySourcePath)
				{
					if(Comparing.equal(DotNetDebuggerUtil.getVmQName(typeMirror.fullName()), vmQualifiedName))
					{
						list.add(typeMirror);
					}
				}
			}

			String assemblyTitle = null;
			if(ProjectFileIndex.getInstance(project).isInLibraryClasses(virtualFile))
			{
				VirtualFile archiveRoot = ArchiveVfsUtil.getVirtualFileForArchive(virtualFile);
				if(archiveRoot != null)
				{
					assemblyTitle = archiveRoot.getNameWithoutExtension();
				}
			}
			else
			{
				Module moduleForFile = ModuleUtilCore.findModuleForFile(virtualFile, project);
				if(moduleForFile == null)
				{
					return list;
				}

				final DotNetModuleLangExtension<?> extension = ModuleUtilCore.getExtension(moduleForFile, DotNetModuleLangExtension.class);
				if(extension == null)
				{
					return list;
				}

				assemblyTitle = getAssemblyTitle(extension);
			}

			for(AppDomainMirror appDomainMirror : myLoadedAppDomains.values())
			{
				AssemblyMirror[] assemblies = appDomainMirror.assemblies();
				for(AssemblyMirror assembly : assemblies)
				{
					String assemblyName = getAssemblyName(assembly.name());
					if(Comparing.equal(assemblyTitle, assemblyName))
					{
						TypeMirror typeByQualifiedName = assembly.findTypeByQualifiedName(vmQualifiedName, false);
						if(typeByQualifiedName != null)
						{
							list.add(typeByQualifiedName);
						}
					}
				}
			}
			return list;
		}
		catch(VMDisconnectedException e)
		{
			return Collections.emptyList();
		}
	}

	@NotNull
	private static String getAssemblyTitle(@NotNull DotNetModuleLangExtension<?> extension)
	{
		return ApplicationManager.getApplication().runReadAction((Computable<String>) () ->
		{
			String assemblyTitle = extension.getAssemblyTitle();
			if(assemblyTitle != null)
			{
				return assemblyTitle;
			}
			return extension.getModule().getName();
		});
	}

	@NotNull
	private static String getAssemblyName(String name)
	{
		int i = name.indexOf(',');
		if(i == -1)
		{
			return name;
		}
		return name.substring(0, i);
	}

	public void resume()
	{
		try
		{
			myVirtualMachine.resume();
		}
		catch(NotSuspendedException ignored)
		{
		}
	}

	public void suspend()
	{
		myVirtualMachine.suspend();
	}

	@NotNull
	public List<ThreadMirror> allThreads()
	{
		return myVirtualMachine.allThreads();
	}

	public void loadAppDomain(AppDomainMirror appDomainMirror)
	{
		myLoadedAppDomains.put(appDomainMirror.id(), appDomainMirror);
	}

	public void unloadAppDomain(AppDomainMirror appDomainMirror)
	{
		myLoadedAppDomains.remove(appDomainMirror.id());
	}
}
