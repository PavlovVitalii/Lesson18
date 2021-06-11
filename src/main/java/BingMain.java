import com.google.gson.Gson;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.*;

public class BingMain {

    private OkHttpClient client = new OkHttpClient();

    private long t0;

    public static void main(String[] args) {
        new BingMain().run();
    }

    private void run() {

        t0 = System.nanoTime();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            public void log(String message) {
                System.out.println(String.format(
                        "[%d]: %s", Thread.currentThread().getId(), message));
            }
        });

        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Request request = new Request.Builder()
                .get()
                .url("http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String json = response.body().string();
                Gson gson = new Gson();
                BingResponse bingResponse = gson.fromJson(json, BingResponse.class);
                BingImege bingImege = bingResponse.images.get(0);
                String fullUrl = "https://bing.com//" + bingImege.urlFromGson;
                imageLoad(fullUrl);

            }
        });


    }

    private void imageLoad(String url) {

        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        String image = request.url().queryParameter("id");
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleImageResponse(response, image);

            }
        });
    }

    private void handleImageResponse(Response response, String imageFile) throws IOException {

        InputStream inputStream = response.body().byteStream();

        try (OutputStream outputStream = new FileOutputStream(imageFile)) {

            while (true) {

                int readbyte = inputStream.read();

                if (readbyte == -1) {
                    break;
                }
                outputStream.write(readbyte);
            }
        }

        final long t1 = System.nanoTime();
        final double fullTime = t1 - t0;
        System.out.println();
        System.out.println(String.format("Full time: %.1fs", fullTime / 1_000_000_000));

    }
}
