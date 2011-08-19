package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ShowMembershipView extends Composite implements ClickHandler, ChangeHandler {

    static ShowMembershipView me;
    private FlexTable table;
    private HTML header;
    private final I18NAccount messages;
    private final Constants constants;
    private final ViewCallback caller;
    private String action;
    protected int currentYear;
    protected int currentSemester;
    private IdHolder<String, Image> idHolder;
    final HelpPanel helpPanel;
    private final Elements elements;
    private ListBoxWithErrorText semesterOrYearListBox;
    private Label countLabel;

    public static ShowMembershipView show(I18NAccount messages, Constants constants, ViewCallback caller,
            HelpPanel helpPanel, Elements elements) {
        if (me == null) {
            me = new ShowMembershipView(messages, constants, caller, helpPanel, elements);
        }
        return me;
    }

    public ShowMembershipView(I18NAccount messages, Constants constants, ViewCallback caller, HelpPanel helpPanel,
            Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.caller = caller;
        this.helpPanel = helpPanel;
        this.elements = elements;

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setText(0, 0, elements.lastname());
        table.setText(0, 1, elements.firstname());
        table.setText(0, 6, "");
        table.getRowFormatter().setStyleName(0, "header");

        header = new HTML();

        semesterOrYearListBox = new ListBoxWithErrorText("choose");
        semesterOrYearListBox.addChangeHandler(this);

        countLabel = new Label();

        DockPanel dp = new DockPanel();
        dp.add(header, DockPanel.NORTH);
        dp.add(semesterOrYearListBox, DockPanel.NORTH);
        dp.add(countLabel, DockPanel.NORTH);
        dp.add(table, DockPanel.NORTH);

        initWidget(dp);
        idHolder = new IdHolder<String, Image>();
    }

    public void initShowAll() {
        header.setHTML(elements.member_heading_all());
        table.setText(0, 2, elements.year_membership());
        table.setText(0, 3, elements.course_membership());
        table.setText(0, 4, elements.train_membership());
        table.setText(0, 5, elements.youth_membership());
        action = "";
        fillSemesterlistbox("alllist");
    }

    public void initShowMembers() {
        header.setHTML(elements.member_heading_year());
        action = "year";
        fillYearListbox();

    }

    public void initShowTrainingMembers() {
        header.setHTML(elements.member_heading_train());
        action = "training";
        fillSemesterlistbox("trainlist");

    }

    public void initShowClassMembers() {
        header.setHTML(elements.member_heading_course());
        action = "class";
        fillSemesterlistbox("classlist");

    }

    private void fillSemesterlistbox(final String action) {
        semesterOrYearListBox.clear();
        semesterOrYearListBox.addItem("","");
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                JSONArray array = responseObj.isArray();

                for (int i = 0; i < array.size(); i++) {
                    JSONObject semester = array.get(i).isObject();

                    semesterOrYearListBox.addItem(semester.get("description"), semester.get("semester"));
                }

                if (action.equals("alllist")) {
                    initAll("");
                } else {
                    init("");
                }
            }
        };
        AuthResponder.get(constants, messages, callback, "registers/members.php?action=" + action);

    }

    private void fillYearListbox() {
        semesterOrYearListBox.clear();
        semesterOrYearListBox.addItem("","");
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                JSONArray array = responseObj.isArray();

                for (int i = 0; i < array.size(); i++) {
                    JSONValue year = array.get(i);

                    semesterOrYearListBox.addItem(year, year);
                }
                init("");
            }
        };
        AuthResponder.get(constants, messages, callback, "registers/members.php?action=yearlist");

    }

    private void init(String posparam) {
        setVisible(true);
        idHolder.init();

        table.setHTML(0, 2, "");
        table.setHTML(0, 3, "");
        table.setHTML(0, 4, "");
        table.setHTML(0, 5, "");

        if (action.equals("year")) {
            table.setText(0, 2, elements.youth());
        }

        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {

                JSONObject root = value.isObject();

                /* Both year and semester isn't present at the same time */
                currentYear = Util.getInt(root.get("year"));
                currentSemester = Util.getInt(root.get("semester"));

                JSONArray members = root.get("members").isArray();

                String count = String.valueOf(members.size());
                
                String match = action.equals("year") ? String.valueOf(currentYear) : String.valueOf(currentSemester);
                Util.setIndexByValue(semesterOrYearListBox.getListbox(), match);
                
                countLabel.setText(messages.members_navig_heading(count));

                for (int i = 0; i < members.size(); i++) {
                    JSONArray names = members.get(i).isArray();

                    int row = i + 1;
                    String id = Util.str(names.get(2));

                    table.setText(row, 1, Util.str(names.get(0)));
                    table.setText(row, 0, Util.str(names.get(1)));

                    if (action.equals("year")) {
                        table.setText(row, 2, Util.str(names.get(3)).equals("1") ? "X" : "");
                        table.getCellFormatter().setStyleName(row, 2, "center");
                    }

                    Image editUserImage = ImageFactory.editImage("ShowMembershipView.editUserImage");
                    editUserImage.addClickHandler(me);
                    table.setWidget(row, 6, editUserImage);

                    idHolder.add(id, editUserImage);
                    String style = (row % 2 == 0) ? "showlineposts2" : "showlineposts1";
                    table.getRowFormatter().setStyleName(row, style);
                }
                helpPanel.resize(me);
            }
        };

        AuthResponder.get(constants, messages, callback, "registers/members.php?" + posparam + "&action=" + action);

    }

    public void onClick(ClickEvent event) {
        Widget sender = (Widget) event.getSource();

        doEditUser(sender);
    }

    private void doEditUser(Widget sender) {
        String userId = idHolder.findId(sender);
        setVisible(false);
        caller.editPerson(userId);
    }

    private void initAll(String posparam) {
        setVisible(true);
        idHolder.init();

        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {

                JSONObject root = value.isObject();

                /* Both year and semester isn't present at the same time */
                currentYear = Util.getInt(root.get("year"));
                currentSemester = Util.getInt(root.get("semester"));

                JSONArray members = root.get("members").isArray();

                String count = String.valueOf(members.size());

                Util.setIndexByValue(semesterOrYearListBox.getListbox(), String.valueOf(currentSemester));
                countLabel.setText(messages.members_navig_heading(count));

                for (int i = 0; i < members.size(); i++) {
                    JSONObject info = members.get(i).isObject();

                    int row = i + 1;
                    String firstname = Util.str(info.get("first"));
                    String lastname = Util.str(info.get("last"));

                    boolean year = Util.getBoolean(info.get("year"));
                    boolean train = Util.getBoolean(info.get("train"));
                    boolean course = Util.getBoolean(info.get("course"));
                    boolean youth = Util.getBoolean(info.get("youth"));

                    String id = Util.str(info.get("id"));
                    table.setText(row, 0, lastname);
                    table.setText(row, 1, firstname);

                    if (year) {
                        table.setText(row, 2, elements.x());
                    }
                    if (course) {
                        table.setText(row, 3, elements.x());
                    }
                    if (train) {
                        table.setText(row, 4, elements.x());
                    }
                    if (youth) {
                        table.setText(row, 5, elements.x());
                    }

                    table.getCellFormatter().setStyleName(row, 2, "center");
                    table.getCellFormatter().setStyleName(row, 3, "center");
                    table.getCellFormatter().setStyleName(row, 4, "center");
                    table.getCellFormatter().setStyleName(row, 5, "center");

                    Image editUserImage = ImageFactory.editImage("ShowMembershipView.editUserImage");
                    editUserImage.addClickHandler(me);
                    table.setWidget(row, 6, editUserImage);

                    idHolder.add(id, editUserImage);
                    String style = (row % 2 == 0) ? "showlineposts2" : "showlineposts1";
                    table.getRowFormatter().setStyleName(row, style);
                }
                helpPanel.resize(me);
            }
        };

        AuthResponder.get(constants, messages, callback, "registers/members.php?" + posparam + "&action=all");

    }

    public void onChange(ChangeEvent event) {
        if(this.semesterOrYearListBox.getSelectedIndex() == 0) {
            return;
        }
        if(action.equals("")) {
            initAll("semester="+Util.getSelected(this.semesterOrYearListBox));
        } else if(action.equals("year")){
            init("year="+Util.getSelected(this.semesterOrYearListBox));
        } else {
            init("semester="+Util.getSelected(this.semesterOrYearListBox));
        }
    }
}
