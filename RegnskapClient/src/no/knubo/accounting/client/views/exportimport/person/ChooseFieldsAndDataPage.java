package no.knubo.accounting.client.views.exportimport.person;

import java.util.ArrayList;
import java.util.HashSet;

import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import net.binarymuse.gwt.client.ui.wizard.event.NavigationEvent;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ChooseFieldsAndDataPage extends WizardPage<ImportPersonContext> implements ClickHandler {

    public static final PageID PAGEID = new PageID();

    private FlowPanel panel;
    private HTMLPanel dataTable;
    private ArrayList<ListBoxWithErrorText> allBoxes;

    private final Elements elements;

    private HashSet<String> excludedElements;

    private HTML errorLabel;

    private final I18NAccount messages;

    private boolean birthDateMandatory;

    public ChooseFieldsAndDataPage(Elements elements, I18NAccount messages) {
        this.elements = elements;
        this.messages = messages;
        panel = new FlowPanel();
        dataTable = new HTMLPanel("");
        panel.add(dataTable);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    public void setHTMLInDataTable(String data) {
        excludedElements = new HashSet<String>();

        errorLabel = new HTML();
        errorLabel.addStyleName("error");
        panel.add(errorLabel);

        panel.remove(dataTable);
        dataTable = new HTMLPanel(data);
        panel.add(dataTable);
        addFormElements();

    }


    @Override
    public String getTitle() {
        return elements.wizard_import_person_choose_fields();
    }

    @Override
    public PageID getPageID() {
        return PAGEID;
    }

    @Override
    public void beforeShow() {
        getWizard().setButtonVisible(ButtonType.BUTTON_CANCEL, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_FINISH, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_NEXT, true);
        getWizard().setButtonVisible(ButtonType.BUTTON_PREVIOUS, true);
        
        getContext().hiddenAction.setValue("findfields");
        getContext().submit();
    }

    @Override
    public void afterShow() {
        Timer t = new Timer() {
            @Override
            public void run() {
                if (allBoxes.size() > 0) {
                    allBoxes.get(0).setFocus(true);
                }
            }
        };
        t.schedule(500);
    }

    @Override
    public void beforeNext(NavigationEvent event) {
        MasterValidator mv = new MasterValidator();

        if (birthDateMandatory) {
            mv.fail(allBoxes.get(0), !Util.selectionContains(this.allBoxes, "birthdate"), messages
                    .import_birthdate_required());
        }

        mv.fail(allBoxes.get(0), !Util.selectionContains(this.allBoxes, "lastname"), messages
                .import_lastname_required());

        mv.fail(allBoxes.get(0), !Util.uniqeSelections(this.allBoxes), messages
                .import_same_field_twice());

        if(!mv.validateStatus()) {
            event.cancel();
            return;
        }
       
        getContext().hiddenExclude.setValue(createExcludeList());

    }

    private void addFormElements() {
        allBoxes = new ArrayList<ListBoxWithErrorText>();
        addColumnSelectors();
        addRowImages();
    }

    private void addRowImages() {
        int row = 0;

        Element elementById = dataTable.getElementById("remove" + row);
        while (elementById != null) {
            dataTable.addAndReplaceElement(createAddImage(String.valueOf(row)), "remove" + row);
            row++;
            elementById = dataTable.getElementById("remove" + row);
        }

    }

    private Widget createRemoveImage(String row) {
        Image image = ImageFactory.removeImage("remove" + row);
        image.addClickHandler(this);
        excludedElements.add(row);
        return image;
    }

    @Override
    public void onClick(ClickEvent event) {
        String id = event.getRelativeElement().getId();

        Util.log("Clicked on:" + id);
        if (id.startsWith("add")) {
            String row = id.substring(3);
            dataTable.addAndReplaceElement(createRemoveImage(row), id);
        } else {
            String row = id.substring(6);
            dataTable.addAndReplaceElement(createAddImage(row), id);
        }

    }

    private Widget createAddImage(String row) {
        Image image = ImageFactory.chooseImage("add" + row);
        image.addClickHandler(this);

        excludedElements.remove(row);
        return image;
    }

    private void addColumnSelectors() {
        int col = 0;

        Element elementById = dataTable.getElementById("col" + col);
        while (elementById != null) {
            dataTable.addAndReplaceElement(createListbox(col), "col" + col);
            col++;
            elementById = dataTable.getElementById("col" + col);
        }
    }

    private ListBoxWithErrorText createListbox(int col) {
        ListBoxWithErrorText box = new ListBoxWithErrorText("col" + col, errorLabel);
        box.getListbox().setName("col" + col);
        box.addItem("", "");
        box.addItem(elements.getString("firstname"), "firstname");
        box.addItem(elements.getString("lastname"), "lastname");
        box.addItem(elements.getString("email"), "email");
        box.addItem(elements.getString("address"), "address");
        box.addItem(elements.getString("postnmb"), "postnmb");
        box.addItem(elements.getString("city"), "city");
        box.addItem(elements.getString("country"), "country");
        box.addItem(elements.getString("phone"), "phone");
        box.addItem(elements.getString("cellphone"), "cellphone");
        box.addItem(elements.getString("employee"), "employee");
        box.addItem(elements.getString("birthdate") + " (dd.mm.yyyy)", "birthdate");
        box.addItem(elements.getString("newsletter"), "newsletter");
        box.addItem(elements.getString("gender") + " (M/F)", "gender");
        box.addItem(elements.getString("membership_required_year"), "membership_required_year");
        box.addItem(elements.getString("membership_required_semester"), "membership_required_semester");
        box.addItem(elements.getString("comment"), "comment");

        allBoxes.add(box);

        return box;
    }

    protected String createExcludeList() {
        StringBuilder sb = new StringBuilder();

        for (String x : excludedElements) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(x);
        }
        return sb.toString();
    }

}
