  function test() 
  {
    for(var x in this)
    {
      if (x === 'eval')
      {
        $ERROR("#1: 'eval' have attribute DontEnum");
      }
      else
        if (x === 'parseInt')
        {
          $ERROR("#1: 'parseInt' have attribute DontEnum");
        }
        else
          if (x === 'parseFloat')
          {
            $ERROR("#1: 'parseFloat' have attribute DontEnum");
          }
          else
            if (x === 'isNaN')
            {
              $ERROR("#1: 'isNaN' have attribute DontEnum");
            }
            else
              if (x === 'isFinite')
              {
                $ERROR("#1: 'isFinite' have attribute DontEnum");
              }
              else
                if (x === 'decodeURI')
                {
                  $ERROR("#1: 'decodeURI' have attribute DontEnum");
                }
                else
                  if (x === 'decodeURIComponent')
                  {
                    $ERROR("#1: 'decodeURIComponent' have attribute DontEnum");
                  }
                  else
                    if (x === 'encodeURI')
                    {
                      $ERROR("#1: 'encodeURI' have attribute DontEnum");
                    }
                    else
                    {
                      var __result1 = x === 'encodeURIComponent';
                      var __expect1 = false;
                    }
    }
  }
  test();
  