package no.knubo.accounting.client;

import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

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
	 * @return The month string or "" if not of month 1 - 12.
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
			return "";
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
}
