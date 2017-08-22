trap "exit" INT
for f in ./tests/test262/*/*.js; do echo $f; ./bin/safe interpret -interpreter:ecma $f; done
