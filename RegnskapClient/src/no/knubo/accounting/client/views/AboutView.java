package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ErrorReportingWindow;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.ServerResponseString;
import no.knubo.accounting.client.misc.WidgetIds;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class AboutView extends Composite implements ClickHandler {

    /** This must match Version.php's version */
    public static final String CLIENT_VERSION = "2.0b3";

    private static AboutView instance;

    private final Constants constants;

    private final I18NAccount messages;

    private HTML personInfo;

    private FlowPanel statusAccountPanel;

    private FlowPanel newsPanel;

    private FlowPanel statusPanel;

    private final Elements elements;

    private final ViewCallback callback;

    public static AboutView getInstance(Constants constants, I18NAccount messages, Elements elements,
            ViewCallback callback) {
        if (instance == null) {
            instance = new AboutView(constants, messages, elements, callback);
        }

        instance.checkServerVersion();
        instance.getNews();
        return instance;
    }

    private void getNews() {
        ServerResponse callback = new ServerResponseString() {

            public void serverResponse(JSONValue responseObj) {
                /* Not used */
            }

            public void serverResponse(String response) {
                newsPanel.clear();
                newsPanel.add(new HTML("<h2>Nyheter</h2>"));
                newsPanel.add(new HTML(parseNews(response)));
            }
        };
        AuthResponder.getExternal(constants, messages, callback, "/wakka/NyhetListe/raw");
    }

    protected String parseNews(String response) {
        String withoutHeader = response.replaceAll("=====Nyheter=====", "");

        String withWikiLinks = withoutHeader.replaceAll("\\[\\[(\\w+)\\s(.+)\\]\\]", "<a href=\"/wakka/$1\">$2</a>");

        String[] split = withWikiLinks.split("\n");

        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");
        for (int i = 0; i < split.length; i++) {
            if (split[i].matches("\\d\\d? \\w+ 20\\d\\d")) {
                sb.append("<li>");
                sb.append("<span class=\"date\">");
                sb.append(split[i]);
                sb.append("</span>");
            } else {
                sb.append(split[i]);
            }

        }

        return sb.toString();
    }

    private void checkServerVersion() {
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {

                JSONObject object = value.isObject();

                String serverVersion = Util.str(object.get("serverversion"));

                if (!(CLIENT_VERSION.equals(serverVersion))) {
                    Elements elements = (Elements) GWT.create(Elements.class);
                    ErrorReportingWindow.reportError(elements.error_client_version(), messages.version_mismatch(
                            CLIENT_VERSION, serverVersion));
                }

                enrichDashboard(object.get("info").isObject(), object.get("accountstatus"));
            }
        };

        AuthResponder.get(constants, messages, callback, "defaults/dashboard.php");
    }

    protected void enrichDashboard(JSONObject info, JSONValue accounts) {
        if(info == null) {
            personInfo.setHTML("Ingen info mottatt. Dette er unormalt...");
            return;
        }
        personInfo.setHTML("<h1>"
                + messages.welcome_message(Util.str(info.get("firstname")) + " " + Util.str(info.get("lastname")), Util
                        .str(info.get("lastlogin"))) + "</h1>");

        fillGeneralStatus(info);

        if (accounts != null) {
            fillStatusAccountsPanel(accounts.isArray());
        }

    }

    private void fillGeneralStatus(JSONObject info) {
        statusPanel.clear();

        FlowPanel fpAccounting = new FlowPanel();
        fpAccounting.addStyleName("accounting");

        addAlertAccounting(fpAccounting, info);

        statusPanel.add(fpAccounting);

        FlowPanel fpBasicData = new FlowPanel();
        fpBasicData.addStyleName("basicdata");

        addAlertBasicData(fpBasicData, info);

        statusPanel.add(fpBasicData);

    }

    private void addAlertBasicData(FlowPanel fpBasicData, JSONObject info) {
        boolean warning = false;
        boolean error = false;

        if (empty(info.get("next_year_price")) && empty(info.get("next_year_youth_price"))) {
            fpBasicData.add(addAnchor(messages.dashboard_missing_year_price_next()));
            warning = true;
        }

        if (empty(info.get("current_year_price")) && empty(info.get("current_year_youth_price"))) {
            fpBasicData.add(addAnchor(messages.dashboard_missing_year_price_current()));
            error = true;
        }

        if (empty(info.get("current_semester_course_price")) && empty(info.get("current_semester_train_price"))
                && empty(info.get("current_semester_youth_price"))) {
            fpBasicData.add(addAnchor(messages.dashboard_missing_semester_price_current()));
            error = true;
        }

        if (empty(info.get("next_semester_course_price")) && empty(info.get("next_semester_train_price"))
                && empty(info.get("next_semester_youth_price"))) {
            fpBasicData.add(addAnchor(messages.dashboard_missing_semester_price_next()));
            warning = true;
        }

        if (Util.getInt(info.get("max_semester_id")) == Util.getInt(info.get("semester_id"))) {
            fpBasicData.add(addAnchor(messages.dashboard_missing_next_semester()));
            warning = true;
        }

        if (error) {
            fpBasicData.insert(ImageFactory.faceSadImage("basic_sad"), 0);
        } else if (warning) {
            fpBasicData.insert(ImageFactory.facePlainImage("basic_plain"), 0);
        } else {
            fpBasicData.add(ImageFactory.faceSmileImage("basic_smile"));
            fpBasicData.add(new Label(messages.dashboard_all_basic_present()));
        }

    }

    private boolean empty(JSONValue value) {
        return Util.strSkipNull(value).length() == 0;
    }

    private Anchor addAnchor(String message) {
        Anchor anchor = new Anchor(message);
        anchor.addClickHandler(this);
        return anchor;
    }

    private void addAlertAccounting(FlowPanel fpAccounting, JSONObject info) {
        boolean warning = false;
        boolean error = false;

        if (info.containsKey("long_size_last_error")) {
            fpAccounting.add(addAnchor(messages.dashboard_long_time_no_accounting()));
            error = true;
        } else if (info.containsKey("long_size_last_warning")) {
            fpAccounting.add(addAnchor(messages.dashboard_long_time_no_accounting()));
            warning = true;
        }

        if (info.containsKey("mabye_change_semester")) {
            fpAccounting.add(new Label(messages.dashboard_maybe_change_fall()));
            warning = true;
        }

        if (error) {
            fpAccounting.insert(ImageFactory.faceSadImage("basic_sad"), 0);
        } else if (warning) {
            fpAccounting.insert(ImageFactory.facePlainImage("basic_plain"), 0);
        } else {
            fpAccounting.add(ImageFactory.faceSmileImage("basic_smile"));
            fpAccounting.add(new Label(messages.dashboard_accounting_ok()));
        }

    }

    private void fillStatusAccountsPanel(JSONArray accounts) {
        statusAccountPanel.clear();
        statusAccountPanel.add(new HTML("<h2>" + elements.menuitem_report_accounttrack() + "</h2>"));

        FlexTable table = new FlexTable();
        statusAccountPanel.add(table);

        PosttypeCache posttypeCache = PosttypeCache.getInstance(constants, messages);

        for (int i = 0; i < accounts.size(); i++) {
            JSONObject account = accounts.get(i).isObject();

            String post = Util.str(account.get("post"));
            table.setText(i, 0, post);
            table.setText(i, 1, posttypeCache.getDescription(post));
            table.setText(i, 2, Util.money(account.get("s")));

            String style = (i % 2 == 0) ? "showlineposts2" : "showlineposts1";
            table.getRowFormatter().setStyleName(i, style);
        }
    }

    private AboutView(Constants constants, I18NAccount messages, Elements elements, ViewCallback callback) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;
        this.callback = callback;

        FlowPanel dashboard = new FlowPanel();
        dashboard.addStyleName("dashboard");

        FlowPanel whoami = new FlowPanel();
        whoami.addStyleName("whoami");
        personInfo = new HTML();
        whoami.add(personInfo);

        FlowPanel english = new FlowPanel();

        english.add(new Anchor(elements.change_language(), "./AccountingGWT.html?locale="
                + elements.change_language_locale()));

        english.addStyleName("english");
        whoami.add(english);

        dashboard.add(whoami);

        statusPanel = new FlowPanel();
        statusPanel.addStyleName("systemstatus");
        dashboard.add(statusPanel);

        newsPanel = new FlowPanel();
        newsPanel.addStyleName("news");
        dashboard.add(newsPanel);

        statusAccountPanel = new FlowPanel();
        statusAccountPanel.addStyleName("accountstatus");

        dashboard.add(statusAccountPanel);

        initWidget(dashboard);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() instanceof Anchor) {
            Anchor anchor = (Anchor) event.getSource();

            switchTo(anchor.getText());

        }
    }

    private void switchTo(String text) {
        if (text.equals(messages.dashboard_long_time_no_accounting())) {
            callback.openView(WidgetIds.SHOW_MONTH, text);
        } else if (text.equals(messages.dashboard_missing_next_semester())) {
            callback.openView(WidgetIds.EDIT_SEMESTER, text);
        } else if (text.equals(messages.dashboard_missing_semester_price_current())
                || text.equals(messages.dashboard_missing_semester_price_next())
                || text.equals(messages.dashboard_missing_year_price_current())
                || text.equals(messages.dashboard_missing_year_price_next())) {
            callback.openView(WidgetIds.EDIT_PRICES, text);
        }

    }

}
