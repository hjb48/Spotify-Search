package harrisonbeerley.spotifysearch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.squareup.picasso.Picasso;

import harrisonbeerley.spotifysearch.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {
    Artist artistToDisplay = null;
    TextView artistName;
    ImageView artistImage;
    TextView followerText;

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        artistName = (TextView) view.findViewById(R.id.artistText);
        artistImage = (ImageView) view.findViewById(R.id.artistDetailImage);
        followerText = (TextView) view.findViewById(R.id.followerText);
        artistToDisplay = FirstFragment.getInstance().getArtistToDisplay();

        artistName.setText(artistToDisplay.getArtistName());
        Picasso.with(getView().getContext()).load(artistToDisplay.getArtistImage()).into(artistImage);
        followerText.setText(String.valueOf(artistToDisplay.getFollowerCount()));

        binding.returnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //TODO: When Return button is clicked go back to the exact state of the first fragment when an artist was clicked

}