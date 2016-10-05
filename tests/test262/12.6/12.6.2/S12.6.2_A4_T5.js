  var i = 0;
  woohoo : {
    while (true)
    {
      i++;
      if (! (i < 10))
      {
        break woohoo;
        $ERROR('#1.1: "break woohoo" must break loop');
      }
    }
    if (i !== 10)
      $ERROR('#1.2: i===10. Actual:  i===' + i);
  }
  