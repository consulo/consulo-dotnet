package consulo.msil.ide.presentation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.text.StringUtil;
import consulo.awt.TargetAWT;
import consulo.dotnet.ide.DotNetElementPresentationUtil;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.ide.IconDescriptorUpdaters;

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
		public javax.swing.Icon getIcon(boolean b)
		{
			return TargetAWT.to(IconDescriptorUpdaters.getIcon(myDeclaration, Iconable.ICON_FLAG_VISIBILITY));
		}
	}

	@Override
	public ItemPresentation getPresentation(NavigationItem item)
	{
		return new Item((DotNetTypeDeclaration) item);
	}
}
