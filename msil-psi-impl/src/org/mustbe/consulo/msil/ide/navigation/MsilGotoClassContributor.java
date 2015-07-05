package org.mustbe.consulo.msil.ide.navigation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredDispatchThread;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleLangExtension;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import org.mustbe.consulo.msil.MsilFileType;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index.MsilIndexKeys;
import org.mustbe.consulo.msil.representation.MsilFileRepresentationManager;
import org.mustbe.consulo.msil.representation.MsilFileRepresentationProvider;
import org.mustbe.dotnet.msil.decompiler.util.MsilHelper;
import com.intellij.ide.DataManager;
import com.intellij.navigation.ChooseByNameContributorEx;
import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ArrayUtil;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Consumer;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.FindSymbolParameters;
import com.intellij.util.indexing.IdFilter;

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
			DataManager.getInstance().getDataContextFromFocus().doWhenDone(new Consumer<DataContext>()
			{
				@RequiredDispatchThread
				@Override
				public void consume(DataContext dataContext)
				{
					final Project project = CommonDataKeys.PROJECT.getData(dataContext);
					assert project != null;
					final List<LanguageFileType> languageFileTypes = new ArrayList<LanguageFileType>();
					Module[] modules = ModuleManager.getInstance(project).getModules();
					MsilFileRepresentationProvider[] extensions = MsilFileRepresentationProvider.EP_NAME.getExtensions();
					for(Module module : modules)
					{
						final DotNetModuleLangExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleLangExtension.class);
						if(extension == null)
						{
							continue;
						}
						MsilFileRepresentationProvider provider = ContainerUtil.find(extensions, new Condition<MsilFileRepresentationProvider>()
						{
							@Override
							public boolean value(MsilFileRepresentationProvider msilFileRepresentationProvider)
							{
								return msilFileRepresentationProvider.getFileType() == extension.getFileType();
							}
						});
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
						PsiFile representationFile = representationManager.getRepresentationFile(languageFileType,
						myMsilClassEntry.getContainingFile()
								.getVirtualFile());
						representationFile.navigate(requestFocus);
						return;
					}

					languageFileTypes.add(MsilFileType.INSTANCE);
					BaseListPopupStep<LanguageFileType> step = new BaseListPopupStep<LanguageFileType>("Choose language", languageFileTypes)
					{
						@NotNull
						@Override
						public String getTextFor(LanguageFileType value)
						{
							return value.getLanguage().getDisplayName();
						}

						@Override
						public Icon getIconFor(LanguageFileType aValue)
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

							PsiFile representationFile = representationManager.getRepresentationFile(selectedValue,
									myMsilClassEntry.getContainingFile().getVirtualFile());
							representationFile.navigate(requestFocus);
							return FINAL_CHOICE;
						}
					};

					ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(step);
					listPopup.showInFocusCenter();
				}
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
	public void processNames(@NotNull final Processor<String> processor, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter)
	{
		StubIndex.getInstance().processAllKeys(MsilIndexKeys.TYPE_BY_NAME_INDEX, new Processor<String>()
		{
			@Override
			public boolean process(String s)
			{
				return processor.process(MsilHelper.cutGenericMarker(s));
			}
		}, scope, filter);
	}

	@Override
	public void processElementsWithName(@NotNull String name,
			@NotNull final Processor<NavigationItem> processor,
			@NotNull FindSymbolParameters parameters)
	{
		StubIndex.getInstance().processElements(MsilIndexKeys.TYPE_BY_NAME_INDEX, name, parameters.getProject(), parameters.getSearchScope(),
				parameters.getIdFilter(), MsilClassEntry.class, new Processor<MsilClassEntry>()
		{
			@Override
			public boolean process(MsilClassEntry msilClassEntry)
			{
				return processor.process(new NavigatableWithRepresentation(msilClassEntry));
			}
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

	@NotNull
	@Override
	public String[] getNames(Project project, boolean includeNonProjectItems)
	{
		CommonProcessors.CollectProcessor<String> processor = new CommonProcessors.CollectProcessor<String>(ContainerUtil.<String>newTroveSet());
		processNames(processor, GlobalSearchScope.allScope(project), IdFilter.getProjectIdFilter(project, includeNonProjectItems));
		return processor.toArray(ArrayUtil.EMPTY_STRING_ARRAY);
	}

	@NotNull
	@Override
	public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems)
	{
		CommonProcessors.CollectProcessor<NavigationItem> processor = new CommonProcessors.CollectProcessor<NavigationItem>(ContainerUtil
				.<NavigationItem>newTroveSet());
		processElementsWithName(name, processor, new FindSymbolParameters(pattern, name, GlobalSearchScope.allScope(project),
				IdFilter.getProjectIdFilter(project, includeNonProjectItems)));
		return processor.toArray(NavigationItem.EMPTY_NAVIGATION_ITEM_ARRAY);
	}
}
