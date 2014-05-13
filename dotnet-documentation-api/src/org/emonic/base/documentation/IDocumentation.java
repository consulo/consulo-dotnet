/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.documentation;

/**
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IDocumentation
{
	public String getName();

	public String getSummary();

	public String getRemarks();

	public String getValue();

	public String getReturns();

	public String getSeeAlso();

	public IDocumentation[] getParams();

	public IDocumentation[] getExceptions();
}