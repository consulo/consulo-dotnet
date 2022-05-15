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

package consulo.msil.impl.representation.projectView;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.psi.PsiFile;
import consulo.msil.lang.psi.MsilFile;
import consulo.msil.representation.MsilFileRepresentationManager;
import consulo.project.Project;
import consulo.project.ui.view.tree.AbstractTreeNode;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.tree.PresentationData;
import consulo.util.lang.Pair;
import consulo.virtualFileSystem.fileType.FileType;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

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

	@RequiredReadAction
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
	@RequiredUIAccess
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
		presentation.setIcon(value.getSecond().getIcon());
	}
}
