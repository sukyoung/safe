if (window['safe_DB']) {
  webix.ready(function(){
    if (webix.CustomScroll && !webix.env.touch) webix.CustomScroll.init();
    var side = {
      margin: 10, padding: 0, type: 'wide',
      rows:[
      { view: 'unitlist', id: 'insts', uniteBy: function(obj) { return obj.kind; },
        select: false },// , height: 300 }, // TODO select: true
      // TODO { id: 'state', body: 'state structure' },
      ]
    };

    webix.ui({
      view: 'accordion', type: 'line',
      rows:[
      { id: 'accord', type: 'wide', collapsed: true, cols:[
        { id: 'side-bar', header: 'instruction'/* TODO & state'*/, width: 500, minWidth: 320, body: side },
        { id: 'resizer', view: 'resizer' },
        { id: 'cy', body: { content: 'cy' } },
      ]},
      // TODO { template: 'search engine', height: 30},
      ]
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
            'background-color': 'white',
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
        var data = safe_DB.insts[id];

        // reset data
        var insts = $$('insts');
        insts.clearAll();
        for (var i in data) insts.add(data[i]);

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
} else {
  // TODO handling no DB case.
}
