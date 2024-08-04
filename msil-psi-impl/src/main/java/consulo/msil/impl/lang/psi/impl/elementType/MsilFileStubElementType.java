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

package consulo.msil.impl.lang.psi.impl.elementType;

import consulo.dotnet.DotNetTypes;
import consulo.internal.dotnet.asm.mbel.ModuleParser;
import consulo.internal.dotnet.msil.decompiler.file.DotNetArchiveFile;
import consulo.language.impl.ast.CharTableImpl;
import consulo.language.psi.PsiFile;
import consulo.language.psi.stub.*;
import consulo.msil.MsilLanguage;
import consulo.msil.lang.psi.MsilFile;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilFileStub;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilStubIndexer;

import jakarta.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilFileStubElementType extends IStubFileElementType<MsilFileStub>
{
	static
	{
		CharTableImpl.addStringsFromClassToStatics(DotNetTypes.class);
	}

	public MsilFileStubElementType()
	{
		super("MSIL_FILE", MsilLanguage.INSTANCE);
	}

	@Override
	public StubBuilder getBuilder()
	{
		return new DefaultStubBuilder()
		{
			@Nonnull
			@Override
			protected StubElement createStubForFile(@Nonnull PsiFile file)
			{
				if(file instanceof MsilFile)
				{
					return new MsilFileStub((MsilFile) file);
				}
				return super.createStubForFile(file);
			}
		};
	}

	@Nonnull
	@Override
	public MsilFileStub deserialize(@Nonnull StubInputStream dataStream, StubElement parentStub) throws IOException
	{
		return new MsilFileStub(null);
	}

	@Override
	public int getStubVersion()
	{
		int version = 74 + DotNetArchiveFile.VERSION + ModuleParser.VERSION;
		for(MsilStubIndexer msilStubIndexer : MsilStubIndexer.EP_NAME.getExtensionList())
		{
			version += msilStubIndexer.getVersion();
		}
		return version;
	}

	@Nonnull
	@Override
	public String getExternalId()
	{
		return "msil.file";
	}
}
