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

package org.mustbe.consulo.lang.variableProfile;

import java.util.ArrayList;
import java.util.List;

import org.consulo.annotations.Immutable;
import org.consulo.util.pointers.Named;
import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 26.01.14
 */
public class VariableProfile implements Named
{
	public static final VariableProfile EMPTY = new VariableProfile("EMPTY");

	private final List<String> myVariables = new ArrayList<String>();
	private final String myName;

	public VariableProfile(String name)
	{
		myName = name;
	}

	public void addVariable(@NotNull String variable)
	{
		myVariables.add(variable);
	}

	public boolean containsVariable(@NotNull String variable)
	{
		return myVariables.contains(variable);
	}

	public void removeVariable(@NotNull String variable)
	{
		myVariables.add(variable);
	}

	@NotNull
	@Immutable
	public List<String> getVariables()
	{
		return myVariables;
	}

	@NotNull
	@Override
	public String getName()
	{
		return myName;
	}
}
