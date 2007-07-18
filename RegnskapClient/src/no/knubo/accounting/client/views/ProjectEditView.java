package no.knubo.accounting.client.views;

import java.util.Iterator;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.CacheCallback;
import no.knubo.accounting.client.cache.ProjectCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
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
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ProjectEditView extends Composite implements ClickListener,
        CacheCallback {

    private static ProjectEditView me;

    private final Constants constants;

    private final I18NAccount messages;

    private FlexTable table;

    private IdHolder idHolder;

    private Button newButton;

    private ProjectEditFields editFields;

    private ProjectCache projectCache;

    public ProjectEditView(I18NAccount messages, Constants constants) {
        this.messages = messages;
        this.constants = constants;

        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, messages.project());
        table.getRowFormatter().setStyleName(0, "header");
        table.getFlexCellFormatter().setColSpan(0, 0, 2);

        newButton = new NamedButton("projectEditView_newButton", messages.projectEditView_newButton());
        newButton.addClickListener(this);

        dp.add(newButton, DockPanel.NORTH);
        dp.add(table, DockPanel.NORTH);

        idHolder = new IdHolder();
        initWidget(dp);
    }

    public static ProjectEditView show(I18NAccount messages, Constants constants) {
        if (me == null) {
            me = new ProjectEditView(messages, constants);
        }
        me.setVisible(true);
        return me;
    }

    public void onClick(Widget sender) {
        if (editFields == null) {
            editFields = new ProjectEditFields();
        }

        int left = 0;
        if (sender == newButton) {
            left = sender.getAbsoluteLeft() + 10;
        } else {
            left = sender.getAbsoluteLeft() - 250;
        }

        int top = sender.getAbsoluteTop() + 10;
        editFields.setPopupPosition(left, top);

        if (sender == newButton) {
            editFields.init();
        } else {
            editFields.init(idHolder.findId(sender));
        }
        editFields.show();
    }

    public void init() {
        projectCache = ProjectCache.getInstance(constants, messages);
        idHolder.init();

        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }

        int row = 1;
        for (Iterator i = projectCache.getAll().iterator(); i.hasNext();) {
            JSONObject object = (JSONObject) i.next();

            String project = Util.str(object.get("description"));
            String id = Util.str(object.get("project"));

            addRow(row, project, id);
            row++;
        }
    }

    private void addRow(int row, String project, String id) {
        table.setHTML(row, 0, project);
        Image editImage = ImageFactory.editImage("projectEditView_editImage");
        editImage.addClickListener(me);
        idHolder.add(id, editImage);

        table.setWidget(row, 1, editImage);

        String style = (row % 2 == 0) ? "showlineposts2" : "showlineposts1";
        table.getRowFormatter().setStyleName(row, style);
    }

    class ProjectEditFields extends DialogBox implements ClickListener {
        private TextBoxWithErrorText projectBox;

        private Button saveButton;

        private Button cancelButton;

        private String currentId;

        private HTML mainErrorLabel;

        ProjectEditFields() {
            setText(messages.project());
            FlexTable edittable = new FlexTable();
            edittable.setStyleName("edittable");

            edittable.setHTML(0, 0, messages.description());
            projectBox = new TextBoxWithErrorText("project");
            projectBox.setMaxLength(100);
            projectBox.setVisibleLength(100);
            edittable.setWidget(0, 1, projectBox);

            DockPanel dp = new DockPanel();
            dp.add(edittable, DockPanel.NORTH);

            saveButton = new NamedButton("projectEditView_saveButton", messages
                    .save());
            saveButton.addClickListener(this);
            cancelButton = new NamedButton("projectEditView_cancelButton",
                    messages.cancel());
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

        public void init(String id) {
            currentId = id;

            String project = ProjectCache.getInstance(constants, messages).getName(id);

            projectBox.setText(project);
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
            final String description = projectBox.getText();
            final String sendId = currentId;

            Util.addPostParam(sb, "description", description);
            Util.addPostParam(sb, "project", sendId);

            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                    constants.baseurl() + "registers/projects.php");

            ServerResponse callback = new ServerResponse() {

                public void serverResponse(String serverResponse) {
                    if ("0".equals(serverResponse)) {
                        mainErrorLabel.setHTML(messages.save_failed());
                        Util.timedMessage(mainErrorLabel, "", 5);
                    } else {
                        if (sendId == null) {
                            JSONValue value = JSONParser.parse(serverResponse);
                            if (value == null) {
                                String error = "Failed to save data - null value.";
                                Window.alert(error);
                                return;
                            }
                            JSONObject object = value.isObject();

                            if (object == null) {
                                String error = "Failed to save data - null object.";
                                Window.alert(error);
                                return;
                            }
                            int row = table.getRowCount();

                            addRow(row, description, sendId);
                        } else {
                            /* Could probably be more effective but why bother? */
                            ProjectCache.getInstance(constants, messages).flush(me);
                        }
                        hide();
                    }
                }
            };

            try {
                builder.setHeader("Content-Type",
                        "application/x-www-form-urlencoded");
                builder.sendRequest(sb.toString(), new AuthResponder(constants, messages, callback));
            } catch (RequestException e) {
                Window.alert("Failed to send the request: " + e.getMessage());
            }

        }

        private boolean validateFields() {
            MasterValidator mv = new MasterValidator();
            Widget[] widgets = new Widget[] { projectBox };
            mv.mandatory(messages.required_field(), widgets);
            return mv.validateStatus();
        }

        public void init() {
            currentId = null;
            projectBox.setText("");
        }
    }

    public void flushCompleted() {
        me.init();
    }
}
