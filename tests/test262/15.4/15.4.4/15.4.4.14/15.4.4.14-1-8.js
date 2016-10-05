  function testcase() 
  {
    var obj = new String("null");
    return Array.prototype.indexOf.call(obj, 'l') === 2;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  