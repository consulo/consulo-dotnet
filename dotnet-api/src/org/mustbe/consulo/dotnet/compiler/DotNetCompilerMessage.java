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

package org.mustbe.consulo.dotnet.compiler;

import com.intellij.openapi.compiler.CompilerMessageCategory;

/**
 * @author VISTALL
 * @since 17.01.14
 */
public class DotNetCompilerMessage
{
	private final CompilerMessageCategory myCategory;
	private final String myMessage;
	private final String myFileUrl;
	private final int myLine;
	private final int myColumn;

	public DotNetCompilerMessage(CompilerMessageCategory category, String message, String fileUrl, int line, int column)
	{
		myCategory = category;
		myMessage = message;
		myFileUrl = fileUrl;
		myLine = line;
		myColumn = column;
	}

	public int getColumn()
	{
		return myColumn;
	}

	public String getFileUrl()
	{
		return myFileUrl;
	}

	public String getMessage()
	{
		return myMessage;
	}

	public int getLine()
	{
		return myLine;
	}

	public CompilerMessageCategory getCategory()
	{
		return myCategory;
	}
}
