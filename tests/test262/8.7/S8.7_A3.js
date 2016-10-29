  var items = new Array("one", "two", "three");
  var itemsRef = items;
  items = new Array("new", "array");
  {
    var __result1 = items == itemsRef;
    var __expect1 = false;
  }
  ;
  