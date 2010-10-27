qx.Class.define("frittregnskapmedlemsportal.Membersearch", {
    extend: qx.core.Object,
    
    members: {
        __win: null,
        __users: null,
        __userList: null,
        __eventKeyUpRunning: false,
        __searchTextField: null,
        __desktop: null,
        openProfileForUser: function(obj) {
    		var win = new qx.ui.window.Window(obj.f+ " "+obj.l, "frittregnskapmedlemsportal/system-users.png");

    		this.__desktop.add(win);
            win.setShowMinimize(false);
            win.setShowMaximize(false);
            win.setAllowMaximize(false);

            win.open();

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
            	me.openProfileForUser(selection[0].getModel());            	
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
