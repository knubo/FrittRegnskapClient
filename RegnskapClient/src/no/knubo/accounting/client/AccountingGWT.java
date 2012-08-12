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
import no.knubo.accounting.client.views.LineEditView;
import no.knubo.accounting.client.views.MonthView;
import no.knubo.accounting.client.views.PersonSearchView;
import no.knubo.accounting.client.views.ViewCallback;
import no.knubo.accounting.client.views.registers.PersonEditView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AccountingGWT implements EntryPoint, ViewCallback {

    static I18NAccount messages;
    static Constants constants;
    static HelpTexts helpTexts;
    static Elements elements;

    static DockPanel activeView;

    private static Image blankImage;
    private static Image loadingImage;

    public static boolean canSeeSecret;
    public static boolean eventEnabled;

    protected int reducedMode;

    private MenuBar eventMenu;
    private MenuBar showMenu;
    private MenuBar peopleMenu;
    private MenuBar reportsMenu;
    private MenuBar settingsMenu;
    private MenuBar topMenu;
    private boolean menuSetUp;

    private static final boolean enableInvoice = true;

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
        messages = (I18NAccount) GWT.create(I18NAccount.class);
        constants = (Constants) GWT.create(Constants.class);
        helpTexts = (HelpTexts) GWT.create(HelpTexts.class);
        elements = (Elements) GWT.create(Elements.class);

        loadCaches(constants, messages);

        DockPanel docPanel = new DockPanel();
        docPanel.setWidth("100%");
        docPanel.setHeight("100%");

        topMenu = new MenuBar();
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
        HelpPanel helpPanel = HelpPanel.getInstance(constants, messages, elements, helpTexts);
        docPanel.add(helpPanel, DockPanel.EAST);
        docPanel.setCellWidth(helpPanel, "100%");

        new Commando(this, WidgetIds.ABOUT, elements.menuitem_about()).execute();

        RootPanel.get().add(docPanel);
    }

    private void setupMenu(MenuBar topMenu) {
        MenuBar registerMenu = addTopMenu(topMenu, elements.menu_register());
        showMenu = addTopMenu(topMenu, elements.menu_show());
        peopleMenu = addTopMenu(topMenu, elements.menu_people());
        eventMenu = eventEnabled ? addTopMenu(topMenu, elements.menu_event()) : new MenuBar();
        MenuBar budgetMenu = addTopMenu(topMenu, elements.menu_budget());

        reportsMenu = addTopMenu(topMenu, elements.menu_reports());
        settingsMenu = addTopMenu(topMenu, elements.menu_settings());
        MenuBar importExportMenu = addTopMenu(topMenu, elements.menu_export_import());
        MenuBar portalMenu = null;

        if (reducedMode == 0) {
            portalMenu = addTopMenu(topMenu, elements.menu_portal());
        }

        MenuBar adminMenu = null;
        if (Window.Location.getHostName().startsWith("master.")) {
            adminMenu = addTopMenu(topMenu, elements.menu_admin());
        }

        MenuBar logoutMenu = addTopMenu(topMenu, elements.menu_logout());
        MenuBar aboutMenu = addTopMenu(topMenu, elements.menu_info());

        setupRegisterMenu(registerMenu);
        setupShowMenu();
        setupPeopleMenu();
        setupEventMenu();
        setupBudgetMenu(budgetMenu);
        setupReportsMenu();
        setupSettingsMenu();
        setupExportImportMenu(importExportMenu);
        setupPortalMenu(portalMenu);
        setupAdminMenu(adminMenu);
        setupAboutMenu(aboutMenu);
        addMenuItem(logoutMenu, elements.menuitem_logout(), WidgetIds.LOGOUT);

    }

    private void setupAdminMenu(MenuBar adminMenu) {
        if (adminMenu != null) {
            addMenuItem(adminMenu, elements.menuitem_admin_installs(), WidgetIds.ADMIN_INSTALLS);
            addMenuItem(adminMenu, elements.menuitem_admin_sql(), WidgetIds.ADMIN_SQL);
            addMenuItem(adminMenu, elements.menuitem_admin_operations(), WidgetIds.ADMIN_OPERATIONS);
            addMenuItem(adminMenu, elements.menuitem_admin_stats(), WidgetIds.ADMIN_STATS);
            addMenuItem(adminMenu, elements.menuitem_admin_poststed(), WidgetIds.ADMIN_NORWEGIAN_CITIES);
            addMenuItem(adminMenu, elements.menuitem_admin_back_admin(), WidgetIds.ADMIN_BACKUP_OPERATIONS);
        }
    }

    private void setupAboutMenu(MenuBar aboutMenu) {
        addMenuItem(aboutMenu, elements.menuitem_about(), WidgetIds.ABOUT);
        addMenuItem(aboutMenu, elements.menuitem_calculator(), WidgetIds.CALCULATOR);
        addMenuItem(aboutMenu, elements.menuitem_serverinfo(), WidgetIds.SERVERINFO);
        addMenuItem(aboutMenu, elements.menuitem_sessioninfo(), WidgetIds.SESSIONINFO);
        addMenuItem(aboutMenu, elements.menuitem_log(), WidgetIds.LOGGING);
        addMenuItem(aboutMenu, elements.menuitem_backup(), WidgetIds.BACKUP);
        addMenuItem(aboutMenu, elements.menuitem_delete(), WidgetIds.REQUEST_DELETE);
    }

    private void setupPortalMenu(MenuBar portalMenu) {
        if (reducedMode == 0) {
            addMenuItem(portalMenu, elements.menuitem_portal_settings(), WidgetIds.PORTAL_SETTINGS);
            addMenuItem(portalMenu, elements.menuitem_portal_members(), WidgetIds.PORTAL_MEMBERLIST);
            addMenuItem(portalMenu, elements.menuitem_portal_profilegallery(), WidgetIds.PORTAL_PROFILE_GALLERY);
        }
    }

    private void setupExportImportMenu(MenuBar importExportMenu) {
        if (reducedMode == 0) {
            addMenuItem(importExportMenu, elements.menuitem_export_person(), WidgetIds.EXPORT_PERSON);
            addMenuItem(importExportMenu, elements.menuitem_import_person(), WidgetIds.IMPORT_PERSON);
            addMenuItem(importExportMenu, elements.menuitem_import_filter(), WidgetIds.IMPORT_FILTER);
        }

        addMenuItem(importExportMenu, elements.menuitem_export_accounting(), WidgetIds.EXPORT_ACCOUNTING);
    }

    private void setupSettingsMenu() {
        if (reducedMode == 0) {
            addMenuItem(settingsMenu, elements.menuitem_useradm(), WidgetIds.EDIT_USERS);
        }
        addMenuItem(settingsMenu, elements.menuitem_email_settings(), WidgetIds.EDIT_EMAIL_CONTENT);
        if (reducedMode == 0) {
            addMenuItem(settingsMenu, elements.menuitem_edit_trust(), WidgetIds.EDIT_TRUST);
        }
        if (reducedMode == 0) {
            if (enableInvoice)
                addMenuItem(settingsMenu, elements.menuitem_settings_invoice(), WidgetIds.INVOICE_SETTINGS);
            addMenuItem(settingsMenu, elements.menuitem_edit_trust_actions(), WidgetIds.EDIT_TRUST_ACTIONS);
        }
        addMenuItem(settingsMenu, elements.menuitem_accounts(), WidgetIds.EDIT_ACCOUNTS);
        addMenuItem(settingsMenu, elements.menuitem_accounttrack(), WidgetIds.EDIT_ACCOUNTTRACK);
        if (reducedMode == 0) {
            addMenuItem(settingsMenu, elements.menuitem_membership_prices(), WidgetIds.EDIT_PRICES);
        }
        addMenuItem(settingsMenu, elements.menuitem_projects(), WidgetIds.EDIT_PROJECTS);
        addMenuItem(settingsMenu, elements.menuitem_semesters(), WidgetIds.EDIT_SEMESTER);
        addMenuItem(settingsMenu, elements.menuitem_edit_happening(), WidgetIds.EDIT_HAPPENING);
        addMenuItem(settingsMenu, elements.menuitem_values(), WidgetIds.SETTINGS);
        addMenuItem(settingsMenu, elements.menuitem_integration(), WidgetIds.INTEGRATION);
    }

    private void setupReportsMenu() {

        MenuBar membershipSubMenu = new MenuBar(true);
        MenuBar accountingSubMenu = new MenuBar(true);
        MenuBar invoiceSubMenu = new MenuBar(true);
        MenuBar belongingsSubMenu = new MenuBar(true);

        reportsMenu.addItem(new MenuItem(elements.menu_sub_members(), false, membershipSubMenu));
        reportsMenu.addItem(new MenuItem(elements.menu_sub_accounting(), false, accountingSubMenu));

        if (enableInvoice) {
            reportsMenu.addItem(new MenuItem(elements.menu_sub_invoice(), false, invoiceSubMenu));
        }
        reportsMenu.addItem(new MenuItem(elements.menu_sub_belongings(), false, belongingsSubMenu));

        addMenuItem(membershipSubMenu, elements.menuitem_report_member_per_year(), WidgetIds.REPORT_MEMBER_PER_YEAR);
        addMenuItem(membershipSubMenu, elements.menuitem_report_member_per_year_gender(), WidgetIds.REPORT_MEMBER_PER_YEAR_GENDER);

        if (reducedMode == 0) {
            addMenuItem(membershipSubMenu, elements.menuitem_report_member_per_year(), WidgetIds.REPORT_MEMBER_PER_YEAR);
            addMenuItem(membershipSubMenu, elements.menuitem_report_member_per_year_gender(), WidgetIds.REPORT_MEMBER_PER_YEAR_GENDER);
            addMenuItem(membershipSubMenu, elements.menuitem_report_addresses(), WidgetIds.REPORT_ADDRESSES);
        }
        addMenuItem(accountingSubMenu, elements.menuitem_report_selectedlines(), WidgetIds.REPORT_SELECTEDLINES);

        if (reducedMode == 0) {
            addMenuItem(reportsMenu, elements.menuitem_report_letter(), WidgetIds.REPORT_LETTER);
            addMenuItem(reportsMenu, elements.menuitem_report_massletter_odf(), WidgetIds.REPORT_ODF_LETTER);
            addMenuItem(reportsMenu, elements.menuitem_report_email(), WidgetIds.REPORT_EMAIL);


            addMenuItem(membershipSubMenu, elements.menuitem_report_users_email(), WidgetIds.REPORT_USERS_EMAIL);
        }
        addMenuItem(accountingSubMenu, elements.menuitem_report_accounttrack(), WidgetIds.REPORT_ACCOUNTTRACK);

        addMenuItem(accountingSubMenu, elements.menuitem_report_year(), WidgetIds.REPORT_YEAR);
        addMenuItem(accountingSubMenu, elements.menuitem_report_earnings_year(), WidgetIds.REPORT_EARNINGS_YEAR);

        if (reducedMode == 0) {
            addMenuItem(reportsMenu, elements.menuitem_fileManage(), WidgetIds.MANAGE_FILES);
            addMenuItem(belongingsSubMenu, elements.menuitem_report_belonging_responsible(), WidgetIds.REPORT_BELONGINGS_RESPONSIBLE);
        }

        addMenuItem(membershipSubMenu, elements.menuitem_report_missing_year_members(), WidgetIds.REPORTS_MISSING_YEAR_MEMBERSHIPS);
        addMenuItem(membershipSubMenu, elements.menuitem_report_missing_semester_members(), WidgetIds.REPORTS_MISSING_SEMESTER_MEMBERSHIPS);

        addMenuItem(invoiceSubMenu, elements.menuitem_invoice_send_email(), WidgetIds.REPORT_INVOICE_EMAIL);
        addMenuItem(invoiceSubMenu, elements.menuitem_invoice_search(), WidgetIds.REPORT_INVOICE_SEARCH);
        
    }

    private void setupBudgetMenu(MenuBar budgetMenu) {
        addMenuItem(budgetMenu, elements.menuitem_budget(), WidgetIds.BUDGET);
        addMenuItem(budgetMenu, elements.menuitem_budgetsimple(), WidgetIds.BUDGET_SIMPLE_TRACKING);
    }

    private void setupEventMenu() {
        addMenuItem(eventMenu, elements.menuitem_event_items(), WidgetIds.EVENT_ITEMS);
        addMenuItem(eventMenu, elements.menuitem_event_lists(), WidgetIds.EVENT_LIST);
    }

    private void setupPeopleMenu() {
        addMenuItem(peopleMenu, elements.menuitem_addperson(), WidgetIds.ADD_PERSON);
        addMenuItem(peopleMenu, elements.menuitem_findperson(), WidgetIds.FIND_PERSON);
    }

    private void setupShowMenu() {
        addMenuItem(showMenu, elements.menuitem_showmonth(), WidgetIds.SHOW_MONTH);
        addMenuItem(showMenu, elements.menuitem_showmonthdetails(), WidgetIds.SHOW_MONTH_DETAILS);
        if (reducedMode == 0) {
            addMenuItem(showMenu, elements.menuitem_showallmembers(), WidgetIds.SHOW_ALL_MEMBERS);
            addMenuItem(showMenu, elements.menuitem_showmembers(), WidgetIds.SHOW_MEMBERS);
            addMenuItem(showMenu, elements.menuitem_showtraining(), WidgetIds.SHOW_TRAINING_MEMBERS);
            addMenuItem(showMenu, elements.menuitem_showclassmembers(), WidgetIds.SHOW_CLASS_MEMBERS);
            addMenuItem(showMenu, elements.menuitem_owning_show(), WidgetIds.OWNINGS_LIST);
            // addMenuItem(showMenu, elements.menuitem_kid_list_transactions(),
            // WidgetIds.LIST_KID_TRANSACTIONS);
        }
    }

    private void setupRegisterMenu(MenuBar registerMenu) {
        addMenuItem(registerMenu, elements.menuitem_regline(), WidgetIds.LINE_EDIT_VIEW);
        addMenuItem(registerMenu, elements.menuitem_massregister(), WidgetIds.MASSREGISTER_VIEW);

        if (reducedMode == 0) {
            addMenuItem(registerMenu, elements.menuitem_registerMembership(), WidgetIds.REGISTER_MEMBERSHIP);
            // addMenuItem(registerMenu,
            // elements.menuitem_register_kid_membership(),
            // WidgetIds.REGISTER_KID_MEMBERSHIP);
        }

        addMenuItem(registerMenu, elements.menuitem_register_happening(), WidgetIds.REGISTER_HAPPENING);

        if (reducedMode == 0) {
            if (enableInvoice) {
                MenuBar invoiceSubMenu = new MenuBar(true);
                registerMenu.addItem(new MenuItem(elements.menu_sub_invoice(), false, invoiceSubMenu));

                addMenuItem(invoiceSubMenu, elements.menuitem_invoice_new(), WidgetIds.INVOICE_NEW);
                addMenuItem(invoiceSubMenu, elements.menuitem_invoice_register(), WidgetIds.INVOICE_REGISTER);
            }
            addMenuItem(registerMenu, elements.menuitem_owning_register(), WidgetIds.OWNINGS_REGISTER);
            addMenuItem(registerMenu, elements.menuitem_truststatus(), WidgetIds.TRUST_STATUS);
        }
        addMenuItem(registerMenu, elements.menuitem_endmonth(), WidgetIds.END_MONTH);
        addMenuItem(registerMenu, elements.menuitem_endsemester(), WidgetIds.END_SEMESTER);
        addMenuItem(registerMenu, elements.menuitem_endyear(), WidgetIds.END_YEAR);
    }

    private void addMenuItem(MenuBar menu, String title, WidgetIds widgetId) {
        MenuItem item = menu.addItem(title, true, new Commando(this, widgetId, title));
        item.addStyleName(title.replaceAll(" ", "_"));
    }

    private MenuBar addTopMenu(MenuBar topMenu, String header) {
        MenuBar menu = new MenuBar(true);
        MenuItem item = new MenuItem(header, menu);
        item.addStyleName(header.replaceAll(" ", "_"));
        topMenu.addItem(item);
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

    static void setActiveWidget(Widget widget) {
        activeView.clear();
        activeView.add(widget, DockPanel.CENTER);
        activeView.setCellHeight(widget, "100%");
        activeView.setCellVerticalAlignment(widget, HasVerticalAlignment.ALIGN_TOP);
        widget.setVisible(true);
    }

    @Override
    public void openDetails(String id) {
        HelpPanel helpPanel = HelpPanel.getInstance(constants, messages, elements, helpTexts);
        LineEditView widget = LineEditView.getInstance(this, messages, constants, helpPanel, elements);
        widget.init(id);

        setActiveWidget(widget);
        helpPanel.setCurrentWidget(widget, WidgetIds.LINE_EDIT_VIEW);
        Window.setTitle(elements.menuitem_showmonthdetails());
    }

    @Override
    public void viewMonth(int year, int month) {
        MonthView instance = MonthView.getInstance(constants, messages, this, elements);

        instance.init(year, month);

        setActiveWidget(instance);
        HelpPanel.getInstance(constants, messages, elements, helpTexts).setCurrentWidget(instance, WidgetIds.SHOW_MONTH);
        Window.setTitle(elements.menuitem_showmonth());
    }

    @Override
    public void searchPerson() {
        PersonSearchView widget = PersonSearchView.getInstance(this, messages, constants, elements);
        setActiveWidget(widget);
        HelpPanel.getInstance(constants, messages, elements, helpTexts).setCurrentWidget(widget, WidgetIds.FIND_PERSON);
        Window.setTitle(elements.menuitem_showmonth());
    }

    @Override
    public void viewMonth() {
        MonthView instance = MonthView.getInstance(constants, messages, this, elements);

        instance.init();

        setActiveWidget(instance);
        HelpPanel.getInstance(constants, messages, elements, helpTexts).setCurrentWidget(instance, WidgetIds.SHOW_MONTH);
        Window.setTitle(elements.menuitem_showmonth());
    }

    @Override
    public void editPerson(String id) {
        HelpPanel helpPanel = HelpPanel.getInstance(constants, messages, elements, helpTexts);

        PersonEditView widget = PersonEditView.getInstance(constants, messages, helpPanel, this, elements);

        widget.init(id);
        setActiveWidget(widget);
        helpPanel.setCurrentWidget(widget, WidgetIds.ADD_PERSON);
        Window.setTitle(elements.title_change_person());
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

    @Override
    public void openView(WidgetIds view, String title, Object... params) {
        new Commando(this, view, title, params).execute();
    }

    @Override
    public void openMassletterEditSimple(String filename, String response) {
        new Commando(this, WidgetIds.EDIT_MASSLETTER_SIMPLE, elements.title_edit_massletter(), filename, response).execute();

    }

    @Override
    public void setReducedMode(int v) {
        reducedMode = v;
        Util.log("Reduced mode:" + v);

        if (!menuSetUp) {
            menuSetUp = true;
            setupMenu(topMenu);
        }
    }

    @Override
    public void openEvent(String id) {
        if (id != null) {
            new Commando(this, WidgetIds.EVENT_EDIT, elements.event_edit(), id).execute();
        } else {
            new Commando(this, WidgetIds.EVENT_EDIT, elements.event_edit()).execute();
        }

    }

    @Override
    public void openEventPartisipants(String id) {
        new Commando(this, WidgetIds.EVENT_PARTISIPANTS_LIST, elements.event_list_partisipants(), id).execute();
    }

    @Override
    public void editEmailTemplateInvoice(String id) {
        new Commando(this, WidgetIds.EDIT_INVOICE_EMAIL, elements.invoice_edit_email_template(), id).execute();
    }

}
