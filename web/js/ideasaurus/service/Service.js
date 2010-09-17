
/**
 * Creates a new Service class which describes an http service
 * @class Service 
 * @implements IService
 * @param {string} [handle] The name of the service. Can be passed as an argument or set with the setter method.
 * @param {string} [requestType] The type of request. Can be either POST or GET.  Can be passed as an argument or set with the setter method.
 * @param {string} [url] The url for the http request.  Can be passed as an argument or set with the setter method.
 * @param {object} [accessor] If the service has a standard parameter for access it can be set here as a key/value pair, such as {procedure:'myProcedure'}.  Can be passed as an argument or set with the setter method.
 * @param {string} [argumentKey] If the service has a standard key for arguments it can be set here.  Can be passed as an argument or set with the setter method.
 * @param {string} [returnDataType] If data is being return in a particular format it can be set here to get parsed. Can be 'xml', 'html', 'script', 'json', 'jsonp', or 'text'.  Can be passed as an argument or set with the setter method 
 */
ideasaurus.service.Service = function( handle, requestType , url , accessor , argumentKey , returnDataType ) // Implements IService
{
	
	var _handle;
	var _url;
	var _requestType;
	var _accessor;
	var _argumentKey;
	var _returnDataType;
	
	/**
	 * Sets the handle property
	 * @fuction
	 * @public
	 * @param {string} value The handle for the service
	 * @throws {TypeError} Throws error if the parameter is not a of the type String
	 */
	this.setHandle = function( value ) 
	{
		if( typeof( value ) == 'string' )
			_handle = value
		else
			throw new Error( 'Error: Service class :: Handle property must be typed String' )
	}
	
	/**
	 * Gets the handle for the service
	 * @function
	 * @public 
	 * @return {string} The handle for the service
	 * @throws {ReferenceError} Throws error if the handle was not set (required)
	 */
	this.getHandle = function() 
	{
		if( _handle )
			return _handle;
		else
			throw new Error( 'Error: Service class :: Handle property was not set' )
	}
	
	/**
	 * Sets the url property
	 * @fuction
	 * @public
	 * @param {string} value The url for the service
	 * @throws {TypeError} Throws error if the parameter is not a of the type String
	 */
	this.setUrl = function( value ) 
	{
		if( typeof( value ) == 'string' )
			_url = value
		else
			throw new Error( 'Error: Service class :: URL property must be typed String' )
	}
	
	/**
	 * Gets the url for the service
	 * @function
	 * @public 
	 * @return {string} The url for the service
	 * @throws {ReferenceError} Throws error if the property was not set (required)
	 */
	this.getUrl = function() 
	{
		if( _url )
			return _url;
		else
			throw new Error( 'Error: Service class :: URL property was not set' )
	}
	
	/**
	 * Sets the requestType property
	 * @fuction
	 * @public
	 * @param {string} value The requestType for the service, can be either POST or GET
	 * @throws {TypeError} Throws error if the parameter is not a of the type String and equal to either POST or GET
	 */
	this.setRequestType = function( value ) 
	{
		if ( value == 'POST' || value ==  'GET' ) 
			_requestType = value;		
		else 
			throw new Error( 'Error: Service class :: requestType must be one of "POST" or "GET"' );
	}
	
	/**
	 * Gets the url for the service
	 * @function
	 * @public 
	 * @return {string} The url for the service
	 * @throws {ReferenceError} Throws error if the property was not set (required)
	 */
	this.getRequestType = function()
	{
		if( _requestType )
			return _requestType;
		else
			throw new Error( 'Service class :: requestType property was not set' )
	}

	/**
	 * Sets the accessor for the service
	 * @function
	 * @public 
	 * @param {object} value Key/Value pair for the accessor
	 * @throws {TypeError} Throws error the parameter is not an enumerable object
	 */
	this.setAccessor = function( value )
	{
		if( isEnumerableObject( value , 'string') )
			_accessor = value;
		else
			throw new Error( 'Service class :: accessor must be an enumberable object with key/value pair (key=string)' )
	}
	
	/**
	 * Gets the accessor for the service
	 * @function
	 * @public 
	 * @return {object} The key/value pair for the accessor
	 */
	this.getAccessor = function()
	{
		if( _accessor )
			return _accessor;
		else
			return null
	}
	
	/**
	 * Sets the argumentKey for the service
	 * @function
	 * @public 
	 * @param {string} value
	 * @throws {TypeError} Throws error the parameter is not a String
	 */
	this.setArgumentKey = function( value )
	{
		if( typeof(value) == 'string' )
			_argumentKey = value
		else
			throw new Error( 'Service class :: argumentKey property must be typed String' )
	}
	
	/**
	 * Gets the argumentKey property
	 * @function 
	 * @public
	 * @return {string} The value of the argumentKey property
	 */
	this.getArgumentKey = function()
	{
		if( _argumentKey )
			return _argumentKey
		else
			return null
	}

	/**
	 * Sets the returnDataType for the service
	 * @function
	 * @public 
	 * @param {string} value
	 * @throws {TypeError} Throws error the parameter is not a String or is not equal to 'xml', 'html', 'script', 'json', 'jsonp', or 'text'. 
	 */
	this.setReturnDataType = function( value )
	{
		// Check to make sure return type is valid for jquery request	
		if( value && (
			value != 'xml' && 
			value != 'html' &&
			value != 'script' &&
			value != 'json' &&
			value != 'jsonp' &&
			value != 'text' )
			 )
			throw new Error( 'Error: Service class :: Return data type of '+value+' is invalid' )	
		else
			_returnDataType = value;
	}
	
	/**
	 * Gets the returnDataType property
	 * @function 
	 * @public
	 * @return {string} The value of the returnDataType property
	 */
	this.getReturnDataType = function()
	{
		if( _returnDataType )
			return _returnDataType
		else
			return null;
	}

	if( handle )
		this.setHandle( handle );
	if( requestType )
		this.setRequestType( requestType );
	if( url )
		this.setUrl( url )
	if( accessor )
		this.setAccessor( accessor )
	if( argumentKey )
		this.setArgumentKey( argumentKey )
	if( returnDataType )
		this.setReturnDataType( returnDataType )
	
}

