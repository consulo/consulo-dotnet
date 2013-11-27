package org.mustbe.consulo.dotnet.compiler;

import org.consulo.lombok.annotations.ProjectService;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.components.StorageScheme;

/**
 * @author VISTALL
 * @since 27.11.13.
 */
@State(
		name = "DotNetCompilerConfiguration",
		storages = {
				@Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/compiler.xml", scheme = StorageScheme.DIRECTORY_BASED)
		})
@ProjectService
public class DotNetCompilerConfiguration implements PersistentStateComponent<Element>
{
	@NotNull
	public String getOutputFile()
	{
		return DotNetMacros.MODULE_OUTPUT_DIR + "/" + DotNetMacros.CONFIGURATION + "/" + DotNetMacros.MODULE_NAME + "." + DotNetMacros
				.OUTPUT_FILE_EXT;
	}

	@Nullable
	@Override
	public Element getState()
	{
		return null;
	}

	@Override
	public void loadState(Element element)
	{

	}
}
