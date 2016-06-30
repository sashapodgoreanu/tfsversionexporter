package com.podgoreanu.tfsversionexporter.wizard;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.microsoft.tfs.client.common.ui.controls.generic.SizeConstrainedComposite;
import com.microsoft.tfs.client.common.ui.framework.helper.GenericElementsContentProvider;
import com.microsoft.tfs.client.common.ui.framework.layout.GridDataBuilder;
import com.microsoft.tfs.client.common.ui.framework.sizing.ControlSize;
import com.microsoft.tfs.client.common.ui.framework.wizard.ExtendedWizardPage;
import com.podgoreanu.tfsversionexporter.bo.VersionedResource;
import com.podgoreanu.tfsversionexporter.service.SourceVersionService;

/**
 * This class will construct the GUI wizard page were user should pick from a
 * table the files he wants to export.
 * 
 * @author a.podgoreanu
 *
 */
public class SelectFilesWizardPage extends ExtendedWizardPage {

	private CheckboxTableViewer tableViewer;

	public static final String PAGE_NAME = SelectFilesWizardPage.class.getSimpleName(); // $NON-NLS-1$

	public SelectFilesWizardPage(String pageName) {
		super(PAGE_NAME, "Versioned Files", "Please select files to export");
		this.setPageComplete(false);
	}

	@Override
	public void doCreateControl(Composite parent, IDialogSettings arg1) {

		// this.container = new Composite(parent, SWT.NONE);

		final SizeConstrainedComposite container = new SizeConstrainedComposite(parent, SWT.NONE);
		container.setDefaultSize(SWT.DEFAULT, SWT.DEFAULT);
		setControl(container);

		final GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = getHorizontalMargin();
		layout.marginHeight = getVerticalMargin();
		layout.horizontalSpacing = getHorizontalSpacing();
		layout.verticalSpacing = getVerticalSpacing();
		container.setLayout(layout);

		setControl(container);

		Table table = new Table(container,
				SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		final TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);

		GridDataBuilder.newInstance().hFill().hHint(30).hGrab().hSpan(2).applyTo(table);

		ProjectPickerWizardPage ppwp = (ProjectPickerWizardPage) this.getPreviousPage();
		SourceVersionService sourceService = new SourceVersionService(ppwp.getProject());

		tableLayout.addColumnData(new ColumnWeightData(40, 20, true));
		final TableColumn nameTableColumn = new TableColumn(table, SWT.NONE);
		nameTableColumn.setText("Name");

		tableLayout.addColumnData(new ColumnWeightData(30, 60, true));
		final TableColumn changeTableColumn = new TableColumn(table, SWT.NONE);
		changeTableColumn.setText("Change Type");

		tableLayout.addColumnData(new ColumnWeightData(60, 60, true));
		final TableColumn folderTableColumn = new TableColumn(table, SWT.NONE);
		folderTableColumn.setText("Folder");

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		this.tableViewer = new CheckboxTableViewer(table);
		this.tableViewer.setContentProvider(new GenericElementsContentProvider());
		this.tableViewer.setLabelProvider(new AddFilesDialogLabelProvider());
		this.tableViewer.setSorter(new ViewerSorter());
		this.tableViewer.setInput(sourceService.getFiles());
		tableViewer.setCheckedElements(sourceService.getChangedFiles());

		this.setPageComplete(true);

		this.tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				SelectFilesWizardPage.this.toggleFinishState();
			}
		});

		ControlSize.setCharSizeHints(table, 25, 25);
	}

	private void toggleFinishState() {
		final Object[] checkedElements = this.tableViewer.getCheckedElements();
		if (checkedElements != null && checkedElements.length > 0) {
			this.setPageComplete(true);
		} else {
			this.setPageComplete(false);
		}
	}

	@Override
	protected void onMovingToPreviousPage() {
		super.onMovingToPreviousPage();
		this.setPageComplete(false);
		this.setControl(null);
	}

	public boolean isPageComplete() {
		if (this.tableViewer == null) {
			return false;
		} else {
			final Object[] checkedElements = this.tableViewer.getCheckedElements();
			if (checkedElements != null && checkedElements.length > 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	public ArrayList<IFile> getCheckedFiles() {
		ArrayList<IFile> checkedFiles = new ArrayList<IFile>();
		final Object[] checkedElements = this.tableViewer.getCheckedElements();
		for (Object o : checkedElements) {
			VersionedResource vr = (VersionedResource) o;
			IFile file = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(new Path(vr.getResource().getFullPath().toString()));
			checkedFiles.add(file);

		}
		return checkedFiles;
	}

	private class AddFilesDialogLabelProvider extends LabelProvider implements ITableLabelProvider {
		public AddFilesDialogLabelProvider() {
		}

		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		public String getColumnText(final Object element, final int columnIndex) {
			final VersionedResource vfile = (VersionedResource) element;
			if (columnIndex == 0) // name
			{
				return vfile.getResource().getName();
			} else if (columnIndex == 1) // edit
			{
				return vfile.getChangeType().toString().toLowerCase();
			} else if (columnIndex == 2) // folder
			{
				return vfile.getResource().getParent().getFullPath().toString();
			}
			return null;
		}

		// this method supports sorting
		@Override
		public String getText(final Object element) {
			return ((VersionedResource) element).getResource().getParent().getFullPath().toString();
		}
	}
}
