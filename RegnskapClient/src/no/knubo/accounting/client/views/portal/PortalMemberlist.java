package no.knubo.accounting.client.views.portal;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.views.ViewCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;

public class PortalMemberlist extends Composite implements ClickHandler {

    private static PortalMemberlist me;
    private AccountTable table;
    private final Constants constants;
    private final I18NAccount messages;
    private final Elements elements;
    private final ViewCallback callback;

    public PortalMemberlist(Constants constants, I18NAccount messages, Elements elements, ViewCallback callback) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;
        this.callback = callback;
        table = new AccountTable("dotted");

        table.setText(0, 0, elements.portal_members());
        table.getRowFormatter().setStyleName(0, "header");

        int col = 0;
        table.setText(1, col++, elements.firstname());
        table.setText(1, col++, elements.lastname());
        table.setText(1, col++, elements.last_login());

        table.getFlexCellFormatter().setColSpan(1, col, 2);
        table.setText(1, col++, elements.portal_homepage());

        table.getFlexCellFormatter().setColSpan(1, col, 2);
        table.setText(1, col++, elements.portal_twitter());
        
        table.getFlexCellFormatter().setColSpan(1, col, 2);
        table.setText(1, col++, elements.portal_facebook());

        table.getFlexCellFormatter().setColSpan(1, col, 2);
        table.setText(1, col++, elements.portal_linkedin());

        table.setText(1, col++, elements.firstname());
        table.setText(1, col++, elements.lastname());
        table.setText(1, col++, elements.birthdate());
        table.setText(1, col++, elements.email());
        table.setText(1, col++, elements.phone());
        table.setText(1, col++, elements.cellphone());
        table.setText(1, col++, elements.address());
        table.setText(1, col++, elements.postnmb());
        table.setText(1, col++, elements.city());
        table.setText(1, col++, elements.country());

        table.getFlexCellFormatter().setColSpan(1, col, 2);
        table.setText(1, col++, elements.portal_image());

        table.getFlexCellFormatter().setColSpan(0, 0, col+5);

        table.getRowFormatter().setStyleName(1, "header");

        initWidget(table);
    }

    public static PortalMemberlist getInstance(Constants constants, I18NAccount messages, Elements elements, ViewCallback callback) {
        if (me == null) {
            me = new PortalMemberlist(constants, messages, elements, callback);
        }
        return me;
    }

    public void init() {
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                JSONArray array = responseObj.isArray();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject object = array.get(i).isObject();

                    String id = Util.str(object.get("person"));
                    Anchor hyperlinkFirstname = new Anchor(Util.str(object.get("firstname")));
                    hyperlinkFirstname.addClickHandler(me);
                    hyperlinkFirstname.setName("fr" + id);

                    Anchor hyperlinkLastname = new Anchor(Util.str(object.get("lastname")));
                    hyperlinkLastname.addClickHandler(me);
                    hyperlinkLastname.setName("lr" + id);

                    int col = 0;

                    table.setWidget(i + 2, col++, hyperlinkFirstname, "desc");
                    table.setWidget(i + 2, col++, hyperlinkLastname, "desc");
                    String dato = Util.str(object.get("lastlogin"));
                    String[] split = dato.split(" ");

                    table.setText(i + 2, col++, Util.formatDate(split[0]) + " " + split[1], "desc");

                    table.setWidget(i + 2, col++, createAnchor(object, "homepage"), "desc");
                    table.setWidget(i + 2, col++, ImageFactory.deleteImage("delhome"+id));
                    
                    table.setWidget(i + 2, col++, createAnchor(object, "twitter"), "desc");
                    table.setWidget(i + 2, col++, ImageFactory.deleteImage("deltwit"+id));

                    table.setWidget(i + 2, col++, createAnchor(object, "facebook"), "desc");
                    table.setWidget(i + 2, col++, ImageFactory.deleteImage("delface"+id));
                    
                    table.setWidget(i + 2, col++, createAnchor(object, "linkedin"), "desc");
                    table.setWidget(i + 2, col++, ImageFactory.deleteImage("dellink"+id));
                    
                    setShow(i, object, "show_firstname", col++);
                    setShow(i, object, "show_lastname", col++);
                    setShow(i, object, "show_birthdate", col++);
                    setShow(i, object, "show_email", col++);
                    setShow(i, object, "show_phone", col++);
                    setShow(i, object, "show_cellphone", col++);
                    setShow(i, object, "show_address", col++);
                    setShow(i, object, "show_postnmb", col++);
                    setShow(i, object, "show_city", col++);
                    setShow(i, object, "show_country", col++);
                    setShow(i, object, "show_image", col++);
                    table.setWidget(i + 2, col++, ImageFactory.deleteImage("delimag"+id));
                }

            }

            private Anchor createAnchor(JSONObject object, String h) {
                String s = Util.str(object.get(h));
                return new Anchor(s, "http://" + s, "_target");
            }

            private void setShow(int i, JSONObject object, String key, int col) {
                table.setText(i + 2, col, Util.getBoolean(object.get(key)) ? elements.portal_show() : elements
                        .portal_hide(), "center");
            }
        };
        AuthResponder.get(constants, messages, callback, "portal/portal_admin.php?action=all");

    }

    public void onClick(ClickEvent event) {
        Anchor anchor = (Anchor) event.getSource();
        String extractedId = anchor.getName().substring(2);

        callback.editPerson(extractedId);
    }

}
