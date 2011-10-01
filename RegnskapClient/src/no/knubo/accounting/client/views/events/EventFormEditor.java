package no.knubo.accounting.client.views.events;

import java.util.Collection;
import java.util.List;

import no.knubo.accounting.client.views.events.dad.PaletteWidget;
import no.knubo.accounting.client.views.events.dad.SetWidgetDropController;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EventFormEditor extends Composite {

    private static final int COLUMNS = 6;
    private static final int ROWS = 4;

    private Event event;
    private PickupDragController dragController;
    private FlexTable flexTable;
    private VerticalPanel sourcePanel;

    public EventFormEditor() {

        AbsolutePanel boundaryPanel = new AbsolutePanel();
        boundaryPanel.setPixelSize(500, 300);
        dragController = new PickupDragController(boundaryPanel, false);
        dragController.setBehaviorMultipleSelection(false);

        flexTable = new FlexTable();
        boundaryPanel.add(flexTable, 5, 5);

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
                eventGroup.setWidget(assignWidget(eventGroup));
            }
        }

    }

    private Widget assignWidget(EventGroup eventGroup) {
        Widget widget = eventGroup.createWidget();
        
        PaletteWidget paletteWidget = new PaletteWidget(widget);
        dragController.makeDraggable(paletteWidget);

        sourcePanel.add(paletteWidget);
        
        return paletteWidget;
    }
}
