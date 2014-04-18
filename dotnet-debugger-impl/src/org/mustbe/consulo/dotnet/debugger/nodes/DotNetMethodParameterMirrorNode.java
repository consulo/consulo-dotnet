package org.mustbe.consulo.dotnet.debugger.nodes;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.frame.XNavigatable;
import mono.debugger.LocalVariableOrParameterMirror;
import mono.debugger.MethodMirror;
import mono.debugger.MethodParameterMirror;
import mono.debugger.StackFrameMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;
import mono.debugger.util.ImmutablePair;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetMethodParameterMirrorNode extends DotNetAbstractVariableMirrorNode
{
	private final MethodParameterMirror myParameter;
	private final StackFrameMirror myFrame;

	public DotNetMethodParameterMirrorNode(MethodParameterMirror parameter, StackFrameMirror frame, Project project)
	{
		super(parameter.name(), project, frame.thread());
		myParameter = parameter;
		myFrame = frame;
	}

	@Override
	public void computeSourcePosition(@NotNull XNavigatable navigatable)
	{
		MethodMirror method = myFrame.location().method();

		DotNetTypeDeclaration[] types = DotNetPsiFacade.getInstance(myProject).findTypes(method.declaringType().qualifiedName(),
				GlobalSearchScope.allScope(myProject), -1);

		if(types.length == 0)
		{
			return;
		}

		MethodParameterMirror[] parameterMirrors = method.parameters();
		for(DotNetTypeDeclaration type : types)
		{
			for(DotNetNamedElement dotNetNamedElement : type.getMembers())
			{
				if(!(dotNetNamedElement instanceof DotNetLikeMethodDeclaration))
				{
					continue;
				}

				if(!Comparing.equal(dotNetNamedElement.getName(), method.name()))
				{
					continue;
				}

				if(((DotNetLikeMethodDeclaration) dotNetNamedElement).getGenericParametersCount() != method.genericParameterCount())
				{
					continue;
				}

				DotNetParameter[] parameters = ((DotNetLikeMethodDeclaration) dotNetNamedElement).getParameters();

				if(parameters.length != parameterMirrors.length)
				{
					continue;
				}

				//TODO [VISTALL] better validation

				int i1 = ArrayUtil.indexOf(parameterMirrors, myParameter);

				DotNetParameter parameter = parameters[i1];
				PsiElement nameIdentifier = parameter.getNameIdentifier();
				if(nameIdentifier == null)
				{
					return;
				}
				navigatable.setSourcePosition(XDebuggerUtil.getInstance().createPositionByOffset(parameter.getContainingFile().getVirtualFile(),
						nameIdentifier.getTextOffset()));
				return;
			}
		}
	}

	@NotNull
	@Override
	public TypeMirror getTypeOfVariable()
	{
		return myParameter.type();
	}

	@NotNull
	@Override
	public Icon getIconForVariable()
	{
		return AllIcons.Nodes.Parameter;
	}

	@Nullable
	@Override
	public Value<?> getValueOfVariable()
	{
		return myFrame.localOrParameterValue(myParameter);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValueForVariable(@NotNull Value<?> value)
	{
		myFrame.setLocalOrParameterValues(new ImmutablePair<LocalVariableOrParameterMirror, Value<?>>(myParameter, value));
	}
}
