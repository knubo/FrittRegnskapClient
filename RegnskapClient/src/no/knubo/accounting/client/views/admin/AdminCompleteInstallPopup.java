package no.knubo.accounting.client.views.admin;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AdminCompleteInstallPopup extends DialogBox implements ClickHandler {

    private AccountTable table;
    private final String currentId;
    private final I18NAccount messages;
    private final Constants constants;
    private Label infoLabel;
    private NamedButton updateButton;
    private NamedButton completeButton;
    private NamedButton cancelButton;

    HashMap<String, Integer> fields = new HashMap<String, Integer>();

    public AdminCompleteInstallPopup(Elements elements, Constants constants, I18NAccount messages, String currentId) {
        this.constants = constants;
        this.messages = messages;
        this.currentId = currentId;
        setText(elements.admin_complete_install());

        table = new AccountTable("tableborder");

        int row = 0;
        table.setText(row++, 0, elements.id());
        addTextfield(row, "username");
        table.setText(row++, 0, elements.user());
        addTextfield(row, "password");
        table.setText(row++, 0, elements.password());
        addTextfield(row, "clubname");
        table.setText(row++, 0, elements.clubname());
        addTextfield(row, "contact");
        table.setText(row++, 0, elements.contact());
        addTextfield(row, "email");
        table.setText(row++, 0, elements.email());
        addTextfield(row, "address");
        table.setText(row++, 0, elements.address());
        addTextfield(row, "postnmb");
        table.setText(row++, 0, elements.postnmb());
        addTextfield(row, "city");
        table.setText(row++, 0, elements.city());
        addTextfield(row, "phone");
        table.setText(row++, 0, elements.phone());
        VerticalPanel vp = new VerticalPanel();
        vp.add(table);
        infoLabel = new Label();
        vp.add(infoLabel);

        HorizontalPanel buttonrow = new HorizontalPanel();

        updateButton = new NamedButton("update", elements.update());
        updateButton.addClickHandler(this);

        completeButton = new NamedButton("complete", elements.complete());
        completeButton.addClickHandler(this);

        cancelButton = new NamedButton("cancel", elements.cancel());
        cancelButton.addClickHandler(this);

        buttonrow.add(updateButton);
        buttonrow.add(completeButton);
        buttonrow.add(cancelButton);
        vp.add(buttonrow);
        loadDetails();
        setWidget(vp);

    }

    private void addTextfield(int row, String field) {
        fields.put(field, row);
        table.setWidget(row, 1, new TextBoxWithErrorText(field));
    }

    private void loadDetails() {
        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONObject obj = responseObj.isObject();

                table.setText(0, 1, Util.str(obj.get("id")));

                for (String key : obj.keySet()) {
                    if (key.equals("id")) {
                        continue;
                    }
                    Integer row = fields.get(key);

                    if (row == null) {
                        Util.log("Fant ikke " + key);
                        continue;
                    }

                    TextBoxWithErrorText box = (TextBoxWithErrorText) table.getWidget(row, 1);
                    box.setText(Util.strSkipNull(obj.get(key)));
                }

            }
        };
        AuthResponder.get(constants, messages, callback, "admin/installs.php?action=install_details&id="
                + this.currentId);

    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == cancelButton) {
            hide();
        }

        if (event.getSource() == updateButton) {
            update();
        }

        if (event.getSource() == completeButton) {
            complete();
        }
    }

    private void complete() {
        
        boolean cont = Window.confirm(messages.confirm_complete_install());
        
        if(!cont) {
            return;
        }
        
        
        JSONObject obj = buildJSON();

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                infoLabel.setText(messages.install_complete());
            }
        };
        StringBuffer parameters = new StringBuffer();
        parameters.append("action=complete_install");

        Util.addPostParam(parameters, "id", currentId);
        Util.addPostParam(parameters, "data", obj.toString());

        AuthResponder.post(constants, messages, callback, parameters, "admin/installs.php");

    }

    private void update() {
        JSONObject obj = buildJSON();

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                infoLabel.setText(messages.save_ok());
            }
        };
        StringBuffer parameters = new StringBuffer();
        parameters.append("action=update_info");

        Util.addPostParam(parameters, "id", currentId);
        Util.addPostParam(parameters, "data", obj.toString());

        AuthResponder.post(constants, messages, callback, parameters, "admin/installs.php");

    }

    private JSONObject buildJSON() {
        JSONObject obj = new JSONObject();

        Set<Entry<String, Integer>> set = fields.entrySet();

        for (Entry<String, Integer> entry : set) {
            String key = entry.getKey();
            int row = entry.getValue();

            TextBoxWithErrorText textbox = (TextBoxWithErrorText) table.getWidget(row, 1);

            obj.put(key, new JSONString(textbox.getText()));
        }
        return obj;
    }

}
