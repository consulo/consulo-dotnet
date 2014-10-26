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
		interface Reflection
		{
			String AssemblyTitleAttribute = "System.Reflection.AssemblyTitleAttribute";
			String AssemblyDescriptionAttribute = "System.Reflection.AssemblyDescriptionAttribute";
			String AssemblyCompanyAttribute = "System.Reflection.AssemblyCompanyAttribute";
			String AssemblyProductAttribute = "System.Reflection.AssemblyProductAttribute";
			String AssemblyCopyrightAttribute = "System.Reflection.AssemblyCopyrightAttribute";
			String AssemblyTrademarkAttribute = "System.Reflection.AssemblyTrademarkAttribute";
			String AssemblyVersionAttribute = "System.Reflection.AssemblyVersionAttribute";
			String AssemblyFileVersionAttribute = "System.Reflection.AssemblyFileVersionAttribute";
		}

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
		String Char = "System.Char";
		String Byte = "System.Byte";
		String SByte = "System.SByte";
		String Int16 = "System.Int16";
		String UInt16 = "System.UInt16";
		String Decimal = "System.Decimal";
		String Nullable$1 = "System.Nullable`1";
		String Int32 = "System.Int32";
		String UInt32 = "System.UInt32";
		String Int64 = "System.Int64";
		String UInt64 = "System.UInt64";
		String Double = "System.Double";
		String Single = "System.Single";
		String Boolean = "System.Boolean";
		String TypedReference = "System.TypedReference";
		String IDisposable = "System.IDisposable";
		String Void = "System.Void";

		String Attribute = "System.Attribute";
		String ObsoleteAttribute = "System.ObsoleteAttribute";
		String ParamArrayAttribute = "System.ParamArrayAttribute";
	}
}
