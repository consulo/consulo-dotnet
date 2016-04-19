package consulo.dotnet.mono.debugger;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclarationUtil;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import com.intellij.openapi.project.Project;
import consulo.dotnet.debugger.DotNetDebugContext;
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
		return DotNetPsiSearcher.getInstance(project).findTypes(getVmQName(typeMirror), debugContext.getResolveScope());
	}

	@NotNull
	public static String getVmQName(@NotNull TypeMirror typeMirror)
	{
		String fullName = typeMirror.fullName();

		// System.List`1[String]
		int i = fullName.indexOf('[');
		if(i != -1)
		{
			fullName = fullName.substring(0, i);
		}

		// change + to / separator
		fullName = fullName.replace('+', DotNetTypeDeclarationUtil.NESTED_SEPARATOR_IN_NAME);
		return fullName;
	}
}
