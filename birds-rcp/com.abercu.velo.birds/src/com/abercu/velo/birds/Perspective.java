package com.abercu.velo.birds;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		System.out.println("*** Opening Main Perspective");

		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
	}

}
