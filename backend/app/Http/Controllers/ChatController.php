<?php

namespace App\Http\Controllers;

use App\Models\Chat;
use App\Models\Pelanggan;
use Illuminate\Http\Request;

class ChatController extends Controller
{
    public function index()
    {
        return response()->json([
            'status' => 'sukses',
            'data' => Chat::with(['user', 'pelanggan'])->groupBy('pelanggan_id')->get()
        ]);
    }

    public function show($hp)
    {
        $pelanggan = Pelanggan::where('hp', $hp)->first();
        return response()->json([
            'status' => 'sukses',
            'data' => Chat::where('pelanggan_id', $pelanggan->id)->with(['user', 'pelanggan'])->get()
        ]);
    }
}
