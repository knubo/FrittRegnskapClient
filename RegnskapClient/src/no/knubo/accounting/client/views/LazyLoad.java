package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.ui.Widget;

public abstract class LazyLoad {

    public abstract Widget getInstance(Constants constants,
            I18NAccount messages, ViewCallback caller);
}
