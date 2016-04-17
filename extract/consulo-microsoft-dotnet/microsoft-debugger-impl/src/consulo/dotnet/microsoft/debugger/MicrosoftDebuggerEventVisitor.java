package consulo.dotnet.microsoft.debugger;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.mustbe.consulo.dotnet.debugger.linebreakType.DotNetBreakpointUtil;
import org.mustbe.consulo.dotnet.module.DotNetAssemblyUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import consulo.dotnet.microsoft.debugger.protocol.clientMessage.InsertBreakpointRequest;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.InsertBreakpointRequestResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.OnBreakpointFire;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.OnEventVisitor;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.OnModuleLoadEvent;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
class MicrosoftDebuggerEventVisitor extends OnEventVisitor
{
	private MicrosoftDebuggerProcessImpl myDebuggerProcess;

	public MicrosoftDebuggerEventVisitor(MicrosoftDebuggerProcessImpl debuggerProcess)
	{
		myDebuggerProcess = debuggerProcess;
	}

	@Override
	public boolean visitOnModuleLoad(OnModuleLoadEvent event, final MicrosoftDebuggerClientContext context)
	{
		File file = new File(event.ModuleFile);

		final String nameWithoutExtension = FileUtilRt.getNameWithoutExtension(file.getName());

		final Project project = myDebuggerProcess.getSession().getProject();

		Map<XLineBreakpoint, InsertBreakpointRequest> map = ApplicationManager.getApplication().runReadAction(new Computable<Map<XLineBreakpoint, InsertBreakpointRequest>>()
		{
			@Override
			public Map<XLineBreakpoint, InsertBreakpointRequest> compute()
			{
				ModuleManager moduleManager = ModuleManager.getInstance(project);
				Module targetModule = null;
				for(Module module : moduleManager.getModules())
				{
					String assemblyTitle = DotNetAssemblyUtil.getAssemblyTitle(module);
					if(assemblyTitle != null && assemblyTitle.equals(nameWithoutExtension))
					{
						targetModule = module;
						break;
					}
				}

				if(targetModule == null)
				{
					return Collections.emptyMap();
				}
				Map<XLineBreakpoint, InsertBreakpointRequest> map = new HashMap<XLineBreakpoint, InsertBreakpointRequest>();
				for(XLineBreakpoint<?> breakpoint : myDebuggerProcess.getEnabledBreakpoints())
				{
					VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(breakpoint.getFileUrl());
					if(fileByUrl == null)
					{
						continue;
					}

					Module moduleForFile = ModuleUtilCore.findModuleForFile(fileByUrl, project);
					if(!targetModule.equals(moduleForFile))
					{
						continue;
					}

					map.put(breakpoint, new InsertBreakpointRequest(FileUtil.toSystemDependentName(fileByUrl.getPath()), breakpoint.getLine()));
				}
				return map;
			}
		});

		for(Map.Entry<XLineBreakpoint, InsertBreakpointRequest> entry : map.entrySet())
		{
			final XLineBreakpoint key = entry.getKey();
			InsertBreakpointRequest value = entry.getValue();

			final InsertBreakpointRequestResult result = context.sendAndReceive(value);
			UIUtil.invokeLaterIfNeeded(new Runnable()
			{
				@Override
				public void run()
				{
					DotNetBreakpointUtil.updateBreakpointPresentation(project, result.Status == 0, key);
				}
			});
		}

		return true;
	}

	@Override
	public boolean visitOnBreakpointFire(final OnBreakpointFire event, MicrosoftDebuggerClientContext context)
	{
		XBreakpoint<?> breakpoint = ApplicationManager.getApplication().runReadAction(new Computable<XBreakpoint<?>>()
		{
			@Override
			public XBreakpoint<?> compute()
			{
				VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(event.FilePath);
				if(fileByPath == null)
				{
					return null;
				}

				for(XLineBreakpoint<?> breakpoint : myDebuggerProcess.getEnabledBreakpoints())
				{
					VirtualFile breakpointFile = VirtualFileManager.getInstance().findFileByUrl(breakpoint.getFileUrl());
					if(breakpointFile == null || !fileByPath.equals(breakpointFile))
					{
						continue;
					}

					int i = breakpoint.getLine() - 1;
					if(i == event.Line)
					{
						return breakpoint;
					}
				}
				return null;
			}
		});

		MicrosoftSuspendContext suspendContext = new MicrosoftSuspendContext(context);
		XDebugSession session = myDebuggerProcess.getSession();
		if(breakpoint != null)
		{
			session.breakpointReached(breakpoint, null, suspendContext);
		}
		else
		{
			session.positionReached(suspendContext);
		}
		return false;
	}
}
