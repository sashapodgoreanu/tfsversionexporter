package com.podgoreanu.tfsversionexporter.wizard;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

import com.microsoft.tfs.client.common.ui.framework.wizard.ExtendedWizardPage;

/**
 * This class will construct the GUI wizard page were user should pick were to
 * export the changes.
 * 
 * @author a.podgoreanu
 *
 */
public class FolderPickExportWizardPage extends ExtendedWizardPage {
	public static final String PAGE_NAME = FolderPickExportWizardPage.class.getSimpleName();

	Composite composite;
	private Text outputDirText;
	private Button windowsChk;
	private Button filesystemChk;

	public FolderPickExportWizardPage() {
		super(PAGE_NAME, "File System", "Please enter a destination directory.");
		setPageComplete(false);
	}

	@Override
	protected void doCreateControl(Composite parent, IDialogSettings dialogSettings) {
		this.composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout(3, false);
		this.composite.setLayout((Layout) layout);
		Label pickerLabel = new Label(this.composite, 0);
		pickerLabel.setText("Output directory:");
		this.outputDirText = new Text(this.composite, 4);
		this.outputDirText.setEditable(true);
		GridData data = new GridData(4, 16777216, true, false);
		this.outputDirText.setLayoutData((Object) data);

		this.outputDirText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				FolderPickExportWizardPage.this.getContainer().updateButtons();
			}
		});
		Button pickFileButton = new Button(this.composite, 0);
		pickFileButton.setText("Browse...");
		pickFileButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(FolderPickExportWizardPage.this.composite.getShell());
				String result = dlg.open();
				if (result != null) {
					FolderPickExportWizardPage.this.outputDirText.setText(result);
					FolderPickExportWizardPage.this.getContainer().updateButtons();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				this.widgetSelected(e);
			}
		});
		Label outputformatLabel = new Label(this.composite, 0);
		outputformatLabel.setText("Output Format:");
		Composite platformOptions = new Composite(this.composite, 0);
		GridData platOptData = new GridData();
		platOptData.horizontalSpan = 2;
		platformOptions.setLayoutData((Object) platOptData);
		GridLayout platOptLayout = new GridLayout(1, false);
		platformOptions.setLayout((Layout) platOptLayout);
		this.windowsChk = new Button(platformOptions, 32);
		this.windowsChk.setText("Zip File - not implemented yet");
		this.windowsChk.addSelectionListener((SelectionListener) new ButtonUpdater(this, null));
		this.filesystemChk = new Button(platformOptions, 32);
		this.filesystemChk.setText("Uncompressed");
		this.filesystemChk.addSelectionListener((SelectionListener) new ButtonUpdater(this, null));
		data = new GridData(3, 16777216, true, false);
		data.horizontalSpan = 3;
		platformOptions.pack();
		this.setControl((Control) this.composite);

	}

	public boolean isPageComplete() {
		File f = new File(this.outputDirText.getText());
		if (f.exists() && (this.windowsChk.getSelection() || this.filesystemChk.getSelection())) {
			return true;
		}
		return false;
	}

	private class ButtonUpdater implements SelectionListener {
		final FolderPickExportWizardPage this$0;

		private ButtonUpdater(FolderPickExportWizardPage folderPickExportWizardPage) {
			this.this$0 = folderPickExportWizardPage;
		}

		public void widgetSelected(SelectionEvent e) {
			this.this$0.getContainer().updateButtons();
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			this.widgetSelected(e);
		}

		public ButtonUpdater(FolderPickExportWizardPage folderPickExportWizardPage, ButtonUpdater buttonUpdater) {
			this(folderPickExportWizardPage);
		}
	}

	@Override
	protected void onMovingToPreviousPage() {
		super.onMovingToPreviousPage();
		this.setPageComplete(false);
		this.setControl(null);
	}

	public String getOutputDir() {
		return this.outputDirText.getText();
	}

	public boolean generateZipfile() {
		return this.windowsChk.getSelection();
	}

	public boolean generateFilesystem() {
		return this.filesystemChk.getSelection();
	}

}
