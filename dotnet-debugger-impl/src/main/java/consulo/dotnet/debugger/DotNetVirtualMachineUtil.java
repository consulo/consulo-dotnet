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

package consulo.dotnet.debugger;

import com.intellij.openapi.project.Project;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.resolve.DotNetPsiSearcher;
import consulo.internal.dotnet.msil.decompiler.util.MsilHelper;
import consulo.logging.Logger;
import consulo.ui.UIAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 19.04.14
 */
public class DotNetVirtualMachineUtil
{
	private static final Logger LOG = Logger.getInstance(DotNetVirtualMachineUtil.class);

	public static void checkCallForUIThread()
	{
		if(UIAccess.isUIThread())
		{
			LOG.error(new IllegalStateException("Calls from UI thread is prohibited"));
		}
	}

	@Nonnull
	@RequiredReadAction
	public static DotNetTypeDeclaration[] findTypesByQualifiedName(@Nonnull DotNetTypeProxy typeMirror, @Nonnull DotNetDebugContext debugContext)
	{
		Project project = debugContext.getProject();
		return DotNetPsiSearcher.getInstance(project).findTypes(DotNetDebuggerUtil.getVmQName(typeMirror), debugContext.getResolveScope());
	}

	@Nonnull
	public static String formatNameWithGeneric(@Nullable DotNetTypeProxy typeMirror)
	{
		if(typeMirror == null)
		{
			return "";
		}
		StringBuilder builder = new StringBuilder();
		formatNameWithGeneric(builder, typeMirror);
		return builder.toString();
	}

	public static void formatNameWithGeneric(@Nonnull StringBuilder builder, @Nonnull DotNetTypeProxy typeMirror)
	{
		builder.append(MsilHelper.prepareForUser(DotNetDebuggerUtil.getVmQName(typeMirror.getFullName())));
	}
}
