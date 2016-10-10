  __str = "";
  outer : for(var index = 0;index < 4;index += 1)
  {
    nested : for(var index_n = 0;index_n <= index;index_n++)
    {
      if (index * index_n == 6)
        continue nested;
      __str += "" + index + index_n;
    }
  }
  {
    var __result1 = __str !== "001011202122303133";
    var __expect1 = false;
  }
  __str = "";
  outer : for(var index = 0;index < 4;index += 1)
  {
    nested : for(var index_n = 0;index_n <= index;index_n++)
    {
      if (index * index_n == 6)
        continue outer;
      __str += "" + index + index_n;
    }
  }
  {
    var __result2 = __str !== "0010112021223031";
    var __expect2 = false;
  }
  __str = "";
  outer : for(var index = 0;index < 4;index += 1)
  {
    nested : for(var index_n = 0;index_n <= index;index_n++)
    {
      if (index * index_n == 6)
        continue;
      __str += "" + index + index_n;
    }
  }
  {
    var __result3 = __str !== "001011202122303133";
    var __expect3 = false;
  }
  