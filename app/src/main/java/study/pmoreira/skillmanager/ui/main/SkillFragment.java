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

import java.util.ArrayList;
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

    private static final String STATE_RV_POSITION = "STATE_RV_POSITION";
    private static final String STATE_SKILLS = "STATE_SKILLS";
    private static final String STATE_QUERY = "STATE_QUERY";

    @BindView(R.id.skill_recyclerview)
    RecyclerView mSkillRecyclerView;

    @BindView(R.id.emptyview)
    View mEmptyView;

    CharSequence mQuery;

    SkillAdapter mAdapter;

    Context mContext;
    List<Skill> mSkills;

    ItemClickListener mItemClickListener = new ItemClickListener() {
        @Override
        public void onItemClick(Skill skill) {
            SkillActivity.startActivity(mContext, skill);
        }
    };

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

        if (savedInstanceState != null) {
            mSkills = savedInstanceState.getParcelableArrayList(STATE_SKILLS);
            mQuery = savedInstanceState.getCharSequence(STATE_QUERY);

            setupRecyclerView();

            mSkillRecyclerView.getLayoutManager().scrollToPosition(savedInstanceState.getInt(STATE_RV_POSITION));
        } else {
            new SkillBusiness().findAll(new OnSkillLoad());
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(STATE_QUERY, mQuery);
        outState.putParcelableArrayList(STATE_SKILLS, new ArrayList<>(mSkills));
        outState.putInt(STATE_RV_POSITION,
                ((StaggeredGridLayoutManager) mSkillRecyclerView.getLayoutManager())
                        .findFirstCompletelyVisibleItemPositions(null)[0]);

    }

    @Override
    public void filter(CharSequence constraint) {
        mQuery = constraint;
        mAdapter.filter(constraint);
    }

    void setupRecyclerView() {
        mSkillRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        mContext.getResources().getInteger(R.integer.main_skill_recycler_span_count),
                        StaggeredGridLayoutManager.VERTICAL));

        mAdapter = new SkillAdapter(mContext, mItemClickListener, mSkills, mEmptyView);
        mAdapter.filter(mQuery);
        mSkillRecyclerView.setAdapter(mAdapter);
    }

    private class OnSkillLoad extends OperationListener<List<Skill>> {

        @Override
        public void onSuccess(List<Skill> skills) {
            mSkills = skills;

            setupRecyclerView();
        }
    }
}
