// XXX
//  function testcase() 
//  {
//    var toStringAccessed = false;
//    var valueOfAccessed = false;
//    var proto = {
//      valueOf : (function () 
//      {
//        valueOfAccessed = true;
//        return 2;
//      })
//    };
//    var Con = (function () 
//    {
//      
//    });
//    Con.prototype = proto;
//    var child = new Con();
//    child.toString = (function () 
//    {
//      toStringAccessed = true;
//      return 2;
//    });
//    var obj = {
//      1 : true,
//      length : child
//    };
//    return Array.prototype.indexOf.call(obj, true) === 1 && valueOfAccessed && ! toStringAccessed;
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
