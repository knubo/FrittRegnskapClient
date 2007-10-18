package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ListBoxWithErrorText;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AccountTrackEditView extends Composite {

    private static AccountTrackEditView me;
    private final I18NAccount messages;
    private final Constants constants;
    private final HelpPanel helpPanel;
    private ListBoxWithErrorText chosenList;

    public static AccountTrackEditView show(I18NAccount messages,
            Constants constants, HelpPanel helpPanel) {
        if (me == null) {
            me = new AccountTrackEditView(messages, constants, helpPanel);
        }
        me.setVisible(true);
        return me;
    }

    public AccountTrackEditView(I18NAccount messages, Constants constants,
            HelpPanel helpPanel) {
        this.messages = messages;
        this.constants = constants;
        this.helpPanel = helpPanel;

        chosenList = new ListBoxWithErrorText("accounttrack_chosen");
        chosenList.getListbox().setMultipleSelect(true);
        chosenList.getListbox().setVisibleItemCount(30);

        VerticalPanel vp = new VerticalPanel();
        vp.add(ImageFactory.chooseImageBig("accounttrack_add"));
        vp.add(ImageFactory.removeImageBig("accounttrack_remove"));

        DockPanel dp = new DockPanel();
        dp.add(chosenList, DockPanel.WEST);
        dp.add(vp, DockPanel.WEST);

        initWidget(dp);
    }

    public void init() {
        chosenList.getListbox().clear();

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "registers/accounttrack.php");

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(String responseText) {
                JSONValue value = JSONParser.parse(responseText);
                JSONArray posts = value.isArray();

                PosttypeCache posttypeCache = PosttypeCache.getInstance(
                        constants, messages);

                for (int i = 0; i < posts.size(); i++) {
                    JSONObject obj = posts.get(i).isObject();
                    String key = Util.str(obj.get("post"));

                    String desc = key + " " + posttypeCache.getDescription(key);
                    chosenList.getListbox().addItem(desc, key);
                }

                helpPanel.resize(me);
            }

        };

        try {
            builder.sendRequest("", new AuthResponder(constants, messages,
                    callback));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
    }

}
