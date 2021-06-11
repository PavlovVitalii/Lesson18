import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.*;

public class AsynchronousMain {

    public static void main(String[] args) {


        System.out.println("+++++++++++++++++++++++++++");
        new AsynchronousMain().run();
        System.out.println("---------------------------");


    }

    private void run() {

       final long t0 = System.nanoTime();

        OkHttpClient client = new OkHttpClient();

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
                .url("https://google.com")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (DataOutputStream out = new DataOutputStream(new FileOutputStream("Google.txt"))) {

                    String xmlPage = response.body().string();
                    System.out.println(xmlPage);
                    out.writeUTF(xmlPage);

                    final long t1 = System.nanoTime();
                    final double fullTime = t1 - t0;
                    System.out.println();
                    System.out.println(String.format("Full time: %.1fs",fullTime / 1_000_000_000));

                }
            }
        });



    }
}
