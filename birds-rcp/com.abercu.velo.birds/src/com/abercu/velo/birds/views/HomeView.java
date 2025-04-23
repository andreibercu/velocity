package com.abercu.velo.birds.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class HomeView extends ViewPart {
	
    public static final String ID = "com.abercu.velo.birds.views.HomeView";

	public HomeView() {}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		Composite buttonContainer = new Composite(parent, SWT.NONE);
		buttonContainer.setLayout(new GridLayout(1, true));
		buttonContainer.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true));

		createStyledButton(buttonContainer, "Birds View", BirdsView.ID);
		createStyledButton(buttonContainer, "Sightings View", SightingsView.ID);
		createStyledButton(buttonContainer, "Add Bird", BirdFormView.ID);
		createStyledButton(buttonContainer, "Add Sighting", SightingFormView.ID);
	}

	@Override
	public void setFocus() {}

	private void createStyledButton(Composite parent, String label, String viewId) {
	    Button button = new Button(parent, SWT.PUSH);
	    button.setText(label);

	    GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
	    gd.widthHint = 200;
	    gd.horizontalIndent = 10;
	    gd.verticalIndent = 5;
	    button.setLayoutData(gd);

	    button.addListener(SWT.Selection, e -> {
	        try {
	            getSite().getPage().showView(viewId);
	        } catch (PartInitException ex) {
	            ex.printStackTrace();
	        }
	    });
	}
}
