package no.knubo.accounting.client.views.events;

import java.util.Collection;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
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
    private static final int ROWS = 4;

    private Event event;
    private PickupDragController dragController;
    private FlexTable flexTable;
    private VerticalPanel sourcePanel;
    private final Elements elements;
    private final I18NAccount messages;

    public EventFormEditor(Elements elements, I18NAccount messages) {
        this.elements = elements;
        this.messages = messages;

        AbsolutePanel boundaryPanel = new AbsolutePanel();
        boundaryPanel.setPixelSize(800, 600);
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

                flexTable.setWidget(i, j, simplePanel);
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

        initWidget(boundaryPanel);

    }

    public void setData(Event event) {
        this.event = event;
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

        sourcePanel.add(paletteWidget);
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

}
