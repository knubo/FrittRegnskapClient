package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.EmploeeCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.cache.ProjectCache;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.NamedButton;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.modules.AccountDetailLinesHelper;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ReportAccountlines extends Composite implements ClickListener {
    private static ReportAccountlines reportInstance;
    private final Constants constants;
    private final I18NAccount messages;
    private final HelpPanel helpPanel;
    private FlexTable table;
    private AccountDetailLinesHelper accountDetailLinesHelper;
    private TextBoxWithErrorText fromDateBox;
    private TextBoxWithErrorText toDateBox;
    private TextBoxWithErrorText projectIdBox;
    private ListBox projectNameBox;
    private TextBoxWithErrorText accountIdBox;
    private ListBox accountNameBox;
    private ListBox personBox;
    private NamedButton searchButton;
    private NamedButton clearButton;

    public static ReportAccountlines getInstance(Constants constants,
            I18NAccount messages, HelpPanel helpPanel) {
        if (reportInstance == null) {
            reportInstance = new ReportAccountlines(constants, messages,
                    helpPanel);
        }
        return reportInstance;
    }

    public ReportAccountlines(Constants constants, I18NAccount messages,
            HelpPanel helpPanel) {
        this.constants = constants;
        this.messages = messages;
        this.helpPanel = helpPanel;

        DockPanel dp = new DockPanel();
        accountDetailLinesHelper = new AccountDetailLinesHelper(constants,
                messages);
        table = new FlexTable();
        table.setStyleName("edittable");

        table.setHTML(0, 0, messages.title_report_accountlines());
        table.getFlexCellFormatter().setColSpan(0, 0, 4);
        // table.getRowFormatter().setStyleName(0, "header");
        table.setHTML(1, 0, messages.from_date());
        table.setHTML(1, 2, messages.to_date());
        table.setHTML(2, 0, messages.account());
        table.setHTML(3, 0, messages.project());
        table.setHTML(4, 0, messages.employee());

        fromDateBox = new TextBoxWithErrorText("from_date");
        fromDateBox.setMaxLength(10);
        fromDateBox.setVisibleLength(10);
        toDateBox = new TextBoxWithErrorText("to_date");
        toDateBox.setMaxLength(10);
        toDateBox.setVisibleLength(10);
        table.setWidget(1, 1, fromDateBox);
        table.setWidget(1, 3, toDateBox);

        HTML errorAccountHtml = new HTML();
        accountIdBox = new TextBoxWithErrorText("account", errorAccountHtml);
        accountIdBox.setVisibleLength(6);
        accountNameBox = new ListBox();
        accountNameBox.setVisibleItemCount(1);
        HorizontalPanel hpAccount = new HorizontalPanel();
        hpAccount.add(accountIdBox);
        hpAccount.add(accountNameBox);
        hpAccount.add(errorAccountHtml);
        table.setWidget(2, 1, hpAccount);
        table.getFlexCellFormatter().setColSpan(2, 1, 3);

        PosttypeCache.getInstance(constants, messages).fillAllPosts(accountNameBox);
        Util.syncListbox(accountNameBox, accountIdBox.getTextBox());

        HTML projectErrorLabel = new HTML();
        projectIdBox = new TextBoxWithErrorText("project", projectErrorLabel);
        projectIdBox.setVisibleLength(6);
        projectNameBox = new ListBox();
        projectNameBox.setVisibleItemCount(1);
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(projectIdBox);
        hp.add(projectNameBox);
        hp.add(projectErrorLabel);
        table.setWidget(3, 1, hp);
        table.getFlexCellFormatter().setColSpan(3, 1, 3);

        ProjectCache.getInstance(constants, messages).fill(projectNameBox);
        Util.syncListbox(projectNameBox, projectIdBox.getTextBox());

        personBox = new ListBox();
        personBox.setVisibleItemCount(1);
        table.setWidget(4, 1, personBox);
        table.getFlexCellFormatter().setColSpan(4, 1, 3);
        EmploeeCache.getInstance(constants, messages).fill(personBox);

        searchButton = new NamedButton("search", messages.search());
        searchButton.addClickListener(this);
        table.setWidget(5, 0, searchButton);
        clearButton = new NamedButton("clear", messages.clear());
        clearButton.addClickListener(this);
        table.setWidget(5, 1, clearButton);

        dp.add(table, DockPanel.NORTH);
        dp.add(accountDetailLinesHelper.getTable(), DockPanel.NORTH);
        initWidget(dp);
        helpPanel.resize(this);
    }

    public void onClick(Widget sender) {
        if (sender == clearButton) {
            doClear();
        } else if (sender == searchButton && validateSearch()) {
            doSearch();
        }
    }

    private boolean validateSearch() {
        MasterValidator mv = new MasterValidator();

        Widget[] datewidgets = new Widget[] { fromDateBox, toDateBox };
        mv.date(messages.date_format(), datewidgets);

        mv.registry(messages.registry_invalid_key(), ProjectCache
                .getInstance(constants, messages), new Widget[] { projectIdBox });

        mv.registry(messages.registry_invalid_key(), PosttypeCache
                .getInstance(constants, messages), new Widget[] { accountIdBox });

        return mv.validateStatus();
    }

    private void doSearch() {
        accountDetailLinesHelper.init();
        StringBuffer searchRequest = new StringBuffer();

        searchRequest.append("action=search");
        Util.addPostParam(searchRequest, "fromdate", fromDateBox.getText());
        Util.addPostParam(searchRequest, "todate", toDateBox.getText());
        Util.addPostParam(searchRequest, "employee", Util
                .getSelected(personBox));
        Util.addPostParam(searchRequest, "project", projectIdBox.getText());
        Util.addPostParam(searchRequest, "account", accountIdBox.getText());

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                constants.baseurl() + "reports/accountlines.php");

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(String serverResponse) {
                JSONValue value = JSONParser.parse(serverResponse);
                JSONArray array = value.isArray();
                accountDetailLinesHelper.renderResult(array);

                helpPanel.resize(reportInstance);
            }

        };

        try {
            builder.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            builder.sendRequest(searchRequest.toString(), new AuthResponder(constants, messages, callback));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }

    }

    private void doClear() {
        fromDateBox.setText("");
        toDateBox.setText("");
        accountIdBox.setText("");
        accountNameBox.setSelectedIndex(0);
        projectIdBox.setText("");
        projectNameBox.setSelectedIndex(0);
        personBox.setSelectedIndex(0);
    }
}
