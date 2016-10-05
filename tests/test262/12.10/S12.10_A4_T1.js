// TODO eval: statement
this.p1 = 1;
var myObj = {
  p1: 'a', 
}
// eval("with(myObj){p1='b'}");
with(myObj){p1='b'}

var __result1 = true
if(myObj.p1 !== 'b'){
    var __result1 = false
}
var __expect1 = true

var __result2 = true
if(myObj.p1 === 1){
    var __result2 = false
}
var __expect2 = true
