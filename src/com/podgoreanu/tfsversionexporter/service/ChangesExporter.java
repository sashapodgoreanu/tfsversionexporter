package com.podgoreanu.tfsversionexporter.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * This abstract class to be implemented with its methods. Export files.
 * 
 * @author a.podgoreanu
 *
 */
public abstract class ChangesExporter {

	private String path;

	public ChangesExporter(String path) {
		this.setPath(path);
	}

	public abstract void exportFiles(List<IFile> files);

	protected void addFile(OutputStream os, InputStream in) throws FileNotFoundException, IOException {
		int bytesRead;
		byte[] buf = new byte[4096];
		while ((bytesRead = in.read(buf)) > 0) {
			os.write(buf, 0, bytesRead);
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
