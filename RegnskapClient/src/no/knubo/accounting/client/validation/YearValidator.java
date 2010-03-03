package no.knubo.accounting.client.validation;

public class YearValidator extends ValidatorBase {

    public YearValidator(String error) {
        super(error);
    }

    @Override
    protected boolean validate(Validateable val) {
        try {
            int number = Integer.parseInt(val.getText());
            
            if(number < 1900 || number > 9999) {
                return false;
            }
            return true;
            
        } catch(NumberFormatException e) {
            return false;
        }
        
    }
}
