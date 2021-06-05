package harrisonbeerley.spotifysearch;

public class Artist {
    private String artistName;
    private String imgSource;
    private int followerCount;

    Artist(String artistName, String imgSource, int followerCount) {
        this.artistName = artistName;
        this.imgSource = imgSource;
        this.followerCount = followerCount;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getArtistImage() {
        return imgSource;
    }

    public int getFollowerCount() {
        return followerCount;
    }
}
