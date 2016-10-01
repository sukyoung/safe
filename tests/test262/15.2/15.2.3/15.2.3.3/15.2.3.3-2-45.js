  function testcase() 
  {
    var obj = {
      "bbq" : 1,
      "abc" : 2
    };
    var valueOfAccessed = false;
    var ownProp = {
      toString : (function () 
      {
        return "bbq";
      }),
      valueOf : (function () 
      {
        valueOfAccessed = true;
        return "abc";
      })
    };
    var desc = Object.getOwnPropertyDescriptor(obj, ownProp);
    return desc.value === 1 && ! valueOfAccessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  