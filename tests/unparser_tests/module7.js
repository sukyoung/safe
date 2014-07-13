module widgets {
  module button {}
  module alert {
    var messageBox;
    var confirmDialog;
  }
  module textarea {}
}
import { messageBox, confirmDialog } from widgets.alert;
