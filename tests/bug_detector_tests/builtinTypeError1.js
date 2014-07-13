// 15.2.4.3 Object.prototype.toLocaleString ( )
//if (Math.random()) {
    var o_15_2_4_3 = {};
    o_15_2_4_3.toString = 123;
    o_15_2_4_3.toLocaleString();
//}

// 15.3.4.3 Function.prototype.apply (thisArg, argArray)
//if (Math.random()) {
    var o_15_3_4_3 = {};
    o_15_3_4_3.apply = Function.apply;
    o_15_3_4_3.apply(undefined, {});
//}

// 15.3.4.4 Function.prototype.call (thisArg [ , arg1 [ , arg2, ... ] ] )
//if (Math.random()) {
    var o_15_3_4_4 = {};
    o_15_3_4_4.call = Function.call;
    o_15_3_4_4.call(undefined);
//}

// 15.3.4.5 Function.prototype.bind (thisArg [, arg1 [, arg2, ...]])
//if (Math.random()) {
    var o_15_3_4_5 = {};
    o_15_3_4_5.bind = Function.bind;
    o_15_3_4_5.bind(undefined);
//}

// 15.9.5.44 Date.prototype.toJSON ( key )
//if (Math.random()) {
    var o_15_9_5_44 = new Date();
    Date.prototype.toISOString = 123;
    o_15_9_5_44.toJSON(0);
//}

// 15.11.4.4 Error.prototype.toString ( )
//if (Math.random()) {
    Error.prototype.toString.call(123);
//}
