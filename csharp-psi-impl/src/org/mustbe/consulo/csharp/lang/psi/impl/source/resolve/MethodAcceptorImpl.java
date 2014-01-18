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

import org.mustbe.consulo.csharp.lang.psi.CSharpMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpTypeUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpMethodCallExpressionImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpReferenceExpressionImpl;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;

/**
 * @author VISTALL
 * @since 07.01.14.
 */
public class MethodAcceptorImpl
{
	private static interface MethodAcceptor
	{
		boolean isAccepted(DotNetExpression[] expressions, DotNetParameter[] parameters);
	}

	private static class SimpleMethodAcceptor implements MethodAcceptor
	{
		@Override
		public boolean isAccepted(DotNetExpression[] expressions, DotNetParameter[] parameters)
		{
			if(expressions.length != parameters.length)
			{
				return false;
			}

			for(int i = 0; i < expressions.length; i++)
			{
				DotNetExpression expression = expressions[i];
				DotNetParameter parameter = parameters[i];

				DotNetTypeRef expressionType = expression.toTypeRef();
				DotNetTypeRef parameterType = parameter.toTypeRef();

				if(!CSharpTypeUtil.isInheritable(expressionType, parameterType, expression))
				{
					return false;
				}
			}

			return true;
		}
	}

	private static final MethodAcceptor[] ourAcceptors = new MethodAcceptor[] {new SimpleMethodAcceptor()};

	public static boolean isAccepted(CSharpReferenceExpressionImpl ref, CSharpMethodDeclaration methodDeclaration)
	{
		CSharpMethodCallExpressionImpl parent = (CSharpMethodCallExpressionImpl) ref.getParent();

		DotNetExpression[] parameterExpressions = parent.getParameterExpressions();
		DotNetParameter[] parameters = methodDeclaration.getParameters();
		for(MethodAcceptor ourAcceptor : ourAcceptors)
		{
			if(ourAcceptor.isAccepted(parameterExpressions, parameters))
			{
				return true;
			}
		}
		return false;
	}
}
