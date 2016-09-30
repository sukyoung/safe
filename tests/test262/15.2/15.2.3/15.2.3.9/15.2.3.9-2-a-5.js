//  TODO getter/setter
// function testcase() 
// {
//   var proto = {

//   };
//   proto.foo = 0;
//   var Con = (function () 
//   {

//   });
//   Con.prototype = proto;
//   var child = new Con();
//   Object.defineProperty(child, "foo", {
//     get : (function () 
//     {
//       return 10;
//     }),
//     configurable : true
//   });
//   Object.freeze(child);
//   var desc = Object.getOwnPropertyDescriptor(child, "foo");
//   delete child.foo;
//   return child.foo === 10 && desc.configurable === false;
// }
// {
//   var __result1 = testcase();
//   var __expect1 = true;
// }

