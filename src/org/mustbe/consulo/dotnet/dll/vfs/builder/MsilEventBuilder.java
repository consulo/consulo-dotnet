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

import org.mustbe.consulo.dotnet.dll.vfs.builder.block.StubBlock;
import edu.arizona.cs.mbel.mbel.Event;
import edu.arizona.cs.mbel.mbel.MethodDef;
import edu.arizona.cs.mbel.mbel.TypeDef;
import edu.arizona.cs.mbel.signature.EventAttributes;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilEventBuilder extends MsilSharedBuilder implements EventAttributes
{
	public static void processEvent(Event event, TypeDef typeDef, StubBlock e)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(".event ");
		toStringFromDefRefSpec(builder, event.getEventType(), typeDef);
		builder.append(" ");
		appendValidName(builder, event.getName());

		StubBlock e1 = new StubBlock(builder, null, StubBlock.BRACES);
		processAttributes(e1, event);

		MethodDef addOnMethod = event.getAddOnMethod();
		if(addOnMethod != null)
		{
			appendAccessor(".addon", typeDef, addOnMethod, e1);
		}

		MethodDef removeOnMethod = event.getRemoveOnMethod();
		if(removeOnMethod != null)
		{
			appendAccessor(".removeon", typeDef, removeOnMethod, e1);
		}

		e.getBlocks().add(e1);
	}
}
