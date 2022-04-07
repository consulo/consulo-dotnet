package consulo.dotnet.documentation;

import consulo.content.base.DocumentationOrderRootType;
import consulo.ide.ServiceManager;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.module.content.ProjectRootManager;
import consulo.module.content.layer.orderEntry.OrderEntry;
import consulo.virtualFileSystem.VirtualFile;
import org.emonic.base.documentation.IDocumentation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @since 13.05.14
 */
@jakarta.inject.Singleton
public class DotNetDocumentationCache
{
	@Nonnull
	public static DotNetDocumentationCache getInstance()
	{
		return ServiceManager.getService(DotNetDocumentationCache.class);
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

		List<VirtualFile> files = new ArrayList<VirtualFile>();
		for(OrderEntry orderEntry : orderEntriesForFile)
		{
			Collections.addAll(files, orderEntry.getFiles(DocumentationOrderRootType.getInstance()));
		}

		for(DotNetDocumentationResolver documentationResolver : DotNetDocumentationResolver.EP_NAME.getExtensionList())
		{
			IDocumentation documentation = documentationResolver.resolveDocumentation(files, navigationElement);
			if(documentation != null)
			{
				return documentation;
			}
		}
		return null;
	}
}
