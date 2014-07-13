function f() {
  var x;
  if(Math.random()) x = null;
  if(Math.random()) x = false;
  if(Math.random()) x = 0;
  if(Math.random()) x = "";

  if(x);

  var y = new Object();
  if(Math.random()) y = "asdf";
  if(y);
}

f();
