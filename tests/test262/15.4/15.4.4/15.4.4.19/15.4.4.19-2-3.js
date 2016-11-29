  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return val > 10;
    }
    var proto = {
      length : 3
    };
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    child.length = 2;
    child[0] = 12;
    child[1] = 11;
    child[2] = 9;
    var testResult = Array.prototype.map.call(child, callbackfn);
    return testResult.length === 2;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  