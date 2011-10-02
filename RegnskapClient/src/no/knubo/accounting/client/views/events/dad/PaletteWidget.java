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

    /**
     * Default constructor to wrap the provided widget.
     * 
     * @param widget
     *            the widget to be wrapped
     */
    public PaletteWidget(Widget widget) {
        this.widget = widget;
        shim = ImageFactory.dragHandleImage();
        add(shim);

        add(widget);
    }

    public Widget getDragHandle() {
        return shim;
    }

    public void replaceWidget(Widget newWidget) {
        remove(widget);
        add(newWidget);
        this.widget = newWidget;
    }
}
