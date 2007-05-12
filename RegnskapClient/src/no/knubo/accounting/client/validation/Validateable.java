package no.knubo.accounting.client.validation;

public interface Validateable {

	public void setErrorText(String text);

	public String getText();

	public void setFocus(boolean b);

    public void setMouseOver(String mouseOver);
}
