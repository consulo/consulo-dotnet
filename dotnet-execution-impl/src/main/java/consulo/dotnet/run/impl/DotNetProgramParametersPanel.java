package consulo.dotnet.run.impl;

import consulo.dotnet.module.extension.DotNetRunModuleExtension;
import consulo.execution.CommonProgramRunConfigurationParameters;
import consulo.execution.ui.awt.CommonProgramParametersPanel;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.module.ui.awt.ModuleListCellRenderer;
import consulo.process.ProcessConsoleType;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.awt.CollectionComboBoxModel;
import consulo.ui.ex.awt.ColoredListCellRenderer;
import consulo.ui.ex.awt.ComboBox;
import consulo.ui.ex.awt.LabeledComponent;
import jakarta.annotation.Nonnull;

import javax.swing.*;
import java.awt.event.ItemEvent;

/**
 * @author VISTALL
 * @since 2020-10-23
 */
public class DotNetProgramParametersPanel extends CommonProgramParametersPanel
{
	private ComboBox<Module> myModuleComboBox;
	private ComboBox<ProcessConsoleType> myConsoleTypeBox;

	private final Project myProject;
	private LabeledComponent<ComboBox<Module>> myModuleLabeled;
	private LabeledComponent<ComboBox<ProcessConsoleType>> myConsoleTypeLabeled;

	public DotNetProgramParametersPanel(Project project)
	{
		super(false);
		myProject = project;
		init();
	}

	@Override
	protected void init()
	{
		super.init();

		setPreferredSize(null);
	}

	@Override
	@RequiredUIAccess
	protected void initComponents()
	{
		super.initComponents();

		myModuleComboBox = new ComboBox<>();
		myModuleComboBox.setRenderer(new ModuleListCellRenderer());
		for(Module module : ModuleManager.getInstance(myProject).getModules())
		{
			if(ModuleUtilCore.getExtension(module, DotNetRunModuleExtension.class) != null)
			{
				myModuleComboBox.addItem(module);
			}
		}

		myModuleComboBox.addItemListener(e ->
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				setModuleContext((Module) myModuleComboBox.getSelectedItem());
			}
		});

		myModuleLabeled = LabeledComponent.create(myModuleComboBox, "Module");
		add(myModuleLabeled);

		myConsoleTypeBox = new ComboBox<>(new CollectionComboBoxModel<>(ProcessConsoleType.listSupported()));
		myConsoleTypeBox.setRenderer(new ColoredListCellRenderer<ProcessConsoleType>()
		{
			@Override
			protected void customizeCellRenderer(@Nonnull JList list, ProcessConsoleType value, int index, boolean selected, boolean hasFocus)
			{
				append(value.getDisplayName().get());
			}
		});
		myConsoleTypeLabeled = LabeledComponent.create(myConsoleTypeBox, "Console");

		add(myConsoleTypeLabeled);
	}

	@Override
	protected void setupAnchor()
	{
		super.setupAnchor();
		myModuleLabeled.setAnchor(myAnchor);
		myConsoleTypeLabeled.setAnchor(myAnchor);
	}

	@Override
	public void reset(CommonProgramRunConfigurationParameters configuration)
	{
		super.reset(configuration);

		DotNetConfiguration dotNetConfiguration = (DotNetConfiguration) configuration;
		myModuleComboBox.setSelectedItem(dotNetConfiguration.getConfigurationModule().getModule());
		setModuleContext(dotNetConfiguration.getConfigurationModule().getModule());
		myConsoleTypeBox.setSelectedItem(dotNetConfiguration.getConsoleType());
	}

	@Override
	public void applyTo(CommonProgramRunConfigurationParameters configuration)
	{
		super.applyTo(configuration);

		DotNetConfiguration dotNetConfiguration = (DotNetConfiguration) configuration;
		dotNetConfiguration.getConfigurationModule().setModule((Module) myModuleComboBox.getSelectedItem());
		dotNetConfiguration.setConsoleType((ProcessConsoleType) myConsoleTypeBox.getSelectedItem());
	}
}
