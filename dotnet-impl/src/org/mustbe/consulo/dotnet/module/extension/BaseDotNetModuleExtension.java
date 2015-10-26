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

package org.mustbe.consulo.dotnet.module.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.consulo.module.extension.ModuleExtension;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.DotNetTarget;
import org.mustbe.consulo.dotnet.module.DotNetNamespaceGeneratePolicy;
import com.intellij.openapi.roots.ModuleRootLayer;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public abstract class BaseDotNetModuleExtension<S extends BaseDotNetModuleExtension<S>> extends BaseDotNetSimpleModuleExtension<S> implements
		DotNetModuleExtension<S>
{
	protected DotNetTarget myTarget = DotNetTarget.EXECUTABLE;
	protected boolean myAllowDebugInfo;
	protected boolean myAllowSourceRoots;
	protected String myMainType;
	protected String myNamespacePrefix;
	protected String myFileName = DEFAULT_FILE_NAME;
	protected String myOutputDirectory = DEFAULT_OUTPUT_DIR;

	public BaseDotNetModuleExtension(@NotNull String id, @NotNull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}

	@Override
	public boolean isModifiedImpl(S ex)
	{
		return super.isModifiedImpl(ex) ||
				!myTarget.equals(ex.myTarget) ||
				myAllowDebugInfo != ex.isAllowDebugInfo() ||
				myAllowSourceRoots != ex.isAllowSourceRoots() ||
				!Comparing.equal(myMainType, ex.myMainType) ||
				!Comparing.equal(myNamespacePrefix, ex.myNamespacePrefix) ||
				!Comparing.equal(getFileName(), ex.getFileName()) ||
				!Comparing.equal(getOutputDir(), ex.getOutputDir());
	}

	@Override
	public boolean isSupportCompilation()
	{
		return true;
	}

	@Override
	public void commit(@NotNull S mutableModuleExtension)
	{
		super.commit(mutableModuleExtension);

		myTarget = mutableModuleExtension.myTarget;
		myAllowDebugInfo = mutableModuleExtension.myAllowDebugInfo;
		myAllowSourceRoots = mutableModuleExtension.myAllowSourceRoots;
		myMainType = mutableModuleExtension.myMainType;
		myFileName = mutableModuleExtension.myFileName;
		myNamespacePrefix = mutableModuleExtension.myNamespacePrefix;
		myOutputDirectory = mutableModuleExtension.myOutputDirectory;
	}

	@RequiredReadAction
	@Override
	protected void loadStateImpl(@NotNull Element element)
	{
		super.loadStateImpl(element);

		myTarget = DotNetTarget.valueOf(element.getAttributeValue("target", DotNetTarget.EXECUTABLE.name()));
		myAllowDebugInfo = Boolean.valueOf(element.getAttributeValue("debug", "false"));
		myAllowSourceRoots = Boolean.valueOf(element.getAttributeValue("allow-source-roots", "false"));
		myFileName = element.getAttributeValue("file-name", DEFAULT_FILE_NAME);
		myOutputDirectory = element.getAttributeValue("output-dir", DEFAULT_OUTPUT_DIR);
		myMainType = element.getAttributeValue("main-type");
		myNamespacePrefix = element.getAttributeValue("namespace-prefix");
	}

	@Override
	protected void getStateImpl(@NotNull Element element)
	{
		super.getStateImpl(element);

		element.setAttribute("target", myTarget.name());
		element.setAttribute("debug", Boolean.toString(myAllowDebugInfo));
		element.setAttribute("allow-source-roots", Boolean.toString(myAllowSourceRoots));
		element.setAttribute("file-name", myFileName);
		element.setAttribute("output-dir", myOutputDirectory);
		element.setAttribute("namespace-prefix", getNamespacePrefix());
		if(myMainType != null)
		{
			element.setAttribute("main-type", myMainType);
		}
	}

	@NotNull
	@Override
	@RequiredReadAction
	public PsiElement[] getEntryPointElements()
	{
		List<PsiElement> list = new ArrayList<PsiElement>();
		for(ModuleExtension moduleExtension : myModuleRootLayer.getExtensions())
		{
			if(moduleExtension instanceof DotNetModuleLangExtension)
			{
				Collections.addAll(list, ((DotNetModuleLangExtension) moduleExtension).getEntryPointElements());
			}
		}
		return ContainerUtil.toArray(list, PsiElement.ARRAY_FACTORY);
	}

	@NotNull
	@Override
	public DotNetNamespaceGeneratePolicy getNamespaceGeneratePolicy()
	{
		if(isAllowSourceRoots())
		{
			return DotNetNamespaceGeneratePolicy.WITH_SOURCE_ROOTS;
		}
		return super.getNamespaceGeneratePolicy();
	}

	@Override
	public boolean isAllowDebugInfo()
	{
		return myAllowDebugInfo;
	}

	@Nullable
	@Override
	public String getMainType()
	{
		return myMainType;
	}

	@Override
	public boolean isAllowSourceRoots()
	{
		return myAllowSourceRoots;
	}

	@NotNull
	@Override
	public String getNamespacePrefix()
	{
		return StringUtil.notNullize(myNamespacePrefix);
	}

	@NotNull
	@Override
	public String getFileName()
	{
		return StringUtil.notNullizeIfEmpty(myFileName, DEFAULT_FILE_NAME);
	}

	@NotNull
	@Override
	public String getOutputDir()
	{
		return StringUtil.notNullizeIfEmpty(myOutputDirectory, DEFAULT_OUTPUT_DIR);
	}

	public void setFileName(@NotNull String name)
	{
		myFileName = name;
	}

	public void setNamespacePrefix(@NotNull String namespacePrefix)
	{
		myNamespacePrefix = namespacePrefix;
	}

	public void setOutputDir(@NotNull String dir)
	{
		myOutputDirectory = dir;
	}

	public void setAllowSourceRoots(boolean val)
	{
		myAllowSourceRoots = val;
	}

	public void setAllowDebugInfo(boolean allowDebugInfo)
	{
		myAllowDebugInfo = allowDebugInfo;
	}

	public void setMainType(String type)
	{
		myMainType = type;
	}

	@Override
	@NotNull
	public DotNetTarget getTarget()
	{
		return myTarget;
	}

	public void setTarget(@NotNull DotNetTarget target)
	{
		myTarget = target;
	}
}
