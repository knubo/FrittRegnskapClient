qx.Class.define("frittregnskapmedlemsportal.EventSignup", {
    extend: qx.core.Object,

    members: {
        __desktop : null,
        __win : null,
        __mainBox : null,
        __detailBox : null,
        __eventAccessor : null,
        register: function() {
            
            var grid = new qx.ui.layout.Grid(6, 20);
            grid.setSpacing(5);
            
            var popup = new qx.ui.popup.Popup(grid).set({
                // backgroundColor: "#FFFAD3",
                padding: [2, 4],
                offset : 3,
                offsetBottom : 20
            });
            
            var header = new qx.ui.basic.Label("Din påmelding");

            
            popup.add(header, {
                row:0, 
                column:0, 
                colSpan:10
            });
            
            var rowlayout = new qx.ui.layout.Flow();
            rowlayout.setSpacingX(7);
            var buttonrow = new qx.ui.container.Composite(rowlayout);           
            
            var registerButton = new qx.ui.form.Button("Meld meg p\u00E5");
            registerButton.setAllowStretchX(false);
            registerButton.addListener("execute", function() {                 
                popup.hide();
                this.doRegister();
            }, this);
            
            var abortButton = new qx.ui.form.Button("Avbryt");
            abortButton.setAllowStretchX(false);
            abortButton.addListener("execute", function() {
                popup.hide();           
            }, this);
            
            buttonrow.add(registerButton);
            buttonrow.add(abortButton);
            
            popup.add(buttonrow, {
                row:1, 
                column:0, 
                colSpan:10
            });
            popup.setAutoHide(false);
            
            popup.show();
           
        },
        doRegister: function() {
            var data = this.__eventAccessor.getData();

            var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_events.php?action=register", "POST", "application/json");
            req.setParameter("data", qx.lang.Json.stringify(data), true);
            
            req.addListener("completed", function(data) {
            
            });
            req.send();

        },
        setupRegisterView : function(event) {

            this.__win.setCaption(event.name);
            
            var grid = new qx.ui.layout.Grid(6, 20);
            grid.setSpacing(5);
            var table = new qx.ui.container.Composite(grid);            
            
            var header = new qx.ui.basic.Label();
            header.setRich(true);
            header.setValue(event.headerHTML);
            this.__detailBox.add(header, {
                row:0, 
                column:0
            });
            this.__detailBox.add(table, {
                row:1, 
                column:0
            });
            
            var rowlayout = new qx.ui.layout.Flow();
            rowlayout.setSpacingX(7);
            var buttonrow = new qx.ui.container.Composite(rowlayout);
            
            
            var registerButton = new qx.ui.form.Button("Meld meg p\u00E5");
            registerButton.setAllowStretchX(false);
            registerButton.addListener("execute", function() {
                this.register();
            }, this);
            
            var abortButton = new qx.ui.form.Button("Avbryt");
            abortButton.setAllowStretchX(false);
            abortButton.addListener("execute", function() {
                this.__win.hide();
            }, this);
            
            buttonrow.add(registerButton);
            buttonrow.add(abortButton);
            
            this.__detailBox.add(buttonrow, {
                row:2, 
                column:0, 
                colSpan:10
            });


            var hrow;
            var hcol;
            
            var accessor = new frittregnskapmedlemsportal.EventAccesser();
            this.__eventAccessor = accessor;
            
            accessor.setEventObject(event);
            
            for(hrow = 0; hrow < 20; hrow++) {
                for(hcol = 0; hcol < 10; hcol++) {
                    if(accessor.hasWidget(hrow, hcol)) {
                        table.add(accessor.getWidget(hrow, hcol), {
                            row:hrow, 
                            column:hcol
                        });
                    }
                }
            }
            
        },
        fixDate: function(date){
            return new qx.util.format.DateFormat("yyyy-MM-dd", "no").parse(date);
        },
        setupDetailView : function() {
            var gridLayout = new qx.ui.layout.Grid(2, 3);
            gridLayout.setSpacingY(10);
        
            this.__detailBox = new qx.ui.container.Composite(gridLayout);
            this.__win.add(this.__detailBox);
            
        },
        setupView: function(desktop) {
            this.__desktop = desktop;
            // Create the Window
            this.__win = new qx.ui.window.Window("Arrangementer", "frittregnskapmedlemsportal/calendar.png");
            var win = this.__win;
            
            var winLayout = new qx.ui.layout.Flow();
            win.setLayout(winLayout);
            win.setShowStatusbar(true);
            win.setStatus("");
            win.setShowClose(false);
            win.setShowMinimize(false);
            win.setShowMaximize(false);
            win.setAllowMaximize(false);

            desktop.add(win);
            win.open();

     
            this.setupDetailView();
        },
        destruct: function() {
            this._disposeObjects("__win","__mainBox","__desktop", "__detailBox", "__eventAccessor");
        }
    }
});