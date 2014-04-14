package org.mustbe.consulo.dotnet.debugger.linebreakType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerProvider;
import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProviderBase;
import mono.debugger.Location;
import mono.debugger.LocationImpl;
import mono.debugger.MethodMirror;
import mono.debugger.TypeMirror;
import mono.debugger.VirtualMachine;
import mono.debugger.protocol.Method_GetDebugInfo;
import mono.debugger.request.EventRequest;
import mono.debugger.request.EventRequestManager;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetLineBreakpointType extends DotNetAbstractBreakpointType
{
	@NotNull
	public static DotNetLineBreakpointType getInstance()
	{
		return EXTENSION_POINT_NAME.findExtension(DotNetLineBreakpointType.class);
	}

	public DotNetLineBreakpointType()
	{
		super("dotnet-linebreapoint", ".NET Line Breakpoint", new XDebuggerEditorsProviderBase()
		{
			@Override
			protected PsiFile createExpressionCodeFragment(
					@NotNull Project project, @NotNull String s, @Nullable PsiElement element, boolean b)
			{
				return null;
			}

			@NotNull
			@Override
			public FileType getFileType()
			{
				return PlainTextFileType.INSTANCE;
			}
		});
	}

	@Override
	public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project)
	{
		PsiManager psiManager = PsiManager.getInstance(project);

		PsiFile psiFile = psiManager.findFile(file);
		if(psiFile == null)
		{
			return false;
		}
		for(DotNetDebuggerProvider dotNetDebuggerProvider : DotNetDebuggerProvider.EP_NAME.getExtensions())
		{
			if(dotNetDebuggerProvider.isSupported(psiFile))
			{
				return true;
			}
		}
		return false;
	}

	@Nullable
	@Override
	public EventRequest createEventRequest(@NotNull Project project, @NotNull VirtualMachine virtualMachine, @NotNull XLineBreakpoint breakpoint)
	{
		Location location = findLocation(project, virtualMachine, breakpoint);
		if(location == null)
		{
			return null;
		}
		EventRequestManager eventRequestManager = virtualMachine.eventRequestManager();
		return eventRequestManager.createBreakpointRequest(location);
	}

	public Location findLocation(final Project project, final VirtualMachine virtualMachine, final XLineBreakpoint<?> lineBreakpoint)
	{
		return ApplicationManager.getApplication().runReadAction(new Computable<Location>()
		{
			@Override
			public Location compute()
			{
				return findLocationImpl(project, virtualMachine, lineBreakpoint);
			}
		});
	}

	public Location findLocationImpl(Project project, VirtualMachine virtualMachine, XLineBreakpoint<?> lineBreakpoint)
	{
		VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(lineBreakpoint.getFileUrl());
		if(fileByUrl == null)
		{
			return null;
		}

		PsiElement psiElement = findPsiElement(project, fileByUrl, lineBreakpoint.getLine());
		if(psiElement == null)
		{
			return null;
		}
		DotNetMethodDeclaration methodDeclaration = PsiTreeUtil.getParentOfType(psiElement, DotNetMethodDeclaration.class, false);
		if(methodDeclaration == null)
		{
			return null;
		}
		PsiElement codeBlock = methodDeclaration.getCodeBlock();
		if(codeBlock == null)
		{
			return null;
		}
		if(!PsiTreeUtil.isAncestor(codeBlock, psiElement, false))
		{
			return null;
		}
		PsiElement parent = methodDeclaration.getParent();
		if(!(parent instanceof DotNetTypeDeclaration))
		{
			return null;
		}
		TypeMirror[] types = virtualMachine.findTypes(((DotNetTypeDeclaration) parent).getPresentableQName(), false);
		if(types.length == 0)
		{
			return null;
		}

		MethodMirror targetMirror = null;
		TypeMirror mirror = types[0];
		for(MethodMirror methodMirror : mirror.methods())
		{
			if(isValidMethodMirror(methodDeclaration, methodMirror))
			{
				targetMirror = methodMirror;
				break;
			}
		}

		if(targetMirror == null)
		{
			return null;
		}

		int index = -1;
		for(Method_GetDebugInfo.Entry entry : targetMirror.debugInfo())
		{
			if(entry.line == (lineBreakpoint.getLine() + 1))
			{
				index = entry.offset;
				break;
			}
		}
		if(index == -1)
		{
			return null;
		}
		return new LocationImpl(virtualMachine, targetMirror, index);
	}

	private boolean isValidMethodMirror(DotNetMethodDeclaration methodDeclaration, MethodMirror methodMirror)
	{
		if(methodMirror.genericParameterCount() != methodDeclaration.getGenericParametersCount())
		{
			return false;
		}
		DotNetParameter[] parameters = methodDeclaration.getParameters();
		if(parameters.length != methodMirror.parameters().length)
		{
			return false;
		}
		//TODO [VISTALL]
		return Comparing.equal(methodDeclaration.getName(), methodMirror.name());
	}

	@Nullable
	protected PsiElement findPsiElement(@NotNull final Project project, @NotNull final VirtualFile file, final int line)
	{

		final Document doc = FileDocumentManager.getInstance().getDocument(file);
		final PsiFile psi = doc == null ? null : PsiDocumentManager.getInstance(project).getPsiFile(doc);
		if(psi == null)
		{
			return null;
		}

		int offset = doc.getLineStartOffset(line);
		int endOffset = doc.getLineEndOffset(line);
		for(int i = offset + 1; i < endOffset; i++)
		{
			PsiElement el = psi.findElementAt(i);
			if(el != null && !(el instanceof PsiWhiteSpace))
			{
				return el;
			}
		}

		return null;
	}
}
