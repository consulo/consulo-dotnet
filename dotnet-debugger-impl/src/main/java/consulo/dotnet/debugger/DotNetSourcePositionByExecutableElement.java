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

package consulo.dotnet.debugger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.annotations.RequiredReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.impl.ui.ExecutionPointHighlighter;

/**
 * @author VISTALL
 * @since 23.07.2015
 */
public class DotNetSourcePositionByExecutableElement implements XSourcePosition, ExecutionPointHighlighter.HighlighterProvider
{
	private XSourcePosition myOriginalPosition;
	private PsiElement myExecutableElement;

	public DotNetSourcePositionByExecutableElement(@Nonnull XSourcePosition originalPosition, @Nonnull PsiElement executableElement)
	{
		myOriginalPosition = originalPosition;
		myExecutableElement = executableElement;
	}

	@Nullable
	@Override
	@RequiredReadAction
	public TextRange getHighlightRange()
	{
		return myExecutableElement.getTextRange();
	}

	@Override
	public int getLine()
	{
		return myOriginalPosition.getLine();
	}

	@Override
	public int getOffset()
	{
		return myOriginalPosition.getOffset();
	}

	@Nonnull
	@Override
	public VirtualFile getFile()
	{
		return myOriginalPosition.getFile();
	}

	@Nonnull
	@Override
	public Navigatable createNavigatable(@Nonnull Project project)
	{
		return myOriginalPosition.createNavigatable(project);
	}
}
