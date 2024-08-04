package consulo.msil.impl.lang.psi.impl.elementType;

import consulo.annotation.access.RequiredReadAction;
import consulo.index.io.StringRef;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.msil.lang.psi.MsilConstantValue;
import consulo.msil.impl.lang.psi.MsilTokens;
import consulo.msil.impl.lang.psi.impl.MsilConstantValueImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilConstantValueStub;
import consulo.util.collection.ArrayUtil;

import jakarta.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 19.05.2015
 */
public class MsilConstantValueStubElementType extends AbstractMsilStubElementType<MsilConstantValueStub, MsilConstantValue>
{
	public static final IElementType[] ourElements = new IElementType[]{
			MsilTokens.NULLREF_KEYWORD,
			MsilTokens.STRING_KEYWORD,
			MsilTokens.FLOAT32_KEYWORD,
			MsilTokens.FLOAT64_KEYWORD,
			MsilTokens.INT8_KEYWORD,
			MsilTokens.UINT8_KEYWORD,
			MsilTokens.INT16_KEYWORD,
			MsilTokens.UINT16_KEYWORD,
			MsilTokens.INT32_KEYWORD,
			MsilTokens.UINT32_KEYWORD,
			MsilTokens.INT64_KEYWORD,
			MsilTokens.UINT64_KEYWORD,
			MsilTokens.CHAR_KEYWORD,
			MsilTokens.BOOL_KEYWORD
	};

	public static TokenSet ourSet = TokenSet.create(ourElements);

	public MsilConstantValueStubElementType()
	{
		super("CONSTANT_VALUE");
	}

	@Nonnull
	@Override
	public MsilConstantValue createPsi(@Nonnull MsilConstantValueStub msilConstantValueStub)
	{
		return new MsilConstantValueImpl(msilConstantValueStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilConstantValueStub createStub(@Nonnull MsilConstantValue psi, StubElement parentStub)
	{
		IElementType valueType = psi.getValueType();
		int index = valueType == null ? -1 : ArrayUtil.indexOf(ourElements, valueType);
		String valueText = psi.getValueText();
		return new MsilConstantValueStub(parentStub, this, index, valueText);
	}

	@Nonnull
	@Override
	public PsiElement createElement(@Nonnull ASTNode astNode)
	{
		return new MsilConstantValueImpl(astNode);
	}

	@Override
	public void serialize(@Nonnull MsilConstantValueStub stub, @Nonnull StubOutputStream dataStream) throws IOException
	{
		dataStream.writeVarInt(stub.getValueIndex());
		dataStream.writeName(stub.getValue());
	}

	@Nonnull
	@Override
	public MsilConstantValueStub deserialize(@Nonnull StubInputStream dataStream, StubElement parentStub) throws IOException
	{
		int valueIndex = dataStream.readVarInt();
		StringRef valueText = dataStream.readName();
		return new MsilConstantValueStub(parentStub, this, valueIndex, StringRef.toString(valueText));
	}
}
