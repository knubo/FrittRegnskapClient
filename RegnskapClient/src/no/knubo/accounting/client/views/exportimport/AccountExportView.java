package no.knubo.accounting.client.views.exportimport;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AccountExportView extends Composite implements ClickHandler {

    private static AccountExportView instance;

    public static AccountExportView getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (instance == null) {
            instance = new AccountExportView(messages, constants, elements);
        }
        return instance;
    }

    private final Constants constants;
    private NamedButton exportFormattedButton;
    private final I18NAccount messages;
    private NamedButton exportRawButton;
    private TextBoxWithErrorText yearTextBox;

    public AccountExportView(final I18NAccount messages, Constants constants, final Elements elements) {
        this.messages = messages;
        this.constants = constants;
        VerticalPanel panel = new VerticalPanel();

        yearTextBox = new TextBoxWithErrorText("year");
        FlowPanel yearPanel = new FlowPanel();
        yearPanel.add(new Label(elements.year()));
        yearPanel.add(yearTextBox);

        panel.add(yearPanel);

        HorizontalPanel buttonPanel = new HorizontalPanel();
        exportFormattedButton = new NamedButton("export_formatted_button", elements.export_formatted());
        exportFormattedButton.addClickHandler(this);
        exportFormattedButton.addStyleName("buttonrow");

        exportRawButton = new NamedButton("export_raw_button", elements.export_raw());
        exportRawButton.addClickHandler(this);
        exportRawButton.addStyleName("buttonrow");

        buttonPanel.add(exportFormattedButton);
        buttonPanel.add(exportRawButton);
        panel.add(buttonPanel);

        initWidget(panel);

    }

    public void onClick(ClickEvent event) {
        MasterValidator mv = new MasterValidator();
        mv.mandatory(messages.required_field(), yearTextBox);
        mv.year(messages.year_required(), yearTextBox);
        
        if(!mv.validateStatus()) {
            return;
        }
        
        if(event.getSource() == exportFormattedButton) {
            Window.open(this.constants.baseurl() + "exportimport/accountexport.php?action=excel&year="+yearTextBox.getText(),
                    "_blank", "");

        } else if(event.getSource() == exportRawButton) {
            Window.open(this.constants.baseurl() + "exportimport/accountexport.php?action=raw&year="+yearTextBox.getText(),
            "_blank", "");
        }
    }

}
