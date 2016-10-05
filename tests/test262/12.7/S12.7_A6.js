  var x = 0, y = 0;
  LABEL1 : do
  {
    x++;
    (function () 
    {
      continue LABEL1;
    })();
    y++;
  }while (0);
  