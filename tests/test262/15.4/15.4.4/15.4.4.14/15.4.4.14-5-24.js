// XXX
//  function testcase() 
//  {
//    var toStringAccessed = false;
//    var valueOfAccessed = false;
//    var fromIndex = {
//      toString : (function () 
//      {
//        toStringAccessed = true;
//        return {
//          
//        };
//      }),
//      valueOf : (function () 
//      {
//        valueOfAccessed = true;
//        return {
//          
//        };
//      })
//    };
//    try
//{      [0, true, ].indexOf(true, fromIndex);
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
