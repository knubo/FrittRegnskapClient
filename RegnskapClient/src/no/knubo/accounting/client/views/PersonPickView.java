package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.NamedButton;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;

public class PersonPickView extends DialogBox implements ClickListener,
        PersonPickCallback {

    private static PersonPickView me;
    private final PersonPickCallback personPick;
    private static HelpPanel helpPanel;

    public static PersonPickView show(I18NAccount messages,
            Constants constants, PersonPickCallback personPick, HelpPanel helpPanel) {
        PersonPickView.helpPanel = helpPanel;
        if (me == null) {
            me = new PersonPickView(messages, constants, personPick);
        }
        return me;
    }

    /**
     * Displays details for a given line.
     * 
     * @param messages
     * @param constants
     * @param line
     * @param caller
     */
    private PersonPickView(I18NAccount messages, Constants constants,
            PersonPickCallback personPick) {
        this.personPick = personPick;
        setText(messages.choose_person());
        
        DockPanel dp = new DockPanel();
        dp.add(PersonSearchView.pick(this, messages, constants),
                DockPanel.NORTH);
        Button hideButton = new NamedButton("person_pick_close", messages
                .person_pick_close());
        hideButton.addClickListener(this);
        dp.add(hideButton, DockPanel.NORTH);
        setWidget(dp);
    }

    public void onClick(Widget sender) {
        hide();
    }

    public void pickPerson(String id, JSONObject personObj) {
        hide();
        personPick.pickPerson(id, personObj);
    }

    public void init() {
        helpPanel.addEventHandler();
    }
}
