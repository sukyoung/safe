var _used = [];

var _oldmethod_0 = parseInt;
parseInt = function() { 
  if(_used[0] === undefined) { _used[0] = 1; print('parseInt'); }
  return _oldmethod_0.apply(this, arguments);
};


var _oldmethod_1 = parseFloat;
parseFloat = function() { 
  if(_used[1] === undefined) { _used[1] = 1; print('parseFloat'); }
  return _oldmethod_1.apply(this, arguments);
};


var _oldmethod_2 = isNaN;
isNaN = function() { 
  if(_used[2] === undefined) { _used[2] = 1; print('isNaN'); }
  return _oldmethod_2.apply(this, arguments);
};


var _oldmethod_3 = isFinite;
isFinite = function() { 
  if(_used[3] === undefined) { _used[3] = 1; print('isFinite'); }
  return _oldmethod_3.apply(this, arguments);
};


var _oldmethod_4 = decodeURI;
decodeURI = function() { 
  if(_used[4] === undefined) { _used[4] = 1; print('decodeURI'); }
  return _oldmethod_4.apply(this, arguments);
};


var _oldmethod_5 = decodeURIComponent;
decodeURIComponent = function() { 
  if(_used[5] === undefined) { _used[5] = 1; print('decodeURIComponent'); }
  return _oldmethod_5.apply(this, arguments);
};


var _oldmethod_6 = encodeURI;
encodeURI = function() { 
  if(_used[6] === undefined) { _used[6] = 1; print('encodeURI'); }
  return _oldmethod_6.apply(this, arguments);
};


var _oldmethod_7 = encodeURIComponent;
encodeURIComponent = function() { 
  if(_used[7] === undefined) { _used[7] = 1; print('encodeURIComponent'); }
  return _oldmethod_7.apply(this, arguments);
};


var _oldmethod_8 = SyntaxError.prototype.toString;
SyntaxError.prototype.toString = function() { 
  if(_used[8] === undefined) { _used[8] = 1; print('SyntaxError.prototype.toString'); }
  return _oldmethod_8.apply(this, arguments);
};


var _oldmethod_9 = Math.abs;
Math.abs = function() { 
  if(_used[9] === undefined) { _used[9] = 1; print('Math.abs'); }
  return _oldmethod_9.apply(this, arguments);
};


var _oldmethod_10 = Math.acos;
Math.acos = function() { 
  if(_used[10] === undefined) { _used[10] = 1; print('Math.acos'); }
  return _oldmethod_10.apply(this, arguments);
};


var _oldmethod_11 = Math.asin;
Math.asin = function() { 
  if(_used[11] === undefined) { _used[11] = 1; print('Math.asin'); }
  return _oldmethod_11.apply(this, arguments);
};


var _oldmethod_12 = Math.atan;
Math.atan = function() { 
  if(_used[12] === undefined) { _used[12] = 1; print('Math.atan'); }
  return _oldmethod_12.apply(this, arguments);
};


var _oldmethod_13 = Math.atan2;
Math.atan2 = function() { 
  if(_used[13] === undefined) { _used[13] = 1; print('Math.atan2'); }
  return _oldmethod_13.apply(this, arguments);
};


var _oldmethod_14 = Math.ceil;
Math.ceil = function() { 
  if(_used[14] === undefined) { _used[14] = 1; print('Math.ceil'); }
  return _oldmethod_14.apply(this, arguments);
};


var _oldmethod_15 = Math.cos;
Math.cos = function() { 
  if(_used[15] === undefined) { _used[15] = 1; print('Math.cos'); }
  return _oldmethod_15.apply(this, arguments);
};


var _oldmethod_16 = Math.exp;
Math.exp = function() { 
  if(_used[16] === undefined) { _used[16] = 1; print('Math.exp'); }
  return _oldmethod_16.apply(this, arguments);
};


var _oldmethod_17 = Math.floor;
Math.floor = function() { 
  if(_used[17] === undefined) { _used[17] = 1; print('Math.floor'); }
  return _oldmethod_17.apply(this, arguments);
};


var _oldmethod_18 = Math.log;
Math.log = function() { 
  if(_used[18] === undefined) { _used[18] = 1; print('Math.log'); }
  return _oldmethod_18.apply(this, arguments);
};


var _oldmethod_19 = Math.max;
Math.max = function() { 
  if(_used[19] === undefined) { _used[19] = 1; print('Math.max'); }
  return _oldmethod_19.apply(this, arguments);
};


var _oldmethod_20 = Math.min;
Math.min = function() { 
  if(_used[20] === undefined) { _used[20] = 1; print('Math.min'); }
  return _oldmethod_20.apply(this, arguments);
};


var _oldmethod_21 = Math.pow;
Math.pow = function() { 
  if(_used[21] === undefined) { _used[21] = 1; print('Math.pow'); }
  return _oldmethod_21.apply(this, arguments);
};


var _oldmethod_22 = Math.random;
Math.random = function() { 
  if(_used[22] === undefined) { _used[22] = 1; print('Math.random'); }
  return _oldmethod_22.apply(this, arguments);
};


var _oldmethod_23 = Math.round;
Math.round = function() { 
  if(_used[23] === undefined) { _used[23] = 1; print('Math.round'); }
  return _oldmethod_23.apply(this, arguments);
};


var _oldmethod_24 = Math.sin;
Math.sin = function() { 
  if(_used[24] === undefined) { _used[24] = 1; print('Math.sin'); }
  return _oldmethod_24.apply(this, arguments);
};


var _oldmethod_25 = Math.sqrt;
Math.sqrt = function() { 
  if(_used[25] === undefined) { _used[25] = 1; print('Math.sqrt'); }
  return _oldmethod_25.apply(this, arguments);
};


var _oldmethod_26 = Math.tan;
Math.tan = function() { 
  if(_used[26] === undefined) { _used[26] = 1; print('Math.tan'); }
  return _oldmethod_26.apply(this, arguments);
};


var _oldmethod_27 = RegExp.prototype.exec;
RegExp.prototype.exec = function() { 
  if(_used[27] === undefined) { _used[27] = 1; print('RegExp.prototype.exec'); }
  return _oldmethod_27.apply(this, arguments);
};


var _oldmethod_28 = RegExp.prototype.test;
RegExp.prototype.test = function() { 
  if(_used[28] === undefined) { _used[28] = 1; print('RegExp.prototype.test'); }
  return _oldmethod_28.apply(this, arguments);
};


var _oldmethod_29 = RegExp.prototype.toString;
RegExp.prototype.toString = function() { 
  if(_used[29] === undefined) { _used[29] = 1; print('RegExp.prototype.toString'); }
  return _oldmethod_29.apply(this, arguments);
};


var _oldmethod_30 = ReferenceError.prototype.toString;
ReferenceError.prototype.toString = function() { 
  if(_used[30] === undefined) { _used[30] = 1; print('ReferenceError.prototype.toString'); }
  return _oldmethod_30.apply(this, arguments);
};


var _oldmethod_31 = Error.prototype.toString;
Error.prototype.toString = function() { 
  if(_used[31] === undefined) { _used[31] = 1; print('Error.prototype.toString'); }
  return _oldmethod_31.apply(this, arguments);
};


var _oldmethod_32 = Object.getPrototypeOf;
Object.getPrototypeOf = function() { 
  if(_used[32] === undefined) { _used[32] = 1; print('Object.getPrototypeOf'); }
  return _oldmethod_32.apply(this, arguments);
};


var _oldmethod_33 = Object.getOwnPropertyDescriptor;
Object.getOwnPropertyDescriptor = function() { 
  if(_used[33] === undefined) { _used[33] = 1; print('Object.getOwnPropertyDescriptor'); }
  return _oldmethod_33.apply(this, arguments);
};


var _oldmethod_34 = Object.getOwnPropertyNames;
Object.getOwnPropertyNames = function() { 
  if(_used[34] === undefined) { _used[34] = 1; print('Object.getOwnPropertyNames'); }
  return _oldmethod_34.apply(this, arguments);
};


var _oldmethod_35 = Object.create;
Object.create = function() { 
  if(_used[35] === undefined) { _used[35] = 1; print('Object.create'); }
  return _oldmethod_35.apply(this, arguments);
};


var _oldmethod_36 = Object.defineProperty;
Object.defineProperty = function() { 
  if(_used[36] === undefined) { _used[36] = 1; print('Object.defineProperty'); }
  return _oldmethod_36.apply(this, arguments);
};


var _oldmethod_37 = Object.defineProperties;
Object.defineProperties = function() { 
  if(_used[37] === undefined) { _used[37] = 1; print('Object.defineProperties'); }
  return _oldmethod_37.apply(this, arguments);
};


var _oldmethod_38 = Object.seal;
Object.seal = function() { 
  if(_used[38] === undefined) { _used[38] = 1; print('Object.seal'); }
  return _oldmethod_38.apply(this, arguments);
};


var _oldmethod_39 = Object.freeze;
Object.freeze = function() { 
  if(_used[39] === undefined) { _used[39] = 1; print('Object.freeze'); }
  return _oldmethod_39.apply(this, arguments);
};


var _oldmethod_40 = Object.preventExtensions;
Object.preventExtensions = function() { 
  if(_used[40] === undefined) { _used[40] = 1; print('Object.preventExtensions'); }
  return _oldmethod_40.apply(this, arguments);
};


var _oldmethod_41 = Object.isSealed;
Object.isSealed = function() { 
  if(_used[41] === undefined) { _used[41] = 1; print('Object.isSealed'); }
  return _oldmethod_41.apply(this, arguments);
};


var _oldmethod_42 = Object.isFrozen;
Object.isFrozen = function() { 
  if(_used[42] === undefined) { _used[42] = 1; print('Object.isFrozen'); }
  return _oldmethod_42.apply(this, arguments);
};


var _oldmethod_43 = Object.isExtensible;
Object.isExtensible = function() { 
  if(_used[43] === undefined) { _used[43] = 1; print('Object.isExtensible'); }
  return _oldmethod_43.apply(this, arguments);
};


var _oldmethod_44 = Object.keys;
Object.keys = function() { 
  if(_used[44] === undefined) { _used[44] = 1; print('Object.keys'); }
  return _oldmethod_44.apply(this, arguments);
};


var _oldmethod_45 = Function.prototype.toString;
Function.prototype.toString = function() { 
  if(_used[45] === undefined) { _used[45] = 1; print('Function.prototype.toString'); }
  return _oldmethod_45.apply(this, arguments);
};


var _oldmethod_46 = Function.prototype.call;
Function.prototype.call = function() { 
  if(_used[46] === undefined) { _used[46] = 1; print('Function.prototype.call'); }
  return _oldmethod_46.apply(this, arguments);
};


var _oldmethod_47 = Function.prototype.bind;
Function.prototype.bind = function() { 
  if(_used[47] === undefined) { _used[47] = 1; print('Function.prototype.bind'); }
  return _oldmethod_47.apply(this, arguments);
};


var _oldmethod_48 = TypeError.prototype.toString;
TypeError.prototype.toString = function() { 
  if(_used[48] === undefined) { _used[48] = 1; print('TypeError.prototype.toString'); }
  return _oldmethod_48.apply(this, arguments);
};


var _oldmethod_49 = JSON.parse;
JSON.parse = function() { 
  if(_used[49] === undefined) { _used[49] = 1; print('JSON.parse'); }
  return _oldmethod_49.apply(this, arguments);
};


var _oldmethod_50 = JSON.stringify;
JSON.stringify = function() { 
  if(_used[50] === undefined) { _used[50] = 1; print('JSON.stringify'); }
  return _oldmethod_50.apply(this, arguments);
};


var _oldmethod_51 = Date.parse;
Date.parse = function() { 
  if(_used[51] === undefined) { _used[51] = 1; print('Date.parse'); }
  return _oldmethod_51.apply(this, arguments);
};


var _oldmethod_52 = Date.UTC;
Date.UTC = function() { 
  if(_used[52] === undefined) { _used[52] = 1; print('Date.UTC'); }
  return _oldmethod_52.apply(this, arguments);
};


var _oldmethod_53 = Date.now;
Date.now = function() { 
  if(_used[53] === undefined) { _used[53] = 1; print('Date.now'); }
  return _oldmethod_53.apply(this, arguments);
};


var _oldmethod_54 = RangeError.prototype.toString;
RangeError.prototype.toString = function() { 
  if(_used[54] === undefined) { _used[54] = 1; print('RangeError.prototype.toString'); }
  return _oldmethod_54.apply(this, arguments);
};


var _oldmethod_55 = String.prototype.toString;
String.prototype.toString = function() { 
  if(_used[55] === undefined) { _used[55] = 1; print('String.prototype.toString'); }
  return _oldmethod_55.apply(this, arguments);
};


var _oldmethod_56 = String.prototype.valueOf;
String.prototype.valueOf = function() { 
  if(_used[56] === undefined) { _used[56] = 1; print('String.prototype.valueOf'); }
  return _oldmethod_56.apply(this, arguments);
};


var _oldmethod_57 = String.prototype.charAt;
String.prototype.charAt = function() { 
  if(_used[57] === undefined) { _used[57] = 1; print('String.prototype.charAt'); }
  return _oldmethod_57.apply(this, arguments);
};


var _oldmethod_58 = String.prototype.charCodeAt;
String.prototype.charCodeAt = function() { 
  if(_used[58] === undefined) { _used[58] = 1; print('String.prototype.charCodeAt'); }
  return _oldmethod_58.apply(this, arguments);
};


var _oldmethod_59 = String.prototype.concat;
String.prototype.concat = function() { 
  if(_used[59] === undefined) { _used[59] = 1; print('String.prototype.concat'); }
  return _oldmethod_59.apply(this, arguments);
};


var _oldmethod_60 = String.prototype.indexOf;
String.prototype.indexOf = function() { 
  if(_used[60] === undefined) { _used[60] = 1; print('String.prototype.indexOf'); }
  return _oldmethod_60.apply(this, arguments);
};


var _oldmethod_61 = String.prototype.lastIndexOf;
String.prototype.lastIndexOf = function() { 
  if(_used[61] === undefined) { _used[61] = 1; print('String.prototype.lastIndexOf'); }
  return _oldmethod_61.apply(this, arguments);
};


var _oldmethod_62 = String.prototype.localeCompare;
String.prototype.localeCompare = function() { 
  if(_used[62] === undefined) { _used[62] = 1; print('String.prototype.localeCompare'); }
  return _oldmethod_62.apply(this, arguments);
};


var _oldmethod_63 = String.prototype.match;
String.prototype.match = function() { 
  if(_used[63] === undefined) { _used[63] = 1; print('String.prototype.match'); }
  return _oldmethod_63.apply(this, arguments);
};


var _oldmethod_64 = String.prototype.replace;
String.prototype.replace = function() { 
  if(_used[64] === undefined) { _used[64] = 1; print('String.prototype.replace'); }
  return _oldmethod_64.apply(this, arguments);
};


var _oldmethod_65 = String.prototype.search;
String.prototype.search = function() { 
  if(_used[65] === undefined) { _used[65] = 1; print('String.prototype.search'); }
  return _oldmethod_65.apply(this, arguments);
};


var _oldmethod_66 = String.prototype.slice;
String.prototype.slice = function() { 
  if(_used[66] === undefined) { _used[66] = 1; print('String.prototype.slice'); }
  return _oldmethod_66.apply(this, arguments);
};


var _oldmethod_67 = String.prototype.split;
String.prototype.split = function() { 
  if(_used[67] === undefined) { _used[67] = 1; print('String.prototype.split'); }
  return _oldmethod_67.apply(this, arguments);
};


var _oldmethod_68 = String.prototype.substring;
String.prototype.substring = function() { 
  if(_used[68] === undefined) { _used[68] = 1; print('String.prototype.substring'); }
  return _oldmethod_68.apply(this, arguments);
};


var _oldmethod_69 = String.prototype.toLowerCase;
String.prototype.toLowerCase = function() { 
  if(_used[69] === undefined) { _used[69] = 1; print('String.prototype.toLowerCase'); }
  return _oldmethod_69.apply(this, arguments);
};


var _oldmethod_70 = String.prototype.toLocaleLowerCase;
String.prototype.toLocaleLowerCase = function() { 
  if(_used[70] === undefined) { _used[70] = 1; print('String.prototype.toLocaleLowerCase'); }
  return _oldmethod_70.apply(this, arguments);
};


var _oldmethod_71 = String.prototype.toUpperCase;
String.prototype.toUpperCase = function() { 
  if(_used[71] === undefined) { _used[71] = 1; print('String.prototype.toUpperCase'); }
  return _oldmethod_71.apply(this, arguments);
};


var _oldmethod_72 = String.prototype.toLocaleUpperCase;
String.prototype.toLocaleUpperCase = function() { 
  if(_used[72] === undefined) { _used[72] = 1; print('String.prototype.toLocaleUpperCase'); }
  return _oldmethod_72.apply(this, arguments);
};


var _oldmethod_73 = String.prototype.trim;
String.prototype.trim = function() { 
  if(_used[73] === undefined) { _used[73] = 1; print('String.prototype.trim'); }
  return _oldmethod_73.apply(this, arguments);
};


var _oldmethod_74 = Number.prototype.toString;
Number.prototype.toString = function() { 
  if(_used[74] === undefined) { _used[74] = 1; print('Number.prototype.toString'); }
  return _oldmethod_74.apply(this, arguments);
};


var _oldmethod_75 = Number.prototype.toLocaleString;
Number.prototype.toLocaleString = function() { 
  if(_used[75] === undefined) { _used[75] = 1; print('Number.prototype.toLocaleString'); }
  return _oldmethod_75.apply(this, arguments);
};


var _oldmethod_76 = Number.prototype.valueOf;
Number.prototype.valueOf = function() { 
  if(_used[76] === undefined) { _used[76] = 1; print('Number.prototype.valueOf'); }
  return _oldmethod_76.apply(this, arguments);
};


var _oldmethod_77 = Number.prototype.toFixed;
Number.prototype.toFixed = function() { 
  if(_used[77] === undefined) { _used[77] = 1; print('Number.prototype.toFixed'); }
  return _oldmethod_77.apply(this, arguments);
};


var _oldmethod_78 = Number.prototype.toExponential;
Number.prototype.toExponential = function() { 
  if(_used[78] === undefined) { _used[78] = 1; print('Number.prototype.toExponential'); }
  return _oldmethod_78.apply(this, arguments);
};


var _oldmethod_79 = Number.prototype.toPrecision;
Number.prototype.toPrecision = function() { 
  if(_used[79] === undefined) { _used[79] = 1; print('Number.prototype.toPrecision'); }
  return _oldmethod_79.apply(this, arguments);
};


var _oldmethod_80 = Date.prototype.toString;
Date.prototype.toString = function() { 
  if(_used[80] === undefined) { _used[80] = 1; print('Date.prototype.toString'); }
  return _oldmethod_80.apply(this, arguments);
};


var _oldmethod_81 = Date.prototype.toDateString;
Date.prototype.toDateString = function() { 
  if(_used[81] === undefined) { _used[81] = 1; print('Date.prototype.toDateString'); }
  return _oldmethod_81.apply(this, arguments);
};


var _oldmethod_82 = Date.prototype.toTimeString;
Date.prototype.toTimeString = function() { 
  if(_used[82] === undefined) { _used[82] = 1; print('Date.prototype.toTimeString'); }
  return _oldmethod_82.apply(this, arguments);
};


var _oldmethod_83 = Date.prototype.toLocaleString;
Date.prototype.toLocaleString = function() { 
  if(_used[83] === undefined) { _used[83] = 1; print('Date.prototype.toLocaleString'); }
  return _oldmethod_83.apply(this, arguments);
};


var _oldmethod_84 = Date.prototype.toLocaleTimeString;
Date.prototype.toLocaleTimeString = function() { 
  if(_used[84] === undefined) { _used[84] = 1; print('Date.prototype.toLocaleTimeString'); }
  return _oldmethod_84.apply(this, arguments);
};


var _oldmethod_85 = Date.prototype.valueOf;
Date.prototype.valueOf = function() { 
  if(_used[85] === undefined) { _used[85] = 1; print('Date.prototype.valueOf'); }
  return _oldmethod_85.apply(this, arguments);
};


var _oldmethod_86 = Date.prototype.getTime;
Date.prototype.getTime = function() { 
  if(_used[86] === undefined) { _used[86] = 1; print('Date.prototype.getTime'); }
  return _oldmethod_86.apply(this, arguments);
};


var _oldmethod_87 = Date.prototype.getFullYear;
Date.prototype.getFullYear = function() { 
  if(_used[87] === undefined) { _used[87] = 1; print('Date.prototype.getFullYear'); }
  return _oldmethod_87.apply(this, arguments);
};


var _oldmethod_88 = Date.prototype.getUTCFullYear;
Date.prototype.getUTCFullYear = function() { 
  if(_used[88] === undefined) { _used[88] = 1; print('Date.prototype.getUTCFullYear'); }
  return _oldmethod_88.apply(this, arguments);
};


var _oldmethod_89 = Date.prototype.getMonth;
Date.prototype.getMonth = function() { 
  if(_used[89] === undefined) { _used[89] = 1; print('Date.prototype.getMonth'); }
  return _oldmethod_89.apply(this, arguments);
};


var _oldmethod_90 = Date.prototype.getUTCMonth;
Date.prototype.getUTCMonth = function() { 
  if(_used[90] === undefined) { _used[90] = 1; print('Date.prototype.getUTCMonth'); }
  return _oldmethod_90.apply(this, arguments);
};


var _oldmethod_91 = Date.prototype.getDate;
Date.prototype.getDate = function() { 
  if(_used[91] === undefined) { _used[91] = 1; print('Date.prototype.getDate'); }
  return _oldmethod_91.apply(this, arguments);
};


var _oldmethod_92 = Date.prototype.getUTCDate;
Date.prototype.getUTCDate = function() { 
  if(_used[92] === undefined) { _used[92] = 1; print('Date.prototype.getUTCDate'); }
  return _oldmethod_92.apply(this, arguments);
};


var _oldmethod_93 = Date.prototype.getDay;
Date.prototype.getDay = function() { 
  if(_used[93] === undefined) { _used[93] = 1; print('Date.prototype.getDay'); }
  return _oldmethod_93.apply(this, arguments);
};


var _oldmethod_94 = Date.prototype.getUTCDay;
Date.prototype.getUTCDay = function() { 
  if(_used[94] === undefined) { _used[94] = 1; print('Date.prototype.getUTCDay'); }
  return _oldmethod_94.apply(this, arguments);
};


var _oldmethod_95 = Date.prototype.getHours;
Date.prototype.getHours = function() { 
  if(_used[95] === undefined) { _used[95] = 1; print('Date.prototype.getHours'); }
  return _oldmethod_95.apply(this, arguments);
};


var _oldmethod_96 = Date.prototype.getUTCHours;
Date.prototype.getUTCHours = function() { 
  if(_used[96] === undefined) { _used[96] = 1; print('Date.prototype.getUTCHours'); }
  return _oldmethod_96.apply(this, arguments);
};


var _oldmethod_97 = Date.prototype.getMinutes;
Date.prototype.getMinutes = function() { 
  if(_used[97] === undefined) { _used[97] = 1; print('Date.prototype.getMinutes'); }
  return _oldmethod_97.apply(this, arguments);
};


var _oldmethod_98 = Date.prototype.getUTCMinutes;
Date.prototype.getUTCMinutes = function() { 
  if(_used[98] === undefined) { _used[98] = 1; print('Date.prototype.getUTCMinutes'); }
  return _oldmethod_98.apply(this, arguments);
};


var _oldmethod_99 = Date.prototype.getSeconds;
Date.prototype.getSeconds = function() { 
  if(_used[99] === undefined) { _used[99] = 1; print('Date.prototype.getSeconds'); }
  return _oldmethod_99.apply(this, arguments);
};


var _oldmethod_100 = Date.prototype.getUTCSeconds;
Date.prototype.getUTCSeconds = function() { 
  if(_used[100] === undefined) { _used[100] = 1; print('Date.prototype.getUTCSeconds'); }
  return _oldmethod_100.apply(this, arguments);
};


var _oldmethod_101 = Date.prototype.getMilliseconds;
Date.prototype.getMilliseconds = function() { 
  if(_used[101] === undefined) { _used[101] = 1; print('Date.prototype.getMilliseconds'); }
  return _oldmethod_101.apply(this, arguments);
};


var _oldmethod_102 = Date.prototype.getUTCMilliseconds;
Date.prototype.getUTCMilliseconds = function() { 
  if(_used[102] === undefined) { _used[102] = 1; print('Date.prototype.getUTCMilliseconds'); }
  return _oldmethod_102.apply(this, arguments);
};


var _oldmethod_103 = Date.prototype.getTimezoneOffset;
Date.prototype.getTimezoneOffset = function() { 
  if(_used[103] === undefined) { _used[103] = 1; print('Date.prototype.getTimezoneOffset'); }
  return _oldmethod_103.apply(this, arguments);
};


var _oldmethod_104 = Date.prototype.setTime;
Date.prototype.setTime = function() { 
  if(_used[104] === undefined) { _used[104] = 1; print('Date.prototype.setTime'); }
  return _oldmethod_104.apply(this, arguments);
};


var _oldmethod_105 = Date.prototype.setMilliseconds;
Date.prototype.setMilliseconds = function() { 
  if(_used[105] === undefined) { _used[105] = 1; print('Date.prototype.setMilliseconds'); }
  return _oldmethod_105.apply(this, arguments);
};


var _oldmethod_106 = Date.prototype.setUTCMilliseconds;
Date.prototype.setUTCMilliseconds = function() { 
  if(_used[106] === undefined) { _used[106] = 1; print('Date.prototype.setUTCMilliseconds'); }
  return _oldmethod_106.apply(this, arguments);
};


var _oldmethod_107 = Date.prototype.setSeconds;
Date.prototype.setSeconds = function() { 
  if(_used[107] === undefined) { _used[107] = 1; print('Date.prototype.setSeconds'); }
  return _oldmethod_107.apply(this, arguments);
};


var _oldmethod_108 = Date.prototype.setUTCSeconds;
Date.prototype.setUTCSeconds = function() { 
  if(_used[108] === undefined) { _used[108] = 1; print('Date.prototype.setUTCSeconds'); }
  return _oldmethod_108.apply(this, arguments);
};


var _oldmethod_109 = Date.prototype.setMinutes;
Date.prototype.setMinutes = function() { 
  if(_used[109] === undefined) { _used[109] = 1; print('Date.prototype.setMinutes'); }
  return _oldmethod_109.apply(this, arguments);
};


var _oldmethod_110 = Date.prototype.setUTCMinutes;
Date.prototype.setUTCMinutes = function() { 
  if(_used[110] === undefined) { _used[110] = 1; print('Date.prototype.setUTCMinutes'); }
  return _oldmethod_110.apply(this, arguments);
};


var _oldmethod_111 = Date.prototype.setHours;
Date.prototype.setHours = function() { 
  if(_used[111] === undefined) { _used[111] = 1; print('Date.prototype.setHours'); }
  return _oldmethod_111.apply(this, arguments);
};


var _oldmethod_112 = Date.prototype.setUTCHours;
Date.prototype.setUTCHours = function() { 
  if(_used[112] === undefined) { _used[112] = 1; print('Date.prototype.setUTCHours'); }
  return _oldmethod_112.apply(this, arguments);
};


var _oldmethod_113 = EvalError.prototype.toString;
EvalError.prototype.toString = function() { 
  if(_used[113] === undefined) { _used[113] = 1; print('EvalError.prototype.toString'); }
  return _oldmethod_113.apply(this, arguments);
};


var _oldmethod_114 = String.fromCharCode;
String.fromCharCode = function() { 
  if(_used[114] === undefined) { _used[114] = 1; print('String.fromCharCode'); }
  return _oldmethod_114.apply(this, arguments);
};


var _oldmethod_115 = Array.prototype.toString;
Array.prototype.toString = function() { 
  if(_used[115] === undefined) { _used[115] = 1; print('Array.prototype.toString'); }
  return _oldmethod_115.apply(this, arguments);
};


var _oldmethod_116 = Array.prototype.indexOf;
Array.prototype.indexOf = function() { 
  if(_used[116] === undefined) { _used[116] = 1; print('Array.prototype.indexOf'); }
  return _oldmethod_116.apply(this, arguments);
};


var _oldmethod_117 = Array.prototype.toLocaleString;
Array.prototype.toLocaleString = function() { 
  if(_used[117] === undefined) { _used[117] = 1; print('Array.prototype.toLocaleString'); }
  return _oldmethod_117.apply(this, arguments);
};


var _oldmethod_118 = Array.prototype.concat;
Array.prototype.concat = function() { 
  if(_used[118] === undefined) { _used[118] = 1; print('Array.prototype.concat'); }
  return _oldmethod_118.apply(this, arguments);
};


var _oldmethod_119 = Array.prototype.join;
Array.prototype.join = function() { 
  if(_used[119] === undefined) { _used[119] = 1; print('Array.prototype.join'); }
  return _oldmethod_119.apply(this, arguments);
};


var _oldmethod_120 = Array.prototype.pop;
Array.prototype.pop = function() { 
  if(_used[120] === undefined) { _used[120] = 1; print('Array.prototype.pop'); }
  return _oldmethod_120.apply(this, arguments);
};


var _oldmethod_121 = Array.prototype.push;
Array.prototype.push = function() { 
  if(_used[121] === undefined) { _used[121] = 1; print('Array.prototype.push'); }
  return _oldmethod_121.apply(this, arguments);
};


var _oldmethod_122 = Array.prototype.reverse;
Array.prototype.reverse = function() { 
  if(_used[122] === undefined) { _used[122] = 1; print('Array.prototype.reverse'); }
  return _oldmethod_122.apply(this, arguments);
};


var _oldmethod_123 = Array.prototype.shift;
Array.prototype.shift = function() { 
  if(_used[123] === undefined) { _used[123] = 1; print('Array.prototype.shift'); }
  return _oldmethod_123.apply(this, arguments);
};


var _oldmethod_124 = Array.prototype.slice;
Array.prototype.slice = function() { 
  if(_used[124] === undefined) { _used[124] = 1; print('Array.prototype.slice'); }
  return _oldmethod_124.apply(this, arguments);
};


var _oldmethod_125 = Array.prototype.sort;
Array.prototype.sort = function() { 
  if(_used[125] === undefined) { _used[125] = 1; print('Array.prototype.sort'); }
  return _oldmethod_125.apply(this, arguments);
};


var _oldmethod_126 = Array.prototype.splice;
Array.prototype.splice = function() { 
  if(_used[126] === undefined) { _used[126] = 1; print('Array.prototype.splice'); }
  return _oldmethod_126.apply(this, arguments);
};


var _oldmethod_127 = Array.prototype.unshift;
Array.prototype.unshift = function() { 
  if(_used[127] === undefined) { _used[127] = 1; print('Array.prototype.unshift'); }
  return _oldmethod_127.apply(this, arguments);
};


var _oldmethod_128 = Array.prototype.lastIndexOf;
Array.prototype.lastIndexOf = function() { 
  if(_used[128] === undefined) { _used[128] = 1; print('Array.prototype.lastIndexOf'); }
  return _oldmethod_128.apply(this, arguments);
};


var _oldmethod_129 = Array.prototype.every;
Array.prototype.every = function() { 
  if(_used[129] === undefined) { _used[129] = 1; print('Array.prototype.every'); }
  return _oldmethod_129.apply(this, arguments);
};


var _oldmethod_130 = Array.prototype.some;
Array.prototype.some = function() { 
  if(_used[130] === undefined) { _used[130] = 1; print('Array.prototype.some'); }
  return _oldmethod_130.apply(this, arguments);
};


var _oldmethod_131 = Array.prototype.forEach;
Array.prototype.forEach = function() { 
  if(_used[131] === undefined) { _used[131] = 1; print('Array.prototype.forEach'); }
  return _oldmethod_131.apply(this, arguments);
};


var _oldmethod_132 = Array.prototype.map;
Array.prototype.map = function() { 
  if(_used[132] === undefined) { _used[132] = 1; print('Array.prototype.map'); }
  return _oldmethod_132.apply(this, arguments);
};


var _oldmethod_133 = Array.prototype.filter;
Array.prototype.filter = function() { 
  if(_used[133] === undefined) { _used[133] = 1; print('Array.prototype.filter'); }
  return _oldmethod_133.apply(this, arguments);
};


var _oldmethod_134 = Array.prototype.reduce;
Array.prototype.reduce = function() { 
  if(_used[134] === undefined) { _used[134] = 1; print('Array.prototype.reduce'); }
  return _oldmethod_134.apply(this, arguments);
};


var _oldmethod_135 = Array.prototype.reduceRight;
Array.prototype.reduceRight = function() { 
  if(_used[135] === undefined) { _used[135] = 1; print('Array.prototype.reduceRight'); }
  return _oldmethod_135.apply(this, arguments);
};


var _oldmethod_136 = Object.prototype.toString;
Object.prototype.toString = function() { 
  if(_used[136] === undefined) { _used[136] = 1; print('Object.prototype.toString'); }
  return _oldmethod_136.apply(this, arguments);
};


var _oldmethod_137 = Object.prototype.toLocaleString;
Object.prototype.toLocaleString = function() { 
  if(_used[137] === undefined) { _used[137] = 1; print('Object.prototype.toLocaleString'); }
  return _oldmethod_137.apply(this, arguments);
};


var _oldmethod_138 = Object.prototype.valueOf;
Object.prototype.valueOf = function() { 
  if(_used[138] === undefined) { _used[138] = 1; print('Object.prototype.valueOf'); }
  return _oldmethod_138.apply(this, arguments);
};


var _oldmethod_139 = Object.prototype.hasOwnProperty;
Object.prototype.hasOwnProperty = function() { 
  if(_used[139] === undefined) { _used[139] = 1; print('Object.prototype.hasOwnProperty'); }
  return _oldmethod_139.apply(this, arguments);
};


var _oldmethod_140 = Object.prototype.isPropertyOf;
Object.prototype.isPropertyOf = function() { 
  if(_used[140] === undefined) { _used[140] = 1; print('Object.prototype.isPropertyOf'); }
  return _oldmethod_140.apply(this, arguments);
};


var _oldmethod_141 = Object.prototype.propertyIsEnumerable;
Object.prototype.propertyIsEnumerable = function() { 
  if(_used[141] === undefined) { _used[141] = 1; print('Object.prototype.propertyIsEnumerable'); }
  return _oldmethod_141.apply(this, arguments);
};


var _oldmethod_142 = Array.isArray;
Array.isArray = function() { 
  if(_used[142] === undefined) { _used[142] = 1; print('Array.isArray'); }
  return _oldmethod_142.apply(this, arguments);
};


var _oldmethod_143 = Boolean.prototype.toString;
Boolean.prototype.toString = function() { 
  if(_used[143] === undefined) { _used[143] = 1; print('Boolean.prototype.toString'); }
  return _oldmethod_143.apply(this, arguments);
};


var _oldmethod_144 = Boolean.prototype.valueOf;
Boolean.prototype.valueOf = function() { 
  if(_used[144] === undefined) { _used[144] = 1; print('Boolean.prototype.valueOf'); }
  return _oldmethod_144.apply(this, arguments);
};


var _oldmethod_145 = URIError.prototype.toString;
URIError.prototype.toString = function() { 
  if(_used[145] === undefined) { _used[145] = 1; print('URIError.prototype.toString'); }
  return _oldmethod_145.apply(this, arguments);
};

