package no.knubo.accounting.client;

import no.knubo.accounting.client.cache.EmploeeCache;
import no.knubo.accounting.client.cache.MonthHeaderCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.cache.ProjectCache;
import no.knubo.accounting.client.views.AboutView;
import no.knubo.accounting.client.views.LazyLoad;
import no.knubo.accounting.client.views.LineEditView;
import no.knubo.accounting.client.views.MonthView;
import no.knubo.accounting.client.views.PersonEditView;
import no.knubo.accounting.client.views.PersonSearchView;
import no.knubo.accounting.client.views.ViewCallback;

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
public class AccountingGWT implements EntryPoint, ViewCallback {

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
        activeView.setStyleName("activeview");
        docPanel.add(activeView, DockPanel.CENTER);

        MenuBar registerMenu = new MenuBar(true);
        topMenu.addItem(new MenuItem(messages.menu_register(), registerMenu));

        MenuBar showMenu = new MenuBar(true);
        topMenu.addItem(new MenuItem(messages.menu_show(), showMenu));

        MenuBar membersMenu = new MenuBar(true);
        topMenu.addItem(new MenuItem(messages.menu_people(), membersMenu));

        MenuBar reportsMenu = new MenuBar(true);
        topMenu.addItem(new MenuItem(messages.menu_reports(), reportsMenu));

        MenuBar settingsMenu = new MenuBar(true);
        topMenu.addItem(new MenuItem(messages.menu_settings(),
                settingsMenu));

        MenuBar aboutMenu = new MenuBar(true);
        topMenu.addItem(new MenuItem(messages.menu_info(), aboutMenu));

        registerMenu.addItem(messages.menuitem_regline(), true,
                commandRegisterNewline());
        registerMenu.addItem(messages.menuitem_registerMembership(), true,
                commandRegisterMembership());
        showMenu.addItem(messages.menuitem_showmonth(), true,
                commandShowMonth());
        membersMenu.addItem(messages.menuitem_addperson(), true,
                commandAddMember());
        membersMenu.addItem(messages.menuitem_findperson(), true,
                commandFindMember());
        settingsMenu.addItem(messages.menuitem_values(), true,
                commandSettings());

        activeView.add(aboutLoader.getInstance(constants, messages, this),
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
        final AccountingGWT around = this;
        return new Command() {

            public void execute() {
                Widget widget = LineEditView.show(around, messages, constants,
                        null);

                setActiveWidget(widget);
            }

        };
    }

    private Command commandRegisterMembership() {
        final AccountingGWT me = this;
        return new Command() {

            public void execute() {
            }

        };
    }

    private Command commandFindMember() {
        final AccountingGWT around = this;
        return new Command() {

            public void execute() {
                Widget widget = PersonSearchView.show(around, messages, constants);

                setActiveWidget(widget);
            }

        };
    }

    private Command commandSettings() {
        final AccountingGWT around = this;
        return new Command() {

            public void execute() {
            }

        };
    }

    private Command commandAddMember() {
        final AccountingGWT me = this;
        return new Command() {

            public void execute() {
                PersonEditView widget = PersonEditView.show(constants,
                        messages, me);
                widget.init(null);
                setActiveWidget(widget);
            }

        };
    }

    private Command commandShowMonth() {
        final AccountingGWT me = this;
        return new Command() {

            public void execute() {
                MonthView widget = (MonthView) monthLoader.getInstance(
                        constants, messages, me);
                widget.init();

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

    public void openDetails(String id) {
        Widget widget = LineEditView.show(this, messages, constants, id);

        setActiveWidget(widget);
    }

    public void viewMonth(String year, String month) {
        MonthView instance = (MonthView) monthLoader.getInstance(constants,
                messages, this);

        instance.init(year, month);

        setActiveWidget(instance);
    }
}
