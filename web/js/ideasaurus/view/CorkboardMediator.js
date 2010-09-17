ideasaurus.view.CorkboardMediator = function() {
	this.corkboardTemplate = facade.retrieveTemplate( ideasaurus.view.CorkboardMediator.getTEMPLATENAME() )
	this.corkboardProxy = facade.retrieveProxy( ideasaurus.model.CorkboardProxy.getNAME() )
	
}

ideasaurus.view.CorkboardMediator.prototype = {
	initialize : function() {
		var thisObj = this;
		this.noteMediator = facade.retrieveMediator( ideasaurus.view.NoteMediator.getNAME() );
		
		this.corkboardProxy.retrieveCorkboardsForCurrentUser( function( corkboards ) {
			
			if( corkboards.length )
			{
				//corkboards.reverse();
				for( var i in corkboards )
				{
					thisObj.addCorkboard( corkboards[i] )
				}
				$( '#corkboard-' + thisObj.corkboardProxy.getSelectedCorkboard().getId() ).find( '.corkboard-btn' ).click();
			}
		})
		
	},
	
	addCorkboard : function( corkboard ) {
		ideasaurus.Interface.ensureImplements( corkboard , ICorkboard )
		var thisObj = facade.retrieveMediator( ideasaurus.view.CorkboardMediator.getNAME() )
		var thisCorkboard = corkboard;
		var corkboardProxy = facade.retrieveProxy( ideasaurus.model.CorkboardProxy.getNAME() );
		ideasaurus.Interface.ensureImplements( corkboard , ICorkboard );
		
		var corkboardLi = $( this.corkboardTemplate ).find( '#corkboard' );
		var corkboardBtn = corkboardLi.find( '.corkboard-btn' );
		var editPanel = corkboardLi.find( '.corkboard-edit-panel' );
		var renameField = corkboardLi.find( '.rename-field' )
		var corkboardEditBtn = corkboardLi.find( '.corkboard-edit-btn' );
		var deleteBtn = corkboardLi.find( '.delete-btn' );
		var saveBtn = corkboardLi.find( '.save-btn' )
		
		corkboardLi.attr( 'id' , 'corkboard-' + thisCorkboard.getId() );
		corkboardBtn.text( thisCorkboard.getLabel() );
		renameField.val( thisCorkboard.getLabel() );
		
		
		
		
		$( '#corkboard-list' ).prepend( corkboardLi )		
		
		corkboardLi.droppable( { 
			accept : '.note',
			hoverClass: 'droppable-hover',
			tolerance : 'pointer',
			over : function( event , ui ) {
				ui.draggable.addClass( 'over-droppable' )
				
			},
			out : function( event , ui ) {
				ui.draggable.removeClass( 'over-droppable' )
				
			},
			drop : function( event , ui) {
				var dropCorkboard = $(this);
				if( dropCorkboard.hasClass( 'selected' ) )
					return
				else
				{
					ui.draggable.draggable('destroy');
					var newCorkboardId = Number( dropCorkboard.attr( 'id' ).split('-')[1] );
					thisObj.noteMediator.moveToCorkboard( newCorkboardId, thisObj.noteMediator.getNoteIdByElementId( ui.draggable.attr('id') ) )
				} 
			}
		})
		
		function closeEditPanel() {
			editPanel.css( 'display' , 'none' )
			corkboardBtn.css( 'text-indent' , '0px' )
		}
		
		function openEditPanel() {
			editPanel.css( 'display' , 'block' )
		 	corkboardBtn.css( 'text-indent' , '-999px' )
		 	renameField.val( corkboardBtn.text() )
		}
		
		renameField.focus( function( event ) { event.target.select() } );
		
		function save() {
			thisCorkboard.setWeight( thisObj.getCorkboardWeightById( corkboard.getId() ) );
			
			if( corkboardLi.hasClass( 'selected' ) )
				thisCorkboard.setFocused( true );
			else
				thisCorkboard.setFocused( false );
			
			if( renameField.val() && renameField.val() != '' )
				thisCorkboard.setLabel( renameField.val() )
			else
				thisCorkboard.setLabel( 'Untitled Corkboard' );
			
			corkboardProxy.updateCorkboard( new ideasaurus.model.vo.CorkboardVO( corkboard.getId() , thisCorkboard.getWeight() , thisCorkboard.getLabel() , thisCorkboard.isFocused()  ) ,
				function( corkboard ) {
					if( corkboard )
					{
						closeEditPanel();
						thisCorkboard = corkboard;
						thisObj.updateCorkboard( corkboard )
					}
				})
		}
		
		corkboardLi.hover( 
			function( event ) {
				corkboardEditBtn.css( 'display' , 'block' )	
			},
			function( event ) {
				if( editPanel.css( 'display' ) == 'none' ) 
					corkboardEditBtn.css( 'display' , 'none' )
			});
			
		corkboardBtn.click( 
		 	function( event ) {
		 		thisObj.selectCorkboard( thisCorkboard )
		 		closeEditPanel()
		 		corkboardEditBtn.css( 'display' , 'none' )
		 		save();
		 	});
		 	
		 corkboardEditBtn.click(
		 	function( event) {
		 		if( editPanel.css( 'display') == 'none' )
		 		{
		 			openEditPanel()
		 		}
		 		else
		 		{
		 			closeEditPanel()
		 			renameField.val( corkboard.getLabel() )
		 		}
		 	});
		
		deleteBtn.click(
			function( event ) {
				if( !corkboardLi.hasClass( 'selected' ) )
					{
					corkboardProxy.deleteCorkboard( corkboard.getId() , 
					function( success ) {
						if( success )
							corkboardLi.remove();
					} );
				} else {
                    alert("Unable to remove a corkboard which is selected.");
                }
			});
		
		saveBtn.click(
			function( event ) {
				save();
			});
			
		renameField.keypress(
			function( event ) {
				if( event.which == 13 )
				{
					save();
				}
			}
		);
	},
	
	updateCorkboard : function( corkboard ) {
		var corkboardLi = $( '#corkboard-' + corkboard.getId() );
		var corkboardBtn = corkboardLi.find( '.corkboard-btn' );
		var renameField = corkboardLi.find( '.rename-field' )
		corkboardBtn.text( corkboard.getLabel() );
		renameField.val( corkboard.getLabel() );
	},
	
	selectCorkboard : function( corkboard ) {
		$( '#corkboard-list' ).children().removeClass( 'selected' )
		$( '#corkboard-' + corkboard.getId() ).addClass( 'selected' )
		facade.invokeCommand( ideasaurus.control.OpenCorkboardCommand.getNAME() , corkboard )
	},
	
	getCorkboardIdFromElementId : function( elementId ) {
		return elementId.split( '-' )[1];
	},
	
	getCorkboardWeightById : function( corkboardId ) {
		var corkboarListChildren = $( '#corkboard-list' ).children()
		var len = ( corkboarListChildren.length - 1 );
		for( var i = 0; i < len; i++ )
		{
			if ( corkboarListChildren[i].getAttribute('id') == 'corkboard-' + corkboardId )
				return (len - i )
		}
		return null;
	} , 
	
	remove : function() {
		
	}
	
}

ideasaurus.view.CorkboardMediator.getNAME = function() {
	return 'CorkboardMediator';	
}

ideasaurus.view.CorkboardMediator.getTEMPLATENAME = function() {
	return 'CorkboardMediatorTemplate';	
}
