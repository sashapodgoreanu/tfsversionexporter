package com.podgoreanu.tfsversionexporter.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * Exports to File System.
 * 
 * @author a.podgoreanu
 *
 */
public class FyleSystemChangesExporter extends ChangesExporter {

	public FyleSystemChangesExporter(String path) {
		super(path);
	}

	public void exportFiles(List<IFile> files) {
		for (IFile file : files) {
			InputStream in = null;

			try {
				in = file.getContents();
			} catch (CoreException e) {
				e.printStackTrace();
			}
			exportFiles(in, new File(this.getPath() + File.separator + file.getProjectRelativePath()));
		}
	}

	private void exportFiles(InputStream in, File destinationFile) {
		File parent = destinationFile.getParentFile();
		parent.mkdirs();

		OutputStream outputStream = null;
		try {
			destinationFile.createNewFile();
			outputStream = new FileOutputStream(destinationFile);
			this.addFile(outputStream, in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}
}
