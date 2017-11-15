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

const MAX_RETRY = 5

class Connection {
  constructor () {
    const loc = window.location
    let uri
    if (loc.protocol === "https:") {
      uri = "wss:"
    } else {
      uri = "ws:"
    }
    const uid = Math.floor(Math.random() * 10000000)
    console.log(`Generated UID: ${uid}`)
    uri += `//${loc.host}/ws?uid=${uid}`

    this.retry = 0
    this.uri = uri
    this.socket = this.connect()
    this.done = false
    this.p = ""
    this.i =-1
  }

  get prompt () {
    return this.p
  }
  set prompt (prompt) {
    this.p = prompt
    $('.console-prompt').text(prompt)
  }
  get iter () {
    return this.i
  }
  set iter(iter) {
    this.i = iter
    $('.console-iter').text(`Iter[${iter}] >`)
  }

  connect () {
    const s = new WebSocket(this.uri)
    s.onopen = () => {
      this.updateStatusLabel()
      this.retry = 0
      console.log('WebSocket is connected!')
    }
    s.onmessage = (msg) => {
      const { cmd, prompt, iter, output, state, done } = JSON.parse(msg.data)
      // TODO: remove eval
      eval(`safe_DB = ${state}`)
      redrawGraph()
      if (cmd !== '') {
        this.print(cmd, MSG_TYPE.COMMAND)
        this.print(output, MSG_TYPE.RESULT)
      }
      this.prompt = prompt
      this.iter = iter

      if (done) {
        this.done = true
        this.close()
      }
    }
    s.onclose = () => {
      this.updateStatusLabel()
      if (this.retry === 0) {
        console.log(`WebSocket connection is lost. Try to reconnect.`)
        this.reconnect()
      } else {
        const timeout = (2**this.retry) * 500
        console.log(`Try to reconnect after ${timeout}ms...`)
        setTimeout(() => {
          this.reconnect()
        }, timeout)
      }
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
    } else {
      this.retry = 0
      this.reconnect()
    }
  }

  close () {
    this.socket.close()
    this.updateStatusLabel()
    if (this.done) {
      this.prompt = 'Analysis has done\nTo restart analysis, please restart the server'
      $('.console-input-line').hide()
    }
    window.onbeforeunload = function () {
    }
  }

  reconnect () {
    if (this.done) {
      return
    } else if (this.retry === MAX_RETRY) {
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

  print (msg, type=MSG_TYPE.DEFAULT) {
    if (msg === 'status') return

    const o = $('.console-output')
    if (type === MSG_TYPE.DEFAULT) {
      o.append(`<span class="console-line">${msg}</span>`)
    } else if (type === MSG_TYPE.COMMAND) {
      let p = ''
      if (this.prompt) {
        p += `${this.prompt}\n`
      }
      if (this.iter > -1) {
        p += `Iter[${this.iter}] > ${msg}`
      }
      o.append($(`<p class="console-line console-line-command"></p>`).text(p))
    } else if (type === MSG_TYPE.ERROR) {
      o.append(`<p class="console-line console-line-error">[Error] ${msg}</p>`)
    } else if (type === MSG_TYPE.RESULT) {
      o.append(`<p class="console-line console-line-result">${msg}</p>`)
    }
    const console = $('.console')
    console.scrollTop(console.prop('scrollHeight'))
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
