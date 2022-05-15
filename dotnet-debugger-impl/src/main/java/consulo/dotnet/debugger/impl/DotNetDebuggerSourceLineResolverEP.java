/*
 * Copyright 2013-2015 must-be.org
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

package consulo.dotnet.debugger.impl;

import consulo.dotnet.debugger.DotNetDebuggerSourceLineResolver;
import consulo.language.LanguageExtension;

/**
 * @author VISTALL
 * @since 19.07.2015
 */
public class DotNetDebuggerSourceLineResolverEP extends LanguageExtension<DotNetDebuggerSourceLineResolver>
{
	public static final DotNetDebuggerSourceLineResolverEP INSTANCE = new DotNetDebuggerSourceLineResolverEP();

	public DotNetDebuggerSourceLineResolverEP()
	{
		super("consulo.dotnet.debuggerSourceLineResolver", new DotNetDefaultDebuggerSourceLineResolver());
	}
}
