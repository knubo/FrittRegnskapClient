package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponseString;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GeneralReportView extends Composite implements ClickListener, ServerResponseString {

    private static GeneralReportView me;

    private final Constants constants;

    private final I18NAccount messages;

    private Elements elements;

    private NamedButton reportButton;

    private HTML result;

    private TextBoxWithErrorText yearTextBox;

    public GeneralReportView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;

        DockPanel dp = new DockPanel();

        reportButton = new NamedButton("GeneralReport.reportButton", elements.do_report());
        reportButton.addClickListener(this);

        yearTextBox = new TextBoxWithErrorText("year");

        HorizontalPanel hp = new HorizontalPanel();
        hp.add(new Label(elements.year()));
        hp.add(yearTextBox);
        
        dp.add(hp, DockPanel.NORTH);
        dp.add(reportButton, DockPanel.NORTH);

        result = new HTML();
        dp.add(result, DockPanel.NORTH);
        
        initWidget(dp);
    }

    public static GeneralReportView show(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new GeneralReportView(messages, constants, elements);
        }
        me.setVisible(true);
        return me;
    }

    public void onClick(Widget sender) {
        StringBuffer sb = new StringBuffer();
        sb.append("year=");
        sb.append(yearTextBox.getText());
        AuthResponder.post(constants, messages, this, sb, "reports/sum_years.php");
    
    }

    public void serverResponse(String response) {
        result.setHTML(response);
    }

    public void serverResponse(JSONValue resonseObj) {
        /* Not used */
    }

    
    

}
