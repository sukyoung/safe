var __globalObject = @Global;
function fnGlobalObject() {
	return __globalObject;
}
  function testcase() 
  {
    var kValue = "abc";
    function callbackfn(val, idx, obj) 
    {
      if (idx === 0)
      {
        return val === kValue;
      }
      return false;
    }
    try
{      var oldLen = fnGlobalObject().length;
      fnGlobalObject()[0] = kValue;
      fnGlobalObject().length = 2;
      var testResult = Array.prototype.map.call(fnGlobalObject(), callbackfn);
      return testResult[0] === true;}
    finally
{      delete fnGlobalObject()[0];
      fnGlobalObject().length = oldLen;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
