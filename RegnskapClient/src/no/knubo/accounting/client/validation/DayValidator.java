package no.knubo.accounting.client.validation;

import java.util.Date;

public class DayValidator extends ValidatorBase {

	private final int year;

	private final int month;

	public DayValidator(String errorText, int month, int year) {
		super(errorText);
		this.month = month - 1;
		this.year = year - 1900;
	}

	protected boolean validate(Validateable val) {
		int day = 0;

		try {
			day = Integer.parseInt(val.getText());
		} catch (NumberFormatException e) {
			return false;
		}

		Date date = new Date(year, month, day, 0, 0, 0);

		return date.getDate() == day && date.getMonth() == month
				&& date.getYear() == year;
	}

}
