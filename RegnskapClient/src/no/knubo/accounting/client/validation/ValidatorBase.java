package no.knubo.accounting.client.validation;

import com.google.gwt.user.client.ui.Widget;

abstract public class ValidatorBase {

    private final String errorText;
    protected String mouseOver;

    public ValidatorBase(String errorText) {
        this.errorText = errorText;
    }

    public boolean validate(MasterValidator validator, Widget[] widgets) {
        boolean validationOK = true;

        for (int i = 0; i < widgets.length; i++) {
            Widget widget = widgets[i];

            if (!(widget instanceof Validateable)) {
                continue;
            }
            Validateable val = (Validateable) widget;

            if (!validator.validate(val, validate(val))) {
                val.setErrorText(errorText);
                val.setFocus(true);
                validationOK = false;

                if (mouseOver != null) {
                    val.setMouseOver(mouseOver);
                } else {
                    val.setMouseOver(null);
                }
            }
        }

        return validationOK;
    }

    abstract protected boolean validate(Validateable val);
}
