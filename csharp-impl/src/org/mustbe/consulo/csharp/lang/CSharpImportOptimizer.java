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

package org.mustbe.consulo.csharp.lang;

import java.util.Set;
import java.util.TreeSet;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpFileFactory;
import org.mustbe.consulo.csharp.lang.psi.CSharpRecursiveElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpFileImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpUsingListImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpUsingStatementImpl;
import org.mustbe.consulo.dotnet.psi.DotNetReferenceExpression;
import com.intellij.lang.ImportOptimizer;
import com.intellij.psi.PsiFile;

/**
 * @author VISTALL
 * @since 01.01.14.
 */
public class CSharpImportOptimizer implements ImportOptimizer
{
	@Override
	public boolean supports(PsiFile psiFile)
	{
		return psiFile instanceof CSharpFileImpl;
	}

	@NotNull
	@Override
	public Runnable processFile(final PsiFile psiFile)
	{
		return new Runnable()
		{
			@Override
			public void run()
			{
				psiFile.accept(new CSharpRecursiveElementVisitor()
				{
					@Override
					public void visitUsingList(CSharpUsingListImpl list)
					{
						formatUsing(list);
					}
				});
			}
		};
	}

	private static void formatUsing(@NotNull CSharpUsingListImpl usingList)
	{
		Set<String> set = new TreeSet<String>();
		for(CSharpUsingStatementImpl statement : usingList.getStatements())
		{
			DotNetReferenceExpression namespaceReference = statement.getNamespaceReference();
			if(namespaceReference == null)  // if using dont have reference - dont format it
			{
				return;
			}
			set.add(namespaceReference.getText());
		}

		StringBuilder builder = new StringBuilder();
		for(String qName : set)
		{
			builder.append("using ").append(qName).append(";\n");
		}
		CSharpUsingListImpl usingListFromText = CSharpFileFactory.createUsingListFromText(usingList.getProject(), builder.toString());

		usingList.replace(usingListFromText);
	}
}
