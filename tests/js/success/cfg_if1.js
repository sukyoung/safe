var human = {girl:"Hoo", boy:"Ha"};
function say (gender) {
  if (gender == "girl") {
    return human.girl;
  } else {
    return human.boy;
  }
}
print ("Girls say " + say("girl") + "! Boys say " + say("boy") + "! and I say " + say("me") + "!");
