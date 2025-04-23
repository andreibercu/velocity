package com.abercu.velo.birds.service;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.abercu.velo.birds.model.Sighting;

public class SightingLabelProvider extends LabelProvider implements ITableLabelProvider {
    
	@Override
    public String getColumnText(Object element, int columnIndex) {
        Sighting s = (Sighting) element;
        switch (columnIndex) {
            case 0: return String.valueOf(s.getId());
            case 1: return String.valueOf(s.getBirdId());
            case 2: return s.getBirdName();
            case 3: return s.getBirdColor();
            case 4: return s.getLocation();
            case 5: return s.getDatetime() != null ? s.getDatetime().toString() : "";
            default: return "";
        }
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }
}
