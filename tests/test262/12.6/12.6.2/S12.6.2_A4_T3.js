  do_out : while (1 === 1)
  {
    if (__in__do__before__break)
      break;
    var __in__do__before__break = "once";
    do_in : while (1)
    {
      var __in__do__IN__before__break = "in";
      break do_out;
      var __in__do__IN__after__break = "the";
    }
    ;
    var __in__do__after__break = "lifetime";
  }
  ;
  {
    var __result1 = ! (__in__do__before__break && __in__do__IN__before__break && ! __in__do__IN__after__break && ! __in__do__after__break);
    var __expect1 = false;
  }
  