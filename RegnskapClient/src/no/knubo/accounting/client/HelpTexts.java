package no.knubo.accounting.client;


/**
 * Interface to represent the messages contained in resource  bundle:
 * 	/Users/knuterikborgen/Documents/workspacephp/RegnskapClient/src/no/knubo/accounting/client/HelpTexts.properties'.
 */
public interface HelpTexts extends com.google.gwt.i18n.client.Messages {
  
  /**
   * Translated "Dag er dagen posteringen skal registreres p&aring; i den aktive m&aring;neden.".
   * 
   * @return translated "Dag er dagen posteringen skal registreres p&aring; i den aktive m&aring;neden."
   * @gwt.key day
   */
  String day();

  /**
   * Translated "Bilag er bilagsnummeret til posteringeng. Bilagsnummer er p&aring;krevd, men m� ikke v&aelig;re unik per postering. God regnskapsskikk tilsier at alle posteringer skal v&aelig;re underst&oslash;ttet av et bilag, hvor bilaget skal inneholde orginalkvitteringer attestert av Leder og Kasserer.".
   * 
   * @return translated "Bilag er bilagsnummeret til posteringeng. Bilagsnummer er p&aring;krevd, men m� ikke v&aelig;re unik per postering. God regnskapsskikk tilsier at alle posteringer skal v&aelig;re underst&oslash;ttet av et bilag, hvor bilaget skal inneholde orginalkvitteringer attestert av Leder og Kasserer."
   * @gwt.key attachment
   */
  String attachment();

  /**
   * Translated "Postnr angir sorteringen som posteringen f&aring;r i den aktive m&aring;neden. Postnummeret tildeles automatisk og det er ikke behov for &aring; endre denne med mindre rekkef&oslash;lgen p&aring; posteringen m&aring; endres.".
   * 
   * @return translated "Postnr angir sorteringen som posteringen f&aring;r i den aktive m&aring;neden. Postnummeret tildeles automatisk og det er ikke behov for &aring; endre denne med mindre rekkef&oslash;lgen p&aring; posteringen m&aring; endres."
   * @gwt.key postnmb
   */
  String postnmb();
}
