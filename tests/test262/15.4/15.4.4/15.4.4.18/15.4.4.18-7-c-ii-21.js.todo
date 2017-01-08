  function testcase() 
  {
    var resultOne = false;
    var resultTwo = false;
    function callbackfn(val, idx, obj) 
    {
      if (idx === 0)
      {
        resultOne = (val === 11);
      }
      if (idx === 1)
      {
        resultTwo = (val === 12);
      }
    }
    var obj = {
      0 : 11,
      1 : 12,
      length : 2
    };
    Array.prototype.forEach.call(obj, callbackfn);
    return resultOne && resultTwo;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  