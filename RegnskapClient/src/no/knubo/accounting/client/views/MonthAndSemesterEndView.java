package no.knubo.accounting.client.views;

import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.ServerResponseWithValidation;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.views.modules.DeprecationRenderer;
import no.knubo.accounting.client.views.modules.RegisterStandards;
import no.knubo.accounting.client.views.modules.RegisterStandardsLoaded;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class MonthAndSemesterEndView extends Composite implements ClickHandler, RegisterStandardsLoaded {

    private static MonthAndSemesterEndView me;

    private final Constants constants;

    private final I18NAccount messages;

    private FlexTable table;

    private HTML dateHeader;

    private final ViewCallback callback;

    private final Elements elements;

    private String endType;

    private NamedButton endButton;

    private HTML header;

    private DeprecationRenderer deprecationRenderer;

    private AccountTable deprecationTable;

    private Label errorLabel;

    private RegisterStandards registerStandards;

    private boolean unhandledKids;

    private Label endInfo;

    public static MonthAndSemesterEndView getInstance(Constants constants, I18NAccount messages, ViewCallback callback,
            Elements elements) {
        if (me == null) {
            me = new MonthAndSemesterEndView(constants, messages, callback, elements);
        }
        return me;
    }

    private MonthAndSemesterEndView(Constants constants, I18NAccount messages, ViewCallback callback, Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.callback = callback;
        this.elements = elements;

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, elements.post());
        table.setHTML(0, 1, elements.amount());
        table.getRowFormatter().setStyleName(0, "header");

        dateHeader = new HTML();
        header = new HTML(elements.end_month_explain());
        endButton = new NamedButton("MonthEndView.endButton", elements.end_month());
        endButton.addClickHandler(this);

        endInfo = new Label();
        
        DockPanel dp = new DockPanel();
        dp.add(dateHeader, DockPanel.NORTH);
        dp.add(header, DockPanel.NORTH);
        dp.add(table, DockPanel.NORTH);

        deprecationRenderer = new DeprecationRenderer();
        deprecationTable = deprecationRenderer.getTable();
        dp.add(deprecationTable, DockPanel.NORTH);
        dp.add(endInfo, DockPanel.NORTH);
        dp.add(endButton, DockPanel.NORTH);
        errorLabel = new Label();
        dp.add(errorLabel, DockPanel.NORTH);

        initWidget(dp);
    }

    public void initEndMonth() {
        unhandledKids = false;

        registerStandards = new RegisterStandards(constants, messages, elements, callback);
        registerStandards.fetchInitalData(false, this);

        endType = "endmonth";
        header.setHTML(elements.end_month_explain());
        endButton.setText(elements.end_month());
        endButton.setId("MonthEndView.endButton");
        endInfo.setText("");
        fetchAndDisplayTransferAmounts();
    }

    public void initEndSemester() {
        registerStandards = new RegisterStandards(constants, messages, elements, callback);
        registerStandards.fetchInitalData(false, this);

        endButton.setText(elements.end_semester());
        endButton.setId("SemesterEndView.endButton");
        endType = "endsemester";
        header.setHTML(elements.end_semester_explain());

        fetchAndDisplayTransferAmounts();
    }

    private void fetchAndDisplayTransferAmounts() {

        while (deprecationTable.getRowCount() > 2) {
            deprecationTable.removeRow(2);
        }

        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }

        
        ServerResponse rh = new ServerResponse() {
            @Override
            public void serverResponse(JSONValue jsonValue) {
                PosttypeCache posttypeCache = PosttypeCache.getInstance(constants, messages);

                JSONObject root = jsonValue.isObject();

                JSONObject kids = root.get("kids").isObject();
                if(Util.getInt(kids.get("kids")) > 0) {
                    endInfo.setText(messages.kid_unhandled());
                    endButton.setEnabled(false);
                    unhandledKids = true;
                }

                
                String year = Util.str(root.get("year"));
                int month = Util.getInt(root.get("month"));

                dateHeader.setHTML("<h2>" + Util.monthString(elements, month) + " " + year + "</h2>");

                JSONValue postsValue = root.get("posts");
                JSONObject object = postsValue.isObject();

                int row = 1;
                for (String post : object.keySet()) {

                    table.setHTML(row, 0, post + " -  " + posttypeCache.getDescription(post));
                    table.getCellFormatter().setStyleName(row, 0, "desc");

                    table.setHTML(row, 1, Util.money(object.get(post)));
                    table.getCellFormatter().setStyleName(row, 1, "right");

                    String style = (row % 2 == 0) ? "showlineposts2" : "showlineposts1";
                    table.getRowFormatter().setStyleName(row, style);

                    row++;
                }

                deprecationRenderer.display(root.get("deprecation").isArray(), constants, messages, elements);
            }

        };

        AuthResponder.get(constants, messages, rh, "accounting/endmonthorsemester.php?action=status");
    }

    @Override
    public void onClick(ClickEvent event) {

        boolean okContinue = Window.confirm(messages.end_month_confirm());

        if (!okContinue) {
            return;
        }

        ServerResponse rh = new ServerResponseWithValidation() {
            
            @Override
            public void serverResponse(JSONValue resonseObj) {
                if ("1".equals(Util.str(resonseObj.isString()))) {
                    callback.viewMonth();
                }
            }

            @Override
            public void validationError(List<String> fields) {
                DialogBox db = new DialogBox();
                db.setText(messages.use_end_month());
                db.setAutoHideEnabled(true);
                db.center();
            }

        };

        AuthResponder.get(constants, messages, rh, "accounting/endmonthorsemester.php?action=" + endType
                + "&deprecate=" + ((deprecationTable.getRowCount() > 2) ? "1" : "0") + "&deprdesc="
                + elements.deprecation());
    }

    @Override
    public void standardsLoaded() {
        
        if(unhandledKids) {
            return;
        }
        
        boolean enabled = registerStandards.getCurrentMonth() != 12;
        endButton.setEnabled(enabled);
        
        if(!enabled) {
            endInfo.setText(messages.end_month_not_in_last_month());
        }
    }

}
