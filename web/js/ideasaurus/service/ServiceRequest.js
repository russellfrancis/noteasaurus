
ideasaurus.service.ServiceRequest = function( requestType, url, requestArguments, callback , returnDataType , async ) // Implements IService, IServiceRequest
{
	
	ideasaurus.service.ServiceRequest.superclass.constructor.call( this , null, requestType , url , null, null , returnDataType );
	
	var _requestArguments;
	var _callback;
	var _async = true;
	
	this.setRequestArguments = function( value )
	{
		if( isEnumerableObject( value ) )
			_requestArguments = value;
			
		else
			throw new Error( 'Error: ServiceRequest class :: requestArguments property must be an enumerable object' );
	}
	
	this.getRequestArguments = function() 
	{
		if( _requestArguments )
			return _requestArguments
		else
			return null
	}
	
	this.setCallback = function( value )
	{
		if( value instanceof Function )
			_callback = value;
		else
			throw new Error( 'Error: Service class :: callback property must be of the type Function' )
	}
	
	this.getCallback = function()
	{
		if( _callback )
			return _callback
		else 
			return null
	} 
	
	this.setAsync = function( value ) {
		if ( typeof( value ) == 'boolean' )
		{
			_async = value
		}
		else
			throw new Error( 'Error :: ServiceRequest class :: async property must be of the type Boolean' )
	}
	
	this.getAsync = function() {
		return _async
	}
	
	if( requestArguments )
		this.setRequestArguments( requestArguments )
	if( callback )
		this.setCallback( callback )
	if( async === false )
		this.setAsync( async )
	                  
}

ideasaurus.extend( ideasaurus.service.ServiceRequest , ideasaurus.service.Service  );