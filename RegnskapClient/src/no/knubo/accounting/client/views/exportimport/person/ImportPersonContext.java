package no.knubo.accounting.client.views.exportimport.person;

import net.binarymuse.gwt.client.ui.wizard.WizardContext;

import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;

public class ImportPersonContext extends WizardContext {

    private boolean complete;
    final FormPanel form;
    final Hidden hiddenAction;
    final Hidden hiddenExclude;

    public ImportPersonContext(FormPanel form, Hidden hiddenAction, Hidden hiddenExclude) {
        this.form = form;
        this.hiddenAction = hiddenAction;
        this.hiddenExclude = hiddenExclude;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public void reset() {
        /* Not used */
    }

    public void submit() {
        form.submit();        
    }

}
