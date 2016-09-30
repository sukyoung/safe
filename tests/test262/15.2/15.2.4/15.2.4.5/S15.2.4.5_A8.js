  {
    var __result1 = ! (Object.prototype.hasOwnProperty.hasOwnProperty('length'));
    var __expect1 = false;
  }
  {
    var __result2 = Object.prototype.hasOwnProperty.propertyIsEnumerable('length');
    var __expect2 = false;
  }
  for (p in Object.prototype.hasOwnProperty)
  {
    if (p === "length")
      $ERROR('#2: the Object.prototype.hasOwnProperty.length property has the attributes DontEnum');
  }
  