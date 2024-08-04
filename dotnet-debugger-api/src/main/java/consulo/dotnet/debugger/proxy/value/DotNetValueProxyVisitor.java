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

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public interface DotNetValueProxyVisitor
{
	default void visitStringValue(@Nonnull DotNetStringValueProxy proxy)
	{
	}

	default void visitNullValue(@Nonnull DotNetNullValueProxy proxy)
	{
	}

	default void visitArrayValue(@Nonnull DotNetArrayValueProxy proxy)
	{
	}

	default void visitEnumValue(@Nonnull DotNetEnumValueProxy proxy)
	{
	}

	default void visitObjectValue(@Nonnull DotNetObjectValueProxy proxy)
	{
	}

	default void visitNumberValue(@Nonnull DotNetNumberValueProxy proxy)
	{
	}

	default void visitBooleanValue(@Nonnull DotNetBooleanValueProxy proxy)
	{
	}

	default void visitCharValue(@Nonnull DotNetCharValueProxy proxy)
	{
	}

	default void visitStructValue(@Nonnull DotNetStructValueProxy proxy)
	{
	}

	default void visitErrorValue(@Nonnull DotNetErrorValueProxy proxy)
	{
	}
}
