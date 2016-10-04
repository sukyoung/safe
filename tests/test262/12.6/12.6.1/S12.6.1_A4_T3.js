  do_out : do
  {
    var __in__do__before__break = "once";
    do_in : do
    {
      var __in__do__IN__before__break = "in";
      break do_out;
      var __in__do__IN__after__break = "the";
    }while (0);
    var __in__do__after__break = "lifetime";
  }while (2 === 1);
  {
    var __result1 = ! (__in__do__before__break && __in__do__IN__before__break && ! __in__do__IN__after__break && ! __in__do__after__break);
    var __expect1 = false;
  }
  