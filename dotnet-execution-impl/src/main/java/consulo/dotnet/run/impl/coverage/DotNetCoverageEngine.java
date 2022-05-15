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

package consulo.dotnet.run.impl.coverage;

import consulo.application.ApplicationManager;
import consulo.dotnet.psi.DotNetFile;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.run.coverage.DotNetConfigurationWithCoverage;
import consulo.execution.configuration.RunConfigurationBase;
import consulo.execution.coverage.*;
import consulo.execution.test.AbstractTestProxy;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiRecursiveElementVisitor;
import consulo.module.Module;
import consulo.project.Project;
import consulo.util.lang.ref.SimpleReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

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

	@Nonnull
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
	public CoverageSuite createCoverageSuite(@Nonnull CoverageRunner covRunner,
			@Nonnull String name,
			@Nonnull CoverageFileProvider coverageDataFileProvider,
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
	public String getQualifiedName(@Nonnull File outputFile, @Nonnull final PsiFile sourceFile)
	{
		return ApplicationManager.getApplication().runReadAction(new Supplier<String>()
		{
			@Override
			public String get()
			{
				final SimpleReference<String> ref = SimpleReference.create();
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
	public CoverageSuite createCoverageSuite(@Nonnull CoverageRunner covRunner,
			@Nonnull String name,
			@Nonnull CoverageFileProvider coverageDataFileProvider,
			@Nonnull CoverageEnabledConfiguration config)
	{
		return new DotNetCoverageSuite(name, coverageDataFileProvider, System.currentTimeMillis(), false, false, true, covRunner, this);
	}

	@Nullable
	@Override
	public CoverageSuite createEmptyCoverageSuite(@Nonnull CoverageRunner coverageRunner)
	{
		return new DotNetCoverageSuite(this);
	}

	@Nonnull
	@Override
	public CoverageAnnotator getCoverageAnnotator(Project project)
	{
		return new DotNetCoverageAnnotator(project);
	}

	@Override
	public boolean coverageEditorHighlightingApplicableTo(@Nonnull PsiFile psiFile)
	{
		return psiFile instanceof DotNetFile;
	}

	@Override
	public boolean acceptedByFilters(@Nonnull PsiFile psiFile, @Nonnull CoverageSuitesBundle suite)
	{
		return true;
	}

	@Override
	public boolean recompileProjectAndRerunAction(@Nonnull Module module, @Nonnull CoverageSuitesBundle suite, @Nonnull Runnable chooseSuiteAction)
	{
		return false;
	}

	@Nonnull
	@Override
	public Set<String> getQualifiedNames(@Nonnull final PsiFile sourceFile)
	{
		return ApplicationManager.getApplication().runReadAction(new Supplier<Set<String>>()
		{
			@Override
			public Set<String> get()
			{
				final Set<String> set = new HashSet<String>();
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
	public List<PsiElement> findTestsByNames(@Nonnull String[] testNames, @Nonnull Project project)
	{
		return null;
	}

	@Nullable
	@Override
	public String getTestMethodName(@Nonnull PsiElement element, @Nonnull AbstractTestProxy testProxy)
	{
		return null;
	}

	@Override
	public String getPresentableText()
	{
		return ".NET Coverage";
	}
}
