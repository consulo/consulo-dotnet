package consulo.dotnet.module.macro;

import com.intellij.ide.macro.Macro;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.util.dataholder.Key;

/**
 * @author VISTALL
 * @since 18.08.14
 */
public class TargetFileExtensionMacro extends Macro
{
	public static final Key<Boolean> DEBUG_SYMBOLS = Key.create("debug.symbols");

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
		final Module module = dataContext.getData(LangDataKeys.MODULE);
		if(module == null)
		{
			return null;
		}
		DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
		if(extension != null)
		{
			Boolean data = dataContext.getData(DEBUG_SYMBOLS);
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