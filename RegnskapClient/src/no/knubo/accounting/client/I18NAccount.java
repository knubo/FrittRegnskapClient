package no.knubo.accounting.client;


/**
 * Interface to represent the messages contained in resource  bundle:
 * 	/Users/knuterikborgen/Documents/workspacephp/RegnskapClient/src/no/knubo/accounting/client/I18NAccount.properties'.
 */
public interface I18NAccount extends com.google.gwt.i18n.client.Messages {
  
  /**
   * Translated "Passord".
   * 
   * @return translated "Passord"
   * @gwt.key password
   */
  String password();

  /**
   * Translated "M�nedsoversikt".
   * 
   * @return translated "M�nedsoversikt"
   * @gwt.key title_monthview
   */
  String title_monthview();

  /**
   * Translated "Brukernavn".
   * 
   * @return translated "Brukernavn"
   * @gwt.key user
   */
  String user();

  /**
   * Translated "Klarte ikke koble til server".
   * 
   * @return translated "Klarte ikke koble til server"
   * @gwt.key failedConnect
   */
  String failedConnect();

  /**
   * Translated "Logg inn".
   * 
   * @return translated "Logg inn"
   * @gwt.key login
   */
  String login();

  /**
   * Translated "Laster inn siden...".
   * 
   * @return translated "Laster inn siden..."
   * @gwt.key loding_page
   */
  String loding_page();

  /**
   * Translated "Vis m&aring;ned".
   * 
   * @return translated "Vis m&aring;ned"
   * @gwt.key menuitem_showmonth
   */
  String menuitem_showmonth();

  /**
   * Translated "Regnskap".
   * 
   * @return translated "Regnskap"
   * @gwt.key menu_accounting
   */
  String menu_accounting();
}
