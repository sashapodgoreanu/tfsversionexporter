package com.podgoreanu.tfsversionexporter.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * Exports to Zip file.
 * 
 * @author a.podgoreanu
 *
 */
public class ZipChangesExporter extends ChangesExporter {

	private ZipOutputStream zos;

	public ZipChangesExporter(String path) {
		super(path);
		File zipfile = new File(path + ".zip");
		// create zip file
		try {
			this.zos = new ZipOutputStream(new FileOutputStream(zipfile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void exportFiles(List<IFile> files) {
		InputStream in = null;
		try {
			for (IFile file : files) {
				in = file.getContents();
				exportFile(in, new File(file.getProjectRelativePath().toString()));
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					zos.closeEntry();
					in.close();
					this.zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void exportFile(InputStream in, File destinationFile) throws IOException {
		this.zos.putNextEntry(new ZipEntry(destinationFile.getPath()));
		this.addFile(zos, in);
	}

}
