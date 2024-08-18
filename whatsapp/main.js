const express = require('express')
const cors = require('cors')
const { Client, LocalAuth, MessageAck, MessageMedia } = require('whatsapp-web.js')
const qrcode = require('qrcode-terminal')
const { default: axios } = require('axios')
const fs = require('fs')
const Pusher = require('pusher')

const pusher = new Pusher({
  appId: '1539789',
  key: '9d95b586eea6b0fd48db',
  secret: '50c43b402ef41a476b1e',
  cluster: 'ap1',
  useTLS: true,
})

let lastGeneratedQr = null

const client = new Client({
  authStrategy: new LocalAuth(),
  puppeteer: {
    headless: true,
    args: ['--no-sandbox', '--disable-setuid-sandbox', '--disable-dev-shm-usage', '--disable-accelerated-2d-canvas', '--no-first-run', '--no-zygote', '--single-process', '--disable-gpu'],
  },
})

const parseACK = (ack) => {
  switch (ack) {
    case MessageAck.ACK_ERROR:
      return 'ERROR'
    case MessageAck.ACK_PENDING:
      return 'PENDING'
    case MessageAck.ACK_SERVER:
      return 'SERVER'
    case MessageAck.ACK_DEVICE:
      return 'DELIVERED'
    case MessageAck.ACK_READ:
      return 'READ'
  }
}

const initWebhook = async (message, type = null) => {
  if (!message.author) {
    const data = {}
    data['id'] = message.id.id
    data['status'] = parseACK(message.ack)
    data['type'] = message.type
    data['message'] = message.body
    data['timestamp'] = new Date(message.timestamp * 1000)
    data['webhook_type'] = type

    if (message.type === 'image') {
      const media = await message.downloadMedia()
      fs.writeFileSync('media/' + message.id.id + '.' + media.mimetype.split('/')[1], Buffer.from(media.data, 'base64').toString('binary'), 'binary')
      data['media'] = media
      data['message'] = message.caption
    }

    if (message.fromMe) {
      if (!type) data['webhook_type'] = 'sending_message'
      data['to'] = message.to.replace('@c.us', '')
      data['isMe'] = true
    } else {
      const user = await message.getContact()
      if (!type) data['webhook_type'] = 'incoming_message'
      data['name'] = user.name
      data['pushname'] = user.pushname
      data['from'] = message.from.replace('@c.us', '')
      data['isMe'] = false
      data['isMyContact'] = user.isMyContact
    }

    await axios.post('http://127.0.0.1:8000/api/whatsapp/webhook', data).catch((error) => {
      console.error(error)
    })
  }
}

client.on('ready', () => {
  console.log('Client is ready!')
})

client.on('qr', async (qr) => {
  lastGeneratedQr = qr
  pusher.trigger('whatsapp', 'qr', {
    qr: qr,
  })
  qrcode.generate(qr, { small: true })
})

client.on('authenticated', () => {
  lastGeneratedQr = null
  console.log('AUTHENTICATED')
})

client.on('auth_failure', (message) => {
  console.log('AUTHENTICATION FAILURE', message)
})

client.on('message_create', async (message) => {
  initWebhook(message)
})

client.on('contact_changed', (message, oldId, newId, isContact) => {
  if (isContact) {
    const data = {
      message: message,
      oldId: oldId,
      newId: newId,
    }
  }
})

client.on('message_ack', async (message) => {
  initWebhook(message, 'status_message')
})

client.on('change_state', (state) => {
  console.log('CHANGE_STATE', state)
})

client.on('disconnected', (reason) => {
  console.log('DISCONNECTED', reason)
})

client.initialize()

const app = express()
app.use(express.json())
app.use(cors())

app.get('/qr', async (req, res) => {
  res.send({ qr: lastGeneratedQr })
})

app.get('/status', async (req, res) => {
  res.send({ status: 'OK' })
})

app.get('/logout', async (req, res) => {
  try {
    await client.logout()
    res.send({ status: 'OK' })
  } catch (error) {
    res.status(500).send({ error: error })
  }
})

app.post('/message', async (req, res, next) => {
  const { to, message, media } = req.body

  client.sendPresenceAvailable()

  setTimeout(() => {
    client.sendPresenceUnavailable()
  }, 5000)

  const content = []
  if (media) {
    content.push(new MessageMedia(media.mimetype, media.data))
    content.push({ caption: message })
  } else {
    content.push(message)
  }

  await client
    .sendMessage(to + '@c.us', ...content)
    .then((result) => {
      const data = {
        id: result.id.id,
        ack: result.ack,
        type: result.type,
        message: result.body,
        timestamp: new Date(result.timestamp * 1000),
        to: result.to.replace('@c.us', ''),
        isMe: true,
        status: parseACK(result.ack),
      }

      res.send(data)
    })
    .catch((error) => {
      res.status(500).send({ error: 'Gagal mengirim pesan' })
    })
})

const PORT = process.env.PORT || 3000
app.listen(PORT, () => console.log(`🚀 @ http://localhost:${PORT}`))
