// TODO eval: statement (rewritten)
__str="";

//__evaluated = eval("for(var ind in (hash={2:'b',1:'a',4:'d',3:'c'}))__str+=hash[ind]");
for(var ind in (hash={2:'b',1:'a',4:'d',3:'c'}))
{
    __str+=hash[ind];
    __evaluated = __str;
}

var __result1 = true;
if ( !( (__evaluated.indexOf("a")!==-1)& (__evaluated.indexOf("b")!==-1)& (__evaluated.indexOf("c")!==-1)&(__evaluated.indexOf("d")!==-1) ) ) {
    var __result1 = false;
}
var __expect1 = true;

var __result2 = true;
if (__str !== __evaluated) {
    var __result2 = false;
}
var __expect2 = true;
