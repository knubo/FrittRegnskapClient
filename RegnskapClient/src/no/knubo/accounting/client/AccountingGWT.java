package no.knubo.accounting.client;

import no.knubo.accounting.client.cache.CountCache;
import no.knubo.accounting.client.cache.EmploeeCache;
import no.knubo.accounting.client.cache.HappeningCache;
import no.knubo.accounting.client.cache.MonthHeaderCache;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.cache.ProjectCache;
import no.knubo.accounting.client.cache.TrustActionCache;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.WidgetIds;
import no.knubo.accounting.client.views.AboutView;
import no.knubo.accounting.client.views.HappeningsView;
import no.knubo.accounting.client.views.LineEditView;
import no.knubo.accounting.client.views.MonthDetailsView;
import no.knubo.accounting.client.views.MonthEndView;
import no.knubo.accounting.client.views.MonthView;
import no.knubo.accounting.client.views.PersonEditView;
import no.knubo.accounting.client.views.PersonSearchView;
import no.knubo.accounting.client.views.ProjectEditView;
import no.knubo.accounting.client.views.RegisterHappeningView;
import no.knubo.accounting.client.views.RegisterMembershipView;
import no.knubo.accounting.client.views.ShowMembershipView;
import no.knubo.accounting.client.views.StandardvaluesView;
import no.knubo.accounting.client.views.TrustStatusView;
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
        docPanel.add(HelpPanel.getInstance(messages), DockPanel.EAST);
        
        MenuBar registerMenu = addTopMenu(topMenu, messages.menu_register());
        MenuBar showMenu = addTopMenu(topMenu, messages.menu_show());
        MenuBar peopleMenu = addTopMenu(topMenu, messages.menu_people());
        MenuBar trustMenu = addTopMenu(topMenu, messages.menu_trust());
        MenuBar reportsMenu = addTopMenu(topMenu, messages.menu_reports());
        MenuBar settingsMenu = addTopMenu(topMenu, messages.menu_settings());
        MenuBar aboutMenu = addTopMenu(topMenu, messages.menu_info());

        registerMenu.addItem(messages.menuitem_regline(), true, new Commando(
                this, WidgetIds.LINE_EDIT_VIEW));
        registerMenu.addItem(messages.menuitem_registerMembership(), true,
                new Commando(this, WidgetIds.REGISTER_MEMBERSHIP));
        registerMenu.addItem(messages.menuitem_register_happening(), true,
                new Commando(this, WidgetIds.REGISTER_HAPPENING));
        registerMenu.addItem(messages.menuitem_endmonth(), true, new Commando(
                this, Commando.END_MONTH));
        registerMenu.addItem(messages.menuitem_endyear(), true, new Commando(
                this, Commando.END_YEAR));

        showMenu.addItem(messages.menuitem_showmonth(), true, new Commando(
                this, WidgetIds.SHOW_MONTH));
        showMenu.addItem(messages.menuitem_showmonthdetails(), true,
                new Commando(this, Commando.SHOW_MONTH_DETAILS));
        showMenu.addItem(messages.menuitem_showmembers(), true, new Commando(
                this, WidgetIds.SHOW_MEMBERS));
        showMenu.addItem(messages.menuitem_showtraining(), true, new Commando(
                this, WidgetIds.SHOW_TRAINING_MEMBERS));
        showMenu.addItem(messages.menuitem_showclassmembers(), true,
                new Commando(this, WidgetIds.SHOW_CLASS_MEMBERS));

        peopleMenu.addItem(messages.menuitem_addperson(), true, new Commando(
                this, WidgetIds.ADD_PERSON));
        peopleMenu.addItem(messages.menuitem_findperson(), true, new Commando(
                this, WidgetIds.FIND_PERSON));

        trustMenu.addItem(messages.menuitem_truststatus(), true, new Commando(
                this, Commando.TRUST_STATUS));

        reportsMenu.addItem(messages.menuitem_report_member_per_year(), true,
                new Commando(this, Commando.REPORT_MEMBER_PER_YEAR));

        settingsMenu.addItem(messages.menuitem_posttypes(), true, new Commando(
                this, Commando.EDIT_POSTTYPES));
        settingsMenu.addItem(messages.menuitem_projects(), true, new Commando(
                this, Commando.EDIT_PROJECTS));
        settingsMenu.addItem(messages.menuitem_edit_happening(), true,
                new Commando(this, Commando.EDIT_HAPPENING));
        settingsMenu.addItem(messages.menuitem_values(), true, new Commando(
                this, WidgetIds.SETTINGS));
        aboutMenu.addItem(messages.menuitem_about(), true, new Commando(this,
                WidgetIds.ABOUT));
        activeView.add(AboutView.getInstance(), DockPanel.CENTER);

        RootPanel.get().add(docPanel);
    }

    private MenuBar addTopMenu(MenuBar topMenu, String header) {
        MenuBar menu = new MenuBar(true);
        topMenu.addItem(new MenuItem(header, menu));
        return menu;
    }

    public static void loadCaches(Constants cons) {
        MonthHeaderCache.getInstance(cons);
        PosttypeCache.getInstance(cons);
        EmploeeCache.getInstance(cons);
        ProjectCache.getInstance(cons);
        CountCache.getInstance(cons);
        HappeningCache.getInstance(cons);
        TrustActionCache.getInstance(cons);
    }

    class Commando implements Command, WidgetIds {

        int action;

        private final ViewCallback callback;

        Commando(ViewCallback callback, int action) {
            this.callback = callback;
            this.action = action;

        }


        public void execute() {
            Widget widget = null;

            HelpPanel.getInstance(messages).setCurrentWidget(action);
            switch (action) {
            case WidgetIds.LINE_EDIT_VIEW:
                widget = LineEditView.show(callback, messages, constants, null);
                break;
            case WidgetIds.REGISTER_MEMBERSHIP:
                widget = RegisterMembershipView.show(messages, constants);
                ((RegisterMembershipView) widget).init();
                break;
            case WidgetIds.REGISTER_HAPPENING:
                widget = RegisterHappeningView.show(messages, constants,
                        callback);
                ((RegisterHappeningView) widget).init();
                break;
            case END_MONTH:
                widget = MonthEndView
                        .getInstance(constants, messages, callback);
                ((MonthEndView) widget).init();
                break;
            case WidgetIds.SETTINGS:
                widget = StandardvaluesView.show(messages, constants);
                ((StandardvaluesView) widget).init();
                break;
            case EDIT_HAPPENING:
                widget = HappeningsView.show(messages, constants);
                ((HappeningsView) widget).init();
                break;
            case EDIT_PROJECTS:
                widget = ProjectEditView.show(messages, constants);
                ((ProjectEditView) widget).init();
                break;
            case WidgetIds.ADD_PERSON:
                widget = PersonEditView.show(constants, messages);
                ((PersonEditView) widget).init(null);
                break;
            case WidgetIds.FIND_PERSON:
                widget = PersonSearchView.show(callback, messages, constants);
                break;
            case WidgetIds.SHOW_MONTH:
                widget = MonthView.getInstance(constants, messages, callback);
                ((MonthView) widget).init();
                break;
            case SHOW_MONTH_DETAILS:
                widget = MonthDetailsView.getInstance(constants, messages);
                ((MonthDetailsView) widget).init();
                break;
            case WidgetIds.SHOW_MEMBERS:
                widget = ShowMembershipView.show(messages, constants, callback);
                ((ShowMembershipView) widget).initShowMembers();
                break;
            case WidgetIds.SHOW_CLASS_MEMBERS:
                widget = ShowMembershipView.show(messages, constants, callback);
                ((ShowMembershipView) widget).initShowClassMembers();
                break;
            case WidgetIds.SHOW_TRAINING_MEMBERS:
                widget = ShowMembershipView.show(messages, constants, callback);
                ((ShowMembershipView) widget).initShowTrainingMembers();
                break;
            case TRUST_STATUS:
                widget = TrustStatusView.getInstance(constants, messages);
                ((TrustStatusView)widget).init();
                break;
            case WidgetIds.ABOUT:
                widget = AboutView.getInstance();
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
        MonthView instance = MonthView.getInstance(constants, messages, this);

        instance.init(year, month);

        setActiveWidget(instance);
    }

    public void viewMonth() {
        MonthView instance = MonthView.getInstance(constants, messages, this);

        instance.init();

        setActiveWidget(instance);
    }

    public void editPerson(String id) {
        PersonEditView widget = PersonEditView.show(constants, messages);
        widget.init(id);
        setActiveWidget(widget);

    }
}
