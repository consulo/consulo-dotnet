package org.mustbe.consulo.nunit.run;

import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.Filter;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.TestFrameworkRunningModel;
import com.intellij.execution.testframework.TestTreeView;
import com.intellij.execution.testframework.sm.SMRunnerUtil;
import com.intellij.execution.testframework.sm.runner.SMTRunnerTreeBuilder;
import com.intellij.execution.testframework.sm.runner.SMTRunnerTreeStructure;
import com.intellij.execution.testframework.sm.runner.SMTestProxy;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerTestTreeView;
import com.intellij.execution.testframework.ui.AbstractTestTreeBuilder;
import com.intellij.execution.testframework.ui.TestResultsPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;

/**
 * @author VISTALL
 * @since 28.03.14
 */
public class NUnitTestResultsPanel extends TestResultsPanel implements TestFrameworkRunningModel
{
	private final SMTestProxy myRoot;
	private TestTreeView myTestTreeView;
	private SMTRunnerTreeBuilder myTreeBuilder;
	private Project myProject;

	public NUnitTestResultsPanel(
			@NotNull JComponent console, TestConsoleProperties properties, ExecutionEnvironment environment, SMTestProxy root)
	{
		super(console, AnAction.EMPTY_ARRAY, properties, environment, "NUnit.Test", 0.2f);
		myRoot = root;
		myProject = environment.getProject();
	}

	@Override
	protected JComponent createStatisticsPanel()
	{
		return null;
	}

	@Override
	protected JComponent createTestTreeView()
	{
		myTestTreeView = new SMTRunnerTestTreeView();
		myTestTreeView.setLargeModel(true);
		myTestTreeView.attachToModel(this);

		SMTRunnerTreeStructure structure = new SMTRunnerTreeStructure(myProject, myRoot);
		myTreeBuilder = new SMTRunnerTreeBuilder(myTestTreeView, structure);
		Disposer.register(this, myTreeBuilder);
		return myTestTreeView;
	}

	@Override
	public TestConsoleProperties getProperties()
	{
		return myProperties;
	}

	@Override
	public void setFilter(Filter filter)
	{

	}

	@Override
	public boolean isRunning()
	{
		return getRoot().isInProgress();
	}

	@Override
	public TestTreeView getTreeView()
	{
		return myTestTreeView;
	}

	@Override
	public AbstractTestTreeBuilder getTreeBuilder()
	{
		return myTreeBuilder;
	}

	@Override
	public boolean hasTestSuites()
	{
		return getRoot().getChildren().size() > 0;
	}

	@Override
	public AbstractTestProxy getRoot()
	{
		return myRoot;
	}

	@Override
	public void selectAndNotify(AbstractTestProxy abstractTestProxy)
	{
		selectWithoutNotify(abstractTestProxy);
	}

	private void selectWithoutNotify(final AbstractTestProxy testProxy)
	{
		if(testProxy == null)
		{
			return;
		}

		SMRunnerUtil.runInEventDispatchThread(new Runnable()
		{
			@Override
			public void run()
			{
				if(myTreeBuilder.isDisposed())
				{
					return;
				}
				myTreeBuilder.select(testProxy, null);
			}
		}, ModalityState.NON_MODAL);
	}
}
