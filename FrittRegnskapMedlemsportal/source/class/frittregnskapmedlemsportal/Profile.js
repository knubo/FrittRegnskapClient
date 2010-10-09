qx.Class.define("frittregnskapmedlemsportal.Profile",
		{
			extend : qx.core.Object,

			members : {
				save : function() {
                    
                    var me = this;
                    
                    var gender = this.__gender.getSelection()[0].getModel().getId();
                    var country = this.__country.getSelection()[0].getModel().getId();
                    
					var data = {
						"firstname" : me.__firstName.getValue(),
						"lastname" : me.__lastName.getValue(),
						"address" : me.__address.getValue(),
						"email" : me.__email.getValue(),
						"phone" : me.__phone.getValue(),
						"cellphone" : me.__cellphone.getValue(),
						"postnmb" : me.__postnmb.getValue(),
						"city" : me.__city.getValue(),
						"birthdate" : me.__birthdate.getValue(),
						"gender" : gender,
						"country" : country,
						"show_firstname" : me.__showFirstname.getValue(),
						"show_lastname" : me.__showLastname.getValue(),
						"show_gender" : me.__showGender.getValue(),
						"show_address" : me.__showAddress.getValue(),
						"show_birthdate" : me.__showBirthdate.getValue(),
						"show_cellphone" : me.__showCellphone.getValue(),
						"show_phone" : me.__showPhone.getValue(),
						"show_country" : me.__showCountry.getValue(),
						"show_city" : me.__showCity.getValue(),
						"show_postnmb" : me.__showPostnmb.getValue(),
						"show_email" : me.__showEmail.getValue(),
						"show_image" : me.__showImage.getValue()
					};

					var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_persons.php?action=save", "POST",
							"application/json");
					req.setParameter("data", qx.lang.Json.stringify(data), true);

					var owner = this;

					req.addListener("completed", function(data) {
						var json = data.getContent();

						if (json["error"]) {
							window.alert("Error");
						} else if (json["result"] == "ok") {
                            owner.__win.setStatus("Oppdatert.");    
						}
					});

					req.send();

				},
				fillProfile : function(json) {
					this.__firstName.setValue(json.firstname);
					this.__lastName.setValue(json.lastname);
					this.__address.setValue(json.address);
					this.__email.setValue(json.email);
					this.__phone.setValue(json.phone);
					this.__cellphone.setValue(json.cellphone);
					this.__postnmb.setValue(json.postnmb);
					this.__city.setValue(json.city);
					this.__birthdate.setValue(this.fixBirthdate(json.birthdate));

					for ( var i = 0; i < this.__genderModel.getLength(); i++) {
						if (this.__genderModel.getItem(i).getId() == json.gender) {
							this.__gender.setModelSelection( [ this.__genderModel.getItem(i) ]);
						}
					}
					for ( var i = 0; i < this.__countryModel.getLength(); i++) {
						if (this.__countryModel.getItem(i).getId() == json.country) {
							this.__country.setModelSelection( [ this.__countryModel.getItem(i) ]);
						}
					}
				},
				fillShareProfile : function(data) {
					this.__showFirstname.setValue(data.show_firstname == 1);
					this.__showLastname.setValue(data.show_lastname == 1);
					this.__showGender.setValue(data.show_gender == 1);
					this.__showAddress.setValue(data.show_address == 1);
					this.__showBirthdate.setValue(data.show_birthdate == 1);
					this.__showCellphone.setValue(data.show_cellphone == 1);
					this.__showPhone.setValue(data.show_phone == 1);
					this.__showCountry.setValue(data.show_country == 1);
					this.__showCity.setValue(data.show_city == 1);
					this.__showPostnmb.setValue(data.show_postnmb == 1);
					this.__showEmail.setValue(data.show_email == 1);
					this.__showImage.setValue(data.show_image == 1);
				},
				fixBirthdate : function(birthdate) {
					var splits = birthdate.split("-");

					if (splits.length != 3) {
						return "";
					}

					return splits[2] + "." + splits[1] + "." + splits[0];
				},
				loadProfileData : function() {
					var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_persons.php?action=me", "GET",
							"application/json");

					var owner = this;

					req.addListener("completed", function(data) {
						var json = data.getContent();

						owner.fillProfile(json);
						owner.fillShareProfile(json);
                        owner.__win.setStatus("");
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
				__countryModel : null,
				__birthdate : null,
				__newsletter : null,
				__genderModel : null,
				__phone : null,
				__cellphone : null,
				__gender : null,
				__showFirstname : null,
				__showLastname : null,
				__showGender : null,
				__showAddress : null,
				__showBirthdate : null,
				__showCellphone : null,
				__showPhone : null,
				__showCountry : null,
				__showCity : null,
				__showPostnmb : null,
				__showEmail : null,
				__showImage : null,
                __win : null,

				createWindowProfile : function(desktop) {
					// Create the Window
					this.__win = new qx.ui.window.Window("Min info", "frittregnskapmedlemsportal/system-users.png");
                    var win = this.__win;
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

					var genderlist = [ {
						title : "Kvinne",
						id : "K"
					}, {
						title : "Mann",
						id : "M"
					} ];
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

					this.__showAddress = new qx.ui.basic.Label("Adresse");
					personbox.add(this.__showAddress, {
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

					var countrylist = [ {
						title : "Norge",
						id : "NO"
					}, {
						title : "Sverige",
						id : "SE"
					}, {
						title : "Danmark",
						id : "DK"
					}, {
						title : "Finland",
						id : "FI"
					}, {
						title : "Annet",
						id : "??"
					} ];
					var countrymodel = qx.data.marshal.Json.createModel(countrylist);
					this.__countryModel = countrymodel;

					new qx.data.controller.List(countrymodel, countrybox, "title");

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

					this.__showFirstname = new qx.ui.form.CheckBox("Fornavn");
					sharingbox.add(this.__showFirstname, {
						row : 0,
						column : 0
					});
					this.__showLastname = new qx.ui.form.CheckBox("Etternavn");
					sharingbox.add(this.__showLastname, {
						row : 0,
						column : 1
					});

					this.__showGender = new qx.ui.form.CheckBox("Kj\u00f8nn")
					sharingbox.add(this.__showGender, {
						row : 0,
						column : 2
					});

					this.__showEmail = new qx.ui.form.CheckBox("Epostadresse")
					sharingbox.add(this.__showEmail, {
						row : 1,
						column : 0
					});

					this.__showPhone = new qx.ui.form.CheckBox("Telefon");
					sharingbox.add(this.__showPhone, {
						row : 1,
						column : 1
					});

					this.__showAddress = new qx.ui.form.CheckBox("Adresse");
					sharingbox.add(this.__showAddress, {
						row : 1,
						column : 2
					});

					this.__showCellphone = new qx.ui.form.CheckBox("Mobil");
					sharingbox.add(this.__showCellphone, {
						row : 2,
						column : 0
					});

					this.__showPostnmb = new qx.ui.form.CheckBox("Postnr");
					sharingbox.add(this.__showPostnmb, {
						row : 2,
						column : 1
					});

					this.__showCity = new qx.ui.form.CheckBox("Sted");
					sharingbox.add(this.__showCity, {
						row : 2,
						column : 2
					});

					this.__showCountry = new qx.ui.form.CheckBox("Land");
					sharingbox.add(this.__showCountry, {
						row : 3,
						column : 0
					});

					this.__showBirthdate = new qx.ui.form.CheckBox("F\u00f8dselsdato");
					sharingbox.add(this.__showBirthdate, {
						row : 3,
						column : 1
					});

					this.__showImage = new qx.ui.form.CheckBox("Bilde");
					sharingbox.add(this.__showImage, {
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
                    
                    var updateButton = new qx.ui.form.Button("Oppdater");
                    updateButton.addListener("execute", this.save, this);
                    
					buttoncontainer.add(updateButton);
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
					this._disposeObjects("__firstName", "__lastName", "__email", "__address", "__cellphone", "__phone", "__gender",
							"__genderModel", "__newsletter", "__birthdate", "__country",
							"__countryModel", //
							"__city", "__postnmb", "__showLastname", "__showFirstname", "__showGender", "__showAddress", "__showBirthdate",
							"__showCellphone", "__showPhone", "__showCountry", "__showCity", "__showPostnmb", //
							"__showEmail", "__showImage", "__win");
				}

			}
		});
