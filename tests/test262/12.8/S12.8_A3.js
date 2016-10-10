  LABEL_OUT : var x = 0, y = 0;
  LABEL_DO_LOOP : do
  {
    LABEL_IN : x = 2;
    break;
    LABEL_IN_2 : var y = 2;
    function IN_DO_FUNC() 
    {
      
    }
  }while (0);
  LABEL_ANOTHER_LOOP : do
  {
    ;
  }while (0);
  function OUT_FUNC() 
  {
    
  }
  {
    var __result1 = (x !== 2) && (y !== 0);
    var __expect1 = false;
  }
  