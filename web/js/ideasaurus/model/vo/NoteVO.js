ideasaurus.model.vo.NoteVO = function( id, corkboardId , isCollapsed , x , y , width , height , skin , content ) { // Implements INote

	var _id;
	var _corkboardId;
	var _isCollapsed;
	var _x;
	var _y;
	var _width;
	var _height;
	var _skin;
	var _content;

	this.setId = function( value )
	{
		if( !_id && typeof( value ) == 'number' )
			_id = value;
		else 
			throw new Error( 'Error :: NoteVO :: setId :: id can only be set once and must be of the type Number' )
	}
	
	this.getId = function()
	{
		return _id;
	}
	
	this.setCorkboardId = function( value )
	{
		if( typeof(value) == 'number' )
			_corkboardId = value;
		else 
			throw new Error( 'Error :: NoteVO :: setCorkboarId :: corkboardId must be of the type Number')
	}


	this.getCorkboardId = function()
	{
		return _corkboardId;
	}

	this.setIsCollapsed = function(value)
	{
		if( typeof(value) == 'boolean')
			_isCollapsed = value
		else
			throw new Error('Error :: NoteVO :: setIsCollapsed :: isCollapsed must of the type Boolean')	
	}
	
	this.getIsCollapsed = function() 
	{
		return _isCollapsed;
	}
	
	this.setX = function( value )
	{
		if (typeof(value) == 'number')
			_x = value
		else 
			throw new Error('Error :: NoteVO :: setX :: x must of the type Number')
	}
	
	this.getX = function() 
	{
		return _x;
	}
	
	this.setY = function( value )
	{
		if( typeof( value ) == 'number')
			_y = value
		else
			throw new Error( 'Error :: NoteVO :: setY :: y must be of the type Number' )
	}
	
	this.getY = function()
	{
		return _y
	}
	
	this.setWidth = function(value)
	{
		if( typeof(value) == 'number' )
			_width = value
		else
			throw new Error( 'Error :: NoteVO :: setWidth :: width must be of the type Number');
	}
	
	this.getWidth = function( )
	{
		return _width
	}
	
	this.setHeight = function( value )
	{
		if( typeof(value) == 'number' )
			_height = value
		else
			throw new Error( 'Error :: NoteVO :: setHeight :: height must of the type Number ')
	}

	this.getHeight = function()
	{
		return _height;
	}
	
	this.setSkin = function( value )
	{
		_skin = value;
	}
	
	this.getSkin = function()
	{
		return _skin;
	}
	
	this.setContent = function( value )
	{
		_content = value;
	}
	
	this.getContent = function() 
	{
		return _content;
	}
	
	if ( id ) this.setId( id );
	if ( corkboardId ) this.setCorkboardId( id );
	if ( isCollapsed === true || isCollapsed === false) this.setIsCollapsed( isCollapsed );
	if ( x ) this.setX( x );
	if ( y ) this.setY( y );
	if ( width ) this.setWidth( width );
	if ( height ) this.setHeight( height );
	if ( skin ) this.setSkin( skin );
	if ( content ) this.setContent( content );
}
