Visualization of SAFE Version 2.0
===========
.. image:: vis/flow.png
   :width: 150 px

.. image:: vis/full.png

Thanks to the `CodeFlower`_ source code visualization tool.
.. _CodeFlower: http://www.redotheweb.com/CodeFlower

.. raw:: html

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
<script type="text/javascript" src="vis/javascripts/d3/d3.js"></script>
<script type="text/javascript" src="vis/javascripts/d3/d3.geom.js"></script>
<script type="text/javascript" src="vis/javascripts/d3/d3.layout.js"></script>
<script type="text/javascript" src="vis/javascripts/CodeFlower.js"></script>
<script type="text/javascript" src="vis/javascripts/dataConverter.js"></script>
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
d3.json('vis/data.json', createCodeFlower);
</script>
    </embed>
