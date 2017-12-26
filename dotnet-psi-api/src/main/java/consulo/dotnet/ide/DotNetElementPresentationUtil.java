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

package consulo.dotnet.ide;

import org.jetbrains.annotations.NotNull;
import consulo.dotnet.psi.DotNetConstructorDeclaration;
import consulo.dotnet.psi.DotNetFieldDeclaration;
import consulo.dotnet.psi.DotNetGenericParameter;
import consulo.dotnet.psi.DotNetGenericParameterListOwner;
import consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import consulo.dotnet.psi.DotNetNamedElement;
import consulo.dotnet.psi.DotNetParameter;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.BitUtil;
import com.intellij.util.Function;
import consulo.internal.dotnet.msil.decompiler.util.MsilHelper;

/**
 * @author VISTALL
 * @since 29.12.13.
 */
public class DotNetElementPresentationUtil
{
	@NotNull
	public static <T extends DotNetGenericParameterListOwner & DotNetNamedElement> String formatTypeWithGenericParameters(@NotNull T el)
	{
		DotNetGenericParameter[] genericParameters = el.getGenericParameters();
		String name = el.getName();
		if(genericParameters.length == 0)
		{
			return name == null ? "<null>" : MsilHelper.cutGenericMarker(name);
		}

		StringBuilder builder = new StringBuilder();
		builder.append(name == null ? "<null>" : MsilHelper.cutGenericMarker(name));
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

	@Deprecated
	public static final int METHOD_SCALA_FORMAT = 1 << 0;
	@Deprecated
	public static final int METHOD_WITH_RETURN_TYPE = 1 << 1;
	@Deprecated
	public static final int METHOD_PARAMETER_NAME = 1 << 2;

	public static final int METHOD_SCALA_LIKE_FULL = METHOD_SCALA_FORMAT | METHOD_WITH_RETURN_TYPE | METHOD_PARAMETER_NAME;

	@NotNull
	@Deprecated
	public static String formatMethod(@NotNull DotNetLikeMethodDeclaration methodDeclaration, int flags)
	{
		StringBuilder builder = new StringBuilder();

		if(BitUtil.isSet(flags, METHOD_WITH_RETURN_TYPE) && !BitUtil.isSet(flags, METHOD_SCALA_FORMAT))
		{
			if(!(methodDeclaration instanceof DotNetConstructorDeclaration))
			{
				builder.append(methodDeclaration.getReturnTypeRef().getPresentableText()).append(" ");
			}
		}

		if(methodDeclaration instanceof DotNetConstructorDeclaration && ((DotNetConstructorDeclaration) methodDeclaration).isDeConstructor())
		{
			builder.append("~");
		}

		builder.append(methodDeclaration.getName());
		formatTypeGenericParameters(methodDeclaration.getGenericParameters(), builder);
		formatParameters(methodDeclaration, builder, flags);
		return builder.toString();
	}

	@NotNull
	@Deprecated
	public static String formatField(@NotNull DotNetFieldDeclaration fieldDeclaration)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(fieldDeclaration.getName());
		builder.append(":");
		builder.append(fieldDeclaration.toTypeRef(true).getPresentableText());
		return builder.toString();
	}

	@Deprecated
	private static void formatParameters(@NotNull DotNetLikeMethodDeclaration methodDeclaration, @NotNull StringBuilder builder, final int flags)
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
					if(!BitUtil.isSet(flags, METHOD_PARAMETER_NAME))
					{
						return parameter.toTypeRef(true).getPresentableText();
					}

					if(BitUtil.isSet(flags, METHOD_SCALA_FORMAT))
					{
						return parameter.getName() + ":" + parameter.toTypeRef(true).getPresentableText();
					}
					else
					{
						return parameter.toTypeRef(true).getPresentableText() + " " + parameter.getName();
					}
				}
			}, ", "));
			builder.append(")");
		}

		if(BitUtil.isSet(flags, METHOD_WITH_RETURN_TYPE) && BitUtil.isSet(flags, METHOD_SCALA_FORMAT))
		{
			if(!(methodDeclaration instanceof DotNetConstructorDeclaration))
			{
				builder.append(":").append(methodDeclaration.getReturnTypeRef().getPresentableText());
			}
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
}
