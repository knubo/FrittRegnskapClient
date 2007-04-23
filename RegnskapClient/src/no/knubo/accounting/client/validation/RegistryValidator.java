package no.knubo.accounting.client.validation;

import no.knubo.accounting.client.cache.Registry;

public class RegistryValidator extends ValidatorBase {

	private final Registry registry;

	public RegistryValidator(String errorText, Registry registry) {
		super(errorText);
		this.registry = registry;
	}

	protected boolean validate(Validateable val) {
		if(val.getText().length() == 0) {
			return true;
		}
		return registry.keyExists(val.getText());
	}
}
