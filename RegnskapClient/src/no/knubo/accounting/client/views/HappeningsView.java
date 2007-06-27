package no.knubo.accounting.client.views;

import java.util.Iterator;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.CacheCallback;
import no.knubo.accounting.client.cache.HappeningCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.NamedButton;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class HappeningsView extends Composite implements ClickListener,
        CacheCallback {

    private static HappeningsView me;

    private final Constants constants;

    private final I18NAccount messages;

    private FlexTable table;

    private IdHolder idHolder;

    private Button newButton;

    private PosttypeCache posttypeCache;

    private HappeningEditFields editFields;

    public HappeningsView(I18NAccount messages, Constants constants) {
        this.messages = messages;
        this.constants = constants;

        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, messages.happening());
        table.setHTML(0, 1, messages.post_description());
        table.setHTML(0, 2, messages.debet_post());
        table.setHTML(0, 3, messages.kredit_post());
        table.setHTML(0, 4, messages.count_required());
        table.setHTML(0, 5, "");
        table.getRowFormatter().setStyleName(0, "header");

        newButton = new NamedButton("HappeningsView.newButton", messages
                .new_happening());
        newButton.addClickListener(this);

        dp.add(newButton, DockPanel.NORTH);
        dp.add(table, DockPanel.NORTH);

        idHolder = new IdHolder();
        initWidget(dp);
    }

    public static HappeningsView show(I18NAccount messages, Constants constants) {
        if (me == null) {
            me = new HappeningsView(messages, constants);
        }
        me.setVisible(true);
        return me;
    }

    public void onClick(Widget sender) {
        if (editFields == null) {
            editFields = new HappeningEditFields();
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
        posttypeCache = PosttypeCache.getInstance(constants);
        idHolder.init();

        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }

        HappeningCache cache = HappeningCache.getInstance(constants);

        int row = 1;
        for (Iterator i = cache.getAll().iterator(); i.hasNext();) {
            JSONObject object = (JSONObject) i.next();

            String description = Util.str(object.get("description"));
            String linedesc = Util.str(object.get("linedesc"));
            String debetpost = Util.str(object.get("debetpost"));
            String kredpost = Util.str(object.get("kredpost"));
            boolean required = "1".equals(Util.str(object.get("count_req")));
            String id = Util.str(object.get("id"));

            addRow(row, description, linedesc, debetpost, kredpost, required,
                    id);
            row++;
        }
    }

    private void addRow(int row, String description, String linedesc,
            String debetpost, String kredpost, boolean required, String id) {
        table.setHTML(row, 0, description);
        table.setHTML(row, 1, linedesc);
        table.setHTML(row, 2, posttypeCache.getDescriptionWithType(debetpost));
        table.setHTML(row, 3, posttypeCache.getDescriptionWithType(kredpost));
        CheckBox box = new CheckBox();
        box.setEnabled(false);
        box.setChecked(required);
        table.setWidget(row, 4, box);
        table.getCellFormatter().setStyleName(row, 4, "center");

        Image editImage = ImageFactory.editImage();
        editImage.addClickListener(me);
        idHolder.add(id, editImage);

        table.setWidget(row, 5, editImage);

        String style = (row % 2 == 0) ? "showlineposts2" : "showlineposts1";
        table.getRowFormatter().setStyleName(row, style);
    }

    class HappeningEditFields extends DialogBox implements ClickListener {
        private TextBoxWithErrorText happeningBox;

        private TextBoxWithErrorText descBox;

        private ListBox debetListBox;

        private ListBox kreditListBox;

        private CheckBox countReq;

        private Button saveButton;

        private TextBoxWithErrorText debetNmbBox;

        private TextBoxWithErrorText kreditNmbBox;

        private Button cancelButton;

        private String currentId;

        private HTML mainErrorLabel;

        HappeningEditFields() {
            setText(messages.happening());
            FlexTable edittable = new FlexTable();
            edittable.setStyleName("edittable");

            edittable.setHTML(0, 0, messages.happening());
            happeningBox = new TextBoxWithErrorText("happening");
            happeningBox.setMaxLength(40);
            happeningBox.setVisibleLength(40);
            edittable.setWidget(0, 1, happeningBox);

            edittable.setHTML(1, 0, messages.post_description());
            descBox = new TextBoxWithErrorText("description");
            descBox.setMaxLength(80);
            descBox.setVisibleLength(80);
            edittable.setWidget(1, 1, descBox);

            edittable.setHTML(2, 0, messages.debet_post());
            debetListBox = new ListBox();
            debetListBox.setVisibleItemCount(1);
            posttypeCache.fillAllPosts(debetListBox);

            HTML debetErrorLabel = new HTML();
            debetNmbBox = new TextBoxWithErrorText("debetpost", debetErrorLabel);
            debetNmbBox.setMaxLength(5);
            debetNmbBox.setVisibleLength(5);
            Util.syncListbox(debetListBox, debetNmbBox.getTextBox());

            HorizontalPanel debetPanel = new HorizontalPanel();
            debetPanel.add(debetNmbBox);
            debetPanel.add(debetListBox);
            debetPanel.add(debetErrorLabel);
            edittable.setWidget(2, 1, debetPanel);

            edittable.setHTML(3, 0, messages.kredit_post());
            kreditListBox = new ListBox();
            kreditListBox.setVisibleItemCount(1);
            posttypeCache.fillAllPosts(kreditListBox);

            HTML kreditErrorLabel = new HTML();
            kreditNmbBox = new TextBoxWithErrorText("creditpost",
                    kreditErrorLabel);
            kreditNmbBox.setMaxLength(5);
            kreditNmbBox.setVisibleLength(5);
            Util.syncListbox(kreditListBox, kreditNmbBox.getTextBox());

            HorizontalPanel kredPanel = new HorizontalPanel();
            kredPanel.add(kreditNmbBox);
            kredPanel.add(kreditListBox);
            kredPanel.add(kreditErrorLabel);
            edittable.setWidget(3, 1, kredPanel);

            edittable.setHTML(4, 0, messages.count_required());
            countReq = new CheckBox();
            edittable.setWidget(4, 1, countReq);

            DockPanel dp = new DockPanel();
            dp.add(edittable, DockPanel.NORTH);

            saveButton = new NamedButton("HappeningsView.saveButton", messages
                    .save());
            saveButton.addClickListener(this);
            cancelButton = new NamedButton("HappeningsView.cancelButton",
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

            JSONObject object = HappeningCache.getInstance(constants)
                    .getHappening(id);

            happeningBox.setText(Util.str(object.get("description")));
            descBox.setText(Util.str(object.get("linedesc")));
            String debetpost = Util.str(object.get("debetpost"));
            debetNmbBox.setText(debetpost);
            String kreditpost = Util.str(object.get("kredpost"));
            kreditNmbBox.setText(kreditpost);

            Util.setIndexByValue(debetListBox, debetpost);
            Util.setIndexByValue(kreditListBox, kreditpost);

            boolean required = "1".equals(Util.str(object.get("count_req")));
            countReq.setChecked(required);
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
            final String description = happeningBox.getText();
            final String linedesc = descBox.getText();
            final String debetpost = debetNmbBox.getText();
            final String kredpost = kreditNmbBox.getText();
            final boolean checked = countReq.isChecked();
            final String reqSent = checked ? "1" : "0";
            final String sendId = currentId;

            Util.addPostParam(sb, "description", description);
            Util.addPostParam(sb, "linedesc", linedesc);
            Util.addPostParam(sb, "debetpost", debetpost);
            Util.addPostParam(sb, "kredpost", kredpost);
            Util.addPostParam(sb, "id", sendId);
            Util.addPostParam(sb, "count_req", reqSent);

            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                    constants.baseurl() + "registers/happening.php");

            RequestCallback callback = new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    Window.alert(exception.getMessage());
                }

                public void onResponseReceived(Request request,
                        Response response) {
                    if ("0".equals(response.getText())) {
                        mainErrorLabel.setHTML(messages.save_failed());
                        Util.timedMessage(mainErrorLabel, "", 5);
                    } else {
                        if (sendId == null) {
                            JSONValue value = JSONParser.parse(response
                                    .getText());
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

                            addRow(row, description, linedesc, debetpost,
                                    kredpost, checked, sendId);
                        } else {
                            /* Could probably be more effective but why bother? */
                            HappeningCache.getInstance(constants).flush(me);
                        }
                        hide();
                    }
                }
            };

            try {
                builder.setHeader("Content-Type",
                        "application/x-www-form-urlencoded");
                builder.sendRequest(sb.toString(), callback);
            } catch (RequestException e) {
                Window.alert("Failed to send the request: " + e.getMessage());
            }

        }

        private boolean validateFields() {
            MasterValidator mv = new MasterValidator();
            mv.mandatory(messages.required_field(), new Widget[] {
                    happeningBox, descBox, debetNmbBox, kreditNmbBox });
            mv.registry(messages.registry_invalid_key(), PosttypeCache
                    .getInstance(constants), new Widget[] { debetNmbBox,
                    kreditNmbBox });
            return mv.validateStatus();
        }

        public void init() {
            currentId = null;
            happeningBox.setText("");
            descBox.setText("");
            debetListBox.setSelectedIndex(0);
            kreditListBox.setSelectedIndex(0);
            countReq.setChecked(false);
            debetNmbBox.setText("");
            kreditNmbBox.setText("");
        }
    }

    public void flushCompleted() {
        me.init();
    }
}
