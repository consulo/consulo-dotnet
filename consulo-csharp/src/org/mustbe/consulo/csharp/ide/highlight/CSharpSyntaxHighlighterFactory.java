package org.mustbe.consulo.csharp.ide.highlight;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;

/**
 * @author VISTALL
 * @since 22.11.13.
 */
public class CSharpSyntaxHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory
{
	@NotNull
	@Override
	protected SyntaxHighlighter createHighlighter()
	{
		return new CSharpSyntaxHighlighter();
	}
}
