package no.knubo.accounting.client.views.events;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EditTextPopup extends DialogBox implements ClickHandler {

    private final SimplePanel targetPanel;
    private NamedButton okButton;
    private NamedButton deleteButton;
    private NamedButton cancelButton;
    private final I18NAccount messages;
    private TextArea text;

    private EditTextPopup(Elements elements, I18NAccount messages, SimplePanel targetPanel) {
        this.messages = messages;
        this.targetPanel = targetPanel;

        setText("Rediger tekst");

        VerticalPanel vp = new VerticalPanel();

        text = new TextArea();

        vp.add(text);

        setModal(true);
        setAutoHideEnabled(false);

        HorizontalPanel buttonRow = new HorizontalPanel();
        vp.add(buttonRow);

        setWidget(vp);

        okButton = new NamedButton("ok", elements.ok());
        okButton.addClickHandler(this);
        buttonRow.add(okButton);

        deleteButton = new NamedButton("delete", elements.delete());
        deleteButton.addClickHandler(this);
        buttonRow.add(deleteButton);

        cancelButton = new NamedButton("cancel", elements.cancel());
        cancelButton.addClickHandler(this);
        buttonRow.add(cancelButton);

        if (targetPanel.getWidget() == null) {
            deleteButton.setEnabled(false);
        } else {
            HTML html = (HTML) targetPanel.getWidget();
            text.setText(html.getHTML());
        }
     
    }

    public static void showPopupForEditingText(Elements elements, I18NAccount messages, SimplePanel sp) {
        EditTextPopup popup = new EditTextPopup(elements, messages, sp);
        popup.center();
        popup.text.setFocus(true);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == cancelButton) {
            hide();
        }

        if (event.getSource() == deleteButton && Window.confirm(messages.confirm_delete_text())) {
            targetPanel.clear();
            hide();
        }

        if (event.getSource() == okButton) {
            if (targetPanel.getWidget() == null) {
                targetPanel.add(new HTML(text.getText()));
            } else {
                ((HTML) targetPanel.getWidget()).setHTML(text.getText());
            }
            hide();
        }
    }

}
