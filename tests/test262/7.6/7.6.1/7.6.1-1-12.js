  function testcase() 
  {
    var tokenCodes = {
      const : 0,
      export : 1,
      import : 2
    };
    var arr = ['const', 'export', 'import', ];
    for(var p in tokenCodes)
    {
      for(var p1 in arr)
      {
        if (arr[p1] === p)
        {
          if (! tokenCodes.hasOwnProperty(arr[p1]))
          {
            return false;
          }
          ;
        }
      }
    }
    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  