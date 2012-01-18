package no.knubo.accounting.client.views.files;

public interface UploadDelegateCallback {

    void uploadComplete();
    
    boolean uploadBody(String body);

    void preUpload();
    
}
