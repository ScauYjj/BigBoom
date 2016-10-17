package com.whyalwaysmea.bigboom.module.moviedetail.ui;

import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whyalwaysmea.bigboom.R;
import com.whyalwaysmea.bigboom.base.BaseView;
import com.whyalwaysmea.bigboom.base.MvpActivity;
import com.whyalwaysmea.bigboom.bean.MovieDetail;
import com.whyalwaysmea.bigboom.imageloader.ImageUtils;
import com.whyalwaysmea.bigboom.module.moviedetail.presenter.MovieDetailPresenterImp;
import com.whyalwaysmea.bigboom.module.moviedetail.ui.adapter.CastAdapter;
import com.whyalwaysmea.bigboom.module.moviedetail.ui.adapter.MoviePhotoAdapter;
import com.whyalwaysmea.bigboom.module.moviedetail.view.IMovieDetailView;
import com.whyalwaysmea.bigboom.utils.MeasureUtil;
import com.whyalwaysmea.bigboom.utils.StatusBarUtil;
import com.whyalwaysmea.bigboom.view.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.whyalwaysmea.bigboom.R.id.toolbar;

public class MovieDetailActivity extends MvpActivity<IMovieDetailView, MovieDetailPresenterImp> implements IMovieDetailView {

    @BindView(R.id.movie_detail_bg)
    ImageView mMovieDetailBg;
    @BindView(toolbar)
    Toolbar mToolbar;
    @BindView(R.id.movie_detail_toolbarlayout)
    CollapsingToolbarLayout mMovieDetailToolbarlayout;
    @BindView(R.id.movie_detail_appbarlayout)
    AppBarLayout mMovieDetailAppbarlayout;
    @BindView(R.id.root_view)
    CoordinatorLayout mRootView;
    @BindView(R.id.progress)
    ContentLoadingProgressBar mProgress;
    @BindView(R.id.genres)
    TextView mGenres;
    @BindView(R.id.original_title)
    TextView mOriginalTitle;
    @BindView(R.id.durations)
    TextView mDurations;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.pubdates)
    TextView mPubdates;
    @BindView(R.id.average_rating)
    TextView mAverageRating;
    @BindView(R.id.ratingBar_hots)
    AppCompatRatingBar mRatingBarHots;
    @BindView(R.id.rating_nums)
    TextView mRatingNums;
    @BindView(R.id.rating_layout)
    LinearLayout mRatingLayout;
    @BindView(R.id.expand_text_view)
    ExpandableTextView mExpandTextView;
    @BindView(R.id.directors_recyclerview)
    RecyclerView mDirectorsRecyclerview;
    @BindView(R.id.photos_recyclerview)
    RecyclerView mPhotosRecyclerview;

    private int mX, mY;
    private String mId;

    private LinearLayoutManager mCastLayoutManager, mPhotoLayoutManager;
    private List<MovieDetail.CastsBean> mCastsBeanList;
    private CastAdapter mCastAdapter;
    private MoviePhotoAdapter mMoviePhotoAdapter;

    @Override
    protected MovieDetailPresenterImp createPresenter(BaseView view) {
        return new MovieDetailPresenterImp(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // 设置全屏，并且不会Activity的布局让出状态栏的空间
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        mPresenter = createPresenter(this);
        StatusBarUtil.setTransparent(this);
        initView();
        initData();
    }

    @Override
    protected void initData() {
        mPresenter.loadSubject(mId);
    }

    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // 设置Toolbar对顶部的距离
            final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mToolbar
                    .getLayoutParams();
            layoutParams.topMargin = MeasureUtil.getStatusBarHeight(this);
        }

        mToolbar.setNavigationIcon(AppCompatResources.getDrawable(this, R.drawable.ic_action_clear));


        mX = getIntent().getIntExtra("X", 0);
        mY = getIntent().getIntExtra("Y", 0);
        mId = getIntent().getStringExtra("ID");

        mRootView.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Animator animator = createRevealAnimator(false, mX, mY);
                    animator.start();
                }
            }
        });

        mCastLayoutManager = new LinearLayoutManager(this);
        mCastLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDirectorsRecyclerview.setLayoutManager(mCastLayoutManager);

        mPhotoLayoutManager = new LinearLayoutManager(this);
        mPhotoLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPhotosRecyclerview.setLayoutManager(mPhotoLayoutManager);
    }


    @Override
    public void setDetailData(MovieDetail detailData) {
        ImageUtils.getInstance().display(mMovieDetailBg, detailData.getImages().getLarge());
        mToolbar.setTitle(detailData.getTitle());
        StringBuffer sbGenres = new StringBuffer();
        for (int i = 0; i < detailData.getGenres().size(); i++) {
            if (i != detailData.getGenres().size() - 1) {
                sbGenres.append(detailData.getGenres().get(i) + "/");
            } else {
                sbGenres.append(detailData.getGenres().get(i));
            }
        }
        mGenres.setText(getString(R.string.genres) + sbGenres.toString());

        StringBuffer sbPubdates = new StringBuffer();
        for (int i = 0; i < detailData.getPubdates().size(); i++) {
            if (i != detailData.getPubdates().size() - 1) {
                sbPubdates.append(detailData.getPubdates().get(i) + "/");
            } else {
                sbPubdates.append(detailData.getPubdates().get(i));
            }
        }
        mPubdates.setText(getString(R.string.pubdates) + sbPubdates.toString());

        StringBuffer sbDurations = new StringBuffer();
        for (int i = 0; i < detailData.getDurations().size(); i++) {
            if (i != detailData.getDurations().size() - 1) {
                sbDurations.append(detailData.getDurations().get(i) + "/");
            } else {
                sbDurations.append(detailData.getDurations().get(i));
            }
        }
        mDurations.setText(getString(R.string.durations) + sbDurations.toString());
        mOriginalTitle.setText(getString(R.string.original_title) + detailData.getOriginal_title());

        mAverageRating.setText("" + detailData.getRating().getAverage());
        mRatingNums.setText("" + detailData.getRatings_count());
        mRatingBarHots.setRating(detailData.getRating().getAverage());
        mExpandTextView.setText(detailData.getSummary());

        mCastsBeanList = new ArrayList<>();
        mCastsBeanList.addAll(detailData.getDirectors());
        mCastsBeanList.addAll(detailData.getCasts());
        mCastAdapter = new CastAdapter(this, mCastsBeanList);
        mDirectorsRecyclerview.setAdapter(mCastAdapter);

        mMoviePhotoAdapter = new MoviePhotoAdapter(this, detailData.getPhotos());
        mPhotosRecyclerview.setAdapter(mMoviePhotoAdapter);
    }

    // 动画
    private Animator createRevealAnimator(boolean reversed, int x, int y) {
        float hypot = (float) Math.hypot(mRootView.getHeight(), mRootView.getWidth());
        float startRadius = reversed ? hypot : 0;
        float endRadius = reversed ? 0 : hypot;

        Animator animator = ViewAnimationUtils.createCircularReveal(
                mRootView, x, y,
                startRadius,
                endRadius);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (reversed)
            animator.addListener(animatorListener);
        return animator;
    }

    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mRootView.setVisibility(View.INVISIBLE);
            finish();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    @Override
    public void showLoading() {
        super.showLoading();
        mProgress.show();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        mProgress.hide();
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator animator = createRevealAnimator(true, mX, mY);
            animator.start();
        } else {
            finish();
        }
    }
}