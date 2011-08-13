package no.knubo.accounting.client.ui;

import no.knubo.accounting.client.misc.FocusCallback;
import no.knubo.accounting.client.validation.Validateable;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TextAreaWithErrorText extends ErrorLabelWidget implements Validateable {
    public TextArea TextArea;

    public TextAreaWithErrorText(String name, HTML errorLabel) {
        super(new TextArea(), errorLabel);
        this.label = errorLabel;
        this.TextArea = (TextArea) widget;
        DOM.setElementAttribute(TextArea.getElement(), "id", name);

        errorLabel.setStyleName("error");
        initWidget(TextArea);
    }

    public TextAreaWithErrorText(String name, boolean flow) {
        super(new TextArea());
        TextArea = (TextArea) widget;
        DOM.setElementAttribute(TextArea.getElement(), "id", name);

        if (flow) {
            FlowPanel fp = new FlowPanel();

            fp.add(TextArea);
            fp.add(label);

            initWidget(fp);
        } else {
            createVerticalPanel();
        }
    }

    public TextAreaWithErrorText(String name) {
        super(new TextArea());
        TextArea = (TextArea) widget;
        DOM.setElementAttribute(TextArea.getElement(), "id", name);

        createVerticalPanel();
    }

    private void createVerticalPanel() {
        VerticalPanel vp = new VerticalPanel();

        vp.add(label);
        vp.add(TextArea);

        initWidget(vp);
    }

    public TextArea getTextArea() {
        return TextArea;
    }

    /**
     * Sets the text and resets error view.
     * 
     * @param string
     */
    public void setText(String string) {
        label.setText("");
        TextArea.setText(string);
    }

    @Override
    public String getText() {
        return TextArea.getText();
    }

    public void setEnabled(boolean enabled) {
        UIObject.setStyleName(TextArea.getElement(), "disabled", !enabled);
        TextArea.setEnabled(enabled);
    }

    Timer timer;

    /**
     * The event returned is always null. It is called after 1 second of
     * waiting.
     * 
     * @param handler
     */
    public void addDelayedKeyUpHandler(final KeyUpHandler handler) {
        KeyUpHandler delayedHandler = new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                if (timer != null) {
                    return;
                }
                timer = new Timer() {

                    @Override
                    public void run() {
                        handler.onKeyUp(null);
                        timer = null;
                    }

                };
                timer.schedule(1000);
            }
        };
        TextArea.addKeyUpHandler(delayedHandler);
    }

    public void addFocusListener(final FocusCallback callback) {
        final ErrorLabelWidget me = this;
        FocusHandler focusHandler = new FocusHandler() {

            public void onFocus(FocusEvent event) {
                callback.onFocus(me);
            }
        };

        BlurHandler blurHandler = new BlurHandler() {

            public void onBlur(BlurEvent event) {
                callback.onLostFocus(me);
            }
        };

        TextArea.addFocusHandler(focusHandler);
        TextArea.addBlurHandler(blurHandler);
    }

    public boolean isEnabled() {
        return TextArea.isEnabled();
    }

    public void setName(String string) {
        TextArea.setName(string);
    }
}
