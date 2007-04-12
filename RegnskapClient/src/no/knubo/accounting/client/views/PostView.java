package no.knubo.accounting.client.views;

import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class PostView extends DialogBox implements ClickListener {

	static PostView me = null;

	public static PostView show(I18NAccount messages, String line) {
		return new PostView(messages, line);
	}

	/**
	 * Displays details for a given line.
	 * 
	 * @param messages
	 * @param line
	 * 
	 * @param line
	 */
	private PostView(I18NAccount messages, String line) {
		setText(messages.detailsline());
		FlexTable table = new FlexTable();
		table.setStyleName("tableborder");

		header(0, 0, messages.postnmb(), table);
		header(1, 0, messages.attachment(), table);
		header(2, 0, messages.date(), table);
		header(3, 0, messages.description(), table);
		header(4, 0, messages.project(), table);
		header(5, 0, messages.person(), table);
		table.insertRow(6);
		table.getCellFormatter().setStyleName(6, 0, "showlinebreak");
		table.getFlexCellFormatter().setColSpan(6, 0, 4);
		header(7, 0, messages.lines(), table);

		/* Widgets placements */
		DockPanel dp = new DockPanel();
		Button closeButton = new Button("Close", this);
		
		dp.add(table, DockPanel.NORTH);
		dp.add(closeButton, DockPanel.SOUTH);
		setWidget(dp);
	}

	private void header(int row, int col, String text, FlexTable table) {
		table.setText(row, col, text);
		table.getCellFormatter().setStyleName(row, col, "showline");
	}

	public void onClick(Widget sender) {
		hide();
	}
}
