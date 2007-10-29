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
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.WidgetIds;
import no.knubo.accounting.client.views.AboutView;
import no.knubo.accounting.client.views.AccountTrackEditView;
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
import no.knubo.accounting.client.views.RegisterHappeningView;
import no.knubo.accounting.client.views.RegisterMembershipView;
import no.knubo.accounting.client.views.ShowMembershipView;
import no.knubo.accounting.client.views.StandardvaluesView;
import no.knubo.accounting.client.views.TrustActionEditView;
import no.knubo.accounting.client.views.TrustEditView;
import no.knubo.accounting.client.views.TrustStatusView;
import no.knubo.accounting.client.views.UsersEditView;
import no.knubo.accounting.client.views.ViewCallback;
import no.knubo.accounting.client.views.budget.BudgetView;
import no.knubo.accounting.client.views.reporting.ReportAccountlines;
import no.knubo.accounting.client.views.reporting.ReportAccounttracking;
import no.knubo.accounting.client.views.reporting.ReportMail;
import no.knubo.accounting.client.views.reporting.ReportMassLetters;
import no.knubo.accounting.client.views.reporting.ReportMembersAddresses;
import no.knubo.accounting.client.views.reporting.ReportMembersBirth;
import no.knubo.accounting.client.views.reporting.ReportUsersEmail;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AccountingGWT implements EntryPoint, ViewCallback {

    private I18NAccount messages;
    private Constants constants;
    private HelpTexts helpTexts;
    private Elements elements;

    private DockPanel activeView;

    private static Image blankImage;
    private static Image loadingImage;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        messages = (I18NAccount) GWT.create(I18NAccount.class);
        constants = (Constants) GWT.create(Constants.class);
        helpTexts = (HelpTexts) GWT.create(HelpTexts.class);
        elements = (Elements) GWT.create(Elements.class);

        loadCaches(constants, messages);

        DockPanel docPanel = new DockPanel();
        docPanel.setWidth("100%");
        docPanel.setHeight("100%");

        MenuBar topMenu = new MenuBar();
        topMenu.setWidth("100%");
        blankImage = ImageFactory.blankImage(16, 16);
        blankImage.setVisible(true);
        loadingImage = ImageFactory.loadingImage("loading");
        loadingImage.setVisible(false);
        docPanel.add(blankImage, DockPanel.WEST);
        docPanel.add(loadingImage, DockPanel.WEST);
        docPanel.add(topMenu, DockPanel.NORTH);
        docPanel.setCellHeight(topMenu, "10px   ");

        activeView = new DockPanel();
        activeView.setStyleName("activeview");
        docPanel.add(activeView, DockPanel.CENTER);
        HelpPanel helpPanel = HelpPanel.getInstance(elements, helpTexts);
        docPanel.add(helpPanel, DockPanel.EAST);
        docPanel.setCellWidth(helpPanel, "100%");

        MenuBar registerMenu = addTopMenu(topMenu, elements.menu_register());
        MenuBar showMenu = addTopMenu(topMenu, elements.menu_show());
        MenuBar peopleMenu = addTopMenu(topMenu, elements.menu_people());
        MenuBar budgetMenu = addTopMenu(topMenu, elements.menu_budget());
        MenuBar trustMenu = addTopMenu(topMenu, elements.menu_trust());
        MenuBar reportsMenu = addTopMenu(topMenu, elements.menu_reports());
        MenuBar settingsMenu = addTopMenu(topMenu, elements.menu_settings());
        MenuBar aboutMenu = addTopMenu(topMenu, elements.menu_info());

        addMenuItem(registerMenu, elements.menuitem_regline(),
                WidgetIds.LINE_EDIT_VIEW);
        addMenuItem(registerMenu, elements.menuitem_registerMembership(),
                WidgetIds.REGISTER_MEMBERSHIP);
        addMenuItem(registerMenu, elements.menuitem_register_happening(),
                WidgetIds.REGISTER_HAPPENING);
        addMenuItem(registerMenu, elements.menuitem_endmonth(),
                WidgetIds.END_MONTH);
        addMenuItem(registerMenu, elements.menuitem_endyear(),
                WidgetIds.END_YEAR);

        addMenuItem(showMenu, elements.menuitem_showmonth(),
                WidgetIds.SHOW_MONTH);
        addMenuItem(showMenu, elements.menuitem_showmonthdetails(),
                WidgetIds.SHOW_MONTH_DETAILS);
        addMenuItem(showMenu, elements.menuitem_showmembers(),
                WidgetIds.SHOW_MEMBERS);
        addMenuItem(showMenu, elements.menuitem_showtraining(),
                WidgetIds.SHOW_TRAINING_MEMBERS);
        addMenuItem(showMenu, elements.menuitem_showclassmembers(),
                WidgetIds.SHOW_CLASS_MEMBERS);

        addMenuItem(peopleMenu, elements.menuitem_addperson(),
                WidgetIds.ADD_PERSON);
        addMenuItem(peopleMenu, elements.menuitem_findperson(),
                WidgetIds.FIND_PERSON);

        addMenuItem(trustMenu, elements.menuitem_truststatus(),
                WidgetIds.TRUST_STATUS);

        addMenuItem(budgetMenu, elements.menuitem_budget(),
                WidgetIds.BUDGET);

        addMenuItem(reportsMenu, elements.menuitem_report_member_per_year(),
                WidgetIds.REPORT_MEMBER_PER_YEAR);
        addMenuItem(reportsMenu, elements.menuitem_report_addresses(),
                WidgetIds.REPORT_ADDRESSES);
        addMenuItem(reportsMenu, elements.menuitem_report_selectedlines(),
                WidgetIds.REPORT_SELECTEDLINES);
        addMenuItem(reportsMenu, elements.menuitem_report_letter(),
                WidgetIds.REPORT_LETTER);
        addMenuItem(reportsMenu, elements.menuitem_report_email(),
                WidgetIds.REPORT_EMAIL);
        addMenuItem(reportsMenu, elements.menuitem_report_users_email(),
                WidgetIds.REPORT_USERS_EMAIL);
        addMenuItem(reportsMenu, elements.menuitem_report_accounttrack(),
                WidgetIds.REPORT_ACCOUNTTRACK);

        addMenuItem(settingsMenu, elements.menuitem_useradm(),
                WidgetIds.EDIT_USERS);
        addMenuItem(settingsMenu, elements.menuitem_edit_trust(),
                WidgetIds.EDIT_TRUST);
        addMenuItem(settingsMenu, elements.menuitem_edit_trust_actions(),
                WidgetIds.EDIT_TRUST_ACTIONS);
        addMenuItem(settingsMenu, elements.menuitem_accounts(),
                WidgetIds.EDIT_ACCOUNTS);
        addMenuItem(settingsMenu, elements.menuitem_accounttrack(),
                WidgetIds.EDIT_ACCOUNTTRACK);
        addMenuItem(settingsMenu, elements.menuitem_projects(),
                WidgetIds.EDIT_PROJECTS);
        addMenuItem(settingsMenu, elements.menuitem_edit_happening(),
                WidgetIds.EDIT_HAPPENING);
        addMenuItem(settingsMenu, elements.menuitem_values(),
                WidgetIds.SETTINGS);

        addMenuItem(aboutMenu, elements.menuitem_about(), WidgetIds.ABOUT);
        addMenuItem(aboutMenu, elements.menuitem_logout(), WidgetIds.LOGOUT);

        new Commando(null, WidgetIds.ABOUT, elements.menuitem_about())
                .execute();

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

        private String title;

        Commando(ViewCallback callback, int action, String title) {
            this.callback = callback;
            this.action = action;
            this.title = title;
        }

        public void execute() {
            Widget widget = null;

            HelpPanel helpPanel = HelpPanel.getInstance(elements, helpTexts);
            switch (action) {
            case WidgetIds.LINE_EDIT_VIEW:
                widget = LineEditView.show(callback, messages, constants, null,
                        HelpPanel.getInstance(elements, helpTexts), elements);
                break;
            case WidgetIds.REGISTER_MEMBERSHIP:
                widget = RegisterMembershipView.show(messages, constants,
                        helpPanel, elements);
                ((RegisterMembershipView) widget).init();
                break;
            case WidgetIds.REGISTER_HAPPENING:
                widget = RegisterHappeningView.show(messages, constants,
                        callback, elements);
                ((RegisterHappeningView) widget).init();
                break;
            case END_MONTH:
                widget = MonthEndView.getInstance(constants, messages,
                        callback, elements);
                ((MonthEndView) widget).init();
                break;
            case WidgetIds.SETTINGS:
                widget = StandardvaluesView.show(messages, constants, elements);
                ((StandardvaluesView) widget).init();
                break;
            case EDIT_HAPPENING:
                widget = HappeningsView.show(messages, constants, elements);
                ((HappeningsView) widget).init();
                break;
            case EDIT_PROJECTS:
                widget = ProjectEditView.show(messages, constants, elements);
                ((ProjectEditView) widget).init();
                break;
            case EDIT_USERS:
                widget = UsersEditView.show(messages, constants, helpPanel,
                        elements);
                ((UsersEditView) widget).init();
                break;
            case EDIT_ACCOUNTS:
                widget = PostTypeEditView.show(messages, constants, helpPanel,
                        elements);
                ((PostTypeEditView) widget).init();
                break;
            case EDIT_ACCOUNTTRACK:
                widget = AccountTrackEditView.show(messages, constants,
                        helpPanel, elements);
                ((AccountTrackEditView) widget).init();
                break;
            case EDIT_TRUST_ACTIONS:
                widget = TrustActionEditView.show(messages, constants,
                        helpPanel, elements);
                ((TrustActionEditView) widget).init();
                break;
            case EDIT_TRUST:
                widget = TrustEditView.show(messages, constants, helpPanel,
                        elements);
                ((TrustEditView) widget).init();
                break;
                
            case BUDGET:
                widget = BudgetView.show(messages, constants, helpPanel, elements);
                ((BudgetView)widget).init();
                break;
            case WidgetIds.ADD_PERSON:
                widget = PersonEditView.show(constants, messages, helpPanel,
                        callback, elements);
                ((PersonEditView) widget).init(null);
                break;
            case WidgetIds.FIND_PERSON:
                widget = PersonSearchView.show(callback, messages, constants,
                        elements);
                break;
            case WidgetIds.SHOW_MONTH:
                widget = MonthView.getInstance(constants, messages, callback,
                        elements);
                ((MonthView) widget).init();
                break;
            case SHOW_MONTH_DETAILS:
                widget = MonthDetailsView.getInstance(constants, messages,
                        elements);
                ((MonthDetailsView) widget).init();
                break;
            case WidgetIds.SHOW_MEMBERS:
                widget = ShowMembershipView.show(messages, constants, callback,
                        helpPanel, elements);
                ((ShowMembershipView) widget).initShowMembers();
                break;
            case WidgetIds.SHOW_CLASS_MEMBERS:
                widget = ShowMembershipView.show(messages, constants, callback,
                        helpPanel, elements);
                ((ShowMembershipView) widget).initShowClassMembers();
                break;
            case WidgetIds.SHOW_TRAINING_MEMBERS:
                widget = ShowMembershipView.show(messages, constants, callback,
                        helpPanel, elements);
                ((ShowMembershipView) widget).initShowTrainingMembers();
                break;
            case TRUST_STATUS:
                widget = TrustStatusView.getInstance(constants, messages,
                        helpPanel, callback, elements);
                ((TrustStatusView) widget).init();
                break;
            case REPORT_ACCOUNTTRACK:
                widget = ReportAccounttracking.getInstance(constants, messages,
                        elements);
                break;
            case REPORT_MEMBER_PER_YEAR:
                widget = ReportMembersBirth.getInstance(constants, messages,
                        helpPanel, elements);
                ((ReportMembersBirth) widget).init();
                break;
            case REPORT_ADDRESSES:
                widget = ReportMembersAddresses.getInstance(constants,
                        messages, helpPanel, elements);
                ((ReportMembersAddresses) widget).init();
                break;
            case REPORT_SELECTEDLINES:
                widget = ReportAccountlines.getInstance(constants, messages,
                        helpPanel, elements);
                break;
            case REPORT_LETTER:
                widget = ReportMassLetters.getInstance(constants, messages,
                        elements);
                ((ReportMassLetters) widget).init();
                break;
            case REPORT_EMAIL:
                widget = ReportMail.getInstance(constants, messages, elements);
                break;
            case REPORT_USERS_EMAIL:
                widget = ReportUsersEmail.getInstance(constants, messages,
                        helpPanel, elements);
                ((ReportUsersEmail) widget).init();
                break;

            case WidgetIds.ABOUT:
                widget = AboutView.getInstance(constants, messages);
                title = title + " - " + AboutView.CLIENT_VERSION;
                break;
            case WidgetIds.LOGOUT:
                widget = LogoutView.getInstance(constants, messages, elements);
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
        Widget widget = LineEditView.show(this, messages, constants, id,
                HelpPanel.getInstance(elements, helpTexts), elements);

        setActiveWidget(widget);
        HelpPanel.getInstance(elements, helpTexts).setCurrentWidget(widget,
                WidgetIds.LINE_EDIT_VIEW);
        Window.setTitle(elements.menuitem_showmonthdetails());
    }

    public void viewMonth(int year, int month) {
        MonthView instance = MonthView.getInstance(constants, messages, this,
                elements);

        instance.init(year, month);

        setActiveWidget(instance);
        HelpPanel.getInstance(elements, helpTexts).setCurrentWidget(instance,
                WidgetIds.SHOW_MONTH);
        Window.setTitle(elements.menuitem_showmonth());
    }

    public void searchPerson() {
        PersonSearchView widget = PersonSearchView.show(this, messages,
                constants, elements);
        setActiveWidget(widget);
        HelpPanel.getInstance(elements, helpTexts).setCurrentWidget(widget,
                WidgetIds.FIND_PERSON);
        Window.setTitle(elements.menuitem_showmonth());
    }

    public void viewMonth() {
        MonthView instance = MonthView.getInstance(constants, messages, this,
                elements);

        instance.init();

        setActiveWidget(instance);
        HelpPanel.getInstance(elements, helpTexts).setCurrentWidget(instance,
                WidgetIds.SHOW_MONTH);
        Window.setTitle(elements.menuitem_showmonth());
    }

    public void editPerson(String id) {
        PersonEditView widget = PersonEditView.show(constants, messages,
                HelpPanel.getInstance(elements, helpTexts), this, elements);

        widget.init(id);
        setActiveWidget(widget);
        HelpPanel.getInstance(elements, helpTexts).setCurrentWidget(widget,
                WidgetIds.ADD_PERSON);
        Window.setTitle(elements.menuitem_addperson());
    }

    public static void setLoading() {
        if (loadingImage != null) {
            loadingImage.setVisible(true);
        }
        if (blankImage != null) {
            blankImage.setVisible(false);
        }
    }

    public static void setDoneLoading() {
        if (loadingImage != null) {
            loadingImage.setVisible(false);
        }
        if (blankImage != null) {
            blankImage.setVisible(true);
        }
    }
}
