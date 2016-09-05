package org.mustbe.consulo.dotnet.module.macro;

import consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.ide.macro.Macro;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;

/**
 * @author VISTALL
 * @since 18.08.14
 */
public class TargetFileExtensionMacro extends Macro
{
	public static DataKey<Boolean> DEBUG_SYMBOLS = DataKey.create("debug.symbols");

	@Override
	public String getName()
	{
		return "TargetFileExt";
	}

	@Override
	public String getDescription()
	{
		return "Target file extension";
	}

	@Override
	public String expand(DataContext dataContext)
	{
		final Module module = LangDataKeys.MODULE.getData(dataContext);
		if(module == null)
		{
			return null;
		}
		DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
		if(extension != null)
		{
			Boolean data = DEBUG_SYMBOLS.getData(dataContext);
			if(data == Boolean.TRUE)
			{
				return extension.getDebugFileExtension();
			}
			else
			{
				return extension.getTarget().getExtension();
			}
		}
		return null;
	}
}