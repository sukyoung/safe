  function testcase() 
  {
    var tokenCodes = {
      
    };
    tokenCodes.if = 0;
    tokenCodes.throw = 1;
    tokenCodes.delete = 2;
    var arr = ['if', 'throw', 'delete', ];
    for(var i = 0;i < arr.length;i++)
    {
      if (tokenCodes[arr[i]] !== i)
      {
        return false;
      }
      ;
    }
    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  