package no.knubo.accounting.client.views.events.dad;

import no.knubo.accounting.client.misc.ImageFactory;

import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget wrapper class used by {@link PalettePanel}.
 */
public class PaletteWidget extends HorizontalPanel implements HasDragHandle {

    private Image shim = new Image();
    private Widget widget;
    private final String groupName;

    /**
     * Default constructor to wrap the provided widget.
     * 
     * @param widget
     *            the widget to be wrapped
     */
    public PaletteWidget(Widget widget, String groupName) {
        this.widget = widget;
        this.groupName = groupName;
        shim = ImageFactory.dragHandleImage();
        add(shim);

        add(widget);
    }

    @Override
    public Widget getDragHandle() {
        return shim;
    }

    public Widget getWidget() {
        return widget;
    }

    public void replaceWidget(Widget newWidget) {
        remove(widget);
        add(newWidget);
        this.widget = newWidget;
    }

    public String getGroupName() {
        return groupName;
    }
}
