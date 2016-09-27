var productlist = {apple:100, banana:200, melon:300};
var fruit, money;
function pay (budget, product) {
  if (budget <= 0) {
      return budget;
  } else {
      if (product == "apple") return budget - productlist.apple;
      else if (product == "banana") return budget - productlist.banana;
      else if (product == "melon") return budget - productlist.melon;
      else return budget;
  }
}
money = 1000;
fruit = "melon";
print ("I had " + money + " won. I bought " + fruit + ", and now I have " + pay (money, fruit) + " won.");
