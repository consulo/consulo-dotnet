/**
 * @author VISTALL
 * @since 15-May-22
 */
open module consulo.dotnet.execution.api
{
	requires transitive consulo.execution.api;
	requires transitive consulo.execution.coverage.api;

	exports consulo.dotnet.run;
	exports consulo.dotnet.run.coverage;
	exports consulo.dotnet.execution.localize;
}