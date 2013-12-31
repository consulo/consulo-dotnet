/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.csharp.lang.psi;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.CSharpFileType;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpFileImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpUsingListImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpUsingStatementImpl;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import lombok.val;

/**
 * @author VISTALL
 * @since 30.12.13.
 */
public class CSharpFileFactory
{
	public static CSharpUsingListImpl createUsingList(@NotNull Project project, @NotNull String qName)
	{
		val fileFromText = (CSharpFileImpl) PsiFileFactory.getInstance(project).createFileFromText("dummy.cs", CSharpFileType.INSTANCE,
				"using " + qName + ";\n");

		return (CSharpUsingListImpl) fileFromText.getFirstChild();
	}

	public static CSharpUsingStatementImpl createUsingStatement(@NotNull Project project, @NotNull String qName)
	{
		val fileFromText = (CSharpFileImpl) PsiFileFactory.getInstance(project).createFileFromText("dummy.cs", CSharpFileType.INSTANCE,
				"using " + qName + ";\n");

		CSharpUsingListImpl firstChild = (CSharpUsingListImpl) fileFromText.getFirstChild();
		return firstChild.getStatements()[0];
	}
}
