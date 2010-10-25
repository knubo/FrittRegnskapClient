qx.Class.define("frittregnskapmedlemsportal.Membersearch", {
	extend : qx.core.Object,

	__win: null,
	members : {
		setupView : function(desktop) {
        // Create the Window
        this.__win = new qx.ui.window.Window("Medlemmer", "frittregnskapmedlemsportal/system-search.png");
        var win = this.__win;
        win.setShowStatusbar(true);
        win.setStatus("");
        win.setShowClose(false);
        win.setShowMinimize(false);
        win.setShowMaximize(false);
        win.setResizable(false);
        win.setAllowMaximize(false);
	    
        desktop.add(win);
        win.open();

        
		},
		destruct : function() {
			this._disposeObjects("__win");
		}

	}
});
