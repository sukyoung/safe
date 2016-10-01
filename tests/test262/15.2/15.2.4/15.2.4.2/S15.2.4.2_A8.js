  {
    var __result1 = ! (Object.prototype.toString.hasOwnProperty('length'));
    var __expect1 = false;
  }
  {
    var __result2 = Object.prototype.toString.propertyIsEnumerable('length');
    var __expect2 = false;
  }
  for(var p in Object.prototype.toString)
  {
    if (p === "length")
      $ERROR('#2: the Object.prototype.toString.length property has the attributes DontEnum');
  }
  