// TODO: user-defined toLocaleString function shadowed by the model code
//  var n = 0;
//  var obj = {
//    toLocaleString : (function () 
//    {
//      n++;
//    })
//  };
//  var arr = [undefined, obj, null, obj, obj, ];
//  arr.toLocaleString();
//  {
//    var __result1 = n !== 3;
//    var __expect1 = false;
//  }
