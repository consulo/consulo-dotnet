/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mustbe.consulo.mono.dotnet.documentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.consulo.lombok.annotations.Logger;
import org.emonic.base.documentation.IDocumentation;
import org.emonic.base.documentation.ITypeDocumentation;
import org.emonic.monodoc.MonodocTree;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.documentation.DotNetDocumentationResolver;
import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.Function;
import com.intellij.util.containers.ConcurrentWeakHashMap;

/**
 * @author VISTALL
 * @since 13.05.14
 */
@Logger
public class MonoDocumentationResolver implements DotNetDocumentationResolver
{
	private Map<VirtualFile, MonodocTree[]> myCache = new ConcurrentWeakHashMap<VirtualFile, MonodocTree[]>();

	@Nullable
	@Override
	public IDocumentation resolveDocumentation(@NotNull VirtualFile virtualFile, @NotNull PsiElement element)
	{
		if(!Comparing.equal(virtualFile.getExtension(), "source"))
		{
			return null;
		}
		MonodocTree[] trees = myCache.get(virtualFile);
		if(trees == null)
		{
			trees = loadTrees(virtualFile);
			if(trees.length == 0)
			{
				return null;
			}
			myCache.put(virtualFile, trees);
		}

		String namespace = null;
		String className = null;
		String memberName = null;
		// FIXME [VISTALL] nested types?
		if(element instanceof DotNetTypeDeclaration)
		{
			namespace = ((DotNetTypeDeclaration) element).getPresentableParentQName();
			className = ((DotNetTypeDeclaration) element).getName();
		}
		else if(element instanceof DotNetQualifiedElement)
		{
			if(element instanceof DotNetMethodDeclaration && ((DotNetMethodDeclaration) element).isDelegate())
			{
				namespace = ((DotNetMethodDeclaration) element).getPresentableParentQName();
				className = ((DotNetMethodDeclaration) element).getName();
			}
			else
			{
				PsiElement parent = element.getParent();
				if(parent instanceof DotNetTypeDeclaration)
				{
					namespace = ((DotNetTypeDeclaration) parent).getPresentableParentQName();
					className = ((DotNetTypeDeclaration) parent).getName();
				}
				else
				{
					return null;
				}
				memberName = ((DotNetQualifiedElement) element).getName();
				memberName += appendArguments(element);
			}
		}

		if(className == null)
		{
			return null;
		}

		for(MonodocTree tree : trees)
		{
			ITypeDocumentation documentation = tree.findDocumentation(namespace, className);
			if(documentation != null)
			{
				if(memberName != null)
				{
					List<IDocumentation> documentations = documentation.getDocumentation();

					for(IDocumentation iDocumentation : documentations)
					{
						if(Comparing.equal(iDocumentation.getName(), memberName))
						{
							return iDocumentation;
						}
					}
					return null;
				}
				return documentation;
			}
		}
		return null;
	}

	private String appendArguments(PsiElement element)
	{
		if(!(element instanceof DotNetMethodDeclaration))
		{
			return "";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("(");
		builder.append(StringUtil.join(((DotNetMethodDeclaration) element).getParameters(), new Function<DotNetParameter, String>()
		{
			@Override
			public String fun(DotNetParameter dotNetParameter)
			{
				return dotNetParameter.toTypeRef(false).getPresentableText();
			}
		},","));
		builder.append(")");
		return builder.toString();
	}

	private MonodocTree[] loadTrees(VirtualFile virtualFile)
	{
		List<MonodocTree> trees = new ArrayList<MonodocTree>(2);
		try
		{
			Document document = JDOMUtil.loadDocument(virtualFile.getInputStream());
			for(Element o : document.getRootElement().getChildren("source"))
			{
				String basefile = o.getAttributeValue("basefile");
				if(basefile == null)
				{
					continue;
				}
				VirtualFile zipFile = virtualFile.getParent().findChild(basefile + ".zip");
				VirtualFile treeFile = virtualFile.getParent().findChild(basefile + ".tree");
				if(zipFile == null || treeFile == null)
				{
					continue;
				}
				MonodocTree tree = new MonodocTree(VfsUtilCore.virtualToIoFile(treeFile), VfsUtilCore.virtualToIoFile(zipFile));

				tree.loadNode();

				trees.add(tree);
			}
		}
		catch(JDOMException e)
		{
			LOGGER.error(e);
		}
		catch(IOException e)
		{
			LOGGER.error(e);
		}
		return trees.toArray(new MonodocTree[trees.size()]);
	}
}
