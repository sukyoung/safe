import crypto as crypto;
import { encrypt, decrypt } from crypto;
import { encrypt: env } from crypto;
module M {
  export * from crypto;
  export { foo, bar } from crypto;
}
/* Not yet supported...
export = function() { }
*/

module foo {
    export var x = 42;
}

import foo as foo;
import { y } from foo;
