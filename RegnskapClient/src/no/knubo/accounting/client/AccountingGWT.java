package no.knubo.accounting.client;

import no.knubo.accounting.client.cache.AccountPlanCache;
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
import no.knubo.accounting.client.views.LogoutView;
import no.knubo.accounting.client.views.MonthDetailsView;
import no.knubo.accounting.client.views.MonthEndView;
import no.knubo.accounting.client.views.MonthView;
import no.knubo.accounting.client.views.PersonEditView;
import no.knubo.accounting.client.views.PersonSearchView;
import no.knubo.accounting.client.views.PostTypeEditView;
import no.knubo.accounting.client.views.ProjectEditView;
import no.knubo.accounting.client.views.UsersEditView;
import no.knubo.accounting.client.views.RegisterHappeningView;
import no.knubo.accounting.client.views.RegisterMembershipView;
import no.knubo.accounting.client.views.ShowMembershipView;
import no.knubo.accounting.client.views.StandardvaluesView;
import no.knubo.accounting.client.views.TrustStatusView;
import no.knubo.accounting.client.views.ViewCallback;
import no.knubo.accounting.client.views.reporting.ReportAccountlines;
import no.knubo.accounting.client.views.reporting.ReportMembersAddresses;
import no.knubo.accounting.client.views.reporting.ReportMembersBirth;

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

    private HelpTexts helpTexts;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        messages = (I18NAccount) GWT.create(I18NAccount.class);
        constants = (Constants) GWT.create(Constants.class);
        helpTexts = (HelpTexts) GWT.create(HelpTexts.class);

        loadCaches(constants, messages);

        DockPanel docPanel = new DockPanel();
        docPanel.setWidth("100%");
        docPanel.setHeight("100%");

        MenuBar topMenu = new MenuBar();
        topMenu.setWidth("100%");
        docPanel.add(topMenu, DockPanel.NORTH);
        docPanel.setCellHeight(topMenu, "10px   ");

        activeView = new DockPanel();
        activeView.setStyleName("activeview");
        docPanel.add(activeView, DockPanel.CENTER);
        HelpPanel helpPanel = HelpPanel.getInstance(messages, helpTexts);
        docPanel.add(helpPanel, DockPanel.EAST);
        docPanel.setCellWidth(helpPanel, "100%");

        MenuBar registerMenu = addTopMenu(topMenu, messages.menu_register());
        MenuBar showMenu = addTopMenu(topMenu, messages.menu_show());
        MenuBar peopleMenu = addTopMenu(topMenu, messages.menu_people());
        MenuBar trustMenu = addTopMenu(topMenu, messages.menu_trust());
        MenuBar reportsMenu = addTopMenu(topMenu, messages.menu_reports());
        MenuBar settingsMenu = addTopMenu(topMenu, messages.menu_settings());
        MenuBar aboutMenu = addTopMenu(topMenu, messages.menu_info());

        addMenuItem(registerMenu, messages.menuitem_regline(),
                WidgetIds.LINE_EDIT_VIEW);
        addMenuItem(registerMenu, messages.menuitem_registerMembership(),
                WidgetIds.REGISTER_MEMBERSHIP);
        addMenuItem(registerMenu, messages.menuitem_register_happening(),
                WidgetIds.REGISTER_HAPPENING);
        addMenuItem(registerMenu, messages.menuitem_endmonth(),
                WidgetIds.END_MONTH);
        addMenuItem(registerMenu, messages.menuitem_endyear(),
                WidgetIds.END_YEAR);

        addMenuItem(showMenu, messages.menuitem_showmonth(),
                WidgetIds.SHOW_MONTH);
        addMenuItem(showMenu, messages.menuitem_showmonthdetails(),
                WidgetIds.SHOW_MONTH_DETAILS);
        addMenuItem(showMenu, messages.menuitem_showmembers(),
                WidgetIds.SHOW_MEMBERS);
        addMenuItem(showMenu, messages.menuitem_showtraining(),
                WidgetIds.SHOW_TRAINING_MEMBERS);
        addMenuItem(showMenu, messages.menuitem_showclassmembers(),
                WidgetIds.SHOW_CLASS_MEMBERS);

        addMenuItem(peopleMenu, messages.menuitem_addperson(),
                WidgetIds.ADD_PERSON);
        addMenuItem(peopleMenu, messages.menuitem_findperson(),
                WidgetIds.FIND_PERSON);

        addMenuItem(trustMenu, messages.menuitem_truststatus(),
                WidgetIds.TRUST_STATUS);

        addMenuItem(reportsMenu, messages.menuitem_report_member_per_year(),
                WidgetIds.REPORT_MEMBER_PER_YEAR);
        addMenuItem(reportsMenu, messages.menuitem_report_addresses(),
                WidgetIds.REPORT_ADDRESSES);
        addMenuItem(reportsMenu, messages.menuitem_report_selectedlines(),
                WidgetIds.REPORT_SELECTEDLINES);

        addMenuItem(settingsMenu, messages.menuitem_useradm(),
                WidgetIds.EDIT_USERS);
        addMenuItem(settingsMenu, messages.menuitem_accounts(),
                WidgetIds.EDIT_ACCOUNTS);
        addMenuItem(settingsMenu, messages.menuitem_projects(),
                WidgetIds.EDIT_PROJECTS);
        addMenuItem(settingsMenu, messages.menuitem_edit_happening(),
                WidgetIds.EDIT_HAPPENING);
        addMenuItem(settingsMenu, messages.menuitem_values(),
                WidgetIds.SETTINGS);

        addMenuItem(aboutMenu, messages.menuitem_about(), WidgetIds.ABOUT);
        addMenuItem(aboutMenu, messages.menuitem_logout(), WidgetIds.LOGOUT);

        activeView.add(AboutView.getInstance(messages), DockPanel.CENTER);

        RootPanel.get().add(docPanel);
    }

    private void addMenuItem(MenuBar menu, String title, int widgetId) {
        menu.addItem(title, true, new Commando(this, widgetId, title));
    }

    private MenuBar addTopMenu(MenuBar topMenu, String header) {
        MenuBar menu = new MenuBar(true);
        topMenu.addItem(new MenuItem(header, menu));
        return menu;
    }

    public static void loadCaches(Constants cons, I18NAccount messages) {
        MonthHeaderCache.getInstance(cons, messages);
        PosttypeCache.getInstance(cons, messages);
        EmploeeCache.getInstance(cons, messages);
        ProjectCache.getInstance(cons, messages);
        CountCache.getInstance(cons, messages);
        HappeningCache.getInstance(cons, messages);
        TrustActionCache.getInstance(cons, messages);
        AccountPlanCache.getInstance(cons, messages);
    }

    class Commando implements Command, WidgetIds {

        int action;

        private final ViewCallback callback;

        private final String title;

        Commando(ViewCallback callback, int action, String title) {
            this.callback = callback;
            this.action = action;
            this.title = title;
        }

        public void execute() {
            Widget widget = null;

            HelpPanel helpPanel = HelpPanel.getInstance(messages, helpTexts);
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
            case EDIT_USERS:
                widget = UsersEditView.show(messages, constants, helpPanel);
                ((UsersEditView) widget).init();
                break;
            case EDIT_ACCOUNTS:
                widget = PostTypeEditView.show(messages, constants, helpPanel);
                ((PostTypeEditView) widget).init();
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
                widget = TrustStatusView.getInstance(constants, messages,
                        helpPanel, callback);
                ((TrustStatusView) widget).init();
                break;
            case REPORT_MEMBER_PER_YEAR:
                widget = ReportMembersBirth.getInstance(constants, messages,
                        helpPanel);
                ((ReportMembersBirth) widget).init();
                break;
            case REPORT_ADDRESSES:
                widget = ReportMembersAddresses.getInstance(constants,
                        messages, helpPanel);
                ((ReportMembersAddresses) widget).init();
                break;
            case REPORT_SELECTEDLINES:
                widget = ReportAccountlines.getInstance(constants, messages,
                        helpPanel);
                break;

            case WidgetIds.ABOUT:
                widget = AboutView.getInstance(messages);
                break;
            case WidgetIds.LOGOUT:
                widget = LogoutView.getInstance(constants, messages);
                break;
            }
            if (widget == null) {
                Window.alert("No action");
                return;
            }
            setActiveWidget(widget);
            Window.setTitle(title);
            helpPanel.setCurrentWidget(widget, action);
        }
    }

    private void setActiveWidget(Widget widget) {
        activeView.clear();
        activeView.add(widget, DockPanel.CENTER);
        activeView.setCellHeight(widget, "100%");
        activeView.setCellVerticalAlignment(widget, DockPanel.ALIGN_TOP);

    }

    public void openDetails(String id) {
        Widget widget = LineEditView.show(this, messages, constants, id);

        setActiveWidget(widget);
        HelpPanel.getInstance(messages, helpTexts).setCurrentWidget(widget,
                WidgetIds.LINE_EDIT_VIEW);
        Window.setTitle(messages.menuitem_showmonthdetails());
    }

    public void viewMonth(int year, int month) {
        MonthView instance = MonthView.getInstance(constants, messages, this);

        instance.init(year, month);

        setActiveWidget(instance);
        HelpPanel.getInstance(messages, helpTexts).setCurrentWidget(instance,
                WidgetIds.SHOW_MONTH);
        Window.setTitle(messages.menuitem_showmonth());
    }

    public void viewMonth() {
        MonthView instance = MonthView.getInstance(constants, messages, this);

        instance.init();

        setActiveWidget(instance);
        HelpPanel.getInstance(messages, helpTexts).setCurrentWidget(instance,
                WidgetIds.SHOW_MONTH);
        Window.setTitle(messages.menuitem_showmonth());
    }

    public void editPerson(String id) {
        PersonEditView widget = PersonEditView.show(constants, messages);

        widget.init(id);
        setActiveWidget(widget);
        HelpPanel.getInstance(messages, helpTexts).setCurrentWidget(widget,
                WidgetIds.ADD_PERSON);
        Window.setTitle(messages.menuitem_addperson());
    }
}
