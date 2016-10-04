  __str = "";
  outer : for (index = 0;index < 4;index += 1)
  {
    nested : for (index_n = 0;index_n <= index;index_n++)
    {
      if (index * index_n >= 4)
        break nested;
      __str += "" + index + index_n;
    }
  }
  {
    var __result1 = __str !== "00101120213031";
    var __expect1 = false;
  }
  __str = "";
  outer : for (index = 0;index < 4;index += 1)
  {
    nested : for (index_n = 0;index_n <= index;index_n++)
    {
      if (index * index_n >= 4)
        break outer;
      __str += "" + index + index_n;
    }
  }
  {
    var __result2 = __str !== "0010112021";
    var __expect2 = false;
  }
  __str = "";
  outer : for (index = 0;index < 4;index += 1)
  {
    nested : for (index_n = 0;index_n <= index;index_n++)
    {
      if (index * index_n >= 4)
        break;
      __str += "" + index + index_n;
    }
  }
  {
    var __result3 = __str !== "00101120213031";
    var __expect3 = false;
  }
  