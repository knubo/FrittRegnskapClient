package no.knubo.accounting.client.views;

public interface ViewCallback {

	/** Requests that detail vindow is opened. 
	 * 
	 * @param id
	 */
	public void openDetails(String id);

	/** Request view of the given month and year. 
	 * 
	 * @param year
	 * @param month
	 */
	public void viewMonth(String year, String month);
}