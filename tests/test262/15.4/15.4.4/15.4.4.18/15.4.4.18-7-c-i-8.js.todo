  function testcase() 
  {
    var testResult = false;
    function callbackfn(val, idx, obj) 
    {
      if (idx === 1)
      {
        testResult = (val === 13);
      }
    }
    try
{      Array.prototype[1] = 13;
      [, , , ].forEach(callbackfn);
      return testResult;}
    finally
{      delete Array.prototype[1];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  