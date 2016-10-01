  function testcase() 
  {
    var obj = {
      "[object Object]" : 1,
      "abc" : 2
    };
    var ownProp = {
      valueOf : (function () 
      {
        return "abc";
      })
    };
    var desc = Object.getOwnPropertyDescriptor(obj, ownProp);
    return desc.value === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  