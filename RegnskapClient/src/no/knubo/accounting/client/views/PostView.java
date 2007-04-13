package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class PostView extends DialogBox implements ClickListener,
		ResponseTextHandler {

	static PostView me = null;

	private FlexTable table;

	private final I18NAccount messages;

	private final Constants constants;

	private Image editImage;

	private Image closeImage;

	public static PostView show(I18NAccount messages, Constants constants,
			String line) {
		if (me == null) {
			me = new PostView(messages, constants, line);
		}
		return me;
	}

	/**
	 * Displays details for a given line.
	 * 
	 * @param messages
	 * @param constants
	 * @param line
	 * 
	 * @param line
	 */
	private PostView(I18NAccount messages, Constants constants, String line) {
		this.messages = messages;
		this.constants = constants;
		setText(messages.detailsline());
		table = new FlexTable();
		table.setStyleName("tableborder");

		header(0, 0, messages.postnmb(), table);
		header(1, 0, messages.attachment(), table);
		header(2, 0, messages.date(), table);
		header(3, 0, messages.description(), table);
		table.insertRow(4);
		table.getCellFormatter().setStyleName(4, 0, "showlinebreak");
		table.getFlexCellFormatter().setColSpan(4, 0, 4);
		header(5, 0, messages.lines(), table);

		/* Widgets placements */
		DockPanel dp = new DockPanel();

		editImage = new Image("images/edit-find-replace.png");
		closeImage = new Image("images/close.png");
		closeImage.addClickListener(this);
		table.setWidget(0, 5, editImage);
		table.setWidget(0, 6, closeImage);

		dp.add(table, DockPanel.NORTH);
		setWidget(dp);

		// TODO Report stuff as being loaded.
		if (!HTTPRequest.asyncGet(constants.baseurl()
				+ "accounting/showline.php?line=" + line, this)) {
			// TODO Report errors.
		}
	}

	private void header(int row, int col, String text, FlexTable table) {
		table.setText(row, col, text);
		table.getCellFormatter().setStyleName(row, col, "showline");
	}

	public void onClick(Widget sender) {
		if (sender == closeImage) {
			hide();
		}
	}

	public void onCompletion(String responseText) {
		JSONValue jsonValue = JSONParser.parse(responseText);
		JSONObject object = jsonValue.isObject();

		table.setText(0, 1, Util.str(object.get("Postnmb")));
		table.setText(1, 1, Util.str(object.get("Attachment")));
		table.getFlexCellFormatter().setColSpan(1, 1, 4);
		table.setText(2, 1, Util.str(object.get("date")));
		table.getFlexCellFormatter().setColSpan(2, 1, 4);
		table.setText(3, 1, Util.str(object.get("Description")));
		table.getFlexCellFormatter().setColSpan(3, 1, 4);

		JSONValue value = object.get("postArray");

		if (value == null) {
			return;
		}
		JSONArray array = value.isArray();
		PosttypeCache postCache = PosttypeCache.getInstance(constants);

		for (int i = 0; i < array.size(); i++) {
			JSONValue postVal = array.get(i);
			JSONObject post = postVal.isObject();

			table.setText(6 + i, 1, Util.debkred(messages, post.get("Debet")));
			table.setText(6 + i, 2, postCache.getDescription(Util.str(post
					.get("Post_type"))));
			table.setText(6 + i, 3, Util.str(post.get("Project")));
			table.setText(6 + i, 4, Util.str(post.get("Person")));
			table.setText(6 + i, 5, Util.money(post.get("Amount")));
			table.getCellFormatter().setStyleName(6 + i, 5, "right");

			table.getRowFormatter().setStyleName(6 + i,
					(i % 2 == 0) ? "showlineposts2" : "showlineposts1");
		}
	}
}
