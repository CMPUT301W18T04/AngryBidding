package ca.ualberta.angrybidding;

import android.app.Application;

import com.slouple.android.BitmapLoader;
import com.slouple.android.BitmapLoaderFactory;
import com.slouple.android.widget.image.ImageSlider;

import java.io.File;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ca.ualberta.angrybidding.map.MapView;

public class AngryBiddingApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BitmapLoaderFactory.addBitmapLoader(MapView.DEFAULT_BITMAP_LOADER_NAME,
                new BitmapLoader(8, true, 128 * 1024 * 1024, new File(getCacheDir(), MapView.DEFAULT_BITMAP_LOADER_NAME), false, -1, 10000)
        );

        BitmapLoaderFactory.addBitmapLoader(ImageSlider.DEFAULT_BITMAP_LOADER_NAME,
                new BitmapLoader(8, false, 128 * 1024 * 1024, new File(getCacheDir(), ImageSlider.DEFAULT_BITMAP_LOADER_NAME), false, -1, 000));

        if (BuildConfig.DEBUG) {
            try {
                TrustManager[] victimizedManager = new TrustManager[]{

                        new X509TrustManager() {

                            public X509Certificate[] getAcceptedIssuers() {

                                X509Certificate[] myTrustedAnchors = new X509Certificate[0];

                                return myTrustedAnchors;
                            }

                            @Override
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            }
                        }
                };

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, victimizedManager, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
