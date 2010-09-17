//var debugMode = true;
var debugMode = false;

ideasaurus.control.ApplicationStartupCommand = function() {
}

ideasaurus.control.ApplicationStartupCommand.prototype =  {
	
	execute : function() {
		
		facade.registerProxy( ideasaurus.model.UserProxy.getNAME() , new ideasaurus.model.UserProxy() );
		facade.registerProxy( ideasaurus.model.NoteProxy.getNAME() , new ideasaurus.model.NoteProxy() );
		facade.registerProxy( ideasaurus.model.CorkboardProxy.getNAME() , new ideasaurus.model.CorkboardProxy() );
		facade.registerProxy( ideasaurus.model.AppStateProxy.getNAME() , new ideasaurus.model.AppStateProxy() );
		
		facade.registerMediator( ideasaurus.view.LoginMediator.getNAME() , new ideasaurus.view.LoginMediator() );
		facade.registerMediator( ideasaurus.view.VerifyMediator.getNAME() , new ideasaurus.view.VerifyMediator() );
        facade.registerMediator( ideasaurus.view.RegisterMediator.getNAME() , new ideasaurus.view.RegisterMediator() );
        facade.registerMediator( ideasaurus.view.ApplicationMediator.getNAME() , new ideasaurus.view.ApplicationMediator() );
		facade.registerMediator( ideasaurus.view.CorkboardMediator.getNAME() , new ideasaurus.view.CorkboardMediator() );
		facade.registerMediator( ideasaurus.view.NoteMediator.getNAME() , new ideasaurus.view.NoteMediator() );
		facade.registerMediator( ideasaurus.view.NotificationMediator.getNAME() , new ideasaurus.view.NotificationMediator() );
		
		this.getUser();
	},
	getUser : function() {
		
		var userProxy = facade.retrieveProxy( ideasaurus.model.UserProxy.getNAME() );
		userProxy.retrieveCurrentUser( this.getUserCompleteHandler )
		
	},
	getUserCompleteHandler : function( user ) {
		if( !user )
		{
            var loggedIn = false;
            var userProxy = facade.retrieveProxy( ideasaurus.model.UserProxy.getNAME() );
            var utility = new ideasaurus.util.Utility();

            var authtoken = utility.readCookie("authtoken");
            if (authtoken != null) {
                var key = authtoken.substring(0,20);
                authtoken = authtoken.substring(20);
                var data = utility.decrypt(utility.base64decode(authtoken), key);
                var email = data.substring(0,data.indexOf(':'));
                var password = data.substring(data.indexOf(':')+1);
                userProxy.login(email, password, function( user ) {
                    if (user !== null) {
                        loggedIn = true;
                        facade.invokeCommand( ideasaurus.control.ChangeStateCommand.getNAME() , ideasaurus.model.AppStateProxy.getCORKBOARDPATH());
                    }
                });
            }

			//production
            if (!loggedIn) {
                if( !debugMode )
                {
                    if( SWFAddress.getPathNames()[0] ) {
                        facade.invokeCommand( ideasaurus.control.ChangeStateCommand.getNAME() , SWFAddress.getPathNames()[0] );
                    } else {
                        facade.invokeCommand( ideasaurus.control.ChangeStateCommand.getNAME() , ideasaurus.model.AppStateProxy.getLOGINPATH() )
                    }
                }
                else //testing
                {
                    userProxy.login( 'kramerica5000@gmail.com' , 'pass' , function( user ) {
                        if (user !== null) {
                            facade.invokeCommand( ideasaurus.control.ChangeStateCommand.getNAME() , ideasaurus.model.AppStateProxy.getCORKBOARDPATH());
                        }
                    } )
                }
            }
		}
		else
		{
			if( SWFAddress.getPathNames()[0] )
				facade.invokeCommand( ideasaurus.control.ChangeStateCommand.getNAME() , SWFAddress.getPathNames()[0] );
			
			else 
				facade.invokeCommand( ideasaurus.control.ChangeStateCommand.getNAME() , ideasaurus.model.AppStateProxy.getCORKBOARDPATH());
		}
	}
}

ideasaurus.control.ApplicationStartupCommand.getNAME = function() { return 'ApplicationStartupCommand'; }
