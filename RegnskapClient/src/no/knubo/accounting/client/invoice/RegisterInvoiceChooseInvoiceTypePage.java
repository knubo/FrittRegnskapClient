package no.knubo.accounting.client.invoice;

import java.math.BigDecimal;
import java.util.Date;

import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import net.binarymuse.gwt.client.ui.wizard.event.NavigationEvent;
import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.WidgetIds;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.ViewCallback;

import org.gwt.advanced.client.datamodel.EditableGridDataModel;
import org.gwt.advanced.client.ui.widget.EditableGrid;
import org.gwt.advanced.client.ui.widget.GridPanel;
import org.gwt.advanced.client.ui.widget.cell.DateCell;
import org.gwt.advanced.client.ui.widget.cell.TextBoxCell;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegisterInvoiceChooseInvoiceTypePage extends WizardPage<InvoiceContext> implements ClickHandler,
        FocusHandler, KeyDownHandler {

    public static final PageID PAGEID = new PageID();

    private final Elements elements;

    private AccountTable table;

    private ListBoxWithErrorText invoiceTemplates;

    private FlowPanel vp;

    private final I18NAccount messages;

    private final Constants constants;

    private NamedButton useButton;

    private TextBoxWithErrorText amount;

    private TextBoxWithErrorText invoiceDueDay;

    private final ViewCallback callback;

    private int invoiceRow;

    private ListBoxWithErrorText splitType;

    private ListBoxWithErrorText monthSelect;

    public RegisterInvoiceChooseInvoiceTypePage(Elements elements, I18NAccount messages, Constants constants,
            ViewCallback callback) {
        this.elements = elements;
        this.messages = messages;
        this.constants = constants;
        this.callback = callback;
        vp = new FlowPanel();

        table = new AccountTable("");
        vp.add(table);

        int row = 0;
        table.setText(row, 0, elements.invoice_template());

        invoiceTemplates = new ListBoxWithErrorText("invoice_templates");
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(invoiceTemplates);

        useButton = new NamedButton("use", elements.use());
        useButton.addClickHandler(this);
        hp.add(useButton);

        table.setWidget(row++, 1, hp);
        table.setHeight("auto");

        invoiceRow = row;
        table.setText(row++, 0, elements.invoice_type());

        amount = new TextBoxWithErrorText("amount");
        amount.getTextBox().addFocusHandler(this);
        amount.getTextBox().addKeyDownHandler(this);
        table.setText(row, 0, elements.amount());
        table.setWidget(row++, 1, amount);

        splitType = new ListBoxWithErrorText("invoice_split_type");
        InvoiceSplitType.addSplitTypeItems(splitType);
        table.setText(row, 0, elements.invoice_split_type());

        table.setWidget(row++, 1, splitType);

        table.setText(row, 0, elements.first_month());

        monthSelect = monthSelect();

        table.setWidget(row++, 1, monthSelect);

        invoiceDueDay = new TextBoxWithErrorText("invoice_due_day");
        invoiceDueDay.setVisibleLength(2);
        invoiceDueDay.setMaxLength(2);
        table.setText(row, 0, elements.invoice_due_day());
        table.setWidget(row++, 1, invoiceDueDay);

        table.setText(row, 0, elements.invoices(), "header");
        row++;

        EditableGridDataModel model = new EditableGridDataModel(new Object[][] {}) {
            @Override
            public void addRow(int beforeRow, Object[] row) throws IllegalArgumentException {
                super.addRow(beforeRow, new Object[] { new Date(), "0.00" });
                gridPanel.unlock();
            }
        };
        model.setPageSize(15);

        gridPanel = new GridPanel();
        edibleGrid = gridPanel.createEditableGrid(new String[] { elements.due_date(), elements.amount() }, new Class[] {
                DateCell.class, TextBoxCell.class }, model);

        edibleGrid.getElement().setId("invoiceGrid");

        gridPanel.setPageNumberBoxDisplayed(true);
        gridPanel.setTotalCountDisplayed(true);
        gridPanel.getTopToolbar().setSaveButtonVisible(false);
        gridPanel.display();
        gridPanel.getGrid().setMultiRowModeEnabled(true);

        VerticalPanel invoiceButtons = new VerticalPanel();
        splitEqual = new NamedButton("", elements.invoice_amount_split_equal());
        splitEqual.addClickHandler(this);
        splitRepeat = new NamedButton("", elements.invoice_amount_repeat());
        splitRepeat.addClickHandler(this);
        invoiceButtons.add(splitRepeat);
        invoiceButtons.add(splitEqual);
        vp.add(invoiceButtons);

        vp.add(gridPanel);

    }

    private ListBoxWithErrorText monthSelect() {
        ListBoxWithErrorText box = new ListBoxWithErrorText("first_month");

        for (int i = 1; i <= 12; i++) {
            if(i<10) {
                box.addItem(elements.getString("month_0" + i), String.valueOf(i));
            } else {                
                box.addItem(elements.getString("month_" + i), String.valueOf(i));
            }
        }

        return box;
    }

    private GridPanel gridPanel;

    protected JSONArray invoices;

    private EditableGrid<?> edibleGrid;

    protected JSONObject prices;

    protected InvoiceType invoiceType;

    protected int currentMonth;

    @Override
    public Widget asWidget() {
        return vp;
    }

    @Override
    public String getTitle() {
        return "Fakturakj�ring";
    }

    @Override
    public void afterShow() {
        super.afterShow();

        gridPanel.setWidth("100%");
        gridPanel.resize();

        // NativeEvent event = Document.get().createFocusEvent();
        // DomEvent.fireNativeEvent(event, foo);
    }

    @Override
    public void beforeShow() {

        getWizard().setButtonVisible(ButtonType.BUTTON_NEXT, true);
        getWizard().setButtonVisible(ButtonType.BUTTON_CANCEL, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_FINISH, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_PREVIOUS, true);

        loadInvoices();
    }

    private void loadInvoices() {
        ServerResponse response = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONObject data = responseObj.isObject();
                invoices = data.get("invoices").isArray();
                currentMonth = Util.getInt(data.get("month"));
                Util.setIndexByValue(monthSelect.getListbox(), String.valueOf(currentMonth));
                JSONValue pricesObj = data.get("prices");

                if (pricesObj == null || pricesObj.isObject() == null) {
                    Window.alert(messages.dashboard_missing_semester_price_current());
                    callback.openView(WidgetIds.EDIT_PRICES, messages.dashboard_missing_semester_price_current());
                    return;
                }
                prices = pricesObj.isObject();
                fillInvoicesChoices();
            }
        };
        AuthResponder.get(constants, messages, response, "accounting/invoice_ops.php?action=all");
    }

    protected void fillInvoicesChoices() {
        invoiceTemplates.clear();

        for (int i = 0; i < invoices.size(); i++) {
            JSONObject invoice = invoices.get(i).isObject();

            invoiceTemplates.addItem(Util.str(invoice.get("description")), Util.str(invoice.get("id")));
        }
    }

    @Override
    public PageID getPageID() {
        return PAGEID;
    }

    @Override
    public void beforeNext(NavigationEvent event) {

        super.beforeNext(event);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == useButton) {
            selectInvoice();
        }
        if (event.getSource() == splitEqual) {
            splitEqualParts();
        }
    }

    private void splitEqualParts() {
        if (!validateSum()) {
            return;
        }

        BigDecimal bigDecimal = new BigDecimal(amount.getText().replaceAll(",", ""));

    }

    private boolean validateSum() {
        MasterValidator mv = new MasterValidator();

        mv.money(messages.field_money(), amount);

        return mv.validateStatus();
    }

    private void selectInvoice() {
        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONObject obj = responseObj.isObject();

                invoiceType = InvoiceType.invoiceType(Util.getInt(obj.get("invoice_type")));
                table.setText(invoiceRow, 1, invoiceType.getDesc());
                Util.setIndexByValue(splitType.getListbox(), Util.str(obj.get("split_type")));
                amount.setText(Util.money(Util.strSkipNull(obj.get("default_amount"))));
            }
        };

        AuthResponder.get(constants, messages, callback,
                "accounting/invoice_ops.php?action=get&id=" + Util.getSelected(invoiceTemplates));
    }

    private NamedButton priceButton(String name, String type) {
        NamedButton button = new NamedButton(name, Util.str(prices.get(type)));
        button.addClickHandler(priceHandler);
        return button;
    }

    PriceHandler priceHandler = new PriceHandler();

    private NamedButton splitEqual;

    private NamedButton splitRepeat;

    class PriceHandler implements ClickHandler {

        private DialogBox db;

        @Override
        public void onClick(ClickEvent event) {
            NamedButton namedButton = (NamedButton) event.getSource();

            amount.setText(Util.money(namedButton.getText()));
            db.hide();
        }

        public void setPopup(DialogBox db) {
            this.db = db;

        }

        public void closePopup() {
            db.hide();
        }

    }

    @Override
    public void onFocus(FocusEvent event) {
        if (invoiceType == InvoiceType.SEMESTER) {
            DialogBox db = new DialogBox();
            db.setText(elements.prices_membership());
            AccountTable priceTable = new AccountTable("edittable");
            priceTable.setText(0, 0, elements.train_membership());
            priceTable.setText(1, 0, elements.course_membership());
            priceTable.setText(2, 0, elements.youth_membership());

            priceTable.setWidget(0, 1, priceButton("train_membership", "train"));
            priceTable.setWidget(1, 1, priceButton("course_membership", "course"));
            priceTable.setWidget(2, 1, priceButton("youth_membership", "youth"));

            priceHandler.setPopup(db);
            db.setWidget(priceTable);
            db.setAutoHideEnabled(true);
            int left = event.getRelativeElement().getAbsoluteLeft();
            int top = event.getRelativeElement().getAbsoluteBottom();

            Util.log("ScreenX:" + left);
            Util.log("ScreenY:" + top);

            db.setModal(false);
            db.setPopupPosition(left, top);
            db.show();
            amount.setFocus(true);
        }
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            priceHandler.closePopup();
        }
        if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
            priceHandler.closePopup();
        }
    }
}