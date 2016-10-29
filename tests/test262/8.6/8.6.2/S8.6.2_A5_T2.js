  this.position = 0;
  var seat = {
    
  };
  seat['move'] = (function () 
  {
    position++;
  });
  seat.move();
  {
    var __result1 = position !== 1;
    var __expect1 = false;
  }
  seat['move']();
  {
    var __result2 = position !== 2;
    var __expect2 = false;
  }
  