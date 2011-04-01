package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponseString;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class GeneralReportView extends Composite implements ClickHandler, ServerResponseString {

    private static GeneralReportView me;

    private final Constants constants;

    private final I18NAccount messages;


    private NamedButton reportButton;

    private HTML result;

    private TextBoxWithErrorText yearTextBox;

    private TextBoxWithErrorText monthTextBox;

    private HorizontalPanel hp;

    public GeneralReportView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;

        DockPanel dp = new DockPanel();

        reportButton = new NamedButton("GeneralReport.reportButton", elements.do_report());
        reportButton.addClickHandler(this);

        yearTextBox = new TextBoxWithErrorText("year");
        monthTextBox = new TextBoxWithErrorText("month");

        hp = new HorizontalPanel();
        hp.add(new Label(elements.year()));
        hp.add(yearTextBox);
        hp.add(new Label(elements.month()));
        hp.add(monthTextBox);
        
        dp.add(hp, DockPanel.NORTH);
        dp.add(reportButton, DockPanel.NORTH);

        result = new HTML();
        dp.add(result, DockPanel.NORTH);
        
        initWidget(dp);
    }
    
    public void initSumYears() {
        hp.setVisible(true);
        reportButton.setVisible(true);
        result.setHTML("");
    }
    
    public void initBelongings() {
        hp.setVisible(false);
        reportButton.setVisible(false);
        result.setHTML("");
        AuthResponder.get(constants, messages, this, "reports/belongings_responsible.php");

    }

    public static GeneralReportView show(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new GeneralReportView(messages, constants, elements);
        }
        me.setVisible(true);
        return me;
    }

    public void onClick(ClickEvent event) {
        
        MasterValidator mv = new MasterValidator();
        mv.mandatory(messages.year_required(), yearTextBox);
        
        if(!mv.validateStatus()) {
            return;
        }

        
        StringBuffer sb = new StringBuffer();
        sb.append("year=");
        sb.append(yearTextBox.getText());

        Util.addPostParam(sb, "month", monthTextBox.getText());
        AuthResponder.post(constants, messages, this, sb, "reports/sum_years.php");
    
    }

    public void serverResponse(String response) {
        result.setHTML(response);
    }

    public void serverResponse(JSONValue resonseObj) {
        /* Not used */
    }

    
    

}
