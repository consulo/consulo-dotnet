/*
 * Copyright 2013-2016 must-be.org
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

package consulo.dotnet.lang.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.util.BitUtil;
import com.intellij.util.NotNullFunction;
import consulo.annotation.UsedInPlugin;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.util.dataholder.Key;
import consulo.util.dataholder.UserDataHolderEx;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Modifier;

/**
 * @author VISTALL
 * @since 12-May-16
 */
public class DotNetTypeRefCacheUtil
{
	private static class DotNetTypeRefCachedValueProvider<E extends PsiElement> implements CachedValueProvider<DotNetTypeRef>
	{
		private final Object[] myDropKey;
		private final E myElement;
		private final NotNullFunction<E, DotNetTypeRef> myResolver;

		public DotNetTypeRefCachedValueProvider(@Nonnull Object[] dropKey, @Nonnull E element, @Nonnull NotNullFunction<E, DotNetTypeRef> resolver)
		{
			myDropKey = dropKey;
			myElement = element;
			myResolver = resolver;
		}

		@Nullable
		@Override
		public Result<DotNetTypeRef> compute()
		{
			DotNetTypeRef result = myResolver.fun(myElement);
			return new Result<>(result, myDropKey);
		}
	}

	private static final Key<CachedValue<DotNetTypeRef>> ourDefaultCacheKey = Key.create("DotNetTypeRefCacheUtil.ourDefaultCacheKey");

	@UsedInPlugin
	@Nonnull
	@RequiredReadAction
	public static <E extends PsiElement> DotNetTypeRef cacheTypeRef(@Nonnull E element, @Nonnull final NotNullFunction<E, DotNetTypeRef> resolver)
	{
		return cacheTypeRef(ourDefaultCacheKey, element, resolver);
	}

	@UsedInPlugin
	@Nonnull
	@RequiredReadAction
	public static <E extends PsiElement> DotNetTypeRef cacheTypeRef(@Nonnull Key<CachedValue<DotNetTypeRef>> key, @Nonnull E element, @Nonnull final NotNullFunction<E, DotNetTypeRef> resolver)
	{
		return cacheTypeRef(key, element, resolver, PsiModificationTracker.OUT_OF_CODE_BLOCK_MODIFICATION_COUNT);
	}

	@UsedInPlugin
	@Nonnull
	@RequiredReadAction
	public static <E extends PsiElement> DotNetTypeRef localCacheTypeRef(@Nonnull E element, @Nonnull final NotNullFunction<E, DotNetTypeRef> resolver)
	{
		return localCacheTypeRef(ourDefaultCacheKey, element, resolver);
	}

	@UsedInPlugin
	@Nonnull
	@RequiredReadAction
	public static <E extends PsiElement> DotNetTypeRef localCacheTypeRef(@Nonnull Key<CachedValue<DotNetTypeRef>> key, @Nonnull E element, @Nonnull final NotNullFunction<E, DotNetTypeRef> resolver)
	{
		return cacheTypeRef(key, element, resolver, PsiModificationTracker.MODIFICATION_COUNT);
	}

	@UsedInPlugin
	@Nonnull
	@RequiredReadAction
	public static <E extends PsiElement> DotNetTypeRef cacheTypeRef(@Nonnull Key<CachedValue<DotNetTypeRef>> key,
																	@Nonnull E element,
																	@Nonnull final NotNullFunction<E, DotNetTypeRef> resolver,
																	Object... modifierKeys)
	{
		return getResultCacheResultImpl(key, element, resolver, modifierKeys);
	}

	@UsedInPlugin
	@Nonnull
	@RequiredReadAction
	private static <E extends PsiElement> DotNetTypeRef getResultCacheResultImpl(@Nonnull Key<CachedValue<DotNetTypeRef>> cachedValueKey,
																				 @Nonnull E element,
																				 @Nonnull final NotNullFunction<E, DotNetTypeRef> resolver,
																				 @Nonnull Object... modifierKeys)
	{
		Class<? extends NotNullFunction> aClass = resolver.getClass();
		if(!BitUtil.isSet(aClass.getModifiers(), Modifier.STATIC))
		{
			throw new IllegalArgumentException("Accepted only static resolver");
		}

		CachedValue<DotNetTypeRef> cachedValue = element.getUserData(cachedValueKey);
		if(cachedValue == null)
		{
			DotNetTypeRefCachedValueProvider<E> provider = new DotNetTypeRefCachedValueProvider<>(modifierKeys, element, resolver);

			cachedValue = ((UserDataHolderEx) element).putUserDataIfAbsent(cachedValueKey, CachedValuesManager.getManager(element.getProject()).createCachedValue(provider, false));

			return cachedValue.getValue();
		}
		return cachedValue.getValue();
	}
}
