module SafeDOM {
  // Error in the proposal?
  import { alert } from DOM;
  export var document = {
    write: function(txt) {
      alert('I\'m sorry, Dave, I\'m afraid I can\'t do that...')
    }
  }
}
