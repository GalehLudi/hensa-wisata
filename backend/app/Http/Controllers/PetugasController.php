<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Validation\Rule;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;

class PetugasController extends Controller
{
    public function index()
    {
        return response()->json([
            'status' => 'sukses',
            'data' => User::get()
        ]);
    }

    public function tambah(Request $request)
    {
        $data = $request->only([
            'nama',
            'email',
            'hp',
            'level',
            'password'
        ]);

        $validator = Validator::make($data, [
            'nama' => ['required'],
            'email' => ['required'],
            'hp' => ['required', Rule::unique(User::class), 'max:13'],
            'level' => ['required'],
            'password' => ['required']
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'gagal',
                'pesan' => 'validasi gagal!',
                'error' => $validator->errors()
            ]);
        }

        $data['password'] = Hash::make($request->input('password'));

        if (User::create($data)) {
            return response()->json([
                'status' => 'sukses',
                'pesan' => 'Petugas Berhasil Ditambahkan'
            ]);
        }

        return response()->json([
            'status' => 'gagal',
            'pesan' => 'Petugas Gagal Ditambahkan'
        ]);
    }

    public function lihat(User $user)
    {
        if ($user) {
            return response()->json([
                'status' => 'sukses',
                'data' => $user
            ]);
        }

        return response()->json([
            'status' => 'gagal',
            'pesan' => 'Petugas Tidak Ditemukan'
        ], 404);
    }

    public function ubah(Request $request, $id)
    {
        $data = $request->only([
            'nama',
            'email',
            'hp',
            'level'
        ]);

        $validator = Validator::make($data, [
            'nama' => ['required'],
            'email' => ['required'],
            'hp' => ['required', Rule::unique(User::class)->ignore($id), 'max:13'],
            'level' => ['required']
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'gagal',
                'pesan' => $validator->errors()->first()
            ]);
        }

        if ($request->input('password')) {
            $data['password'] = Hash::make($request->input('password'));
        }

        if (User::find($id)->update($data)) {
            return response()->json([
                'status' => 'sukses',
                'pesan' => 'Petugas Berhasil Diubah'
            ]);
        }

        return response()->json([
            'statsus' => 'gagal',
            'pesan' => 'Petugas Gagal Diubah'
        ]);
    }

    public function hapus($id)
    {
        if (User::find($id)->delete()) {
            return response()->json([
                'status' => 'sukses',
                'pesan' => 'Petugas Berhasil Dihapus'
            ]);
        }

        return response()->json([
            'status' => 'gagal',
            'pesan' => 'Petugas Gagal Dihapus'
        ]);
    }
}
