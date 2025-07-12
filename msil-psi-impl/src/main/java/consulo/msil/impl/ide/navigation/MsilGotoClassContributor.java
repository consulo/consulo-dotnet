package consulo.msil.impl.ide.navigation;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.progress.ProgressManager;
import consulo.application.util.function.Processor;
import consulo.content.scope.SearchScope;
import consulo.dataContext.DataManager;
import consulo.dotnet.module.extension.DotNetModuleLangExtension;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.ide.navigation.ChooseByNameContributorEx;
import consulo.ide.navigation.GotoClassOrTypeContributor;
import consulo.internal.dotnet.msil.decompiler.util.MsilHelper;
import consulo.language.editor.CommonDataKeys;
import consulo.language.file.LanguageFileType;
import consulo.language.impl.psi.FakePsiElement;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.search.FindSymbolParameters;
import consulo.language.psi.stub.IdFilter;
import consulo.language.psi.stub.StubIndex;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.msil.MsilFileType;
import consulo.msil.impl.lang.psi.impl.elementType.stub.index.MsilIndexKeys;
import consulo.msil.lang.psi.MsilClassEntry;
import consulo.msil.representation.MsilFileRepresentationManager;
import consulo.msil.representation.MsilFileRepresentationProvider;
import consulo.navigation.ItemPresentation;
import consulo.navigation.NavigationItem;
import consulo.project.Project;
import consulo.ui.ex.popup.BaseListPopupStep;
import consulo.ui.ex.popup.JBPopupFactory;
import consulo.ui.ex.popup.ListPopup;
import consulo.ui.ex.popup.PopupStep;
import consulo.ui.image.Image;
import consulo.util.collection.ContainerUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author VISTALL
 * @since 05.07.2015
 */
@ExtensionImpl
public class MsilGotoClassContributor implements ChooseByNameContributorEx, GotoClassOrTypeContributor
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
				final Set<LanguageFileType> languageFileTypes = new HashSet<>();
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
						return value.getLanguage().getDisplayName().get();
					}

					@Override
					public Image getIconFor(LanguageFileType aValue)
					{
						return aValue.getIcon();
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
	public void processNames(@Nonnull final Processor<String> processor, @Nonnull SearchScope scope, @Nullable IdFilter filter)
	{
		StubIndex.getInstance().processAllKeys(MsilIndexKeys.TYPE_BY_NAME_INDEX, s ->
		{
			ProgressManager.checkCanceled();
			return processor.process(MsilHelper.cutGenericMarker(s));
		}, (GlobalSearchScope) scope, filter);
	}

	@Override
	public void processElementsWithName(@Nonnull String name, @Nonnull final Processor<NavigationItem> processor, @Nonnull FindSymbolParameters parameters)
	{
		StubIndex.getInstance().processElements(MsilIndexKeys.TYPE_BY_NAME_INDEX, name, parameters.getProject(), (GlobalSearchScope) parameters.getSearchScope(), parameters.getIdFilter(),
				MsilClassEntry.class,
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
}
