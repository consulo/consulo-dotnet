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

import org.mustbe.consulo.dotnet.dll.vfs.builder.block.LineStubBlock;
import org.mustbe.consulo.dotnet.dll.vfs.builder.block.StubBlock;
import edu.arizona.cs.mbel.mbel.Field;
import edu.arizona.cs.mbel.mbel.TypeDef;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilFieldBuilder extends MsilSharedBuilder
{
	public static void processField(Field field, TypeDef typeDef, StubBlock block)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(".field ");
		typeToString(builder, field.getSignature().getType(), typeDef);
		builder.append(" ");
		builder.append(field.getName());
		builder.append(";\n");

		block.getBlocks().add(new LineStubBlock(builder));
	}
}
