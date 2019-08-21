package mm.com.naingpyaehlyan.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import mm.com.naingpyaehlyan.Helper.ConnectivityHelper;
import mm.com.naingpyaehlyan.R;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {

    TextView txtTitle_Detail, txtIngredients_Detail, txtPublisher_url_Detail;
    ImageView imageView_Detail;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle("F 2 F");
        txtTitle_Detail = (TextView)findViewById(R.id.txt_Title_Detail);
        txtIngredients_Detail = (TextView)findViewById(R.id.txt_Ingredients_Detail);
        txtPublisher_url_Detail = (TextView)findViewById(R.id.txt_Publisher_Detail);
        imageView_Detail = (ImageView)findViewById(R.id.image_View_Detail);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        OkHttpTask okHttpTask = new OkHttpTask();
        okHttpTask.execute();
    }
//--------------------------------------------------------------------------------------------------
    public class OkHttpTask extends AsyncTask<String, String, String>{
        String get_url = "https://www.food2fork.com/api/get";
        OkHttpClient client = new OkHttpClient();

        //protected String Api_KEY = "f8ab694f60165e7d5005c8a9c622baa0";
        protected String Api_KEY = "1d0bf1be2a42f46e5ca58f2cc65bd29f";
        //protected String Api_KEY = "72e22ca2513818de89408543d35366e8";
//--------------------------------------------------------------------------------------------------
        @Override
        protected String doInBackground(String... strings) {
            Bundle bundle = getIntent().getExtras();
                String id = bundle.getString("RECIPE_ID");
                HttpUrl httpUrl = HttpUrl.parse(get_url).newBuilder()
                        .addQueryParameter("rId", id)
                        .addQueryParameter("key", Api_KEY)
                        .build();

                    Request request = new Request.Builder()
                            .url(httpUrl)
                            .build();

            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    return null;
                }
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
//--------------------------------------------------------------------------------------------------
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject recipe = jsonObject.getJSONObject("recipe");
                String title = recipe.getString("title");
                String publisher = recipe.getString("publisher_url");
                String img = recipe.getString("image_url");

                txtTitle_Detail.setText(title);
                txtPublisher_url_Detail.setText(publisher);
                Glide.with(DetailActivity.this)
                        .load(img)
                        .into(imageView_Detail);

                JSONArray ingredients = recipe.getJSONArray("ingredients");
                for (int i = 0; i < ingredients.length(); i++){
                    txtIngredients_Detail.append(ingredients.getString(i)+"\n");


                    //----------- Only One Output to TextView ------------------008577
                //    txtIngredients_Detail.setText(ingredients.getString(i)+"\n");
                }
                progressDialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
 /*
    //------------------------------- Use putExtras KEYWORD -------------------------------
    public void showData(){
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            //------------------ TITLE --------------------
            String title = extra.getString("RECIPE_TITLE");

            //------------------ PUBLISHER ----------------
            String publisher = extra.getString("RECIPE_PUBLISHER");

            //------------------ IMG URL ------------------
            String img = extra.getString("RECIPE_IMG_URL");

            txtTitle_Detail.setText(title);
            txtPublisher_url_Detail.setText(publisher);
            Glide.with(this)
                    .load(img)
                    .into(imageView_Detail);
            progressDialog.dismiss();
        }
    }*/
    //--------------------------------------------------------------------------------------
}
