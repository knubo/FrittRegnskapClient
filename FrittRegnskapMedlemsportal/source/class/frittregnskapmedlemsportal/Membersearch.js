qx.Class.define("frittregnskapmedlemsportal.Membersearch", {
    extend: qx.core.Object,
    
    members: {
        __win: null,
        __users: null,
        __userList: null,
        __eventKeyUpRunning: false,
        __searchTextField: null,
        __desktop: null,
        
        createLabel: function(txt) {
			var lbl = new qx.ui.basic.Label(txt);
			lbl.setPadding(4);
			lbl.set({
		        decorator: "main"
		      });

			lbl.setAllowStretchX(true);
			lbl.setSelectable(true);
			return lbl;
		}, 
		
		addImagePopup: function(image) {

			var popup= new qx.ui.popup.Popup(new qx.ui.layout.Grow());
			var imagePopup = new qx.ui.basic.Image();
			imagePopup.addListener("mouseout", function() {
				popup.hide();
			}, this);

			popup.add(imagePopup);
			
			imagePopup.setSource(image.getSource());
			popup.placeToPoint(image.getContainerLocation());
			popup.show();
		},
        
        openProfileForUser: function(obj) {
    		var win = new qx.ui.window.Window(obj.f+ " "+obj.l, "frittregnskapmedlemsportal/system-users.png");
    		var layout = new qx.ui.layout.Grid();
            layout.setColumnFlex(0, 1);

    		win.setLayout(layout);
    		this.__desktop.add(win);
            win.setShowMinimize(false);
            win.setShowMaximize(false);
            win.setAllowMaximize(false);
            win.setResizable(false);
            
            var personbox = new qx.ui.groupbox.GroupBox("Info");
            personbox.setAllowStretchX(true);

            var personLayout = new qx.ui.layout.Grid(10, 5);
            	
           	personLayout.setColumnFlex(1, 1);
            personLayout.setColumnFlex(3, 1);
            personLayout.setColumnFlex(5, 1);

            personbox.setLayout(personLayout);
            
            personbox.add(new qx.ui.basic.Label("Fornavn"), {
                row: 0,
                column: 0
            });

            personbox.add(this.createLabel(obj.f), {
            	row: 0,
            	column: 1
            });
            
            if(obj.l && obj.l.length > 0) {
	            personbox.add(new qx.ui.basic.Label("Etternavn"), {
	                row: 0,
	                column: 2
	            });
	            personbox.add(this.createLabel(obj.l), {
	            	row: 0,
	            	column: 3
	            });
            }

            if(obj.g && obj.g.length > 0) {
	            personbox.add(new qx.ui.basic.Label("Kj\u00f8nn"), {
	                row: 0,
	                column: 4
	            });
	            
	            var gender="";
	            if(obj.g == "K") {
	            	gender = "Kvinne";
	            }
	            if(obj.g == "M") {
	            	gender = "Mann";
	            }
	            personbox.add(this.createLabel(gender), {
	            	row: 0,
	            	column: 5
	            });
	            
            }
            
            if(obj.e && obj.e.length > 0) {
	            personbox.add(new qx.ui.basic.Label("Epostadresse(r)"), {
	                row: 1,
	                column: 0
	            });
	            
	            var emailLabel = this.createLabel(obj.e);
	            personbox.add(emailLabel, {
	            	row: 1,
	            	column: 1,
	            	colSpan:2
	            });
            }
            
            if(obj.q && obj.q.length > 0) {
	            personbox.add(new qx.ui.basic.Label("Telefon"), {
	                row: 1,
	                column: 4
	            });
	            personbox.add(this.createLabel(obj.q), {
	                row: 1,
	                column: 5
	            });
            }
            
            if(obj.z && obj.z.length > 0) {
            	personbox.add(new qx.ui.basic.Label("Adresse"), {
            		row: 2,
            		column: 0
            	});
            	personbox.add(this.createLabel(obj.z), {
            		row: 2,
            		column: 1,
            		colSpan:2
            	});
            }
            
            if(obj.c && obj.c.length > 0) {
	            personbox.add(new qx.ui.basic.Label("Mobil"), {
	                row: 2,
	                column: 4
	            });
	            personbox.add(this.createLabel(obj.c), {
	                row: 2,
	                column: 5
	            });
            }
            
            if(obj.v && obj.v.length > 0) {
	            personbox.add(new qx.ui.basic.Label("Postnr"), {
	                row: 3,
	                column: 0
	            });
	            personbox.add(this.createLabel(obj.v), {
	                row: 3,
	                column: 1
	            });
            }
            
            if(obj.x && obj.x.length > 0) {
	
	            personbox.add(new qx.ui.basic.Label("Sted"), {
	                row: 3,
	                column: 2
	            });
	            personbox.add(this.createLabel(obj.x), {
	                row: 3,
	                column: 3
	            });
            }
            
            if(obj.b && obj.b.length > 0) {
	            personbox.add(new qx.ui.basic.Label("Land"), {
	                row: 3,
	                column: 4
	            });
	            
	            
	            var country = "";
	            
	            if(obj.b == "NO") {
	            	country = "Norge";
	            }
	            if(obj.b == "SE") {
	            	country = "Sverige";
	            }
	            if(obj.b == "DK") {
	            	country = "Danmark";
	            }
	            if(obj.b == "FI") {
	            	country = "Finland";
	            }
	            if(obj.b == "??") {
	            	country = "Annet";
	            }
	            personbox.add(this.createLabel(country), {
	                row: 3,
	                column: 5
	            });
            }
            
            
            if(obj.n && obj.n.length > 0) {
            	var birthdate = "";
            	var bdObj = new qx.util.format.DateFormat("yyyy-MM-dd", "no").parse(obj.n);
            	birthdate = new qx.util.format.DateFormat("dd.MM.yyyy", "no").format(bdObj);
            	
            	personbox.add(new qx.ui.basic.Label("F\u00f8dselsdato"), {
            		row: 4,
            		column: 0
            	});
            	personbox.add(this.createLabel(birthdate), {
            		row: 4,
            		column: 1
            	});
            }
            
            personbox.add(new qx.ui.basic.Label("F\u00f8rste medlems\u00E5r"), {
            	row: 4,
        		column: 2
            });

            personbox.add(this.createLabel(obj.y), {
            	row: 4,
            	column: 3
            });
            
            
            
            win.add(personbox, {
                row: 0,
                column: 0
            });
            
            if(obj.s) {
	            var bildebox = new qx.ui.groupbox.GroupBox("Bilde");
	            bildebox.setAllowStretchX(false);
	            bildebox.setLayout(new qx.ui.layout.VBox(10));
	            
	            var image = new qx.ui.basic.Image();
	            image.setScale(true);
	            image.setMaxWidth(100);
	            image.setMaxHeight(130);
	            image.setSource("/RegnskapServer/services/portal/portal_persons.php?action=image&personId="+obj.p);
	
	            var me = this;
	            image.addListener('mouseover', function() {
	            	me.addImagePopup(image);
	            });
	            
	            bildebox.add(image);
	
	            
	            win.add(bildebox, {
	                row: 0,
	                column: 1
	            });
            }
            
            var buttoncontainer = new qx.ui.container.Composite(new qx.ui.layout.HBox(24));
            buttoncontainer.setMarginTop(10);
            buttoncontainer.setLayout(new qx.ui.layout.HBox(10));

            var sendEmailButton = new qx.ui.form.Button("Send epost til "+obj.f);
            sendEmailButton.addListener("execute", function() {
            	this.sendEmail(obj.e);
            }, this);
            buttoncontainer.add(sendEmailButton);

            
            win.add(buttoncontainer, { 
            	row: 1,
                column: 0,
                colSpan:2
            });
            
            win.open();

		},
		sendEmail: function(email) {
			if(email && email.length > 0) {
				var emails = email.split(",");
				
				if(emails.length > 0) {
					email = emails[0];
				}
				
				var mailto_link = 'mailto:'+email;

				win = window.open(mailto_link,'emailWindow');
				win.close();
				return;
			}
		}, 
        
        filterUsers: function(){
            this.__eventKeyUpRunning = false;
            
            try {
                this.__userList.removeAll();
                
                var listToIterate = this.users;
                
                var filter = this.__searchTextField.getValue().toLowerCase();
                var keywords = filter.split(" ");
                
                if (keywords.length > 0) {
                    listToIterate = new Array();
                    
                    for (var i = 0; i < this.__users.length; i++) {
                        var user = this.__users[i];
                        var found = true;
                        for (var k = 0; k < keywords.length; k++) {
                            if (user.f.toLowerCase().indexOf(keywords[k]) == -1 && user.l.toLowerCase().indexOf(keywords[k]) == -1) {
                                found = false;
                                break;
                            }
                        }
                        if (found) {
                            listToIterate.push(user);
                            
                            if(listToIterate.length > 400) {
                            	break;
                            }
                        }
                    }
                }
                
                var includeImages = listToIterate.length <= 10;
                
                for (var i = 0; i < listToIterate.length; i++) {
                    var user = listToIterate[i];
                    
                    if(includeImages) {
                    	var itemWithImage = new qx.ui.form.ListItem(user.f + " " + user.l, "/RegnskapServer/services/portal/portal_persons.php?action=image&personId="+user.p);
                    	itemWithImage.setIconPosition("right");
                    	itemWithImage.setModel(user);
                    	itemWithImage.getChildControl("icon").setMaxWidth(50); 
                    	itemWithImage.getChildControl("icon").setMaxHeight(50); 
                    	itemWithImage.getChildControl("icon").setScale(true); 
                    	itemWithImage.getChildControl("label").setWidth(130);
                    	
                    	this.__userList.add(itemWithImage);
                    } else {
                    	var item = new qx.ui.form.ListItem(user.f + " " + user.l);
                    	item.setModel(user);
                    	this.__userList.add(item);
                    }
                    
                    if(i > 400) {
                    	return;
                    }
                }
            } 
            catch (error) {
                if (console) {
                    console.log(error);
                }
            }
        },
        
        loadData: function(){
            var req = new qx.io.remote.Request("/RegnskapServer/services/portal/portal_persons.php?action=share", "GET", "application/json");
            
            var me = this;
            req.addListener("completed", function(response){
                me.__users = response.getContent();
                me.filterUsers();
            });
            req.send();
            
        },
        setupView: function(desktop){
        	this.__desktop = desktop;
            // Create the Window
            this.__win = new qx.ui.window.Window("Medlemmer", "frittregnskapmedlemsportal/system-search.png");
            var win = this.__win;
            win.setLayout(new qx.ui.layout.Grow());
            win.setShowStatusbar(true);
            win.setStatus("");
            win.setShowClose(false);
            win.setShowMinimize(false);
            win.setShowMaximize(false);
            win.setAllowMaximize(false);
            
            var searchBox = new qx.ui.groupbox.GroupBox("Finn person");
            var layout = new qx.ui.layout.Grid(2, 2);
            searchBox.setLayout(layout);
            
            layout.setRowFlex(1, 1);
            layout.setColumnFlex(1, 1);
            
            var label = new qx.ui.basic.Label("S&oslash;k");
            label.setRich(true);
            searchBox.add(label, {
                row: 0,
                column: 0
            });
            
            var me = this;
            
            this.__searchTextField = new qx.ui.form.TextField("");
            this.__searchTextField.addListener("keyup", function(event){
            
                if (!me.__eventKeyUpRunning) {
                    me.__eventKeyUpRunning = true;
                    qx.event.Timer.once(function(e){
                        me.filterUsers();
                    }, window, 1000);
                    
                }
                
            });
            
            searchBox.add(this.__searchTextField, {
                row: 0,
                column: 1
            });
            
            var userList = new qx.ui.form.List;
            userList.setScrollbarY("on");
            userList.setAllowStretchX(true);
            userList.setAllowStretchY(true);
            userList.setWidth(200);
            this.__userList = userList;
            
            userList.addListener("dblclick", function(e) {
            	var selection = userList.getSelection();
            	try {
            	me.openProfileForUser(selection[0].getModel());
            	} catch(error) {
            		if(console) {
            			console.log("openProfile:"+error);
            		}
            	}
            });
            
            
            searchBox.add(userList, {
                row: 1,
                column: 0,
                colSpan: 2
            });
            
            win.add(searchBox);
            
            desktop.add(win);
            win.open();
            
            this.loadData();
            
        },
        destruct: function(){
            this._disposeObjects("__win,", "__users", "__userList", "__eventKeyUpRunning", "__searchTextField","__desktop");
        }
        
    }
});
