  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "prop", {
      value : "ThisIsAString"
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return obj.prop === "ThisIsAString" && desc.value === "ThisIsAString";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  