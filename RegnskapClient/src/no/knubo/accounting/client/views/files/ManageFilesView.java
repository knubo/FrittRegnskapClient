package no.knubo.accounting.client.views.files;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

public class ManageFilesView extends Composite implements ClickHandler {

    private static ManageFilesView instance;
    private final I18NAccount messages;
    private final Constants constants;
    private Label statusLabel;
    private FlexTable table;
    private IdHolder<String, Image> idHolder;

    public static ManageFilesView getInstance(Constants constants, I18NAccount messages,
            Elements elements) {
        if (instance == null) {
            instance = new ManageFilesView(messages, constants, elements);
        }
        return instance;
    }

    public void init() {
        idHolder.init();

        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }

        ServerResponse callback = new ServerResponse() {
            public void serverResponse(JSONValue value) {
                JSONArray files = value.isArray();

                for (int i = 0; i < files.size(); i++) {
                    String fileName = Util.str(files.get(i));
                    table.setText(i + 1, 0, fileName);
                    Image deleteImage = ImageFactory.deleteImage("delete_file");

                    deleteImage.addClickHandler(instance);

                    idHolder.add(fileName, deleteImage);
                    table.setWidget(i + 1, 1, deleteImage);
                }
            }

        };

        AuthResponder.get(constants, messages, callback, "files/files.php?action=list");

    }

    private ManageFilesView(final I18NAccount messages, Constants constants, Elements elements) {

        this.messages = messages;
        this.constants = constants;

        idHolder = new IdHolder<String, Image>();

        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setText(0, 0, elements.files());
        table.getFlexCellFormatter().setColSpan(0, 0, 2);
        table.getRowFormatter().setStyleName(0, "header");

        dp.add(table, DockPanel.NORTH);

        final FormPanel form = new FormPanel();
        form.setAction(constants.baseurl() + "files/files.php");

        // Because we're going to add a FileUpload widget, we'll need to set the
        // form to use the POST method, and multi-part MIME encoding.
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        VerticalPanel panel = new VerticalPanel();
        form.setWidget(panel);

        final Label tb = new Label("Filnavn");
        panel.add(tb);

        Hidden hidden = new Hidden();
        hidden.setName("action");
        hidden.setValue("upload");
        panel.add(hidden);

        FileUpload upload = new FileUpload();
        upload.setName("uploadFormElement");
        panel.add(upload);

        panel.add(new Button("Last opp fil", new ClickHandler() {
			
			public void onClick(ClickEvent event) {
                form.submit();
            }
        }));

        statusLabel = new Label();

        panel.add(statusLabel);

        form.addSubmitHandler(new SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
                if (tb.getText().length() == 0) {
                    Window.alert("The text box must not be empty");
                    event.cancel();
                }
            }
        });
        
        form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
        	
			public void onSubmitComplete(SubmitCompleteEvent event) {
                String result = event.getResults();

                if (result == null) {
                    Window.alert(messages.save_failed_badly());
                    return;
                }
                JSONValue jsonVal = JSONParser.parse(result);

                if (jsonVal == null) {
                    Window.alert(messages.save_failed_badly());
                    return;
                }

                JSONObject jsonObj = jsonVal.isObject();

                if (jsonObj == null) {
                    Window.alert(messages.save_failed_badly());
                    return;
                }

                if ("1".equals(Util.str(jsonObj.get("status")))) {
                    statusLabel.setText(messages.save_ok());
                    Util.timedMessage(statusLabel, "", 15);
                    init();
                } else {
                    statusLabel.setText(messages.save_failed());
                    Util.timedMessage(statusLabel, "", 15);
                }
            }
            
        });

        dp.add(form, DockPanel.NORTH);

        initWidget(dp);
    }

    public void onClick(ClickEvent event) {
    	Widget sender = (Widget) event.getSource();
        String fileName = idHolder.findId(sender);

        boolean result = Window.confirm(messages.delete_file_question(fileName));

        if (!result) {
            return;
        }

        ServerResponse callback = new ServerResponse() {
            public void serverResponse(JSONValue value) {
                JSONObject obj = value.isObject();

                if ("1".equals(Util.str(obj.get("result")))) {
                    init();
                } else {
                    Window.alert(messages.save_failed_badly());
                }
            }
        };

        AuthResponder.get(constants, messages, callback, "files/files.php?action=delete&file="
                + fileName);

    }

}
