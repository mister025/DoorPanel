package tu.foxtrot.foxtrotdoorpanelmobileapp.network.interfacesApi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import tu.foxtrot.foxtrotdoorpanelmobileapp.network.models.Event;
import tu.foxtrot.foxtrotdoorpanelmobileapp.network.responseObjects.Worker;
import tu.foxtrot.foxtrotdoorpanelmobileapp.network.responseObjects.LoginResponse;

public interface WorkersAPI {

    @POST("workers")
    Call<Worker> createWorker(@Body Worker worker);

    @GET("workers/{id}")
    Call<Worker> getWorkerById(@Path("id") int workerId);

    @FormUrlEncoded
    @POST("workers/status")
    Call<String> updateWorkerStatus(@Header("Authorization") String token,
                                    @Field("status") String status);

    @FormUrlEncoded
    @POST("workers/room")
    Call<String> updateWorkerRoom(@Header("Authorization") String token,
                                  @Field("room") String room);

    @POST("workers/{id}/timeslot")
    Call<String> addWorkerTimeslot(@Path("id") int workerId, @Body Event timeslot);

    @FormUrlEncoded
    @POST("workers/login/")
    Call<LoginResponse> login(@Field("email") String email, @Field("password") String password);
}

