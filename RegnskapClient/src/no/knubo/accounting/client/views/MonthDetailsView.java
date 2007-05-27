package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.EmploeeCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.cache.ProjectCache;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.views.modules.YearMonthComboHelper;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class MonthDetailsView extends Composite implements ResponseTextHandler,
        ClickListener, ChangeListener {

    private static MonthDetailsView me;

    private final Constants constants;

    private final I18NAccount messages;

    private final ViewCallback caller;

    private FlexTable table;

    private Image backImage;

    private Image nextImage;

    private ListBox monthYearCombo;

    private YearMonthComboHelper yearMonthComboHelper;

    private String currentMonth;

    private String currentYear;

    public MonthDetailsView(Constants constants, I18NAccount messages,
            ViewCallback caller) {
        this.constants = constants;
        this.messages = messages;
        this.caller = caller;

        table = new FlexTable();
        table.setStyleName("tableborder");

        table.setHTML(0, 1, messages.attachment());
        table.setHTML(0, 2, messages.date());
        table.setHTML(0, 3, messages.description());
        table.setHTML(0, 4, messages.project());
        table.setHTML(0, 5, messages.employee());
        table.setHTML(0, 6, messages.debkred());
        table.setHTML(0, 7, messages.amount());
        table.getRowFormatter().setStyleName(0, "header");

        DockPanel dp = new DockPanel();

        backImage = ImageFactory.previousImage();
        backImage.addClickListener(this);

        nextImage = ImageFactory.nextImage();
        nextImage.addClickListener(this);

        monthYearCombo = new ListBox();
        monthYearCombo.setMultipleSelect(false);
        monthYearCombo.setVisibleItemCount(1);
        monthYearCombo.addChangeListener(this);

        yearMonthComboHelper = new YearMonthComboHelper(messages, constants,
                monthYearCombo);
        yearMonthComboHelper.fillYearMonthCombo();

        HorizontalPanel navPanel = new HorizontalPanel();
        navPanel.add(backImage);
        navPanel.add(monthYearCombo);
        navPanel.add(nextImage);

        dp.add(navPanel, DockPanel.NORTH);
        dp.add(table, DockPanel.NORTH);

        initWidget(dp);
    }

    public static MonthDetailsView getInstance(Constants constants,
            I18NAccount messages, ViewCallback caller) {
        if (me == null) {
            me = new MonthDetailsView(constants, messages, caller);
        }
        return me;
    }

    public void init() {
        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }
        currentMonth = null;
        currentYear = null;

        if (!HTTPRequest.asyncGet(this.constants.baseurl()
                + "accounting/showmonthpost.php", this)) {
            Window.alert(messages.failedConnect());
        }

    }

    public void onCompletion(String responseText) {
        JSONValue value = JSONParser.parse(responseText);
        JSONArray array = value.isArray();

        ProjectCache projectCache = ProjectCache.getInstance(constants);
        EmploeeCache emploeeCache = EmploeeCache.getInstance(constants);
        PosttypeCache posttypeCache = PosttypeCache.getInstance(constants);

        int row = 0;
        for (int i = 0; i < array.size(); i++) {
            JSONValue one = array.get(i);

            JSONObject object = one.isObject();

            row++;
            table.getRowFormatter().setStyleName(row, "showpostheader");

            table.setText(row, 0, Util.str(object.get("Postnmb")) + "/"
                    + Util.str(object.get("Id")));
            table.getCellFormatter().setStyleName(row, 0, "right");

            table.setText(row, 1, Util.str(object.get("Attachment")));
            table.getCellFormatter().setStyleName(row, 1, "right");

            String date = Util.str(object.get("date"));
            table.setText(row, 2, date);
            table.getCellFormatter().setStyleName(row, 2, "datefor");

            if (currentMonth == null && date != null && date.length() > 0) {
                currentMonth = Util.getMonth(object.get("date")).trim();
            }

            if (currentYear == null && date != null && date.length() > 0) {
                currentYear = Util.getYear(object.get("date")).trim()   ;
            }

            table.setText(row, 3, Util.str(object.get("Description")));
            table.getCellFormatter().setStyleName(row, 3, "desc");
            table.getFlexCellFormatter().setColSpan(row, 3, 5);

            JSONValue postArrVal = object.get("postArray");

            if (postArrVal == null) {
                continue;
            }

            JSONArray postArr = postArrVal.isArray();

            if (postArr == null) {
                continue;
            }

            for (int j = 0; j < postArr.size(); j++) {
                JSONValue postVal = postArr.get(j);

                JSONObject postObj = postVal.isObject();

                row++;

                table.getRowFormatter().setStyleName(
                        row,
                        (j % 2 == 0) ? "smallerfont showlineposts2"
                                : "smallerfont showlineposts1");

                String posttype = Util.str(postObj.get("Post_type"));

                table.setText(row, 3, posttype + " "
                        + posttypeCache.getDescription(posttype));
                table.getCellFormatter().setStyleName(row, 3, "desc");

                table.setText(row, 4, projectCache.getName(Util.str(postObj
                        .get("Project"))));
                table.getCellFormatter().setStyleName(row, 4, "desc");

                table.setText(row, 5, emploeeCache.getName(Util.str(postObj
                        .get("Person"))));
                table.getCellFormatter().setStyleName(row, 5, "desc");

                table.setText(row, 6, Util.debkred(messages, postObj
                        .get("Debet")));

                table.setText(row, 7, Util.money(postObj.get("Amount")));
                table.getCellFormatter().setStyleName(row, 7, "right");

            }
        }

        if (currentMonth != null && currentYear != null) {
            yearMonthComboHelper.setIndex(currentYear, currentMonth);
        }
    }

    public void onClick(Widget sender) {
        // TODO Auto-generated method stub

    }

    public void onChange(Widget sender) {
        ListBox listBox = (ListBox) sender;

        String value = Util.getSelected(listBox);
        String[] monthYear = value.split("/");

        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }
        
        currentYear = null;
        currentMonth = null;
        
        if (!HTTPRequest.asyncGet(this.constants.baseurl()
                + "accounting/showmonthpost.php?year=" + monthYear[0]
                + "&month=" + monthYear[1], this)) {
            Window.alert(messages.failedConnect());
        }

    }
}
