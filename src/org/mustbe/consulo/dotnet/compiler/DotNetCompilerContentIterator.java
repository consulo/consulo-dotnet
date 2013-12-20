package org.mustbe.consulo.dotnet.compiler;

import java.util.Collection;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.FileIndex;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author VISTALL
 * @since 20.12.13.
 */
public class DotNetCompilerContentIterator implements ContentIterator
{
	private final FileType myFileType;
	private final FileIndex myFileIndex;
	private final boolean myInSourceOnly;
	private final Collection<VirtualFile> myFiles;

	public DotNetCompilerContentIterator(FileType fileType, FileIndex fileIndex, boolean inSourceOnly, Collection<VirtualFile> files)
	{
		myFileType = fileType;
		myFileIndex = fileIndex;
		myInSourceOnly = inSourceOnly;
		myFiles = files;
	}

	@Override
	public boolean processFile(VirtualFile fileOrDir)
	{
		if(fileOrDir.isDirectory())
		{
			return true;
		}
		if(!fileOrDir.isInLocalFileSystem())
		{
			return true;
		}
		/*if(myInSourceOnly && !myFileIndex.isInSourceContent(fileOrDir))
		{
			return true;
		}  */
		if(myFileType == null || myFileType == fileOrDir.getFileType())
		{
			myFiles.add(fileOrDir);
		}
		return true;
	}
}
