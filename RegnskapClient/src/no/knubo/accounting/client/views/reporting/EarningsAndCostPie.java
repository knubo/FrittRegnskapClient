package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.PieChart;
import com.google.gwt.visualization.client.visualizations.PieChart.Options;

public class EarningsAndCostPie extends Composite implements ClickHandler {

    private static EarningsAndCostPie me;
    private final Elements elements;
    private final Constants constants;
    private final I18NAccount messages;
    private int year;
    protected JSONObject earningsData;
    private DockPanel dp;
    private HTML periodeHeader;
    private Image previousImage;
    private Image nextImage;
    protected PieChart pieEarnings;
    private HTML totalEarningsLabel;
    protected JSONObject costData;
    protected PieChart pieCost;
    private HTML totalCostLabel;
    private CheckBox utgiftBox;

    public EarningsAndCostPie(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;
    }

    public void init() {

        if (dp == null) {
            dp = new DockPanel();

            periodeHeader = new HTML();
            previousImage = ImageFactory.previousImage("ShowMembershipView.previousImage");
            previousImage.addClickHandler(this);
            nextImage = ImageFactory.nextImage("ShowMembershipView.nextImage");
            nextImage.addClickHandler(this);

            utgiftBox = new CheckBox(elements.show_expences());
            utgiftBox.addClickHandler(this);
            
            HorizontalPanel hp = new HorizontalPanel();
            hp.add(previousImage);
            hp.add(periodeHeader);
            hp.add(nextImage);
            hp.add(utgiftBox);

            dp.add(hp, DockPanel.NORTH);

            totalEarningsLabel = new HTML();
            totalCostLabel = new HTML();

            dp.add(totalEarningsLabel, DockPanel.NORTH);
            dp.add(totalCostLabel, DockPanel.NORTH);
            initWidget(dp);
        }

        
        if (pieEarnings != null) {
            dp.remove(pieEarnings);
        }
        if (pieCost != null) {
            dp.remove(pieCost);
        }

        if(totalEarningsLabel != null) {
            dp.remove(totalEarningsLabel);
        }
        
        if (totalCostLabel != null) {
            dp.remove(totalCostLabel);
        }

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue parse) {
                JSONObject object = parse.isObject();
                year = Util.getInt(object.get("year"));
                periodeHeader.setText(String.valueOf(year));
                earningsData = object.get("earnings").isObject();
                costData = object.get("cost").isObject();
                setupPies(dp);

            }
        };

        AuthResponder.get(constants, messages, callback, "reports/year_data.php?action=earningsandcost&year=" + year);

    }

    private void setupPies(final DockPanel dp) {
        // Create a callback to be called when the visualization API
        // has been loaded.
        Runnable onLoadCallback = new Runnable() {
            public void run() {

                if (!utgiftBox.getValue()) {
                    // Create a pie chart visualization.
                    pieEarnings = new PieChart(createTable(earningsData, totalEarningsLabel), createOptionsIncome());
                    // pie.addSelectHandler(createSelectHandler(pie));
                    dp.add(totalEarningsLabel, DockPanel.NORTH);
                    dp.add(pieEarnings, DockPanel.NORTH);
                } else {
                    // Create a pie chart visualization.
                    pieCost = new PieChart(createTable(costData, totalCostLabel), createOptionsCost());
                    // pie.addSelectHandler(createSelectHandler(pie));
                    dp.add(totalCostLabel, DockPanel.NORTH);
                    dp.add(pieCost, DockPanel.NORTH);
                }
            }
        };

        // Load the visualization api, passing the onLoadCallback to be called
        // when loading is done.
        VisualizationUtils.loadVisualizationApi(onLoadCallback, PieChart.PACKAGE);
    }

    private Options createOptionsIncome() {
        Options options = Options.create();
        options.setWidth(800);
        options.setHeight(600);
        options.set3D(true);
        options.setTitle(messages.income_year(String.valueOf(year)));
        return options;
    }

    private Options createOptionsCost() {
        Options options = Options.create();
        options.setWidth(800);
        options.setHeight(600);
        options.set3D(true);
        options.setTitle(messages.cost_year(String.valueOf(year)));
        return options;
    }

    private AbstractDataTable createTable(JSONObject postData, HTML label) {
        DataTable data = DataTable.create();
        data.addColumn(ColumnType.STRING, elements.post());
        data.addColumn(ColumnType.NUMBER, elements.amount());

        if (postData == null) {
            return data;
        }

        data.addRows(postData.keySet().size());

        double total = 0;
        int row = 0;
        for (String key : postData.keySet()) {
            JSONObject one = postData.get(key).isObject();
            data.setValue(row, 0, Util.str(one.get("description")));
            double d = Util.getDouble(one.get("value"));
            total += d;
            data.setValue(row, 1, d);

            row++;
        }

        label.setText("Sum: " + Util.money(String.valueOf(total)));

        return data;
    }

    public static Widget getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new EarningsAndCostPie(messages, constants, elements);
        }
        me.init();
        return me;
    }

    public void onClick(ClickEvent event) {
        Widget sender = (Widget) event.getSource();
        if (sender == previousImage) {
            year--;
            init();
        } else if (sender == nextImage) {
            year++;
            init();
        } else {
            init();
        }
    }
}
