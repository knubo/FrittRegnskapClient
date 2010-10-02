
qx.Class.define("frittregnskapmedlemsportal.Login", {
	extend : qx.core.Object,

	members : {

		setupLoginIfNeeded : function() {
			var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_authenticate.php?action=sessionvalid", "GET",
					"application/json");
			req.setAsynchronous(false);

			var loginNeeded = false;

			req.addListener("failed", function(t) {
				loginNeeded = true;
			});

			req.send();

			return loginNeeded;
		},

		__loginButton : null,
		__newUserButton: null,

		doLogin : function() {
			var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_authenticate.php?action=login", "POST",
				"application/json");
			
            var userField = document.getElementById("userfield");
            var passwordField = document.getElementById("passwordfield");
			
			req.setParameter("user", userField.value, true);
			req.setParameter("password", passwordField.value , true);
			
			var owner = this;
			
			req.addListener("completed", function(data) {
				var json = data.getContent();

				if(json["error"]) {
                    window.alert("Error");
				} else if(json["result"] == "ok") {
                    document.getElementById("loginform").submit();
				}
			});
			
			req.send();
		},
		
		newPassword : function() {
		},

		__prepareEffect : function() {
            var userField = document.getElementById("userfield");

            userField.focus();
		},

		setupLoginWindow : function(desktop) {
            
            var htmlElement = document.getElementById("loginbuttons");
            var inlineIsle = new qx.ui.root.Inline(htmlElement, true, true);
            inlineIsle.setLayout(new qx.ui.layout.Flow());
            
            
			this.__loginButton = new qx.ui.form.Button("Logg inn");
			this.__loginButton.setAllowStretchX(false);

			inlineIsle.add(this.__loginButton);

			/* Check input on click */
			this.__loginButton.addListener("execute", this.doLogin, this);
			
			this.__newUserButton = new qx.ui.form.Button("Ny bruker/Glemt passord");
			this.__newUserButton.setAllowStretchX(false);
			
			inlineIsle.add(this.__newUserButton);
			
			/* Check input on click */
			this.__newUserButton.addListener("execute", this.newPassword, this);

		
			/* Prepare effect as soon as the container is ready */
			inlineIsle.addListener("appear", this.__prepareEffect, this);

		}
	},
	destruct : function() {
		this._disposeObjects("__loginButton", "__newUserButton");
	}

});