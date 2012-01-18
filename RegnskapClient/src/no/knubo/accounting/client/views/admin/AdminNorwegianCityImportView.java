package no.knubo.accounting.client.views.admin;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.views.files.UploadDelegate;
import no.knubo.accounting.client.views.files.UploadDelegateCallback;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;

public class AdminNorwegianCityImportView extends Composite implements UploadDelegateCallback {
    private static AdminNorwegianCityImportView me;
    private UploadDelegate uploadDelegate;

    public static AdminNorwegianCityImportView getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new AdminNorwegianCityImportView(messages, constants, elements);
        }
        return me;
    }

    public AdminNorwegianCityImportView(I18NAccount messages, Constants constants, Elements elements) {
    
        uploadDelegate = new UploadDelegate("admin/admin_import_cities.php", this, constants, messages, elements);

        DockPanel dp = new DockPanel();

        dp.add(uploadDelegate.getForm(), DockPanel.NORTH);

    
        initWidget(dp);
    }

    public void uploadComplete() {
        /* Not needed */
    }

    public boolean uploadBody(String body) {
        return false;
    }

    public void preUpload() {
        /* Not needed */
    }

    
}
