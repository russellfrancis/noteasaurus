ideasaurus.view.RegisterMediator = function() {
	this.template = facade.retrieveTemplate( ideasaurus.view.RegisterMediator.getTEMPLATENAME() )
}

ideasaurus.view.RegisterMediator.prototype = {
	initialize : function() {
		var jqTemplate = $( this.template );
		
		$( 'body' ).append( jqTemplate );
		$( '#register-success' ).hide();
		$( '#register-submit' ).click( this.registerSubmitHandler );
        $( '#landing-page-link').click( this.landingPageHandler );
		
		$( '.error' , jqTemplate ).css( 'display' , 'none' );
        $( '.message' , jqTemplate ).css( 'display' , 'none' );
	},

	remove : function() {
		$( '#register' ).remove();
	},

	registerSubmitHandler : function( e ) {
		var userProxy = facade.retrieveProxy( ideasaurus.model.UserProxy.getNAME() );
        userProxy.registerUser(
            $('#password').val(),
            $('#email').val(),
            function( data ) {
            	if( data.status == 'SUCCESS' )
            	{
            		$('#register-entry').hide();
                	$('#register-success').show();
            	}
            	else
            	{
               		$('#email').removeClass('error');
                	$('#password').removeClass('error');
                	$('label').removeClass('error');
               		if (!data.result) {
                    	$('#register > #register-entry > .error').css('display', 'block').text(data.detailed_message);
                	} else {
                    	var errHtml = "";
                    	for (var field in data.result) {
                        	$('#'+field).addClass('error');
                        	$('[for=' + field + ']').addClass('error');
                        	errHtml += field + ": ";
                        	for (var index in data.result[field]) {
                            	errHtml += data.result[field][index] + "; ";
                        	}
                        	errHtml += "<br/>";
                    	}
                		$('#register > #register-entry > .error').css('display', 'block').html(errHtml);
                	}
                    
                }
            });        
    },
    
    landingPageHandler : function(e) {
        facade.invokeCommand( ideasaurus.control.ChangeStateCommand.getNAME(), ideasaurus.model.AppStateProxy.getLOGINPATH() );
    }
}

ideasaurus.view.RegisterMediator.getNAME = function() {
	return 'RegisterMediator'
}

ideasaurus.view.RegisterMediator.getTEMPLATENAME = function() {
	return 'RegisterTemplate'
}