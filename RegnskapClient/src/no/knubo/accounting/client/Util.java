package no.knubo.accounting.client;

import java.util.HashMap;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Various nifty utilities for the project.
 * 
 * @author knuterikborgen
 */
public class Util {

    /**
     * Forwards the clientside browser to the given location.
     * 
     * @param msg
     *            The url to forward to.
     */
    public static native void forward(String url) /*-{
     $wnd.location.href = url;
     }-*/;

    /**
     * Converts a number into a i18n month from the property file.
     * 
     * @param i18n
     *            I18N interface
     * @param m
     *            The month to find, as a wrapped int.
     * @return The month string or "ERROR" if not of month 1 - 12.
     */
    public static String monthString(I18NAccount i18n, String m) {
        int month = Integer.parseInt(m);
        switch (month) {
        case 1:
            return i18n.month_01();
        case 2:
            return i18n.month_02();
        case 3:
            return i18n.month_03();
        case 4:
            return i18n.month_04();
        case 5:
            return i18n.month_05();
        case 6:
            return i18n.month_06();
        case 7:
            return i18n.month_07();
        case 8:
            return i18n.month_08();
        case 9:
            return i18n.month_09();
        case 10:
            return i18n.month_10();
        case 11:
            return i18n.month_11();
        case 12:
            return i18n.month_12();
        default:
            return "ERROR";
        }
    }

    /**
     * Formats a jsonvalue as date 'dd.mm.yyyy' assumed from a date of format
     * 'yyyy-mm-dd'.
     * 
     * @param value
     *            The jason value
     * @return The value, or toString if it isn't a jsonString.
     */
    public static String formatDate(JSONValue value) {
        JSONString string = value.isString();

        if (string == null) {
            return value.toString();
        }
        String[] dateparts = string.stringValue().split("-");

        return dateparts[2] + "." + dateparts[1] + "." + dateparts[0];
    }

    /**
     * Extracts a java string from a jsonvalue.
     * 
     * @param value
     *            The value to extract.
     * @return The string or toString() if not a string for clarity.
     */
    public static String str(JSONValue value) {
        if (value == null) {
            return "ERROR";
        }

        JSONString string = value.isString();
        if (string == null) {
            return value.toString();
        }
        return string.stringValue();
    }

    public static String money(JSONValue value) {
        if (value == null) {
            return "ERROR";
        }

        JSONString string = value.isString();
        if (string == null) {
            return value.toString();
        }
        return money(string.stringValue());
    }

    public static String money(String original) {
        String str = original;

        if (str.charAt(0) == '-') {
            return "-" + money(str.substring(1));
        }

        if (str.indexOf('.') == -1) {
            str = str + ".00";
        }

        String x = str.substring(0, str.length() - 3);
        // 100000000.00
        int count = x.length() / 3;

        if (count < 0) {
            return str;
        }
        int left = x.length() % 3;

        String res = null;
        if (left > 0) {
            res = x.substring(0, left);
            if (count > 0) {
                res += ",";
            }
        } else {
            res = "";
        }

        for (int i = left; i < x.length(); i += 3) {
            res += x.substring(i, i + 3);

            if (i + 3 < x.length()) {
                res += ",";
            }
        }
        return res + "." + str.substring(str.length() - 2);
    }

    public static String debkred(I18NAccount messages, JSONValue value) {
        JSONString string = value.isString();

        if (string == null) {
            return "ERROR";
        }

        return debkred(messages, string.stringValue());
    }

    public static String debkred(I18NAccount messages, String string) {
        if ("1".equals(string)) {
            return messages.debet();
        }

        return messages.kredit();
    }

    /**
     * Adds listeners to the listbox and textbox so that the selected elements
     * id is displayed in the textbox when selected and visa versa for the
     * textbox.
     * 
     * @param listbox
     * @param textbox
     */
    public static void syncListbox(final ListBox listbox, final TextBox textbox) {
        ChangeListener listchange = new ChangeListener() {

            public void onChange(Widget sender) {
                textbox.setText(listbox.getValue(listbox.getSelectedIndex()));
            }

        };
        listbox.addChangeListener(listchange);

        ChangeListener textchange = new ChangeListener() {

            public void onChange(Widget sender) {
                String id = textbox.getText();

                for (int i = 0; i < listbox.getItemCount(); i++) {
                    if (listbox.getValue(i).equals(id)) {
                        listbox.setSelectedIndex(i);
                        return;
                    }
                }
            }

        };
        textbox.addChangeListener(textchange);
    }

    /**
     * Get month part of string on format dd.mm.yyyy
     * 
     * @param value
     * @return
     */
    public static String getMonth(JSONValue value) {
        JSONString string = value.isString();

        if (string == null) {
            return "ERROR";
        }

        return string.stringValue().substring(3, 5);
    }

    /**
     * Get year part of string on format dd.mm.yyyy
     * 
     * @param value
     * @return
     */
    public static String getYear(JSONValue value) {
        JSONString string = value.isString();

        if (string == null) {
            return "ERROR";
        }

        return string.stringValue().substring(6);
    }

    /**
     * Get day part of string on format dd.mm.yyyy
     * 
     * @param value
     * @return
     */
    public static String getDay(JSONValue value) {
        JSONString string = value.isString();

        if (string == null) {
            return "ERROR";
        }

        return string.stringValue().substring(0, 2);
    }

    public static HashMap timers = new HashMap();

    /**
     * Sets given message after some seconds.
     * 
     * @param label
     *            Label to set text in.
     * @param message
     *            Text to set.
     * @param seconds
     *            The amount of seconds before the text is set.
     */
    public static void timedMessage(final Label label, final String message,
            int seconds) {

        Timer runningTimer = (Timer) timers.get(label);

        if (runningTimer != null) {
            runningTimer.cancel();
        }
        Timer timer = new Timer() {

            public void run() {
                label.setText(message);
                timers.remove(label);
            }
        };
        timers.put(label, timer);
        timer.schedule(seconds * 1000);
    }

    /**
     * Adds &param=value encoded to the stringbuffer. Note the &.
     * 
     * @param sb
     *            The string buffer to add
     * @param param
     *            the paramameter name
     * @param value
     *            The value. if it is null or of length 0 it is not added.
     */
    public static void addPostParam(StringBuffer sb, String param, String value) {
        if (value == null || value.length() == 0) {
            return;
        }
        sb.append("&");
        sb.append(URL.encodeComponent(param));
        sb.append("=");
        sb.append(URL.encodeComponent(value));
    }

    public static String getSelected(ListBox box) {
        return box.getValue(box.getSelectedIndex());
    }

    public static String fixMoney(String original) {

        String text = original.replace(',', '.');

        int posp = text.indexOf('.');

        if (posp == -1) {
            return text + ".00";
        }

        /* Add the last 0 if just one number after . */
        if (text.substring(posp + 1).length() == 1) {
            return text + "0";
        }

        return text;
    }

    public static void setIndexByValue(ListBox listbox, String string) {
        if (listbox.getItemCount() == 0) {
            return;
        }
        for (int i = listbox.getItemCount(); i-- >= 0;) {
            if (listbox.getValue(i).equals(string)) {
                listbox.setSelectedIndex(i);
                return;
            }
        }
    }

    public static int getInt(JSONValue value) {
        if (value == null) {
            return 0;
        }
        JSONString str = value.isString();
        if (str == null) {
            return 0;
        }
        return Integer.parseInt(str.stringValue());
    }
}
