/* MBEL: The Microsoft Bytecode Engineering Library
 * Copyright (C) 2003 The University of Arizona
 * http://www.cs.arizona.edu/mbel/license.html
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.arizona.cs.mbel.mbel;

/**
 * This represents a manifest resource that resides in another file that
 * is referenced by this module.
 *
 * @author Michael Stepp
 */
public class FileManifestResource extends ManifestResource
{
	private FileReference file;

	/**
	 * Makes a FileManifestResource with the given File reference and flags
	 *
	 * @param ref   the file in which the resource lies
	 * @param flags a bit vector of flags (defined in ManifestResourceAttributes)
	 */
	public FileManifestResource(FileReference ref, long flags)
	{
		super(ref.getFileName(), flags);
		file = ref;
	}

	/**
	 * Returns the file reference for this resource
	 */
	public FileReference getFileReference()
	{
		return file;
	}

	/**
	 * Compares 2 FileManifestResources.
	 * Returns true iff super.equals and the file references are equal
	 */
	public boolean equals(Object o)
	{
		if(!super.equals(o))
		{
			return false;
		}
		if(!(o instanceof FileManifestResource))
		{
			return false;
		}

		FileManifestResource res = (FileManifestResource) o;
		return file.equals(res.file);
	}
   
/*
   public void output(){
      System.out.print("FileManifestResource[Name=\""+getName()+"\", File=");
      file.output();
      System.out.print("]");
   }
*/
}
