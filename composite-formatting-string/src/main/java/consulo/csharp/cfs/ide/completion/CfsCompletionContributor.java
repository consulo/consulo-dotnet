package consulo.csharp.cfs.ide.completion;

import consulo.application.AllIcons;
import consulo.csharp.cfs.lang.CfsTokens;
import consulo.dotnet.psi.DotNetCallArgumentList;
import consulo.dotnet.psi.DotNetExpression;
import consulo.language.editor.completion.*;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.pattern.StandardPatterns;
import consulo.language.psi.PsiLanguageInjectionHost;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.util.ProcessingContext;
import consulo.ui.image.Image;
import consulo.util.collection.ArrayUtil;
import consulo.util.lang.StringUtil;

import javax.annotation.Nonnull;

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
			public void addCompletions(@Nonnull CompletionParameters parameters, ProcessingContext context, @Nonnull CompletionResultSet result)
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
					builder = builder.withIcon((Image) AllIcons.Nodes.Parameter);
					builder = builder.withPresentableText(StringUtil.trimLog(callArgument.getText().trim(), 15));
					result.addElement(builder);
				}
			}
		});
	}
}
