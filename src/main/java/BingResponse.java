import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BingResponse {

    @SerializedName("images")
    public List<BingImege> images;
}
