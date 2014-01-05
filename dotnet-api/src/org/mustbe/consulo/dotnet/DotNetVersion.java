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
