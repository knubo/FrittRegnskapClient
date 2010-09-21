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

		__effect : null,
		__container : null,
		__loginButton : null,
		__newUserButton: null,
		__loginName:null,
		__password:null,

		doLogin : function() {
			var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_authenticate.php?action=login", "POST",
				"application/json");
			req.setParameter("user", this.__loginName.getValue(), true);
			req.setParameter("password", this.__password.getValue() , true);
			
			var owner = this;
			
			req.addListener("completed", function(data) {
				var json = data.getContent();

				if(json["error"]) {
					owner.__effect.start();
				} else if(json["result"] == "ok") {
					window.alert("Login OK");
				}
			});
			
			req.send();
		},
		
		newPassword : function() {
		},

		__prepareEffect : function() {
			this.__effect = new qx.fx.effect.combination.Shake(this.__container.getContainerElement().getDomElement());

			this.__loginName.focus();
		},

		setupLoginWindow : function(desktop) {

			this.__loginButton = new qx.ui.form.Button("Logg inn");
			this.__loginButton.setAllowStretchX(false);

			this.__container.add(this.__loginButton, {
				row : 3,
				column : 1
			});

			/* Check input on click */
			this.__loginButton.addListener("execute", this.doLogin, this);
			
			this.__newUserButton = new qx.ui.form.Button("Ny bruker/Glemt passord");
			this.__newUserButton.setAllowStretchX(false);
			
			this.__container.add(this.__newUserButton, {
				row : 4,
				column : 1
			});
			
			/* Check input on click */
			this.__newUserButton.addListener("execute", this.newPassword, this);

		
			/* Prepare effect as soon as the container is ready */
			this.__container.addListener("appear", this.__prepareEffect, this);

		}
	},
	destruct : function() {
		this._disposeObjects("__container", "__loginButton", "__effect", "__newUserButton", "__loginName", "__password");
	}

});