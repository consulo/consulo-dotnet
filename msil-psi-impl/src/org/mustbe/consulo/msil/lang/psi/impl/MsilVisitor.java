/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mustbe.consulo.msil.lang.psi.impl;

import consulo.dotnet.psi.DotNetPointerType;
import consulo.dotnet.psi.DotNetTypeWithTypeArguments;
import org.mustbe.consulo.msil.lang.psi.*;
import com.intellij.psi.PsiElementVisitor;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilVisitor extends PsiElementVisitor
{
	public void visitAssemblyEntry(MsilAssemblyEntry entry)
	{
		visitElement(entry);
	}

	public void visitCustomAttribute(MsilCustomAttribute attribute)
	{
		visitElement(attribute);
	}

	public void visitClassEntry(MsilClassEntry entry)
	{
		visitElement(entry);
	}

	public void visitEventEntry(MsilEventEntry entry)
	{
		visitElement(entry);
	}

	public void visitFieldEntry(MsilFieldEntry entry)
	{
		visitElement(entry);
	}

	public void visitMethodEntry(MsilMethodEntry entry)
	{
		visitElement(entry);
	}

	public void visitPropertyEntry(MsilPropertyEntry entry)
	{
		visitElement(entry);
	}

	public void visitModifierList(MsilModifierList list)
	{
		visitElement(list);
	}

	public void visitPointerType(DotNetPointerType pointerType)
	{
		visitElement(pointerType);
	}

	public void visitTypeWithTypeArguments(DotNetTypeWithTypeArguments type)
	{
		visitElement(type);
	}

	public void visitCustomAttributeSignature(MsilCustomAttributeSignature signature)
	{
		visitElement(signature);
	}

	public void visitTypeParameterAttributeList(MsilTypeParameterAttributeList attributeList)
	{
		visitElement(attributeList);
	}

	public void visitParameterAttributeList(MsilParameterAttributeList attributeList)
	{
		visitElement(attributeList);
	}

	public void visitParameter(MsilParameter msilParameter)
	{
		visitElement(msilParameter);
	}

	public void visitArrayDimension(MsilArrayDimension dimension)
	{
		visitElement(dimension);
	}

	public void visitArrayType(MsilArrayType msilArrayType)
	{
		visitElement(msilArrayType);
	}

	public void visitContantValue(MsilConstantValue value)
	{
		visitElement(value);
	}
}
