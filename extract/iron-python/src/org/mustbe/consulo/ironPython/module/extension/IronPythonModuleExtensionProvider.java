package org.mustbe.consulo.ironPython.module.extension;

import javax.swing.Icon;

import org.mustbe.consulo.ironPython.IronPythonIcons;
import org.consulo.module.extension.ModuleExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 11.02.14
 */
public class IronPythonModuleExtensionProvider implements ModuleExtensionProvider<IronPythonModuleExtension, IronPythonMutableModuleExtension>
{
	@Nullable
	@Override
	public Icon getIcon()
	{
		return IronPythonIcons.IronPython;
	}

	@NotNull
	@Override
	public String getName()
	{
		return "IronPython";
	}

	@NotNull
	@Override
	public IronPythonModuleExtension createImmutable(@NotNull String s, @NotNull Module module)
	{
		return new IronPythonModuleExtension(s, module);
	}

	@NotNull
	@Override
	public IronPythonMutableModuleExtension createMutable(@NotNull String s, @NotNull Module module)
	{
		return new IronPythonMutableModuleExtension(s, module);
	}
}
