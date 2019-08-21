package mm.com.naingpyaehlyan.Model;

public class DataModel {
    String title, publisher, rank, imageUrl, recipe_id;

    public DataModel(String title, String publisher, String rank, String imageUrl, String recipe_id) {
        this.title = title;
        this.publisher = publisher;
        this.rank = rank;
        this.imageUrl = imageUrl;
        this.recipe_id = recipe_id;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getRank() {
        return rank;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRecipe_id() {
        return recipe_id;
    }
}
