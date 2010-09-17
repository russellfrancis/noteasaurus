ideasaurus.model.CorkboardProxy = function() {
	
	var _corkboards = new Array();
	var _selectedCorkboard = null;
	
	this.setSelectedCorkboard = function( value ) {
		ideasaurus.Interface.ensureImplements( value , ICorkboard );
		_selectedCorkboard = value;
	}
	
	this.getSelectedCorkboard = function() {
		return _selectedCorkboard;
	}
	
	var retrieveCorkboardForUserService = new ideasaurus.service.Service(
		'retrieveCorkboardsForCurrentUser',
		'POST',
		'/remote-procedure-call/json',
		{procedure : 'retrieveCorkboardsForCurrentUser'},
		null,
		'json'
		);
		
	var createCorkboardService = new ideasaurus.service.Service(
		'createCorkboard',
		'POST',
		'/remote-procedure-call/json',
		{ procedure : 'createCorkboard' },
		'arguments',
		'json'
		);
	
	var updateCorkboardService = new ideasaurus.service.Service(
		'updateCorkboard',
		'POST',
		'/remote-procedure-call/json',
		{ procedure : 'updateCorkboard' },
		'arguments',
		'json'
		);
		
	var deleteCorkboardService = new ideasaurus.service.Service(
		'deleteCorkboard',
		'POST',
		'/remote-procedure-call/json',
		{ procedure : 'deleteCorkboard' },
		'arguments',
		'json'
		);
		
	ideasaurus.service.ServiceDelegate.addService( retrieveCorkboardForUserService );
	ideasaurus.service.ServiceDelegate.addService( createCorkboardService );	
	ideasaurus.service.ServiceDelegate.addService( updateCorkboardService );
	ideasaurus.service.ServiceDelegate.addService( deleteCorkboardService );
}		
		
ideasaurus.model.CorkboardProxy.prototype = {	
		
	retrieveCorkboardsForCurrentUser : function( callback ) {
		var thisObj = this
		
		ideasaurus.service.ServiceDelegate.retrieveCorkboardsForCurrentUser( null ,
			function( data , status) {
				if( !data )
				{
					facade.invokeCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , status )
					if( callback ) callback( [] )
				}
				
				
				else if( ideasaurus.service.ResponseValidator.validate( data ) )
				{
					var corkboards = new Array();
					
					var len = data.result.length;
					
					for( var i = 0; i < len; i++ )
					{
						var newCorkboard = new ideasaurus.model.vo.CorkboardVO( data.result[i].id , data.result[i].weight , data.result[i].label , data.result[i].is_focused )
						
						if( !thisObj.getSelectedCorkboard() && newCorkboard.isFocused() )
							thisObj.setSelectedCorkboard( newCorkboard )
						
						if( Number( SWFAddress.getPathNames()[1] ) == newCorkboard.getId() )
							thisObj.setSelectedCorkboard( newCorkboard )
							
						corkboards.push( newCorkboard )
					}
					
					if( !thisObj.getSelectedCorkboard() && corkboards.length )
						thisObj.setSelectedCorkboard( corkboards[0] )
					
					 
					callback( corkboards )
				}
				
			})
	},
	
	createCorkboard : function( corkboard , callback ) {
		ideasaurus.Interface.ensureImplements( corkboard , ICorkboard )
		ideasaurus.service.ServiceDelegate.createCorkboard( { label:corkboard.getLabel() , weight:corkboard.getWeight() , is_focused:corkboard.isFocused() } ,
			function( data , status ) {
				if( !data )
				{
					facade.invokeCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , status )
				}
				else if( ideasaurus.service.ResponseValidator.validate( data ) )
				{
					var newCorkboard = new ideasaurus.model.vo.CorkboardVO( data.result.id , data.result.weight , data.result.label , data.result.is_focused );
					callback( newCorkboard );
				}
			});
	},
	
	updateCorkboard : function( corkboard , callback ) {
		ideasaurus.Interface.ensureImplements( corkboard , ICorkboard )
		ideasaurus.service.ServiceDelegate.updateCorkboard( { id : corkboard.getId() , weight : corkboard.getWeight(), label : corkboard.getLabel(), is_focused : corkboard.isFocused() } ,
			function( data , status) {
				if( !data )
				{
					facade.invokeCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , status )
				}
				else if( ideasaurus.service.ResponseValidator.validate( data ) )
				{
					//var updatedCorkboard = new ideasaurus.model.vo.CorkboardVO( data.result.id, data.result.weight, data.result.label );
                    callback( new ideasaurus.model.vo.CorkboardVO( data.result.id, data.result.weight, data.result.label , data.result.is_focused ) );
				} 
				
					
			})
	},
	
	deleteCorkboard : function( corkboardId , callback ) {
		ideasaurus.service.ServiceDelegate.deleteCorkboard( { id : corkboardId } , 
			function( data , status ) {
				if( !data )
				{
					facade.invokeCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , status )
					if( callback ) callback( false )
				}
				else if( ideasaurus.service.ResponseValidator.validate( data ) )
					callback( true )
				else
					callback( false )
			}
		 );
	}
	
	
}

ideasaurus.model.CorkboardProxy.getNAME = function(){
	return 'CorkboardProxy';	
}


