ideasaurus.model.AppStateProxy = function() {
	
	var _currentStatePath = null;
	var _currentState = null;
	var _states = new Array(
		new ideasaurus.model.vo.AppStateVO( ideasaurus.model.AppStateProxy.getLOGINPATH(), ideasaurus.view.LoginMediator.getNAME() , 'Noteasaurus :: Login' , false ),
		new ideasaurus.model.vo.AppStateVO( ideasaurus.model.AppStateProxy.getREGISTERPATH(), ideasaurus.view.RegisterMediator.getNAME() , 'Noteasaurus :: Register' , false ),
		new ideasaurus.model.vo.AppStateVO( ideasaurus.model.AppStateProxy.getVERIFYPATH(), ideasaurus.view.VerifyMediator.getNAME() , 'Noteasaurus :: Verify your Account' , false ),
		new ideasaurus.model.vo.AppStateVO( ideasaurus.model.AppStateProxy.getCORKBOARDPATH(), ideasaurus.view.ApplicationMediator.getNAME() , 'Notesaurus :: Corkboard' , true )
	);
	
	this.setCurrentState = function( path ){
		if( _currentStatePath != path ) 
		{
			if( _currentStatePath )
			{
				var currentState = this.getStateByPath( _currentStatePath )
				var currentStateMediator = facade.retrieveMediator( currentState.getMediatorName() )
				currentStateMediator.remove();
			}
			var newState = this.getStateByPath( path )
			var newStateMediator = facade.retrieveMediator( newState.getMediatorName() )
			newStateMediator.initialize();
			_currentState = newState;
			_currentStatePath = newState.getPath();
		}		
	}
	
	this.getCurrentState = function() {
		return _currentState;
	}
	
	this.getStateByPath = function( path ) {
		for ( var i in _states )
		{
			if ( _states[i].getPath() == path )
			{
				return _states[i];
			}
		}
		return null;
	}
	
}

ideasaurus.model.AppStateProxy.getNAME = function() { return 'AppStateProxy'; }
ideasaurus.model.AppStateProxy.getLOGINPATH = function() { return 'login'; }
ideasaurus.model.AppStateProxy.getREGISTERPATH = function() { return 'register'; }
ideasaurus.model.AppStateProxy.getVERIFYPATH = function() { return 'verify'; }
ideasaurus.model.AppStateProxy.getCORKBOARDPATH = function() { return 'corkboard'; }
