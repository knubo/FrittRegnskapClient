package no.knubo.accounting.client.misc;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ImageFactory {

    public static Image previousImage(String id) {
        return withId(new Image("images/go-previous.png"), id);
    }

    public static Image nextImage(String id) {
        return withId(new Image("images/go-next.png"), id);
    }

    public static Image previousImageBig(String id) {
        return withId(new Image("images/go-previous-big.png"), id);
    }

    public static Image nextImageBig(String id) {
        return withId(new Image("images/go-next-big.png"), id);
    }

    public static Image removeImage(String id) {
        return withId(new Image("images/list-remove.png"), id);
    }

    public static Widget removeImageBig(String id) {
        return withId(new Image("images/list-remove-big.png"), id);
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

    public static Widget chooseImageBig(String id) {
        return withId(new Image("images/list-add-big.png"), id);
    }

    public static Image deleteImage(String id) {
        return withId(new Image("images/edit-delete.png"), id);
    }

    public static Image loadingImage(String id) {
        return withId(new Image("images/ajax-loader.gif"), id);
    }

    public static Image blankImage(int sizex, int sizey) {
        Image img = new Image("images/blank.gif");
        img.setSize(sizex + "px", sizey + "px");
        return img;
    }

    private static Image withId(Image image, String id) {
        DOM.setElementAttribute(image.getElement(), "id", id);
        return image;
    }

}
