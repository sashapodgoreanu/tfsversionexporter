package com.podgoreanu.tfsversionexporter.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

import com.microsoft.tfs.client.common.ui.controls.generic.SizeConstrainedComposite;
import com.microsoft.tfs.client.common.ui.framework.wizard.ExtendedWizardPage;

/**
 * This class will construct the GUI wizard page were user should pick from what
 * project to export the changes.
 * 
 * @author a.podgoreanu
 *
 */
public class ProjectPickerWizardPage extends ExtendedWizardPage {

	public static final String PAGE_NAME = ProjectPickerWizardPage.class.getSimpleName();
	private IProject project;

	public ProjectPickerWizardPage() {
		super(PAGE_NAME, "Select Project", "Choose project name from wich to export modified files");
		setPageComplete(false);
	}

	@Override
	protected void doCreateControl(Composite parent, IDialogSettings arg1) {
		final SizeConstrainedComposite container = new SizeConstrainedComposite(parent, SWT.NONE);
		container.setDefaultSize(SWT.DEFAULT, SWT.DEFAULT);
		setControl(container);

		final GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = getHorizontalMargin();
		layout.marginHeight = getVerticalMargin();
		layout.horizontalSpacing = getHorizontalSpacing();
		layout.verticalSpacing = getVerticalSpacing() - 2;
		container.setLayout(layout);

		Group group = new Group(container, SWT.SHADOW_IN);
		group.setText("Select TFS Project");
		group.setLayout(new RowLayout(SWT.VERTICAL));

		// add projects to dialog
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				Button button = (Button) event.widget;
				ProjectPickerWizardPage.this.setProject((IProject) button.getData("project"));
				ProjectPickerWizardPage.this.setPageComplete(true);
			};
		};
		for (IProject project : projects) {

			Button button = new Button(group, SWT.RADIO);
			button.setData("project", project);
			button.setText(project.getName());
			button.addListener(SWT.Selection, listener);
		}
		setControl(container);

	}

	@Override
	protected boolean onPageFinished() {
		return this.isPageComplete();
	}

	@Override
	protected void onMovingToPreviousPage() {
		super.onMovingToPreviousPage();
		this.setPageComplete(false);
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
