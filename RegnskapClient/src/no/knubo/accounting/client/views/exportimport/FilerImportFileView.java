package no.knubo.accounting.client.views.exportimport;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.views.files.UploadDelegate;
import no.knubo.accounting.client.views.files.UploadDelegateCallback;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;

public class FilerImportFileView extends Composite implements UploadDelegateCallback {
    private static FilerImportFileView me;
    private UploadDelegate uploadDelegate;
    private Frame outputFrame;
    private HTML uploadOutput;
    private final Constants constants;
    private Hidden separator;
    private TextBoxWithErrorText separatorInput;

    public static FilerImportFileView getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new FilerImportFileView(messages, constants, elements);
        }
        return me;
    }

    public FilerImportFileView(I18NAccount messages, Constants constants, Elements elements) {

        this.constants = constants;

        separator = new Hidden();
        separator.setName("separator");
        separator.setValue(";");
        
        uploadDelegate = new UploadDelegate("exportimport/filter_for_import_upload.php", this, constants, messages,
                elements, separator);

        DockPanel dp = new DockPanel();

        dp.add(new Label(elements.delimiter()), DockPanel.NORTH);
        separatorInput = new TextBoxWithErrorText("delimiter");
        separatorInput.setText(";");
        dp.add(separatorInput, DockPanel.NORTH);
        
        dp.add(uploadDelegate.getForm(), DockPanel.NORTH);

        uploadOutput = new HTML();
        dp.add(uploadOutput, DockPanel.NORTH);

        outputFrame = new Frame();
        outputFrame.setWidth((Window.getClientWidth()-60)+"px");
        outputFrame.setHeight((Window.getClientHeight()-100)+"px");
        dp.add(outputFrame, DockPanel.NORTH);

        initWidget(dp);
    }

    public void uploadComplete() {
        /* Not needed */
    }

    public boolean uploadBody(String body) {
        uploadOutput.setHTML(body);
        outputFrame.setUrl(constants.baseurl() + "exportimport/filter_for_import_read.php");
        return true;
    }

    public void preUpload() {
        separator.setValue(separatorInput.getText());
        uploadOutput.setHTML("...<blink>.</blink>");
    }

}
