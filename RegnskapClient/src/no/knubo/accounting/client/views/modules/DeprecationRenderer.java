package no.knubo.accounting.client.views.modules;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.ui.AccountTable;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public class DeprecationRenderer {

    private AccountTable table;

    public AccountTable getTable() {
        return table;
    }

    public DeprecationRenderer() {
        table = new AccountTable("tableborder");
    }
    
    public void display(JSONArray data, Constants constants, I18NAccount messages,Elements elements) {
        
        PosttypeCache posttypeCache = PosttypeCache.getInstance(constants, messages);
        
        table.setText(0, 0, elements.deprecation());
        table.setColSpanAndRowStyle(0,0,4,"header");
        
        table.setText(1, 0, elements.owning());
        table.setText(1, 1, elements.owning_account());
        table.setColSpanAndRowStyle(1, 1, 2, "desc");
        table.setText(1, 2, elements.amount());
        table.setHeaderRowStyle(1);
        
        for(int i = 0; i < data.size(); i++) {
            int row = i+2;
            JSONObject obj = data.get(i).isObject();
            
            table.setText(row, 0, Util.str(obj.get("belonging")),"desc");
            String account = Util.str(obj.get("owning_account"));
            table.setText(row, 1, account,"desc");
            table.setText(row, 2, posttypeCache.getDescription(account),"desc");
            table.setText(row, 3, Util.money(obj.get("deprecation_amount")),"right desc");
            table.alternateStyle(row, (row+1) % 2 == 0);
        }
    }

}
