package no.knubo.accounting.client.ui;

import no.knubo.accounting.client.validation.Validateable;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;

public class ErrorLabelWidget extends Composite implements Validateable {

    protected final FocusWidget widget;
    protected HTML label;

    public ErrorLabelWidget(FocusWidget widget, HTML label) {
        this.widget = widget;
        this.label = label;

    }

    public ErrorLabelWidget(FocusWidget widget) {
        this.widget = widget;
        label = new HTML();
        label.setStyleName("error");
    }

    public void setFocus(boolean b) {
        widget.setFocus(b);
    }

    public void setMouseOver(String mouseOver) {
        widget.setTitle(mouseOver);
    }

    public String getText() {
        return "";
    }

    /**
     * Sets error to be displayed if error occures.
     * 
     * @param text
     *            The text.
     */
    public void setErrorText(String text) {
        label.setHTML(text);
    }
}
