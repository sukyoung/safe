var __globalObject = @Global;
function fnGlobalObject() {
	return __globalObject;
}

  function testcase() 
  {
    var obj = {
      
    };
    obj.test = (function () 
    {
      this._12_14_15_foo = "test";
    });
    try
{      throw obj.test;
      return false;}
    catch (e)
{      e();
      return fnGlobalObject()._12_14_15_foo === "test";}

    finally
{      delete fnGlobalObject()._12_14_15_foo;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
