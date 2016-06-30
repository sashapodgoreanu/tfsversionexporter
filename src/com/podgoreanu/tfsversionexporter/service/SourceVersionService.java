package com.podgoreanu.tfsversionexporter.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

import com.microsoft.tfs.client.common.framework.resources.LocationUnavailablePolicy;
import com.microsoft.tfs.client.common.framework.resources.Resources;
import com.microsoft.tfs.client.common.repository.TFSRepository;
import com.microsoft.tfs.client.eclipse.TFSRepositoryProvider;
import com.microsoft.tfs.client.eclipse.util.TeamUtils;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.ChangeType;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingChange;
import com.podgoreanu.tfsversionexporter.TFSVersionExporterPlugin;
import com.podgoreanu.tfsversionexporter.bo.VersionedResource;
import com.podgoreanu.tfsversionexporter.bo.VersionedResource.ChangeVType;

/**
 * This class is used to search in the project all the local changes before user
 * commits the changes to a TFS server
 * 
 * @author a.podgoreanu
 *
 */
public class SourceVersionService {
	private IProject project;
	private Set<VersionedResource> files;

	public SourceVersionService(IProject project) {
		this.setProject(project);
		this.files = new HashSet<VersionedResource>();
		doCreatePendingSet();
	}

	private void doCreatePendingSet() {
		IResourceVisitor visitor = new IResourceVisitor() {
			public boolean visit(IResource resource) throws CoreException {
				final String resourcePath = Resources.getLocation(resource, LocationUnavailablePolicy.IGNORE_RESOURCE);
				// try to get the repository for this resource
				try {
					final TFSRepositoryProvider repositoryProvider = (TFSRepositoryProvider) TeamUtils
							.getRepositoryProvider(resource);
					final TFSRepository repository = repositoryProvider.getRepository();
					decorateFromPendingChanges(resource, repository, resourcePath);

				} catch (Exception e) {

				}
				return true;
			}
		};
		// Visit project for changes
		try {
			this.project.accept(visitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void decorateFromPendingChanges(final IResource resource, final TFSRepository repository,
			final String resourcePath) {
		if (resourcePath == null) {
			return;
		} else if (resource.getType() == IResource.FILE) {
			this.decorateFromFilePendingChanges(resource, repository, resourcePath);
		} else if (true) {
			this.decorateFromFolderPendingChanges(resource, repository, resourcePath);
		}
	}

	private void decorateFromFilePendingChanges(final IResource resource, final TFSRepository repository,
			final String resourcePath) {
		final PendingChange pendingChange = repository.getPendingChangeCache()
				.getPendingChangeByLocalPath(resourcePath);

		// no pending changes, don't alter any decorations
		if (pendingChange == null || pendingChange.getChangeType() == null) {
			return;
		}

		final ChangeType pendingChangeType = pendingChange.getChangeType();
		VersionedResource versionedResource = null;
		// handle adds
		if (pendingChangeType.contains(ChangeType.ADD)) {
			versionedResource = new VersionedResource(resource, ChangeVType.ADD);
			files.add(versionedResource);
			TFSVersionExporterPlugin.log("add");
		}
		// edits
		else if (pendingChangeType.contains(ChangeType.EDIT)) {
			versionedResource = new VersionedResource(resource, ChangeVType.EDIT);
			files.add(versionedResource);
			TFSVersionExporterPlugin.log("edit");
		}
	}

	private void decorateFromFolderPendingChanges(final IResource resource, final TFSRepository repository,
			final String resourcePath) {
		if (repository.getPendingChangeCache().hasPendingChangesByLocalPathRecursive(resourcePath) == false) {
			return;
		}
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public Set<VersionedResource> getFiles() {
		return files;
	}

	public VersionedResource[] getChangedFiles() {
		ArrayList<VersionedResource> changedFiles = new ArrayList<VersionedResource>();
		for (VersionedResource file : files) {
			if (file.getChangeType().equals(ChangeVType.EDIT)) {
				changedFiles.add(file);
			}
		}
		VersionedResource[] vr = new VersionedResource[changedFiles.size()];
		for (int i = 0; i < changedFiles.size(); i++) {
			vr[i] = changedFiles.get(i);
		}
		return vr;
	}

	public void setFiles(Set<VersionedResource> files) {
		this.files = files;
	}

}
