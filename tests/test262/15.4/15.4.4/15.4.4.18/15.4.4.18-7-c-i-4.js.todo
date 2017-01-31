  function testcase() 
  {
    var testResult = false;
    function callbackfn(val, idx, obj) 
    {
      if (idx === 0)
      {
        testResult = (val === 12);
      }
    }
    try
{      Array.prototype[0] = 11;
      [12, ].forEach(callbackfn);
      return testResult;}
    finally
{      delete Array.prototype[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  