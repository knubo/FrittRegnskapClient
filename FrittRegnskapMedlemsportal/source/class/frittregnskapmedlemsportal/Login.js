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

		doLogin : function() {
			this.__effect.start();
		},
		
		newPassword : function() {
		},

		__prepareEffect : function() {
			this.__effect = new qx.fx.effect.combination.Shake(this.__container.getContainerElement().getDomElement());
		},

		setupLoginWindow : function(desktop) {

			var layout = new qx.ui.layout.Grid(9, 5);
			layout.setColumnAlign(0, "right", "top");
			layout.setColumnAlign(2, "right", "top");

			/* Container widget */
			this.__container = new qx.ui.groupbox.GroupBox().set( {
				contentPadding : [ 16, 16, 16, 16 ]
			});

			this.__container.setLayout(layout);

			this.__container.addListener("resize", function(e) {
				var bounds = this.__container.getBounds();
				this.__container.set( {
					marginTop : Math.round(-bounds.height / 2),
					marginLeft : Math.round(-bounds.width / 2)
				});
			}, this);

			desktop.add(this.__container, {
				left : "50%",
				top : "30%"
			});

			var labels = [ "Navn", "Passord" ];
			for ( var i = 0; i < labels.length; i++) {
				this.__container.add(new qx.ui.basic.Label(labels[i]).set( {
					allowShrinkX : false,
					paddingTop : 3
				}), {
					row : i,
					column : 0
				});
			}

			var field1 = new qx.ui.form.TextField();
			var field2 = new qx.ui.form.PasswordField();

			this.__container.add(field1.set( {
				allowShrinkX : false,
				paddingTop : 3
			}), {
				row : 0,
				column : 1
			});

			this.__container.add(field2.set( {
				allowShrinkX : false,
				paddingTop : 3
			}), {
				row : 1,
				column : 1
			});

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
		this._disposeObjects("__container", "__loginButton", "__effect", "__newUserButton");
	}

});