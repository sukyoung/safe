  function testcase() 
  {
    var strObj = new String(undefined);
    return strObj.trim() === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  