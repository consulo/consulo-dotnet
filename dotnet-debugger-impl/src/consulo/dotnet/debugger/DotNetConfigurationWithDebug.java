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

package consulo.dotnet.debugger;

import org.jetbrains.annotations.NotNull;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.xdebugger.XDebugSession;
import consulo.dotnet.execution.DebugConnectionInfo;

/**
 * @author VISTALL
 * @since 27-Dec-16
 */
public interface DotNetConfigurationWithDebug extends RunConfiguration
{
	default boolean canRun()
	{
		return true;
	}

	@NotNull
	DotNetDebugProcessBase createDebuggerProcess(@NotNull XDebugSession session, @NotNull DebugConnectionInfo debugConnectionInfo) throws ExecutionException;
}