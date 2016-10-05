// XXX
//  function testcase() 
//  {
//    var func = (function (a, b) 
//    {
//      arguments[2] = false;
//      return Array.prototype.indexOf.call(arguments, true) === 1 && Array.prototype.indexOf.call(arguments, false) === - 1;
//    });
//    return func(0, true);
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
