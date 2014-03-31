package org.mustbe.consulo.ironPython.module.extension;

import javax.swing.JComponent;

import org.consulo.module.extension.MutableModuleExtensionWithSdk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author VISTALL
 * @since 11.02.14
 */
public class IronPythonMutableModuleExtension extends IronPythonModuleExtension implements MutableModuleExtensionWithSdk<IronPythonModuleExtension>
{
	public IronPythonMutableModuleExtension(@NotNull String id, @NotNull ModifiableRootModel module)
	{
		super(id, module);
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull Runnable runnable)
	{
		return createConfigurablePanelImpl(runnable);
	}

	@Override
	public void setEnabled(boolean b)
	{
		myIsEnabled = b;
	}

	@Override
	public boolean isModified(@NotNull IronPythonModuleExtension extension)
	{
		return isModifiedImpl(extension);
	}
}
