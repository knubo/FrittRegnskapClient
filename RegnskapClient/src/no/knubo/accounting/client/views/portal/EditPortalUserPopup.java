package no.knubo.accounting.client.views.portal;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public class EditPortalUserPopup extends DialogBox implements ClickHandler {

    private final Constants constants;
    private AccountTable delTable;
    private Image delHomepage;
    private Image delTwitter;
    private Image delFacebook;
    private Image delLinkedin;
    private NamedButton blockAccessButton;
    private NamedButton grantAccessButton;
    private NamedButton delPortalImage;
    private final JSONObject object;
    private final String id;
    private Image profileImage;
    private final I18NAccount messages;
    private final Elements elements;

    public EditPortalUserPopup(Elements elements, Constants constants, I18NAccount messages, JSONObject object,
            String id) {
        this.elements = elements;
        this.constants = constants;
        this.messages = messages;
        this.object = object;
        this.id = id;

        setModal(true);

        String title = "Rediger detaljer for " + Util.str(object.get("firstname")) + " "
                + Util.str(object.get("lastname"));
        setText(title);
        setAnimationEnabled(true);
        setAutoHideEnabled(true);

        HorizontalPanel hp = new HorizontalPanel();
        delTable = new AccountTable("tableborder");
        hp.add(delTable);

        delTable.setHeader(0, 0, elements.portal_homepage());
        delTable.setHeader(1, 0, elements.portal_twitter());
        delTable.setHeader(2, 0, elements.portal_facebook());
        delTable.setHeader(3, 0, elements.portal_linkedin());
        delTable.setHeader(4, 0, elements.portal_access());
        delTable.setHTML(5, 0, "&nbsp;");

        delTable.setText(0, 1, Util.str(object.get("homepage")));
        delTable.setText(1, 1, Util.str(object.get("twitter")));
        delTable.setText(2, 1, Util.str(object.get("facebook")));
        delTable.setText(3, 1, Util.str(object.get("linkedin")));
        boolean isDeactivated = Util.getBoolean(object.get("deactivated"));

        delTable.setText(4, 1, isDeactivated ? elements.portal_access_blocked() : elements.portal_access_granted());

        delHomepage = ImageFactory.deleteImage("delhomepage");
        delTwitter = ImageFactory.deleteImage("deltwitter");
        delFacebook = ImageFactory.deleteImage("delfacebook");
        delLinkedin = ImageFactory.deleteImage("dellinkedin");
        delHomepage.addClickHandler(this);
        delTwitter.addClickHandler(this);
        delFacebook.addClickHandler(this);
        delLinkedin.addClickHandler(this);

        delTable.setWidget(0, 2, delHomepage);
        delTable.setWidget(1, 2, delTwitter);
        delTable.setWidget(2, 2, delFacebook);
        delTable.setWidget(3, 2, delLinkedin);

        blockAccessButton = new NamedButton("block_acceess", "Sperr tilgang");
        blockAccessButton.addClickHandler(this);

        grantAccessButton = new NamedButton("grant_acceess", "Gi tilgang");
        grantAccessButton.addClickHandler(this);

        delPortalImage = new NamedButton("delete_portal_image", "Slett profilbilde");
        delPortalImage.addClickHandler(this);

        delTable.setWidget(5, 0, blockAccessButton);
        delTable.setWidget(5, 1, grantAccessButton);
        delTable.setWidget(6, 3, delPortalImage);

        profileImage = new Image("/RegnskapServer/services/portal/portal_admin.php?action=image&image=" + id
                + "&foolcache=" + System.currentTimeMillis());
        delTable.setWidget(0, 3, profileImage);
        delTable.getFlexCellFormatter().setRowSpan(0, 3, 6);

        blockAccessButton.setEnabled(!isDeactivated);
        grantAccessButton.setEnabled(isDeactivated);

        add(hp);

    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == delPortalImage) {
            if (Window.confirm(messages.portal_confirm_delete_image())) {
                deleteImage();
            }
        } else if (event.getSource() == delFacebook) {
            if (Window.confirm(messages.portal_confirm_delete_facebook())) {
                delField("facebook");
            }
        } else if (event.getSource() == delHomepage) {
            if (Window.confirm(messages.portal_confirm_delete_hompeage())) {
                delField("homepage");
            }
        } else if (event.getSource() == delLinkedin) {
            if(Window.confirm(messages.portal_confirm_delete_linkedin())) {
                delField("linkedin");
            }
        } else if (event.getSource() == delTwitter) {
            if(Window.confirm(messages.portal_confirm_delete_twitter())) {
                delField("twitter");
            }
        } else if(event.getSource() == blockAccessButton) {
            setBlocked(true);
        } else if(event.getSource() == grantAccessButton) {
            setBlocked(false);
        }
    }

    private void setBlocked(final boolean blocked) {
        ServerResponse callback = new ServerResponse() {
            
            public void serverResponse(JSONValue responseObj) {
                blockAccessButton.setEnabled(!blocked);
                grantAccessButton.setEnabled(blocked);
                delTable.setText(4, 1, blocked ? elements.portal_access_blocked() : elements.portal_access_granted());

            }
        };
        AuthResponder.get(constants, messages, callback , "portal/portal_admin.php?action=setblocked&blocked=" + (blocked ? 1 : 0) + "&id=" + id);

    }

    private void delField(final String field) {
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                if (field.equals("facebook")) {
                    delTable.setText(2, 1, "");
                } else if (field.equals("homepage")) {
                    delTable.setText(0, 1, "");
                } else if (field.equals("linkedin")) {
                    delTable.setText(3, 1, "");
                } else if (field.equals("twitter")) {
                    delTable.setText(1, 1, "");
                }
            }
        };
        AuthResponder.get(constants, messages, callback, "portal/portal_admin.php?action=del" + field + "&id=" + id);

    }

    private void deleteImage() {
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                profileImage.setUrl("???");
            }
        };
        AuthResponder.get(constants, messages, callback, "portal/portal_admin.php?action=delProfileImage&image=" + id);
    }
}
