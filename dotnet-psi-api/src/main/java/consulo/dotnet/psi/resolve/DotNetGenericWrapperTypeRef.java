package consulo.dotnet.psi.resolve;


/**
 * @author VISTALL
 * @since 25.10.14
 */
public interface DotNetGenericWrapperTypeRef extends DotNetTypeRef, DotNetTypeRefWithInnerTypeRef
{
	DotNetTypeRef[] getArgumentTypeRefs();
}
