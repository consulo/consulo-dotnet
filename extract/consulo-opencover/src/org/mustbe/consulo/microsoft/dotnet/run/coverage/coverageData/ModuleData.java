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

package org.mustbe.consulo.microsoft.dotnet.run.coverage.coverageData;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author VISTALL
 * @since 10.01.15
 */
public class ModuleData
{
	private String myName;

	private Deque<ClassData> myClasses = new ArrayDeque<ClassData>();

	public ModuleData(String name)
	{
		myName = name;
	}

	public void addClass(ClassData classData)
	{
		myClasses.addLast(classData);
	}

	public Deque<ClassData> getClasses()
	{
		return myClasses;
	}

	public ClassData getLastClass()
	{
		return myClasses.getLast();
	}

	public String getName()
	{
		return myName;
	}
}
