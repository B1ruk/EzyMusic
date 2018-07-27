package io.starter.biruk.ezymusic.view.mainView;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.roughike.bottombar.BottomBar;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.model.dao.songDao.SongDao;
import io.starter.biruk.ezymusic.model.songFetcher.SongStorageUtil;
import io.starter.biruk.ezymusic.presenter.MainPresenter;
import io.starter.biruk.ezymusic.util.ViewTypeConstant;
import io.starter.biruk.ezymusic.view.albumsView.AlbumFragment;
import io.starter.biruk.ezymusic.view.artistsView.ArtistFragment;
import io.starter.biruk.ezymusic.view.miniView.MiniPlayerFragment;
import io.starter.biruk.ezymusic.view.playlistView.PlaylistFragment;
import io.starter.biruk.ezymusic.view.searchView.SearchLibraryActivity;
import io.starter.biruk.ezymusic.view.songsView.SongsFragment;

public class MainActivity extends AppCompatActivity implements MainView {

    private static final String TAG = "mainactivity";
    Toolbar mToolbar;
    BottomBar mBottomBar;

    FrameLayout miniPlayerContainer;
    LinearLayout rootLayout;

    SongsFragment songsFragment;
    ArtistFragment artistFragment;
    AlbumFragment albumFragment;
    MiniPlayerFragment miniPlayerFragment;
    PlaylistFragment playlistFragment;

    private MainPresenter mainPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        mBottomBar = (BottomBar) findViewById(R.id.bottom_bar);
        miniPlayerContainer = (FrameLayout) findViewById(R.id.mini_player);
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);

        initFragment();

        initBottomBar();
        initMiniPlayer();

        mainPresenter = new MainPresenter(new SongStorageUtil(this), this, new SongDao(this)
                , Schedulers.io(), AndroidSchedulers.mainThread());


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        permissionCheck();

    }

    public void permissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.INTERNET
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermis) {
                            if (multiplePermis.areAllPermissionsGranted()){
                                mainPresenter.fetchSongs();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        }
                    }).withErrorListener(dexterError -> {

            }).check();


        } else {
            mainPresenter.fetchSongs();
        }

    }

    private void initFragment() {
        songsFragment = SongsFragment.getInstance(ViewTypeConstant.DEFAULT_VIEW);
        miniPlayerFragment = new MiniPlayerFragment();
        albumFragment = AlbumFragment.getInstance(ViewTypeConstant.DEFAULT_VIEW);
        artistFragment = new ArtistFragment();
        playlistFragment = new PlaylistFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initMiniPlayer() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mini_player, miniPlayerFragment)
                .commit();
    }

    private void initBottomBar() {
        mBottomBar.setOnTabSelectListener(tabId -> {
            switch (tabId) {
                case R.id.bottom_nav_songs:
                    initFragment(songsFragment);
                    break;
                case R.id.bottom_nav_artists:
                    initFragment(artistFragment);
                    break;
                case R.id.bottom_nav_albums:
                    initFragment(albumFragment);
                    break;
                case R.id.bottom_nav_playlists:
                    initFragment(playlistFragment);
                    break;
            }
        });
    }

    private void initFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu, menu);

        MenuItem searchBar = menu.findItem(R.id.search);
        MenuItem themes = menu.findItem(R.id.themes);

        searchBar.setOnMenuItemClickListener(item -> launchSearchView());
        themes.setOnMenuItemClickListener(item -> launchThemesView());

        return super.onCreateOptionsMenu(menu);
    }

    private boolean launchSearchView() {
        Intent launchSearch = new Intent(this, SearchLibraryActivity.class);
        startActivity(launchSearch);
        return true;
    }

    private boolean launchThemesView() {
        return true;
    }


    @Override
    public void displaySongError() {
        Snackbar.make(rootLayout, "error loading songs", Snackbar.LENGTH_SHORT).show();
    }
}
