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
import no.knubo.accounting.client.views.RegisterMembershipView;
import no.knubo.accounting.client.views.ShowMembershipView;
import no.knubo.accounting.client.views.StandardvaluesView;
import no.knubo.accounting.client.views.ViewCallback;

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

        MenuBar registerMenu = addTopMenu(topMenu, messages.menu_register());
        MenuBar showMenu = addTopMenu(topMenu, messages.menu_show());
        MenuBar peopleMenu = addTopMenu(topMenu, messages.menu_people());
        MenuBar reportsMenu = addTopMenu(topMenu, messages.menu_reports());
        MenuBar settingsMenu = addTopMenu(topMenu, messages.menu_settings());
        MenuBar aboutMenu = addTopMenu(topMenu, messages.menu_info());

        registerMenu.addItem(messages.menuitem_regline(), true, new Commando(
                this, Commando.LINE_EDIT_VIEW));
        registerMenu.addItem(messages.menuitem_registerMembership(), true,
                new Commando(this, Commando.REGISTER_MEMBERSHIP));
        showMenu.addItem(messages.menuitem_showmonth(), true, new Commando(
                this, Commando.SHOW_MONTH));
        showMenu.addItem(messages.menuitem_showmembers(), true, new Commando(
                this, Commando.SHOW_MEMBERS));
        showMenu.addItem(messages.menuitem_showtraining(), true, new Commando(
                this, Commando.SHOW_TRAINING_MEMBERS));
        showMenu.addItem(messages.menuitem_showclassmembers(), true,
                new Commando(this, Commando.SHOW_CLASS_MEMBERS));

        peopleMenu.addItem(messages.menuitem_addperson(), true, new Commando(
                this, Commando.ADD_PERSON));
        peopleMenu.addItem(messages.menuitem_findperson(), true, new Commando(
                this, Commando.FIND_PERSON));
        settingsMenu.addItem(messages.menuitem_values(), true, new Commando(
                this, Commando.SETTINGS));
        aboutMenu.addItem(messages.menuitem_about(), true, new Commando(this,
                Commando.ABOUT));
        activeView.add(aboutLoader.getInstance(constants, messages, this),
                DockPanel.CENTER);

        RootPanel.get().add(docPanel);
    }

    private MenuBar addTopMenu(MenuBar topMenu, String header) {
        MenuBar menu = new MenuBar(true);
        topMenu.addItem(new MenuItem(header, menu));
        return menu;
    }

    private void loadCaches(Constants cons) {
        MonthHeaderCache.getInstance(cons);
        PosttypeCache.getInstance(cons);
        EmploeeCache.getInstance(cons);
        ProjectCache.getInstance(cons);
    }

    class Commando implements Command {

        int action;

        private final ViewCallback callback;

        Commando(ViewCallback callback, int action) {
            this.callback = callback;
            this.action = action;

        }

        static final int LINE_EDIT_VIEW = 1;

        static final int REGISTER_MEMBERSHIP = 2;

        static final int SETTINGS = 3;

        static final int ADD_PERSON = 4;

        static final int SHOW_MONTH = 5;

        static final int SHOW_MEMBERS = 6;

        static final int FIND_PERSON = 7;

        static final int SHOW_CLASS_MEMBERS = 8;

        static final int SHOW_TRAINING_MEMBERS = 9;

        public static final int ABOUT = 10;

        public void execute() {
            Widget widget = null;

            switch (action) {
            case LINE_EDIT_VIEW:
                widget = LineEditView.show(callback, messages, constants, null);
                break;
            case REGISTER_MEMBERSHIP:
                widget = RegisterMembershipView.show(messages, constants,
                        callback);
                ((RegisterMembershipView)widget).init();
                break;
            case SETTINGS:
                widget = StandardvaluesView.show(messages, constants);
                ((StandardvaluesView) widget).init();
                break;
            case ADD_PERSON:
                widget = PersonEditView.show(constants, messages, callback);
                ((PersonEditView) widget).init(null);
                break;
            case FIND_PERSON:
                widget = PersonSearchView.show(callback, messages, constants);
                break;
            case SHOW_MONTH:
                widget = monthLoader.getInstance(constants, messages, callback);
                ((MonthView) widget).init();
                break;
            case SHOW_MEMBERS:
                widget = ShowMembershipView.show(messages, constants, callback);
                ((ShowMembershipView) widget).initShowMembers();
                break;
            case SHOW_CLASS_MEMBERS:
                widget = ShowMembershipView.show(messages, constants, callback);
                ((ShowMembershipView) widget).initShowClassMembers();
                break;
            case SHOW_TRAINING_MEMBERS:
                widget = ShowMembershipView.show(messages, constants, callback);
                ((ShowMembershipView) widget).initShowTrainingMembers();
                break;
            case ABOUT:
                widget = AboutView.loader().getInstance(constants, messages,
                        callback);
                break;
            }
            if (widget == null) {
                Window.alert("No action");
                return;
            }
            setActiveWidget(widget);
        }
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

    public void editPerson(String id) {
        PersonEditView widget = PersonEditView.show(constants, messages, this);
        widget.init(id);
        setActiveWidget(widget);

    }
}
