package no.knubo.accounting.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Various nifty utilities for the project.
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

    public static boolean getBoolean(JSONValue str) {
        return "1".equals(str(str)) || "true".equals(str(str));
    }

    /**
     * Converts a number into a i18n month from the property file.
     * 
     * @param i18n
     *            I18N interface
     * @param month
     *            The month to find.
     * @return The month string or "ERROR" if not of month 1 - 12.
     */
    public static String monthString(Elements i18n, int month) {
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
    
    @SuppressWarnings("deprecation")
    public static int currentYear() {
        return new Date().getYear() + 1900;
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
            return value.toString().trim();
        }
        return string.stringValue().trim();
    }

    public static String money(JSONValue value) {
        if (value == null) {
            return "ERROR";
        }

        JSONString string = value.isString();
        if (string == null) {
            return money(value.toString());
        }
        return money(string.stringValue());
    }

    public static String money(String original) {
        if(original.length() == 0) {
            return "";
        }
        
        if (original.charAt(0) == '-') {
            return "-" + money(original.substring(1));
        }

        String work = original.replaceAll(",","");
        
        int sepPos = work.indexOf('.');

        String x = null;
        if (sepPos == -1) {
            x = work;
        } else {
            x = work.substring(0, sepPos);
        }
        // 100000000.00
        int count = x.length() / 3;

        if (count == 0) {
            return x + fixDecimal(work, sepPos);
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
        return res + fixDecimal(work, sepPos);
    }

    private static String fixDecimal(String str, int sepPos) {
        if (sepPos == -1) {
            return ".00";
        }

        String sub = str.substring(sepPos);
        switch (sub.length()) {
        case 3:
            return sub;
        case 2:
            return sub + "0";
        default:
            return sub.substring(0, 3);
        }
    }

    public static String debkred(Elements messages, JSONValue value) {
        JSONString string = value.isString();

        if (string == null) {
            return "ERROR";
        }

        return debkred(messages, string.stringValue());
    }

    public static String debkred(Elements messages, String string) {
        if ("1".equals(string)) {
            return messages.debet();
        }

        if ("-1".equals(string)) {
            return messages.kredit();
        }

        return "";
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
    	ChangeHandler listchange = new ChangeHandler() {

    		public void onChange(ChangeEvent event) {
                textbox.setText(listbox.getValue(listbox.getSelectedIndex()));
            }

        };
        listbox.addChangeHandler(listchange);

        ChangeHandler textchange = new ChangeHandler() {

    		public void onChange(ChangeEvent event) {
                String id = textbox.getText();

                for (int i = 0; i < listbox.getItemCount(); i++) {
                    if (listbox.getValue(i).equals(id)) {
                        listbox.setSelectedIndex(i);
                        return;
                    }
                }
            }

        };
        textbox.addChangeHandler(textchange);
    }

    /**
     * Get month part of string on format dd.mm.yyyy
     * 
     * @param value
     * @return
     */
    public static int getMonth(JSONValue value) {
        JSONString string = value.isString();

        if (string == null) {
            return 0;
        }

        return Integer.parseInt(string.stringValue().substring(3, 5));
    }

    /**
     * Get year part of string on format dd.mm.yyyy
     * 
     * @param value
     * @return
     */
    public static int getYear(JSONValue value) {
        JSONString string = value.isString();

        if (string == null) {
            return 0;
        }

        return Integer.parseInt(string.stringValue().substring(6));
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

    public static HashMap<Label, Timer> timers = new HashMap<Label, Timer>();

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
    public static void timedMessage(final Label label, final String message, int seconds) {

        Timer runningTimer = timers.get(label);

        if (runningTimer != null) {
            runningTimer.cancel();
        }
        Timer timer = new Timer() {

            @Override
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
        if (box.getItemCount() == 0) {
            return "";
        }
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

    public static void setIndexByItemText(ListBox listbox, String match) {
        if (listbox.getItemCount() == 0) {
            Window.alert("No items in combobox");
            return;
        }

        if (match == null) {
            Window.alert("Can't match null.");
            return;
        }

        for (int i = listbox.getItemCount(); i-- > 0;) {
            if (match.equals(listbox.getItemText(i))) {
                listbox.setSelectedIndex(i);
                return;
            }
        }
    }

    public static void setIndexByValue(ListBox listbox, String match) {
        if (listbox.getItemCount() == 0) {
            Window.alert("No items in combobox");
            return;
        }

        if (match == null) {
            Window.alert("Can't match null.");
            return;
        }

        for (int i = listbox.getItemCount(); i-- > 0;) {
            if (match.equals(listbox.getValue(i))) {
                listbox.setSelectedIndex(i);
                return;
            }
        }
    }

    public static double getDouble(JSONValue value) {
        if (value == null || isNull(value)) {
            return 0;
        }

        if (value.isNumber() != null) {
            JSONNumber numb = value.isNumber();
            double dub = numb.doubleValue();
            return dub;
        }

        String str = str(value);
        return Double.parseDouble(str.trim());
        
    }
    
    public static int getInt(JSONValue value) {
        if (value == null || isNull(value)) {
            return 0;
        }

        if (value.isNumber() != null) {
            JSONNumber numb = value.isNumber();
            double dub = numb.doubleValue();
            return (int) dub;
        }

        String str = str(value);
        return Integer.parseInt(str.trim());
    }

    /**
     * Links two checkboxes and make sure that only one is checked at a time.
     * 
     * @param checkOne
     * @param checkTwo
     */
    public static void linkJustOne(final CheckBox checkOne, final CheckBox checkTwo) {
        ClickHandler listener = new ClickHandler() {

        	public void onClick(ClickEvent event) {
                CheckBox sender = (CheckBox) event.getSource();
				if (sender  == checkOne) {
                    if (checkTwo.isEnabled() && checkOne.getValue()) {
                        checkTwo.setValue(false);
                    } else {
                        checkOne.setValue(false);
                    }
                } else if (sender == checkTwo) {
                    if (checkOne.isEnabled() && checkTwo.getValue()) {
                        checkOne.setValue(false);
                    } else {
                        checkTwo.setValue(false);
                    }
                }
            }

        };
        checkOne.addClickHandler(listener);
        checkTwo.addClickHandler(listener);
    }

    public static boolean isNull(JSONValue value) {
        String string = str(value);
        if (string.equals("null")) {
            return true;
        }
        return false;
    }

    public static String strSkipNull(JSONValue value) {
        String string = str(value);
        if (string.equals("null")) {
            return "";
        }
        return string;
    }

    public static int getInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void setCellId(FlexTable table, int row, int col, String id) {
        DOM.setElementAttribute(table.getCellFormatter().getElement(row, col), "id", id);
    }

    /**
     * Iterates fields and looks them up in translate. If found, it uses that
     * value, if not found it writes the non translated value in [].
     * 
     * @param fields
     * @param translate
     * @return The concatenated field list.
     */
    public static String translate(List<String> fields, HashMap<String,String> translate) {
        StringBuffer sb = new StringBuffer();

        for (String fieldName : fields) {

            String translated = translate.get(fieldName);

            if (sb.length() > 0) {
                sb.append(", ");
            }
            if (translated != null) {
                sb.append(translated);
            } else {
                sb.append("[" + fieldName + "]");
            }
        }
        return sb.toString();
    }

    public static String getSelectedText(ListBox box) {
        if (box.getItemCount() == 0) {
            return "";
        }
        return box.getItemText(box.getSelectedIndex());
    }

    public static native void log(String string) /*-{
                                                 if(window['console'])
                                                 window['console'].log(string);
                                                 }-*/;

}
