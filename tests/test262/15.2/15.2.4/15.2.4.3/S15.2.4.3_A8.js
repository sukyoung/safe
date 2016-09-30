  {
    var __result1 = ! (Object.prototype.toLocaleString.hasOwnProperty('length'));
    var __expect1 = false;
  }
  {
    var __result2 = Object.prototype.toLocaleString.propertyIsEnumerable('length');
    var __expect2 = false;
  }
  for (p in Object.prototype.toLocaleString)
  {
    if (p === "length")
      $ERROR('#2: the Object.prototype.toLocaleString.length property has the attributes DontEnum');
  }
  