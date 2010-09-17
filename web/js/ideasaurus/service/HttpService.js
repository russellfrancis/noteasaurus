ideasaurus.service.HttpService = (function() {
	
	var queue = new Array();
	var activeService;
	var activeServiceCallback;

	
	function processQueue()
	{
		
		activeService = queue[0];
		activeServiceCallback = activeService.getCallback()
		
		
		
		if( activeService.getRequestType() == 'POST' )
		{
			
			$.ajax( { 
				processData : false , 
				type : 'POST' , 
				success : serviceCompleteHandler , 
				error : serviceErrorHandler ,
				url : activeService.getUrl(),
				data : JSON.stringify( activeService.getRequestArguments() ),
				dataType : activeService.getReturnDataType(),
				async : activeService.getAsync()
				} )
				
		}	
		else
			$.get( activeService.getUrl() , activeService.getRequestArguments() , serviceCompleteHandler , activeService.getReturnDataType() )
		
	}
 	
 	function serviceErrorHandler( XMLHttpRequest, textStatus, errorThrown )
 	{
 		if(activeServiceCallback)
 			activeServiceCallback( null , textStatus )
 			
 		queue.shift()
		if( queue.length )
			processQueue()
 	}
 	
 	function serviceCompleteHandler( data , status )
 	{
 		if(activeServiceCallback)
 			activeServiceCallback( data , status )
 		
 		queue.shift()
		if( queue.length )
			processQueue()
 	}
	
	return {
		send: function( serviceRequest )
		{
			ideasaurus.Interface.ensureImplements( serviceRequest , IService , IServiceRequest )
			queue.push( serviceRequest );
			if( queue.length == 1 )
				processQueue()
		}
	}
	
})();