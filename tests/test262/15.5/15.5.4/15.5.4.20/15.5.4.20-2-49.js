  function testcase() 
  {
    var regObj = new RegExp(/test/);
    return String.prototype.trim.call(regObj) === "/test/";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  