// XXX
//  function testcase() 
//  {
//    var toStringAccessed = false;
//    var valueOfAccessed = false;
//    var obj = {
//      1 : true,
//      length : {
//        toString : (function () 
//        {
//          toStringAccessed = true;
//          return '2';
//        }),
//        valueOf : (function () 
//        {
//          valueOfAccessed = true;
//          return {
//            
//          };
//        })
//      }
//    };
//    return Array.prototype.indexOf.call(obj, true) === 1 && toStringAccessed && valueOfAccessed;
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
