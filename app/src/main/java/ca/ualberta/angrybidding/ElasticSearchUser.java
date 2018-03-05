package ca.ualberta.angrybidding;

import android.content.Context;
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

    private transient String id;
    private String passwordHash;

    public ElasticSearchUser(String id, String username, String passwordHash, String emailAddress) {
        super(username, emailAddress);
        this.id = id;
        this.passwordHash = passwordHash;
    }

    public String getID() {
        return this.id;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

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

    public interface GetUserListener {
        void onFound(ElasticSearchUser user);

        void onNotFound();

        void onError(VolleyError error);
    }

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

    public interface UserLoginListener {
        void onSuccess(ElasticSearchUser user);

        void onFailure();

        void onError(VolleyError error);
    }

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

            // Check Duplicate Username or EmailAddress
            SearchRequest searchRequest = new SearchRequest(ELASTIC_SEARCH_INDEX, query, new SearchResponseListener() {
                @Override
                public void onResult(SearchResult searchResult) {
                    if (searchResult.getHitCount() != 0) {
                        listener.onDuplicate();
                    } else {
                        // Add New User
                        AddRequest addRequest = new AddRequest(ELASTIC_SEARCH_INDEX, userJson, new AddResponseListener() {
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

    public interface UserSignUpListener {
        void onSuccess(ElasticSearchUser user);

        void onDuplicate();

        void onError(VolleyError error);
    }
}
