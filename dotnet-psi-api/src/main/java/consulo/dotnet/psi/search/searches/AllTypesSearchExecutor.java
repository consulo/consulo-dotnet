package consulo.dotnet.psi.search.searches;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.application.util.query.QueryExecutor;
import consulo.dotnet.psi.DotNetTypeDeclaration;

/**
 * @author VISTALL
 * @since 03-Jul-22
 */
@ExtensionAPI(ComponentScope.APPLICATION)
public interface AllTypesSearchExecutor extends QueryExecutor<DotNetTypeDeclaration, AllTypesSearch.SearchParameters>
{
}
