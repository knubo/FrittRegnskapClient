package no.knubo.accounting.client.views;

import java.util.Iterator;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.MonthHeaderCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.views.modules.YearMonthComboHelper;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class MonthView extends Composite implements 
        ClickListener, ChangeListener, ServerResponse {

    private static MonthView instance;

    public static MonthView getInstance(Constants constants, I18NAccount messages,
            ViewCallback caller) {
        if (instance == null) {
            instance = new MonthView(constants, messages, caller);
        }
        return instance;
    }

    private FlexTable table;

    private final Constants constants;

    private ListBox monthYearCombo;

    private final I18NAccount messages;

    private Image backImage;

    private Image nextImage;

    private int currentYear;

    private int currentMonth;

    private DockPanel dockPanel;

    private final ViewCallback caller;

    private YearMonthComboHelper yearMonthComboHelper;

    public MonthView(Constants constants, I18NAccount messages,
            ViewCallback caller) {
        this.constants = constants;
        this.messages = messages;
        this.caller = caller;

        dockPanel = new DockPanel();
        newTable();

        backImage = ImageFactory.previousImage("MonthView.backImage");
        backImage.addClickListener(this);

        nextImage = ImageFactory.nextImage("MonthView.nextImage");
        nextImage.addClickListener(this);

        monthYearCombo = new ListBox();
        monthYearCombo.setMultipleSelect(false);
        monthYearCombo.setVisibleItemCount(1);
        monthYearCombo.addChangeListener(this);

        yearMonthComboHelper = new YearMonthComboHelper(messages, constants,
                monthYearCombo);

        HorizontalPanel navPanel = new HorizontalPanel();
        navPanel.add(backImage);
        navPanel.add(monthYearCombo);
        navPanel.add(nextImage);

        dockPanel.add(navPanel, DockPanel.NORTH);

        initWidget(dockPanel);
    }

    public void init() {
        dockPanel.remove(table);
        newTable();

        getData("");
    }

    public void init(int year, int month) {
        dockPanel.remove(table);
        newTable();
        getData("month=" + month + "&year=" + year);
    }

    private void newTable() {
        table = new FlexTable();
        setupHeaders();
        dockPanel.add(table, DockPanel.CENTER);
    }

    private void getData(String params) {
        yearMonthComboHelper.fillYearMonthCombo();
        
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "accounting/showmonth.php");

        try {
            builder.sendRequest(params, new AuthResponder(constants, this));
        } catch (RequestException e) {
            Window.alert("AU:"+e);
        }
    }

    private void setupHeaders() {
        table.setStyleName("tableborder");
        table.insertRow(0);
        table.insertRow(0);

        table.getRowFormatter().setStyleName(0, "header");
        table.getRowFormatter().setStyleName(1, "debkred");

        int row = 0;
        int col = 1;
        table.addCell(row);
        table.addCell(row);
        table.addCell(row);
        table.addCell(row);

        table.addCell(row + 1);
        table.getFlexCellFormatter().setColSpan(row + 1, 0, 4);
        table.getCellFormatter().setStyleName(row + 1, 0, "leftborder");

        table.setText(row, col++, messages.attachment());
        table.setText(row, col++, messages.date());

        table.setText(row, col++, messages.description());

        /* Colposition for row 2 */
        int col2 = 1;
        List names = MonthHeaderCache.getInstance(constants).headers();

        for (Iterator i = names.iterator(); i.hasNext();) {
            String header = (String) i.next();

            /* The posts headers, using 2 colspan */
            table.getFlexCellFormatter().setColSpan(row, col, 2);
            table.addCell(row);
            table.getCellFormatter().setStyleName(row, col, "center");
            table.setText(row, col++, header);

            /* Add DEBET/KREDIT headers */
            table.addCell(row + 1);
            table.addCell(row + 1);

            table.getCellFormatter().setStyleName(row + 1, col2, "leftborder");
            table.setText(row + 1, col2++, messages.debet());
            table.getCellFormatter().setStyleName(row + 1, col2, "rightborder");
            table.setText(row + 1, col2++, messages.kredit());
        }
    }

    public void serverResponse(String responseText) {
        JSONValue jsonValue = JSONParser.parse(responseText);
        JSONObject root = jsonValue.isObject();

        JSONValue lines = root.get("lines");

        currentYear = Util.getInt(root.get("year"));
        currentMonth = Util.getInt(root.get("month"));

        yearMonthComboHelper.setIndex(currentYear, currentMonth);

        JSONArray array = lines.isArray();

        /* Every 3. line flip style for row */
        String rowStyle = "line1";

        /* Renders table with money data */
        for (int i = 0; i < array.size(); i++) {

            JSONObject rowdata = array.get(i).isObject();

            if (rowdata == null) {
                Window.alert("Didn't get rowdata:" + array.get(i));
                return;
            }
            /* +2 to skip headers. */
            int rowIndex = table.insertRow(i + 2);
            table.getRowFormatter().setStyleName(rowIndex, rowStyle);

            if (i % 3 == 2) {
                rowStyle = (rowStyle.equals("line1")) ? "line2" : "line1";
            }

            table.addCell(rowIndex);
            table.setText(rowIndex, 0, Util.str(rowdata.get("Postnmb")) + "/"
                    + Util.str(rowdata.get("Id")));
            table.getCellFormatter().setStyleName(rowIndex, 0, "right");

            table.addCell(rowIndex);
            table.setText(rowIndex, 1, Util.str(rowdata.get("Attachment")));
            table.getCellFormatter().setStyleName(rowIndex, 1, "right");

            table.addCell(rowIndex);
            table.setText(rowIndex, 2, Util.str(rowdata.get("date")));
            table.getCellFormatter().setStyleName(rowIndex, 2, "datefor");

            Hyperlink link = new Hyperlink(
                    Util.str(rowdata.get("Description")), "detail"
                            + Util.str(rowdata.get("Id")));
            link.addClickListener(this);
            table.setWidget(rowIndex, 3, link);

            table.getCellFormatter().setStyleName(rowIndex, 3, "desc");

            render_posts(rowIndex, rowdata.get("groupDebetMonth"), rowdata
                    .get("groupKredMonth"));
        }
    }

    private void render_posts(int rowIndex, JSONValue debet, JSONValue kred) {
        JSONObject debetObj = debet.isObject();
        JSONObject kredObj = kred.isObject();

        int col = 4;
        for (Iterator i = MonthHeaderCache.getInstance(constants).keys()
                .iterator(); i.hasNext();) {

            String k = (String) i.next();

            /* DEBET */
            printDebKredVal(rowIndex, debetObj, col, k);
            col++;

            /* KREDIT */
            printDebKredVal(rowIndex, kredObj, col, k);
            col++;
        }
    }

    private void printDebKredVal(int rowIndex, JSONObject obj, int col, String k) {
        table.addCell(rowIndex);
        table.getCellFormatter().setStyleName(rowIndex, col, "right");

        if (obj == null) {
            return;
        }
        JSONValue value = obj.get(k);

        if (value != null) {
            table.setText(rowIndex, col, Util.money(Util.fixMoney(Util
                    .str(value))));
        }
    }

    public void onClick(Widget sender) {
        if (sender == backImage) {
            newView(false);
        } else if (sender == nextImage) {
            newView(true);
        } else {
            Hyperlink link = (Hyperlink) sender;

            String token = link.getTargetHistoryToken();
            String line = token.substring(6);
            openDetails(link, line);
        }
    }

    /**
     * Opens up the detail window for a given line.
     * 
     * @param link
     * 
     * @param line
     */
    private void openDetails(Hyperlink link, String line) {
        PostView pv = PostView.show(messages, constants, caller, line);
        int left = link.getAbsoluteLeft() + 100;
        int top = link.getAbsoluteTop() + 10;
        pv.setPopupPosition(left, top);
        pv.show();
    }

    private void newView(boolean nextMonth) {
        dockPanel.remove(table);
        newTable();
        if (nextMonth) {
            int m = currentMonth + 1;

            if (m > 12) {
                int y = currentYear + 1;
                getData("month=1&year=" + y);
            } else {
                getData("month=" + m + "&year=" + currentYear);
            }
        } else {
            int m = currentMonth - 1;

            if (m < 1) {
                int y = currentYear - 1;
                getData("month=12&year=" + y);
            } else {
                getData("month=" + m + "&year=" + currentYear);
            }
        }
    }

    public void onChange(Widget sender) {
        ListBox listBox = (ListBox) sender;

        String value = Util.getSelected(listBox);
        String[] monthYear = value.split("/");
        init(Integer.parseInt(monthYear[0]), Integer.parseInt(monthYear[1]));
    }

   
}
