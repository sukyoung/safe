/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

webix.ready(function(){
  if (webix.CustomScroll && !webix.env.touch) webix.CustomScroll.init();
  var side = {
    margin: 10, padding: 0, type: 'wide',
    rows:[
    { view: 'unitlist', id: 'insts', uniteBy: function(obj) { return obj.kind; },
      select: false, height: 300, select: true },
    { view: 'tree', id: 'state' },
    ]
};

webix.ui({
  rows:[
  { view: 'toolbar', id: 'toolbar', elements: [
    { view: 'label', label: 'SAFE 2.0' },
    { view: 'icon', icon: 'bars',
      click: function() {
        if( $$('options').config.hidden) {
          $$('options').show();
        } else $$('options').hide();
      }
    },
  ]},
  { view: 'accordion', id: 'accord', type: 'wide', collapsed: true, cols:[
    { id: 'side-bar', header: 'Instructions & State', width: 500, minWidth: 320, body: side },
    { id: 'resizer', view: 'resizer' },
    { id: 'cy', body: { content: 'cy' } },
  ]},
  // TODO { template: 'search engine', height: 30},
  ]
});

webix.ui({
  view: "sidemenu",
  id: "options",
  width: 200,
  position: "right",
  state:function(state){
    var toolbarHeight = $$('toolbar').$height;
    state.top = toolbarHeight;
    state.height -= toolbarHeight;
  },
  body:{
    view: 'form',
    scroll: false,
    elements:[
    {view: 'toggle', name: 'worklist', offLabel: 'Worklist: off', onLabel: 'Worklist: on',
      click: function() {
        var blocks = cy.nodes()
        if (this.config.value) {
          blocks.data('bc', 'white')
        } else {
          blocks.filter(function() { return this.data('inWL'); }).data('bc', '#3498DB');
        }
      }
    },
    ],
    select:true,
    type:{
      height: 40
    }
  }
});

webix.ui.fullScreen();

// redraw CFG after collapsing
$$('accord').attachEvent("onAfterCollapse", function(){
  cy.resize();
});

// redraw CFG after expanding
$$('accord').attachEvent("onAfterExpand", function(){
  cy.resize();
});

// redraw CFG when the 'cy' view resized
$$('cy').attachEvent('onViewResize', function() {
  cy.resize();
});

$(function(){
  var cy = window.cy = cytoscape({
    container: document.getElementById('cy'),
    layout: {
      name: 'dagre'
    },

    style: [
    {
      selector: 'node',
      style: {
        'shape': 'roundrectangle',
        'border-width': 'data(border)',
        'border-color': 'data(color)',
        'content': 'data(content)',
        'text-valign': 'center',
        'text-halign': 'center',
        'color': 'data(color)',
        'width': '120',
        'background-color': 'data(bc)',
      }
    },

    {
      selector: 'edge',
      style: {
        'width': 'data(width)',
        'target-arrow-shape': 'data(arrow)',
        'line-color': 'data(color)',
        'color': 'data(color)',
        'target-arrow-color': 'data(color)',
        'curve-style': 'bezier',
        'line-style': 'data(style)',
        'label': 'data(label)',
        'text-rotation': 'autorotate',
        'text-background-opacity': 1,
        'text-background-color': 'white',
      }
    }
    ],

    elements: {
      nodes: safe_DB.nodes,
      edges: safe_DB.edges,
    },
  });

  var blocks = cy.nodes().filter(function() { var a = this.id().includes(':'); return a; });
  blocks.on('click', function(e){
    $$('side-bar').expand();
    var target = e.cyTarget;
    var id = target.id();
    var insts_data = safe_DB.insts[id];
    var state_data = safe_DB.state[id];

    // reset insts data
    var insts = $$('insts');
    insts.clearAll();
    for (var i in insts_data) insts.add(insts_data[i]);

    // reset state data
    var state = $$('state');
    state.clearAll();
    for (var i in state_data) state.add(state_data[i].value, undefined, state_data[i].parent);

    $$('insts').select('block');
    cy.center(target);
    // TODO zoom is better?
    // cy.zoom({
    //   level: 2.0,
    //   position: target.position(),
    // });
  });
});
})
