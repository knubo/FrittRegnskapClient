package no.knubo.accounting.client.views.modules;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;

public class YearMonthComboHelper {

    private final I18NAccount messages;

    private final Constants constants;

    private final ListBox monthYearCombo;

    public YearMonthComboHelper(I18NAccount messages, Constants constants,
            ListBox monthYearCombo) {
        this.messages = messages;
        this.constants = constants;
        this.monthYearCombo = monthYearCombo;
    }

    public void fillYearMonthCombo() {
        ResponseTextHandler resp = new ResponseTextHandler() {

            public void onCompletion(String responseText) {
                JSONValue value = JSONParser.parse(responseText);

                JSONArray array = value.isArray();

                for (int i = 0; i < array.size(); i++) {
                    JSONObject object = array.get(i).isObject();

                    String month = Util.str(object.get("month"));
                    String year = Util.str(object.get("year"));

                    String desc = Util.monthString(messages, month) + " "
                            + year;
                    String val = year + "/" + month;
                    monthYearCombo.addItem(desc, val);
                }
            }

        };
        if (!HTTPRequest.asyncGet(this.constants.baseurl()
                + "defaults/yearmonths.php", resp)) {
            // TODO Report errors.
        }
    }

    public void setIndex(String currentYear, String currentMonth) {

        String matchMonth = currentMonth;
        if (currentMonth.startsWith("0")) {
            matchMonth = currentMonth.substring(1);
        }

        if (monthYearCombo.getItemCount() > 0) {
            String value = currentYear + "/" + matchMonth;
            Util.setIndexByValue(monthYearCombo, value);
        } else {
            Window.alert("Too early set year and month");
        }
    }

}
