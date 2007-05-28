package no.knubo.accounting.client.views;

public interface ViewCallback {

    /**
     * Requests that detail vindow is opened.
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
    public void viewMonth(String year, String month);

    /**
     * Request edit of the person with the given id.
     * 
     * @param id
     */
    public void editPerson(String id);

    /** Request that view of current month and year. */
    public void viewMonth();
}
