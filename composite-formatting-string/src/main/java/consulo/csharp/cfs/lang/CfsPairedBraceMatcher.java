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

package consulo.csharp.cfs.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 31.08.14
 */
public class CfsPairedBraceMatcher implements PairedBraceMatcher
{
	private static BracePair[] ourPairs = new BracePair[]{
			new BracePair(CfsTokens.START, CfsTokens.END, true)
	};

	@Override
	public BracePair[] getPairs()
	{
		return ourPairs;
	}

	@Override
	public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType elementType, @Nullable IElementType contextElement)
	{
		return contextElement != null && (contextElement == CfsTokens.START || contextElement == CfsTokens.END);
	}

	@Override
	public int getCodeConstructStart(PsiFile file, int i)
	{
		return i;
	}
}
