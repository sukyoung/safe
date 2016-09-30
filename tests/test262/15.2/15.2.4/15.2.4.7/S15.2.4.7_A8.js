  {
    var __result1 = ! (Object.prototype.propertyIsEnumerable.hasOwnProperty('length'));
    var __expect1 = false;
  }
  {
    var __result2 = Object.prototype.propertyIsEnumerable.propertyIsEnumerable('length');
    var __expect2 = false;
  }
  for (p in Object.prototype.propertyIsEnumerable)
  {
    if (p === "length")
      $ERROR('#2: the Object.prototype.propertyIsEnumerable.length property has the attributes DontEnum');
  }
  