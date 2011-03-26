package no.knubo.accounting.client.views.ownings;

import java.util.Set;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.views.ViewCallback;
import no.knubo.accounting.client.views.modules.RegisterStandards;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OwningChangeReasonPopup extends DialogBox implements ClickHandler {

    private final JSONObject owning;
    private AccountTable table;
    private AccountTable change;
    private TextBoxWithErrorText postNmbBox;
    private TextBoxWithErrorText dayBox;
    private TextBoxWithErrorText attachmentBox;
    private TextBoxWithErrorText descriptionBox;
    private final Elements elements;
    private boolean deleteCallback;
    private NamedButton executeButton;
    private NamedButton closeButton;
    private final OwningChange caller;
    private RegisterStandards registerStandard;
    private final Constants constants;
    private final I18NAccount messages;
    private Label changeLabel;

    public OwningChangeReasonPopup(Elements elements, Constants constants, I18NAccount messages, ViewCallback callback,
            JSONObject current, OwningChange caller) {
        this.elements = elements;
        this.constants = constants;
        this.messages = messages;

        this.owning = current;
        this.caller = caller;

        setText(elements.attachment_change_belonging());

        table = new AccountTable("tableborder");

        registerStandard = new RegisterStandards(constants, messages, elements, callback);

        postNmbBox = registerStandard.getPostNmbBox();
        dayBox = registerStandard.createDayBox();
        attachmentBox = registerStandard.getAttachmentBox();
        descriptionBox = registerStandard.createDescriptionBox();

        table.setText(0, 0, elements.postnmb());
        table.setWidget(0, 1, postNmbBox);

        table.setText(1, 0, elements.attachment());
        table.setWidget(1, 1, attachmentBox);

        table.setText(2, 0, elements.day());
        table.setWidget(2, 1, dayBox);

        table.setText(3, 0, elements.description());
        table.setWidget(3, 1, descriptionBox);

        setModal(true);

        VerticalPanel vp = new VerticalPanel();
        vp.add(table);

        changeLabel = new Label();
        changeLabel.setVisible(false);
        changeLabel.addStyleName("airTop");
        vp.add(changeLabel);
        
        change = new AccountTable("tableborder");
        change.setVisible(false);
        change.addStyleName("airTop");
        vp.add(change);

        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.addStyleName("airTop");

        executeButton = new NamedButton("execute", elements.update());
        executeButton.addClickHandler(this);
        buttonPanel.add(executeButton);

        closeButton = new NamedButton("close", elements.close());
        closeButton.addClickHandler(this);
        buttonPanel.add(closeButton);

        vp.add(buttonPanel);
        add(vp);

        registerStandard.fetchInitalData(true);
    }

    public void delete() {
        descriptionBox.setText(elements.deleted() + " " + Util.str(owning.get("belonging")));
        deleteCallback = true;
        center();
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == closeButton) {
            hide();
            return;
        }

        if (event.getSource() == executeButton) {
            doExecute();
            return;
        }

    }

    private void doExecute() {
        JSONObject data = new JSONObject();
        data.put("postNmb", new JSONString(postNmbBox.getText()));
        data.put("attachment", new JSONString(attachmentBox.getText()));
        data.put("day", new JSONString(dayBox.getText()));
        data.put("description", new JSONString(descriptionBox.getText()));
        data.put("month", new JSONNumber(registerStandard.getCurrentMonth()));
        data.put("year", new JSONNumber(registerStandard.getCurrentYear()));

        if (!registerStandard.validateTop()) {
            return;
        }
        hide();

        if (deleteCallback) {
            caller.deleteExecuted(data);
        } else {
            caller.changeExecuted(data);
        }
    }

    public void modify(JSONValue responseObj) {
        change.setVisible(true);
        changeLabel.setVisible(true);
        change.setText(0, 0, elements.post());
        change.setText(0, 1, "");
        change.setText(0, 2, elements.debet());
        change.setText(0, 3, elements.kredit());
        change.setHeaderRowStyle(0);

        changeLabel.setText(messages.beloning_change_accounting());
        
        PosttypeCache posttypeCache = PosttypeCache.getInstance(constants, messages);

        JSONObject object = responseObj.isObject();

        Set<String> keys = object.keySet();

        int row = 1;
        for (String k : keys) {
            Double d = Util.getDouble(object.get(k));

            change.setText(row, 0, k);
            change.setText(row, 1, posttypeCache.getDescription(k));
            if (d > 0) {
                change.setText(row, 2, Util.money(d));
            } else {
                change.setText(row, 3, Util.money(0 - d));
            }
            row++;
        }

        descriptionBox.setText("Changed " + Util.str(owning.get("belonging")));
        center();
    }

}
