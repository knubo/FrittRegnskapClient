package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.EmploeeCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.cache.ProjectCache;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LineEditView extends Composite implements ClickListener {

	private static LineEditView me;

	private IdHolder removeIdHolder = new IdHolder();

	public static LineEditView show(ViewCallback caller, I18NAccount messages, Constants constants,
			String line) {
		if (me == null) {
			me = new LineEditView(caller, messages, constants, line);
		}
		me.init(line);
		return me;
	}

	private FlexTable postsTable;

	private TextBoxWithErrorText postNmbBox;

	private final I18NAccount messages;

	private final Constants constants;

	private TextBoxWithErrorText dayBox;

	private TextBoxWithErrorText attachmentBox;

	private TextBoxWithErrorText descriptionBox;

	private HTML dateHeader;

	private Button updateButton;

	private ListBox debKredbox;

	private TextBoxWithErrorText amountBox;

	private TextBoxWithErrorText accountIdBox;

	private ListBox accountNameBox;

	// private ListBox fordringBox;
	private Label rowErrorLabel;

	private TextBoxWithErrorText projectIdBox;

	private ListBox projectNameBox;

	private ListBox personBox;

	private Button addLineButton;

	private final ViewCallback caller;

	private LineEditView(ViewCallback caller, I18NAccount messages, Constants constants, String line) {

		this.caller = caller;
		this.messages = messages;
		this.constants = constants;
		DockPanel dp = new DockPanel();
		dp.add(mainFields(), DockPanel.NORTH);
		dp.add(newFields(), DockPanel.NORTH);
		dp.add(regnLinesView(), DockPanel.NORTH);

		initWidget(dp);
	}

	private void init(String line) {
		currentLine = line;

		postNmbBox.setText("");
		dayBox.setText("");
		attachmentBox.setText("");
		descriptionBox.setText("");
		amountBox.setText("");
		accountIdBox.setText("");
		accountNameBox.setSelectedIndex(0);
		projectIdBox.setText("");
		projectNameBox.setSelectedIndex(0);
		personBox.setSelectedIndex(0);

		removeIdHolder.init();
		while (postsTable.getRowCount() > 1) {
			postsTable.removeRow(1);
		}

		addLineButton.setEnabled(line != null);

		if (line != null) {
			showLine(line);
		} else {
			fetchInitalData();
			dayBox.setFocus(true);
		}
	}

	protected String currentYear;

	protected String currentMonth;

	protected String currentLine;

	private Label updateLabel;

	private Image previousImage;

	private Image nextImage;

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
				addSumLineSetDefaults(Util.str(root.get("sum")));
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

		String posttype = Util.str(object.get("Post_type"));
		String person = Util.str(object.get("Person"));
		String project = Util.str(object.get("Project"));
		String amount = Util.money(object.get("Amount"));
		String debkred = Util.str(object.get("Debet"));
		String id = Util.str(object.get("Id"));

		addRegnLine(posttype, person, project, amount, debkred, id);
	}

	private void addRegnLine(String posttype, String person, String project,
			String amount, String debkred, String id) {
		int rowcount = postsTable.getRowCount();

		PosttypeCache postCache = PosttypeCache.getInstance(constants);
		EmploeeCache empCache = EmploeeCache.getInstance(constants);
		ProjectCache projectCache = ProjectCache.getInstance(constants);

		postsTable.setText(rowcount, 0, posttype + "-"
				+ postCache.getDescription(posttype));

		postsTable.getRowFormatter().setStyleName(rowcount,
				(rowcount % 2 == 0) ? "showlineposts2" : "showlineposts1");

		postsTable.setText(rowcount, 1, projectCache.getName(project));
		postsTable.setText(rowcount, 2, empCache.getName(person));

		postsTable.setText(rowcount, 3, Util.debkred(messages, debkred));
		postsTable.setText(rowcount, 4, amount);
		postsTable.getCellFormatter().setStyleName(rowcount, 4, "right");

		Image removeImage = new Image("images/list-remove.png");
		postsTable.setWidget(rowcount, 5, removeImage);
		removeImage.addClickListener(this);

		removeIdHolder.add(id, removeImage);
	}

	/**
	 * Adds sum line and sets default value for amount and debet/kredit. Also
	 * sets default values for comboboxes and projects.
	 * 
	 * @param sumAmount
	 *            The amount to display.
	 */
	private void addSumLineSetDefaults(String sumAmount) {
		int row = postsTable.getRowCount();
		postsTable.setText(row, 0, messages.sum());
		postsTable.setText(row, 4, Util.money(Util.fixMoney(sumAmount)));

		projectNameBox.setSelectedIndex(0);
		projectIdBox.setText("");
		accountNameBox.setSelectedIndex(0);
		accountIdBox.setText("");
		personBox.setSelectedIndex(0);
		
		if (sumAmount.equals("0")) {
			amountBox.setText("");			
			postsTable.getCellFormatter().setStyleName(row, 4, "right");
			return;
		}
		
		postsTable.getCellFormatter().setStyleName(row, 4, "right error");

		if (sumAmount.startsWith("-")) {
			amountBox.setText(sumAmount.substring(1));
			debKredbox.setSelectedIndex(0); // Debet
		} else {
			debKredbox.setSelectedIndex(1); // Kredit
			amountBox.setText(sumAmount);
		}
	}

	private void removeSumLine() {
		postsTable.removeRow(postsTable.getRowCount() - 1);
	}

	private Widget newFields() {
		VerticalPanel panel = new VerticalPanel();

		FlexTable table = new FlexTable();
		table.setStyleName("edittable");
		panel.add(table);

		table.setHTML(0, 1, messages.amount());

		debKredbox = new ListBox();
		debKredbox.setVisibleItemCount(1);
		debKredbox.addItem(messages.debet(), "1");
		debKredbox.addItem(messages.kredit(), "-1");
		table.setWidget(1, 0, debKredbox);

		amountBox = new TextBoxWithErrorText();
		amountBox.setVisibleLength(10);
		table.setWidget(1, 1, amountBox);
		table.getFlexCellFormatter().setColSpan(1, 1, 2);

		table.setText(2, 0, messages.account());

		HTML errorAccountHtml = new HTML();
		accountIdBox = new TextBoxWithErrorText(errorAccountHtml);
		accountIdBox.setVisibleLength(6);
		table.setWidget(3, 0, accountIdBox);
		table.setWidget(3, 2, errorAccountHtml);

		accountNameBox = new ListBox();
		accountNameBox.setVisibleItemCount(1);
		table.setWidget(3, 1, accountNameBox);

		/* Above remove button. */
		table.addCell(3);

		PosttypeCache.getInstance(constants).fill(accountNameBox);
		Util.syncListbox(accountNameBox, accountIdBox.getTextBox());

		// table.setText(2, 2, messages.fordring());
		// fordringBox = new ListBox();
		// fordringBox.setVisibleItemCount(1);
		// table.setWidget(3, 2, fordringBox);

		table.setText(4, 0, messages.project());

		HTML projectErrorLabel = new HTML();

		projectIdBox = new TextBoxWithErrorText(projectErrorLabel);
		projectIdBox.setVisibleLength(6);
		table.setWidget(5, 0, projectIdBox);

		projectNameBox = new ListBox();
		projectNameBox.setVisibleItemCount(1);

		HorizontalPanel hp = new HorizontalPanel();
		hp.add(projectNameBox);
		hp.add(projectErrorLabel);
		table.setWidget(5, 1, hp);
		table.getFlexCellFormatter().setColSpan(5, 1, 2);

		ProjectCache.getInstance(constants).fill(projectNameBox);
		Util.syncListbox(projectNameBox, projectIdBox.getTextBox());

		table.setText(6, 0, messages.person());

		personBox = new ListBox();
		personBox.setVisibleItemCount(1);
		table.setWidget(7, 0, personBox);
		table.getFlexCellFormatter().setColSpan(7, 0, 2);
		EmploeeCache.getInstance(constants).fill(personBox);

		addLineButton = new Button();
		addLineButton.setText(messages.add());
		addLineButton.addClickListener(this);
		table.setWidget(8, 0, addLineButton);

		return panel;
	}

	private Widget regnLinesView() {
		VerticalPanel vp = new VerticalPanel();

		postsTable = new FlexTable();
		postsTable.setStyleName("tableborder");
		vp.add(postsTable);

		postsTable.getRowFormatter().setStyleName(0, "header");
		postsTable.setText(0, 0, messages.account());
		postsTable.setText(0, 1, messages.project());
		postsTable.setText(0, 2, messages.person());
		postsTable.setText(0, 3, messages.debet() + "/" + messages.kredit());
		postsTable.setHTML(0, 4, messages.amount());
		postsTable.getFlexCellFormatter().setColSpan(0, 4, 2);

		return vp;
	}

	private Widget mainFields() {

		VerticalPanel vp = new VerticalPanel();

		dateHeader = new HTML();
		dateHeader.setText("...");
		dateHeader.addClickListener(this);
		vp.add(dateHeader);

		FlexTable table = new FlexTable();
		table.setStyleName("edittable");
		vp.add(table);

		postNmbBox = new TextBoxWithErrorText();
		postNmbBox.setMaxLength(7);
		postNmbBox.setVisibleLength(5);
		table.setWidget(0, 1, postNmbBox);
		table.setText(0, 0, messages.postnmb());

		dayBox = new TextBoxWithErrorText();
		dayBox.setMaxLength(2);
		dayBox.setVisibleLength(2);
		table.setWidget(1, 1, dayBox);
		table.setText(1, 0, messages.day());

		attachmentBox = new TextBoxWithErrorText();
		attachmentBox.setMaxLength(7);
		attachmentBox.setVisibleLength(7);
		table.setWidget(2, 1, attachmentBox);
		table.setText(2, 0, messages.attachment());

		descriptionBox = new TextBoxWithErrorText();
		descriptionBox.setMaxLength(40);
		descriptionBox.setVisibleLength(40);
		table.setWidget(3, 1, descriptionBox);
		table.setText(3, 0, messages.description());

		updateButton = new Button();
		updateButton.setText(messages.update());
		updateButton.addClickListener(this);

		table.setWidget(4, 0, updateButton);
		updateLabel = new Label();
		table.setWidget(4, 1, updateLabel);

		HorizontalPanel hp = new HorizontalPanel();

		previousImage = new Image("images/go-previous.png");
		previousImage.addClickListener(this);

		nextImage = new Image("images/go-next.png");
		nextImage.addClickListener(this);
		hp.add(previousImage);
		hp.add(nextImage);
		table.setWidget(0, 2, hp);

		return vp;
	}

	private void setDateHeader() {
		dateHeader.setHTML(Util.monthString(messages, currentMonth) + " "
				+ currentYear);
	}

	public void onClick(Widget sender) {
		if (sender == updateButton) {
			doUpdate();
		} else if (sender == addLineButton) {
			doRowInsert();
		} else if (sender == nextImage) {

		} else if (sender == previousImage) {

		} else if (sender == dateHeader) {
			caller.viewMonth(currentYear, currentMonth);
		} else {
			doRowRemove(sender);
		}
	}

	private void doRowRemove(Widget sender) {
		final String id = removeIdHolder.findId(sender);

		if (id == null) {
			Window.alert("Failed to find id for delete.");
			return;
		}

		StringBuffer sb = new StringBuffer();

		sb.append("action=delete");
		Util.addPostParam(sb, "id", id);
		Util.addPostParam(sb, "line", currentLine);

		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
				constants.baseurl() + "accounting/editaccountpost.php");

		RequestCallback callback = new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				Window.alert(exception.getMessage());
			}

			public void onResponseReceived(Request request, Response response) {
				if ("0".equals(response.getText().trim())) {
					rowErrorLabel.setText(messages.save_failed());
				} else {
					String[] parts = response.getText().trim().split(":");
					// Parts[0] should be same as id, but I use id.
					removeVisibleRow(id);
					removeSumLine();
					addSumLineSetDefaults(parts[1]);
				}
				Util.timedMessage(rowErrorLabel, "", 5);
			}
		};

		try {
			builder.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			builder.sendRequest(sb.toString(), callback);
		} catch (RequestException e) {
			Window.alert("Failed to send the request: " + e.getMessage());
		}

	}

	protected void removeVisibleRow(String id) {
		for (int i = 1; i < postsTable.getRowCount(); i++) {
			String removeId = removeIdHolder.findId(postsTable.getWidget(i, 5));

			if (id.equals(removeId)) {
				postsTable.removeRow(i);
				resetPostsTableStyle();
				return;
			}
		}
		Window.alert("Failed to remove line for id " + id);
	}

	private void resetPostsTableStyle() {
		for (int row = 1; row < postsTable.getRowCount(); row++) {
			postsTable.getRowFormatter().setStyleName(row,
					(row % 2 == 0) ? "showlineposts2" : "showlineposts1");
		}
	}

	private void doRowInsert() {

		if (!validateRowInsert()) {
			return;
		}
		final String personId = Util.getSelected(personBox);
		final String debk = Util.getSelected(debKredbox);
		final String post_type = accountIdBox.getText();
		final String money = Util.fixMoney(amountBox.getText());
		final String projectId = projectIdBox.getText();

		StringBuffer sb = new StringBuffer();
		sb.append("action=insert");
		Util.addPostParam(sb, "line", currentLine);
		Util.addPostParam(sb, "debet", debk);
		Util.addPostParam(sb, "post_type", post_type);
		Util.addPostParam(sb, "amount", money);
		Util.addPostParam(sb, "project", projectId);
		Util.addPostParam(sb, "person", personId);

		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
				constants.baseurl() + "accounting/editaccountpost.php");

		RequestCallback callback = new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				Window.alert(exception.getMessage());
			}

			public void onResponseReceived(Request request, Response response) {
				String data = response.getText().trim();
				if ("0".equals(data)) {
					rowErrorLabel.setText(messages.save_failed());
				} else {
					removeSumLine();
					String[] parts = data.split(":");

					addRegnLine(post_type, personId, projectId, Util
							.money(money), debk, parts[0]);
					addSumLineSetDefaults(parts[1]);
				}
				Util.timedMessage(updateLabel, "", 5);
			}

		};

		try {
			builder.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			builder.sendRequest(sb.toString(), callback);
		} catch (RequestException e) {
			Window.alert("Failed to send the request: " + e.getMessage());
		}

	}

	private void doUpdate() {
		if (!validateTop()) {
			return;
		}

		updateButton.setEnabled(false);
		updateLabel.setText("...");

		StringBuffer sb = new StringBuffer();

		if (currentLine != null) {
			sb.append("action=update");
		} else {
			sb.append("action=insert");
		}
		Util.addPostParam(sb, "day", dayBox.getText());
		if (currentLine != null) {
			Util.addPostParam(sb, "line", currentLine);
		}
		Util.addPostParam(sb, "desc", descriptionBox.getText());
		Util.addPostParam(sb, "attachment", attachmentBox.getText());
		Util.addPostParam(sb, "postnmb", postNmbBox.getText());
		Util.addPostParam(sb, "month", currentMonth);
		Util.addPostParam(sb, "year", currentYear);

		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
				constants.baseurl() + "accounting/editaccountline.php");

		RequestCallback callback = new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				Window.alert(exception.getMessage());
			}

			public void onResponseReceived(Request request, Response response) {
				if ("0".equals(response.getText().trim())) {
					updateLabel.setText(messages.save_failed());
				} else {
					updateLabel.setText(messages.save_ok());

					if (currentLine == null) {
						currentLine = response.getText().trim();
						addLineButton.setEnabled(true);
					}
				}
				Util.timedMessage(updateLabel, "", 5);
				updateButton.setEnabled(true);
			}
		};

		try {
			builder.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			builder.sendRequest(sb.toString(), callback);
		} catch (RequestException e) {
			Window.alert("Failed to send the request: " + e.getMessage());
		}
	}

	private boolean validateTop() {

		MasterValidator masterValidator = new MasterValidator();

		masterValidator.mandatory(messages.required_field(), new Widget[] {
				descriptionBox, attachmentBox, dayBox, postNmbBox });

		masterValidator.range(messages.field_to_low_zero(), new Integer(1),
				null, new Widget[] { attachmentBox, postNmbBox });

		masterValidator.day(messages.illegal_day(), Integer
				.parseInt(currentYear), Integer.parseInt(currentMonth),
				new Widget[] { dayBox });

		return masterValidator.validateStatus();
	}

	private boolean validateRowInsert() {
		MasterValidator masterValidator = new MasterValidator();

		masterValidator.mandatory(messages.required_field(), new Widget[] {
				amountBox, accountIdBox });

		masterValidator.money(messages.field_money(),
				new Widget[] { amountBox });

		masterValidator.registry(messages.registry_invalid_key(), ProjectCache
				.getInstance(constants), new Widget[] { projectIdBox });

		masterValidator.registry(messages.registry_invalid_key(), PosttypeCache
				.getInstance(constants), new Widget[] { accountIdBox });

		return masterValidator.validateStatus();
	}
}