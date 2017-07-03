package internship.irina.com.recv;

/**
 * Created by Irina on 6/30/2017.
 */

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("place/nearbysearch/json")
    Call<PlacesPOJO.Root> doPlaces(@Query(value = "type", encoded = true) String type, @Query(value = "location", encoded = true) String location, @Query(value = "name", encoded = true) String name, @Query(value = "opennow", encoded = true) boolean opennow, @Query(value = "rankby", encoded = true) String rankby, @Query(value = "key", encoded = true) String key);

}
