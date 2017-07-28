package study.pmoreira.skillmanager.ui.main.collaborator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.model.Collaborator;
import study.pmoreira.skillmanager.ui.AdapterFilter.OnFilter;

class CollaboratorAdapter extends RecyclerView.Adapter<CollaboratorAdapter.ViewHolder> implements Filterable {

    private static final String INVALID_IMAGE = "-1";

    private Context mContext;
    private LayoutInflater mInflater;

    private CollaboratorAdapterFilter mCollaboratorFilter;

    private ItemClickListener mItemClickListener;

    private List<Collaborator> mCollaborators;

    interface ItemClickListener {
        void onItemClick(Collaborator collab);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View root;

        @BindView(R.id.picture_imageview)
        ImageView mPictureImageView;

        @BindView(R.id.name_textview)
        TextView mNameTextView;

        @BindView(R.id.role_textview)
        TextView mRoleTextView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            root = view;
        }
    }

    CollaboratorAdapter(Context context, ItemClickListener clickListener, List<Collaborator> collab, View emptyView) {
        mContext = context;
        mItemClickListener = clickListener;
        mInflater = LayoutInflater.from(mContext);

        mCollaborators = collab;

        emptyView.setVisibility(collab.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.collaborator_rv_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Collaborator collab = mCollaborators.get(position);

        holder.mNameTextView.setText(collab.getName());
        holder.mRoleTextView.setText(collab.getRole());

        Picasso.with(mContext)
                .load(TextUtils.isEmpty(collab.getPictureUrl()) ? INVALID_IMAGE : collab.getPictureUrl())
                .error(R.drawable.collaborator_placeholder)
                .into(holder.mPictureImageView);

        holder.root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(collab);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCollaborators.size();
    }

    @Override
    public Filter getFilter() {
        if (mCollaboratorFilter == null) {
            mCollaboratorFilter = new CollaboratorAdapterFilter(mCollaborators, new OnFilter<Collaborator>() {
                @Override
                public void publishResults(List<Collaborator> results) {
                    mCollaborators = results;
                    notifyDataSetChanged();
                }
            });
        }
        return mCollaboratorFilter;
    }

    void filter(CharSequence constraint) {
        getFilter().filter(TextUtils.isEmpty(constraint) ? "" : String.valueOf(constraint).trim());
    }
}
