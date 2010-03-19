package no.knubo.accounting.client.views.modules;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;

public class AccountSelector extends DialogBox implements ClickHandler {

    private static AccountSelector me;
    private ListBox availablePostTypes;
    private ListBox chosenPostTypes;
    private NamedButton useAccountsButton;
    private NamedButton cancelButton;
    private Image previousImageBig;
    private Image nextImageBig;
    private final Constants constants;
    private final I18NAccount messages;
    private AccountSelected callback;

    private AccountSelector(Elements elements, Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        FlexTable ft = new FlexTable();

        ft.setText(0, 0, elements.available_accounts());
        ft.setText(0, 1, elements.chosen_accounts());

        availablePostTypes = new ListBox(true);
        availablePostTypes.setVisibleItemCount(15);
        ft.setWidget(1, 0, availablePostTypes);

        chosenPostTypes = new ListBox(true);
        chosenPostTypes.setVisibleItemCount(15);
        ft.setWidget(1, 1, chosenPostTypes);

        previousImageBig = ImageFactory.previousImageBig("remove");
        previousImageBig.addClickHandler(this);
        ft.setWidget(2, 0, previousImageBig);
        ft.getCellFormatter().setStyleName(2, 0, "right");
        nextImageBig = ImageFactory.nextImageBig("remove");
        nextImageBig.addClickHandler(this);
        ft.setWidget(2, 1, nextImageBig);

        useAccountsButton = new NamedButton("use_accounts", elements.use_selected());
        useAccountsButton.addClickHandler(this);
        cancelButton = new NamedButton("cancel", elements.cancel());
        cancelButton.addClickHandler(this);
        
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(useAccountsButton);
        hp.add(cancelButton);
        ft.setWidget(3, 0, hp);
        ft.getFlexCellFormatter().setColSpan(3, 0, 2);
        
        setWidget(ft);
    }

    public static AccountSelector getInstance(Elements elements, Constants constants, I18NAccount messages) {
        if (me == null) {
            me = new AccountSelector(elements, constants, messages);
        }
        return me;

    }

    public void init(String accountToSelects, String accounts, AccountSelected callback) {
        this.callback = callback;
        PosttypeCache posttypeCache = PosttypeCache.getInstance(constants, messages);
        setText(accountToSelects);
        availablePostTypes.clear();
        chosenPostTypes.clear();

        if (!accounts.isEmpty()) {
            String[] accountsInUse = accounts.split(",");

            for (String account : accountsInUse) {
                posttypeCache.addPost(chosenPostTypes, account, true);
            }
        }

        posttypeCache.fillAllPosts(availablePostTypes, chosenPostTypes, false, true);

    }

    public void onClick(ClickEvent event) {
        try {
            if (event.getSource() == cancelButton) {
                hide();
            }
            if (event.getSource() == nextImageBig) {
                moveSelection(availablePostTypes, chosenPostTypes);
            }
            if (event.getSource() == previousImageBig) {
                moveSelection(chosenPostTypes, availablePostTypes);
            }
            if (event.getSource() == useAccountsButton) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < chosenPostTypes.getItemCount(); i++) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(chosenPostTypes.getValue(i));
                }
                callback.selectedAccounts(sb.toString(), getText());
                hide();
            }
        } catch (Exception e) {
            Util.log(e.toString());
        }
    }

    private void moveSelection(ListBox src, ListBox dest) {
        for (int i = src.getItemCount() - 1; i >= 0; i--) {
            if (src.isItemSelected(i)) {
                String text = src.getItemText(i);
                String value = src.getValue(i);

                dest.addItem(text, value);
                src.removeItem(i);
            }
        }
    }
}
