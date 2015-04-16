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

package org.mustbe.consulo.dotnet.debugger;

import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.VMDisconnectedException;
import mono.debugger.VirtualMachine;
import mono.debugger.request.EventRequestManager;

/**
 * @author VISTALL
 * @since 16.04.2015
 */
public class DotNetVirtualMachine
{
	private List<TypeMirror> myLoadedTypeMirrors = new LinkedList<TypeMirror>();
	private VirtualMachine myVirtualMachine;

	private boolean mySupportSearchTypesBySourcePaths;
	private boolean mySupportSearchTypesByQualifiedName;

	public DotNetVirtualMachine(@NotNull VirtualMachine virtualMachine)
	{
		myVirtualMachine = virtualMachine;
		mySupportSearchTypesByQualifiedName =  myVirtualMachine.isAtLeastVersion(2, 9);
		mySupportSearchTypesBySourcePaths =  myVirtualMachine.isAtLeastVersion(2, 7);
	}

	public void dispose()
	{
		myLoadedTypeMirrors.clear();
		myVirtualMachine.dispose();
	}

	public void loadTypeMirror(TypeMirror typeMirror)
	{
		myLoadedTypeMirrors.add(typeMirror);
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

	public TypeMirror findTypeMirror(@NotNull final VirtualFile virtualFile, @NotNull final String vmQualifiedName)
	{
		try
		{
			if(mySupportSearchTypesByQualifiedName)
			{
				TypeMirror[] typesByQualifiedName = myVirtualMachine.findTypesByQualifiedName(vmQualifiedName, false);
				return typesByQualifiedName.length == 0 ? null : typesByQualifiedName[0];
			}
			else if(mySupportSearchTypesBySourcePaths)
			{
				TypeMirror[] typesBySourcePath = myVirtualMachine.findTypesBySourcePath(virtualFile.getPath(), SystemInfo.isFileSystemCaseSensitive);
				return ContainerUtil.find(typesBySourcePath, new Condition<TypeMirror>()
				{
					@Override
					public boolean value(TypeMirror typeMirror)
					{
						return Comparing.equal(typeMirror.qualifiedName(), vmQualifiedName);
					}
				});
			}
			else
			{
				for(TypeMirror loadedTypeMirror : myLoadedTypeMirrors)
				{
					if(loadedTypeMirror.qualifiedName().equals(vmQualifiedName))
					{
						return loadedTypeMirror;
					}
				}
				return null;
			}
		}
		catch(VMDisconnectedException e)
		{
			return null;
		}
	}

	public void resume()
	{
		myVirtualMachine.resume();
	}

	@NotNull
	public List<ThreadMirror> allThreads()
	{
		return myVirtualMachine.allThreads();
	}
}
