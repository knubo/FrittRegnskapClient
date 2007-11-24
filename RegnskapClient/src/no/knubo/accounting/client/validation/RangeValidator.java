package no.knubo.accounting.client.validation;

public class RangeValidator extends ValidatorBase {

    private final Integer minValue;
    private final Integer maxValue;

    public RangeValidator(String errorText, Integer minValue, Integer maxValue) {
        super(errorText);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    protected boolean validate(Validateable val) {
        String sval = val.getText();

        try {
            int value = 0;
            if (sval != null && sval.length() > 0) {
                value = Integer.parseInt(sval);
            }

            if (minValue != null && value < minValue.intValue()) {
                return false;
            }
            if (maxValue != null && value > maxValue.intValue()) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

}
