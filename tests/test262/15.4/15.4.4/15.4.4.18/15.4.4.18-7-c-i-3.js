  function testcase() 
  {
    var kValue = "abc";
    var testResult = false;
    function callbackfn(val, idx, obj) 
    {
      if (idx === 5)
      {
        testResult = (val === kValue);
      }
    }
    var proto = {
      5 : 100
    };
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    child[5] = kValue;
    child.length = 10;
    Array.prototype.forEach.call(child, callbackfn);
    return testResult;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  