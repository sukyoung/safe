  function testcase() 
  {
    var kValue = "abc";
    function callbackfn(val, idx, obj) 
    {
      if (idx === 5)
      {
        return val === kValue;
      }
      return false;
    }
    var proto = {
      5 : 12,
      length : 10
    };
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    child[5] = kValue;
    var testResult = Array.prototype.map.call(child, callbackfn);
    return testResult[5] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  