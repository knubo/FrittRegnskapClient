package no.knubo.accounting.client;

import no.knubo.accounting.client.cache.EmploeeCache;
import no.knubo.accounting.client.cache.MonthHeaderCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.cache.ProjectCache;
import no.knubo.accounting.client.views.AboutView;
import no.knubo.accounting.client.views.LazyLoad;
import no.knubo.accounting.client.views.LineEditView;
import no.knubo.accounting.client.views.MonthView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
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

	private LazyLoad aboutLoader = AboutView.loader();

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

		MenuBar topMenu = new MenuBar();
		docPanel.add(topMenu, DockPanel.NORTH);

		activeView = new DockPanel();
		docPanel.add(activeView, DockPanel.CENTER);

		MenuBar registerMenu = new MenuBar(true);
		topMenu.addItem(new MenuItem(messages.menu_register(), registerMenu));

		MenuBar showMenu = new MenuBar(true);
		topMenu.addItem(new MenuItem(messages.menu_show(), showMenu));

		MenuBar reportsMenu = new MenuBar(true);
		topMenu.addItem(new MenuItem(messages.menu_reports(), reportsMenu));

		MenuBar aboutMenu = new MenuBar(true);
		topMenu.addItem(new MenuItem(messages.menu_info(), aboutMenu));
		
		registerMenu.addItem(messages.menuitem_regline(), true,
				commandRegisterNewline());
		showMenu.addItem(messages.menuitem_showmonth(), true,
				commandShowMonth());

		activeView.add(aboutLoader.getInstance(constants, messages),
				DockPanel.CENTER);

		RootPanel.get().add(docPanel);
	}

	private void loadCaches(Constants cons) {
		MonthHeaderCache.getInstance(cons);
		PosttypeCache.getInstance(cons);
		EmploeeCache.getInstance(cons);
		ProjectCache.getInstance(cons);
	}

	private Command commandRegisterNewline() {
		return new Command() {

			public void execute() {
				Widget widget = LineEditView.show(messages, constants, null);

				setActiveWidget(widget);
			}

		};
	}

	private Command commandShowMonth() {
		return new Command() {

			public void execute() {
				Widget widget = monthLoader.getInstance(constants, messages);

				setActiveWidget(widget);
			}
		};
	}

	private void setActiveWidget(Widget widget) {
		activeView.clear();
		activeView.add(widget, DockPanel.CENTER);
		activeView.setCellWidth(widget, "100%");
		activeView.setCellHeight(widget, "100%");
		activeView.setCellVerticalAlignment(widget, DockPanel.ALIGN_TOP);
	}

	public void execute() {

	}
}
