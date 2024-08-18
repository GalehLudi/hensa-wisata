<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Reservasi extends Model
{
    use HasFactory;

    protected $table = 'reservasi';

    protected $fillable = [
        'kode',
        'pelanggan_id',
        'waktu',
        'status',
        'harga'
    ];

    public function pelanggan()
    {
        return $this->belongsTo(Pelanggan::class);
    }

    public function penerbangan()
    {
        return $this->hasOne(Penerbangan::class);
    }

    public function penumpang()
    {
        return $this->hasMany(Penumpang::class);
    }

    public function pembayaran()
    {
        return $this->hasMany(Pembayaran::class);
    }
}
