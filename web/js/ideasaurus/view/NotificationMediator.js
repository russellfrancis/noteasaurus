ideasaurus.view.NotificationMediator = function() {
	var _isOpen = false;
	var _openNotification = null;
	var _idIncrement = 0;
	var _currentId;
	
	this.template = facade.retrieveTemplate( ideasaurus.view.NotificationMediator.getTEMPLATENAME() );

	
	this.initialize = function( notification ) {
		ideasaurus.Interface.ensureImplements( notification , INotification )
		
		if( notification.getId() == _currentId && notification.getOpen() === false )
		{
			this.remove();
			return null;
		}
		else 
		{
			var thisObj = this;
			
			if( _isOpen )
				this.remove();
				
			var notificationElement = $( this.template )
			notificationElement.find( '.message' ).text( notification.getMessage() );
			
			if( !notification.getIsCloseable() )
				notificationElement.find( '#close-notification' ).remove();
				
			else
				notificationElement.find( '#close-notification' ).click( 
					function() {
						thisObj.remove();
					})
			
			if( notification.getType() == ideasaurus.model.vo.NotificationVO.getERRORTYPE() )
				notificationElement.addClass( 'error-notification' )
				
			notificationElement.css( 'display' , 'none' );
			$( 'body' ).append( notificationElement );
			notificationElement.slideDown( 'fast' );
			
			_isOpen = true
			_openNotification = notification;
			
			_idIncrement++;
			_currentId = _idIncrement
			return _idIncrement;
		}
	}
	
	this.remove = function() {
		$( '#notification' ).remove();
		_isOpen = false
		_openNotification = null;
		_currentId = null;
	}
	
	this.getOpenNotification = function() {
		return _openNotification;
	}
	
}

ideasaurus.view.NotificationMediator.getNAME = function() {
	return 'NotificationMediator'	
}

ideasaurus.view.NotificationMediator.getTEMPLATENAME = function() {
	return 'NotificationTemplate'	
}