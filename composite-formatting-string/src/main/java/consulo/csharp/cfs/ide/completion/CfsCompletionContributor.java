package consulo.csharp.cfs.ide.completion;

import org.jetbrains.annotations.NotNull;
import consulo.csharp.cfs.lang.CfsTokens;
import consulo.dotnet.psi.DotNetCallArgumentList;
import consulo.dotnet.psi.DotNetExpression;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import consulo.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.psi.impl.source.tree.injected.Place;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ProcessingContext;

/**
 * @author VISTALL
 * @since 24.03.2015
 */
public class CfsCompletionContributor extends CompletionContributor
{
	public CfsCompletionContributor()
	{
		extend(CompletionType.BASIC, StandardPatterns.psiElement().withElementType(CfsTokens.INDEX), new CompletionProvider()
		{
			@Override
			public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result)
			{
				int thisArgumentInex = -1;
				DotNetExpression[] callArguments = null;
				Place shreds = InjectedLanguageUtil.getShreds(parameters.getOriginalFile());
				for(PsiLanguageInjectionHost.Shred shred : shreds)
				{
					PsiLanguageInjectionHost host = shred.getHost();
					if(host == null)
					{
						continue;
					}
					DotNetCallArgumentList callArgumentList = PsiTreeUtil.getParentOfType(host, DotNetCallArgumentList.class);
					if(callArgumentList != null)
					{
						if(host instanceof DotNetExpression)
						{
							thisArgumentInex = ArrayUtil.find(callArguments = callArgumentList.getExpressions(), host);
						}
						break;
					}
				}

				if(callArguments == null || thisArgumentInex == -1)
				{
					return;
				}

				for(int i = (thisArgumentInex + 1); i < callArguments.length; i++)
				{
					DotNetExpression callArgument = callArguments[i];

					LookupElementBuilder builder = LookupElementBuilder.create(String.valueOf(i - 1));
					builder = builder.withTypeText(String.valueOf(i - 1));
					builder = builder.withIcon(AllIcons.Nodes.Parameter);
					builder = builder.withPresentableText(StringUtil.trimLog(callArgument.getText().trim(), 15));
					result.addElement(builder);
				}
			}
		});
	}
}
