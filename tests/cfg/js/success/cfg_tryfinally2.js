var x = 5;
try { 
  x;
} finally {
  x = 7;
  try {
    x = 2;
  } finally {
      x = 10;
      try {print(x);}
      finally {x = 9;}
    }
}
x = 12;

