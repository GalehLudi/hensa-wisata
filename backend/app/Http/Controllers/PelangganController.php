<?php

namespace App\Http\Controllers;

use App\Models\Pelanggan;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\Rule;

class PelangganController extends Controller
{
    public function index()
    {
        return response()->json([
            'status' => 'sukses',
            'data' => Pelanggan::get()
        ]);
    }

    public function tambah(Request $request)
    {
        $data = $request->only([
            'nama',
            'hp'
        ]);

        $validator = Validator::make($data, [
            'nama' => ['required'],
            'hp' => ['required', Rule::unique(Pelanggan::class), 'max:13']
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'gagal',
                'pesan' => $validator->errors()
            ]);
        }

        if (Pelanggan::create($data)) {
            return response()->json([
                'status' => 'sukses',
                'pesan' => 'Pelanggan Berhasil Ditambahkan'
            ]);
        }

        return response()->json([
            'status' => 'gagal',
            'pesan' => 'Pelanggan Gagal Ditambahkan'
        ]);
    }

    public function lihat(Pelanggan $pelanggan)
    {
        if ($pelanggan) {
            return response()->json([
                'status' => 'sukses',
                'pesan' => 'Pelanggan Ditemukan',
                'data' => $pelanggan
            ]);
        }

        return response()->json([
            'status' => 'gagal',
            'pesan' => 'Pelanggan Tidak Ditemukan'
        ]);
    }

    public function ubah(Request $request, $id)
    {
        $data = $request->only([
            'nama',
            'hp'
        ]);

        $validator = Validator::make($data, [
            'nama' => ['required'],
            'hp' => ['required', Rule::unique(Pelanggan::class)->ignore($id), 'max:13']
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'gagal',
                'pesan' => $validator->errors()
            ]);
        }

        if (Pelanggan::find($id)->update($data)) {
            return response()->json([
                'status' => 'sukses',
                'pesan' => 'Pelanggan Berhasil Diubah'
            ]);
        }

        return response()->json([
            'statsus' => 'gagal',
            'pesan' => 'Pelanggan Gagal Diubah'
        ]);
    }

    public function hapus($id)
    {
        if (Pelanggan::find($id)->delete()) {
            return response()->json([
                'status' => 'sukses',
                'pesan' => 'Pelanggan Berhasil Dihapus'
            ]);
        }

        return response()->json([
            'status' => 'gagal',
            'pesan' => 'Pelanggan Gagal Dihapus'
        ]);
    }
}
