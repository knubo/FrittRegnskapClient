package no.knubo.accounting.client.misc;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;

public class ImageFactory {

    public static Image previousImage(String name) {
        return withName(new Image("images/go-previous.png"), name);
    }

    public static Image nextImage(String name) {
        return withName(new Image("images/go-next.png"), name);
    }

    public static Image removeImage(String name) {
        return withName(new Image("images/list-remove.png"), name);
    }

    public static Image editImage(String name) {
        return withName(new Image("images/edit-find-replace.png"), name);
    }

    public static Image closeImage(String name) {
        return withName(new Image("images/close.png"), name);
    }

    private static Image withName(Image image, String name) {
        DOM.setElementAttribute(image.getElement(), "name", name);
        return image;
    }
}
