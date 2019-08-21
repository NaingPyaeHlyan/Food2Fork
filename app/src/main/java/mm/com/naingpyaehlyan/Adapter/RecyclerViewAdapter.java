package mm.com.naingpyaehlyan.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import mm.com.naingpyaehlyan.Activity.DetailActivity;
import mm.com.naingpyaehlyan.Helper.ConnectivityHelper;
import mm.com.naingpyaehlyan.Model.DataModel;
import mm.com.naingpyaehlyan.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public void setDataModelList(List<DataModel> dataModelList) {
        this.dataModelList = dataModelList;
    }
    public void appendDataModelList(List<DataModel> dataModelList){
        this.dataModelList.addAll(dataModelList);
    }

    public List<DataModel> getDataModelList() {
        return dataModelList;
    }

    private List<DataModel> dataModelList;
    private Context context;
//--------------------------------------------------------------------------------------------------
    public RecyclerViewAdapter(List<DataModel> dataModelList, Context context) {
        this.dataModelList = dataModelList;
        this.context = context;
    }
//--------------------------------------------------------------------------------------------------
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent, false);
        return new ViewHolder(view);
    }
//--------------------------------------------------------------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.txtTitle.setText((position + 1) + ". " +dataModelList.get(position).getTitle());
        holder.txtPublisher.setText(dataModelList.get(position).getPublisher());
        holder.txtRank.setText(dataModelList.get(position).getRank());
        Glide.with(context)
                .load(dataModelList.get(position).getImageUrl())
                .into(holder.imageView);

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityHelper.isConnectedToNetwork(context.getApplicationContext())){
                    String value_Id = dataModelList.get(position).getRecipe_id();
//                String value_TITLE = dataModelList.get(position).getTitle();
//                String value_PUBLISHER = dataModelList.get(position).getPublisher();
//                String value_IMG_URL = dataModelList.get(position).getImageUrl();

                    Intent intent = new Intent(context, DetailActivity.class);
                    Bundle extra = new Bundle();
                    extra.putString("RECIPE_ID", value_Id);
//-------------------------------------------------------------------------------------
//                extra.putString("RECIPE_TITLE",value_TITLE);
//                extra.putString("RECIPE_PUBLISHER",value_PUBLISHER);
//                extra.putString("RECIPE_IMG_URL",value_IMG_URL);
//-------------------------------------------------------------------------------------
                    intent.putExtras(extra);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else {
                    Toast.makeText(context.getApplicationContext(), "No Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
//--------------------------------------------------------------------------------------------------
    @Override
    public int getItemCount() {
        return dataModelList.size();
    }
//--------------------------------------------------------------------------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtPublisher, txtRank;
        ImageView imageView;
        CardView cardView;
        View rootView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rootView = itemView;
            // Main Activity
            txtTitle = (TextView)itemView.findViewById(R.id.txt_Title);
            txtPublisher = (TextView)itemView.findViewById(R.id.txt_Publisher);
            txtRank = (TextView)itemView.findViewById(R.id.txt_Rank);
            imageView = (ImageView)itemView.findViewById(R.id.my_Image_View);
            cardView = (CardView)itemView.findViewById(R.id.my_Card_View);
        }
    }
}
//--------------------------------------------------------------------------------------------------
