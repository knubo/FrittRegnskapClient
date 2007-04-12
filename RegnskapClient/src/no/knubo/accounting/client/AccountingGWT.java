package no.knubo.accounting.client;

import no.knubo.accounting.client.cache.MonthHeaderCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.views.LazyLoad;
import no.knubo.accounting.client.views.MonthView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AccountingGWT implements EntryPoint {

	private I18NAccount messages;

	private LazyLoad monthLoader = MonthView.loader();

	private DockPanel activeView;

	private Constants constants;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		messages = (I18NAccount) GWT.create(I18NAccount.class);
		constants = (Constants) GWT.create(Constants.class);

		loadCaches(constants);
		DockPanel docPanel = new DockPanel();

		MenuBar menuBar = new MenuBar();
		docPanel.add(menuBar, DockPanel.NORTH);

		activeView = new DockPanel();
		docPanel.add(activeView, DockPanel.CENTER);

		MenuBar acountMenu = new MenuBar(true);
		menuBar.addItem(new MenuItem(messages.menu_accounting(), acountMenu));

		acountMenu.addItem(messages.menuitem_showmonth(), true,
				commandShowMonth());

		RootPanel.get().add(docPanel);
	}

	private void loadCaches(Constants cons) {
		MonthHeaderCache.getInstance(cons);
		PosttypeCache.getInstance(cons);
	}

	private Command commandShowMonth() {
		return new Command() {

			public void execute() {
				activeView.clear();
				Window.setTitle(messages.title_monthview());
				Widget widget = monthLoader.getInstance(constants, messages);

				activeView.add(widget, DockPanel.CENTER);
				activeView.setCellWidth(widget, "100%");
				activeView.setCellHeight(widget, "100%");
				activeView
						.setCellVerticalAlignment(widget, DockPanel.ALIGN_TOP);
			}

		};
	}

	public void execute() {

	}
}
