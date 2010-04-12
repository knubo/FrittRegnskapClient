package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedTextArea;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EditMassletterView extends DialogBox implements ClickHandler {

    private static EditMassletterView me;
    private NamedTextArea source;
    private NamedButton cancelButton;
    private NamedButton saveButton;
    private final Constants constants;
    private final I18NAccount messages;
    private String template;

    public static EditMassletterView getInstance(Constants constants, I18NAccount messages, Elements elements) {

        if (me != null) {
            return me;
        }

        me = new EditMassletterView(constants, messages, elements);

        return me;
    }

    public EditMassletterView(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
        this.messages = messages;
        VerticalPanel vp = new VerticalPanel();

        setText(elements.edit_massleter());
        
        source = new NamedTextArea("massletter_source");
        source.setCharacterWidth(80);
        source.setVisibleLines(40);
        vp.add(source);

        HorizontalPanel hp = new HorizontalPanel();
        
        cancelButton = new NamedButton("cancel", elements.cancel());
        cancelButton.addClickHandler(this);
        saveButton = new NamedButton("save", elements.save());
        saveButton.addClickHandler(this);
        hp.add(saveButton);
        hp.add(cancelButton);
        vp.add(hp);
        
        setWidget(vp);
    }

    public void init(String data, String template) {
        this.template = template;
        source.setText(data);
        center();
    }
    
    public void onClick(ClickEvent event) {
        if(event.getSource() == cancelButton) {
            hide();
        }
        if(event.getSource() == saveButton) {
            save();
        }
    }

    private void save() {
        ServerResponse callback = new ServerResponse() {
            
            public void serverResponse(JSONValue responseObj) {
                if(!"1".equals(Util.str(responseObj))) {
                    Window.alert(messages.save_failed_badly());
                }
                hide();
            }
        };
        StringBuffer parameters = new StringBuffer(); 
        parameters.append("action=save");
        Util.addPostParam(parameters, "data", source.getText());
        Util.addPostParam(parameters, "template", template);
        
        AuthResponder.post(constants, messages, callback  , parameters, "reports/massletter.php");
        
    }
}
