<?php

namespace App\Http\Controllers;

use App\Models\Reservasi;
use App\Models\Pembayaran;
use Illuminate\Support\Arr;
use Illuminate\Http\Request;
use Illuminate\Validation\Rule;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Validator;

class TransaksiController extends Controller
{
    public function index(Request $request)
    {
        $reservasi = new Reservasi();
        $reservasi = $reservasi->with(['penerbangan', 'penumpang', 'pelanggan']);
        if ($request->query('filter')) {
            if ($request->query('filter') == 'pembayaran') {
                $reservasi = $reservasi->with(['pembayaran'])->where('status', 'proses')->latest()->get();
                $reservasi = $reservasi->filter(fn($reservasi) => $reservasi->harga && $reservasi->harga - $reservasi->pembayaran()->sum('jumlah') > 0);
            } else {
                $reservasi = $reservasi->where('status', $request->query('filter'))->get();
            }
        } else {
            $reservasi = $reservasi->get();
        }

        return response()->json([
            'status' => 'sukses',
            'data' => $reservasi
        ]);
    }

    public function lihat(Reservasi $reservasi)
    {
        return response()->json([
            'status' => 'sukses',
            'data' => $reservasi->load(['pelanggan', 'penerbangan', 'penumpang', 'pembayaran'])
        ]);
    }

    public function batal(Reservasi $reservasi)
    {
        if ($reservasi->update(['status' => 'batal']))
            return response()->json([
                'status' => 'sukses',
                'pesan' => 'Transaksi dibatalkan'
            ]);

        return response()->json([
            'status' => 'gagal',
            'pesan' => 'Transaksi gagal dibatalkan'
        ]);
    }

    public function proses(Request $request, Reservasi $reservasi)
    {
        $validator = Validator::make($request->all(), [
            'kode' => ['required'],
            'waktu_berangkat' => ['required'],
            'waktu_tiba' => ['required'],
            'harga' => ['required'],
            'tiket' => ['required', Rule::file()->types('application/pdf')],
        ]);

        if ($validator->fails())
            return response()->json([
                'status' => 'validasi gagal',
                'error' => Arr::map($validator->errors()->toArray(), fn($error) => $error[0])
            ]);

        $file = $request->file('tiket')->storeAs('tiket', $reservasi->kode . '-' . $request->kode . '.pdf', 'public');

        if ($file) {
            $penerbangan = $reservasi->penerbangan()->update([
                'kode' => $request->kode,
                'waktu_berangkat' => $request->waktu_berangkat,
                'waktu_tiba' => $request->waktu_tiba
            ]);

            if ($reservasi->update(['harga' => $request->harga, 'status' => 'selesai']) && $penerbangan) {
                Http::baseUrl(route('chat', $reservasi->pelanggan()->first()->hp))->post('', [
                    'pesan' => 'Transaksi telah diproses. Dengan kode ti',
                ]);

                return response()->json([
                    'status' => 'sukses',
                    'pesan' => "Reservasi dengan kode reservasi {$reservasi->kode} telah diproses.\n\nKode tiket: {$request->kode}\nWaktu berangkat: {$request->waktu_berangkat}\nWaktu tiba: {$request->waktu_tiba}\nHarga: {$request->harga}",
                    'file' => $file
                ]);
            }
        }

        return response()->json([
            'status' => 'gagal',
            'pesan' => 'Transaksi gagal diproses'
        ]);
    }

    public function bayar(Request $request, Pembayaran $pembayaran)
    {
        $state = false;
        if ($request->post('status') === 'terima') {
            $state = $pembayaran->update(['status' => 'terima']);
        } else if ($request->post('status') === 'tolak') {
            $state = $pembayaran->update(['status' => 'tolak']);
        }

        if ($state) {
            Http::baseUrl(route('chat', $pembayaran->reservasi()->pelanggan()->first()->hp))->post('', [
                'pesan' => "Pembayaran dengan kode reservasi {$pembayaran->reservasi()->first()->kode} telah {$request->post('status')}.",
            ]);

            return response()->json([
                'status' => 'sukses',
                'pesan' => "Pembayaran dengan kode reservasi {$pembayaran->reservasi()->first()->kode} telah {$request->post('status')}."
            ]);
        }

        return response()->json([
            'status' => 'gagal',
            'pesan' => "Pembayaran dengan kode reservasi {$pembayaran->reservasi()->first()->kode} gagal {$request->post('status')}."
        ]);
    }
}
