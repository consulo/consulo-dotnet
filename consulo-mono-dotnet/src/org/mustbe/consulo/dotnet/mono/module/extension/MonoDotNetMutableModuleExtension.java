package org.mustbe.consulo.dotnet.mono.module.extension;

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
public class MonoDotNetMutableModuleExtension extends MonoDotNetModuleExtension implements MutableModuleExtensionWithSdk<MonoDotNetModuleExtension>
{
	private MonoDotNetModuleExtension myOriginalModuleExtension;

	public MonoDotNetMutableModuleExtension(@NotNull String id, @NotNull Module module, @NotNull MonoDotNetModuleExtension originalModuleExtension)
	{
		super(id, module);
		myOriginalModuleExtension = originalModuleExtension;
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull ModifiableRootModel modifiableRootModel, @Nullable Runnable runnable)
	{
		return wrapToNorth(new ModuleExtensionWithSdkPanel(this, runnable));
	}

	@NotNull
	@Override
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		return (MutableModuleInheritableNamedPointer<Sdk>) super.getInheritableSdk();
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
