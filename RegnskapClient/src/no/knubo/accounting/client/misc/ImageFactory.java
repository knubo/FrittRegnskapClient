package no.knubo.accounting.client.misc;

import com.google.gwt.user.client.ui.Image;

public class ImageFactory {

    public static Image previousImage() {
        return new Image("images/go-previous.png");
    }

    public static Image nextImage() {
        return new Image("images/go-next.png");
    }

    public static Image removeImage() {
        return new Image("images/list-remove.png");
    }

    public static Image editImage() {
        return new Image("images/edit-find-replace.png");
    }

    public static Image closeImage() {
        return new Image("images/close.png");
    }
}
