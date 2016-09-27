try {
throw "1";
} catch(e) {"1"} finally {"2"}

try{ } catch(e) {}
try{} finally {}
try{} catch(e){} finally {}
try{try{} catch(e){}} catch(e){} finally {}
