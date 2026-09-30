package consulo.dotnet.run.impl;

import consulo.application.Application;
import consulo.dotnet.execution.localize.DotNetExecutionLocalize;
import consulo.dotnet.module.extension.DotNetRunModuleExtension;
import consulo.execution.ui.CommonProgramParametersLayout;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.process.ProcessConsoleType;
import consulo.project.Project;
import consulo.ui.ComboBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.dialog.DialogService;
import consulo.ui.util.FormBuilder;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DotNetProgramParametersPanel extends CommonProgramParametersLayout<DotNetConfiguration> {
    private final Project myProject;

    private @Nullable ComboBox<Module> myModuleComboBox;
    private @Nullable ComboBox<ProcessConsoleType> myConsoleTypeBox;

    public DotNetProgramParametersPanel(Project project) {
        super(project.getApplication().getInstance(DialogService.class));
        myProject = project;
    }

    @Override
    @RequiredUIAccess
    protected void addAfter(FormBuilder builder) {
        List<Module> modules = new ArrayList<>();
        for (Module module : ModuleManager.getInstance(myProject).getModules()) {
            if (ModuleUtilCore.getExtension(module, DotNetRunModuleExtension.class) != null) {
                modules.add(module);
            }
        }

        ComboBox<Module> moduleComboBox = ComboBox.create(modules);
        moduleComboBox.setRender((presentation, item) ->
        {
            Module module = item.getValue();
            if (module != null) {
                presentation.withIcon(PlatformIconGroup.nodesModule());
                presentation.append(module.getName());
            }
        });
        moduleComboBox.addValueListener(event -> setModuleContext(event.getValue()));
        myModuleComboBox = moduleComboBox;
        builder.addLabeled(DotNetExecutionLocalize.runConfigurationModuleLabel(), moduleComboBox);

        ComboBox<ProcessConsoleType> consoleTypeBox = ComboBox.create(ProcessConsoleType.listSupported());
        consoleTypeBox.setRender((presentation, item) ->
        {
            ProcessConsoleType consoleType = item.getValue();
            if (consoleType != null) {
                presentation.append(consoleType.getDisplayName());
            }
        });
        myConsoleTypeBox = consoleTypeBox;
        builder.addLabeled(DotNetExecutionLocalize.runConfigurationConsoleLabel(), consoleTypeBox);
    }

    @Override
    @RequiredUIAccess
    public void reset(DotNetConfiguration configuration) {
        super.reset(configuration);

        Module module = configuration.getConfigurationModule().getModule();

        ComboBox<Module> moduleComboBox = myModuleComboBox;
        if (moduleComboBox != null) {
            moduleComboBox.setValue(module, false);
        }
        setModuleContext(module);

        ComboBox<ProcessConsoleType> consoleTypeBox = myConsoleTypeBox;
        if (consoleTypeBox != null) {
            consoleTypeBox.setValue(configuration.getConsoleType());
        }
    }

    @Override
    @RequiredUIAccess
    public void apply(DotNetConfiguration configuration) {
        super.apply(configuration);

        ComboBox<Module> moduleComboBox = myModuleComboBox;
        configuration.getConfigurationModule().setModule(moduleComboBox == null ? null : moduleComboBox.getValue());

        ComboBox<ProcessConsoleType> consoleTypeBox = myConsoleTypeBox;
        if (consoleTypeBox != null) {
            configuration.setConsoleType(consoleTypeBox.getValue());
        }
    }
}
