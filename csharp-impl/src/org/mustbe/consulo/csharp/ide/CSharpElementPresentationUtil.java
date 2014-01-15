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

package org.mustbe.consulo.csharp.ide;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpRecursiveElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpArrayTypeImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpNativeTypeImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpPointerTypeImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpTypeWrapperWithTypeArgumentsImpl;
import org.mustbe.consulo.dotnet.psi.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Function;
import lombok.val;

/**
 * @author VISTALL
 * @since 29.12.13.
 */
public class CSharpElementPresentationUtil
{
	@NotNull
	public static String formatTypeWithGenericParameters(@NotNull DotNetTypeDeclaration typeDeclaration)
	{
		DotNetGenericParameter[] genericParameters = typeDeclaration.getGenericParameters();
		String name = typeDeclaration.getName();
		if(genericParameters.length == 0)
		{
			return name == null ? "<null>" : name;
		}

		StringBuilder builder = new StringBuilder();
		builder.append(name == null ? "<null>" : name);
		formatTypeGenericParameters(genericParameters, builder);
		return builder.toString();
	}

	public static void formatTypeGenericParameters(@NotNull DotNetGenericParameter[] parameters, @NotNull StringBuilder builder)
	{
		if(parameters.length > 0)
		{
			builder.append("<");
			builder.append(StringUtil.join(parameters, new Function<DotNetGenericParameter, String>()
			{
				@Override
				public String fun(DotNetGenericParameter dotNetGenericParameter)
				{
					return dotNetGenericParameter.getName();
				}
			}, ", "));
			builder.append(">");
		}
	}

	@NotNull
	public static String formatMethod(@NotNull DotNetMethodDeclaration methodDeclaration)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(methodDeclaration.getName());
		formatTypeGenericParameters(methodDeclaration.getGenericParameters(), builder);
		formatParameters(methodDeclaration, builder);
		return builder.toString();
	}

	@NotNull
	public static String formatField(@NotNull DotNetFieldDeclaration fieldDeclaration)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(fieldDeclaration.getName());
		builder.append(":");
		builder.append(formatType(fieldDeclaration.getType()));
		return builder.toString();
	}

	private static void formatParameters(@NotNull DotNetMethodDeclaration methodDeclaration, @NotNull StringBuilder builder)
	{
		DotNetParameter[] parameters = methodDeclaration.getParameters();
		if(parameters.length == 0)
		{
			builder.append("()");
		}
		else
		{
			builder.append("(");
			builder.append(StringUtil.join(parameters, new Function<DotNetParameter, String>()
			{
				@Override
				public String fun(DotNetParameter parameter)
				{
					return parameter.getName() + ":" + formatType(parameter.getType());
				}
			}, ", "));
			builder.append(")");
		}

		if(!(methodDeclaration instanceof DotNetConstructorDeclaration))
		{
			builder.append(":").append(formatType(methodDeclaration.getReturnType()));
		}
	}

	@NotNull
	public static String formatGenericParameters(@NotNull DotNetGenericParameterListOwner owner)
	{
		DotNetGenericParameter[] genericParameters = owner.getGenericParameters();
		if(genericParameters.length == 0)
		{
			return "";
		}
		return "<" + StringUtil.join(genericParameters, new Function<DotNetGenericParameter, String>()
		{
			@Override
			public String fun(DotNetGenericParameter genericParameter)
			{
				return genericParameter.getName();
			}
		}, ", ") + ">";
	}

	public static String formatType(DotNetType type)
	{
		if(type == null)
		{
			return "<null>";
		}

		val builder = new StringBuilder();
		type.accept(new CSharpRecursiveElementVisitor()
		{
			@Override
			public void visitReferenceType(DotNetReferenceType type)
			{
				DotNetReferenceExpression referenceExpression = type.getReferenceExpression();
				if(referenceExpression == null)
				{
					builder.append("<null>");
				}
				else
				{
					builder.append(referenceExpression.getReferenceName());
				}
			}

			@Override
			public void visitTypeWrapperWithTypeArguments(CSharpTypeWrapperWithTypeArgumentsImpl typeArguments)
			{
				builder.append(formatType(typeArguments.getInnerType()));

				DotNetType[] arguments = typeArguments.getArguments();
				if(arguments.length > 0)
				{
					builder.append("<");
					for(int i = 0; i < arguments.length; i++)
					{
						DotNetType argument = arguments[i];
						if(i != 0)
						{
							builder.append(", ");
						}
						builder.append(formatType(argument));
					}
					builder.append(">");
				}
			}

			@Override
			public void visitNativeType(CSharpNativeTypeImpl type)
			{
				builder.append(type.getText());
			}

			@Override
			public void visitPointerType(CSharpPointerTypeImpl type)
			{
				super.visitPointerType(type);
				builder.append("*");
			}

			@Override
			public void visitArrayType(CSharpArrayTypeImpl type)
			{
				super.visitArrayType(type);
				builder.append("[]");
			}
		});
		return builder.toString();
	}
}
