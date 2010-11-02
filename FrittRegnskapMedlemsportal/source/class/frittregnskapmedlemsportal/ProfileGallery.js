qx.Class.define("frittregnskapmedlemsportal.ProfileGallery", {
	extend : qx.core.Object,

	members : {
		__index : 0,
		__desktop : 0,
		__imagesAndLabels : 0,
		__users : 0,
		__nextButton : 0,
		__previousButton : 0,
		createSlots : function(win) {
			this.__imagesAndLabels = {};

			var count = 0;
			for ( var row = 0; row < 3; row++) {
				for ( var col = 0; col < 8; col++) {
					var groupBox = new qx.ui.groupbox.GroupBox("");
					groupBox.setLayout(new qx.ui.layout.Grow());
					var image = new qx.ui.basic.Image();
					groupBox.add(image);

					groupBox.setMinWidth(124);
					groupBox.setMinHeight(154);

					image.setScale(true);
					image.setMaxWidth(100);
					image.setMaxHeight(130);
					
					image.imageIndex = count;
					var me = this;
					
					image.addListener("dblclick", function() {
						var index = me.__imagesAndLabels["slot" + this.imageIndex + "index"];
						
						console.log("Index is:"+index+" for count:"+count);
						
						if(index >= 0) {
							new frittregnskapmedlemsportal.Membersearch().openProfileForUser(me.__users[index], me.__desktop);
						}
					}, image);

					this.__imagesAndLabels["slot" + count + "groupbox"] = groupBox;
					this.__imagesAndLabels["slot" + count + "image"] = image;

					count++;
					win.add(groupBox, {
						row : row,
						column : col
					});
				}
			}

		},
		loadImages : function() {
			var count = 0;

			this.__previousButton.setEnabled((this.__index - 24 >= 0) ? true : false);

			while (count < 24 && this.__index < this.__users.length) {
				var user = this.__users[this.__index];

				if (user.s == 1) {
					this.__imagesAndLabels["slot" + count + "image"]
							.setSource("/RegnskapServer/services/portal/portal_persons.php?action=image&personId=" + user.p);

					var name = user.f + " " + user.l;

					this.__imagesAndLabels["slot" + count + "image"].setToolTipText(name);
					this.__imagesAndLabels["slot" + count + "index"] = this.__index;
					if (name.length > 20) {
						name = user.f + " ...";
					}

					this.__imagesAndLabels["slot" + count + "groupbox"].setLegend(name);

					count++;
				}
				this.__index++;
			}

			if (count < 24) {
				for ( var i = count; i < 24; i++) {
					this.__imagesAndLabels["slot" + i + "image"].setSource("frittregnskapmedlemsportal/camera-photo.png");
					this.__imagesAndLabels["slot" + i + "image"].setToolTipText("");
					this.__imagesAndLabels["slot" + i + "groupbox"].setLegend(" ");
					this.__imagesAndLabels["slot" + i + "index"] = -1;

				}
			}

			this.__nextButton.setEnabled((this.__index < this.__users.length) ? true : false);
		},
		showProfileGallery : function(desktop, users) {
			this.__win = new qx.ui.window.Window("Profilbilder", "frittregnskapmedlemsportal/camera-photo.png");
			this.__desktop = desktop;
			this.__users = users;
			var win = this.__win;
			win.setLayout(new qx.ui.layout.Grid());
			win.setShowStatusbar(false);
			win.setShowClose(true);
			win.setShowMinimize(false);
			win.setShowMaximize(false);
			win.setAllowMaximize(false);

			this.createSlots(win);

			var buttoncontainer = new qx.ui.container.Composite(new qx.ui.layout.HBox(24));
			buttoncontainer.setMarginTop(10);
			buttoncontainer.setLayout(new qx.ui.layout.HBox(10));

			this.__previousButton = new qx.ui.form.Button("Forrige");
			this.__previousButton.addListener("execute", function() {

				var count = 0;
				while (count < (24 * 2) && this.__index > 0) {
					var user = this.__users[this.__index];

					if (user && (user.s == 1)) {
						count++;
					}
					this.__index--;
				}

				this.loadImages();
			}, this);

			buttoncontainer.add(this.__previousButton);

			this.__nextButton = new qx.ui.form.Button("Neste");
			this.__nextButton.addListener("execute", function() {
				this.loadImages();
			}, this);

			buttoncontainer.add(this.__nextButton);
			win.add(buttoncontainer, {
				row : 4,
				column : 0,
				colSpan : 7
			});

			this.loadImages();

			desktop.add(win);
			win.open();
		}
	}
});