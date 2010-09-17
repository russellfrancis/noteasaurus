/**
 * The ServiceDelegate class allows you to create standard services that can be invoked in similar 
 * fashion to a method call.
 * @class
 */
ideasaurus.service.ServiceDelegate = (function() 
{
	
	var methods = {}
	
	/**
	 * @function The addService method creates a new service provided by the delagate. After a service is added
	 * it can be called as if its a method member of the ServiceDelegate class. All services take two arguments:
	 * <args> and <callback>. Args are arguments to be passed to the remote procedure, and callback is a function
	 * that is called with the http response. The callback takes <data> and <status> arguments.  
	 */
	methods.addService = function( service )
	{
		ideasaurus.Interface.ensureImplements( service, IService );
		
		methods[service.getHandle()] = function( args , callback ) {
			
			var requestArgs = null;
			if( args )
			{
				requestArgs = {}
				if( isEnumerableObject( args ) )
				{
					if( service.getArgumentKey() )
						requestArgs[service.getArgumentKey()] = args;
					else
						requestArgs = args;
					
					if( service.getAccessor() )
						requestArgs = combine( requestArgs , service.getAccessor() )
				}
			}
			else
			{
				if( service.getAccessor() )
					requestArgs = service.getAccessor()
			}
			
			var serviceRequest = new ideasaurus.service.ServiceRequest( service.getRequestType(), service.getUrl() , requestArgs , callback , service.getReturnDataType() )
			ideasaurus.service.HttpService.send( serviceRequest );
			
		}
	}
	
	
	return methods;
	
})();



