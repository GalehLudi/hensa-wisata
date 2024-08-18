<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Pembayaran extends Model
{
    use HasFactory;

    protected $table = 'pembayaran';

    protected $fillable = [
        'reservasi_id',
        'jumlah',
        'waktu',
        'bukti',
        'status'
    ];

    public function reservasi()
    {
        return $this->belongsTo(Reservasi::class);
    }
}
