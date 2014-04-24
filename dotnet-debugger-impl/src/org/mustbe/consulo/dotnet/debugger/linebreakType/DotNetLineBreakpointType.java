package org.mustbe.consulo.dotnet.debugger.linebreakType;

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.compiler.DotNetMacros;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugThread;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerProvider;
import org.mustbe.consulo.dotnet.debugger.DotNetVirtualMachineUtil;
import org.mustbe.consulo.dotnet.module.MainConfigurationLayer;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiWhiteSpace;
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

		TypeMirror mirror = typeMirror == null ? findTypeMirror(virtualMachine, fileByUrl, (DotNetTypeDeclaration) parent) : typeMirror;

		if(mirror == null)
		{
			return null;
		}

		MethodMirror targetMirror = null;
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

			File outputFile = getOutputFile(parent);
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

	private static File getOutputFile(PsiElement element)
	{
		DotNetModuleExtension extension = ModuleUtilCore.getExtension(element, DotNetModuleExtension.class);
		if(extension == null)
		{
			return null;
		}
		String currentLayerName = extension.getCurrentLayerName();
		MainConfigurationLayer currentLayer = (MainConfigurationLayer) extension.getCurrentLayer();
		val exeFile = DotNetMacros.extract(extension.getModule(), currentLayerName, currentLayer);
		return new File(exeFile);
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
