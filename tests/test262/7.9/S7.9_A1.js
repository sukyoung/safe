  label1 : for(var i = 0;i <= 0;i++)
  {
    for(var j = 0;j <= 1;j++)
    {
      if (j === 0)
      {
        continue label1;
      }
      else
      {
        $ERROR('#1: Check continue statement for automatic semicolon insertion');
      }
    }
  }
  var result = false;
  label2 : for(var i = 0;i <= 1;i++)
  {
    for(var j = 0;j <= 1;j++)
    {
      if (j === 0)
      {
        continue;
        label2;
      }
      else
      {
        result = true;
      }
    }
  }
  {
    var __result1 = result !== true;
    var __expect1 = false;
  }
  