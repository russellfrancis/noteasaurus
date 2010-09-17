ideasaurus.view.NoteMediator = function() {

	this.template = facade.retrieveTemplate( ideasaurus.view.NoteMediator.getTEMPLATENAME() )
	this.noteProxy = facade.retrieveProxy( ideasaurus.model.NoteProxy.getNAME() )
	this.topZIndex = 0;
	
}

ideasaurus.view.NoteMediator.prototype = {
	
	/**
	 * @function
	 * Adds notes to the DOM for the selected corkboard
	 */
	initialize : function() {
		var thisObj = this;
		this.noteProxy.retrieveNotesForCorkboard( 
			facade.retrieveProxy( ideasaurus.model.CorkboardProxy.getNAME() ).getSelectedCorkboard().getId() ,
			function( notes ) {
				var len = notes.length;
				for( var i = 0; i < len; i++)
				{
					thisObj.addNote( notes[i] , i )
				}
			});
	},
	
	/**
	 * @function
	 * Removes all notes from the DOM
	 */
	remove : function() {
		$( '#corkboard-area' ).find( '.note' ).remove();
	},
	
	/**
	 * @function
	 * @param {INote} noteObj The note object that is represented by the DOM object inserted by this function
	 * @param {number} index The index position where this note has been inserted into the DOM. Used to determine note focus and z-index order
	 * @todo Currently this method is passed a INote object which is used to model the DOM object it creates. The INote object is maintained within the scope of the function
	 * and not accessible from the outside. This method should be changed to use a cached INote object stored on the NoteProxy object. This should simplify this method and also
	 * make it easier to manipulate the INote object that its modeling when doing 'Undo' methods, etc. 
	 */
	addNote : function( noteObj , index ) {
		ideasaurus.Interface.ensureImplements( noteObj , INote );
		
		var thisObj = this;
		var noteId = noteObj.getId()
		var thisNoteObj = noteObj;
		var note = $( this.template )
		var contentPosition;
		var contentPadding = 10;
		var textAreaPosition;
		var inEditMode = false;
		
		var noteControlBar = note.find( '.note-control-bar' );
		var content = note.find( '.note-content' );
		var textArea = note.find( 'textarea' );
		var deleteBtn = noteControlBar.find( '.delete-note-btn');
		var noteColorBtn = noteControlBar.find( '.color-note-btn' );
		var colorPanel = noteControlBar.find( '.color-panel');
		
		
		note.attr( 'id' , 'note-' + thisNoteObj.getId() );
		note.css( 'width' , thisNoteObj.getWidth() + 'px' );
		note.css( 'height' , thisNoteObj.getHeight() + 'px' );
		note.css( 'top' , thisNoteObj.getY() + 'px' );
		note.css( 'left' , thisNoteObj.getX() + 'px' )
		note.css( 'background-color' , ideasaurus.model.NoteProxy.skins[thisNoteObj.getSkin()].background );
		note.css( 'border-color' , ideasaurus.model.NoteProxy.skins[thisNoteObj.getSkin()].border );
		
		textArea.css( 'border-color' , ideasaurus.model.NoteProxy.skins[thisNoteObj.getSkin()].border );
		
		deleteBtn.css( 'border-color' , ideasaurus.model.NoteProxy.skins[thisNoteObj.getSkin()].border );
		deleteBtn.hover(
			function() {
				deleteBtn.css( 'background-color' , ideasaurus.model.NoteProxy.skins[thisNoteObj.getSkin()].border );
			},
			function() {
				deleteBtn.css( 'background-color' , 'transparent' );
			}
		)
		
		
		content.html( thisObj.prepareText( thisNoteObj.getContent() ) );
		
		noteControlBar.css( 'border-color' , ideasaurus.model.NoteProxy.skins[thisNoteObj.getSkin()].border )
		
		colorPanel.css( 'display' , 'none' );
		
		content.css( 'padding' , contentPadding + 'px' )
		content.css( 'overflow' , 'auto' )
		textArea.css( 'display' , 'none' )
		textArea.css( 'padding' , '8px' )
		
		for( var i in ideasaurus.model.NoteProxy.skins )
		{
			var colorName = ideasaurus.model.NoteProxy.skins[i].getNAME();
			var colorBtn = $( '<a class="color-btn" title="'+colorName+'" href="javascript:void(0)">'+colorName+'</a>' )
			colorBtn.css( 'background-color' , ideasaurus.model.NoteProxy.skins[i].background );
			colorBtn.css( 'border-color' , ideasaurus.model.NoteProxy.skins[i].border )
			colorBtn.click( 
				function( event ) {
					var selectedColor = $( event.target ).text();
					thisNoteObj.setSkin( selectedColor )
					note.css( 'background-color' , ideasaurus.model.NoteProxy.skins[selectedColor].background );
					note.css( 'border-color' , ideasaurus.model.NoteProxy.skins[selectedColor].border );
					noteControlBar.css( 'border-color' , ideasaurus.model.NoteProxy.skins[selectedColor].border )
					
					deleteBtn.css( 'border-color' , ideasaurus.model.NoteProxy.skins[selectedColor].border );
					deleteBtn.hover(
						function() {
							deleteBtn.css( 'background-color' , ideasaurus.model.NoteProxy.skins[selectedColor].border );
						},
						function() {
							deleteBtn.css( 'background-color' , 'transparent' );
						}
					)
					
					var updateNote = new ideasaurus.model.vo.NoteVO( thisNoteObj.getId() );
					updateNote.setSkin( selectedColor )
					thisObj.saveNote( updateNote )
				})
			colorPanel.append( colorBtn )
		}
		
		if( index != undefined )
		{
			note.css( 'z-index' , index );
			this.topZIndex = index;
		}
		else
		{
			this.topZIndex++
			note.css( 'z-index' , this.topZIndex );
		}
		
		
		$( '#corkboard-area' ).append( note )
		contentPosition = content.position().top + ( ( contentPadding * 2 ) + 5 ) ;
		textAreaPadding = ( ( contentPadding * 2 ) - 2 ) ;
		textAreaPosition = content.position().top + ( textAreaPadding + 7 ) ;
		
		
		content.css( 'height' , ( thisNoteObj.getHeight() - contentPosition ) + 'px' );
		
		textArea.css( 'width' , ( thisNoteObj.getWidth() - textAreaPadding )  + 'px' )
		textArea.css( 'height' , ( note.outerHeight() - textAreaPosition ) + 'px' )
		
		noteControlBar.click( 
			function( event ) { 
				thisObj.focusNote( note , new ideasaurus.model.vo.NoteVO( thisNoteObj.getId() ) ) 
				if( inEditMode )
					editMode( false )
			} )
		
		textArea.blur( function() { editMode( false ) })
		/*
		textArea.keydown( function( event ) {
				if( event.which == 13 )
					editMode( false )
			} )
		
		*/ 
		
		noteColorBtn.click( function( event ){ colorPanel.css( 'display' , 'block' ) ;  }	)
		colorPanel.hover( 
			function(){ },
			function(){ colorPanel.css( 'display' , 'none' ) } )
		
		deleteBtn.click( function( event ) { event.stopPropagation(); thisObj.deleteNote( note , thisNoteObj ) });
		
		note.draggable( { handle : '.note-control-bar' , opacity: .5 , containment : 'document', cursor: 'move',
			stop: function( event , ui ) {
				event.stopPropagation();
				if( $( event.target ).parent().hasClass( 'over-droppable' ) )
					return
				else
				{
					thisNoteObj.setX( ui.position.left );
					thisNoteObj.setY( ui.position.top );
					thisObj.saveNote( new ideasaurus.model.vo.NoteVO( thisNoteObj.getId() , null , null , thisNoteObj.getX() , thisNoteObj.getY() ) );
				}
			},
			start: function( event , ui ) {
				thisObj.focusNote( note )
				if( inEditMode )
					editMode( false )
			} 
		} )
		
		note.resizable( {
				start : function( event , ui ) { thisObj.focusNote( note ) },
				stop : function( event , ui ) {
					thisNoteObj.setHeight( ui.size.height )
					thisNoteObj.setWidth( ui.size.width )
					if(!inEditMode)
					{
						textArea.css( 'width' , ( ui.size.width - textAreaPadding ) + 'px' )
						textArea.css( 'height' , ( ui.size.height - textAreaPosition ) + 'px' )
					}
					else
					{
						content.css( 'height' , ( ui.size.height - contentPosition ) + 'px' )
					}
					thisObj.saveNote( new ideasaurus.model.vo.NoteVO( thisNoteObj.getId() , null , null , null , null , thisNoteObj.getWidth() , thisNoteObj.getHeight()  ) )
					
				},
				resize : function( event , ui ) {
					if(!inEditMode)
						content.css( 'height' , ( ui.size.height - contentPosition ) + 'px' )
					else
					{
						textArea.css( 'width' , ( ui.size.width - textAreaPadding ) + 'px' )
						textArea.css( 'height' , ( ui.size.height - textAreaPosition ) + 'px' )
					}
				},
				minHeight: 100, 
				minWidth: 100, 
				handles: 'se'
			} )
		
		content.click( 
			function() {
				editMode( true )
			} )
		
		function editMode( flag ) { 
			if( flag )
			{
				inEditMode = true;
				content.css( 'display' , 'none' );
				textArea.css( 'display' , 'block' )
				textArea.val( $.trim( thisNoteObj.getContent() ) )
				textArea.focus();
				thisObj.focusNote( note , new ideasaurus.model.vo.NoteVO( thisNoteObj.getId() ) )
			}
			else
			{
				inEditMode = false;
				content.css( 'display' , 'block' )
				textArea.css( 'display' , 'none' )	
				thisNoteObj.setContent( textArea.val() )  
				var updateNote = new ideasaurus.model.vo.NoteVO( thisNoteObj.getId() )
				updateNote.setContent( thisNoteObj.getContent() )
				thisObj.saveNote( updateNote )
				content.html( thisObj.prepareText( textArea.val() ) )
			}
		}
		
	},
	
	focusNote : function( element , noteObj ) {
		$( '#corkboard-area' ).children().removeClass( 'focused' )
		
		this.topZIndex++
		element.css( 'z-index' , this.topZIndex )
		element.addClass( 'focused' )
		
		if( noteObj )
			this.saveNote( noteObj )
	},
	
	saveNote : function( noteObj ) {
		this.noteProxy.updateNote( noteObj )
	},
	
	deleteNote : function( element , noteObj ) {
		this.noteProxy.deleteNote( noteObj.getId() , function( success) {
			if( success )
				element.remove();
		} ) 
	},
	
	moveToCorkboard : function( corkboardId , noteId ) {
		var updateNote = new ideasaurus.model.vo.NoteVO( noteId );
		updateNote.setCorkboardId( corkboardId );
		this.saveNote( updateNote );
		//var noteElement = $( '#note-' + noteId );
		$( '#note-' + noteId ).remove();
		//noteElement.remove()
		
	},
	
	getNoteIdByElementId : function( elementId ) {
		
		return Number( elementId.split( '-' )[1] );
	},
	
	/**
	 * @function
	 * Method to prepare text for inserting into the DOM. 
	 */
	prepareText : function( text ) {
		if( !text )
			return '';
		
		var s = text;

		// escape special html characters.
        s = s.replace( /&/g, '&amp;' );
        s = s.replace( /</g, '&lt;' );
        s = s.replace( />/g, '&gt;' );
        s = s.replace( /"/g, '&quot;' );

        // faithfully represent spaces and tabs
        //s = s.replace( / /g, '&nbsp;');//prevents wrapping
        s = s.replace( /\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');

        // Bold & Italic
        s = s.replace( /'''''(.+?)'''''/g, '<strong><i>$1</i></strong>');

        // Bold
        s = s.replace( /'''(.+?)'''/g, '<strong>$1</strong>');

        // Italic
        s = s.replace( /''(.+?)''/g, '<i>$1</i>');

        // Headings
        s = s.replace( /(\s)======(.+?)======(\s)/g, '$1<h6>$2</h6>$3');
        s = s.replace( /^======(.+?)======(\s)/, '<h6>$1</h6>$2');
        s = s.replace( /(\s)======(.+?)======$/, '$1<h6>$2</h6>');
        s = s.replace( /^======(.+?)======$/, '<h6>$1</h6>');

        s = s.replace( /(\s)=====(.+?)=====(\s)/g, '$1<h5>$2</h5>$3');
        s = s.replace( /^=====(.+?)=====(\s)/, '<h5>$1</h5>$2');
        s = s.replace( /(\s)=====(.+?)=====$/, '$1<h5>$2</h5>');
        s = s.replace( /^=====(.+?)=====$/, '<h5>$1</h5>');

        s = s.replace( /(\s)====(.+?)====(\s)/g, '$1<h4>$2</h4>$3');
        s = s.replace( /^====(.+?)====(\s)/, '<h4>$1</h4>$2');
        s = s.replace( /(\s)====(.+?)====$/, '$1<h4>$2</h4>');
        s = s.replace( /^====(.+?)====$/, '<h4>$1</h4>');

        s = s.replace( /(\s)===(.+?)===(\s)/g, '$1<h3>$2</h3>$3');
        s = s.replace( /^===(.+?)===(\s)/, '<h3>$1</h3>$2');
        s = s.replace( /(\s)===(.+?)===$/, '$1<h3>$2</h3>');
        s = s.replace( /^===(.+?)===$/, '<h3>$1</h3>');

        s = s.replace( /(\s)==(.+?)==(\s)/g, '$1<h2>$2</h2>$3');
        s = s.replace( /^==(.+?)==(\s)/, '<h2>$1</h2>$2');
        s = s.replace( /(\s)==(.+?)==$/, '$1<h2>$2</h2>');
        s = s.replace( /^==(.+?)==$/, '<h2>$1</h2>');
        
        s = s.replace( /(\s)=(.+?)=(\s)/g, '$1<h1>$2</h1>$3');
        s = s.replace( /^=(.+?)=(\s)/, '<h1>$1</h1>$2');
        s = s.replace( /(\s)=(.+?)=$/, '$1<h1>$2</h1>');
        s = s.replace( /^=(.+?)=$/, '<h1>$1</h1>');

        // Rewrite links
        s = s.replace( /(http:\/\/)([^\s<]+)/gi, '<a target="_blank" href="$1$2">$1$2</a>');
        s = s.replace( /(https:\/\/)([^\s<]+)/gi, '<a target="_blank" href="$1$2">$1$2</a>');
        s = s.replace( /(^|[^\/])(www\.)([^\s<]+)/gi, '$1<a target="_blank" href="http://$2$3">$2$3</a>');

		// convert line breaks to break tags
		s = s.replace( /\n|\r/g , '<br/>'  );

		return s;
	}
}

ideasaurus.view.NoteMediator.getNAME = function() {
	return 'NoteMediator';	
}

ideasaurus.view.NoteMediator.getTEMPLATENAME = function() {
	return 'NoteTemplate';	
}