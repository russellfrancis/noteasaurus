ideasaurus.ApplicationFacade = function() {
	ideasaurus.ApplicationFacade.superclass.constructor.call( this , null );
}

ideasaurus.extend( ideasaurus.ApplicationFacade , ideasaurus.Facade );

ideasaurus.ApplicationFacade.prototype.init = function() {
		this.registerCommand( ideasaurus.control.ApplicationStartupCommand.getNAME() , ideasaurus.control.ApplicationStartupCommand );
		this.registerCommand( ideasaurus.control.ChangeStateCommand.getNAME() , ideasaurus.control.ChangeStateCommand );
		this.registerCommand( ideasaurus.control.OpenCorkboardCommand.getNAME() , ideasaurus.control.OpenCorkboardCommand );
		this.registerCommand( ideasaurus.control.ShowNotificationCommand.getNAME() , ideasaurus.control.ShowNotificationCommand );
		this.registerCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , ideasaurus.control.HTTPErrorCommand );
		this.invokeCommand( ideasaurus.control.ApplicationStartupCommand.getNAME() );
}