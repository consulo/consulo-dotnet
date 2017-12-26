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

package consulo.csharp.cfs.lang;

import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 31.08.14
 */
public interface CfsTokens
{
	IElementType TEXT = new IElementType("CFS_TEXT", CfsLanguage.INSTANCE);

	IElementType START = new IElementType("CFS_START", CfsLanguage.INSTANCE);

	IElementType END = new IElementType("CFS_END", CfsLanguage.INSTANCE);

	IElementType INDEX = new IElementType("CFS_INDEX", CfsLanguage.INSTANCE);

	IElementType FORMAT = new IElementType("CFS_FORMAT", CfsLanguage.INSTANCE);

	IElementType ALIGN = new IElementType("CFS_ALIGN", CfsLanguage.INSTANCE);

	IElementType COMMA = new IElementType("CFS_COMMA", CfsLanguage.INSTANCE);

	IElementType COLON = new IElementType("CFS_COLON", CfsLanguage.INSTANCE);
}
