  function testcase() 
  {
    var strObj = String("    abc");
    return "abc" === strObj.trim() && strObj.toString() === "    abc";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  