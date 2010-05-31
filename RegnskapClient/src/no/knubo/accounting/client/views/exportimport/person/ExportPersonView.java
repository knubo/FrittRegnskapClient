package no.knubo.accounting.client.views.exportimport.person;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ExportPersonView extends Composite implements ClickHandler {

    private static ExportPersonView instance;

    public static ExportPersonView getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (instance == null) {
            instance = new ExportPersonView(messages, constants, elements);
        }
        return instance;
    }

    private TextBoxWithErrorText delimiterBox;
    private final Constants constants;
    private NamedButton exportButton;
    private final I18NAccount messages;

    public ExportPersonView(final I18NAccount messages, Constants constants, final Elements elements) {
        this.messages = messages;
        this.constants = constants;
        VerticalPanel panel = new VerticalPanel();
        Label delimiter = new Label(elements.delimiter());
        panel.add(delimiter);

        delimiterBox = new TextBoxWithErrorText("delimiter");
        delimiterBox.setName("delimiter");
        delimiterBox.setMaxLength(1);
        panel.add(delimiterBox);

        exportButton = new NamedButton("export_button", elements.export_spreadsheet());
        exportButton.addClickHandler(this);
        panel.add(exportButton);

        initWidget(panel);

    }

    public void init() {
        delimiterBox.setFocus(true);
    }
    
    public void onClick(ClickEvent event) {
        MasterValidator mv = new MasterValidator();
        mv.mandatory(messages.required_field(), delimiterBox);
        
        if(!mv.validateStatus()) {
            return;
        }
        
        Window.open(this.constants.baseurl() + "exportimport/personexport.php?delimiter=" + URL.encodeComponent(delimiterBox.getText()),
                "_blank", "");
    }

}
