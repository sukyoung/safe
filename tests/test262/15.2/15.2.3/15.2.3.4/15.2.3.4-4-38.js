  function testcase() 
  {
    var obj = {
      "a" : "a"
    };
    var result = Object.getOwnPropertyNames(obj);
    return result[0] === "a";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  