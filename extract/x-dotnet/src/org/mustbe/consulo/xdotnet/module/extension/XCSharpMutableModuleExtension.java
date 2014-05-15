package org.mustbe.consulo.xdotnet.module.extension;

import javax.swing.JComponent;

import org.consulo.module.extension.MutableModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.module.extension.CSharpMutableModuleExtension;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author VISTALL
 * @since 31.03.14
 */
public class XCSharpMutableModuleExtension extends XCSharpModuleExtension implements MutableModuleExtension<XCSharpModuleExtension>,
		CSharpMutableModuleExtension<XCSharpModuleExtension>
{
	public XCSharpMutableModuleExtension(@NotNull String id, @NotNull ModifiableRootModel module)
	{
		super(id, module);
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull Runnable updateOnCheck)
	{
		return createConfigurablePanelImpl(updateOnCheck);
	}

	@Override
	public boolean isModified(@NotNull XCSharpModuleExtension originalExtension)
	{
		return isModifiedImpl(originalExtension);
	}
}
