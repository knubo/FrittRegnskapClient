package no.knubo.accounting.client.views.exportimport.person;

import net.binarymuse.gwt.client.ui.wizard.Wizard;
import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class ImportPersonView extends Composite implements SubmitCompleteHandler {

    private static ImportPersonView instance;
    private Hidden hiddenAction;
    private final Elements elements;
    private VerticalPanel panel;
    private Hidden hiddenExclude;
    private FormPanel form;
    private ChooseFieldsAndDataPage chooseFieldsAndDataPage;
    private PreviewPage previewPage;

    public static ImportPersonView getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (instance == null) {
            instance = new ImportPersonView(messages, constants, elements);
        }
        return instance;
    }

    public ImportPersonView(final I18NAccount messages, Constants constants, final Elements elements) {

        this.elements = elements;
        form = new FormPanel();
        form.setAction(constants.baseurl() + "exportimport/personimport.php");

        // Because we're going to add a FileUpload widget, we'll need to set the
        // form to use the POST method, and multi-part MIME encoding.
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);
        form.addSubmitCompleteHandler(this);

        panel = new VerticalPanel();
        form.setWidget(panel);

        hiddenAction = new Hidden();
        hiddenAction.setName("action");
        panel.add(hiddenAction);

        hiddenExclude = new Hidden();
        hiddenExclude.setName("exclude");
        panel.add(hiddenExclude);

    
        panel.add(createWizard());


        initWidget(form);
    }

    private Widget createWizard() {
        Wizard<ImportPersonContext> wizard = new Wizard<ImportPersonContext>(elements.menuitem_import_person(),
                new ImportPersonContext());
        chooseFieldsAndDataPage = new ChooseFieldsAndDataPage(elements, hiddenAction, form);
        previewPage = new PreviewPage(elements);

        wizard.addPage(new WelcomePage(elements));
        wizard.addPage(new SelectFilePage(elements, hiddenAction, form));
        wizard.addPage(chooseFieldsAndDataPage);
        wizard.addPage(previewPage);
        wizard.addPage(new ResultPage(elements));
        wizard.setSize("800px", "600px");

        previewPage.addEventListeners();
        
        return wizard;
    }


    public void onSubmitComplete(SubmitCompleteEvent event) {
        if (hiddenAction.getValue().equals("findfields")) {
            chooseFieldsAndDataPage.setHTMLInDataTable(event.getResults());

        } else if(hiddenAction.getValue().equals("preview")) {
            previewPage.setPreviewHTML(event.getResults());
        }
    }

}
