package no.knubo.accounting.client.misc;

import no.knubo.accounting.client.ui.ErrorLabelWidget;
import no.knubo.accounting.client.validation.Validateable;

public interface FocusCallback {

    void onLostFocus(ErrorLabelWidget me);

    void onFocus(Validateable me);
}
