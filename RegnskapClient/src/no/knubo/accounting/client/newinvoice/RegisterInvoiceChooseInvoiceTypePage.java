package no.knubo.accounting.client.newinvoice;

import java.math.BigDecimal;
import java.util.Date;

import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import net.binarymuse.gwt.client.ui.wizard.event.NavigationEvent;
import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.RegnskapLocalStorage;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.invoice.InvoiceSplitType;
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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegisterInvoiceChooseInvoiceTypePage extends WizardPage<InvoiceContext> implements ClickHandler,
        FocusHandler, KeyDownHandler, ChangeHandler {

    public static final String INVOICES_KEY = "invoices";
    public static final String INVOICE_TEMPLATE_KEY = "invoice_template";

    public static final PageID PAGEID = new PageID();

    private final Elements elements;

    private AccountTable table;

    private ListBoxWithErrorText invoiceTemplates;

    private FlowPanel vp;

    private final I18NAccount messages;

    private final Constants constants;

    private NamedButton useButton;

    private TextBoxWithErrorText amount;

    TextBoxWithErrorText invoiceDueDay;

    private final ViewCallback callback;

    private int invoiceRow;

    ListBoxWithErrorText splitType;

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

        invoiceDueDay = new TextBoxWithErrorText("invoice_due_day");
        invoiceDueDay.setVisibleLength(2);
        invoiceDueDay.setMaxLength(2);
        table.setText(row, 0, this.elements.invoice_due_day());
        table.setWidget(row++, 1, invoiceDueDay);

        row = addSplitTable(row);
        row = addSingleTable(row);

        table.setHeaders(row++, elements.invoices(), "");

        model = new InvoiceModel(new Object[][] {});
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

        vp.add(gridPanel);

    }

    private int addSingleTable(int rowIn) {
        int row = rowIn;

        table.setColSpan(row, 0, 3);
        AccountTable singleTable = new AccountTable("tableborder");
        table.setWidget(row++, 0, singleTable);

        monthsSingle = new ListBoxWithErrorText("month");
        monthsSingle.addItem("", "");
        for (int i = 1; i <= 12; i++) {
            monthsSingle.addItem(Util.monthString(elements, i), String.valueOf(i));
        }

        singleTable.setHeaders(0, elements.invoice_single());
        singleTable.setColSpan(0, 0, 2);
        singleTable.setWidget(1, 0, monthsSingle);
        
        addSingleButton = new NamedButton("add", elements.add());
        singleTable.setWidget(1, 1, addSingleButton);
        addSingleButton.addClickHandler(this);

        return row;
    }

    private int addSplitTable(int rowIn) {
        int row = rowIn;

        table.setColSpan(row, 0, 3);
        AccountTable splitTable = new AccountTable("tableborder");
        table.setWidget(row++, 0, splitTable);

        splitTable.setHeaders(0, this.elements.invoice_split_type());
        splitTable.setColSpan(0, 0, 3);

        splitType = new ListBoxWithErrorText("invoice_split_type");
        splitType.addStyleName("buttonrow");
        splitType.addChangeHandler(this);
        InvoiceSplitType.addSplitTypeItems(splitType);

        splitEqual = new NamedButton("", this.elements.invoice_amount_split_equal());
        splitEqual.addStyleName("buttonrow");
        splitEqual.addClickHandler(this);
        splitRepeat = new NamedButton("", this.elements.invoice_amount_repeat());
        splitRepeat.addStyleName("buttonrow");
        splitRepeat.addClickHandler(this);

        splitEqual.setEnabled(false);
        splitRepeat.setEnabled(false);

        splitTable.setWidget(1, 0, splitType);
        splitTable.setWidget(1, 1, splitRepeat);
        splitTable.setWidget(1, 2, splitEqual);
        return row;
    }

    protected void saveInvoicesLocalStorage() {
        Object[][] data = model.getData();

        JSONArray arr = new JSONArray();

        int pos = 0;
        for (Object[] objects : data) {
            Date date = (Date) objects[0];
            String money = (String) objects[1];

            JSONObject invoice = new JSONObject();
            invoice.put("date", new JSONNumber(date.getTime()));
            invoice.put("amount", new JSONString(money));
            arr.set(pos++, invoice);
        }

        RegnskapLocalStorage.saveInvoices(arr);
    }

    private void loadInvoicesFromLocalStorage(String data) {
        JSONArray invoices = JSONParser.parseStrict(data).isArray();

        model.removeAllNoLocalUpdate();

        for (int i = 0; i < invoices.size(); i++) {
            JSONObject invoice = invoices.get(i).isObject();

            JSONNumber date = invoice.get("date").isNumber();

            model.addNoLocalUpdate(new Date((long) date.doubleValue()), Util.str(invoice.get("amount")));
        }

    }

    private GridPanel gridPanel;

    protected JSONArray invoicesTemplates;

    private EditableGrid<?> edibleGrid;

    protected JSONObject prices;

    protected InvoiceType invoiceType;

    int currentMonth;

    protected int currentYear;

    @Override
    public Widget asWidget() {
        return vp;
    }

    @Override
    public String getTitle() {
        return elements.invoice_create();
    }

    @Override
    public void afterShow() {
        super.afterShow();

        gridPanel.setWidth("100%");
        gridPanel.resize();

        String invoices = RegnskapLocalStorage.getInvoices();

        if (invoices != null) {
            loadInvoicesFromLocalStorage(invoices);
        } else {
            model.removeAll();
        }
    }

    @Override
    public void beforeShow() {

        getWizard().setButtonVisible(ButtonType.BUTTON_NEXT, true);
        getWizard().setButtonVisible(ButtonType.BUTTON_CANCEL, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_FINISH, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_PREVIOUS, true);

        loadInvoiceTemplates();
    }

    private void loadInvoiceTemplates() {
        ServerResponse response = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONObject data = responseObj.isObject();
                invoicesTemplates = data.get(INVOICES_KEY).isArray();
                currentMonth = Util.getInt(data.get("month"));
                currentYear = Util.getInt(data.get("year"));
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

        invoiceTemplates.addItem("", "");

        for (int i = 0; i < invoicesTemplates.size(); i++) {
            JSONObject invoice = invoicesTemplates.get(i).isObject();

            invoiceTemplates.addItem(Util.str(invoice.get("description")), Util.str(invoice.get("id")));
        }

        selectInvoiceTemplate();
    }

    private void selectInvoiceTemplate() {
        Integer templateId = RegnskapLocalStorage.getInvoiceTemplate();

        if (templateId == null) {
            return;
        }

        Util.setIndexByValue(invoiceTemplates.getListbox(), String.valueOf(templateId));
        selectInvoice();
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
            splitParts(event, false);
        }

        if (event.getSource() == splitRepeat) {
            splitParts(event, true);
        }

        if (event.getSource() == addSingleButton) {
            addSingle();
        }
    }

    private void addSingle() {
        if (monthsSingle.getSelectedIndex() == 0 || !validateAddInvoice()) {
            return;
        }

        BigDecimal bigDecimalAmount = new BigDecimal(amount.getText().replaceAll(",", ""));

        int day = Integer.parseInt(invoiceDueDay.getText());

        String month = Util.getSelected(monthsSingle);

        Date date = Util.date(day, Integer.parseInt(month) - 1, currentYear - 1900);

        Object[] rowdata = { date, bigDecimalAmount.setScale(2).toPlainString() };

        addRow(0, rowdata);

    }

    private void splitParts(ClickEvent event, boolean repeat) {
        if (!validateAddInvoice()) {
            return;
        }

        BigDecimal bigDecimalAmount = new BigDecimal(amount.getText().replaceAll(",", ""));

        AddInvoicesPopup popup = new AddInvoicesPopup(this, elements, bigDecimalAmount, repeat);
        popup.setPopupPosition(event.getClientX(), event.getClientY());
        popup.show();

    }

    private boolean validateAddInvoice() {
        MasterValidator mv = new MasterValidator();

        mv.money(messages.field_money(), amount);
        mv.mandatory(messages.required_field(), invoiceDueDay, amount);

        return mv.validateStatus();
    }

    private void selectInvoice() {
        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONObject obj = responseObj.isObject();

                RegnskapLocalStorage.setInvoiceTemplate(Util.getInt(obj.get("id")));

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

    private InvoiceModel model;
    private NamedButton addSingleButton;
    private ListBoxWithErrorText monthsSingle;

    final class InvoiceModel extends EditableGridDataModel {
        private InvoiceModel(Object[][] data) {
            super(data);
        }

        @Override
        public void update(int row, int column, Object data) {
            super.update(row, column, data);
            saveInvoicesLocalStorage();
        }

        @Override
        public void update(Object[][] data) {
            super.update(data);
            saveInvoicesLocalStorage();
        }

        @Override
        public void addRow(int beforeRow, Object[] row) throws IllegalArgumentException {

            if (row[0] == null) {
                super.addRow(0, new Object[] { new Date(), "0.00" });
            } else {
                super.addRow(beforeRow, new Object[] { row[0], row[1] });
            }

            gridPanel.unlock();
            saveInvoicesLocalStorage();
        }

        @Override
        public void removeAll() {
            super.removeAll();
            saveInvoicesLocalStorage();
        }

        @Override
        public void removeRow(int rowNumber) throws IllegalArgumentException {
            super.removeRow(rowNumber);
            saveInvoicesLocalStorage();
        }

        public void removeAllNoLocalUpdate() {
            super.removeAll();
        }

        public void addNoLocalUpdate(Date date, String money) {
            super.addRow(model.getRows().length, new Object[] { date, money });
        }
    }

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

    void addRow(int beforeRow, Object[] row) {
        model.addRow(beforeRow, row);
    }

    @Override
    public void onChange(ChangeEvent event) {
        // if (event.getSource() == splitType) {

        boolean enableSplit = splitType.getSelectedIndex() > 0;

        splitEqual.setEnabled(enableSplit);
        splitRepeat.setEnabled(enableSplit);
        // }

    }
}