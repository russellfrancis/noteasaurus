ideasaurus.model.vo.UserVO = function( id , email ) { // Implements IUserVO
	var _id;
	var _email;
	
	this.setId = function( value ) {
		if( !_id )
			_id = value;
	}
	
	this.getId = function() {
		return _id
	}
	
	this.setEmail = function( value ) {
		_email = value
	}
	
	this.getEmail = function() {
		return _email;
	}
	
	if( id ) this.setId( id );
	if( email ) this.setEmail( email );
}