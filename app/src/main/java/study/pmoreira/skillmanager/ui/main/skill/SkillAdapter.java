package study.pmoreira.skillmanager.ui.main.skill;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.model.Skill;
import study.pmoreira.skillmanager.ui.AdapterFilter.OnFilter;

class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.ViewHolder> implements Filterable {

    private static final String INVALID_IMAGE = "-1";

    private Context mContext;
    private LayoutInflater mInflater;

    private SkillAdapterFilter mSkillFilter;

    private ItemClickListener mItemClickListener;

    private List<Skill> mSkills;

    interface ItemClickListener {
        void onItemClick(Skill skill, View v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View root;

        @BindView(R.id.skill_pic_imageview)
        ImageView mSkillPicImageView;

        @BindView(R.id.skill_name_textview)
        TextView mSkillNameTextView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            root = view;
        }
    }

    SkillAdapter(Context context, ItemClickListener clickListener, List<Skill> skills, View emptyView) {
        mContext = context;
        mItemClickListener = clickListener;
        mInflater = LayoutInflater.from(mContext);

        mSkills = skills;

        emptyView.setVisibility(skills.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.skill_cardview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Skill skill = mSkills.get(position);

        holder.mSkillNameTextView.setText(skill.getName());
        Glide.with(mContext)
                .load(TextUtils.isEmpty(skill.getPictureUrl()) ? INVALID_IMAGE : skill.getPictureUrl())
                .apply(new RequestOptions().error(R.drawable.skill_placeholder))
                .into(holder.mSkillPicImageView);

        holder.root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(skill, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSkills.size();
    }

    @Override
    public Filter getFilter() {
        if (mSkillFilter == null) {
            mSkillFilter = new SkillAdapterFilter(mSkills, new OnFilter<Skill>() {
                @Override
                public void publishResults(List<Skill> results) {
                    mSkills = results;
                    notifyDataSetChanged();
                }
            });
        }
        return mSkillFilter;
    }

    void filter(CharSequence constraint) {
        getFilter().filter(TextUtils.isEmpty(constraint) ? "" : String.valueOf(constraint).trim());
    }
}
