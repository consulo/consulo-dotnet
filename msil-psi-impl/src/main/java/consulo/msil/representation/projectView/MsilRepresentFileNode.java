/*
 * Copyright 2013-2014 must-be.org
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

package consulo.msil.representation.projectView;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import consulo.annotations.RequiredDispatchThread;
import consulo.awt.TargetAWT;
import consulo.msil.lang.psi.MsilFile;
import consulo.msil.representation.MsilFileRepresentationManager;

/**
 * @author VISTALL
 * @since 27.05.14
 */
public class MsilRepresentFileNode extends AbstractTreeNode<Pair<String, ? extends FileType>>
{
	private final MsilFile myPsiFile;

	public MsilRepresentFileNode(Project project, MsilFile psiFile, Pair<String, ? extends FileType> value)
	{
		super(project, value);
		myPsiFile = psiFile;
	}

	@Nonnull
	@Override
	public Collection<? extends AbstractTreeNode> getChildren()
	{
		return Collections.emptyList();
	}

	@Override
	public boolean canNavigate()
	{
		return true;
	}

	@Override
	public boolean canRepresent(Object element)
	{
		return false;
	}

	@Override
	@RequiredDispatchThread
	public void navigate(boolean requestFocus)
	{
		PsiFile representationFile = MsilFileRepresentationManager.getInstance(getProject()).getRepresentationFile(getValue().getSecond(),
				myPsiFile);
		if(representationFile == null)
		{
			return;
		}
		representationFile.navigate(requestFocus);
	}

	@Override
	protected void update(PresentationData presentation)
	{
		Pair<String, ? extends FileType> value = getValue();

		presentation.setPresentableText(value.getFirst());
		presentation.setIcon(TargetAWT.to(value.getSecond().getIcon()));
	}
}
