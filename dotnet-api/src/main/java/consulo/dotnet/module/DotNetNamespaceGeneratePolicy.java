package consulo.dotnet.module;

import consulo.annotation.UsedInPlugin;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.language.psi.PsiDirectory;
import consulo.language.psi.PsiPackage;
import consulo.language.psi.PsiPackageManager;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 26.10.2015
 */
public abstract class DotNetNamespaceGeneratePolicy
{
	public static final DotNetNamespaceGeneratePolicy WITH_SOURCE_ROOTS = new DotNetNamespaceGeneratePolicy()
	{
		@RequiredReadAction
		@Nullable
		@Override
		public String calculateDirtyNamespace(@Nonnull PsiDirectory directory)
		{
			PsiPackage aPackage = PsiPackageManager.getInstance(directory.getProject()).findPackage(directory, DotNetModuleExtension.class);
			String namespace = null;
			if(aPackage != null)
			{
				namespace = aPackage.getQualifiedName();
			}
			return namespace;
		}
	};

	public static final DotNetNamespaceGeneratePolicy DEFAULT = new DotNetNamespaceGeneratePolicy()
	{
		@RequiredReadAction
		@Nullable
		@Override
		public String calculateDirtyNamespace(@Nonnull PsiDirectory directory)
		{
			String namespace = null;
			Module moduleForPsiElement = ModuleUtilCore.findModuleForPsiElement(directory);
			if(moduleForPsiElement != null)
			{
				DotNetModuleExtension extension = ModuleUtilCore.getExtension(moduleForPsiElement, DotNetModuleExtension.class);
				if(extension != null)
				{
					VirtualFile moduleDir = moduleForPsiElement.getModuleDir();
					if(moduleDir == null)
					{
						return null;
					}
					String relativePath = VfsUtil.getRelativePath(directory.getVirtualFile(), moduleDir, '.');

					if(!StringUtil.isEmpty(relativePath))
					{
						String namespacePrefix = extension.getNamespacePrefix();
						if(!StringUtil.isEmpty(namespacePrefix))
						{
							namespace = namespacePrefix + "." + relativePath;
						}
						else
						{
							namespace = relativePath;
						}
					}
					else
					{
						namespace = extension.getNamespacePrefix();
					}
				}
			}
			return namespace;
		}
	};

	@Nullable
	@RequiredReadAction
	@UsedInPlugin
	public String calculateNamespace(@Nonnull PsiDirectory directory)
	{
		String namespace = calculateDirtyNamespace(directory);
		if(StringUtil.isEmpty(namespace))
		{
			return null;
		}

		namespace = StringUtil.replaceChar(namespace, ' ', '_');
		if(Character.isDigit(namespace.charAt(0)))
		{
			namespace = '_' + namespace;
		}
		return namespace;
	}

	@Nullable
	protected abstract String calculateDirtyNamespace(@Nonnull PsiDirectory directory);
}
