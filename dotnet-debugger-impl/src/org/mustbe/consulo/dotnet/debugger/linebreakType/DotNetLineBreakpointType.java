package org.mustbe.consulo.dotnet.debugger.linebreakType;

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugThread;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerProvider;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerUtil;
import org.mustbe.consulo.dotnet.debugger.DotNetVirtualMachineUtil;
import org.mustbe.consulo.dotnet.psi.DotNetCodeBlockOwner;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import lombok.val;
import mono.debugger.AssemblyMirror;
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
		super("dotnet-linebreapoint", ".NET Line Breakpoint", null);
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

	@Override
	public boolean createRequest(
			@NotNull Project project, @NotNull VirtualMachine virtualMachine, @NotNull XLineBreakpoint breakpoint, @Nullable TypeMirror typeMirror)
	{
		XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();

		EventRequest eventRequest = createEventRequest(project, virtualMachine, breakpoint, typeMirror);
		if(eventRequest == null)
		{
			breakpointManager.updateBreakpointPresentation(breakpoint, AllIcons.Debugger.Db_invalid_breakpoint, null);
			return false;
		}
		breakpointManager.updateBreakpointPresentation(breakpoint, AllIcons.Debugger.Db_verified_breakpoint, null);
		eventRequest.enable();
		breakpoint.putUserData(DotNetDebugThread.EVENT_REQUEST, eventRequest);
		return true;
	}

	@Nullable
	public EventRequest createEventRequest(
			@NotNull Project project, @NotNull VirtualMachine virtualMachine, @NotNull XLineBreakpoint breakpoint, TypeMirror typeMirror)
	{
		Location location = findLocationImpl(project, virtualMachine, breakpoint, typeMirror);
		if(location == null)
		{
			return null;
		}
		EventRequestManager eventRequestManager = virtualMachine.eventRequestManager();
		return eventRequestManager.createBreakpointRequest(location);
	}

	public Location findLocationImpl(
			Project project, VirtualMachine virtualMachine, XLineBreakpoint<?> lineBreakpoint, @Nullable TypeMirror typeMirror)
	{
		VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(lineBreakpoint.getFileUrl());
		if(fileByUrl == null)
		{
			return null;
		}

		PsiElement psiElement = DotNetDebuggerUtil.findPsiElement(project, fileByUrl, lineBreakpoint.getLine());
		if(psiElement == null)
		{
			return null;
		}
		DotNetCodeBlockOwner codeBlockOwner = PsiTreeUtil.getParentOfType(psiElement, DotNetCodeBlockOwner.class, false);
		if(codeBlockOwner == null)
		{
			return null;
		}
		PsiElement codeBlock = codeBlockOwner.getCodeBlock();
		if(codeBlock == null)
		{
			return null;
		}
		if(!PsiTreeUtil.isAncestor(codeBlock, psiElement, false))
		{
			return null;
		}
		DotNetTypeDeclaration typeDeclaration = PsiTreeUtil.getParentOfType(codeBlockOwner, DotNetTypeDeclaration.class);
		if(typeDeclaration == null)
		{
			return null;
		}

		TypeMirror mirror = typeMirror == null ? findTypeMirror(virtualMachine, fileByUrl, typeDeclaration) : typeMirror;

		if(mirror == null)
		{
			return null;
		}

		MethodMirror targetMirror = null;
		int index = -1;
		highLoop:for(MethodMirror methodMirror : mirror.methods())
		{
			for(Method_GetDebugInfo.Entry entry : methodMirror.debugInfo())
			{
				if(entry.line == (lineBreakpoint.getLine() + 1))
				{
					targetMirror = methodMirror;
					index = entry.offset;
					break highLoop;
				}
			}
		}

		if(targetMirror == null)
		{
			return null;
		}

		return new LocationImpl(virtualMachine, targetMirror, index);
	}

	private TypeMirror findTypeMirror(VirtualMachine virtualMachine, VirtualFile virtualFile, DotNetTypeDeclaration parent)
	{
		virtualMachine.eventRequestManager().createAppDomainCreate().enable();
		val vmQualifiedName = DotNetVirtualMachineUtil.toVMQualifiedName(parent);
		if(virtualMachine.isAtLeastVersion(2, 9))
		{
			TypeMirror[] typesByQualifiedName = virtualMachine.findTypesByQualifiedName(vmQualifiedName, false);
			return typesByQualifiedName.length == 0 ? null : typesByQualifiedName[0];
		}
		else if(virtualMachine.isAtLeastVersion(2, 7))
		{
			TypeMirror[] typesBySourcePath = virtualMachine.findTypesBySourcePath(virtualFile.getPath(), SystemInfo.isFileSystemCaseSensitive);
			return ContainerUtil.find(typesBySourcePath, new Condition<TypeMirror>()
			{
				@Override
				public boolean value(TypeMirror typeMirror)
				{
					return Comparing.equal(typeMirror.originalQualifiedName(), vmQualifiedName);
				}
			});
		}
		else
		{
			AssemblyMirror[] assemblies = virtualMachine.rootAppDomain().assemblies();

			File outputFile = DotNetDebuggerUtil.getOutputFile(parent);
			if(outputFile == null)
			{
				return null;
			}

			for(AssemblyMirror assembly : assemblies)
			{
				File file = new File(assembly.location());
				if(FileUtil.filesEqual(outputFile, file))
				{
					return assembly.findTypeByQualifiedName(vmQualifiedName, false);
				}
			}
			return null;
		}
	}
}
