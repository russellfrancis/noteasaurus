ideasaurus.control.Controller = (function() {
	
	var commands = new Array();
	
	return {
		registerCommand : function( name , command ) {
			var command = new command();
			ideasaurus.Interface.ensureImplements( command , ICommand )
			commands.push( { name: name , command: command } )
		},
		invokeCommand : function( name , params ){
			for( var i in commands )
			{
				if( commands[i].name == name ){
					var returnVal = commands[i].command.execute( params );
					if( returnVal )
						return returnVal
					else 
						return true;
				}
			}
			throw new Error( 'Error :: Controller class :: Command not found' );
		}
		
	}
	
	
})();