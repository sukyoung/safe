/*
 * ****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ***************************************************************************
 */

webix.ready(function(){
  if (webix.CustomScroll && !webix.env.touch) webix.CustomScroll.init();
  var side = {
    margin: 10, padding: 0, type: 'wide',
    rows:[
      { view: 'unitlist', id: 'insts', uniteBy: function(obj) { return obj.kind; },
        height: 300, select: true },
      { view: 'tree', id: 'state' },
    ]
  };

  webix.ui({
    rows:[
      { view: 'toolbar', id: 'toolbar', elements: [
        { view: 'label', label: 'SAFE 2.0' },
        { view: 'button', id: 'next',
          type: 'iconButton', icon: 'play', label: 'Next', width: 80,
          click () {
            conn.cmd('next')
          },
        },
        { view: 'icon', icon: 'bars',
          click: function() {
            if( $$('options').config.hidden) {
              $$('options').show();
            } else $$('options').hide();
          }
        },
      ]},
      { view: 'accordion', id: 'accord', type: 'wide', collapsed: true, cols:[
        { id: 'side-bar', header: 'Instructions & State', collapsed: false, width: 500, minWidth: 320, body: side },
        { id: 'resizer', view: 'resizer' },
        { id: 'cy', body: { content: 'cy' } },
        { id: 'console', header: 'Console', minWidth: 720, width: '50%', height: '100%', body: { content: 'console' } },
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
    cy.layout();
  });

// redraw CFG after expanding
  $$('accord').attachEvent("onAfterExpand", function(){
    cy.resize();
    cy.layout();
  });

// redraw CFG when the 'cy' view resized
  $$('cy').attachEvent('onViewResize', function() {
    cy.resize();
    cy.layout();
  });

  drawGraph();

  $('.console').click(function () {
    $('.console-input').focus()
  })
})

function drawGraph () {
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

  const blocks = cy.nodes().filter(function() { var a = this.id().includes(':'); return a; });
  blocks.on('click', function(e){
    const target = e.cyTarget;
    const id = target.id();
    conn.getBlockState(id)
  });
}

function redrawGraph () {
  if (window.cy.destroy) {
    window.cy.destroy()
  }
  drawGraph()
}
