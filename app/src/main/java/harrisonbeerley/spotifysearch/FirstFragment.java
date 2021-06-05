package harrisonbeerley.spotifysearch;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import harrisonbeerley.spotifysearch.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {
    private HashMap<LinearLayout, Artist> artistButtons = new HashMap<>();
    Artist artistToDisplay = null;
    private FragmentFirstBinding binding;
    private static FirstFragment instance = null;
    EditText searchBar;
    TableLayout tl;
    String accessToken;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState

    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
@RequiresApi(api = Build.VERSION_CODES.O)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchBar = (EditText) view.findViewById(R.id.keywordSearch);
        tl = (TableLayout) view.findViewById(R.id.searchResultsView);
        instance = this;

        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

        String authUrl = "https://accounts.spotify.com/api/token?grant_type=client_credentials";
        String credentials = getString(R.string.credentials);
        String authEndpoint = "https://accounts.spotify.com/api/token?grant_type=client_credentials";
        String searchEndpoint = "https://api.spotify.com/v1/search/";
        String auth = "Basic " + new String(Base64.getEncoder().encode(credentials.getBytes()));


        JsonObjectRequest authRequest = new JsonObjectRequest(
            Request.Method.POST,
            authUrl,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        updateAuthToken(response.get("access_token").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("auth_json", "Error" + error.getMessage());
                }
            }
        ) {
            @Override
            public Map getHeaders()  throws AuthFailureError {
               Map headers = new HashMap();
               headers.put("Content-Type", "application/x-www-form-urlencoded");
               headers.put("Authorization", auth);
               return headers;
            }
        };
        requestQueue.add(authRequest);

        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                searchBar.onEditorAction(EditorInfo.IME_ACTION_DONE);
                removeOldItems();
                String searchText = searchBar.getText().toString();
                if (!searchText.equals("")) {
                    String parameters = "?q=" + searchText.replace(" ", "%20") + "&type=artist";
                    String searchUrl = searchEndpoint + parameters;
                    JsonObjectRequest searchRequest = new JsonObjectRequest(
                            Request.Method.GET,
                            searchUrl,
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        loadResults((JSONObject) response.get("artists"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("auth_json", "Error" + error.getMessage());
                                }
                            }
                    ) {
                        @Override
                        public Map getHeaders()  throws AuthFailureError {
                            Map headers = new HashMap();
                            headers.put("Authorization", "Bearer " + accessToken);
                            return headers;
                        }
                    };
                    requestQueue.add(searchRequest);
                }
            }
        });

    }

    private void removeOldItems() {
        if (artistButtons.size() != 0) {
            for (LinearLayout layout : artistButtons.keySet()) {
                ViewGroup parent = (ViewGroup) layout.getParent();
                parent.removeView(layout);
            }
        }
        artistButtons = new HashMap<>();
    }

    private void loadResults(JSONObject response) throws JSONException {
        LayoutInflater inflater = getLayoutInflater();
        JSONArray artists = response.getJSONArray("items");
        List<Artist> artistInfo = new ArrayList<>();
        for (int i = 0; i < artists.length(); i++) {
            JSONObject thisArtist = (JSONObject) artists.get(i);
            JSONArray imageOptions = thisArtist.getJSONArray("images");
            JSONObject image;
            if (imageOptions.length() == 0) {
                imageOptions.put(new JSONObject().put("url", new String("https://arfancosmetic.com/wp-content/uploads/2018/09/image-not-available.png")));
            }
            image = (JSONObject) imageOptions.get(0);

            JSONObject followers = (JSONObject) thisArtist.getJSONObject("followers");
            artistInfo.add(new Artist(thisArtist.get("name").toString(), image.get("url").toString(), (Integer) followers.get("total")));
        }
        for (Artist artist : artistInfo) {
            LinearLayout row = (LinearLayout) inflater.inflate(R.layout.tablerow, tl, false);
            TextView artistText = (TextView) row.findViewById(R.id.artistName);
            ImageView artistImage = (ImageView) row.findViewById(R.id.artistImage);
            artistText.setText(artist.getArtistName());
            Picasso.with(getView().getContext()).load(artist.getArtistImage()).into(artistImage);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    artistToDisplay = artistButtons.get(view);
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                }
            });
            artistButtons.put(row, artist);
            tl.addView(row);
        }

    }

    private void updateAuthToken(String access_token) {
        accessToken = access_token;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static FirstFragment getInstance() {
        return instance;
    }

    public Artist getArtistToDisplay() {
        return artistToDisplay;
    }

}