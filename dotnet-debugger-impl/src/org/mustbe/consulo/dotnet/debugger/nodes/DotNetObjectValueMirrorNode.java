package org.mustbe.consulo.dotnet.debugger.nodes;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import mono.debugger.FieldMirror;
import mono.debugger.ObjectValueMirror;
import mono.debugger.TypeMirror;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetObjectValueMirrorNode extends XNamedValue
{
	private final Project myProject;
	private final ObjectValueMirror myObjectValueMirror;

	public DotNetObjectValueMirrorNode(Project project, ObjectValueMirror objectValueMirror)
	{
		super("this");
		myProject = project;
		myObjectValueMirror = objectValueMirror;
	}

	@Override
	public void computeChildren(@NotNull XCompositeNode node)
	{
		XValueChildrenList childrenList = new XValueChildrenList();

		TypeMirror type = myObjectValueMirror.type();
		assert type != null;
		List<FieldMirror> fieldMirrors = type.fieldsDeep();
		for(FieldMirror fieldMirror : fieldMirrors)
		{
			childrenList.add(new DotNetFieldMirrorNode(fieldMirror, myProject, myObjectValueMirror));
		}
		node.addChildren(childrenList, true);
	}

	@Override
	public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place)
	{
		node.setPresentation(AllIcons.Debugger.Value, new XValuePresentation()
		{
			@Override
			public void renderValue(@NotNull XValueTextRenderer renderer)
			{
			}
		}, true);
	}
}
