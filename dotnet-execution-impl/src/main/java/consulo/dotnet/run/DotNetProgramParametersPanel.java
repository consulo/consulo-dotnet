package consulo.dotnet.run;

import com.intellij.application.options.ModuleListCellRenderer;
import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.execution.ui.CommonProgramParametersPanel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.ColoredListCellRenderer;
import consulo.dotnet.module.extension.DotNetRunModuleExtension;
import consulo.execution.console.ConsoleType;
import consulo.ui.annotation.RequiredUIAccess;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @since 2020-10-23
 */
public class DotNetProgramParametersPanel extends CommonProgramParametersPanel
{
	private ComboBox<Module> myModuleComboBox;
	private ComboBox<ConsoleType> myConsoleTypeBox;

	private final Project myProject;
	private LabeledComponent<ComboBox<Module>> myModuleLabeled;
	private LabeledComponent<ComboBox<ConsoleType>> myConsoleTypeLabeled;

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

		List<ConsoleType> consoleTypeList = new ArrayList<>();
		for(ConsoleType consoleType : ConsoleType.values())
		{
			if(consoleType.isAvaliable())
			{
				consoleTypeList.add(consoleType);
			}
		}
		myConsoleTypeBox = new ComboBox<>(new CollectionComboBoxModel<>(consoleTypeList));
		myConsoleTypeBox.setRenderer(new ColoredListCellRenderer<ConsoleType>()
		{
			@Override
			protected void customizeCellRenderer(@Nonnull JList list, ConsoleType value, int index, boolean selected, boolean hasFocus)
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
		dotNetConfiguration.setConsoleType((ConsoleType) myConsoleTypeBox.getSelectedItem());
	}
}
