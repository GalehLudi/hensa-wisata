<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;

class AuthController extends Controller
{
    public function login(Request $request)
    {
        $validator = Validator::make($request->only(['user', 'password', 'device']), [
            'user' => ['required'],
            'password' => ['required'],
            'device' => ['required']
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'gagal',
                'pesan' => $validator->errors()
            ]);
        }

        $user = User::where('email', $request->post('user'))->first();
        if ($user && Hash::check($request->post('password'), $user->password)) {
            return response()->json([
                'status' => 'sukses',
                'pesan' => 'Login Berhasil',
                'token' => $user->createToken($request->input('device'))->plainTextToken
            ]);
        }

        return response()->json([
            'status' => 'gagal',
            'pesan' => 'Login Gagal'
        ]);
    }

    public function logout()
    {
        $user = User::find(Auth::id())->first();
        $user->currentAccessToken()->delete();
        return response()->json([
            'status' => 'sukses',
            'pesan' => 'Logout Berhasil'
        ]);
    }

    public function user()
    {
        return response()->json([
            'status' => 'sukses',
            'data' => Auth::user()
        ]);
    }
}
