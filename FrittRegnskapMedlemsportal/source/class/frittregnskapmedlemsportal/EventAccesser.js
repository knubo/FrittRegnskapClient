qx.Class.define("frittregnskapmedlemsportal.EventAccesser", {
    extend: qx.core.Object,

    members: {
          __event : null,
          __inputs : {},
          __index : {},
         setEventObject : function(event) {
              this.__event = event;
              this.indexGroups();
              this.setWidgets();
          },
          
          indexGroups : function() {
              var groups = this.__event.groups; 

              for(var group in groups) {
                var loc = groups[group];
                this.__index[loc["row"] + "-" + loc["col"]] = group;
              }
          },
          setWidgets: function() {
              var choices = this.__event.choices;

              var choicePerGroup = {};

              for(var i in choices) {
                  var choice = choices[i];

                  if(choice.hasOwnProperty("inputType") && choice.inputType != "") {
                      this.setSingleWidget(choice);
                      continue;
                  }

                  var soFar;
                  if(!choicePerGroup.hasOwnProperty(choice.group)) {
                     soFar = [];
                     choicePerGroup[choice.group] = soFar;
                  } else {
                     soFar = choicePerGroup[choice.group];
                  }
                  
                  soFar.push(choice);
              }              

              for(var i in choicePerGroup) {
                var choices = choicePerGroup[i];
                
                if(choices.length == 1) {
                    this.__inputs[choices[0].group] = new qx.ui.form.CheckBox(choices[0].name);
                } else {
                    var select = new qx.ui.form.SelectBox();
                    this.__inputs[choices[0].group] = select;
                    
                    var model = new qx.data.Array();
                    for(var c in choices) {
                        var choice = choices[c];
                        
                        model.push(choice.name);
                    }
                    
                    new qx.data.controller.List(model, select);
                }
              }
          },
        
          setSingleWidget: function(choice) {
             if(choice.inputType == "Textarea") {
                this.__inputs[choice.group] = new qx.ui.form.TextArea();
             } else if(choice.inputType == "Textfield") {
                this.__inputs[choice.group] = new qx.ui.form.TextField();
             } else if(choice.inputType == "Checkbox") {
                this.__inputs[choice.group] = new qx.ui.form.CheckBox(choice.name);
             } else {
                alert(choice.inputType);
             }
          },          

          hasWidget : function(row, col) {
              if(this.__event.html.hasOwnProperty(row+":"+col)) {
                  return true;
              }
          
              return this.__index.hasOwnProperty(row + "-" + col);
          },
          
          getWidget: function (row, col) {
              if(this.__event.html.hasOwnProperty(row+":"+col)) {
                 var html = new qx.ui.basic.Label();
                 html.setRich(true);
                 html.setValue(this.__event.html[row+":"+col]);
                 return html;
              }         
          
             var group = this.__index[row + "-" + col];
             var widget = this.__inputs[group];
             
             if(widget == null) {
                return new qx.ui.basic.Label("missing "+group);
             }          
             return widget;
          },
          
          getData : function() {
              var data = {};
          
              data["id"] = this.__event.id;
          
              for(var group in this.__inputs) {
                var input = this.__inputs[group];
                 if(input.getValue) {
                     data[group] = input.getValue(); 
                 } else {
                    var selected = input.getSelection();

                    data[group] = selected[0].getModel(); 
                    
                 }
              }         
              return data;
          }
          
          
    }
    
});