package org.mustbe.consulo.dotnet.psi;

import org.consulo.lombok.annotations.ArrayFactoryFields;
import com.intellij.psi.PsiNameIdentifierOwner;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
@ArrayFactoryFields
public interface DotNetGenericParameter extends DotNetElement, DotNetModifierListOwner, PsiNameIdentifierOwner
{
}
