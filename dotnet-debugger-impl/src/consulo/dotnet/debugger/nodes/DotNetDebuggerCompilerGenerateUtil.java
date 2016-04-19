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

package consulo.dotnet.debugger.nodes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.Couple;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;

/**
 * @author VISTALL
 * @since 22.07.2015
 */
public class DotNetDebuggerCompilerGenerateUtil
{
	public static final Pattern LambdaMethodPattern = Pattern.compile("<([\\S\\d]+)>m__([\\d]+)");
	public static final Pattern YieldNestedTypePattern = Pattern.compile("<([\\S\\d]+)>c__Iterator([\\d]+)");
	public static final Pattern AsyncNestedTypePattern = Pattern.compile("<([\\S\\d]+)>c__async([\\d]+)");
	public static final Pattern SomeReferenceToOriginalPattern = Pattern.compile("<([\\S\\d]+)>__([\\d]+)");

	private static final Pattern AsyncLambdaFirstWrapperMS = Pattern.compile("<>c__DisplayClass([\\d]+)_([\\d]+)");
	private static final Pattern AsyncLambdaFirstWrapperMono = Pattern.compile("<([\\S\\d]+)>c__AnonStorey([\\d]+)");

	@Nullable
	public static Couple<String> extractLambdaInfo(@NotNull DotNetMethodProxy methodMirror)
	{
		Matcher matcher = LambdaMethodPattern.matcher(methodMirror.getName());
		if(matcher.matches())
		{
			return Couple.of(matcher.group(1), matcher.group(2));
		}
		return null;
	}

	public static boolean isAsyncLambdaWrapper(@NotNull DotNetTypeProxy typeMirror)
	{
		return typeMirror.isNested() && (AsyncLambdaFirstWrapperMono.matcher(typeMirror.getName()).matches() || AsyncLambdaFirstWrapperMS.matcher(typeMirror.getName()).matches());
	}

	public static boolean isYieldOrAsyncNestedType(@NotNull DotNetTypeProxy typeMirror)
	{
		return typeMirror.isNested() && (YieldNestedTypePattern.matcher(typeMirror.getName()).matches() || AsyncNestedTypePattern.matcher(typeMirror.getName()).matches());
	}

	public static boolean isYieldOrAsyncThisField(@NotNull String fieldName)
	{
		return "$this".equals(fieldName) || "<>f__this".equals(fieldName);
	}

	public static boolean needSkipVariableByName(@NotNull String name)
	{
		if(name.isEmpty())
		{
			return true;
		}

		char firstChar = name.charAt(0);
		return firstChar == '<' || firstChar == '$';
	}

	@Nullable
	public static String extractNotGeneratedName(@NotNull String name)
	{
		Matcher matcher = SomeReferenceToOriginalPattern.matcher(name);
		if(matcher.matches())
		{
			return matcher.group(1);
		}

		if(needSkipVariableByName(name))
		{
			return null;
		}
		return name;
	}
}
