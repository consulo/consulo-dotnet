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

package org.mustbe.consulo.dotnet.ui.profile;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

import org.consulo.module.extension.ModuleExtension;
import org.consulo.module.extension.ModuleExtensionChangeListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.module.ConfigurationProfile;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.module.extension.DotNetMutableModuleExtension;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import com.intellij.openapi.wm.impl.status.TextPanel;
import com.intellij.ui.ClickListener;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.ui.UIUtil;
import lombok.val;

/**
 * @author VISTALL
 * @since 31.01.14
 */
public class ProfileWidget extends EditorBasedWidget implements CustomStatusBarWidget
{
	@NotNull
	private final TextPanel myComponent;

	private boolean myActionEnabled;

	public ProfileWidget(@NotNull Project project)
	{
		super(project);

		myComponent = new TextPanel()
		{
			@Override
			protected void paintComponent(@NotNull final Graphics g)
			{
				super.paintComponent(g);
				if(myActionEnabled && getText() != null)
				{
					final Rectangle r = getBounds();
					final Insets insets = getInsets();
					AllIcons.Ide.Statusbar_arrows.paintIcon(this, g, r.width - insets.right - AllIcons.Ide.Statusbar_arrows.getIconWidth() - 2,
							r.height / 2 - AllIcons.Ide.Statusbar_arrows.getIconHeight() / 2);
				}
			}
		};

		new ClickListener()
		{
			@Override
			public boolean onClick(MouseEvent e, int clickCount)
			{
				update();
				showPopup(e);
				return true;
			}
		}.installOn(myComponent);
		myComponent.setBorder(WidgetBorder.INSTANCE);
	}

	@Override
	public void install(@NotNull StatusBar statusBar)
	{
		super.install(statusBar);

		myProject.getMessageBus().connect().subscribe(ModuleExtension.CHANGE_TOPIC, new ModuleExtensionChangeListener.Adapter()
		{
			@Override
			public void afterExtensionChanged(@NotNull ModuleExtension<?> extension, @NotNull ModuleExtension<?> extension2)
			{
				if(extension instanceof DotNetModuleExtension)
				{
					update();
				}
			}
		});
	}

	private void showPopup(MouseEvent e)
	{
		if(!myActionEnabled)
		{
			return;
		}
		DataContext dataContext = getContext();
		DefaultActionGroup actionGroup = new DefaultActionGroup();

		ConfigurationProfile currentProfile = getCurrentProfile();

		List<ConfigurationProfile> profiles = getProfiles();
		for(val profile : profiles)
		{
			if(profile == currentProfile)
			{
				continue;
			}

			actionGroup.add(new AnAction(profile.getName())
			{
				@Override
				public void actionPerformed(AnActionEvent anActionEvent)
				{
					Project project = getProject();
					VirtualFile selectedFile = getSelectedFile();
					if(selectedFile == null || project == null)
					{
						return;
					}
					val moduleForFile = ModuleUtilCore.findModuleForFile(selectedFile, project);
					if(moduleForFile == null)
					{
						return;
					}
					val modifiableModel = ModuleRootManager.getInstance(moduleForFile).getModifiableModel();

					DotNetMutableModuleExtension extension = modifiableModel.getExtension(DotNetMutableModuleExtension.class);

					assert extension != null;
					extension.setCurrentProfile(profile.getName());

					new WriteAction<Object>()
					{
						@Override
						protected void run(Result<Object> objectResult) throws Throwable
						{
							modifiableModel.commit();
						}
					}.execute();
				}
			});
		}

		ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup("Select profile", actionGroup, dataContext,
				JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, false);
		Dimension dimension = popup.getContent().getPreferredSize();
		Point at = new Point(0, -dimension.height);
		popup.show(new RelativePoint(e.getComponent(), at));
		Disposer.register(this, popup); // destroy popup on unexpected project close
	}

	@NotNull
	private DataContext getContext()
	{
		Editor editor = getEditor();
		DataContext parent = DataManager.getInstance().getDataContext((Component) myStatusBar);
		return SimpleDataContext.getSimpleContext(PlatformDataKeys.VIRTUAL_FILE_ARRAY.getName(), new VirtualFile[]{getSelectedFile()},
				SimpleDataContext.getSimpleContext(CommonDataKeys.PROJECT.getName(), getProject(),
						SimpleDataContext.getSimpleContext(PlatformDataKeys.CONTEXT_COMPONENT.getName(), editor == null ? null : editor.getComponent
								(), parent)));
	}

	@NotNull
	private List<ConfigurationProfile> getProfiles()
	{
		VirtualFile file = getSelectedFile();
		Project project = getProject();

		Module moduleForFile = file == null || project == null ? null : ModuleUtilCore.findModuleForFile(file, project);
		DotNetModuleExtension<?> dotNetModuleExtension = moduleForFile == null ? null : ModuleUtilCore.getExtension(moduleForFile,
				DotNetModuleExtension.class);

		return dotNetModuleExtension == null ? Collections.<ConfigurationProfile>emptyList() : dotNetModuleExtension.getProfiles();
	}

	@Nullable
	private ConfigurationProfile getCurrentProfile()
	{
		VirtualFile file = getSelectedFile();
		Project project = getProject();

		Module moduleForFile = file == null || project == null ? null : ModuleUtilCore.findModuleForFile(file, project);
		DotNetModuleExtension<?> dotNetModuleExtension = moduleForFile == null ? null : ModuleUtilCore.getExtension(moduleForFile,
				DotNetModuleExtension.class);

		return dotNetModuleExtension == null ? null : dotNetModuleExtension.getCurrentProfile();
	}

	private void update()
	{
		UIUtil.invokeLaterIfNeeded(new Runnable()
		{
			@Override
			public void run()
			{
				myActionEnabled = false;

				String toolTipText = null;
				String panelText = null;

				ConfigurationProfile profile = getCurrentProfile();
				if(profile != null)
				{
					myActionEnabled = true;

					toolTipText = "Profile: " + profile.getName();
					panelText = ".NET: " + profile.getName();
					myComponent.setVisible(true);
				}

				myActionEnabled = profile != null;
				myComponent.setVisible(profile != null);

				myComponent.resetColor();

				String toDoComment;

				if(myActionEnabled)
				{
					toDoComment = "Click to change";
					myComponent.setForeground(UIUtil.getActiveTextColor());
					myComponent.setTextAlignment(Component.LEFT_ALIGNMENT);
				}
				else
				{
					toDoComment = "";
					myComponent.setForeground(UIUtil.getInactiveTextColor());
					myComponent.setTextAlignment(Component.CENTER_ALIGNMENT);
				}

				myComponent.setToolTipText(String.format("%s%n%s", toolTipText, toDoComment));
				myComponent.setText(panelText);


				if(myStatusBar != null)
				{
					myStatusBar.updateWidget(ID());
				}
			}
		});
	}


	@Override
	public void selectionChanged(@NotNull FileEditorManagerEvent event)
	{
		if(ApplicationManager.getApplication().isUnitTestMode())
		{
			return;
		}
		update();
	}

	@Override
	public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file)
	{
		update();
	}

	@NotNull
	@Override
	public String ID()
	{
		return "DotNetProfileWidget";
	}

	@Nullable
	@Override
	public WidgetPresentation getPresentation(@NotNull PlatformType platformType)
	{
		return null;
	}

	@Override
	public JComponent getComponent()
	{
		return myComponent;
	}
}
