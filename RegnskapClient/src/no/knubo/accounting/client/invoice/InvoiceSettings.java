package no.knubo.accounting.client.invoice;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.ViewCallback;
import no.knubo.accounting.client.views.files.UploadDelegate;
import no.knubo.accounting.client.views.files.UploadDelegateCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class InvoiceSettings extends Composite implements ClickHandler {
    private static InvoiceSettings me;
    private I18NAccount messages;
    private Constants constants;
    private AccountTable table;
    private NamedButton newButton;
    private final Elements elements;
    private InvoiceSettings.InvoiceEditFields editFields;
    private final ViewCallback callback;

    public InvoiceSettings(I18NAccount messages, Constants constants, Elements elements, ViewCallback callback) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;
        this.callback = callback;

        DockPanel dp = new DockPanel();

        table = new AccountTable("tableborder");
        table.setText(0, 0, elements.invoice_templates());
        table.getFlexCellFormatter().setColSpan(0, 0, 10);
        table.getRowFormatter().setStyleName(0, "header");
        table.setText(1, 0, elements.description());
        table.setText(1, 2, elements.invoice_split_type(), "desc");
        table.setText(1, 3, elements.invoice_due_day(), "desc");
        table.setText(1, 4, elements.invoice_default_amount(), "desc");
        table.setText(1, 5, elements.invoice_email_ready(), "desc");
        table.setText(1, 6, elements.invoice_email_sender(), "desc");
        table.setText(1, 7, elements.invoice_odt_template());
        table.setText(1, 9, "");

        table.getRowFormatter().setStyleName(1, "header");

        newButton = new NamedButton("invoice_template_new", elements.invoice_template_new());
        newButton.addClickHandler(this);

        dp.add(newButton, DockPanel.NORTH);
        dp.add(table, DockPanel.NORTH);
        initWidget(dp);
    }

    public static InvoiceSettings getInstance(I18NAccount messages, Constants constants, Elements elements,
            ViewCallback callback) {
        if (me == null) {
            me = new InvoiceSettings(messages, constants, elements, callback);
        }
        me.setVisible(true);
        return me;
    }

    private JSONArray invoices;
    private AccountTable filestable;
    private DialogBox chooseTemplatePopup;

    public void init(final Object[] params) {

        if (params != null && params.length > 0 && params[0] instanceof JSONObject) {
            saveEmailTemplate((JSONObject) params[0]);
            return;
        }

        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONObject data = responseObj.isObject();
                invoices = data.get("invoices").isArray();
                showInvoices(params);
            }
        };

        AuthResponder.get(constants, messages, callback, "accounting/invoice_ops.php?action=all");

    }

    private void saveEmailTemplate(final JSONObject emailTemplateSettings) {

        StringBuffer params = new StringBuffer();
        params.append("action=saveEmailTemplate");
        Util.addPostParam(params, "emailTemplate", emailTemplateSettings.toString());
        ServerResponse cb = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                init(new String[] { Util.str(emailTemplateSettings.get("id")) });
            }
        };
        AuthResponder.post(constants, messages, cb, params, "accounting/invoice_ops.php");

    }

    protected void showInvoices(Object[] params) {

        int row = 2;

        PosttypeCache postTypeCache = PosttypeCache.getInstance(constants, messages);

        for (int i = 0; i < invoices.size(); i++) {
            JSONObject invoice = invoices.get(i).isObject();

            table.setText(row, 0, Util.strSkipNull(invoice.get("description")));
            table.setText(row, 2, InvoiceSplitType.invoiceSplitType(Util.getInt(invoice.get("split_type"))).getDesc(),
                    "desc");
            table.setText(row, 3, Util.strSkipNull(invoice.get("invoice_due_day")), "desc");
            table.setText(row, 4, Util.money(Util.strSkipNull(invoice.get("default_amount"))), "desc");
            table.setText(row, 5, Util.getBoolean(invoice.get("emailOK")) ? elements.ready() : elements.not_ready());
            table.setText(row, 6, Util.strSkipNull(invoice.get("email_from")), "desc");

            String creditPostType = Util.strSkipNull(invoice.get("credit_post_type"));

            table.setText(row, 7, Util.strSkipNull(invoice.get("invoice_template")));
            
            if (creditPostType.length() > 0) {
                table.setText(row, 8, postTypeCache.getDescriptionWithType(creditPostType), "desc");
            }
            

            Image editImage = ImageFactory.editImage("invoiceTypeEdit_" + Util.str(invoice.get("id")));
            editImage.addClickHandler(this);
            table.setWidget(row, 9, editImage);

            String style = (((row + 1) % 6) < 3) ? "line2" : "line1";
            table.getRowFormatter().setStyleName(row, style);
            row++;
        }

        if (params != null && params.length > 0) {
            showInitFields(null, "invoiceTypeEdit_" + params[0], false);
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        Widget sender = (Widget) event.getSource();
        String id = sender.getElement().getId();

        boolean openNew = sender == newButton;

        showInitFields(sender, id, openNew);
    }

    private void showInitFields(Widget sender, String id, boolean openNew) {
        if (editFields == null) {
            editFields = new InvoiceEditFields();
        }

        int left = 50;
        int top = 50;

        if (sender != null) {
            top = sender.getAbsoluteTop() + 10;
            if (openNew) {
                left = sender.getAbsoluteLeft() + 10;
            } else {
                left = sender.getAbsoluteLeft() - 150;
            }
        }
        editFields.setPopupPosition(left, top);

        if (openNew) {
            editFields.init();
        } else {

            editFields.init(id);
        }
        editFields.show();
    }

    class InvoiceEditFields extends DialogBox implements ClickHandler {
        private TextBoxWithErrorText description;
        private TextBoxWithErrorText emailSender;

        private Button saveButton;
        private Button cancelButton;
        private HTML mainErrorLabel;
        private FlexTable edittable;
        private ListBoxWithErrorText splitType;
        private TextBoxWithErrorText invoiceDueDay;
        private NamedButton editInvoiceTemplate;
        private NamedButton chooseODTTemplate;
        private String currentId;
        private Label invoiceTemplate = new Label();

        InvoiceEditFields() {
            setText(elements.invoice_template());
            edittable = new FlexTable();
            edittable.setStyleName("edittable");

            edittable.setText(0, 0, elements.description());
            edittable.setText(2, 0, elements.invoice_split_type());
            edittable.setText(3, 0, elements.invoice_due_day());
            edittable.setText(5, 0, elements.invoice_email_ready());
            edittable.setText(6, 0, elements.invoice_odt_template());
            edittable.setText(7, 0, elements.invoice_email_sender());

            emailSender = new TextBoxWithErrorText("invoice_email_sender");
            emailSender.setMaxLength(255);
            emailSender.setVisibleLength(100);

            description = new TextBoxWithErrorText("description");
            description.setMaxLength(255);
            description.setVisibleLength(100);

            splitType = new ListBoxWithErrorText("invoice_split_type");
            InvoiceSplitType.addSplitTypeItems(splitType);


            invoiceDueDay = new TextBoxWithErrorText("invoice_due_date");

            edittable.setWidget(0, 1, description);
            edittable.setWidget(2, 1, splitType);
            edittable.setWidget(3, 1, invoiceDueDay);
            editInvoiceTemplate = new NamedButton("invoice_edit_email_template", elements.invoice_edit_email_template());
            editInvoiceTemplate.addClickHandler(this);
            chooseODTTemplate = new NamedButton("invoice_choose_odt_template", elements.invoice_choose_odt_template());
            chooseODTTemplate.addClickHandler(this);
            
            edittable.setWidget(6, 1, invoiceTemplate);
            edittable.setWidget(7, 1, emailSender);

            DockPanel dp = new DockPanel();
            dp.add(edittable, DockPanel.NORTH);

            saveButton = new NamedButton("invoiceEditView_saveButton", elements.save());
            saveButton.addClickHandler(this);
            cancelButton = new NamedButton("invoiceEditView_cancelButton", elements.cancel());
            cancelButton.addClickHandler(this);

            mainErrorLabel = new HTML();
            mainErrorLabel.setStyleName("error");

            HorizontalPanel buttonPanel = new HorizontalPanel();
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            buttonPanel.add(editInvoiceTemplate);
            buttonPanel.add(chooseODTTemplate);
            buttonPanel.add(mainErrorLabel);
            dp.add(buttonPanel, DockPanel.NORTH);
            setWidget(dp);
        }

        @Override
        public void onClick(ClickEvent event) {
            Widget sender = (Widget) event.getSource();
            if (sender == cancelButton) {
                hide();
            } else if (sender == saveButton && validateFields()) {
                doSave();
            } else if (sender == editInvoiceTemplate) {
                doEditInvoice();
            } else if (sender == chooseODTTemplate) {
                doChooseInvoiceTemplate();
            } else if(sender instanceof Anchor) {
                Anchor anchor = (Anchor) sender;
                invoiceTemplate.setText(anchor.getText());
                chooseTemplatePopup.hide();
            }
        }

        private void doEditInvoice() {
            callback.editEmailTemplateInvoice(currentId);
            hide();
        }

        private void doSave() {
            StringBuffer sb = new StringBuffer();
            sb.append("action=save");

            Util.addPostParam(sb, "id", currentId);
            Util.addPostParam(sb, "description", description.getText());
            Util.addPostParam(sb, "split_type", splitType.getText());
            Util.addPostParam(sb, "invoice_due_day", invoiceDueDay.getText());
            Util.addPostParam(sb, "email_from", emailSender.getText());
            Util.addPostParam(sb, "invoice_template", invoiceTemplate.getText());

            ServerResponse callback = new ServerResponse() {

                @Override
                public void serverResponse(JSONValue value) {
                    JSONObject object = value.isObject();

                    if ("1".equals(Util.str(object.get("result")))) {
                        me.init(null);
                        hide();
                    } else {
                        mainErrorLabel.setText(messages.save_failed());
                        Util.timedMessage(mainErrorLabel, "", 10);
                    }
                }
            };

            AuthResponder.post(constants, messages, callback, sb, "accounting/invoice_ops.php");
        }

        public void init() {

            editInvoiceTemplate.setEnabled(false);
            currentId = "";
            description.setText("");
            emailSender.setText("");

            mainErrorLabel.setText("");
            splitType.setSelectedIndex(0);
            invoiceDueDay.setText("");
        }

        private void init(String itemId) {

            currentId = itemId.substring("invoiceTypeEdit_".length());

            editInvoiceTemplate.setEnabled(true);
            JSONObject invoice = findInvoice(currentId);

            description.setText(Util.strSkipNull(invoice.get("description")));
            Util.setIndexByValue(splitType.getListbox(), Util.str(invoice.get("split_type")));
            invoiceDueDay.setText(Util.strSkipNull(invoice.get("invoice_due_day")));
            edittable.setText(5, 1, Util.getBoolean(invoice.get("emailOK")) ? elements.ready() : elements.not_ready());
            emailSender.setText(Util.strSkipNull(invoice.get("email_from")));

            invoiceTemplate.setText(Util.strSkipNull(invoice.get("invoice_template")));
            
            
            
            mainErrorLabel.setText("");
        }

        private boolean validateFields() {
            MasterValidator mv = new MasterValidator();
            mv.mandatory(messages.required_field(), emailSender, description);


            mv.email(messages.invalid_email(), emailSender);

            return mv.validateStatus();
        }
    }

    public JSONObject findInvoice(String id) {
        for (int i = 0; i < invoices.size(); i++) {
            JSONObject obj = invoices.get(i).isObject();
            if (Util.str(obj.get("id")).equals(id)) {
                return obj;
            }
        }

        throw new RuntimeException("No invoice found " + id);
    }

    public void doChooseInvoiceTemplate() {
        chooseTemplatePopup = new DialogBox();
        
        UploadDelegateCallback uploadHandler = new UploadDelegateCallback() {
            
            @Override
            public void uploadComplete() {
                fillFilesTable();
            }
            
            @Override
            public boolean uploadBody(String body) {
                return false;
            }   
            
            @Override
            public void preUpload() {
                /* Empty */
            }
        };
        UploadDelegate uploadDelegate = new UploadDelegate("files/files.php", uploadHandler , constants, messages, elements);

        VerticalPanel panel = new VerticalPanel();

        panel.add(fillFilesTable());
        
        panel.add(uploadDelegate.getForm());
        chooseTemplatePopup.add(panel);

        NamedButton cancelButton = new NamedButton("cancel", elements.cancel());
        cancelButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                chooseTemplatePopup.hide();
            }
        });
        panel.add(cancelButton);

        chooseTemplatePopup.center();
    }

    private Widget fillFilesTable() {
        
        if(filestable == null) {
            filestable = new AccountTable("tableborder");
        }
        filestable.clear();
        
        ServerResponse callback = new ServerResponse() {
            @Override
            public void serverResponse(JSONValue value) {
                JSONObject data = value.isObject();

                JSONArray files = data.get("files").isArray();

                for (int i = 0; i < files.size(); i++) {
                    JSONObject fileinfo = files.get(i).isObject();
                    String fileName = Util.str(fileinfo.get("name"));

                    if (Util.getBoolean(fileinfo.get("link"))) {
                        filestable.setWidget(i + 1, 0, chooseFileWidget(fileName));
                    } else {
                        filestable.setText(i + 1, 0, fileName);
                    }

                    filestable.setText(i + 1, 1, Util.str(fileinfo.get("size")));
                    filestable.getCellFormatter().setStyleName(i + 1, 1, "desc right");

                }

                int row = filestable.getRowCount();
                String title = elements.total();
                if (Util.str(data.get("used")).length() > 1) {
                    title += " (" + Util.str(data.get("used")) + "% / " + Util.str(data.get("quota")) + ")";
                }
                filestable.setText(row, 0, title);
                filestable.setText(row, 1, Util.str(data.get("totalsize")));
                filestable.getCellFormatter().setStyleName(row, 1, "desc right");
            }

        };

        AuthResponder.get(constants, messages, callback, "files/files.php?action=list");

        return filestable;
    }

    protected Widget chooseFileWidget(String fileName) {
        Anchor anchor = new Anchor(fileName);
        anchor.addClickHandler(editFields);
        return anchor;
    }
}
