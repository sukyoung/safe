var __globalObject = @Global;
function fnGlobalObject() {
	return __globalObject;
}
  function testcase() 
  {
    var testResult = false;
    function callbackfn(val, idx, obj) 
    {
      if (idx === 0)
      {
        testResult = (val === 11);
      }
    }
    try
{      var oldLen = fnGlobalObject().length;
      fnGlobalObject()[0] = 11;
      fnGlobalObject().length = 1;
      Array.prototype.forEach.call(fnGlobalObject(), callbackfn);
      return testResult;}
    finally
{      delete fnGlobalObject()[0];
      fnGlobalObject().length = oldLen;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
