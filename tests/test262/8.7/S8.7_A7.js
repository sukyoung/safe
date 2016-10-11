  var n = {
    
  };
  var m = n;
  {
    var __result1 = typeof m !== "object";
    var __expect1 = false;
  }
  function populateAge(person) 
  {
    person.age = 50;
  }
  populateAge(m);
  {
    var __result2 = n.age !== 50;
    var __expect2 = false;
  }
  