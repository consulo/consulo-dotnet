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

package consulo.dotnet.run.coverage;

import gnu.trove.THashSet;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.psi.DotNetFile;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.coverage.CoverageAnnotator;
import com.intellij.coverage.CoverageEngine;
import com.intellij.coverage.CoverageFileProvider;
import com.intellij.coverage.CoverageRunner;
import com.intellij.coverage.CoverageSuite;
import com.intellij.coverage.CoverageSuitesBundle;
import com.intellij.coverage.view.CoverageViewExtension;
import com.intellij.coverage.view.CoverageViewManager;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;

/**
 * @author VISTALL
 * @since 10.01.15
 */
public class DotNetCoverageEngine extends CoverageEngine
{
	@Override
	public boolean isApplicableTo(@Nullable RunConfigurationBase conf)
	{
		return conf instanceof DotNetConfigurationWithCoverage;
	}

	@Override
	public CoverageViewExtension createCoverageViewExtension(Project project,
			CoverageSuitesBundle suiteBundle,
			CoverageViewManager.StateBean stateBean)
	{
		return new DotNetCoverageViewExtension(project, suiteBundle, stateBean);
	}

	@Override
	public boolean canHavePerTestCoverage(@Nullable RunConfigurationBase conf)
	{
		return false;
	}

	@NotNull
	@Override
	public CoverageEnabledConfiguration createCoverageEnabledConfiguration(@Nullable RunConfigurationBase conf)
	{
		if(!(conf instanceof DotNetConfigurationWithCoverage))
		{
			throw new IllegalArgumentException();
		}
		return new DotNetCoverageEnabledConfiguration((DotNetConfigurationWithCoverage) conf);
	}

	@Nullable
	@Override
	public CoverageSuite createCoverageSuite(@NotNull CoverageRunner covRunner,
			@NotNull String name,
			@NotNull CoverageFileProvider coverageDataFileProvider,
			@Nullable String[] filters,
			long lastCoverageTimeStamp,
			@Nullable String suiteToMerge,
			boolean coverageByTestEnabled,
			boolean tracingEnabled,
			boolean trackTestFolders,
			Project project)
	{
		return new DotNetCoverageSuite(name, coverageDataFileProvider, lastCoverageTimeStamp, coverageByTestEnabled, tracingEnabled,
				trackTestFolders, covRunner, project, this);
	}

	@Nullable
	@Override
	public String getQualifiedName(@NotNull File outputFile, @NotNull final PsiFile sourceFile)
	{

		return ApplicationManager.getApplication().runReadAction(new Computable<String>()
		{
			@Override
			public String compute()
			{
				final Ref<String> ref = Ref.create();
				sourceFile.accept(new PsiRecursiveElementVisitor()
				{
					@Override
					public void visitElement(PsiElement element)
					{
						super.visitElement(element);
						if(element instanceof DotNetTypeDeclaration)
						{
							ref.set(((DotNetTypeDeclaration) element).getVmQName());
						}
					}
				});
				return ref.get();
			}
		});
	}

	@Nullable
	@Override
	public CoverageSuite createCoverageSuite(@NotNull CoverageRunner covRunner,
			@NotNull String name,
			@NotNull CoverageFileProvider coverageDataFileProvider,
			@NotNull CoverageEnabledConfiguration config)
	{
		return new DotNetCoverageSuite(name, coverageDataFileProvider, System.currentTimeMillis(), false, false, true, covRunner, this);
	}

	@Nullable
	@Override
	public CoverageSuite createEmptyCoverageSuite(@NotNull CoverageRunner coverageRunner)
	{
		return new DotNetCoverageSuite(this);
	}

	@NotNull
	@Override
	public CoverageAnnotator getCoverageAnnotator(Project project)
	{
		return new DotNetCoverageAnnotator(project);
	}

	@Override
	public boolean coverageEditorHighlightingApplicableTo(@NotNull PsiFile psiFile)
	{
		return psiFile instanceof DotNetFile;
	}

	@Override
	public boolean acceptedByFilters(@NotNull PsiFile psiFile, @NotNull CoverageSuitesBundle suite)
	{
		return true;
	}

	@Override
	public boolean recompileProjectAndRerunAction(@NotNull Module module, @NotNull CoverageSuitesBundle suite, @NotNull Runnable chooseSuiteAction)
	{
		return false;
	}

	@NotNull
	@Override
	public Set<String> getQualifiedNames(@NotNull final PsiFile sourceFile)
	{
		return ApplicationManager.getApplication().runReadAction(new Computable<Set<String>>()
		{
			@Override
			public Set<String> compute()
			{
				final Set<String> set = new THashSet<String>();
				sourceFile.accept(new PsiRecursiveElementVisitor()
				{
					@Override
					public void visitElement(PsiElement element)
					{
						super.visitElement(element);
						if(element instanceof DotNetTypeDeclaration)
						{
							set.add(((DotNetTypeDeclaration) element).getVmQName());
						}
					}
				});
				return set;
			}
		});
	}

	@Override
	public List<PsiElement> findTestsByNames(@NotNull String[] testNames, @NotNull Project project)
	{
		return null;
	}

	@Nullable
	@Override
	public String getTestMethodName(@NotNull PsiElement element, @NotNull AbstractTestProxy testProxy)
	{
		return null;
	}

	@Override
	public String getPresentableText()
	{
		return ".NET Coverage";
	}
}
