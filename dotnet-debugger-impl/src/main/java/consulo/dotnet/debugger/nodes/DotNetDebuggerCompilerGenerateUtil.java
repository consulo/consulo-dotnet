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

import com.intellij.openapi.util.Couple;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author VISTALL
 * @since 22.07.2015
 */
public class DotNetDebuggerCompilerGenerateUtil
{
	public static final Pattern LambdaMethodPattern = Pattern.compile("<([\\S\\d]+)>m__([\\d]+)");
	public static final Pattern YieldNestedTypePattern = Pattern.compile("<([\\S\\d]+)>c__Iterator(\\p{XDigit}+)");
	public static final Pattern YieldNestedTypeRoslynPattern = Pattern.compile("<([\\S\\d]+)>d__(\\p{XDigit}+)");
	public static final Pattern AsyncNestedTypePattern = Pattern.compile("<([\\S\\d]+)>c__async(\\p{XDigit}+)");

	public static final Pattern SomeReferenceToOriginalPattern = Pattern.compile("<([\\S\\d]+)>__([\\d]+)");
	public static final Pattern SomeReferenceToOriginalRoslynPattern = Pattern.compile("<([\\S\\d]+)>(\\d+)__([\\d]+)");

	private static final Pattern AsyncLambdaFirstWrapperMS = Pattern.compile("<>c__DisplayClass([\\d]+)_([\\d]+)");
	private static final Pattern AsyncLambdaFirstWrapperMono = Pattern.compile("<([\\S\\d]+)>c__AnonStorey([\\d]+)");

	private static final Pattern LocalVarWrapperPatternMono = Pattern.compile("\\$locvar\\p{XDigit}+");
	private static final Pattern LocalVarWrapperPatternMS = Pattern.compile("CS\\$<>\\p{XDigit}+__locals\\p{XDigit}+");

	private static final Pattern AsyncThisWrapperRoslyn = Pattern.compile("<>(\\d)__this");

	@Nullable
	public static Couple<String> extractLambdaInfo(@Nonnull DotNetMethodProxy methodMirror)
	{
		Matcher matcher = LambdaMethodPattern.matcher(methodMirror.getName());
		if(matcher.matches())
		{
			return Couple.of(matcher.group(1), matcher.group(2));
		}
		return null;
	}

	public static boolean isLocalVarWrapper(@Nonnull String name)
	{
		return LocalVarWrapperPatternMono.matcher(name).matches() || LocalVarWrapperPatternMS.matcher(name).matches();
	}

	public static boolean isAsyncLambdaWrapper(@Nonnull DotNetTypeProxy typeMirror)
	{
		return typeMirror.isNested() && (AsyncLambdaFirstWrapperMono.matcher(typeMirror.getName()).matches() || AsyncLambdaFirstWrapperMS.matcher(typeMirror.getName()).matches());
	}

	public static boolean isYieldOrAsyncNestedType(@Nonnull DotNetTypeProxy typeMirror)
	{
		String name = typeMirror.getName();

		if(typeMirror.isNested())
		{
			if(YieldNestedTypePattern.matcher(name).matches())
			{
				return true;
			}

			if(YieldNestedTypeRoslynPattern.matcher(name).matches())
			{
				return true;
			}
		}

		if(AsyncNestedTypePattern.matcher(name).matches())
		{
			return true;
		}
		return false;
	}

	public static boolean isYieldOrAsyncThisField(@Nonnull String fieldName)
	{
		return "$this".equals(fieldName) || "<>f__this".equals(fieldName) || AsyncThisWrapperRoslyn.matcher(fieldName).matches();
	}

	public static boolean needSkipVariableByName(@Nonnull String name)
	{
		if(name.isEmpty())
		{
			return true;
		}

		char firstChar = name.charAt(0);
		return firstChar == '<' || firstChar == '$';
	}

	@Nullable
	public static String extractNotGeneratedName(@Nonnull String name)
	{
		Matcher matcher = SomeReferenceToOriginalPattern.matcher(name);
		if(matcher.matches())
		{
			return matcher.group(1);
		}

		matcher = SomeReferenceToOriginalRoslynPattern.matcher(name);
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
