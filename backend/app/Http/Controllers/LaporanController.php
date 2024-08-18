<?php

namespace App\Http\Controllers;

use App\Models\Reservasi;
use Barryvdh\DomPDF\Facade\Pdf;
use Illuminate\Http\Request;

class LaporanController extends Controller
{
    public function __invoke(Request $request)
    {
        $kop = "data:image/jpg;base64," . base64_encode(file_get_contents(public_path('image/kop.jpg')));

        $laporan = new Reservasi();
        if ($request->query('bulan')) {
            $laporan = $laporan->whereMonth('waktu', $request->query('bulan'));
        }

        if ($request->query('tahun')) {
            $laporan = $laporan->whereYear('waktu', $request->query('tahun'));
        }

        $laporan = $laporan->get();

        $pdf = Pdf::loadView('laporan', compact('kop', 'laporan'));
        return $pdf->download();
    }
}
