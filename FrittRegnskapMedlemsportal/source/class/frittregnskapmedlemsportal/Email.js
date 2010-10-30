qx.Class.define("frittregnskapmedlemsportal.Email", {
		extend : qx.core.Object,

		members : {
			__textarea : 0,
			__sendButton : 0,
			__infoLabel : 0,

			sendEmail : function(personId) {
				this.__sendButton.setEnabled(false);

				this.__infoLabel.setValue("Eposten er sendt.");

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
