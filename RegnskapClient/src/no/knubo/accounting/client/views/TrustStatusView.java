package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;

public class TrustStatusView extends Composite {

    private static TrustStatusView instance;

    private final Constants constants;

    private final I18NAccount messages;

    private FlexTable table;

    public static TrustStatusView getInstance(Constants constants,
            I18NAccount messages) {
        if (instance == null) {
            instance = new TrustStatusView(constants, messages);
        }
        return instance;
    }

    public TrustStatusView(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tableborder");
        dp.add(table, DockPanel.NORTH);

        initWidget(dp);
    }

    public void init() {
        table.clear();

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                constants.baseurl() + "accounting/edittrust.php");

        RequestCallback callback = new RequestCallback() {
            public void onError(Request request, Throwable exception) {
                Window.alert(exception.getMessage());
            }

            public void onResponseReceived(Request request, Response response) {
                JSONValue value = JSONParser.parse(response.getText());
                JSONObject object = value.isObject();
                renderResult(object);
            }
        };

        try {
            builder.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            builder.sendRequest("action=status", callback);
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
    }

    protected void renderResult(JSONObject object) {
        JSONValue value = object.get("types");
        JSONArray array = value.isArray();
        JSONValue dataVal = object.get("data");
        JSONObject dataObj = dataVal.isObject();
        JSONValue sumFondVal = object.get("sumfond");
        JSONValue sumClubVal = object.get("sumclub");
        JSONObject sumFondObj = sumFondVal.isObject();
        JSONObject sumClubObj = sumClubVal.isObject();

        for (int i = 0; i < array.size(); i++) {
            JSONValue fondVal = array.get(i);
            JSONObject fondObj = fondVal.isObject();

            String fond = Util.str(fondObj.get("fond"));
            String description = Util.str(fondObj.get("description"));
            JSONValue fondLines = dataObj.get(fond);
            String sumFond = Util.money(sumFondObj.get(fond));
            String sumClub = Util.money(sumClubObj.get(fond));
            
            renderFond(fond, description, fondLines.isArray(), sumFond, sumClub);
        }
    }

    private void renderFond(String fond, String description,
            JSONArray fondlines, String sumFond, String sumClub) {
        int row = table.getRowCount();
        table.setHTML(row, 0, description);
        table.getRowFormatter().setStyleName(row, "header");
        table.getFlexCellFormatter().setColSpan(row, 0, 4);
        row++;

        table.setHTML(row, 0, messages.description());
        table.setHTML(row, 1, messages.date());
        table.setHTML(row, 2, messages.trust_account());
        table.setHTML(row, 3, messages.club_account());
        table.getRowFormatter().setStyleName(row, "header");
        row++;

        for (int i = 0; i < fondlines.size(); i++) {
            JSONValue lineVal = fondlines.get(i);
            JSONObject lineObj = lineVal.isObject();

            table.setHTML(row, 0, Util.str(lineObj.get("Description")));
            table.getCellFormatter().setStyleName(row, 0, "desc");
            table.setHTML(row, 1, Util.formatDate(lineObj.get("Occured")));
            table.getCellFormatter().setStyleName(row, 1, "desc");

            table.setHTML(row, 2, Util.money(lineObj.get("Fond_account")));
            table.getCellFormatter().setStyleName(row, 2, "right colspace");

            table.setHTML(row, 3, Util.money(lineObj.get("Club_account")));
            table.getCellFormatter().setStyleName(row, 3, "right colspace");

            String style = (i % 2 == 0) ? "showlineposts2" : "showlineposts1";
            table.getRowFormatter().setStyleName(row, style);
            row++;
        }
        table.setHTML(row, 0, messages.sum());
        table.setText(row, 2, sumFond);
        table.getCellFormatter().setStyleName(row, 2, "right");
        table.setText(row, 3, sumClub);
        table.getCellFormatter().setStyleName(row, 3, "right");
        table.getRowFormatter().setStyleName(row, "sumline");
        row++;
    }

}
