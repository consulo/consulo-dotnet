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

package consulo.msbuild.projectView;

import org.jetbrains.annotations.NotNull;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import consulo.msbuild.solution.SolutionVirtualFile;

/**
 * @author VISTALL
 * @since 29-Jan-17
 */
public class SolutionViewFileNode extends PsiFileNode
{
	private SolutionVirtualFile mySolutionVirtualFile;
	private boolean myRestrictPatcher;

	public SolutionViewFileNode(Project project, PsiFile value, ViewSettings viewSettings, SolutionVirtualFile solutionVirtualFile, boolean restrictPatcher)
	{
		super(project, value, viewSettings);
		mySolutionVirtualFile = solutionVirtualFile;
		myRestrictPatcher = restrictPatcher;
	}

	public boolean isRestrictPatcher()
	{
		return myRestrictPatcher;
	}

	@NotNull
	public SolutionVirtualFile getSolutionVirtualFile()
	{
		return mySolutionVirtualFile;
	}
}
