package com.abercu.velo.birds.views;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import com.abercu.velo.birds.model.Bird;
import com.abercu.velo.birds.model.Page;
import com.abercu.velo.birds.service.BirdService;

public class BirdsView extends ViewPart {

    public static final String ID = "com.abercu.velo.birds.views.BirdsView";

    private final BirdService birdService;
    
	private TableViewer viewer;
	private int currentPage = 0;
	private int totalPages = 1;
	private final int pageSize = 5;

	private Button prevButton;
	private Button nextButton;
	private Label pageInfoLabel;
    
    public BirdsView() {
    	this.birdService = new BirdService();
    }

    @Override
    public void createPartControl(Composite parent) {      
        parent.setLayout(new GridLayout(1, false));

        Composite topBar = new Composite(parent, SWT.NONE);
        topBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        topBar.setLayout(new GridLayout(2, false)); // button + label
        
        Label loadingLabel = new Label(topBar, SWT.NONE);
        loadingLabel.setText("Loading...");
        loadingLabel.setVisible(false); // hidden by default
        
        Button refreshButton = new Button(topBar, SWT.PUSH);
        refreshButton.setText("Refresh");

        viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        viewer.getTable().setHeaderVisible(true);
        viewer.getTable().setLinesVisible(true);
        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createColumns(viewer);
        viewer.setContentProvider(ArrayContentProvider.getInstance());
        
        refreshButton.addListener(SWT.Selection, e -> refreshBirdList(loadingLabel));

        refreshBirdList(loadingLabel);
        
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

        // Add listeners
        prevButton.addListener(SWT.Selection, e -> {
            if (currentPage > 0) {
                currentPage--;
                refreshBirdList(loadingLabel);
            }
        });

        nextButton.addListener(SWT.Selection, e -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                refreshBirdList(loadingLabel);
            }
        });
    }
    
    private void refreshBirdList(Label loadingLabel) {
        loadingLabel.setVisible(true);
        loadingLabel.getParent().layout(); // trigger UI re-render

        new Thread(() -> {
            Page<Bird> page = birdService.fetchBirdsFromApi(currentPage, pageSize);
            
            Display.getDefault().asyncExec(() -> {
                viewer.setInput(page.content);
                currentPage = page.number;
                totalPages = page.totalPages;
                updatePaginationControls();

                loadingLabel.setVisible(false);
                loadingLabel.getParent().layout(); // trigger UI re-render
            });
        }).start();
    }

    private void updatePaginationControls() {
        pageInfoLabel.setText("Page " + (currentPage + 1) + " / " + totalPages);
        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(currentPage < totalPages - 1);
        pageInfoLabel.getParent().layout();
    }

    private void createColumns(TableViewer viewer) {
        String[] titles = { "Id", "Name", "Color", "Weight (kg)", "Height (cm)" };
        int[] bounds = { 100, 150, 100, 100, 100 };
        
        TableViewerColumn col = createTableViewerColumn(viewer, titles[0], bounds[0], 0);
        col.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element) {
                return ((Bird) element).getId().toString();
            }
        });

        col = createTableViewerColumn(viewer, titles[1], bounds[1], 1);
        col.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element) {
                return ((Bird) element).getName();
            }
        });

        col = createTableViewerColumn(viewer, titles[2], bounds[2], 2);
        col.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element) {
                return ((Bird) element).getColor();
            }
        });

        col = createTableViewerColumn(viewer, titles[3], bounds[3], 3);
        col.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element) {
            	Bird bird = (Bird) element;
                return bird.getWeight() == null ? "" : String.valueOf(bird.getWeight());
            }
        });

        col = createTableViewerColumn(viewer, titles[4], bounds[4], 4);
        col.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element) {
            	Bird bird = (Bird) element;
                return bird.getHeight() == null ? "" : String.valueOf(bird.getHeight());
            }
        });

        Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
    }

    private TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int bound, final int colNumber) {
        TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        return viewerColumn;
    }

	@Override
	public void setFocus() {
        viewer.getControl().setFocus();
	}

}
