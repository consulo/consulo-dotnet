package edu.arizona.cs.mbel.signature;

/**
 * @author VISTALL
 * @since 09.12.13.
 */
public interface GenericParamAttributes
{
	int VarianceMask = 0x0003;
	int NonVariant = 0x0000;
	int Covariant = 0x0001;
	int Contravariant = 0x0002;

	int SpecialConstraintMask = 0x001c;
	int ReferenceTypeConstraint = 0x0004;
	int NotNullableValueTypeConstraint = 0x0008;
	int DefaultConstructorConstraint = 0x0010;
}
