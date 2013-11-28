package org.mustbe.consulo.csharp.lang.parser;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.parser.decl.DeclarationParsing;
import org.mustbe.consulo.csharp.lang.parser.stmt.UsingStatementParsing;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpParser extends SharingParsingHelpers implements PsiParser
{
	@NotNull
	@Override
	public ASTNode parse(@NotNull IElementType elementType, @NotNull PsiBuilder builder, @NotNull LanguageVersion languageVersion)
	{
		builder.setDebugMode(true);
		val builderWrapper = new CSharpBuilderWrapper(builder);

		val marker = builderWrapper.mark();

		UsingStatementParsing.parseUsingList(builderWrapper);

		while(!builder.eof())
		{
			if(!DeclarationParsing.parse(builderWrapper, false))
			{
				builder.advanceLexer();
			}
		}

		marker.done(elementType);
		return builder.getTreeBuilt();
	}
}
