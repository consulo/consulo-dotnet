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

import java.util.List;

import org.jetbrains.annotations.Nullable;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class TypeRef
{
	public int ModuleNameId;

	public int ClassToken;

	public String VmQName;

	public boolean IsPointer;

	public boolean IsByRef;

	@Nullable
	public List<Integer> ArraySizes;

	@Nullable
	public List<Integer> ArrayLowerBounds;

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder("TypeRef{");
		sb.append("ModuleNameId='").append(ModuleNameId).append('\'');
		sb.append(", ClassToken=").append(ClassToken);
		sb.append(", VmQName='").append(VmQName).append('\'');
		sb.append(", IsPointer=").append(IsPointer);
		sb.append(", IsByRef=").append(IsByRef);
		sb.append(", ArraySizes=").append(ArraySizes);
		sb.append(", ArrayLowerBounds=").append(ArrayLowerBounds);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		if(o == null || getClass() != o.getClass())
		{
			return false;
		}

		TypeRef typeRef = (TypeRef) o;

		if(ClassToken != typeRef.ClassToken)
		{
			return false;
		}
		if(IsPointer != typeRef.IsPointer)
		{
			return false;
		}
		if(IsByRef != typeRef.IsByRef)
		{
			return false;
		}
		if(ModuleNameId != typeRef.ModuleNameId)
		{
			return false;
		}
		if(VmQName != null ? !VmQName.equals(typeRef.VmQName) : typeRef.VmQName != null)
		{
			return false;
		}
		if(ArraySizes != null ? !ArraySizes.equals(typeRef.ArraySizes) : typeRef.ArraySizes != null)
		{
			return false;
		}
		if(ArrayLowerBounds != null ? !ArrayLowerBounds.equals(typeRef.ArrayLowerBounds) : typeRef.ArrayLowerBounds != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = ModuleNameId;
		result = 31 * result + ClassToken;
		result = 31 * result + (VmQName != null ? VmQName.hashCode() : 0);
		result = 31 * result + (IsPointer ? 1 : 0);
		result = 31 * result + (IsByRef ? 1 : 0);
		result = 31 * result + (ArraySizes != null ? ArraySizes.hashCode() : 0);
		result = 31 * result + (ArrayLowerBounds != null ? ArrayLowerBounds.hashCode() : 0);
		return result;
	}
}
