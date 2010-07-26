package no.knubo.accounting.client.views.reporting;

import java.util.HashMap;
import java.util.Set;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.ViewCallback;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SimpleMassletterEditView extends Composite implements KeyDownHandler {

    private static SimpleMassletterEditView instance;
    private final Constants constants;
    private final I18NAccount messages;
    private final Elements elements;
    private final ViewCallback callback;
    private TextArea editArea;
    private JSONArray fonts;
    protected JSONArray images;
    private HashMap<String, String> addText;
    public int lastUsedFont = 0;

    public SimpleMassletterEditView(Constants constants, I18NAccount messages, Elements elements, ViewCallback callback) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;
        this.callback = callback;

        VerticalPanel vp = new VerticalPanel();
        HorizontalPanel hp = new HorizontalPanel();

        editArea = new TextArea();
        editArea.setWidth("50em");
        editArea.setHeight("20em");
        editArea.addKeyDownHandler(this);

        hp.add(editArea);

        DecoratedTabPanel tabPanel = new DecoratedTabPanel();
        setupTabs(tabPanel);
        tabPanel.selectTab(0);

        hp.add(tabPanel);

        vp.add(hp);
        FlowPanel buttonPanel = new FlowPanel();

        buttonPanel.add(new NamedButton("save", elements.save()));
        buttonPanel.add(new NamedButton("cancel", elements.cancel()));
        buttonPanel.add(new NamedButton("preview", elements.preview()));
        vp.add(buttonPanel);

        initFonts();

        initWidget(vp);
    }

    private void initFonts() {
        ServerResponse init = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                fonts = responseObj.isArray();
                initAddtext();
            }
        };
        AuthResponder.get(constants, messages, init, "reports/massletter.php?action=fonts");

    }

    private void setupTabs(DecoratedTabPanel tabPanel) {
        tabPanel.add(createDocumentTab(), elements.massletter_document());
        tabPanel.add(createGiroTable(), elements.massletter_giro());
        tabPanel.add(createMemberCardTable(), elements.massletter_membercard());
        tabPanel.add(new FlowPanel(), elements.preview_actual());
    }

    private AccountTable createMemberCardTable() {
        AccountTable table = new AccountTable("");
        table.setWidget(0, 0, new CheckBox(elements.massletter_include_card()));
        table.getFlexCellFormatter().setColSpan(0, 0, 2);
        table.setText(1, 0, elements.massletter_start_y());
        table.setWidget(1, 1, new TextBoxWithErrorText("start_y", 3));
        table.setText(2, 0, elements.massletter_start_x());
        table.setWidget(2, 1, new TextBoxWithErrorText("start_x", 3));

        return table;
    }

    private AccountTable createGiroTable() {
        AccountTable table = new AccountTable("");
        table.setWidget(0, 0, new CheckBox(elements.massletter_include_giro()));
        table.getFlexCellFormatter().setColSpan(0, 0, 2);
        table.setText(1, 0, elements.massletter_start_y());
        table.setWidget(1, 1, new TextBoxWithErrorText("start_y", 3));

        table.setText(2, 0, elements.massletter_from());
        table.setColSpanAndRowStyle(2, 0, 2, "header");
        table.setText(3, 0, elements.name());
        table.setWidget(3, 1, new TextBoxWithErrorText("massletter_from"));
        table.setText(4, 0, elements.address());
        table.setWidget(4, 1, new TextBoxWithErrorText("massletter_address"));
        table.setText(5, 0, elements.postnmb() + " " + elements.city());
        table.setWidget(5, 1, new TextBoxWithErrorText("massletter_city"));
        table.setText(6, 0, elements.massletter_payment_info());
        table.setColSpanAndRowStyle(6, 0, 2, "header");
        table.setText(7, 0, elements.massletter_accountnumber());
        table.setWidget(7, 1, new TextBoxWithErrorText("massletter_accountnumber"));
        table.setText(8, 0, elements.massletter_due_date());
        table.setWidget(8, 1, new TextBoxWithErrorText("massletter_due_date", 11));

        return table;
    }

    private AccountTable createDocumentTab() {
        AccountTable table = new AccountTable("");
        table.setText(0, 0, elements.massletter_sheet_type());
        table.setWidget(0, 1, new TextBoxWithErrorText("sheet_type"));

        table.setText(1, 0, elements.massletter_orientation(), "nowrap");
        ListBox sheetTypeListBox = new ListBox();
        sheetTypeListBox.addItem(elements.massletter_portrait());
        sheetTypeListBox.addItem(elements.massletter_landscape());
        table.setWidget(1, 1, sheetTypeListBox);

        table.setText(2, 0, elements.massletter_margins(), "nowrap");
        FlowPanel marginsPanel = new FlowPanel();
        HTML errorLabel = new HTML();
        marginsPanel.add(new TextBoxWithErrorText("margin_top", errorLabel, 3));
        marginsPanel.add(new TextBoxWithErrorText("margin_bottom", errorLabel, 3));
        marginsPanel.add(new TextBoxWithErrorText("margin_left", errorLabel, 3));
        marginsPanel.add(new TextBoxWithErrorText("margin_right", errorLabel, 3));
        marginsPanel.add(errorLabel);
        table.setWidget(2, 1, marginsPanel);

        return table;
    }

    public static SimpleMassletterEditView getInstance(Constants constants, I18NAccount messages, Elements elements,
            ViewCallback callback) {
        if (instance == null) {
            instance = new SimpleMassletterEditView(constants, messages, elements, callback);
        }

        return instance;
    }

    public void init(String[] params) {
        String filename = params[0];
        String response = params[1];

        initImages();
    }

    private void initAddtext() {
        addText = new HashMap<String, String>();
        addText.put("font", Util.str(fonts.get(lastUsedFont)) + " 10");
        addText.put("ezSetY", "0");
        addText.put("ezSetDy", "0");
        addText.put("wraptext", "Skriv inn tekst");
        addText.put("image", "...");
        addText.put("setColor", "0,0,0");

    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        editArea.setFocus(true);
    }

    private void initImages() {
        ServerResponse init = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                images = responseObj.isArray();
            }
        };
        AuthResponder.get(constants, messages, init, "reports/massletter.php?action=images");

    }

    public void onKeyDown(KeyDownEvent event) {
        if (event.isControlKeyDown() && event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {

            if (!editArea.isAttached()) {
                Window.alert("Edit area not attached!");
                return;
            }

            AutoFill autoFill = new AutoFill("wraptext", "font", "ezSetY", "ezSetDy", "image", "wrapopts", "setColor",
                    "rectangle", "reltext");
            showAutofill(autoFill);
        } else {
            doAutoAssist(event);
        }
    }

    private void showAutofill(AutoFill autoFill) {
        autoFill.setPopupPosition(editArea.getAbsoluteLeft() + editArea.getOffsetWidth() / 2, editArea.getAbsoluteTop()
                + editArea.getOffsetHeight() / 2);
        autoFill.show();
        autoFill.init();
    }

    class AutoFill extends DialogBox implements ClickHandler, KeyDownHandler {
        private ListBox box;
        private boolean clean;
        private int clipLength;
        private int previousSelectedIndex = 0;
        private boolean isFontSelect;

        AutoFill() {
            setText("Hjelper...");
            box = new ListBox();
            box.addClickHandler(this);
            box.addKeyDownHandler(this);

            setWidget(box);
        }

        AutoFill(String... choices) {
            this();

            for (String string : choices) {
                box.addItem(string);
            }
            box.setVisibleItemCount(box.getItemCount());
            clean = false;
        }

        public AutoFill(JSONArray fonts, int clipLength, boolean isFontSelect) {
            this();
            this.clipLength = clipLength;
            this.isFontSelect = isFontSelect;

            for (int i = 0; i < fonts.size(); i++) {
                box.addItem(Util.str(fonts.get(i)));
            }
            box.setVisibleItemCount(box.getItemCount());
            clean = true;
            box.setSelectedIndex(previousSelectedIndex);

        }

        void init() {
            box.setFocus(true);
        }

        public void onClick(ClickEvent event) {
            String text = Util.getSelectedText(box);
            String allText = editArea.getText();

            if (allText == null) {
                allText = "";
            }

            int cursorPos = editArea.getCursorPos();

            if (isFontSelect) {
                lastUsedFont = box.getSelectedIndex();
            }

            if (clipLength > 0) {
                allText = allText.substring(0, cursorPos) + allText.substring(cursorPos + clipLength);
            }

            int nextCursorPos = cursorPos + text.length() + 1;

            Util.log("Setting cursor pos:" + nextCursorPos);

            if (!clean) {
                text += " " + addText.get(text);
            } else {
                previousSelectedIndex = box.getSelectedIndex();
            }
            String newText;
            if (cursorPos == allText.length()) {
                newText = allText + text;
            } else {

                if (!clean && allText.charAt(cursorPos) != '\n') {
                    text = "\n" + text;
                }
                newText = allText.substring(0, cursorPos) + text + allText.substring(cursorPos);
            }
            hide();
            editArea.setFocus(true);
            editArea.setText(newText);
            editArea.setCursorPos(nextCursorPos);
            calculateSelection();
        }

        public void onKeyDown(KeyDownEvent event) {

            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                onClick(null);
                event.stopPropagation();
                event.preventDefault();
            } else if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                hide();
                editArea.setFocus(true);
            }

        }
    }

    public void calculateSelection() {
        int pos = editArea.getCursorPos();
        int start = pos;

        String scan = editArea.getText();

        while (start > 0 && scan.charAt(--start) != '\n') {
            /* Do loop */
        }

        /* It ends on the \n - we do not want that char... */
        if (start > 0) {
            start++;
        }

        int all = scan.indexOf('\n', start);
        if (all == -1) {
            all = scan.length();
        }

        String action = scan.substring(start, all);

        Util.log("Selection action:" + action + " start:" + start + " " + all);

        if (action.startsWith("ezSetY ")) {
            int params = start + 7;
            editArea.setSelectionRange(params, (all - params));
        } else if (action.startsWith("wraptext ")) {
            int params = start + 9;
            editArea.setSelectionRange(params, (all - params));
        } else if (action.startsWith("font ")) {
            int fontSeparatorStart = action.indexOf(' ', 6);

            int params = start + 5;
            Util.log("selection: " + action + " start:" + params + " length:" + (fontSeparatorStart - 5));
            editArea.setSelectionRange(params, fontSeparatorStart - 5);
        } else if (action.startsWith("image ")) {
            showImagePopup(action.substring(6).split(","));
        }
    }

    private void doAutoAssist(KeyDownEvent event) {
        int nativeKeyCode = event.getNativeKeyCode();

        if (KeyCodeEvent.isArrow(nativeKeyCode) || nativeKeyCode == KeyCodes.KEY_PAGEUP
                || nativeKeyCode == KeyCodes.KEY_PAGEDOWN || nativeKeyCode == KeyCodes.KEY_END
                || nativeKeyCode == KeyCodes.KEY_HOME) {
            return;
        }

        int pos = editArea.getCursorPos();
        int start = pos;

        String scan = editArea.getText();

        while (start > 0 && scan.charAt(--start) != '\n') {
            /* Do loop */
        }
        /* It ends on the \n - we do not want that char... */
        if (start > 0) {
            start++;
        }

        int all = scan.indexOf('\n', start);
        if (all == -1) {
            all = scan.length();
        }

        String action = scan.substring(start, all);

        Util.log("Action:" + action + " " + start + ">" + all);

        Set<String> actionsToCheck = addText.keySet();

        for (String toCheck : actionsToCheck) {
            if (action.startsWith(toCheck)) {

                /* Enter moves to next line, unless... */
                if (nativeKeyCode == KeyCodes.KEY_ENTER) {

                    /* , unless on first char then we accept it as a linefeed */
                    if (pos == start) {
                        return;
                    }

                    event.stopPropagation();
                    event.preventDefault();

                    if (all + 1 > scan.length()) {
                        editArea.setText(scan + "\n");
                    } else {
                        editArea.setCursorPos(all + 1);
                    }
                    return;
                }
                /* Check if delete entire line */
                if (pos >= start && pos < start + toCheck.length()) {
                    if (nativeKeyCode == KeyCodes.KEY_DELETE || nativeKeyCode == KeyCodes.KEY_BACKSPACE) {
                        event.stopPropagation();
                        event.preventDefault();
                        editArea.setText(scan.substring(0, start) + scan.substring(all));
                        editArea.setCursorPos(start);
                        return;
                    }

                    Util.log("Stop not delete in key");
                    event.preventDefault();
                    event.stopPropagation();
                    return;
                }
            }
        }

        if (action.startsWith("ezSetY ") || action.startsWith("ezSetDy ")) {
            if ((nativeKeyCode < '0' || nativeKeyCode > '9') && nativeKeyCode != KeyCodes.KEY_DELETE
                    && nativeKeyCode != KeyCodes.KEY_BACKSPACE) {
                Util.log("Stop not number or delete");
                event.preventDefault();
                event.stopPropagation();
                return;
            }
        } else if (action.startsWith("font ")) {
            int fontSeparatorStart = action.indexOf(' ', 5);

            if (pos > (start + 4) && (pos < (start + fontSeparatorStart) || fontSeparatorStart == -1)) {
                calculateSelection();
                showFontPopup();
                event.preventDefault();
                event.stopPropagation();
                return;
            }

            if ((nativeKeyCode < '0' || nativeKeyCode > '9') && nativeKeyCode != KeyCodes.KEY_DELETE
                    && nativeKeyCode != KeyCodes.KEY_BACKSPACE) {
                Util.log("Stop not number or delete");
                event.preventDefault();
                event.stopPropagation();
                return;
            }
        } else if (action.startsWith("image")) {
            calculateSelection();
        }
    }

    private void showImagePopup(String... params) {
        final DialogBox popup = new DialogBox();
        popup.setText("Bilde");
        AccountTable imagetable = new AccountTable("tableborder");

        final ListBoxWithErrorText imageListbox = new ListBoxWithErrorText("image");
        final TextBoxWithErrorText padding = new TextBoxWithErrorText("padding", 4);
        final TextBoxWithErrorText width = new TextBoxWithErrorText("padding", 4);
        final ListBoxWithErrorText just = new ListBoxWithErrorText("just");
        just.addItem("hoyre", "right");
        just.addItem("venstre", "left");
        just.addItem("midtjustert", "center");
        final Image imagePreview = new Image();
        imagePreview.addStyleName("shrinked");

        ChangeHandler imageSelect = new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                String file = Util.getSelected(imageListbox);
                imagePreview.setUrl(constants.baseurl() + "files/files.php?action=image&file=" + file);
            }
        };
        imageListbox.addChangeHandler(imageSelect);

        imageListbox.addItem("", "");
        for (int i = 0; i < images.size(); i++) {
            String file = Util.str(images.get(i));
            imageListbox.addItem(file, file);
        }

        imagetable.setText(0, 0, "Bilde");
        imagetable.setWidget(0, 1, imageListbox);
        imagetable.setText(1, 0, "Padding");
        imagetable.setWidget(1, 1, padding);
        imagetable.setText(2, 0, "Width");
        imagetable.setWidget(2, 1, width);
        imagetable.setText(3, 0, "Just");
        imagetable.setWidget(3, 1, just);
        imagetable.setWidget(0, 2, imagePreview);
        imagetable.getFlexCellFormatter().setRowSpan(0, 2, 5);

        final NamedButton useButton = new NamedButton("use", "Bruk");
        final NamedButton cancelButton = new NamedButton("cancel", elements.cancel());
        ClickHandler clickHandler = new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (event.getSource() == useButton) {
                    MasterValidator mv = new MasterValidator();
                    mv.mandatory(messages.required_field(), imageListbox, width);
                    mv.range(messages.field_positive(), 0, null, padding);
                    mv.range(messages.field_to_low_zero(), 1, null, width);
                    if (!mv.validateStatus()) {
                        return;
                    }
                    useImage(imageListbox.getText(), padding.getText(), width.getText(), just.getText());
                }
                popup.hide();
                editArea.setFocus(true);
            }

        };

        HorizontalPanel fp = new HorizontalPanel();
        useButton.addClickHandler(clickHandler);
        useButton.addStyleName("buttonrow");
        fp.add(useButton);
        cancelButton.addClickHandler(clickHandler);
        cancelButton.addStyleName("buttonrow");
        fp.add(cancelButton);

        imagetable.setWidget(4, 0, fp);

        popup.setWidget(imagetable);
        popup.center();
        imageListbox.setFocus(true);

        for (String one : params) {
            if (one.startsWith("file:")) {
                String file = params[0].substring(5);
                Util.setIndexByValue(imageListbox.getListbox(), file);
                imagePreview.setUrl(constants.baseurl() + "files/files.php?action=image&file=" + file);
            } else if (one.startsWith("padding:")) {
                padding.setText(params[1].substring(8));
            } else if (one.startsWith("width:")) {
                width.setText(one.substring(6));
            } else if (one.startsWith("just:")) {
                Util.setIndexByValue(just.getListbox(), one.substring(5));
            }
        }
    }

    protected void useImage(String image, String padding, String width, String just) {
        int pos = editArea.getCursorPos();
        int start = pos;

        String scan = editArea.getText();

        while (start > 0 && scan.charAt(--start) != '\n') {
            /* Do loop */
        }
        /* It ends on the \n - we do not want that char... */
        if (start > 0) {
            start++;
        }

        int all = scan.indexOf('\n', start);

        if (all == -1) {
            all = scan.length();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(scan.substring(0, start));
        sb.append("image file:");
        sb.append(image);
        sb.append(",padding:");
        if (padding.length() > 0) {
            sb.append(padding);
        } else {
            sb.append("0");
        }
        sb.append(",width:");
        sb.append(width);
        sb.append(",just:");
        sb.append(just);
        sb.append('\n');
        String newString = scan.substring(0, start) + sb.toString() + scan.substring(all);

        editArea.setText(newString);
        editArea.setCursorPos(start + sb.length());
    }

    private void showFontPopup() {
        AutoFill autoFill = new AutoFill(fonts, editArea.getSelectionLength(), true);
        showAutofill(autoFill);
    }

}
