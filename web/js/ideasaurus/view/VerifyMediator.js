ideasaurus.view.VerifyMediator = function() {
	this.template = facade.retrieveTemplate( ideasaurus.view.VerifyMediator.getTEMPLATENAME() );
}

ideasaurus.view.VerifyMediator.prototype = {
	
	initialize : function() {
		
		var page = $( this.template );
		var message = page.find( '#message' );
		var button = page.find( '#next-btn' )
		
		var uid = SWFAddress.getParameter( 'uid' );
		var token = SWFAddress.getParameter( 'token' );
		
		if( !uid || !token )
		{
			message.text( 'Your registration was not successful. Try clicking the verify link from your email again or try registering again.' );
			button.text( 'Register' );
			button.click( 
				function() {
					facade.invokeCommand( ideasaurus.control.ChangeStateCommand.getNAME() , ideasaurus.model.AppStateProxy.getREGISTERPATH() );
				})
		}
		else 
		{
			var userProxy = facade.retrieveProxy( ideasaurus.model.UserProxy.getNAME() )
			userProxy.verifyUser( uid , token ,
				function( user ) {
					if( user ) 
					{
						message.text( 'Your registration is complete! You can start using Noteasaurus!' )	
						button.html( 'Get Started&nbsp;&raquo;' );
						button.click( 
							function() {
								facade.invokeCommand( ideasaurus.control.ChangeStateCommand.getNAME() , ideasaurus.model.AppStateProxy.getCORKBOARDPATH() )
							})
					}
					else 
					{
						message.text( 'Your registration was not successful. Try clicking the verify link from your email again or try registering again.' );
						button.text( 'Register' );
						button.click( 
							function() {
								facade.invokeCommand( ideasaurus.control.ChangeStateCommand.getNAME() , ideasaurus.model.AppStateProxy.getREGISTERPATH() );
							})
					}
				})
		}
		
		
		
		
		$( 'body' ).append( page )
		
	},
	
	remove : function() {
		$( '#verify' ).remove();
	}
}


ideasaurus.view.VerifyMediator.getNAME = function() {
	return 'VerifyMediator';	
}

ideasaurus.view.VerifyMediator.getTEMPLATENAME = function() {
	return 'VerifyMediatorTemplate';	
}