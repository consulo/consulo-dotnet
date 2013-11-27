package org.mustbe.consulo.dotnet.run;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.compiler.DotNetMacros;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
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
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
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
 * @since 26.11.13.
 */
public class DotNetConfiguration extends ModuleBasedConfiguration<RunConfigurationModule>
{
	public DotNetConfiguration(String name, RunConfigurationModule configurationModule, ConfigurationFactory factory)
	{
		super(name, configurationModule, factory);
	}

	public DotNetConfiguration(RunConfigurationModule configurationModule, ConfigurationFactory factory)
	{
		super(configurationModule, factory);
	}

	@Override
	public Collection<Module> getValidModules()
	{
		val list = new ArrayList<Module>();
		for(val module : ModuleManager.getInstance(getProject()).getModules())
		{
			if(ModuleUtilCore.getExtension(module, DotNetModuleExtension.class) != null)
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
		return new DotNetConfigurationEditor(getProject());
	}

	@Nullable
	@Override
	public RunProfileState getState(@NotNull Executor executor, @NotNull final ExecutionEnvironment executionEnvironment) throws ExecutionException
	{
		val module = getConfigurationModule().getModule();
		if(module == null)
		{
			throw new ExecutionException("Module is empty");
		}
		val exeFile = DotNetMacros.extract(module, false, false);
		if(!new File(exeFile).exists())
		{
			throw new ExecutionException(exeFile + " is not exists");
		}
		return new RunProfileState()
		{
			@Nullable
			@Override
			public ExecutionResult execute(Executor executor, @NotNull ProgramRunner programRunner) throws ExecutionException
			{
				val builder = TextConsoleBuilderFactory.getInstance().createBuilder(executionEnvironment.getProject());

				GeneralCommandLine commandLine = new GeneralCommandLine();
				commandLine.setExePath(exeFile);

				OSProcessHandler osProcessHandler = new OSProcessHandler(commandLine);

				ConsoleView console = builder.getConsole();
				console.attachToProcess(osProcessHandler);
				return new DefaultExecutionResult(console, osProcessHandler);
			}
		};
	}
}
