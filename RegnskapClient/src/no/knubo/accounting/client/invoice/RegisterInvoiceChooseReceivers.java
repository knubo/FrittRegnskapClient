package no.knubo.accounting.client.invoice;

import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.views.PersonPickCallback;
import no.knubo.accounting.client.views.PersonSearchView;
import no.knubo.accounting.client.views.ViewCallback;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegisterInvoiceChooseReceivers extends WizardPage<InvoiceContext> implements PersonPickCallback {

    public static final PageID PAGEID = new PageID();
    private final Elements elements;
    private final I18NAccount messages;
    private final Constants constants;

    FlowPanel vp = new FlowPanel();
    private AccountTable selected;

    public RegisterInvoiceChooseReceivers(Elements elements, I18NAccount messages, Constants constants,
            ViewCallback caller) {
        this.elements = elements;
        this.messages = messages;
        this.constants = constants;

        PersonSearchView searchView = PersonSearchView.pick(this, messages, constants, elements);

        DecoratedTabPanel tabPanel = new DecoratedTabPanel();
        vp.add(tabPanel);
        
        tabPanel.add(createMainView(), "Lister");
        tabPanel.add(searchView, elements.search());
        
        selected = new AccountTable("tableborder");

        selected.setHeadingWithColspan(0, 3, elements.invoice_recepients());
        selected.setHeaders(1, elements.firstname(), elements.lastname(), "");
        
        vp.add(selected);
    }

    private Widget createMainView() {
        return new AccountTable("tableborder");
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
        
    }

}
