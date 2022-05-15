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

package consulo.dotnet.cfs.ide.highlight;

import consulo.annotation.access.RequiredReadAction;
import consulo.codeEditor.Editor;
import consulo.dotnet.cfs.psi.CfsFile;
import consulo.dotnet.cfs.psi.CfsItem;
import consulo.language.editor.highlight.usage.HighlightUsagesHandlerBase;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiReference;
import consulo.util.collection.SmartList;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author VISTALL
 * @since 28.03.2015
 */
public class CfsItemHighlightUsagesFromItemHandler extends HighlightUsagesHandlerBase<CfsItem>
{
	private final CfsItem myCfsItem;

	public CfsItemHighlightUsagesFromItemHandler(Editor editor, PsiFile file, CfsItem cfsItem)
	{
		super(editor, file);
		myCfsItem = cfsItem;
	}

	@Override
	public List<CfsItem> getTargets()
	{
		CfsFile containingFile = (CfsFile) myCfsItem.getContainingFile();
		int index = myCfsItem.getIndex();
		if(index == -1)
		{
			return Collections.emptyList();
		}
		List<CfsItem> list = new SmartList<CfsItem>();
		for(CfsItem cfsItem : containingFile.getItems())
		{
			if(cfsItem.getIndex() == index)
			{
				list.add(cfsItem);
			}
		}
		return list;
	}

	@Override
	protected void selectTargets(List<CfsItem> targets, Consumer<List<CfsItem>> selectionConsumer)
	{
		selectionConsumer.accept(targets);
	}

	@Override
	@RequiredReadAction
	public void computeUsages(List<CfsItem> targets)
	{
		for(CfsItem target : targets)
		{
			CfsItemHighlightUsagesHandlerFactory.addOccurrence(myReadUsages, target);
			PsiReference reference = target.getReference();
			if(reference == null)
			{
				continue;
			}
			PsiElement element = reference.resolve();
			if(element == null)
			{
				continue;
			}
			CfsItemHighlightUsagesHandlerFactory.addOccurrence(myWriteUsages, element);
		}
	}
}
