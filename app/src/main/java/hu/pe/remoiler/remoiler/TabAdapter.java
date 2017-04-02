package hu.pe.remoiler.remoiler;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public TabAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    /**
     * Return the {@link Fragment} that should be displayed for the given page number.
     */
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            default:
            case 0:
                return new SwitchFragment();
            case 1:
                return new ScheduleFragment();
        }
    }

    /**
     * Return the total number of pages.
     */
    @Override
    public int getCount() {
        return 2;
    }

    /**
     * Return page title for given page number.
     */

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            default:
            case 0:
                return mContext.getString(R.string.fragment_switch);
            case 1:
                return mContext.getString(R.string.fragment_schedule);
        }
    }
}
