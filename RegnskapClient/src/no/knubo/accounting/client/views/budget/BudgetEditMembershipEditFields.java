package no.knubo.accounting.client.views.budget;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BudgetEditMembershipEditFields extends DialogBox implements ClickHandler {
    private Button saveButton;

    private Button cancelButton;

    private HTML mainErrorLabel;

    private I18NAccount messages;

    private Constants constants;

    private Elements elements;

    private TextBoxWithErrorText courseMemberBox;

    private TextBoxWithErrorText trainMemberBox;

    private TextBoxWithErrorText yearMemberBox;

    private FlexTable edittable;

    private BudgetView budgetView;

    private int year;

    private boolean fall;

    BudgetEditMembershipEditFields(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;

        setText(elements.member_heading_all());
        edittable = new FlexTable();
        edittable.setStyleName("edittable");

        courseMemberBox = new TextBoxWithErrorText("course_membership");
        courseMemberBox.setMaxLength(5);
        courseMemberBox.setVisibleLength(5);

        trainMemberBox = new TextBoxWithErrorText("course_membership");
        trainMemberBox.setMaxLength(5);
        trainMemberBox.setVisibleLength(5);

        yearMemberBox = new TextBoxWithErrorText("course_membership");
        yearMemberBox.setMaxLength(5);
        yearMemberBox.setVisibleLength(5);

        edittable.setText(0, 0, elements.semester());
        edittable.setText(1, 0, elements.member_heading_year());
        edittable.setText(2, 0, elements.member_heading_course());
        edittable.setText(3, 0, elements.member_heading_train());

        edittable.setWidget(1, 1, yearMemberBox);
        edittable.setWidget(2, 1, courseMemberBox);
        edittable.setWidget(3, 1, trainMemberBox);

        DockPanel dp = new DockPanel();
        dp.add(edittable, DockPanel.NORTH);

        saveButton = new NamedButton("budgetMembershipEditFields_saveButton", elements.save());
        saveButton.addClickHandler(this);
        cancelButton = new NamedButton("budgetMembershipEditFields_cancelButton", elements.cancel());
        cancelButton.addClickHandler(this);

        mainErrorLabel = new HTML();
        mainErrorLabel.setStyleName("error");

        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(mainErrorLabel);
        dp.add(buttonPanel, DockPanel.NORTH);
        setWidget(dp);
    }

    public void onClick(ClickEvent event) {
    	Widget sender = (Widget) event.getSource();
    	
    	if (sender == cancelButton) {
            hide();
        }
        if (validateFields()) {
            doSave();
        }
    }

    private void doSave() {
        StringBuffer sb = new StringBuffer();
        sb.append("action=saveMemberships");

        Util.addPostParam(sb, "keyYear", String.valueOf(year));
        Util.addPostParam(sb, "keyFall", fall ? "1" : "0");
        Util.addPostParam(sb, "course", courseMemberBox.getText());
        Util.addPostParam(sb, "train", trainMemberBox.getText());
        Util.addPostParam(sb, "year", yearMemberBox.getText());
        

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {
                JSONObject object = value.isObject();

                if ("1".equals(Util.str(object.get("result")))) {
                    budgetView.init();
                    hide();
                } else {
                    mainErrorLabel.setHTML(messages.save_failed());
                    Util.timedMessage(mainErrorLabel, "", 5);
                }
            }
        };

        AuthResponder.post(constants, messages, callback, sb, "accounting/budget.php");
    }

    private boolean validateFields() {
        MasterValidator mv = new MasterValidator();
        Widget[] widgets = new Widget[] { courseMemberBox, trainMemberBox, yearMemberBox };
        mv.range(messages.field_positive(), new Integer(0), new Integer(99999), widgets);
        return mv.validateStatus();
    }

    public void init(BudgetView budgetView, JSONObject obj) {
        this.budgetView = budgetView;
        String yearcount = Util.str(obj.get("year"));
        String coursecount = Util.str(obj.get("course"));
        String traincount = Util.str(obj.get("train"));
        year = Util.getInt(obj.get("keyYear"));
        fall = Util.getInt(obj.get("keyFall")) == 1;

        yearMemberBox.setEnabled(!fall);

        yearMemberBox.setText(yearcount);
        courseMemberBox.setText(coursecount);
        trainMemberBox.setText(traincount);
        edittable.setText(0, 1, (fall ? elements.fall() : elements.spring()) + " " + year);
    }
}
