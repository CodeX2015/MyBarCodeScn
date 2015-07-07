package com.android.app.mybarcodescn;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.app.mybarcodescn.adapters.StickyListHeaderAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.WeakHashMap;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by CodeX on 07.07.2015.
 */

public class ActivityConsumerBasket extends AppCompatActivity {
    private ExpandableStickyListHeadersListView mListView;
    private ArrayList<ProductDetails> products;
    StickyListHeaderAdapter mStickyListHeaderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        mListView = (ExpandableStickyListHeadersListView) findViewById(R.id.lvList);
        mListView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (mListView.isHeaderCollapsed(headerId)) {
                    //mListView.expand(headerId);
                } else {
                    //mListView.collapse(headerId);
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //click
            }
        });
        loadData();
    }

    private void loadData() {
        Utils.loadData(new Utils.LoadListener() {
            @Override
            public void OnLoadComplete(Object result) {
                products = (ArrayList<ProductDetails>) result;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (products != null) {
                            setAdapter();
                        } else {
                            Utils.EmptyMessage(ActivityConsumerBasket.this);
                        }
                    }
                });
            }

            @Override
            public void OnLoadError(final String error) {
                Log.d("FGLG_loadData ERR", error);
            }
        }, this);
    }

    private void setAdapter() {
        mListView.setAnimExecutor(new AnimationExecutor());
        mStickyListHeaderAdapter = new StickyListHeaderAdapter(this, products);
        mListView.setAdapter(mStickyListHeaderAdapter);
    }

    //animation executor
    class AnimationExecutor implements ExpandableStickyListHeadersListView.IAnimationExecutor {

        @Override
        public void executeAnim(final View target, final int animType) {
            WeakHashMap<View, Integer> mOriginalViewHeightPool = new WeakHashMap<View, Integer>();
            if (ExpandableStickyListHeadersListView.ANIMATION_EXPAND == animType && target.getVisibility() == View.VISIBLE) {
                return;
            }
            if (ExpandableStickyListHeadersListView.ANIMATION_COLLAPSE == animType && target.getVisibility() != View.VISIBLE) {
                return;
            }
            if (mOriginalViewHeightPool.get(target) == null) {
                mOriginalViewHeightPool.put(target, target.getHeight());
            }
            final int viewHeight = mOriginalViewHeightPool.get(target);
            float animStartY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? 0f : viewHeight;
            float animEndY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? viewHeight : 0f;
            final ViewGroup.LayoutParams lp = target.getLayoutParams();
            ValueAnimator animator = ValueAnimator.ofFloat(animStartY, animEndY);
            animator.setDuration(200);
            target.setVisibility(View.VISIBLE);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND) {
                        target.setVisibility(View.VISIBLE);
                    } else {
                        target.setVisibility(View.GONE);
                    }
                    target.getLayoutParams().height = viewHeight;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    lp.height = ((Float) valueAnimator.getAnimatedValue()).intValue();
                    target.setLayoutParams(lp);
                    target.requestLayout();
                }
            });
            animator.start();

        }
    }
}
