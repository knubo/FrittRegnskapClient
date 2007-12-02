package no.knubo.accounting.client.ui;

import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;

public class AccountTable extends FlexTable {

    public AccountTable(String style) {
        super();
        setStyleName(style);
    }

    public void setInt(int row, int column, int value) {
        setText(row, column, String.valueOf(value));
    }

    public void setMoney(int row, int column, double value) {
        setText(row, column, Util.money(String.valueOf(value)));
        getCellFormatter().setStyleName(row, column, "right");
    }

    public void setMoney(int row, int column, JSONValue value) {
        setText(row, column, Util.money(String.valueOf(value)));
        getCellFormatter().setStyleName(row, column, "right");
    }

    public void setMoney(int row, int column, double value, String style) {
        setText(row, column, Util.money(String.valueOf(value)));
        getCellFormatter().setStyleName(row, column, style);
    }

    public void setMoney(int row, int column, JSONValue value, String style) {
        setText(row, column, Util.money(String.valueOf(value)));
        getCellFormatter().setStyleName(row, column, style);
    }

    public void setTooltip(int row, int col, String tooltip) {
        Element element = getFlexCellFormatter().getElement(row, col);
        DOM.setElementAttribute(element, "title", tooltip);
    }

    public void setHeaderRowStyle(int row) {
        getRowFormatter().setStyleName(row, "header desc");
    }

    public void setHeaderColStyle(int column) {
        getColumnFormatter().setStyleName(column, "header desc");
    }

    public void alternateStyle(int row, boolean alt) {
        String style = alt ? "showlineposts2" : "showlineposts1";
        getRowFormatter().setStyleName(row, style);
    }
}
