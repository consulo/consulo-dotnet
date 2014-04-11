package org.mustbe.consulo.dotnet.debugger;

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
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XNavigatable;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import mono.debugger.MethodMirror;
import mono.debugger.MethodParameterMirror;
import mono.debugger.PrimitiveValueMirror;
import mono.debugger.StackFrameMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetMethodParameterMirrorNode extends XNamedValue
{
	private final Value myValue;
	private final MethodParameterMirror myParameter;
	private final StackFrameMirror myFrame;
	private final Project myProject;

	public DotNetMethodParameterMirrorNode(MethodParameterMirror parameter, StackFrameMirror frame, Project project)
	{
		super(parameter.name());
		myParameter = parameter;
		myFrame = frame;
		myProject = project;
		myValue = frame.parameterValue(parameter);
	}

	@Override
	public boolean canNavigateToTypeSource()
	{
		return true;
	}

	@Override
	public void computeTypeSourcePosition(@NotNull XNavigatable navigatable)
	{
		DotNetTypeDeclaration type = DotNetPsiFacade.getInstance(myProject).findType(myParameter.type().qualifiedName(),
				GlobalSearchScope.allScope(myProject), -1);

		if(type == null)
		{
			return;
		}
		PsiElement nameIdentifier = type.getNameIdentifier();
		if(nameIdentifier == null)
		{
			return;
		}
		navigatable.setSourcePosition(XDebuggerUtil.getInstance().createPositionByOffset(type.getContainingFile().getVirtualFile(),
				nameIdentifier.getTextOffset()));
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

	@Override
	public void computePresentation(@NotNull XValueNode xValueNode, @NotNull XValuePlace xValuePlace)
	{
		xValueNode.setPresentation(AllIcons.Nodes.Parameter, new XValuePresentation()
		{
			@Nullable
			@Override
			public String getType()
			{
				return myParameter.type().qualifiedName();
			}

			@Override
			public void renderValue(@NotNull XValueTextRenderer xValueTextRenderer)
			{
				if(myValue != null)
				{
					if(myValue instanceof PrimitiveValueMirror)
					{
						xValueTextRenderer.renderNumericValue(String.valueOf(((PrimitiveValueMirror) myValue).value()));
					}
				}
				else
				{
					xValueTextRenderer.renderComment("error");
				}
			}
		}, true);
	}
}
