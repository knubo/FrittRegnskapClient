package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.misc.ImageFactory;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ShowMembershipView extends Composite implements ClickListener {

    static ShowMembershipView me;

    private FlexTable table;

    private HTML header;

    private HTML periodeHeader;

    private Image previousImage;

    private Image nextImage;

    private final I18NAccount messages;

    private final Constants constants;

    private final ViewCallback caller;
    
    private String action;

    public static ShowMembershipView show(I18NAccount messages,
            Constants constants, ViewCallback caller) {
        if (me == null) {
            me = new ShowMembershipView(messages, constants, caller);
        }
        return me;
    }

    public ShowMembershipView(I18NAccount messages, Constants constants,
            ViewCallback caller) {
        this.messages = messages;
        this.constants = constants;
        this.caller = caller;

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, messages.lastname());
        table.setHTML(0, 1, messages.firstname());
        table.setHTML(0, 2, "");
        table.getRowFormatter().setStyleName(0, "header");

        header = new HTML();
        periodeHeader = new HTML();
        previousImage = ImageFactory.previousImage();
        nextImage = ImageFactory.nextImage();
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(previousImage);
        hp.add(periodeHeader);
        hp.add(nextImage);

        DockPanel dp = new DockPanel();
        dp.add(header, DockPanel.NORTH);
        dp.add(hp, DockPanel.NORTH);
        dp.add(table, DockPanel.NORTH);

        initWidget(dp);
    }

    public void initShowMembers() {
        init();
        header.setHTML(messages.member_heading_year());
        action = "year";
    }

    public void initShowTrainingMembers() {
        init();
        header.setHTML(messages.member_heading_train());
        action = "training";
    }

    public void initShowClassMembers() {
        init();
        header.setHTML(messages.member_heading_course());
        action = "class";
    }

    private void init() {
        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }
    }

    public void onClick(Widget sender) {
        if(sender == previousImage) {
            
        } else if(sender == nextImage) {
            
        }
    }

}
