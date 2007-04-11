package no.knubo.accounting.client;

import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class Util {

	public static native void forward(String msg) /*-{
	 $wnd.location.href = msg;
	 }-*/;

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

	public static String formatDate(JSONValue value) {
		JSONString string = value.isString();
		
		if(string == null) {
			return value.toString();
		}
		String[] dateparts = string.stringValue().split("-");
		
		return dateparts[2]+"."+dateparts[1]+"."+dateparts[0];
	}

}
