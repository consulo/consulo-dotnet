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

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.wrapper;

import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.psi.DotNetReferenceType;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeGenericExtractor;

/**
 * @author VISTALL
 * @since 13.01.14
 */
public class GenericUnwrapTool
{
	@SuppressWarnings("unchecked")
	public static <T extends DotNetNamedElement> T extract(T namedElement, DotNetRuntimeGenericExtractor extractor)
	{
		if(extractor == DotNetRuntimeGenericExtractor.EMPTY)
		{
			return namedElement;
		}
		/*if(namedElement instanceof CSharpMethodDeclaration)
		{
			return (T) new CSharpLightMethodDeclaration((CSharpMethodDeclaration) namedElement);
		} */
		return namedElement;
	}

	public static DotNetType unwrapType(DotNetType type)
	{
		type.accept(new CSharpElementVisitor()
		{
			@Override
			public void visitReferenceType(DotNetReferenceType type)
			{
				super.visitReferenceType(type);
			}
		});
		return null;
	}
}
