package ca.ualberta.angrybidding;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.slouple.util.Hash;

import org.json.JSONException;
import org.json.JSONObject;

import ca.ualberta.angrybidding.elasticsearch.AddRequest;
import ca.ualberta.angrybidding.elasticsearch.AddResponseListener;
import ca.ualberta.angrybidding.elasticsearch.SearchRequest;
import ca.ualberta.angrybidding.elasticsearch.SearchResponseListener;
import ca.ualberta.angrybidding.elasticsearch.SearchResult;
import ca.ualberta.angrybidding.elasticsearch.TermOrQuery;

public class ElasticSearchUser extends User {
    public static final String ELASTIC_SEARCH_INDEX = "user";
    public static final String HASH_ALGORITHM = Hash.SHA_512;

    public static final String PREFERENCE_NAME = "User";
    public static final String PREFERENCE_KEY = "Object";

    private transient String id;
    private String passwordHash;

    /**
     *
     * @param id ElasticSearch object id of the user
     * @param username Username of the user
     * @param passwordHash Password Hash generated for this user
     * @param emailAddress Email address of the user
     */
    public ElasticSearchUser(String id, String username, String passwordHash, String emailAddress) {
        super(username, emailAddress);
        this.id = id;
        this.passwordHash = passwordHash;
    }

    /**
     * @return ElasticSearch id
     */
    public String getID() {
        return this.id;
    }

    /**
     * @return Password hash of the user
     */
    public String getPasswordHash() {
        return this.passwordHash;
    }

    /**
     * Removes user from SharePreferences which is same as logging out.
     * @param context Context
     */
    public static void removeMainUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(PREFERENCE_KEY);
        editor.apply();
    }

    /**
     * Set a new logged in user
     * @param context Context
     * @param user User who logged in
     */
    public static void setMainUser(Context context, ElasticSearchUser user) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREFERENCE_KEY, new Gson().toJson(user));
        editor.apply();
    }

    /**
     * Get the logged in User
     * @param context Context
     * @return Currently logged in User
     */
    public static ElasticSearchUser getMainUser(Context context) {
        try {
            SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, 0);
            String userJson = settings.getString(PREFERENCE_KEY, null);
            if (userJson != null) {
                return new Gson().fromJson(userJson, ElasticSearchUser.class);
            } else {
                return null;
            }
        } catch (Throwable throwable) {
            Log.e("ElasticSearchUser", throwable.getMessage(), throwable);
            removeMainUser(context);
            return null;
        }
    }

    /**
     * Find full User object from username
     * @param context Context
     * @param username Username of the user
     * @param listener Listener to call when user is found
     */
    public static void getUserByUsername(final Context context, String username, final GetUserListener listener) {
        final String lowerUsername = username.toLowerCase().trim();

        TermOrQuery query = new TermOrQuery();
        query.addTerm("username", lowerUsername);

        SearchRequest searchRequest = new SearchRequest(ELASTIC_SEARCH_INDEX, query, new SearchResponseListener() {
            @Override
            public void onResult(SearchResult searchResult) {
                if (searchResult.getHitCount() > 0) {
                    SearchResult.SearchResultObject resultObject = searchResult.getSearchResultObjects().get(0);
                    JSONObject source = resultObject.getSource();
                    try {
                        ElasticSearchUser user = new ElasticSearchUser(resultObject.getId(), source.getString("username"),
                                source.getString("passwordHash"), source.getString("emailAddress"));
                        listener.onFound(user);
                    } catch (JSONException e) {
                        Log.e("ElasticSearchUser", e.getMessage(), e);
                    }
                } else {
                    listener.onNotFound();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });
        searchRequest.submit(context);
    }

    /**
     * Listener for searching for a single user
     */
    public interface GetUserListener {
        void onFound(ElasticSearchUser user);

        void onNotFound();

        void onError(VolleyError error);
    }

    /**
     * Login using username and password
     * @param context Context
     * @param username Username of the user
     * @param password Password of the user
     * @param listener Listener to call on response
     */
    public static void login(Context context, String username, String password, final UserLoginListener listener) {
        final String lowerUsername = username.toLowerCase().trim();
        final String lowerPassword = password.toLowerCase().trim();
        final String passwordHash = Hash.getHash(lowerPassword.getBytes(), HASH_ALGORITHM);
        getUserByUsername(context, lowerUsername, new GetUserListener() {
            @Override
            public void onFound(ElasticSearchUser user) {
                //Compare Password Hashes
                if (user.getPasswordHash().equals(passwordHash)) {
                    listener.onSuccess(user);
                } else {
                    listener.onFailure();
                }
            }

            @Override
            public void onNotFound() {
                listener.onFailure();
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(error);
            }
        });
    }

    /**
     * Listener for User Login
     */
    public interface UserLoginListener {
        /**
         * User has successfully logged in
         * @param user User object of the successfully logged in user
         */
        void onSuccess(ElasticSearchUser user);

        /**
         * Login failed due to incorrect username or password
         */
        void onFailure();

        /**
         * On other error such as network
         * @param error
         */
        void onError(VolleyError error);
    }

    /**
     * Sign Up using username, password and email address
     * Username and email address will be checked for uniqueness
     * @param context Context
     * @param username Username of the user
     * @param password Password of the user
     * @param emailAddress Email address of the user
     * @param listener Listener to call on response
     */
    public static void signUp(final Context context, String username, String password, String emailAddress, final UserSignUpListener listener) {
        try {
            final String lowerUsername = username.toLowerCase().trim();
            final String lowerPassword = password.toLowerCase().trim();
            final String lowerEmailAddress = emailAddress.toLowerCase().trim();

            final String passwordHash = Hash.getHash(lowerPassword.getBytes(), HASH_ALGORITHM);

            final ElasticSearchUser user = new ElasticSearchUser(null, lowerUsername, passwordHash, lowerEmailAddress);
            final JSONObject userJson = new JSONObject(new Gson().toJson(user));

            TermOrQuery query = new TermOrQuery();
            query.addTerm("username", lowerUsername);
            query.addTerm("emailAddress", lowerEmailAddress);

            // Add New User
            final AddRequest addRequest = new AddRequest(ELASTIC_SEARCH_INDEX, userJson, new AddResponseListener() {
                @Override
                public void onCreated(String id) {
                    ElasticSearchUser user = new ElasticSearchUser(id, lowerUsername, passwordHash, lowerEmailAddress);
                    listener.onSuccess(user);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onError(error);
                }
            });

            // Check Duplicate Username or EmailAddress
            SearchRequest searchRequest = new SearchRequest(ELASTIC_SEARCH_INDEX, query, new SearchResponseListener() {
                @Override
                public void onResult(SearchResult searchResult) {
                    if (searchResult.getHitCount() != 0) {
                        listener.onDuplicate();
                    } else {
                        addRequest.submit(context);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onError(error);
                }
            });
            searchRequest.submit(context);
        } catch (JSONException e) {
            listener.onError(new VolleyError(e));
        }
    }

    /**
     * Listener for Sign Up
     */
    public interface UserSignUpListener {
        /**
         * User has successfully signed up
         * @param user User object of the successfully sign up user
         */
        void onSuccess(ElasticSearchUser user);

        /**
         * Username or email address is taken
         */
        void onDuplicate();

        /**
         * Other errors such as network
         * @param error
         */
        void onError(VolleyError error);
    }
}
