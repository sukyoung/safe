  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return ('[object JSON]' === Object.prototype.toString.call(obj));
    }
    try
{      JSON.length = 1;
      JSON[0] = 1;
      var testResult = Array.prototype.map.call(JSON, callbackfn);
      return testResult[0] === true;}
    finally
{      delete JSON.length;
      delete JSON[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  