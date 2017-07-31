package study.pmoreira.skillmanager.ui.main.collaborator;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.business.CollaboratorBusiness;
import study.pmoreira.skillmanager.business.CollaboratorSkillBusiness;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.Collaborator;
import study.pmoreira.skillmanager.model.CollaboratorSkill;
import study.pmoreira.skillmanager.ui.SearchFilter;
import study.pmoreira.skillmanager.ui.SearchableFragment;
import study.pmoreira.skillmanager.ui.collaborator.CollaboratorActivity;
import study.pmoreira.skillmanager.ui.main.collaborator.CollaboratorAdapter.ItemClickListener;

public class CollaboratorFragment extends SearchableFragment implements SearchFilter {

    private static final String STATE_RV_POSITION = "STATE_RV_POSITION";
    private static final String STATE_COLLABORATORS = "STATE_COLLABORATORS";
    private static final String STATE_COLLABORATOR_SKILLS = "STATE_COLLABORATOR_SKILLS";

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.emptyview)
    View mEmptyView;

    CollaboratorAdapter mAdapter;

    Context mContext;
    List<Collaborator> mCollaborators;
    List<CollaboratorSkill> mCollaboratorSkills;


    ItemClickListener mItemClickListener = new ItemClickListener() {
        @Override
        public void onItemClick(Collaborator collab) {
            CollaboratorActivity.startActivity(mContext, collab);
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
        View view = inflater.inflate(R.layout.main_collaborator_rv, container, false);

        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            mCollaborators = savedInstanceState.getParcelableArrayList(STATE_COLLABORATORS);
            mCollaboratorSkills = savedInstanceState.getParcelableArrayList(STATE_COLLABORATOR_SKILLS);

            setQuery(savedInstanceState.getCharSequence(STATE_QUERY));

            setupRecyclerView();

            mRecyclerView.getLayoutManager().scrollToPosition(savedInstanceState.getInt(STATE_RV_POSITION));
        } else {
            new CollaboratorBusiness().findAll(new OnCollaboratorLoad());
            new CollaboratorSkillBusiness().findAll(new OnCollaboratorSkillsLoad());
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_COLLABORATORS, new ArrayList<>(mCollaborators));
        outState.putParcelableArrayList(STATE_COLLABORATOR_SKILLS, new ArrayList<>(mCollaboratorSkills));
        outState.putInt(STATE_RV_POSITION,
                ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());

    }

    @Override
    public void filter(CharSequence constraint) {
        if (mAdapter == null) return;

        mAdapter.filter(constraint);
        setQuery(constraint);
    }

    void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mAdapter = new CollaboratorAdapter(mContext, mItemClickListener, mCollaborators,
                mCollaboratorSkills, mEmptyView);
        mAdapter.filter(getQuery());
        mRecyclerView.setAdapter(mAdapter);
    }

    private class OnCollaboratorLoad extends OperationListener<List<Collaborator>> {

        @Override
        public void onSuccess(List<Collaborator> collaborators) {
            mCollaborators = collaborators;

            setupRecyclerView();
        }
    }

    private class OnCollaboratorSkillsLoad extends OperationListener<List<CollaboratorSkill>> {

        @Override
        public void onSuccess(List<CollaboratorSkill> collaboratorSkills) {
            mCollaboratorSkills = collaboratorSkills;

            setupRecyclerView();
        }
    }

}
