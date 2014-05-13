package org.mustbe.consulo.dotnet.documentation;

import java.util.List;

import org.consulo.lombok.annotations.ApplicationService;
import org.emonic.base.documentation.IDocumentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * @author VISTALL
 * @since 13.05.14
 */
@ApplicationService
public class DotNetDocumentationCache
{
	public DotNetDocumentationCache(VirtualFileManager virtualFileManager)
	{
		virtualFileManager.addVirtualFileListener(new VirtualFileAdapter()
		{
			@Override
			public void contentsChanged(@NotNull VirtualFileEvent event)
			{
				super.contentsChanged(event);
			}

			@Override
			public void fileDeleted(@NotNull VirtualFileEvent event)
			{
				super.fileDeleted(event);
			}
		});
	}

	@Nullable
	public IDocumentation findDocumentation(PsiElement scope)
	{
		PsiElement navigationElement = scope.getNavigationElement();
		if(navigationElement == null)
		{
			navigationElement = scope;
		}
		PsiFile containingFile = navigationElement.getContainingFile();
		if(containingFile == null)
		{
			return null;
		}
		VirtualFile virtualFile = containingFile.getVirtualFile();
		if(virtualFile == null)
		{
			return null;
		}

		List<OrderEntry> orderEntriesForFile = ProjectRootManager.getInstance(navigationElement.getProject()).getFileIndex().getOrderEntriesForFile
				(virtualFile);

		for(OrderEntry orderEntry : orderEntriesForFile)
		{
			for(VirtualFile docVirtualFile : orderEntry.getFiles(OrderRootType.DOCUMENTATION))
			{
				for(DotNetDocumentationResolver documentationResolver : DotNetDocumentationResolver.EP_NAME.getExtensions())
				{
					IDocumentation documentation = documentationResolver.resolveDocumentation(docVirtualFile, navigationElement);
					if(documentation != null)
					{
						return documentation;
					}
				}
			}
		}
		return null;
	}
}
