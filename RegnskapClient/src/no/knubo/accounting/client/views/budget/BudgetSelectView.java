package no.knubo.accounting.client.views.budget;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BudgetSelectView extends DialogBox implements ClickHandler {

    private static BudgetSelectView me;
    private TextBoxWithErrorText newBudgetYear;
    private ListBoxWithErrorText budgetYear;
    private NamedButton budgetUserButton;
    private BudgetView budgetView;
    private final I18NAccount messages;

    private BudgetSelectView(Elements elements, I18NAccount messages) {
        this.messages = messages;
        setText(elements.choose_year());

        VerticalPanel vp = new VerticalPanel();

        budgetYear = new ListBoxWithErrorText("budget_year_select");
        vp.add(budgetYear);

        HorizontalPanel hp1 = new HorizontalPanel();

        hp1.add(new Label(elements.budget_new_year()));
        newBudgetYear = new TextBoxWithErrorText("budget_year");
        hp1.add(newBudgetYear);

        vp.add(hp1);

        budgetUserButton = new NamedButton("budget_use", elements.select_budget_year());
        vp.add(budgetUserButton);
        budgetUserButton.addClickHandler(this);

        setWidget(vp);
    }

    public static BudgetSelectView getInstance(Elements elements, I18NAccount messages) {
        if (me == null) {
            me = new BudgetSelectView(elements, messages);
        }
        return me;
    }

    public void setBudgetYears(JSONArray data) {
        budgetYear.clear();
        budgetYear.addItem(new JSONString(""), new JSONString(""));
        for (int i = 0; i < data.size(); i++) {
            JSONValue year = data.get(i).isObject().get("year");
            budgetYear.addItem(year, year);
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        String selectedYear = Util.getSelected(budgetYear.getListbox());
        
        if (!selectedYear.isEmpty()) {
            budgetView.setBudgetYear(selectedYear);
            hide();
        } else {
            MasterValidator mv = new MasterValidator();
            mv.year(messages.illegal_year(), newBudgetYear);

            if (mv.validateStatus()) {
                budgetView.setBudgetYear(newBudgetYear.getText());
                hide();
            }
        }
    }

    public void selectBudgetYear(BudgetView budgetView) {
        this.budgetView = budgetView;
        show();
    }

}
