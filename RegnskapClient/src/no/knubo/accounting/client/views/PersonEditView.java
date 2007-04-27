package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class PersonEditView extends Composite implements ClickListener {

    String currentId;

    private static PersonEditView me;

    private final ViewCallback caller;

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

    private Button updateButton;

    public PersonEditView(ViewCallback caller, I18NAccount messages,
            Constants constants) {
        this.caller = caller;
        this.messages = messages;
        this.constants = constants;

        DockPanel dp = new DockPanel();
        FlexTable table = new FlexTable();
        table.setStyleName("edittable");

        dp.add(table, DockPanel.NORTH);

        table.setText(0, 0, messages.firstname());
        table.setText(1, 0, messages.lastname());
        table.setText(2, 0, messages.email());
        table.setText(3, 0, messages.address());
        table.setText(4, 0, messages.postnmb());
        table.setText(5, 0, messages.city());
        table.setText(6, 0, messages.country());
        table.setText(7, 0, messages.phone());
        table.setText(8, 0, messages.cellphone());
        table.setText(9, 0, messages.employee());

        firstnameBox = new TextBoxWithErrorText();
        firstnameBox.setMaxLength(50);
        lastnameBox = new TextBoxWithErrorText();
        lastnameBox.setMaxLength(50);
        emailBox = new TextBoxWithErrorText();
        emailBox.setMaxLength(100);
        addressBox = new TextBoxWithErrorText();
        addressBox.setMaxLength(80);
        postnmbBox = new TextBoxWithErrorText();
        postnmbBox.setMaxLength(4);
        cityBox = new TextBoxWithErrorText();
        cityBox.setMaxLength(13);
        countryListBox = new ListBox();
        countryListBox.setVisibleItemCount(1);
        countryListBox.addItem(messages.country_norway(), "NO");
        countryListBox.addItem(messages.country_sweeden(), "SE");
        phoneBox = new TextBoxWithErrorText();
        phoneBox.setMaxLength(13);
        cellphoneBox = new TextBoxWithErrorText();
        cellphoneBox.setMaxLength(13);
        employeeCheck = new CheckBox();

        updateButton = new Button(messages.update());

        table.setWidget(0, 1, firstnameBox);
        table.setWidget(1, 1, lastnameBox);
        table.setWidget(2, 1, emailBox);
        table.setWidget(3, 1, addressBox);
        table.setWidget(4, 1, postnmbBox);
        table.setWidget(5, 1, cityBox);
        table.setWidget(6, 1, countryListBox);
        table.setWidget(7, 1, phoneBox);
        table.setWidget(8, 1, cellphoneBox);
        table.setWidget(9, 1, employeeCheck);
        table.setWidget(10, 0, updateButton);

        initWidget(dp);
    }

    public static PersonEditView show(Constants constants,
            I18NAccount messages, ViewCallback caller) {
        if (me == null) {
            me = new PersonEditView(caller, messages, constants);
        }
        return me;
    }

    public void onClick(Widget sender) {
    }

    public void init(String currentId) {
        this.currentId = currentId;
        
        if (currentId == null) {
            updateButton.setHTML(messages.save());
        } else {
            updateButton.setHTML(messages.update());
        }
    }

}
