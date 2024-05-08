package com.gunnarro.android.terex.integration.breg;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.integration.breg.model.BregMapper;
import com.gunnarro.android.terex.integration.breg.model.Enhet;

import java.io.IOException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * https://api.skatteetaten.no/api/innkreving/restanser/v2/
 * <p>
 * https://api.skatteetaten.no/api/formuesgrunnlageiendomsskatt/v1/
 * <p>
 * sentral godkjenning
 * https://sgregister.dibk.no/api
 * curl https://sgregister.dibk.no/api/enterprises/123456789.json \
 * -H 'Accept: application/vnd.sgpub.v1'
 */
/**
 * https://api.skatteetaten.no/api/formuesgrunnlageiendomsskatt/v1/
 */
/**
 * sentral godkjenning
 * https://sgregister.dibk.no/api
 * curl https://sgregister.dibk.no/api/enterprises/123456789.json \
 *    -H 'Accept: application/vnd.sgpub.v1'
 */

/**
 * https://data.brreg.no/enhetsregisteret/api/docs/index.html#enheter-sok
 */
public class BregService {

    public static final ExecutorService serviceExecutor = Executors.newFixedThreadPool(2);
    private final OkHttpClient okHttpClient;

    private final String bregUrl = "https://data.brreg.no/enhetsregisteret/api/enheter/";

    public BregService() {
        //  int cacheSize = 10 * 1024 * 1024;
        //  File cacheDirectory = new File("src/test/resources/cache");
        //  Cache cache = new Cache(cacheDirectory, cacheSize);
        okHttpClient = new OkHttpClient.Builder()
                .followRedirects(false)
                //.cache(cache)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .addInterceptor(new DefaultContentTypeInterceptor("application/vnd.brreg.enhetsregisteret.enhet.v2+json;charset=UTF-8"))
                .build();
    }

    public OrganizationDto findOrganization(String organizationNumber) {
        try {
            Log.i("findOrganization", "find info for orgnr:" + organizationNumber);
            CompletionService<OrganizationDto> service = new ExecutorCompletionService<>(serviceExecutor);
            service.submit(() -> findOrganizationLocal(organizationNumber));
            Future<OrganizationDto> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error lookup org number!!", e.getMessage(), e.getCause());
        }
    }

    private OrganizationDto findOrganizationLocal(String organizationNumber) {
        // HttpUrl.Builder urlBuilder
        //         = HttpUrl.parse("https://data.brreg.no/enhetsregisteret/api/enheter/")
        //         .newBuilder();
        // urlBuilder.addQueryParameter("id", "1");

        //String url = urlBuilder.build().toString();

        // data.brreg.no: 195.43.63.68
        Request request = new Request.Builder()
                .url(bregUrl + organizationNumber)
                .build();

        try {
            Gson gson = new Gson();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();

            if (response.body() != null) {
                Enhet enhet = gson.fromJson(response.body().string(), Enhet.class);
                response.close();
                return BregMapper.toOrganizationDto(enhet);
            }
        } catch (Exception e) {
            // should throw an exception which tell that the remote call to breg failed.
            e.printStackTrace();
        }
        return null;
    }


    // fullmakt
    public void getPowerOfAttorney(String organizationNumber) throws IOException {
        //curl 'https://data.brreg.no/fullmakt/enheter/910336819/signatur' -i -X GET \
        //-H 'Accept: application/json;charset=UTF-8'

        Request request = new Request.Builder()
                .url("https://data.brreg.no/fullmakt/enheter/%s/signatur" + organizationNumber)
                .build();

        Gson gson = new Gson();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();

        if (response.body() != null) {
            Enhet enhet = gson.fromJson(response.body().string(), Enhet.class);
            response.close();
            //  return toOrganizationDto(enhet);
        }
        //return null;
    }


    static class DefaultContentTypeInterceptor implements Interceptor {

        private final String contentType;

        public DefaultContentTypeInterceptor(String contentType) {
            this.contentType = contentType;
        }

        @NonNull
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest
                    .newBuilder()
                    .header("Content-Type", contentType)
                    .header("Accept", contentType)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }
}
