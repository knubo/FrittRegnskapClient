package no.knubo.accounting.client.views;

import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.EmploeeCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.cache.ProjectCache;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.ServerResponseWithValidation;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.modules.CountFields;
import no.knubo.accounting.client.views.modules.ProjectFillPopup;
import no.knubo.accounting.client.views.modules.RegisterStandards;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LineEditView extends Composite implements ClickHandler {

    private static LineEditView me;

    private IdHolder<String, Image> removeIdHolder = new IdHolder<String, Image>();

    public static LineEditView getInstance(ViewCallback caller, I18NAccount messages, Constants constants,
            HelpPanel helpPanel, Elements elements) {
        if (me == null) {
            me = new LineEditView(caller, messages, constants, helpPanel, elements);
        }
        return me;
    }

    private AccountTable postsTable;

    private TextBoxWithErrorText postNmbBox;

    private final I18NAccount messages;

    private final Constants constants;

    private TextBoxWithErrorText dayBox;

    private TextBoxWithErrorText attachmentBox;

    private TextBoxWithErrorText descriptionBox;

    private Button updateButton;

    private ListBox debKredbox;

    private TextBoxWithErrorText amountBox;

    private TextBoxWithErrorText accountIdBox;

    private ListBox accountNameBox;

    // private ListBox fordringBox;
    private Label rowErrorLabel;

    private TextBoxWithErrorText projectIdBox;

    private ListBox projectNameBox;

    private ListBox personBox;

    private HTML dateHeader;

    private Button addLineButton;

    private final ViewCallback caller;

    private RegisterStandards registerStandards;

    private CountFields countFields;

    private final HelpPanel helpPanel;

    private final Elements elements;

    private LineEditView(ViewCallback caller, I18NAccount messages, Constants constants, HelpPanel helpPanel,
            Elements elements) {

        this.caller = caller;
        this.messages = messages;
        this.constants = constants;
        this.helpPanel = helpPanel;
        this.elements = elements;

        registerStandards = new RegisterStandards(constants, messages, elements, caller);

        DockPanel dp = new DockPanel();
        dp.add(mainFields(), DockPanel.NORTH);
        dp.add(newFields(), DockPanel.NORTH);

        countFields = new CountFields(constants, messages, elements);
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(regnLinesView());
        hp.add(countFields.getTable());
        dp.add(hp, DockPanel.NORTH);

        initWidget(dp);
    }

    public void init() {
        init(null, null);
    }

    public void init(String line) {
        init(line, null);
    }

    private void init(String line, String navigate) {
        currentLine = line;

        postNmbBox.setText("");
        dayBox.setText("");
        attachmentBox.setText("");
        descriptionBox.setText("");
        amountBox.setText("");
        accountIdBox.setText("");
        accountNameBox.setSelectedIndex(0);
        projectIdBox.setText("");
        projectNameBox.setSelectedIndex(0);
        personBox.setSelectedIndex(0);

        removeIdHolder.init();
        while (postsTable.getRowCount() > 1) {
            postsTable.removeRow(1);
        }

        addLineButton.setEnabled(line != null);

        if (line != null) {
            showLine(line, navigate);
            countFields.init(line);
        } else {
            registerStandards.fetchInitalData(true);
            dayBox.setFocus(true);
        }
        helpPanel.resize(me);
    }

    protected String currentLine;

    private Label updateLabel;

    private Image previousImage;

    private Image nextImage;

    private Label currentId;

    private ListBoxWithErrorText defaultProjectNameBox;

    private ProjectCache projectCache;

    protected boolean disableDelete = false;

    private NamedButton editProjectButton;

    private void showLine(String line, String navigate) {

        ServerResponse rh = new ServerResponse() {
            @Override
            public void serverResponse(JSONValue responseValue) {

                JSONObject root = responseValue.isObject();

                disableDelete = Util.getBoolean(root.get("disableDelete"));
                currentLine = Util.str(root.get("Id"));
                currentId.setText(currentLine);
                registerStandards.setCurrentMonth(Util.getMonth(root.get("date")));
                registerStandards.setCurrentYear(Util.getYear(root.get("date")));
                registerStandards.setDateHeader();

                dayBox.setText(Util.getDay(root.get("date")));

                attachmentBox.setText(Util.str(root.get("Attachment")));
                postNmbBox.setText(Util.str(root.get("Postnmb")));
                descriptionBox.setText(Util.str(root.get("Description")));

                JSONValue value = root.get("postArray");
                JSONArray array = value.isArray();

                for (int i = 0; i < array.size(); i++) {
                    addRegnLine(array.get(i));
                }
                addSumLineSetDefaults(Util.str(root.get("sum")));

                enableDisableButtonsBasedOnDeleteIsPossible();
            }

        };

        AuthResponder.get(constants, messages, rh, "accounting/editaccountline.php?action=query&line=" + line
                + (navigate != null ? "&" + navigate : ""));
    }

    protected void enableDisableButtonsBasedOnDeleteIsPossible() {
        addLineButton.setEnabled(!disableDelete);
        descriptionBox.setEnabled(!disableDelete);
        dayBox.setEnabled(!disableDelete);
    }

    protected void addRegnLine(JSONValue value) {
        JSONObject object = value.isObject();

        String posttype = Util.str(object.get("Post_type"));
        String person = Util.str(object.get("Person"));
        String project = Util.str(object.get("Project"));
        String amount = Util.money(object.get("Amount"));
        String debkred = Util.str(object.get("Debet"));
        String id = Util.str(object.get("Id"));

        addRegnLine(posttype, person, project, amount, debkred, id);
    }

    private void addRegnLine(String posttype, String person, String project, String amount, String debkred, String id) {
        int rowcount = postsTable.getRowCount();

        PosttypeCache postCache = PosttypeCache.getInstance(constants, messages);
        EmploeeCache empCache = EmploeeCache.getInstance(constants, messages);

        postsTable.setText(rowcount, 0, posttype + "-" + postCache.getDescription(posttype));

        postsTable.getRowFormatter().setStyleName(rowcount, (rowcount % 2 == 0) ? "showlineposts2" : "showlineposts1");

        postsTable.setText(rowcount, 1, projectCache.getName(project));
        postsTable.setText(rowcount, 2, empCache.getName(person));

        postsTable.setText(rowcount, 3, Util.debkred(elements, debkred));
        postsTable.setText(rowcount, 4, amount);
        postsTable.getCellFormatter().setStyleName(rowcount, 4, "right");

        if (!disableDelete) {
            Image removeImage = ImageFactory.removeImage("LineEditView.removeImage");
            postsTable.setWidget(rowcount, 5, removeImage);
            removeImage.addClickHandler(this);

            removeIdHolder.add(id, removeImage);
        }
    }

    /**
     * Adds sum line and sets default value for amount and debet/kredit. Also
     * sets default values for comboboxes and projects.
     * 
     * @param sumAmount
     *            The amount to display.
     */
    private void addSumLineSetDefaults(String sumAmount) {
        int row = postsTable.getRowCount();
        postsTable.setText(row, 0, elements.sum());
        postsTable.setText(row, 4, Util.money(Util.fixMoney(sumAmount)));

        changeProjectBoxBasedOnDefault();

        accountNameBox.setSelectedIndex(0);
        accountIdBox.setText("");
        personBox.setSelectedIndex(0);

        if (sumAmount.equals("0")) {
            amountBox.setText("");
            postsTable.getCellFormatter().setStyleName(row, 4, "right");
            return;
        }

        postsTable.getCellFormatter().setStyleName(row, 4, "right error");

        if (sumAmount.startsWith("-")) {
            amountBox.setText(sumAmount.substring(1));
            debKredbox.setSelectedIndex(0); // Debet
        } else {
            debKredbox.setSelectedIndex(1); // Kredit
            amountBox.setText(sumAmount);
        }
    }

    private void removeSumLine() {
        if (postsTable.getRowCount() > 1) {
            postsTable.removeRow(postsTable.getRowCount() - 1);
        }
    }

    private Widget newFields() {
        VerticalPanel panel = new VerticalPanel();

        FlexTable table = new FlexTable();
        table.setStyleName("edittable");
        panel.add(table);

        table.setHTML(0, 1, elements.amount());

        debKredbox = new ListBox();
        debKredbox.setVisibleItemCount(1);
        debKredbox.addItem(elements.debet(), "1");
        debKredbox.addItem(elements.kredit(), "-1");
        table.setWidget(1, 0, debKredbox);

        amountBox = registerStandards.createAmountBox();
        table.setWidget(1, 1, amountBox);
        table.getFlexCellFormatter().setColSpan(1, 1, 2);

        table.setText(2, 0, elements.account());

        HTML errorAccountHtml = new HTML();
        accountIdBox = new TextBoxWithErrorText("account", errorAccountHtml);
        accountIdBox.setVisibleLength(6);
        table.setWidget(3, 0, accountIdBox);
        table.setWidget(3, 2, errorAccountHtml);

        accountNameBox = new ListBox();
        accountNameBox.setVisibleItemCount(1);

        ListBox costBox = new ListBox();
        costBox.setVisibleItemCount(1);
        ListBox earningsBox = new ListBox();
        earningsBox.setVisibleItemCount(1);
        ListBox capitalBox = new ListBox();
        capitalBox.setVisibleItemCount(1);

        table.setText(2, 1, elements.account_all());
        table.setWidget(3, 1, accountNameBox);

        table.setText(2, 2, elements.account_cost());
        table.setWidget(3, 2, costBox);

        table.setText(2, 3, elements.account_earnings());
        table.setWidget(3, 3, earningsBox);

        table.setText(2, 4, elements.account_capital());
        table.setWidget(3, 4, capitalBox);

        /* Above remove button. */
        table.addCell(3);

        PosttypeCache postTypeCache = PosttypeCache.getInstance(constants, messages);
        postTypeCache.fillAllPosts(accountNameBox);
        postTypeCache.fillAllCost(costBox);
        postTypeCache.fillAllEarnings(earningsBox);
        postTypeCache.fillAllCapital(capitalBox);

        Util.syncListbox(accountNameBox, accountIdBox.getTextBox());
        Util.syncListbox(costBox, accountIdBox.getTextBox());
        Util.syncListbox(earningsBox, accountIdBox.getTextBox());
        Util.syncListbox(capitalBox, accountIdBox.getTextBox());
        Util.syncListboxes(accountNameBox, costBox, earningsBox, capitalBox);

        table.setText(4, 0, elements.project());

        HTML projectErrorLabel = new HTML();

        projectIdBox = new TextBoxWithErrorText("project", projectErrorLabel);
        projectIdBox.setVisibleLength(6);
        table.setWidget(5, 0, projectIdBox);

        projectNameBox = new ListBox();
        projectNameBox.setVisibleItemCount(1);

        HorizontalPanel hp = new HorizontalPanel();
        hp.add(projectNameBox);
        hp.add(projectErrorLabel);
        table.setWidget(5, 1, hp);
        table.getFlexCellFormatter().setColSpan(5, 1, 2);

        ProjectCache.getInstance(constants, messages).fill(projectNameBox);
        Util.syncListbox(projectNameBox, projectIdBox.getTextBox());

        table.setText(6, 0, elements.person());

        personBox = new ListBox();
        personBox.setVisibleItemCount(1);
        table.setWidget(7, 0, personBox);
        table.getFlexCellFormatter().setColSpan(7, 0, 2);
        EmploeeCache.getInstance(constants, messages).fill(personBox);

        addLineButton = new NamedButton("LineEditView.addLineButton");
        addLineButton.setText(elements.add());
        addLineButton.addClickHandler(this);
        table.setWidget(8, 0, addLineButton);

        editProjectButton = new NamedButton("project_set", elements.project_set());
        editProjectButton.addClickHandler(this);
        table.setWidget(8, 1, editProjectButton);

        return panel;
    }

    private Widget regnLinesView() {
        VerticalPanel vp = new VerticalPanel();

        postsTable = new AccountTable("tableborder");
        vp.add(postsTable);

        postsTable.getRowFormatter().setStyleName(0, "header");
        postsTable.setText(0, 0, elements.account());
        postsTable.setText(0, 1, elements.project());
        postsTable.setText(0, 2, elements.person());
        postsTable.setText(0, 3, elements.debet() + "/" + elements.kredit());
        postsTable.setHTML(0, 4, elements.amount());
        postsTable.getFlexCellFormatter().setColSpan(0, 4, 2);

        return vp;
    }

    private Widget mainFields() {

        VerticalPanel vp = new VerticalPanel();

        dateHeader = registerStandards.getDateHeader();
        dateHeader.setText("...");
        dateHeader.addClickHandler(this);
        vp.add(dateHeader);

        FlexTable table = new FlexTable();
        table.setStyleName("edittable");
        vp.add(table);

        postNmbBox = registerStandards.getPostNmbBox();
        table.setWidget(0, 1, postNmbBox);
        table.setText(0, 0, elements.postnmb());

        dayBox = registerStandards.createDayBox();
        table.setWidget(1, 1, dayBox);
        table.setText(1, 0, elements.day());

        attachmentBox = registerStandards.getAttachmentBox();
        table.setWidget(2, 1, attachmentBox);
        table.setText(2, 0, elements.attachment());

        descriptionBox = registerStandards.createDescriptionBox();
        table.setWidget(3, 1, descriptionBox);
        table.setText(3, 0, elements.description());

        projectCache = ProjectCache.getInstance(constants, messages);

        defaultProjectNameBox = new ListBoxWithErrorText("default_project");
        projectCache.fill(defaultProjectNameBox.getListbox());

        defaultProjectNameBox.setVisibleItemCount(1);
        defaultProjectNameBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                changeProjectBoxBasedOnDefault();
            }
        });

        table.setText(4, 0, elements.project());
        table.setWidget(4, 1, defaultProjectNameBox);

        updateButton = new NamedButton("LineEditView.updateButton");
        updateButton.setText(elements.update());
        updateButton.addClickHandler(this);

        table.setWidget(5, 0, updateButton);
        updateLabel = new Label();
        table.setWidget(5, 1, updateLabel);

        HorizontalPanel hp = new HorizontalPanel();

        previousImage = ImageFactory.previousImage("ShowMembershipView.previousImage");
        previousImage.addClickHandler(this);

        nextImage = ImageFactory.nextImage("ShowMembershipView.nextImage");
        nextImage.addClickHandler(this);
        currentId = new Label();
        hp.add(previousImage);
        hp.add(currentId);
        hp.add(nextImage);
        table.setWidget(0, 2, hp);

        return vp;
    }

    @Override
    public void onClick(ClickEvent event) {
        Widget sender = (Widget) event.getSource();
        if (sender == updateButton) {
            doUpdate();
        } else if (sender == addLineButton) {
            doRowInsert();
        } else if (sender == nextImage) {
            init(currentLine, "navigate=next");
        } else if (sender == previousImage) {
            init(currentLine, "navigate=previous");
        } else if (sender == dateHeader) {
            caller.viewMonth(registerStandards.getCurrentYear(), registerStandards.getCurrentMonth());
        } else if (sender == editProjectButton) {
            showProjectEditPopup();
        } else {
            doRowRemove(sender);
        }
    }

    private void showProjectEditPopup() {
        if (currentLine == null) {
            return;
        }
        ServerResponse cb = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                ProjectFillPopup pfp = new ProjectFillPopup(messages, constants, elements, me);
                pfp.createProjectEditPopup(responseObj);
            }
        };
        AuthResponder.get(constants, messages, cb, "accounting/editaccountline.php?action=posts&line=" + currentLine);

    }

  
    private void doRowRemove(Widget sender) {
        final String id = removeIdHolder.findId(sender);

        if (id == null) {
            Window.alert("Failed to find id for delete.");
            return;
        }

        StringBuffer sb = new StringBuffer();

        sb.append("action=delete");
        Util.addPostParam(sb, "id", id);
        Util.addPostParam(sb, "line", currentLine);

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue value) {
                JSONObject obj = value.isObject();

                String result = Util.str(obj.get("result"));

                if ("0".equals(result)) {
                    rowErrorLabel.setText(messages.save_failed());
                } else {
                    String[] parts = result.split(":");
                    // Parts[0] should be same as id, but I use id.
                    removeVisibleRow(id);
                    removeSumLine();
                    addSumLineSetDefaults(parts[1]);
                }
                Util.timedMessage(rowErrorLabel, "", 5);
            }
        };

        AuthResponder.post(constants, messages, callback, sb, "accounting/editaccountpost.php");

    }

    protected void removeVisibleRow(String id) {
        for (int i = 1; i < postsTable.getRowCount(); i++) {
            String removeId = removeIdHolder.findId(postsTable.getWidget(i, 5));

            if (id.equals(removeId)) {
                postsTable.removeRow(i);
                resetPostsTableStyle();
                return;
            }
        }
        Window.alert("Failed to remove line for id " + id);
    }

    private void resetPostsTableStyle() {
        for (int row = 1; row < postsTable.getRowCount(); row++) {
            postsTable.getRowFormatter().setStyleName(row, (row % 2 == 0) ? "showlineposts2" : "showlineposts1");
        }
    }

    private void doRowInsert() {

        if (!validateRowInsert()) {
            return;
        }
        final String personId = Util.getSelected(personBox);
        final String debk = Util.getSelected(debKredbox);
        final String post_type = accountIdBox.getText();
        final String money = Util.fixMoney(amountBox.getText());
        final String projectId = projectIdBox.getText();

        StringBuffer sb = new StringBuffer();
        sb.append("action=insert");
        Util.addPostParam(sb, "line", currentLine);
        Util.addPostParam(sb, "debet", debk);
        Util.addPostParam(sb, "post_type", post_type);
        Util.addPostParam(sb, "amount", money);
        Util.addPostParam(sb, "project", projectId);
        Util.addPostParam(sb, "person", personId);

        ServerResponse callback = new ServerResponseWithValidation() {

            @Override
            public void serverResponse(JSONValue value) {
                JSONObject obj = value.isObject();

                String serverResponse = Util.str(obj.get("result"));

                if ("0".equals(serverResponse)) {
                    rowErrorLabel.setText(messages.save_failed());
                } else {
                    removeSumLine();
                    String[] parts = serverResponse.split(":");

                    addRegnLine(post_type, personId, projectId, Util.money(money), debk, parts[0]);
                    addSumLineSetDefaults(parts[1]);
                }
                Util.timedMessage(updateLabel, "", 5);
            }

            @Override
            public void validationError(List<String> fields) {
                MasterValidator masterValidator = new MasterValidator();

                masterValidator.mandatory(messages.required_field(), projectIdBox);

                masterValidator.validateStatus();
            }
        };

        AuthResponder.post(constants, messages, callback, sb, "accounting/editaccountpost.php");

    }

    private void doUpdate() {
        if (!registerStandards.validateTop()) {
            return;
        }

        updateButton.setEnabled(false);
        updateLabel.setText("...");

        StringBuffer sb = new StringBuffer();

        if (currentLine != null) {
            sb.append("action=update");
        } else {
            sb.append("action=insert");
        }
        Util.addPostParam(sb, "day", dayBox.getText());
        if (currentLine != null) {
            Util.addPostParam(sb, "line", currentLine);
        }
        Util.addPostParam(sb, "desc", descriptionBox.getText());
        Util.addPostParam(sb, "attachment", attachmentBox.getText());
        Util.addPostParam(sb, "postnmb", postNmbBox.getText());
        Util.addPostParam(sb, "month", String.valueOf(registerStandards.getCurrentMonth()));
        Util.addPostParam(sb, "year", String.valueOf(registerStandards.getCurrentYear()));

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue value) {
                JSONObject obj = value.isObject();

                String serverResponse = Util.str(obj.get("result"));

                if ("0".equals(serverResponse)) {
                    updateLabel.setText(messages.save_failed());
                } else {
                    updateLabel.setText(messages.save_ok());

                    if (currentLine == null) {
                        currentLine = serverResponse;
                        addLineButton.setEnabled(true);
                    }
                }
                Util.timedMessage(updateLabel, "", 5);
                updateButton.setEnabled(true);
            }
        };

        AuthResponder.post(constants, messages, callback, sb, "accounting/editaccountline.php");
    }

    private boolean validateRowInsert() {
        MasterValidator masterValidator = new MasterValidator();

        masterValidator.mandatory(messages.required_field(), new Widget[] { amountBox, accountIdBox });

        masterValidator.money(messages.field_money(), new Widget[] { amountBox });

        masterValidator.registry(messages.registry_invalid_key(), projectCache, new Widget[] { projectIdBox });

        masterValidator.registry(messages.registry_invalid_key(), PosttypeCache.getInstance(constants, messages),
                new Widget[] { accountIdBox });

        return masterValidator.validateStatus();
    }

    void changeProjectBoxBasedOnDefault() {
        projectNameBox.setSelectedIndex(defaultProjectNameBox.getSelectedIndex());
        projectIdBox.setText(projectCache.getId(Util.getSelectedText(defaultProjectNameBox.getListbox())));
    }

    public void projectSet() {
        init(currentLine);
    }

}
