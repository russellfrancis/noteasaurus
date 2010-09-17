ideasaurus.control.ChangeStateCommand = function() {
	SWFAddress.setHistory( false )
	SWFAddress.setStrict(false)
	SWFAddress.addEventListener( SWFAddressEvent.CHANGE , this.addressChangeHandler )
}

ideasaurus.control.ChangeStateCommand.prototype = {
	execute : function( path ) {
		
		var appStateProxy = facade.retrieveProxy( ideasaurus.model.AppStateProxy.getNAME() )

		if( appStateProxy.getStateByPath( path ).getIsProtected() && !facade.retrieveProxy( ideasaurus.model.UserProxy.getNAME() ).getUser() )
		{
			SWFAddress.setHistory( false )
			appStateProxy.setCurrentState( ideasaurus.model.AppStateProxy.getLOGINPATH() );
			SWFAddress.setValue( '/'+appStateProxy.getCurrentState().getPath()+'/' )
			SWFAddress.setTitle( appStateProxy.getCurrentState().getTitle() );
		}
		else 
		{
			if( appStateProxy.getCurrentState() && !SWFAddress.getHistory() )
				SWFAddress.setHistory( true )
				
			appStateProxy.setCurrentState( path )
			SWFAddress.setTitle( appStateProxy.getCurrentState().getTitle() );
			
			if( SWFAddress.getPathNames()[0] != path )
				SWFAddress.setValue( '/'+appStateProxy.getCurrentState().getPath()+'/' )
		}
	},
	
	addressChangeHandler : function( event ) {
		var appStateProxy = facade.retrieveProxy( ideasaurus.model.AppStateProxy.getNAME() )
		
		if( !event.pathNames[0] )
			return
		
		// true if attempting to access protected area when not logged in
		if( appStateProxy.getStateByPath( event.pathNames[0] ).getIsProtected() && !facade.retrieveProxy( ideasaurus.model.UserProxy.getNAME() ).getUser() )
		{
			SWFAddress.setHistory( false )
			appStateProxy.setCurrentState( ideasaurus.model.AppStateProxy.getLOGINPATH() );
			SWFAddress.setValue( '/'+appStateProxy.getCurrentState().getPath()+'/' )
			SWFAddress.setTitle( appStateProxy.getCurrentState().getTitle() );
		}
		else 
		{
			if( appStateProxy.getCurrentState() && !SWFAddress.getHistory() )
				SWFAddress.setHistory( true )
				
			if( event.pathNames[0] != appStateProxy.getCurrentState().getPath() )
			{
				appStateProxy.setCurrentState( event.pathNames[0] )
				SWFAddress.setTitle( appStateProxy.getCurrentState().getTitle() );
			}
		}
	}	
}

ideasaurus.control.ChangeStateCommand.getNAME = function() { return 'ChangeStateCommand'; }