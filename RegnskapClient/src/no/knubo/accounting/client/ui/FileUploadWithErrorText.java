package no.knubo.accounting.client.ui;

import no.knubo.accounting.client.validation.Validateable;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.UIObject;

public class FileUploadWithErrorText extends Composite implements Validateable {
    public FileUpload fileUpload;
    private HTML label;

    public FileUploadWithErrorText(String name) {
        this(name, new HTML());
    }

    public FileUploadWithErrorText(String name, HTML errorLabel) {
        this.label = errorLabel;
        this.fileUpload = new FileUpload();
        DOM.setElementAttribute(fileUpload.getElement(), "id", name);

        errorLabel.setStyleName("error");

        HorizontalPanel fp = new HorizontalPanel();
        fp.add(fileUpload);
        fp.add(errorLabel);
        initWidget(fp);
    }

    public void setEnabled(boolean enabled) {
        UIObject.setStyleName(fileUpload.getElement(), "disabled", !enabled);
        fileUpload.setEnabled(enabled);
    }

    public boolean isEnabled() {
        return fileUpload.isEnabled();
    }

    @Override
    public String getText() {
        return fileUpload.getFilename();
    }

    @Override
    public void setErrorText(String text) {
        label.setText(text);
    }

    @Override
    public void setFocus(boolean b) {
        /* Not possible */
    }

    @Override
    public void setMouseOver(String mouseOver) {
        fileUpload.setTitle(mouseOver);
    }

    public void setName(String string) {
        fileUpload.setName(string);
    }
}
