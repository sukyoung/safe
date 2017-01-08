var __globalObject = @Global;
function fnGlobalObject() {
	return __globalObject;
}
  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return idx === 0 && val === 11;
    }
    try
{      var oldLen = fnGlobalObject().length;
      fnGlobalObject()[0] = 11;
      fnGlobalObject().length = 1;
      var newArr = Array.prototype.filter.call(fnGlobalObject(), callbackfn);
      return newArr.length === 1 && newArr[0] === 11;}
    finally
{      delete fnGlobalObject()[0];
      fnGlobalObject().length = oldLen;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
