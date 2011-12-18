qx.Class.define("frittregnskapmedlemsportal.Event", {
    extend: qx.core.Object,

    members: {
        __desktop : null,
        __win : null,
        __mainBox : null,
        __detailBox : null,
        __eventAccessor : null,
        changeToEventView : function(event) {
            var hide = new qx.fx.effect.core.Fade(this.__mainBox.getContainerElement().getDomElement());
            hide.start();
            
            this.__detailBox.show();

            this.loadDynamicView(event);
            
        },
        loadDynamicView : function(event) {
            var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_events.php?action=get&id="+event["id"], "GET", "application/json");

            req.addListener("completed", function(data){
                var json = data.getContent();

                try {
                    this.setupDynamicView(json);
                }
                catch (error) {
                    console.log(error);
                }
            }, this);

            req.send();
            
        },
        register: function() {
            var data = this.__eventAccessor.getData();

            var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_events.php?action=register", "POST", "application/json");
            req.setParameter("data", qx.lang.Json.stringify(data), true);
            
            req.addListener("completed", function(data) {
            
            });
            req.send();

        },
        setupDynamicView : function(event) {
            this.__detailBox.removeAll();

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
        
        fillEventList : function(events) {

            var mainBox =  new qx.ui.container.Composite(new qx.ui.layout.VBox(events.length));
            
            this.__mainBox = mainBox;
            this.__win.add(mainBox);

            for(var i=0;i<events.length;i++){
                var event = events[i];

                var eventbox = new qx.ui.groupbox.GroupBox(event["eventdesc"]);
                var gridLayout = new qx.ui.layout.Grid(2, 3);
                gridLayout.setSpacingY(10);
                eventbox.setLayout(gridLayout);

                var eventdate = new qx.util.format.DateFormat("dd.MM.yyyy", "no").format(this.fixDate(event.eventDate));
                eventbox.add(new qx.ui.basic.Label("Moroa starter: "+eventdate), {
                    row:0, 
                    column:0
                });

                var signUpButton = new qx.ui.form.Button("Meld meg p\u00E5");
                signUpButton.addListener("execute", function() {
                    this.changeToEventView(event);
                }, this);
                
                
                eventbox.add(signUpButton, {
                    row:1, 
                    column:0
                });

                mainBox.add(eventbox);

            }


        },
        fixDate: function(date){
            return new qx.util.format.DateFormat("yyyy-MM-dd", "no").parse(date);
        },
        
        loadEvents: function() {
            var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_events.php?action=list", "GET", "application/json");

            req.addListener("completed", function(data){
                var json = data.getContent();

                try {
                    this.fillEventList(json);
                    this.__win.setStatus("");
                }
                catch (error) {
                    console.log(error);
                }
            }, this);

            req.send();

        },
        setupDetailView : function() {
            var gridLayout = new qx.ui.layout.Grid(2, 3);
            gridLayout.setSpacingY(10);
        
            this.__detailBox = new qx.ui.container.Composite(gridLayout);
            this.__detailBox.hide();
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

            this.loadEvents();
            this.setupDetailView();
        },
        destruct: function() {
            this._disposeObjects("__win","__mainBox","__desktop", "__detailBox", "__eventAccessor");
        }
    }
});