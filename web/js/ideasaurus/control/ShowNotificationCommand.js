ideasaurus.control.ShowNotificationCommand = function() {
}

ideasaurus.control.ShowNotificationCommand.prototype = {
	
	execute : function( notification ) {
		return facade.retrieveMediator( ideasaurus.view.NotificationMediator.getNAME() ).initialize( notification );
	} 
	
}


ideasaurus.control.ShowNotificationCommand.getNAME = function() {
	return 'ShowNotificationCommand';	
}