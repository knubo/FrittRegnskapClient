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
   * Translated "Domenet er allerede tatt".
   * 
   * @return translated "Domenet er allerede tatt"
   */
  @DefaultMessage("Domenet er allerede tatt")
  @Key("admin_error_domain")
  String admin_error_domain();

  /**
   * Translated "Ukjent wikilogin".
   * 
   * @return translated "Ukjent wikilogin"
   */
  @DefaultMessage("Ukjent wikilogin")
  @Key("admin_error_wikilogin")
  String admin_error_wikilogin();

  /**
   * Translated "Bilaget er benyttet tidligere.".
   * 
   * @return translated "Bilaget er benyttet tidligere."
   */
  @DefaultMessage("Bilaget er benyttet tidligere.")
  @Key("attachment_used")
  String attachment_used();

  /**
   * Translated "Sikkerhetskopi kan ikke benyttes uten tilgang til hemmelige adresser.".
   * 
   * @return translated "Sikkerhetskopi kan ikke benyttes uten tilgang til hemmelige adresser."
   */
  @DefaultMessage("Sikkerhetskopi kan ikke benyttes uten tilgang til hemmelige adresser.")
  @Key("backup_error")
  String backup_error();

  /**
   * Translated "Fikk ikke forventet svar fra server. Operasjonen er trolig ikke gjennomført.".
   * 
   * @return translated "Fikk ikke forventet svar fra server. Operasjonen er trolig ikke gjennomført."
   */
  @DefaultMessage("Fikk ikke forventet svar fra server. Operasjonen er trolig ikke gjennomført.")
  @Key("bad_server_response")
  String bad_server_response();

  /**
   * Translated "Endring av eiendelen gir endring i bokføringen av den. Under er posteringene som vil bli utført i regnskapet ved endring.".
   * 
   * @return translated "Endring av eiendelen gir endring i bokføringen av den. Under er posteringene som vil bli utført i regnskapet ved endring."
   */
  @DefaultMessage("Endring av eiendelen gir endring i bokføringen av den. Under er posteringene som vil bli utført i regnskapet ved endring.")
  @Key("beloning_change_accounting")
  String beloning_change_accounting();

  /**
   * Translated "Budjsett {0}".
   * 
   * @return translated "Budjsett {0}"
   */
  @DefaultMessage("Budjsett {0}")
  @Key("budget")
  String budget(String arg0);

  /**
   * Translated "Nullstill felter?".
   * 
   * @return translated "Nullstill felter?"
   */
  @DefaultMessage("Nullstill felter?")
  @Key("confirm_clear")
  String confirm_clear();

  /**
   * Translated "Vil du virkelig slette forekomst?".
   * 
   * @return translated "Vil du virkelig slette forekomst?"
   */
  @DefaultMessage("Vil du virkelig slette forekomst?")
  @Key("confirm_delete")
  String confirm_delete();

  /**
   * Translated "Vil du virkelig slette forekomst? Dette vil avskrive eiendelen i regnskapet?".
   * 
   * @return translated "Vil du virkelig slette forekomst? Dette vil avskrive eiendelen i regnskapet?"
   */
  @DefaultMessage("Vil du virkelig slette forekomst? Dette vil avskrive eiendelen i regnskapet?")
  @Key("confirm_delete_deprecate")
  String confirm_delete_deprecate();

  /**
   * Translated "Fortsette med import av personer?".
   * 
   * @return translated "Fortsette med import av personer?"
   */
  @DefaultMessage("Fortsette med import av personer?")
  @Key("confirm_import_person")
  String confirm_import_person();

  /**
   * Translated "Vil du registrere medlemskap uten bilagspostering?".
   * 
   * @return translated "Vil du registrere medlemskap uten bilagspostering?"
   */
  @DefaultMessage("Vil du registrere medlemskap uten bilagspostering?")
  @Key("confirm_register_membership")
  String confirm_register_membership();

  /**
   * Translated "Vil du sende ut portalaktiveringsbrev?".
   * 
   * @return translated "Vil du sende ut portalaktiveringsbrev?"
   */
  @DefaultMessage("Vil du sende ut portalaktiveringsbrev?")
  @Key("confirm_send_portal_letter")
  String confirm_send_portal_letter();

  /**
   * Translated "Vil du sende ut velkomstbrev?".
   * 
   * @return translated "Vil du sende ut velkomstbrev?"
   */
  @DefaultMessage("Vil du sende ut velkomstbrev?")
  @Key("confirm_send_welcome_letter")
  String confirm_send_welcome_letter();

  /**
   * Translated "Utgifter for {0}".
   * 
   * @return translated "Utgifter for {0}"
   */
  @DefaultMessage("Utgifter for {0}")
  @Key("cost_year")
  String cost_year(String arg0);

  /**
   * Translated "Regnskapsføringen er i rute".
   * 
   * @return translated "Regnskapsføringen er i rute"
   */
  @DefaultMessage("Regnskapsføringen er i rute")
  @Key("dashboard_accounting_ok")
  String dashboard_accounting_ok();

  /**
   * Translated "Alle grunndata er på plass".
   * 
   * @return translated "Alle grunndata er på plass"
   */
  @DefaultMessage("Alle grunndata er på plass")
  @Key("dashboard_all_basic_present")
  String dashboard_all_basic_present();

  /**
   * Translated "Lenge siden siste sikkerhetskopi".
   * 
   * @return translated "Lenge siden siste sikkerhetskopi"
   */
  @DefaultMessage("Lenge siden siste sikkerhetskopi")
  @Key("dashboard_long_since_last_backup")
  String dashboard_long_since_last_backup();

  /**
   * Translated "Lenge siden siste regnskapsføring".
   * 
   * @return translated "Lenge siden siste regnskapsføring"
   */
  @DefaultMessage("Lenge siden siste regnskapsføring")
  @Key("dashboard_long_time_no_accounting")
  String dashboard_long_time_no_accounting();

  /**
   * Translated "Neste månedsavslutning bødr bli semesteravslutning?".
   * 
   * @return translated "Neste månedsavslutning bødr bli semesteravslutning?"
   */
  @DefaultMessage("Neste månedsavslutning bødr bli semesteravslutning?")
  @Key("dashboard_maybe_change_fall")
  String dashboard_maybe_change_fall();

  /**
   * Translated "Mangler definisjon av neste semester".
   * 
   * @return translated "Mangler definisjon av neste semester"
   */
  @DefaultMessage("Mangler definisjon av neste semester")
  @Key("dashboard_missing_next_semester")
  String dashboard_missing_next_semester();

  /**
   * Translated "Mangler priser for inneværende semester".
   * 
   * @return translated "Mangler priser for inneværende semester"
   */
  @DefaultMessage("Mangler priser for inneværende semester")
  @Key("dashboard_missing_semester_price_current")
  String dashboard_missing_semester_price_current();

  /**
   * Translated "Mangler priser for neste semester".
   * 
   * @return translated "Mangler priser for neste semester"
   */
  @DefaultMessage("Mangler priser for neste semester")
  @Key("dashboard_missing_semester_price_next")
  String dashboard_missing_semester_price_next();

  /**
   * Translated "Mangler priser for inneværende års årsmedlemskap".
   * 
   * @return translated "Mangler priser for inneværende års årsmedlemskap"
   */
  @DefaultMessage("Mangler priser for inneværende års årsmedlemskap")
  @Key("dashboard_missing_year_price_current")
  String dashboard_missing_year_price_current();

  /**
   * Translated "Mangler priser for neste års årsmedlemskap".
   * 
   * @return translated "Mangler priser for neste års årsmedlemskap"
   */
  @DefaultMessage("Mangler priser for neste års årsmedlemskap")
  @Key("dashboard_missing_year_price_next")
  String dashboard_missing_year_price_next();

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
   * Translated "Innkjøpsdatoen kan ikke være i fremtiden.".
   * 
   * @return translated "Innkjøpsdatoen kan ikke være i fremtiden."
   */
  @DefaultMessage("Innkjøpsdatoen kan ikke være i fremtiden.")
  @Key("deprecation_future")
  String deprecation_future();

  /**
   * Translated "Eiendelen er allerede ferdig avskrevet så det blir ikke utført avskrivning på denne.".
   * 
   * @return translated "Eiendelen er allerede ferdig avskrevet så det blir ikke utført avskrivning på denne."
   */
  @DefaultMessage("Eiendelen er allerede ferdig avskrevet så det blir ikke utført avskrivning på denne.")
  @Key("deprecation_nothing_left")
  String deprecation_nothing_left();

  /**
   * Translated "Innkjøpspris kan ikke være lavere enn gjennstående beløp.".
   * 
   * @return translated "Innkjøpspris kan ikke være lavere enn gjennstående beløp."
   */
  @DefaultMessage("Innkjøpspris kan ikke være lavere enn gjennstående beløp.")
  @Key("deprecation_purchase_price_too_low")
  String deprecation_purchase_price_too_low();

  /**
   * Translated "Ved oppretting av eiendel blir nå gjennstående beløp overført til eiendelskonti og månedlige avskrivninger vil bilagføres automatisk til eiendelen er avskrevet.".
   * 
   * @return translated "Ved oppretting av eiendel blir nå gjennstående beløp overført til eiendelskonti og månedlige avskrivninger vil bilagføres automatisk til eiendelen er avskrevet."
   */
  @DefaultMessage("Ved oppretting av eiendel blir nå gjennstående beløp overført til eiendelskonti og månedlige avskrivninger vil bilagføres automatisk til eiendelen er avskrevet.")
  @Key("deprecation_with_account")
  String deprecation_with_account();

  /**
   * Translated "Hvis du ikke inngir antall år for avskrivning, med påfølgende konti, vil eiendelen kun registreres i eiendelsregisteret og avskrivning blir ikke utført. ".
   * 
   * @return translated "Hvis du ikke inngir antall år for avskrivning, med påfølgende konti, vil eiendelen kun registreres i eiendelsregisteret og avskrivning blir ikke utført. "
   */
  @DefaultMessage("Hvis du ikke inngir antall år for avskrivning, med påfølgende konti, vil eiendelen kun registreres i eiendelsregisteret og avskrivning blir ikke utført. ")
  @Key("deprecation_without_account")
  String deprecation_without_account();

  /**
   * Translated "Kladd lagret {0}.".
   * 
   * @return translated "Kladd lagret {0}."
   */
  @DefaultMessage("Kladd lagret {0}.")
  @Key("draft_saved")
  String draft_saved(String arg0);

  /**
   * Translated "Vil du slette valgt epost fra arkivet?".
   * 
   * @return translated "Vil du slette valgt epost fra arkivet?"
   */
  @DefaultMessage("Vil du slette valgt epost fra arkivet?")
  @Key("email_delete_confirm")
  String email_delete_confirm();

  /**
   * Translated "Vil du åpne valgt epost for redigering?".
   * 
   * @return translated "Vil du åpne valgt epost for redigering?"
   */
  @DefaultMessage("Vil du åpne valgt epost for redigering?")
  @Key("email_edit_confirm")
  String email_edit_confirm();

  /**
   * Translated "Ugyldig epostadresse inngitt. Ta kontakt med admin@frittregnskap.no om du mener du har inngitt riktig epostadresse.".
   * 
   * @return translated "Ugyldig epostadresse inngitt. Ta kontakt med admin@frittregnskap.no om du mener du har inngitt riktig epostadresse."
   */
  @DefaultMessage("Ugyldig epostadresse inngitt. Ta kontakt med admin@frittregnskap.no om du mener du har inngitt riktig epostadresse.")
  @Key("email_forgotten_error")
  String email_forgotten_error();

  /**
   * Translated "Epost for glemt passord er sendt.".
   * 
   * @return translated "Epost for glemt passord er sendt."
   */
  @DefaultMessage("Epost for glemt passord er sendt.")
  @Key("email_forgotten_sent")
  String email_forgotten_sent();

  /**
   * Translated "Vil du avslutte måned?".
   * 
   * @return translated "Vil du avslutte måned?"
   */
  @DefaultMessage("Vil du avslutte måned?")
  @Key("end_month_confirm")
  String end_month_confirm();

  /**
   * Translated "I årets siste måned må årsavslutning benyttes.".
   * 
   * @return translated "I årets siste måned må årsavslutning benyttes."
   */
  @DefaultMessage("I årets siste måned må årsavslutning benyttes.")
  @Key("end_month_not_in_last_month")
  String end_month_not_in_last_month();

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
   * Translated "Tips: Senere finner du hjelp ved å klikke på <em>hjelp</em> (til høyre)".
   * 
   * @return translated "Tips: Senere finner du hjelp ved å klikke på <em>hjelp</em> (til høyre)"
   */
  @DefaultMessage("Tips: Senere finner du hjelp ved å klikke på <em>hjelp</em> (til høyre)")
  @Key("first_time_hint")
  String first_time_hint();

  /**
   * Translated "Gi inn epostadressen du er registrert med i systemet og du vil motta en epost med informasjon for å logge inn slik at du kan endre passordet.".
   * 
   * @return translated "Gi inn epostadressen du er registrert med i systemet og du vil motta en epost med informasjon for å logge inn slik at du kan endre passordet."
   */
  @DefaultMessage("Gi inn epostadressen du er registrert med i systemet og du vil motta en epost med informasjon for å logge inn slik at du kan endre passordet.")
  @Key("forgottenPasswordIntro")
  String forgottenPasswordIntro();

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
   * Translated "Du må velge felt for fødselsdato.".
   * 
   * @return translated "Du må velge felt for fødselsdato."
   */
  @DefaultMessage("Du må velge felt for fødselsdato.")
  @Key("import_birthdate_required")
  String import_birthdate_required();

  /**
   * Translated "Du må velge felt for etternavn.".
   * 
   * @return translated "Du må velge felt for etternavn."
   */
  @DefaultMessage("Du må velge felt for etternavn.")
  @Key("import_lastname_required")
  String import_lastname_required();

  /**
   * Translated "Du kan kun velge et felt en gang.".
   * 
   * @return translated "Du kan kun velge et felt en gang."
   */
  @DefaultMessage("Du kan kun velge et felt en gang.")
  @Key("import_same_field_twice")
  String import_same_field_twice();

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
   * Translated "Konto er allerede valgt.".
   * 
   * @return translated "Konto er allerede valgt."
   */
  @DefaultMessage("Konto er allerede valgt.")
  @Key("kid_account_used")
  String kid_account_used();

  /**
   * Translated "Ukjent beløp".
   * 
   * @return translated "Ukjent beløp"
   */
  @DefaultMessage("Ukjent beløp")
  @Key("kid_bad_payment")
  String kid_bad_payment();

  /**
   * Translated "Det er allerede registrert kursmedlemskap.".
   * 
   * @return translated "Det er allerede registrert kursmedlemskap."
   */
  @DefaultMessage("Det er allerede registrert kursmedlemskap.")
  @Key("kid_course_already")
  String kid_course_already();

  /**
   * Translated "Posteringene har ikke beløp som ikke stemmer overens med kostnadene for medlemskap. Vil du fortsette?".
   * 
   * @return translated "Posteringene har ikke beløp som ikke stemmer overens med kostnadene for medlemskap. Vil du fortsette?"
   */
  @DefaultMessage("Posteringene har ikke beløp som ikke stemmer overens med kostnadene for medlemskap. Vil du fortsette?")
  @Key("kid_does_not_match")
  String kid_does_not_match();

  /**
   * Translated "Det er allerede registrert årsmedlemskap.".
   * 
   * @return translated "Det er allerede registrert årsmedlemskap."
   */
  @DefaultMessage("Det er allerede registrert årsmedlemskap.")
  @Key("kid_membership_already")
  String kid_membership_already();

  /**
   * Translated "Du har ikke registerert noe medlemskap. Vil du fortsette?".
   * 
   * @return translated "Du har ikke registerert noe medlemskap. Vil du fortsette?"
   */
  @DefaultMessage("Du har ikke registerert noe medlemskap. Vil du fortsette?")
  @Key("kid_no_membership")
  String kid_no_membership();

  /**
   * Translated "{0} av KID innbetalingene blir ikke registert da de ikke er komplette. Vil du fortsette?".
   * 
   * @return translated "{0} av KID innbetalingene blir ikke registert da de ikke er komplette. Vil du fortsette?"
   */
  @DefaultMessage("{0} av KID innbetalingene blir ikke registert da de ikke er komplette. Vil du fortsette?")
  @Key("kid_not_all_transactions")
  String kid_not_all_transactions(String arg0);

  /**
   * Translated "Det er allerede registrert treningsmedlemskap.".
   * 
   * @return translated "Det er allerede registrert treningsmedlemskap."
   */
  @DefaultMessage("Det er allerede registrert treningsmedlemskap.")
  @Key("kid_train_already")
  String kid_train_already();

  /**
   * Translated "Det er allerede registrert ungdomsmedlemskap.".
   * 
   * @return translated "Det er allerede registrert ungdomsmedlemskap."
   */
  @DefaultMessage("Det er allerede registrert ungdomsmedlemskap.")
  @Key("kid_youth_already")
  String kid_youth_already();

  /**
   * Translated "Din siste registrering er ikke balansert. Denne åpnes nå slik at du kan balansere den.".
   * 
   * @return translated "Din siste registrering er ikke balansert. Denne åpnes nå slik at du kan balansere den."
   */
  @DefaultMessage("Din siste registrering er ikke balansert. Denne åpnes nå slik at du kan balansere den.")
  @Key("line_debet_kredit_mismatch")
  String line_debet_kredit_mismatch();

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
   * Translated "Du må sette intielle verdier før du kan registrere medlemsskap. Konfigureringen åpnes nå.   ".
   * 
   * @return translated "Du må sette intielle verdier før du kan registrere medlemsskap. Konfigureringen åpnes nå.   "
   */
  @DefaultMessage("Du må sette intielle verdier før du kan registrere medlemsskap. Konfigureringen åpnes nå.   ")
  @Key("need_first_time_setup")
  String need_first_time_setup();

  /**
   * Translated "Du har ikke tilgang til operasjonen".
   * 
   * @return translated "Du har ikke tilgang til operasjonen"
   */
  @DefaultMessage("Du har ikke tilgang til operasjonen")
  @Key("no_access")
  String no_access();

  /**
   * Translated "Klarte ikke koble til databasen. Prøv å oppfriske nettleseren og eventuelt prøv igjen senere. Dette kan være en midlertidlig problem hos hostingtjenesten. Om feilen vedvarer, meld inn feil. ".
   * 
   * @return translated "Klarte ikke koble til databasen. Prøv å oppfriske nettleseren og eventuelt prøv igjen senere. Dette kan være en midlertidlig problem hos hostingtjenesten. Om feilen vedvarer, meld inn feil. "
   */
  @DefaultMessage("Klarte ikke koble til databasen. Prøv å oppfriske nettleseren og eventuelt prøv igjen senere. Dette kan være en midlertidlig problem hos hostingtjenesten. Om feilen vedvarer, meld inn feil. ")
  @Key("no_db_connection")
  String no_db_connection();

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
   * Translated "Vil du slette brukers link til facebook? Dette kan ikke angres.".
   * 
   * @return translated "Vil du slette brukers link til facebook? Dette kan ikke angres."
   */
  @DefaultMessage("Vil du slette brukers link til facebook? Dette kan ikke angres.")
  @Key("portal_confirm_delete_facebook")
  String portal_confirm_delete_facebook();

  /**
   * Translated "Vil du slette brukers link til hjemmeside? Dette kan ikke angres.".
   * 
   * @return translated "Vil du slette brukers link til hjemmeside? Dette kan ikke angres."
   */
  @DefaultMessage("Vil du slette brukers link til hjemmeside? Dette kan ikke angres.")
  @Key("portal_confirm_delete_hompeage")
  String portal_confirm_delete_hompeage();

  /**
   * Translated "Vil du slette profilbildet til brukeren? Dette kan ikke angres.".
   * 
   * @return translated "Vil du slette profilbildet til brukeren? Dette kan ikke angres."
   */
  @DefaultMessage("Vil du slette profilbildet til brukeren? Dette kan ikke angres.")
  @Key("portal_confirm_delete_image")
  String portal_confirm_delete_image();

  /**
   * Translated "Vil du slette brukers link til linkedin? Dette kan ikke angres.".
   * 
   * @return translated "Vil du slette brukers link til linkedin? Dette kan ikke angres."
   */
  @DefaultMessage("Vil du slette brukers link til linkedin? Dette kan ikke angres.")
  @Key("portal_confirm_delete_linkedin")
  String portal_confirm_delete_linkedin();

  /**
   * Translated "Vil du slette brukers link til twitter? Dette kan ikke angres.".
   * 
   * @return translated "Vil du slette brukers link til twitter? Dette kan ikke angres."
   */
  @DefaultMessage("Vil du slette brukers link til twitter? Dette kan ikke angres.")
  @Key("portal_confirm_delete_twitter")
  String portal_confirm_delete_twitter();

  /**
   * Translated "Opplastning mislyktes i og med at den filen du opplastet gikk over din diskkvote.".
   * 
   * @return translated "Opplastning mislyktes i og med at den filen du opplastet gikk over din diskkvote."
   */
  @DefaultMessage("Opplastning mislyktes i og med at den filen du opplastet gikk over din diskkvote.")
  @Key("quota_exceeded")
  String quota_exceeded();

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
   * Translated "Minst en person må kunne tildele hemmelige adresser. ".
   * 
   * @return translated "Minst en person må kunne tildele hemmelige adresser. "
   */
  @DefaultMessage("Minst en person må kunne tildele hemmelige adresser. ")
  @Key("secret_at_least_one")
  String secret_at_least_one();

  /**
   * Translated "Du må ha tilgang til å lese hemmelige adresser for å kunne endre tilgang for dem.".
   * 
   * @return translated "Du må ha tilgang til å lese hemmelige adresser for å kunne endre tilgang for dem."
   */
  @DefaultMessage("Du må ha tilgang til å lese hemmelige adresser for å kunne endre tilgang for dem.")
  @Key("secret_no_access")
  String secret_no_access();

  /**
   * Translated "Portalaktiveringsbrev sendt.".
   * 
   * @return translated "Portalaktiveringsbrev sendt."
   */
  @DefaultMessage("Portalaktiveringsbrev sendt.")
  @Key("sendt_portal_letter")
  String sendt_portal_letter();

  /**
   * Translated "Velkomstbrev sendt.".
   * 
   * @return translated "Velkomstbrev sendt."
   */
  @DefaultMessage("Velkomstbrev sendt.")
  @Key("sendt_welcome_letter")
  String sendt_welcome_letter();

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
   * Translated "Velkommen {0}, forrige innlogging var {1}.".
   * 
   * @return translated "Velkommen {0}, forrige innlogging var {1}."
   */
  @DefaultMessage("Velkommen {0}, forrige innlogging var {1}.")
  @Key("welcome_message")
  String welcome_message(String arg0,  String arg1);

  /**
   * Translated "år er påkrevet felt.".
   * 
   * @return translated "år er påkrevet felt."
   */
  @DefaultMessage("år er påkrevet felt.")
  @Key("year_required")
  String year_required();
}
