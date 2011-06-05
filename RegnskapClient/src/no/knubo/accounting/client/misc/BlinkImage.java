package no.knubo.accounting.client.misc;

import no.knubo.accounting.client.Util;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

public class BlinkImage extends Image {

    private final String imageOne;
    private final String imageTwo;

    BlinkImage(String imageOne, String imageTwo) {
        super(imageOne);
        this.imageOne = imageOne;
        this.imageTwo = imageTwo;
    }

    public void blinkOne() {
        internalBlink(imageOne, imageTwo);
    }

    public void blinkTwo() {
        internalBlink(imageTwo, imageOne);
    }

    private void internalBlink(final String blink1, final String blink2) {
        final Timer timer = new Timer() {
            int count = 0;
            @Override
            public void run() {
                if(count ++ > 7) {
                    cancel();
                    return;
                }
                if(count % 2 == 0) {
                    setUrl(blink1);
                } else {
                    setUrl(blink2);
                }
            }
        };
        timer.scheduleRepeating(1 * 600);
    }
    
}
