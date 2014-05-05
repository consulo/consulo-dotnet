package org.mustbe.consulo.mono.csharp.module.extension;

import org.consulo.module.extension.ModuleExtensionWithSdk;
import org.consulo.module.extension.ModuleInheritableNamedPointer;
import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author VISTALL
 * @since 05.05.14
 */
public abstract class InnerMonoModuleExtension<T extends InnerMonoModuleExtension<T>> extends ModuleExtensionImpl<T> implements
		ModuleExtensionWithSdk<T>
{
	private ModuleInheritableNamedPointer<Sdk> myPointer;

	protected Sdk myParentSdk;

	private Sdk myLazySdk;

	public InnerMonoModuleExtension(@NotNull String id, @NotNull ModifiableRootModel rootModel)
	{
		super(id, rootModel);
		myPointer = new DummyModuleInheritableNamedPointer<Sdk>()
		{
			@Override
			public Sdk get()
			{
				return InnerMonoModuleExtension.this.get();
			}
		};
	}

	private Sdk get()
	{
		DotNetModuleExtension extension = myRootModel.getExtension(DotNetModuleExtension.class);
		assert extension != null;

		Sdk parentSdk = extension.getSdk();
		if(parentSdk != myParentSdk)
		{
			myLazySdk = null;
		}

		if(myLazySdk == null)
		{
			myParentSdk = parentSdk;
			if(myParentSdk == null)
			{
				return null;
			}
			myLazySdk = createSdk(myParentSdk.getHomeDirectory());
		}
		return myLazySdk;
	}

	protected void setEnabledImpl(boolean val)
	{
		myIsEnabled = val;
		if(val)
		{
			myRootModel.addModuleExtensionSdkEntry(this);
		}
	}

	protected abstract Sdk createSdk(VirtualFile virtualFile);

	@NotNull
	@Override
	public ModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		return myPointer;
	}

	@Nullable
	@Override
	public Sdk getSdk()
	{
		return getInheritableSdk().get();
	}

	@Nullable
	@Override
	public String getSdkName()
	{
		return getInheritableSdk().getName();
	}
}
