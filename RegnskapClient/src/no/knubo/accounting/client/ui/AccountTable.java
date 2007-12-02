package no.knubo.accounting.client.ui;

import no.knubo.accounting.client.Util;

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
}
