/**
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

socket.onopen = () => {
    socket.send('test')
}

socket.onmessage = (msg) => {
    console.log(msg)
}

socket.onerror = (e) => {
    console.log(e)
}