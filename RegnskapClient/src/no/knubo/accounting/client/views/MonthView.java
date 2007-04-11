package no.knubo.accounting.client.views;

import java.util.Iterator;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.MonthHeaderCache;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MonthView extends Composite implements ResponseTextHandler {

	static class MonthLoader extends LazyLoad {
		private MonthView instance;

		public final Widget getInstance(Constants constants,
				I18NAccount messages) {
			if (instance != null) {
				return instance;
			}
			return (instance = new MonthView(constants, messages));
		}
	}

	private FlexTable table;

	private final Constants constants;

	private Label monthYearLabel;

	private final I18NAccount messages;

	public MonthView(Constants constants, I18NAccount messages) {
		this.constants = constants;
		this.messages = messages;

		DockPanel dockPanel = new DockPanel();
		table = new FlexTable();
		setupHeaders();
		dockPanel.add(table, DockPanel.CENTER);

		HorizontalPanel navPanel = new HorizontalPanel();
		Button backButton = new Button("<");
		Button nextButton = new Button(">");

		monthYearLabel = new Label();
		navPanel.add(backButton);
		navPanel.add(monthYearLabel);
		navPanel.add(nextButton);

		dockPanel.add(navPanel, DockPanel.NORTH);

		// TODO Report stuff as being loaded.
		if (!HTTPRequest.asyncGet(this.constants.baseurl()
				+ "accounting/showmonth.php", this)) {
			// TODO Report errors.
		}

		initWidget(dockPanel);
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

	public static LazyLoad loader() {
		return new MonthLoader();
	}

	public void onCompletion(String responseText) {
		JSONValue jsonValue = JSONParser.parse(responseText);

		JSONObject root = jsonValue.isObject();

		JSONValue year = root.get("year");
		JSONValue month = root.get("month");
		JSONValue lines = root.get("lines");

		monthYearLabel.setText(Util.monthString(messages, month.isString()
				.stringValue())
				+ " " + year.isString().stringValue());

		JSONArray array = lines.isArray();

		String rowStyle = "line1";

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

			table.addCell(rowIndex);
			table.setText(rowIndex, 3, Util.str(rowdata.get("Description")));
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
			table.setText(rowIndex, col, "ERROR");
			return;
		}
		JSONValue value = obj.get(k);
		
		if(value != null) {
			table.setText(rowIndex, col, Util.str(value));
		}
	}
}
