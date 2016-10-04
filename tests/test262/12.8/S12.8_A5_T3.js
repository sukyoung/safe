  (function () 
  {
    LABEL_OUT : var x = 0, y = 0;
    LABEL_DO_LOOP : do
    {
      LABEL_IN : x++;
      if (x === 10)
        return;
      break LABEL_IN;
      LABEL_IN_2 : y++;
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
  })();
  