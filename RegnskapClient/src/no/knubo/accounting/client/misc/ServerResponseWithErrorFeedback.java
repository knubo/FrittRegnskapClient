package no.knubo.accounting.client.misc;

public interface ServerResponseWithErrorFeedback extends ServerResponse {

    public void onError();
}
