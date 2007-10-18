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
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AccountTrackEditView extends Composite implements ClickListener {

    private static AccountTrackEditView me;
    private final I18NAccount messages;
    private final Constants constants;
    private final HelpPanel helpPanel;
    private ListBoxWithErrorText chosenList;
    private ListBoxWithErrorText availableList;
    private Image nextImageBig;
    private Image previousImageBig;

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

        chosenList = new ListBoxWithErrorText("accounttrack_chosen", true);
        chosenList.getListbox().setVisibleItemCount(30);

        availableList = new ListBoxWithErrorText("accounttrack_available", true);
        availableList.getListbox().setVisibleItemCount(30);

        VerticalPanel vpImages = new VerticalPanel();
        nextImageBig = ImageFactory.nextImageBig("accounttrack_add");
        nextImageBig.addClickListener(this);
        vpImages.add(nextImageBig);
        previousImageBig = ImageFactory.previousImageBig("accounttrack_remove");
        previousImageBig.addClickListener(this);
        vpImages.add(previousImageBig);
        vpImages.setStyleName("accounttrack_midle");
        DockPanel dp = new DockPanel();
        
        VerticalPanel vpChosen = new VerticalPanel();
        vpChosen.add(new Label(messages.chosen_accounts()));
        vpChosen.add(chosenList);
        
        VerticalPanel vpAvailable = new VerticalPanel();
        vpAvailable.add(new Label(messages.available_accounts()));
        vpAvailable.add(availableList);
        
        dp.add(vpChosen, DockPanel.WEST);
        dp.add(vpImages, DockPanel.WEST);
        dp.add(vpAvailable, DockPanel.WEST);

        initWidget(dp);
    }

    public void init() {
        chosenList.getListbox().clear();
        availableList.getListbox().clear();

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
                fillAvailableList();

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

    protected void fillAvailableList() {
        PosttypeCache postTypeCache = PosttypeCache.getInstance(constants,
                messages);

        postTypeCache.fillAllPosts(availableList.getListbox(), chosenList
                .getListbox(), false, true);
    }

    public void onClick(Widget sender) {
        if (sender == nextImageBig) {
            JSONArray change = moveElements(chosenList.getListbox(),
                    availableList.getListbox());
            sendChange("remove", change);
        } else if (sender == previousImageBig) {
            JSONArray change = moveElements(availableList.getListbox(),
                    chosenList.getListbox());
            sendChange("add", change);
        }
    }

    private void sendChange(String action, JSONArray change) {

        if(change.size() == 0) {
            Window.alert(messages.save_failed());
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("action=" + action);
        Util.addPostParam(sb, "values", change.toString());

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                constants.baseurl() + "registers/accounttrack.php");

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(String serverResponse) {
                JSONValue parse = JSONParser.parse(serverResponse);
                if (parse == null) {
                    Window.alert(messages.save_failed_badly());
                    init();
                    return;
                }
                JSONObject object = parse.isObject();

                if (object == null
                        || "0".equals(Util.str(object.get("result")))) {
                    Window.alert(messages.save_failed_badly());
                    init();
                }
            }
        };

        try {
            builder.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            builder.sendRequest(sb.toString(), new AuthResponder(constants,
                    messages, callback));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }

    }

    private JSONArray moveElements(ListBox fromList, ListBox toList) {
        JSONArray array = new JSONArray();
        int pos = 0;

        for (int i = fromList.getItemCount(); i-- > 0;) {
            if (fromList.isItemSelected(i)) {
                String text = fromList.getItemText(i);
                String value = fromList.getValue(i);

                array.set(pos++, new JSONString(value));
                toList.addItem(text, value);
                fromList.setItemSelected(i, false);
                fromList.removeItem(i);
            }
        }
        return array;
    }
}
