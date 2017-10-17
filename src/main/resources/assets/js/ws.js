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

const loc = window.location
let uri
if (loc.protocol === "https:") {
  uri = "wss:"
} else {
  uri = "ws:"
}
uri += `//${loc.host}/ws`

const socket = new WebSocket(uri)

function send(msg) {
  $('.console-output').append(`<p class="console-line-command">&gt; ${msg}`)
  socket.send(msg)
}

socket.onopen = () => {
  $('.console-output').append(`<p>[Connected]</pj>`)
  send('test')
}
socket.onmessage = (msg) => {
  const o = $('.console-output')
  o.append(`<p class="console-line-result">${msg.data}</p>`)
  o.scrollTop(o.prop('scrollHeight'))
}
socket.onerror = (e) => {
  $('.console-output').append(`<p class="console-line-error">${e}</p>`)
}

$(function () {
  $('.console-input').keypress(function(e) {
    if (e.which === 13) {
      send($(this).val())
      $(this).val('')
    }
  })
})
