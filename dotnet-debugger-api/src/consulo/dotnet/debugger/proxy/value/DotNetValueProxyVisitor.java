/*
 * Copyright 2013-2016 must-be.org
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

package consulo.dotnet.debugger.proxy.value;

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public interface DotNetValueProxyVisitor
{
	class Adaptor implements DotNetValueProxyVisitor
	{
		@Override
		public void visitStringValue(@NotNull DotNetStringValueProxy proxy)
		{

		}

		@Override
		public void visitNullValue(@NotNull DotNetNullValueProxy proxy)
		{

		}

		@Override
		public void visitArrayValue(@NotNull DotNetArrayValueProxy proxy)
		{

		}

		@Override
		public void visitEnumValue(@NotNull DotNetEnumValueProxy proxy)
		{

		}

		@Override
		public void visitObjectValue(@NotNull DotNetObjectValueProxy proxy)
		{

		}

		@Override
		public void visitNumberValue(@NotNull DotNetNumberValueProxy proxy)
		{

		}

		@Override
		public void visitBooleanValue(@NotNull DotNetBooleanValueProxy proxy)
		{

		}

		@Override
		public void visitCharValue(@NotNull DotNetCharValueProxy proxy)
		{

		}

		@Override
		public void visitStructValue(@NotNull DotNetStructValueProxy proxy)
		{

		}
	}

	void visitStringValue(@NotNull DotNetStringValueProxy proxy);

	void visitNullValue(@NotNull DotNetNullValueProxy proxy);

	void visitArrayValue(@NotNull DotNetArrayValueProxy proxy);

	void visitEnumValue(@NotNull DotNetEnumValueProxy proxy);

	void visitObjectValue(@NotNull DotNetObjectValueProxy proxy);

	void visitNumberValue(@NotNull DotNetNumberValueProxy proxy);

	void visitBooleanValue(@NotNull DotNetBooleanValueProxy proxy);

	void visitCharValue(@NotNull DotNetCharValueProxy proxy);

	void visitStructValue(@NotNull DotNetStructValueProxy proxy);
}
