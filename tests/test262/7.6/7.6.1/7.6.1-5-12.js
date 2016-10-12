  function testcase() 
  {
    var tokenCodes = {
      const : 0,
      export : 1,
      import : 2
    };
    var arr = ['const', 'export', 'import', ];
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
  