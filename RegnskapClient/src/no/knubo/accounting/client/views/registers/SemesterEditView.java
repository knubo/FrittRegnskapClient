package no.knubo.accounting.client.views.registers;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.NamedButton;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class SemesterEditView extends Composite implements ClickListener {
    private static SemesterEditView me;
    private I18NAccount messages;
    private Constants constants;
    private FlexTable table;
    private NamedButton newButton;
    private IdHolder idHolder;
    private final Elements elements;
    private SemesterEditView.SemesterEditFields editFields;

    public SemesterEditView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;

        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setText(0, 0, elements.menuitem_semesters());
        table.getFlexCellFormatter().setColSpan(0, 0, 4);
        table.getRowFormatter().setStyleName(0, "header");
        table.setText(1, 0, elements.year());
        table.setText(1, 1, elements.spring());
        table.setText(1, 2, elements.fall());
        table.setText(1, 3, "");
        table.getRowFormatter().setStyleName(1, "header");

        dp.add(table, DockPanel.NORTH);
        idHolder = new IdHolder();
        initWidget(dp);
    }

    public static SemesterEditView show(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new SemesterEditView(messages, constants, elements);
        }
        me.setVisible(true);
        return me;
    }

    public void init() {
        idHolder.init();
        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }

        ServerResponse callback = new ServerResponse() {
            public void serverResponse(JSONValue value) {
                showSemesters(value.isArray());
            }

        };

        AuthResponder.get(constants, messages, callback, "registers/semesters.php?action=all");
    }

    protected void showSemesters(JSONArray array) {

        int row = table.getRowCount() - 1;

        int currentYear = 0;
        for (int i = 0; i < array.size(); i++) {
            JSONObject semester = array.get(i).isObject();

            int year = Util.getInt(semester.get("year"));
            String desc = Util.str(semester.get("description"));
            boolean fall = Util.getBoolean(semester.get("fall"));
            if (currentYear != year) {
                currentYear = year;
                row++;
                table.setText(row, 0, String.valueOf(year));
                Image editImage = ImageFactory.editImage("semesterEditImage");
                editImage.addClickListener(this);
                table.setWidget(row, 3, editImage);
                idHolder.add(String.valueOf(row), editImage);
            }

            table.setHTML(row, fall ? 2 : 1, desc);
            table.getCellFormatter().setStyleName(row, fall ? 2 : 1, "desc");
            String style = (((row + 1) % 6) < 3) ? "line2" : "line1";
            table.getRowFormatter().setStyleName(row, style);

        }
    }

    public void onClick(Widget sender) {

        if (editFields == null) {
            editFields = new SemesterEditFields();
        }
        
        int left = 0;
        if (sender == newButton) {
            left = sender.getAbsoluteLeft() + 10;
        } else {
            left = sender.getAbsoluteLeft() - 150;
        }

        int top = sender.getAbsoluteTop() + 10;
        editFields.setPopupPosition(left, top);

        if (sender == newButton) {
            editFields.init();
        } else {
            int row = Integer.valueOf(idHolder.findId(sender)).intValue();
            String year = table.getText(row, 0);
            String springDesc = table.getText(row, 1);
            String fallDesc = table.getText(row, 2);

            editFields.init(year, springDesc, fallDesc);
        }
        editFields.show();
    }

    class SemesterEditFields extends DialogBox implements ClickListener {
        private TextBoxWithErrorText springBox;
        private TextBoxWithErrorText fallBox;

        private Button saveButton;
        private Button cancelButton;
        private HTML mainErrorLabel;
        private FlexTable edittable;

        SemesterEditFields() {
            setText(elements.menuitem_semesters());
            edittable = new FlexTable();
            edittable.setStyleName("edittable");

            edittable.setHTML(0, 0, elements.year());
            edittable.setHTML(1, 0, elements.spring());
            edittable.setHTML(2, 0, elements.fall());
            springBox = new TextBoxWithErrorText("spring");
            springBox.setMaxLength(20);
            springBox.setVisibleLength(20);
            edittable.setWidget(1, 1, springBox);

            fallBox = new TextBoxWithErrorText("fall");
            fallBox.setMaxLength(20);
            fallBox.setVisibleLength(20);
            edittable.setWidget(2, 1, fallBox);

            DockPanel dp = new DockPanel();
            dp.add(edittable, DockPanel.NORTH);

            saveButton = new NamedButton("semesterEditView_saveButton", elements.save());
            saveButton.addClickListener(this);
            cancelButton = new NamedButton("semesterEditView_cancelButton", elements.cancel());
            cancelButton.addClickListener(this);

            mainErrorLabel = new HTML();
            mainErrorLabel.setStyleName("error");

            HorizontalPanel buttonPanel = new HorizontalPanel();
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            buttonPanel.add(mainErrorLabel);
            dp.add(buttonPanel, DockPanel.NORTH);
            setWidget(dp);
        }

        public void onClick(Widget sender) {
            if (sender == cancelButton) {
                hide();
            } else if (sender == saveButton && validateFields()) {
                doSave();
            }
        }

        private void doSave() {
            StringBuffer sb = new StringBuffer();
            sb.append("action=save");
            // final String description = projectBox.getText();
            // final String sendId = currentId;
            //
            // Util.addPostParam(sb, "description", description);
            // Util.addPostParam(sb, "project", sendId);

            ServerResponse callback = new ServerResponse() {

                public void serverResponse(JSONValue value) {
                    JSONObject object = value.isObject();

                    
                    
                    hide();
                }
            };

            AuthResponder.post(constants, messages, callback, sb, "registers/semesters.php");
        }

        private void init() {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        private void init(String year, String springDesc, String fallDesc) {
            edittable.setHTML(0, 1, year);
            springBox.setText(springDesc);
            fallBox.setText(fallDesc);
        }

        private boolean validateFields() {
            MasterValidator mv = new MasterValidator();
            Widget[] widgets = new Widget[] { springBox, fallBox };
            mv.mandatory(messages.required_field(), widgets);
            return mv.validateStatus();
        }

    }

}
