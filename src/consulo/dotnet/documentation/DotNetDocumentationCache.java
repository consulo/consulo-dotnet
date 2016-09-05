package consulo.dotnet.documentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.emonic.base.documentation.IDocumentation;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import consulo.lombok.annotations.ApplicationService;
import consulo.roots.types.DocumentationOrderRootType;

/**
 * @author VISTALL
 * @since 13.05.14
 */
@ApplicationService
public class DotNetDocumentationCache
{
	public DotNetDocumentationCache()
	{
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

		for(DotNetDocumentationResolver documentationResolver : DotNetDocumentationResolver.EP_NAME.getExtensions())
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
