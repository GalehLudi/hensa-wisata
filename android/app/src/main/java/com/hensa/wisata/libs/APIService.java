package com.hensa.wisata.libs;

import com.hensa.wisata.models.Auth;
import com.hensa.wisata.models.Chat;
import com.hensa.wisata.models.Pelanggan;
import com.hensa.wisata.models.Petugas;
import com.hensa.wisata.models.Reservasi;
import com.hensa.wisata.models.ResetPassword;
import com.hensa.wisata.models.ResponseBody;
import com.hensa.wisata.models.Transaksi;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    @POST("login")
    Call<ResponseBody<Void>> login(@Body Auth user);

    @DELETE("logout")
    Call<ResponseBody<Void>> logout();

    @GET("user")
    Call<ResponseBody<Petugas>> user();

    @POST("user")
    Call<ResponseBody<Void>> storeUser(@Body Petugas petugas);

    @POST("password-reset")
    Call<ResponseBody<Void>> resetPassword(@Body ResetPassword resetPassword);

    @GET("petugas")
    Call<ResponseBody<List<Petugas>>> petugas();

    @POST("petugas/tambah")
    Call<ResponseBody<Void>> storePetugas(@Body Petugas petugas);

    @GET("petugas/{id}")
    Call<ResponseBody<Petugas>> getPetugas(@Path("id") Number id);

    @PUT("petugas/ubah/{id}")
    Call<ResponseBody<Void>> editPetugas(@Path("id") Number id, @Body Petugas petugas);

    @GET("pelanggan/{hp}")
    Call<ResponseBody<Pelanggan>> getPelanggan(@Path("hp") Number hp);

    @GET("chat")
    Call<ResponseBody<List<Chat>>> chat();

    @GET("chat/{hp}")
    Call<ResponseBody<List<Chat>>> userChat(@Path("hp") String hp);

    @POST("chat/{hp}")
    Call<ResponseBody<Chat>> storeChat(@Path("hp") String hp, @Body Chat chat);

    @POST("reservasi/tambah")
    Call<ResponseBody<Void>> storeReservasi(@Body Reservasi reservasi);

    @GET("transaksi")
    Call<ResponseBody<List<Transaksi>>> transaksi(@Query("filter") String filter);

    @GET("transaksi/{id}")
    Call<ResponseBody<Transaksi>> getTransaksi(@Path("id") Number id);

    @Multipart
    @POST("transaksi/{id}")
    Call<ResponseBody<Void>> prosesTransaksi(@Path("id") Number id, @PartMap Map<String, RequestBody> transaksi, @Part MultipartBody.Part tiket);

    @DELETE("transaksi/{id}")
    Call<ResponseBody<Void>> batalTransaksi(@Path("id") Number id);

    @POST("transaksi/bayar/{id}")
    Call<ResponseBody<Void>> bayarTransaksi(@Path("id") Number id, @Body String status);

    @GET("laporan")
    Call<okhttp3.ResponseBody> laporan(@Query("bulan") String bulan, @Query("tahun") String tahun);
}
