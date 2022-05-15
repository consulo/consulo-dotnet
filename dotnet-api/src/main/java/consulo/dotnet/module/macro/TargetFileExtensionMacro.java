package consulo.dotnet.module.macro;

import consulo.dataContext.DataContext;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.pathMacro.Macro;
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
		final Module module = dataContext.getData(Module.KEY);
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