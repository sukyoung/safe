// TODO getter/setter
//   function testcase() 
//   {
//     var o = {
      
//     };
//     Object.defineProperty(o, "bar", {
//       get : (function () 
//       {
//         this.barGetter = true;
//         return 42;
//       }),
//       set : (function (x) 
//       {
//         this.barSetter = true;
//       })
//     });
//     try
// {      o.foo(o["bar"]);
//       throw new Exception("o.foo does not exist!");}
//     catch (e)
// {      return (e instanceof TypeError) && (o.barGetter === true) && (o.barSetter === undefined);}

//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
  
