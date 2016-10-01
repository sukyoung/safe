  function testcase() 
  {
    var obj = {
      "abc" : 1
    };
    var ownProp = {
      toString : (function () 
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
  