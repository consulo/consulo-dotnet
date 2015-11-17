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

package org.mustbe.consulo.dotnet.debugger.nodes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.text.StringUtil;
import mono.debugger.MethodMirror;
import mono.debugger.TypeMirror;

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
	public static Couple<String> extractLambdaInfo(@NotNull MethodMirror methodMirror)
	{
		Matcher matcher = LambdaMethodPattern.matcher(methodMirror.name());
		if(matcher.matches())
		{
			return Couple.of(matcher.group(1), matcher.group(2));
		}
		return null;
	}

	public static boolean isAsyncLambdaWrapper(@NotNull TypeMirror typeMirror)
	{
		return typeMirror.isNested() && (AsyncLambdaFirstWrapperMono.matcher(typeMirror.name()).matches() || AsyncLambdaFirstWrapperMS.matcher(typeMirror
				.name()).matches());
	}

	public static boolean isYieldOrAsyncNestedType(@NotNull TypeMirror typeMirror)
	{
		return typeMirror.isNested() && (YieldNestedTypePattern.matcher(typeMirror.name()).matches() || AsyncNestedTypePattern.matcher(typeMirror
				.name()).matches());
	}

	@Nullable
	public static String extractNotGeneratedName(@NotNull String name)
	{
		// generated member
		if(StringUtil.startsWithChar(name, '$'))
		{
			return null;
		}

		Matcher matcher = SomeReferenceToOriginalPattern.matcher(name);
		if(matcher.matches())
		{
			return matcher.group(1);
		}
		return name;
	}
}
