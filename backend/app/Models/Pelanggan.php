<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Pelanggan extends Model
{
    use HasFactory;

    protected $table = 'pelanggan';

    protected $fillable = [
        'nama',
        'hp'
    ];

    public function chat()
    {
        return $this->hasMany(Chat::class);
    }

    public function reservasi()
    {
        return $this->hasMany(Reservasi::class);
    }
}
