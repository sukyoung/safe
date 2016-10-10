// XXX
//  function testcase() 
//  {
//    var toStringAccessed = false;
//    var valueOfAccessed = false;
//    var obj = {
//      length : {
//        toString : (function () 
//        {
//          toStringAccessed = true;
//          return {
//            
//          };
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
//    try
//{      Array.prototype.indexOf.call(obj);
//      return false;}
//    catch (e)
//{      return toStringAccessed && valueOfAccessed;}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
