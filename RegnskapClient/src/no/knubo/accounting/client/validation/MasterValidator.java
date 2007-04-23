package no.knubo.accounting.client.validation;

import java.util.ArrayList;
import java.util.List;

import no.knubo.accounting.client.cache.Registry;

import com.google.gwt.user.client.ui.Widget;

public class MasterValidator {
	boolean status = true;

	List failedFields = new ArrayList();

	public void mandatory(String string, Widget[] widgets) {
		MandatoryValidator mandatoryValidation = new MandatoryValidator(
				string);

		status &= mandatoryValidation.validate(this, widgets);
	}

	public void day(String error, int year, int month, Widget[] widgets) {
		DayValidator dayvalidator = new DayValidator(error, month, year);
		status &= dayvalidator.validate(this, widgets);
	}
	
	public void money(String error, Widget[] widgets) {
		MoneyValidator moneyValidator = new MoneyValidator(error);
		status &= moneyValidator.validate(this, widgets);
	}
	
	public void range(String error, Integer minVal, Integer maxVal,
			Widget[] widgets) {
		RangeValidator rangeValidator = new RangeValidator(error, minVal,
				maxVal);

		status &= rangeValidator.validate(this, widgets);
	}

	public void registry(String error, Registry registry, Widget[] widgets) {
		RegistryValidator registryValidator = new RegistryValidator(error, registry);
		
		status &= registryValidator.validate(this, widgets);
	}
	
	public boolean validateStatus() {
		return status;
	}

	/**
	 * Called from the validators. It should return validation status. It also
	 * ensures that a field is validated false only once.
	 * 
	 * @param field
	 * @param valStatus
	 * @return
	 */
	boolean validate(Validateable field, boolean valStatus) {
		if (failedFields.contains(field)) {
			return true;
		}

		if (!valStatus) {
			failedFields.add(field);
		} else {
			field.setErrorText("");
		}
		
		return valStatus;
	}

	

}
