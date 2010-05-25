package no.knubo.accounting.client.views.exportimport.person;

import java.util.ArrayList;
import java.util.HashSet;

import net.binarymuse.gwt.client.ui.wizard.Wizard;
import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class ImportPersonView extends Composite implements SubmitCompleteHandler, ClickHandler {

    private static ImportPersonView instance;
    private Hidden hiddenAction;
    private final Elements elements;
    private HTMLPanel dataTable;
    private HTML errorLabel;
    private ArrayList<ListBoxWithErrorText> allBoxes;
    private VerticalPanel panel;
    private HashSet<String> excludedElements;
    private Button insertButton;
    private Hidden hiddenExclude;

    public static ImportPersonView getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (instance == null) {
            instance = new ImportPersonView(messages, constants, elements);
        }
        return instance;
    }

    public ImportPersonView(final I18NAccount messages, Constants constants, final Elements elements) {

        this.elements = elements;
        final FormPanel form = new FormPanel();
        form.setAction(constants.baseurl() + "exportimport/personimport.php");

        // Because we're going to add a FileUpload widget, we'll need to set the
        // form to use the POST method, and multi-part MIME encoding.
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);
        form.addSubmitCompleteHandler(this);

        panel = new VerticalPanel();
        form.setWidget(panel);

        Label delimiter = new Label(elements.delimiter());
        panel.add(delimiter);

        TextBox delimiterBox = new TextBox();
        delimiterBox.setName("delimiter");
        delimiterBox.setMaxLength(1);

        panel.add(delimiterBox);

        Label tb = new Label(elements.file());
        panel.add(tb);

        hiddenAction = new Hidden();
        hiddenAction.setName("action");
        panel.add(hiddenAction);

        hiddenExclude = new Hidden();
        hiddenExclude.setName("exclude");
        panel.add(hiddenExclude);

        FileUpload upload = new FileUpload();
        upload.setName("uploadFormElement");
        panel.add(upload);

        Button uploadButton = new Button(elements.find_fields(), new ClickHandler() {
            public void onClick(ClickEvent event) {
                hiddenAction.setValue("findfields");
                form.submit();
            }
        });

        insertButton = new Button(elements.add(), new ClickHandler() {
            public void onClick(ClickEvent event) {
                boolean result = Window.confirm(messages.confirm_import_person());
                hiddenExclude.setValue(createExcludeList());
                if (result) {
                    hiddenAction.setValue("insert");

                    form.submit();
                }
            }
        });

        insertButton.setVisible(false);

        FlowPanel fp = new FlowPanel();
        fp.add(uploadButton);
        fp.add(insertButton);

        panel.add(fp);

        panel.add(createWizard());
        
        errorLabel = new HTML();
        panel.add(errorLabel);

        dataTable = new HTMLPanel("");
        panel.add(dataTable);

        initWidget(form);
    }

    private Widget createWizard() {
        Wizard<ImportPersonContext> wizard =
            new Wizard<ImportPersonContext>(elements.menuitem_import_person(), new ImportPersonContext());
        wizard.addPage(new WelcomePage());
        wizard.addPage(new SelectFilePage());
        wizard.addPage(new ChooseFieldsAndDataPage());
        wizard.addPage(new PreviewPage());
        wizard.addPage(new ResultPage());
        wizard.setSize("800px", "600px");
        
        
        return wizard;
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

    public void onSubmitComplete(SubmitCompleteEvent event) {
        insertButton.setVisible(true);
        if (hiddenAction.getValue().equals("findfields")) {
            panel.remove(dataTable);
            excludedElements = new HashSet<String>();

            dataTable = new HTMLPanel(event.getResults());
            panel.add(dataTable);
            addFormElements();
        }
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
        ListBoxWithErrorText box = new ListBoxWithErrorText("select" + col, errorLabel);
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
        box.addItem(elements.getString("gender") + " (M/F)",
                "gender");

        allBoxes.add(box);

        return box;
    }

}