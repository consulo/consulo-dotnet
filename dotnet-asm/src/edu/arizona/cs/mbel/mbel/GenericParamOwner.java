package edu.arizona.cs.mbel.mbel;

import java.util.List;

/**
 * @author VISTALL
 * @since 23.11.13.
 */
public interface GenericParamOwner
{
	void addGenericParam(GenericParamDef genericParamDef);

	List<GenericParamDef> getGenericParams();
}
