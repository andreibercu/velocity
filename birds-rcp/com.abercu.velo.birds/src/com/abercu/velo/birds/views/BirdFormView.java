package com.abercu.velo.birds.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.abercu.velo.birds.service.BirdService;

public class BirdFormView extends ViewPart {

    public static final String ID = "com.abercu.velo.birds.views.BirdFormView";

    private final BirdService birdService;

    private Text nameText, colorText, weightText, heightText;
    private Label statusLabel;
    
	public BirdFormView() {
		this.birdService = new BirdService();
	}

	@Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(2, false));

        nameText = createLabeledInput(parent, "Name:");
        colorText = createLabeledInput(parent, "Color:");
        weightText = createLabeledInput(parent, "Weight (kg):");
        heightText = createLabeledInput(parent, "Height (cm):");

        Button createButton = new Button(parent, SWT.PUSH);
        createButton.setText("Create Bird");
        createButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

        statusLabel = new Label(parent, SWT.NONE);
        statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        createButton.addListener(SWT.Selection, e -> createBird());
    }

    private Text createLabeledInput(Composite parent, String labelText) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText);
        
        Text input = new Text(parent, SWT.BORDER);
        input.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        return input;
    }
    
    private void createBird() {
    	String name = nameText.getText().trim();
        String color = colorText.getText().trim();
        String weight = weightText.getText().trim();
        String height = heightText.getText().trim();

        if (name.isEmpty() || color.isEmpty()) {
            statusLabel.setText("Name and Color are required.");
            return;
        }
        
        try {
            Double weightVal = weight.isEmpty() ? null : Double.parseDouble(weight);
            Double heightVal = height.isEmpty() ? null : Double.parseDouble(height);

            new Thread(() -> {
                boolean success = birdService.postBird(name, color, weightVal, heightVal);
                
                Display.getDefault().asyncExec(() -> {
                    if (success) {
                        statusLabel.setText("Bird created successfully!");
                        nameText.setText(""); colorText.setText("");
                        weightText.setText(""); heightText.setText("");
                    } else {
                        statusLabel.setText("Failed creating bird.");
                    }
                });
            }).start();

        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid number format for weight or height.");
        }
    }

    @Override
    public void setFocus() {
        nameText.setFocus();
    }
}
