package study.pmoreira.skillmanager.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.model.Skill;


class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.ViewHolder> {

    private static final String INVALID_IMAGE = "-1";

    private Context mContext;
    private LayoutInflater mInflater;

    private List<Skill> mSkills;

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.skill_pic_imageview)
        ImageView mSkillPicImageView;

        @BindView(R.id.skill_name_textview)
        TextView mSkillNameTextView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    SkillAdapter(Context context, List<Skill> skills, View emptyView) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);

        mSkills = skills;

        emptyView.setVisibility(skills.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.main_skill_cardview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Skill skill = mSkills.get(position);

        holder.mSkillNameTextView.setText(skill.getName());
        Picasso.with(mContext)
                .load(TextUtils.isEmpty(skill.getPictureUrl()) ? INVALID_IMAGE : skill.getPictureUrl())
                .error(R.drawable.skill_placeholder)
                .into(holder.mSkillPicImageView);
    }

    @Override
    public int getItemCount() {
        return mSkills.size();
    }

}