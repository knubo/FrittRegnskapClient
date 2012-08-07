package no.knubo.accounting.client;

import static no.knubo.accounting.client.AccountingGWT.constants;
import static no.knubo.accounting.client.AccountingGWT.elements;
import static no.knubo.accounting.client.AccountingGWT.helpTexts;
import static no.knubo.accounting.client.AccountingGWT.messages;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.invoice.InvoiceSettings;
import no.knubo.accounting.client.invoice.SendInvoiceEmail;
import no.knubo.accounting.client.misc.WidgetIds;
import no.knubo.accounting.client.newinvoice.RegisterInvoiceView;
import no.knubo.accounting.client.views.AboutView;
import no.knubo.accounting.client.views.HappeningsView;
import no.knubo.accounting.client.views.IntegrationView;
import no.knubo.accounting.client.views.LineEditView;
import no.knubo.accounting.client.views.LogView;
import no.knubo.accounting.client.views.LogoutView;
import no.knubo.accounting.client.views.MassRegisterView;
import no.knubo.accounting.client.views.MonthAndSemesterEndView;
import no.knubo.accounting.client.views.MonthDetailsView;
import no.knubo.accounting.client.views.MonthView;
import no.knubo.accounting.client.views.PersonSearchView;
import no.knubo.accounting.client.views.RegisterHappeningView;
import no.knubo.accounting.client.views.RegisterMembershipView;
import no.knubo.accounting.client.views.RequestDeleteView;
import no.knubo.accounting.client.views.SessionsView;
import no.knubo.accounting.client.views.ShowMembershipView;
import no.knubo.accounting.client.views.SystemInfoView;
import no.knubo.accounting.client.views.TrustStatusView;
import no.knubo.accounting.client.views.ViewCallback;
import no.knubo.accounting.client.views.YearEndView;
import no.knubo.accounting.client.views.admin.AdminBackupView;
import no.knubo.accounting.client.views.admin.AdminInstallsView;
import no.knubo.accounting.client.views.admin.AdminNorwegianCityImportView;
import no.knubo.accounting.client.views.admin.AdminOperationsView;
import no.knubo.accounting.client.views.admin.AdminSQLView;
import no.knubo.accounting.client.views.admin.AdminStatsView;
import no.knubo.accounting.client.views.budget.BudgetSimpleTracking;
import no.knubo.accounting.client.views.budget.BudgetView;
import no.knubo.accounting.client.views.events.EventListView;
import no.knubo.accounting.client.views.events.EventManagementListView;
import no.knubo.accounting.client.views.events.EventManagementView;
import no.knubo.accounting.client.views.events.EventPartisipantsListView;
import no.knubo.accounting.client.views.exportimport.AccountExportView;
import no.knubo.accounting.client.views.exportimport.FilerImportFileView;
import no.knubo.accounting.client.views.exportimport.person.ExportPersonView;
import no.knubo.accounting.client.views.exportimport.person.ImportPersonView;
import no.knubo.accounting.client.views.files.BackupView;
import no.knubo.accounting.client.views.files.ManageFilesView;
import no.knubo.accounting.client.views.kid.ListKIDView;
import no.knubo.accounting.client.views.kid.RegisterMembershipKIDView;
import no.knubo.accounting.client.views.ownings.OwningsListView;
import no.knubo.accounting.client.views.ownings.RegisterOwningsView;
import no.knubo.accounting.client.views.portal.PortalGallery;
import no.knubo.accounting.client.views.portal.PortalMemberlist;
import no.knubo.accounting.client.views.portal.PortalSettings;
import no.knubo.accounting.client.views.registers.AccountTrackEditView;
import no.knubo.accounting.client.views.registers.EmailSettingsView;
import no.knubo.accounting.client.views.registers.MembershipPriceEditView;
import no.knubo.accounting.client.views.registers.PersonEditView;
import no.knubo.accounting.client.views.registers.PostTypeEditView;
import no.knubo.accounting.client.views.registers.ProjectEditView;
import no.knubo.accounting.client.views.registers.SemesterEditView;
import no.knubo.accounting.client.views.registers.StandardvaluesView;
import no.knubo.accounting.client.views.registers.TrustActionEditView;
import no.knubo.accounting.client.views.registers.TrustEditView;
import no.knubo.accounting.client.views.registers.UsersEditView;
import no.knubo.accounting.client.views.reporting.EarningsAndCostPie;
import no.knubo.accounting.client.views.reporting.GeneralReportView;
import no.knubo.accounting.client.views.reporting.ReportAccountlines;
import no.knubo.accounting.client.views.reporting.ReportAccounttracking;
import no.knubo.accounting.client.views.reporting.ReportMassLetterODF;
import no.knubo.accounting.client.views.reporting.ReportMassLetters;
import no.knubo.accounting.client.views.reporting.ReportMembersAddresses;
import no.knubo.accounting.client.views.reporting.ReportMembersBirth;
import no.knubo.accounting.client.views.reporting.ReportMembersBirthGender;
import no.knubo.accounting.client.views.reporting.ReportUsersEmail;
import no.knubo.accounting.client.views.reporting.SimpleMassletterEditView;
import no.knubo.accounting.client.views.reporting.mail.ReportMail;

import org.gwtwidgets.client.ui.SimpleCalcPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

class Commando implements Command {

    WidgetIds action;

    private final ViewCallback callback;

    private String title;

    private final Object[] params;

    Commando(ViewCallback callback, WidgetIds action, String title, Object... params) {
        this.callback = callback;
        this.action = action;
        this.title = title;
        this.params = params;
    }

    @Override
    public void execute() {
        Widget widget = null;

        HelpPanel helpPanel = HelpPanel.getInstance(constants, messages, elements, helpTexts);
        switch (action) {
        case LINE_EDIT_VIEW:
            widget = LineEditView.getInstance(callback, messages, constants, helpPanel, elements);
            ((LineEditView) widget).init();
            break;
        case MASSREGISTER_VIEW:
            widget = MassRegisterView.getInstance(messages, constants, elements, callback);
            ((MassRegisterView) widget).init();
            break;
        case REGISTER_MEMBERSHIP:
            widget = RegisterMembershipView.getInstance(messages, constants, helpPanel, elements);
            ((RegisterMembershipView) widget).init();
            break;
        case REGISTER_KID_MEMBERSHIP:
            widget = RegisterMembershipKIDView.getInstance(messages, constants, elements, callback);
            ((RegisterMembershipKIDView) widget).init();
            break;
        case LIST_KID_TRANSACTIONS:
            widget = ListKIDView.getInstance(messages, constants, elements, helpPanel);
            break;

        case REGISTER_HAPPENING:
            widget = RegisterHappeningView.getInstance(messages, constants, callback, elements, callback);
            ((RegisterHappeningView) widget).init();
            break;
        case END_MONTH:
            widget = MonthAndSemesterEndView.getInstance(constants, messages, callback, elements);
            ((MonthAndSemesterEndView) widget).initEndMonth();
            break;
        case END_SEMESTER:
            widget = MonthAndSemesterEndView.getInstance(constants, messages, callback, elements);
            ((MonthAndSemesterEndView) widget).initEndSemester();
            break;
        case INTEGRATION:
            widget = IntegrationView.getInstance(messages, constants, elements);
            ((IntegrationView) widget).init();
            break;
        case SETTINGS:
            widget = StandardvaluesView.getInstance(messages, constants, elements);
            ((StandardvaluesView) widget).init();
            break;
        case EDIT_EMAIL_CONTENT:
            widget = EmailSettingsView.getInstance(messages, constants, elements);
            ((EmailSettingsView) widget).init();
            break;
        case EDIT_HAPPENING:
            widget = HappeningsView.getInstance(messages, constants, elements);
            ((HappeningsView) widget).init();
            break;
        case EDIT_PROJECTS:
            widget = ProjectEditView.getInstance(messages, constants, elements);
            ((ProjectEditView) widget).init();
            break;
        case EDIT_USERS:
            widget = UsersEditView.getInstance(messages, constants, helpPanel, elements);
            ((UsersEditView) widget).init();
            break;
        case EDIT_ACCOUNTS:
            widget = PostTypeEditView.getInstance(messages, constants, helpPanel, elements);
            ((PostTypeEditView) widget).init();
            break;
        case EDIT_ACCOUNTTRACK:
            widget = AccountTrackEditView.getInstance(messages, constants, helpPanel, elements);
            ((AccountTrackEditView) widget).init();
            break;
        case EDIT_TRUST_ACTIONS:
            widget = TrustActionEditView.show(messages, constants, helpPanel, elements);
            ((TrustActionEditView) widget).init();
            break;
        case EDIT_TRUST:
            widget = TrustEditView.getInstance(messages, constants, helpPanel, elements);
            ((TrustEditView) widget).init();
            break;

        case BUDGET:
            widget = BudgetView.getInstance(messages, constants, helpPanel, elements, callback);
            ((BudgetView) widget).init();
            break;
        case BUDGET_SIMPLE_TRACKING:
            widget = BudgetSimpleTracking.getInstance(messages, constants, elements);
            ((BudgetSimpleTracking) widget).init();
            break;

        case ADD_PERSON:
            widget = PersonEditView.getInstance(constants, messages, helpPanel, callback, elements);
            ((PersonEditView) widget).init(null);
            break;
        case FIND_PERSON:
            widget = PersonSearchView.getInstance(callback, messages, constants, elements);
            break;
        case SHOW_MONTH:
            widget = MonthView.getInstance(constants, messages, callback, elements);
            ((MonthView) widget).init();
            break;
        case SHOW_MONTH_DETAILS:
            widget = MonthDetailsView.getInstance(constants, messages, elements);
            ((MonthDetailsView) widget).init();
            break;
        case SHOW_MEMBERS:
            widget = ShowMembershipView.getInstance(messages, constants, callback, helpPanel, elements);
            ((ShowMembershipView) widget).initShowMembers();
            break;
        case SHOW_ALL_MEMBERS:
            widget = ShowMembershipView.getInstance(messages, constants, callback, helpPanel, elements);
            ((ShowMembershipView) widget).initShowAll();
            break;
        case SHOW_CLASS_MEMBERS:
            widget = ShowMembershipView.getInstance(messages, constants, callback, helpPanel, elements);
            ((ShowMembershipView) widget).initShowClassMembers();
            break;
        case SHOW_TRAINING_MEMBERS:
            widget = ShowMembershipView.getInstance(messages, constants, callback, helpPanel, elements);
            ((ShowMembershipView) widget).initShowTrainingMembers();
            break;
        case TRUST_STATUS:
            widget = TrustStatusView.getInstance(constants, messages, helpPanel, callback, elements);
            ((TrustStatusView) widget).init();
            break;
        case REPORT_ACCOUNTTRACK:
            widget = ReportAccounttracking.getInstance(constants, messages, elements);
            break;
        case REPORT_MEMBER_PER_YEAR:
            widget = ReportMembersBirth.getInstance(constants, messages, helpPanel, elements);
            ((ReportMembersBirth) widget).init();
            break;
        case REPORT_MEMBER_PER_YEAR_GENDER:
            widget = ReportMembersBirthGender.getInstance(constants, messages, helpPanel, elements);
            ((ReportMembersBirthGender) widget).init();
            break;

        case REPORT_ADDRESSES:
            widget = ReportMembersAddresses.getInstance(constants, messages, helpPanel, elements);
            ((ReportMembersAddresses) widget).init();
            break;
        case REPORT_SELECTEDLINES:
            widget = ReportAccountlines.getInstance(constants, messages, helpPanel, elements);
            break;
        case REPORT_LETTER:
            widget = ReportMassLetters.getInstance(constants, messages, elements, callback);
            ((ReportMassLetters) widget).init();
            break;
        case REPORT_ODF_LETTER:
            widget = ReportMassLetterODF.getInstance(constants, messages, elements);
            ((ReportMassLetterODF) widget).init();
            break;

        case REPORT_EMAIL:
            widget = ReportMail.getInstance(constants, messages, elements, callback);
            ((ReportMail) widget).initSendingEmail();
            break;
        case EDIT_INVOICE_EMAIL:
            widget = ReportMail.getInstance(constants, messages, elements, callback);
            ((ReportMail) widget).initEditEmailTemplate((String) params[0]);
            break;
        case REPORT_USERS_EMAIL:
            widget = ReportUsersEmail.getInstance(constants, messages, helpPanel, elements);
            ((ReportUsersEmail) widget).init();
            break;
        case REPORT_YEAR:
            widget = GeneralReportView.getInstance(messages, constants, elements);
            ((GeneralReportView) widget).initSumYears();
            break;
        case REPORT_BELONGINGS_RESPONSIBLE:
            widget = GeneralReportView.getInstance(messages, constants, elements);
            ((GeneralReportView) widget).initBelongings();
            break;

        case REPORT_EARNINGS_YEAR:
            widget = EarningsAndCostPie.getInstance(messages, constants, elements);
            break;
        case MANAGE_FILES:
            widget = ManageFilesView.getInstance(constants, messages, elements);
            ((ManageFilesView) widget).init();
            break;

        case ABOUT:
            widget = AboutView.getInstance(constants, messages, elements, callback, helpTexts);
            title = title + " - " + AboutView.CLIENT_VERSION;
            break;
        case SERVERINFO:
            widget = SystemInfoView.getInstance(constants, messages);
            break;
        case SESSIONINFO:
            widget = SessionsView.getInstance(constants, elements, messages);
            break;
        case LOGGING:
            widget = LogView.getInstance(messages, constants, elements);
            ((LogView) widget).init();
            break;
        case BACKUP:
            widget = BackupView.getInstance(constants, messages, elements);
            ((BackupView) widget).init();
            break;
        case LOGOUT:
            widget = LogoutView.getInstance(constants, messages, elements);
            return;
        case EDIT_PRICES:
            widget = MembershipPriceEditView.getInstance(messages, constants, elements);
            ((MembershipPriceEditView) widget).init();
            break;
        case EDIT_SEMESTER:
            widget = SemesterEditView.getInstance(messages, constants, elements);
            ((SemesterEditView) widget).init();
            break;
        case END_YEAR:
            widget = YearEndView.getInstance(constants, messages, callback, elements);
            ((YearEndView) widget).init();
            break;
        case IMPORT_PERSON:
            widget = ImportPersonView.getInstance(constants, messages, elements);
            break;
        case EXPORT_PERSON:
            widget = ExportPersonView.getInstance(constants, messages, elements);
            ((ExportPersonView) widget).init();
            break;
        case ADMIN_INSTALLS:
            widget = AdminInstallsView.getInstance(messages, constants, elements);
            ((AdminInstallsView) widget).init();
            break;
        case ADMIN_STATS:
            widget = AdminStatsView.getInstance(messages, constants, elements);
            ((AdminStatsView) widget).init();
            break;
        case ADMIN_SQL:
            widget = AdminSQLView.getInstance(messages, constants, elements);
            ((AdminSQLView) widget).init();
            break;
        case ADMIN_OPERATIONS:
            widget = AdminOperationsView.getInstance(messages, constants, elements);
            ((AdminOperationsView) widget).init();
            break;
        case ADMIN_NORWEGIAN_CITIES:
            widget = AdminNorwegianCityImportView.getInstance(messages, constants, elements);
            break;

        case EXPORT_ACCOUNTING:
            widget = AccountExportView.getInstance(constants, messages, elements);
            break;
        case EDIT_MASSLETTER_SIMPLE:
            widget = SimpleMassletterEditView.getInstance(constants, messages, elements);
            ((SimpleMassletterEditView) widget).init();
            break;
        case CALCULATOR:
            createCalculatorPopup();
            return;
        case PORTAL_MEMBERLIST:
            widget = PortalMemberlist.getInstance(constants, messages, elements, callback);
            ((PortalMemberlist) widget).init();
            break;
        case PORTAL_PROFILE_GALLERY:
            widget = PortalGallery.getInstance(constants, messages);
            ((PortalGallery) widget).init();
            break;
        case PORTAL_SETTINGS:
            widget = PortalSettings.getInstance(constants, messages, elements);
            ((PortalSettings) widget).init();
            break;
        case OWNINGS_LIST:
            widget = OwningsListView.getInstance(constants, messages, elements, helpPanel, callback);
            ((OwningsListView) widget).init();
            break;
        case OWNINGS_REGISTER:
            widget = RegisterOwningsView.getInstance(constants, messages, elements, callback);
            ((RegisterOwningsView) widget).init();
            break;
        case REQUEST_DELETE:
            widget = RequestDeleteView.getInstance(constants, messages, elements);
            break;
        case EVENT_ITEMS:
            widget = EventManagementListView.getInstance(constants, messages, elements, callback);
            ((EventManagementListView) widget).init();
            break;
        case EVENT_EDIT:
            widget = EventManagementView.getInstance(constants, messages, elements);
            if (params != null && params.length > 0) {
                ((EventManagementView) widget).init((String) params[0]);
            } else {
                ((EventManagementView) widget).init();
            }
            break;
        case EVENT_PARTISIPANTS_LIST:
            widget = EventPartisipantsListView.getInstance(constants, messages, elements);
            ((EventPartisipantsListView) widget).init((String) params[0]);
            break;
        case EVENT_LIST:
            widget = EventListView.getInstance(constants, messages, elements, callback);
            ((EventListView) widget).init();
            break;
        case IMPORT_FILTER:
            widget = FilerImportFileView.getInstance(messages, constants, elements);
            break;
        case ADMIN_BACKUP_OPERATIONS:
            widget = AdminBackupView.getInstance(messages, constants, elements);
            ((AdminBackupView) widget).init();
            break;
        case REPORTS_MISSING_SEMESTER_MEMBERSHIPS:
            widget = GeneralReportView.getInstance(messages, constants, elements);
            ((GeneralReportView) widget).initMissingSemesterMembers();
            break;
        case REPORTS_MISSING_YEAR_MEMBERSHIPS:
            widget = GeneralReportView.getInstance(messages, constants, elements);
            ((GeneralReportView) widget).initMissingYearMembers();
            break;
        case INVOICE_SETTINGS:
            widget = InvoiceSettings.getInstance(messages, constants, elements, callback);
            ((InvoiceSettings) widget).init(params);
            break;
        case INVOICE_NEW:
            widget = RegisterInvoiceView.getInstance(constants, messages, elements, callback);
            break;
        case REPORT_INVOICE_EMAIL:
            widget = SendInvoiceEmail.getInstance(messages, constants, elements);
            ((SendInvoiceEmail)widget).init();
            break;
        }

        if (widget == null) {
            Window.alert("No action");
            return;
        }
        AccountingGWT.setActiveWidget(widget);
        if (widget.getTitle() != null && widget.getTitle().length() > 0) {
            Window.setTitle(widget.getTitle());
        } else {
            Window.setTitle(title);
        }
        helpPanel.setCurrentWidget(widget, action);
    }

    private void createCalculatorPopup() {

        final DialogBox db = new DialogBox();
        db.addStyleName("calculator");
        db.setModal(false);
        db.setText(elements.menuitem_calculator());

        VerticalPanel vp = new VerticalPanel();
        SimpleCalcPanel simpleCalcPanel = new SimpleCalcPanel();
        vp.add(simpleCalcPanel);

        Button okButton = new Button(elements.ok());
        okButton.addStyleName("buttonrow");
        okButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                db.hide();
            }
        });
        vp.add(okButton);

        db.setWidget(vp);
        db.center();
    }

}