ideasaurus.view.View = (function() {
	
	var _mediators = new Array();
	var _templates = new Array();
	
	return {
		registerMediator : function( name , mediator ){
			ideasaurus.Interface.ensureImplements( mediator , IMediator );
			_mediators.push( {name : name , mediator : mediator } )
		},
		retrieveMediator : function( name ) {
			for( var i in _mediators )
			{
				if( _mediators[i].name == name )
					return _mediators[i].mediator
			}
			throw new Error( 'Error :: View class :: retrieveMediator :: mediator '+name+' could not be found' )
		},
		registerTemplate : function( name , path , callback )
		{
			
			var serviceRequest = new ideasaurus.service.ServiceRequest( 'GET' , 
																		ideasaurus.applicationPath + path , 
																		null , 
																		function(data, success) { _templates.push( {name:name , template:data}) ; callback()},
																		'html'
																		 );
			
			ideasaurus.service.HttpService.send( serviceRequest );
			
		},
		retrieveTemplate : function( name )
		{
			for( var i in _templates )
			{
				if( _templates[i].name == name )
					return _templates[i].template;
			}
			throw new Error( 'Error :: View class :: retrieveTemplate :: template '+name+' could not be found')
		}
		
	}
	
	
})();