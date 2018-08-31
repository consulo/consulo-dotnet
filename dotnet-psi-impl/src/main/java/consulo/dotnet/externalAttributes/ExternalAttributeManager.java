package consulo.dotnet.externalAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.intellij.ProjectTopics;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootAdapter;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.ArrayUtil;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBusConnection;
import consulo.roots.types.BinariesOrderRootType;
import consulo.vfs.util.ArchiveVfsUtil;

/**
 * @author VISTALL
 * @since 27.07.2015
 */
@Singleton
public class ExternalAttributeManager
{
	@Nonnull
	public static ExternalAttributeManager getInstance(@Nonnull Project project)
	{
		return ServiceManager.getService(project, ExternalAttributeManager.class);
	}

	private Map<VirtualFile, ExternalAttributeHolder> myCache = ContainerUtil.createConcurrentWeakMap();
	private final Project myProject;

	@Inject
	ExternalAttributeManager(Project project)
	{
		myProject = project;
		MessageBusConnection connect = project.getMessageBus().connect();
		connect.subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener()
		{
			@Override
			public void after(@Nonnull List<? extends VFileEvent> events)
			{
				for(VFileEvent event : events)
				{
					myCache.remove(event.getFile());
				}
			}
		});

		connect.subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter()
		{
			@Override
			public void rootsChanged(ModuleRootEvent event)
			{
				myCache.clear();
			}
		});
	}

	@Nonnull
	public ExternalAttributeHolder resolveHolder(@Nonnull VirtualFile virtualFile)
	{
		VirtualFile localFile = ArchiveVfsUtil.getVirtualFileForArchive(virtualFile);
		if(localFile == null)
		{
			return ExternalAttributeHolder.EMPTY;
		}

		ExternalAttributeHolder externalAttributeHolder = myCache.get(localFile);
		if(externalAttributeHolder == null)
		{
			myCache.put(localFile, externalAttributeHolder = resolveHolderImpl(virtualFile, localFile));
			return externalAttributeHolder;
		}
		else
		{
			return externalAttributeHolder;
		}
	}

	@Nonnull
	private ExternalAttributeHolder resolveHolderImpl(@Nonnull VirtualFile virtualFile, @Nonnull VirtualFile localFile)
	{
		VirtualFile archiveFile = ArchiveVfsUtil.getArchiveRootForLocalFile(localFile);
		if(archiveFile == null)
		{
			return ExternalAttributeHolder.EMPTY;
		}

		List<OrderEntry> orderEntriesForFile = ProjectFileIndex.getInstance(myProject).getOrderEntriesForFile(virtualFile);

		List<VirtualFile> externalAttributeFiles = new SmartList<VirtualFile>();
		for(OrderEntry orderEntry : orderEntriesForFile)
		{
			if(ArrayUtil.contains(archiveFile, orderEntry.getFiles(BinariesOrderRootType.getInstance())))
			{
				VirtualFile[] files = orderEntry.getFiles(ExternalAttributesRootOrderType.getInstance());
				if(files.length != 0)
				{
					Collections.addAll(externalAttributeFiles, files);
				}
			}
		}

		if(externalAttributeFiles.isEmpty())
		{
			return ExternalAttributeHolder.EMPTY;
		}
		else if(externalAttributeFiles.size() == 1)
		{
			return SingleExternalAttributeHolder.load(externalAttributeFiles.get(0));
		}
		else
		{
			List<ExternalAttributeHolder> list = new SmartList<ExternalAttributeHolder>();
			for(VirtualFile externalAttributeFile : externalAttributeFiles)
			{
				list.add(SingleExternalAttributeHolder.load(externalAttributeFile));
			}
			return new CompositeExternalAttributeHolder(list);
		}
	}
}
