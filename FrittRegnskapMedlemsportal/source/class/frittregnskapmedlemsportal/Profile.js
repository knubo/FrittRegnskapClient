qx.Class.define("frittregnskapmedlemsportal.Profile", {
	extend : qx.core.Object,

	members : {
		fixBirthdate : function(birthdate) {
			var splits = birthdate.split("-");
			
			if(splits.length != 3) {
				return "";
			}
			
			return splits[2] + "." + splits[1] + "." + splits[0];
		},
		loadProfileData : function() {
			var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_persons.php?action=me", "GET", "application/json");

			var owner = this;

			req.addListener("completed", function(data) {
				var json = data.getContent();
				owner.__firstName.setValue(json.firstname);
				owner.__lastName.setValue(json.lastname);
				owner.__address.setValue(json.address);
				owner.__email.setValue(json.email);
				owner.__phone.setValue(json.phone);
				owner.__cellphone.setValue(json.cellphone);
				owner.__postnmb.setValue(json.postnmb);
				owner.__city.setValue(json.city);
				owner.__birthdate.setValue(owner.fixBirthdate(json.birthdate));

				for(var i = 0; i < owner.__genderModel.getLength(); i++) {
					if(owner.__genderModel.getItem(i).getId() == json.gender) {
						owner.__gender.setModelSelection([owner.__genderModel.getItem(i)]);
					}
				}
				
			});

			req.send();
		},

		__firstName : null,
		__lastName : null,
		__email : null,
		__address : null,
		__postnmb : null,
		__city : null,
		__country : null,
		__birthdate : null,
		__newsletter : null,
		__genderModel : null,
		__phone : null,
		__cellphone : null,
		__gender : null,

		createWindowProfile : function(desktop) {
			// Create the Window
			var win = new qx.ui.window.Window("Min info", "frittregnskapmedlemsportal/system-users.png");
			win.setLayout(new qx.ui.layout.Grid());
			win.setShowStatusbar(true);
			win.setStatus("Henter data...");
			win.setShowClose(false);
			win.setShowMinimize(false);
			win.setShowMaximize(false);

			desktop.add(win);
			win.open();

			var personbox = new qx.ui.groupbox.GroupBox("Personalia");
			personbox.setLayout(new qx.ui.layout.Grid(10, 5))

			personbox.add(new qx.ui.basic.Label("Fornavn"), {
				row : 0,
				column : 0
			});

			this.__firstName = new qx.ui.form.TextField("");
			personbox.add(this.__firstName, {
				row : 0,
				column : 1
			});
			personbox.add(new qx.ui.basic.Label("Etternavn"), {
				row : 0,
				column : 2
			});

			this.__lastName = new qx.ui.form.TextField("");
			personbox.add(this.__lastName, {
				row : 0,
				column : 3
			});

			personbox.add(new qx.ui.basic.Label("Kj\u00f8nn"), {
				row : 0,
				column : 4
			});

			var genderbox = new qx.ui.form.SelectBox();
			this.__gender = genderbox;
			
			var genderlist = [{title:"Kvinne", id:"K"}, {title:"Mann", id:"M"}]; 
			var gendermodel = qx.data.marshal.Json.createModel(genderlist);
			
			this.__genderModel = gendermodel;
			
			new qx.data.controller.List(gendermodel, genderbox, "title"); 
			
			personbox.add(genderbox, {
				row : 0,
				column : 5
			});

			personbox.add(new qx.ui.basic.Label("Epostadresse"), {
				row : 1,
				column : 0
			});

			this.__email = new qx.ui.form.TextField("");
			personbox.add(this.__email, {
				row : 1,
				column : 1,
				colSpan : 3
			});

			personbox.add(new qx.ui.basic.Label("Telefon"), {
				row : 1,
				column : 4
			});
			this.__phone = new qx.ui.form.TextField("");
			personbox.add(this.__phone, {
				row : 1,
				column : 5
			});

			personbox.add(new qx.ui.basic.Label("Adresse"), {
				row : 2,
				column : 0
			});

			this.__address = new qx.ui.form.TextField("");
			personbox.add(this.__address, {
				row : 2,
				column : 1,
				colSpan : 3
			});

			personbox.add(new qx.ui.basic.Label("Mobil"), {
				row : 2,
				column : 4
			});

			this.__cellphone = new qx.ui.form.TextField("");
			personbox.add(this.__cellphone, {
				row : 2,
				column : 5
			});

			personbox.add(new qx.ui.basic.Label("Postnr"), {
				row : 3,
				column : 0
			});

			this.__postnmb = new qx.ui.form.TextField("");
			personbox.add(this.__postnmb, {
				row : 3,
				column : 1
			});

			personbox.add(new qx.ui.basic.Label("Sted"), {
				row : 3,
				column : 2
			});
			this.__city = new qx.ui.form.TextField("");
			personbox.add(this.__city, {
				row : 3,
				column : 3
			});

			personbox.add(new qx.ui.basic.Label("Land"), {
				row : 3,
				column : 4
			});
			this.__country = new qx.ui.form.SelectBox();
			var countrybox = this.__country;
			countrybox.add(new qx.ui.form.ListItem("Norge", "NO"));
			countrybox.add(new qx.ui.form.ListItem("Sverige", "SE"));
			countrybox.add(new qx.ui.form.ListItem("Danmark", "DK"));
			countrybox.add(new qx.ui.form.ListItem("Finland", "FI"));
			countrybox.add(new qx.ui.form.ListItem("Annet", "??"));
			personbox.add(countrybox, {
				row : 3,
				column : 5
			});
			personbox.add(new qx.ui.basic.Label("F\u00f8dselsdato"), {
				row : 4,
				column : 0
			});
			this.__birthdate = new qx.ui.form.TextField("");
			personbox.add(this.__birthdate, {
				row : 4,
				column : 1
			});

			win.add(personbox, {
				row : 0,
				column : 0
			});

			var bildebox = new qx.ui.groupbox.GroupBox("Bilde");
			bildebox.setLayout(new qx.ui.layout.VBox(10));
			bildebox.add(new qx.ui.basic.Image("frittregnskapmedlemsportal/knuterikLiten.jpg"));

			win.add(bildebox, {
				row : 0,
				column : 1
			});

			var sharingbox = new qx.ui.groupbox.GroupBox("Vises for andre");
			sharingbox.setLayout(new qx.ui.layout.Grid(4));
			sharingbox.add(new qx.ui.form.CheckBox("Fornavn"), {
				row : 0,
				column : 0
			});
			sharingbox.add(new qx.ui.form.CheckBox("Etternavn"), {
				row : 0,
				column : 1
			});
			sharingbox.add(new qx.ui.form.CheckBox("Kj\u00f8nn"), {
				row : 0,
				column : 2
			});
			sharingbox.add(new qx.ui.form.CheckBox("Epostadresse"), {
				row : 1,
				column : 0
			});
			sharingbox.add(new qx.ui.form.CheckBox("Telefon"), {
				row : 1,
				column : 1
			});
			sharingbox.add(new qx.ui.form.CheckBox("Adresse"), {
				row : 1,
				column : 2
			});
			sharingbox.add(new qx.ui.form.CheckBox("Mobil"), {
				row : 2,
				column : 0
			});
			sharingbox.add(new qx.ui.form.CheckBox("Postnr"), {
				row : 2,
				column : 1
			});
			sharingbox.add(new qx.ui.form.CheckBox("Sted"), {
				row : 2,
				column : 2
			});
			sharingbox.add(new qx.ui.form.CheckBox("Land"), {
				row : 3,
				column : 0
			});
			sharingbox.add(new qx.ui.form.CheckBox("F\u00f8dselsdato"), {
				row : 3,
				column : 1
			});
			sharingbox.add(new qx.ui.form.CheckBox("Bilde"), {
				row : 3,
				column : 2
			});

			var sitebox = new qx.ui.groupbox.GroupBox("Eksterne nettsteder");
			sitebox.setLayout(new qx.ui.layout.Grid(10, 5));
			sitebox.add(new qx.ui.basic.Label("Hjemmeside"), {
				row : 0,
				column : 0
			});
			sitebox.add(new qx.ui.form.TextField(""), {
				row : 0,
				column : 1
			});

			sitebox.add(new qx.ui.basic.Label("Twitter ID"), {
				row : 1,
				column : 0
			});
			sitebox.add(new qx.ui.form.TextField(""), {
				row : 1,
				column : 1
			});

			sitebox.add(new qx.ui.basic.Label("Facebook ID"), {
				row : 0,
				column : 2
			});
			sitebox.add(new qx.ui.form.TextField(""), {
				row : 0,
				column : 3
			});

			sitebox.add(new qx.ui.basic.Label("LinkedIn"), {
				row : 1,
				column : 2
			});
			sitebox.add(new qx.ui.form.TextField(""), {
				row : 1,
				column : 3
			});

			var container = new qx.ui.container.Composite(new qx.ui.layout.HBox(24));
			container.add(sharingbox);
			container.add(sitebox);

			var buttoncontainer = new qx.ui.container.Composite(new qx.ui.layout.HBox(24));
			buttoncontainer.setMarginTop(10);
			buttoncontainer.setLayout(new qx.ui.layout.HBox(10));
			buttoncontainer.add(new qx.ui.form.Button("Oppdater"));
			buttoncontainer.add(new qx.ui.form.Button("Last opp profilbilde"));

			win.add(container, {
				row : 2,
				column : 0,
				colSpan : 2
			});
			win.add(buttoncontainer, {
				row : 3,
				column : 0,
				colSpan : 2
			});

			this.loadProfileData();
		},
		destruct : function() {
			this._disposeObjects("__firstName", "__lastName", "__email", "__address", "__cellphone", "__phone", "__gender", "__genderModel", "__newsletter",
					"__birthdate", "__country", "__city", "__postnmb");
		}

	}
});