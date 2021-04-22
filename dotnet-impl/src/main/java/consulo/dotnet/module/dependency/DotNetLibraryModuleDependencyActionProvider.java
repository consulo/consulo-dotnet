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

package consulo.dotnet.module.dependency;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.classpath.ClasspathPanel;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Computable;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import consulo.dotnet.module.extension.DotNetSimpleModuleExtension;
import consulo.localize.LocalizeValue;
import consulo.module.extension.ModuleExtensionProviderEP;
import consulo.module.extension.impl.ModuleExtensionProviders;
import consulo.roots.ModuleRootLayer;
import consulo.roots.ui.configuration.LibrariesConfigurator;
import consulo.roots.ui.configuration.ModulesConfigurator;
import consulo.roots.ui.configuration.classpath.AddModuleDependencyActionProvider;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.image.Image;
import consulo.util.concurrent.AsyncResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author VISTALL
 * @since 28.09.14
 */
public class DotNetLibraryModuleDependencyActionProvider implements AddModuleDependencyActionProvider<List<Map.Entry<String, String>>, DotNetLibraryModuleDependencyContext>
{
	private static class SelectDialog extends DialogWrapper
	{
		private final JBList<Map.Entry<String, String>> myLibraryList;
		@Nonnull
		private final DotNetLibraryModuleDependencyContext myContext;

		protected SelectDialog(@Nonnull DotNetLibraryModuleDependencyContext context, @Nonnull Image defaultIcon)
		{
			super(context.getProject(), false);
			myContext = context;
			setTitle("Select Library");

			myLibraryList = new JBList<>();
			myLibraryList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			myLibraryList.setCellRenderer(new ColoredListCellRenderer<Map.Entry<String, String>>()
			{
				@Override
				protected void customizeCellRenderer(@Nonnull JList<? extends Map.Entry<String, String>> list, Map.Entry<String, String> entry, int i, boolean b, boolean b1)
				{
					setIcon(defaultIcon);

					append(entry.getKey());
					append(" ");
					append("(" + entry.getValue() + ")", SimpleTextAttributes.GRAY_ATTRIBUTES);
				}
			});

			init();
		}

		@Nullable
		@Override
		protected String getDimensionServiceKey()
		{
			setScalableSize(300, 600);
			return super.getDimensionServiceKey();
		}

		@Nullable
		@Override
		protected Border createContentPaneBorder()
		{
			return JBUI.Borders.empty();
		}

		@Nullable
		@Override
		protected JComponent createCenterPanel()
		{
			reloadSystemLibraries();
			return ScrollPaneFactory.createScrollPane(myLibraryList, true);
		}

		@Nonnull
		public List<Map.Entry<String, String>> getSelectedValues()
		{
			return myLibraryList.getSelectedValuesList();
		}

		private void reloadSystemLibraries()
		{
			final DotNetSimpleModuleExtension<?> extension = myContext.getClasspathPanel().getRootModel().getExtension(DotNetSimpleModuleExtension.class);
			if(extension == null)
			{
				return;
			}
			myLibraryList.setPaintBusy(true);
			Application application = Application.get();

			application.executeOnPooledThread((Runnable) () -> {
				final Map<String, String> availableSystemLibraries = myContext.getAvailableSystemLibraries();

				final Map<String, String> map = application.runReadAction((Computable<Map<String, String>>) () -> {
					Map<String, String> map1 = new TreeMap<>();

					for(Map.Entry<String, String> entry : availableSystemLibraries.entrySet())
					{
						if(DotNetLibraryModuleDependencyContext.findOrderEntry(entry.getKey(), myContext.getClasspathPanel().getRootModel()) != null)
						{
							continue;
						}
						map1.put(entry.getKey(), entry.getValue());
					}
					return map1;
				});

				UIUtil.invokeLaterIfNeeded(() -> {
					CollectionListModel<Map.Entry<String, String>> model = new CollectionListModel<Map.Entry<String, String>>(map.entrySet());

					myLibraryList.setModel(model);
					myLibraryList.setPaintBusy(false);
				});
			});
		}

	}

	@Override
	public boolean isAvailable(@Nonnull DotNetLibraryModuleDependencyContext context)
	{
		ModifiableRootModel model = context.getClasspathPanel().getRootModel();
		return model.getExtension(DotNetSimpleModuleExtension.class) != null;
	}

	@Override
	public DotNetLibraryModuleDependencyContext createContext(@Nonnull ClasspathPanel classpathPanel,
															  @Nonnull ModulesConfigurator modulesConfigurator,
															  @Nonnull LibrariesConfigurator librariesConfigurator)
	{
		return new DotNetLibraryModuleDependencyContext(classpathPanel, modulesConfigurator, librariesConfigurator);
	}

	@Nonnull
	@Override
	public LocalizeValue getActionName(@Nonnull ModuleRootLayer moduleRootLayer)
	{
		return LocalizeValue.of(".NET Standard Library");
	}

	@Nonnull
	@Override
	public Image getIcon(@Nonnull ModuleRootLayer moduleRootLayer)
	{
		DotNetSimpleModuleExtension extension = moduleRootLayer.getExtension(DotNetSimpleModuleExtension.class);
		if(extension != null)
		{
			ModuleExtensionProviderEP provider = ModuleExtensionProviders.findProvider(extension.getId());
			assert provider != null;
			return provider.getIcon();
		}
		throw new IllegalArgumentException("No .NET extension");
	}

	@RequiredUIAccess
	@Nonnull
	@Override
	public AsyncResult<List<Map.Entry<String, String>>> invoke(@Nonnull DotNetLibraryModuleDependencyContext context)
	{
		Image defaultIcon = getIcon(context.getClasspathPanel().getRootModel());

		AsyncResult<List<Map.Entry<String, String>>> result = AsyncResult.undefined();

		SelectDialog dialog = new SelectDialog(context, defaultIcon);
		AsyncResult<Void> showAsync = dialog.showAsync();

		showAsync.doWhenDone(() -> result.setDone(dialog.getSelectedValues()));
		showAsync.doWhenRejected((Runnable) result::setRejected);
		return result;
	}
}
