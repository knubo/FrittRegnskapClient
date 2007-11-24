package no.knubo.accounting.client.misc;

import com.google.gwt.user.client.ui.HTML;

public class HTMLWithError extends HTML {

    public void setHTML(String html) {
        removeError();
        super.setHTML(html);
    }

    void removeError() {
        setStyleName("normal");
    }

    public void setError(String error) {
        setStyleName("error");
        super.setText(error);
    }

    public void setText(String text) {
        removeError();
        super.setText(text);
    }
}
