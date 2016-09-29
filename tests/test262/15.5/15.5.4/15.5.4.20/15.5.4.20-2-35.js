  function testcase() 
  {
    return (String.prototype.trim.call(new String("abc")) === "abc");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  