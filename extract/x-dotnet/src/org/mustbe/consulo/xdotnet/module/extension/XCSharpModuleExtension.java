package org.mustbe.consulo.xdotnet.module.extension;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.module.extension.CSharpModuleExtension;
import org.mustbe.consulo.dotnet.compiler.DotNetCompilerOptionsBuilder;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.microsoft.csharp.module.extension.MicrosoftCSharpModuleExtension;
import org.mustbe.consulo.microsoft.dotnet.sdk.MicrosoftDotNetSdkType;
import org.mustbe.consulo.mono.csharp.module.extension.MonoCSharpModuleExtension;
import org.mustbe.consulo.mono.dotnet.sdk.MonoSdkType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author VISTALL
 * @since 31.03.14
 */
public class XCSharpModuleExtension extends CSharpModuleExtension<XCSharpModuleExtension>
{
	public XCSharpModuleExtension(@NotNull String id, @NotNull ModifiableRootModel module)
	{
		super(id, module);
	}

	@NotNull
	@Override
	public DotNetCompilerOptionsBuilder createCompilerOptionsBuilder()
	{
		DotNetModuleExtension extension = myRootModel.getExtension(DotNetModuleExtension.class);
		assert extension != null;

		Sdk sdk = extension.getSdk();
		assert sdk != null;
		SdkTypeId sdkType = sdk.getSdkType();
		if(sdkType instanceof MicrosoftDotNetSdkType)
		{
			return MicrosoftCSharpModuleExtension.createCompilerOptionsBuilderImpl(this);
		}
		else if(sdkType instanceof MonoSdkType)
		{
			return MonoCSharpModuleExtension.createCompilerOptionsBuilderImpl(this);
		}
		throw new IllegalArgumentException(sdkType.getName());
	}
}
