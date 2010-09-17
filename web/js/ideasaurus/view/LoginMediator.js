ideasaurus.view.LoginMediator = function() {
	this.template = facade.retrieveTemplate( ideasaurus.view.LoginMediator.getTEMPLATENAME() )	
}

ideasaurus.view.LoginMediator.prototype = {
	initialize : function() {
		var jqTemplate = $( this.template );
		$( 'body' ).append( jqTemplate );
		$( '#login-submit' ).click( this.loginSubmitHandler );
        $( '#register-submit' ).click( this.registerSubmitHandler );
        $( '#password' ).keypress( this.passwordEnterHandler );
		$( '.error' , jqTemplate ).css( 'display' , 'none' );
		
	},
	remove : function() {
		$( '#login' ).remove();
	},
    passwordEnterHandler : function( e ) {
        if (e) {
            var keycode = e.which;
            if (keycode == 13) {
                var loginMediator = new ideasaurus.view.LoginMediator();
                return loginMediator.loginSubmitHandler( e );
            }
        }
        return true;
    },
	loginSubmitHandler : function( e ) {
        var utility = new ideasaurus.util.Utility();
		var userProxy = facade.retrieveProxy( ideasaurus.model.UserProxy.getNAME() );
        var rememberMe = $('#remember_me:checked').val() !== undefined;
        var email = $( '#email' ).val();
        var password = $( '#password' ).val();
		userProxy.login( email , password ,
			function( user ) {
				if( !user ) 
                {
					$( '#login > .error' ).css( 'display' , 'block' ).text( 'The email or password you entered is incorrect. Please try again.' );
                }
				else
				{
                    /* If the user has opted to be remembered, create a cookie we can use to login next time. */
                    if (rememberMe) {
                        var expires = new Date();
                        /* current time in milliseconds + milliseconds in 14 days. */
                        expires.setTime( expires.getTime()+(1000*60*60*24*14));
                        var encryptKey = utility.randomString(20);
                        utility.createCookie("authtoken",
                            encryptKey + utility.base64encode(utility.encrypt(email+":"+password,encryptKey)),
                            expires.toGMTString());
                    }
					facade.invokeCommand( ideasaurus.control.ChangeStateCommand.getNAME() , ideasaurus.model.AppStateProxy.getCORKBOARDPATH() );
				}	
			} );
	},
    registerSubmitHandler : function( e ) {
        facade.invokeCommand( ideasaurus.control.ChangeStateCommand.getNAME() , ideasaurus.model.AppStateProxy.getREGISTERPATH() );
    }
}

ideasaurus.view.LoginMediator.getNAME = function() {
	return 'LoginMediator'	
}

ideasaurus.view.LoginMediator.getTEMPLATENAME = function() {
	return 'LoginTemplate'	
}