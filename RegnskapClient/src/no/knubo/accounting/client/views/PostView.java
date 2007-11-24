package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.EmploeeCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.cache.ProjectCache;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.views.modules.CountFields;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class PostView extends DialogBox implements ClickListener, ResponseTextHandler {

    static PostView me = null;

    private FlexTable table;

    private final I18NAccount messages;

    private final Constants constants;

    private Image editImage;

    private Image closeImage;

    private final ViewCallback caller;

    private String currentId;

    private CountFields countfields;

    private final Elements elements;

    public static PostView show(I18NAccount messages, Constants constants, ViewCallback caller,
            String line, Elements elements) {
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
    private PostView(I18NAccount messages, Constants constants, ViewCallback caller,
            Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.caller = caller;
        this.elements = elements;
        setText(elements.detailsline());
        table = new FlexTable();
        table.setStyleName("tableborder");

        countfields = new CountFields(constants, messages, elements);

        header(0, 0, elements.postnmb(), table);
        header(1, 0, elements.attachment(), table);
        header(2, 0, elements.date(), table);
        header(3, 0, elements.description(), table);
        table.insertRow(4);
        table.getFlexCellFormatter().setColSpan(4, 0, 4);
        table.getRowFormatter().setStyleName(4, "showlinebreak");
        header(5, 0, elements.lines(), table);

        /* Widgets placements */
        DockPanel dp = new DockPanel();

        editImage = ImageFactory.editImage("PostView.editImage");
        editImage.addClickListener(this);
        closeImage = ImageFactory.closeImage("PostView.closeImage");
        closeImage.addClickListener(this);
        table.setWidget(0, 5, editImage);
        table.setWidget(0, 6, closeImage);

        dp.add(table, DockPanel.NORTH);
        dp.add(countfields.getTable(), DockPanel.NORTH);
        setWidget(dp);

    }

    private void init(String line) {
        while (table.getRowCount() > 5) {
            table.removeRow(5);
        }

        // TODO Report stuff as being loaded.
        if (!HTTPRequest.asyncGet(constants.baseurl() + "accounting/showline.php?line=" + line,
                this)) {
            // TODO Report errors.
        }
        countfields.init(line);
    }

    private void header(int row, int col, String text, FlexTable table) {
        table.setText(row, col, text);
        table.getCellFormatter().setStyleName(row, col, "showline");
    }

    public void onClick(Widget sender) {
        if (sender == closeImage) {
            hide();
        } else if (sender == editImage) {
            hide();
            caller.openDetails(currentId);
        }
    }

    public void onCompletion(String responseText) {
        JSONValue jsonValue = JSONParser.parse(responseText);
        JSONObject object = jsonValue.isObject();

        currentId = Util.str(object.get("Id"));
        table.setText(0, 1, Util.str(object.get("Postnmb")));
        table.setText(1, 1, Util.str(object.get("Attachment")));
        table.getFlexCellFormatter().setColSpan(1, 1, 4);
        table.setText(2, 1, Util.str(object.get("date")));
        table.getFlexCellFormatter().setColSpan(2, 1, 4);
        table.setText(3, 1, Util.str(object.get("Description")));
        table.getFlexCellFormatter().setColSpan(3, 1, 4);

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

            table.setText(6 + i, 1, Util.debkred(elements, post.get("Debet")));
            table.setText(6 + i, 2, postCache.getDescription(Util.str(post.get("Post_type"))));
            table.setText(6 + i, 3, projectCache.getName(Util.str(post.get("Project"))));
            table.setText(6 + i, 4, emploeeCache.getName(Util.str(post.get("Person"))));
            table.setText(6 + i, 5, Util.money(post.get("Amount")));
            table.getCellFormatter().setStyleName(6 + i, 5, "right");

            table.getRowFormatter().setStyleName(6 + i,
                    (i % 2 == 0) ? "showlineposts2" : "showlineposts1");
        }
    }
}
