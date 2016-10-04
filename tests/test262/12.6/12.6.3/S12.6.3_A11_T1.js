  __str = "";
  for (index = 0;index < 10;index += 1)
  {
    if (index < 5)
      continue;
    __str += index;
  }
  {
    var __result1 = __str !== "56789";
    var __expect1 = false;
  }
  