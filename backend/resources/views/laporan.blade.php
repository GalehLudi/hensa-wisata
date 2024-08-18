<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <title>Laporan</title>

    <style>
        html,
        body {
            margin: 0;
            padding: 0;
        }

        .page-break {
            page-break-after: always;
        }

        table {
            width: calc(100% - 50px);
            margin-left: 25px;
            margin-right: 25px;
            border-collapse: collapse;
        }

        .data th,
        .data td {
            padding: 5px;
            border: 1px solid black;
        }
    </style>
</head>

<body>
    <img src="{{ $kop }}" style="width: 100%;">

    <h3 style="text-align: center">Laporan Reservasi</h3>

    <table class="data">
        <thead style="font-size: 14px">
            <tr>
                <th>No</th>
                <th>Pelanggan</th>
                <th>Telepon</th>
                <th>Kode Reservasi</th>
                <th>Penerbangan</th>
                <th>Tanggal</th>
                <th>Harga</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody style="font-size: 12px">
            @foreach ($laporan as $item)
                <tr>
                    <th>{{ $loop->iteration }}</th>
                    <td>{{ $item->pelanggan->nama }}</td>
                    <td>{{ $item->pelanggan->hp }}</td>
                    <td>{{ $item->kode }}</td>
                    <td>{{ $item->penerbangan->berangkat . ' - ' . $item->penerbangan->tujuan }}</td>
                    <td style="text-align: center">{{ \Carbon\Carbon::parse($item->waktu)->format('d F Y') }}</td>
                    <td style="text-align: right">Rp.{{ $item->harga ?? 0 }}</td>
                    <td style="text-align: center">
                        {{ $item->harga ? ($item->harga - $item->pembayaran->sum('jumlah') === 0 ? 'Lunas' : 'Belum Lunas') : '-' }}
                    </td>
                </tr>
            @endforeach
        </tbody>
    </table>

    <br>
    <br>

    <table style="width: 200px; position: absolute; right: 0;">
        <tr>
            <td style="text-align: center; font-size: 14px">
                {{ \Carbon\Carbon::now()->format('d F Y') }}
            </td>
        </tr>
        <tr>
            <td style="text-align: center; font-size: 14px">
                Mengetahui,
                <br>
                <br>
                <br>
                <br>
                <br>
                <br>
            </td>
        </tr>
        <tr>
            <td style="border: 0px; border-top: 1px; border-style: solid; border-color: black;"></td>
        </tr>
    </table>
</body>

</html>
