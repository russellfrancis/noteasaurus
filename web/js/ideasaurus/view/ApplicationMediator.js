ideasaurus.view.ApplicationMediator = function() { // Implements IMediator
	this.template = facade.retrieveTemplate( ideasaurus.view.ApplicationMediator.getTEMPLATENAME() );
}

ideasaurus.view.ApplicationMediator.prototype = {
	initialize : function() {
		var jqInterface = $( this.template )
		$('body').append( jqInterface );
        $('#cbar-logout-btn').click( this.logoutSubmitHandler );

        var userProxy = facade.retrieveProxy( ideasaurus.model.UserProxy.getNAME() );
        $('#user-email-label').text( "Welcome " + userProxy.getUser().getEmail() + "!");

        var corkboardMediator = facade.retrieveMediator( ideasaurus.view.CorkboardMediator.getNAME() );
        var corkboardProxy = facade.retrieveProxy( ideasaurus.model.CorkboardProxy.getNAME());
        var noteProxy = facade.retrieveProxy( ideasaurus.model.NoteProxy.getNAME() );
        var noteMediator = facade.retrieveMediator( ideasaurus.view.NoteMediator.getNAME() );
        
        corkboardMediator.initialize();
        
        $( '#cbar-new-corkboard-btn' ).click( 
			function() {
				corkboardProxy.createCorkboard( new ideasaurus.model.vo.CorkboardVO( null , $( '#corkboard-list' ).children().length, 'Untitled Corkboard' , false ) , 
					function( corkboard ) {
						corkboardMediator.addCorkboard( corkboard )
						if( $( '#corkboard-list' ).children().length == 2 )
							$( '#corkboard-' + corkboard.getId() + ' > .corkboard-btn' ).click();
					})
			} )	
        
        $( '#cbar-new-note-btn' ).click(
        	function() {
        		if( corkboardProxy.getSelectedCorkboard() )
        		{
	        		noteProxy.createNote( ideasaurus.model.NoteProxy.defaultNoteFactory( corkboardProxy.getSelectedCorkboard().getId() ) ,
	        		function( note ) {
	        			noteMediator.addNote( note );
	        		})
        		} 
        		else
        		{
        			corkboardProxy.createCorkboard( new ideasaurus.model.vo.CorkboardVO( null , $( '#corkboard-list' ).children().length, 'Untitled Corkboard' , false ) , 
					function( corkboard ) {
						corkboardMediator.addCorkboard( corkboard )
						$( '#corkboard-' + corkboard.getId() + ' > .corkboard-btn' ).click();
						$( '#cbar-new-note-btn' ).click()
					})
        		}
        	}
        )
	},
	remove : function() {
		$( '#application' ).remove();
	},
	logoutSubmitHandler : function( e ) {
        var utility = new ideasaurus.util.Utility();
		var userProxy = facade.retrieveProxy( ideasaurus.model.UserProxy.getNAME() );
		userProxy.logout( function() {
            utility.removeCookie("authtoken");
            facade.invokeCommand( ideasaurus.control.ChangeStateCommand.getNAME() , ideasaurus.model.AppStateProxy.getLOGINPATH() )
		} );
	}
}

ideasaurus.view.ApplicationMediator.getNAME = function() {
	return 'ApplicationMediator';	
}

ideasaurus.view.ApplicationMediator.getTEMPLATENAME = function() {
	return 'ApplicationMediatorTemplate';	
}