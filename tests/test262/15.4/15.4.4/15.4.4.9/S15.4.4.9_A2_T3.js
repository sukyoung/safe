// TODO [[DefineOwnProperty]] for Array object
//  var obj = {
//    
//  };
//  obj.shift = Array.prototype.shift;
//  obj.length = 2.5;
//  var shift = obj.shift();
//  {
//    var __result1 = shift !== undefined;
//    var __expect1 = false;
//  }
//  {
//    var __result2 = obj.length !== 1;
//    var __expect2 = false;
//  }
//  obj.length = new Number(2);
//  var shift = obj.shift();
//  {
//    var __result3 = shift !== undefined;
//    var __expect3 = false;
//  }
//  {
//    var __result4 = obj.length !== 1;
//    var __expect4 = false;
//  }
//  
