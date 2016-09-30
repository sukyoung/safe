  function testcase() 
  {
    var proto = [];
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    return ! Array.isArray(child);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  