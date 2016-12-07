var __globalObject = @Global;
function fnGlobalObject() {
	return __globalObject;
}
  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return fnGlobalObject();
    }
    var newArr = [11, ].filter(callbackfn);
    return newArr.length === 1 && newArr[0] === 11;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
