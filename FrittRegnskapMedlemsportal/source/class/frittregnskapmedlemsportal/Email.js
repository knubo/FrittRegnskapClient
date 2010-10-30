qx.Class.define("frittregnskapmedlemsportal.Email", {
		extend : qx.core.Object,

		members : {
			__textarea : 0,
			__sendButton : 0,
			__infoLabel : 0,

			sendEmail : function(personId) {
				this.__sendButton.setEnabled(false);

	            var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_email.php?action=email", "POST", "application/json");
	            
	            req.setParameter("body", this.__textarea.getValue(), true);
	            req.setParameter("personId", personId, true);
	            
	            req.addListener("failed", function(t) {
                	this.__infoLabel.setValue("Eposten ble ikke sendt. Pr&oslash;v igjen senere.");
    				this.__sendButton.setEnabled(true);
	            });
	            
	            
	            req.addListener("completed", function(data){
	                var json = data.getContent();
	                
	                if(json.status == 1) {
	                	this.__infoLabel.setValue("Eposten er sendt.");
	                } else {
	                	this.__infoLabel.setValue("Eposten ble ikke vellykket sendt. Trolig har vedkommende en ikke gyldig epostadresse.");
	                }
	                
	            },this);
	            
	            req.send();

				

			},
			setupEmailWindow : function(desktop, personId, firstname) {
				var win = new qx.ui.window.Window("Epost til " + firstname, "frittregnskapmedlemsportal/internet-mail.png");
				var layout = new qx.ui.layout.Grid();

				layout.setRowFlex(1, 1);
				layout.setColumnFlex(0, 1);
				win.setLayout(layout);
				win.setShowMinimize(false);
				win.setShowMaximize(false);
				win.setAllowMaximize(false);

				var heading = new qx.ui.basic.Label(
						"Skriv inn meldingen som du vil sende til "
								+ firstname
								+ ". Meldingen vil i tillegg til din tekst inneholde en standardtekst fra Fritt Regnskap hvor din epostadresse og fullt navn er inkludert slik at et svar kan sendes direkte til deg.");
				heading.setRich(true);
				heading.setAllowStretchY(true);

				win.add(heading, {
					row : 0,
					column : 0
				});

				var messageBox = new qx.ui.groupbox.GroupBox("Melding");
				messageBox.setLayout(new qx.ui.layout.Grow());

				this.__textarea = new qx.ui.form.TextArea();

				messageBox.add(this.__textarea);

				win.add(messageBox, {
					row : 1,
					column : 0
				});

	            var buttoncontainer = new qx.ui.container.Composite(new qx.ui.layout.HBox(24));
	            buttoncontainer.setMarginTop(10);
	            buttoncontainer.setLayout(new qx.ui.layout.HBox(10));

				this.__sendButton = new qx.ui.form.Button("Send epost");
				this.__sendButton.setAllowStretchX(false);
				
				this.__sendButton.addListener("execute", function() {
					this.sendEmail(personId);
				},this);

				
				buttoncontainer.add(this.__sendButton)
				
				win.add(buttoncontainer, {
					row : 2,
					column : 0
				});

				this.__infoLabel = new qx.ui.basic.Label("");
				this.__infoLabel.setAllowStretchX(true);
				this.__infoLabel.setRich(true);
				buttoncontainer.add(this.__infoLabel);

				
				
				win.setWidth(500);
				win.setHeight(300);

				desktop.add(win);
				win.open();

				this.__textarea.focus();
			},
			destruct : function() {
				this._disposeObjects("__textarea", "__sendButton", "__infoLabel");
			}
		}
});
