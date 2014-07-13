 function Person(n) {
    this.setName(n);
    Person.prototype.count++;
  }
  Person.prototype.count = 0;
  Person.prototype.setName = function(n) { this.name = n; }
  function Student(n,s) {
    this.b = Person;
    this.b(n);
    delete this.b;
    this.studentid = s.toString();
  }
  Student.prototype = new Person;
  
  var t = 100026.0;
  var x = new Student("Joe Average", t++);
  var y = new Student("John Doe", t)
  y.setName("John Q. Doe");
//  dumpObject(x);
//  dumpObject(y);
//  assert(x.name === "Joe Average")
  var __result1 = x.name;  // for SAFE
  var __expect1 = "Joe Average";  // for SAFE

//  assert(y.name === "John Q. Doe")
  var __result2 = y.name;  // for SAFE
  var __expect2 = "John Q. Doe";  // for SAFE
  
//  assert(y.studentid === "100027");
  var __result3 = y.studentid;  // for SAFE
  var __expect3 = "100027";  // for SAFE

//  assert(x.count === 3)
  var __result4 = x.count;  // for SAFE
  var __expect4 = 3;  // for SAFE

  
//dumpModifiedState();
