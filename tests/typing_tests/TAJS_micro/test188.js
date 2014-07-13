jQuery = function() {}

jQuery.extend = function() {
    for (i = 0; i < 1; i++ ) {
	    options = arguments[ 0 ]
	    //options = {a:2, b:9 } // Works!
//	    dumpObject(options)
	    for ( name in options ) {
//	      dumpValue("Inside loop")
	   	  __result1 = "Inside loop";  // for SAFE

	    }
    }
}
__expect1 = "Inside loop";  // for SAFE

jQuery.extend({
    each: function( ) {},
    browser: {}
});

var __result2 = options.each instanceof Function;  // for SAFE
var __expect2 = true;  // for SAFE

var __result3 = options.browser instanceof Object;  // for SAFE
var __expect3 = true;  // for SAFE
