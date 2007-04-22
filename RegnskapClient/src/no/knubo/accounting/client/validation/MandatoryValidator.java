package no.knubo.accounting.client.validation;


public class MandatoryValidator extends ValidatorBase {

	public MandatoryValidator(String errorText) {
		super(errorText);
	}
	
	protected boolean validate(Validateable val) {
		return !(val.getText() == null || val.getText().trim().length() == 0);
	}
}
