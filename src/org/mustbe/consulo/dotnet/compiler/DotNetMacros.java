package org.mustbe.consulo.dotnet.compiler;

import org.consulo.compiler.ModuleCompilerPathsManager;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.roots.impl.ProductionContentFolderTypeProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;

/**
 * @author VISTALL
 * @since 27.11.13.
 */
public class DotNetMacros
{
	public static final String MODULE_OUTPUT_DIR = "${module-output-dir}";

	public static final String CONFIGURATION = "${configuration}";

	public static final String MODULE_NAME = "${module-name}";

	public static final String OUTPUT_FILE_EXT = "${output-file-ext}";

	@NotNull
	public static String extract(@NotNull Module module, boolean debug, boolean library)
	{
		ModuleCompilerPathsManager compilerPathsManager = ModuleCompilerPathsManager.getInstance(module);

		String path = DotNetCompilerConfiguration.getInstance(module.getProject()).getOutputFile();
		path = StringUtil.replace(path, MODULE_OUTPUT_DIR, compilerPathsManager.getCompilerOutputUrl(ProductionContentFolderTypeProvider
				.getInstance()));

		path = StringUtil.replace(path, CONFIGURATION, debug ? "debug" : "release");
		path = StringUtil.replace(path, MODULE_NAME, module.getName());
		path = StringUtil.replace(path, OUTPUT_FILE_EXT, library ? "dll" : "exe");
		return FileUtil.toSystemDependentName(VfsUtil.urlToPath(path));
	}
}
