/*
 * Copyright 2013-2016 must-be.org
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

package consulo.dotnet.run.filters;

import consulo.content.scope.SearchScope;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.resolve.DotNetPsiSearcher;
import consulo.execution.ui.console.Filter;
import consulo.execution.ui.console.HyperlinkInfo;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.navigation.Navigatable;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.LocalFileSystem;
import consulo.virtualFileSystem.VirtualFile;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author VISTALL
 * @since 03.04.2016
 */
public class DotNetExceptionFilter implements Filter
{
	private static class TypeLink implements HyperlinkInfo
	{
		private String myType;
		private SearchScope mySearchScope;

		public TypeLink(String type, SearchScope searchScope)
		{
			myType = type;
			mySearchScope = searchScope;
		}

		@Override
		@RequiredUIAccess
		public void navigate(Project project)
		{
			DotNetTypeDeclaration type = DotNetPsiSearcher.getInstance(project).findType(myType, mySearchScope);
			if(type instanceof Navigatable)
			{
				((Navigatable) type).navigate(true);
			}
		}
	}

	private static final Pattern ourPattern = Pattern.compile("  at (.+)\\.(.+) \\((.+)?\\) (\\[.+\\] )?in (.+):(\\d+)\\W+");

	private final Project myProject;
	private final SearchScope mySearchScope;

	public DotNetExceptionFilter(Project project, SearchScope searchScope)
	{
		myProject = project;
		mySearchScope = searchScope;
	}

	private static final int typeGroup = 1;
	private static final int methodGroup = 2;
	private static final int methodArgsGroup = 3;
	private static final int filePathGroup = 5;
	private static final int lineNumberGroup = 6;

	@Nullable
	@Override
	public Result applyFilter(String line, int entireLength)
	{
		int start = entireLength - line.length();
		Matcher matcher = ourPattern.matcher(line);
		if(matcher.matches())
		{
			String type = matcher.group(typeGroup);
			String filePath = matcher.group(filePathGroup);
			int lineNumber = StringUtil.parseInt(matcher.group(lineNumberGroup), -1);

			List<ResultItem> resultItems = new ArrayList<ResultItem>(2);
			resultItems.add(new ResultItem(start + matcher.start(typeGroup), start + matcher.start(typeGroup) + type.length(), new TypeLink(type, mySearchScope)));
			VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(filePath);
			if(fileByPath != null)
			{
				HyperlinkInfo hyperlinkInfo = HyperlinkInfoFactory.getInstance().createMultipleFilesHyperlinkInfo(Collections.singletonList(fileByPath), lineNumber, myProject);
				resultItems.add(new ResultItem(start + matcher.start(filePathGroup), start + matcher.end(lineNumberGroup), hyperlinkInfo));
			}
			return new Result(resultItems);
		}
		return null;
	}
}
