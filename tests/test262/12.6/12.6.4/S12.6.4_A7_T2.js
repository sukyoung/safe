  __obj = {
    aa : 1,
    ba : 2,
    ca : 3
  };
  __accum = "";
  for(var __key in __obj)
  {
    erasator_T_1000(__obj, "b");
    __accum += (__key + __obj[__key]);
  }
  {
    var __result1 = ! ((__accum.indexOf("aa1") !== - 1) && (__accum.indexOf("ca3") !== - 1));
    var __expect1 = false;
  }
  {
    var __result2 = __accum.indexOf("ba2") !== - 1;
    var __expect2 = false;
  }
  function erasator_T_1000(hash_map, charactr) 
  {
    for (key in hash_map)
    {
      if (key.indexOf(charactr) === 0)
      {
        delete hash_map[key];
      }
      ;
    }
  }
  