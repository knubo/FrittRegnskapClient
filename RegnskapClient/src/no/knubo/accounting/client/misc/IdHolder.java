package no.knubo.accounting.client.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/** Indexes a set of widgets based on an id string. */
public class IdHolder {

	/* These holds images for remove buttons and their corresponding ids. */
	private List widgets = new ArrayList();

	private List ids = new ArrayList();

	/** Zeroes the lists. */
	public void init() {
		widgets.clear();
		ids.clear();
	}

	public void add(String id, Image removeImage) {
		ids.add(id);
		widgets.add(removeImage);
	}

	public String findRemoveId(Widget sender) {
		Iterator idIt = ids.iterator();
		for (Iterator imageIt = widgets.iterator(); imageIt.hasNext();) {
			Image image = (Image) imageIt.next();
			String id = (String) idIt.next();

			if (image == sender) {
				return id;
			}
		}
		Window.alert("Failed to find id.");
		return null;
	}
}
