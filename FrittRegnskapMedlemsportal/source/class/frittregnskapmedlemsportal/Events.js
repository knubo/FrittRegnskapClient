qx.Class.define("frittregnskapmedlemsportal.Events", {
    extend: qx.core.Object,

    members: {
        __desktop : null,
        __win : null,
        __mainBox : null,
        changeToEventView : function(event) {
            var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_events.php?action=get&id="+event["id"], "GET", "application/json");

            req.addListener("completed", function(data){
                var json = data.getContent();

                try {
                    var signup = new frittregnskapmedlemsportal.EventSignup();
                    signup.setupView(this.__desktop);
                    signup.setupRegisterView(json);
                }
                catch (error) {
                    console.log(error);
                }
            }, this);

            req.send();
            
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
                
                var eventId = event["id"];
                signUpButton.addListener("execute", function() {
                    this.t.changeToEventView(this.e);
                    
                }, {t:this, e:event});
                
                
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
        },
        destruct: function() {
            this._disposeObjects("__win","__mainBox","__desktop", "__detailBox");
        }
    }
});