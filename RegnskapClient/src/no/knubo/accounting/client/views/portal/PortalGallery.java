package no.knubo.accounting.client.views.portal;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.ui.Composite;

public class PortalGallery extends Composite {

    private static PortalGallery me;

    public PortalGallery(Constants constants, I18NAccount messages, Elements elements) {
        
    }

    public static PortalGallery getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (me == null) {
            me = new PortalGallery(constants, messages, elements);
        }
        return me;
    }

    public void init() {
        
    }
    
}
