/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var human = {girl:"Hoo", boy:"Ha"};
function say (gender) {
  if (gender == "girl") {
    return human.girl;
  } else {
    return human.boy;
  }
}
print ("Girls say " + say("girl") + "! Boys say " + say("boy") + "! and I say " + say("me") + "!");
