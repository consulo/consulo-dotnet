package consulo.msil.impl.ide.presentation;

import consulo.component.util.Iconable;
import consulo.dotnet.psi.DotNetElementPresentationUtil;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.language.icon.IconDescriptorUpdaters;
import consulo.navigation.ItemPresentation;
import consulo.navigation.ItemPresentationProvider;
import consulo.navigation.NavigationItem;
import consulo.ui.image.Image;
import consulo.util.lang.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 05.07.2015
 */
public class MsilClassItemPresentationProvider implements ItemPresentationProvider<NavigationItem>
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

	@Override
	public ItemPresentation getPresentation(NavigationItem item)
	{
		return new Item((DotNetTypeDeclaration) item);
	}
}
