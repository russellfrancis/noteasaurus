ideasaurus.model.vo.CorkboardVO = function( id , weight , label , is_focused ) { // Implements ICorkboard
	
	var _id;
	var _label;
	var _weight;
    var _is_focused;
	
	this.setId = function( value ) {
		_id = isNumber( value );
	}
	
	this.getId = function( ) {
		return _id;
	}
	
	this.setLabel = function( value ) {
		_label = value;
	}

	this.getLabel = function() {
		return _label;
	}
	
	this.setWeight = function(value) {
		_weight = value;
	}
	
	this.getWeight = function() {
		return _weight;
	}

    this.setFocused = function(value) {
        _is_focused = value;
    }

    this.isFocused = function() {
        return _is_focused;
    }
	
	if( id ) this.setId( id );
	if( weight ) this.setWeight( weight );
	if( label ) this.setLabel( label );
	if( is_focused != undefined ) this.setFocused( is_focused );
}