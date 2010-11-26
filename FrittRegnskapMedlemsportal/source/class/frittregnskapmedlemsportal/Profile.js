qx.Class.define("frittregnskapmedlemsportal.Profile", {
    extend: qx.core.Object,
    
    members: {
        logout: function(){
            var ok = confirm("Vil du logge ut?");
            
            if (ok) {
                var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_authenticate.php?action=logout", "GET", "application/json");
                req.send();
                req.addListener("completed", function(data){
                    window.location = ".";
                });
            }
        },
        
        changePassword: function(){
            var popup = new qx.ui.popup.Popup(new qx.ui.layout.Grow());
            
            var box = new qx.ui.groupbox.GroupBox("Nytt passord");
            popup.add(box);
            
            var gridLayout = new qx.ui.layout.Grid(2, 3);
            gridLayout.setSpacingY(10);
            box.setLayout(gridLayout);
            
            
            box.add(new qx.ui.basic.Label("Passord"), {
                column: 0,
                row: 0
            });
            
            box.add(new qx.ui.basic.Label("Gjennta passord"), {
                column: 0,
                row: 1
            });
            
            var password1 = new qx.ui.form.PasswordField("");
            password1.setRequired(true);
            
            box.add(password1, {
                column: 1,
                row: 0
            });
            
            var password2 = new qx.ui.form.PasswordField("");
            password2.setRequired(true);
            
            box.add(password2, {
                column: 1,
                row: 1
            });
            
            var infoLabel = new qx.ui.basic.Label();
            infoLabel.setAllowStretchY(true);
            infoLabel.setRich(true);
            
            box.add(infoLabel, {
                row: 2,
                column: 0,
                colSpan: 2
            });
            
            
            var updateButton = new qx.ui.form.Button("Bytt passord");
            updateButton.addListener("execute", function(){
            
                if (password1.getValue().length < 7) {
                    infoLabel.setTextColor("red");
                    infoLabel.setValue("Passord m&aring; minst v&aelig;re 7 tegn.");
                    return;
                }
                
                if (password1.getValue() != password2.getValue()) {
                    infoLabel.setTextColor("red");
                    infoLabel.setValue("Inngitte passord er ikke like.");
                    return;
                }
                
                var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_authenticate.php?action=password", "POST", "application/json");
                
                req.setParameter("password", password1.getValue(), true);
                
                req.addListener("completed", function(data){
                    var json = data.getContent();
                    
                    if (json["error"]) {
                        infoLabel.setTextColor("red");
                        infoLabel.setValue(json["error"]);
                    }
                    else 
                        if (json["status"] == "ok") {
                            infoLabel.setTextColor("green");
                            infoLabel.setValue("Passord byttet.");
                        }
                        else {
                            infoLabel.setTextColor("red");
                            
                            if (json["error"]) {
                                infoLabel.setValue(json["error"]);
                            }
                            else {
                                infoLabel.setValue("Klarte ikke bytte passord.");
                            }
                        }
                });
                
                req.send();
                
            }, this);
            
            box.add(updateButton, {
                column: 0,
                row: 3
            })
            
            var cancelButton = new qx.ui.form.Button("Avbryt");
            cancelButton.addListener("execute", function(){
                popup.hide();
            }, this);
            box.add(cancelButton, {
                column: 1,
                row: 3
            })
            
            
            
            popup.placeToWidget(this.__changePasswordButton);
            popup.show();
            password1.focus();
            
        },
        
        save: function(){
        
            if (!this.__manager.validate()) {
                this.__win.setStatus("Rett feil i skjema.");
                return;
            }
            
            var me = this;
            
            var gender = this.__gender.getSelection()[0].getModel().getId();
            var country = this.__country.getSelection()[0].getModel().getId();
            var birthdate = new qx.util.format.DateFormat("dd.MM.yyyy", "no").format(me.__birthdate.getValue())
            var data = {
                "firstname": me.__firstName.getValue(),
                "lastname": me.__lastName.getValue(),
                "address": me.__address.getValue(),
                "email": me.__email.getValue(),
                "phone": me.__phone.getValue(),
                "cellphone": me.__cellphone.getValue(),
                "postnmb": me.__postnmb.getValue(),
                "city": me.__city.getValue(),
                "birthdate": birthdate,
                "gender": gender,
                "country": country,
                "show_firstname": me.__showFirstname.getValue(),
                "show_lastname": me.__showLastname.getValue(),
                "show_gender": me.__showGender.getValue(),
                "show_address": me.__showAddress.getValue(),
                "show_birthdate": me.__showBirthdate.getValue(),
                "show_cellphone": me.__showCellphone.getValue(),
                "show_phone": me.__showPhone.getValue(),
                "show_country": me.__showCountry.getValue(),
                "show_city": me.__showCity.getValue(),
                "show_postnmb": me.__showPostnmb.getValue(),
                "show_email": me.__showEmail.getValue(),
                "show_image": me.__showImage.getValue(),
                "homepage": me.__homepage.getValue(),
                "linkedin": me.__linkedin.getValue(),
                "facebook": me.__facebook.getValue(),
                "twitter": me.__twitter.getValue(),
                "newsletter": me.__newsletter.getValue()
            };
            
            var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_persons.php?action=save", "POST", "application/json");
            req.setParameter("data", qx.lang.Json.stringify(data), true);
            
            req.addListener("completed", function(data){
                var json = data.getContent();
                
                if (json["error"]) {
                    this.__win.setStatus(json["error"]);
                }
                else 
                    if (json["result"] == "ok") {
                        this.__win.setStatus("Oppdatert.");
                    }
                    else {
                        this.__win.setStatus("Klarte ikke oppdatere profil");
                        
                    }
            }, this);
            
            req.send();
            
        },
        fillProfile: function(json){
            this.__firstName.setValue(json.firstname);
            this.__lastName.setValue(json.lastname);
            this.__address.setValue(json.address);
            this.__email.setValue(json.email);
            this.__phone.setValue(json.phone);
            this.__cellphone.setValue(json.cellphone);
            this.__postnmb.setValue(json.postnmb);
            this.__city.setValue(json.city);
            this.__birthdate.setValue(this.fixBirthdate(json.birthdate));
            
            this.__facebook.setValue(json.facebook ? json.facebook : "");
            this.__twitter.setValue(json.twitter ? json.twitter : "");
            this.__linkedin.setValue(json.linkedin ? json.linkedin : "");
            this.__homepage.setValue(json.homepage ? json.homepage : "");
            
            for (var i = 0; i < this.__genderModel.getLength(); i++) {
                if (this.__genderModel.getItem(i).getId() == json.gender) {
                    this.__gender.setModelSelection([this.__genderModel.getItem(i)]);
                }
            }
            for (var i = 0; i < this.__countryModel.getLength(); i++) {
                if (this.__countryModel.getItem(i).getId() == json.country) {
                    this.__country.setModelSelection([this.__countryModel.getItem(i)]);
                }
            }
            
            if (json.has_profile_image) {
                this.__image.setSource("/RegnskapServer/services/portal/portal_persons.php?action=myimage");
            }
            this.__newsletter.setValue(json.newsletter == 1);
            
        },
        fillShareProfile: function(data){
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
        fixBirthdate: function(birthdate){
            return new qx.util.format.DateFormat("yyyy-MM-dd", "no").parse(birthdate);
        },
        loadProfileData: function(){
            var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_persons.php?action=me", "GET", "application/json");
            
            
            req.addListener("completed", function(data){
                var json = data.getContent();
                
                try {
                    this.fillProfile(json);
                    this.fillShareProfile(json);
                    this.__win.setStatus("");
                } 
                catch (error) {
                    console.log(error);
                }
            }, this);
            
            req.send();
        },
        
        __firstName: null,
        __lastName: null,
        __email: null,
        __address: null,
        __postnmb: null,
        __city: null,
        __country: null,
        __countryModel: null,
        __birthdate: null,
        __newsletter: null,
        __genderModel: null,
        __phone: null,
        __cellphone: null,
        __gender: null,
        __showFirstname: null,
        __showLastname: null,
        __showGender: null,
        __showAddress: null,
        __showBirthdate: null,
        __showCellphone: null,
        __showPhone: null,
        __showCountry: null,
        __showCity: null,
        __showPostnmb: null,
        __showEmail: null,
        __showImage: null,
        __win: null,
        __manager: null,
        __image: null,
        __changePasswordButton: null,
        __logoutButton: null,
        __homepage: null,
        __twitter: null,
        __facebook: null,
        __linkedin: null,
        __newsletter: null,
        
        createWindowProfile: function(desktop){
            // Create the Window
            this.__win = new qx.ui.window.Window("Min info", "frittregnskapmedlemsportal/system-users.png");
            var win = this.__win;
            
            win.setLayout(new qx.ui.layout.Grid());
            win.setShowStatusbar(true);
            win.setStatus("Henter data...");
            win.setShowClose(false);
            win.setShowMinimize(false);
            win.setShowMaximize(false);
            win.setResizable(false);
            win.setAllowMaximize(false);
            win.moveTo(40, 40);
            
            desktop.add(win);
            win.open();
            
            this.__manager = new qx.ui.form.validation.Manager();
            
            
            var personbox = new qx.ui.groupbox.GroupBox("Personalia");
            personbox.setLayout(new qx.ui.layout.Grid(10, 5))
            
            personbox.add(new qx.ui.basic.Label("Fornavn"), {
                row: 0,
                column: 0
            });
            
            this.__firstName = new qx.ui.form.TextField("");
            this.__firstName.setRequired(true);
            this.__manager.add(this.__firstName);
            this.__firstName.setRequiredInvalidMessage("Fornavn m\u00E5 fylles ut");
            
            personbox.add(this.__firstName, {
                row: 0,
                column: 1
            });
            personbox.add(new qx.ui.basic.Label("Etternavn"), {
                row: 0,
                column: 2
            });
            
            this.__lastName = new qx.ui.form.TextField("");
            this.__lastName.setRequired(true);
            this.__lastName.setRequiredInvalidMessage("Etternavn m\u00E5 fylles ut");
            this.__manager.add(this.__lastName);
            personbox.add(this.__lastName, {
                row: 0,
                column: 3
            });
            
            personbox.add(new qx.ui.basic.Label("Kj\u00f8nn"), {
                row: 0,
                column: 4
            });
            
            var genderbox = new qx.ui.form.SelectBox();
            this.__gender = genderbox;
            
            var genderlist = [{
                title: "Kvinne",
                id: "K"
            }, {
                title: "Mann",
                id: "M"
            }];
            var gendermodel = qx.data.marshal.Json.createModel(genderlist);
            
            this.__genderModel = gendermodel;
            
            new qx.data.controller.List(gendermodel, genderbox, "title");
            
            personbox.add(genderbox, {
                row: 0,
                column: 5
            });
            
            personbox.add(new qx.ui.basic.Label("Epostadresse(r)"), {
                row: 1,
                column: 0
            });
            
            this.__email = new qx.ui.form.TextField("");
            
            this.__manager.add(this.__email, function(value){
                var multiple = value.split(",");
                
                if (multiple.length == 0) {
                    qx.util.Validate.checkEmail(value, "Epostadressen er ikke gyldig");
                }
                
                for (var i = 0; i < multiple.length; i++) {
                    qx.util.Validate.checkEmail(multiple[i], "Epostadressene er ikke gyldig");
                }
                
                return true;
            });
            
            
            personbox.add(this.__email, {
                row: 1,
                column: 1,
                colSpan: 3
            });
            
            personbox.add(new qx.ui.basic.Label("Telefon"), {
                row: 1,
                column: 4
            });
            this.__phone = new qx.ui.form.TextField("");
            personbox.add(this.__phone, {
                row: 1,
                column: 5
            });
            
            this.__showAddress = new qx.ui.basic.Label("Adresse");
            personbox.add(this.__showAddress, {
                row: 2,
                column: 0
            });
            
            this.__address = new qx.ui.form.TextField("");
            personbox.add(this.__address, {
                row: 2,
                column: 1,
                colSpan: 3
            });
            
            personbox.add(new qx.ui.basic.Label("Mobil"), {
                row: 2,
                column: 4
            });
            
            this.__cellphone = new qx.ui.form.TextField("");
            personbox.add(this.__cellphone, {
                row: 2,
                column: 5
            });
            
            personbox.add(new qx.ui.basic.Label("Postnr"), {
                row: 3,
                column: 0
            });
            
            this.__postnmb = new qx.ui.form.TextField("");
            
            personbox.add(this.__postnmb, {
                row: 3,
                column: 1
            });
            
            personbox.add(new qx.ui.basic.Label("Sted"), {
                row: 3,
                column: 2
            });
            this.__city = new qx.ui.form.TextField("");
            personbox.add(this.__city, {
                row: 3,
                column: 3
            });
            
            personbox.add(new qx.ui.basic.Label("Land"), {
                row: 3,
                column: 4
            });
            this.__country = new qx.ui.form.SelectBox();
            var countrybox = this.__country;
            
            var countrylist = [{
                title: "Norge",
                id: "NO"
            }, {
                title: "Sverige",
                id: "SE"
            }, {
                title: "Danmark",
                id: "DK"
            }, {
                title: "Finland",
                id: "FI"
            }, {
                title: "Annet",
                id: "??"
            }];
            var countrymodel = qx.data.marshal.Json.createModel(countrylist);
            this.__countryModel = countrymodel;
            
            new qx.data.controller.List(countrymodel, countrybox, "title");
            
            personbox.add(countrybox, {
                row: 3,
                column: 5
            });
            personbox.add(new qx.ui.basic.Label("F\u00f8dselsdato"), {
                row: 4,
                column: 0
            });
            this.__birthdate = new qx.ui.form.DateField("");
            this.__birthdate.setRequired(true);
            this.__birthdate.setRequiredInvalidMessage("F\u00f8dselsdato m\u00E5 fylles ut");
            
            this.__manager.add(this.__birthdate);
            
            this.__birthdate.setDateFormat(new qx.util.format.DateFormat("dd.MM.yyyy", "no"));
            personbox.add(this.__birthdate, {
                row: 4,
                column: 1
            });
            
            this.__newsletter = new qx.ui.form.CheckBox("Abonner p\u00E5 nyhetsbrev");
            personbox.add(this.__newsletter, {
                row: 4,
                column: 2,
                colSpan: 2
            });
            
            win.add(personbox, {
                row: 0,
                column: 0
            });
            
            var bildebox = new qx.ui.groupbox.GroupBox("Bilde");
            bildebox.setLayout(new qx.ui.layout.VBox(10));
            
            this.__image = new qx.ui.basic.Image();
            this.__image.setScale(true);
            this.__image.setWidth(100);
            this.__image.setMaxWidth(100);
            this.__image.setMaxHeight(130);
            bildebox.add(this.__image);
            
            win.add(bildebox, {
                row: 0,
                column: 1
            });
            
            var sharingbox = new qx.ui.groupbox.GroupBox("Vises for andre");
            sharingbox.setLayout(new qx.ui.layout.Grid(4));
            
            this.__showFirstname = new qx.ui.form.CheckBox("Fornavn");
            sharingbox.add(this.__showFirstname, {
                row: 0,
                column: 0
            });
            this.__showLastname = new qx.ui.form.CheckBox("Etternavn");
            sharingbox.add(this.__showLastname, {
                row: 0,
                column: 1
            });
            
            this.__showGender = new qx.ui.form.CheckBox("Kj\u00f8nn")
            sharingbox.add(this.__showGender, {
                row: 0,
                column: 2
            });
            
            this.__showEmail = new qx.ui.form.CheckBox("Epostadresse")
            sharingbox.add(this.__showEmail, {
                row: 1,
                column: 0
            });
            
            this.__showPhone = new qx.ui.form.CheckBox("Telefon");
            sharingbox.add(this.__showPhone, {
                row: 1,
                column: 1
            });
            
            this.__showAddress = new qx.ui.form.CheckBox("Adresse");
            sharingbox.add(this.__showAddress, {
                row: 1,
                column: 2
            });
            
            this.__showCellphone = new qx.ui.form.CheckBox("Mobil");
            sharingbox.add(this.__showCellphone, {
                row: 2,
                column: 0
            });
            
            this.__showPostnmb = new qx.ui.form.CheckBox("Postnr");
            sharingbox.add(this.__showPostnmb, {
                row: 2,
                column: 1
            });
            
            this.__showCity = new qx.ui.form.CheckBox("Sted");
            sharingbox.add(this.__showCity, {
                row: 2,
                column: 2
            });
            
            this.__showCountry = new qx.ui.form.CheckBox("Land");
            sharingbox.add(this.__showCountry, {
                row: 3,
                column: 0
            });
            
            this.__showBirthdate = new qx.ui.form.CheckBox("F\u00f8dselsdato");
            sharingbox.add(this.__showBirthdate, {
                row: 3,
                column: 1
            });
            
            this.__showImage = new qx.ui.form.CheckBox("Bilde");
            sharingbox.add(this.__showImage, {
                row: 3,
                column: 2
            });
            
            var sitebox = new qx.ui.groupbox.GroupBox("Mine eksterne lenker (vises alltid for andre)");
            sitebox.setAllowStretchX(true);
            sitebox.setAllowGrowX(true);
            
            var gridSitelayout = new qx.ui.layout.Grid(4, 2);
            gridSitelayout.setColumnFlex(1, 1);
            gridSitelayout.setColumnFlex(3, 1);
            sitebox.setLayout(gridSitelayout);
            sitebox.add(new qx.ui.basic.Label("Hjemmeside"), {
                row: 0,
                column: 0
            });
            this.__homepage = new qx.ui.form.TextField("");
            this.__homepage.setAllowStretchX(true);
            this.__homepage.setAllowGrowX(true);
            this.__homepage.setWidth(130);
            sitebox.add(this.__homepage, {
                row: 0,
                column: 1
            });
            
            sitebox.add(new qx.ui.basic.Label("Twitter"), {
                row: 1,
                column: 0
            });
            
            this.__twitter = new qx.ui.form.TextField("");
            this.__twitter.setAllowStretchX(true);
            this.__twitter.setAllowGrowX(true);
            sitebox.add(this.__twitter, {
                row: 1,
                column: 1
            });
            
            sitebox.add(new qx.ui.basic.Label("Facebook"), {
                row: 0,
                column: 2
            });
            
            this.__facebook = new qx.ui.form.TextField("");
            this.__facebook.setAllowStretchX(true);
            this.__facebook.setAllowGrowX(true);
            this.__facebook.setWidth(130);
            sitebox.add(this.__facebook, {
                row: 0,
                column: 3
            });
            
            sitebox.add(new qx.ui.basic.Label("LinkedIn"), {
                row: 1,
                column: 2
            });
            
            this.__linkedin = new qx.ui.form.TextField("");
            this.__linkedin.setAllowStretchX(true);
            this.__linkedin.setAllowGrowX(true);
            
            sitebox.add(this.__linkedin, {
                row: 1,
                column: 3
            });
            
            var container = new qx.ui.container.Composite(new qx.ui.layout.HBox(24));
            container.setWidth("100%");
            container.setAllowStretchX(true);
            container.setAllowGrowX(true);
            container.add(sharingbox);
            container.add(sitebox);
            
            var buttoncontainer = new qx.ui.container.Composite(new qx.ui.layout.HBox(24));
            buttoncontainer.setMarginTop(10);
            buttoncontainer.setLayout(new qx.ui.layout.HBox(10));
            
            var updateButton = new qx.ui.form.Button("Oppdater personalia");
            updateButton.addListener("execute", this.save, this);
            
            buttoncontainer.add(updateButton);
            
            var profilePicture = new frittregnskapmedlemsportal.ProfilePicture();
            buttoncontainer.add(profilePicture.createUploadButton(this.__image,this.__win));
            
            this.__changePasswordButton = new qx.ui.form.Button("Endre passord");
            this.__changePasswordButton.addListener("execute", this.changePassword, this);
            buttoncontainer.add(this.__changePasswordButton);
            
            
            this.__logoutButton = new qx.ui.form.Button("Logg ut");
            this.__logoutButton.addListener("execute", this.logout, this);
            buttoncontainer.add(this.__logoutButton);
            
            
            profilePicture.addMouseOverFullImage(this.__image);
            
            win.add(container, {
                row: 2,
                column: 0,
                colSpan: 2
            });
            win.add(buttoncontainer, {
                row: 3,
                column: 0,
                colSpan: 2
            });
            
            this.loadProfileData();
        },
        destruct: function(){
            this._disposeObjects("__firstName", "__lastName", "__email", "__address", "__cellphone", "__phone", "__gender", "__genderModel", "__newsletter", "__birthdate", "__country", "__countryModel", //
 "__city", "__postnmb", "__showLastname", "__showFirstname", "__showGender", "__showAddress", "__showBirthdate", "__showCellphone", "__showPhone", "__showCountry", "__showCity", "__showPostnmb", //
 "__showEmail", "__showImage", "__win", "__manager", "__image", "__changePasswordButton", "__logoutButton", "__homepage", "__linkedin", "__facebook", "__twitter", "__newsletter");
        }
        
    }
});
