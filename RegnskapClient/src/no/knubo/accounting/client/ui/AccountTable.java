package no.knubo.accounting.client.ui;

import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class AccountTable extends FlexTable {

    public AccountTable(String style) {
        super();
        setStyleName(style);
    }

    public void setStr(int row, int column, JSONValue value) {
        setText(row, column, Util.str(value));
    }

    public void setHeader(int row, int column, String text) {
        setText(row, column, text, "header desc");
    }

    public void setText(int row, int column, String text, String style) {
        super.setText(row, column, text);
        getCellFormatter().addStyleName(row, column, style);

    }

    public void setInt(int row, int column, int value) {
        super.setText(row, column, String.valueOf(value));
    }

    public void setMoney(int row, int column, double value) {
        super.setText(row, column, Util.money(String.valueOf(value)));
        getCellFormatter().setStyleName(row, column, "right");
    }

    public void setMoney(int row, int column, JSONValue value) {
        super.setText(row, column, Util.money(String.valueOf(value)));
        getCellFormatter().setStyleName(row, column, "right");
    }

    public void setMoney(int row, int column, double value, String style) {
        super.setText(row, column, Util.money(String.valueOf(value)));
        getCellFormatter().setStyleName(row, column, style);
    }

    public void setMoney(int row, int column, JSONValue value, String style) {
        super.setText(row, column, Util.money(String.valueOf(value)));
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

    public void alternateStyle(int row, int modifier) {
        String style = ((row + modifier) % 2 == 0) ? "showlineposts2" : "showlineposts1";
        getRowFormatter().setStyleName(row, style);
    }

    public void alternateStyle(int row, boolean alt) {
        String style = alt ? "showlineposts2" : "showlineposts1";
        getRowFormatter().setStyleName(row, style);
    }

    public void setColStyle(int row, int col, String style) {
        getCellFormatter().addStyleName(row, col, style);
    }

    public void setWidget(int row, int col, Widget widget, String style) {
        super.setWidget(row, col, widget);
        getCellFormatter().addStyleName(row, col, style);
    }

    
    public void setColSpan(int row, int col, int span) {
        getFlexCellFormatter().setColSpan(row, col, span);        
    }
    
    public void setColSpanAndRowStyle(int row, int col, int span, String rowstyle) {
        getFlexCellFormatter().setColSpan(row, col, span);
        getRowFormatter().setStyleName(row, rowstyle);
    }

    public FlexCellFormatter setWidgetFlex(int row, int col, Widget widget) {
        setWidget(row, col, widget);
        return getFlexCellFormatter();
    }

    public void setText(int row, String... txt) {
        for (int i = 0; i < txt.length; i++) {
            if (txt[i] != null) {
                setText(row, i, txt[i]);
            }
        }
    }

    public void setHeaders(int row, String... headers) {
        setText(row, headers);
        setHeaderRowStyle(row);
    }

    public void alternateStyleBlue(int row, int modifier) {
        String style = ((row + modifier) % 2 == 0) ? "line1" : "line2";
        getRowFormatter().setStyleName(row, style);
    }

    public void setHeadingWithColspan(int row, int colspan, String txt) {
        setText(row, 0, txt);
        setColSpanAndRowStyle(row, 0, colspan, "header");
    }

}
