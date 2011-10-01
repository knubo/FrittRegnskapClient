package no.knubo.accounting.client.views.events;


public class EventInList {

    static int i = 42;

    public String getName() {
        return "Winter jump";
    }
    
    public String getStartDate() {
        return "20.09.2011";
    }

    public String getEndDate() {
        return "30.11.2011";
    }
    
    public String getEventDate() {
        return "31.11.2011";
    }


    public String getId() {
        return (i++)+"";
    }

}
