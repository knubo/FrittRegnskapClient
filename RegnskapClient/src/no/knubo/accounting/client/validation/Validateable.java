package no.knubo.accounting.client.validation;

public interface Validateable {

    public String getText();

    /**
     * Sets error to be displayed if error occures.
     * 
     * @param text
     *            The text.
     */
    public void setErrorText(String text);

    public void setFocus(boolean b);

    public void setMouseOver(String mouseOver);
}
