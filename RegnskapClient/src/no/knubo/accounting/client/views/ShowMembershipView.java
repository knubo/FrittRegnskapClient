package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ShowMembershipView extends Composite implements ClickListener {

    static ShowMembershipView me;

    private FlexTable table;

    private HTML header;

    private HTML periodeHeader;

    private Image previousImage;

    private Image nextImage;

    private final I18NAccount messages;

    private final Constants constants;

    private final ViewCallback caller;

    private String action;

    protected int currentYear;

    protected int currentSemester;

    private IdHolder idHolder;

    public static ShowMembershipView show(I18NAccount messages,
            Constants constants, ViewCallback caller) {
        if (me == null) {
            me = new ShowMembershipView(messages, constants, caller);
        }
        return me;
    }

    public ShowMembershipView(I18NAccount messages, Constants constants,
            ViewCallback caller) {
        this.messages = messages;
        this.constants = constants;
        this.caller = caller;

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, messages.lastname());
        table.setHTML(0, 1, messages.firstname());
        table.setHTML(0, 2, "");
        table.getRowFormatter().setStyleName(0, "header");

        header = new HTML();
        periodeHeader = new HTML();
        previousImage = ImageFactory.previousImage();
        previousImage.addClickListener(this);
        nextImage = ImageFactory.nextImage();
        nextImage.addClickListener(this);
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(previousImage);
        hp.add(periodeHeader);
        hp.add(nextImage);

        DockPanel dp = new DockPanel();
        dp.add(header, DockPanel.NORTH);
        dp.add(hp, DockPanel.NORTH);
        dp.add(table, DockPanel.NORTH);

        initWidget(dp);
        idHolder = new IdHolder();
    }

    public void initShowMembers() {
        header.setHTML(messages.member_heading_year());
        action = "year";
        init("");
    }

    public void initShowTrainingMembers() {
        header.setHTML(messages.member_heading_train());
        action = "training";
        init("");
    }

    public void initShowClassMembers() {
        header.setHTML(messages.member_heading_course());
        action = "class";
        init("");
    }

    private void init(String posparam) {
        setVisible(true);
        idHolder.init();

        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }

        ResponseTextHandler getValues = new ResponseTextHandler() {

            public void onCompletion(String responseText) {
                JSONValue value = JSONParser.parse(responseText);

                if (value == null) {
                    Window.alert("Failed to load data.");
                    return;
                }

                JSONObject root = value.isObject();

                /* Both year and semester isn't present at the same time */
                currentYear = Util.getInt(root.get("year"));
                currentSemester = Util.getInt(root.get("semester"));

                JSONArray members = root.get("members").isArray();

                String count = String.valueOf(members.size());
                periodeHeader.setHTML(messages.members_navig_heading(Util
                        .str(root.get("text")), count));

                for (int i = 0; i < members.size(); i++) {
                    JSONArray names = members.get(i).isArray();

                    int row = i + 1;
                    String firstname = Util.str(names.get(0));
                    String lastname = Util.str(names.get(1));
                    String id = Util.str(names.get(2));
                    table.setText(row, 0, lastname);
                    table.setText(row, 1, firstname);

                    Image editUserImage = ImageFactory.editImage();
                    editUserImage.addClickListener(me);
                    table.setWidget(row, 2, editUserImage);

                    idHolder.add(id, editUserImage);
                    String style = (row % 2 == 0) ? "showlineposts2"
                            : "showlineposts1";
                    table.getRowFormatter().setStyleName(row, style);
                }
            }

        };
        // TODO Report stuff as being loaded.
        if (!HTTPRequest.asyncGet(this.constants.baseurl()
                + "registers/members.php?" + posparam + "&action=" + action,
                getValues)) {
            Window.alert("Failed to load data.");
        }
    }

    public void onClick(Widget sender) {
        if (sender == previousImage) {
            doNext(-1);
        } else if (sender == nextImage) {
            doNext(1);
        } else {
            doEditUser(sender);
        }
    }

    private void doNext(int diff) {
        if (currentSemester != 0) {
            init("semester=" + (currentSemester + diff));
            return;
        }

        init("year=" + (currentYear + diff));

    }

    private void doEditUser(Widget sender) {
        String userId = idHolder.findId(sender);
        setVisible(false);
        caller.editPerson(userId);
    }

}
