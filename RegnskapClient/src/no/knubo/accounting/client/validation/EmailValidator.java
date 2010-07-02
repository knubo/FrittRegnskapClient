package no.knubo.accounting.client.validation;

public class EmailValidator extends ValidatorBase {

    public EmailValidator(String errorText) {
        super(errorText);
    }

    @Override
	protected boolean validate(Validateable val) {
        String email = val.getText().trim();

        if (email.length() == 0) {
            return true;
        }


        String[] severalEmails = email.split("\\,");

        if(severalEmails.length > 1) {
            for (String string : severalEmails) {
                if(!doValidation(string)) {
                    return false;
                }
            }
            return true;
        }
        
        return doValidation(email);
    }

    private boolean doValidation(String email) {
        int apos = email.indexOf('@');

        /* One char before and not last sign, not 2+ of them. */
        boolean clientCheck = apos > 0 && apos != (email.length() - 1)
                && email.indexOf('@', apos + 1) == -1;

        return clientCheck;
    }
}
