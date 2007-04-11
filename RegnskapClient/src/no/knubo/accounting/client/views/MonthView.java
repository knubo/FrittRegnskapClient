package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
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

		for (int i = 0; i < array.size(); i++) {
			JSONObject rowdata = array.get(i).isObject();
			int rowIndex = table.insertRow(i);
			table.addCell(rowIndex);
			table.setText(rowIndex, 0, str(rowdata.get("postnmb")) + "/"
					+ str(rowdata.get("id")));
			table.addCell(rowIndex);
			table.setText(rowIndex, 1, str(rowdata.get("attachnmb")));
			table.addCell(rowIndex);
			table.setText(rowIndex, 2, Util.formatDate(rowdata.get("occured")));
			table.addCell(rowIndex);
			table.setText(rowIndex,3, str(rowdata.get("description")));
		}

	}

	private String str(JSONValue value) {
		JSONString string = value.isString();

		if (string == null) {
			return value.toString();
		}
		return string.stringValue();
	}
}
