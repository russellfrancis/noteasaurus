ideasaurus.model.UserProxy = function() {
	
	var _user = null;
	
	this.setUser = function ( userVO ) {
        if (userVO != null) {
            ideasaurus.Interface.ensureImplements( userVO , IUserVO )
        }
		_user = userVO
	}
	
	this.getUser = function() {
		return _user;
	}
	
	var retrieveCurrentUserService = new ideasaurus.service.Service(
        'retrieveCurrentUser',
        'POST',
        'remote-procedure-call/json',
        {procedure:'retrieveCurrentUser'},
        'arguments',
        'json');

	var loginService = new ideasaurus.service.Service(
        'login',
        'POST',
        'remote-procedure-call/json',
        {procedure : 'login'},
        'arguments',
        'json' );

    var logoutService = new ideasaurus.service.Service(
        'logout',
        'POST',
        'remote-procedure-call/json',
        {procedure : 'logout'},
        'arguments',
        'json' );

    var registerUserService = new ideasaurus.service.Service(
        'registerUser',
        'POST',
        'remote-procedure-call/json',
        {procedure : 'registerUser'},
        'arguments',
        'json' );

    var verifyUserService = new ideasaurus.service.Service(
        'verifyUser',
        'POST',
        'remote-procedure-call/json',
        {procedure : 'verifyUser'},
        'arguments',
        'json' );

	ideasaurus.service.ServiceDelegate.addService( retrieveCurrentUserService );
	ideasaurus.service.ServiceDelegate.addService( loginService );
	ideasaurus.service.ServiceDelegate.addService( logoutService );
    ideasaurus.service.ServiceDelegate.addService( registerUserService );
    ideasaurus.service.ServiceDelegate.addService( verifyUserService );
}

ideasaurus.model.UserProxy.prototype = {
	retrieveCurrentUser :function( callback ) {
		var thisObj = this;
		ideasaurus.service.ServiceDelegate.retrieveCurrentUser( null , 
			
			function( data , status ){ 
				if( data.status == 'SUCCESS' )
				{
					if( !data )
					{
						facade.invokeCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , status )
						if( callback ) callback( thisObj.getUser() )
					}
					
					else if( data.result )
					{
						thisObj.setUser( new ideasaurus.model.vo.UserVO( data.result.id, data.result.email ) );
					}
					if( callback ) callback( thisObj.getUser() )
				}
			} );
	},
	login : function ( email , password , callback ) {
		var thisObj = this;
		var shaObj = new jsSHA( password );
        var hash = shaObj.getHash("HEX");
		ideasaurus.service.ServiceDelegate.login( { email : email , password : hash } ,
			
			function( data , status ) { 

				if( !data )
				{
					facade.invokeCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , status )
					if( callback ) callback( thisObj.getUser() )
				}
				else if( data.status == 'SUCCESS' )
				{
					if( data.result )
					{
						thisObj.setUser( new ideasaurus.model.vo.UserVO( data.result.id, data.result.email ) );
					}
					if( callback ) callback( thisObj.getUser() )	
				}
			});
	},
    logout : function ( callback ) {
        var thisObj = this;
        ideasaurus.service.ServiceDelegate.logout( null, 
            function( data, status ) {
                if( !data )
				{
					facade.invokeCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , status )
					if( callback ) callback( false )
				}
                
                else if ( data.status == 'SUCCESS' )
                {
                    thisObj.setUser(null);
                    if (callback) {
                        callback( true );
                    }
                }
            });
    },
    registerUser : function ( password, email, callback ) {
		ideasaurus.service.ServiceDelegate.registerUser(
            { password : password, email : email } ,

			function( data , status ) {
				if( !data ) {
    				facade.invokeCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , status )
				} else {
                    if(callback) callback( data );
                } 
            });
    },
    verifyUser : function ( uid, token, callback ) {
        var thisObj = this;
        ideasaurus.service.ServiceDelegate.verifyUser(
            { uid : uid, token : token },
            function ( data, status ) {
				
				if( !data )
				{
					facade.invokeCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , status )
					if( callback ) callback( thisObj.getUser() )
				}
				else if( data.status == 'SUCCESS' )
				{
                    if (data.result) {
                        thisObj.setUser( new ideasaurus.model.vo.UserVO( data.result.id , data.result.email ) );
                    }
                    if( callback ) callback( thisObj.getUser() )
				}
			}
        );
    }
}

ideasaurus.model.UserProxy.getNAME = function() {
	return 'UserProxy';	
}
