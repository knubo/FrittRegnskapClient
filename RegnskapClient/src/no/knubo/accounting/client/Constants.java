package no.knubo.accounting.client;

/**
 * Interface to represent the constants contained in resource bundle:
 * 	'/Users/knuterikborgen/kode/workspace/RegnskapClient/src/no/knubo/accounting/client/Constants.properties'.
 */
public interface Constants extends com.google.gwt.i18n.client.Constants {
  
  /**
   * Translated "../no.knubo.accounting.AccountingGWT/AccountingGWT.html".
   * 
   * @return translated "../no.knubo.accounting.AccountingGWT/AccountingGWT.html"
   */
  @DefaultStringValue("../no.knubo.accounting.AccountingGWT/AccountingGWT.html")
  @Key("appURL")
  String appURL();

  /**
   * Translated "../../../RegnskapServer/services/".
   * 
   * @return translated "../../../RegnskapServer/services/"
   */
  @DefaultStringValue("../../../RegnskapServer/services/")
  @Key("baseurl")
  String baseurl();

  /**
   * Translated "../no.knubo.accounting.Login/Login.html".
   * 
   * @return translated "../no.knubo.accounting.Login/Login.html"
   */
  @DefaultStringValue("../no.knubo.accounting.Login/Login.html")
  @Key("loginURL")
  String loginURL();
}
