/*
 * Copyright 2013-2015 must-be.org
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

package org.mustbe.consulo.microsoft.dotnet.run.coverage;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author VISTALL
 * @since 10.01.15
 */
@XmlRootElement(name = "CoverageSession")
public class CoverageSession
{
	public static class Summary
	{
		@XmlAttribute
		public double sequenceCoverage;
	}

	public static class Modules
	{
		@XmlElement(name = "Module")
		public Module[] Modules;
	}

	public static  class Files
	{
		@XmlElement(name = "File")
		public File[] Files;
	}

	public static class Classes
	{
		@XmlElement(name = "Class")
		public Class[] Classes;
	}

	public static class Methods
	{
		@XmlElement(name = "Method")
		public Method[] Methods;
	}

	public static class Module
	{
		@XmlElement(name = "Summary")
		public Summary Summary;

		@XmlElement(name = "FullName")
		public String FullName;

		@XmlElement(name = "ModuleName")
		public String ModuleName;

		@XmlElement(name = "Files")
		public Files Files;

		@XmlElement(name = "Classes")
		public Classes Classes;
	}

	public static class File
	{
		@XmlAttribute(name = "uid")
		public int UId;

		@XmlAttribute(name = "fullPath")
		public String FullPath;
	}

	public static class Class
	{
		@XmlElement(name = "Summary")
		public Summary Summary;

		@XmlElement(name = "FullName")
		public String FullName;

		@XmlElement(name = "Methods")
		public Methods Methods;
	}

	public static class Method
	{
		@XmlElement(name = "Summary")
		public Summary Summary;

		@XmlElement(name = "Name")
		public String Name;

		@XmlElement(name = "FileRef")
		public FileRef FileRef;

		@XmlElement(name = "SequencePoints")
		public SequencePoints SequencePoints;
	}

	public static class FileRef
	{
		@XmlAttribute(name = "uid")
		public int UId;
	}

	public static class SequencePoints
	{
		@XmlElement(name = "SequencePoint")
		public SequencePoint[] Points;
	}

	public static class SequencePoint
	{
		@XmlAttribute(name = "ordinal")
		public int ordinal;

		@XmlAttribute(name = "sl")
		public int StartLine;

		@XmlAttribute(name = "el")
		public int EndLine;

		@XmlAttribute(name = "fileid")
		public int FileUId;
	}

	@XmlElement(name = "Summary")
	public Summary Summary;

	@XmlElement(name = "Modules")
	public Modules Modules;
}
