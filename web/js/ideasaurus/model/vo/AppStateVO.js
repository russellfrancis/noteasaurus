ideasaurus.model.vo.AppStateVO = function( path , mediatorName , title , isProtected ) {
	
	var _path;
	var _title;
	var _mediatorName;
	var _isProtected;
	
	this.setPath = function( value ) {
		_path = value;
	}
	
	this.getPath = function() {
		return _path;
	}
	
	this.setMediatorName = function( value ) {
		_mediatorName = value;
	}
	
	this.getMediatorName = function() {
		return _mediatorName;
	}
	
	this.setTitle = function( value ) {
		_title = value
	}
	
	this.getTitle = function() {
		return _title;
	}
	
	this.setIsProtected = function( value ){
		if( typeof( value ) == 'boolean' )
			_isProtected = value
		else
			throw new Error( 'Error :: AppStateVO :: setIsProtected :: isProtected must be of the type Boolean' )
	}
	
	this.getIsProtected = function() {
		return _isProtected;
	}
	
	if( path ) this.setPath( path );
	if( mediatorName ) this.setMediatorName( mediatorName );
	if( title ) this.setTitle( title );
	if( isProtected != undefined ) this.setIsProtected( isProtected );
	
}