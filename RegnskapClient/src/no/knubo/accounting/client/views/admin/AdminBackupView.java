package no.knubo.accounting.client.views.admin;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.FileUploadWithErrorText;
import no.knubo.accounting.client.views.files.UploadDelegate;
import no.knubo.accounting.client.views.files.UploadDelegateCallback;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;

public class AdminBackupView extends Composite implements UploadDelegateCallback {


    private static AdminBackupView me;
    private final Elements elements;
    private final I18NAccount messages;
    private final Constants constants;
    private FileUploadWithErrorText upload;
    private UploadDelegate uploadDelegate;

    public AdminBackupView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;
        
        uploadDelegate = new UploadDelegate("admin/admin_backup.php", this, constants, messages, elements);

        DockPanel dp = new DockPanel();

        dp.add(uploadDelegate.getForm(), DockPanel.NORTH);


    }

    public static AdminBackupView getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new AdminBackupView(messages, constants, elements);
        }
        return me;
    }

    @Override
    public void uploadComplete() {
        /* Not needed */        
    }

    @Override
    public boolean uploadBody(String body) {
        return false;
    }

    @Override
    public void preUpload() {
        /* Not needed */
    }
}
