package org.mustbe.consulo.ironPython.module.extension;

import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.consulo.python.module.extension.PyModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.ironPython.module.IronPythonConfigurationLayer;
import org.mustbe.consulo.module.extension.ChildLayeredModuleExtensionImpl;
import org.mustbe.consulo.module.extension.ConfigurationLayer;
import org.mustbe.consulo.module.extension.LayeredModuleExtension;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.jetbrains.python.sdk.PythonSdkType;

/**
 * @author VISTALL
 * @since 11.02.14
 */
public class IronPythonModuleExtension extends ChildLayeredModuleExtensionImpl<IronPythonModuleExtension> implements PyModuleExtension<IronPythonModuleExtension>
{
	public IronPythonModuleExtension(@NotNull String id, @NotNull ModifiableRootModel modifiableRootModel)
	{
		super(id, modifiableRootModel);
	}

	@NotNull
	@Override
	public Class<? extends LayeredModuleExtension> getHeadClass()
	{
		return DotNetModuleExtension.class;
	}

	@NotNull
	@Override
	protected ConfigurationLayer createLayer()
	{
		return new IronPythonConfigurationLayer(this);
	}

	@NotNull
	@Override
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		IronPythonConfigurationLayer currentProfileEx = (IronPythonConfigurationLayer) getCurrentLayer();
		return currentProfileEx.getInheritableSdk();
	}

	@Nullable
	@Override
	public Sdk getSdk()
	{
		IronPythonConfigurationLayer currentProfileEx = (IronPythonConfigurationLayer) getCurrentLayer();
		return currentProfileEx.getInheritableSdk().get();
	}

	@Nullable
	@Override
	public String getSdkName()
	{
		IronPythonConfigurationLayer currentProfileEx = (IronPythonConfigurationLayer) getCurrentLayer();
		return currentProfileEx.getInheritableSdk().getName();
	}

	@NotNull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return PythonSdkType.class;
	}
}
