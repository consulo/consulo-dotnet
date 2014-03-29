package org.mustbe.consulo.nunit.run;

import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.execution.configurations.ModuleRunProfile;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.SMTestProxy;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.execution.testframework.ui.TestResultsPanel;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.config.Storage;

/**
 * @author VISTALL
 * @since 28.03.14
 */
public class NUnitTestsOutputConsoleView extends BaseTestsOutputConsoleView
{
	private final NUnitTestResultsPanel myResultsPanel;

	public NUnitTestsOutputConsoleView(final ExecutionEnvironment executionEnvironment, ConsoleView console, SMTestProxy root)
	{
		super(new TestConsoleProperties(new Storage.PropertiesComponentStorage("NUnitSupport.", PropertiesComponent.getInstance()),
				executionEnvironment.getProject(), executionEnvironment.getExecutor())
		{
			@Override
			protected GlobalSearchScope initScope()
			{
				RunConfiguration configuration = getConfiguration();
				if(!(configuration instanceof ModuleRunProfile))
				{
					return GlobalSearchScope.allScope(getProject());
				}
				Module[] modules = ((ModuleRunProfile) configuration).getModules();
				if(modules.length == 0)
				{
					return GlobalSearchScope.allScope(getProject());
				}

				GlobalSearchScope scope = GlobalSearchScope.EMPTY_SCOPE;
				for(Module each : modules)
				{
					DotNetModuleExtension extension = ModuleUtilCore.getExtension(each, DotNetModuleExtension.class);
					if(extension != null)
					{
						scope = scope.uniteWith(extension.getScopeForResolving(true));
					}
					else
					{
						scope = scope.uniteWith(GlobalSearchScope.moduleRuntimeScope(each, true));
					}
				}
				return scope;
			}

			@Override
			public RunConfiguration getConfiguration()
			{
				return (RunConfiguration) executionEnvironment.getRunProfile();
			}
		}, root);
		myResultsPanel = new NUnitTestResultsPanel(console.getComponent(), getProperties(), executionEnvironment, root);
		initUI();
	}

	public NUnitTestResultsPanel getResultsPanel()
	{
		return myResultsPanel;
	}

	@Override
	protected TestResultsPanel createTestResultsPanel()
	{
		return myResultsPanel;
	}
}
