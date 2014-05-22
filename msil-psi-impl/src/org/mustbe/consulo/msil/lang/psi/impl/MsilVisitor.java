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

import org.mustbe.consulo.msil.lang.psi.MsilAssemblyEntry;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import org.mustbe.consulo.msil.lang.psi.MsilCustomAttribute;
import org.mustbe.consulo.msil.lang.psi.MsilEventEntry;
import org.mustbe.consulo.msil.lang.psi.MsilFieldEntry;
import org.mustbe.consulo.msil.lang.psi.MsilMethodEntry;
import org.mustbe.consulo.msil.lang.psi.MsilModifierList;
import org.mustbe.consulo.msil.lang.psi.MsilPropertyEntry;
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
}
