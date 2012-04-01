/* ************************************************************************
 Copyright:
 License:
 Authors:
 ************************************************************************ */
/* ************************************************************************
 #asset(frittregnskapmedlemsportal/*)
 ************************************************************************ */
/**
 * This is the main application class of your custom application "FrittRegnskapMedlemsportal"
 */
qx.Class.define("frittregnskapmedlemsportal.Application", {
    extend: qx.application.Inline,
    
    
    
    /*
     *****************************************************************************
     MEMBERS
     *****************************************************************************
     */
    members: {
        __portalEnabled : 0,
        
        checkPortalStatus: function(){
            var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_authenticate.php?action=status", "GET", "application/json");
            req.setAsynchronous(false);
            
            var loginNeeded = false;
            
            req.addListener("completed", function(data){
                var json = data.getContent();
                
                if (json["portal_status"] == 0 || json["portal_status"] == 4) {
                    document.getElementById("allLoginStuff").style.display = "none";
                    document.getElementById("applicationStuff").style.display = "block";
                    document.getElementById("isle").innerHTML = "Portalen for dette domenet er ikke aktivert";
                }
                else 
                    if (json["portal_status"] == 2) {
                        document.getElementById("applicationStuff").style.display = "block";
                        document.getElementById("allLoginStuff").style.display = "none";
                        document.getElementById("isle").innerHTML = "Portalen er stengt grunnet manglende betaling.";
                    }
                    else 
                        if (json["portal_status"] == 3) {
                            document.getElementById("applicationStuff").style.display = "block";
                            document.getElementById("allLoginStuff").style.display = "none";
                            document.getElementById("isle").innerHTML = "Portalen er stengt av klubbens administrator.";
                        }
                        else 
                            if (json["portal_status"] == 1) {
                                document.getElementById("maintitle1").innerHTML = json["portal_title"];
                                document.getElementById("maintitle2").innerHTML = json["portal_title"];
                                this.setPortalEnabled(json["eventEnabled"]);
                                this.setupApplication();
                            }
            }, this);
            
            req.send();
        },
        
        /**
         * This method contains the initial application code and gets called
         * during startup of the application
         *
         * @lint ignoreDeprecated(alert)
         */
        main: function(){
            // Call super class
            this.base(arguments);
            
            // Enable logging in debug variant
            if ((qx.core.Environment.get("qx.debug"))) {
                // support native logging capabilities, e.g. Firebug for Firefox
                qx.log.appender.Native;
                // support additional cross-browser console. Press F7 to toggle visibility
                qx.log.appender.Console;
            }
            
            /*
             -------------------------------------------------------------------------
             Below is your actual application code...
             -------------------------------------------------------------------------
             */
            /*
             -------------------------------------------------------------------------
             USE AN EXISTING NODE TO ADD WIDGETS INTO THE PAGE LAYOUT FLOW
             -------------------------------------------------------------------------
             */
            this.checkPortalStatus();
        },
        setPortalEnabled: function(stat) {
            this.__portalEnabled = stat;
        },
        setupApplication: function(){
        
            var login = new frittregnskapmedlemsportal.Login();
            
            if (!login.setupLoginIfNeeded()) {
                document.getElementById("allLoginStuff").style.display = "none";
                document.getElementById("applicationStuff").style.display = "block";
                
                // Hint: the second and the third parameter control if the dimensions
                // of the element should be respected or not.
                var htmlElement = document.getElementById("isle");
                var inlineIsle = new qx.ui.root.Inline(htmlElement, true, true);
                
                
                // use VBox layout instead of basic
                inlineIsle.setLayout(new qx.ui.layout.Grow());
                
                var windowManager = new qx.ui.window.Manager();
                var desktop = new qx.ui.window.Desktop(windowManager);
                desktop.set({
                    decorator: "main",
                    backgroundColor: "background-pane"
                });
                
                inlineIsle.add(desktop);
                new frittregnskapmedlemsportal.Membersearch().setupView(desktop);
                new frittregnskapmedlemsportal.Profile().createWindowProfile(desktop);
                
                if(this.__portalEnabled) {
                   new frittregnskapmedlemsportal.Events().setupView(desktop);                    
                }
            }
            else {            
                login.setupLoginWindow();
                document.getElementById("applicationStuff").style.display = "none";

            }
            
        }
        
    }
});
