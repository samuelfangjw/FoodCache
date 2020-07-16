package com.breakfastseta.foodcache.inventory;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.breakfastseta.foodcache.App;

// For Foodcache Card
public class ViewPagerAdapter extends FragmentStateAdapter {
    private static int CARD_ITEM_SIZE = App.getTabs().size();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CardFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
    
}
