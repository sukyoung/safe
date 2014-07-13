/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var x;
x = "A";
while(true) {
    x = "B";
    throw "C";
    if (x == "B") {throw 3;;}
}
x = "E"
