  function testcase() 
  {
    var obj = {
      
    };
    var numObj = new Number();
    Object.defineProperty(obj, "prop", {
      value : numObj
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === numObj && desc.value === numObj;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  