<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\ChatController;
use App\Http\Controllers\LaporanController;
use App\Http\Controllers\PetugasController;
use App\Http\Controllers\PelangganController;
use App\Http\Controllers\ReservasiController;
use App\Http\Controllers\TransaksiController;
use App\Http\Controllers\WhatsappGatewayController;

Route::controller(AuthController::class)->group(function () {
    Route::post('login', 'login');
    Route::delete('logout', 'logout')->middleware('auth:sanctum');
    Route::get('user', 'user')->middleware('auth:sanctum');
});

Route::middleware('auth:sanctum')->group(function () {
    Route::controller(PetugasController::class)->prefix('petugas')->group(function () {
        Route::get('', 'index');
        Route::post('tambah', 'tambah');
        Route::get('{user}', 'lihat');
        Route::put('ubah/{user}', 'ubah');
        Route::delete('hapus/{user}', 'hapus');
    });

    Route::controller(PelangganController::class)->prefix('pelanggan')->group(function () {
        Route::get('', 'index');
        Route::post('tambah', 'tambah');
        Route::get('{pelanggan:hp}', 'lihat');
        Route::put('ubah/{pelanggan}', 'ubah');
        Route::delete('hapus/{pelanggan}', 'hapus');
    });

    Route::controller(ReservasiController::class)->prefix('reservasi')->group(function () {
        Route::get('', 'index');
        Route::post('tambah', 'tambah');
        Route::get('{reservasi}', 'lihat');
        Route::put('ubah/{reservasi}', 'ubah');
        Route::delete('hapus/{reservasi}', 'hapus');
    });

    Route::controller(ChatController::class)->prefix('chat')->group(function () {
        Route::get('', 'index');
        Route::get('{hp}', 'show');
    });

    Route::post('/chat/{tujuan}', [WhatsappGatewayController::class, 'sendMessage'])->name('chat');

    Route::controller(TransaksiController::class)->prefix('transaksi')->group(function () {
        Route::get('', 'index');
        Route::get('{reservasi}', 'lihat');
        Route::post('{reservasi}', 'proses');
        Route::delete('{reservasi}', 'batal');
        Route::post('/bayar/{pembayaran}', 'bayar');
    });

    Route::get('/laporan', LaporanController::class);
});

Route::post('/whatsapp/webhook', [WhatsappGatewayController::class, 'webhook']);
