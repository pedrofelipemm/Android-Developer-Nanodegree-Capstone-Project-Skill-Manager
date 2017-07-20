package study.pmoreira.skillmanager.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.business.SkillBusiness;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.Skill;
import study.pmoreira.skillmanager.ui.main.SkillAdapter.ItemClickListener;
import study.pmoreira.skillmanager.ui.skill.SkillActivity;

public class SkillFragment extends Fragment {

    @BindView(R.id.skill_recyclerview)
    RecyclerView mSkillRecyclerView;

    @BindView(R.id.emptyview)
    View emptyView;

    private SkillBusiness mSkillBusiness;

    Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_skill_cardview_list, container, false);

        ButterKnife.bind(this, view);
        mSkillBusiness = new SkillBusiness();

        mSkillBusiness.findAll(new OnSkillLoad());

        return view;
    }

    //TODO: keep recycler position on device rotation
    //TODO: don't fetch on device roation

    private class OnSkillLoad extends OperationListener<List<Skill>> implements ItemClickListener {
        @Override
        public void onSuccess(List<Skill> skills) {
            int spanCount = mContext.getResources().getInteger(R.integer.main_skill_recycler_span_count);

            mSkillRecyclerView.setAdapter(new SkillAdapter(mContext, this, skills, emptyView));
            mSkillRecyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL));
        }

        @Override
        public void onItemClick(Skill skill) {
            SkillActivity.startActivity(mContext, skill);
        }
    }
}
