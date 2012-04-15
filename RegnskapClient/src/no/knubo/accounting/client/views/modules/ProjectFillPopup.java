package no.knubo.accounting.client.views.modules;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.EmploeeCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.cache.ProjectCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.views.LineEditView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

public class ProjectFillPopup implements ClickHandler {

    private final I18NAccount messages;
    private final Constants constants;
    private final Elements elements;
    private NamedButton useButton;
    private AccountTable table;
    private DialogBox db;
    private NamedButton cancel;
    private NamedButton save;
    private final LineEditView lineEditView;

    public ProjectFillPopup(I18NAccount messages, Constants constants, Elements elements, LineEditView me) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;
        this.lineEditView = me;
    }

    public void createProjectEditPopup(JSONValue responseObj) {
        HorizontalPanel hp = createProjectSelect(true, "");

        table = new AccountTable("tableborder");
        table.setWidget(0, 0, hp);
        table.getFlexCellFormatter().setColSpan(0, 0, 3);

        fillLines(responseObj.isObject());

        HorizontalPanel buttonPanel = new HorizontalPanel();
        save = new NamedButton("save", elements.save());
        save.addStyleName("buttonrow");
        save.addClickHandler(this);
        buttonPanel.add(save);

        cancel = new NamedButton("cancel", elements.cancel());
        cancel.addClickHandler(this);
        cancel.addStyleName("buttonrow");
        buttonPanel.add(cancel);

        table.setWidget(table.getRowCount(), 0, buttonPanel);

        db = new DialogBox();
        db.setText(elements.project_set());
        db.setWidget(table);
        db.setModal(true);
        db.setAutoHideEnabled(false);
        db.center();

    }

    private void fillLines(JSONObject root) {

        JSONValue value = root.get("postArray");
        JSONArray array = value.isArray();

        for (int i = 0; i < array.size(); i++) {
            addRegnLine(array.get(i));
        }
    }

    private void addRegnLine(JSONValue value) {
        JSONObject object = value.isObject();

        String posttype = Util.str(object.get("Post_type"));
        String person = Util.str(object.get("Person"));
        String project = Util.str(object.get("Project"));
        String amount = Util.money(object.get("Amount"));
        String debkred = Util.str(object.get("Debet"));
        String id = Util.str(object.get("Id"));

        int row = table.getRowCount();

        PosttypeCache postCache = PosttypeCache.getInstance(constants, messages);
        EmploeeCache empCache = EmploeeCache.getInstance(constants, messages);

        table.setWidget(row, 0, createProjectSelect(false, project));
        table.setText(row, 1, ProjectCache.getInstance(constants, messages).getName(project));

        table.setText(row, 1, posttype + "-" + postCache.getDescription(posttype));

        table.getRowFormatter().setStyleName(row, (row % 2 == 0) ? "showlineposts2" : "showlineposts1");

        table.setText(row, 3, empCache.getName(person));

        table.setText(row, 4, Util.debkred(elements, debkred));
        table.setText(row, 5, amount);
        table.setText(row, 6, id);
        table.getCellFormatter().setStyleName(row, 6, "hidden");

        table.getCellFormatter().setStyleName(row, 4, "right");

    }

    private HorizontalPanel createProjectSelect(boolean setForAll, String project) {
        TextBoxWithErrorText idBox = new TextBoxWithErrorText("project");
        idBox.setVisibleLength(6);

        ListBox nameBox = new ListBox();
        nameBox.setVisibleItemCount(1);

        HorizontalPanel hp = new HorizontalPanel();
        hp.add(idBox);
        hp.add(nameBox);

        ProjectCache.getInstance(constants, messages).fill(nameBox);
        Util.syncListbox(nameBox, idBox.getTextBox());

        if (project != null) {
            idBox.setText(project);
            Util.setIndexByValue(nameBox, project);
        }

        if (setForAll) {
            useButton = new NamedButton("set_for_all", elements.set_for_all());
            useButton.addClickHandler(this);
            hp.add(useButton);
        }

        return hp;
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == useButton) {
            setForAll();
        } else if (event.getSource() == cancel) {
            db.hide();
        } else if (event.getSource() == save) {
            save();
        }
    }

    private void save() {
        JSONArray data = new JSONArray();

        for (int i = 1; i < table.getRowCount() - 1; i++) {
            HorizontalPanel panel = (HorizontalPanel) table.getWidget(i, 0);

            TextBoxWithErrorText idBox = (TextBoxWithErrorText) panel.getWidget(0);

            JSONObject one = new JSONObject();
            one.put("project", new JSONString(idBox.getText()));
            one.put("id", new JSONString(table.getText(i, 6)));
            data.set(i - 1, one);
        }

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                lineEditView.projectSet();
                db.hide();
            }
        };

        StringBuffer params = new StringBuffer();
        params.append("action=update_projects");
        Util.addPostParam(params, "projects", data.toString());

        AuthResponder.post(constants, messages, callback, params, "accounting/editaccountline.php");
    }

    private void setForAll() {
        HorizontalPanel globalpanel = (HorizontalPanel) table.getWidget(0, 0);

        TextBoxWithErrorText globalidBox = (TextBoxWithErrorText) globalpanel.getWidget(0);
        ListBox globalnameBox = (ListBox) globalpanel.getWidget(1);

        for (int i = 1; i < table.getRowCount() - 1; i++) {
            HorizontalPanel panel = (HorizontalPanel) table.getWidget(i, 0);

            TextBoxWithErrorText idBox = (TextBoxWithErrorText) panel.getWidget(0);
            ListBox nameBox = (ListBox) panel.getWidget(1);

            idBox.setText(globalidBox.getText());
            nameBox.setSelectedIndex(globalnameBox.getSelectedIndex());
        }
    }
}
