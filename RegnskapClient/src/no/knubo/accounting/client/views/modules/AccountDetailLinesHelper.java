package no.knubo.accounting.client.views.modules;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.EmploeeCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.cache.ProjectCache;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class AccountDetailLinesHelper {

    private FlexTable table;
    private final Constants constants;
    private final I18NAccount messages;
    private int currentMonth;
    private int currentYear;

    public AccountDetailLinesHelper(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        table = new FlexTable();
        table.setStyleName("tableborder");

        table.setHTML(0, 1, messages.attachment());
        table.setHTML(0, 2, messages.date());
        table.setHTML(0, 3, messages.description());
        table.setHTML(0, 4, messages.project());
        table.setHTML(0, 5, messages.employee());
        table.setHTML(0, 6, messages.debkred());
        table.setHTML(0, 7, messages.amount());
        table.getRowFormatter().setStyleName(0, "header");

    }

    public Widget getTable() {
        return table;
    }

    public void init() {
        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }
    }

    public void renderResult(JSONArray array) {

        ProjectCache projectCache = ProjectCache.getInstance(constants, messages);
        EmploeeCache emploeeCache = EmploeeCache.getInstance(constants, messages);
        PosttypeCache posttypeCache = PosttypeCache.getInstance(constants, messages);

        int row = 0;
        for (int i = 0; i < array.size(); i++) {
            JSONValue one = array.get(i);

            JSONObject object = one.isObject();

            row++;
            table.getRowFormatter().setStyleName(row, "showpostheader");

            table.setText(row, 0, Util.str(object.get("Postnmb")) + "/"
                    + Util.str(object.get("Id")));
            table.getCellFormatter().setStyleName(row, 0, "right");

            table.setText(row, 1, Util.str(object.get("Attachment")));
            table.getCellFormatter().setStyleName(row, 1, "right");

            String date = Util.str(object.get("date"));
            table.setText(row, 2, date);
            table.getCellFormatter().setStyleName(row, 2, "datefor");

            if (currentMonth == 0 && date != null && date.length() > 0) {
                currentMonth = Util.getMonth(object.get("date"));
            }

            if (currentYear == 0 && date != null && date.length() > 0) {
                currentYear = Util.getYear(object.get("date"));
            }

            table.setText(row, 3, Util.str(object.get("Description")));
            table.getCellFormatter().setStyleName(row, 3, "desc");
            table.getFlexCellFormatter().setColSpan(row, 3, 5);

            JSONValue postArrVal = object.get("postArray");

            if (postArrVal == null) {
                continue;
            }

            JSONArray postArr = postArrVal.isArray();

            if (postArr == null) {
                continue;
            }

            for (int j = 0; j < postArr.size(); j++) {
                JSONValue postVal = postArr.get(j);

                JSONObject postObj = postVal.isObject();

                row++;

                table.getRowFormatter().setStyleName(
                        row,
                        (j % 2 == 0) ? "smallerfont showlineposts2"
                                : "smallerfont showlineposts1");

                String posttype = Util.str(postObj.get("Post_type"));

                table.setText(row, 3, posttype + " "
                        + posttypeCache.getDescription(posttype));
                table.getCellFormatter().setStyleName(row, 3, "desc");

                table.setText(row, 4, projectCache.getName(Util.str(postObj
                        .get("Project"))));
                table.getCellFormatter().setStyleName(row, 4, "desc");

                table.setText(row, 5, emploeeCache.getName(Util.str(postObj
                        .get("Person"))));
                table.getCellFormatter().setStyleName(row, 5, "desc");

                table.setText(row, 6, Util.debkred(messages, postObj
                        .get("Debet")));

                table.setText(row, 7, Util.money(postObj.get("Amount")));
                table.getCellFormatter().setStyleName(row, 7, "right");

            }
        }
    }

    public int getMonthAfterRender() {
        return currentMonth;
    }

    public int getYearAfterRender() {
        return currentYear;
    }
}
