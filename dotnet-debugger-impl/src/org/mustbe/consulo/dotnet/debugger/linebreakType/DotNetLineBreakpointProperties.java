package org.mustbe.consulo.dotnet.debugger.linebreakType;

import org.jdom.Element;
import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetLineBreakpointProperties extends XBreakpointProperties<Element>
{
	@Nullable
	@Override
	public Element getState()
	{
		return null;
	}

	@Override
	public void loadState(Element element)
	{

	}
}
