package org.mustbe.consulo.dotnet.debugger.nodes;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.frame.XCompositeNode;
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
public class DotNetObjectValueMirrorNode extends AbstractTypedMirrorNode
{
	@NotNull
	private final Project myProject;
	@NotNull
	private final TypeMirror myTypeMirror;
	private final ObjectValueMirror myObjectValueMirror;

	public DotNetObjectValueMirrorNode(@NotNull Project project, @NotNull TypeMirror typeMirror, @Nullable ObjectValueMirror objectValueMirror)
	{
		super(objectValueMirror == null ? "static" : "this", project);
		myProject = project;
		myTypeMirror = typeMirror;
		myObjectValueMirror = objectValueMirror;
	}

	@Override
	public void computeChildren(@NotNull XCompositeNode node)
	{
		XValueChildrenList childrenList = new XValueChildrenList();

		List<FieldMirror> fieldMirrors = myTypeMirror.fieldsDeep();
		for(FieldMirror fieldMirror : fieldMirrors)
		{
			if(!fieldMirror.isStatic() && myObjectValueMirror == null)
			{
				continue;
			}
			childrenList.add(new DotNetFieldOrPropertyMirrorNode(fieldMirror, myProject, fieldMirror.isStatic() ? null : myObjectValueMirror));
		}
		node.addChildren(childrenList, true);
	}

	@Override
	public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place)
	{
		node.setPresentation(myObjectValueMirror == null ? AllIcons.Nodes.Static : AllIcons.Debugger.Value, new XValuePresentation()
		{
			@Nullable
			@Override
			public String getType()
			{
				TypeMirror typeOfVariable = getTypeOfVariable();
				return typeOfVariable.qualifiedName();
			}

			@Override
			public void renderValue(@NotNull XValueTextRenderer renderer)
			{
				if(myObjectValueMirror != null)
				{
					renderer.renderValue("ToString()");
				}
			}
		}, true);
	}

	@NotNull
	@Override
	public TypeMirror getTypeOfVariable()
	{
		return myObjectValueMirror == null ? myTypeMirror : myObjectValueMirror.type();
	}
}
