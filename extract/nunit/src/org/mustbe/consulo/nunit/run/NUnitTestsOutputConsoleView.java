package org.mustbe.consulo.nunit.run;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.SMTestProxy;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.execution.testframework.ui.TestResultsPanel;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.ide.util.PropertiesComponent;
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
