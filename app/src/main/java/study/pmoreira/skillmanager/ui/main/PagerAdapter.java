package study.pmoreira.skillmanager.ui.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.ui.SearchFilter;
import study.pmoreira.skillmanager.ui.main.collaborator.CollaboratorFragment;
import study.pmoreira.skillmanager.ui.main.skill.SkillFragment;

class PagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private SearchFilter mSearchFilter;

    PagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = new SkillFragment();
                break;
            case 1:
                fragment = new CollaboratorFragment();
                break;
            default:
                throw new IllegalArgumentException(mContext.getString(R.string.main_pager_invalid_position, position));
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return mContext.getResources().getInteger(R.integer.main_pager_adapter_count);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.main_pager_title_skills);
            case 1:
                return mContext.getString(R.string.main_pager_title_collaborators);
            default:
                throw new IllegalArgumentException(mContext.getString(R.string.main_pager_invalid_position, position));
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (object instanceof SearchFilter) {
            mSearchFilter = (SearchFilter) object;
        }
    }

    void filter(CharSequence constraint) {
        if (mSearchFilter == null) return;
        mSearchFilter.filter(constraint);
    }

}
