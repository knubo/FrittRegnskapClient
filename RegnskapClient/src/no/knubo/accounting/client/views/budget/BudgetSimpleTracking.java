package no.knubo.accounting.client.views.budget;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class BudgetSimpleTracking  extends Composite implements ClickHandler {

    private static BudgetSimpleTracking me;
    private ListBoxWithErrorText yearBox;
    private final I18NAccount messages;
    private final Constants constants;
    private NamedButton selectButton;

    public BudgetSimpleTracking(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        DockPanel dp = new DockPanel();

        HorizontalPanel hp = new HorizontalPanel();
        dp.add(hp, DockPanel.NORTH);
        hp.add(new Label(elements.choose_year()));
        
        yearBox = new ListBoxWithErrorText("budget_years");
        hp.add(yearBox);

        selectButton = new NamedButton("select_year", elements.select_budget_year());
        selectButton.addClickHandler(this);
        dp.add(selectButton, DockPanel.NORTH);
        initWidget(dp);
    }

    public static Widget getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if(me == null) {
            me = new BudgetSimpleTracking(messages, constants, elements);
        }
        return me;
    }
    
    public void init() {
        ServerResponse rh = new ServerResponse() {
            
            public void serverResponse(JSONValue responseObj) {
                JSONArray array = responseObj.isArray();
                
                yearBox.clear();
                
                for(int i=0;i<array.size();i++) {
                    yearBox.addItem(array.get(i).isObject().get("year"));
                }
            }
        }; 
        AuthResponder.get(constants, messages, rh, constants.baseurl() + "accounting/budget.php?action=years");

    }

    public void onClick(ClickEvent event) {
    }

}
