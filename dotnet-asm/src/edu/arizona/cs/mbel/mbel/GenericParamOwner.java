package edu.arizona.cs.mbel.mbel;

import java.util.List;

import org.consulo.annotations.Immutable;
import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 23.11.13.
 */
public interface GenericParamOwner
{
	void addGenericParam(GenericParamDef genericParamDef);

	@Immutable
	@NotNull
	List<GenericParamDef> getGenericParams();
}
