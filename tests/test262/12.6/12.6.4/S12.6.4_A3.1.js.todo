// TODO eval: statement (rewritten)
__str="";

//__evaluated = eval("for(var ind in (arr=[2,1,4,3]))__str+=arr[ind]");
for(var ind in (arr=[2,1,4,3]))
{
    __str+=arr[ind]
    __evaluated = __str;
}

var __result1 = true;
if (__evaluated !== __str) {
    var __result1 = false;
}
var __expect1 = true;

var __result2 = true;
if (!( (__str.indexOf("2")!==-1)&&(__str.indexOf("1")!==-1)&&(__str.indexOf("4")!==-1)&&(__str.indexOf("3")!==-1) )) {
    var __result2 = false;
}
var __expect2 = true;
