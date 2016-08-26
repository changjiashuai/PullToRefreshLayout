package io.github.changjiashuai.pulltorefreshlayout;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.changjiashuai.pulltorefresh.R;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class StringAdapter extends RecyclerView.Adapter<StringAdapter.ViewHolder> {

    private List<String> mStrings;

    public StringAdapter(List<String> strings){
        mStrings = strings;
    }

    public void setStrings(List<String> strings) {
        mStrings = strings;
        notifyDataSetChanged();
    }

    public void insert(List<String> strings){
        int positionStart = mStrings.size();
        int itemCount = strings.size();
        mStrings.addAll(strings);
        notifyItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.string_item_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder itemViewHolder, int position) {

        itemViewHolder.mTvName.setText(mStrings.get(position));
    }

    @Override
    public int getItemCount() {

        return mStrings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTvName;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
