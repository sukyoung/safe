// XXX
//  function testcase() 
//  {
//    var proto = {
//      
//    };
//    Object.defineProperty(proto, "length", {
//      get : (function () 
//      {
//        return 0;
//      }),
//      configurable : true
//    });
//    var Con = (function () 
//    {
//      
//    });
//    Con.prototype = proto;
//    var child = new Con();
//    Object.defineProperty(child, "length", {
//      value : 2,
//      configurable : true
//    });
//    child[1] = true;
//    return Array.prototype.indexOf.call(child, true) === 1;
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
