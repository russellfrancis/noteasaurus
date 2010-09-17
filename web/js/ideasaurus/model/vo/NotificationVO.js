ideasaurus.model.vo.NotificationVO = function( type , message , isCloseable , open ) {
	
	var _type // error || notification
	var _message //string
	var _isCloseable //boolean
	var _open //boolean
	var _id // number
	this.setType = function( value ) {
		if ( value == ideasaurus.model.vo.NotificationVO.geNOTIFICATIONTYPE() || value == ideasaurus.model.vo.NotificationVO.getERRORTYPE() )
			_type = value;
		
		else
			throw new Error( 'Error :: NotificationVO :: setType :: type parameter must be set to "error" or "notification". Use the getERRORTYPE() and getNOTIFICATIONTYPE() constants.' )
	}
	
	this.getType = function() {
		return _type;
	}
		
	this.setMessage = function( value ) {
		_message = value;
	}	
	
	this.getMessage = function() {
		return _message;
	}
	
	this.setIsCloseable = function( value ) {
		if( typeof( value  ) == 'boolean' )
			_isCloseable = value
		else
			throw new Error( 'Error :: NotificationVO :: setIsClosable :: isClosable parameter must be of the type boolean' )
	}	
	
	this.getIsCloseable = function() {
		return _isCloseable;
	}
		
	this.setOpen = function( value ) {
		if( typeof( value  ) == 'boolean' )
			_open = value
		else
			throw new Error( 'Error :: NotificationVO :: setOpen :: open parameter must be of the type boolean' )
	}	
	
	this.getOpen = function() {
		return _open;
	}
	
	this.setId = function( value ) {
		_id = value
	}

	this.getId = function( value ) {
		return _id;
	}
		
	if( type ) this.setType( type );
	if( message ) this.setMessage( message );
	if( isCloseable != undefined ) this.setIsCloseable( isCloseable );
	if( open != undefined ) this.setOpen( open );
}


ideasaurus.model.vo.NotificationVO.getERRORTYPE = function() { return 'error'; }
ideasaurus.model.vo.NotificationVO.geNOTIFICATIONTYPE = function() { return 'notification'; }