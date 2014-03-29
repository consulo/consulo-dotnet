package org.mustbe.consulo.nunit.run;

import java.util.ArrayList;
import java.util.Collection;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.compiler.DotNetMacros;
import org.mustbe.consulo.dotnet.module.MainConfigurationLayer;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.nunit.module.extension.NUnitModuleExtension;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.sm.runner.SMTestProxy;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.03.14
 */
public class NUnitConfiguration extends ModuleBasedConfiguration<RunConfigurationModule>
{
	public NUnitConfiguration(String name, RunConfigurationModule configurationModule, ConfigurationFactory factory)
	{
		super(name, configurationModule, factory);
	}

	@Override
	public Collection<Module> getValidModules()
	{
		val list = new ArrayList<Module>();
		for(val module : ModuleManager.getInstance(getProject()).getModules())
		{
			if(ModuleUtilCore.getExtension(module, NUnitModuleExtension.class) != null)
			{
				list.add(module);
			}
		}
		return list;
	}

	@Override
	public void readExternal(Element element) throws InvalidDataException
	{
		super.readExternal(element);
		readModule(element);
	}

	@Override
	public void writeExternal(Element element) throws WriteExternalException
	{
		super.writeExternal(element);
		writeModule(element);
	}

	@NotNull
	@Override
	public SettingsEditor<? extends RunConfiguration> getConfigurationEditor()
	{
		return new NUnitConfigurationEditor(getProject());
	}

	@Nullable
	@Override
	public RunProfileState getState(@NotNull Executor executor, @NotNull final ExecutionEnvironment env) throws ExecutionException
	{
		return new RunProfileState()
		{
			@Nullable
			@Override
			public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException
			{
				NUnitConfiguration runProfile = (NUnitConfiguration) env.getRunProfile();

				Module module = runProfile.getConfigurationModule().getModule();

				DotNetModuleExtension dotNetModuleExtension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
				NUnitModuleExtension nUnitModuleExtension = ModuleUtilCore.getExtension(module, NUnitModuleExtension.class);

				String currentLayerName = dotNetModuleExtension.getCurrentLayerName();
				MainConfigurationLayer currentLayer = (MainConfigurationLayer) dotNetModuleExtension.getCurrentLayer();

				val exeFile = DotNetMacros.extract(module, currentLayerName, currentLayer);

				GeneralCommandLine commandLine = new GeneralCommandLine();
				commandLine.setExePath(nUnitModuleExtension.getSdk().getHomePath() + "/bin/nunit-console.exe");
				commandLine.addParameter(exeFile);
				commandLine.addParameter("/nologo");
				commandLine.addParameter("/labels");

				TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(getProject());
				ConsoleView console = builder.getConsole();

				val rootTestProxy = new SMTestProxy.SMRootTestProxy();
				val testsOutputConsoleView = new NUnitTestsOutputConsoleView(env, console, rootTestProxy);

				OSProcessHandler osProcessHandler = new OSProcessHandler(commandLine);
				osProcessHandler.addProcessListener(new NUnitProcessAdapter(module, rootTestProxy, testsOutputConsoleView));

				console.attachToProcess(osProcessHandler);

				return new DefaultExecutionResult(testsOutputConsoleView, osProcessHandler);
			}
		};
	}
}
