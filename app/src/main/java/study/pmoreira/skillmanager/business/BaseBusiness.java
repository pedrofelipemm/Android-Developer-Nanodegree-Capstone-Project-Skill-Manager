package study.pmoreira.skillmanager.business;

import android.content.Context;

abstract class BaseBusiness {

    final Context mContext;

    BaseBusiness(Context context) {
        mContext = context;
    }
}
