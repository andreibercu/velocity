package com.abercu.velo.birds.views;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.abercu.velo.birds.service.SightingsService;

public class SightingFormView extends ViewPart {

    public static final String ID = "com.abercu.velo.birds.views.SightingFormView";

    private final SightingsService sightingsService;
    
    private Text birdIdText, locationText, datetimeText;
    private Label statusLabel;
    
    public SightingFormView() {
    	this.sightingsService = new SightingsService();
    }

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(2, false));

        createLabeledInput(parent, "Bird ID:", birdIdText = new Text(parent, SWT.BORDER));
        createLabeledInput(parent, "Location:", locationText = new Text(parent, SWT.BORDER));
        createLabeledInput(parent, "Datetime (ISO-8601):", datetimeText = new Text(parent, SWT.BORDER));

        Button createButton = new Button(parent, SWT.PUSH);
        createButton.setText("Create Sighting");
        createButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

        statusLabel = new Label(parent, SWT.NONE);
        statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        createButton.addListener(SWT.Selection, e -> createSighting());
    }

    private void createLabeledInput(Composite parent, String labelText, Text inputField) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText);
        inputField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    private void createSighting() {
        String birdIdStr = birdIdText.getText().trim();
        String location = locationText.getText().trim();
        String datetimeStr = datetimeText.getText().trim();

        if (birdIdStr.isEmpty() || location.isEmpty()) {
            statusLabel.setText("Bird Id and Location attributes are required.");
            return;
        }

        try {
            Long birdId = Long.parseLong(birdIdStr);
            Instant datetime = datetimeStr.isEmpty()
            		? Instant.now()
            		: Instant.parse(datetimeStr);

            

            new Thread(() -> {
                boolean success = sightingsService.postSighting(birdId, location, datetime);
                
                Display.getDefault().asyncExec(() -> {
                    if (success) {
                        statusLabel.setText("Sighting created successfully!");
                        birdIdText.setText("");
                        locationText.setText("");
                        datetimeText.setText("");
                    } else {
                        statusLabel.setText("Failed to create sighting.");
                    }
                });
            }).start();

        } catch (NumberFormatException e) {
            statusLabel.setText("Bird ID must be a number.");
        } catch (DateTimeParseException e) {
            statusLabel.setText("Datetime format invalid. Use ISO-8601.");
        }
    }

    @Override
    public void setFocus() {
        birdIdText.setFocus();
    }

}
