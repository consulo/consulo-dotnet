/*
 * Copyright 2013-2015 must-be.org
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

package parsing;

import consulo.language.file.LanguageFileType;
import consulo.msil.MsilFileType;
import consulo.test.junit.impl.language.SimpleParsingTest;
import org.junit.jupiter.api.Test;

/**
 * @author VISTALL
 * @since 13.05.2015
 */
public class MsilParsingTest extends SimpleParsingTest<Object> {
    public MsilParsingTest() {
        super("parsing", "msil");
    }

    @Override
    protected LanguageFileType getFileType(Context context, Object testContext) {
        return MsilFileType.INSTANCE;
    }

    @Override
    protected boolean checkAllPsiRoots() {
        return false;
    }

    @Test
    public void testFieldConstWithValue(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testArrayList(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testFieldWithAttribute(Context context) throws Exception {
        doTest(context, null);
    }

    @Test
    public void testGenericConstraintAttribute(Context context) throws Exception {
        doTest(context, null);
    }
}
