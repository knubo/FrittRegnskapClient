package no.knubo.accounting.client.views.modules;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class UserSearchFields implements ClickHandler, KeyPressHandler {
    private TextBox firstnameBox;

    private TextBox lastnameBox;

    private TextBox emailBox;

    private ListBox employeeList;

    private Button searchButton;

    private Button clearButton;

    private FlexTable searchTable;

    private final UserSearchCallback searchCallback;

    private boolean excludeHidden = true;

    private ListBox genderBox;

    private TextBox membernumberBox;

    public UserSearchFields(UserSearchCallback searchCallback, Elements elements) {
        this.searchCallback = searchCallback;
        searchTable = new FlexTable();
        searchTable.setStyleName("edittable");
        firstnameBox = new TextBox();
        firstnameBox.setMaxLength(50);
        firstnameBox.addKeyPressHandler(this);
        lastnameBox = new TextBox();
        lastnameBox.setMaxLength(50);
        lastnameBox.addKeyPressHandler(this);
        membernumberBox = new TextBox();
        membernumberBox.setMaxLength(4);
        membernumberBox.addKeyPressHandler(this);
        
        emailBox = new TextBox();
        emailBox.setMaxLength(100);
        emailBox.addStyleName("fullwidth");
        emailBox.addKeyPressHandler(this);
        employeeList = new ListBox();
        employeeList.setVisibleItemCount(1);
        employeeList.addItem("", "");
        employeeList.addItem(elements.not_employee(), "0");
        employeeList.addItem(elements.employee(), "1");

        searchTable.setText(0, 0, elements.member_number());
        searchTable.setWidget(0, 1, membernumberBox);
        searchTable.setText(0, 2, elements.firstname());
        searchTable.setWidget(0, 3, firstnameBox);
        searchTable.setText(0, 4, elements.lastname());
        searchTable.setWidget(0, 5, lastnameBox);
        searchTable.setText(1, 0, elements.email());
        searchTable.setWidget(1, 1, emailBox);
        searchTable.getFlexCellFormatter().setColSpan(1, 1, 3);
        searchTable.setText(2, 0, elements.employee());
        searchTable.setWidget(2, 1, employeeList);

        genderBox = new ListBox();
        genderBox.addItem("", "");
        genderBox.addItem(elements.gender_male(), "M");
        genderBox.addItem(elements.gender_female(), "F");
        genderBox.addItem(elements.gender_unset(), "U");

        searchTable.setText(2, 2, elements.gender());
        searchTable.setWidget(2, 3, genderBox);

        searchButton = new NamedButton("search", elements.search());
        searchButton.addClickHandler(this);
        searchButton.addKeyPressHandler(this);

        searchTable.setWidget(3, 0, searchButton);
        clearButton = new NamedButton("clear", elements.clear());
        clearButton.addClickHandler(this);
        searchTable.setWidget(3, 1, clearButton);

    }

    public Widget getSearchTable() {
        return searchTable;
    }

    public void onKeyPress(KeyPressEvent event) {
        if (event.getCharCode() == KeyCodes.KEY_ENTER) {
            doSearch();
        }

    }

    public void onClick(ClickEvent event) {
        Widget sender = (Widget) event.getSource();
        if (sender == searchButton) {
            doSearch();
        } else if (sender == clearButton) {
            doClear();
        }
    }

    public void includeHidden() {
        excludeHidden = false;
    }

    private void doSearch() {
        StringBuffer sb = new StringBuffer();

        sb.append("action=search");
        Util.addPostParam(sb, "firstname", firstnameBox.getText());
        Util.addPostParam(sb, "lastname", lastnameBox.getText());
        Util.addPostParam(sb, "employee", Util.getSelected(employeeList));
        Util.addPostParam(sb, "email", emailBox.getText());
        Util.addPostParam(sb, "getmemb", "1");
        Util.addPostParam(sb, "gender", Util.getSelected(genderBox));
        Util.addPostParam(sb, "id", membernumberBox.getText());

        if (excludeHidden) {
            Util.addPostParam(sb, "hidden", "1");
        }
        searchCallback.doSearch(sb);
    }

    private void doClear() {
        firstnameBox.setText("");
        lastnameBox.setText("");
        emailBox.setText("");
        employeeList.setSelectedIndex(0);
        searchCallback.doClear();
        membernumberBox.setText("");
    }

    public void setFocus() {
        membernumberBox.setFocus(true);
    }

}
