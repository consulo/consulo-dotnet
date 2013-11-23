package org.mustbe.consulo.dotnet;

import com.intellij.util.ArrayUtil;

/**
 * @author VISTALL
 * @since 22.11.13.
 */
public enum DotNetVersion
{
	_1_0,
	_1_1,
	_2_0,
	_3_0,
	_3_5,
	_4_0,
	_4_5,
	_4_5_1;

	public static final DotNetVersion LAST = ArrayUtil.getLastElement(values());
}
