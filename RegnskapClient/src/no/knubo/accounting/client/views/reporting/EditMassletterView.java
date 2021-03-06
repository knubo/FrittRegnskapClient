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
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EditMassletterView extends DialogBox implements ClickHandler, LoadHandler {

    private static EditMassletterView me;
    private NamedTextArea source;
    private NamedButton cancelButton;
    private NamedButton saveButton;
    private final Constants constants;
    private final I18NAccount messages;
    private String template;
    private NamedButton previewButton;
    private Image previewImage;
    private int countTriggersReload;

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

        HorizontalPanel editPanel = new HorizontalPanel();
        source = new NamedTextArea("massletter_source");
        source.setCharacterWidth(80);
        source.setVisibleLines(40);
        editPanel.add(source);

        previewImage = new Image();
        editPanel.add(previewImage);
        vp.add(editPanel);

        HorizontalPanel hp = new HorizontalPanel();

        cancelButton = new NamedButton("cancel", elements.cancel());
        cancelButton.addClickHandler(this);
        saveButton = new NamedButton("save", elements.save());
        saveButton.addClickHandler(this);
        previewButton = new NamedButton("preview", elements.preview());
        previewButton.addClickHandler(this);

        hp.add(saveButton);
        hp.add(cancelButton);
        hp.add(previewButton);
        vp.add(hp);

        setWidget(vp);
    }

    public void init(String data, String template) {
        this.template = template;
        source.setText(data);
        setPopupPosition(10,10);
        show();
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == cancelButton) {
            hide();
        }
        if (event.getSource() == saveButton) {
            save(true);
        }
        if (event.getSource() == previewButton) {
            save(false);
        }
    }

    private void preview() {
        countTriggersReload++;
        previewImage.setUrl(constants.baseurl() + "reports/massletter.php?action=preview&template=" + template
                + "&reload=" + countTriggersReload);
        
        previewImage.addLoadHandler(this);
    }

    private void save(final boolean doHide) {
        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                if (!"1".equals(Util.str(responseObj))) {
                    Window.alert(messages.save_failed_badly());
                }
                if (doHide) {
                    hide();
                } else {
                    preview();
                }
            }
        };
        StringBuffer parameters = new StringBuffer();
        parameters.append("action=save");
        Util.addPostParam(parameters, "data", source.getText());
        Util.addPostParam(parameters, "template", template);

        AuthResponder.post(constants, messages, callback, parameters, "reports/massletter.php");

    }

    @Override
    public void onLoad(LoadEvent event) {
        source.setHeight(previewImage.getHeight()+"px");
    }
}
