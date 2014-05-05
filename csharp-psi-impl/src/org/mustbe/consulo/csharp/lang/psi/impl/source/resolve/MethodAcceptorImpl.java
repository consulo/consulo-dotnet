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

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpModifier;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpTypeUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpExpressionWithParameters;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpMethodCallExpressionImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpReferenceExpressionImpl;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetParameterListOwner;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.util.ArrayUtil2;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 07.01.14.
 */
public class MethodAcceptorImpl
{
	private static interface MethodAcceptor
	{
		boolean isAccepted(@NotNull CSharpExpressionWithParameters scope, DotNetExpression[] expressions, DotNetParameter[] parameters);
	}

	private static class SimpleMethodAcceptor implements MethodAcceptor
	{
		@Override
		public boolean isAccepted(@NotNull CSharpExpressionWithParameters scope, DotNetExpression[] expressions, DotNetParameter[] parameters)
		{
			if(expressions.length != parameters.length)
			{
				return false;
			}

			for(int i = 0; i < expressions.length; i++)
			{
				DotNetExpression expression = expressions[i];
				DotNetParameter parameter = parameters[i];
				if(expression == null)
				{
					return false;
				}

				DotNetTypeRef expressionType = expression.toTypeRef();
				DotNetTypeRef parameterType = parameter.toTypeRef(true);

				if(!CSharpTypeUtil.isInheritable(expressionType, parameterType, scope))
				{
					return false;
				}
			}

			return true;
		}
	}

	private static class MethodAcceptorWithDefaultValues implements MethodAcceptor
	{
		@Override
		public boolean isAccepted(@NotNull CSharpExpressionWithParameters scope, DotNetExpression[] expressions, DotNetParameter[] parameters)
		{
			if(expressions.length >= parameters.length)
			{
				return false;
			}

			for(int i = 0; i < parameters.length; i++)
			{
				DotNetExpression expression = ArrayUtil2.safeGet(expressions, i);
				DotNetParameter parameter = parameters[i];

				// if expression no found - but parameter have default value - it value
				if(expression == null && parameter.getInitializer() != null)
				{
					continue;
				}

				if(expression == null)
				{
					return false;
				}

				DotNetTypeRef expressionType = expression.toTypeRef();
				DotNetTypeRef parameterType = parameter.toTypeRef(true);

				if(!CSharpTypeUtil.isInheritable(expressionType, parameterType, scope))
				{
					return false;
				}
			}

			return true;
		}
	}

	private static class MethodAcceptorForExtensions implements MethodAcceptor
	{
		@Override
		public boolean isAccepted(@NotNull CSharpExpressionWithParameters scope, DotNetExpression[] expressions, DotNetParameter[] parameters)
		{
			if(parameters.length == 0 || !parameters[0].hasModifier(CSharpModifier.THIS))
			{
				return false;
			}
			if(scope instanceof CSharpMethodCallExpressionImpl)
			{
				DotNetExpression callExpression = ((CSharpMethodCallExpressionImpl) scope).getCallExpression();
				if(callExpression instanceof CSharpReferenceExpressionImpl)
				{
					PsiElement qualifier = ((CSharpReferenceExpressionImpl) callExpression).getQualifier();
					if(!(qualifier instanceof DotNetExpression))
					{
						return false;
					}

					DotNetExpression[] newExpressions = new DotNetExpression[expressions.length + 1];
					newExpressions[0] = (DotNetExpression) qualifier;
					System.arraycopy(expressions, 0, newExpressions, 1, expressions.length);

					return MethodAcceptorImpl.isAccepted(new DelegateExpressionWithParameters(scope, newExpressions), parameters);
				}
			}
			return false;
		}
	}

	private static final MethodAcceptor[] ourAcceptors = new MethodAcceptor[]{
			new SimpleMethodAcceptor(),
			new MethodAcceptorWithDefaultValues(),
			new MethodAcceptorForExtensions()
	};

	public static boolean isAccepted(CSharpExpressionWithParameters scope, DotNetParameterListOwner methodDeclaration)
	{
		DotNetParameter[] parameters = methodDeclaration.getParameters();

		return isAccepted(scope, parameters);
	}

	public static boolean isAccepted(CSharpExpressionWithParameters scope, DotNetParameter[] parameters)
	{
		DotNetExpression[] parameterExpressions = scope.getParameterExpressions();

		for(MethodAcceptor ourAcceptor : ourAcceptors)
		{
			if(ourAcceptor.isAccepted(scope, parameterExpressions, parameters))
			{
				return true;
			}
		}
		return false;
	}
}
