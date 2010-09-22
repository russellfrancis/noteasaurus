ideasaurus.model.NoteProxy = function() { // Implements IProxy
	
	
	this.notes = new Array();
	
	var createNoteService = new ideasaurus.service.Service(
		'createNote',
		'POST',
		'remote-procedure-call/json',
		{procedure : 'createNote'},
		'arguments',
		'json'
	);
	
	var retrieveNotesForCorkboardService = new ideasaurus.service.Service(
		'retrieveNotesForCorkboard',
		'POST',
		'remote-procedure-call/json',
		{procedure : 'retrieveNotesForCorkboard'},
		'arguments',
		'json'
	);
	
	var updateNoteService = new ideasaurus.service.Service(
		'updateNote',
		'POST',
		'remote-procedure-call/json',
		{procedure : 'updateNote'},
		'arguments',
		'json'
	);
	
	var deleteNoteService = new ideasaurus.service.Service(
		'deleteNote',
		'POST',
		'remote-procedure-call/json',
		{procedure : 'deleteNote'},
		'arguments',
		'json'
		)
	
	
	ideasaurus.service.ServiceDelegate.addService( createNoteService );
	ideasaurus.service.ServiceDelegate.addService( retrieveNotesForCorkboardService );
	ideasaurus.service.ServiceDelegate.addService( updateNoteService );
	ideasaurus.service.ServiceDelegate.addService( deleteNoteService );
}


ideasaurus.model.NoteProxy.prototype = {
	
	getNoteById : function( id ) {
		for( var i in this.notes ) 
		{
			if( this.notes[i].getId() == id )
				return this.notes[i]	
		}
		return null;
		//maybe add a method here to get note using web service if not cached
	},
	
	getNoteIndexById : function( id ) {
		var len = this.notes.length;
		for( var i = 0; i < len; i++ ) 
		{
			if( this.notes[i].getId() == id )
				return i	
		}
		return null;
		//maybe add a method here to get note using web service if not cached
	},
	
	createNote : function( note , callback ) {
		var thisObj = this;
		ideasaurus.Interface.ensureImplements( note , INote )
		
		ideasaurus.service.ServiceDelegate.createNote( 
			{
				corkboardId : note.getCorkboardId(),
				content : note.getContent(),
				x: note.getX(),
				y: note.getY(),
				width : note.getWidth(),
				height : note.getHeight(),
				skin : note.getSkin(),
				collapsed : note.getIsCollapsed()
			},
			function( data , status ) {
				if( !data )
				{
					facade.invokeCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , status )
				}
				else if( ideasaurus.service.ResponseValidator.validate( data ) )
				{
					note.setId( data.result.id );
					callback( note );
				}
			});
	} ,
	
	updateNote : function( note , callback) {
		ideasaurus.Interface.ensureImplements( note , INote )
		
		var props = {};
		
		if( note.getId() ) 
			props.id = note.getId();
		else
			throw new Error( 'Error :: NoteProxy :: updateNote :: INote is required to have an ID.'  );
			
		if( note.getCorkboardId() ) props.corkboardId = note.getCorkboardId();
		if( note.getContent() ) props.content = note.getContent();
		if( note.getX() ) props.x = note.getX();
		if( note.getY() ) props.y = note.getY();
		if( note.getSkin() ) props.skin = note.getSkin();
		if( note.getHeight() ) props.height = note.getHeight();
		if( note.getWidth() ) props.width = note.getWidth();
		if( note.getIsCollapsed() ) props.collapsed = note.getIsCollapsed();
		
		ideasaurus.service.ServiceDelegate.updateNote( props ,
			function( data , status ) { 
				if( !data )
				{
					facade.invokeCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , status )
					if( callback ) callback( false )
				}
				else if( ideasaurus.service.ResponseValidator.validate( data ) ) 
					if( callback ) callback( true )
				else
					if( callback ) callback( false )
			})
		
	},
	/*
	updateNote : function( id , props ) {
		
		var props = {};
		
			
		if( note.getContent() ) props.content = note.getContent();
		if( note.getX() ) props.x = note.getX();
		if( note.getY() ) props.y = note.getY();
		if( note.getSkin() ) props.skin = note.getSkin();
		if( note.getHeight() ) props.height = note.getHeight();
		if( note.getWidth() ) props.width = note.getWidth();
		if( note.getIsCollapsed() ) props.collapsed = note.getIsCollapsed();
		
		ideasaurus.service.ServiceDelegate.updateNote( props ,
			function( data , status ) { 
				if( !data )
				{
					facade.invokeCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , status )
					if( callback ) callback( false )
				}
				else if( callback ) 
				{
					if( data.status == 'SUCCESS')
						callback( true )
					else
						callback( false )
				}
			})
		
	},
	*/
	retrieveNotesForCorkboard : function( corkboardId , callback ) {
		var thisObj = this;
		this.notes.length = 0;
		ideasaurus.service.ServiceDelegate.retrieveNotesForCorkboard( { id: corkboardId } , 
		function( data , status) {
			if( !data )
			{
				facade.invokeCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , status )
				if( callback ) callback( false )
			}
			else if( ideasaurus.service.ResponseValidator.validate( data ) )
			{
				var notes = new Array();
				for (var i in data.result )
				{
					thisObj.notes.push(
						new ideasaurus.model.vo.NoteVO(
							data.result[i].id,
							null,
							data.result[i].collapsed,
							data.result[i].x,
							data.result[i].y,
							data.result[i].width,
							data.result[i].height,
							data.result[i].skin,
							data.result[i].content
						)
					)
				}
				callback( thisObj.notes )
			}
		});
	},
	
	deleteNote : function( id , callback ) {
		ideasaurus.service.ServiceDelegate.deleteNote( { id : id } , 
			function( data , status ){
				if( !data || data.status != 'SUCCESS'  )
				{
					facade.invokeCommand( ideasaurus.control.HTTPErrorCommand.getNAME() , status )
					if( callback ) callback( false )
				}
				else if ( ideasaurus.service.ResponseValidator.validate( data ) )
					if( callback ) callback( true )	
			}
		)
	}
}

ideasaurus.model.NoteProxy.getNAME = function() {
	return 'NoteProxy';	
}

ideasaurus.model.NoteProxy.defaultNoteFactory = function( corkboardId ) {
	if( !corkboardId ) throw new Error( 'Error :: NoteProxy :: getDefaultNote :: corkboardId is a required parameter.' );

	var note = new ideasaurus.model.vo.NoteVO();
	note.setCorkboardId( corkboardId );
	note.setSkin( ideasaurus.model.NoteProxy.skins.yellow.getNAME() );
	note.setHeight( 200 );
	note.setWidth( 200 );
	note.setIsCollapsed( false );
	note.setX( 50 );
	note.setY( 50 );
	note.setContent( '' );
	
	return note;
}

ideasaurus.model.NoteProxy.skins = {
// |------------------------| <-- This is the maximum length of a skin label 24 characters.
	yellow                  : { background: '#F6F7A3' , border : '#DADB97' , getNAME : function() { return 'yellow' } },
	green                   : { background: '#C7DE75' , border : '#AFC170' , getNAME : function() { return 'green' } },
	blue                    : { background: '#B5DDEF' , border : '#77B0CA' , getNAME : function() { return 'blue' } },
	orange                  : { background: '#FBA662' , border : '#DF8D4C' , getNAME : function() { return 'orange' } },
	purple                  : { background: '#FFB6F0' , border : '#CC88BC' , getNAME : function() { return 'purple' } }
}
