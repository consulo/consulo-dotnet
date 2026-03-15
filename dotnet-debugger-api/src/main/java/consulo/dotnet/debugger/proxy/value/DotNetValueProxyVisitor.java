/*
 * Copyright 2013-2016 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License") {}
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

package consulo.dotnet.debugger.proxy.value;


/**
 * @author VISTALL
 * @since 18.04.2016
 */
public interface DotNetValueProxyVisitor
{
	default void visitStringValue(DotNetStringValueProxy proxy)
	{
	}

	default void visitNullValue(DotNetNullValueProxy proxy)
	{
	}

	default void visitArrayValue(DotNetArrayValueProxy proxy)
	{
	}

	default void visitEnumValue(DotNetEnumValueProxy proxy)
	{
	}

	default void visitObjectValue(DotNetObjectValueProxy proxy)
	{
	}

	default void visitNumberValue(DotNetNumberValueProxy proxy)
	{
	}

	default void visitBooleanValue(DotNetBooleanValueProxy proxy)
	{
	}

	default void visitCharValue(DotNetCharValueProxy proxy)
	{
	}

	default void visitStructValue(DotNetStructValueProxy proxy)
	{
	}

	default void visitErrorValue(DotNetErrorValueProxy proxy)
	{
	}
}
