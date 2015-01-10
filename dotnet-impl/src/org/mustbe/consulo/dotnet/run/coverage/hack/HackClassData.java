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

package org.mustbe.consulo.dotnet.run.coverage.hack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.intellij.rt.coverage.data.ClassData;
import com.intellij.rt.coverage.data.ProjectData;

/**
 * @author VISTALL
 * @since 10.01.15
 */
public class HackClassData extends ClassData
{
	private double mySequenceCoverage;

	public static HackClassData getOrCreateClassData(ProjectData projectData, String name)
	{
		try
		{
			Field myClassesField = projectData.getClass().getDeclaredField("myClasses");
			myClassesField.setAccessible(true);
			Object o = myClassesField.get(projectData);

			Method getMethod = o.getClass().getDeclaredMethod("get", String.class);
			getMethod.setAccessible(true);

			Object invoke = getMethod.invoke(o, name);
			if(invoke != null)
			{
				return (HackClassData) invoke;
			}

			Method putMethod = o.getClass().getDeclaredMethod("put", String.class, ClassData.class);
			putMethod.setAccessible(true);

			HackClassData hackClassData = new HackClassData(name);
			putMethod.invoke(o, name, hackClassData);

			assert projectData.getClassData(name) instanceof HackClassData;
			return hackClassData;
		}
		catch(NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch(NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch(InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return new HackClassData(name);
	}

	public HackClassData(String name)
	{
		super(name);
	}

	public double getSequenceCoverage()
	{
		return mySequenceCoverage;
	}

	public void setSequenceCoverage(double sequenceCoverage)
	{
		mySequenceCoverage = sequenceCoverage;
	}
}
