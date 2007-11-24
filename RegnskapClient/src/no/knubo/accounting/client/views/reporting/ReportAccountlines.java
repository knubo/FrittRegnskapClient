package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
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

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
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
    private TextBoxWithErrorText descBox;
    private CheckBox showOnlyPosts;

    public static ReportAccountlines getInstance(Constants constants, I18NAccount messages,
            HelpPanel helpPanel, Elements elements) {
        if (reportInstance == null) {
            reportInstance = new ReportAccountlines(constants, messages, helpPanel, elements);
        }
        return reportInstance;
    }

    public ReportAccountlines(Constants constants, I18NAccount messages, HelpPanel helpPanel,
            Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.helpPanel = helpPanel;

        DockPanel dp = new DockPanel();
        accountDetailLinesHelper = new AccountDetailLinesHelper(constants, messages, elements);
        table = new FlexTable();
        table.setStyleName("edittable");

        table.setHTML(0, 0, elements.title_report_accountlines());
        table.getFlexCellFormatter().setColSpan(0, 0, 3);
        // table.getRowFormatter().setStyleName(0, "header");
        table.setText(1, 0, elements.from_date());
        table.setText(2, 0, elements.account());
        table.setText(3, 0, elements.project());
        table.setText(4, 0, elements.employee());
        table.setText(5, 0, elements.description());

        HorizontalPanel datesHP = new HorizontalPanel();

        fromDateBox = new TextBoxWithErrorText("from_date");
        fromDateBox.setMaxLength(10);
        fromDateBox.setVisibleLength(10);
        toDateBox = new TextBoxWithErrorText("to_date");
        toDateBox.setMaxLength(10);
        toDateBox.setVisibleLength(10);

        datesHP.add(fromDateBox);
        datesHP.add(new Label(elements.to_date()));
        datesHP.add(toDateBox);
        table.setWidget(1, 1, datesHP);

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

        ProjectCache.getInstance(constants, messages).fill(projectNameBox);
        Util.syncListbox(projectNameBox, projectIdBox.getTextBox());

        personBox = new ListBox();
        personBox.setVisibleItemCount(1);
        table.setWidget(4, 1, personBox);
        EmploeeCache.getInstance(constants, messages).fill(personBox);

        descBox = new TextBoxWithErrorText("description");
        table.setWidget(5, 1, descBox);

        HorizontalPanel shp = new HorizontalPanel();
        showOnlyPosts = new CheckBox();
        shp.add(showOnlyPosts);
        shp.add(new Label(elements.show_only_selcted_post()));

        table.setWidget(6, 1, shp);

        HorizontalPanel buttonBlock = new HorizontalPanel();
        searchButton = new NamedButton("search", elements.search());
        searchButton.addClickListener(this);
        buttonBlock.add(searchButton);
        clearButton = new NamedButton("clear", elements.clear());
        clearButton.addClickListener(this);
        buttonBlock.add(clearButton);

        table.setWidget(7, 0, buttonBlock);
        table.getFlexCellFormatter().setColSpan(7, 0, 2);

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

        mv.registry(messages.registry_invalid_key(), ProjectCache.getInstance(constants, messages),
                new Widget[] { projectIdBox });

        mv.registry(messages.registry_invalid_key(),
                PosttypeCache.getInstance(constants, messages), new Widget[] { accountIdBox });

        return mv.validateStatus();
    }

    private void doSearch() {
        accountDetailLinesHelper.init();
        StringBuffer searchRequest = new StringBuffer();

        searchRequest.append("action=search");
        Util.addPostParam(searchRequest, "fromdate", fromDateBox.getText());
        Util.addPostParam(searchRequest, "todate", toDateBox.getText());
        Util.addPostParam(searchRequest, "employee", Util.getSelected(personBox));
        Util.addPostParam(searchRequest, "project", projectIdBox.getText());
        final String accountId = accountIdBox.getText();
        Util.addPostParam(searchRequest, "account", accountId);
        Util.addPostParam(searchRequest, "description", descBox.getText());

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {
                JSONArray array = value.isArray();

                accountDetailLinesHelper.renderResult(array, showOnlyPosts.isChecked() ? accountId
                        : null);
                helpPanel.resize(reportInstance);
            }

        };

        AuthResponder
                .post(constants, messages, callback, searchRequest, "reports/accountlines.php");

    }

    private void doClear() {
        fromDateBox.setText("");
        toDateBox.setText("");
        accountIdBox.setText("");
        accountNameBox.setSelectedIndex(0);
        projectIdBox.setText("");
        projectNameBox.setSelectedIndex(0);
        personBox.setSelectedIndex(0);
        descBox.setText("");
        showOnlyPosts.setChecked(false);
    }
}
