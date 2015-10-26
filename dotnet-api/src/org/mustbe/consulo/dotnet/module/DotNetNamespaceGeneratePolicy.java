package org.mustbe.consulo.dotnet.module;

import org.consulo.psi.PsiPackage;
import org.consulo.psi.PsiPackageManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;

/**
 * @author VISTALL
 * @since 26.10.2015
 */
public interface DotNetNamespaceGeneratePolicy
{
	DotNetNamespaceGeneratePolicy WITH_SOURCE_ROOTS = new DotNetNamespaceGeneratePolicy()
	{
		@RequiredReadAction
		@Nullable
		@Override
		public String calculateNamespace(@NotNull PsiDirectory directory)
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

	DotNetNamespaceGeneratePolicy DEFAULT = new DotNetNamespaceGeneratePolicy()
	{
		@RequiredReadAction
		@Nullable
		@Override
		public String calculateNamespace(@NotNull PsiDirectory directory)
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
			return namespace == null ? null : StringUtil.replaceChar(namespace, ' ', '_');
		}
	};

	@Nullable
	@RequiredReadAction
	String calculateNamespace(@NotNull PsiDirectory directory);
}
