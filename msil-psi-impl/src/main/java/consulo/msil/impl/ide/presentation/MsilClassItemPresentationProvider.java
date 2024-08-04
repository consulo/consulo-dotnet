package consulo.msil.impl.ide.presentation;

import consulo.annotation.component.ExtensionImpl;
import consulo.component.util.Iconable;
import consulo.dotnet.psi.DotNetElementPresentationUtil;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.language.icon.IconDescriptorUpdaters;
import consulo.msil.lang.psi.MsilClassEntry;
import consulo.navigation.ItemPresentation;
import consulo.navigation.ItemPresentationProvider;
import consulo.ui.image.Image;
import consulo.util.lang.StringUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 05.07.2015
 */
@ExtensionImpl
public class MsilClassItemPresentationProvider implements ItemPresentationProvider<MsilClassEntry>
{
	private static class Item implements ItemPresentation
	{
		private final DotNetTypeDeclaration myDeclaration;

		public Item(@Nonnull DotNetTypeDeclaration declaration)
		{
			myDeclaration = declaration;
		}

		@Nullable
		@Override
		public String getPresentableText()
		{
			return DotNetElementPresentationUtil.formatTypeWithGenericParameters(myDeclaration);
		}

		@Nullable
		@Override
		public String getLocationString()
		{
			String presentableParentQName = myDeclaration.getPresentableParentQName();
			if(StringUtil.isEmpty(presentableParentQName))
			{
				return null;
			}
			return "(" + presentableParentQName + ")";
		}

		@Nullable
		@Override
		public Image getIcon()
		{
			return IconDescriptorUpdaters.getIcon(myDeclaration, Iconable.ICON_FLAG_VISIBILITY);
		}
	}

	@Nonnull
	@Override
	public Class<MsilClassEntry> getItemClass()
	{
		return MsilClassEntry.class;
	}

	@Nonnull
	@Override
	public ItemPresentation getPresentation(MsilClassEntry item)
	{
		return new Item((DotNetTypeDeclaration) item);
	}
}
