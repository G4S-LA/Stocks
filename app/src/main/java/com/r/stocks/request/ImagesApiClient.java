package com.r.stocks.request;

import android.util.Log;

import com.r.stocks.models.CompanyModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.r.stocks.request.MyService.IMAGE_FOLDER;
import static com.r.stocks.request.MyService.IMAGE_RESOLUTION;

public class ImagesApiClient {

    private static final String TAG = "ImageAPI";
    private static ImagesApiClient instance;

    public static ImagesApiClient getInstance() {
        if (instance == null) {
            instance = new ImagesApiClient();
        }
        return instance;
    }

    public void searchImages(final CompanyModel company) {
        final String file = IMAGE_FOLDER + company.getTicker() + IMAGE_RESOLUTION;
        final String ticker = company.getTicker();

        if (!haveImage(file)) {
            Log.v(TAG, "Ищу картинку для " + ticker);
            Call<ResponseBody> call = MyService.getImageApi().getImage(ticker);
            call.enqueue((new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Log.v(TAG, "Response of image - OK");
                        DownloadImage(response.body(), ticker);
                    } catch (Exception e) {
                        Log.v(TAG, "Image exception for company " + ticker);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.v(TAG, "Response of image - FAIL");
                }
            }));
        }
    }

    private boolean haveImage(String ticker) {
        File file = new File(ticker);
        return file.exists();
    }

    private void DownloadImage(ResponseBody body, String name) {
        Log.v(TAG, "Downloading Image");
        String file = IMAGE_FOLDER + name + IMAGE_RESOLUTION;
        try (InputStream in = body.byteStream();
             FileOutputStream out = new FileOutputStream(file)) {
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            Log.v(TAG, "Download image for " + name + " finished");
        } catch (IOException e) {
            Log.v(TAG, "Downloading Image - FAIL");
        }
    }

}
