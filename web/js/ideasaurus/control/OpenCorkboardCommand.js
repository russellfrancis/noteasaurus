ideasaurus.control.OpenCorkboardCommand = function() {
	SWFAddress.addEventListener( SWFAddressEvent.CHANGE , this.addressChangeHandler );
}

ideasaurus.control.OpenCorkboardCommand.prototype = {
	
	execute : function( corkboard ) {
		
		var newPath = '/' + ideasaurus.model.AppStateProxy.getCORKBOARDPATH() + '/' + corkboard.getId() + '/';
		facade.retrieveProxy( ideasaurus.model.CorkboardProxy.getNAME() ).setSelectedCorkboard( corkboard )
		SWFAddress.setTitle( 'Noteasaurus :: ' + corkboard.getLabel() );
		
		
		if( corkboard.getId() != Number( SWFAddress.getPathNames()[1] ) )
			SWFAddress.setValue( newPath );
			
		var noteMediator = facade.retrieveMediator( ideasaurus.view.NoteMediator.getNAME() )
		noteMediator.remove();
		noteMediator.initialize();
	},
	
	addressChangeHandler : function( event ) {
		var corkboardProxy = facade.retrieveProxy( ideasaurus.model.CorkboardProxy.getNAME() );
		
		if( !SWFAddress.getPathNames()[0] )
			return
		
		if( corkboardProxy.getSelectedCorkboard()
			&& SWFAddress.getPathNames()[0] == ideasaurus.model.AppStateProxy.getCORKBOARDPATH() 
			&& Number( SWFAddress.getPathNames()[1] ) != corkboardProxy.getSelectedCorkboard().getId() )
		{
			$( '#corkboard-' + SWFAddress.getPathNames()[1] ).find( '.corkboard-btn' ).click();		
		}
		
		
	}
		
}

ideasaurus.control.OpenCorkboardCommand.getNAME = function() {
	return 'OpenCorkboardCommand';	
}