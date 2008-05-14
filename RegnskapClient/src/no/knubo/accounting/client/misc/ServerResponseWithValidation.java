package no.knubo.accounting.client.misc;

import java.util.List;

public interface ServerResponseWithValidation extends ServerResponse {

    public void validationError(List<String> fields);
}
