Visualization of SAFE Version 2.0
===========
SAFE 2.0 flow:

![](flow.png =400x)

SAFE 2.0 directory structure:

![](full.png =500x)

Using the [CodeFlower](http://www.redotheweb.com/CodeFlower) source code visualization tool:

<embed>
    <style type="text/css">
circle.node {
  cursor: pointer;
  stroke: #000;
  stroke-width: .5px;
}
circle.node.directory {
  stroke: #9ecae1;
  stroke-width: 2px;
}
circle.node.collapsed {
  stroke: #555;
}
.nodetext {
  fill: #252929;
  font-weight: bold;
  text-shadow: 0 0 0.2em white;
}
line.link {
  fill: none;
  stroke: #9ecae1;
  stroke-width: 1.5px;
}
    </style>
<div id="visualization"></div>
<script type="text/javascript" src="javascripts/d3/d3.js"></script>
<script type="text/javascript" src="javascripts/d3/d3.geom.js"></script>
<script type="text/javascript" src="javascripts/d3/d3.layout.js"></script>
<script type="text/javascript" src="javascripts/CodeFlower.js"></script>
<script type="text/javascript" src="javascripts/dataConverter.js"></script>
<script type="text/javascript">
var currentCodeFlower;
var createCodeFlower = function(json) {
  // remove previous flower to save memory
  if (currentCodeFlower) currentCodeFlower.cleanup();
  // adapt layout size to the total number of elements
  var total = countElements(json);
  w = parseInt(Math.sqrt(total) * 40, 10);
  h = parseInt(Math.sqrt(total) * 40, 10);
  // create a new CodeFlower
  currentCodeFlower = new CodeFlower("#visualization", w, h).update(json);
};
d3.json('data.json', createCodeFlower);
</script>
</embed>
