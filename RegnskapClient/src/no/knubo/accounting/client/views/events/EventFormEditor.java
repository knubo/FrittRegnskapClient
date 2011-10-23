package no.knubo.accounting.client.views.events;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.misc.CKEditorFunctions;
import no.knubo.accounting.client.ui.NamedTextArea;
import no.knubo.accounting.client.views.events.dad.PaletteWidget;
import no.knubo.accounting.client.views.events.dad.SetWidgetDropController;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EventFormEditor extends Composite implements ClickHandler {

    private static final int COLUMNS = 6;
    private static final int ROWS = 10;

    private Event event;
    private PickupDragController dragController;
    private FlexTable flexTable;
    private VerticalPanel sourcePanel;
    private final Elements elements;
    private final I18NAccount messages;
    private boolean editorReplaced;

    public EventFormEditor(Elements elements, I18NAccount messages) {
        this.elements = elements;
        this.messages = messages;

        VerticalPanel vp = new VerticalPanel();

        AbsolutePanel boundaryPanel = new AbsolutePanel();
        boundaryPanel.setPixelSize(600, 400);
        dragController = new PickupDragController(boundaryPanel, false);
        dragController.setBehaviorMultipleSelection(false);

        flexTable = new FlexTable();
        boundaryPanel.add(flexTable, 5, 5);

        flexTable.addClickHandler(this);

        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                // create a simple panel drop target for the current cell
                SimplePanel simplePanel = new SimplePanel();
                simplePanel.addStyleName("eventbox");

                flexTable.setWidget(j, i, simplePanel);
                // flexTable.getCellFormatter().setStyleName(i, j,
                // CSS_DEMO_PUZZLE_CELL);

                // instantiate a drop controller of the panel in the current
                // cell
                SetWidgetDropController dropController = new SetWidgetDropController(simplePanel);
                dragController.registerDropController(dropController);
            }
        }

        sourcePanel = new VerticalPanel();
        flexTable.setWidget(0, COLUMNS + 1, sourcePanel);
        flexTable.getFlexCellFormatter().setRowSpan(0, COLUMNS + 1, ROWS);

        NamedTextArea aboveTextBox = new NamedTextArea("above_text_box");
        aboveTextBox.setSize("60em", "20em");

        vp.add(aboveTextBox);
        vp.add(boundaryPanel);
        initWidget(vp);
    }

    public void setData(Event event) {
        this.event = event;

        resetView();
        setPreSetHTML(event);
        setUpWidgets();
        
        if (!editorReplaced) {
            editorReplaced = true;
            setupRichEditor();
            CKEditorFunctions.configStylesInt("", "");
        }

        setHTML(event.getHeaderHTML());
}

    private void resetView() {
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                Widget widget = flexTable.getWidget(j, i);

                if (widget instanceof SimplePanel) {
                    SimplePanel sp = (SimplePanel) widget;
                    if (sp.getWidget() != null) {
                        sp.clear();
                    }
                }
            }
        }
    }

    private void setPreSetHTML(Event event) {
        Map<Pair<Integer, Integer>, String> htmls = event.getHTMLLabels();

        Set<Pair<Integer, Integer>> pairs = htmls.keySet();

        for (Pair<Integer, Integer> pair : pairs) {
            SimplePanel sp = (SimplePanel) flexTable.getWidget(pair.getA(), pair.getB());

            sp.add(new HTML(htmls.get(pair)));
        }
    }

    public void setUpWidgets() {
        Collection<EventGroup> eventGroups = event.getEventGroups();

        for (EventGroup eventGroup : eventGroups) {
            if (!eventGroup.hasWidget()) {
                assignWidget(eventGroup);
            }
        }

    }

    private void assignWidget(EventGroup eventGroup) {
        Widget paletteWidget = eventGroup.createWidget();

        dragController.makeDraggable(paletteWidget);

        if (eventGroup.isPositioned()) {
            SimplePanel sp = (SimplePanel) flexTable.getWidget(eventGroup.getRow(), eventGroup.getCol());
            sp.add(paletteWidget);

        } else {
            sourcePanel.add(paletteWidget);
        }
    }

    public void onClick(ClickEvent event) {
        Cell cell = flexTable.getCellForEvent(event);
        int x = cell.getCellIndex();
        int y = cell.getRowIndex();

        Widget widget = flexTable.getWidget(y, x);

        if (widget instanceof SimplePanel) {
            SimplePanel sp = (SimplePanel) widget;

            if (sp.getWidget() == null || sp.getWidget() instanceof HTML) {
                EditTextPopup.showPopupForEditingText(elements, messages, sp);
            }

        }

    }

    public String getHTMLView() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<div>");
        sb.append(getHTML());
        sb.append("</div>");
        sb.append("<table>\n");
        for (int row = 0; row < flexTable.getRowCount(); row++) {
            sb.append("<tr>");
            for (int col = 0; col < flexTable.getCellCount(row); col++) {
                sb.append("<td>");

                Widget w = flexTable.getWidget(row, col);

                if (w != null && w instanceof SimplePanel) {
                    SimplePanel sp = (SimplePanel) w;
                    w = sp.getWidget();

                    if (w instanceof HTML) {
                        sb.append(w.getElement().getInnerHTML());
                    } else if (w instanceof PaletteWidget) {
                        PaletteWidget pw = (PaletteWidget) w;

                        sb.append(pw.getWidget().getElement().getInnerHTML());
                    }
                }

                sb.append("</td>");
            }
            sb.append("</tr>\n");
        }

        sb.append("</table>\n");
        return sb.toString();
    }

    public void setGroupPositionsAndHTML() {
        event.resetHTML();

        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                Widget widget = flexTable.getWidget(j, i);

                if (!(widget instanceof SimplePanel)) {
                    continue;
                }
                SimplePanel sp = (SimplePanel) widget;

                widget = sp.getWidget();

                if (widget instanceof PaletteWidget) {
                    PaletteWidget pw = (PaletteWidget) widget;
                    event.setGroupPosition(j, i, pw.getGroupName());

                } else if (widget instanceof HTML) {
                    HTML html = (HTML) widget;
                    event.setHTML(j, i, html.getHTML());
                }
            }
        }
        event.setHeaderHTML(getHTML());
    }

    static native void setupRichEditor()
    /*-{
       $wnd['CKEDITOR'].replace( 'above_text_box' );
     
    }-*/;

    static native String getHTML()
    /*-{
    
        return $wnd['CKEDITOR'].instances.above_text_box.getData();
    }-*/;

    static native void setHTML(String x)
    /*-{
       $wnd['CKEDITOR'].instances.above_text_box.setData(x);
    }-*/;

}
