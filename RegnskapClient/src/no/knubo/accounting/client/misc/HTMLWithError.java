package no.knubo.accounting.client.misc;

import com.google.gwt.user.client.ui.HTML;

public class HTMLWithError extends HTML {

    @Override
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

    @Override
	public void setText(String text) {
        removeError();
        super.setText(text);
    }
}
