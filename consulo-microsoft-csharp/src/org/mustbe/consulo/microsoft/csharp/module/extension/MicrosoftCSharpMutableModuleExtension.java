package org.mustbe.consulo.microsoft.csharp.module.extension;

import javax.swing.JComponent;

import org.consulo.module.extension.MutableModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class MicrosoftCSharpMutableModuleExtension extends MicrosoftCSharpModuleExtension implements MutableModuleExtension<MicrosoftCSharpModuleExtension>
{
	private MicrosoftCSharpModuleExtension myOriginal;

	public MicrosoftCSharpMutableModuleExtension(@NotNull String id, @NotNull Module module, @NotNull MicrosoftCSharpModuleExtension original)
	{
		super(id, module);
		myOriginal = original;
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
		return myIsEnabled != myOriginal.isEnabled();
	}

	@Override
	public void commit()
	{
		myOriginal.commit(this);
	}
}
