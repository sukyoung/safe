  __str = "";
  outer : for (index = 0;index < 4;index += 1)
  {
    nested : for (index_n = 0;index_n <= index;index_n++)
    {
      if (index * index_n == 6)
        continue nonexist;
      __str += "" + index + index_n;
    }
  }
  