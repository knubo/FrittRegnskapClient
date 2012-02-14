package no.knubo.accounting.client.views.budget;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.LineChart.Options;

public class BudgetGraphView extends DialogBox implements Runnable, ClickHandler, ChangeHandler {

    private static BudgetGraphView me;

    DockPanel dp;

    private LineChart chart;

    private FlowPanel chartHolder;

    private NamedButton closeButton;

    private BudgetDrawDelegate budgetDrawDelegate;

    private ListBox sizeListbox;

    int height = 600;
    int width = 800;
    
    private BudgetGraphView(Elements elements) {
        dp = new DockPanel();

        setText(elements.budget_chart());
        setModal(false);

        chartHolder = new FlowPanel();

        FlowPanel buttonPanel = new FlowPanel();
        closeButton = new NamedButton("close_button", elements.close());
        closeButton.addClickHandler(this);
        buttonPanel.add(closeButton);

        sizeListbox = new ListBox();
        sizeListbox.addItem("640x400");
        sizeListbox.addItem("800x600");
        sizeListbox.addItem("1024x768");
        sizeListbox.addItem("1280x960");
        sizeListbox.addChangeHandler(this);
        sizeListbox.setSelectedIndex(1);
        buttonPanel.add(sizeListbox);
        
        dp.add(buttonPanel, DockPanel.NORTH);
        dp.add(chartHolder, DockPanel.NORTH);

        setWidget(dp);
    }

    public static BudgetGraphView getInstance(Elements elements) {
        if (me == null) {
            me = new BudgetGraphView(elements);
        }

        return me;
    }

    public void init(BudgetDrawDelegate budgetDrawDelegate) {
        this.budgetDrawDelegate = budgetDrawDelegate;
        VisualizationUtils.loadVisualizationApi(this, LineChart.PACKAGE);
    }

    @Override
    public void run() {
        if (chart != null) {
            chartHolder.remove(chart);
        }
        chart = new LineChart();
        chartHolder.add(chart);

        DataTable data = DataTable.create();

        try {
            budgetDrawDelegate.fillDatatableYearBased(data);
        } catch (Exception e) {
            Util.log(e.toString());
        }
        Options opts = Options.create();
        opts.setWidth(width);
        opts.setHeight(height);
        chart.draw(data, opts);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == closeButton) {
            hide();
        }
    }

    @Override
    public void onChange(ChangeEvent event) {
        String selectedText = Util.getSelectedText(sizeListbox);
        int xIndex = selectedText.indexOf('x');
        width = Integer.parseInt(selectedText.substring(0, xIndex));
        height = Integer.parseInt(selectedText.substring(xIndex+1));
        init(budgetDrawDelegate);
    }
}
