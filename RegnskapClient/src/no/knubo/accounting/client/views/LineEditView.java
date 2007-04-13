package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.cache.PosttypeCache;

import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LineEditView extends Composite implements ClickListener,
		ResponseTextHandler {

	private static LineEditView me;

	public static LineEditView show(I18NAccount messages, Constants constants,
			String line) {
		if (me == null) {
			me = new LineEditView(messages, constants, line);
		}
		return me;
	}

	private FlexTable newFieldTable;

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

	private ListBox fordringBox;

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
			// TODO go fetch new data.
		}

	}

	private Widget regnLinesView() {
		VerticalPanel panel = new VerticalPanel();

		Label header = new Label();
		header.setText(messages.newline());

		panel.add(header);

		FlexTable table = new FlexTable();
		panel.add(table);

		table.setText(0, 1, messages.amount());

		debKredbox = new ListBox();
		debKredbox.setVisibleItemCount(1);
		debKredbox.addItem(messages.debet());
		debKredbox.addItem(messages.kredit());
		table.setWidget(1, 0, debKredbox);

		amountBox = new TextBox();
		amountBox.setVisibleLength(10);
		table.setWidget(1, 1, amountBox);

		table.setText(2, 0, messages.account());
		table.setText(2, 2, messages.fordring());

		accountIdBox = new TextBox();
		accountIdBox.setVisibleLength(6);
		table.setWidget(3, 0, accountIdBox);

		accountNameBox = new ListBox();
		accountNameBox.setVisibleItemCount(1);
		table.setWidget(3, 1, accountNameBox);

		PosttypeCache.getInstance(constants).fill(accountNameBox);

//		fordringBox = new ListBox();
//		fordringBox.setVisibleItemCount(1);
//		table.setWidget(3, 2, fordringBox);

		table.setText(4, 0, messages.project());

		projectIdBox = new TextBox();
		projectIdBox.setVisibleLength(6);
		table.setWidget(5, 0, projectIdBox);

		projectNameBox = new ListBox();
		projectNameBox.setVisibleItemCount(1);
		table.setWidget(5, 1, projectNameBox);

		table.setText(6, 0, messages.person());
		personBox = new ListBox();
		personBox.setVisibleItemCount(1);
		table.setWidget(7, 0, personBox);

		addLineButton = new Button();
		addLineButton.setText(messages.add());
		table.setWidget(8, 1, addLineButton);
		table.getFlexCellFormatter().setColSpan(8, 1, 2);

		return panel;
	}

	private Widget newFields() {
		VerticalPanel vp = new VerticalPanel();

		Label header = new Label(messages.lines());
		vp.add(header);

		newFieldTable = new FlexTable();
		vp.add(newFieldTable);

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

	public void onClick(Widget sender) {
	}

	public void onCompletion(String responseText) {
	}
}
