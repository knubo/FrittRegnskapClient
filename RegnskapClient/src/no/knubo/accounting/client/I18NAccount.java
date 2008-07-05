package no.knubo.accounting.client;


/**
 * Interface to represent the messages contained in resource  bundle:
 * 	/Users/knuterikborgen/Documents/workspacephp/RegnskapClient/src/no/knubo/accounting/client/I18NAccount.properties'.
 */
public interface I18NAccount extends com.google.gwt.i18n.client.Messages {
  
  /**
   * Translated "Mangler fødselsdato - {0} medlemmer".
   * 
   * @return translated "Mangler fødselsdato - {0} medlemmer"
   * @gwt.key report_year_unset
   */
  String report_year_unset(String arg0);

  /**
   * Translated "Ulovlig måned".
   * 
   * @return translated "Ulovlig måned"
   * @gwt.key illegal_month
   */
  String illegal_month();

  /**
   * Translated "Bilaget er benyttet tidligere.".
   * 
   * @return translated "Bilaget er benyttet tidligere."
   * @gwt.key attachment_used
   */
  String attachment_used();

  /**
   * Translated "Inngis dag må også medlemsskapstype velges  ".
   * 
   * @return translated "Inngis dag må også medlemsskapstype velges  "
   * @gwt.key add_member_day_require_action
   */
  String add_member_day_require_action();

  /**
   * Translated "Dato inngis på formatet dd.mm.yyyy".
   * 
   * @return translated "Dato inngis på formatet dd.mm.yyyy"
   * @gwt.key date_format
   */
  String date_format();

  /**
   * Translated "26 - 30 år - {0} medlemmer".
   * 
   * @return translated "26 - 30 år - {0} medlemmer"
   * @gwt.key report_year_30
   */
  String report_year_30(String arg0);

  /**
   * Translated "Penger inngis på format 10.45 og må være større eller lik 0".
   * 
   * @return translated "Penger inngis på format 10.45 og må være større eller lik 0"
   * @gwt.key field_money
   */
  String field_money();

  /**
   * Translated "Send epost til {0} mottagere?".
   * 
   * @return translated "Send epost til {0} mottagere?"
   * @gwt.key mail_confirm
   */
  String mail_confirm(String arg0);

  /**
   * Translated "Fikk ikke forventet svar fra server. Operasjonen er trolig ikke gjennomført.".
   * 
   * @return translated "Fikk ikke forventet svar fra server. Operasjonen er trolig ikke gjennomført."
   * @gwt.key bad_server_response
   */
  String bad_server_response();

  /**
   * Translated "Slett bruker?".
   * 
   * @return translated "Slett bruker?"
   * @gwt.key delete_user_question
   */
  String delete_user_question();

  /**
   * Translated "Vil du virkelig slette forekomst?".
   * 
   * @return translated "Vil du virkelig slette forekomst?"
   * @gwt.key confirm_delete
   */
  String confirm_delete();

  /**
   * Translated "Feil ved lagring av data".
   * 
   * @return translated "Feil ved lagring av data"
   * @gwt.key save_failed_badly
   */
  String save_failed_badly();

  /**
   * Translated "...lagret".
   * 
   * @return translated "...lagret"
   * @gwt.key save_ok
   */
  String save_ok();

  /**
   * Translated "41+ år - {0} medlemmer".
   * 
   * @return translated "41+ år - {0} medlemmer"
   * @gwt.key report_year_above
   */
  String report_year_above(String arg0);

  /**
   * Translated "{0} - {1} medlemmer".
   * 
   * @return translated "{0} - {1} medlemmer"
   * @gwt.key members_navig_heading
   */
  String members_navig_heading(String arg0,  String arg1);

  /**
   * Translated "Feltet må fylles ut".
   * 
   * @return translated "Feltet må fylles ut"
   * @gwt.key required_field
   */
  String required_field();

  /**
   * Translated "Klarte ikke koble til server".
   * 
   * @return translated "Klarte ikke koble til server"
   * @gwt.key failedConnect
   */
  String failedConnect();

  /**
   * Translated "Følgende felter er ikke validert ok: {0}.".
   * 
   * @return translated "Følgende felter er ikke validert ok: {0}."
   * @gwt.key field_validation_fail
   */
  String field_validation_fail(String arg0);

  /**
   * Translated "Slett filen {0}?".
   * 
   * @return translated "Slett filen {0}?"
   * @gwt.key delete_file_question
   */
  String delete_file_question(String arg0);

  /**
   * Translated "Avbryte utsendelse av mail?".
   * 
   * @return translated "Avbryte utsendelse av mail?"
   * @gwt.key mail_abort_confirm
   */
  String mail_abort_confirm();

  /**
   * Translated "19 år eller yngre - {0} medlemmer".
   * 
   * @return translated "19 år eller yngre - {0} medlemmer"
   * @gwt.key report_year_19
   */
  String report_year_19(String arg0);

  /**
   * Translated "Ulovlig dato".
   * 
   * @return translated "Ulovlig dato"
   * @gwt.key illegal_day
   */
  String illegal_day();

  /**
   * Translated "Velg mottakere av epost for å fortsette.".
   * 
   * @return translated "Velg mottakere av epost for å fortsette."
   * @gwt.key mail_choose_recivers
   */
  String mail_choose_recivers();

  /**
   * Translated "Vil du avslutte måned?".
   * 
   * @return translated "Vil du avslutte måned?"
   * @gwt.key end_month_confirm
   */
  String end_month_confirm();

  /**
   * Translated "Søket ga Ingen treff".
   * 
   * @return translated "Søket ga Ingen treff"
   * @gwt.key no_result
   */
  String no_result();

  /**
   * Translated "Fikk ikke svar fra server. Program- eller databasefeil.".
   * 
   * @return translated "Fikk ikke svar fra server. Program- eller databasefeil."
   * @gwt.key no_server_response
   */
  String no_server_response();

  /**
   * Translated "...ingen data oppdatert.".
   * 
   * @return translated "...ingen data oppdatert."
   * @gwt.key save_failed
   */
  String save_failed();

  /**
   * Translated "For mange treff. Viser kun {0}.".
   * 
   * @return translated "For mange treff. Viser kun {0}."
   * @gwt.key too_many_hits
   */
  String too_many_hits(String arg0);

  /**
   * Translated "Forekomst finnes ikke".
   * 
   * @return translated "Forekomst finnes ikke"
   * @gwt.key registry_invalid_key
   */
  String registry_invalid_key();

  /**
   * Translated "31 - 40 år - {0} medlemmer".
   * 
   * @return translated "31 - 40 år - {0} medlemmer"
   * @gwt.key report_year_40
   */
  String report_year_40(String arg0);

  /**
   * Translated "Feltet må ha større verdi enn 0".
   * 
   * @return translated "Feltet må ha større verdi enn 0"
   * @gwt.key field_to_low_zero
   */
  String field_to_low_zero();

  /**
   * Translated "Søket mislyktes - programfeil eller databasefeil".
   * 
   * @return translated "Søket mislyktes - programfeil eller databasefeil"
   * @gwt.key search_failed
   */
  String search_failed();

  /**
   * Translated "Feltet må være større eller lik 0".
   * 
   * @return translated "Feltet må være større eller lik 0"
   * @gwt.key field_positive
   */
  String field_positive();

  /**
   * Translated "Du har ikke tilgang til operasjonen".
   * 
   * @return translated "Du har ikke tilgang til operasjonen"
   * @gwt.key no_access
   */
  String no_access();

  /**
   * Translated "Din cachet(?) versjon av klienten er ikke på samme versjon som serverversjonen. Klientversjonen er {0} og serverversjonen er {1}. Prøv en shift reload av siden. Dette kan gjøre at deler av applikasjonen ikke fungerer som forventet.".
   * 
   * @return translated "Din cachet(?) versjon av klienten er ikke på samme versjon som serverversjonen. Klientversjonen er {0} og serverversjonen er {1}. Prøv en shift reload av siden. Dette kan gjøre at deler av applikasjonen ikke fungerer som forventet."
   * @gwt.key version_mismatch
   */
  String version_mismatch(String arg0,  String arg1);

  /**
   * Translated "Feil i fødselsdato - {0} medlemmer".
   * 
   * @return translated "Feil i fødselsdato - {0} medlemmer"
   * @gwt.key report_year_wrong
   */
  String report_year_wrong(String arg0);

  /**
   * Translated "20 - 25 år - {0} medlemmer".
   * 
   * @return translated "20 - 25 år - {0} medlemmer"
   * @gwt.key report_year_25
   */
  String report_year_25(String arg0);

  /**
   * Translated "Ulovlig epostadresse".
   * 
   * @return translated "Ulovlig epostadresse"
   * @gwt.key invalid_email
   */
  String invalid_email();

  /**
   * Translated "Du er ikke innlogget - �pner innloggingsvindu".
   * 
   * @return translated "Du er ikke innlogget - �pner innloggingsvindu"
   * @gwt.key not_logged_in
   */
  String not_logged_in();
}
