ideasaurus.control.HTTPErrorCommand = function() {
	
}

ideasaurus.control.HTTPErrorCommand.prototype = {

	execute : function( message ) {
		
		var notificationMessage;
		
		switch( message ) {
			
			case 'timeout':
			notificationMessage = 'There appears to be a internet connection problem. Continuing to use this application may result in unsaved data or irregular behaviour.';
			break;
			
			default:
			notificationMessage = 'There was an error in the application that may prevent it from behaving normally. Try reloading the page or returning later.';
			break;
		}
		
		var notification = new ideasaurus.model.vo.NotificationVO( ideasaurus.model.vo.NotificationVO.getERRORTYPE() , notificationMessage , true , true )
		facade.invokeCommand( ideasaurus.control.ShowNotificationCommand.getNAME() , notification )
	}
}

ideasaurus.control.HTTPErrorCommand.getNAME = function() {
	return 'HTTPErrorCommand';
}
