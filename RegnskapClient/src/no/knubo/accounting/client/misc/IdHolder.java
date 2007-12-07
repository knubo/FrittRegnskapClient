package no.knubo.accounting.client.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/** Indexes a set of widgets based on an id string. */
public class IdHolder {

    private List widgets = new ArrayList();
    private List ids = new ArrayList();

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
    public void add(String id, Widget widget) {
        ids.add(id);
        widgets.add(widget);
    }

    public int remove(String id) {
        Iterator widgetIt = widgets.iterator();
        Iterator idsIt = ids.iterator();
        int pos = 0;

        while (idsIt.hasNext()) {
            String oneId = (String) idsIt.next();
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
        Iterator idIt = ids.iterator();
        for (Iterator widgetIt = widgets.iterator(); widgetIt.hasNext();) {
            Widget widget = (Widget) widgetIt.next();
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
    public String findId(Widget sender) {
        Iterator idIt = ids.iterator();
        for (Iterator widgetIt = widgets.iterator(); widgetIt.hasNext();) {
            Widget widget = (Widget) widgetIt.next();
            String id = (String) idIt.next();

            if (widget == sender) {
                return id;
            }
        }
        return null;
    }

    
    public List getWidgets() {
        return widgets;
    }

    public void addObject(JSONObject obj, Image widget) {
        ids.add(obj);
        widgets.add(widget);        
    }
}
