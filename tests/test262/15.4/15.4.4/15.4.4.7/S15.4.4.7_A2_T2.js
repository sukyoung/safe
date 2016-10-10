// TODO [[DefineOwnProperty]] for Array object
//  var obj = {
//    
//  };
//  obj.push = Array.prototype.push;
//  obj.length = NaN;
//  var push = obj.push(- 1);
//  {
//    var __result1 = push !== 1;
//    var __expect1 = false;
//  }
//  {
//    var __result2 = obj.length !== 1;
//    var __expect2 = false;
//  }
//  {
//    var __result3 = obj["0"] !== - 1;
//    var __expect3 = false;
//  }
//  obj.length = Number.POSITIVE_INFINITY;
//  var push = obj.push(- 4);
//  {
//    var __result4 = push !== 1;
//    var __expect4 = false;
//  }
//  {
//    var __result5 = obj.length !== 1;
//    var __expect5 = false;
//  }
//  {
//    var __result6 = obj["0"] !== - 4;
//    var __expect6 = false;
//  }
//  obj.length = Number.NEGATIVE_INFINITY;
//  var push = obj.push(- 7);
//  {
//    var __result7 = push !== 1;
//    var __expect7 = false;
//  }
//  {
//    var __result8 = obj.length !== 1;
//    var __expect8 = false;
//  }
//  {
//    var __result9 = obj["0"] !== - 7;
//    var __expect9 = false;
//  }
//  obj.length = 0.5;
//  var push = obj.push(- 10);
//  {
//    var __result10 = push !== 1;
//    var __expect10 = false;
//  }
//  {
//    var __result11 = obj.length !== 1;
//    var __expect11 = false;
//  }
//  {
//    var __result12 = obj["0"] !== - 10;
//    var __expect12 = false;
//  }
//  obj.length = 1.5;
//  var push = obj.push(- 13);
//  {
//    var __result13 = push !== 2;
//    var __expect13 = false;
//  }
//  {
//    var __result14 = obj.length !== 2;
//    var __expect14 = false;
//  }
//  {
//    var __result15 = obj["1"] !== - 13;
//    var __expect15 = false;
//  }
//  obj.length = new Number(0);
//  var push = obj.push(- 16);
//  {
//    var __result16 = push !== 1;
//    var __expect16 = false;
//  }
//  {
//    var __result17 = obj.length !== 1;
//    var __expect17 = false;
//  }
//  {
//    var __result18 = obj["0"] !== - 16;
//    var __expect18 = false;
//  }
//  
