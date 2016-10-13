  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return val > 10;
    }
    var arrProtoLen;
    try
{      arrProtoLen = Array.prototype.length;
      Array.prototype.length = 0;
      var testResult = [12, 11, ].map(callbackfn);
      return testResult.length === 2;}
    finally
{      Array.prototype.length = arrProtoLen;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  