module E {
  import odd from O;
  export function even(n) {
    return n == 0 || odd(n - 1);
  }
}

module O {
  import even from E;
  export function odd(n) {
    return n != 0 && even(n - 1);
  }
}
