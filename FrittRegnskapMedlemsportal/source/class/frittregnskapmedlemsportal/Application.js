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
            if (qx.core.Variant.isSet("qx.debug", "on")) {
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
            }
            else {

                login.setupLoginWindow();
            }
            
        }
        
    }
});
