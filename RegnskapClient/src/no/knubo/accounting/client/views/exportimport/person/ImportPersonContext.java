package no.knubo.accounting.client.views.exportimport.person;

import net.binarymuse.gwt.client.ui.wizard.WizardContext;

public class ImportPersonContext extends WizardContext {

    private boolean complete;

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public void reset() {
        
    }

}
