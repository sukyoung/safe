done :
  done : {
    break done;
  }

done : {
  function f() {
    done : {
      function g() { done : { break done; } }
    }
  }
}
