package no.knubo.accounting.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AccountingGWT implements EntryPoint, Command {

	private I18NAccount messages;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		messages = (I18NAccount) GWT.create(I18NAccount.class);

		DockPanel docPanel = new DockPanel();
		
		MenuBar menuBar = new MenuBar();
		docPanel.add(menuBar, DockPanel.NORTH);
		
		MenuBar acountMenu = new MenuBar(true);
		menuBar.addItem(new MenuItem(messages.menu_accounting(), acountMenu));

		acountMenu.addItem(messages.menuitem_showmonth(), true, this);
		RootPanel.get().add(menuBar);
	}

	public void execute() {
		// TODO Auto-generated method stub

	}
}
