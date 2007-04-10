package no.knubo.accounting.client;

public class Util {

	public static native void forward(String msg) /*-{
	  $wnd.location.href = msg;
	}-*/;

}
