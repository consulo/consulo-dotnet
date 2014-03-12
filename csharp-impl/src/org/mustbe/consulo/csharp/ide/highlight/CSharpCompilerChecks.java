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

package org.mustbe.consulo.csharp.ide.highlight;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.Map;

import org.mustbe.consulo.csharp.ide.highlight.check.CompilerCheck;
import org.mustbe.consulo.csharp.ide.highlight.check.CompilerCheckEx;
import org.mustbe.consulo.csharp.ide.highlight.check.CompilerCheckForWithNameArgument;
import org.mustbe.consulo.csharp.ide.highlight.check.SimpleCompilerCheck;
import org.mustbe.consulo.csharp.lang.psi.CSharpInheritUtil;
import org.mustbe.consulo.csharp.lang.psi.CSharpMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpThrowStatementImpl;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.csharp.lang.psi.CSharpModifier;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetParameterList;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.util.ArrayUtil2;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Processor;

/**
 * @author VISTALL
 * @since 09.03.14
 */
public interface CSharpCompilerChecks
{
	CompilerCheck<CSharpThrowStatementImpl> CS0155 = SimpleCompilerCheck.of(HighlightInfoType.ERROR, new Processor<CSharpThrowStatementImpl>()
	{
		@Override
		public boolean process(CSharpThrowStatementImpl statement)
		{
			DotNetExpression expression = statement.getExpression();
			if(expression == null)
			{
				return false;
			}

			DotNetTypeRef dotNetTypeRef = expression.toTypeRef();

			return !CSharpInheritUtil.isParentOrSelf(DotNetTypes.System_Exception, dotNetTypeRef, statement, true);
		}
	});

	CompilerCheck<DotNetParameter> CS0231 = SimpleCompilerCheck.of(HighlightInfoType.ERROR, new Processor<DotNetParameter>()
	{
		@Override
		public boolean process(DotNetParameter dotNetParameter)
		{
			if(!dotNetParameter.hasModifier(CSharpModifier.PARAMS))
			{
				return false;
			}

			DotNetParameterList parent = (DotNetParameterList) dotNetParameter.getParent();

			DotNetParameter[] parameters = parent.getParameters();

			return ArrayUtil.getLastElement(parameters) != dotNetParameter;
		}
	});

	CompilerCheck<CSharpMethodDeclaration> CS1100 = CompilerCheckForWithNameArgument.of(HighlightInfoType.ERROR, new Processor<CSharpMethodDeclaration>()
	{
		@Override
		public boolean process(CSharpMethodDeclaration methodDeclaration)
		{
			DotNetParameter[] parameters = methodDeclaration.getParameters();
			for(int i = 0; i < parameters.length; i++)
			{
				DotNetParameter parameter = parameters[i];
				if(i == 0)
				{
					continue;
				}

				if(parameter.hasModifier(CSharpModifier.THIS))
				{
					return true;
				}
			}
			return false;
		}
	});

	CompilerCheck<DotNetParameter> CS1737 = SimpleCompilerCheck.of(HighlightInfoType.ERROR, new Processor<DotNetParameter>()
	{
		@Override
		public boolean process(DotNetParameter dotNetParameter)
		{
			if(dotNetParameter.getInitializer() == null)
			{
				return false;
			}

			DotNetParameterList parent = (DotNetParameterList) dotNetParameter.getParent();

			DotNetParameter[] parameters = parent.getParameters();

			int i = ArrayUtil.indexOf(parameters, dotNetParameter);

			DotNetParameter nextParameter = ArrayUtil2.safeGet(parameters, i + 1);
			return nextParameter != null && nextParameter.getInitializer() == null;
		}
	});

	Map<CompilerCheck<PsiElement>, Class<?>> ourValues = new LinkedHashMap<CompilerCheck<PsiElement>, Class<?>>();

	Object ourInitializer = new Object()
	{
		{
			for(Field field : CSharpCompilerChecks.class.getFields())
			{
				if(field.getType() == Map.class || field.getType() == Object.class)
				{
					continue;
				}

				ParameterizedType genericType = (ParameterizedType) field.getGenericType();
				try
				{
					CompilerCheckEx<PsiElement> o = (CompilerCheckEx<PsiElement>) field.get(null);
					o.setId(field.getName());

					Class<?> type = (Class<?>) genericType.getActualTypeArguments()[0];
					ourValues.put(o, type);
				}
				catch(IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	};
}
