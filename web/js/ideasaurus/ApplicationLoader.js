/**
 * The ApplicationLoader can be configured to load classes and templates used by the application
 */

ideasaurus.ApplicationLoader = (function() {
	
	var _callback;
	var progressBar;
	var loadText;
	
	function startLoad() {
		
		var loadWindow = $(
            '<div id="loadWindow" class="note-box">' +
            '<h1>Loading Notes</h1>' +
            '<div id="progressbarContainer">' +
                '<div id="progressBar"></div>' +
            '</div>' +
            '<p id="loadText"></p>'+
            '</div>'
        );
		$( 'body' ).append( loadWindow );

		progressBar = $('#progressBar')
		loadText = $('#loadText')
		
		loadClasses()
	}
	
	function loadClasses() {

		var classLoadStep = 0;
		var classes = new Array(
			//service package
			'ideasaurus.service.interfaces',
			'ideasaurus.service.HttpService',
			'ideasaurus.service.Service',
			'ideasaurus.service.ServiceRequest',
			'ideasaurus.service.ServiceDelegate',
			'ideasaurus.service.ResponseValidator',
			//control package
			'ideasaurus.control.interfaces',
			'ideasaurus.control.Controller',
			'ideasaurus.control.ApplicationStartupCommand',
			'ideasaurus.control.ChangeStateCommand',
			'ideasaurus.control.OpenCorkboardCommand',
			'ideasaurus.control.ShowNotificationCommand',
			'ideasaurus.control.HTTPErrorCommand',
			//model package
			'ideasaurus.model.interfaces',
			'ideasaurus.model.Model',
			'ideasaurus.model.NoteProxy',
			'ideasaurus.model.UserProxy',
			'ideasaurus.model.CorkboardProxy',
			'ideasaurus.model.AppStateProxy',
			//model.vo package
			'ideasaurus.model.vo.NoteVO',
			'ideasaurus.model.vo.UserVO',
			'ideasaurus.model.vo.CorkboardVO',
			'ideasaurus.model.vo.AppStateVO',
			'ideasaurus.model.vo.NotificationVO',
			//view package
			'ideasaurus.view.interfaces',
			'ideasaurus.view.View',
			'ideasaurus.view.ApplicationMediator',
			'ideasaurus.view.NoteMediator',
			'ideasaurus.view.LoginMediator',
			'ideasaurus.view.VerifyMediator',
            'ideasaurus.view.RegisterMediator',
			'ideasaurus.view.CorkboardMediator',
			'ideasaurus.view.NotificationMediator',
            //util package
			'ideasaurus.util.Utility',
			//application
			'ideasaurus.Facade',
			'ideasaurus.ApplicationFacade'
		)
		
		var classLen = classes.length;
		next();
		
		function next() {
			if( ( classLoadStep + 1 ) <= classLen )
			{
				ideasaurus.importClass( classes[classLoadStep] , next );
				classLoadStep++;
			}
			else
            {
				loadTemplates();
            }

			progressBar.css( 'width' , ( ( classLoadStep / classLen ) * 100 ) + '%' )
			
		}

	}
	
	function loadTemplates() {
		var templateLoadStep = 0;

		var templates = new Array(
			{name: ideasaurus.view.NoteMediator.getTEMPLATENAME() , path: 'ideasaurus/view/template/note.html' },
			{name: ideasaurus.view.ApplicationMediator.getTEMPLATENAME() , path: 'ideasaurus/view/template/application.html'},
			{name: ideasaurus.view.LoginMediator.getTEMPLATENAME() , path: 'ideasaurus/view/template/login.html' },
			{name: ideasaurus.view.VerifyMediator.getTEMPLATENAME() , path: 'ideasaurus/view/template/verify.html' },
			{name: ideasaurus.view.CorkboardMediator.getTEMPLATENAME() , path: 'ideasaurus/view/template/corkboard.html' },
			{name: ideasaurus.view.NotificationMediator.getTEMPLATENAME() , path: 'ideasaurus/view/template/notification.html' },
            {name: ideasaurus.view.RegisterMediator.getTEMPLATENAME(), path: 'ideasaurus/view/template/register.html' }
		)
		var templateLen = templates.length;
		next();
		
		function next() {
			if( ( templateLoadStep + 1 ) <= templateLen )
			{
				ideasaurus.view.View.registerTemplate( templates[templateLoadStep].name , templates[templateLoadStep].path , next )
				templateLoadStep++;
			} 
			else
				loadComplete();

			progressBar.css( 'width' , ( ( templateLoadStep / templateLen ) * 100 ) + '%' )
		}
		
	}
	
	function loadComplete() {
		$('#loadWindow').remove();
		
		if( _callback )
			_callback()
	}
	
	return {
		load : function( callback ) {
			if( callback ) _callback = callback;
			startLoad();
		}
	}
	
})();

//ideasaurus.importClass( 'ideasaurus.model.TestClass' )