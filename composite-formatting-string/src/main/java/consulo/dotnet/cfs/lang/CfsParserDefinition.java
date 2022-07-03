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

package consulo.dotnet.cfs.lang;

import consulo.annotation.component.ExtensionImpl;
import consulo.dotnet.cfs.psi.CfsFile;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IFileElementType;
import consulo.language.file.FileViewProvider;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.version.LanguageVersionableParserDefinition;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 20.08.14
 */
@ExtensionImpl
public class CfsParserDefinition extends LanguageVersionableParserDefinition
{
	private static final IFileElementType FILE_ELEMENT = new IFileElementType(CfsLanguage.INSTANCE);

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return CfsLanguage.INSTANCE;
	}

	@Nonnull
	@Override
	public IFileElementType getFileNodeType()
	{
		return FILE_ELEMENT;
	}

	@Nonnull
	@Override
	public PsiElement createElement(ASTNode astNode)
	{
		return new ASTWrapperPsiElement(astNode);
	}

	@Override
	public PsiFile createFile(FileViewProvider fileViewProvider)
	{
		return new CfsFile(fileViewProvider);
	}
}
