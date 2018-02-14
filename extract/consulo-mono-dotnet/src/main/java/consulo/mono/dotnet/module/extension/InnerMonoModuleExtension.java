package consulo.mono.dotnet.module.extension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.module.extension.ModuleExtensionWithSdk;
import consulo.module.extension.ModuleInheritableNamedPointer;
import consulo.module.extension.impl.ModuleExtensionImpl;
import consulo.roots.ModifiableModuleRootLayer;
import consulo.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 05.05.14
 */
public abstract class InnerMonoModuleExtension<T extends InnerMonoModuleExtension<T>> extends ModuleExtensionImpl<T> implements ModuleExtensionWithSdk<T>
{
	private ModuleInheritableNamedPointer<Sdk> myPointer;

	protected Sdk myParentSdk;

	private Sdk myLazySdk;

	public InnerMonoModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer rootModel)
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
		MonoDotNetModuleExtension extension = myModuleRootLayer.getExtensionWithoutCheck(MonoDotNetModuleExtension.class);

		Sdk parentSdk = !extension.isEnabled() ? null : extension.getSdk();
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
			((ModifiableModuleRootLayer)myModuleRootLayer).addModuleExtensionSdkEntry(this);
		}
	}

	protected abstract Sdk createSdk(VirtualFile virtualFile);

	@Nonnull
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
