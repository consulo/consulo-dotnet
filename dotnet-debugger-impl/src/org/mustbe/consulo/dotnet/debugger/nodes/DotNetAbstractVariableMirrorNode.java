package org.mustbe.consulo.dotnet.debugger.nodes;

import java.util.List;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XNavigatable;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import mono.debugger.FieldMirror;
import mono.debugger.NumberValueMirror;
import mono.debugger.ObjectValueMirror;
import mono.debugger.StringValueMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;
import mono.debugger.ValueVisitor;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public abstract class DotNetAbstractVariableMirrorNode extends XNamedValue
{
	@NotNull
	protected final Project myProject;

	public DotNetAbstractVariableMirrorNode(@NotNull String name, @NotNull Project project)
	{
		super(name);
		myProject = project;
	}

	@NotNull
	public abstract TypeMirror getTypeOfVariable();

	@NotNull
	public abstract Icon getIconForVariable();

	@Nullable
	public abstract Value<?> getValueOfVariable();

	@Override
	public boolean canNavigateToTypeSource()
	{
		return true;
	}

	@Override
	public void computeChildren(@NotNull XCompositeNode node)
	{
		final Value<?> valueOfVariable = getValueOfVariable();
		if(!(valueOfVariable instanceof ObjectValueMirror))
		{
			return;
		}

		TypeMirror type = valueOfVariable.type();

		assert type != null;

		XValueChildrenList childrenList = new XValueChildrenList();

		childrenList.add(new DotNetObjectValueMirrorNode(myProject, type, null));

		List<FieldMirror> fieldMirrors = type.fieldsDeep();
		for(FieldMirror fieldMirror : fieldMirrors)
		{
			if(fieldMirror.isStatic())
			{
				continue;
			}
			childrenList.add(new DotNetFieldOrPropertyMirrorNode(fieldMirror, myProject, (ObjectValueMirror) valueOfVariable));
		}
		node.addChildren(childrenList, true);
	}

	@Override
	public void computeTypeSourcePosition(@NotNull XNavigatable navigatable)
	{
		DotNetTypeDeclaration type = DotNetPsiFacade.getInstance(myProject).findType(getTypeOfVariable().qualifiedName(),
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
	public void computePresentation(@NotNull XValueNode xValueNode, @NotNull XValuePlace xValuePlace)
	{
		final Value<?> valueOfVariable = getValueOfVariable();
		xValueNode.setPresentation(getIconForVariable(), new XValuePresentation()
		{
			@Nullable
			@Override
			public String getType()
			{
				return getTypeOfVariable().qualifiedName();
			}

			@Override
			public void renderValue(@NotNull final XValueTextRenderer xValueTextRenderer)
			{
				if(valueOfVariable == null)
				{
					xValueTextRenderer.renderKeywordValue("null?");
				}
				else
				{
					valueOfVariable.accept(new ValueVisitor.Adapter()
					{
						@Override
						public void visitStringValue(@NotNull StringValueMirror value, @NotNull String mainValue)
						{
							xValueTextRenderer.renderStringValue(mainValue);
						}

						@Override
						public void visitNumberValue(@NotNull NumberValueMirror value, @NotNull Number mainValue)
						{
							xValueTextRenderer.renderNumericValue(String.valueOf(mainValue));
						}
					});
				}
			}
		}, valueOfVariable instanceof ObjectValueMirror);
	}
}
