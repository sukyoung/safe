//// fancy object stuff simulating inheritance
// a class
function Person(name) {
  this.name = name;
  this.count = 0;
}
Person.prototype.getName = function() { return this.name; };
Person.prototype.say = function(stuff) { return(stuff); };
Person.prototype.slap =
  function() {
  if(this.count<2)
    {this.count++;return("argh")}
  else
    {this.count=0;return("ouch");}
};

var jimmy = new Person("jimmy");
//dumpValue(jimmy.getName());
var __result1 = jimmy.getName();  // for SAFE
var __expect1 = "jimmy";  // for SAFE

//dumpValue(jimmy.say("hello"));
var __result2 = jimmy.say("hello");  // for SAFE
var __expect2 = "hello";  // for SAFE

//dumpValue(jimmy.slap());
var __result3 = jimmy.slap();  // for SAFE
var __expect3 = "argh";  // for SAFE

//dumpValue(jimmy.slap());
var __result4 = jimmy.slap();  // for SAFE
var __expect4 = "argh";  // for SAFE

//dumpValue(jimmy.slap());
var __result5 = jimmy.slap();  // for SAFE
var __expect5 = "ouch";  // for SAFE

//dumpValue(jimmy.slap());

// a subclass
function Singer(name) {
  Person.call(this,name);
}
//dumpObject(Singer.prototype);
Singer.prototype = new Person();
//dumpObject(Singer.prototype);
Singer.prototype.Super = Person.prototype;
//dumpObject(Singer.prototype);
Singer.prototype.sing = function(song) { return this.say(song+" tra-la-la"); };
//dumpObject(Singer.prototype);

var jerry = new Singer("jerry");
//dumpObject(jerry); // should have 'name' and 'count'
var __result6 = jerry.name;  // for SAFE
var __expect6 = "jerry";  // for SAFE

var __result7 = jerry.count;  // for SAFE
var __expect7 = 0;  // for SAFE

//dumpObject(Singer);
//dumpObject(Singer.prototype);
//dumpObject(Person);
//dumpObject(Person.prototype);
//dumpValue(jerry.getName()); // should be (approximation of) "jerry"
var __result8 = jerry.getName();  // for SAFE
var __expect8 = "jerry";  // for SAFE

//dumpValue(jerry.say("hello"));
var __result9 = jerry.say("hello");  // for SAFE
var __expect9 = "hello";  // for SAFE

//dumpValue(jerry.sing("a song"));
var __result10 = jerry.sing("a song");  // for SAFE
var __expect10 = "a song tra-la-la";  // for SAFE

//dumpValue(jerry.slap());
var __result11 = jerry.slap();  // for SAFE
var __expect11 = "argh";  // for SAFE

//dumpValue(jerry.slap());
//dumpValue(jerry.slap());
//dumpValue(jerry.slap());


// another subclass
function Rockstar(name) {
  Singer.call(this,name);
}
Rockstar.prototype = new Singer();
Rockstar.prototype.Super = Singer.prototype;
Rockstar.prototype.say = function(words) { return this.Super.say("Gee, "+words); };

var marilyn = new Rockstar("marilyn");
//dumpValue(marilyn.getName());
var __result12 = marilyn.getName();  // for SAFE
var __expect12 = "marilyn";  // for SAFE

//dumpValue(marilyn.say("I'm bad"));
var __result13 = marilyn.say("I'm bad");  // for SAFE
var __expect13 = "Gee, I'm bad";  // for SAFE

//dumpValue(marilyn.sing("a hard day's night"));
var __result14 = marilyn.sing("a hard day's night");  // for SAFE
var __expect14 = "Gee, a hard day's night tra-la-la";  // for SAFE

//dumpValue(marilyn.slap());
var __result15 = marilyn.slap();  // for SAFE
var __expect15 = "argh";  // for SAFE

//dumpValue(marilyn.slap());
//dumpValue(marilyn.slap());
//dumpValue(marilyn.slap());
