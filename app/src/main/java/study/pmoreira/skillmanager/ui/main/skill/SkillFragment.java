package study.pmoreira.skillmanager.ui.main.skill;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
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
import study.pmoreira.skillmanager.ui.ClickableView;
import study.pmoreira.skillmanager.ui.SearchableFragment;
import study.pmoreira.skillmanager.ui.main.skill.SkillAdapter.ItemClickListener;
import study.pmoreira.skillmanager.ui.skill.EditSkillActivity;
import study.pmoreira.skillmanager.ui.skill.SkillActivity;

public class SkillFragment extends SearchableFragment implements ClickableView {

    private static final String STATE_RV_POSITION = "STATE_RV_POSITION";
    private static final String STATE_SKILLS = "STATE_SKILLS";

    @BindView(R.id.skill_recyclerview)
    RecyclerView mSkillRecyclerView;

    @BindView(R.id.emptyview)
    View mEmptyView;

    SkillAdapter mAdapter;

    Context mContext;
    List<Skill> mSkills;

    ItemClickListener mItemClickListener = new ItemClickListener() {
        @Override
        public void onItemClick(Skill skill, View v) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)
                    mContext, v, mContext.getString(R.string.transition_skill));

            SkillActivity.startActivity(mContext, skill, options);
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
            setQuery(savedInstanceState.getCharSequence(STATE_QUERY));

            setupRecyclerView();

            mSkillRecyclerView.getLayoutManager().scrollToPosition(savedInstanceState.getInt(STATE_RV_POSITION));
        } else {
            SkillBusiness.findAll(new OnSkillLoad());
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_SKILLS, new ArrayList<>(mSkills));
        outState.putInt(STATE_RV_POSITION,
                ((StaggeredGridLayoutManager) mSkillRecyclerView.getLayoutManager())
                        .findFirstCompletelyVisibleItemPositions(null)[0]);

    }

    @Override
    public void filter(CharSequence constraint) {
        if (mAdapter == null) return;

        mAdapter.filter(constraint);
        setQuery(constraint);
    }

    void setupRecyclerView() {
        mSkillRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        mContext.getResources().getInteger(R.integer.main_skill_recycler_span_count),
                        StaggeredGridLayoutManager.VERTICAL));

        mAdapter = new SkillAdapter(mContext, mItemClickListener, mSkills, mEmptyView);
        mAdapter.filter(getQuery());
        mSkillRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick() {
        EditSkillActivity.startActivity(mContext);
    }

    private class OnSkillLoad extends OperationListener<List<Skill>> {

        @Override
        public void onSuccess(List<Skill> skills) {
            mSkills = skills;

            setupRecyclerView();
        }
    }
}
