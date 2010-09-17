var IService = new ideasaurus.Interface( 'IService' , [
	'setHandle',
	'getHandle',
	'setRequestType',
	'getRequestType',
	'setUrl',
	'getUrl',
	'setAccessor',
	'getAccessor',
	'setArgumentKey',
	'getArgumentKey',
	'setReturnDataType',
	'getReturnDataType' 
	 ] );
	 
var IServiceRequest = new ideasaurus.Interface( 'IServiceRequest' , [
	'setCallback' , 
	'getCallback' , 
	'setRequestArguments' , 
	'getRequestArguments',
	'setAsync',
	'getAsync'
	] )
	