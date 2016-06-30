package com.podgoreanu.tfsversionexporter.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;
import com.microsoft.tfs.client.common.ui.framework.wizard.ExtendedWizard;
import com.podgoreanu.tfsversionexporter.TFSVersionExporterPlugin;
import com.podgoreanu.tfsversionexporter.service.ChangesExporter;
import com.podgoreanu.tfsversionexporter.service.FyleSystemChangesExporter;
import com.podgoreanu.tfsversionexporter.service.ZipChangesExporter;

/**
 * This class contains ExtendedWizard pages.
 * 
 * @author a.podgoreanu
 *
 */
public class TFSExportWizard extends ExtendedWizard implements IWorkbenchWizard {

	private final static ImageHelper imageHelper = new ImageHelper(TFSVersionExporterPlugin.PLUGIN_ID);
	SelectFilesWizardPage selectFilesWizardPage;
	ProjectPickerWizardPage projectPicker;
	FolderPickExportWizardPage folderPickExportWizard;

	public TFSExportWizard() {
		// TODO
		super("", imageHelper.getImageDescriptor("images/wizard/pageheader.png"));
	}

	@Override
	// TODO
	public String getWindowTitle() {
		return "Export My Data";
	}

	@Override
	public void addPages() {
		this.addPage(projectPicker);
		this.addPage(selectFilesWizardPage);
		this.addPage(folderPickExportWizard);

	}

	@Override
	public IWizardPage getNextPage(final IWizardPage page) {

		if (page instanceof SelectFilesWizardPage && selectFilesWizardPage.isPageComplete()) {
			return getPage(FolderPickExportWizardPage.PAGE_NAME);
		} else if (page instanceof ProjectPickerWizardPage && projectPicker.isPageComplete()) {
			return getPage(SelectFilesWizardPage.PAGE_NAME);
		} else {
			return getPage(ProjectPickerWizardPage.PAGE_NAME);
		}

	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		selectFilesWizardPage = new SelectFilesWizardPage("");
		projectPicker = new ProjectPickerWizardPage();
		folderPickExportWizard = new FolderPickExportWizardPage();

	}

	@Override
	public boolean enableNext(final IWizardPage page) {
		return page instanceof ProjectPickerWizardPage && page.isPageComplete()
				|| page instanceof SelectFilesWizardPage && page.isPageComplete();
	}

	@Override
	public boolean enableFinish(final IWizardPage currentPage) {
		return currentPage instanceof FolderPickExportWizardPage && currentPage.isPageComplete();
	}

	@Override
	public boolean doPerformFinish() {
		ArrayList<IFile> filesToExport = selectFilesWizardPage.getCheckedFiles();
		if (this.folderPickExportWizard.generateFilesystem()) {
			doExport(filesToExport, new FyleSystemChangesExporter(this.folderPickExportWizard.getOutputDir()
					+ File.separator + this.projectPicker.getProject().getName()));
		}
		if (this.folderPickExportWizard.generateZipfile()) {
			doExport(filesToExport, new ZipChangesExporter(this.folderPickExportWizard.getOutputDir() + File.separator
					+ this.projectPicker.getProject().getName()));
		}
		return true;
	}

	private void doExport(List<IFile> files, ChangesExporter exporter) {
		exporter.exportFiles(files);
	}

	@Override
	public boolean isHelpAvailable() {
		return false;
	}
}
