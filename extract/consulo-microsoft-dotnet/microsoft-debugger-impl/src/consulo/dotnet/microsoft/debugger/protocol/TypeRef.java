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

package consulo.dotnet.microsoft.debugger.protocol;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class TypeRef
{
	public String ModuleName;

	public int ClassToken;

	public String VmQName;

	public TypeRef(String moduleName, int classToken)
	{
		ModuleName = moduleName;
		ClassToken = classToken;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder("TypeRef{");
		sb.append("ModuleName=").append(ModuleName);
		sb.append(", ClassToken=").append(ClassToken);
		sb.append(", VmQName=").append(VmQName);
		sb.append('}');
		return sb.toString();
	}
}
