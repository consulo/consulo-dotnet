package org.mustbe.consulo.dotnet.debugger.nodes;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetTypes;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import mono.debugger.FieldOrPropertyMirror;
import mono.debugger.InvokeFlags;
import mono.debugger.MethodMirror;
import mono.debugger.ObjectValueMirror;
import mono.debugger.StringValueMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetObjectValueMirrorNode extends AbstractTypedMirrorNode
{
	@NotNull
	private final Project myProject;
	@NotNull
	private final ThreadMirror myThreadMirror;
	@NotNull
	private final TypeMirror myTypeMirror;
	private final ObjectValueMirror myObjectValueMirror;

	private String myToStringValue;

	public DotNetObjectValueMirrorNode(
			@NotNull Project project,
			@NotNull ThreadMirror threadMirror,
			@NotNull TypeMirror typeMirror,
			@Nullable ObjectValueMirror objectValueMirror)
	{
		super(objectValueMirror == null ? "static" : "this", project);
		myProject = project;
		myThreadMirror = threadMirror;
		myTypeMirror = typeMirror;
		myObjectValueMirror = objectValueMirror;
		TypeMirror type = objectValueMirror == null ? null : objectValueMirror.type();
		if(type == null)
		{
			return;
		}
		MethodMirror toString = type.findMethodByName("ToString", true);
		if(toString == null)
		{
			return;
		}

		String qTypeOfValue = toString.declaringType().qualifiedName();
		if(Comparing.equal(qTypeOfValue, DotNetTypes.System_Object))
		{
			myToStringValue = "{" + qTypeOfValue + "@" + myObjectValueMirror.address() + "}";
		}
		else
		{
			Value<?> invoke = toString.invoke(threadMirror, InvokeFlags.DISABLE_BREAKPOINTS, objectValueMirror);
			if(invoke instanceof StringValueMirror)
			{
				myToStringValue = ((StringValueMirror) invoke).value();
			}
		}
	}

	@Override
	public void computeChildren(@NotNull XCompositeNode node)
	{
		XValueChildrenList childrenList = new XValueChildrenList();

		List<FieldOrPropertyMirror> fieldMirrors = myTypeMirror.fieldAndProperties(true);
		for(FieldOrPropertyMirror fieldMirror : fieldMirrors)
		{
			if(!fieldMirror.isStatic() && myObjectValueMirror == null)
			{
				continue;
			}
			childrenList.add(new DotNetFieldOrPropertyMirrorNode(fieldMirror, myProject, myThreadMirror,
					fieldMirror.isStatic() ? null : myObjectValueMirror));
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
					renderer.renderValue(myToStringValue);
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
