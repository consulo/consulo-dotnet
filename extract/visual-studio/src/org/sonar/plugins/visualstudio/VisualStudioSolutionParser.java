/*
 * SonarQube Visual Studio Plugin
 * Copyright (C) 2014 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.visualstudio;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

public class VisualStudioSolutionParser
{

	private static final String PROJECT_LINE_LOOKAHEAD = "Project(";
	private static final Pattern PROJECT_LINE_PATTERN = Pattern.compile(
			"Project\\(\"[^\"]++\"\\)\\s*+=\\s*+\"([^\"]++)\",\\s*+\"([^\"]++)\",\\s*+\"[^\"]++\"");

	public static VisualStudioSolution parse(File file)
	{
		ImmutableList.Builder<VisualStudioSolutionProject> projectsBuilder = ImmutableList.builder();

		try
		{
			int lineNumber = 1;
			for(String line : Files.readLines(file, Charsets.UTF_8))
			{
				if(line.startsWith(PROJECT_LINE_LOOKAHEAD))
				{
					projectsBuilder.add(parseProjectLine(file, lineNumber, line));
				}
				lineNumber++;
			}
		}
		catch(IOException e)
		{
			throw Throwables.propagate(e);
		}

		return new VisualStudioSolution(projectsBuilder.build());
	}

	private static VisualStudioSolutionProject parseProjectLine(File file, int lineNumber, String line)
	{
		Matcher matcher = PROJECT_LINE_PATTERN.matcher(line);
		if(!matcher.matches())
		{
			throw new ParseErrorException("Expected the line " + lineNumber + " of " + file.getAbsolutePath() + " to match the regular expression "
					+ PROJECT_LINE_PATTERN);
		}

		return new VisualStudioSolutionProject(matcher.group(1), matcher.group(2));
	}

	private static class ParseErrorException extends RuntimeException
	{

		private static final long serialVersionUID = 1L;

		public ParseErrorException(String message)
		{
			super(message);
		}
	}
}
