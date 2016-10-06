//  XXX: test is weired it should check "this === undefine"
//       not "this !== undefined" (by jihyeok)
//  function testcase() 
//  {
//    function foo() 
//    {
//      'Use Strict';
//      return (this !== undefined);
//    }
//    return foo.call(undefined);
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
