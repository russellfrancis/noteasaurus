ideasaurus.model.Model = ( function(){
	var proxies = new Array();
	
	return {
		registerProxy : function( name , proxy ) {
			proxies.push( { name:name, proxy: proxy } )
		},
		retrieveProxy : function( name ){
			for( var i in proxies )
			{
				if( proxies[i].name == name )
					return proxies[i].proxy
			}
			throw new Error( 'Error :: Model Class :: retrieveProxy :: Proxy "'+name+'" not found' )
		}
	}
	
})();