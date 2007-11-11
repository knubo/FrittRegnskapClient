package no.knubo.accounting.client.views.registers;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.CacheCallback;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.NamedButton;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.Map;

public class MembershipPriceEditView extends Composite implements
        ClickListener, CacheCallback {

    private static MembershipPriceEditView me;
    private final Constants constants;
    private final I18NAccount messages;
    private FlexTable table;
    private IdHolder lineHolder;
    private MembershipPriceEditFields editFields;
    private final Elements elements;

    public MembershipPriceEditView(I18NAccount messages, Constants constants,
            Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;

        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tableborder");

        table.setText(0, 0, elements.menuitem_membership_prices());
        table.getFlexCellFormatter().setColSpan(0, 0, 7);
        table.setText(2, 0, elements.year());
        table.setText(1, 1, "");
        table.setText(1, 2, elements.spring());
        table.setText(2, 1, elements.year_membership());
        table.setText(2, 2, elements.course_membership());
        table.setText(2, 3, elements.train_membership());
        table.setText(1, 4, elements.fall());
        table.setText(1, 5, "");
        table.setText(1, 6, "");
        table.setText(2, 4, elements.course_membership());
        table.setText(2, 5, elements.train_membership());
        table.setText(2, 6, "");
        table.getRowFormatter().setStyleName(0, "header");
        table.getRowFormatter().setStyleName(1, "header");
        table.getRowFormatter().setStyleName(2, "header");

        dp.add(table, DockPanel.NORTH);

        lineHolder = new IdHolder();
        initWidget(dp);
    }

    public static MembershipPriceEditView show(I18NAccount messages,
            Constants constants, Elements elements) {
        if (me == null) {
            me = new MembershipPriceEditView(messages, constants, elements);
        }
        me.setVisible(true);
        return me;
    }

    public void onClick(Widget sender) {
        if (editFields == null) {
            editFields = new MembershipPriceEditFields();
        }

        int left = sender.getAbsoluteLeft() - 250;

        int top = sender.getAbsoluteTop() + 10;
        editFields.setPopupPosition(left, top);

        int row = Integer.parseInt(lineHolder.findId(sender));

        table.getText(row, 1);

        editFields.init(table.getText(row, 0), table.getText(row, 1), table
                .getText(row, 2), table.getText(row, 3), table.getText(row, 4),
                table.getText(row, 5));

        editFields.show();
    }

    public void init() {
        lineHolder.init();

        while (table.getRowCount() > 3) {
            table.removeRow(3);
        }

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl()
                        + "registers/membershipprices.php?action=all");

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(String responseText) {
                JSONValue value = JSONParser.parse(responseText);
                JSONObject obj = value.isObject();
                JSONArray semesters = obj.get("semesters").isArray();
                JSONObject prices = obj.get("price").isObject();

                Map priceYear = getPricesMap(prices.get("year").isArray(),
                        "year");
                Map priceCourse = getPricesMap(prices.get("course").isArray(),
                        "semester");
                Map priceTrain = getPricesMap(prices.get("train").isArray(),
                        "semester");

                String currentYear = null;

                int row = 3;
                for (int i = 0; i < semesters.size(); i++) {
                    JSONObject semObj = semesters.get(i).isObject();

                    String year = Util.str(semObj.get("year"));
                    String semester = Util.str(semObj.get("semester"));
                    int colPlus = "1".equals(Util.str(semObj.get("fall"))) ? 2
                            : 0;

                    if (!year.equals(currentYear)) {
                        row++;
                        String style = (((row + 2) % 6) < 3) ? "line2"
                                : "line1";
                        table.getRowFormatter().setStyleName(row, style);
                        table.setText(row, 0, year);

                        if (priceYear.containsKey(year)) {
                            table.setText(row, 1, (String) priceYear.get(year));
                            table.getCellFormatter().setStyleName(row, 1,
                                    "right");
                        }

                        Image editImage = ImageFactory
                                .editImage("membershipPriceEdit");
                        editImage.addClickListener(me);
                        table.setWidget(row, 6, editImage);

                        currentYear = year;
                        lineHolder.add(String.valueOf(row), editImage);
                    }

                    if (priceCourse.containsKey(semester)) {
                        table.setText(row, 2 + colPlus, (String) priceCourse
                                .get(semester));
                        table.getCellFormatter().setStyleName(row, 2 + colPlus,
                                "right");
                    }
                    if (priceTrain.containsKey(semester)) {
                        table.setText(row, 3 + colPlus, (String) priceTrain
                                .get(semester));
                        table.getCellFormatter().setStyleName(row, 3 + colPlus,
                                "right");
                    }

                }

            }

            private Map getPricesMap(JSONArray array, String type) {
                HashMap result = new HashMap();

                for (int i = 0; i < array.size(); i++) {
                    JSONObject priceObj = array.get(i).isObject();

                    result.put(Util.str(priceObj.get(type)), Util.str(priceObj
                            .get("amount")));
                }

                return result;
            }

        };

        try {
            builder.sendRequest("", new AuthResponder(constants, messages,
                    callback));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }

    }

    class MembershipPriceEditFields extends DialogBox implements ClickListener {

        private Button saveButton;
        private Button cancelButton;
        private HTML mainErrorLabel;
        private TextBoxWithErrorText yearPrice;
        private TextBoxWithErrorText springCoursePrice;
        private TextBoxWithErrorText springTrainPrice;
        private TextBoxWithErrorText fallCoursePrice;
        private TextBoxWithErrorText fallTrainPrice;
        private String year;

        MembershipPriceEditFields() {
            setText(elements.menuitem_membership_prices());
            FlexTable edittable = new FlexTable();
            edittable.setStyleName("edittable");

            edittable.setText(1, 0, elements.year_membership());
            edittable.setText(2, 0, elements.spring());
            edittable.setText(3, 0, elements.course_membership());
            edittable.setText(4, 0, elements.train_membership());
            edittable.setText(5, 0, elements.fall());
            edittable.setText(6, 0, elements.course_membership());
            edittable.setText(7, 0, elements.train_membership());

            yearPrice = new TextBoxWithErrorText("yearPrice");
            springCoursePrice = new TextBoxWithErrorText("springCoursePrice");
            springTrainPrice = new TextBoxWithErrorText("springTrainPrice");
            fallCoursePrice = new TextBoxWithErrorText("fallCoursePrice");
            fallTrainPrice = new TextBoxWithErrorText("fallTrainPrice");

            edittable.setWidget(1, 1, yearPrice);
            edittable.setWidget(3, 1, springCoursePrice);
            edittable.setWidget(4, 1, springTrainPrice);
            edittable.setWidget(6, 1, fallCoursePrice);
            edittable.setWidget(7, 1, fallTrainPrice);

            DockPanel dp = new DockPanel();
            dp.add(edittable, DockPanel.NORTH);

            saveButton = new NamedButton("membershipPriceEditView_saveButton",
                    elements.save());
            saveButton.addClickListener(this);
            cancelButton = new NamedButton(
                    "membershipPriceEditView_cancelButton", elements.cancel());
            cancelButton.addClickListener(this);

            mainErrorLabel = new HTML();
            mainErrorLabel.setStyleName("error");

            HorizontalPanel buttonPanel = new HorizontalPanel();
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            buttonPanel.add(mainErrorLabel);
            dp.add(buttonPanel, DockPanel.NORTH);
            setWidget(dp);
        }

        public void onClick(Widget sender) {
            if (sender == cancelButton) {
                hide();
            } else if (sender == saveButton && validateFields()) {
                doSave();
            }
        }

        private void doSave() {
            StringBuffer sb = new StringBuffer();
            sb.append("action=save");
            Util.addPostParam(sb, "year", year);
            Util.addPostParam(sb, "yearPrice", yearPrice.getText());
            Util.addPostParam(sb, "springCoursePrice", springCoursePrice.getText());
            Util.addPostParam(sb, "springTrainPrice", springTrainPrice.getText());
            Util.addPostParam(sb, "fallCoursePrice", fallCoursePrice.getText());
            Util.addPostParam(sb, "fallCoursePrice", fallTrainPrice.getText());
            
            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                    constants.baseurl() + "registers/projects.php");

            ServerResponse callback = new ServerResponse() {

                public void serverResponse(String serverResponse) {
                    
                }
            };
            
            try {
                builder.setHeader("Content-Type",
                        "application/x-www-form-urlencoded");
                builder.sendRequest(sb.toString(), new AuthResponder(constants,
                        messages, callback));
            } catch (RequestException e) {
                Window.alert("Failed to send the request: " + e.getMessage());
            }


        }

        private void init(String year, String yearPrice,
                String springCoursePrice, String springTrainPrice,
                String fallCoursePrice, String fallTrainPrice) {
            this.year = year;
            this.yearPrice.setText(yearPrice);
            this.springCoursePrice.setText(springCoursePrice);
            this.springTrainPrice.setText(springTrainPrice);
            this.fallCoursePrice.setText(fallCoursePrice);
            this.fallTrainPrice.setText(fallTrainPrice);
            setText(elements.menuitem_membership_prices()+" "+year);


        }

        private boolean validateFields() {
            MasterValidator mv = new MasterValidator();
            
            mv.mandatory(messages.required_field(), new Widget[] {yearPrice, springCoursePrice, springTrainPrice, fallCoursePrice, fallTrainPrice});
            return mv.validateStatus();
        }
    }

    public void flushCompleted() {
        me.init();
    }
}
