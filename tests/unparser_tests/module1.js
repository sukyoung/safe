module Browser {
  module DOM {
    var document;
    export document;
  }
  export DOM;
}
module DesignMode {
  import { document: DOMdocument } from Browser.DOM;
  export function initialize() {
    document = DOMdocument.createElement('iframe');
  }
  export var document;
}
import Browser.DOM as DOM;
//import DOM.*;
import { document } from DOM;
DesignMode.initialize();
document.body.appendChild(DesignMode.document);
