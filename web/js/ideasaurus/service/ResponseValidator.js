ideasaurus.service.ResponseValidator = function() {
	
	
}

ideasaurus.service.ResponseValidator.validate = function( response ) {
	
	switch( response.status_code ){
		case 0: return true;
		break;
		
		case 2001://logged out
		facade.retrieveProxy( ideasaurus.model.UserProxy.getNAME() ).setUser( null );
		facade.invokeCommand( ideasaurus.control.ChangeStateCommand.getNAME() , ideasaurus.model.AppStateProxy.getLOGINPATH() )
		return false;
		break;
		
		default://any other failure
		
		var notification = new ideasaurus.model.vo.NotificationVO( ideasaurus.model.vo.NotificationVO.getERRORTYPE() , detailed_message , true , true )
		facade.invokeCommand( ideasaurus.control.ShowNotificationCommand.getNAME() , notification )
		
		
		return false;
		break;
		
	}
	
	
	
	
}