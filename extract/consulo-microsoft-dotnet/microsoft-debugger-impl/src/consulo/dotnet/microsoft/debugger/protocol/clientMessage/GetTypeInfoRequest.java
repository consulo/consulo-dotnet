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

package consulo.dotnet.microsoft.debugger.protocol.clientMessage;

import consulo.dotnet.microsoft.debugger.protocol.TypeRef;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class GetTypeInfoRequest
{
	public TypeRef Type;

	public GetTypeInfoRequest(TypeRef typeRef)
	{
		Type = typeRef;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder("GetTypeInfoRequest{");
		sb.append("Type=").append(Type);
		sb.append('}');
		return sb.toString();
	}
}
