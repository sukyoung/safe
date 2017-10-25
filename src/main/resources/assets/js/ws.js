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

const MSG_TYPE = {
  COMMAND: 'COMMAND',
  RESULT: 'RESULT',
  ERROR: 'ERROR',
  DEFAULT: 'DEFAULT',
}

class Connection {
  constructor () {
    const loc = window.location
    let uri
    if (loc.protocol === "https:") {
      uri = "wss:"
    } else {
      uri = "ws:"
    }
    uri += `//${loc.host}/ws`

    this.retry = 0
    this.uri = uri
    this.socket = this.connect()
    this.isFirst = true
    this.prompt = '>'
    this.done = false
  }

  connect () {
    const s = new WebSocket(this.uri)
    s.onopen = () => {
      this.updateStatusLabel()
      this.retry = 0

      if (this.isFirst) {
        this.send('help')
        this.isFirst = false
      }
    }
    s.onmessage = (msg) => {
      const { prompt, output, done } = JSON.parse(msg.data)
      this.print(output, MSG_TYPE.RESULT)
      this.updatePrompt(prompt)
      if (done) {
        this.done = true
        this.close()
      }
    }
    s.onerror = (e) => {
      console.log(e)
    }
    s.onclose = () => {
      this.updateStatusLabel()
      window.onbeforeunload = function () {
      }
      this.reconnect()
    }

    window.onbeforeunload = function () {
      s.onclose = function () {} // disable onclose handler first
      s.close()
    };

    return s
  }

  send (cmd) {
    if (this.isConnected() && !this.done) {
      this.socket.send(JSON.stringify({
        cmd,
      }))
      this.print(cmd, MSG_TYPE.COMMAND)
    } else {
      this.reconnect()
    }
  }

  close () {
    this.socket.close()
    this.updateStatusLabel()
    this.updatePrompt("[Done]")
    window.onbeforeunload = function () {
    }
  }

  reconnect () {
    if (this.done) {
      return
    } else if (this.retry === 11) {
      this.print("Failed to connect server", MSG_TYPE.ERROR)
      return
    } else if (this.retry > 10) {
      return
    }

    if (this.socket.readyState === WebSocket.CLOSED || this.socket.readyState === WebSocket.CLOSING) {
      this.retry += 1
      this.close()
      this.socket = this.connect()
    }
  }

  isConnected () {
    return this.socket.readyState === WebSocket.OPEN
  }

  updateStatusLabel () {
    const status = $('.console-status')
    const text = $('.console-status-text')
    switch (this.socket.readyState) {
      case WebSocket.OPEN:
        status.attr('class', 'console-status status-connected')
        text.text('CONNECTED')
        break
      case WebSocket.CLOSED:
      case WebSocket.CLOSING:
        status.attr('class', 'console-status status-closed')
        text.text('CLOSED')
        break
      case WebSocket.CONNECTING:
        status.attr('class', 'console-status status-connecting')
        text.text('CONNECTING')
        break
      default:
    }
  }

  updatePrompt (prompt) {
    this.prompt = prompt
    $('.console-prompt').text(prompt)
  }

  print (msg, type=MSG_TYPE.DEFAULT) {
    const o = $('.console-output')
    if (type === MSG_TYPE.DEFAULT) {
      o.append(`<span class="console-line">${msg}</span>`)
    } else if (type === MSG_TYPE.COMMAND) {
      o.append($(`<p class="console-line console-line-command"></p>`).text(`${this.prompt} ${msg}`))
    } else if (type === MSG_TYPE.ERROR) {
      o.append(`<p class="console-line console-line-error">[Error] ${msg}</p>`)
    } else if (type === MSG_TYPE.RESULT) {
      o.append(`<p class="console-line console-line-result">${msg}</p>`)
    }
    o.scrollTop(o.prop('scrollHeight'))
  }
}

let conn = new Connection()

$(function () {
  $('.console-input').keypress(function(e) {
    if (e.which === 13) {
      const cmd = $(this).val() || 'next'
      conn.send(cmd)
      $(this).val('')
    }
  })
})
