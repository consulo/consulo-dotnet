package org.mustbe.consulo.nunit.run;

import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.application.options.ModuleListCellRenderer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.util.ui.FormBuilder;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.03.14
 */
public class NUnitConfigurationEditor extends SettingsEditor<NUnitConfiguration>
{
	private final Project myProject;

	private ComboBox myModuleComboBox;

	public NUnitConfigurationEditor(Project project)
	{
		myProject = project;
	}

	@Override
	protected void resetEditorFrom(NUnitConfiguration runConfiguration)
	{
		myModuleComboBox.setSelectedItem(runConfiguration.getConfigurationModule().getModule());
	}

	@Override
	protected void applyEditorTo(NUnitConfiguration runConfiguration) throws ConfigurationException
	{
		runConfiguration.getConfigurationModule().setModule((Module) myModuleComboBox.getSelectedItem());
	}

	@NotNull
	@Override
	protected JComponent createEditor()
	{
		myModuleComboBox = new ComboBox();
		myModuleComboBox.setRenderer(new ModuleListCellRenderer());
		for(val module : ModuleManager.getInstance(myProject).getModules())
		{
			if(ModuleUtilCore.getExtension(module, DotNetModuleExtension.class) != null)
			{
				myModuleComboBox.addItem(module);
			}
		}

		FormBuilder formBuilder = FormBuilder.createFormBuilder();
		formBuilder.addLabeledComponent("Module", myModuleComboBox);

		return formBuilder.getPanel();
	}
}
