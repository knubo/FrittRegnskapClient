
qx.Class.define("frittregnskapmedlemsportal.Login", {
    extend: qx.core.Object,
    
    members: {
    
        setupLoginIfNeeded: function(){
            var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_authenticate.php?action=sessionvalid", "GET", "application/json");
            req.setAsynchronous(false);
            
            var loginNeeded = false;
            
            req.addListener("failed", function(t){
                loginNeeded = true;
            });
            
            req.send();
            
            return loginNeeded;
        },
        
        __loginButton: null,
        __newUserButton: null,
        __errorLabel: null,
        
        doLogin: function(){
            var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_authenticate.php?action=login", "POST", "application/json");
            
            var userField = document.getElementById("userfield");
            var passwordField = document.getElementById("passwordfield");
            
            req.setParameter("user", userField.value, true);
            req.setParameter("password", passwordField.value, true);
            
            var owner = this;
            
            req.addListener("completed", function(data){
                var json = data.getContent();
                
                if (json["error"]) {
                    owner.__errorLabel.setValue("Feil brukernavn(epost) eller passord.");
                }
                else 
                    if (json["result"] == "ok") {
                    
                        document.getElementById("loginform").submit();
                    }
            });
            
            req.send();
        },
        
        newPassword: function(){
        },
        
        __prepareEffect: function(){
            var userField = document.getElementById("userfield");
            
            userField.focus();
        },
        
        setupLoginWindow: function(){
        
            var htmlElement = document.getElementById("loginbuttons");
            var inlineIsle = new qx.ui.root.Inline(htmlElement, false, false);
            inlineIsle.setLayout(new qx.ui.layout.Grid());
            inlineIsle.setBackgroundColor("white");
            
            this.__loginButton = new qx.ui.form.Button("Logg inn");
            this.__loginButton.id = "login_button_id";
            this.__loginButton.setAllowStretchX(false);
            this.__loginButton.setMarginRight(10);
            
            
            inlineIsle.add(this.__loginButton, {
                row: 0,
                column: 0
            });
            
            /* Check input on click */
            this.__loginButton.addListener("execute", this.doLogin, this);
            
            this.__newUserButton = new qx.ui.form.Button("Ny bruker/Glemt passord");
            this.__newUserButton.setAllowStretchX(false);
            
            inlineIsle.add(this.__newUserButton, {
                row: 0,
                column: 1
            });
            
            this.__errorLabel = new qx.ui.basic.Label("");
            this.__errorLabel.setTextColor("red");
            this.__errorLabel.setBackgroundColor("white");
            
            inlineIsle.add(this.__errorLabel, {
                row: 1,
                column: 0,
                colSpan: 3
            })
            
            /* Check input on click */
            this.__newUserButton.addListener("execute", this.newPassword, this);
            
            
            /* Prepare effect as soon as the container is ready */
            inlineIsle.addListener("appear", this.__prepareEffect, this);
            
            var userField = document.getElementById("userfield");
            var passwordField = document.getElementById("passwordfield");
            
            var me = this;
            
            
            var catchEnter = function(e){
                var charCode;
                
                if (e && e.which) {
                    charCode = e.which;
                }
                else 
                    if (window.event) {
                        e = window.event;
                        charCode = e.keyCode;
                    }
                
                if (charCode == 13) {
                    me.doLogin();
                }
            };
            userField.onkeypress = catchEnter;
            passwordField.onkeypress = catchEnter;
        }
    },
    destruct: function(){
        this._disposeObjects("__loginButton", "__newUserButton", "__errorLabel");
    }
    
});
