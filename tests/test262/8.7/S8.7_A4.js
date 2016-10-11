  var item = new String("test");
  var itemRef = item;
  item += "ing";
  {
    var __result1 = item == itemRef;
    var __expect1 = false;
  }
  ;
  