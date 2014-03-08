package org.mustbe.consulo.ironPython.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import org.consulo.python.module.extension.PyModuleExtension;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.jetbrains.python.sdk.PythonSdkType;

/**
 * @author VISTALL
 * @since 11.02.14
 */
public class IronPythonModuleExtension extends ModuleExtensionWithSdkImpl<IronPythonModuleExtension> implements
		PyModuleExtension<IronPythonModuleExtension>
{
	public IronPythonModuleExtension(@NotNull String id, @NotNull ModifiableRootModel module)
	{
		super(id, module);
	}

	@Override
	protected Class<? extends SdkType> getSdkTypeClass()
	{
		return PythonSdkType.class;
	}
}
