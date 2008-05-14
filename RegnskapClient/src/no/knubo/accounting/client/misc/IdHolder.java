package no.knubo.accounting.client.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/** Indexes a set of widgets based on an id string. */
public class IdHolder<A, W extends Widget> {

    private List<W> widgets = new ArrayList<W>();
    private List<A> ids = new ArrayList<A>();

    /** Empties registered widgets. */
    public void init() {
        widgets.clear();
        ids.clear();
    }

    /**
     * Register a widget with an id.
     * 
     * @param id
     * @param widget
     */
    public void add(A id, W widget) {
        ids.add(id);
        widgets.add(widget);
    }

    public int remove(A id) {
        Iterator<W> widgetIt = widgets.iterator();
        Iterator<A> idsIt = ids.iterator();
        int pos = 0;

        while (idsIt.hasNext()) {
            A oneId = idsIt.next();
            widgetIt.next();

            if (oneId.equals(id)) {
                idsIt.remove();
                widgetIt.remove();
                return pos;
            }
            pos++;
        }
        Window.alert("Should have found id to remove - program error.");
        return 0;
    }

    /**
     * Finds the id for a given widget.
     * 
     * @param sender
     *            The widget to find.
     * @return The id, or null if not found.
     */
    public Object findObject(Widget sender) {
        Iterator<A> idIt = ids.iterator();
        for (Iterator<W> widgetIt = widgets.iterator(); widgetIt.hasNext();) {
            Widget widget = widgetIt.next();
            Object id = idIt.next();

            if (widget == sender) {
                return id;
            }
        }
        return null;
    }

    /**
     * Finds the id for a given widget.
     * 
     * @param sender
     *            The widget to find.
     * @return The id, or null if not found.
     */
    public A findId(Widget sender) {
        Iterator<A> idIt = ids.iterator();
        for (Iterator<W> widgetIt = widgets.iterator(); widgetIt.hasNext();) {
            Widget widget = widgetIt.next();
            A id = idIt.next();

            if (widget == sender) {
                return id;
            }
        }
        return null;
    }

    
    public List<W> getWidgets() {
        return widgets;
    }

    public void addObject(A obj, W widget) {
        ids.add(obj);
        widgets.add(widget);        
    }
}
