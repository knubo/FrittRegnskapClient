package no.knubo.accounting.client.views.files;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

public class UploadDelegate {
    private FormPanel form;
    
    public UploadDelegate(final UploadDelegateCallback callback, final Constants constants, final I18NAccount messages, final Elements elements) {

        form = new FormPanel();
        form.setAction(constants.baseurl() + "files/files.php");

        // Because we're going to add a FileUpload widget, we'll need to set the
        // form to use the POST method, and multi-part MIME encoding.
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        VerticalPanel panel = new VerticalPanel();
        form.setWidget(panel);

        final Label tb = new Label(elements.file());
        panel.add(tb);

        Hidden hidden = new Hidden();
        hidden.setName("action");
        hidden.setValue("upload");
        panel.add(hidden);

        FileUpload upload = new FileUpload();
        upload.setName("uploadFormElement");
        panel.add(upload);

        panel.add(new Button(elements.upload_file(), new ClickHandler() {

            public void onClick(ClickEvent event) {
                form.submit();
            }
        }));

        final Label statusLabel = new Label();

        panel.add(statusLabel);

        form.addSubmitHandler(new SubmitHandler() {
            public void onSubmit(SubmitEvent event) {
                if (tb.getText().length() == 0) {
                    Window.alert("The text box must not be empty");
                    event.cancel();
                }
            }
        });

        form.addSubmitCompleteHandler(new SubmitCompleteHandler() {

            public void onSubmitComplete(SubmitCompleteEvent event) {
                String result = event.getResults();

                if (result == null) {
                    Window.alert(messages.save_failed_badly());
                    return;
                }
                JSONValue jsonVal = JSONParser.parse(result);

                if (jsonVal == null) {
                    Window.alert(messages.save_failed_badly());
                    return;
                }

                JSONObject jsonObj = jsonVal.isObject();

                if (jsonObj == null) {
                    Window.alert(messages.save_failed_badly());
                    return;
                }

                String status = Util.str(jsonObj.get("status"));
                if ("1".equals(status)) {
                    statusLabel.setText(messages.save_ok());
                    Util.timedMessage(statusLabel, "", 15);
                    callback.uploadComplete();
                } else if ("-1".equals(status)) {
                    statusLabel.setText(messages.quota_exceeded());
                    Util.timedMessage(statusLabel, "", 15);
                    callback.uploadComplete();
                } else {
                    statusLabel.setText(messages.save_failed());
                    Util.timedMessage(statusLabel, "", 15);
                }
            }

        });
    }
    
    public FormPanel getForm() {
        return form;
    }
}
