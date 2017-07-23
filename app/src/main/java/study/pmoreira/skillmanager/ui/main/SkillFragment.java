package study.pmoreira.skillmanager.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
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
import study.pmoreira.skillmanager.ui.SearchFilter;
import study.pmoreira.skillmanager.ui.main.SkillAdapter.ItemClickListener;
import study.pmoreira.skillmanager.ui.skill.SkillActivity;

public class SkillFragment extends Fragment implements SearchFilter {

    @BindView(R.id.skill_recyclerview)
    RecyclerView mSkillRecyclerView;

    @BindView(R.id.emptyview)
    View emptyView;

    CharSequence mQuery;

    SkillAdapter mAdapter;

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

        new SkillBusiness().findAll(new OnSkillLoad());

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        //TODO:
    }

    @Override
    public void filter(CharSequence constraint) {
        if (mAdapter == null) {
            mQuery = constraint;
        } else {
            mAdapter.filter(constraint);
        }
    }

    //TODO: keep recycler position on device rotation
    //TODO: don't fetch on device roation

    private class OnSkillLoad extends OperationListener<List<Skill>> implements ItemClickListener {

        @Override
        public void onSuccess(List<Skill> skills) {
            int spanCount = mContext.getResources().getInteger(R.integer.main_skill_recycler_span_count);

            mAdapter = new SkillAdapter(mContext, this, skills, emptyView);

            mSkillRecyclerView.setAdapter(mAdapter);
            mSkillRecyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL));

            if (!TextUtils.isEmpty(mQuery)) {
                mAdapter.filter(mQuery);
            }
        }

        @Override
        public void onItemClick(Skill skill) {
            SkillActivity.startActivity(mContext, skill);
        }
    }
}
