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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import consulo.dotnet.psi.DotNetConstructorDeclaration;
import consulo.dotnet.psi.DotNetModifier;
import consulo.dotnet.psi.DotNetNamedElement;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.dotnet.psi.DotNetReferenceExpression;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.DotNetXXXAccessor;
import consulo.dotnet.resolve.DotNetPsiSearcher;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ArrayListSet;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XExpression;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.impl.ui.XDebuggerUIConstants;
import com.intellij.xdebugger.settings.XDebuggerSettingsManager;
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

	public DotNetStackFrame(DotNetDebugContext debuggerContext, DotNetStackFrameProxy frameProxy)
	{
		myDebuggerContext = debuggerContext;
		myFrameProxy = frameProxy;
	}

	@Nullable
	@Override
	public XSourcePosition getSourcePosition()
	{
		DotNetSourceLocation sourceLocation = myFrameProxy.getSourceLocation();
		if(sourceLocation == null)
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
						PsiElement executableElement = ApplicationManager.getApplication().runReadAction(new Computable<PsiElement>()
						{
							@Override
							public PsiElement compute()
							{
								return method.findExecutableElementFromDebugInfo(myDebuggerContext.getProject(), executableChildrenAtLineIndex);
							}
						});

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

			return ApplicationManager.getApplication().runReadAction(new Computable<XSourcePosition>()
			{
				@Override
				public XSourcePosition compute()
				{
					DotNetTypeDeclaration type = DotNetPsiSearcher.getInstance(myDebuggerContext.getProject()).findType(declarationType.getFullName(), myDebuggerContext.getResolveScope());
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
				}
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
		return new XDebuggerEvaluator()
		{
			@Override
			public boolean isCodeFragmentEvaluationSupported()
			{
				return false;
			}

			@Override
			public void evaluate(@NotNull XExpression expression, @NotNull XEvaluationCallback callback, @Nullable XSourcePosition expressionPosition)
			{
				DotNetDebuggerProvider provider = DotNetDebuggerProvider.getProvider(expression.getLanguage());
				if(provider != null)
				{
					if(provider.getEditorLanguage() == expression.getLanguage())
					{
						provider.evaluate(myFrameProxy, myDebuggerContext, expression.getExpression(), null, callback, expressionPosition);
					}
				}
			}

			@Override
			public void evaluate(@NotNull String expression, @NotNull XEvaluationCallback callback, @Nullable XSourcePosition expressionPosition)
			{
				if(expressionPosition == null)
				{
					return;
				}

				FileType fileType = expressionPosition.getFile().getFileType();
				if(fileType instanceof LanguageFileType)
				{
					DotNetDebuggerProvider provider = DotNetDebuggerProvider.getProvider(((LanguageFileType) fileType).getLanguage());
					if(provider != null)
					{
						provider.evaluate(myFrameProxy, myDebuggerContext, expression, null, callback, expressionPosition);
					}
				}
			}
		};
	}


	@Override
	public void customizePresentation(ColoredTextContainer component)
	{
		DotNetSourceLocation sourceLocation = myFrameProxy.getSourceLocation();
		if(sourceLocation == null)
		{
			component.setIcon(AllIcons.Debugger.Frame);
			component.append("<unknown>", SimpleTextAttributes.REGULAR_ATTRIBUTES);
			return;
		}

		DotNetMethodProxy method = sourceLocation.getMethod();

		DotNetTypeProxy declarationType = method.getDeclarationType();

		String name = method.getName();
		if(name.equals(XStubUtil.CONSTRUCTOR_NAME))
		{
			name = declarationType.getName() + "()";
		}
		else if(name.equals(XStubUtil.STATIC_CONSTRUCTOR_NAME))
		{
			name = declarationType.getName();
		}
		else
		{
			name = method.getName() + "()";
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
	public void computeChildren(@NotNull XCompositeNode node)
	{
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
			PsiElement psiElement = DotNetSourcePositionUtil.resolveTargetPsiElement(myDebuggerContext, myFrameProxy);
			if(psiElement != null)
			{
				final Set<DotNetReferenceExpression> referenceExpressions = new ArrayListSet<>();
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

					if(!referenceExpressions.isEmpty())
					{
						DotNetDebuggerProvider provider = DotNetDebuggerProvider.getProvider(psiElement.getLanguage());
						if(provider != null)
						{
							Consumer<XNamedValue> callback = xNamedValue -> childrenList.add(xNamedValue);

							for(DotNetReferenceExpression referenceExpression : referenceExpressions)
							{
								provider.evaluate(myFrameProxy, myDebuggerContext, referenceExpression, visitedVariables, callback);
							}
						}
					}
				}
			}
		}

		node.addChildren(childrenList, true);
	}
}
