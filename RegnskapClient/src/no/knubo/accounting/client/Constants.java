package no.knubo.accounting.client;

/**
 * Interface to represent the constants contained in resource bundle:
 * 	'/Users/knuterikborgen/kode/workspace/client/RegnskapClient/src/no/knubo/accounting/client/Constants.properties'.
 */
public interface Constants extends com.google.gwt.i18n.client.Constants {
  
  /**
   * Translated "../prg/AccountingGWT.html".
   * 
   * @return translated "../prg/AccountingGWT.html"
   */
  @DefaultStringValue("../prg/AccountingGWT.html")
  @Key("appURL")
  String appURL();

  /**
   * Translated "/RegnskapServer/services/".
   * 
   * @return translated "/RegnskapServer/services/"
   */
  @DefaultStringValue("/RegnskapServer/services/")
  @Key("baseurl")
  String baseurl();

  /**
   * Translated "../login/Login.html".
   * 
   * @return translated "../login/Login.html"
   */
  @DefaultStringValue("../login/Login.html")
  @Key("loginURL")
  String loginURL();
}
