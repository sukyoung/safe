  var __proto = {
    phylum : "avis"
  };
  {
    var __result1 = ! ("valueOf" in __proto);
    var __expect1 = false;
  }
  function Robin() 
  {
    this.name = "robin";
  }
  ;
  Robin.prototype = __proto;
  var __my__robin = new Robin;
  {
    var __result2 = ! ("phylum" in __my__robin);
    var __expect2 = false;
  }
  {
    var __result3 = __my__robin.hasOwnProperty("phylum");
    var __expect3 = false;
  }
  