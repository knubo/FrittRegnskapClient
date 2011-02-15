package no.knubo.accounting.client.views.portal;

import java.util.HashSet;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PortalGallery extends Composite {

    private static PortalGallery me;
    private final Constants constants;
    private final I18NAccount messages;
    protected HashSet<Integer> idsWithImages;
    protected JSONArray objectsWithIds;
    private VerticalPanel panel;

    public PortalGallery(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        
        panel = new VerticalPanel();
        
        initWidget(panel);
    }

    public static PortalGallery getInstance(Constants constants, I18NAccount messages) {
        if (me == null) {
            me = new PortalGallery(constants, messages);
        }
        return me;
    }

    public void init() {
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                JSONArray array = responseObj.isArray();

                HashSet<Integer> toSet = new HashSet<Integer>();

                for (int i = 0; i < array.size(); i++) {
                    toSet.add(Util.getInt(array.get(i)));
                }
                idsWithImages = toSet;

                if (idsWithImages != null && objectsWithIds != null) {
                    showImages();
                }
            }
        };

        ServerResponse callbackInfo = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                objectsWithIds = responseObj.isArray();

                if (idsWithImages != null && objectsWithIds != null) {
                    showImages();
                }
            }
        };
        AuthResponder.get(constants, messages, callback, "portal/portal_admin.php?action=allimages");

        AuthResponder.get(constants, messages, callbackInfo, "portal/portal_admin.php?action=all");

    }

    protected void showImages() {
        for (int i = 0; i < objectsWithIds.size(); i++) {
             JSONObject object = objectsWithIds.get(i).isObject();
             
             int id = Util.getInt(object.get("person"));
             
             if(!idsWithImages.contains(id)) {
                 continue;
             }
             
             panel.add(new Label(Util.str(object.get("firstname"))+" "+Util.str(object.get("lastname"))));
             panel.add(new Image("/RegnskapServer/services/portal/portal_admin.php?action=image&image="+id));
        }
    }

}
