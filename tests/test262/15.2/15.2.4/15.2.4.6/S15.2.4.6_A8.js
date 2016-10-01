  {
    var __result1 = ! (Object.prototype.isPrototypeOf.hasOwnProperty('length'));
    var __expect1 = false;
  }
  {
    var __result2 = Object.prototype.isPrototypeOf.propertyIsEnumerable('length');
    var __expect2 = false;
  }
  for (p in Object.prototype.isPrototypeOf)
  {
    if (p === "length")
      $ERROR('#2: the Object.prototype.isPrototypeOf.length property has the attributes DontEnum');
  }
  