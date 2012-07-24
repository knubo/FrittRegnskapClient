package no.knubo.accounting.client.newinvoice;

import java.util.HashSet;

import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.RegnskapLocalStorage;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.views.PersonPickCallback;
import no.knubo.accounting.client.views.PersonSearchView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegisterInvoiceChooseReceivers extends WizardPage<InvoiceContext> implements PersonPickCallback,
        ClickHandler {

    public static final PageID PAGEID = new PageID();
    private final Elements elements;
    private final I18NAccount messages;
    private final Constants constants;

    FlowPanel vp = new FlowPanel();
    private AccountTable selected;
    private NamedButton requiredMembershipSemesters;
    private NamedButton requiredmembershipYear;
    private NamedButton previousYearMembership;
    private NamedButton previousSemesterMembership;

    HashSet<String> selectedIds = new HashSet<String>();

    public RegisterInvoiceChooseReceivers(Elements elements, I18NAccount messages, Constants constants) {
        this.elements = elements;
        this.messages = messages;
        this.constants = constants;

        PersonSearchView searchView = PersonSearchView.pick(this, messages, constants, elements);

        DecoratedTabPanel tabPanel = new DecoratedTabPanel();
        vp.add(tabPanel);

        tabPanel.add(createMainView(), "Lister");
        tabPanel.add(searchView, elements.search());

        tabPanel.selectTab(0);

        selected = new AccountTable("tableborder");

        selected.setHeadingWithColspan(0, 4, elements.invoice_recepients());
        selected.setHeaders(1, elements.firstname(), elements.lastname(), "", "");

        vp.add(selected);
    }

    private Widget createMainView() {
        VerticalPanel buttonPanel = new VerticalPanel();
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        requiredmembershipYear = new NamedButton("invoice_all_members_required_membership_year",
                elements.invoice_all_members_required_membership_year());
        requiredmembershipYear.addClickHandler(this);
        requiredmembershipYear.addStyleName("buttonrow");
        buttonPanel.add(requiredmembershipYear);

        requiredMembershipSemesters = new NamedButton("invoice_all_members_required_membership_semester",
                elements.invoice_all_members_required_membership_semester());
        requiredMembershipSemesters.addClickHandler(this);
        requiredMembershipSemesters.addStyleName("buttonrow");
        buttonPanel.add(requiredMembershipSemesters);

        previousYearMembership = new NamedButton("invoice_all_previous_year_membership",
                elements.invoice_all_previous_year_membership());
        previousYearMembership.addClickHandler(this);
        previousYearMembership.addStyleName("buttonrow");
        buttonPanel.add(previousYearMembership);

        previousSemesterMembership = new NamedButton("invoice_all_previous_semester_membership",
                elements.invoice_all_previous_semester_membership());
        previousSemesterMembership.addClickHandler(this);
        previousSemesterMembership.addStyleName("buttonrow");
        buttonPanel.add(previousSemesterMembership);

        return buttonPanel;
    }

    @Override
    public net.binarymuse.gwt.client.ui.wizard.WizardPage.PageID getPageID() {
        return PAGEID;
    }

    @Override
    public String getTitle() {
        return elements.invoice_choose_recepients();
    }

    @Override
    public Widget asWidget() {
        return vp;
    }

    @Override
    public void pickPerson(String id, JSONObject personObj) {
        addPerson(personObj);
    }

    @Override
    public void beforeShow() {
        getWizard().setButtonVisible(ButtonType.BUTTON_CANCEL, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_FINISH, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_NEXT, true);
        getWizard().setButtonVisible(ButtonType.BUTTON_PREVIOUS, true);

        loadRecepientsFromLocalStorage();
    }

    private void loadRecepientsFromLocalStorage() {
        selectedIds.clear();

        while(selected.getRowCount() > 2) {
            selected.removeRow(2);
        }

        JSONArray receivers = RegnskapLocalStorage.getInvoiceReciversAsJSONArray();

        for (int i = 0; i < receivers.size(); i++) {
            addPerson(receivers.get(i).isObject());
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == requiredmembershipYear) {
            fetchRecepients("members_must_have_year");

        } else if (event.getSource() == requiredMembershipSemesters) {
            fetchRecepients("members_must_have_semester");

        } else if (event.getSource() == previousYearMembership) {
            fetchRecepients("members_previous_year");

        } else if (event.getSource() == previousSemesterMembership) {
            fetchRecepients("members_previous_semester");

        } else if (event.getSource() instanceof Image) {
            removeSelected((Image) event.getSource());

        }
    }

    private void removeSelected(Image source) {
        String personId = source.getElement().getId();

        RegnskapLocalStorage.removePerson(personId);

        String id = personId.substring("person_".length());
        selectedIds.remove(id);

        for (int i = 2; i < selected.getRowCount(); i++) {
            Image image = (Image) selected.getWidget(i, 3);

            if (image.getElement().getId().equals(personId)) {
                selected.removeRow(i);
                return;
            }
        }

    }

    private void fetchRecepients(String string) {
        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONArray array = responseObj.isArray();

                for (int i = 0; i < array.size(); i++) {
                    JSONObject person = array.get(i).isObject();

                    addPerson(person);
                }

            }
        };

        AuthResponder.get(constants, messages, callback, "accounting/invoice_ops.php?action=" + string);
    }

    protected void addPerson(JSONObject person) {
        String id = Util.str(person.get("id"));

        if (selectedIds.contains(id)) {
            return;
        }

        selectedIds.add(id);

        int row = selected.getRowCount();
        selected.setText(row, Util.str(person.get("firstname")), Util.str(person.get("lastname")), "", id);
        Image removeImage = ImageFactory.removeImage("remove");
        removeImage.getElement().setId("person_" + id);
        removeImage.addClickHandler(this);
        selected.setWidget(row, 3, removeImage);

        RegnskapLocalStorage.savePerson(id, person);
    }

}
