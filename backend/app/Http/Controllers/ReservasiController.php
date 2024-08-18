<?php

namespace App\Http\Controllers;

use App\Models\Pelanggan;
use App\Models\Reservasi;
use Illuminate\Http\Request;
use Illuminate\Support\Arr;
use Illuminate\Support\Facades\DB;
use Illuminate\Validation\Rule;
use Illuminate\Support\Facades\Validator;

class ReservasiController extends Controller
{
    public function index()
    {
        return response()->json([
            'status' => 'sukses',
            'reservasi' => Reservasi::with('pelanggan', 'penerbangan', 'penumpang', 'pembayaran')->get()
        ]);
    }

    public function tambah(Request $request)
    {
        $data = $request->only([
            'kode',
            'berangkat',
            'tujuan',
            'waktu_berangkat',
            'waktu_tiba',
            'harga',
            'pelanggan',
            'penumpang'
        ]);

        $validator = Validator::make($data, [
            'kode' => ['required'],
            'berangkat' => ['required'],
            'tujuan' => ['required'],
            'waktu_berangkat' => ['required'],
            'waktu_tiba' => ['required'],
            'harga' => ['required'],
            'pelanggan' => ['required'],
            'penumpang' => ['required']
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'validasi gagal',
                'error' => Arr::map($validator->errors()->toArray(), fn ($error) => $error[0])
            ]);
        }

        $pelanggan = Pelanggan::where('nama', $request->input('pelanggan'))->orWhere('hp', $request->input('pelanggan'))->first();
        if (!$pelanggan) {
            return response()->json([
                'status' => 'pelanggan gagal',
                'pesan' => 'Pelanggan Tidak Ditemukan'
            ]);
        }

        $data['waktu'] = now();
        $data['status'] = 'proses';
        DB::beginTransaction();
        $reservasi = $pelanggan->reservasi()->create([
            'kode' => now()->getTimestamp(),
            ...$data
        ]);
        $penerbangan = $reservasi->penerbangan()->create($data);
        $penumpang = $reservasi->penumpang()->createMany($data['penumpang']);
        if ($reservasi && $penerbangan && $penumpang) {
            DB::commit();
            return response()->json([
                'status' => 'sukses',
                'pesan' => 'Reservasi Berhasil Ditambahkan'
            ]);
        }
        DB::rollBack();

        return response()->json([
            'status' => 'gagal',
            'pesan' => 'Reservasi Gagal Ditambahkan'
        ]);
    }

    public function lihat($id)
    {
        $user = Reservasi::find($id)->with('pelanggan', 'penerbangan', 'penumpang', 'pembayaran')->first();
        if ($user) {
            return response()->json([
                'status' => 'sukses',
                'pesan' => $user
            ]);
        }

        return response()->json([
            'status' => 'gagal',
            'pesan' => 'Reservasi Tidak Ditemukan'
        ]);
    }

    public function ubah(Request $request, $id)
    {
        $data = $request->only([
            'waktu',
            'status'
        ]);

        $validator = Validator::make($data, [
            'waktu' => ['required'],
            'status' => ['required'],
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'gagal',
                'pesan' => $validator->errors()
            ]);
        }

        if (Reservasi::find($id)->update($data)) {
            return response()->json([
                'status' => 'sukses',
                'pesan' => 'Reservasi Berhasil Diubah'
            ]);
        }

        return response()->json([
            'statsus' => 'gagal',
            'pesan' => 'Reservasi Gagal Diubah'
        ]);
    }

    public function hapus($id)
    {
        if (Reservasi::find($id)->delete()) {
            return response()->json([
                'status' => 'sukses',
                'pesan' => 'Reservasi Berhasil Dihapus'
            ]);
        }

        return response()->json([
            'status' => 'gagal',
            'pesan' => 'Reservasi Gagal Dihapus'
        ]);
    }
}
