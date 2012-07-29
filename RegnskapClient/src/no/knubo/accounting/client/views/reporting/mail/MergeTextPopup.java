package no.knubo.accounting.client.views.reporting.mail;

import java.util.HashMap;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedTextArea;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;

public class MergeTextPopup extends DialogBox implements ClickHandler {

    private final NamedTextArea bodyBox;
    private HashMap<NamedButton, String> buttons;
    private AccountTable choiceTable;

    public MergeTextPopup(Elements elements, NamedTextArea bodyBox, boolean includeInvoices) {
        this.bodyBox = bodyBox;

        choiceTable = new AccountTable("tableborder");

        buttons = new HashMap<NamedButton, String>();

        addButton("email_merge_text_unsubscribe_link", elements.email_merge_text_unsubscribe_link(), "unsubscribeurl");

        if (includeInvoices) {
            addButton("email_merge_text_invoice_link", elements.email_merge_text_invoice_link(), "invoice");
            addButton("email_merge_text_invoice_amount", elements.email_merge_text_invoice_amount(), "amount");
            addButton("email_merge_text_invoice_due_date", elements.email_merge_text_invoice_due_date(), "due_date");
        }

        setAutoHideEnabled(true);
        setWidget(choiceTable);
    }

    private void addButton(String id, String txt, String mergeText) {
        NamedButton button = new NamedButton(id, txt);

        buttons.put(button, mergeText);

        button.addClickHandler(this);
        choiceTable.setWidget(choiceTable.getRowCount(), 0, button);

    }

    @Override
    public void onClick(ClickEvent event) {
        fixtext(buttons.get(event.getSource()));
    }

    private void fixtext(String subst) {
        hide();

        if (bodyBox.isVisible()) {

            int cursorPos = bodyBox.getCursorPos();

            String text = bodyBox.getText();

            String changeTo = text.substring(0, cursorPos) + "{" + subst + "}" + text.substring(cursorPos);

            bodyBox.setText(changeTo);
            bodyBox.setCursorPos(cursorPos + 2 + subst.length());
            bodyBox.setFocus(true);
        } else {
            ReportMail.insertTextInHTMLAtCursorPos("{" + subst + "}");
        }
    }

}
