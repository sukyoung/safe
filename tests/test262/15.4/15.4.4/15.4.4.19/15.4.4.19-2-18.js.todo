  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return parseInt(val, 10) > 1;
    }
    var str = new String("432");
    try
{      String.prototype[3] = "1";
      var testResult = Array.prototype.map.call(str, callbackfn);
      return 3 === testResult.length;}
    finally
{      delete String.prototype[3];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  