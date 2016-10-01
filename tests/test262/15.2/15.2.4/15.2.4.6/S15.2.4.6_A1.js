  function USER_FACTORY(name) 
  {
    this.name = name;
    this.getName = (function () 
    {
      return name;
    });
  }
  function FORCEDUSER_FACTORY(name, grade) 
  {
    this.name = name;
    this.grade = grade;
    this.getGrade = (function () 
    {
      return grade;
    });
  }
  var proto = new USER_FACTORY("noname");
  FORCEDUSER_FACTORY.prototype = proto;
  var luke = new FORCEDUSER_FACTORY("Luke Skywalker", 12);
  var __result1 = proto.isPrototypeOf(luke);
  var __expect1 = true;
  var __result2 = USER_FACTORY.prototype.isPrototypeOf(luke);
  var __expect2 = true;
  var __result3 = Number.isPrototypeOf(luke);
  var __expect3 = false;
  
