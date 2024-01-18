package net.kdt.pojavlaunch.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.kdt.pojavlaunch.R;

public class FragmentUtils {
    public static void replaceFragment(FragmentManager manager, Fragment destination, String transactionName, boolean save, boolean reverseAnim) {
        FragmentTransaction transaction = manager.beginTransaction();
        reverseAnim = !reverseAnim;
        transaction.setCustomAnimations(reverseAnim ? R.anim.slide_in_right : R.anim.slide_in_left,
                reverseAnim ? R.anim.slide_out_left : R.anim.slide_out_right,
                reverseAnim ? R.anim.slide_in_left : R.anim.slide_in_right,
                reverseAnim ? R.anim.slide_out_right : R.anim.slide_out_left);
        if(save) transaction.addToBackStack(transactionName);
        transaction.replace(R.id.fragmentContainerView, destination);
        transaction.setReorderingAllowed(true);
        transaction.commit();
    }
}
