package no.knubo.accounting.client.views.registers;

import java.util.HashMap;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.BlinkImage;
import no.knubo.accounting.client.misc.HTMLWithError;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.ServerResponseWithValidation;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedTextArea;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.ViewCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class PersonEditView extends Composite implements ClickHandler, KeyUpHandler {

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
    private HTMLWithError saveStatus;
    private CheckBox newsletterCheck;
    private CheckBox secretaddressCheck;
    private TextBoxWithErrorText birthdateBox;
    private NamedTextArea commentBox;
    private ListBox genderBox;
    private Button updateButton;

    private final HelpPanel helpPanel;
    private CheckBox hiddenCheck;

    private FlexTable membershipsTable;

    private IdHolder<String, Image> deleteIdHolder;

    private final Elements elements;

    private boolean birthdateRequired;

    private Image addressSearch;

    private BlinkImage addressInfo;

    public PersonEditView(I18NAccount messages, Constants constants, HelpPanel helpPanel, final ViewCallback caller,
            Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.helpPanel = helpPanel;
        this.elements = elements;

        DockPanel dp = new DockPanel();
        FlexTable table = new FlexTable();
        table.setStyleName("edittable");

        membershipsTable = new FlexTable();
        membershipsTable.setStyleName("tableborder");

        deleteIdHolder = new IdHolder<String, Image>();

        dp.add(table, DockPanel.NORTH);
        dp.add(membershipsTable, DockPanel.NORTH);

        table.setHTML(0, 0, elements.firstname());
        table.setHTML(1, 0, elements.lastname());
        table.setHTML(2, 0, elements.birthdate());
        table.setHTML(3, 0, elements.email());
        table.setHTML(4, 0, elements.address());
        table.setHTML(5, 0, elements.postnmb());
        table.setHTML(6, 0, elements.city());
        table.setHTML(7, 0, elements.country());
        table.setHTML(8, 0, elements.phone());
        table.setHTML(9, 0, elements.cellphone());
        table.setHTML(10, 0, elements.newsletter());
        table.setHTML(11, 0, elements.gender());
        table.setHTML(12, 0, elements.secret_address());
        table.setHTML(13, 0, elements.comment());
        table.setHTML(14, 0, elements.employee());
        table.setHTML(15, 0, elements.hidden_person());

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
        addressBox.getTextBox().addKeyUpHandler(this);

        postnmbBox = new TextBoxWithErrorText("postalnumber");
        postnmbBox.setMaxLength(4);
        cityBox = new TextBoxWithErrorText("city");
        cityBox.setMaxLength(13);
        countryListBox = new ListBox();
        countryListBox.setVisibleItemCount(1);
        countryListBox.addItem(elements.country_norway(), "NO");
        countryListBox.addItem(elements.country_sweeden(), "SE");
        countryListBox.addItem(elements.country_denmark(), "DK");
        countryListBox.addItem(elements.country_finland(), "FI");
        countryListBox.addItem(elements.country_other(), "??");
        phoneBox = new TextBoxWithErrorText("phone");
        phoneBox.setMaxLength(13);
        cellphoneBox = new TextBoxWithErrorText("cellphone");
        cellphoneBox.setMaxLength(13);

        commentBox = new NamedTextArea("comment");
        commentBox.setVisibleLines(5);
        commentBox.setCharacterWidth(60);

        genderBox = new ListBox();
        genderBox.addItem("", "");
        genderBox.addItem(elements.gender_male(), "M");
        genderBox.addItem(elements.gender_female(), "F");

        employeeCheck = new CheckBox();
        newsletterCheck = new CheckBox();
        hiddenCheck = new CheckBox();
        secretaddressCheck = new CheckBox();

        updateButton = new NamedButton("PersonEditView.updateButton", elements.update());
        updateButton.addClickHandler(this);

        saveStatus = new HTMLWithError();

        addressSearch = ImageFactory.searchImage("findAddress");
        addressSearch.addClickHandler(this);

        Anchor toSearch = new Anchor(elements.back_search());
        toSearch.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                caller.searchPerson();
            }

        });

        table.setWidget(0, 1, firstnameBox);
        table.setWidget(1, 1, lastnameBox);
        table.setWidget(2, 1, birthdateBox);
        table.setWidget(1, 1, lastnameBox);
        table.setWidget(3, 1, emailBox);

        addressInfo = ImageFactory.lighbulb("addressStatus");
        addressInfo.setVisible(false);
        addressInfo.addClickHandler(this);

        HorizontalPanel fp = new HorizontalPanel();
        fp.add(addressBox);
        fp.add(addressSearch);
        fp.add(addressInfo);
        table.setWidget(4, 1, fp);
        table.setWidget(5, 1, postnmbBox);
        table.setWidget(6, 1, cityBox);
        table.setWidget(7, 1, countryListBox);
        table.setWidget(8, 1, phoneBox);
        table.setWidget(9, 1, cellphoneBox);
        table.setWidget(10, 1, newsletterCheck);
        table.setWidget(11, 1, genderBox);
        table.setWidget(12, 1, secretaddressCheck);
        table.setWidget(13, 1, commentBox);
        table.setWidget(14, 1, employeeCheck);
        table.setWidget(15, 1, hiddenCheck);
        table.setWidget(16, 0, updateButton);
        table.setWidget(17, 1, saveStatus);
        table.setWidget(18, 0, toSearch);
        initWidget(dp);
    }

    public static PersonEditView show(Constants constants, I18NAccount messages, HelpPanel helpPanel,
            ViewCallback caller, Elements elements) {
        if (me == null) {
            me = new PersonEditView(messages, constants, helpPanel, caller, elements);
        }
        return me;
    }

    public void onClick(ClickEvent event) {
        Widget sender = (Widget) event.getSource();

        if (sender == updateButton) {
            doSave();
            return;
        }

        if (sender == addressInfo) {
            copyStreetFromTitle();
            return;
        }

        if (sender == addressSearch) {
            searchAddressDisplayPopupIfMultile();
            return;
        }

        String deleteId = deleteIdHolder.findId(sender);

        if (deleteId != null) {
            doDeleteMembership(deleteId);
        }
    }

    private void copyStreetFromTitle() {
        if (addressInfo.getTitle().startsWith(elements.selected())) {
            addressBox.setText(addressInfo.getTitle().substring(elements.selected().length() + 2));
        }
    }

    private void searchAddressDisplayPopupIfMultile() {
        String address = addressBox.getText();

        if (address.length() == 0) {
            return;
        }

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                pickOrDisplay(responseObj.isArray());
            }
        };
        AuthResponder.get(constants, messages, callback, "registers/cities.php?street=" + address);
    }

    protected void pickOrDisplay(JSONArray array) {
        addressInfo.setVisible(true);

        String title = null;
        if (array.size() == 1) {

            JSONObject cityRow = array.get(0).isObject();

            cityBox.setText(Util.str(cityRow.get("city")));
            postnmbBox.setText(Util.str(cityRow.get("zipcode")));
            title = elements.search() + ":" + Util.str(cityRow.get("street"));

            addressInfo.blinkTwo();

        } else if (array.size() == 0) {
            title = messages.no_result();

            addressInfo.blinkOne();
        } else {
            title = "";
            new PickCityPopup(this, array, addressInfo);
            addressInfo.blinkTwo();
        }
        addressInfo.setTitle(title);
        addressBox.setTitle(title);
    }

    private void doDeleteMembership(String deleteId) {
        boolean confirm = Window.confirm(messages.confirm_delete());

        if (!confirm) {
            return;
        }

        if (deleteId.startsWith("year")) {
            String year = deleteId.substring(4);
            sendDeleteMessage("year=" + year + "&action=deleteyear");
        } else if (deleteId.startsWith("train")) {
            String semester = deleteId.substring(5);
            sendDeleteMessage("semester=" + semester + "&action=deletetrain");

        } else if (deleteId.startsWith("course")) {
            String semester = deleteId.substring(6);
            sendDeleteMessage("semester=" + semester + "&action=deletecourse");

        }

    }

    private void sendDeleteMessage(String deletemessage) {
        String toSend = deletemessage + "&personId=" + currentId;

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {
                JSONObject object = value.isObject();

                if (object == null) {
                    Window.alert(messages.save_failed_badly());
                    return;
                }

                if ("0".equals(Util.str(object.get("result")))) {
                    Window.alert(messages.save_failed_badly());
                } else {
                    init(currentId);
                }
            }
        };

        AuthResponder.get(constants, messages, callback, "registers/members.php?" + toSend);
    }

    private void doOpen() {
        StringBuffer sb = new StringBuffer();

        sb.append("action=get");
        Util.addPostParam(sb, "id", currentId);

        ServerResponse callback = new ServerResponse() {
            public void serverResponse(JSONValue value) {
                JSONObject object = value.isObject();

                if (object == null) {
                    Window.alert("Failed to load person");
                    return;
                }
                setPersonData(object);
                showMemberships(object.get("Memberships"));
            }

        };

        AuthResponder.post(constants, messages, callback, sb, "registers/persons.php");

    }

    protected void showMemberships(JSONValue value) {
        JSONObject obj = value.isObject();

        if (obj == null) {
            return;
        }

        showMemberships("train", elements.train_membership(), obj.get("train").isArray());
        showMemberships("course", elements.course_membership(), obj.get("course").isArray());
        showMemberships("youth", elements.youth_membership(), obj.get("youth").isArray());
        showMemberships("year", elements.year_membership(), obj.get("year").isArray());
    }

    private void showMemberships(String type, String title, JSONArray memberships) {

        if (memberships.size() == 0) {
            return;
        }

        int rows = membershipsTable.getRowCount();
        membershipsTable.setText(rows, 0, title);
        membershipsTable.getFlexCellFormatter().setColSpan(rows, 0, 2);
        membershipsTable.getRowFormatter().setStyleName(rows, "header");

        rows++;

        for (int i = 0; i < memberships.size(); i++) {
            JSONObject obj = memberships.get(i).isObject();

            int row = rows + i;

            Image deleteImage = ImageFactory.deleteImage("personeditview.deleteImage");
            deleteImage.addClickHandler(this);
            membershipsTable.setWidget(row, 1, deleteImage);

            if (obj.containsKey("Text")) {
                membershipsTable.setHTML(row, 0, Util.str(obj.get("Text")));
                deleteIdHolder.add(type + obj.get("Semester"), deleteImage);
            } else {
                membershipsTable.setHTML(row, 0, Util.str(obj.get("Year")));
                deleteIdHolder.add(type + obj.get("Year"), deleteImage);
            }

            String style = (row % 2 == 0) ? "showlineposts2" : "showlineposts1";
            membershipsTable.getRowFormatter().setStyleName(row, style);

        }
    }

    void setPersonData(JSONObject object) {
        firstnameBox.setText(Util.strSkipNull(object.get("FirstName")));
        lastnameBox.setText(Util.strSkipNull(object.get("LastName")));
        birthdateBox.setText(Util.strSkipNull(object.get("Birthdate")));
        addressBox.setText(Util.strSkipNull(object.get("Address")));
        postnmbBox.setText(Util.strSkipNull(object.get("PostNmb")));
        cityBox.setText(Util.strSkipNull(object.get("City")));
        phoneBox.setText(Util.strSkipNull(object.get("Phone")));
        cellphoneBox.setText(Util.strSkipNull(object.get("Cellphone")));
        Util.setIndexByValue(countryListBox, Util.str(object.get("Country")));
        emailBox.setText(Util.strSkipNull(object.get("Email")));
        employeeCheck.setValue("1".equals(Util.str(object.get("IsEmployee"))));
        newsletterCheck.setValue("1".equals(Util.str(object.get("Newsletter"))));
        hiddenCheck.setValue("1".equals(Util.str(object.get("Hidden"))));
        Util.setIndexByValue(genderBox, Util.str(object.get("Gender")));

        birthdateRequired = Util.getBoolean(object.get("BirthdateRequired"));

        commentBox.setText(Util.strSkipNull(object.get("Comment")));
        secretaddressCheck.setValue("1".equals(Util.str(object.get("Secretaddress"))));
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
        String isChecked = employeeCheck.getValue() ? "1" : "0";
        Util.addPostParam(sb, "employee", isChecked);
        String newsletter = newsletterCheck.getValue() ? "1" : "0";
        Util.addPostParam(sb, "newsletter", newsletter);
        String hidden = hiddenCheck.getValue() ? "1" : "0";
        Util.addPostParam(sb, "hidden", hidden);
        Util.addPostParam(sb, "gender", Util.getSelected(genderBox));
        Util.addPostParam(sb, "secretaddress", secretaddressCheck.getValue() ? "1" : "0");
        Util.addPostParam(sb, "comment", commentBox.getText());

        ServerResponseWithValidation callback = new ServerResponseWithValidation() {

            public void serverResponse(JSONValue value) {

                JSONObject obj = value.isObject();

                String id = Util.str(obj.get("result"));

                if ("0".equals(id)) {
                    saveStatus.setText(messages.save_failed());
                } else {
                    saveStatus.setText(messages.save_ok());
                    if (currentId == null) {
                        currentId = id;
                        updateButton.setHTML(elements.update());
                    }
                }
                Util.timedMessage(saveStatus, "", 5);
            }

            public void validationError(List<String> fields) {
                HashMap<String, String> translate = new HashMap<String, String>();
                translate.put("email", "epost");

                String fieldtext = Util.translate(fields, translate);

                saveStatus.setError(messages.field_validation_fail(fieldtext));
            }
        };

        AuthResponder.post(constants, messages, callback, sb, "registers/persons.php");
    }

    public void init(String currentId) {
        this.currentId = currentId;

        deleteIdHolder.init();

        while (membershipsTable.getRowCount() > 0) {
            membershipsTable.removeRow(0);
        }

        genderBox.setSelectedIndex(0);

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
            employeeCheck.setValue(false);
            newsletterCheck.setValue(false);
            commentBox.setText("");
            secretaddressCheck.setValue(false);
            updateButton.setHTML(elements.save());
        } else {
            doOpen();
            updateButton.setHTML(elements.update());
        }
        helpPanel.resize(this);
    }

    private boolean validateSave() {
        MasterValidator masterValidator = new MasterValidator();

        masterValidator.mandatory(messages.required_field(), lastnameBox, firstnameBox, genderBox);

        if (birthdateRequired) {
            masterValidator.mandatory(messages.required_field(), birthdateBox);
        }

        masterValidator.date(messages.date_format(), birthdateBox);

        masterValidator.email(messages.invalid_email(), emailBox);

        return masterValidator.validateStatus();
    }

    public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            searchAddressDisplayPopupIfMultile();
        }
    }

    public void cityPicked(String street, String zip, String city) {
        addressInfo.setTitle(elements.selected() + ": " + street);
        if (zip.length() < 4) {
            postnmbBox.setText("0" + zip);

        } else {
            postnmbBox.setText(zip);
        }
        cityBox.setText(city);
    }
}
