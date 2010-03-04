package no.knubo.accounting.client;

/**
 * Interface to represent the messages contained in resource bundle:
 * 	/Users/knuterikborgen/kode/workspace/RegnskapClient/src/no/knubo/accounting/client/I18NAccount.properties'.
 */
public interface I18NAccount extends com.google.gwt.i18n.client.Messages {
  
  /**
   * Translated "Kontoen er allerede i bruk.".
   * 
   * @return translated "Kontoen er allerede i bruk."
   */
  @DefaultMessage("Kontoen er allerede i bruk.")
  @Key("account_already_used")
  String account_already_used();

  /**
   * Translated "Inngis dag må også medlemsskapstype velges  ".
   * 
   * @return translated "Inngis dag må også medlemsskapstype velges  "
   */
  @DefaultMessage("Inngis dag må også medlemsskapstype velges  ")
  @Key("add_member_day_require_action")
  String add_member_day_require_action();

  /**
   * Translated "Bilaget er benyttet tidligere.".
   * 
   * @return translated "Bilaget er benyttet tidligere."
   */
  @DefaultMessage("Bilaget er benyttet tidligere.")
  @Key("attachment_used")
  String attachment_used();

  /**
   * Translated "Fikk ikke forventet svar fra server. Operasjonen er trolig ikke gjennomført.".
   * 
   * @return translated "Fikk ikke forventet svar fra server. Operasjonen er trolig ikke gjennomført."
   */
  @DefaultMessage("Fikk ikke forventet svar fra server. Operasjonen er trolig ikke gjennomført.")
  @Key("bad_server_response")
  String bad_server_response();

  /**
   * Translated "Budjsett {0}".
   * 
   * @return translated "Budjsett {0}"
   */
  @DefaultMessage("Budjsett {0}")
  @Key("budget")
  String budget(String arg0);

  /**
   * Translated "Vil du virkelig slette forekomst?".
   * 
   * @return translated "Vil du virkelig slette forekomst?"
   */
  @DefaultMessage("Vil du virkelig slette forekomst?")
  @Key("confirm_delete")
  String confirm_delete();

  /**
   * Translated "Utgifter for {0}".
   * 
   * @return translated "Utgifter for {0}"
   */
  @DefaultMessage("Utgifter for {0}")
  @Key("cost_year")
  String cost_year(String arg0);

  /**
   * Translated "Dato inngis på formatet dd.mm.yyyy".
   * 
   * @return translated "Dato inngis på formatet dd.mm.yyyy"
   */
  @DefaultMessage("Dato inngis på formatet dd.mm.yyyy")
  @Key("date_format")
  String date_format();

  /**
   * Translated "Slett filen {0}?".
   * 
   * @return translated "Slett filen {0}?"
   */
  @DefaultMessage("Slett filen {0}?")
  @Key("delete_file_question")
  String delete_file_question(String arg0);

  /**
   * Translated "Slett bruker?".
   * 
   * @return translated "Slett bruker?"
   */
  @DefaultMessage("Slett bruker?")
  @Key("delete_user_question")
  String delete_user_question();

  /**
   * Translated "Vil du avslutte måned?".
   * 
   * @return translated "Vil du avslutte måned?"
   */
  @DefaultMessage("Vil du avslutte måned?")
  @Key("end_month_confirm")
  String end_month_confirm();

  /**
   * Translated "Vil du virkelig utføre årsavslutning?".
   * 
   * @return translated "Vil du virkelig utføre årsavslutning?"
   */
  @DefaultMessage("Vil du virkelig utføre årsavslutning?")
  @Key("end_year_confirm")
  String end_year_confirm();

  /**
   * Translated "Klarte ikke koble til server".
   * 
   * @return translated "Klarte ikke koble til server"
   */
  @DefaultMessage("Klarte ikke koble til server")
  @Key("failedConnect")
  String failedConnect();

  /**
   * Translated "Penger inngis på format 10.45 og må være større eller lik 0".
   * 
   * @return translated "Penger inngis på format 10.45 og må være større eller lik 0"
   */
  @DefaultMessage("Penger inngis på format 10.45 og må være større eller lik 0")
  @Key("field_money")
  String field_money();

  /**
   * Translated "Feltet må være større eller lik 0".
   * 
   * @return translated "Feltet må være større eller lik 0"
   */
  @DefaultMessage("Feltet må være større eller lik 0")
  @Key("field_positive")
  String field_positive();

  /**
   * Translated "Feltet må ha større verdi enn 0".
   * 
   * @return translated "Feltet må ha større verdi enn 0"
   */
  @DefaultMessage("Feltet må ha større verdi enn 0")
  @Key("field_to_low_zero")
  String field_to_low_zero();

  /**
   * Translated "Følgende felter er ikke validert ok: {0}.".
   * 
   * @return translated "Følgende felter er ikke validert ok: {0}."
   */
  @DefaultMessage("Følgende felter er ikke validert ok: {0}.")
  @Key("field_validation_fail")
  String field_validation_fail(String arg0);

  /**
   * Translated "Ulovlig dato".
   * 
   * @return translated "Ulovlig dato"
   */
  @DefaultMessage("Ulovlig dato")
  @Key("illegal_day")
  String illegal_day();

  /**
   * Translated "Ulovlig måned".
   * 
   * @return translated "Ulovlig måned"
   */
  @DefaultMessage("Ulovlig måned")
  @Key("illegal_month")
  String illegal_month();

  /**
   * Translated "Ulovlig år".
   * 
   * @return translated "Ulovlig år"
   */
  @DefaultMessage("Ulovlig år")
  @Key("illegal_year")
  String illegal_year();

  /**
   * Translated "Inntekter for {0}".
   * 
   * @return translated "Inntekter for {0}"
   */
  @DefaultMessage("Inntekter for {0}")
  @Key("income_year")
  String income_year(String arg0);

  /**
   * Translated "Ulovlig epostadresse".
   * 
   * @return translated "Ulovlig epostadresse"
   */
  @DefaultMessage("Ulovlig epostadresse")
  @Key("invalid_email")
  String invalid_email();

  /**
   * Translated "Avbryte utsendelse av mail?".
   * 
   * @return translated "Avbryte utsendelse av mail?"
   */
  @DefaultMessage("Avbryte utsendelse av mail?")
  @Key("mail_abort_confirm")
  String mail_abort_confirm();

  /**
   * Translated "Velg mottakere av epost for å fortsette.".
   * 
   * @return translated "Velg mottakere av epost for å fortsette."
   */
  @DefaultMessage("Velg mottakere av epost for å fortsette.")
  @Key("mail_choose_recivers")
  String mail_choose_recivers();

  /**
   * Translated "Send epost til {0} mottagere?".
   * 
   * @return translated "Send epost til {0} mottagere?"
   */
  @DefaultMessage("Send epost til {0} mottagere?")
  @Key("mail_confirm")
  String mail_confirm(String arg0);

  /**
   * Translated "{0} - {1} medlemmer".
   * 
   * @return translated "{0} - {1} medlemmer"
   */
  @DefaultMessage("{0} - {1} medlemmer")
  @Key("members_navig_heading")
  String members_navig_heading(String arg0,  String arg1);

  /**
   * Translated "Du har ikke tilgang til operasjonen".
   * 
   * @return translated "Du har ikke tilgang til operasjonen"
   */
  @DefaultMessage("Du har ikke tilgang til operasjonen")
  @Key("no_access")
  String no_access();

  /**
   * Translated "Søket ga Ingen treff".
   * 
   * @return translated "Søket ga Ingen treff"
   */
  @DefaultMessage("Søket ga Ingen treff")
  @Key("no_result")
  String no_result();

  /**
   * Translated "Fikk ikke svar fra server. Program- eller databasefeil.".
   * 
   * @return translated "Fikk ikke svar fra server. Program- eller databasefeil."
   */
  @DefaultMessage("Fikk ikke svar fra server. Program- eller databasefeil.")
  @Key("no_server_response")
  String no_server_response();

  /**
   * Translated "N/A".
   * 
   * @return translated "N/A"
   */
  @DefaultMessage("N/A")
  @Key("not_a_number")
  String not_a_number();

  /**
   * Translated "Du er ikke innlogget - åpner innloggingsvindu".
   * 
   * @return translated "Du er ikke innlogget - åpner innloggingsvindu"
   */
  @DefaultMessage("Du er ikke innlogget - åpner innloggingsvindu")
  @Key("not_logged_in")
  String not_logged_in();

  /**
   * Translated "Forekomst finnes ikke".
   * 
   * @return translated "Forekomst finnes ikke"
   */
  @DefaultMessage("Forekomst finnes ikke")
  @Key("registry_invalid_key")
  String registry_invalid_key();

  /**
   * Translated "19 år eller yngre - {0} medlemmer".
   * 
   * @return translated "19 år eller yngre - {0} medlemmer"
   */
  @DefaultMessage("19 år eller yngre - {0} medlemmer")
  @Key("report_year_19")
  String report_year_19(String arg0);

  /**
   * Translated "20 - 25 år - {0} medlemmer".
   * 
   * @return translated "20 - 25 år - {0} medlemmer"
   */
  @DefaultMessage("20 - 25 år - {0} medlemmer")
  @Key("report_year_25")
  String report_year_25(String arg0);

  /**
   * Translated "26 - 30 år - {0} medlemmer".
   * 
   * @return translated "26 - 30 år - {0} medlemmer"
   */
  @DefaultMessage("26 - 30 år - {0} medlemmer")
  @Key("report_year_30")
  String report_year_30(String arg0);

  /**
   * Translated "31 - 40 år - {0} medlemmer".
   * 
   * @return translated "31 - 40 år - {0} medlemmer"
   */
  @DefaultMessage("31 - 40 år - {0} medlemmer")
  @Key("report_year_40")
  String report_year_40(String arg0);

  /**
   * Translated "41+ år - {0} medlemmer".
   * 
   * @return translated "41+ år - {0} medlemmer"
   */
  @DefaultMessage("41+ år - {0} medlemmer")
  @Key("report_year_above")
  String report_year_above(String arg0);

  /**
   * Translated "Mangler fødselsdato - {0} medlemmer".
   * 
   * @return translated "Mangler fødselsdato - {0} medlemmer"
   */
  @DefaultMessage("Mangler fødselsdato - {0} medlemmer")
  @Key("report_year_unset")
  String report_year_unset(String arg0);

  /**
   * Translated "Feil i fødselsdato - {0} medlemmer".
   * 
   * @return translated "Feil i fødselsdato - {0} medlemmer"
   */
  @DefaultMessage("Feil i fødselsdato - {0} medlemmer")
  @Key("report_year_wrong")
  String report_year_wrong(String arg0);

  /**
   * Translated "Feltet må fylles ut".
   * 
   * @return translated "Feltet må fylles ut"
   */
  @DefaultMessage("Feltet må fylles ut")
  @Key("required_field")
  String required_field();

  /**
   * Translated "...ingen data oppdatert.".
   * 
   * @return translated "...ingen data oppdatert."
   */
  @DefaultMessage("...ingen data oppdatert.")
  @Key("save_failed")
  String save_failed();

  /**
   * Translated "Feil ved lagring av data".
   * 
   * @return translated "Feil ved lagring av data"
   */
  @DefaultMessage("Feil ved lagring av data")
  @Key("save_failed_badly")
  String save_failed_badly();

  /**
   * Translated "...lagret".
   * 
   * @return translated "...lagret"
   */
  @DefaultMessage("...lagret")
  @Key("save_ok")
  String save_ok();

  /**
   * Translated "Søket mislyktes - programfeil eller databasefeil".
   * 
   * @return translated "Søket mislyktes - programfeil eller databasefeil"
   */
  @DefaultMessage("Søket mislyktes - programfeil eller databasefeil")
  @Key("search_failed")
  String search_failed();

  /**
   * Translated "For mange treff. Viser kun {0}.".
   * 
   * @return translated "For mange treff. Viser kun {0}."
   */
  @DefaultMessage("For mange treff. Viser kun {0}.")
  @Key("too_many_hits")
  String too_many_hits(String arg0);

  /**
   * Translated "Din cachet(?) versjon av klienten er ikke på samme versjon som serverversjonen. Klientversjonen er {0} og serverversjonen er {1}. Prøv en shift reload av siden. Dette kan gjøre at deler av applikasjonen ikke fungerer som forventet.".
   * 
   * @return translated "Din cachet(?) versjon av klienten er ikke på samme versjon som serverversjonen. Klientversjonen er {0} og serverversjonen er {1}. Prøv en shift reload av siden. Dette kan gjøre at deler av applikasjonen ikke fungerer som forventet."
   */
  @DefaultMessage("Din cachet(?) versjon av klienten er ikke på samme versjon som serverversjonen. Klientversjonen er {0} og serverversjonen er {1}. Prøv en shift reload av siden. Dette kan gjøre at deler av applikasjonen ikke fungerer som forventet.")
  @Key("version_mismatch")
  String version_mismatch(String arg0,  String arg1);

  /**
   * Translated "år er påkrevet felt.".
   * 
   * @return translated "år er påkrevet felt."
   */
  @DefaultMessage("år er påkrevet felt.")
  @Key("year_required")
  String year_required();
}
