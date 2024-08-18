<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Penerbangan extends Model
{
    use HasFactory;

    protected $table = 'penerbangan';

    protected $fillable = [
        'reservasi_id',
        'kode',
        'berangkat',
        'tujuan',
        'waktu_berangkat',
        'waktu_tiba'
    ];

    public function reservasi()
    {
        return $this->belongsTo(Reservasi::class);
    }
}
