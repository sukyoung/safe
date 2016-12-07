var __globalObject = @Global;
function fnGlobalObject() {
	return __globalObject;
}
  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = (obj.length === 2);
    }
    try
{      var oldLen = fnGlobalObject().length;
      fnGlobalObject()[0] = 12;
      fnGlobalObject()[1] = 11;
      fnGlobalObject()[2] = 9;
      fnGlobalObject().length = 2;
      Array.prototype.forEach.call(fnGlobalObject(), callbackfn);
      return result;}
    finally
{      delete fnGlobalObject()[0];
      delete fnGlobalObject()[1];
      delete fnGlobalObject()[2];
      fnGlobalObject().length = oldLen;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
