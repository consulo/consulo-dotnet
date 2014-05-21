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

package org.mustbe.consulo.dotnet.dll.vfs.builder;

import java.util.List;

import org.mustbe.consulo.dotnet.dll.vfs.builder.block.StubBlock;
import com.intellij.util.BitUtil;
import com.intellij.util.PairFunction;
import edu.arizona.cs.mbel.mbel.MethodDef;
import edu.arizona.cs.mbel.mbel.TypeDef;
import edu.arizona.cs.mbel.signature.ParamAttributes;
import edu.arizona.cs.mbel.signature.ParameterInfo;
import edu.arizona.cs.mbel.signature.ParameterSignature;
import edu.arizona.cs.mbel.signature.SignatureConstants;
import edu.arizona.cs.mbel.signature.TypeSignature;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilMethodBuilder extends MsilSharedBuilder
{
	public static void processMethod(MethodDef methodDef, final TypeDef typeDef, StubBlock block)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(".method ");
		typeToString(builder, methodDef.getSignature().getReturnType().getInnerType(), typeDef);
		builder.append(" ");
		builder.append(methodDef.getName());

		builder.append("(");

		List<ParameterSignature> parameters = methodDef.getSignature().getParameters();
		join(builder, parameters, new PairFunction<StringBuilder, ParameterSignature, Void>()
		{
			@Override
			public Void fun(StringBuilder builder, ParameterSignature parameterSignature)
			{
				ParameterInfo parameterInfo = parameterSignature.getParameterInfo();
				if(parameterInfo != null)
				{
					if(BitUtil.isSet(parameterInfo.getFlags(), ParamAttributes.Out))
					{
						builder.append("[out] ");
					}
				}

				TypeSignature typeSignature = parameterSignature;
				if(parameterSignature.getType() == SignatureConstants.ELEMENT_TYPE_TYPEONLY)
				{
					typeSignature = parameterSignature.getInnerType();
				}

				typeToString(builder, typeSignature, typeDef);

				if(parameterInfo != null)
				{
					builder.append(" ").append(parameterInfo.getName());
				}
				return null;
			}
		}, ", ");
		builder.append(")");

		StubBlock e = new StubBlock(builder, null, BRACES);

		processAttributes(e, methodDef);

		block.getBlocks().add(e);
	}
}
