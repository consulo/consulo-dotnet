/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consulo.dotnet.debugger;

import gnu.trove.THashSet;

import java.io.File;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.containers.ArrayListSet;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.impl.ui.XDebuggerUIConstants;
import com.intellij.xdebugger.settings.XDebuggerSettingsManager;
import consulo.annotations.RequiredDispatchThread;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.debugger.breakpoint.properties.DotNetLineBreakpointProperties;
import consulo.dotnet.debugger.nodes.DotNetDebuggerCompilerGenerateUtil;
import consulo.dotnet.debugger.nodes.DotNetSourcePositionUtil;
import consulo.dotnet.debugger.nodes.objectReview.DefaultStackFrameComputer;
import consulo.dotnet.debugger.nodes.objectReview.StackFrameComputer;
import consulo.dotnet.debugger.nodes.objectReview.YieldOrAsyncStackFrameComputer;
import consulo.dotnet.debugger.proxy.DotNetAbsentInformationException;
import consulo.dotnet.debugger.proxy.DotNetInvalidObjectException;
import consulo.dotnet.debugger.proxy.DotNetInvalidStackFrameException;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetSourceLocation;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.psi.DotNetAccessorOwner;
import consulo.dotnet.psi.DotNetConstructorDeclaration;
import consulo.dotnet.psi.DotNetModifier;
import consulo.dotnet.psi.DotNetNamedElement;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.dotnet.psi.DotNetReferenceExpression;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.DotNetXXXAccessor;
import consulo.dotnet.resolve.DotNetPsiSearcher;
import consulo.internal.dotnet.msil.decompiler.textBuilder.util.XStubUtil;
import consulo.internal.dotnet.msil.decompiler.util.MsilHelper;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetStackFrame extends XStackFrame
{
	private static final StackFrameComputer[] ourStackFrameComputers = new StackFrameComputer[]{
			new YieldOrAsyncStackFrameComputer(),
			new DefaultStackFrameComputer()
	};

	private final DotNetDebugContext myDebuggerContext;
	private final DotNetStackFrameProxy myFrameProxy;
	private final XSourcePosition myXSourcePosition;

	private final DotNetSourceLocation mySourceLocation;

	public DotNetStackFrame(DotNetDebugContext debuggerContext, DotNetStackFrameProxy frameProxy)
	{
		myDebuggerContext = debuggerContext;
		myFrameProxy = frameProxy;

		DotNetSourceLocation location = myFrameProxy.getSourceLocation();
		mySourceLocation = location == null ? null : location.lightCopy();
		myXSourcePosition = calcSourcePosition();
	}

	@Nullable
	@Override
	@RequiredDispatchThread
	public XSourcePosition getSourcePosition()
	{
		return myXSourcePosition;
	}

	@Nullable
	private XSourcePosition calcSourcePosition()
	{
		DotNetVirtualMachineUtil.checkCallForUIThread();

		DotNetSourceLocation sourceLocation = mySourceLocation;
		if(mySourceLocation == null)
		{
			return null;
		}
		VirtualFile fileByPath = sourceLocation.getFilePath() == null ? null : LocalFileSystem.getInstance().findFileByPath(sourceLocation.getFilePath());
		final DotNetMethodProxy method = sourceLocation.getMethod();
		if(fileByPath != null)
		{
			XBreakpoint<?> breakpoint = myDebuggerContext.getBreakpoint();
			XSourcePosition originalPosition = XDebuggerUtil.getInstance().createPosition(fileByPath, sourceLocation.getLineZeroBased());
			assert originalPosition != null;
			if(breakpoint instanceof XLineBreakpoint)
			{
				DotNetLineBreakpointProperties properties = (DotNetLineBreakpointProperties) breakpoint.getProperties();
				final Integer executableChildrenAtLineIndex = properties.getExecutableChildrenAtLineIndex();
				if(executableChildrenAtLineIndex != null && executableChildrenAtLineIndex > -1)
				{
					if(DotNetDebuggerCompilerGenerateUtil.extractLambdaInfo(method) != null)
					{
						PsiElement executableElement = ReadAction.compute(() -> method.findExecutableElementFromDebugInfo(myDebuggerContext.getProject(), executableChildrenAtLineIndex));

						if(executableElement != null)
						{
							return new DotNetSourcePositionByExecutableElement(originalPosition, executableElement);
						}
					}
				}
			}

			return originalPosition;
		}
		else
		{
			final DotNetTypeProxy declarationType = method.getDeclarationType();

			return ReadAction.compute(() ->
			{
				DotNetTypeDeclaration type = DotNetPsiSearcher.getInstance(myDebuggerContext.getProject()).findType(DotNetDebuggerUtil.getVmQName(declarationType), myDebuggerContext.getResolveScope
						());
				if(type == null)
				{
					return null;
				}

				PsiElement target = type;
				for(DotNetNamedElement element : type.getMembers())
				{
					if(Comparing.equal(getVmName(element), method.getName()))
					{
						target = element;
						break;
					}

					if(element instanceof DotNetAccessorOwner)
					{
						for(DotNetXXXAccessor accessor : ((DotNetAccessorOwner) element).getAccessors())
						{
							if(Comparing.equal(getVmName(accessor), method.getName()))
							{
								target = element;
								break;
							}
						}
					}
				}
				return XDebuggerUtil.getInstance().createPositionByOffset(target.getContainingFile().getVirtualFile(), target.getTextOffset());
			});
		}
	}

	@RequiredReadAction
	private static String getVmName(DotNetNamedElement element)
	{
		if(element instanceof DotNetConstructorDeclaration)
		{
			if(((DotNetConstructorDeclaration) element).hasModifier(DotNetModifier.STATIC))
			{
				return MsilHelper.STATIC_CONSTRUCTOR_NAME;
			}

			if(((DotNetConstructorDeclaration) element).isDeConstructor())
			{
				return "Finalize";
			}
			return MsilHelper.CONSTRUCTOR_NAME;
		}
		if(element instanceof DotNetXXXAccessor)
		{
			PsiElement parent = element.getParent();
			if(parent instanceof DotNetNamedElement)
			{
				DotNetXXXAccessor.Kind accessorKind = ((DotNetXXXAccessor) element).getAccessorKind();
				if(accessorKind != null)
				{
					return accessorKind.name().toUpperCase(Locale.US) + "_" + ((DotNetNamedElement) parent).getName();
				}
			}
		}
		return element.getName();
	}

	@Nullable
	@Override
	public Object getEqualityObject()
	{
		return myFrameProxy.getEqualityObject();
	}

	@Nullable
	@Override
	public XDebuggerEvaluator getEvaluator()
	{
		return new DotNetDebuggerEvaluator(myFrameProxy, myDebuggerContext);
	}

	@Override
	public void customizePresentation(ColoredTextContainer component)
	{
		DotNetSourceLocation sourceLocation = mySourceLocation;
		if(sourceLocation == null)
		{
			component.setIcon(AllIcons.Debugger.Frame);
			component.append("<unknown>", SimpleTextAttributes.REGULAR_ATTRIBUTES);
			return;
		}

		DotNetMethodProxy method = sourceLocation.getMethod();

		DotNetTypeProxy declarationType = method.getDeclarationType();

		String name = method.getName();
		switch(name)
		{
			case XStubUtil.CONSTRUCTOR_NAME:
				name = declarationType.getName() + "()";
				break;
			case XStubUtil.STATIC_CONSTRUCTOR_NAME:
				name = declarationType.getName();
				break;
			default:
				name = method.getName() + "()";
				break;
		}

		Couple<String> lambdaInfo = DotNetDebuggerCompilerGenerateUtil.extractLambdaInfo(method);
		if(lambdaInfo != null)
		{
			name = "lambda$" + lambdaInfo.getSecond();
		}

		component.setIcon(AllIcons.Debugger.Frame);
		component.append(name, SimpleTextAttributes.REGULAR_ATTRIBUTES);

		StringBuilder builder = new StringBuilder();
		String fileName = sourceLocation.getFilePath();
		if(fileName != null)
		{
			builder.append(":");
			builder.append(new File(fileName).getName());
		}

		builder.append(":");
		builder.append(sourceLocation.getLineOneBased());
		builder.append(":");
		builder.append(sourceLocation.getColumn());
		builder.append(", ");
		if(lambdaInfo == null)
		{
			builder.append(DotNetVirtualMachineUtil.formatNameWithGeneric(declarationType));
		}
		else
		{
			builder.append(lambdaInfo.getFirst()).append("(...)");
		}

		component.append(builder.toString(), SimpleTextAttributes.GRAY_ATTRIBUTES);
	}

	@Override
	@RequiredReadAction
	public void computeChildren(@Nonnull XCompositeNode node)
	{
		myDebuggerContext.invoke(() -> computeChildrenImpl(node));
	}

	private void computeChildrenImpl(@Nonnull XCompositeNode node)
	{
		DotNetVirtualMachineUtil.checkCallForUIThread();

		final XValueChildrenList childrenList = new XValueChildrenList();
		final Set<Object> visitedVariables = new THashSet<>();
		try
		{
			final DotNetValueProxy value = myFrameProxy.getThisObject();

			for(StackFrameComputer objectReviewer : ourStackFrameComputers)
			{
				if(objectReviewer.computeStackFrame(myDebuggerContext, value, myFrameProxy, visitedVariables, childrenList))
				{
					break;
				}
			}
		}
		catch(DotNetAbsentInformationException e)
		{
			node.setMessage("Stack frame info is not available", XDebuggerUIConstants.INFORMATION_MESSAGE_ICON, XDebuggerUIConstants.VALUE_NAME_ATTRIBUTES, null);
			return;
		}
		catch(DotNetInvalidObjectException e)
		{
			// this object is not available
		}
		catch(DotNetInvalidStackFrameException e)
		{
			node.setErrorMessage("Stack frame info is not valid");
			return;
		}

		if(XDebuggerSettingsManager.getInstance().getDataViewSettings().isAutoExpressions())
		{
			final Ref<DotNetDebuggerProvider> providerRef = Ref.create();
			final Set<DotNetReferenceExpression> referenceExpressions = new ArrayListSet<>();

			ApplicationManager.getApplication().runReadAction(() ->
			{
				PsiElement psiElement = DotNetSourcePositionUtil.resolveTargetPsiElement(myDebuggerContext, myFrameProxy);
				if(psiElement != null)
				{
					DotNetDebuggerProvider provider = DotNetDebuggerProvider.getProvider(psiElement.getLanguage());
					if(provider == null)
					{
						return;
					}
					providerRef.set(provider);

					DotNetQualifiedElement parentQualifiedElement = PsiTreeUtil.getParentOfType(psiElement, DotNetQualifiedElement.class);
					if(parentQualifiedElement != null)
					{
						parentQualifiedElement.accept(new PsiRecursiveElementVisitor()
						{
							@Override
							public void visitElement(PsiElement element)
							{
								super.visitElement(element);
								if(element instanceof DotNetReferenceExpression)
								{
									PsiElement parent = element.getParent();
									if(parent instanceof DotNetReferenceExpression && ((DotNetReferenceExpression) parent).getQualifier() == element)
									{
										return;
									}

									referenceExpressions.add((DotNetReferenceExpression) element);
								}
							}
						});
					}
				}
			});

			if(!referenceExpressions.isEmpty())
			{
				DotNetDebuggerProvider provider = providerRef.get();
				assert provider != null;
				for(DotNetReferenceExpression referenceExpression : referenceExpressions)
				{
					try
					{
						provider.evaluate(myFrameProxy, myDebuggerContext, referenceExpression, visitedVariables, childrenList::add);
					}
					catch(IllegalArgumentException ignored)
					{
					}
				}
			}
		}

		node.addChildren(childrenList, true);
	}
}
