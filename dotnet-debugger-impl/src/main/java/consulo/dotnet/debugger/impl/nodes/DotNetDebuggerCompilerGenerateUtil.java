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

package consulo.dotnet.debugger.impl.nodes;

import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.util.lang.Couple;

import org.jspecify.annotations.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author VISTALL
 * @since 22.07.2015
 */
public class DotNetDebuggerCompilerGenerateUtil
{
	// mcs: <Method>m__0 - or <>m__0 in the <Method>c__AnonStorey0 type, which has the captured variables
	public static final Pattern LambdaMethodPattern = Pattern.compile("<([\\S\\d]*)>m__([\\d]+)");
	// Roslyn: <Method>b__0_1 - in the type of the method, in <>c without captured variables, in <>c__DisplayClass0_0 with them
	public static final Pattern LambdaMethodRoslynPattern = Pattern.compile("<([\\S\\d]+)>b__([\\d_]+)");
	private static final Pattern LambdaCacheTypeRoslyn = Pattern.compile("<>c(`\\d+)?");
	public static final Pattern YieldNestedTypePattern = Pattern.compile("<([\\S\\d]+)>c__Iterator(\\p{XDigit}+)");
	public static final Pattern YieldNestedTypeRoslynPattern = Pattern.compile("<([\\S\\d]+)>d__(\\p{XDigit}+)");
	public static final Pattern AsyncNestedTypePattern = Pattern.compile("<([\\S\\d]+)>c__async(\\p{XDigit}+)");

	public static final Pattern SomeReferenceToOriginalPattern = Pattern.compile("<([\\S\\d]+)>__([\\d]+)");
	public static final Pattern SomeReferenceToOriginalRoslynPattern = Pattern.compile("<([\\S\\d]+)>(\\d+)__([\\d]+)");

	private static final Pattern AsyncLambdaFirstWrapperMS = Pattern.compile("<>c__DisplayClass([\\d]+)(_[\\d]+)?(`\\d+)?");
	private static final Pattern AsyncLambdaFirstWrapperMono = Pattern.compile("<([\\S\\d]+)>c__AnonStorey([\\d]+)");

	private static final Pattern LocalVarWrapperPatternMono = Pattern.compile("\\$locvar\\p{XDigit}+");
	private static final Pattern LocalVarWrapperPatternMS = Pattern.compile("CS\\$<>\\p{XDigit}+__locals\\p{XDigit}+");

	private static final Pattern AsyncThisWrapperRoslyn = Pattern.compile("<>(\\d)__this");

	@Nullable
	public static Couple<String> extractLambdaInfo(DotNetMethodProxy methodMirror)
	{
		return extractLambdaInfo(methodMirror.getName(), methodMirror.getDeclarationType().getName());
	}

	/**
	 * @return the name of the method the lambda is written in, and the number of the lambda - null if the method is no lambda
	 */
	@Nullable
	public static Couple<String> extractLambdaInfo(String methodName, String declarationTypeName)
	{
		Matcher matcher = LambdaMethodPattern.matcher(methodName);
		if(matcher.matches())
		{
			String method = matcher.group(1);
			if(method.isEmpty())
			{
				// <>m__0 of <Method>c__AnonStorey0
				Matcher storey = AsyncLambdaFirstWrapperMono.matcher(declarationTypeName);
				method = storey.matches() ? storey.group(1) : method;
			}
			return Couple.of(method, matcher.group(2));
		}

		matcher = LambdaMethodRoslynPattern.matcher(methodName);
		if(matcher.matches())
		{
			return Couple.of(matcher.group(1), matcher.group(2));
		}
		return null;
	}

	/**
	 * A nested type the compiler makes for the lambdas of the methods of its outer type: the cache of lambdas without captured
	 * variables (Roslyn <>c), and the closures with them (Roslyn <>c__DisplayClass0_0, mcs <Method>c__AnonStorey0)
	 */
	public static boolean isLambdaHolderType(DotNetTypeProxy typeMirror)
	{
		return typeMirror.isNested() && isLambdaHolderTypeName(typeMirror.getName());
	}

	public static boolean isLambdaHolderTypeName(String name)
	{
		return LambdaCacheTypeRoslyn.matcher(name).matches() || AsyncLambdaFirstWrapperMS.matcher(name).matches()
				|| AsyncLambdaFirstWrapperMono.matcher(name).matches();
	}

	public static boolean isLocalVarWrapper(String name)
	{
		return LocalVarWrapperPatternMono.matcher(name).matches() || LocalVarWrapperPatternMS.matcher(name).matches();
	}

	public static boolean isAsyncLambdaWrapper(DotNetTypeProxy typeMirror)
	{
		return typeMirror.isNested() && (AsyncLambdaFirstWrapperMono.matcher(typeMirror.getName()).matches() || AsyncLambdaFirstWrapperMS.matcher(typeMirror.getName()).matches());
	}

	public static boolean isYieldOrAsyncNestedType(DotNetTypeProxy typeMirror)
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

	public static boolean isYieldOrAsyncThisField(String fieldName)
	{
		return "$this".equals(fieldName) || "<>f__this".equals(fieldName) || AsyncThisWrapperRoslyn.matcher(fieldName).matches();
	}

	public static boolean needSkipVariableByName(String name)
	{
		if(name.isEmpty())
		{
			return true;
		}

		char firstChar = name.charAt(0);
		return firstChar == '<' || firstChar == '$';
	}

	@Nullable
	public static String extractNotGeneratedName(String name)
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
