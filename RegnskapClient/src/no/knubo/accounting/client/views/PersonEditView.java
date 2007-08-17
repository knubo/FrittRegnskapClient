package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.NamedButton;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class PersonEditView extends Composite implements ClickListener {

    String currentId;

    private static PersonEditView me;

    private final I18NAccount messages;

    private final Constants constants;

    private TextBoxWithErrorText firstnameBox;

    private TextBoxWithErrorText lastnameBox;

    private TextBoxWithErrorText emailBox;

    private TextBoxWithErrorText postnmbBox;

    private TextBoxWithErrorText cityBox;

    private ListBox countryListBox;

    private TextBoxWithErrorText phoneBox;

    private TextBoxWithErrorText cellphoneBox;

    private CheckBox employeeCheck;

    private TextBoxWithErrorText addressBox;

    private HTML saveStatus;

    private Button updateButton;

    private TextBoxWithErrorText birthdateBox;

    private CheckBox newsletterCheck;

    private final HelpPanel helpPanel;

    public PersonEditView(I18NAccount messages, Constants constants,
            HelpPanel helpPanel) {
        this.messages = messages;
        this.constants = constants;
        this.helpPanel = helpPanel;

        DockPanel dp = new DockPanel();
        FlexTable table = new FlexTable();
        table.setStyleName("edittable");

        dp.add(table, DockPanel.NORTH);

        table.setHTML(0, 0, messages.firstname());
        table.setHTML(1, 0, messages.lastname());
        table.setHTML(2, 0, messages.birthdate());
        table.setHTML(3, 0, messages.email());
        table.setHTML(4, 0, messages.address());
        table.setHTML(5, 0, messages.postnmb());
        table.setHTML(6, 0, messages.city());
        table.setHTML(7, 0, messages.country());
        table.setHTML(8, 0, messages.phone());
        table.setHTML(9, 0, messages.cellphone());
        table.setHTML(10, 0, messages.employee());
        table.setHTML(11, 0, messages.newsletter());

        firstnameBox = new TextBoxWithErrorText("firstname");
        firstnameBox.setMaxLength(50);
        firstnameBox.setVisibleLength(50);
        lastnameBox = new TextBoxWithErrorText("lastname");
        lastnameBox.setMaxLength(50);
        lastnameBox.setVisibleLength(50);
        birthdateBox = new TextBoxWithErrorText("birthdate");
        birthdateBox.setMaxLength(10);

        emailBox = new TextBoxWithErrorText("email");
        emailBox.setMaxLength(100);
        emailBox.setVisibleLength(100);
        addressBox = new TextBoxWithErrorText("address");
        addressBox.setMaxLength(80);
        addressBox.setVisibleLength(80);
        postnmbBox = new TextBoxWithErrorText("postalnumber");
        postnmbBox.setMaxLength(4);
        cityBox = new TextBoxWithErrorText("city");
        cityBox.setMaxLength(13);
        countryListBox = new ListBox();
        countryListBox.setVisibleItemCount(1);
        countryListBox.addItem(messages.country_norway(), "NO");
        countryListBox.addItem(messages.country_sweeden(), "SE");
        countryListBox.addItem(messages.country_denmark(), "DK");
        countryListBox.addItem(messages.country_finland(), "FI");
        countryListBox.addItem(messages.country_other(), "??");
        phoneBox = new TextBoxWithErrorText("phone");
        phoneBox.setMaxLength(13);
        cellphoneBox = new TextBoxWithErrorText("cellphone");
        cellphoneBox.setMaxLength(13);
        employeeCheck = new CheckBox();
        newsletterCheck = new CheckBox();

        updateButton = new NamedButton("PersonEditView.updateButton", messages
                .update());
        updateButton.addClickListener(this);

        saveStatus = new HTML();

        table.setWidget(0, 1, firstnameBox);
        table.setWidget(1, 1, lastnameBox);
        table.setWidget(2, 1, birthdateBox);
        table.setWidget(1, 1, lastnameBox);
        table.setWidget(3, 1, emailBox);
        table.setWidget(4, 1, addressBox);
        table.setWidget(5, 1, postnmbBox);
        table.setWidget(6, 1, cityBox);
        table.setWidget(7, 1, countryListBox);
        table.setWidget(8, 1, phoneBox);
        table.setWidget(9, 1, cellphoneBox);
        table.setWidget(10, 1, employeeCheck);
        table.setWidget(11, 1, newsletterCheck);
        table.setWidget(12, 0, updateButton);
        table.setWidget(12, 1, saveStatus);
        initWidget(dp);
    }

    public static PersonEditView show(Constants constants,
            I18NAccount messages, HelpPanel helpPanel) {
        if (me == null) {
            me = new PersonEditView(messages, constants, helpPanel);
        }
        return me;
    }

    public void onClick(Widget sender) {
        if (sender == updateButton) {
            doSave();
        }
    }

    private void doOpen() {
        StringBuffer sb = new StringBuffer();

        sb.append("action=get");
        Util.addPostParam(sb, "id", currentId);

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                constants.baseurl() + "registers/persons.php");

        ServerResponse callback = new ServerResponse() {
            public void serverResponse(String serverResponse) {
                JSONValue value = JSONParser.parse(serverResponse);

                if (value == null) {
                    Window.alert("Failed to load person");
                    return;
                }
                JSONObject object = value.isObject();

                if (object == null) {
                    Window.alert("Failed to load person");
                    return;
                }

                firstnameBox.setText(Util.str(object.get("FirstName")));
                lastnameBox.setText(Util.str(object.get("LastName")));
                birthdateBox.setText(Util.str(object.get("Birthdate")));
                addressBox.setText(Util.str(object.get("Address")));
                postnmbBox.setText(Util.str(object.get("PostNmb")));
                cityBox.setText(Util.str(object.get("City")));
                phoneBox.setText(Util.str(object.get("Phone")));
                cellphoneBox.setText(Util.str(object.get("Cellphone")));
                Util.setIndexByValue(countryListBox, Util.str(object
                        .get("Country")));
                emailBox.setText(Util.str(object.get("Email")));
                employeeCheck.setChecked("1".equals(Util.str(object
                        .get("IsEmployee"))));
                newsletterCheck.setChecked("1".equals(Util.str(object
                        .get("Newsletter"))));
            }
        };

        try {
            builder.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            builder.sendRequest(sb.toString(), new AuthResponder(constants,
                    messages, callback));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }

    }

    private void doSave() {
        if (!validateSave()) {
            return;
        }

        StringBuffer sb = new StringBuffer();

        sb.append("action=save");
        Util.addPostParam(sb, "id", currentId);
        Util.addPostParam(sb, "firstname", firstnameBox.getText());
        Util.addPostParam(sb, "birthdate", birthdateBox.getText());
        Util.addPostParam(sb, "lastname", lastnameBox.getText());
        Util.addPostParam(sb, "email", emailBox.getText());
        Util.addPostParam(sb, "address", addressBox.getText());
        Util.addPostParam(sb, "postnmb", postnmbBox.getText());
        Util.addPostParam(sb, "city", cityBox.getText());
        Util.addPostParam(sb, "country", Util.getSelected(countryListBox));
        Util.addPostParam(sb, "phone", phoneBox.getText());
        Util.addPostParam(sb, "cellphone", cellphoneBox.getText());
        String isChecked = employeeCheck.isChecked() ? "1" : "0";
        Util.addPostParam(sb, "employee", isChecked);
        String newsletter = newsletterCheck.isChecked() ? "1" : "0";
        Util.addPostParam(sb, "newsletter", newsletter);

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                constants.baseurl() + "registers/persons.php");

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(String serverResponse) {
                int id = Util.getInt(serverResponse);

                if (id == 0) {
                    if ("0".equals(serverResponse)) {
                        saveStatus.setText(messages.save_failed());
                    } else {
                        Window.alert("Server error:" + serverResponse);
                    }
                } else {
                    saveStatus.setText(messages.save_ok());
                    if (currentId == null) {
                        currentId = serverResponse;
                        updateButton.setHTML(messages.update());
                    }
                }
                Util.timedMessage(saveStatus, "", 5);
            }
        };

        try {
            builder.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            builder.sendRequest(sb.toString(), new AuthResponder(constants,
                    messages, callback));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
    }

    public void init(String currentId) {
        this.currentId = currentId;

        if (currentId == null) {
            firstnameBox.setText("");
            lastnameBox.setText("");
            emailBox.setText("");
            addressBox.setText("");
            postnmbBox.setText("");
            cityBox.setText("");
            countryListBox.setSelectedIndex(0);
            phoneBox.setText("");
            cellphoneBox.setText("");
            employeeCheck.setChecked(false);
            newsletterCheck.setChecked(false);
            updateButton.setHTML(messages.save());
        } else {
            doOpen();
            updateButton.setHTML(messages.update());
        }
        helpPanel.resize(this);
    }

    private boolean validateSave() {
        MasterValidator masterValidator = new MasterValidator();

        masterValidator.mandatory(messages.required_field(), new Widget[] {
                lastnameBox, firstnameBox });

        masterValidator.date(messages.date_format(),
                new Widget[] { birthdateBox });

        masterValidator.email(messages.invalid_email(),
                new Widget[] { emailBox });

        return masterValidator.validateStatus();
    }
}
