var IProxy = new ideasaurus.Interface( 'IProxy' , [] )

var INote = new ideasaurus.Interface( 'INote' , [
	'setId' , 
	'getId' , 
	'setCorkboardId' ,
	'getCorkboardId' ,
	'setIsCollapsed',
	'getIsCollapsed',
	'setX',
	'getX',
	'setY',
	'getY',
	'setWidth',
	'getWidth',
	'setSkin',
	'getSkin',
	'setContent',
	'getContent'
	] )
	
var IUserVO = new ideasaurus.Interface( 'IUserVO' , [
	'setId',
	'getId',
	'setEmail',
	'getEmail'
])

var ICorkboard = new ideasaurus.Interface( 'ICorkboard' , [
	'setId',
	'getId',
	'setWeight',
	'getWeight',
	'setLabel',
	'getLabel',
    'setFocused',
    'isFocused'
	]);
	
	
var IAppState = new ideasaurus.Interface( 'IAppState' , [
	'setPath' , 
	'getPath' , 
	'setMediatorName' , 
	'getMediatorName',
	'setTitle',
	'getTitle'
	])	
	
var INotification = new ideasaurus.Interface( 'INotification' , [
	'setType',
	'getType',
	'setIsCloseable',
	'getIsCloseable',
	'setMessage',
	'getMessage',
	'setOpen',
	'getOpen',
	'setId',
	'getId'
	])