package org.mustbe.consulo.dotnet.microsoft.module.extension;

import javax.swing.JComponent;

import org.consulo.module.extension.MutableModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public class MicrosoftDotNetMutableModuleExtension extends MicrosoftDotNetModuleExtension implements MutableModuleExtension<MicrosoftDotNetModuleExtension>
{
	private MicrosoftDotNetModuleExtension myOriginalModuleExtension;

	public MicrosoftDotNetMutableModuleExtension(@NotNull String id, @NotNull Module module, @NotNull MicrosoftDotNetModuleExtension originalModuleExtension)
	{
		super(id, module);
		myOriginalModuleExtension = originalModuleExtension;
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull ModifiableRootModel modifiableRootModel, @Nullable Runnable runnable)
	{
		return null;
	}

	@Override
	public void setEnabled(boolean b)
	{
		myIsEnabled = b;
	}

	@Override
	public boolean isModified()
	{
		return myOriginalModuleExtension.isEnabled() != isEnabled();
	}

	@Override
	public void commit()
	{
		myOriginalModuleExtension.commit(this);
	}
}
