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

import java.util.ArrayDeque;
import java.util.Deque;

import org.mustbe.consulo.microsoft.dotnet.run.coverage.coverageData.ClassData;
import org.mustbe.consulo.microsoft.dotnet.run.coverage.coverageData.ModuleData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.intellij.rt.coverage.data.ProjectData;

/**
 * @author VISTALL
 * @since 10.01.15
 */
public class OpenCoverXmlHandler extends DefaultHandler
{
	enum State
	{
		NONE,
		MODULE,
		CLASS,
		METHOD
	}

	private final static String MODULE_ELEMENT_NAME = "Module";
	private final static String CLASS_ELEMENT_NAME = "Class";
	private final static String METHOD_ELEMENT_NAME = "Method";

	private State myState = State.NONE;
	private boolean myNameState;
	private String myTempName;

	private Deque<ModuleData> myModuleDataList = new ArrayDeque<ModuleData>();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		switch(myState)
		{
			case NONE:
				if(MODULE_ELEMENT_NAME.equals(localName))
				{
					myState = State.MODULE;
				}
				break;
			case MODULE:
				if(CLASS_ELEMENT_NAME.equals(localName))
				{
					myState = State.CLASS;
				}
				if("FullName".equals(localName))
				{
					myNameState = true;
				}
				break;
			case CLASS:
				if(METHOD_ELEMENT_NAME.equals(localName))
				{
					myState = State.METHOD;
				}
				if("FullName".equals(localName))
				{
					myNameState = true;
				}
				break;
			case METHOD:
				if("Name".equals(localName))
				{
					myNameState = true;
				}
				break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		if(myNameState)
		{
			if(myTempName == null)
			{
				myTempName = "";
			}
			String text = new String(ch, start, length);
			myTempName += text;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if(myNameState)
		{
			assert myTempName != null;
			switch(myState)
			{
				case MODULE:
					ModuleData moduleData = new ModuleData(myTempName);
					myModuleDataList.addLast(moduleData);
					break;
				case CLASS:
					ModuleData last = myModuleDataList.getLast();
					last.addClass(new ClassData(myTempName));
					break;
				case METHOD:
					break;
			}
			myNameState = false;
			myTempName = null;
		}

		switch(myState)
		{
			case NONE:
				break;
			case MODULE:
				if(MODULE_ELEMENT_NAME.equals(localName))
				{
					myState = State.NONE;
				}
				break;
			case CLASS:
				if(CLASS_ELEMENT_NAME.equals(localName))
				{
					myState = State.MODULE;
				}
				break;
			case METHOD:
				if(METHOD_ELEMENT_NAME.equals(localName))
				{
					myState = State.METHOD;
				}
				break;
		}
	}

	public ProjectData getProjectData()
	{
		ProjectData projectData = new ProjectData();
		for(ModuleData moduleData : myModuleDataList)
		{
			for(ClassData o : moduleData.getClasses())
			{
				com.intellij.rt.coverage.data.ClassData orCreateClassData = projectData.getOrCreateClassData(o.getName());
			}
		}
		return projectData;
	}
}
