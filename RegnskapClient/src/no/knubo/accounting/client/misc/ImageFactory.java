package no.knubo.accounting.client.misc;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;

public class ImageFactory {

    public static Image previousImage(String id) {
        return withId(new Image("images/go-previous.png"), id);
    }

    public static Image nextImage(String id) {
        return withId(new Image("images/go-next.png"), id);
    }

    public static Image removeImage(String id) {
        return withId(new Image("images/list-remove.png"), id);
    }

    public static Image editImage(String id) {
        return withId(new Image("images/edit-find-replace.png"), id);
    }

    public static Image closeImage(String id) {
        return withId(new Image("images/close.png"), id);
    }
    
    public static Image searchImage(String id) {
        return withId(new Image("images/system-search.png"), id);
    }

    public static Image chooseImage(String id) {
        return withId(new Image("images/list-add.png"), id);
    }


    private static Image withId(Image image, String id) {
        DOM.setElementAttribute(image.getElement(), "id", id);
        return image;
    }
}
