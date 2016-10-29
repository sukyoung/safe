  var obj = {
    bar : true,
    some : 1,
    foo : "a"
  };
  var count = 0;
  for (property in obj)
    count++;
  {
    var __result1 = count !== 3;
    var __expect1 = false;
  }
  var obj_ = {
    bar : true
  };
  obj_.some = 1;
  obj_.foo = "a";
  count = 0;
  for (property in obj_)
    count++;
  {
    var __result2 = count !== 3;
    var __expect2 = false;
  }
  var obj__ = new Object();
  obj__.bar = true;
  obj__.some = 1;
  obj__.foo = "a";
  count = 0;
  for (property in obj__)
    count++;
  {
    var __result3 = count !== 3;
    var __expect3 = false;
  }
  