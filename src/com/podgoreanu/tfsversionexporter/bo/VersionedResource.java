package com.podgoreanu.tfsversionexporter.bo;

import org.eclipse.core.resources.IResource;

/**
 * This class contains a IResorce and its change type
 * 
 * @author a.podgoreanu
 *
 */
public class VersionedResource {
	private IResource resource;
	private ChangeVType changeType;

	public enum ChangeVType {
		ADD, EDIT, LOCK
	};

	public VersionedResource(IResource resource, ChangeVType changeType) {
		super();
		this.resource = resource;
		this.changeType = changeType;
	}

	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public ChangeVType getChangeType() {
		return changeType;
	}

	public void setChangeType(ChangeVType changeType) {
		this.changeType = changeType;
	}

}
