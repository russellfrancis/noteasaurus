
/**
 * The Facade class is a facade of the Model, Controller, and View classes and mirrors all of their methods in a single class. This class makes
 * it uneccessary to interact directly with the Model, Controller, or View classes.
 * @class Creates a new Facade class
 */
ideasaurus.Facade = function() {
	
	
	
};

ideasaurus.Facade.prototype = {
	/**
	 * Registers an application level command. Commands contain the business logic for the application.
	 * @function
	 * @param {string} name The name of the command
	 * @param {ICommand} command The command constructor
	 */
	registerCommand : function( name , command ) {
		return ideasaurus.control.Controller.registerCommand( name, command );
	},
	/**
	 * Invokes a (registered) command
	 * @function
	 * @param {string} name The name of the command to invoke
	 * @param {mixed} params Arguments to be passed to the command
	 */
	invokeCommand : function( name , params ) {
		return ideasaurus.control.Controller.invokeCommand( name, params )
	},
	/**
	 * Registers a Proxy. Proxies provide the means to interact with data models. They aggregate the model objects, contain 
	 * logic for making http requests and for getting, filtering, and modifying modeled application data
	 * @function
	 * @param {string} name The name of the proxy 
	 * @param {Object} proxy An instance of the proxy class
	 */
	registerProxy : function( name , proxy ){
		return ideasaurus.model.Model.registerProxy( name , proxy )
	},
	/**
	 * Retrieves a proxy by name
	 * @function
	 * @param {string} name The name of the proxy to retrieve
	 */
	retrieveProxy : function( name ){
		return ideasaurus.model.Model.retrieveProxy( name )
	},
	/**
	 * Registers a mediator. Mediators interact with ui (DOM) elements. They listem for user UI events and translate those events into commands.
	 * @function
	 * @param {string} name The name of the mediator
	 * @param {object} mediator A mediator instance
	 */
	registerMediator : function( name , mediator )
	{
		return ideasaurus.view.View.registerMediator( name , mediator)
	},
	/**
	 * Retrieves a mediator.
	 * @function
	 * @param {string} name The name of the mediator to be retrieved
	 */
	retrieveMediator : function( name )
	{
		return ideasaurus.view.View.retrieveMediator( name )
	},
	/**
	 * Registers a new template. Templates are HTML files that can be used by mediators to attach DOM elements. Registering a new template makes an
	 * http request for the html file and saves it to memory. When the file has loaded an optional callback function will can be executed
	 * @param {string} name The name of the template
	 * @param {string} path Path to the template (relative to the application directory)
	 * @param {function} [callback] Callback method called when the html file has been loaded
	 */
	registerTemplate : function( name , path , callback )
	{
		return ideasaurus.view.View.registerTemplate( name , path , callback )
	},
	/**
	 * Retrieves a template
	 * @function
	 * @param {string} name The name of the template
	 */
	retrieveTemplate : function( name )
	{
		return ideasaurus.view.View.retrieveTemplate( name );
	}
	
	
}