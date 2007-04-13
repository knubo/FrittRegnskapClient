package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
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
		return panel;
	}

	private Widget newFields() {
		newFieldTable = new FlexTable();
		return newFieldTable;
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
		table.setText(1, 1, messages.day());

		attachmentBox = new TextBox();
		attachmentBox.setMaxLength(7);
		attachmentBox.setVisibleLength(7);
		table.setWidget(2, 1, attachmentBox);
		table.setText(2, 1, messages.attachment());

		descriptionBox = new TextBox();
		descriptionBox.setMaxLength(40);
		descriptionBox.setVisibleLength(40);
		table.setWidget(3, 1, descriptionBox);
		table.setText(3, 1, messages.description());

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
