package consulo.dotnet.mono.debugger;

import org.jetbrains.annotations.NotNull;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.resolve.DotNetPsiSearcher;
import com.intellij.openapi.project.Project;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetDebuggerUtil;
import mono.debugger.TypeMirror;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MonoDebugUtil
{
	@NotNull
	@RequiredReadAction
	public static DotNetTypeDeclaration[] findTypesByQualifiedName(@NotNull TypeMirror typeMirror, @NotNull DotNetDebugContext debugContext)
	{
		Project project = debugContext.getProject();
		return DotNetPsiSearcher.getInstance(project).findTypes(DotNetDebuggerUtil.getVmQName(typeMirror.fullName()), debugContext.getResolveScope());
	}
}
