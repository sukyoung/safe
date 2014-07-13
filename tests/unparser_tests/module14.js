module Lexer {
    import { open, close } from io.File;
    export function scan(inf) {
        try {
            var h = open(inf)
        }
        finally { close(h) }
    }
}
