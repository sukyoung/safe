  {
    var __result1 = ! (Object.prototype.valueOf.hasOwnProperty('length'));
    var __expect1 = false;
  }
  {
    var __result2 = Object.prototype.valueOf.propertyIsEnumerable('length');
    var __expect2 = false;
  }
  for (p in Object.prototype.valueOf)
  {
    if (p === "length")
      $ERROR('#2: the Object.prototype.valueOf.length property has the attributes DontEnum');
  }
  