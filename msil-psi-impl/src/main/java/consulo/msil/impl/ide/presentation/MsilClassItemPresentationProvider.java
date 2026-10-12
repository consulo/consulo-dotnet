package consulo.msil.impl.ide.presentation;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.component.util.Iconable;
import consulo.dotnet.psi.DotNetElementPresentationUtil;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.language.icon.IconDescriptorUpdaters;
import consulo.localize.LocalizeValue;
import consulo.msil.lang.psi.MsilClassEntry;
import consulo.navigation.ItemPresentation;
import consulo.navigation.ItemPresentationProvider;
import consulo.ui.image.Image;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 2015-07-05
 */
@ExtensionImpl
public class MsilClassItemPresentationProvider implements ItemPresentationProvider<MsilClassEntry> {
    private static class Item implements ItemPresentation {
        private final DotNetTypeDeclaration myDeclaration;

        public Item(DotNetTypeDeclaration declaration) {
            myDeclaration = declaration;
        }

        @Override
        @RequiredReadAction
        public LocalizeValue getPresentableText() {
            return LocalizeValue.of(DotNetElementPresentationUtil.formatTypeWithGenericParameters(myDeclaration));
        }

        @Override
        @RequiredReadAction
        public @Nullable String getLocationString() {
            String presentableParentQName = myDeclaration.getPresentableParentQName();
            if (StringUtil.isEmpty(presentableParentQName)) {
                return null;
            }
            return "(" + presentableParentQName + ")";
        }

        @Override
        @RequiredReadAction
        public @Nullable Image getIcon() {
            return IconDescriptorUpdaters.getIcon(myDeclaration, Iconable.ICON_FLAG_VISIBILITY);
        }
    }

    @Override
    public Class<MsilClassEntry> getItemClass() {
        return MsilClassEntry.class;
    }

    @Override
    public ItemPresentation getPresentation(MsilClassEntry item) {
        return new Item(item);
    }
}
