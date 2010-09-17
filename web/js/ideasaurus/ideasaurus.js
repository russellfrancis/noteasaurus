/**
 * @fileOverview This is the base file for the application which needs to be loaded before the rest of the application.
 * It contains a method for importing other classes as well as other utility methods and classes
 */

/**
 * @namespace Root namespace for application framework
 */
var ideasaurus = {}

//Namespace manifest for documentation. Make sure this file is loaded first so the namespaces aren't whiped out!

/**
 * @namespace Namespace for the model (data) tier of the application
 */
ideasaurus.model = {}

/**
 * @namespace Namespace for the value objects used in the application
 */ 
ideasaurus.model.vo = {} 

/**
 * @namespace Namespace for the control tier of the application which contains the application business logic
 */
ideasaurus.control = {}

/**
 * @namespace Namespace for the view tier of the application which provides interaction with the UI 
 */
ideasaurus.view = {}

/**
 * @namespace Namespace for the service tier of the application which provides http methods for accessing remote data sources
 */
ideasaurus.service = {}

ideasaurus.util = {}

/**
 * @property {string} applicationPath The path to the root directory of the application. Assigned dynamically when loaded
 */
ideasaurus.applicationPath;

ideasaurus.testPath = 'http://localhost:8080';

(function(){
	$( 'script' ).each( function(){ 
			var src = this.src;
			if( src.match(/ideasaurus\/ideasaurus\.js/) ) 
			{
				var endPos = src.indexOf( 'ideasaurus/ideasaurus.js' )
				ideasaurus.applicationPath = src.substr( 0 , endPos )
				return false;
			}
		})
})();

/**
 * Class for importing classes. This method simulates importing in other languages. The scripts are loaded via http, evaluated and executed.
 * Namespaces are automatically generated if they do not already exist. Class's namespaces and directory structure should match up.
 * The purpose of this class is to provide the flexibility of dynamically loading a lot of scripts for an application whose file stucture
 * is organized as one file per Class. It can be problematic as the scripts are evaluated which messes up the ability of debuggers like
 * firebug to track errors to line number and to step through code line by line. This class could be changed to use <document.write> to 
 * add script tags to the document at load time, which should restore debugger function. This would come at the cost of some flexibility however. 
 * @class 
 * @static
 * @param {string} classPath The package and class name. The packages should use dot notation, and the class name should have the extension left
 * off. The package struction must match the file stucture. So <ideasaurus.model.vo.NoteVO> = <applicationPath/ideasaurus/model/vo/NoteVO.js>.
 * The path is relative to <applicationPath>.
 * @todo Add some error handling for 404's etc
 */
ideasaurus.importClass = (function() {
	
	var queue = new Array();
	
	function processQueue() 
	{
		var path = ideasaurus.applicationPath + queue[0].classPath.replace( /[.]/g , '/' ) + '.js';
		var nameSpace = queue[0].classPath.split('.')
		nameSpace.pop()
		ideasaurus.Namespace.register( nameSpace.join( '.' ) )
/*		$.getScript( path , loadComplete )*/
        $.ajax({
            type: "GET",
            url: path,
            success: loadComplete,
            dataType: "script",
            cache: true
        });
	}
	
	function loadComplete()
	{
		if( queue[0].callback ) queue[0].callback();
		queue.shift();
		if( queue.length ) processQueue();
	}
	
	
	return function( classPath , callback) 
	{
		if( !callback ) callback = null;
		queue.push( {classPath:classPath , callback:callback} )
		if( queue.length == 1 ) processQueue();
	}
	
})();

/**
 * @class Utility class for managing namespaces
 * @author VectorX
 * @link http://www.codeproject.com/KB/scripting/jsnamespaces.aspx
 */
ideasaurus.Namespace =
{
    /**
     * Registers a namespace
     * @static
     * @function
     * @param {string} _Name The name of the namespace.
     */
    register : function(_Name)
    {
        var chk = false;
        var cob = "";
        var spc = _Name.split(".");
        for(var i = 0; i<spc.length; i++)
        {
            if(cob!=""){cob+=".";}
            cob+=spc[i];
            chk = this.exists(cob);
            if(!chk){this.create(cob);}
        }
        //if(chk){ throw "Namespace: " + _Name + " is already defined."; }
    },

    create : function(_Src)
    {
        eval("window." + _Src + " = new Object();");
    },

    exists : function(_Src)
    {
        eval("var NE = false; try{if(" + _Src + "){NE = true;}else{NE = false;}}catch(err){NE=false;}");
        return NE;
    }
}


// Constructor.
/**
 * Creates a new interface
 * @class 
 * @param {string} name Name of the interface
 * @param {array} methods Array of the names of the methods required by the interface
 */
ideasaurus.Interface = function(name, methods) {
    if(arguments.length != 2) {
        throw new Error("Interface constructor called with " + arguments.length
          + "arguments, but expected exactly 2.");
    }
    
    this.name = name;
    this.methods = [];
    for(var i = 0, len = methods.length; i < len; i++) {
        if(typeof methods[i] !== 'string') {
            throw new Error("Interface constructor expects method names to be " 
              + "passed in as a string.");
        }
        this.methods.push(methods[i]);        
    }    
};    

// Static class method.
/**
 * Checks to see if an object implements an interface
 * @function
 * @static
 * @param {object} object Object being checked
 * @param {object} interface Interfaces requiring implentation
 */
ideasaurus.Interface.ensureImplements = function(object) {
    if(arguments.length < 2) {
        throw new Error("Function Interface.ensureImplements called with " + 
          arguments.length  + "arguments, but expected at least 2.");
    }

    for(var i = 1, len = arguments.length; i < len; i++) {
        var iface = arguments[i];
        if(iface.constructor !== ideasaurus.Interface) {
            throw new Error("Function Interface.ensureImplements expects arguments "   
              + "two and above to be instances of Interface.");
        }
        
        for(var j = 0, methodsLen = iface.methods.length; j < methodsLen; j++) {
            var method = iface.methods[j];
            if(!object[method] || typeof object[method] !== 'function') {
                throw new Error("Function Interface.ensureImplements: object " 
                  + "does not implement the " + iface.name
                  + " interface. Method " + method + " was not found.");
            }
        }
    }
};

// For Extending Classes
/**
 * @function
 * Extends one class with another
 * @param {object} subClass The class that is extending
 * @param {object} superClass The class being extended
 */
ideasaurus.extend = function( subClass , superClass ) 
{
	var F = function() {};
	F.prototype = superClass.prototype;
	subClass.prototype = new F();
	subClass.prototype.constructor = subClass;
	
	subClass.superclass = superClass.prototype;
	if( superClass.prototype.constructor == Object.prototype.constructor )
		superClass.prototype.constructor = superClass
	
}


isEnumerableObject = function( obj , type )
{
	if( obj instanceof Object )
	{
		var enumCount = 0;
		for( var i in obj )
		{
			if( type && typeof(obj[i]) != type )
				return false
			enumCount++;	
		}	
		if( enumCount > 0 )
			return true
		else
			throw new Error( 'Object arguments must be enumerable key/value pairs (value=string)' )
	}
	else
	{
		return false
	}
	
}

isNumber = function( number ) 
{
	switch( typeof(number) )
	{
		case 'number' : return number
		case 'string' :
			if( !isNaN( parseInt( number ) ) )
				return Number( number )
		default :
			return false;
	}
}

combine = function( obj_1 , obj_2 )
{
	for( var i in obj_1 )
	{
		obj_2[i] = obj_1[i]
	}	
	return obj_2;

}
