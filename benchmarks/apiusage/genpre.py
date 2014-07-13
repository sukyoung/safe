#!/usr/bin/python

# Generate JavaScript file that hooks built-in functions

builtins = {
		"":
			[
			#"eval", 
			"parseInt", "parseFloat", "isNaN", "isFinite", 
			"decodeURI", "decodeURIComponent", "encodeURI", "encodeURIComponent",
			],

		"Object":
			["getPrototypeOf", "getOwnPropertyDescriptor", "getOwnPropertyNames", 
			"create", "defineProperty", "defineProperties", "seal", "freeze", 
			"preventExtensions", "isSealed", "isFrozen", "isExtensible", "keys"],

		"Object.prototype":
			[
			"toString", 
			"toLocaleString", "valueOf", "hasOwnProperty", "isPropertyOf", "propertyIsEnumerable"], 

		"Function.prototype":
			[
			#"apply",
			"toString", "call", "bind"],

		"Array": 
			["isArray"],

		"Array.prototype": 
			[
			"toString", "indexOf", "toLocaleString", "concat", "join", "pop", "push", "reverse", 
			"shift", "slice", "sort", "splice", "unshift", "lastIndexOf", "every", "some", 
			"forEach", "map", "filter", "reduce", "reduceRight"], 

		"String":
			["fromCharCode"],

		"String.prototype": 
			[
			"toString",
			"valueOf", "charAt", "charCodeAt", "concat", "indexOf", "lastIndexOf", 
			"localeCompare", "match", "replace", "search", "slice", "split", "substring", 
			"toLowerCase", "toLocaleLowerCase", "toUpperCase", "toLocaleUpperCase", "trim" ],

		"Boolean.prototype":
			[
			"toString", "valueOf" ],

		"Number.prototype":
			[
			"toString", "toLocaleString", "valueOf", "toFixed", "toExponential", "toPrecision"],

		"Math":
			["abs", "acos", "asin", "atan", "atan2", "ceil", "cos", "exp", "floor", 
			"log", "max", "min", "pow", "random", "round", "sin", "sqrt", "tan"],

		"Date":
			[ "parse", "UTC", "now"], 

		"Date.prototype":
			[ 
			"toString", "toDateString", "toTimeString", "toLocaleString", "toLocaleTimeString", 
			"valueOf", "getTime", "getFullYear", "getUTCFullYear", "getMonth", 
			"getUTCMonth", "getDate", "getUTCDate", "getDay", "getUTCDay", "getHours", 
			"getUTCHours", "getMinutes", "getUTCMinutes", "getSeconds", "getUTCSeconds", 
			"getMilliseconds", "getUTCMilliseconds", "getTimezoneOffset", "setTime", 
			"setMilliseconds", "setUTCMilliseconds", "setSeconds", "setUTCSeconds", "setMinutes", 
			"setUTCMinutes", "setHours", "setUTCHours"],

		"RegExp.prototype":
			[ "exec", "test", "toString" ],

		"JSON":
			["parse","stringify"],

		"Error.prototype": ["toString"],
		"EvalError.prototype": ["toString"],
		"RangeError.prototype": ["toString"],
		"ReferenceError.prototype": ["toString"],
		"SyntaxError.prototype": ["toString"],
		"TypeError.prototype": ["toString"],
		"URIError.prototype": ["toString"],

}

print "var _used = [];"

fn = 0
for o, ms in builtins.iteritems():
		for m in ms:
				if len(o) == 0:
						om = m
				else:
						om = o+"."+m
				print """
var _oldmethod_%d = %s;
%s = function() { 
  if(_used[%d] === undefined) { _used[%d] = 1; print('%s'); }
  return _oldmethod_%d.apply(this, arguments);
};
""" % (fn, om, om, fn, fn, om, fn)
				fn = fn + 1


