/*
 * Copyright 2013-2017 consulo.io
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

package consulo.msbuild.solution;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xml.DomElement;

/**
 * @author VISTALL
 * @since 28-Jan-17
 */
public class SolutionVirtualFile extends SolutionVirtualItem
{
	@Nullable
	private VirtualFile myVirtualFile;
	@Nullable
	private SolutionVirtualFileSubType mySubType;
	@Nullable
	private String myDependentUpon;
	@Nullable
	private String myGenerator;

	private boolean myGenerated;

	public SolutionVirtualFile(@NotNull String name, @Nullable SolutionVirtualDirectory parent, @Nullable DomElement element, @Nullable VirtualFile virtualFile)
	{
		super(name, parent, element);
		myVirtualFile = virtualFile;
	}

	public SolutionVirtualFile setGenerated(boolean generated)
	{
		myGenerated = generated;
		return this;
	}

	public boolean isGenerated()
	{
		return myGenerated;
	}

	public SolutionVirtualFile setGenerator(@Nullable String generator)
	{
		myGenerator = generator;
		return this;
	}

	@Nullable
	public String getGenerator()
	{
		return myGenerator;
	}

	public SolutionVirtualFile setSubType(@Nullable SolutionVirtualFileSubType subType)
	{
		mySubType = subType;
		return this;
	}

	@Nullable
	public SolutionVirtualFileSubType getSubType()
	{
		return mySubType;
	}

	@Nullable
	public VirtualFile getVirtualFile()
	{
		return myVirtualFile;
	}

	public SolutionVirtualFile setDependentUpon(@Nullable String dependentUpon)
	{
		myDependentUpon = dependentUpon;
		return this;
	}

	@Nullable
	public String getDependentUpon()
	{
		return myDependentUpon;
	}
}
