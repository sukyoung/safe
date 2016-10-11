  var items = new Array("one", "two", "three");
  var itemsRef = items;
  items.push("four");
  var itemsRef = items;
  {
    var __result1 = itemsRef.length !== 4;
    var __expect1 = false;
  }
  ;
  var items = new Array("one", "two", "three");
  var itemsRef = items;
  items[1] = "duo";
  {
    var __result2 = itemsRef[1] !== "duo";
    var __expect2 = false;
  }
  ;
  