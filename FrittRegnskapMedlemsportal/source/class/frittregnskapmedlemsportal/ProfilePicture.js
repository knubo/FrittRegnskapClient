qx.Class.define("frittregnskapmedlemsportal.ProfilePicture", {
	extend : qx.core.Object,
	__popup : null,
	__image : null,
	members : {
		createUploadButton : function(profileImage, win) {

			this.__popup = new qx.ui.popup.Popup(new qx.ui.layout.Grow());
			this.__image = new qx.ui.basic.Image();
			this.__image.addListener("mouseout", function() {
				this.__popup.hide();
			}, this);

			this.__popup.add(this.__image);

			var form = new frittregnskapmedlemsportal.UploadForm('uploadFrm',
					'/RegnskapServer//services/portal/portal_persons.php?action=imageupload');
			form.setParameter('rm', 'upload');
			form.setLayout(new qx.ui.layout.Basic());

			var file = new frittregnskapmedlemsportal.UploadButton('uploadfile', 'Endre profilbilde', 'icon/16/actions/document-save.png');
			form.add(file, {
				left : 0,
				top : 0
			});

			form.addListener('completed', function(e) {
				var response = this.getIframeHtmlContent();

				if(response.length > 0) {
					win.setStatus(response);
				}
				
				profileImage.setSource("/RegnskapServer/services/portal/portal_persons.php?action=myimage&foolcache="
						+ new Date().getTime());
			});

			
			
			form.addListener('sending', function(e) {
				this.debug('sending');
			});

			file.addListener('changeFileName', function(e) {
				if (e.getData() != '') {
					form.send();
				}
			});
			return form;
		},
		addMouseOverFullImage : function(profileImage) {
			profileImage.addListener('mouseover', function() {
				this.__image.setSource(profileImage.getSource());
				this.__popup.placeToPoint(profileImage.getContainerLocation());
				this.__popup.show();
			}, this);
		},
		destruct : function() {
			this._disposeObjects("__popup", "__image");
		}
	}
});