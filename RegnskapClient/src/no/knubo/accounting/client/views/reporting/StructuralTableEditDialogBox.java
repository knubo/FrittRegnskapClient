package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class StructuralTableEditDialogBox extends DialogBox implements ClickHandler, KeyPressHandler, KeyDownHandler {

    private final TextArea editArea;
    private NamedButton useButton;
    private NamedButton cancelButton;
    private Image previousImage;
    private Image nextImage;
    private Image upImage;
    private Image downImage;
    private AccountTable data;

    private int editRow = 0;
    private int editCol = 0;
    private TextBox editBox;

    public StructuralTableEditDialogBox(TextArea editArea, Elements elements) {
        this.editArea = editArea;
        setText("Table");

        AccountTable meta = buildMetaTable();

        data = new AccountTable("spred");
        data.addClickHandler(this);

        editBox = new TextBox();
        editBox.addKeyPressHandler(this);
        editBox.addKeyDownHandler(this);
        data.setWidget(0, 0, editBox);

        DockPanel dp = new DockPanel();
        previousImage = ImageFactory.previousImage("table_reduce_cols");
        previousImage.addClickHandler(this);
        nextImage = ImageFactory.nextImage("table_expand_cols");
        nextImage.addClickHandler(this);
        upImage = ImageFactory.upImage("table_reduce_rows");
        upImage.addClickHandler(this);
        downImage = ImageFactory.downImage("table_expand_rows");
        downImage.addClickHandler(this);

        dp.add(data, DockPanel.CENTER);
        dp.add(previousImage, DockPanel.WEST);
        dp.add(nextImage, DockPanel.WEST);
        dp.add(upImage, DockPanel.EAST);
        dp.add(downImage, DockPanel.EAST);

        VerticalPanel vp = new VerticalPanel();
        vp.add(meta);
        vp.add(dp);

        useButton = new NamedButton("use", "Bruk");
        cancelButton = new NamedButton("cancel", elements.cancel());
        FlowPanel buttonPanel = new FlowPanel();
        useButton.addClickHandler(this);
        useButton.addStyleName("buttonrow");
        buttonPanel.add(useButton);
        cancelButton.addClickHandler(this);
        cancelButton.addStyleName("buttonrow");
        buttonPanel.add(cancelButton);

        vp.add(buttonPanel);

        setWidget(vp);
    }

    private AccountTable buildMetaTable() {
        AccountTable meta = new AccountTable("tableborder");
        meta.setText(0, 0, "Fontst¿rrelse");
        meta.setWidget(0, 1, new TextBoxWithErrorText("font", 3));

        meta.setText(1, 0, "Kolonneoverskrifter");
        ListBoxWithErrorText headerListbox = new ListBoxWithErrorText("column_header");
        headerListbox.addItem("Skjul", "0");
        headerListbox.addItem("Vis", "1");

        meta.setWidget(1, 1, headerListbox);

        meta.setText(2, 0, "Linjer");
        ListBoxWithErrorText lines = new ListBoxWithErrorText("lines");
        meta.setWidget(2, 1, lines);
        meta.setText(3, 0, "X-posisjon");
        ListBoxWithErrorText xPosition = new ListBoxWithErrorText("x_position");
        meta.setWidget(3, 1, xPosition);
        meta.setText(4, 0, "X-orientering");
        ListBoxWithErrorText xOrientation = new ListBoxWithErrorText("x_orientation");
        meta.setWidget(4, 1, xOrientation);
        meta.setText(5, 0, "Skygger");
        ListBoxWithErrorText shadows = new ListBoxWithErrorText("shadows");
        meta.setWidget(5, 1, shadows);
        return meta;
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == cancelButton) {
            hide();
            editArea.setFocus(true);
        } else if (event.getSource() == nextImage) {
            int row = data.getRowCount() - 1;
            int col = data.getCellCount(0);
            for (int i = 0; i <= row; i++) {
                data.setWidget(i, col, ImageFactory.blankImage(15, 15));
            }

        } else if (event.getSource() == downImage) {
            int row = data.getRowCount();
            int cellCount = data.getCellCount(0);
            for (int i = 0; i < cellCount; i++) {
                data.setWidget(row, i, ImageFactory.blankImage(15, 15));
            }
        } else if (event.getSource() == data) {
            Cell cell = data.getCellForEvent(event);
            int x = cell.getCellIndex();
            int y = cell.getRowIndex();

            editCellAt(y, x);
        } else if (event.getSource() == upImage) {
            if (editRow < 1) {
                return;
            }
            data.remove(editBox);
            data.removeRow(editRow);
            editRow = -1;
        } else if (event.getSource() == previousImage) {
            if (editCol < 1) {
                return;
            }
            data.remove(editBox);
            for (int row = 0; row < data.getRowCount(); row++) {
                data.removeCell(row, editCol);
            }
            editCol = -1;

        }
    }

    private void editCellAt(int y, int x) {
        String editValue = editBox.getText().trim();
        data.remove(editBox);
        if (editRow >= 0 && editCol >= 0) {
            if (editValue.isEmpty()) {
                data.setWidget(editRow, editCol, ImageFactory.blankImage(15, 15));
            } else {
                data.setText(editRow, editCol, editValue, "desc");
            }
        }
        String nextValue = data.getText(y, x);
        editBox.setText(nextValue);
        resizeTextbox();
        data.setWidget(y, x, editBox);
        editBox.setFocus(true);
        editRow = y;
        editCol = x;
    }

    @Override
    public void onKeyPress(KeyPressEvent event) {
        resizeTextbox();
    }

    private void resizeTextbox() {
        editBox.setVisibleLength(editBox.getText().length() + 1);
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
            int cols = data.getCellCount(0);

            if (editCol < cols - 1) {
                editCellAt(editRow, editCol + 1);

            } else {
                if (editRow == (data.getRowCount() - 1)) {
                    editCellAt(0, 0);
                } else {
                    editCellAt(editRow + 1, 0);
                }
            }
            event.stopPropagation();
            event.preventDefault();
        }

        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            int rows = data.getRowCount();

            if (editRow < rows - 1) {
                editCellAt(editRow + 1, editCol);

            } else {
                if (editCol == (data.getCellCount(0) - 1)) {
                    editCellAt(0, 0);
                } else {
                    editCellAt(0, editCol + 1);
                }
            }
            event.stopPropagation();
            event.preventDefault();
        }
    }

}
