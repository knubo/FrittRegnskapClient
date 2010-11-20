
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
            
            
            req.addListener("completed", function(data){
                var json = data.getContent();
                
                if (json["error"]) {
                    this.__errorLabel.setValue(json["error"]);
                }
                else 
                    if (json["result"] == "ok") {
                    
                        document.getElementById("loginform").submit();
                    }
            },this);
            
            req.send();
        },
        
        sendOneTimeLink: function(email, infoLabel){
            var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_authenticate.php?action=connect&email=" + email.getValue(), "GET", "application/json");
            
            req.addListener("completed", function(data){
                var json = data.getContent();
                
                if (json["error"]) {
                    infoLabel.setTextColor("red");
                    infoLabel.setValue(json["error"]);
                }
                else 
                    if (json["status"] == "ok") {
                        infoLabel.setTextColor("green");
                        infoLabel.setValue("Epost med engagslink er sendt til inngitt epostadresse.");
                    }
                    else {
                        infoLabel.setTextColor("red");
                        infoLabel.setValue("Klarte ikke sende engangslink.");
                    }
            });
            
            req.send();
            
            
        },
        
        
        newPassword: function(){
            var popup = new qx.ui.popup.Popup(new qx.ui.layout.Grow());
            
            var box = new qx.ui.groupbox.GroupBox("Nytt passord");
            popup.add(box);
            
            
            var gridLayout = new qx.ui.layout.Grid(2, 3);
            gridLayout.setSpacingY(10);
            box.setLayout(gridLayout);
            
            
            var label = new qx.ui.basic.Label();
            label.setRich(true);
            label.setValue("Gi inn epostadressen som du er registrert med og en engangslink blir sendt til epostadressen. Etter innlogging kan du bytte passord.<br/>");
            
            box.add(label, {
                row: 0,
                column: 0,
                colSpan: 2
            });
            
            box.add(new qx.ui.basic.Label("Epostadresse:"), {
                row: 1,
                column: 0
            });
            
            var email = new qx.ui.form.TextField("");
            email.setWidth(200);
            box.add(email, {
                row: 1,
                column: 1
            });
            
            var infoLabel = new qx.ui.basic.Label();
            infoLabel.setAllowStretchY(true);
            infoLabel.setRich(true);
            
            box.add(infoLabel, {
                row: 2,
                column: 0,
                colSpan: 2
            });
            
            var sendButton = new qx.ui.form.Button("Send engangslink");
            box.add(sendButton, {
                row: 3,
                column: 0
            });
            
            sendButton.addListener("execute", function(){
                this.sendOneTimeLink(email, infoLabel);
            }, this);
            
            
            var cancelButton = new qx.ui.form.Button("Lukk");
            cancelButton.setAllowStretchX(false);
            
            cancelButton.addListener("execute", function(){
                popup.hide();
            }, this);
            
            box.add(cancelButton, {
                row: 3,
                column: 1
            })
            
            popup.placeToWidget(this.__newUserButton);
            popup.show();
            email.focus();
            
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
