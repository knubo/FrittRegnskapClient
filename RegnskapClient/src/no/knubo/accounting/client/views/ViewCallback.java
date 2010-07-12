package no.knubo.accounting.client.views;

import no.knubo.accounting.client.misc.WidgetIds;

public interface ViewCallback {

    /**
     * Requests that detail window for a post is opened.
     * 
     * @param id
     */
    public void openDetails(String id);

    /**
     * Request view of the given month and year.
     * 
     * @param year
     * @param month
     */
    public void viewMonth(int year, int month);

    /**
     * Request edit of the person with the given id.
     * 
     * @param id
     */
    public void editPerson(String id);

    /** Request that view of current month and year. */
    public void viewMonth();

    /**
     * Request view of person search.
     */
    public void searchPerson();

    public void openView(WidgetIds view, String title);

    public void openMassletterEditSimple(String filename, String response);
}
