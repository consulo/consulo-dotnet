package org.mustbe.consulo.dotnet.microsoft.module.extension;

import javax.swing.JComponent;

import org.consulo.module.extension.MutableModuleExtensionWithSdk;
import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.consulo.module.extension.ui.ModuleExtensionWithSdkPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public class MicrosoftDotNetMutableModuleExtension extends MicrosoftDotNetModuleExtension implements MutableModuleExtensionWithSdk<MicrosoftDotNetModuleExtension>
{
	private MicrosoftDotNetModuleExtension myOriginalModuleExtension;

	public MicrosoftDotNetMutableModuleExtension(@NotNull String id, @NotNull Module module, @NotNull MicrosoftDotNetModuleExtension originalModuleExtension)
	{
		super(id, module);
		myOriginalModuleExtension = originalModuleExtension;
	}

	@NotNull
	@Override
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		return (MutableModuleInheritableNamedPointer<Sdk>) super.getInheritableSdk();
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull ModifiableRootModel modifiableRootModel, @Nullable Runnable runnable)
	{
		return wrapToNorth(new ModuleExtensionWithSdkPanel(this, runnable));
	}

	@Override
	public void setEnabled(boolean b)
	{
		myIsEnabled = b;
	}

	@Override
	public boolean isModified()
	{
		return isModifiedImpl(myOriginalModuleExtension);
	}

	@Override
	public void commit()
	{
		myOriginalModuleExtension.commit(this);
	}
}
