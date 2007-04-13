package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.EmploeeCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.cache.ProjectCache;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LineEditView extends Composite implements ClickListener {

	private static LineEditView me;

	public static LineEditView show(I18NAccount messages, Constants constants,
			String line) {
		if (me == null) {
			me = new LineEditView(messages, constants, line);
		}
		return me;
	}

	private FlexTable postsTable;

	private TextBox postNmbBox;

	private final I18NAccount messages;

	private final Constants constants;

	private TextBox dayBox;

	private TextBox attachmentBox;

	private TextBox descriptionBox;

	private Label dateHeader;

	private Button updateButton;

	private ListBox debKredbox;

	private TextBox amountBox;

	private TextBox accountIdBox;

	private ListBox accountNameBox;

	// private ListBox fordringBox;

	private TextBox projectIdBox;

	private ListBox projectNameBox;

	private ListBox personBox;

	private Button addLineButton;

	private LineEditView(I18NAccount messages, Constants constants, String line) {

		this.messages = messages;
		this.constants = constants;
		DockPanel dp = new DockPanel();
		dp.add(mainFields(), DockPanel.NORTH);
		dp.add(newFields(), DockPanel.NORTH);
		dp.add(regnLinesView(), DockPanel.NORTH);

		initWidget(dp);
		if (line != null) {
			showLine(line);
		} else {
			fetchInitalData();
			dayBox.setFocus(true);
			addLineButton.setEnabled(false);
		}

	}

	protected String currentYear;

	protected String currentMonth;

	protected String currentLine;

	private void fetchInitalData() {

		ResponseTextHandler rh = new ResponseTextHandler() {
			public void onCompletion(String responseText) {
				JSONValue jsonValue = JSONParser.parse(responseText);

				JSONObject root = jsonValue.isObject();

				currentYear = Util.str(root.get("year"));
				currentMonth = Util.str(root.get("month"));
				setDateHeader();

				attachmentBox.setText(Util.str(root.get("attachment")));
				postNmbBox.setText(Util.str(root.get("postnmb")));
			}

		};
		// TODO Report stuff as being loaded.
		if (!HTTPRequest.asyncGet(constants.baseurl() + "defaults/newline.php",
				rh)) {
			// TODO Report errors.
		}
	}

	private void showLine(String line) {
		ResponseTextHandler rh = new ResponseTextHandler() {
			public void onCompletion(String responseText) {
				JSONValue jsonValue = JSONParser.parse(responseText);

				JSONObject root = jsonValue.isObject();

				currentMonth = Util.getMonth(root.get("date"));
				currentYear = Util.getYear(root.get("date"));
				setDateHeader();
				dayBox.setText(Util.getDay(root.get("date")));

				attachmentBox.setText(Util.str(root.get("Attachment")));
				postNmbBox.setText(Util.str(root.get("Postnmb")));
				descriptionBox.setText(Util.str(root.get("Description")));

				JSONValue value = root.get("postArray");
				JSONArray array = value.isArray();

				for (int i = 0; i < array.size(); i++) {
					addRegnLine(array.get(i));
				}
			}
		};
		// TODO Report stuff as being loaded.
		if (!HTTPRequest.asyncGet(constants.baseurl()
				+ "accounting/editaccountline.php?action=query&line=" + line,
				rh)) {
			Window.alert("Failed to load");
		}
	}

	protected void addRegnLine(JSONValue value) {
		JSONObject object = value.isObject();

		int rowcount = postsTable.getRowCount();

		PosttypeCache postCache = PosttypeCache.getInstance(constants);
		EmploeeCache empCache = EmploeeCache.getInstance(constants);
		ProjectCache projectCache = ProjectCache.getInstance(constants);

		String posttype = Util.str(object.get("Post_type"));
		postsTable.setText(rowcount, 0, posttype + "-"
				+ postCache.getDescription(posttype));
		
		postsTable.getRowFormatter().setStyleName(rowcount,
				(rowcount % 2 == 0) ? "showlineposts2" : "showlineposts1");


		postsTable.setText(rowcount, 1, empCache.getName(Util.str(object
				.get("Person"))));

		postsTable.setText(rowcount, 2, projectCache.getName(Util.str(object
				.get("Project"))));

		postsTable.setText(rowcount, 3, Util.debkred(messages, object
				.get("Debet")));
		postsTable.setText(rowcount, 4, Util.money(object.get("Amount")));

		Image removeImage = new Image("images/list-remove.png");
		postsTable.setWidget(rowcount, 5, removeImage);
	}

	private Widget newFields() {
		VerticalPanel panel = new VerticalPanel();

		Label header = new Label();
		header.setText(messages.newline());

		panel.add(header);

		FlexTable table = new FlexTable();
		panel.add(table);

		table.setHTML(0, 1, messages.amount());

		debKredbox = new ListBox();
		debKredbox.setVisibleItemCount(1);
		debKredbox.addItem(messages.debet());
		debKredbox.addItem(messages.kredit());
		table.setWidget(1, 0, debKredbox);

		amountBox = new TextBox();
		amountBox.setVisibleLength(10);
		table.setWidget(1, 1, amountBox);

		table.setText(2, 0, messages.account());

		accountIdBox = new TextBox();
		accountIdBox.setVisibleLength(6);
		table.setWidget(3, 0, accountIdBox);

		accountNameBox = new ListBox();
		accountNameBox.setVisibleItemCount(1);
		table.setWidget(3, 1, accountNameBox);

		PosttypeCache.getInstance(constants).fill(accountNameBox);
		Util.syncListbox(accountNameBox, accountIdBox);

		// table.setText(2, 2, messages.fordring());
		// fordringBox = new ListBox();
		// fordringBox.setVisibleItemCount(1);
		// table.setWidget(3, 2, fordringBox);

		table.setText(4, 0, messages.project());

		projectIdBox = new TextBox();
		projectIdBox.setVisibleLength(6);
		table.setWidget(5, 0, projectIdBox);

		projectNameBox = new ListBox();
		projectNameBox.setVisibleItemCount(1);
		table.setWidget(5, 1, projectNameBox);
		ProjectCache.getInstance(constants).fill(projectNameBox);
		Util.syncListbox(projectNameBox, projectIdBox);

		table.setText(6, 0, messages.person());
		personBox = new ListBox();
		personBox.setVisibleItemCount(1);
		table.setWidget(7, 0, personBox);
		table.getFlexCellFormatter().setColSpan(7, 0, 2);
		EmploeeCache.getInstance(constants).fill(personBox);

		addLineButton = new Button();
		addLineButton.setText(messages.add());
		table.setWidget(8, 1, addLineButton);
		table.getFlexCellFormatter().setColSpan(8, 1, 2);

		return panel;
	}

	private Widget regnLinesView() {
		VerticalPanel vp = new VerticalPanel();

		Label header = new Label(messages.lines());
		vp.add(header);

		postsTable = new FlexTable();
		postsTable.setStyleName("tableborder");
		vp.add(postsTable);

		postsTable.getRowFormatter().setStyleName(0, "header");
		postsTable.setText(0, 0, messages.account());
		postsTable.setText(0, 1, messages.project());
		postsTable.setText(0, 2, messages.person());
		postsTable.setText(0, 3, messages.debet() + "/" + messages.kredit());
		postsTable.setText(0, 4, messages.amount());

		return vp;
	}

	private Widget mainFields() {

		VerticalPanel vp = new VerticalPanel();

		dateHeader = new Label();
		dateHeader.setText("...");
		vp.add(dateHeader);

		FlexTable table = new FlexTable();
		vp.add(table);

		postNmbBox = new TextBox();
		postNmbBox.setMaxLength(7);
		postNmbBox.setVisibleLength(5);
		table.setWidget(0, 1, postNmbBox);
		table.setText(0, 0, messages.postnmb());

		dayBox = new TextBox();
		dayBox.setMaxLength(2);
		dayBox.setVisibleLength(2);
		table.setWidget(1, 1, dayBox);
		table.setText(1, 0, messages.day());

		attachmentBox = new TextBox();
		attachmentBox.setMaxLength(7);
		attachmentBox.setVisibleLength(7);
		table.setWidget(2, 1, attachmentBox);
		table.setText(2, 0, messages.attachment());

		descriptionBox = new TextBox();
		descriptionBox.setMaxLength(40);
		descriptionBox.setVisibleLength(40);
		table.setWidget(3, 1, descriptionBox);
		table.setText(3, 0, messages.description());

		updateButton = new Button();
		updateButton.setText(messages.update());
		updateButton.addClickListener(this);

		table.setWidget(4, 0, updateButton);

		return vp;
	}

	private void setDateHeader() {
		dateHeader.setText(Util.monthString(messages, currentMonth) + " "
				+ currentYear);
	}

	public void onClick(Widget sender) {
	}

	public void onCompletion(String responseText) {
	}
}
