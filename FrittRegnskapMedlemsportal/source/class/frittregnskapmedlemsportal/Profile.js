qx.Class.define("frittregnskapmedlemsportal.Profile", {
    extend: qx.core.Object,

    members: {
		createWindowProfile: function(desktop) {
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
		        row: 0,
		        column: 0
		    });
		    personbox.add(new qx.ui.form.TextField(""), {
		        row: 0,
		        column: 1
		    });
		    personbox.add(new qx.ui.basic.Label("Etternavn"), {
		        row: 0,
		        column: 2
		    });
		    personbox.add(new qx.ui.form.TextField(""), {
		        row: 0,
		        column: 3
		    });
		    
		    personbox.add(new qx.ui.basic.Label("Kj\u00f8nn"), {
		        row: 0,
		        column: 4
		    });
		    
		    var genderbox = new qx.ui.form.SelectBox();
		    genderbox.add(new qx.ui.form.ListItem("Kvinne"));
		    genderbox.add(new qx.ui.form.ListItem("Mann"));
		    personbox.add(genderbox, {
		        row: 0,
		        column: 5
		    });
		    
		    
		    personbox.add(new qx.ui.basic.Label("Epostadresse"), {
		        row: 1,
		        column: 0
		    });
		    personbox.add(new qx.ui.form.TextField(""), {
		        row: 1,
		        column: 1,
		        colSpan: 3
		    });
		    
		    personbox.add(new qx.ui.basic.Label("Telefon"), {
		        row: 1,
		        column: 4
		    });
		    personbox.add(new qx.ui.form.TextField(""), {
		        row: 1,
		        column: 5
		    });
		    
		    personbox.add(new qx.ui.basic.Label("Adresse"), {
		        row: 2,
		        column: 0
		    });
		    personbox.add(new qx.ui.form.TextField(""), {
		        row: 2,
		        column: 1,
		        colSpan: 3
		    });
		    
		    personbox.add(new qx.ui.basic.Label("Mobil"), {
		        row: 2,
		        column: 4
		    });
		    personbox.add(new qx.ui.form.TextField(""), {
		        row: 2,
		        column: 5
		    });
		    
		    personbox.add(new qx.ui.basic.Label("Postnr"), {
		        row: 3,
		        column: 0
		    });
		    personbox.add(new qx.ui.form.TextField(""), {
		        row: 3,
		        column: 1
		    });
		    
		    personbox.add(new qx.ui.basic.Label("Sted"), {
		        row: 3,
		        column: 2
		    });
		    personbox.add(new qx.ui.form.TextField(""), {
		        row: 3,
		        column: 3
		    });
		    
		    personbox.add(new qx.ui.basic.Label("Land"), {
		        row: 3,
		        column: 4
		    });
		    var countrybox = new qx.ui.form.SelectBox();
		    countrybox.add(new qx.ui.form.ListItem("Norge", "NO"));
		    countrybox.add(new qx.ui.form.ListItem("Sverige", "SE"));
		    countrybox.add(new qx.ui.form.ListItem("Danmark", "DK"));
		    countrybox.add(new qx.ui.form.ListItem("Finland", "FI"));
		    countrybox.add(new qx.ui.form.ListItem("Annet", "??"));
		    personbox.add(countrybox, {
		        row: 3,
		        column: 5
		    });
		    personbox.add(new qx.ui.basic.Label("F\u00f8dselsdato"), {
		        row: 4,
		        column: 0
		    });
		    personbox.add(new qx.ui.form.TextField(""), {
		        row: 4,
		        column: 1
		    });
		    
		    
		    
		    win.add(personbox, {
		        row: 0,
		        column: 0
		    });
		    
		    var bildebox = new qx.ui.groupbox.GroupBox("Bilde");
		    bildebox.setLayout(new qx.ui.layout.VBox(10));
		    bildebox.add(new qx.ui.basic.Image("frittregnskapmedlemsportal/knuterikLiten.jpg"));
		    
		    win.add(bildebox, {
		        row: 0,
		        column: 1
		    });
		    
		    var sharingbox = new qx.ui.groupbox.GroupBox("Vises for andre");
		    sharingbox.setLayout(new qx.ui.layout.Grid(4));
		    sharingbox.add(new qx.ui.form.CheckBox("Fornavn"), {
		        row: 0,
		        column: 0
		    });
		    sharingbox.add(new qx.ui.form.CheckBox("Etternavn"), {
		        row: 0,
		        column: 1
		    });
		    sharingbox.add(new qx.ui.form.CheckBox("Kj\u00f8nn"), {
		        row: 0,
		        column: 2
		    });
		    sharingbox.add(new qx.ui.form.CheckBox("Epostadresse"), {
		        row: 1,
		        column: 0
		    });
		    sharingbox.add(new qx.ui.form.CheckBox("Telefon"), {
		        row: 1,
		        column: 1
		    });
		    sharingbox.add(new qx.ui.form.CheckBox("Adresse"), {
		        row: 1,
		        column: 2
		    });
		    sharingbox.add(new qx.ui.form.CheckBox("Mobil"), {
		        row: 2,
		        column: 0
		    });
		    sharingbox.add(new qx.ui.form.CheckBox("Postnr"), {
		        row: 2,
		        column: 1
		    });
		    sharingbox.add(new qx.ui.form.CheckBox("Sted"), {
		        row: 2,
		        column: 2
		    });
		    sharingbox.add(new qx.ui.form.CheckBox("Land"), {
		        row: 3,
		        column: 0
		    });
		    sharingbox.add(new qx.ui.form.CheckBox("F\u00f8dselsdato"), {
		        row: 3,
		        column: 1
		    });
		    sharingbox.add(new qx.ui.form.CheckBox("Bilde"), {
		        row: 3,
		        column: 2
		    });
		    
		    
		    var sitebox = new qx.ui.groupbox.GroupBox("Eksterne nettsteder");
		    sitebox.setLayout(new qx.ui.layout.Grid(10, 5));
		    sitebox.add(new qx.ui.basic.Label("Hjemmeside"), {
		        row: 0,
		        column: 0
		    });
		    sitebox.add(new qx.ui.form.TextField(""), {
		        row: 0,
		        column: 1
		    });
		    
		    sitebox.add(new qx.ui.basic.Label("Twitter ID"), {
		        row: 1,
		        column: 0
		    });
		    sitebox.add(new qx.ui.form.TextField(""), {
		        row: 1,
		        column: 1
		    });
		    
		    sitebox.add(new qx.ui.basic.Label("Facebook ID"), {
		        row: 0,
		        column: 2
		    });
		    sitebox.add(new qx.ui.form.TextField(""), {
		        row: 0,
		        column: 3
		    });
		    
		    sitebox.add(new qx.ui.basic.Label("LinkedIn"), {
		        row: 1,
		        column: 2
		    });
		    sitebox.add(new qx.ui.form.TextField(""), {
		        row: 1,
		        column: 3
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
		        row: 2,
		        column: 0,
		        colSpan: 2
		    });
		    win.add(buttoncontainer, {
		        row: 3,
		        column: 0,
		        colSpan: 2
		    });
		}
	}
});