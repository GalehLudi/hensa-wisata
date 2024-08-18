<?php

namespace App\Http\Controllers;

use App\Models\Chat;
use App\Models\Pelanggan;
use Illuminate\Support\Arr;
use Illuminate\Support\Str;
use Illuminate\Http\Request;
use Illuminate\Validation\Rule;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Validator;

class WhatsappGatewayController extends Controller
{
    private $httpClient;
    private string $device = 'web-whatsapp';

    public function __construct()
    {
        $this->httpClient = Http::baseUrl(env('WHATSAPP_API_URL'));
    }

    public function getDevice()
    {
        return $this->httpClient->get('devices/' . $this->device)->json();
    }

    public function getQrCode()
    {
        if ($this->httpClient->get('devices/' . $this->device)->ok())
            return $this->httpClient->get('qr', [
                'device_id' => $this->device,
            ])->json();

        $this->httpClient->post('devices', [
            'device_id' => $this->device,
        ]);

        return $this->httpClient->get('qr', [
            'device_id' => $this->device,
        ])->json();
    }

    public function getMessage()
    {
        return $this->httpClient->get('messages', [
            // 'status' => 'pending'
        ])->json();
    }

    public function sendMessage(Request $request, $tujuan)
    {
        $validator = Validator::make($request->all(), [
            'pesan' => ['required']
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'validasi gagal',
                'error' => Arr::map($validator->errors()->toArray(), fn($error) => $error[0])
            ]);
        }

        return $this->initSendMessage($tujuan, $request->post('pesan'), $request->post('file'));
    }

    private  function initSendMessage($to, $message, $file = null)
    {
        $data = [
            'to' => $to,
            'message' => $message
        ];

        $media = [];
        if ($file) {
            $media['mimetype'] = Storage::mimeType('public/' . $file);
            $media['data'] = Storage::disk('public')->get($file);
        }

        $response = $this->httpClient->post('message', $data, $media)->json();

        $chat = Chat::create([
            'user_id' => Auth::id(),
            'pelanggan_id' => Pelanggan::where('hp', $to)->first()->id,
            'chat_id' => $response['id'],
            'pesan' => $message,
            'dari' => 'petugas',
            'file' => $file,
            'status' => $response['status']
        ]);

        return $chat;
    }

    public function webhook(Request $request)
    {
        $filename = "";
        if ($request->post('webhook_type') === 'incoming_message') {
            if (Str::contains($request->post('type'), 'image', true)) {
                $media = base64_decode($request->post('media')['data']);
                $filename = $request->post('id') . Str::of($request->post('media')['mimetype'])->replace('image/', '.');
                Storage::disk('public')->put('media/' . $filename, $media);
            }

            $pelanggan = Pelanggan::where('hp', $request->post('from'))->first();
            if (!$pelanggan) {
                $pelanggan = Pelanggan::create([
                    'hp' => $request->post('from'),
                    'nama' => $request->post('isMyContact') ? $request->post('name') : $request->post('pushname')
                ]);
            }

            Chat::create([
                'pelanggan_id' => $pelanggan->id,
                'chat_id' => $request->post('id'),
                'pesan' => $request->post('message'),
                'dari' => 'pelanggan',
                'file' => $filename ? 'media/' . $filename : null,
                'status' => $request->post('status')
            ]);

            if (Str::contains($request->post('message'), 'formulir reservasi tiket', true)) {
                $messages = Str::of($request->post('message'))->split("/\n/");
                $data = [];
                foreach ($messages as &$message) {
                    if (Str::contains($message, ':')) {
                        $message = Str::of($message)->explode(':');

                        if (Str::contains($message[0], 'penumpang', true) || Str::contains($message[0], 'umur', true))
                            $data[Str::lower(Str::trim($message[0]))][] = Str::trim($message[1]);
                        else
                            $data[Str::lower(Str::trim($message[0]))] = Str::trim($message[1]);
                    }
                }

                $validator = Validator::make($data, [
                    'keberangkatan' => ['required'],
                    'tujuan' => ['required'],
                    'waktu' => ['required'],
                    'penumpang' => ['required'],
                    'penumpang.*' => ['required'],
                    'umur' => ['required'],
                    'umur.*' => ['required'],
                ]);

                if ($validator->fails()) {
                    $this->initSendMessage($request->post('from'), Arr::join($validator->errors()->all(), "\n"));

                    return;
                }

                $data['berangkat'] = $data['keberangkatan'];
                $data['status'] = 'proses';

                $reservasi =  $pelanggan->reservasi()->create([
                    'kode' => now()->getTimestamp(),
                    ...$data
                ]);
                $penerbangan = $reservasi->penerbangan()->create($data);

                $penumpang = [];
                foreach ($data['penumpang'] as $key => $value) {
                    $penumpang[] = [
                        'nama' => $value,
                        'umur' => $data['umur'][$key]
                    ];
                }
                $penumpang = $reservasi->penumpang()->createMany($penumpang);

                if ($reservasi && $penerbangan && $penumpang) {
                    $this->initSendMessage($request->post('from'), "Reservasi tiket sedang diproses. Silahkan menunggu konfirmasi selanjutnya.\n\nKode reservasi anda : {$reservasi->kode}.");

                    return;
                }

                $this->initSendMessage($request->post('from'), "Reservasi tiket gagal diproses. Silahkan coba kembali.");
            } else if (Str::contains($request->post('message'), ['reservasi', 'pesan tiket'], true)) {
                $this->initSendMessage($request->post('from'), "*Formulir Reservasi Tiket*\n\nKeberangkatan :\nTujuan :\nWaktu :\nPenumpang :\nUmur :");
                $this->initSendMessage($request->post('from'), "*_Note_*: Jika penumpang lebih dari 1 orang, Anda dapat menambahkan format penumpang seperti berikut.\n\nPenumpang : Budi\nUmur : 20\nPenumpang : Andi\nUmur : 25\ndan seterusnya.");
            } else if (Str::contains($request->post('message'), 'list tagihan', true)) {
                $reservasi = $pelanggan->reservasi()->with('pembayaran')->get();
                $reservasi = $reservasi->filter(function ($reservasi) {
                    if ($reservasi->harga && $reservasi->harga - $reservasi->pembayaran()->sum('jumlah') > 0) {
                        return $reservasi;
                    }
                });

                $index = 0;
                $tagihan = $reservasi->map(function ($reservasi) use (&$index) {
                    $tagihan = $reservasi->harga - $reservasi->pembayaran->sum('jumlah');
                    $index++;
                    return "
                    {$index}. Kode Reservasi : `{$reservasi->kode}`
                    Penerbang : {$reservasi->penerbangan()->keberangkatan} - {$reservasi->penerbangan()->tujuan}
                    Tanggal : {$reservasi->waktu}
                    Tagihan : Rp.{$tagihan}
                    ";
                });

                $this->initSendMessage($request->post('from'), "*Daftar tagihan*\n\n" . Arr::join($tagihan, "\n\n"));
            } else if (Str::contains($request->post('message'), 'formulir pembayaran tagihan', true)) {
                $message = Str::of($request->post('message'))->split("/\n/");
                $data = [];
                foreach ($message as &$message) {
                    if (Str::contains($message, ':')) {
                        $message = Str::of($message)->explode(':');
                        $data[Str::lower(Str::trim($message[0]))] = Str::trim($message[1]);
                    }
                }

                $validator = Validator::make($data, [
                    'kode reservasi' => ['required', Rule::exists('reservasi', 'kode')],
                    'waktu pembayaran' => ['required'],
                    'jumlah' => ['required'],
                ]);

                if ($validator->fails()) {
                    $this->initSendMessage($request->post('from'), Arr::join($validator->errors()->all(), "\n"));

                    return;
                }

                $pembayaran = $pelanggan->pembayaran()->create([
                    'reservasi_id' => $pelanggan->reservasi()->where('kode', $data['kode reservasi'])->first()->id,
                    'waktu' => $data['waktu pembayaran'],
                    'jumlah' => $data['jumlah'],
                    'status' => 'menunggu',
                ]);

                $this->initSendMessage($request->post('from'), "Pembayaran tagihan {$pembayaran->reservasi->kode} sebesar Rp.{$pembayaran->jumlah} telah dilakukan.\nSilahkan kirim bukti pembayaran dengan menambahkan bukti pembayaran: kode reservasi pada bagian pesan.\n\nContoh: `bukti pembayaran: 123456`");
            } else if (Str::contains($request->post('message'), 'bukti pembayaran', true) && Str::contains($request->post('type'), 'image', true)) {
                $kode = Str::of($request->post('message'))->replace("/[^0-9]/", '');
                $reservasi = $pelanggan->reservasi()->where('kode', $kode)->first();
                $pembayaran = $pelanggan->pembayaran()->where('reservasi_id', $reservasi->id)->first();

                if ($pembayaran) {
                    $pembayaran->update([
                        'bukti' => $filename,
                        'status' => 'proses',
                    ]);

                    $this->initSendMessage($request->post('from'), "Pembayaran tagihan {$reservasi->kode} sebesar Rp.{$pembayaran->jumlah} telah diproses.\nSilahkan tunggu konfirmasi selanjutnya.");
                }
            } else if (Str::contains($request->post('message'), 'bayar tagihan', true)) {
                $this->initSendMessage($request->post('from'), "Silahkan isi form pembayaran di bawah ini dan juga upload bukti pembayaran.\n\nUntuk melihat kode reservasi, Anda dapat mengirimkan pesan dengan format `list tagihan`\n\n*_Note_*: Bukti pembayaran harus berupa gambar dan pada bagian pesan harus ditambahkan kode reservasi.");
                $this->initSendMessage($request->post('from'), "*Formulir Pembayaran*\n\nKode Reservasi: \nWaktu Pembayaran: \nJumlah:");
            }
        } else if ($request->post('webhook_type') === 'status_message') {
            Chat::where('chat_id', $request->post('id'))->update([
                'status' => $request->post('status')
            ]);
        }

        return;
    }
}
