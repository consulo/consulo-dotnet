package consulo.msil.ide;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.util.BitUtil;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.psi.DotNetInheritUtil;
import consulo.dotnet.psi.DotNetModifierListOwner;
import consulo.ide.IconDescriptor;
import consulo.ide.IconDescriptorUpdater;
import consulo.msil.lang.psi.MsilClassEntry;
import consulo.msil.lang.psi.MsilTokens;
import consulo.msil.lang.psi.impl.MsilNamespaceAsElementImpl;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.image.Image;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 05.07.2015
 */
public class MsilIconDescriptorUpdater implements IconDescriptorUpdater
{
	@Override
	@RequiredReadAction
	public void updateIcon(@Nonnull IconDescriptor iconDescriptor, @Nonnull PsiElement element, int flags)
	{
		if(element instanceof MsilNamespaceAsElementImpl)
		{
			iconDescriptor.setMainIcon(PlatformIconGroup.nodesNamespace());
			return;
		}

		if(element instanceof MsilClassEntry)
		{
			Image main = null;

			MsilClassEntry typeDeclaration = (MsilClassEntry) element;
			if(!DumbService.getInstance(element.getProject()).isDumb())
			{
				if(DotNetInheritUtil.isAttribute(typeDeclaration))
				{
					main = typeDeclaration.hasModifier(MsilTokens.ABSTRACT_KEYWORD) ? AllIcons.Nodes.AbstractAttribute : AllIcons.Nodes.Attribute;
				}
				else if(DotNetInheritUtil.isException(typeDeclaration))
				{
					main = typeDeclaration.hasModifier(MsilTokens.ABSTRACT_KEYWORD) ? AllIcons.Nodes.AbstractException : AllIcons.Nodes.ExceptionClass;
				}
				else if(DotNetInheritUtil.isInheritor(typeDeclaration, DotNetTypes.System.MulticastDelegate, false))
				{
					main = AllIcons.Nodes.Method;
				}
				else if(typeDeclaration.isEnum())
				{
					main = AllIcons.Nodes.Enum;
				}
				else if(typeDeclaration.isStruct())
				{
					main = AllIcons.Nodes.Struct;
				}
			}

			if(main == null)
			{
				if(typeDeclaration.isInterface())
				{
					main = AllIcons.Nodes.Interface;
				}
				else
				{
					main = typeDeclaration.hasModifier(MsilTokens.ABSTRACT_KEYWORD) ? AllIcons.Nodes.AbstractClass : AllIcons.Nodes.Class;
				}
			}

			iconDescriptor.setMainIcon(main);

			processModifierListOwner(element, iconDescriptor, flags);
		}
	}

	@RequiredReadAction
	private static void processModifierListOwner(PsiElement element, IconDescriptor iconDescriptor, int flags)
	{
		DotNetModifierListOwner owner = (DotNetModifierListOwner) element;
		if(BitUtil.isSet(flags, Iconable.ICON_FLAG_VISIBILITY))
		{
			if(owner.hasModifier(MsilTokens.PRIVATE_KEYWORD))
			{
				iconDescriptor.setRightIcon(AllIcons.Nodes.C_private);
			}
			else if(owner.hasModifier(MsilTokens.PUBLIC_KEYWORD))
			{
				iconDescriptor.setRightIcon(AllIcons.Nodes.C_public);
			}
			else if(owner.hasModifier(MsilTokens.PROTECTED_KEYWORD))
			{
				iconDescriptor.setRightIcon(AllIcons.Nodes.C_protected);
			}
			else
			{
				iconDescriptor.setRightIcon(AllIcons.Nodes.C_plocal);
			}
		}

		if(owner.hasModifier(MsilTokens.SEALED_KEYWORD))
		{
			iconDescriptor.addLayerIcon(AllIcons.Nodes.FinalMark);
		}
	}
}
