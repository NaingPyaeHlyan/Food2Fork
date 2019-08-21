package mm.com.naingpyaehlyan.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import mm.com.naingpyaehlyan.Adapter.RecyclerViewAdapter;
import mm.com.naingpyaehlyan.Helper.ConnectivityHelper;
import mm.com.naingpyaehlyan.Model.DataModel;
import mm.com.naingpyaehlyan.R;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    SearchView searchView;
    List<DataModel> dataModelList = new ArrayList<>(); //activity's data list
    String q = "";
    int page_int = 1;

    private Handler handler = new Handler();
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    boolean loading = false;
    int currentItem, totalItem, scrollOutItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        searchView = (SearchView) findViewById(R.id.my_Search_View);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.my_Recycler_View);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            SearchOkHttpTask searchOkHttpTask = new SearchOkHttpTask();
            q = getIntent().getStringExtra("q");
            if (q == null) q = "";
            searchOkHttpTask.execute(q);

            checkDataEveryMinute();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        loading = true;
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    currentItem = layoutManager.getChildCount();
                    totalItem = layoutManager.getItemCount();
                    scrollOutItem = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (loading && currentItem + scrollOutItem == totalItem) {
                        //fetch data
                        loading = false;
                        fetchData();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                private void fetchData() {

                    page_int += 1;
                    SearchOkHttpTask searchOkHttpTask = new SearchOkHttpTask();
                    searchOkHttpTask.execute(q);

            /*    new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page_int += 1;
                        SearchOkHttpTask searchOkHttpTask = new SearchOkHttpTask();
                        searchOkHttpTask.execute(q);
                    }
                }, 0);*/
                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);
                    doUpdate();
                }

                private void doUpdate() {
                    dataModelList.clear();
                    page_int = 1;
                    SearchOkHttpTask searchOkHttpTask = new SearchOkHttpTask();
                    searchOkHttpTask.execute(q);
                    Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    q = query;
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("q", q);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        } else {
            startActivity(new Intent(this, ErrorActivity.class));
            finish();
        }
    }

    public void checkDataEveryMinute(){
        handler.postDelayed(run, 10000);
    }
    Runnable run = new Runnable() {
        @Override
        public void run() {
            if (!ConnectivityHelper.isConnectedToNetwork(getApplicationContext())){
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),"No Connection", Snackbar.LENGTH_LONG);

                snackbar.setAction("Try again",new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        checkDataEveryMinute();
                    }
                });
                snackbar.show();
            }
        }
    };

    public class SearchOkHttpTask extends AsyncTask<String, String, List<DataModel>>{

        String base_url = "https://www.food2fork.com/api/search?";
        OkHttpClient client = new OkHttpClient();

        //protected String Api_KEY = "f8ab694f60165e7d5005c8a9c622baa0";
        protected String Api_KEY = "1d0bf1be2a42f46e5ca58f2cc65bd29f";
         //protected String Api_KEY ="72e22ca2513818de89408543d35366e8";
        @Override
        protected List<DataModel> doInBackground(String... strings) {

            String page  = Integer.toString(page_int);
            List<DataModel> responseList = new ArrayList<>();

                HttpUrl httpUrl = HttpUrl.parse(base_url).newBuilder()
                        .addQueryParameter("q", strings[0])
                        .addQueryParameter("key", Api_KEY)
                        .addQueryParameter("page", page)
                        .build();
                Request request = new Request.Builder()
                        .url(httpUrl)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String jsonStr = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray jsonArray = jsonObject.getJSONArray("recipes");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        DataModel dataModel = new DataModel(
                                object.getString("title"),
                                object.getString("publisher"),
                                object.getString("social_rank"),
                                object.getString("image_url"),
                                object.getString("recipe_id")
                        );
                        responseList.add(dataModel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return responseList;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(List<DataModel> dataModels) {

            super.onPostExecute(dataModels);
            if (adapter == null){
                adapter = new RecyclerViewAdapter(dataModels, getApplicationContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }
            else {

                //1 Pull to refresh 30 => set
                //2 endless scroll 30+ => append

                if(page_int == 1){
    //                set
                    adapter = new RecyclerViewAdapter(dataModels,getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    adapter.setDataModelList(dataModels); //pull to refresh
                    adapter.notifyDataSetChanged();
                }
                else{
 //                   append
                    adapter.getDataModelList().addAll(dataModels);
                    adapter.appendDataModelList(dataModelList); //endless scroll
                    adapter.notifyDataSetChanged();
                }   //MainActivity.this.dataModelList = adapter.getDataModelList();
            }
        }
    }
    public void onBackPressed(){
        moveTaskToBack(true);
        finish();
    }
}