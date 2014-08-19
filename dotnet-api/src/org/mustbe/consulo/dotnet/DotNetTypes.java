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

package org.mustbe.consulo.dotnet;

/**
 * @author VISTALL
 * @since 12.12.13.
 */
public interface DotNetTypes
{
	interface System
	{
		interface Runtime
		{
			interface CompilerServices
			{
				String ExtensionAttribute = "System.Runtime.CompilerServices.ExtensionAttribute";
				String IndexerName = "System.Runtime.CompilerServices.IndexerName";
			}
		}

		String Array = "System.Array";
		String Serializable = "System.Serializable";
		String Exception = "System.Exception";
		String MulticastDelegate = "System.MulticastDelegate";
		String Object = "System.Object";
		String Enum = "System.Enum";
		String ValueType = "System.ValueType";
		String IntPtr = "System.IntPtr";
		String UIntPtr = "System.UIntPtr";
		String Type = "System.Type";
		String String = "System.String";
		String Int32 = "System.Int32";
		String Boolean = "System.Boolean";
		String IDisposable = "System.IDisposable";

		String Attribute = "System.Attribute";
		String ObsoleteAttribute = "System.ObsoleteAttribute";
		String ParamArrayAttribute = "System.ParamArrayAttribute";
	}
}
