<!DOCTYPE html>
<html lang="en">

  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>

    <script src="https://js.pusher.com/8.2.0/pusher.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/qrcodejs@1.0.0/qrcode.min.js"
      integrity="sha256-xUHvBjJ4hahBW8qN9gceFBibSFUzbe9PNttUvehITzY=" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
  </head>

  <body>
    <div id="qrcode" hidden></div>
  </body>

  <script>
    var pusher = new Pusher('9d95b586eea6b0fd48db', {
      cluster: 'ap1'
    });

    var qrPreview = document.getElementById("qrcode");

    var qrcode = new QRCode(qrPreview, {
      text: "",
      width: 250,
      height: 250,
      colorDark: "#000000",
      colorLight: "#ffffff",
      correctLevel: QRCode.CorrectLevel.M
    });

    axios.get('http://127.0.0.1:3000/qr').then(function(response) {
      qrcode.clear()
      qrcode.makeCode(JSON.stringify(response.data['qr']));
      qrPreview.removeAttribute("hidden");
    })

    var channel = pusher.subscribe('whatsapp');
    channel.bind('qr', function(data) {
      qrcode.clear()
      qrcode.makeCode(JSON.stringify(data['qr']));
      qrPreview.removeAttribute("hidden");
    });
  </script>

</html>
