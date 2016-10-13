  function testcase() 
  {
    return JSON.stringify(42, (function (k, v) 
    {
      return v == 42 ? {
        forty : 2
      } : v;
    })) === '{"forty":2}';
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  