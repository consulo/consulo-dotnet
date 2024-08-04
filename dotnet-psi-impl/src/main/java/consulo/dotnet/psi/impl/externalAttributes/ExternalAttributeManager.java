package consulo.dotnet.psi.impl.externalAttributes;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.component.messagebus.MessageBusConnection;
import consulo.content.base.BinariesOrderRootType;
import consulo.dotnet.externalAttributes.ExternalAttributeHolder;
import consulo.dotnet.externalAttributes.ExternalAttributesRootOrderType;
import consulo.module.content.ProjectFileIndex;
import consulo.module.content.layer.event.ModuleRootEvent;
import consulo.module.content.layer.event.ModuleRootListener;
import consulo.module.content.layer.orderEntry.OrderEntry;
import consulo.project.Project;
import consulo.util.collection.ArrayUtil;
import consulo.util.collection.ContainerUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.archive.ArchiveVfsUtil;
import consulo.virtualFileSystem.event.BulkFileListener;
import consulo.virtualFileSystem.event.VFileEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author VISTALL
 * @since 27.07.2015
 */
@Singleton
@ServiceAPI(ComponentScope.PROJECT)
@ServiceImpl
public class ExternalAttributeManager
{
	@Nonnull
	public static ExternalAttributeManager getInstance(@Nonnull Project project)
	{
		return project.getInstance(ExternalAttributeManager.class);
	}

	private Map<VirtualFile, ExternalAttributeHolder> myCache = ContainerUtil.createConcurrentWeakMap();
	private final Project myProject;

	@Inject
	ExternalAttributeManager(Project project)
	{
		myProject = project;
		MessageBusConnection connect = project.getMessageBus().connect();
		connect.subscribe(BulkFileListener.class, new BulkFileListener()
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

		connect.subscribe(ModuleRootListener.class, new ModuleRootListener()
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

		List<VirtualFile> externalAttributeFiles = new ArrayList<>();
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
			List<ExternalAttributeHolder> list = new ArrayList<>();
			for(VirtualFile externalAttributeFile : externalAttributeFiles)
			{
				list.add(SingleExternalAttributeHolder.load(externalAttributeFile));
			}
			return new CompositeExternalAttributeHolder(list);
		}
	}
}
