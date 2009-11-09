package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.CacheCallback;
import no.knubo.accounting.client.cache.HappeningCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponseWithErrorFeedback;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class HappeningsView extends Composite implements ClickHandler,
		CacheCallback {

	private static HappeningsView me;

	private final Constants constants;

	private final I18NAccount messages;

	private FlexTable table;

	private IdHolder<String, Image> idHolder;

	private Button newButton;

	private PosttypeCache posttypeCache;

	private HappeningEditFields editFields;

	private final Elements elements;

	public HappeningsView(I18NAccount messages, Constants constants,
			Elements elements) {
		this.messages = messages;
		this.constants = constants;
		this.elements = elements;

		DockPanel dp = new DockPanel();

		table = new FlexTable();
		table.setStyleName("tableborder");
		table.setHTML(0, 0, elements.happening());
		table.setHTML(0, 1, elements.post_description());
		table.setHTML(0, 2, elements.debet_post());
		table.setHTML(0, 3, elements.kredit_post());
		table.setHTML(0, 4, elements.count_required());
		table.setHTML(0, 5, "");
		table.getRowFormatter().setStyleName(0, "header");

		newButton = new NamedButton("HappeningsView.newButton", elements
				.new_happening());
		newButton.addClickHandler(this);

		dp.add(newButton, DockPanel.NORTH);
		dp.add(table, DockPanel.NORTH);

		idHolder = new IdHolder<String, Image>();
		initWidget(dp);
	}

	public static HappeningsView show(I18NAccount messages,
			Constants constants, Elements elements) {
		if (me == null) {
			me = new HappeningsView(messages, constants, elements);
		}
		me.setVisible(true);
		return me;
	}

	public void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();
		
		if (editFields == null) {
			editFields = new HappeningEditFields();
		}

		int left = 0;
		if (sender == newButton) {
			left = sender.getAbsoluteLeft() + 10;
		} else {
			left = sender.getAbsoluteLeft() - 250;
		}

		int top = sender.getAbsoluteTop() + 10;
		editFields.setPopupPosition(left, top);

		if (sender == newButton) {
			editFields.init();
		} else {
			editFields.init(idHolder.findId(sender));
		}
		editFields.show();
	}

	public void init() {
		posttypeCache = PosttypeCache.getInstance(constants, messages);
		idHolder.init();

		while (table.getRowCount() > 1) {
			table.removeRow(1);
		}

		HappeningCache cache = HappeningCache.getInstance(constants, messages);

		int row = 1;

		for (JSONObject object : cache.getAll()) {

			String description = Util.str(object.get("description"));
			String linedesc = Util.str(object.get("linedesc"));
			String debetpost = Util.str(object.get("debetpost"));
			String kredpost = Util.str(object.get("kredpost"));
			boolean required = "1".equals(Util.str(object.get("count_req")));
			String id = Util.str(object.get("id"));

			addRow(row, description, linedesc, debetpost, kredpost, required,
					id);
			row++;
		}
	}

	private void addRow(int row, String description, String linedesc,
			String debetpost, String kredpost, boolean required, String id) {
		table.setHTML(row, 0, description);
		table.setHTML(row, 1, linedesc);
		table.setHTML(row, 2, posttypeCache.getDescriptionWithType(debetpost));
		table.setHTML(row, 3, posttypeCache.getDescriptionWithType(kredpost));

		for (int i = 0; i < 4; i++) {
			table.getCellFormatter().setStyleName(row, i, "desc");
		}

		CheckBox box = new CheckBox();
		box.setEnabled(false);
		box.setValue(required);
		table.setWidget(row, 4, box);
		table.getCellFormatter().setStyleName(row, 4, "center");

		Image editImage = ImageFactory.editImage("HappeningsView.editImage");
		editImage.addClickHandler(me);
		idHolder.add(id, editImage);

		table.setWidget(row, 5, editImage);

		String style = (((row + 2) % 6) < 3) ? "line2" : "line1";
		table.getRowFormatter().setStyleName(row, style);
	}

	class HappeningEditFields extends DialogBox implements ClickHandler {
		private TextBoxWithErrorText happeningBox;

		private TextBoxWithErrorText descBox;

		private ListBox debetListBox;

		private ListBox kreditListBox;

		private CheckBox countReq;

		private Button saveButton;

		private TextBoxWithErrorText debetNmbBox;

		private TextBoxWithErrorText kreditNmbBox;

		private Button cancelButton;

		private String currentId;

		private HTML mainErrorLabel;

		HappeningEditFields() {
			setText(elements.happening());
			FlexTable edittable = new FlexTable();
			edittable.setStyleName("edittable");

			edittable.setHTML(0, 0, elements.happening());
			happeningBox = new TextBoxWithErrorText("happening");
			happeningBox.setMaxLength(40);
			happeningBox.setVisibleLength(40);
			edittable.setWidget(0, 1, happeningBox);

			edittable.setHTML(1, 0, elements.post_description());
			descBox = new TextBoxWithErrorText("description");
			descBox.setMaxLength(80);
			descBox.setVisibleLength(80);
			edittable.setWidget(1, 1, descBox);

			edittable.setHTML(2, 0, elements.debet_post());
			debetListBox = new ListBox();
			debetListBox.setVisibleItemCount(1);
			posttypeCache.fillAllPosts(debetListBox);

			HTML debetErrorLabel = new HTML();
			debetNmbBox = new TextBoxWithErrorText("debetpost", debetErrorLabel);
			debetNmbBox.setMaxLength(5);
			debetNmbBox.setVisibleLength(5);
			Util.syncListbox(debetListBox, debetNmbBox.getTextBox());

			HorizontalPanel debetPanel = new HorizontalPanel();
			debetPanel.add(debetNmbBox);
			debetPanel.add(debetListBox);
			debetPanel.add(debetErrorLabel);
			edittable.setWidget(2, 1, debetPanel);

			edittable.setHTML(3, 0, elements.kredit_post());
			kreditListBox = new ListBox();
			kreditListBox.setVisibleItemCount(1);
			posttypeCache.fillAllPosts(kreditListBox);

			HTML kreditErrorLabel = new HTML();
			kreditNmbBox = new TextBoxWithErrorText("creditpost",
					kreditErrorLabel);
			kreditNmbBox.setMaxLength(5);
			kreditNmbBox.setVisibleLength(5);
			Util.syncListbox(kreditListBox, kreditNmbBox.getTextBox());

			HorizontalPanel kredPanel = new HorizontalPanel();
			kredPanel.add(kreditNmbBox);
			kredPanel.add(kreditListBox);
			kredPanel.add(kreditErrorLabel);
			edittable.setWidget(3, 1, kredPanel);

			edittable.setHTML(4, 0, elements.count_required());
			countReq = new CheckBox();
			edittable.setWidget(4, 1, countReq);

			DockPanel dp = new DockPanel();
			dp.add(edittable, DockPanel.NORTH);

			saveButton = new NamedButton("HappeningsView.saveButton", elements
					.save());
			saveButton.addClickHandler(this);
			cancelButton = new NamedButton("HappeningsView.cancelButton",
					elements.cancel());
			cancelButton.addClickHandler(this);

			mainErrorLabel = new HTML();
			mainErrorLabel.setStyleName("error");

			HorizontalPanel buttonPanel = new HorizontalPanel();
			buttonPanel.add(saveButton);
			buttonPanel.add(cancelButton);
			buttonPanel.add(mainErrorLabel);
			dp.add(buttonPanel, DockPanel.NORTH);
			setWidget(dp);
		}

		public void init(String id) {
			currentId = id;

			JSONObject object = HappeningCache.getInstance(constants, messages)
					.getHappening(id);

			happeningBox.setText(Util.str(object.get("description")));
			descBox.setText(Util.str(object.get("linedesc")));
			String debetpost = Util.str(object.get("debetpost"));
			debetNmbBox.setText(debetpost);
			String kreditpost = Util.str(object.get("kredpost"));
			kreditNmbBox.setText(kreditpost);

			Util.setIndexByValue(debetListBox, debetpost);
			Util.setIndexByValue(kreditListBox, kreditpost);

			boolean required = "1".equals(Util.str(object.get("count_req")));
			countReq.setValue(required);
		}

	    public void onClick(ClickEvent event) {
	    	Widget sender = (Widget) event.getSource();
	    	
			if (sender == cancelButton) {
				hide();
			} else if (sender == saveButton && validateFields()) {
				doSave();
			}
		}

		private void doSave() {
			StringBuffer sb = new StringBuffer();
			sb.append("action=save");
			final String description = happeningBox.getText();
			final String linedesc = descBox.getText();
			final String debetpost = debetNmbBox.getText();
			final String kredpost = kreditNmbBox.getText();
			final boolean checked = countReq.getValue();
			final String reqSent = checked ? "1" : "0";
			final String sendId = currentId;

			Util.addPostParam(sb, "description", description);
			Util.addPostParam(sb, "linedesc", linedesc);
			Util.addPostParam(sb, "debetpost", debetpost);
			Util.addPostParam(sb, "kredpost", kredpost);
			Util.addPostParam(sb, "id", sendId);
			Util.addPostParam(sb, "count_req", reqSent);

			ServerResponseWithErrorFeedback callback = new ServerResponseWithErrorFeedback() {

				public void serverResponse(JSONValue value) {
					if (sendId == null) {
						if (value == null) {
							mainErrorLabel
									.setHTML(messages.save_failed_badly());
							Util.timedMessage(mainErrorLabel, "", 5);
							return;
						}
						JSONObject object = value.isObject();

						if (object == null) {
							String error = "Failed to save data - null object.";
							Window.alert(error);
							return;
						}
						int row = table.getRowCount();

						addRow(row, description, linedesc, debetpost, kredpost,
								checked, sendId);
					} else {
						/* Could probably be more effective but why bother? */
						HappeningCache.getInstance(constants, messages).flush(
								me);
					}
					hide();
				}

				public void onError() {
					mainErrorLabel.setHTML(messages.save_failed());
					Util.timedMessage(mainErrorLabel, "", 5);
				}
			};

			AuthResponder.post(constants, messages, callback, sb,
					"registers/happening.php");

		}

		private boolean validateFields() {
			MasterValidator mv = new MasterValidator();
			mv.mandatory(messages.required_field(), new Widget[] {
					happeningBox, descBox, debetNmbBox, kreditNmbBox });
			mv.registry(messages.registry_invalid_key(), PosttypeCache
					.getInstance(constants, messages), new Widget[] {
					debetNmbBox, kreditNmbBox });
			return mv.validateStatus();
		}

		public void init() {
			currentId = null;
			happeningBox.setText("");
			descBox.setText("");
			debetListBox.setSelectedIndex(0);
			kreditListBox.setSelectedIndex(0);
			countReq.setValue(false);
			debetNmbBox.setText("");
			kreditNmbBox.setText("");
		}
	}

	public void flushCompleted() {
		me.init();
	}
}
