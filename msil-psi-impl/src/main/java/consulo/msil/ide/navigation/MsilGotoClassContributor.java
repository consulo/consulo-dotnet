package consulo.msil.ide.navigation;

import gnu.trove.THashSet;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Icon;

import com.intellij.ide.DataManager;
import com.intellij.navigation.ChooseByNameContributorEx;
import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ArrayUtil;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.FindSymbolParameters;
import com.intellij.util.indexing.IdFilter;
import consulo.annotation.access.RequiredReadAction;
import consulo.awt.TargetAWT;
import consulo.dotnet.module.extension.DotNetModuleLangExtension;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.internal.dotnet.msil.decompiler.util.MsilHelper;
import consulo.msil.MsilFileType;
import consulo.msil.lang.psi.MsilClassEntry;
import consulo.msil.lang.psi.impl.elementType.stub.index.MsilIndexKeys;
import consulo.msil.representation.MsilFileRepresentationManager;
import consulo.msil.representation.MsilFileRepresentationProvider;

/**
 * @author VISTALL
 * @since 05.07.2015
 */
public class MsilGotoClassContributor implements ChooseByNameContributorEx, GotoClassContributor
{
	private static final class NavigatableWithRepresentation extends FakePsiElement implements NavigationItem
	{
		private MsilClassEntry myMsilClassEntry;

		public NavigatableWithRepresentation(MsilClassEntry msilClassEntry)
		{
			myMsilClassEntry = msilClassEntry;
		}

		@Nullable
		@Override
		public String getName()
		{
			return myMsilClassEntry.getName();
		}

		@Nullable
		@Override
		public ItemPresentation getPresentation()
		{
			return ((NavigationItem) myMsilClassEntry).getPresentation();
		}

		@Override
		public boolean equals(Object o)
		{
			if(this == o)
			{
				return true;
			}
			if(o == null || getClass() != o.getClass())
			{
				return false;
			}

			NavigatableWithRepresentation that = (NavigatableWithRepresentation) o;

			if(!myMsilClassEntry.equals(that.myMsilClassEntry))
			{
				return false;
			}

			return true;
		}

		@Override
		public int hashCode()
		{
			return myMsilClassEntry.hashCode();
		}

		@Override
		public void navigate(final boolean requestFocus)
		{
			DataManager.getInstance().getDataContextFromFocus().doWhenDone(dataContext -> {
				final Project project = dataContext.getData(CommonDataKeys.PROJECT);
				assert project != null;
				final Set<LanguageFileType> languageFileTypes = new THashSet<>();
				Module[] modules = ModuleManager.getInstance(project).getModules();
				List<MsilFileRepresentationProvider> extensions = MsilFileRepresentationProvider.EP_NAME.getExtensionList();
				for(Module module : modules)
				{
					final DotNetModuleLangExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleLangExtension.class);
					if(extension == null)
					{
						continue;
					}
					MsilFileRepresentationProvider provider = ContainerUtil.find(extensions, it -> it.getFileType() == extension.getFileType());
					if(provider == null)
					{
						continue;
					}
					languageFileTypes.add(extension.getFileType());
				}

				if(languageFileTypes.isEmpty())
				{
					((NavigationItem) myMsilClassEntry).navigate(requestFocus);
					return;
				}

				final MsilFileRepresentationManager representationManager = MsilFileRepresentationManager.getInstance(project);
				if(languageFileTypes.size() == 1)
				{
					LanguageFileType languageFileType = ContainerUtil.getFirstItem(languageFileTypes);
					PsiFile representationFile = representationManager.getRepresentationFile(languageFileType, myMsilClassEntry.getContainingFile().getVirtualFile());
					representationFile.navigate(requestFocus);
					return;
				}

				languageFileTypes.add(MsilFileType.INSTANCE);
				BaseListPopupStep<LanguageFileType> step = new BaseListPopupStep<LanguageFileType>("Choose " + "language", languageFileTypes.toArray(new LanguageFileType[languageFileTypes.size
						()]))
				{
					@Nonnull
					@Override
					public String getTextFor(LanguageFileType value)
					{
						return value.getLanguage().getDisplayName();
					}

					@Override
					public Icon getIconFor(LanguageFileType aValue)
					{
						return TargetAWT.to(aValue.getIcon());
					}

					@Override
					public PopupStep onChosen(LanguageFileType selectedValue, boolean finalChoice)
					{
						if(selectedValue == MsilFileType.INSTANCE)
						{
							((NavigationItem) myMsilClassEntry).navigate(requestFocus);
							return FINAL_CHOICE;
						}

						PsiFile representationFile = representationManager.getRepresentationFile(selectedValue, myMsilClassEntry.getContainingFile().getVirtualFile());
						representationFile.navigate(requestFocus);
						return FINAL_CHOICE;
					}
				};

				ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(step);
				listPopup.showCenteredInCurrentWindow(project);
			});
		}

		@Override
		public boolean canNavigate()
		{
			return ((NavigationItem) myMsilClassEntry).canNavigate();
		}

		@Override
		public boolean canNavigateToSource()
		{
			return ((NavigationItem) myMsilClassEntry).canNavigateToSource();
		}

		@Override
		public PsiElement getParent()
		{
			return myMsilClassEntry.getParent();
		}
	}

	@Override
	public void processNames(@Nonnull final Processor<String> processor, @Nonnull GlobalSearchScope scope, @Nullable IdFilter filter)
	{
		StubIndex.getInstance().processAllKeys(MsilIndexKeys.TYPE_BY_NAME_INDEX, s ->
		{
			ProgressManager.checkCanceled();
			return processor.process(MsilHelper.cutGenericMarker(s));
		}, scope, filter);
	}

	@Override
	public void processElementsWithName(@Nonnull String name, @Nonnull final Processor<NavigationItem> processor, @Nonnull FindSymbolParameters parameters)
	{
		StubIndex.getInstance().processElements(MsilIndexKeys.TYPE_BY_NAME_INDEX, name, parameters.getProject(), parameters.getSearchScope(), parameters.getIdFilter(), MsilClassEntry.class,
				msilClassEntry ->
				{
					ProgressManager.checkCanceled();
					return processor.process(new NavigatableWithRepresentation(msilClassEntry));
				});
	}

	@Nullable
	@Override
	@RequiredReadAction
	public String getQualifiedName(NavigationItem item)
	{
		if(item instanceof DotNetQualifiedElement)
		{
			return ((DotNetQualifiedElement) item).getPresentableQName();
		}
		return null;
	}

	@Nullable
	@Override
	public String getQualifiedNameSeparator()
	{
		return ".";
	}

	@Nonnull
	@Override
	public String[] getNames(Project project, boolean includeNonProjectItems)
	{
		CommonProcessors.CollectProcessor<String> processor = new CommonProcessors.CollectProcessor<>(ContainerUtil.<String>newTroveSet());
		processNames(processor, GlobalSearchScope.allScope(project), IdFilter.getProjectIdFilter(project, includeNonProjectItems));
		return processor.toArray(ArrayUtil.STRING_ARRAY_FACTORY);
	}

	@Nonnull
	@Override
	public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems)
	{
		CommonProcessors.CollectProcessor<NavigationItem> processor = new CommonProcessors.CollectProcessor<>(ContainerUtil.<NavigationItem>newTroveSet());
		processElementsWithName(name, processor, new FindSymbolParameters(pattern, name, GlobalSearchScope.allScope(project), IdFilter.getProjectIdFilter(project, includeNonProjectItems)));
		return processor.toArray(NavigationItem.ARRAY_FACTORY);
	}
}
