/*
 * Copyright 2000-2012 JetBrains s.r.o.
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
package org.mustbe.consulo.dotnet.module.extension;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.diagnostic.LogUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.ConcurrencyUtil;
import com.intellij.util.containers.ContainerUtil;
import edu.arizona.cs.mbel.mbel.ModuleParser;
import edu.arizona.cs.mbel.parse.MSILParseException;

/**
 * @author VISTALL
 * @see com.intellij.openapi.util.io.ZipFileCache
 * @since 03.11.14
 * <p/>
 * This is variable of but for ModuleParser
 */
public class DotNetLibraryOpenCache
{
	private static final int PERIOD = 10000;   // disposer schedule, ms
	private static final int TIMEOUT = 60000;  // released file close delay, ms

	private static class CacheRecord
	{
		private final String path;
		private final ModuleParser file;
		private int count = 1;
		private long released = 0;

		private CacheRecord(@NotNull String path, @NotNull ModuleParser file) throws IOException
		{
			this.path = path;
			this.file = file;
		}
	}

	private static final Object ourLock = new Object();
	private static final Map<String, CacheRecord> ourPathCache = ContainerUtil.newTroveMap(FileUtil.PATH_HASHING_STRATEGY);
	private static final Map<ModuleParser, CacheRecord> ourFileCache = ContainerUtil.newHashMap();
	private static final Map<ModuleParser, Integer> ourQueue = ContainerUtil.newHashMap();

	static
	{
		ConcurrencyUtil.newSingleScheduledThreadExecutor("DotNetLibraryOpenCache Dispose", Thread.MIN_PRIORITY).scheduleWithFixedDelay(new Runnable()
		{
			@Override
			public void run()
			{
				getFilesToClose(0, System.currentTimeMillis() - TIMEOUT);
			}
		}, PERIOD, PERIOD, TimeUnit.MILLISECONDS);
	}

	@NotNull
	public static ModuleParser acquire(@NotNull String path) throws IOException, MSILParseException
	{
		path = FileUtil.toCanonicalPath(path);

		synchronized(ourLock)
		{
			CacheRecord record = ourPathCache.get(path);
			if(record != null)
			{
				record.count++;
				return record.file;
			}
		}

		CacheRecord record;
		ModuleParser file = tryOpen(path);

		synchronized(ourLock)
		{
			record = ourPathCache.get(path);
			if(record == null)
			{
				record = new CacheRecord(path, file);
				ourPathCache.put(path, record);
				ourFileCache.put(file, record);
				return file;
			}
			else
			{
				record.count++;
			}
		}

		return record.file;
	}

	@NotNull
	public static ModuleParser acquireWithNext(@NotNull String path) throws IOException, MSILParseException
	{
		ModuleParser acquire = acquire(path);
		acquire.parseNext();
		return acquire;
	}

	private static ModuleParser tryOpen(String path) throws IOException, MSILParseException
	{
		path = FileUtil.toSystemDependentName(path);
		debug("opening %s", path);
		return new ModuleParser(new FileInputStream(path));
	}

	private static int tryCloseFiles()
	{
		List<ModuleParser> toClose = getFilesToClose(5, 0);
		if(toClose == null)
		{
			return 0;
		}
		logger().warn("too many open files, closed: " + toClose.size());
		return toClose.size();
	}

	@Nullable
	private static List<ModuleParser> getFilesToClose(int limit, long timeout)
	{
		List<ModuleParser> toClose = null;

		synchronized(ourLock)
		{
			Iterator<CacheRecord> i = ourPathCache.values().iterator();
			while(i.hasNext() && (limit == 0 || toClose == null || toClose.size() < limit))
			{
				CacheRecord record = i.next();
				if(record.count <= 0 && (timeout == 0 || record.released <= timeout))
				{
					i.remove();
					ourFileCache.remove(record.file);
					if(toClose == null)
					{
						toClose = ContainerUtil.newArrayList();
					}
					toClose.add(record.file);
				}
			}
		}

		return toClose;
	}

	public static void release(@NotNull ModuleParser file)
	{
		synchronized(ourLock)
		{
			CacheRecord record = ourFileCache.get(file);
			if(record != null)
			{
				record.count--;
				record.released = System.currentTimeMillis();
				logger().assertTrue(record.count >= 0, record.path);
				return;
			}

			Integer count = ourQueue.get(file);
			if(count != null)
			{
				count--;
				if(count == 0)
				{
					ourQueue.remove(file);
				}
				else
				{
					ourQueue.put(file, count);
				}
				return;
			}
		}

		logger().warn(new IllegalArgumentException("stray file: " + file.getAssemblyInfo().getName()));
	}

	public static void reset(@NotNull Collection<String> paths)
	{
		debug("resetting %s", paths);

		List<ModuleParser> toClose = ContainerUtil.newSmartList();

		synchronized(ourLock)
		{
			for(String path : paths)
			{
				path = FileUtil.toCanonicalPath(path);
				CacheRecord record = ourPathCache.remove(path);
				if(record != null)
				{
					ourFileCache.remove(record.file);
					if(record.count > 0)
					{
						ourQueue.put(record.file, record.count);
					}
					else
					{
						toClose.add(record.file);
					}
				}
			}
		}
	}


	private static Logger logger()
	{
		return Logger.getInstance(DotNetLibraryOpenCache.class);
	}

	private static void debug(@NotNull String format, Object... args)
	{
		LogUtil.debug(logger(), format, args);
	}
}
