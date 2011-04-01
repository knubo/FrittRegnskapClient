package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.EmploeeCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.cache.ProjectCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.views.modules.CountFields;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class PostView extends DialogBox implements ClickHandler, ServerResponse {

    static PostView me = null;

    private AccountTable table;

    private final I18NAccount messages;

    private final Constants constants;

    private Image editImage;

    private Image closeImage;

    private final ViewCallback caller;

    private String currentId;

    private CountFields countfields;

    private final Elements elements;

    public static PostView show(I18NAccount messages, Constants constants, ViewCallback caller, String line,
            Elements elements) {
        if (me == null) {
            me = new PostView(messages, constants, caller, elements);
        }
        me.init(line);
        return me;
    }

    /**
     * Displays details for a given line.
     * 
     * @param messages
     * @param constants
     * @param line
     * @param caller
     */
    private PostView(I18NAccount messages, Constants constants, ViewCallback caller, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.caller = caller;
        this.elements = elements;
        setText(elements.detailsline());
        table = new AccountTable("tableborder");

        countfields = new CountFields(constants, messages, elements);

        header(0, 0, elements.postnmb(), table);
        header(1, 0, elements.attachment(), table);
        header(2, 0, elements.date(), table);
        header(3, 0, elements.description(), table);
        header(4, 0, elements.edited_by(), table);
        table.insertRow(5);
        table.getFlexCellFormatter().setColSpan(5, 0, 4);
        table.getRowFormatter().setStyleName(4, "showlinebreak");

        /* Widgets placements */
        DockPanel dp = new DockPanel();

        editImage = ImageFactory.editImage("PostView.editImage");
        editImage.addClickHandler(this);
        closeImage = ImageFactory.closeImage("PostView.closeImage");
        closeImage.addClickHandler(this);
        table.setWidget(0, 8, editImage, "right");
        table.setWidget(0, 9, closeImage);

        dp.add(table, DockPanel.NORTH);
        dp.add(countfields.getTable(), DockPanel.NORTH);
        setWidget(dp);

    }

    private void init(String line) {
        while (table.getRowCount() > 6) {
            table.removeRow(6);
        }

        AuthResponder.get(constants, messages, this, "accounting/showline.php?line=" + line);
        countfields.init(line);
    }

    private void header(int row, int col, String text, FlexTable table) {
        table.setText(row, col, text);
        table.getCellFormatter().setStyleName(row, col, "showline");
    }

    public void onClick(ClickEvent event) {
        Widget sender = (Widget) event.getSource();
        if (sender == closeImage) {
            hide();
        } else if (sender == editImage) {
            hide();
            caller.openDetails(currentId);
        }
    }

    public void serverResponse(JSONValue jsonValue) {
        JSONObject object = jsonValue.isObject();

        currentId = Util.str(object.get("Id"));
        table.setText(0, 1, Util.str(object.get("Postnmb")));
        table.setText(1, 1, Util.str(object.get("Attachment")));
        table.getFlexCellFormatter().setColSpan(1, 1, 4);
        table.setText(2, 1, Util.str(object.get("date")));
        table.getFlexCellFormatter().setColSpan(2, 1, 4);
        table.setText(3, 1, Util.str(object.get("Description")), "desc");
        table.getFlexCellFormatter().setColSpan(3, 1, 4);
        table.setText(4, 1, Util.str(object.get("EditedByPersonName")), "desc");

        JSONValue value = object.get("postArray");

        if (value == null) {
            return;
        }
        JSONArray array = value.isArray();
        PosttypeCache postCache = PosttypeCache.getInstance(constants, messages);
        ProjectCache projectCache = ProjectCache.getInstance(constants, messages);
        EmploeeCache emploeeCache = EmploeeCache.getInstance(constants, messages);

        for (int i = 0; i < array.size(); i++) {
            JSONValue postVal = array.get(i);
            JSONObject post = postVal.isObject();

            table.setText(7 + i, 1, Util.debkred(elements, post.get("Debet")));
            table.setText(7 + i, 2, post.get("Post_type") + " - "
                    + postCache.getDescription(Util.str(post.get("Post_type"))));
            table.setText(7 + i, 3, projectCache.getName(Util.str(post.get("Project"))));
            table.setText(7 + i, 4, emploeeCache.getName(Util.str(post.get("Person"))));
            table.setText(7 + i, 5, Util.money(post.get("Amount")));
            table.getCellFormatter().setStyleName(7 + i, 5, "right");
            String personName = Util.strSkipNull(post.get("EditedByPersonName"));

            if (!personName.isEmpty()) {
                table.setText(7 + i, 6, "(" + personName + ")");
            }

            String belonging = Util.strSkipNull(post.get("BelongingDesc"));

            if (!belonging.isEmpty()) {
                table.setText(7 + i, 7, belonging);
            }

            table.getRowFormatter().setStyleName(7 + i, (i % 2 == 0) ? "showlineposts2" : "showlineposts1");
        }
    }
}
