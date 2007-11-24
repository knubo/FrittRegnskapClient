package no.knubo.accounting.client.views.modules;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.ui.ListBox;

public class YearMonthComboHelper {

    private final Constants constants;

    private final ListBox monthYearCombo;

    private int currentYear;

    private int currentMonth;

    private final Elements elements;

    public YearMonthComboHelper(Constants constants, ListBox monthYearCombo, Elements elements) {
        this.constants = constants;
        this.monthYearCombo = monthYearCombo;
        this.elements = elements;
    }

    public void fillYearMonthCombo() {
        monthYearCombo.clear();
        ResponseTextHandler resp = new ResponseTextHandler() {

            public void onCompletion(String responseText) {
                JSONValue value = JSONParser.parse(responseText);

                JSONArray array = value.isArray();

                for (int i = 0; i < array.size(); i++) {
                    JSONObject object = array.get(i).isObject();

                    int month = Util.getInt(object.get("month"));
                    int year = Util.getInt(object.get("year"));

                    String desc = Util.monthString(elements, month) + " " + year;
                    String val = year + "/" + month;
                    monthYearCombo.addItem(desc, val);
                }
                setIndex(currentYear, currentMonth);
            }

        };
        if (!HTTPRequest.asyncGet(this.constants.baseurl() + "defaults/yearmonths.php", resp)) {
            // TODO Report errors.
        }
    }

    public void setIndex(int currentYear, int currentMonth) {

        this.currentYear = currentYear;
        this.currentMonth = currentMonth;
        String matchMonth = String.valueOf(currentMonth);

        if (monthYearCombo.getItemCount() > 0) {
            String value = currentYear + "/" + matchMonth;
            Util.setIndexByValue(monthYearCombo, value);
        }
    }

}
