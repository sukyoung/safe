// TODO [[DefineOwnProperty]] for Array object
//  var obj = {
//    
//  };
//  obj.slice = Array.prototype.slice;
//  obj[4294967294] = "x";
//  obj.length = - 1;
//  var arr = obj.slice(4294967294, 4294967295);
//  {
//    var __result1 = arr.length !== 1;
//    var __expect1 = false;
//  }
//  {
//    var __result2 = arr[0] !== "x";
//    var __expect2 = false;
//  }
//  
