  do
  {
    __in__do__before__break = "reached";
    break;
    __in__do__after__break = "where am i";
  }while (2 === 1);
  {
    var __result1 = __in__do__before__break !== "reached";
    var __expect1 = false;
  }
  {
    var __result2 = typeof __in__do__after__break !== "undefined";
    var __expect2 = false;
  }
  