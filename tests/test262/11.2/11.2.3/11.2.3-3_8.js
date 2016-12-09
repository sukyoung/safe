var __globalObject = @Global;
function fnGlobalObject() {
	return __globalObject;
}

  function testcase() 
  {
    if (this !== fnGlobalObject())
    {
      return;
    }
    var fooCalled = false;
    function foo() 
    {
      fooCalled = true;
    }
    try
{      this.bar(foo());
      throw new Exception("this.bar does not exist!");}
    catch (e)
{      return (e instanceof TypeError) && (fooCalled === true);}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
