package no.knubo.accounting.client.views;

import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.cache.ProjectCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.ServerResponseWithValidation;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.modules.RegisterStandards;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MassRegisterView extends Composite implements ClickHandler {

    private static MassRegisterView me;
    private final I18NAccount messages;
    private final Constants constants;
    private final Elements elements;
    private AccountTable table;
    private Label infoLabel;
    private ListBoxWithErrorText projectListBox;
    private ListBoxWithErrorText debetListBox;
    private ListBoxWithErrorText creditListBox;
    private ProjectCache projectCache;
    private HTML projectErrorLabel;
    private TextBoxWithErrorText projectIdBox;
    private HTML debetErrorLabel;
    private TextBoxWithErrorText debetBox;
    private HTML creditErrorLabel;
    private TextBoxWithErrorText creditBox;
    private AccountTable linetable;
    private RegisterStandards standards;
    private TextBoxWithErrorText amountBox;
    private TextBoxWithErrorText dayBox;
    private TextBoxWithErrorText descriptionBox;
    private TextBoxWithErrorText attachmentBox;
    private NamedButton addButton;
    private TextBoxWithErrorText postnmbbox;
    private PosttypeCache postTypeCache;

    public static MassRegisterView show(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new MassRegisterView(messages, constants, elements);
        }
        return me;
    }

    public MassRegisterView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;

        table = new AccountTable("edittable");

        table.setText(0, 0, elements.project());
        table.setText(1, 0, elements.debet());
        table.setText(2, 0, elements.kredit());
        table.setText(3, 0, elements.amount());

        projectErrorLabel = new HTML();
        projectIdBox = new TextBoxWithErrorText("project", projectErrorLabel);
        projectIdBox.setVisibleLength(6);
        table.setWidget(0, 1, projectIdBox);
        projectListBox = new ListBoxWithErrorText("project", projectErrorLabel);
        table.setWidget(0, 2, projectListBox);
        table.setWidget(0, 3, projectErrorLabel);
        Util.syncListbox(projectListBox.getListbox(), projectIdBox.getTextBox());

        debetErrorLabel = new HTML();
        debetBox = new TextBoxWithErrorText("account", debetErrorLabel);
        debetBox.setVisibleLength(6);
        debetListBox = new ListBoxWithErrorText("debet", debetErrorLabel);
        table.setWidget(1, 1, debetBox);
        table.setWidget(1, 2, debetListBox);
        table.setWidget(1, 3, debetErrorLabel);

        standards = new RegisterStandards(constants, messages, elements);
        amountBox = standards.createAmountBox();

        table.setWidget(3, 1, amountBox);
        table.setColSpanAndRowStyle(3, 1, 3, "");
        Util.syncListbox(debetListBox.getListbox(), debetBox.getTextBox());

        creditErrorLabel = new HTML();
        creditBox = new TextBoxWithErrorText("account", creditErrorLabel);
        creditBox.setVisibleLength(6);
        creditListBox = new ListBoxWithErrorText("credit");
        table.setWidget(2, 1, creditBox);
        table.setWidget(2, 2, creditListBox);
        table.setWidget(2, 3, creditErrorLabel);
        Util.syncListbox(creditListBox.getListbox(), creditBox.getTextBox());

        projectCache = ProjectCache.getInstance(constants, messages);
        projectCache.fill(projectListBox.getListbox());

        postTypeCache = PosttypeCache.getInstance(constants, messages);
        postTypeCache.fillAllPosts(debetListBox);
        postTypeCache.fillAllPosts(creditListBox);

        linetable = new AccountTable("tableborder");

        linetable.setText(0, 0, elements.postnmb());
        linetable.setText(0, 1, elements.attachment());
        linetable.setText(0, 2, elements.day());
        linetable.setText(0, 3, elements.description());
        linetable.setText(0, 4, "");

        dayBox = standards.createDayBox();
        linetable.setWidget(1, 2, dayBox);

        postnmbbox = standards.getPostNmbBox();

        linetable.setWidget(1, 0, postnmbbox);

        attachmentBox = standards.getAttachmentBox();
        linetable.setWidget(1, 1, attachmentBox);

        descriptionBox = standards.createDescriptionBox();
        linetable.setWidget(1, 3, descriptionBox);

        addButton = new NamedButton("add_button", elements.add());
        addButton.addStyleName("nowrap");
        addButton.addClickHandler(this);
        linetable.setWidget(1, 4, addButton);

        VerticalPanel vp = new VerticalPanel();

        infoLabel = standards.getDateHeader();
        infoLabel.setText("...");
        vp.add(infoLabel);
        vp.add(table);
        vp.add(linetable);

        initWidget(vp);
    }

    public void init() {
        standards.fetchInitalData(true);
        projectIdBox.setFocus(true);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == addButton) {
            addLine();
        }
    }

    private void addLine() {
        if (!validate()) {
            return;
        }

        sendData();

    }

    private void sendData() {
        StringBuffer parameters = new StringBuffer();
        parameters.append("action=save");
        final String day = dayBox.getText();
        Util.addPostParam(parameters, "day", day);
        Util.addPostParam(parameters, "amount", amountBox.getText());
        final String desc = descriptionBox.getText();
        Util.addPostParam(parameters, "desc", desc);
        final String attachnment = attachmentBox.getText();
        Util.addPostParam(parameters, "attachment", attachnment);
        Util.addPostParam(parameters, "debet", debetBox.getText());
        Util.addPostParam(parameters, "kredit", creditBox.getText());
        Util.addPostParam(parameters, "project", projectIdBox.getText());
        final String postnmb = postnmbbox.getText();
        Util.addPostParam(parameters, "postnmb", postnmb);
        
        ServerResponse callback = new ServerResponseWithValidation() {
            
            public void serverResponse(JSONValue responseObj) {
                JSONObject object = responseObj.isObject();
              
                dayBox.setText("");
                descriptionBox.setText("");
                dayBox.setFocus(true);
                
                int row = linetable.getRowCount();
                linetable.insertRow(row);
                linetable.alternateStyle(row, 0);
                linetable.setText(row, 0, postnmb);
                linetable.setText(row, 1, attachnment);
                linetable.setText(row, 2, day);
                linetable.setText(row, 3, desc);
                
                standards.fillFields(object);
            }
            
            public void validationError(List<String> fields) {
                MasterValidator masterValidator = new MasterValidator();

                masterValidator.mandatory(messages.required_field(), projectIdBox);

                masterValidator.validateStatus();
            }
        };
        
        AuthResponder.post(constants, messages, callback, parameters, "accounting/addMassregister.php");
    }

    private boolean validate() {
        if (!standards.validateTop()) {
            return false;
        }
        MasterValidator mv = new MasterValidator();

        mv.mandatory(messages.required_field(), amountBox, postnmbbox, attachmentBox, dayBox, descriptionBox);
        mv.range(messages.field_to_low_zero(), 0, Integer.MAX_VALUE, postnmbbox, attachmentBox);
        mv.day(messages.illegal_day(), standards.getCurrentYear(), standards.getCurrentMonth(), dayBox);
        mv.money(messages.field_money(), amountBox);
        mv.registry(messages.registry_invalid_key(), projectCache, projectIdBox);
        mv.registry(messages.registry_invalid_key(), postTypeCache, debetBox, creditBox);

        if (!mv.validateStatus()) {
            return false;
        }

        return true;

    }
}
