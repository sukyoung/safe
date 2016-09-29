//  TODO [[DefaultValue]]
//  function testcase() 
//  {
//    var obj = {
//      "1" : 1
//    };
//    var toStringAccessed = false;
//    var valueOfAccessed = false;
//    var ownProp = {
//      toString : (function () 
//      {
//        toStringAccessed = true;
//        return [1, ];
//      }),
//      valueOf : (function () 
//      {
//        valueOfAccessed = true;
//        return [1, ];
//      })
//    };
//    try
//{      Object.getOwnPropertyDescriptor(obj, ownProp);
//      return false;}
//    catch (e)
//{      return toStringAccessed && valueOfAccessed && e instanceof TypeError;}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
