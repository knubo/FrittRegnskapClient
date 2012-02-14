package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedCheckBox;
import no.knubo.accounting.client.ui.NamedRadioButton;
import no.knubo.accounting.client.ui.TextAreaWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RequestDeleteView extends Composite implements ClickHandler {
    private static RequestDeleteView instance;
    private final Constants constants;
    private final I18NAccount messages;
    private TextAreaWithErrorText reason;
    private NamedRadioButton deleteAll;
    private NamedRadioButton deleteSome;
    private NamedCheckBox deleteAccountingData;
    private NamedCheckBox deletePeopleMembers;
    private NamedButton deleteButton;

    public RequestDeleteView(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
        this.messages = messages;

        VerticalPanel vp = new VerticalPanel();

        HTML intro = new HTML(elements.delete_intro());
        intro.setWidth("500px");
        intro.addStyleName("airBottom");
        vp.add(intro);

        deleteSome = new NamedRadioButton("choose_delete", elements.delete_some());
        deleteSome.addClickHandler(this);
        vp.add(deleteSome);

        deleteAccountingData = new NamedCheckBox("delete_accounting_data");
        deletePeopleMembers = new NamedCheckBox("delete_people_and_members");

        deleteAccountingData.addClickHandler(this);
        deletePeopleMembers.addClickHandler(this);

        vp.add(createCheckbox(elements.delete_accounting_data(), deleteAccountingData));
        vp.add(createCheckbox(elements.delete_people_and_members(), deletePeopleMembers));

        deleteAll = new NamedRadioButton("choose_delete", elements.delete_all());
        deleteAll.addClickHandler(this);
        vp.add(deleteAll);

        Label reasonLabel = new Label(elements.delete_reason());
        reasonLabel.addStyleName("airTop");
        vp.add(reasonLabel);
        reason = new TextAreaWithErrorText("delete_reason");
        vp.add(reason);

        deleteButton = new NamedButton("delete_request", elements.delete_request());
        deleteButton.addClickHandler(this);

        deleteButton.setEnabled(false);
        deletePeopleMembers.setEnabled(false);
        deleteAccountingData.setEnabled(false);

        vp.add(deleteButton);

        initWidget(vp);

    }

    private HorizontalPanel createCheckbox(String title, NamedCheckBox checkbox) {
        HorizontalPanel hp = new HorizontalPanel();
        hp.addStyleName("deleteBox");
        NamedCheckBox deleteAccountingData = checkbox;

        hp.add(deleteAccountingData);
        hp.add(new Label(title));
        return hp;
    }

    public static RequestDeleteView getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (instance == null) {
            instance = new RequestDeleteView(constants, messages, elements);
        }
        return instance;
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == deleteButton) {
            requestDelete();
        }

        if (event.getSource() == deleteAll) {
            deleteAccountingData.setValue(false);
            deletePeopleMembers.setValue(false);
            deleteButton.setEnabled(true);
            deletePeopleMembers.setEnabled(false);
            deleteAccountingData.setEnabled(false);
        }

        if (event.getSource() == deleteSome) {
            deleteButton.setEnabled(true);
            deletePeopleMembers.setEnabled(true);
            deleteAccountingData.setEnabled(true);
        }
    }

    private void requestDelete() {
        MasterValidator mv = new MasterValidator();

        boolean fail = deleteSome.getValue() && !deletePeopleMembers.getValue() && !deleteAccountingData.getValue();
        mv.fail(deleteSome, fail, messages.select_at_least_one());
        mv.mandatory(messages.required_field(), reason);

        if (!mv.validateStatus()) {
            return;
        }

        boolean okDelete = Window.confirm(messages.delete_confirm());

        if (!okDelete) {
            return;
        }

        sendDeleteRequest();

    }

    private void sendDeleteRequest() {
        StringBuffer parameters = new StringBuffer();

        parameters.append("deleteAccountingData=" + (deleteAccountingData.getValue() ? "1" : "0"));
        Util.addPostParam(parameters, "deletePeopleMembers", deletePeopleMembers.getValue() ? "1" : "0");
        Util.addPostParam(parameters, "deleteAll", deleteAll.getValue() ? "1" : "0");
        Util.addPostParam(parameters, "deleteSome", deleteSome.getValue() ? "1" : "0");
        Util.addPostParam(parameters, "reason", reason.getText());

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                Window.alert(messages.delete_request_sent());
            }
        };
        AuthResponder.post(constants, messages, callback, parameters, "accounting/delete.php");
    }

}
