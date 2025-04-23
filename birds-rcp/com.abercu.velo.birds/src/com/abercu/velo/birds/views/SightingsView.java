package com.abercu.velo.birds.views;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.abercu.velo.birds.model.Page;
import com.abercu.velo.birds.model.Sighting;
import com.abercu.velo.birds.service.SightingLabelProvider;
import com.abercu.velo.birds.service.SightingsService;

public class SightingsView extends ViewPart {

    public static final String ID = "com.abercu.velo.birds.views.SightingsView";
    
    private final SightingsService sightingsService;
    
    private TableViewer viewer;
    private Text searchText;
    
    private int currentPage = 0;
    private int totalPages = 1;
    private final int pageSize = 5;

    private Button prevButton;
    private Button nextButton;
    private Label pageInfoLabel;

	public SightingsView() {
		this.sightingsService = new SightingsService();
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

        // SEARCH BAR
        Composite searchBar = new Composite(parent, SWT.NONE);
        searchBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        searchBar.setLayout(new GridLayout(3, false));

        searchText = new Text(searchBar, SWT.BORDER | SWT.SEARCH);
        searchText.setMessage("Fuzzy search sightings using Elasticsearch - Use comma separated values ...");
        searchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Button searchButton = new Button(searchBar, SWT.PUSH);
        searchButton.setText("Search");

        Button clearButton = new Button(searchBar, SWT.PUSH);
        clearButton.setText("Clear");
        
        searchButton.addListener(SWT.Selection, e -> {
        	refreshSightingList();
        });

        clearButton.addListener(SWT.Selection, e -> {
            searchText.setText("");
            currentPage = 0;
            refreshSightingList();
        });
        
        // listener for Enter
        searchText.addListener(SWT.DefaultSelection, e -> {
        	refreshSightingList();
        });

        // TABLE
        viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        viewer.getTable().setHeaderVisible(true);
        viewer.getTable().setLinesVisible(true);
        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createColumns();
        viewer.setContentProvider(ArrayContentProvider.getInstance());

        refreshSightingList();
        
        // PAGINATION
        Composite paginationBar = new Composite(parent, SWT.NONE);
        paginationBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        paginationBar.setLayout(new GridLayout(3, false));

        prevButton = new Button(paginationBar, SWT.PUSH);
        prevButton.setText("Previous");
        prevButton.setEnabled(false);

        pageInfoLabel = new Label(paginationBar, SWT.NONE);
        pageInfoLabel.setText("Page 1 / 1");

        nextButton = new Button(paginationBar, SWT.PUSH);
        nextButton.setText("Next");
        nextButton.setEnabled(false);

        prevButton.addListener(SWT.Selection, e -> {
            if (currentPage > 0) {
                currentPage--;
                refreshSightingList();
            }
        });

        nextButton.addListener(SWT.Selection, e -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                refreshSightingList();
            }
        });
	}

	private void createColumns() {
        String[] titles = { "ID", "Bird ID", "Bird Name", "Bird Color", "Location", "Datetime"};
        int[] widths = { 100, 100, 150, 150, 150, 150 };

        for (int i = 0; i < titles.length; i++) {
            TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
            TableColumn tableColumn = column.getColumn();
            tableColumn.setText(titles[i]);
            tableColumn.setWidth(widths[i]);
            tableColumn.setResizable(true);
        }

        viewer.setLabelProvider(new SightingLabelProvider());
    }

	private void refreshSightingList() {
        String term = searchText.getText().trim();

        new Thread(() -> {
            Page<Sighting> page = sightingsService.searchSightings(term, currentPage, pageSize);
            Display.getDefault().asyncExec(() -> {
                viewer.setInput(page.content);
                
                currentPage = page.number;
                totalPages = page.totalPages;
                updatePaginationControls();
            });
        }).start();
    }
	
	private void updatePaginationControls() {
        pageInfoLabel.setText("Page " + (currentPage + 1) + " / " + totalPages);
        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(currentPage < totalPages - 1);
        pageInfoLabel.getParent().layout();
    }

	@Override
	public void setFocus() {}

}
