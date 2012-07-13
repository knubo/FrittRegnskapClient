package no.knubo.accounting.client;

import java.util.HashSet;

import no.knubo.accounting.client.invoice.RegisterInvoiceChooseInvoiceTypePage;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.storage.client.Storage;

public class RegnskapLocalStorage {

    public static void saveInvoices(JSONArray arr) {
        Storage storage = Storage.getLocalStorageIfSupported();
        storage.setItem(RegisterInvoiceChooseInvoiceTypePage.INVOICES_KEY, arr.toString());
    }

    public static String getInvoices() {
        Storage storage = Storage.getLocalStorageIfSupported();

        return storage.getItem(RegisterInvoiceChooseInvoiceTypePage.INVOICES_KEY);
    }

    public static void removePerson(String personId) {
        Storage storage = Storage.getLocalStorageIfSupported();
        storage.removeItem(personId);
    }

    public static void savePerson(String id, JSONObject person) {
        Storage storage = Storage.getLocalStorageIfSupported();

        storage.setItem("person_" + id, person.toString());
    }

    public static void removeInvoicesData() {
        Storage storage = Storage.getLocalStorageIfSupported();

        HashSet<String> keysToRemove = new HashSet<String>();
        int length = storage.getLength();

        for (int i = 0; i < length; i++) {
            String key = storage.key(i);

            if (key.startsWith("person")) {
                keysToRemove.add(key);
            }
        }

        storage.removeItem(RegisterInvoiceChooseInvoiceTypePage.INVOICES_KEY);

        for (String key : keysToRemove) {
            storage.removeItem(key);
        }
    }

    public static StringBuffer getInvoiceRecivers() {
        Storage storage = Storage.getLocalStorageIfSupported();
        int length = storage.getLength();
        StringBuffer receivers = new StringBuffer();
        receivers.append("[");

        boolean added = false;
        for (int i = 0; i < length; i++) {
            String key = storage.key(i);
            String value = storage.getItem(key);
            Util.log("Key="+key+" value="+value);

            if (key.startsWith("person")) {
                if (added) {
                    receivers.append(",");
                } else {
                    added = true;
                }
                receivers.append(value);
            }
        }

        receivers.append("]");
        
        return receivers;
    }

    public static JSONArray getInvoiceReciversAsJSONArray() {
        return JSONParser.parseStrict(getInvoiceRecivers().toString()).isArray();
    }

    public static int getInvoiceCount() {
        JSONArray invoices = JSONParser.parseStrict(getInvoices()).isArray();
        
        return invoices.size();
    }
}
