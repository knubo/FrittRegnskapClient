qx.Class.define("frittregnskapmedlemsportal.ProfilePicture", {
	extend : qx.core.Object,

	members : {
		createUploadButton : function(profileImage) {

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
		}

	}
});