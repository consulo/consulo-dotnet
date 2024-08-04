package consulo.msil.impl.lang.psi;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.psi.stub.ObjectStubSerializerProvider;
import consulo.language.psi.stub.StubElementTypeHolder;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author VISTALL
 * @since 09-Jul-22
 */
@ExtensionImpl
public class MsilStubElementTypeHolder extends StubElementTypeHolder<MsilStubElements>
{
	@Nullable
	@Override
	public String getExternalIdPrefix()
	{
		return null;
	}

	@Nonnull
	@Override
	public List<ObjectStubSerializerProvider> loadSerializers()
	{
		return allFromStaticFields(MsilStubElements.class, Field::get);
	}
}
