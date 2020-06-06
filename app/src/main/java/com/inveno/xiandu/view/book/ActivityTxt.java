package com.inveno.xiandu.view.book;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SharedElementCallback;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.text.ClipboardManager;
import android.text.Html;
import android.text.InputType;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils.TruncateAt;
import android.text.format.Formatter;
import android.text.format.Time;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.inveno.xiandu.R;
import com.inveno.xiandu.view.BaseActivity;
import com.inveno.xiandu.view.book.A;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipOutputStream;

public class ActivityTxt extends BaseActivity implements Runnable, OnTouchListener, android.view.View.OnClickListener, OnLongClickListener {

    public static ActivityTxt selfPref;
    boolean hasActTrans, hasActTransExtra, actTransArrive, actTransEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreateTime = System.currentTimeMillis();
        checkLollipopFullScreenState();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        selfPref = this;
        if (Build.VERSION.SDK_INT >= 11) {
            getWindow().addFlags(0x01000000);// WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            if (Build.VERSION.SDK_INT >= 17) //v3.2.1 android 4.0.4 has slow matter, so set to android 4.2+ (code at ScrollView2.java)
                A.fitHardwareAccelerated = true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            A.disableTransOverlap(this);
            getWindow().setNavigationBarColor(0xff000000);
            hasActTransExtra = getIntent().getBooleanExtra("hasActTrans", false);
            if (A.openBookAnim)
                hasActTrans = hasActTransExtra;
            setEnterSharedElementCallback(new SharedElementCallback() {
                public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                    actTransEnd = true;
                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                }

                public void onSharedElementsArrived(List<String> sharedElementNames, List<View> sharedElements, OnSharedElementsReadyListener listener) {
                    hasActTrans = true;
                    actTransArrive = true;
                    super.onSharedElementsArrived(sharedElementNames, sharedElements, listener);
                }
            });
        }

        setContentView(R.layout.show_txt);
        initExtras(true);
        initView();

//		if (restartWhenLowMemory(30, false, 1200)) // do this in loadFile()?
//			return;

        if (getIntent().getExtras() == null || !getIntent().getExtras().getBoolean("noad"))
            handler.sendMessage(handler.obtainMessage(IF_SPLASH_AD, 1, 0));
        else
            ActivityMain.openBookView = null;

        if (Build.VERSION.SDK_INT >= 28) {
            getWindow().getDecorView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                public void onViewAttachedToWindow(View v) {
                    A.checkCutoutScreen(ActivityTxt.this);
                }

                public void onViewDetachedFromWindow(View v) {
                }
            });
        }

        new Thread(this).start();
        AndroidOreoPlaySilence();
    }

    private void AndroidOreoPlaySilence() { // play sound to enbable headset button event
        if (Build.VERSION.SDK_INT < 26)
            return;
        if (A.doHeadsetKey != A.DO_SPEAK)
            return;
        A.playSound(this, R.raw.silence);
    }

    public boolean closeOldReader(Bundle savedInstanceState) {
        if (T.isOutOfMemoryError) {
            super.onCreate(savedInstanceState);
            setContentView(new View(this));
            delaySystemExit(1200);
            A.log("==========>>>RestartApp - txt");
            return true;
        }

        if (!T.isNull(selfPref))
            try {
                A.log("--------->>>Prior ActivityTxt isn't null, close it");
                if (!selfPref.isPaused && selfPref.txtView != null) {
                    selfPref.saveLastPostion(true, true);
                    A.log("==========>>>Save Prior TXT");
                }
                if (A.isInAutoScroll)
                    selfPref.do_AutoScroll(false, false);
                if (A.isSpeaking)
                    selfPref.stop_speak(false);
                A.isSpeaking = false;
                A.isInAutoScroll = false;
                selfPref.finish();
                selfPref = null;
            } catch (Exception e) {
                A.error(e);
            }
        return false;
    }

    long multiWindowTime;
    boolean beforMultiWindowIsLandscape;

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        A.log("-txt:onMultiWindowModeChanged:" + isInMultiWindowMode + ", isPaused:" + isPaused + ", landscape:" + A.isLandscape());
        multiWindowTime = SystemClock.elapsedRealtime();
        beforMultiWindowIsLandscape = A.isLandscape();
        resetFlipCache();
        forceNoCurlCache = true;

        if (A.isLandscape() && !isFinishing()) {
            A.log("----------------multi window state changed, restart txt----------");
            restartReaderIntent();
        }
    }

    public void restartReaderIntent() {
        ActivityMain.openBookView = null;
        A.log("--------------restartReaderIntent-----------");
        handler.removeCallbacksAndMessages(null);
        Intent i = new Intent(this, ActivityTxt.class);
        i.putExtra("bookFile", A.lastFile);
        startActivity(i);
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        A.screenChangedTime = System.currentTimeMillis();
        actTransEnd = true;
        A.log("-txt:onConfigurationChanged, isPaused:" + isPaused + ", landscape:" + A.isLandscape());
        A.getScreenRealHeight(this);
        A.baseFameHeight = 0;

        if (SystemClock.elapsedRealtime() - multiWindowTime < 2000 && !isFinishing()
                && (beforMultiWindowIsLandscape || A.isLandscape())) {
            A.log("===============multi window state changed, restart txt============");
            restartReaderIntent();
            ;
            return;
        }

        if (isPaused) { //v1.7 landscape mode: shutdown screen -> onPause -> onCongig(!here) //-> light screen -> onResume -> onConfig
            if (isAutoRotate() && isTxtScrollReady) {
                A.log("******AUTO-SENSOR onConfigurationChanged in background******", true);
                reloadBookOnError();
            }
            return;
        }

        if (isAutoRotate()) { //v4.5
            if (pdf != null)
                restartReaderToTxt();
            else
                restartReaderIntent();
            return;
        }

        try {
            cachedSplitsPageCount = cachedSplitsPageNum = null;
            resetPageCount();
            checkhBarWidth = false;
            bookBackgroundStyle = -1;
            pdfSetImmersiveMode(false);
            A.onConfigurationChangedTime = SystemClock.elapsedRealtime();

            checkDualPageMode();
            checkLandscape2PagePadding();
            if (isTxtScrollReady) {
                if (isPressScreenStateButton || justEnabledDualPage) {
                    isPressScreenStateButton = justEnabledDualPage = false;
                    handler.removeCallbacksAndMessages(null);
                    if (dualPageEnabled()) {
                        new Handler() {
                            public void handleMessage(Message msg) {
                                isTxtScrollReady = false;
                                handler.postDelayed(ActivityTxt.this, 10);
                            }
                        }.sendEmptyMessageDelayed(0, 100);
                    } else
                        handler.postDelayed(this, 10);
                } else {
                    if (isAutoRotate()) { //auto-sensor
                        if (resumeTime != -1 && System.currentTimeMillis() - resumeTime > 1000)
                            if (isBigChapter(A.lastChapter))
                                createProgressDlg(A.ebook.getChapters().get(A.lastChapter).name);
                        resetFlipCache();
                        isTxtScrollReady = false;
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(this, 10);
                    }
                    statusHandlerSendMessage(1, 150);
                }
            }

            if (A.isInAutoScroll)
                do_AutoScroll(false);
            if (isPdf() && pdf != null) {
                pdfRemoveThumb();
                pdfSetProperties(true);
            }
            if (curl3d != null)
                initCurl3d(true);
            setFlipViewINVISIBLE(true);
        } catch (Exception e) {
            A.error(e);
        }

        if (A.fitCutout == 1 && A.isCutoutScreen())
            statusHandlerSendMessage(1, 100);
    }

    private void reloadBookOnError() {
        checkDualPageMode();
        resetFlipCache();
        isTxtScrollReady = false;
        handler.postDelayed(this, 0);
        statusHandlerSendMessage(1, 100);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        A.log("-txt:onRestoreInstanceState");
        try {
            super.onRestoreInstanceState(savedInstanceState);
        } catch (Exception e) {
            A.error(e);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        A.log("-txt:onSaveInstanceState");
        try { // java.lang.String cannot be cast to java.lang.Object[]
            super.onSaveInstanceState(outState);
        } catch (Exception e) {
            A.error(e);
        }
    }

    public long resumeTime, onCreateTime;
    public boolean isPaused, userAction;

    @Override
    protected void onResume() {
        boolean ok = false;
        try {
            super.onResume();
            A.log("-txt:resume, isPaused:" + isPaused);
            selfPref = this;
            userAction = false;
            resumeTime = System.currentTimeMillis();
            tastReadRecord = System.currentTimeMillis();
            activeTime = statisticsTime = System.currentTimeMillis();
            A.invokeRemind1Time = SystemClock.elapsedRealtime();
            if (isPdf() && pdf != null)
                pdf.isOnPaused = false;
            isPressScreenStateButton = false;
            A.getScreenRealHeight(this);
            if (A.adjustBrightness) {
                setScreenBrightness(A.brightnessValue, false);
                setLeds();
            }
            if (shakeSensorLisener != null) {
                shakeSensorLisener.tilt_hold_count = 0;
                shakeSensorLisener.tilt_base_z = 10000;
            }
            registerHardwares();
            if (showStatusbar() && isTxtScrollReady) {
                updateBarTime();
            }
            if (A.passwordCheckFailed)
                terminate_reader();

            if (A.isSpeaking) {
                if (tts_screen_off_pos != -1)
                    txtScrollByDelay(tts_screen_off_pos);
                else if (txtView.hStart != -1)
                    txtScrollByDelay(txtView.hStart);
                updateProgressStatus();
            }

            A.setSystemUiVisibility(true);
            setScreenAwakeTime = 0;
            checkScreenAwake();
            pdfSetImmersiveMode(false);

            setAutoTheme();
            if (isPaused && txtView != null) {
                boolean txtBroken = txtViewTextBroken();
                if (txtView.getLayout() == null || txtBroken) {
                    if (!A.canShowAds()) {
                        A.log("******txtView.getLayout()==null or broken******" + txtView.getLayout() + ", broken:" + txtBroken + ", lastPosition:" + A.lastPosition, true);
                    }
                    isPaused = false;
                    clearTxtView();
                    resetFlipCache();
                    if (getBookType() == A.FILE_TXT)
                        A.getTxts().clear();
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(this, txtBroken ? 500 : 250);
                }
            }
            handler.sendEmptyMessageDelayed(SEARCH_CLIPBOARD_BOOK, 500);
            isPaused = false;
            ok = true;
        } catch (Exception e) {
            A.error(e);
        }

        if (baseFrame != null && baseFrame.getHeight() > 0)
            A.baseFameHeight = baseFrame.getHeight();
        if (!ok)
            restartReaderToMain();
        handler.sendMessage(handler.obtainMessage(IF_SPLASH_AD, 0, 0));
    }

    private void ifShowSplashAd(boolean directShow) {
        if (inExiting || isFinishing())
            return;
        if (A.newVersionRun)
            return;
        if (directShow) {
            ActivityMain.showSplashAd2(this, 127, true, true);
        } else {
            long pauseElapsed = pausedTime > 0 ? System.currentTimeMillis() - pausedTime : 0;
            int add = SplashActivity.count > 0 ? 1 : 0;
            if (pauseElapsed > T.minute(60)) // 120 -> 60
                ActivityMain.showSplashAd2(this, -1, true, false);
            else if (pauseElapsed > T.minute(30) && T.myRandom(2 + add) == 0) // 60 -> 30
                ActivityMain.showSplashAd2(this, -1, true, false);
            else if (pauseElapsed > T.minute(3) &&  // 5 -> 3
                    System.currentTimeMillis() - A.realSplashAdTime > T.minute(20) && // 30 - 20
                    T.myRandom(3 * (add + 1)) == 0) // 5 -> 3
                ActivityMain.showSplashAd2(this, -1, true, false);
            else if (reminderJustShowed()) {
                A.trySplashAdTime = 0;
                if (ActivityMain.showSplashAd2(this, -1, true, false)) {
                    remind1StartTime = 0;
                    A.trackEvent("splashTry", "fromReminderReturn", A.getAppVersion(), 1);
                }
            }
        }
        ActivityMain.openBookView = null;
    }

    public long pausedTime;

    @Override
    protected void onPause() {//注意: 横屏再自动竖屏的时候onPause()被执行了两次,最后一次txtView.getLayout()变成了空值! //z
        super.onPause();
        isPaused = true;
        pausedTime = System.currentTimeMillis();
        A.lastReadTime = System.currentTimeMillis();
        isPressScreenStateButton = false;
        A.saveMemoryLog("-txt:pause");

        try {
            saveLastPostion(true);
            if (A.doShakePhone != A.DO_SPEAK || !A.isSpeaking)
                unregisterShakeSensor();
            saveStatisticsInfo();
            if (A.isInAutoScroll)
                do_AutoScroll(false);
            if (isOnExiting) {
                A.forceRebootToMain = true;
                T.deleteFile(A.txtPauseOnlyTagFile);
            } else
                A.forceRebootToMain = false;
            A.forceRebootToTxt = false;
            A.SaveOptions(this);
            show_tts_icon();
            saveTxtFileCache();

            if (isPdf() && pdf != null) {
                pdf.isOnPaused = true;
                if (A.pdfAnnotUpdated) {
                    A.log("****SAVE PDF DOCUMENT****");
                    A.pdfAnnotUpdated = false;
                    m_doc.Save();
                    pdfLastSaveTime = SystemClock.elapsedRealtime();
                } else
                    pdfCheckBookcover();
            }
            if (isOnExiting)
                clearMemoryOnExit();
            txtView.clearLrCache2();
            txtView2.clearLrCache2();
        } catch (Exception e) {
            A.error(e);
        }
//		A.updateWidget(this, true);
    }

    @Override
    protected void onStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            if (!hasActTrans && getWindow().getSharedElementEnterTransition() != null)
                hasActTrans = true;
        A.log("-txt:onStart, hasTrans: " + hasActTrans);
        super.onStart();
        setScreenOrientation();
        if (!isTxtScrollReady)
            checkLanguageSetting();
    }

    long statisticsTime;

    private void saveStatisticsInfo() {
        A.hasStatisticsAction = true;
        BookDb.ReadStatistics r = BookDb.getSavedStatistics(A.lastFile);
        long readTime = System.currentTimeMillis() - statisticsTime;
        if (read_words > 0 && statisticsTime > 0 && readTime > 2000) {
            recordDateReadTime(r, readTime, read_words);
            A.statistics_time += readTime;
            r.usedTime += readTime;
            r.readWords += read_words;
            BookDb.insertReadStatistics(r);
        }
        read_words = 0;
        statisticsTime = System.currentTimeMillis();
    }

    private void recordDateReadTime(BookDb.ReadStatistics r, long readTime, int readWords) {
        long todayNumber = T.getTodayNumber();
        long todayBegin = T.getTodayStartMills(); // same as: todayNumber * T.day(1);
        long todayElapsed = System.currentTimeMillis() - todayBegin;
        long yesterdayRead = 0;
        int yesterdayWords = 0;
        if (readTime > todayElapsed) {
            yesterdayRead = readTime - todayElapsed;
            yesterdayWords = (int) (readWords * yesterdayRead / readTime);
            readWords -= yesterdayWords;
            readTime = todayElapsed;
        }
        if (yesterdayRead > 0)
            addDateReadTime(r, todayNumber - 1, yesterdayRead, yesterdayWords);
        addDateReadTime(r, todayNumber, readTime, readWords);
//		A.log("*" + yesterdayRead + " r.dates:" + r.dates);
    }

    private void addDateReadTime(BookDb.ReadStatistics r, long dayNumber, long readTime, int readWords) {
//		String s = dayNumber + "\n";
        boolean old = true;
        int i = r.dates.indexOf(dayNumber + "\n");
        if (i == -1) {
            i = r.dates.indexOf(dayNumber + "|");
            old = false;
        }
        if (i != -1) {
            if (old) {
                int i2 = r.dates.indexOf("\n", i);
                r.dates = r.dates.substring(0, i)
                        + (dayNumber + "|" + readTime + "@" + readWords)
                        + r.dates.substring(i2);
            } else {
                int i2 = r.dates.indexOf("|", i);
                int i3 = r.dates.indexOf("@", i);
                int iEnd = r.dates.indexOf("\n", i);
                if (iEnd > i2) {
                    int oldTime = T.string2Int(r.dates.substring(i2 + 1, iEnd));
                    int oldWords = i3 > 0 ? T.string2Int(r.dates.substring(i3 + 1, iEnd)) : 0;
                    r.dates = r.dates.substring(0, i)
                            + (dayNumber + "|" + (oldTime + readTime) + "@" + (oldWords + readWords))
                            + r.dates.substring(iEnd);
                }
            }
        } else {
            r.dates += (dayNumber + "|" + readTime + "@" + readWords + "\n");
        }
    }

    private void checkLanguageSetting() {
        if (A.languageID != 0)
            handler.sendEmptyMessageDelayed(LANGUAGE_CHECK, 500);
    }

    boolean fromOuterApp, fromShuku, isFirstBook;

    private void initExtras(boolean checkExtras) {
        try {
            A.lastFileAnnotUpdated = false;
            A.tmpOutOfMemoryTag = false;
            A.forceCssFontName = null;
            A.typefaceCache = null;
            A.moveStart = false;
            A.baseFameHeight = 0;
            A.deletedNotes = null;
            pdfThumb = null;
            read_words = 0;
            startTime = SystemClock.elapsedRealtime();
            inPreShow = false;
            showHintAtLoadingEnd = false;
            hintForBigFileOverload = "";
            isPaused = false;
            isTxtScrollReady = false;
            htmlText = "";
            htmlFileWordCount = 0;
            htmlFileCharCount = 0;
            A.forceRebootToMain = false;
            A.fileEncoding = null;
            A.highlighInited = false;
            A.pdfStatus = STATUS.sta_none;
            BaseEBook.isGetWordsWoking = false;
            MyHtml.hasRuby = false;
            resetPageCount();
            A.lastWebY = -1;
            bookType = -1;
            cachedSplitsPageCount = cachedSplitsPageNum = null;
            A.forceCssFontTipped = null;

            long positionFromExtras = -1;
            int chapterFromExtras = 0;
            int splitIndexFromExtras = 0;
            boolean online_book = false;

            if (checkExtras) {
                A.clearTxts();
                A.getLocaleInfo(this, true);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    fromOuterApp = extras.getBoolean("fromOuterApp");

                    online_book = extras.containsKey("online_book");
                    if (extras.containsKey("fromShuku"))
                        fromShuku = true;
                    if (extras.containsKey("bookFile") && !T.isNull(extras.getString("bookFile")))
                        A.lastFile = extras.getString("bookFile");
                    if (extras.containsKey("position")) //from bookmark
                        positionFromExtras = extras.getLong("position");
                    if (extras.containsKey("chapter")) //from bookmark
                        chapterFromExtras = extras.getInt("chapter");
                    if (extras.containsKey("splitIndex")) //from bookmark
                        splitIndexFromExtras = extras.getInt("splitIndex");

                    if (extras.containsKey("fromWidget") && extras.getBoolean("fromWidget")) {
                        if (A.needPasswordProtect && !A.passwordOK) {
                            A.forceRebootToMain = A.forceRebootToTxt = false;
                            A.SaveOptions(this);
                            System.exit(0); //v2.6.6
                        } else if (A.getFileType(A.lastFile) == A.FILE_CBR || A.getFileType(A.lastFile) == A.FILE_CBZ) {
                            Intent i = new Intent(this, PicGalleryShow.class);
                            i.putExtra("imageFile", A.lastFile);
                            startActivityForResult(i, 0);
                            finish();
                            return;
                        }
                    }
                }
            }

            isFirstBook = !T.isFile(A.xml_files_folder + "/" + A.POSITION_FILE + ".xml");
            if (positionFromExtras != -1) {
                A.lastPosition = positionFromExtras;
                A.lastChapter = chapterFromExtras;
                A.lastSplitIndex = splitIndexFromExtras;
                if (getSharedPreferences(A.POSITION_FILE, 0).contains(A.lastFile.toLowerCase()))
                    ignoreChapterListAtBegin = true;
            } else if (!online_book) {
                getReadingPosition();
            }

            boolean allowProgressDlg = !online_book && !allowBookCoverAnimat();
            if (ActivityMain.restoreFromTxtUnexpected) {
                ActivityMain.restoreFromTxtUnexpected = false;
                allowProgressDlg = false;
            }

            if (allowProgressDlg)
                if (getBookType() != A.FILE_TXT) {
                    if (!isPdf() && A.getFileType() != A.FILE_WBPUB)
                        createProgressDlg("", getString(R.string.loading) + "...");
                } else if (isBigFileBigPosition(A.lastFile))
                    createProgressDlg(A.lastFile);

            if (A.bluelightEnable && A.bluelightOpacity > 0)
                handler.sendEmptyMessage(CHECK_BLUELIGHT_FILTER);
        } catch (Exception e) {
            A.error(e);
        }
        handler.sendEmptyMessageDelayed(UPDATE_SHELF_STATE, 200);
        A.flip_animation = getBookType() == A.FILE_PDF ? A.flip_pdf : A.flip_ebook;
    }

    boolean ignoreChapterListAtBegin; //v1.8.11

    private void getReadingPosition() {//0.015s for 100+ books
        SharedPreferences sp = getSharedPreferences(A.POSITION_FILE, 0);
        String s = "0";
        String saveName = A.lastFile.toLowerCase();
        saveName = BaseCompressor.getZRCacheFile(saveName, true).toLowerCase();
        if (sp.contains(saveName)) {
            s = sp.getString(saveName, "0");
            ignoreChapterListAtBegin = true;
        } else {
            ignoreChapterListAtBegin = false;
            for (String key : sp.getAll().keySet()) {
                if (key.endsWith("/" + T.getFilename(saveName))) {
                    s = sp.getString(key, "0");
                    ignoreChapterListAtBegin = true;
                    break;
                }
            }
        }
        A.getLastPositionAndChapterFromString(s);
        A.log("#loadPosition:" + s + ", " + A.lastPosition);
        if (A.useWebView && sp.contains(saveName + 2)) //v1.2.24
            A.lastWebY = sp.getInt(saveName + 2, 0);
    }

    public static final int PAGE_UP = -1;
    public static final int PAGE_DOWN = 1;
    private static final int SHOW_TXT = 101;
    private static final int SHOW_HTML = 102;
    private static final int SHOW_EBOOK = 103;
    private static final int HIDE_EBOOK_COVER = 1042;
    private static final int SHOW_PDF = 105;
    private static final int SHOW_PDF2 = 106;

    private static final int PRETEXT_SHOW = 201;
    private static final int SCROLL_TXT_AGAIN = 202;
    private static final int AUTO_SCROLL_MESSAGE = 301;
    private static final int SHOW_SCROLLBAR = 401;
    private static final int LONG_TIME_TAP_EVENT = 501;
    private static final int FOOTNOTE_LONG_TIME_TAP_EVENT = 502;
    private static final int LANGUAGE_CHECK = 601;
    public static final int GET_CLOUD_POSITION = 801;
    public static final int INIT_CURL3D_RENDER = 850;
    public static final int INIT_CURL3D = 851;
    public static final int INIT_CURL3D_2 = 8512;
    public static final int REFRESH_EBOOK_REMAINING_TIME = 901;
    public static final int LOOP_EVENT_CHECK = 902;
    public static final int DELAY_CURL3D_SHOT2 = 904;
    //	public static final int DELAY_PAGE_SCROLL = 9041;
    public static final int DELAY_CACHE_SHOT1 = 9042;
    public static final int DELAY_CACHE_SHOT2 = 9043;
    public static final int DELAY_HIDE_CURL_FLIPVIEW = 9044;
    //	public static final int DELAY_CURL3D_CLICK = 905;
    public static final int DELAY_CURL3D_TOUCH_UP = 906;
    public static final int DELAY_FLIP_CLICK = 907;
    public static final int PAGE_DOWN_TO_NEXT_CHAPTER = 909;
    public static final int PRE_LOAD_NEXT_CHAPTER = 910;
    public static final int SPEAK_PDF_TEXT_REFLOW = 920;
    public static final int RESTART_CONFIRM_MESSAGE = 921;
    public static final int SYNC_FINISHED = 922;
    //	public static final int REFRESH_TYPEFACE = 923;
    //	public static final int CREATE_LAYOUT = 925;
    public static final int CHECK_DUAL_PAGE = 950;
    public static final int SCROLL_NO_DELAY = 951;
    public static final int SET_FONT_SIZE = 952;
    public static final int SET_MARGIN = 953;
    public static final int CHECK_BOTTOM_HALF_LINE = 954;
    public static final int PDF_MISS_FONT = 955;
    public static final int GPU_SHADOW = 956;
    public static final int FORCEFITHARDWAREACCELERATE = 957;
    public static final int CHECK_BLUELIGHT_FILTER = 958;
    //	public static final int START_AUTOSCROLL_EVENT = 959;
    public static final int DO_FINISHED = 960;
    public static final int ERROR_FINISHED = 961;
    public static final int BOOK_INFO_COUNT_DONE = 962;
    public static final int CACHE_NEXT_CHAPTER_CURL = 963;
    public static final int CACHE_NEXT_CHAPTER_CURL2 = 964;
    public static final int RESTART_HANDLER = 965;
    public static final int RESTART_INTENT_HANDLER = 9652;
    public static final int CACHING_TASK = 967;
    public static final int CACHING_TASK_END = 9672;
    public static final int CACHE_FINISHED = 968;
    public static final int CACHE_ERROR = 969;
    public static final int SWITCH_FINISHED = 970;
    public static final int SWITCH_ERROR = 971;
    public static final int UPDATE_WEB_CHAPTERS = 973;
    public static final int DOWNLOAD_WEB_IMAGE_FINISHED = 974;
    public static final int DOWNLOAD_WEB_IMAGE_FAILED = 975;
    public static final int SEARCH_CLIPBOARD_BOOK = 976;
    public static final int HIDE_TXTCACHE = 977;
    public static final int IF_SPLASH_AD = 978;
    public static final int VALUE_ANIMATION_END = 979;
    public static final int CACHE_STORE_URL_START = 980;
    public static final int CACHE_STORE_URL_FINISH = 981;
    public static final int FIRST_BOOK_TIP = 982;
    public static final int FIRST_BOOK_TIP2 = 983;
    public static final int UPDATE_SHELF_STATE = 984;
    public static final int GET_BOOK_COMMENT = 985;

    public void run() {
        currentPage = -1;
        bookType = -1;
        A.screenChangedTime = 0;
        handler.sendEmptyMessageDelayed(LOOP_EVENT_CHECK, 60 * 1000);

        switch (getBookType()) {
            case A.FILE_TXT:
                if (A.getTxts().size() == 0)
                    loadFile(A.lastFile);
                handler.sendEmptyMessageDelayed(SHOW_TXT, getShowContentDelay()); //delayTime获取不能放在前面, 前面是0, 到这里onStart()执行完变成新的了
                break;

            case A.FILE_HTML:
                if (htmlText.equals("") && !A.useWebView)
                    loadFile(A.lastFile);
                handler.sendEmptyMessageDelayed(SHOW_HTML, getShowContentDelay());
                break;

            case A.FILE_EBOOK:
                if (A.ebook == null)
                    loadFile(A.lastFile);
                handler.sendEmptyMessageDelayed(SHOW_EBOOK, getShowContentDelay());
                break;

            case A.FILE_PDF:
                loadFile(A.lastFile);
                handler.sendEmptyMessageDelayed(SHOW_PDF, getShowContentDelay());
                break;
        }

    }

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
//			A.log("===========msg:" + msg.what + " tv:" + txtView.getWidth() + " base:" + baseFrame.getWidth() + "/" + A.getScreenWidth());
            if (isFinishing())
                return;
            setStatusBarHeight();
            if (msg.what == GET_BOOK_COMMENT) {
                getBookCommentCount();
                return;
            }
            if (msg.what == StoreWebView.STORE_CHARGED_NOTIFY) {
                W.showAdFreeForStoreChargeTip(ActivityTxt.this, msg.arg1, msg.arg2);
                return;
            }
            if (msg.what == SEARCH_CLIPBOARD_BOOK) {
                WB.searchClipboardBook(ActivityTxt.this);
                return;
            }
            if (msg.what == ActivityMain.DOWNLOAD_BOOK_TASK) {
                BrowserAct.downloadBook2();
                hideProgressDlg();
                return;
            }
            if (msg.what == DOWNLOAD_WEB_IMAGE_FINISHED) {
                if (progressDlg != null) {
                    hideProgressDlg();
                    saveLastPostion(true);
                    reloadBook();
                }
                return;
            }
            if (msg.what == DOWNLOAD_WEB_IMAGE_FAILED) {
                hideProgressDlg();
                T.showToastText(ActivityTxt.this, (String) msg.obj);
                return;
            }
            if (msg.what == DownloadTask.DOWNLOAD_FINISHED) {
                WebBookDetailAct.doOnFinish((DownloadTask.Result) msg.obj);
                return;
            }
            if (msg.what == DownloadTask.DOWNLOAD_ERROR) {
                WebBookDetailAct.doOnError((DownloadTask.Result) msg.obj);
                return;
            }
            if (msg.what == DownloadTask.SCAN_FINISHED) {
                clearCachedWebViews();
                return;
            }
            if (msg.what == UPDATE_WEB_CHAPTERS) {
                updateWebBookChapters(false);
                return;
            }

            if (msg.what == DO_FINISHED) {
                doFinish();
                return;
            }
            if (msg.what == ERROR_FINISHED) {
                error_and_exit((String) msg.obj, msg.arg1 == 1 ? true : false);
                return;
            }

            try {
                switch (msg.what) {
                    case SHOW_TXT:
                        if (inExiting) return;
                        A.log("===========SHOW_TXT");
                        if (waitActTransFinish(msg))
                            return;
//						if (inPreShow)
//							inPreShow = false;
//						else
                        showTxtByPosition(A.lastPosition, null);
                        resetPageCount();
                        showReadProgress(0);
                        preShowTxtChapterList();
                        hideProgressDlg();
                        if (hide_ebook_cover())
                            return;
                        if (!hintForBigFileOverload.equals("")) {
                            T.showToastText(ActivityTxt.this, hintForBigFileOverload, 1);
                            hintForBigFileOverload = "";
                        } else if (showHintAtLoadingEnd) {
                            showHintAtLoadingEnd = false;
                            T.showToastText(ActivityTxt.this, getString(R.string.loaded_all));
                        } else if (fileNotExists)
                            T.showToastText(ActivityTxt.this, A.lastFile + getString(R.string.not_exists), 1);
                        break;

                    case SHOW_HTML:
                        if (inExiting) return;
                        A.log("===========SHOW_HTML");
                        if (waitActTransFinish(msg))
                            return;
                        if (A.useWebView) {
                            hideProgressDlg();
                            showInWebView("");
                            return;
                        }
                        if (!fileNotExists) {
                            if (!isTxtScrollReady && A.lastPosition > 0)
                                txtView.disableDraw = txtView2.disableDraw = true;
                            try {
                                htmlText = T.deleteHtmlStyle(T.getHtmlBody(htmlText));
                                htmlSrc = A.adjustChapterHtml(htmlText);
                                if (!A.noSplitHtmls()) {
                                    if (A.lastSplitIndex > A.splitHtmls.size() - 1)
                                        A.lastSplitIndex = 0;//A.splitHtmls.size()-1;
                                    htmlSrc = A.splitHtmls.get(A.lastSplitIndex);
                                }
                                htmlSrc = A.chineseJianFanConvert(htmlSrc);
                                txtViewSetText(MyHtml.fromHtml(htmlSrc, createHtmlBookImageGetter(), -1));
                            } catch (Exception e) { //when convert error
                                A.error(e);
                                txtViewSetText(htmlText);
                            }
                            txtFileFinalScrollAtStartup(A.lastPosition);
                            hideProgressDlg();
                            if (hide_ebook_cover())
                                return;
                        } else
                            T.showToastText(ActivityTxt.this, A.lastFile + getString(R.string.not_exists), 1);

                        break;

                    case HIDE_EBOOK_COVER:
                        hide_ebook_cover();
                        break;

                    case SHOW_EBOOK:
                        if (inExiting)
                            return;
                        handler.sendEmptyMessageDelayed(SCROLL_TXT_AGAIN, 30);  //delay to show book cover?
                        break;

                    case PRETEXT_SHOW:
                        if (inExiting) return;
                        if (waitActTransFinish(msg))
                            return;
                        hideProgressDlg();
                        showPreText((String) msg.obj, msg.arg1);
                        break;

                    case SCROLL_TXT_AGAIN:
                        if (inExiting) return;
//						A.log("===========SCROLL_TXT_AGAIN " + baseFrame.getWidth() + "|" + A.getScreenWidth2() + " hasTrans: " + hasActTrans);
                        if (waitActTransFinish(msg))
                            return;
//						A.log("handle msg: SCROLL_TXT_AGAIN"+", lastPosition:"+A.lastPosition);
                        boolean clickTipShowed = initClickTip(false);
                        hasShuPingPage = false;

                        switch (getBookType()) {
                            case A.FILE_TXT:
                            case A.FILE_HTML:
//								A.log("---txtFileFinalScrollAtStartup 3: " + txtView.getLineCount() + " tvH: "+txtView.getHeight());
                                hasShuPingPage = A.hasShupingPage(A.lastFile, getBookName(), getAuthor());
                                txtView.disableDraw = txtView2.disableDraw = false;
                                MyLayout layout = txtView.getLayout();
                                if (layout == null)
                                    return;
                                int line = layout.getLineForOffset(msg.arg1);
                                int y = txtView.getLineTop2(line);
                                txtScrollTo(y);
                                break;

                            case A.FILE_EBOOK:
                                checkStatusBar();
                                if (A.isEBookOK()) {
                                    if (isDrmBook())
                                        return;

                                    hasShuPingPage = A.hasShupingPage(A.lastFile, getBookName(), getAuthor());
                                    if (isWebBook()) {
                                        A.useWebView = false;
                                        handler.sendEmptyMessageDelayed(CACHE_STORE_URL_START, 3000);
                                    }
                                    boolean atFirst = !isTxtScrollReady && A.lastChapter == 0 && A.lastPosition == 0;
                                    if (!isTxtScrollReady && A.lastPosition > 0)
                                        txtView.disableDraw = txtView2.disableDraw = true;
                                    showEBookByPosition(A.lastChapter, A.lastSplitIndex, A.lastPosition, true);
                                    if (A.ebook.showChaptersAtBegin() && (isWebBook() ||
                                            (!ignoreChapterListAtBegin && atFirst && A.ebook.getChapters().size() > 3))) {
                                        if (!clickTipShowed)
                                            do_show_chapters(0);
                                    }
                                    if (atFirst && A.getFileType() == A.FILE_CHM)
                                        inverseLayoutVisible(false);
                                } else {
                                    hideProgressDlg();
                                    error_and_exit(null, true);
                                    return;
                                }
                                break;
                        }

                        if (getBookType() != A.FILE_EBOOK)
                            hideProgressDlg();
                        if (A.autoUpdateReadingFollow && isWebBook())
                            if (!A.getFinishBooks().contains(A.lastFile))
                                if (!A.isBookJustUpdated(A.lastFile))
                                    handler.sendEmptyMessageDelayed(UPDATE_WEB_CHAPTERS, 3000);

                        resetPageCount();
                        ebook_inLoading = false;
                        if (!isTxtScrollReady) {
                            isTxtScrollReady = true;
                            loadNotes();
                        }
                        pdfHideLay();
                        if (txtLay.getVisibility() == View.GONE)
                            txtLay.setVisibility(View.VISIBLE);
                        if (needPreCurl())
                            if (!A.openBookAnim || !coverShowed)
                                init3dHandler();
                        checkScreenAwake();

                        if (isFirstBook) {
                            isFirstBook = false;
                            handler.sendEmptyMessageDelayed(FIRST_BOOK_TIP, 100);
                        }
                        if (hasShuPingPage)
                            handler.sendEmptyMessageDelayed(GET_BOOK_COMMENT, 300);
                        break;

                    case SHOW_SCROLLBAR:
                        showScrollbarProgress(false);
                        break;

                    case AUTO_SCROLL_MESSAGE:
                        do_autoscroll_message();
                        break;

                    case LONG_TIME_TAP_EVENT:
                        longTimeTapEventSent = false;
                        if (longTimeTapEvent && !isPaused && !pdfAnnotStart) {
                            longTimeTapEvent = false;
                            if (!stopLongTap(hMotionEvent))
                                doLongTimeTapEvent();
                        }
                        break;

                    case FOOTNOTE_LONG_TIME_TAP_EVENT:
                        handler.removeMessages(FOOTNOTE_LONG_TIME_TAP_EVENT);
                        if (footnoteLongTimeTapEvent) {
                            footnoteLongTimeTapEvent = false;
                            copyFootnoteToClipboard(msg);
                        }
                        break;

                    case LANGUAGE_CHECK:
                        A.setLanguage(ActivityTxt.this);
                        break;

                    case GET_CLOUD_POSITION:
                        break;

                    case SHOW_PDF:
                        if (waitActTransFinish(msg))
                            return;
                        pdfOpen(A.lastFile);
                        if (needPreCurl())
                            if (!A.openBookAnim || !coverShowed)
                                init3dHandler();
                        break;

                    case SHOW_PDF2:
                        hideProgressDlg();
                        pdfOpen2(A.lastFile);
                        break;


                    case REFRESH_EBOOK_REMAINING_TIME:
                        refresh_ebook_remaining_time(msg.arg1, msg.arg1 == -1);
                        break;

                    case LOOP_EVENT_CHECK:
                        if (isFinishing())
                            return;
//					A.log("->check loop event");
                        checkRemindEvent();
                        checkTiltEvent();
                        checkScreenAwake();
                        handler.removeMessages(LOOP_EVENT_CHECK);
                        handler.sendEmptyMessageDelayed(LOOP_EVENT_CHECK, 30 * 1000);
                        doTaskRead();
                        break;

                    case INIT_CURL3D_RENDER:
                        initCurl3dRender();
                        break;

                    case INIT_CURL3D:
                        initCurl3d(false);
                        handler.sendEmptyMessageDelayed(INIT_CURL3D_RENDER, 50);
                        break;

                    case INIT_CURL3D_2:
                        initCurl3d(false);
                        break;

                    case DELAY_CURL3D_SHOT2:
                        if (needPreCurl())
                            get3dCurlShot(false);
                        else
                            getValueShot(false);
                        break;

//				case DELAY_PAGE_SCROLL:
//					pageScroll(pageDirection);
//					break;
//
                    case DELAY_CACHE_SHOT1:
                        createCachePageShots();
                        break;

                    case DELAY_CACHE_SHOT2:
                        createCachePageShots2();
                        break;

                    case DELAY_HIDE_CURL_FLIPVIEW:
                        endCachingShot();
//						A.saveMemoryLog("createCachePageShots(5-delay) " + A.lastChapter + "," + txtScroll.getScrollY());
                        break;

//					case DELAY_CURL3D_CLICK:
//						handleCurlMessage(createCurlMsg(0, null));
//						break;

                    case DELAY_CURL3D_TOUCH_UP:
                        handleCurlMessage(createCurlMsg(msg.arg1, hMotionEvent));
                        break;

                    case DELAY_FLIP_CLICK:
                        showPageAnimation(pageDirection == PAGE_DOWN);
                        break;

                    case SCROLL_NO_DELAY:
                        txtScrollNoDelay(msg.arg1);
                        break;

                    case SET_FONT_SIZE:
                        setFontSizeHandler();
                        break;

                    case SET_MARGIN:
                        A.setTxtScrollPadding(A.txtScroll, false);
                        if (dualPageEnabled())
                            A.setTxtScrollPadding(A.txtScroll2, false);
//						contentLay.postInvalidate();
                        checkBottomHalfLineHandler();
                        break;

                    case CHECK_BOTTOM_HALF_LINE:
                        checkBottomHalfLineHandler();
                        contentLay.postInvalidate();
                        break;

                    case PAGE_DOWN_TO_NEXT_CHAPTER:
                        if (A.isSpeaking && isEndOfBook())
                            stop_speak();
                        else pageDownToNextChapter(msg.arg1);
                        break;

                    case PRE_LOAD_NEXT_CHAPTER:
                        handler.removeMessages(PRE_LOAD_NEXT_CHAPTER);
                        preNextChapter(false);
                        break;

                    case SPEAK_PDF_TEXT_REFLOW:
                        speakLines = getSpeakText();
                        setSpeakQueue(0);
                        break;

                    case RESTART_CONFIRM_MESSAGE:
                        new MyDialog.Builder(ActivityTxt.this).setTitle(A.getContext().getString(R.string.error)).setMessage((String) msg.obj)
                                .setPositiveButton(R.string.ok, null)
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        restartReaderToTxt();
                                    }
                                }).show();
                        break;

                    case SYNC_FINISHED:
                        if (restart_after_sync_finish)
                            restartWhenLowMemory(100, true, 50);
                        break;

//					case REFRESH_TYPEFACE:
//						if (A.missedFonts != null)
//							A.missedFonts.clear();
//						A.setTxtViewTypeface();
//						handler.sendMessageDelayed(handler.obtainMessage(SCROLL_NO_DELAY, (int) A.lastPosition, 0), 100);
//						handler.sendMessageDelayed(handler.obtainMessage(SCROLL_NO_DELAY, (int) A.lastPosition, 0), 200);
//						if (!showCssMissedFontsTip() && A.forceCssFontName != null)
//							if (A.hasRealFont(A.forceCssFontName, 0) && !A.fontName.equals(A.DEFAULT_FONTFACE))
//								T.showToastText(ActivityTxt.this,
//										Html.fromHtml(getString(R.string.epub_embedded_fonts) + ": <b>" + A.forceCssFontName + ".ttf</b>"), 1);
//						break;

                    case CHECK_DUAL_PAGE:
                        boolean resend = A.getScreenHeight2() > baseFrame.getHeight() || A.getScreenHeight2() > baseFrame.getWidth();
                        if (!resend)
                            setTxtView2Visible();
                        if (resend || msg.arg1 != 1)
                            handler.sendMessageDelayed(handler.obtainMessage(CHECK_DUAL_PAGE, 1, 1), resend ? 500 : 100);
                        break;

                    case PDF_MISS_FONT:
                        pdfShowMissFontTip();
                        break;

                    case GPU_SHADOW:
                        eraseGPUShadow(0);
                        break;

                    case FORCEFITHARDWAREACCELERATE:
                        updateForFitHardwareAccelerate();
                        break;

                    case CHECK_BLUELIGHT_FILTER:
                        showShadeCoverOnView();
                        T.showToastText(ActivityTxt.this, getString(R.string.bluelight_filter_enabled), 0);
                        break;

					/*case START_AUTOSCROLL_EVENT:
						txtScrollNoDelay(msg.arg1);
						A.log("start scroll, h:" + txtView.getHeight() + " y:" + txtScroll.getScrollY() + " to:" + msg.arg1);
						if (txtView.getHeight() == 0 && txtView.getText().length() > 1) {
							A.log("*******start scroll error, h=0" + " to:" + msg.arg1, true);
							txtView.nullLayouts();
							txtView2.nullLayouts();
							clearTxtView();
							checkDualPageMode();
							handler.sendMessageDelayed(handler.obtainMessage(START_AUTOSCROLL_EVENT, msg.arg1, 0), 1000);
						} else
							startAutoScrollEvent();
						break;*/

                    case BOOK_INFO_COUNT_DONE:
                        do_book_info_count_done();
                        break;

                    case CACHE_NEXT_CHAPTER_CURL:
//						if (createTxtCacheView())
//							handler.sendEmptyMessageDelayed(CACHE_NEXT_CHAPTER_CURL, 50);
//						else
                        createNextChapterCurlCache();
                        break;

                    case CACHE_NEXT_CHAPTER_CURL2:
                        createNextChapterCurlCache2();
                        break;

                    case RESTART_HANDLER:
                        System.exit(0);
                        break;

                    case RESTART_INTENT_HANDLER:
                        restartReaderIntent();
                        break;

                    case CACHING_TASK:
                        cachingChapter();
                        break;

                    case CACHING_TASK_END:
                        stopCacheWebView();
                        break;

                    case CACHE_FINISHED:
                        doCacheFinished((DownloadTask.Result) msg.obj);
                        break;

                    case CACHE_ERROR:
                        doCacheError((DownloadTask.Result) msg.obj);
                        break;

                    case SWITCH_FINISHED:
                        doSwitchSourceFinished((DownloadTask.Result) msg.obj, msg.arg1 == 1, msg.arg2 == 1);
                        break;

                    case SWITCH_ERROR:
                        doSwitchSourceError((DownloadTask.Result) msg.obj);
                        break;

                    case HIDE_TXTCACHE:
                        hideTxtCacheHandler();
                        break;

                    case IF_SPLASH_AD:
                        ifShowSplashAd(msg.arg1 == 1);
                        break;

                    case VALUE_ANIMATION_END:
                        doValueAnimationEnd();
                        break;

                    case CACHE_STORE_URL_START:
                        if (!inExiting && !isFinishing())
                            cacheStoreUrl();
                        break;

                    case CACHE_STORE_URL_FINISH:
                        doCacheStoreUrlFinished((DownloadTask.Result) msg.obj);
                        break;

                    case FIRST_BOOK_TIP:
                        inverseLayoutVisible(false);
//						handler.sendEmptyMessageDelayed(FIRST_BOOK_TIP2, 1000);
                        break;

                    case FIRST_BOOK_TIP2:
//						if (b_fontsize != null && layoutVisible)
//							b_fontsize.performClick();
                        break;

                    case UPDATE_SHELF_STATE:
                        update_shelf_state();
                }

            } catch (Exception e) {
                A.error(e);
            }
        }

        int waitTransCount = 0;

        private boolean waitActTransFinish(Message msg) {
            boolean result = false;
            if (A.openBookAnim && hasActTrans && !A.isSplitScreen(ActivityTxt.this) && waitTransCount < 15 &&
                    ((actTransArrive && !actTransEnd) || baseFrame.getWidth() < A.getScreenWidth())) {
                msg = Message.obtain(msg);
                A.log("-waitActTransFinish, re_send: " + msg.what + ", baseFrame: " + baseFrame.getWidth() + ", system: " + A.getScreenWidth());
                result = true;
                waitTransCount++;
                handler.sendMessageDelayed(msg, 50);
            }
            return result;
        }
    };

    private void update_shelf_state() {
        if (ActivityMain.selfPref != null) {
            ActivityMain.selfPref.removeFollowState(A.lastFile);
            if (A.sortByRecent || A.sortByUpdate)
                if (ActivityMain.selfPref.shelfPager != null)
                    ActivityMain.selfPref.initShelfs(false);
        }
    }

    boolean inCloudSyncing;

    private void reloadBook() {
        ebook_inLoading = true;
        handler.post(this);
    }

    private long setScreenAwakeTime;

    protected void checkScreenAwake() {
        if (baseFrame == null)
            return;
        if (!A.keepScreenAwake)
            return;
        if (A.isInAutoScroll) {
            baseFrame.setKeepScreenOn(true);
            return;
        }
        if (A.screenAwakeExtend > 30)
            return;
        if (setScreenAwakeTime == 0) {
//			A.log("*screen awake LIGHT");
            setScreenAwakeTime = SystemClock.elapsedRealtime();
            baseFrame.setKeepScreenOn(true);
            handler.sendEmptyMessageDelayed(LOOP_EVENT_CHECK, 10 * 1000);
        } else if (SystemClock.elapsedRealtime() - setScreenAwakeTime > A.screenAwakeExtend * 60 * 1000) {
            if (!isPaused) {
                A.log("*screen awake OFF");
                baseFrame.setKeepScreenOn(false);
            }
        }
    }

    protected boolean isDrmBook() {
        if (A.ebook != null && A.ebook.isDrmProtected()) {
            hideProgressDlg();
            new MyDialog.Builder(this).setTitle(getString(R.string.error)).setView(LayoutInflater.from(this).inflate(R.layout.drm, null))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            doFinish();
                        }
                    }).show();
            return true;
        }
        return false;
    }

    protected void preShowTxtChapterList() {
        if (A.chapters != null)
            return;
        this.runOnUiThread(new Runnable() {
            public void run() {
                int count = A.getTxtChapters().size();
                if (count > 0)
                    forceUpdateForFitHardwareAccelerate(200);
                if (A.lastPosition != 0 || !A.isChinese)
                    return;
                if (ignoreChapterListAtBegin)
                    return;
                ignoreChapterListAtBegin = true;
                if (count > 5 && A.lastPosition == 0)
                    do_show_chapters(0);
            }
        });
    }

    protected void loadNotes() {
        new Thread() {
            @Override
            public void run() {
//				if (!isPdf())
                try {
                    A.checkNotesHighlights(true);
                    A.getBookmarks();
                    A.highlighInited = true;
                    txtView.postInvalidate();
                    txtView2.postInvalidate();
                } catch (Exception e) {
                    A.error(e);
                }
            }
        }.start();
    }

    boolean coverShowed;

    protected void show_ebook_cover() {
        if (!allowBookCoverAnimat())
            if (isPdf() || A.isWebBook())
                return;
        Drawable d = A.getBookCover(A.lastFile);
        if (d != null) {
            flipView.isCoverShow = coverShowed = true;
            flipView.setBackgroundDrawable(null);
            flipView.setImageDrawable(d);
            flipView.setVisibility(View.VISIBLE);
            contentLay.setVisibility(View.INVISIBLE);
            baseFrame.setBackgroundDrawable(null);
        }
    }

    boolean hideCoverDelayed;

    protected boolean hide_ebook_cover() {
        if (flipView.isCoverShow) {
            flipView.isCoverShow = false;
            if (allowBookCoverAnimat()) {
                if (!hideCoverDelayed) {
                    hideCoverDelayed = true;
                    flipView.isCoverShow = true;
                    handler.sendEmptyMessageDelayed(HIDE_EBOOK_COVER, A.isFlipCurl() ? 50 : 100);
                    return true;
                }

                if (flipView.getWidth() == 0) { //landscape, will cause not curl3D initial
                    setFlipViewINVISIBLE(true);
                    flipView.setImageDrawable(null);
                    if (needPreCurl())
                        init3dHandler();
                } else {
                    flipView.setBitmap1(T.drawableToBitmap(flipView.getDrawable()));
                    flipView.setBitmap2(null);
                    flipView.setImageDrawable(null);
                    flipView.startAnimation(true, -100, new A.AfterFlipCurl() {
                        @Override
                        public void Finished(boolean success) {
                            if (needPreCurl())
                                init3dHandler();
                        }
                    });
                }
            } else {
                setFlipViewINVISIBLE(true);
                flipView.setImageDrawable(null);
            }
            contentLay.setVisibility(View.VISIBLE);
            A.setBackgroundImage(baseFrame);
            if (A.isFlipCurl())
                hideProgressDlg();
            return true;
        }
        return false;
    }

    private boolean allowBookCoverAnimat() {
        return A.openBookAnim && (hasActTransExtra || System.currentTimeMillis() - A.trySplashAdTime2 > 10000);
    }

    protected void showPreText(String text, int p) { // for txt only
        if (!inPreShow && txtView.getText().length() > 0)
            return;
        txtViewSetText(text);
        txtFileFinalScrollAtStartup(p);
    }

    public void saveLastPostion(boolean saveToFile) {
        if (dntSavePositionAgain)
            return;
        if (isPdf()) {
            if (pdf != null && saveToFile)
                pdfSaveLastPosition();
        } else
            saveLastPostion(saveToFile, false);
    }

    long ignoreSavePosTime;

    public void saveLastPostion(boolean saveToFile, boolean ignoreZero) {
        if (dntSavePositionAgain)
            return;
        if (SystemClock.elapsedRealtime() - ignoreSavePosTime < 2000)
            return;
        if (inCloudSyncing)
            return;
        if (ebook_inLoading && !saveToFile)
            return;
        if (txtView == null || txtViewTextBroken())
            return;

        if (!userAction && //todo: v4.2 incorrect when app killed in screen off: onStart-onResume-onPause-onStop-SCREEN LOCK OFF-onStart
                (System.currentTimeMillis() - resumeTime < 3000 || System.currentTimeMillis() - onCreateTime < 3000)) {
            if (saveToFile) A.log("=================force NOT saveToFile=============");
            return;
        }

        if (web == null) {
            MyLayout lo = txtView.getLayout();
            if (lo != null && isTxtScrollReady) { //v1.3.8 add isTxtScrollReady, forbid reset to 0
                int p = lo.getLineStart(lo.getLineForVertical(txtScroll.getScrollY()));

                if (getBookType() == A.FILE_TXT) {
                    A.lastPosition = A.getTxtRealPos(p);
                } else
                    A.lastPosition = p;
                saveToFile = saveToFile && !(ignoreZero && p == 0);
            } else
                saveToFile = false;

//			if (forceNotSaveZeroPosition && A.lastPosition == 0)
//				saveToFile = false;
        } else
            A.lastWebY = A.verticalAlignment ? web.getScrollX() : web.getScrollY();

        if (saveToFile && !inPreShow && registerHardwaresTime == -1) {
            SharedPreferences.Editor et = getSharedPreferences(A.POSITION_FILE, 0).edit();
            String s = A.lastChapter + "@" + A.lastSplitIndex + "#" + A.lastPosition + ":" + getPercentStr2();
            et.putString(A.lastFile.toLowerCase(), s);
            if (web != null)
                et.putInt(A.lastFile.toLowerCase() + 2, A.lastWebY);
            et.apply();
            A.log("*savePosition:" + s + ", " + A.lastPosition);
        }
    }

    private int bookType = -1;

    public int getBookType() {
        if (bookType == -1)
            bookType = A.getBookType();
        return bookType;
    }

    private boolean isHtmlContent() {
        return getBookType() == A.FILE_HTML || (getBookType() == A.FILE_EBOOK && A.ebook != null && A.ebook.isHtml());
    }

    private boolean isBigFileBigPosition(String lastFile) {
        if (lastFile.indexOf("?") != -1)
            return false;
        File file = new File(lastFile);
        if (file.isFile()) {
            long size = file.length();
            if (A.lastPosition > size)
                A.lastPosition = size;
            if (size > A.BIG_FILESIZE * 2)
                return true;
        }
        return false;
    }

    private boolean restartWhenLowMemory(int percent, boolean restartToMain, long delayTime) {
        if (A.isLowMemory(percent) || A.isLowestMemory()) {//剩余内存少于xx%, 销毁程序重新启动
            A.saveMemoryLog(restartToMain ? "**txt force reboot to main**" : "**txt force reboot**");
            A.forceRebootToTxt = !restartToMain;
            A.forceRebootToMain = restartToMain;
            A.SaveOptions(this);
            A.log("*delay restart: " + delayTime);
            if (delayTime > 0) {
                delaySystemExit(delayTime);
            } else
                System.exit(0);
            return true;
        }
        return false;
    }

    boolean inExiting;

    private void delaySystemExit(long delayTime) {
//		new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				System.exit(0);
//			}
//		}.sendEmptyMessageDelayed(0, delayTime);
        inExiting = true;
        handler.sendEmptyMessageDelayed(RESTART_HANDLER, delayTime);
    }

    private void loadTxtHtmlFile(String filename) {
        try {
            A.fileEncoding = A.getFileSavedEncode(filename);
            InputStream is = new DataInputStream(new FileInputStream(filename));
            BufferedReader read;
            if (A.fileEncoding.equals(""))
                read = new BufferedReader(new InputStreamReader(is));
            else
                read = new BufferedReader(new InputStreamReader(is, A.fileEncoding));

            if (getBookType() == A.FILE_TXT) {
                String preShowText = getTxtFileCache();
                if (preShowText == null && A.lastPosition < A.fixedBlockLength)
                    try {
                        inPreShow = true;
                        A.lastBlockIndex = 0;
                        txtCachePos2 = (int) A.lastPosition;
                        final char[] preBuffer = new char[A.fixedBlockLength * 3];
                        int size = read.read(preBuffer);
                        preShowText = String.valueOf(preBuffer, 0, size);
                        preShowText = A.getTxtDisplayText(preShowText); //v5.0
                        preShowText = A.chineseJianFanConvert(preShowText);
                    } catch (Exception e) {
                        A.error(e);
                        inPreShow = false;
                        preShowText = null;
                    } finally {
                        read.close(); //v1.9.6
                        is = new DataInputStream(new FileInputStream(filename));
                        read = new BufferedReader(new InputStreamReader(is, A.fileEncoding));
                    }
                if (preShowText != null) {
                    inPreShow = true;
                    handler.sendMessageDelayed(handler.obtainMessage(PRETEXT_SHOW, txtCachePos2, 0, preShowText),
                            (long) (getShowContentDelay() * 1.5));
                }
            }

            long totalSize = 0;
            long checkSize = 0;

            if (getBookType() == A.FILE_TXT) {
                final char[] buffer = new char[A.fixedBlockLength];
                while (true) {
                    final int size = read.read(buffer);
                    if (size != -1) {
                        A.getTxts().add((String.valueOf(buffer, 0, size)));
                    } else {
                        read.close();
                        break;
                    }

                    totalSize += size;
                    checkSize += size;
                    if ((totalSize > 1000000) && (checkSize > 500000)) { //已加载超过100万字(2M)后每加载500K检测一下内存
                        checkSize = 0;
                        if (A.isLowMemory(15)) {//剩余内存少于15%, 停止加载
                            A.tmpOutOfMemoryTag = true;
                            hintForBigFileOverload = getString(R.string.low_memory_limits)
                                    + Formatter.formatFileSize(ActivityTxt.this, totalSize * 2);
                            read.close();
                            break;
                        }
                    }
                }

            } else {
                StringBuilder htmlSB = new StringBuilder(); //needn't set init capacity?
                final char[] buffer = new char[A.fixedBlockLength];
                while (true) {
                    final int size = read.read(buffer);
                    if (size != -1) {
                        htmlSB.append(String.valueOf(buffer, 0, size));
                    } else {
                        read.close();
                        break;
                    }
                }
                htmlText = htmlSB.toString();
                htmlText = T.deleteHtmlComment(htmlText);
            }

        } catch (OutOfMemoryError e) {
            A.error(e);
            System.exit(0);
        } catch (UnsupportedEncodingException e) {
            A.error(e);
            SharedPreferences sp = getSharedPreferences(A.ENCODING_FILE, 0);
            if (sp.contains(A.lastFile))
                sp.edit().remove(A.lastFile).commit();
            A.textEncode = A.getDefaultTextEncode("");
            getSharedPreferences(A.OPTIONS_FILE, 0).edit().putString("textEncode", "").commit();
            handler.sendMessage(handler.obtainMessage(ERROR_FINISHED, 0, 0, "Unsupported Encoding Exception: " + A.errorMsg(e)));
        } catch (Exception e) {
            A.error(e);
            System.exit(0);
        }
    }

    private int getShowContentDelay() {
        return A.screenChangedTime > 0 ? 500 : A.openBookAnim ? 80 : 0;
    }

    private void loadFile(String filename) {
        preNextChapterText = null;
        oldPriorChapterText = null;

        A.log("*loadFile:" + filename);
        if (restartWhenLowMemory(30, false, A.isGalaxyS8() ? 0 : 1000))//剩余内存少于35%, 销毁程序重新启动 //todo: delay in Galaxy S8 won't restart app, why?
            return;

        getSharedPreferences(A.OPTIONS_FILE, 0).edit().putBoolean("forceRebootToMain", true).commit();//(1)防止无法加载的书籍重复连续加载
        filename = BaseCompressor.getZRCacheFile(filename);
        if (filename == null) {
            handler.sendEmptyMessage(DO_FINISHED);
            return;
        }

        if (statusHandler != null)
            statusHandler.removeCallbacksAndMessages(null);
        if (!A.useCssFont && A.forceCssFontName != null) {
            A.forceCssFontName = null;
            clearTxtView();
            A.setTxtViewTypeface();
        }

        A.tmp_out_file = A.book_cache + "/tmp";
        if (filename.toLowerCase().endsWith(".fb2") && T.isFile(filename + ".zip"))
            filename = filename + ".zip";
        File file = new File(filename);
        if (file.isFile()) {
            A.clearTxts();

            switch (getBookType()) {
                case A.FILE_TXT:
                case A.FILE_HTML:
                    loadTxtHtmlFile(filename);
                    break;
                case A.FILE_EBOOK:
                    A.loadEBook(filename);
                    break;
            }

            if (T.isOutOfMemoryError && A.isLowestMemory()) {
                A.saveMemoryLog(T.getFilename(A.lastFile) + " open failed:");
                System.exit(0);
            } else
                T.isOutOfMemoryError = false;

            addHistory(A.lastFile);//如果出错, 就不用加入历史记录了
            A.clearTxts2(); //txts2 is String[], must put here to create new
        }
        A.lastFile = filename;
        A.forceRebootToMain = false; //(2)加载成功, 恢复原值
//		A.SaveOptions(this); //don't save here
        PrefSearch.results = null;
        A.loadHighlightAllItems();
        inWebReading = isInWebReading();
        startWithWebReading = inWebReading;
        A.uEvent("book_read", T.getFilename(A.lastFile));
//		A.trackEvent("book_read", T.getFilename(A.lastFile), "", 1);
    }

    private boolean isInWebReading() {
        if (!isWebBook())
            return false;
        if (A.forceYouHua && A.ebook.online_site != S.store)
            return true;
        return A.getWebReadings().contains(A.ebook.online_site.siteTag);
    }

    private void addHistory(final String filename) { //take too long time when history > 100
        Thread thread = new Thread() {
            @Override
            public void run() {
                A.addHistory(filename, true);
            }
        };
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    MRTextView txtView, txtView2;//, txtViewS;        //txtViewS for auto scroll to cover 1
    public ScrollView2 txtScroll, txtScroll2;//, txtScrollS;  //txtScrollS for auto scroll to cover 1
    public FrameLayout baseFrame, txtLayS, curl3dLay;
    View txtLineS, backLay, shadeView;
    public ContentLay contentLay;
    public View dualPageBackground;
    ImageView linkBackIv, chromeIv, ttsIv, nav_return, pdfPenIv, downloadIv;
    public FlipImageView flipView;
    TextView titleTextView, percentView, urlTv, siteTv, priorTv, nextTv, onlineSwitch;
    DotImageView scrollBlock;//, imageZoom;
    public View topLay, buttonsLay, txtLay, pdfLay, menuB, urlLay, funcLay;
    public ViewGroup bottomLay;
    View fontPanel, brightnessPanel, colorPanel, spacePanel, nightPanel;
    ListView menuLv;
    public PDFReader pdf;

    private ProgressDialog progressDlg;
    int fileProgress = 0;
    long startTime;//, preShowTime;
    boolean showHintAtLoadingEnd;

    boolean inPreShow = false;
    boolean layoutVisible = false;
    public long setLayoutTime = 0;

    String hintForBigFileOverload = "";
    public boolean isTxtScrollReady;
    public String htmlText = "";
    boolean fileNotExists;
    //	LinearLayout zoomLay;

    private void initView() {
        baseFrame = findViewById(R.id.txtFrame);
        contentLay = findViewById(R.id.contentLay);
        dualPageBackground = findViewById(R.id.dualPageBackground);
        txtLay = findViewById(R.id.txtLay);
        shadeView = findViewById(R.id.shadeView);
        txtScroll = findViewById(R.id.txtScrollView01);
        txtScroll2 = findViewById(R.id.txtScrollView02);
        txtView = findViewById(R.id.txtTextView01);
        txtView2 = findViewById(R.id.txtTextView02);

        txtScroll.setSmoothScrollingEnabled(false);
        txtScroll2.setSmoothScrollingEnabled(false);
        txtScroll.setFadingEdgeLength(0);
        txtScroll2.setFadingEdgeLength(0);
        setScrollEdgeColor();

        scrollCache = findViewById(R.id.scrollCache);
        scrollCache.setSmoothScrollingEnabled(false);
        scrollCache.setFadingEdgeLength(0);
        scrollCache.setOnTouchListener(this);
        txtCache = findViewById(R.id.txtCache);

        txtLayS = findViewById(R.id.txtScrollLay);
        curl3dLay = findViewById(R.id.curl3dLay);
        flipView = findViewById(R.id.flipImageView);
        linkBackIv = findViewById(R.id.linkBackIv);
        ttsIv = findViewById(R.id.ttsB);
        scrollBlock = findViewById(R.id.txtScrollPos);

        initReadProgressViews();
        initHighlightViews();
        txtScroll.setOnTouchListener(this);
        txtScroll2.setOnTouchListener(this);
        flipView.setOnTouchListener(this);
        linkBackIv.setOnClickListener(this);
        ttsIv.setOnClickListener(this);

        setFullscreen(false);
        layoutVisible = false;
        setLayoutTime = 0;

        initViewParams();
        if (!isPdf()) {
            A.loadVisualOptions(false);
            adjustNightTheme();
        } else
            baseFrame.setBackgroundColor(Color.BLACK);
        baseFrame.setKeepScreenOn(A.keepScreenAwake);

        contentLay.setDrawingCacheEnabled(false);
        contentLay.setAnimationCacheEnabled(false);

        checkDualPageMode();
        checkLandscape2PagePadding();

        if (A.openBookAnim || getBookType() == A.FILE_EBOOK)
            show_ebook_cover();
        if (needPreCurl())
            if (!A.immersive_fullscreen || !A.landscape2PageMode || getBookType() == A.FILE_PDF)
                if (!A.openBookAnim)
                    initCurl3d(false);
    }

    private void setScrollEdgeColor() {
//		try {
//			EdgeEffect effect = new EdgeEffect(this);
//			effect.setColor(0xee009689);
//			Field f1 = ScrollView.class.getDeclaredField("mEdgeGlowTop");
//			f1.setAccessible(true);
//			f1.set(txtScroll, effect);
//			Field f2 = ScrollView.class.getDeclaredField("mEdgeGlowBottom");
//			f2.setAccessible(true);
//			f2.set(txtScroll, effect);
//		} catch (Exception e) {
//			A.error(e);
//		}
    }

    private void initViewParams() {
        A.baseFrame = baseFrame;
        A.contentLay = contentLay;
        A.txtLay = txtLay;
        A.txtView = txtView;
        A.txtScroll = txtScroll;
        A.txtView2 = txtView2;
        A.txtScroll2 = txtScroll2;
        A.scrollCache = scrollCache;
        A.txtCache = txtCache;
    }

    public NewCurl3D curl3d;
    public FlipImageView curl3dCover;

    private void initCurl3d(boolean forceRecreate) {
        if (curl3d != null && !forceRecreate)
            return;

        if (forceRecreate || needPreCurl()) {
            A.log("*initCurl3d");
            if (curl3d != null) {
                curl3d.clearAllShots();
                curl3dLay.removeView(curl3d);
                handler.sendEmptyMessage(INIT_CURL3D_RENDER);
            }

            if (needInit3DCurl()) {
                curl3dLay.setVisibility(View.VISIBLE);
            } else
                curl3dLay.setVisibility(View.INVISIBLE);

            if (dualPageEnabled() && A.pageStyle2PageMode) {
                Rect r = new Rect();
                Drawable d = getResources().getDrawable(R.drawable.bookstyle);
                d.getPadding(r);
                int t = r.top * 25 / 100, l = r.left * 72 / 100; //not highquality: 58
                curl3dLay.setPadding(l, t, l, t);
            } else
                curl3dLay.setPadding(0, 0, 0, 0);

            curl3d = new NewCurl3D(this);
            curl3dLay.addView(curl3d, new FrameLayout.LayoutParams(-1, -1));
            curl3d.setEnableTouchPressure(true);
            curl3d.setOnScrollListener(onFlipCurlingEnd);
            curl3d.setViewMode(dualPageEnabled(), A.getScreenWidth2() - curl3dLay.getPaddingLeft() * 2,
                    A.getScreenHeight2() - curl3dLay.getPaddingTop() * 2);

            if (curl3dCover == null) {
                curl3dCover = new FlipImageView(this);
                curl3dLay.addView(curl3dCover, new FrameLayout.LayoutParams(-1, -1));
            }
            if (needInit3DCurl()) {
                A.setBackgroundImage(curl3dCover);
                curl3d.renderState = 2;
            }
            curl3d.curl3dLay = curl3dLay;
            curl3d.curl3dCover = curl3dCover;
            curl3dCover.setVisibility(View.VISIBLE);
            curl3dCover.bringToFront();
            curl3dLay.setOnTouchListener(this);
        }
    }

    private boolean needPreCurl() {
        return A.isFlipCurl() || (A.isFlipNone() && A.isHoriFlipCurl());
    }

    private void initCurl3dRender() {
        if (curl3d != null && curl3d.renderInited == 0) {
            if (needInit3DCurl()) {
                get3dCurlShot(true);
                curl3d.updateBitmaps();
                curl3d.pageScroll(true); //run in background to init
            }
            curl3d.renderInited = 1;
            handler.sendEmptyMessage(INIT_CURL3D_RENDER);
        } else if (curl3d != null && curl3d.renderInited < 2) {
            curl3d.renderInited = 2;
            setCurl3dVisibility(false);
        }
    }

    private boolean needInit3DCurl() {
        return Build.VERSION.SDK_INT < 11 || A.isSonyROM || A.isHtcRom;
    }

    private void setCurl3dVisibility(boolean visible) {
        hide_ebook_cover();
        if (visible) {
            curl3d.setDurationTime(get3dFlipSpeed());
            curl3dLay.setVisibility(View.VISIBLE);
        } else {
            flippingAnimationTime = 0;
//			drawEmptyNextPage = false;
            curl3dLay.setVisibility(View.GONE);
            curl3dLay.setTag(null); //curl3d not start flag
            if (curl3dCover.getVisibility() != View.GONE) {
                curl3dCover.setVisibility(View.GONE);
                curl3dCover.setBackgroundDrawable(null);
                curl3dCover.setImageDrawable(null);
            }
            contentLay.postInvalidate();
        }
    }

    private int get3dFlipSpeed() { // todo:
        if (A.flip_animation == A.FLIP_CURL3D)
            return (60 - A.flipSpeed) * 8;
        else
            return (60 - A.flipSpeed) * 10;
    }

    public long lastFlipScrollY;
    public Bitmap tmpFlipShot1, tmpFlipShot2, tmpFlipShot3;
    private boolean forceDelayFlipAnimation, getting3dCurlShot;
    ;

    private boolean get3dCurlShot(boolean front) {
        if (front)
            curlDelayedMsg = null;

        if (!front && forceDelayFlipAnimation) {
            if (curl3dCover.getVisibility() != View.VISIBLE)
                showCurl3dCover(tmpFlipShot1);
            setCurl3dVisibility(true);
            handler.sendEmptyMessageDelayed(DELAY_CURL3D_SHOT2, 10);
            return false;
        }

        try {
//			A.saveMemoryLog("get3dCurlShot(1):"+txtScroll.getScrollY()+", ");
            getting3dCurlShot = true;
            boolean dualPage = dualPageEnabled();
            if (curl3dLay.getWidth() > 0)
                curl3d.setViewMode(dualPage, curl3dLay.getWidth() - curl3dLay.getPaddingLeft() * 2, baseFrame.getHeight() - curl3dLay.getPaddingTop() * 2);

            if (front) {
                if (pageDirection == PAGE_UP)
                    T.recycle(tmpFlipShot3);
                curl3dLay.setTag(1); //curl3d start flag
            }

            long curY = getFlipScrollY();
            if (!front)
                lastFlipScrollY = curY;

            boolean samePage = false;
            if (front) {
                samePage = lastFlipScrollY == curY && !T.isRecycled(tmpFlipShot2);
                if (samePage) {
                    T.recycle(tmpFlipShot1);
                    tmpFlipShot1 = tmpFlipShot2;
                } else {
                    T.recycle(tmpFlipShot2);
                    T.recycle(tmpFlipShot3);
                    tmpFlipShot1 = getPageShot(false, HighQuality3D());
                    get3dAboutColor();
                }
            } else
                tmpFlipShot2 = !T.isRecycled(tmpFlipShot3) ? tmpFlipShot3 : getPageShot(false, HighQuality3D());
            Bitmap curlShot = front ? tmpFlipShot1 : tmpFlipShot2;

            boolean hasCache = samePage && curl3dCover.getTag() != null;
            if (front && curl3d.renderInited > 0) {
                if (hasCache)
                    curl3d.renderState = 2;
                else
                    showCurl3dCover(curlShot);
            }

            if (dualPage) {
                if (front && hasCache) {
                    curl3d.shots[0] = curl3d.shots[2];
                    curl3d.shots[1] = curl3d.shots[3];
                } else {
//					Config config = dualPage? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565; //too slow in dual page
                    Config config = Bitmap.Config.RGB_565;
                    Bitmap bm1 = Bitmap.createBitmap(curl3d.pageWidth, curl3d.pageHeight, config);
                    Bitmap bm2 = Bitmap.createBitmap(curl3d.pageWidth, curl3d.pageHeight, config);
                    int t = curl3dLay.getPaddingTop();
                    int l = curl3dLay.getPaddingLeft();
                    Rect r1 = new Rect(l, t, l + curl3d.pageWidth, t + curl3d.pageHeight);
                    Rect r2 = new Rect(0, 0, curl3d.pageWidth, curl3d.pageHeight);
                    Canvas c1 = new Canvas(bm1);
                    c1.drawBitmap(curlShot, r1, r2, null);
                    Canvas c2 = new Canvas(bm2);
                    r1 = new Rect(r1.right, t, r1.right + curl3d.pageWidth, t + curl3d.pageHeight);
                    c2.drawBitmap(curlShot, r1, r2, null);

                    curl3d.setPageShot(front ? 0 : 2, bm1, true);
                    curl3d.setPageShot(front ? 1 : 3, bm2, true);
                }
            } else {
                if (front && hasCache)// && curl3d.shots[1]!=null)
                    curl3d.shots[0] = curl3d.shots[1]; //the same texture, needn't assign page2Bm
                else
                    curl3d.setPageShot(front ? 0 : 1, curlShot, true);
            }
//			A.saveMemoryLog("get3dCurlShot(2):");
            if (curl3d.renderInited > 0) {
                if (front) {
                    pageScroll(pageDirection, true, false);
                    saveLastPostion(false);
                    showReadProgress(pageDirection == PAGE_DOWN ? 1 : -1);
                    return get3dCurlShot(false);
                } else if (curlDelayedMsg != null) {
                    Message delayedMsg = curlDelayedMsg;
                    curlDelayedMsg = null;
                    handleCurlMessage(delayedMsg);
                }
            }
            getting3dCurlShot = false;
            return true;
        } catch (OutOfMemoryError e) {
            A.error(e);
            System.gc();
            setCurl3dVisibility(false);
        } catch (Exception e) {
            A.error(e);
        }
        getting3dCurlShot = false;
        return false;
    }

    private boolean HighQuality3D() {//zth: take 30% more memory, n10 with 4.2.2 has bug
//		if (A.showStatusbar && !dualPageEnabled() && !isPdf())
//			return true;
//		if (Build.VERSION.SDK_INT > 14 && Runtime.getRuntime().maxMemory() > 120 * 1000000)
//			return true;
        if (dualPageBackground.getVisibility() == View.VISIBLE && getDualDrawableRes() != -1 && Build.VERSION.SDK_INT > 14)
            return true;
        return false;
    }

    private void get3dAboutColor() {
        if (curl3d != null)
            curl3d.aboutColor = isPdf() ? A.pdf_back_color : !A.useBackgroundImage ? A.backgroundColor :
                    T.getDrawableAboutColor(A.getBackgroundDrawable(A.backgroundImage));
    }

    private long getFlipScrollY() {
        if (pdf != null) {
            return pdf.pdfView.viewGetPos().page_x + pdf.pdfView.viewGetPos().page_y + A.lastPosition;
        } else if (getBookType() == A.FILE_TXT) {
            saveLastPostion(false);
            return A.lastPosition;
        } else
            return txtScroll.getScrollY();
    }

    boolean curl3dCoverShowed;

    private void showCurl3dCover(Bitmap bm) {
//		A.saveMemoryLog("-getcover(1):");
        try {
            curl3d.renderState = 0;
            int l = curl3dLay.getPaddingLeft();
            if (l > 0) {
                int t = curl3dLay.getPaddingTop();
                int w = curl3dLay.getWidth() - 2 * l;
                int h = baseFrame.getHeight() - 2 * t;
                Bitmap bm1 = Bitmap.createBitmap(w, h, Config.RGB_565);
                Canvas c1 = new Canvas(bm1);
                c1.drawBitmap(bm, new Rect(l, t, l + w, t + h), new Rect(0, 0, w, h), null);
                curl3dCover.setImageBitmap(bm1);
            } else if (!T.isRecycled(bm))
                curl3dCover.setImageBitmap(bm.copy(bm.getConfig(), false));
            curl3dCover.setTag(1);
            curl3dCoverShowed = true;
            curl3dCover.setVisibility(View.VISIBLE);
        } catch (OutOfMemoryError e) {
            A.error(e);
        }
//		A.saveMemoryLog("-getcover(2):");
    }

    //-------------

    public void setBottomIconsVisibility() {
        if (topLay == null)
            return;

//		b_option.setVisibility(A.showOptionButton ? View.VISIBLE : View.GONE);
//		b_speak.setVisibility(((A.isProVersion || A.inviteProVersion) && A.showSpeakButton) ? View.VISIBLE : View.GONE);
//		b_orientation.setVisibility(A.showOrientationButton ? View.VISIBLE : View.GONE);
//		b_visual.setVisibility(A.showVisualButton ? View.VISIBLE : View.GONE);
//		b_control.setVisibility(A.showControlButton ? View.VISIBLE : View.GONE);
//		b_misc.setVisibility(A.showMiscButton ? View.VISIBLE : View.GONE);
//		b_daynight.setVisibility(A.showDayNightButton ? View.VISIBLE : View.GONE);
//		b_autoscroll.setVisibility(A.showAutoscrollButton ? View.VISIBLE : View.GONE);
//		b_bookmark.setVisibility(A.showBookmarkButton ? View.VISIBLE : View.GONE);
//		b_chapter.setVisibility(A.showChapterButton ? View.VISIBLE : View.GONE);
//		b_search.setVisibility(A.showSearchButton ? View.VISIBLE : View.GONE);
//		b_brightness.setVisibility(A.showBrightnessButton ? View.VISIBLE : View.GONE);
//		b_tilt.setVisibility(A.showTiltButton ? View.VISIBLE : View.GONE);
//		b_fontsize.setVisibility(A.showFontSizeButton ? View.VISIBLE : View.GONE);
//		b_shutdown.setVisibility(A.showShutDownButton ? View.VISIBLE : View.GONE);

        b_chapter.setOnClickListener(this);
        b_fontsize.setOnClickListener(this);
        b_brightness.setOnClickListener(this);
        b_option.setOnClickListener(this);
        b_daynight.setOnClickListener(this);
        b_storeUrl.setOnClickListener(this);
        b_toShelf.setOnClickListener(this);
        b_read.setOnClickListener(this);
        b_write.setOnClickListener(this);
        daynightLay.setOnClickListener(this);
        daynightLay.setOnLongClickListener(this);
        menuB.setOnClickListener(this);

//		b_orientation.setOnClickListener(this);
//		b_visual.setOnClickListener(this);
//		b_control.setOnClickListener(this);
//		b_misc.setOnClickListener(this);
//		b_speak.setOnClickListener(this);
//		b_autoscroll.setOnClickListener(this);
//		b_bookmark.setOnClickListener(this);
//		b_tilt.setOnClickListener(this);
//		b_shutdown.setOnClickListener(this);
//		b_search.setOnClickListener(this);
//
//		b_orientation.setOnLongClickListener(this);
//		b_option.setOnLongClickListener(this);
//		b_visual.setOnLongClickListener(this);
//		b_control.setOnLongClickListener(this);
//		b_misc.setOnLongClickListener(this);
//		b_daynight.setOnLongClickListener(this);
//		b_speak.setOnLongClickListener(this);
//		b_autoscroll.setOnLongClickListener(this);
//		b_bookmark.setOnLongClickListener(this);
//		b_chapter.setOnLongClickListener(this);
//		b_brightness.setOnLongClickListener(this);
//		b_tilt.setOnLongClickListener(this);
//		b_fontsize.setOnLongClickListener(this);
//		b_search.setOnLongClickListener(this);

        chromeIv.setOnLongClickListener(this);
    }

    private void clearTxtView() {
        txtView.setText("");
        txtScroll.scrollTo(0, 0);
        txtView2.setText("");
        if (txtView.getLayout() == null)
            try {
                txtView.assumeLayout();
                txtView2.assumeLayout();
            } catch (Exception e) {
                A.error(e);
            }
    }

    private void showOptionsMenu(final View v) {
/*
		if (inPreShow)
			return;
		if (A.isInAutoScroll)
			do_AutoScroll(false);
		if (A.isSpeaking)
			stop_speak();
		saveLastPostion(true);
		A.SaveOptions(this);//防止内存不足时出错, 先提前保存

		if (v == null)
			inverseLayoutVisible(true);
		hideProgressDlg();
		hideScrollbarProgress();

		String[] original = this.getResources().getStringArray(R.array.options_menu);
		String[] cacheUrls = new String[8]; //10
		cacheUrls[0] = original[0];
		cacheUrls[1] = original[1];
		cacheUrls[2] = original[2];
		cacheUrls[3] = original[3];
		cacheUrls[4] = "-";
		cacheUrls[5] = original[4];
		cacheUrls[6] = getString(R.string.share) + "...";
		cacheUrls[7] = getString(R.string.menu_3);

		if (isPdfNoflow())
			cacheUrls[0] = "PDF " + getString(R.string.button_options);


		if (v == null)
			new MyDialog.Builder(this)
				.setItems(cacheUrls, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						doOptionsMenuClick(which, null);
					}
				}).show();
		else {
			MyDialog menu = new MyDialog(this, v, cacheUrls, new MyDialog.MenuItemClick() {
				public void onClick(int which) {
					doOptionsMenuClick(which, v);
				}
			});
			menu.forceNightMode = true;
			menu.show();
		}
*/

//		if (!fromButton)
//			new MyDialog.Builder(this)
//					.setItems(cacheUrls, new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int which) {
//							doOptionsMenuClick(which, fromButton);
//						}
//					}).show();
//		else new MyDialog(this, menuB, cacheUrls, new MyDialog.MenuItemClick() {
//			public void onClick(int which) {
//				doOptionsMenuClick(which, fromButton);
//			}
//		}).show(A.d(48), -A.d(48));
    }

    protected void showMoreOptionsMenu(View v) {
/*		String[] original = this.getResources().getStringArray(R.array.more_options_menu);
		String[] cacheUrls;
		cacheUrls = new String[original.length + 2];
		cacheUrls[0] = getString(R.string.speak);
		for (int i = 0; i < original.length; i++)
			cacheUrls[i + 1] = original[i];
		cacheUrls[cacheUrls.length - 1] = getString(R.string.button_brightness);

		if (v == null)
			new MyDialog.Builder(this).setItems(cacheUrls, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					doMoreOptionsMenuClick(which);
				}
			}).show();
		else new MyDialog(this, v, cacheUrls, new MyDialog.MenuItemClick() {
			public void onClick(int which) {
				doMoreOptionsMenuClick(which);
			}
		}).show(A.d(48), -A.d(48));*/
    }

    private void shareCurrentBook(View v) {
        String[] items = new String[3];
        items[0] = getString(R.string.share);
        items[1] = getString(R.string.send_file);
        items[2] = getString(R.string.bug_report);

        if (v == null)
            new MyDialog.Builder(this)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            doShareMenu(which);
                        }
                    }).show();
        else new MyDialog(this, v, items, new MyDialog.MenuItemClick() {
            public void onClick(int which) {
                doShareMenu(which);
            }
        }).show(A.d(40), -A.d(40));
    }

    private void doShareMenu(int which) {
        inverseLayoutVisible(true);
//		if (which == 0)
//			doAppInvites();
        if (which == 0)
            doShareReadState(true);
        if (which == 1)
            A.sendFile(ActivityTxt.this, A.lastFile);
        if (which == 2) {
            String filename = BaseCompressor.getZRCacheFile(A.lastFile);
            String device = A.createAppInfo();
            String text = "Please describe how to repeat the bug:\n-------------\n" + device + "\n-------------\n";
            String[] tos = {"soushushenqi@163.com"};
            Bitmap bm = getPageShot(false, false);
            String screenshot = A.book_cache + "/screenshot.jpg";
            T.bitmapToFile(bm, screenshot);
            ArrayList<Uri> uris = new ArrayList<Uri>();
            uris.add(Uri.fromFile(new File(screenshot)));
            uris.add(Uri.fromFile(new File(filename)));
            T.copyFile(A.xml_files_folder + "/" + A.OPTIONS_FILE + ".xml", A.book_cache + "/settings.txt", true);
            uris.add(Uri.fromFile(new File(A.book_cache + "/settings.txt")));

            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.putExtra(Intent.EXTRA_EMAIL, tos);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Bug report: " + A.versionTag);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            intent.setType("*/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            startActivity(Intent.createChooser(intent, ""));

            A.myDebugTag++;
            if (A.myDebugTag > 1)
                T.showToastText(this, device, 1);
        }
    }

    private String getShareTitle(int type) {
        String text = A.getBookName();
        if (getBookType() == A.FILE_EBOOK && A.ebook != null && !A.ebook.getAuthor().equals(""))
            text = text + (type == 0 ? " - " : " ") + A.ebook.getAuthor();
        return text;
    }

    private void showThemeWindow() {
//		inverseLayoutVisible(true);
//		final boolean italic = A.fontItalic;
//		hideScrollbarProgress();
//		new PrefTheme(ActivityTxt.this, new OnGetTheme() {
//			public void getTheme(String name) {
//				if (isPdfNoflow()) {
//					pdfRemoveThumb();
//					A.pdf_theme = Integer.valueOf(name);
//					pdfUpdateView(true);
//					setStatusBarProperties(true);
//					return;
//				}
//				loadTheme(name, false);
//				A.saveTheme(A.TEMP_THEME, true);
//				if (web != null)
//					reloadWebView();
//				if (italic != A.fontItalic && A.textJustified) {
//					saveLastPostion(true);
//					restartReaderToTxt(); //restart
//				}
//			}
//		}, false).show();
    }


    private void checkLollipopFullScreenState() {
        if (!A.fullscreen && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(0xff000000);
//			getWindow().setStatusBarColor(A.isNightState()? 0xff303135 : 0xff595a5f);
        }
    }

    public void showMiscOptions() {
    }

    private void checkOptionsChanged() {
        if (preOpenBookAnim != A.openBookAnim) {
            A.forceRebootToTxt = true;
            A.SaveOptions(this);
            finish();
            if (!T.isNull(ActivityMain.selfPref))
                ActivityMain.selfPref.finish();
            startActivity(new Intent(this, ActivityMain.class));
            return;
        }

        if (checkFlipChanged())
            return;

        if (preFullScreen != A.fullscreen) {
            if (A.isCutoutScreen()) {
                restartReaderIntent();
                return;
            }
            checkLollipopFullScreenState();
            setFullscreen(false);
            if (A.isFullScreenPhone())
                setStatusBarProperties(true);
        }

        if (A.fullscreen && (fullscreenWithStatus != A.fullscreenWithStatus || preFitCutout != A.fitCutout)) {
            restartReaderIntent();
            return;
        }

        if (!preChapterStyle.equals(WBpub.getTitleStyle1())) {
            restartReaderIntent();
            return;
        }

        baseFrame.setKeepScreenOn(A.keepScreenAwake);

        if (preLandscape2Page != A.landscape2PageMode && A.isLandscape() && !isPdf()) {
            justEnabledDualPage = true;
            restartReaderToTxt();
            return;
        } else if (preIndent != A.indentParagraph) {
            A.resetHighlights();
            if (getBookType() == A.FILE_TXT) {
                A.clearTxts2();
                showTxtByPosition(A.lastPosition, null);
            } else {
                clearpreNextCache();
                reloadBook();
            }
        } else {
            String disableCSS2 = "" + A.disableCSS + A.cssFontStyle + A.cssFontColor + A.cssFontSize + A.cssAlignment + A.cssJustify
                    + A.cssIndent + A.cssLineSpace + A.cssOthers;
            if ((preCssFont != A.useCssFont || !preDisableCSS.equals(disableCSS2))
                    && (getBookType() == A.FILE_EBOOK || getBookType() == A.FILE_HTML)) {
                createProgressDlg(A.lastFile);
                A.ebook = null;
                reloadBook();
            }
        }

        if (preShowStatusBar != A.showStatusbar || preShowRemaingTime != A.showRemainingTime || preRemaingTimeInStatusBar != A.remaingTimeInStatusBar) {
            if (dualPageEnabled()) {
                restartReaderToTxt();
            } else {
                checkStatusBar();
                clearCurlCache();
            }
        }

        if (preAutoUpdateReadingFollow != A.autoUpdateReadingFollow && A.autoUpdateReadingFollow)
            handler.sendEmptyMessageDelayed(UPDATE_WEB_CHAPTERS, 100);
    }

    private boolean checkFlipChanged() {
        if (preFlipAnimation != A.flip_animation)
            updateFlipIndicator();
        needToVerifyFlip = false;
        boolean isCurl = preFlipAnimation != A.flip_animation && A.isFlipCurl();
        if (!isCurl && prehori_fling_animation != A.hori_fling_animation && A.allow_scroll_horizontally)
            if (A.hori_fling_animation == A.FLIP_CURL3D || A.hori_fling_animation == A.FLIP_CURL3D_G)
                isCurl = true;
        if (isCurl) {
            if (curl3d == null || curl3d.mTurnType != curl3d.getStyle()) {
                restartReaderToTxt(); //restart
                return true;
            } else
                curl3d.clearAllShots();
        }
        preFlipAnimation = A.flip_animation;
        return false;
    }

    public void showControlOptions() {
//		inverseLayoutVisible(true);
//		final int preScreenState = A.screenState;
//		PrefControl pc = new PrefControl(ActivityTxt.this);
//		pc.setOnDismissListener(new OnDismissListener() {
//			public void onDismiss(DialogInterface dialog) {
//				A.setSystemUiVisibility(true);
//				if (preScreenState != A.screenState) {
//					isPressScreenStateButton = false;
//					setScreenOrientation();
//				}
//				registerHardwares();
//			}
//		});
//		pc.show();
    }

    public void showVisualOptions() {
//		inverseLayoutVisible(true); //why not hide system bar sometimes
//		saveLastPostion(true);
//		final int flip_animation = A.flip_animation;
//		if (isPdfNoflow()) {
//			PrefPdf pp = new PrefPdf(this);
//			pp.setOnDismissListener(new OnDismissListener() {
//				public void onDismiss(DialogInterface dialog) {
//					A.setSystemUiVisibility(true);
//					if (flip_animation != A.flip_animation && A.isFlipCurl()) {
//						if (curl3d == null || curl3d.mTurnType != curl3d.getStyle())
//							restartReaderToTxt(); //restart
//						else
//							curl3d.clearAllShots();
//					}
//				}
//			});
//			pp.show();
//			return;
//		}
//
//		final boolean italic = A.fontItalic;
//		final String hyphenationLang = A.hyphenationLang;
//		PrefVisual pv = new PrefVisual(ActivityTxt.this);
//		pv.setOnDismissListener(new OnDismissListener() {
//			public void onDismiss(DialogInterface dialog) {
//				A.setSystemUiVisibility(true);
//				if (italic != A.fontItalic && A.textJustified && getBookType() != A.FILE_TXT)
//					restartReaderToTxt(); //restart
//				else if (flip_animation != A.flip_animation && A.isFlipCurl()) {
//					if (curl3d == null || curl3d.mTurnType != curl3d.getStyle())
//						restartReaderToTxt(); //restart
//					else
//						curl3d.clearAllShots();
//				}
//
//				if (web == null)
//					checkStatusBar();
//				else
//					reloadWebView();
//				if (showStatusbar())
//					setStatusBarProperties(true);
//				if (A.textHyphenation && !hyphenationLang.equals(A.hyphenationLang)) {
//					A.setHyphenation();
//					reloadBook();
//				}else
//					resetFlipCache();
//			}
//		});
//		pv.show();
    }

//	private boolean outOfMemoryHint() {
//		if (T.isOutOfMemoryError) {
//			T.showToastText(this, "Low memory, restart reader to continue...");
//			return true;
//		}
//		return false;
//	}

    protected void showExitMenu() {
        terminate_reader();
//		try {
//			if (fromLongTap) {
//				String[] tmps = this.getResources().getStringArray(R.array.options_menu);
//				new MyDialog(this, b_options, getResources().getStringArray(R.array.exit_menu), tmps[6], new MyDialog.MenuItemClick() {
//					public void onClick(int which) {
//						if (which == 0)
//							do_back_to_book_stack();
//						if (which == 1)
//							T.openHomeScreen(ActivityTxt.this);
//						if (which == 2)
//							do_exit_reader();
//					}
//				}).show();
//			} else
//				new MyDialog.Builder(this).setItems(R.array.exit_menu, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						if (which == 0)
//							do_back_to_book_stack();
//						if (which == 1)
//							T.openHomeScreen(ActivityTxt.this);
//						if (which == 2)
//							do_exit_reader();
//					}
//				}).show();
//		} catch (Exception e) {
//			A.error(e);
//		}
    }

    private void doMoreOptionsMenuClick(int which) {
        inverseLayoutVisible(true);
        which--;
        switch (which) {
            case -1:
                do_speak();
                break;
            case 0:
                do_text_select(false);
                break;
            case 1:
                do_search();
                break;
            case 2:
                do_book_info();
                break;
            case 3:
                do_show_chapters(0);
                break;
            case 4:
                do_prior_chapter();
                break;
            case 5:
                do_next_chapter();
                break;
            case 6:
                do_prior_file();
                break;
            case 7:
                do_next_file();
                break;
            case 8:
                do_bookmark();
                break;
            case 9:
                inverseLayoutVisible(false);
                break;
            case 10:
                do_font_size();
                break;
            case 11:
                do_brightness();
                break;
        }
    }

    private boolean do_prior_chapter() {
        boolean found1 = false;
//		forceNotSaveZeroPosition = false;

        switch (getBookType()) {
            case A.FILE_TXT:
            case A.FILE_HTML:
                if (A.getTxtChapters().size() > 0) {
                    int chapterId = A.getTxtChapterId(A.lastPosition, null, null);
                    if (chapterId > 0) {
                        long old = A.lastPosition;
                        A.lastPosition = A.getTxtChapters().get(chapterId - 1).getDisplayPosition(chapterId - 1);
                        if (old == A.lastPosition && chapterId > 1)
                            A.lastPosition = A.getTxtChapters().get(chapterId - 2).getDisplayPosition(chapterId - 2);
                        found1 = true;
                        reloadBook();
                    } else if (A.lastPosition > 0) {
                        A.lastPosition = 0;
                        found1 = true;
                        reloadBook();
                    }
                }
                break;

            case A.FILE_EBOOK:
                if (A.lastChapter > 0) {
                    found1 = true;
                    A.lastChapter--;
                    A.lastPosition = 0;
                    A.lastSplitIndex = 0;
                    ebookPageUp = false;
                    clearTxtView();
                    A.SaveOptions(this);
                    if (isBigChapter(A.lastChapter))
                        createProgressDlg(A.ebook.getChapters().get(A.lastChapter).name);
                    reloadBook();
                }
                break;

            case A.FILE_PDF:
                if (!isBeginOfBook()) {
                    do_page_up();
                    found1 = true;
                }
                break;
        }

        if (!found1) {
            if (!isPdf())
                T.showToastText(ActivityTxt.this, getString(R.string.no_prior_chapter));
        } else
            resetFlipCache();
        return found1;
    }

    private boolean do_next_chapter() {
        boolean found2 = false;
//		forceNotSaveZeroPosition = false;

        switch (getBookType()) {
            case A.FILE_TXT:
            case A.FILE_HTML:
                if (A.getTxtChapters().size() > 0) {
                    int chapterId = A.getTxtChapterId(A.lastPosition, null, null);
                    if (chapterId < A.getTxtChapters().size() - 1) {
                        found2 = true;
                        if (chapterId == 0 && A.lastPosition < A.getTxtChapters().get(0).getDisplayPosition(0)) {
                            A.lastPosition = A.getTxtChapters().get(0).getDisplayPosition(0);
                        } else {
                            long old = A.lastPosition;
                            A.lastPosition = A.getTxtChapters().get(chapterId + 1).getDisplayPosition(chapterId + 1);
                            if (old == A.lastPosition && chapterId < A.getTxtChapters().size() - 2)
                                A.lastPosition = A.getTxtChapters().get(chapterId + 2).getDisplayPosition(chapterId + 2);
                        }
                        reloadBook();
                    }
                }
                break;

            case A.FILE_EBOOK:
                if (A.ebook != null && A.lastChapter < A.ebook.getChapters().size() - 1) {
                    found2 = true;
                    A.lastChapter++;
                    A.lastPosition = 0;
                    A.lastSplitIndex = 0;
                    ebookPageUp = false;
                    clearTxtView();
                    A.SaveOptions(this);
                    if (isBigChapter(A.lastChapter))
                        createProgressDlg(A.ebook.getChapters().get(A.lastChapter).name);
                    reloadBook();
                }
                break;

            case A.FILE_PDF:
                if (!isEndOfBook()) {
                    do_page_down();
                    found2 = true;
                }
                break;
        }

        if (!found2) {
            if (!isPdf())
                T.showToastText(ActivityTxt.this, getString(R.string.no_next_chapter));
        } else
            resetFlipCache();
        return found2;
    }

    public void inverseLayoutVisible(boolean forceHide) {
        updateBarTime();
        hide_ebook_cover();
        pdfHideSearchLay();
        hideSearchPanel();

        if (inPreShow) {
            if (!forceHide && (new File(A.lastFile)).length() > A.BIG_FILESIZE) {
                showHintAtLoadingEnd = true;
                T.showToastText(this, getString(R.string.loading_info));
            }
            return;
        }

        if (!layoutVisible)
            A.baseFameHeight = baseFrame.getHeight();

        if (forceHide || setLayoutTime == 0 || SystemClock.elapsedRealtime() - setLayoutTime > 500) { //0.5second
            if (!layoutVisible && !forceHide) {

                if (A.isInAutoScroll)
                    do_AutoScroll(false);
                if (A.isSpeaking)
                    stop_speak();
                if (A.fullscreen && !A.fullscreenWithStatus)
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

                if (initTopBottomLay(true)) {
                    justBackFromOtherPanel = false;
                    setFlipViewINVISIBLE(true);
                    setCurlViewINVISIBLE();
                    restoreAppendedBottomHandler(false);
                    pdfSetImmersiveMode(true);
                    setFullscreen(true);
                    setTopBottomLayVisible(View.VISIBLE);
                    layoutAnimationShow();
                    pdfShowThumb(true);
                    pdfShowLockIcon(true);
                    setChromeVisibility(true);
//					nav_return.setVisibility(View.GONE);

                    layoutVisible = true;
                    showSeekBarProgress();
                    setLayoutTime = SystemClock.elapsedRealtime();

                    hideLinkBackButton();
                    showBottomIconsHint();
                    A.setSystemUiVisibility(false);
                    flippingAnimationTime = 0;
                    A.moveStart = moveStart2;
//					if (dPadKeyPressed)
//						new Handler() {
//							public void handleMessage(Message msg) {
//								buttonsLay.requestFocus();
//							}
//						}.sendEmptyMessageDelayed(0, 300);
                }

            } else {
                if (layoutVisible) {
                    restoreAppendedBottomHandler(true);
                    if (A.fullscreen && !A.fullscreenWithStatus)
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    if (topLay.getVisibility() == View.VISIBLE)
                        layoutAnimationHide();
                    pdfShowThumb(false);
                    pdfShowLockIcon(false);
                    setTopBottomLayVisible(View.GONE);
                    setChromeVisibility(false);
                    layoutVisible = false;
                    setLayoutTime = SystemClock.elapsedRealtime();
                    A.setSystemUiVisibility(true);
                    pdfSetImmersiveMode(false);
                    A.moveStart = moveStart2;
                }
            }
        }
    }

    //	int bottomBackgroundColor = 0xe8111111;
    int bottomBackgroundColor = 0xf1111111;

    private void setTopBottomLayVisible(int visible) {
        if (topLay == null || bottomLay == null)
            return;

        topLay.setVisibility(visible);
        bottomLay.setVisibility(visible);
        if (visible != View.GONE) {
            topLay.findViewById(R.id.sv).setBackgroundColor(bottomBackgroundColor);
            topLay.findViewById(R.id.notificationBar).setBackgroundColor(0xff111111);
            urlLay.setBackgroundColor(0xd1111111);
            funcLay.setBackgroundColor(bottomBackgroundColor);
            menuLv.setBackgroundColor(bottomBackgroundColor);
            showBottomBar(0);
            setNightModeIndicator();
            nav_return.setTag(null);
            nav_return.setVisibility(link_backs.size() == 0 ? View.GONE : View.VISIBLE);
        } else if (needToVerifyFlip)
            if (checkFlipChanged())
                return;

        updateBarIcons();
    }

    private void updateBarIcons() {
        int visible = isWebBook() ? View.VISIBLE : View.GONE;
        downloadIv.setVisibility(visible);
        onlineSwitch.setVisibility(visible);
        onlineSwitch.setText(A.fanti(inWebReading ? "优化" : "原版"));
        b_option.getDrawable().setColorFilter(0xffffffff, PorterDuff.Mode.SRC_IN);
        if (isWebBook())
            downloadIv.getDrawable().setColorFilter(0xffffffff, PorterDuff.Mode.SRC_IN);
        urlLay.setVisibility(visible);
        if (isWebBook()) {
            urlTv.setText(A.ebook.getChapters().get(A.lastChapter).url);
            siteTv.setText(A.ebook.online_site.name());
        }
//		chapterTv.setText(A.ebook != null ? A.ebook.getChapters().get(A.lastChapter).name : T.getFilename(A.lastFile));
//		menuB.setVisibility(isWebBook() || A.ebook == null? View.VISIBLE : View.GONE);
    }

    int bottomType, preBottomType;
    boolean justBackFromOtherPanel;

    void showBottomBar(int type) { //-1 hide all, 0 main, 1 font, 2 brightness, 3 menu, 4 color, 5 space, 6, night
        preBottomType = bottomType;
        bottomType = type;
        initTopBottomLay(false);
        bottomLay.findViewById(R.id.shuping_lay).setVisibility(hasShuPingPage ? View.VISIBLE : View.GONE);
        showCommentCount();
        preLay = nextLay = null;
        if (type == -1) {
            slideDownBottomLay();
            bottomLay.setVisibility(View.GONE);
            if (needToVerifyFlip)
                checkFlipChanged();
            return;
        }

        if (type > 0 && bottomLay.getVisibility() == View.VISIBLE)
            for (int i = 0; i < bottomLay.getChildCount(); i++)
                if (bottomLay.getChildAt(i) != daynightLay && bottomLay.getChildAt(i).getVisibility() == View.VISIBLE)
                    preLay = bottomLay.getChildAt(i);

        if (type == 1)
            initFontPanel();
        if (type == 2)
            initBrightPanel();
        if (type == 4)
            initColorPanel();
        if (type == 5)
            initSpacePanel();
        if (type == 6)
            initNightPanel();

        if (type == 2 && brightnessPanel == null && A.brightnessValue != -100)
            T.showToastText(this, (A.brightnessValue + "%"), 0, Gravity.CENTER);

        funcLay.setVisibility(type == 0 ? View.VISIBLE : View.GONE);
        menuLv.setVisibility(type == 3 ? View.VISIBLE : View.GONE);
        if (fontPanel != null)
            fontPanel.setVisibility(type == 1 ? View.VISIBLE : View.GONE);
        if (brightnessPanel != null)
            brightnessPanel.setVisibility(type == 2 ? View.VISIBLE : View.GONE);
        if (colorPanel != null)
            colorPanel.setVisibility(type == 4 ? View.VISIBLE : View.GONE);
        if (spacePanel != null)
            spacePanel.setVisibility(type == 5 ? View.VISIBLE : View.GONE);
        if (nightPanel != null)
            nightPanel.setVisibility(type == 6 ? View.VISIBLE : View.GONE);

        bottomLay.setVisibility(View.VISIBLE);
        b_daynight.setVisibility(type != 0 || topLay.getVisibility() == View.GONE ? View.GONE : View.VISIBLE);
        b_storeUrl.setVisibility(showStoreButton() ? View.VISIBLE : View.GONE);
        b_toShelf.setVisibility(BookDb.getBook(A.lastFile) == null ? View.VISIBLE : View.GONE);

        for (int i = 0; i < bottomLay.getChildCount(); i++)
            if (bottomLay.getChildAt(i) != daynightLay && bottomLay.getChildAt(i).getVisibility() == View.VISIBLE)
                nextLay = bottomLay.getChildAt(i);

        if (preLay != null && nextLay != null) {
            if (preLay != nextLay) {
                preLay.setVisibility(View.VISIBLE);
                nextLay.setVisibility(View.GONE);
            }
            slideDownBottomLay();
        } else
            sideUpBottomLay();
    }

    private void slideDownBottomLay() {
        Animation a = new TranslateAnimation(0, 0, 0, bottomLay.getHeight());
        a.setDuration(layDuration);
        a.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (nextLay != null)
                    sideUpBottomLay();
                else
                    bottomLay.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        bottomLay.startAnimation(a);
    }

    View preLay, nextLay;

    private void sideUpBottomLay() {
        if (preLay != null && preLay != nextLay)
            preLay.setVisibility(View.GONE);
        if (nextLay != null) {
            nextLay.setVisibility(View.VISIBLE);
            bottomLay.setVisibility(View.VISIBLE);
            if (nextLay.getHeight() == 0)
                nextLay.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            Animation a = new TranslateAnimation(0, 0, bottomLay.getHeight() - A.navBarheight, 0);
            a.setDuration(layDuration);
            bottomLay.startAnimation(a);
        }
    }

    void do_bottom_more() {
        String[] items = new String[]{A.isLandscape() ? getString(R.string.shuping) : getString(R.string.hengping), getString(R.string.speak),
                getString(R.string.zidongfanye), getString(R.string.search), getString(R.string.shezhi)};
        int[] res = new int[]{R.drawable.b3_screenorientation, R.drawable.b3_speak, R.drawable.b3_autoscroll,
                R.drawable.b3_search, R.drawable.b3_options};
        showBottomMenu(items, res, 0);
    }

    private void showBottomSearchOptions() {
        String[] items = new String[]{getString(R.string.sousuobenshuneirong) + (isWebBook() ? getString(R.string.jinxianyixiazai) : ""),
                getString(R.string.sousuoxiangguanwangye), getString(R.string.sousuobenshushuping),
                getString(R.string.shujisousuo1), getString(R.string.soujisousuo2)};
        int[] res = new int[]{R.drawable.b3_search, R.drawable.b3_search, R.drawable.b3_search, R.drawable.b3_search, R.drawable.b3_search};
        showBottomMenu(items, res, 1);
    }

    private void showBottomTtsOptions() {
        String[] items = new String[]{
                A.fanti("设置语音引擎"),
                A.fanti(A.tts_stop_enable ? "取消自动停止(" + A.tts_stop_time + "分钟)" : "设置自动停止"),
                A.fanti(A.tts_interval_enable ? "取消朗读间隔(" + A.tts_interval_time + "毫秒)" : "设置朗读间隔"),
                A.fanti("设置字符过滤")};
        int[] res = new int[]{R.drawable.b3_speak, R.drawable.b3_speak, R.drawable.b3_speak, R.drawable.b3_speak};

        if (Build.VERSION.SDK_INT >= 26) {
            String[] items2 = new String[items.length + 1];
            for (int i = 0; i < items.length; i++)
                items2[i] = items[i];
            items2[items2.length - 1] = A.fanti("设置耳机蓝牙控制(" + (A.doHeadsetKey == A.DO_SPEAK ? "已启用" : "已禁用") + ")");
            items = items2;
            res = new int[]{R.drawable.b3_speak, R.drawable.b3_speak, R.drawable.b3_speak, R.drawable.b3_speak, R.drawable.b3_speak};
        }

        showBottomMenu(items, res, 100);
    }

    void showBottomMenu(CharSequence[] items, int[] res, int level) {
        menuLv.setAdapter(new BottomMenuAdapter(items, res, level));
        if (level == 100) {
            layoutVisible = true;
            bottomLay.bringToFront();
            shadeView.bringToFront();
        }
        showBottomBar(3);
    }

    private class BottomMenuAdapter extends BaseAdapter {
        CharSequence[] items;
        int[] res;
        int level;

        public BottomMenuAdapter(CharSequence[] items, int[] res, int level) {
            this.items = items;
            this.res = res;
            this.level = level;
        }

        public int getCount() {
            return items.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout lay = convertView != null ? (LinearLayout) convertView :
                    (LinearLayout) LayoutInflater.from(ActivityTxt.this).inflate(R.layout.menu_row, null);
            ImageView iv = (ImageView) lay.findViewById(R.id.iv);
            TextView tv = (TextView) lay.findViewById(R.id.name);
            if (res[position] > 0) {
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(res[position]);
            } else
                iv.setVisibility(View.INVISIBLE);
            tv.setVisibility(View.VISIBLE);
            tv.setText(items[position]);
//			lay.setBackgroundResource(A.getSelectedRes(ActivityTxt.this));
            lay.setBackgroundResource(R.drawable.my_list_selector);
            lay.setTag(position);
            lay.setOnClickListener(onClick);
            return lay;
        }

        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (Integer) v.getTag();
                if (level == 0) {
                    switch (pos) {
                        case 0:
                            doScreenOrientation();
                            break;
                        case 1:
                            do_speak();
                            break;
                        case 2:
                            do_AutoScroll(true);
                            break;
                        case 3:
                            showBottomSearchOptions();
                            break;
                        case 4:
                            showReadingOptionsAct();
                            break;
                    }
                    if (pos != 3) inverseLayoutVisible(true);
                }
                if (level == 1) {
                    switch (pos) {
                        case 0:
                            do_search();
                            break;
                        case 1:
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com/s?ie=utf-8&wd=" + getShareTitle(1))));
                            break;
                        case 2:
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com/s?ie=utf-8&wd=" + A.getBookName() + " 书评")));
                            break;
                        case 3:
                            doWebBookSearch(A.getBookName());
                            break;
                        case 4:
                            String author = A.ebook != null ? A.ebook.getAuthor() : "";
                            doWebBookSearch(author.startsWith("(") ? "" : author);
                            break;
                    }
                    inverseLayoutVisible(true);
                }
                if (level == 100) {
                    switch (pos) {
                        case 0:
                            T.openTtsOptions(ActivityTxt.this);
                            break;
                        case 1:
                            if (A.tts_stop_enable) {
                                A.tts_stop_enable = false;
                                getSpeakHandler().removeMessages(STOP_SPEAK);
                                T.showToastText(ActivityTxt.this, "已取消自动停止朗读");
                            } else {
                                final EditText et = new EditText(ActivityTxt.this);
                                et.setText("" + A.tts_stop_time);
                                et.setSingleLine();
                                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                                new MyDialog.Builder(ActivityTxt.this).setTitle(A.fanti("设置自动停止时间(分钟)"))
                                        .setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        int i = T.string2Int(et.getText().toString());
                                        if (i != A.tts_stop_time && i > 0) {
                                            A.tts_stop_time = i;
                                        }
                                        A.tts_stop_enable = true;
                                        setStopTtsHandler();
                                    }
                                }).setNegativeButton("取消", null).show();
                            }
                            break;
                        case 2:
                            if (A.tts_interval_enable) {
                                A.tts_interval_enable = false;
                                T.showToastText(ActivityTxt.this, "已取消朗读间隔, 下次开始朗读时生效");
                            } else {
                                final EditText et = new EditText(ActivityTxt.this);
                                et.setText("" + A.tts_interval_time);
                                et.setSingleLine();
                                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                                new MyDialog.Builder(ActivityTxt.this).setTitle(A.fanti("设置朗读间隔(毫秒)"))
                                        .setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        int i = T.string2Int(et.getText().toString());
                                        if (i != A.tts_interval_time && i > 0) {
                                            A.tts_interval_time = i;
                                        }
                                        A.tts_interval_enable = true;
                                        T.showToastText(ActivityTxt.this, "朗读间隔设置为" + A.tts_interval_time + "秒, 下次开始朗读时生效");
                                    }
                                }).setNegativeButton("取消", null).show();
                            }
                            break;
                        case 3:
                            show_tts_chars_filter();
                            break;
                        case 4:
                            if (A.doHeadsetKey != A.DO_SPEAK) {
                                A.doHeadsetKey = A.DO_SPEAK;
                                A.doMediaPlayPause = A.DO_SPEAK;
                                A.doMediaPlayNext = A.DO_PAGE_DOWN;
                                A.doMediaPlayPrevious = A.DO_PAGE_UP;
                                T.showAlertText(ActivityTxt.this, A.fanti("\n已启用耳机及蓝牙控制, 重启阅读器后生效."));
                            } else {
                                A.doHeadsetKey = A.doMediaPlayPause = A.doMediaPlayNext = A.doMediaPlayPrevious = A.DO_NONE;
                                T.showToastText(ActivityTxt.this, A.fanti("已禁用耳机及蓝牙控制"), 0, Gravity.CENTER);
                            }
                            break;
                    }
                    inverseLayoutVisible(true);
                }
            }
        };
    }

    private void doWebBookSearch(String key) {
		/*final EditText et = new ClearableEditText(this);
		et.setText(key);
		et.setSingleLine();
		new MyDialog.Builder(this)
				.setTitle("搜索书源")
				.setView(et)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String key = et.getText().toString().trim();
						if (!T.isNull(key)){
							WB.doWebSearchWithSelect(ActivityTxt.this, key);
							inverseLayoutVisible(true);
						}
					}
				})
				.setNegativeButton(R.string.cancel, null)
				.show();*/
        WB.doWebSearchWithSelect(this, key);
    }

    //----------------- tts chars filter ------------

    private void show_tts_chars_filter() {
        new MyDialog.Builder(this).setTitle(getString(R.string.tts_filter))
                .setView(createTtsFiltersView()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveFilterList();
                A.saveTTSFilterToFile(null);
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                A.ttsFilters = null;
                A.initTTSFilters();
            }
        }).setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                A.log("tts filter cancelled");
                saveFilterList();
                A.saveTTSFilterToFile(null);
            }
        }).show();
    }

    LinearLayout filterLay;

    private View createTtsFiltersView() {
        A.initTTSFilters();
        LinearLayout base = new LinearLayout(ActivityTxt.this);
        base.setPadding(A.d(10), A.d(10), A.d(6), A.d(6));
        base.setOrientation(LinearLayout.VERTICAL);
        filterLay = new LinearLayout(ActivityTxt.this);
        filterLay.setOrientation(LinearLayout.VERTICAL);

        CheckBox cb = new CheckBox(ActivityTxt.this);
        cb.setText(R.string.use_regular_expression);
        cb.setChecked(A.tts_filter_with_regular_expression);
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                A.tts_filter_with_regular_expression = isChecked;
            }
        });
        cb.setTextSize(13);

        LinearLayout importLay = new LinearLayout(ActivityTxt.this);
        TextView tv1 = new TextView(ActivityTxt.this);
        TextView tv2 = new TextView(ActivityTxt.this);
        tv1.setTextSize(13);
        tv2.setTextSize(13);
        tv1.setPadding(A.d(6), A.d(4), A.d(6), A.d(0));
        tv2.setPadding(A.d(6), A.d(4), A.d(6), A.d(0));
        tv1.setBackgroundResource(R.drawable.my_list_selector);
        tv2.setBackgroundResource(R.drawable.my_list_selector);
        tv1.setTextColor(CSS.LINK_COLOR);
        tv2.setTextColor(CSS.LINK_COLOR);
        tv1.getPaint().setUnderlineText(true);
        tv2.getPaint().setUnderlineText(true);
        tv1.setText(getString(R.string.import_ebooks));
        tv2.setText(getString(R.string.export));
        importLay.setOrientation(LinearLayout.HORIZONTAL);
        importLay.addView(tv1);
        importLay.addView(tv2);

        tv1.setOnClickListener(new View.OnClickListener() { //import
            @Override
            public void onClick(View v) {
                final EditText et2 = new EditText(ActivityTxt.this);
                String filename = T.getFilePath(A.lastFile) + "/" + T.getOnlyFilename(A.lastFile) + ".ttsfilter";
                if (!T.isFile(filename))
                    filename = "/sdcard/" + T.getOnlyFilename(A.lastFile) + ".ttsfilter";
                et2.setText(filename);
                new MyDialog.Builder(ActivityTxt.this)
                        .setTitle(R.string.import_ebooks)
                        .setView(et2)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                String filterFile = et2.getText().toString();
                                if (T.isFile(filterFile)) {
                                    String text = T.getFileText(filterFile);
                                    if (text != null) {
                                        while (true) {
                                            int n = text.indexOf("\n");
                                            if (n == -1)
                                                break;
                                            String s = text.substring(0, n);
                                            text = text.substring(n + 1);
                                            int i = s.indexOf("#->#");
                                            if (i > 0) {
                                                A.ttsFilters.add(new A.TTS_Filter_Item(s.substring(0, i), s.substring(i + 4)));
                                                View item = filterLay.getChildAt(filterLay.getChildCount() - 1);
                                                item.setTag(A.ttsFilters.size() - 1);
                                                setFilterItemProperties(item);

                                                View last = LayoutInflater.from(ActivityTxt.this).inflate(R.layout.tts_filter, null);
                                                last.setTag(A.ttsFilters.size());
                                                setFilterItemProperties(last);
                                                filterLay.addView(last, -1, -2);
                                            }
                                        }
                                    }
                                } else
                                    T.showAlertText(ActivityTxt.this, getString(R.string.error),
                                            "\"" + filterFile + "\" " + getString(R.string.not_exists));
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

            }
        });
        tv2.setOnClickListener(new View.OnClickListener() { //import
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(ActivityTxt.this);
                et.setText("/sdcard/" + T.getOnlyFilename(A.lastFile) + ".ttsfilter");
                new MyDialog.Builder(ActivityTxt.this)
                        .setTitle(R.string.export)
                        .setView(et)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                String filename = et.getText().toString();
                                saveFilterList();
                                if (A.saveTTSFilterToFile(filename))
                                    T.showToastText(ActivityTxt.this, getString(R.string.export_success) + "\n" + filename, 1);
                                else
                                    T.showToastText(ActivityTxt.this, getString(R.string.export_failed), 1);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });

        for (int i = 0; i <= A.ttsFilters.size(); i++) {
            View item = LayoutInflater.from(ActivityTxt.this).inflate(R.layout.tts_filter, null);
            item.setTag(i);
            setFilterItemProperties(item);
            filterLay.addView(item, -1, -2);
        }

        ScrollView sv = new ScrollView(ActivityTxt.this);
        sv.addView(filterLay, -1, -1);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(-1, -1);
        lp1.weight = 5;
        base.addView(sv, lp1);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(-2, -2);
        lp2.topMargin = lp2.bottomMargin = A.d(10);
        lp2.height = 1;
        View lineV = new View(ActivityTxt.this);
        lineV.setBackgroundColor(0x55555555);
        base.addView(lineV, lp2);
        base.addView(cb);
        base.addView(importLay);

        if (Build.VERSION.SDK_INT < 11)
            A.forceNightTextColors(base);

        return base;
    }

    private void setFilterItemProperties(View item) {
        int i = (Integer) item.getTag();
        EditText et1 = (EditText) item.findViewById(R.id.editText1);
        EditText et2 = (EditText) item.findViewById(R.id.editText2);
        View splitter = item.findViewById(R.id.arrow);
        TextView op = item.findViewById(R.id.op);
        op.setTag(item);
        op.setOnClickListener(onFilterItemClick);
        et1.setText(i < A.ttsFilters.size() ? A.ttsFilters.get(i).original : "");
        et2.setText(i < A.ttsFilters.size() ? A.ttsFilters.get(i).replaceWith : "");
        int visibility = i == A.ttsFilters.size() ? View.INVISIBLE : View.VISIBLE;
        et1.setVisibility(visibility);
        et2.setVisibility(visibility);
        et1.setTextSize(15);
        et2.setTextSize(15);
        splitter.setVisibility(visibility);
        op.setText(i == A.ttsFilters.size() ? "+" : "-");
    }

    private void saveFilterList() {
        if (filterLay != null) {
            A.ttsFilters = new ArrayList<>();
            for (int i = 0; i < filterLay.getChildCount() - 1; i++) {
                View item = filterLay.getChildAt(i);
                item.setTag(i);
                String original = ((EditText) item.findViewById(R.id.editText1)).getText().toString();
                String replaceWith = ((EditText) item.findViewById(R.id.editText2)).getText().toString();
                A.ttsFilters.add(new A.TTS_Filter_Item(original, replaceWith));
            }
        }
    }

    View.OnClickListener onFilterItemClick = new View.OnClickListener() {
        public void onClick(View v) {
            View item = (View) v.getTag();
            int position = (Integer) item.getTag();
            if (position >= A.ttsFilters.size()) {
                A.ttsFilters.add(new A.TTS_Filter_Item("", ""));
                item.setTag(A.ttsFilters.size() - 1);
                setFilterItemProperties(item);
                View newItem = LayoutInflater.from(ActivityTxt.this).inflate(R.layout.tts_filter, null);
                newItem.setTag(A.ttsFilters.size());
                setFilterItemProperties(newItem);
                filterLay.addView(newItem, -1, -2);
            } else {
                filterLay.removeView(item);
                saveFilterList();
            }
        }
    };

    private boolean hideSearchPanel() {
        if (search_panel != null && search_panel.getVisibility() == View.VISIBLE) {
            search_panel.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    //-------------------------

    static boolean justEnabledDualPage;
    int preFlipAnimation, prehori_fling_animation, preFitCutout;
    boolean fullscreenWithStatus, preFullScreen, preCssFont, preIndent, preLandscape2Page, preShowStatusBar, preShowRemaingTime,
            preAutoUpdateReadingFollow, preRemaingTimeInStatusBar, preOpenBookAnim;
    String preDisableCSS, preChapterStyle;

    void showReadingOptionsAct() {
        preFlipAnimation = A.flip_animation;
        preFullScreen = A.fullscreen;
        fullscreenWithStatus = A.fullscreenWithStatus;
        preFitCutout = A.fitCutout;
        preCssFont = A.useCssFont;
        preIndent = A.indentParagraph;
        preLandscape2Page = A.landscape2PageMode;
        preShowStatusBar = A.showStatusbar;
        preShowRemaingTime = A.showRemainingTime;
        preAutoUpdateReadingFollow = A.autoUpdateReadingFollow;
        preRemaingTimeInStatusBar = A.remaingTimeInStatusBar;
        prehori_fling_animation = A.hori_fling_animation;
        preDisableCSS = "" + A.disableCSS + A.cssFontStyle + A.cssFontColor + A.cssFontSize + A.cssAlignment
                + A.cssJustify + A.cssIndent + A.cssLineSpace + A.cssOthers;
        preOpenBookAnim = A.openBookAnim;
        preChapterStyle = WBpub.getTitleStyle1();
        startActivityForResult(new Intent(this, ReadingOptionsAct.class), 125);
    }

    SeekBar progressSK, brightnessSK, bluelightSK;
    ImageView b_speak, b_autoscroll, b_bookmark, b_orientation, //b_visual, b_control, b_misc, b_shutdown,
            b_chapter, b_brightness, b_fontsize, b_option, b_daynight, b_search, b_tilt;
    View daynightLay, b_write, b_read;
    TextView b_storeUrl, b_toShelf, commentTv;

    private boolean initTopBottomLay(boolean handle) {
        if (topLay != null)
            return true;

        topLay = ((ViewStub) findViewById(R.id.viewStub_top)).inflate();
        bottomLay = (ViewGroup) ((ViewStub) findViewById(R.id.viewStub_bottom)).inflate();

        topLay.findViewById(R.id.notificationBar).setVisibility(View.GONE);
        urlLay = topLay.findViewById(R.id.urlLay);
        chromeIv = (ImageView) topLay.findViewById(R.id.chromeSwitch);
        pdfPenIv = (ImageView) topLay.findViewById(R.id.pdfPen);
        downloadIv = (ImageView) topLay.findViewById(R.id.txtDownload);
        progressSK = (SeekBar) bottomLay.findViewById(R.id.txtSeekBar01);
        backLay = topLay.findViewById(R.id.txtBackLay);
        titleTextView = (TextView) topLay.findViewById(R.id.txtTitle);
        onlineSwitch = (TextView) topLay.findViewById(R.id.zsSwitch);
        urlTv = (TextView) topLay.findViewById(R.id.txtUrl);
        siteTv = (TextView) topLay.findViewById(R.id.siteTv);
        menuB = topLay.findViewById(R.id.menuB);

        b_storeUrl = topLay.findViewById(R.id.b_storeUrl);
        b_toShelf = topLay.findViewById(R.id.b_toShelf);
        b_read = bottomLay.findViewById(R.id.b_read);
        b_write = bottomLay.findViewById(R.id.b_write);
        commentTv = bottomLay.findViewById(R.id.commentTv);
        if (!T.isNull(A.shuPingTip))
            ((TextView) bottomLay.findViewById(R.id.shuPingTip)).setText(A.shuPingTip);

        menuLv = (ListView) bottomLay.findViewById(R.id.menu);
        funcLay = bottomLay.findViewById(R.id.func);
        nav_return = (ImageView) bottomLay.findViewById(R.id.nav_return);
//		chapterTv = (TextView) bottomLay.findViewById(R.id.chapterTv);
        percentView = (TextView) bottomLay.findViewById(R.id.txtTextViewPercent);
        priorTv = (TextView) bottomLay.findViewById(R.id.priorChapterTv);
        nextTv = (TextView) bottomLay.findViewById(R.id.nextChapterTv);
        percentView.setVisibility(View.GONE); //todo:
        b_chapter = (ImageView) bottomLay.findViewById(R.id.b_chapter);
        b_fontsize = (ImageView) bottomLay.findViewById(R.id.b_fontsize);
        b_brightness = (ImageView) bottomLay.findViewById(R.id.b_brightness);
        b_option = (ImageView) bottomLay.findViewById(R.id.b_option);
        b_daynight = (ImageView) bottomLay.findViewById(R.id.b_daynight);
        daynightLay = bottomLay.findViewById(R.id.daynightLay);
        buttonsLay = bottomLay.findViewById(R.id.buttonsLay);

//		if (A.isTablet){
//			topLay.findViewById(R.id.sv).getLayoutParams().height = A.d(64); //phone 56, tablet 64
//			buttonsLay.getLayoutParams().height = A.d(60);
//		}

        setTopBottomLayVisible(View.GONE);
        topLay.setVisibility(View.INVISIBLE);
        bottomLay.setVisibility(View.INVISIBLE);

        setBottomIconsVisibility();
        backLay.setOnClickListener(this);
        titleTextView.setOnClickListener(this);
        onlineSwitch.setOnClickListener(this);
        percentView.setOnClickListener(this);
        nav_return.setOnClickListener(this);
        progressSK.setOnSeekBarChangeListener(seekBarChanged);
        chromeIv.setOnClickListener(this);
        pdfPenIv.setOnClickListener(this);
        urlTv.setOnClickListener(this);
        siteTv.setOnClickListener(this);
        downloadIv.setOnClickListener(this);
        priorTv.setOnClickListener(barClick);
        nextTv.setOnClickListener(barClick);

        urlTv.setTextSize(A.isTablet ? 13 : 11);
        siteTv.setTextSize(A.isTablet ? 13 : 11);
        urlTv.setTextSize(A.isTablet ? 13 : 11);
        urlTv.setTextSize(A.isTablet ? 13 : 11);
        priorTv.setTextSize(A.isTablet ? 13 : 11);
        nextTv.setTextSize(A.isTablet ? 13 : 11);

        if (handle)
            new Handler() {
                public void handleMessage(Message msg) {
                    inverseLayoutVisible(false);
                }
            }.sendEmptyMessageDelayed(0, 0);

        return false;
    }

    private void showBottomIconsHint() {
//		if (A.hintForBottomBar && (A.runCount > (A.isProVersion ? 0 : 1)))
//			if (layoutVisible)
//				A.customizeBottomIcons(this, true);
    }

    int layDuration = 150;

    private void layoutAnimationShow() {
        if (dontShowLayoutAnimation())
            return;
//		Animation animation1 = new TranslateAnimation(0, 0, bottomLay.getHeight() - A.navBarheight, 0);
//		animation1.setDuration(layDuration);
//		bottomLay.startAnimation(animation1);
        Animation animation2 = new TranslateAnimation(0, 0, -topLay.getHeight() - topLayPaddingTop - sysbar_height, topLayPaddingTop);
        animation2.setDuration(layDuration);
        topLay.startAnimation(animation2);
    }

    private void layoutAnimationHide() {
        if (isPdf())
            return;
        if (dontShowLayoutAnimation())
            return;
        Animation animation1 = new TranslateAnimation(0, 0, 0, bottomLay.getHeight());// - A.navBarheight);
        animation1.setDuration(layDuration);
        bottomLay.startAnimation(animation1);
        Animation animation2 = new TranslateAnimation(0, 0, topLayPaddingTop, -topLay.getHeight() - topLayPaddingTop - sysbar_height);
        animation2.setDuration(layDuration);
        topLay.startAnimation(animation2);
    }

    private boolean dontShowLayoutAnimation() {
        if (topLay == null || bottomLay == null)
            return true;
        if (A.verticalAlignment)
            return true;
//		if (A.isTablet && (A.getDensity() == 1 || A.getDensity() > 1.5))
//			return true;
        if (isPdf() && pdf != null && A.pdf_theme == 1 && !pdf.textReflow)
            return true;
//		if (A.isSonyROM && A.getDensity() > 2)
//			return;
        return false;
    }

    private void hideTopLay() {
        if (topLay != null)
            topLay.setVisibility(View.INVISIBLE);
    }

    View.OnLongClickListener barLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (v == flip2) {
                if (A.flip_ebook != A.FLIP_HORIZONTAL) {
                    A.flip_animation = A.flip_ebook = A.FLIP_HORIZONTAL;
                    T.showToastText(ActivityTxt.this, A.fanti("已设置为滑动式左右翻页"), 0, Gravity.CENTER);
                } else {
                    A.flip_animation = A.flip_ebook = A.FLIP_SHIFT_HORIZONTAL;
                    T.showToastText(ActivityTxt.this, A.fanti("已设置为覆盖式左右翻页"), 0, Gravity.CENTER);
                }
                updateFlipIndicator();
                needToVerifyFlip = true;
                return true;
            }
            if (v == flip3) {
                if (A.flip_ebook != A.FLIP_CURL3D) {
                    A.flip_animation = A.flip_ebook = A.FLIP_CURL3D;
                    T.showToastText(ActivityTxt.this, A.fanti("已设置为仿真翻页二"));
                } else {
                    A.flip_animation = A.flip_ebook = A.FLIP_CURL3D_G;
                    T.showToastText(ActivityTxt.this, A.fanti("已设置为仿真翻页一"));
                }
                updateFlipIndicator();
                needToVerifyFlip = true;
                return true;
            }
            return false;
        }
    };

    View.OnClickListener barClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.priorChapterTv:
                    if (nav_return.getTag() == null) {
                        nav_return.setTag(1);
                        saveLinkBackInfo(true);
                    }
                    if (do_prior_chapter()) {
                        hideTopLay();
                        nav_return.setVisibility(View.VISIBLE);
                        updateBarIcons();
                    }
                    break;
                case R.id.nextChapterTv:
                    if (nav_return.getTag() == null) {
                        nav_return.setTag(1);
                        saveLinkBackInfo(true);
                    }
                    if (do_next_chapter()) {
                        hideTopLay();
                        nav_return.setVisibility(View.VISIBLE);
                        updateBarIcons();
                    }
                    break;

                case R.id.moreFont:
                    if (!shareValidteOK(1))
                        return;
//					inverseLayoutVisible(true);
                    new PrefFontPick(ActivityTxt.this, new PrefFontPick.OnGetFont() {
                        public void getFont(String newName) {
                            if (!newName.equals(A.fontName)) {
                                String family1 = MyHtml.familyBold + MyHtml.familyItalic + MyHtml.familyBoldItalic;
                                A.fontName = newName;
                                MyHtml.initFamilyFontParams();
                                String family2 = MyHtml.familyBold + MyHtml.familyItalic + MyHtml.familyBoldItalic;
                                refreshFontStyle();
                                if (!family1.equals(family2))
                                    refreshTxtRender();
                                eraseGPUShadow(100);
                                hideTopLay();
                            }
                        }
                    }).show();
                    break;
                case R.id.moreTheme:
                    A.themeId = 5;
                    updateThemeIndicator();
                    showBottomBar(4);
                    break;
                case R.id.moreTypeset:
                    if (!shareValidteOK(2))
                        return;
//					A.typesetId = 5;
//					updateTypesetIndicator();
                    showBottomBar(5);
                    break;
                case R.id.fontFan:
                    if (A.textJian2Fan) {
                        A.textJian2Fan = false;
                        A.textFan2Jian = true;
                        T.showAlertText(ActivityTxt.this, A.fanti("已设置文本为繁简转换, 不需要时请及时取消. 如果所阅读文本并非繁体字, 强制繁简转换后可能会出现一些异常文字."));
                    } else if (A.textFan2Jian) {
                        A.textJian2Fan = false;
                        A.textFan2Jian = false;
                        T.showToastText(ActivityTxt.this, A.fanti("已取消简繁/繁简转换, 目前为原始文本"), 1, Gravity.CENTER);
                    } else {
                        A.textJian2Fan = true;
                        A.textFan2Jian = false;
                        T.showToastText(ActivityTxt.this, A.fanti("已设置文本为简繁转换"), 1, Gravity.CENTER);
                    }
                    updateFantiTv();
                    refreshTxtRender();
                    hideTopLay();
                    break;
                case R.id.fontsize1:
                    fontSizeAdd(-1);
                    hideTopLay();
                    break;
                case R.id.fontsize2:
                    fontSizeAdd(1);
                    hideTopLay();
                    break;
                case R.id.fontsizeTv:
                    final EditText et = new EditText(ActivityTxt.this);
                    et.setText("" + (int) (A.fontSize));
                    et.setSingleLine();
                    et.setInputType(InputType.TYPE_CLASS_NUMBER);
                    new MyDialog.Builder(ActivityTxt.this).setTitle(R.string.font_size).setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int newSize = T.string2Int(et.getText().toString());
                            if (newSize != (int) (A.fontSize) && newSize > 4 && newSize <= 100) {
                                fontSizeAdd((int) (newSize - A.fontSize));
                                hideTopLay();
                            }
                        }
                    }).setNegativeButton("取消", null).show();
                    break;
                case R.id.typeset1:
                    setTypesetId(1);
                    break;
                case R.id.typeset2:
                    setTypesetId(2);
                    break;
                case R.id.typeset3:
                    setTypesetId(3);
                    break;
                case R.id.typeset4:
                    setTypesetId(4);
                    break;
                case R.id.standardT:
                    if (A.textDense) {
                        A.textDense = false;
                        standardT.setChecked(!A.textDense);
                        denseT.setChecked(A.textDense);
                        refreshTxtRender();
                        hideTopLay();
                    }
                    break;
                case R.id.denseT:
                    if (!A.textDense) {
                        A.textDense = true;
                        standardT.setChecked(!A.textDense);
                        denseT.setChecked(A.textDense);
                        refreshTxtRender();
                        hideTopLay();
                    }
                    T.showAlertText(ActivityTxt.this, "关于紧凑排版的说明", "该模式下每行会尽量显示更多字符, 允许标点出现在行首, 允许拆分标点组合, 并对句号+引号等组合的占位进行压缩处理.");
                    break;
                case R.id.theme1:
                    setThemeId(1);
                    break;
                case R.id.theme2:
                    setThemeId(2);
                    break;
                case R.id.theme3:
                    setThemeId(3);
                    break;
                case R.id.theme4:
                    setThemeId(4);
                    break;
                case R.id.flip1:
                    A.flip_animation = A.flip_ebook = A.FLIP_NONE;
                    A.disableMove = false;
                    updateFlipIndicator();
                    needToVerifyFlip = true;
                    break;
                case R.id.flip2:
                    A.flip_animation = A.flip_ebook = A.FLIP_SHIFT_HORIZONTAL;
                    updateFlipIndicator();
                    needToVerifyFlip = true;
                    break;
                case R.id.flip3:
                    A.flip_animation = A.flip_ebook = A.FLIP_CURL3D_G;
                    updateFlipIndicator();
                    needToVerifyFlip = true;
                    break;
                case R.id.moreFlip:
                    ReadingOptionsAct.showFlipOptions(ActivityTxt.this, flipPanel, null, new ReadingOptionsAct.OnFlipSelected() {
                        @Override
                        public void afterSelect() {
                            updateFlipIndicator();
                            needToVerifyFlip = true;
                        }
                    });
                    break;

                case R.id.fontColor:
                    new ColorDialog(ActivityTxt.this, getString(R.string.font_color), true, A.fontColor, new ColorDialog.OnSaveColor() {
                        public void getColor(int color) {
                            if (setFontColor(color)) {
                                ((RoundButton) bottomLay.findViewById(R.id.fontColor)).setSolidColor(A.fontColor);
                                clearCurlCache();
                                hideTopLay();
                            }
                        }
                    }).show();
                    break;
                case R.id.backgroundColorTv:
                    if (A.useBackgroundImage) {
                        A.useBackgroundImage = false;
                        baseFrame.setBackgroundColor(A.backgroundColor);
                        updateColorPanel();
                        clearCurlCache();
                        hideTopLay();
                    }
                    break;
                case R.id.backgroundColor:
                    new ColorDialog(ActivityTxt.this, getString(R.string.background_color), false, A.backgroundColor, new ColorDialog.OnSaveColor() {
                        public void getColor(int color) {
                            color = A.deleteAlphaOfColor(color);
                            if (color != A.backgroundColor || A.useBackgroundImage) {
                                A.backgroundColor = color;
                                checkFontColor(null, color);
                                A.useBackgroundImage = false;
                                baseFrame.setBackgroundColor(color);
                                updateColorPanel();
                                clearCurlCache();
                                hideTopLay();
                            }
                        }
                    }).show();
                    break;
                case R.id.backgroundImageTv:
                    if (!A.useBackgroundImage) {
                        Drawable d = A.getImagesDrawable(A.getBackgroundImages(false), A.backgroundImage, A.NORMAL_SIZE, A.NORMAL_QUALITY);
                        if (d == null)
                            return;
                        A.setBackgroundDrawable(baseFrame, d);
                        A.useBackgroundImage = true;
                        checkFontColor(d, A.fontColor);
                        updateColorPanel();
                        clearCurlCache();
                        hideTopLay();
                    }
                    break;
                case R.id.backgroundImage:
                    new PrefImageBrowser1(ActivityTxt.this, new PrefImageBrowser1.OnSaveImage() {
                        public void onGetImageFile(String filename, String outerPath) {
                            A.outerImagesFolder = outerPath;
                            A.log(filename);
                            if (filename == null)
                                return;
                            Drawable d = A.getImagesDrawable(A.getBackgroundImages(false), filename, A.NORMAL_SIZE, A.NORMAL_QUALITY);
                            if (d == null)
                                return;
                            A.setBackgroundDrawable(baseFrame, d);
                            A.useBackgroundImage = true;
                            A.backgroundImage = filename;
                            checkFontColor(d, A.fontColor);
                            updateColorPanel();
                            clearCurlCache();
                            hideTopLay();
                        }
                    }, A.getBackgroundImages(true), true, A.outerImagesFolder, A.SMALL_SIZE, A.d(90), A.d(70),
                            getString(R.string.background_image), A.backgroundImage).show();
                    break;
                case R.id.saveTheme:
                    saveToTheme();
                    break;
                case R.id.exportTheme:
                    exportTheme();
                    break;
                case R.id.importTheme:
                    importTheme();
                    break;
                case R.id.setNightTheme:
                case R.id.setNightTheme2:
                    showBottomBar(6);
                    updateNightPanel();
                    break;


                case R.id.nfontColor:
                    new ColorDialog(ActivityTxt.this, getString(R.string.font_color), true, A.getNightTheme().pFontColor, new ColorDialog.OnSaveColor() {
                        public void getColor(int color) {
                            A.PageTheme pt = A.getNightTheme();
                            pt.pFontColor = color;
                            pt.saveToXml(pt.pName, false);
                            ((RoundButton) nightPanel.findViewById(R.id.nfontColor)).setSolidColor(pt.pFontColor);
                            if (A.mainNightTheme && setFontColor(color)) {
                                updateColorPanel();
                                clearCurlCache();
                                hideTopLay();
                            }
                        }
                    }).show();
                    break;
                case R.id.nbackgroundColorTv: {
                    A.PageTheme pt = A.getNightTheme();
                    if (pt.pUseBackgroundImage) {
                        pt.pUseBackgroundImage = false;
                        pt.saveToXml(pt.pName, false);
                        updateNightPanel();
                        if (A.mainNightTheme) {
                            A.useBackgroundImage = false;
                            checkFontColor(null, pt.pBackgroundColor);
                            baseFrame.setBackgroundColor(pt.pBackgroundColor);
                            updateColorPanel();
                            clearCurlCache();
                            hideTopLay();
                        }
                    }
                }
                break;
                case R.id.nbackgroundColor:
                    new ColorDialog(ActivityTxt.this, getString(R.string.background_color), false, A.getNightTheme().pBackgroundColor, new ColorDialog.OnSaveColor() {
                        public void getColor(int color) {
                            A.PageTheme pt = A.getNightTheme();
                            color = A.deleteAlphaOfColor(color);
                            if (color != pt.pBackgroundColor || pt.pUseBackgroundImage) {
                                pt.pBackgroundColor = color;
                                pt.pUseBackgroundImage = false;
                                pt.saveToXml(pt.pName, false);
                                updateNightPanel();
                                if (A.mainNightTheme) {
                                    A.useBackgroundImage = false;
                                    checkFontColor(null, color);
                                    A.baseFrame.setBackgroundColor(color);
                                    updateColorPanel();
                                    clearCurlCache();
                                    hideTopLay();
                                }
                            }
                        }
                    }).show();
                    break;
                case R.id.nbackgroundImageTv: {
                    A.PageTheme pt = A.getNightTheme();
                    if (!pt.pUseBackgroundImage) {
                        Drawable d = A.getImagesDrawable(A.getBackgroundImages(false), pt.pBackgroundImage, A.NORMAL_SIZE, A.NORMAL_QUALITY);
                        if (d == null)
                            return;
                        pt.pUseBackgroundImage = true;
                        pt.saveToXml(pt.pName, false);
                        updateNightPanel();
                        if (A.mainNightTheme) {
                            A.setBackgroundDrawable(A.baseFrame, d);
                            A.useBackgroundImage = true;
                            A.backgroundImage = pt.pBackgroundImage;
                            checkFontColor(d, A.fontColor);
                            updateColorPanel();
                            clearCurlCache();
                            hideTopLay();
                        }
                    }
                }
                break;
                case R.id.nbackgroundImage:
                    new PrefImageBrowser1(ActivityTxt.this, new PrefImageBrowser1.OnSaveImage() {
                        public void onGetImageFile(String filename, String outerPath) {
                            A.outerImagesFolder = outerPath;
                            if (filename == null)
                                return;
                            Drawable d = A.getImagesDrawable(A.getBackgroundImages(false), filename, A.NORMAL_SIZE, A.NORMAL_QUALITY);
                            if (d == null)
                                return;

                            A.PageTheme pt = A.getNightTheme();
                            pt.pUseBackgroundImage = true;
                            pt.pBackgroundImage = filename;
                            pt.saveToXml(pt.pName, false);
                            updateNightPanel();

                            if (A.mainNightTheme) {
                                A.setBackgroundDrawable(A.baseFrame, d);
                                A.useBackgroundImage = true;
                                A.backgroundImage = filename;
                                checkFontColor(d, A.fontColor);
                                updateColorPanel();
                                clearCurlCache();
                            }
                            hideTopLay();
                        }
                    }, A.getBackgroundImages(true), true, A.outerImagesFolder, A.SMALL_SIZE, A.d(90), A.d(70),
                            getString(R.string.background_image), A.backgroundImage).show();
                    break;
                case R.id.nbrightIndicator:
                case R.id.nbrightText:
                    A.autoThemeNightWithBright = !A.autoThemeNightWithBright;
                    ((ImageView) nightPanel.findViewById(R.id.nbrightIndicator)).setImageResource(A.autoThemeNightWithBright ? R.drawable.indicator2 : R.drawable.indicator3);
                    break;
                case R.id.nbrightTv:
                    final EditText et2 = new EditText(ActivityTxt.this);
                    et2.setText("" + A.autoThemeNightBrightness);
                    et2.setSingleLine();
                    et2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    new MyDialog.Builder(ActivityTxt.this).setTitle(R.string.brightness).setView(et2).setPositiveButton("确定", new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                int value = Integer.valueOf(et2.getText().toString());
                                if (value != A.autoThemeNightBrightness && value > -50 && value <= 100) {
                                    A.autoThemeNightBrightness = value;
                                    A.autoThemeNightWithBright = true;
                                    ((TextView) nightPanel.findViewById(R.id.nbrightTv)).setText(A.autoThemeNightBrightness + "%");
                                    if (A.mainNightTheme && A.autoThemeNightWithBright)
                                        setScreenBrightness(A.autoThemeNightBrightness, true);
                                }
                            } catch (Exception e) {
                                A.error(e);
                            }
                        }
                    }).setNegativeButton("取消", null).show();
                    break;
                case R.id.nautoCancelNight:
                    A.autoThemeDay = !A.autoThemeDay;
                    ((RoundButton) nightPanel.findViewById(R.id.nautoCancelNight)).setChecked(A.autoThemeDay);
                    break;

                case R.id.resetSpace:
                    A.set_default_Margin();
                    A.set_default_Line_Font_Space();
                    hideTopLay();
                    setTypesetId(2);
                    updateMarginSK(false);
                    standardT.setChecked(!A.textDense);
                    denseT.setChecked(A.textDense);
                    refreshTxtRender();
                    adjustMargin(0);
                    T.showToastText(ActivityTxt.this, "已重置各间距为缺省值", 1, Gravity.CENTER);
                    break;

            }
        }
    };

    TextView fontSizeTv;
    RoundButton fantiTv, flip1, flip2, flip3, moreFlip;
    RoundImage theme1, theme2, theme3, theme4;
    View flipPanel;
    boolean needToVerifyFlip;

    private void initFontPanel() {
        preFlipAnimation = A.flip_animation;

        if (fontPanel == null) {
            fontPanel = ((ViewStub) bottomLay.findViewById(R.id.stub_font)).inflate();
            fontPanel.setBackgroundColor(bottomBackgroundColor);

            fontPanel.findViewById(R.id.moreFont).setOnClickListener(barClick);
            fontPanel.findViewById(R.id.moreTheme).setOnClickListener(barClick);
            fontPanel.findViewById(R.id.moreTypeset).setOnClickListener(barClick);
            fontPanel.findViewById(R.id.fontsize1).setOnClickListener(barClick);
            fontPanel.findViewById(R.id.fontsize2).setOnClickListener(barClick);
            fontPanel.findViewById(R.id.fontFan).setOnClickListener(barClick);
            fontPanel.findViewById(R.id.fontsizeTv).setOnClickListener(barClick);
            fontPanel.findViewById(R.id.typeset1).setOnClickListener(barClick);
            fontPanel.findViewById(R.id.typeset2).setOnClickListener(barClick);
            fontPanel.findViewById(R.id.typeset3).setOnClickListener(barClick);
            fontPanel.findViewById(R.id.typeset4).setOnClickListener(barClick);

            flipPanel = fontPanel.findViewById(R.id.flipPanel);
            flip1 = fontPanel.findViewById(R.id.flip1);
            flip2 = fontPanel.findViewById(R.id.flip2);
            flip3 = fontPanel.findViewById(R.id.flip3);
            moreFlip = fontPanel.findViewById(R.id.moreFlip);
            flip1.setOnClickListener(barClick);
            flip2.setOnClickListener(barClick);
            flip3.setOnClickListener(barClick);
            moreFlip.setOnClickListener(barClick);
            flip2.setOnLongClickListener(barLongClick);
            flip3.setOnLongClickListener(barLongClick);

            theme1 = bottomLay.findViewById(R.id.theme1);
            theme2 = bottomLay.findViewById(R.id.theme2);
            theme3 = bottomLay.findViewById(R.id.theme3);
            theme4 = bottomLay.findViewById(R.id.theme4);
            theme1.setOnClickListener(barClick);
            theme2.setOnClickListener(barClick);
            theme3.setOnClickListener(barClick);
            theme4.setOnClickListener(barClick);

            ((RoundImage) fontPanel.findViewById(R.id.typeset1)).getDrawable().setColorFilter(0xffbbbbbb, PorterDuff.Mode.SRC_IN);
            ((RoundImage) fontPanel.findViewById(R.id.typeset2)).setColorFilter(0xffbbbbbb, PorterDuff.Mode.SRC_IN);
            ((RoundImage) fontPanel.findViewById(R.id.typeset3)).getDrawable().setColorFilter(0xffbbbbbb, PorterDuff.Mode.SRC_IN);

            fantiTv = (RoundButton) fontPanel.findViewById(R.id.fontFan);
            updateFantiTv();
            fontSizeTv = (TextView) fontPanel.findViewById(R.id.fontsizeTv);
            fontSizeTv.setText("" + (int) (A.fontSize));
            updateTypesetIndicator();
            updateThemeIndicator();
            updateFlipIndicator();
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    updateThemeImage();
                }
            }.sendEmptyMessageDelayed(0, 0);
        }

        flipPanel.setVisibility(A.flipOptionInReaderBar ? View.VISIBLE : View.GONE);
    }

    private void updateFantiTv() {
        fantiTv.setChecked(A.textJian2Fan || A.textFan2Jian);
        fantiTv.setText(A.textFan2Jian ? "简" : "繁");
    }

    private void initColorPanel() {
        if (colorPanel == null) {
            colorPanel = ((ViewStub) bottomLay.findViewById(R.id.stub_color)).inflate();
            colorPanel.setBackgroundColor(bottomBackgroundColor);

            colorPanel.findViewById(R.id.fontColor).setOnClickListener(barClick);
            colorPanel.findViewById(R.id.backgroundColor).setOnClickListener(barClick);
            colorPanel.findViewById(R.id.backgroundColorTv).setOnClickListener(barClick);
            colorPanel.findViewById(R.id.backgroundImage).setOnClickListener(barClick);
            colorPanel.findViewById(R.id.backgroundImageTv).setOnClickListener(barClick);
            colorPanel.findViewById(R.id.saveTheme).setOnClickListener(barClick);
            colorPanel.findViewById(R.id.exportTheme).setOnClickListener(barClick);
            colorPanel.findViewById(R.id.importTheme).setOnClickListener(barClick);
            colorPanel.findViewById(R.id.setNightTheme).setOnClickListener(barClick);
        }
        updateColorPanel();
    }

    SeekBar lineSK, paraSK, letterSK, leftSK, rightSK, topSK, bottomSK;
    RoundButton standardT, denseT;

    private void initSpacePanel() {
        if (spacePanel == null) {
            spacePanel = ((ViewStub) bottomLay.findViewById(R.id.stub_space)).inflate();
            spacePanel.setBackgroundColor(bottomBackgroundColor);
            spacePanel.findViewById(R.id.resetSpace).setOnClickListener(barClick);

            standardT = spacePanel.findViewById(R.id.standardT);
            denseT = spacePanel.findViewById(R.id.denseT);
            spacePanel.findViewById(R.id.standardT).setOnClickListener(barClick);
            spacePanel.findViewById(R.id.denseT).setOnClickListener(barClick);
            if (Build.VERSION.SDK_INT < 21)
                spacePanel.findViewById(R.id.letterSpaceLay).setVisibility(View.GONE);

            lineSK = (SeekBar) spacePanel.findViewById(R.id.lineSK);
            letterSK = (SeekBar) spacePanel.findViewById(R.id.letterSK);
            paraSK = (SeekBar) spacePanel.findViewById(R.id.paraSK);
            leftSK = (SeekBar) spacePanel.findViewById(R.id.leftSK);
            rightSK = (SeekBar) spacePanel.findViewById(R.id.rightSK);
            topSK = (SeekBar) spacePanel.findViewById(R.id.topSK);
            bottomSK = (SeekBar) spacePanel.findViewById(R.id.bottomSK);

            lineSK.setOnSeekBarChangeListener(spaceSeekBarChanged);
            letterSK.setOnSeekBarChangeListener(spaceSeekBarChanged);
            paraSK.setOnSeekBarChangeListener(spaceSeekBarChanged);
            leftSK.setOnSeekBarChangeListener(spaceSeekBarChanged);
            rightSK.setOnSeekBarChangeListener(spaceSeekBarChanged);
            topSK.setOnSeekBarChangeListener(spaceSeekBarChanged);
            bottomSK.setOnSeekBarChangeListener(spaceSeekBarChanged);

            spacePanel.findViewById(R.id.s11).setTag(11);
            spacePanel.findViewById(R.id.s11).setOnClickListener(spaceChangedClick);
            spacePanel.findViewById(R.id.s12).setTag(12);
            spacePanel.findViewById(R.id.s12).setOnClickListener(spaceChangedClick);
            spacePanel.findViewById(R.id.s21).setTag(21);
            spacePanel.findViewById(R.id.s21).setOnClickListener(spaceChangedClick);
            spacePanel.findViewById(R.id.s22).setTag(22);
            spacePanel.findViewById(R.id.s22).setOnClickListener(spaceChangedClick);
            spacePanel.findViewById(R.id.s31).setTag(31);
            spacePanel.findViewById(R.id.s31).setOnClickListener(spaceChangedClick);
            spacePanel.findViewById(R.id.s32).setTag(32);
            spacePanel.findViewById(R.id.s32).setOnClickListener(spaceChangedClick);
            spacePanel.findViewById(R.id.s41).setTag(41);
            spacePanel.findViewById(R.id.s41).setOnClickListener(spaceChangedClick);
            spacePanel.findViewById(R.id.s42).setTag(42);
            spacePanel.findViewById(R.id.s42).setOnClickListener(spaceChangedClick);
            spacePanel.findViewById(R.id.s51).setTag(51);
            spacePanel.findViewById(R.id.s51).setOnClickListener(spaceChangedClick);
            spacePanel.findViewById(R.id.s52).setTag(52);
            spacePanel.findViewById(R.id.s52).setOnClickListener(spaceChangedClick);
            spacePanel.findViewById(R.id.s61).setTag(61);
            spacePanel.findViewById(R.id.s61).setOnClickListener(spaceChangedClick);
            spacePanel.findViewById(R.id.s62).setTag(62);
            spacePanel.findViewById(R.id.s62).setOnClickListener(spaceChangedClick);
            spacePanel.findViewById(R.id.s71).setTag(71);
            spacePanel.findViewById(R.id.s71).setOnClickListener(spaceChangedClick);
            spacePanel.findViewById(R.id.s72).setTag(72);
            spacePanel.findViewById(R.id.s72).setOnClickListener(spaceChangedClick);
        }
        updateMarginSK(false);
        standardT.setChecked(!A.textDense);
        denseT.setChecked(A.textDense);
    }

    private void updateMarginSK(boolean valueOnly) {
        if (!valueOnly) {
            lineSK.setProgress(A.lineSpace + 3);
            letterSK.setProgress(A.fontSpace + 3);
            paraSK.setProgress(A.paragraphSpace);
            leftSK.setProgress(A.vdr(A.leftMargin));
            rightSK.setProgress(A.vdr(A.rightMargin));
            topSK.setProgress(A.vdr(A.topMargin));
            bottomSK.setProgress(A.vdr(A.bottomMargin));
        }
        ((TextView) spacePanel.findViewById(R.id.linev)).setText("行间距(" + (A.lineSpace) + ")");
        ((TextView) spacePanel.findViewById(R.id.letterv)).setText("字间距(" + (A.fontSpace) + ")");
        ((TextView) spacePanel.findViewById(R.id.parav)).setText("段间距(" + (A.paragraphSpace) + ")");
        ((TextView) spacePanel.findViewById(R.id.leftv)).setText("左边空白(" + (A.vdr(A.leftMargin)) + ")");
        ((TextView) spacePanel.findViewById(R.id.rightv)).setText("右边空白(" + (A.vdr(A.rightMargin)) + ")");
        ((TextView) spacePanel.findViewById(R.id.topv)).setText("上边空白(" + (A.vdr(A.topMargin)) + ")");
        ((TextView) spacePanel.findViewById(R.id.bottomv)).setText("下边空白(" + (A.vdr(A.bottomMargin)) + ")");
    }

    private void initNightPanel() {
        if (nightPanel == null) {
            nightPanel = ((ViewStub) bottomLay.findViewById(R.id.stub_night)).inflate();
            nightPanel.setBackgroundColor(bottomBackgroundColor);

            nightPanel.findViewById(R.id.nfontColor).setOnClickListener(barClick);
            nightPanel.findViewById(R.id.nbackgroundColor).setOnClickListener(barClick);
            nightPanel.findViewById(R.id.nbackgroundColorTv).setOnClickListener(barClick);
            nightPanel.findViewById(R.id.nbackgroundImage).setOnClickListener(barClick);
            nightPanel.findViewById(R.id.nbackgroundImageTv).setOnClickListener(barClick);
            nightPanel.findViewById(R.id.nbrightIndicator).setOnClickListener(barClick);
            nightPanel.findViewById(R.id.nbrightText).setOnClickListener(barClick);
            nightPanel.findViewById(R.id.nbrightTv).setOnClickListener(barClick);
            nightPanel.findViewById(R.id.nautoCancelNight).setOnClickListener(barClick);
        }
    }

    View.OnClickListener spaceChangedClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = (Integer) v.getTag();
            int value = 0;
            switch (id) {
                case 11:
                    value = A.lineSpace - 1;
                    break;
                case 12:
                    value = A.lineSpace + 1;
                    break;
                case 21:
                    value = A.paragraphSpace - 1;
                    break;
                case 22:
                    value = A.paragraphSpace + 1;
                    break;
                case 31:
                    value = add(A.leftMargin, -1);
                    break;
                case 32:
                    value = add(A.leftMargin, 1);
                    break;
                case 41:
                    value = add(A.rightMargin, -1);
                    break;
                case 42:
                    value = add(A.rightMargin, 1);
                    break;
                case 51:
                    value = add(A.topMargin, -1);
                    break;
                case 52:
                    value = add(A.topMargin, 1);
                    break;
                case 61:
                    value = add(A.bottomMargin, -1);
                    break;
                case 62:
                    value = add(A.bottomMargin, 1);
                    break;
                case 71:
                    value = A.fontSpace - 1;
                    break;
                case 72:
                    value = A.fontSpace + 1;
                    break;
            }
            if (id == 11 || id == 12) {
                A.lineSpace = value;
                A.setLineSpace(A.txtView);
                A.setLineSpace(A.txtView2);
                if (txtView.getSpanned() != null)
                    if (txtView.getSpanned().getSpans(0, A.txtView.getText().length(), MyFloatSpan.class).length > 0)
                        refreshTxtRender();
                value = A.lineSpace;
                lineSK.setProgress(value + 3);
            }
            if (id == 21 || id == 22) {
                A.paragraphSpace = value;
                if (A.paragraphSpace > 20)
                    A.paragraphSpace = 20;
                if (A.paragraphSpace < 0)
                    A.paragraphSpace = 0;
                A.setLineSpace(A.txtView);
                A.setLineSpace(A.txtView2);
                if (txtView.getSpanned() != null)
                    if (txtView.getSpanned().getSpans(0, txtView.getText().length(), MyMarginSpan.class).length > 0)
                        refreshTxtRender();
                value = A.paragraphSpace;
                paraSK.setProgress(value);
            }
            if (id == 71 || id == 72) {
                A.fontSpace = value;
                A.setFontSpace(A.txtView);
                A.setFontSpace(A.txtView2);
                refreshTxtRender();
                value = A.fontSpace;
                letterSK.setProgress(value + 3);
            }
            if (id == 31 || id == 32) {
                A.leftMargin = value;
                adjustMargin(0);
                value = A.vd(A.leftMargin);
                leftSK.setProgress(value);
            }
            if (id == 41 || id == 42) {
                A.rightMargin = value;
                adjustMargin(1);
                value = A.vd(A.rightMargin);
                rightSK.setProgress(value);
            }
            if (id == 51 || id == 52) {
                A.topMargin = value;
                adjustMargin(2);
                value = A.vd(A.topMargin);
                topSK.setProgress(value);
            }
            if (id == 61 || id == 62) {
                A.bottomMargin = value;
                adjustMargin(3);
                value = A.vd(A.bottomMargin);
                bottomSK.setProgress(value);
            }
//			T.showToastText(ActivityTxt.this, "" + value, 0, Gravity.CENTER);
            A.typesetId = 5;
            updateTypesetIndicator();
            updateMarginSK(true);
        }

        private int add(int m, int a) {
            int to = A.vd(m) + a;
            if (a > 0) {
                for (int i = m + 1; ; i++)
                    if (A.vd(i) >= to)
                        return i;
            } else {
                for (int i = m - 1; ; i--)
                    if (A.vd(i) < to)
                        return i + 1;
            }
        }
    };

    OnSeekBarChangeListener spaceSeekBarChanged = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser)
                return;

//			T.showToastText(ActivityTxt.this, "" + progress, 0, Gravity.CENTER);
            clearCurlCache();
            hideTopLay();

            if (seekBar == lineSK) {
                A.lineSpace = progress - 3;
                A.setLineSpace(A.txtView);
                A.setLineSpace(A.txtView2);
                if (txtView.getSpanned() != null)
                    if (txtView.getSpanned().getSpans(0, A.txtView.getText().length(), MyFloatSpan.class).length > 0)
                        refreshTxtRender();
            }
            if (seekBar == paraSK) {
                A.paragraphSpace = progress;
                A.setLineSpace(A.txtView);
                A.setLineSpace(A.txtView2);
                if (txtView.getSpanned() != null)
                    if (txtView.getSpanned().getSpans(0, txtView.getText().length(), MyMarginSpan.class).length > 0)
                        refreshTxtRender();
            }
            if (seekBar == letterSK) {
                A.fontSpace = progress - 3;
                A.setFontSpace(A.txtView);
                A.setFontSpace(A.txtView2);
                refreshTxtRender();
            }
            if (seekBar == leftSK) {
                A.leftMargin = A.dr(progress);
                adjustMargin(0);
            }
            if (seekBar == rightSK) {
                A.rightMargin = A.dr(progress);
                adjustMargin(1);
            }
            if (seekBar == topSK) {
                A.topMargin = A.dr(progress);
                adjustMargin(2);
            }
            if (seekBar == bottomSK) {
                A.bottomMargin = A.dr(progress);
                adjustMargin(3);
            }
            A.typesetId = 5;
            updateTypesetIndicator();
            updateMarginSK(true);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    RoundButton brightAutoCheck, bluelightCheck, brightSwipeCheck;

    private void initBrightPanel() {
        if (brightnessPanel == null) {
            brightnessPanel = ((ViewStub) bottomLay.findViewById(R.id.stub_brightness)).inflate();
            brightnessPanel.setBackgroundColor(bottomBackgroundColor);
            brightnessPanel.findViewById(R.id.setNightTheme2).setOnClickListener(barClick);
            setNightModeIndicator();

            brightnessSK = (SeekBar) brightnessPanel.findViewById(R.id.brightnessSK);
            brightnessSK.setMax(150);
            brightAutoCheck = (RoundButton) brightnessPanel.findViewById(R.id.autoCheck);
            brightSwipeCheck = (RoundButton) brightnessPanel.findViewById(R.id.brightSwipeCheck);
            brightSwipeCheck.setChecked(A.adjustBrightness);
            brightnessSK.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        setScreenBrightness(progress - 50, true);
                        brightAutoCheck.setChecked(false);
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            brightnessPanel.findViewById(R.id.decTv).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    A.brightnessValue = brightnessSK.getProgress() - 50 - 1;
                    setScreenBrightness(A.brightnessValue, true);
                    setBrightnessSKValue();
                }
            });
            brightnessPanel.findViewById(R.id.incTv).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    A.brightnessValue = brightnessSK.getProgress() - 50 + 1;
                    setScreenBrightness(A.brightnessValue, true);
                    setBrightnessSKValue();
                }
            });
            brightAutoCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (A.brightnessValue != -100) {
                        A.brightnessValue = -100;
                        setScreenBrightness(-100, false);
                        brightAutoCheck.setChecked(true);
                    } else {
                        setScreenBrightness(brightnessSK.getProgress() - 50, true);
                        brightAutoCheck.setChecked(false);
                    }
                }
            });

            brightSwipeCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    A.adjustBrightness = !A.adjustBrightness;
                    brightSwipeCheck.setChecked(A.adjustBrightness);
                }
            });

            bluelightSK = (SeekBar) brightnessPanel.findViewById(R.id.opacitySK);
            bluelightSK.setMax(95);
            bluelightSK.setProgress(A.bluelightOpacity);
            bluelightCheck = (RoundButton) brightnessPanel.findViewById(R.id.opacityCheck);
            bluelightCheck.setChecked(A.bluelightEnable);
            bluelightSK.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        A.bluelightEnable = true;
                        A.bluelightOpacity = progress;
                        updateBluelightFilter();
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            brightnessPanel.findViewById(R.id.decTv2).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    A.bluelightEnable = true;
                    A.bluelightOpacity--;
                    updateBluelightFilter();
                }
            });
            brightnessPanel.findViewById(R.id.incTv2).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    A.bluelightEnable = true;
                    A.bluelightOpacity++;
                    updateBluelightFilter();
                }
            });
            bluelightCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    A.bluelightEnable = !A.bluelightEnable;
                    updateBluelightFilter();
                }
            });
        }
    }

    private void setNightModeIndicator() {
        if (b_daynight != null) {
            b_daynight.setImageResource(A.mainNightTheme ? R.drawable.b2_day : R.drawable.b2_night);
        }
        if (colorPanel != null)
            ((RoundButton) colorPanel.findViewById(R.id.setNightTheme)).setChecked(A.mainNightTheme);
        if (brightnessPanel != null)
            ((RoundButton) brightnessPanel.findViewById(R.id.setNightTheme2)).setChecked(A.mainNightTheme);
    }

    private void updateBluelightFilter() {
        if (A.bluelightOpacity < 0)
            A.bluelightOpacity = 0;
        if (A.bluelightOpacity > 100)
            A.bluelightOpacity = 100;
        if (A.bluelightOpacity == 0)
            A.bluelightEnable = false;
        showShadeCoverOnView();

        if (bluelightSK != null) {
            bluelightSK.setProgress(A.bluelightOpacity);
            bluelightCheck.setChecked(A.bluelightEnable);
        }
        T.showToastText(ActivityTxt.this, (A.bluelightOpacity + "%"), 0, Gravity.CENTER);
    }

    protected void setBrightnessSKValue() {
        if (brightnessSK != null) {
            int value = A.brightnessValue >= -50 ? A.brightnessValue : getSystemBrightness();
            brightnessSK.setProgress(value + 50);
            brightAutoCheck.setChecked(A.brightnessValue == -100);
        }
    }

    private boolean isBackKeyDown = false;
    private int keyDownCount = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isPaused)
            return false;
        if (layoutVisible && isDPadKey(keyCode))
            return super.onKeyDown(keyCode, event);

        userAction = true;
        recordUserActiveTime();
        setScreenAwakeTime = 0;
        keyDownCount++;
        checkScreenAwake();
        if (isWebViewKeyDown(keyCode, event))
            return true;

        isBackKeyDown = false;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bottomType > 0 && bottomLay.getVisibility() == View.VISIBLE) {
                if (topLay.getVisibility() == View.VISIBLE) {
                    showBottomBar(bottomType == 6 ? (preBottomType == 4 ? 4 : 2) : bottomType == 4 || bottomType == 5 ? 1 : 0);
                    justBackFromOtherPanel = true;
                } else
                    showBottomBar(-1);
                return true;
            }
            if (hideTipsPanel())
                return true;
            if (pdfHideAnnotLay(true, true))
                return true;
            if (hideDotViews())
                return true;
            if (pdfHideSearchLay())
                return true;
            if (hideSearchPanel())
                return true;
            if (A.isInAutoScroll) {
                do_AutoScroll(false);
                return true;
            }
            if (A.isSpeaking) {
                if (tts_panel_visible)
                    showTtsPanel(false);
                else
                    stop_speak();
                return true;
            }
            if (layoutVisible) {
                inverseLayoutVisible(true);
                if (justBackFromOtherPanel) {
                    justBackFromOtherPanel = false;
                    return true;
                }
                if (!A.immersive_fullscreen)
                    return true;
            }
            if (stayInCacheing())
                return true;
            A.forceRebootToMain = true;
            isBackKeyDown = true;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_PAGE_UP || keyCode == 105) { //sony prs-t1
            if (A.isSpeaking)
                return false;
            if (mp3PlayState == 1)
                return false;
            if (A.isInAutoScroll) {
                autoScrollTurnPage(PAGE_UP, true);
                return true;
            }
            if (ebook_inLoading)
                return true;
            if (layoutVisible && A.pageSound) {
                adjustVolume(true);
                return true;
            }
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == 106) { //sony prs-t1
            if (A.isSpeaking)
                return false;
            if (mp3PlayState == 1)
                return false;
            if (A.isInAutoScroll) {
                autoScrollTurnPage(PAGE_DOWN, true);
                return true;
            }
            if (ebook_inLoading)
                return true;
            if (layoutVisible && A.pageSound) {
                adjustVolume(false);
                return true;
            }
        }

        if (doKeyDown(keyCode)) {
            isBackKeyDown = false;
            return true;
        }

        if (isDPadKey(keyCode))
            return true;

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (isPaused)
            return false;
        if (layoutVisible && isDPadKey(keyCode))
            return super.onKeyUp(keyCode, event);

        keyDownCount = 0;
        if (isWebViewKeyUp(keyCode, event))
            return true;

        if (layoutVisible) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    return false;
            }
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                if (A.doMenuKey == A.DO_NONE)
                    inverseLayoutVisible(false);
                return true;
//			case KeyEvent.KEYCODE_DPAD_UP: //up & down keyDown event can't be capture()
//				if (doKeyDown(keyCode))
//					return true;
//				return true;
//			case KeyEvent.KEYCODE_DPAD_DOWN:
//				if (doKeyDown(keyCode))
//					return true;
//				break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (A.doDPadLeft != A.DO_NONE)
                    return true;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (A.doDPadRight != A.DO_NONE)
                    return true;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (A.doDPadCenter != A.DO_NONE)
                    return true;
                break;
            case KeyEvent.KEYCODE_SEARCH:
                if (A.doSearchKey != A.DO_NONE)
                    return true;
                return true;
            case KeyEvent.KEYCODE_CAMERA:
                if (A.doCameraKey != A.DO_NONE)
                    return true;
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_PAGE_UP:
            case 105:
                if (A.isSpeaking)
                    return false;
                if (mp3PlayState == 1)
                    return false;
                if ((A.isInAutoScroll) || ebook_inLoading || (A.doVolumeKeyUp != A.DO_NONE))
                    return true;
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case 106:
                if (A.isSpeaking)
                    return false;
                if (mp3PlayState == 1)
                    return false;
                if ((A.isInAutoScroll) || ebook_inLoading || (A.doVolumeKeyDown != A.DO_NONE))
                    return true;
                break;
            case KeyEvent.KEYCODE_BACK:
                if (isBackKeyDown) { //v1.8.7 if hasn't this, press back key after dictionary, will close
                    if (A.doBackKey == A.DO_NONE)
//					if (!navigateBackLinks())
                        if (!forceExitRestart())
                            doFinish();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_HOME:
                if (A.doHomeKey != A.DO_NONE)
                    return true;
                break;
        }

        if (isDPadKey(keyCode))
            return true;

        return super.onKeyUp(keyCode, event);
    }

    private boolean dPadKeyPressed;

    private boolean isDPadKey(int keyCode) {
        boolean result = keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_CENTER;
        if (result)
            dPadKeyPressed = true;
        return result;
    }


    private long getAboutPosition() { //快速大概值, 必须为long类型, 要不getAboutPosision*1000长文本时可能会溢出: (int)10,000,000*(int)1000=负数
        int y = txtScroll.getScrollY();
        int h = txtView.getRealHeight();
        if (h == 0)
            return 0;

        switch (getBookType()) {
            case A.FILE_TXT:
                int addedLength = A.getBlocksLength(A.lastBlockIndex) * y / h;
                int index = (A.lastBlockIndex == 0) ? 0 : A.lastBlockIndex - 1;
                return A.getPriorTxtLength(index) + addedLength;
            case A.FILE_HTML:
                return (htmlText.length() * y / h);
            case A.FILE_EBOOK:
                if (A.ebook.getChapters().size() == 0)
                    return 0;
                long addedLength2 = A.ebook.getChapters().get(A.lastChapter).size * y / h;
                int index2 = (A.lastChapter == 0) ? 0 : A.lastChapter - 1;
                return BaseEBook.getPriorTextLength2(index2) + addedLength2;
        }

        return 0;
    }

    private void showSeekBarProgress() {
        if (!layoutVisible)
            return;
        try {
            switch (getBookType()) {
                case A.FILE_TXT:
                    titleTextView.setText(T.getFilename(A.lastFile));
                    if (A.txtLength() > 0) {
                        long p = isEndOfBook() ? 1000 : getAboutPosition() * 1000 / A.txtLength();
                        setSeekBarProgress(p); //不先用long取值, 10M*1000 int就显示不出来了
                    } else
                        progressSK.setProgress(0);
                    break;

                case A.FILE_HTML:
                    titleTextView.setText(T.getFilename(A.lastFile));
                    long p;
                    if (isEndOfBook()) {
                        p = 1000;
                    } else if (A.noSplitHtmls()) {
                        p = (htmlText.length() > 0) ? getCurrentPosition() * 1000 / getBookLength() : 0;
                    } else {
                        p = 1000 * getCurrentPosition() / getBookLength();
                    }
                    setSeekBarProgress(p);
                    break;

                case A.FILE_EBOOK:
//					titleTextView.setText(A.ebook.getBookName() + (T.isNull(A.ebook.getAuthor())? "" : " - " + A.ebook.getAuthor()));
                    titleTextView.setText(A.ebook.getBookName());
//					percentView.setVisibility(isWebBook()? View.GONE : View.VISIBLE);
                    if (isWebBook()) {
                        int i = isEndOfBook() ? 1000 : A.lastChapter * 1000 / A.ebook.getChapters().size();
                        if (A.lastChapter > 0 && i == 0)
                            i = 1;
                        setSeekBarProgress(i);
                    } else {
                        if (getEBookTotalSize() > 0) {
                            saveLastPostion(false);
                            long p1 = getCurrentPosition();
                            long p2 = isEndOfBook() ? 1000 : p1 * 1000 / getEBookTotalSize();
                            setSeekBarProgress(p1 > 0 && p2 == 0 ? 1 : p2);
                        } else
                            progressSK.setProgress(0);
                    }
                    break;

                case A.FILE_PDF:
                    titleTextView.setText(A.getBookName());
                    int total = m_doc.GetPageCount();
                    progressSK.setMax(total - 1);
                    progressSK.setProgress(pdfGetCurrPageNo());
                    percentView.setText(statusLay.getVisibility() == View.VISIBLE ? statusRight.getText().toString() : "" + (pdfGetCurrPageNo() + 1)
                            + "/" + total);
                    break;
            }

        } catch (Exception e) {
            A.error(e);
        }
    }

    private void setSeekBarProgress(long p) {
        progressSK.setProgress((int) (p));
    }

    private int getSeekBarProgress(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (progress < 0)
            progress = 0;
        if (progress > 1000)
            progress = 1000;
        return progress;
    }

    private void hideScrollbarProgress() {
        if (scrollBlock.getVisibility() != View.INVISIBLE)
            scrollBlock.setVisibility(View.INVISIBLE);
//		if (scrollBlock2.getVisibility() != View.INVISIBLE)
//			scrollBlock2.setVisibility(View.INVISIBLE);
    }

    private void showScrollbarProgress(boolean always) {
        scrollBlock.setVisibility(View.GONE);
        if (showStatusbar()) //v2.5.4 hide it
            return;
        if (web != null)
            return;
        if (!A.isFlipNone())
            return;
        if (tmpHorizaontalScroll)
            return;
        if (inPreShow || dualPageEnabled())
            return;
        if (isPdf())
            return;

        int h = T.getScreenHeight(this) - (showStatusbar() ? statusLay.getHeight() : 0);
        if (txtView.getRealHeight() < h * 2)
            return;

        switch (getBookType()) {
            case A.FILE_TXT:
                if (A.getTxts().size() == 0)
                    return;
                break;
            case A.FILE_HTML:
                if (htmlText.length() == 0)
                    return;
                break;
            case A.FILE_EBOOK:
                break;
        }

        int bw = scrollBlock.getWidth();
        int bh = scrollBlock.getHeight();

        long p = 0;
        int pageCount = 1;
        switch (getBookType()) {
            case A.FILE_TXT:
                p = h * getAboutPosition() / ((A.txtLength() > 0) ? A.txtLength() : h);
                pageCount = (int) (getTotalPages() / 100000);
                break;
            case A.FILE_HTML:
            case A.FILE_EBOOK:
                pageCount = 1 + txtView.getRealHeight() / A.getPageHeight();
                p = h * txtScroll.getScrollY() / txtView.getRealHeight();
                break;
        }
        int ad = h / pageCount;
        p = p + ad * p / h;

        int l = A.getScreenWidth2() - bw - 1;
        int t = (int) p;
        if (t > h - bh || (!A.isTablet && p > h - ad * 2 / 3))
            t = h - bh - 1;
        if (t < 2)
            t = 2;

        scrollBlock.layout(l, t, l + bw, t + bh);
        scrollBlock.setVisibility(View.VISIBLE);
        if (!always) {
            Animation animation2 = new AlphaAnimation(0.8f, 0.1f);
            animation2.setDuration(500);
            scrollBlock.clearAnimation();
            scrollBlock.startAnimation(animation2);
            scrollBlock.setVisibility(View.GONE);
        }
    }

    public int pageDirection = 1;
    private boolean ebook_inLoading = false;
    private long pageScrollTime;

    private void pageScroll(int direction) {
        pageScroll(direction, false, false);
        saveLastPostion(false);
    }

    private void pageScroll(int direction, boolean fromCurlFlip, boolean forCacheOnly) {
        recordUserActiveTime();
        if (!forCacheOnly && waitingForCahingShots())
            return;

        if (ignoreFirstOrLastPageFlip(direction, !forCacheOnly))
            return;

        try {
            MyLayout lo = txtView.getLayout();
            if (lo == null && !isPdf())
                return;
            int y = txtScroll.getScrollY();
            int svH = A.getPageHeight();
            int lh = txtView.getLineHeight();
            boolean isPageDown = direction == PAGE_DOWN;
//			boolean forceScroll = false;

            if (!forCacheOnly) {
                if (ebook_inLoading || (flippingAnimation()))
                    return;
                if (hide_ebook_cover())
                    return;
                if (SystemClock.elapsedRealtime() - changeChapterTime < 500)
                    return;

                userAction = true;
                longTimeTapEvent = false;
                hideLinkBackButton();
                if (layoutVisible)
                    inverseLayoutVisible(true);
                hideDotViews();
                A.moveStart = false;
                registerHardwaresTime = -1;
                pageDirection = direction;
//				forceNotSaveZeroPosition = direction != PAGE_UP;
                if (!A.isInAutoScroll && !A.isSpeaking) {
                    if ((tmpHorizaontalScroll && A.isFlipNone()) || (!fromCurlFlip && A.isFlipCurl())) {
                        if (A.isHoriFlipCurl() || !tmpHorizaontalScroll) {
                            tmpHorizaontalScroll = false;
                            if (doCurlFlip())
                                return;
                        }
                    }
                    if ((tmpHorizaontalScroll && A.isFlipNone()) || (!fromCurlFlip && isValueFlip())) {
                        if (A.isHoriValueFlip() || !tmpHorizaontalScroll) {
                            tmpHorizaontalScroll = false;
                            if (doValueFlip())
                                return;
                        }
                    }
                }

                playPageSound();
                if (pdf == null || !pdf.textReflow)
                    saveFlipShot(forceTiltSpeed != 0 && A.isFlipNone());
            }

            if (pdfPageScroll(isPageDown)) {
                if (!pdf.textReflow)
                    showPageAnimation(isPageDown);
                return;
            }

            if (!forCacheOnly) {
                statistics_add();
                pageScrollTime = SystemClock.elapsedRealtime();
            }

            if (getBookType() == A.FILE_TXT) {
                int curLine = getNextPageLine2(isPageDown, forCacheOnly);
                int atLeast = svH * txtKeepPage(0) + lh * 2;
                boolean loadPrior = A.lastBlockIndex > 1 && y < atLeast; //剩下不足一页, 提前重新加载view内容, 以实现平滑滚动
                boolean loadNext = A.lastBlockIndex < A.getTxts().size() - 2
                        && txtView.getHeight() - (y + svH * txtKeepPage(0)) < atLeast;
                if (loadPrior || loadNext) {
                    A.log("reload txt..");
                    txtReload = true;
                    if (!inPreShow) {
                        int p = lo.getLineStart(curLine);
                        String veri = p < txtView.getText2().length() - 20 ? txtView.getText2().substring(p, p + 20) : null;
                        A.lastPosition = A.getTxtRealPos(p);
                        resetLastEndOfSpeakingText1();
                        showTxtByPosition(A.lastPosition, veri);
                        resetLastEndOfSpeakingText2();
                    }
                } else {
                    int y2 = txtView.getLineTop2(curLine);
                    txtScrollTo(y2);
                }

            } else {
                int y2;
                boolean dualPageEnabled = dualPageEnabled();
                try {
                    boolean forceToNextChapter = false;
                    if (isPageDown && lastLineDisplayed())
                        if (dualPageEnabled || A.currentChapterWithImageOnly()
                                || y + A.getPageHeight() >= txtView.getRealHeight()
                                || !txtView.lineIsSuperLongImage(getRealLineCount() - 1))
                            forceToNextChapter = true;

                    if (forceToNextChapter) {
                        pageDownToNextChapter(-1);
                        showPageAnimation(true);
                        return;
                    }

                    if (y == 0 && !isPageDown) {
                        if (!waitingForCahingShots()) {
                            pageUpToPriorChapter();
                            showPageAnimation(false);
                        }
                        return;
                    }

                    int toLine = getNextPageLine2(isPageDown, forCacheOnly);
                    if (!dualPageEnabled && toLine > 0 && txtView.lineIsSuperLongImage(toLine - 1)) { //for super long image
                        y2 = y + (isPageDown ? svH : -svH);
                    } else {
                        y2 = txtView.getLineTop2(toLine);
                        if (isPageDown && y2 <= y)
                            y2 = y + svH;
                    }

                    if (y2 < 0)
                        y2 = 0;
                    txtScrollTo(y2);
                } catch (Exception e) {
                    A.error(e);
                    y2 = -1;
                }

                if (!checkIfChangeChapterOrSpeak(direction, y, y2)) // todo: useless, never execute?
                    if (isPageDown)
                        preNextChapter(true);
            }

            if (!forCacheOnly) {
                if (A.isSpeaking && getBookType() == A.FILE_TXT)
                    speakCurrentPage(y);
                get_flip_handler().sendEmptyMessage(direction);
            }

        } catch (Exception e) {
            A.error(e);
        }
    }

    int lastSpeakingPosOff;

    private void resetLastEndOfSpeakingText1() {
        if (!A.isSpeaking)
            return;
        int lastLine = A.getLastDisplayLine(txtScroll, -1);
        int pageEnd = txtView.getLayout().getLineEnd(lastLine);
        lastSpeakingPosOff = pageEnd - lastEndOfSpeakText;
        if (lastSpeakingPosOff < 0 || lastSpeakingPosOff > 100)
            lastSpeakingPosOff = 0;
    }

    private void resetLastEndOfSpeakingText2() {
        if (!A.isSpeaking)
            return;
        lastEndOfSpeakText = -1;
        if (lastSpeakingPosOff == 0)
            return;
        int line = txtView.getLayout().getLineForVertical(txtScroll.getScrollY());
        int cur = txtView.getLayout().getLineStart(line);
        lastEndOfSpeakText = cur - lastSpeakingPosOff;
        lastSpeakingPosOff = 0;
    }

    private ArrayList<Integer> yBeforePageDown = new ArrayList<Integer>();
    public boolean hasPageUpCache;

    private int getNextPageLine2(boolean isPageDown, boolean forCacheOnly) {
        MyLayout lo = txtView.getLayout();
        int y2, line;
        boolean dualPage = dualPageEnabled();

        if (isPageDown) {
            if (!forCacheOnly && !isEndOfBook())
                yBeforePageDown.add(txtScroll.getScrollY());
//			if (!forCacheOnly && !isPaused &&
            if (A.flip_animation == 0 && !isPaused &&
                    ((!dualPage && txtView.lastIgnoreLine() > 0) || (dualPage && txtView2.lastIgnoreLine() > 0))) {
                line = !dualPage ? txtView.lastIgnoreLine() : txtView2.lastIgnoreLine();
            } else {
                int lastLine = A.getLastDisplayLine(dualPage ? txtScroll2 : txtScroll, -1);
                line = lastLine + 1;
                if (txtView.isNormalImageLine(lastLine))
                    if (!txtView.isImageDrawed(lastLine))
                        if (!dualPage || lo.getLineForVertical(txtScroll2.getScrollY()) != lastLine)
                            line = lastLine;
            }

        } else {
            hasPageUpCache = false;
            if (!forCacheOnly && yBeforePageDown.size() > 0) { //v2.3.3
                hasPageUpCache = true;
                return lo.getLineForVertical(yBeforePageDown.remove(yBeforePageDown.size() - 1));
            }
            int y = txtScroll.getScrollY();
            int cur_line = lo.getLineForVertical(y);
            y2 = y - A.getPageHeight() * txtKeepPage(0);
            line = y2 < txtView.getLineHeight() / 2 ? 0 : lo.getLineForVertical(y2) + 1;
            int suggest_last_line = A.getLastDisplayLine(txtScroll, line);
            if (dualPage)
                suggest_last_line = A.getLastDisplayLine(txtScroll2, suggest_last_line + 1);
            if (line > 0 && suggest_last_line >= cur_line)
                line--;
        }

        if (A.keepOneLineWhenPaging && !A.isSpeaking)
            if (txtView.getPageBreakLine() != line)
                line--;
        if (line < 0)
            line = 0;
        if (line > getRealLineCount() - 1)
            line = getRealLineCount() - 1;

        return line;
    }

    public boolean checkIfChangeChapterOrSpeak(int direction, int y, int y2) {
        int y3 = txtScroll.getScrollY();
        int lh = txtView.getLineHeight() - 3;
        if (y2 == -1 || Math.abs(y3 - y) < lh) {
            if (direction == PAGE_DOWN) {
                if (y2 != -1 && (y3 + A.getPageHeight() < txtView.getRealHeight() - lh)) {
                    txtScroll.pageScroll(View.FOCUS_DOWN);
                    if (checkDualPageMode())
                        setTxtView2Visible();
                } else
                    pageDownToNextChapter(y);
            } else
                pageUpToPriorChapter();
            return true;
        } else if (A.isSpeaking)
            speakCurrentPage(y);

        return false;
    }

    private long changeChapterTime = -1;

    private void pageDownToNextChapter(int y) {
        ActivityMain.chapterChanged = true;
        if (isValueFlip())
            txtCache.alone = true;
        if (!A.noSplitHtmls() && A.lastSplitIndex < A.splitHtmls.size() - 1) { //v1.1.7
            saveChapterTextToPriorCache(A.lastChapter, A.lastSplitIndex, htmlSrc);
            A.lastSplitIndex++;
            String progressTitle = (A.ebook != null ? A.ebook.getChapters().get(A.lastChapter).name : T.getFilename(A.lastFile)) + " ("
                    + (A.lastSplitIndex + 1) + "/" + A.splitHtmls.size() + ")";
            if (hasNextChapterText(true, progressTitle))
                return;

//			if (A.flip_animation==0)
            createProgressDlg(progressTitle);
            forceDelayFlipAnimation = true;
            txtScrollHandler.sendMessageDelayed(txtScrollHandler.obtainMessage(4, PAGE_DOWN), 50);
        } else {
            if (getBookType() == A.FILE_HTML)
                return;
            if (A.lastChapter < A.ebook.getChapters().size() - 1) {
                saveChapterTextToPriorCache(A.lastChapter, A.lastSplitIndex, htmlSrc);
                A.lastChapter++;
                A.lastPosition = 0;
                A.lastSplitIndex = 0;
                String progressTitle = A.ebook.getChapters().get(A.lastChapter).name;
                if (hasNextChapterText(true, progressTitle))
                    return;
                A.log("-------no cahce for next chapter");
//			    if (A.flip_animation==0)
                if (isBigChapter(A.lastChapter))
                    createProgressDlg(progressTitle);
                forceDelayFlipAnimation = true;
                ebookPageUp = false;
                ebookChapterDown = true;
                reloadBook();
            }
        }
    }

    private void pageUpToPriorChapter() {
        ActivityMain.chapterChanged = true;
        if (isValueFlip())
            txtCache.alone = true;
        if (!A.noSplitHtmls() && A.lastSplitIndex > 0) {
            saveChapterTextToNextCache(A.lastChapter, A.lastSplitIndex);
            A.lastSplitIndex--;
            String progressTitle = (A.ebook != null ? A.ebook.getChapters().get(A.lastChapter).name : T.getFilename(A.lastFile)) + " ("
                    + (A.lastSplitIndex + 1) + "/" + A.splitHtmls.size() + ")";
            if (hasPriorChapterText(progressTitle))
                return;

//			if (A.flip_animation==0)
            createProgressDlg(progressTitle);
            forceDelayFlipAnimation = true;
            txtScrollHandler.sendMessageDelayed(txtScrollHandler.obtainMessage(4, PAGE_UP), 50);
        } else {
            if (A.lastChapter > 0) {
                saveChapterTextToNextCache(A.lastChapter, A.lastSplitIndex);
                A.lastChapter--;
                A.lastSplitIndex = -1;
                A.lastPosition = A.ebook.getChapters().get(A.lastChapter).size;
                String progressTitle = A.ebook.getChapters().get(A.lastChapter).name;
                if (hasPriorChapterText(progressTitle))
                    return;

//				if (A.flip_animation==0)
                if (isBigChapter(A.lastChapter))
                    createProgressDlg(progressTitle);
                A.lastSplitIndex = 0;
                forceDelayFlipAnimation = true;
                ebookPageUp = true;
                reloadBook();
                if (A.flip_animation == 0)
                    forceUpdateForFitHardwareAccelerate(50);
            }
        }
    }

    private boolean isBigChapter(int id) {
        if (A.ebook == null || id < 0 || id >= A.ebook.getChapters().size())
            return false;

//		if (A.flip_animation > 0 && A.sysHasNavBar())
//			return false;

        Chapter c = A.ebook.getChapters().get(id);
        int base = 8000;

        if (Build.VERSION.SDK_INT >= 21 || Runtime.getRuntime().maxMemory() > 100 * 1000000)
            base *= 2;

        if (c.css != null && c.css.styles != null) {
            if (c.css.styles.size() > 1000)
                base /= 5;
            else if (c.css.styles.size() > 600)
                base /= 4;
            else if (c.css.styles.size() > 300)
                base /= 3;
            else if (c.css.styles.size() > 100)
                base /= 2;
        }

        return c.size > base;
    }

    private int getPageLineCount() {
        float rest = (float) A.getPageHeight() / txtView.getLineHeight();
        int pageLineCount = (int) rest;
        boolean addOne = pageAddOneLine(rest);
        if (addOne)
            pageLineCount++;
        return pageLineCount;
    }

    private boolean pageAddOneLine(float rest) {
        return (rest - (int) rest) > A.oneLineTag();
    }

    private Handler txtScrollHandler = new Handler() { //check if need to load next/prior chapter
        public void handleMessage(Message msg) {
            if (txtView.getLayout() == null)
                return;

            try {
                switch (msg.what) {
                    case 0: //to next chapter
                        txtScrollTo(0);
                        forceDelayFlipAnimation = false;
                        ebook_inLoading = false;
                        break;
                    case 1: //to prior chapter, go bottom
                        disableTxtViewDraw();
                        int tvH = txtView.getRealHeight();
                        int svH = A.getPageHeight();
                        int lh = txtView.getLineHeight();
                        int toY = 0;

                        if (dualPageEnabled()) {
                            if (tvH > svH * 2 - lh) {
//								fixBottomForScrollTo(txtView.getRealHeight());
                                int line = txtView.getLayout().getLineForVertical(tvH - svH * 2 + lh * 3);
                                toY = txtView.getLineTop2(line);
                            }
                        } else {
                            int line = txtView.getLayout().getLineForVertical(tvH - svH + lh * 2);

                            if (txtView.getSpanned() != null) {
                                int start = txtView.getLayout().getLineStart(line);
                                CSS.PAGE_BREAK[] sps = txtView.getSpanned().getSpans(start, txtView.getText().length(), CSS.PAGE_BREAK.class);
                                if (sps.length > 0) {
                                    int start2 = txtView.getSpanned().getSpanStart(sps[sps.length - 1]);
                                    if (start2 > start && start2 < txtView.getText().length())
                                        line = txtView.getLayout().getLineForOffset(start2);
                                }
                            }

                            toY = txtView.getLineTop2(line);
                        }

                        goToLastTime = SystemClock.elapsedRealtime();
                        txtScrollTo(toY);
                        enableTxtViewDraw();
                        forceDelayFlipAnimation = false;
                        ebook_inLoading = false;

                        break;
                    case 2: //scroll html by percent
                        long y = (Integer) msg.obj;
                        y = y * txtView.getRealHeight() / A.maxHtmlChapterSize;
                        txtScrollTo((int) y);
                        break;
                    case 3: //scroll to position
                        int line = txtView.getLayout().getLineForOffset((Integer) msg.obj);
                        int y2 = txtView.getLineTop2(line);
                        txtScrollTo(y2);
                        break;
                    case 4: //load splitted part of chapter
                        ebook_inLoading = true;
                        clearTxtView();
                        if (A.ebook == null || A.ebook.isHtml()) { //html or epub
                            imageGetter = A.ebook == null ? createHtmlBookImageGetter() : A.ebook.getMyImageGetter();
                            if (A.noSplitHtmls())
                                return;
                            if (A.lastSplitIndex >= A.splitHtmls.size())
                                A.lastSplitIndex = A.splitHtmls.size() - 1;
                            htmlSrc = A.splitHtmls.get(A.lastSplitIndex);
                            htmlSrc = A.chineseJianFanConvert(htmlSrc);
                            txtViewSetText(MyHtml.fromHtml(htmlSrc, imageGetter, A.lastChapter));
                        } else {
                            String text = A.chineseJianFanConvert(A.splitHtmls.get(A.lastSplitIndex));
                            txtViewSetText(text);
                        }

                        txtScrollHandler.sendEmptyMessageDelayed((Integer) msg.obj == PAGE_DOWN ? 0 : 1, 50);
                        if (A.isSpeaking)
                            speakCurrentPage(-100);
                }
            } finally {
                changeChapterTime = SystemClock.elapsedRealtime();
                hideProgressDlg();
            }
        }
    };

    protected void saveFlipShot(boolean forceVisible) {
        if (inPreShow)
            return;
        if (!forceVisible) {
            if (A.isSpeaking)
                return;
            if (A.isInAutoScroll)
                return;
            if (!tmpHorizaontalScroll && A.flip_animation < 3)
                return;
            if (tmpHorizaontalScroll && A.flip_animation < 3 && A.hori_fling_animation < 3)
                return;
            if (isValueFlip()) // tilt use old style
                return;
        }

        inverseLayoutVisible(true);
        if (topLay != null)
            topLay.setAnimation(null);
        if (bottomLay != null)
            bottomLay.setAnimation(null);

        if (!forceVisible && ignoreFirstOrLastPageFlip(pageDirection, false))
            return;

        boolean samePage = lastFlipScrollY == getFlipScrollY() && !T.isRecycled(tmpFlipShot2);
        if (!samePage)
            T.recycle(tmpFlipShot2);
        Bitmap bm = samePage ? tmpFlipShot2 : null;

        if (!forceVisible) {
//			A.log("getAnimationShot(1)");
            contentLay.setAnimationState(false);
            if (bm == null)
                bm = getPageShot(true, true);
            flipView.setBitmap1(bm);
            flipView.animateState = 0;
        } else {
            if (bm == null)
                bm = getPageShot(true, true);
        }

        if (bm != null) {
            flipView.setImageBitmap(bm);
            if (forceTiltSpeed != 0 && A.isFlipNone())
                try {
                    flipView.setBitmap1(bm.copy(bm.getConfig(), false));
                } catch (OutOfMemoryError e) {
                    System.gc();
                }
            flipView.setVisibility(View.VISIBLE);
            flipView.invalidate();
        } else
            A.log("*************saveFlipShot failed***********");
    }

    //-------curl:-----------------------------------------------
    private boolean doCurlFlip() {
        if (isPdf() && pdf.textReflow)
            return false;
        if (!get3dCurlShot(true)) {
            curlDelayedMsg = createCurlMsg(0, null);
            return true;
        }
        Message m = createCurlMsg(0, null);
        handleCurlMessage(m);
        return true;
    }

    private boolean doValueFlip() {
        if (isPdf() && pdf.textReflow)
            return false;
        if (!getValueShot(true)) {
            curlDelayedMsg = createValueStartMsg();
            return true;
        }
        handleValueMessage(createValueStartMsg());
        return true;
    }

    long curlBackTime = 0;
    private A.AfterFlipCurl onFlipCurlingEnd = new A.AfterFlipCurl() {
        public void Finished(boolean success) {
            if (!success) {
                flippingAnimationTime = 0;
                if (getBookType() == A.FILE_TXT && currentPage > 1)
                    currentPage--;
                if (pageDirection == PAGE_DOWN) {
                    curl3dCover.setTag(null);
                }
                curlBackTime = SystemClock.elapsedRealtime();
                int tmp = A.lastChapter;
                pageScroll(-pageDirection, true, false);
                if (tmp == A.lastChapter)
                    curlBackTime = 0;
            }
            handleCurlMessage(createCurlMsg(1, null));
        }
    };

    private Handler curl2dHandler;

    private void curlHandlerSendMessage(Message msg) {
        if (isValueFlip() || isHoriValueFlip()) {
            handleCurlMessage(msg);
            return;
        }
        if (curl2dHandler == null) {
            curl2dHandler = new Handler() {
                public void handleMessage(Message msg) {
                    handleCurlMessage(msg);
                }
            };
        }
        curl2dHandler.sendMessage(msg);
    }

    ;

    long cachingPageShotsTime;

    private boolean waitingForCahingShots() {
        if (cachingPageShotsTime > 0) {
            if (SystemClock.elapsedRealtime() - cachingPageShotsTime > 2500)
                cachingPageShotsTime = 0;
            A.log("waitingForCahingShots:" + A.lastChapter + "," + txtScroll.getScrollY());
            return true;
        }
        return false;
    }

    private boolean createCachePageShotsHandler(int delay) {
        cachingPageShotsTime = 0;
        if (!T.isRecycled(tmpFlipShot3) && tmpFlipShot3 != tmpFlipShot2)
            T.recycle(tmpFlipShot3);
        tmpFlipShot3 = null;

        if (!A.isFlipCurl())
            return false;
        if (flippingAnimationTime > 0)
            return false;
        if (!curl3dCoverShowed && Build.VERSION.SDK_INT < 11)
            return false;
        if (getBookType() == A.FILE_PDF)
            return false;
        if (getBookType() == A.FILE_TXT && dualPageEnabled())
            return false;

        handler.removeMessages(DELAY_CACHE_SHOT1);
        if (delay == 0)
            return createCachePageShots();
        else
            handler.sendEmptyMessageDelayed(DELAY_CACHE_SHOT1, delay);
        return false;
    }

    boolean delayCache, txtReload;

    //	boolean firstTimeCahce=true;
    private boolean createCachePageShots() {
        try {
            if (getting3dCurlShot || isEndOfBook() || A.isSpeaking || A.isInAutoScroll)
                return false;
            if (getBookType() == A.FILE_EBOOK && lastLineDisplayed())
                return false;

//			A.saveMemoryLog("createCachePageShots(1) "+A.lastChapter+","+txtScroll.getScrollY());
            if (flippingAnimationTime > 0)
                return false;

//			A.logTime = SystemClock.elapsedRealtime();
            cachingPageShotsTime = SystemClock.elapsedRealtime();
            get3dAboutColor(); //v3.0
            boolean samePage = lastFlipScrollY == getFlipScrollY() && !T.isRecycled(tmpFlipShot2);
            T.recycle(tmpFlipShot1);
            tmpFlipShot1 = tmpFlipShot3 = null;
            if (!samePage) {
                T.recycle(tmpFlipShot2);
                tmpFlipShot2 = getPageShot(false, HighQuality3D());
                lastFlipScrollY = getFlipScrollY();
            }

            if (curl3dLay.getVisibility() != View.VISIBLE) {
                flipView.bm1 = tmpFlipShot2;
                flipView.for3dCurlCache = true;
                flipView.setVisibility(View.VISIBLE);
            }

            int v1 = txtView2.getVisibility();
            cacheScrollY = txtScroll.getScrollY();
            txtReload = false;
            if (getBookType() == A.FILE_TXT) {
                int line = txtView.getLayout().getLineForVertical(txtScroll.getScrollY());
                cacheTxtText = txtView.getLineText(line);
                cacheTxtText2 = line < txtView.getLineCount() - 1 ? txtView.getLineText(line + 1) : null;
            }
            pageScroll(PAGE_DOWN, false, true);

//			if (firstTimeCahce) //罕见bug, 刚启动时createCachePageShots2()的showReadProgress(-1)可能不更新
//				firstTimeCahce = false;
//			else
//				showReadProgress(1);

            delayCache = v1 != txtView2.getVisibility();
            if (delayCache) {
                handler.sendEmptyMessage(DELAY_CACHE_SHOT2);
            } else
                createCachePageShots2();
            return true;
        } catch (Exception e) {
            A.error(e);
            return false;
        }
    }

    private int cacheScrollY;
    private String cacheTxtText, cacheTxtText2;

    private void createCachePageShots2() {
        tmpFlipShot3 = getPageShot(false, HighQuality3D());
//		A.saveMemoryLog("createCachePageShots(3) "+A.lastChapter+","+txtScroll.getScrollY());
        if (txtReload) {
            pageScroll(PAGE_UP, false, true);
            int line = txtView.getLayout().getLineForVertical(txtScroll.getScrollY());
            int lineCount = txtView.getLineCount();
            if (!txtView.getLineText(line).equals(cacheTxtText) ||  //zth: v3.1.1 fix unknow error temporarily
                    (cacheTxtText2 != null && line < lineCount - 1 && !txtView.getLineText(line + 1).equals(cacheTxtText2))) {
                int newY = -1;
                for (int i = 1; i < 50; i++) {
                    if (line - i > 0 && txtView.getLineText(line - i).equals(cacheTxtText)
                            && (cacheTxtText2 == null || txtView.getLineText(line - i + 1).equals(cacheTxtText2)))
                        newY = txtView.getLineTop2(line - i);
                    else if (line + i < lineCount - 2 && txtView.getLineText(line + i).equals(cacheTxtText)
                            && (cacheTxtText2 == null || txtView.getLineText(line + i + 1).equals(cacheTxtText2)))
                        newY = txtView.getLineTop2(line + i);
                    if (newY > 0) {
                        txtScrollTo(newY);
                        A.log("*************now:" + txtScroll.getScrollY() + ", shouldbe:" + newY + ", offset:" + i);
                        break;
                    }
                }
            }
        } else
            txtScrollTo(cacheScrollY);
//		showReadProgress(-1);
        statusHandlerSendMessage(0, 0);

        if (delayCache) {
            handler.sendEmptyMessage(DELAY_HIDE_CURL_FLIPVIEW);
//			A.saveMemoryLog("createCachePageShots(4-delay) "+A.lastChapter+","+txtScroll.getScrollY());
        } else {
            endCachingShot();
        }
//		A.saveMemoryLog("createCachePageShots(5) " + A.lastChapter + "," + txtScroll.getScrollY());
    }

    private void endCachingShot() {
        if (curl3dLay.getVisibility() != View.VISIBLE) {
            flipView.setVisibility(View.GONE);
            flipView.for3dCurlCache = false;
        } else
            setCurl3dVisibility(false);
        cachingPageShotsTime = 0;
    }

    Message curlDelayedMsg;

    private void handleCurlMessage(Message msg) {
        if (isValueFlip() || isHoriValueFlip()) {
            handleValueMessage(msg);
            return;
        }

        if (curl3d == null)
            return;
        if (curl3d.mAnimate || waitingForCahingShots()) {
            pagingByMove = true;
            return;
        }

        switch (msg.what) { //3d curl
            case 0: //click to start curling
                curl3d.updateBitmaps();
                setCurl3dVisibility(true);
                curl3d.pageScroll(pageDirection == PAGE_DOWN);
                flippingAnimationTime = SystemClock.elapsedRealtime();
                break;

            case 1: //curling is over
//				A.saveMemoryLog("clearShots(1):");
//				A.log("3d curl end(1)");
                if (delayHideCurl) {
                    delayHideCurl2 = true;
                    return;
                }
//				A.log("3d curl end(2)");
                contentLay.setAnimationState(false);
                contentLay.invalidate();
                curl3d.clearAllShots();
                boolean cache = curlBackTime == 0 || pageDirection != PAGE_UP;
                if (cache) {
                    setCurl3dVisibility(false);
                    setFlipViewINVISIBLE(true);
                }
                T.recycle(tmpFlipShot1);
//				A.saveMemoryLog("clearShots(2):");
                flippingAnimationTime = 0;
                if (cache && !createCachePageShotsHandler(0)) {
                    setCurl3dVisibility(false);
                }
                break;

            case 2: //finger is down
                curlDownMsgSending = false;
                break;

            case 3: //finger is moved
                if (pagingByMove)
                    return;
                if (forceDisableCurlEvent)
                    return;
                if (!sentCurlTouchEvent && !enoughTurnMove(msg.arg1, pressDownX, true)) //v1.9.4
                    return;
                if (!gotCurlImage) {
                    pageDirection = msg.arg1 > pressDownX ? PAGE_UP : PAGE_DOWN;
                    if (ignoreFirstOrLastPageFlip(pageDirection, true))
                        return;
                    gotCurlImage = true;

                    if (!sentCurlTouchEvent && isOppositeTouch(msg.arg1, pressDownX)) {
                        pagingByMove = true;
                        pageScroll(pageDirection);
                    } else {
                        if (!get3dCurlShot(true)) {
                            curlDelayedMsg = createCurlMsg(3, hMotionEvent);
                            return;
                        }
                        handleCurlMessage(createCurlMsg(3, hMotionEvent));
                    }
                } else {
                    if (forceDelayFlipAnimation || curlDelayedMsg != null)
                        return;
                    MotionEvent e2 = MotionEvent.obtain(hMotionEvent);
                    if (!sentCurlTouchEvent) {
                        sentCurlTouchEvent = true;
                        e2.setLocation(pressDownX, e2.getY()); //v4.2 while swipe from left across middle to right
                        e2.setAction(MotionEvent.ACTION_DOWN);
                        curl3d.updateBitmaps();
                        setCurl3dVisibility(true);
                        curl3d.simulateOnTouch(e2, false);
                        flippingAnimationTime = SystemClock.elapsedRealtime();
                    } else {
                        e2.setLocation(getTouchX(e2), e2.getY());
                        curl3d.simulateOnTouch(e2, false);
                    }
                }
                break;

            case 4: //finger is up
                if (pagingByMove || forceDisableCurlEvent) {
                    return;
                }

                if (!gotCurlImage) {
                    gotCurlImage = true;
                    if (Math.abs(mCurlFlingDirection) > 600)
                        pageDirection = mCurlFlingDirection > 0 ? PAGE_UP : PAGE_DOWN;
                    else if (enoughTurnMove(msg.arg1, pressDownX, true))
                        pageDirection = msg.arg1 > pressDownX ? PAGE_UP : PAGE_DOWN;
                    else
                        pageDirection = getCurlClickDirection(hMotionEvent);

                    if (pageDirection == 0)
                        return;
                    if (ignoreFirstOrLastPageFlip(pageDirection, true))
                        return;
                    if (ignorePdfFlingAction())
                        return;
                    if (!get3dCurlShot(true)) {
                        curlDelayedMsg = createCurlMsg(0, null);
                    } else
                        handleCurlMessage(createCurlMsg(0, null));
                } else {
                    if (curlDelayedMsg != null) {
                        handler.sendMessageDelayed(handler.obtainMessage(DELAY_CURL3D_TOUCH_UP, msg.what, 0), 10);
                        return;
                    }

                    if (curl3dLay.getVisibility() == View.VISIBLE) {
                        MotionEvent e2 = MotionEvent.obtain(hMotionEvent);
                        e2.setLocation(getTouchX(e2), e2.getY());
                        if ((mCurlFlingDirection > 200 && pageDirection == PAGE_DOWN) || (mCurlFlingDirection < -200 && pageDirection == PAGE_UP)) {
                            curl3d.simulateOnTouch(e2, true);
                        } else {
                            curl3d.simulateOnTouch(e2, false);
                        }
                    } else
                        flippingAnimationTime = 0;
                }
                gotCurlImage = false;
                break;
        }
    }

    private void handleValueMessage(Message msg) {
        if (flippingAnimationTime > 0) {
            A.log(" ================== handleValueMessage()flippingAnimationTime > 0 ================== ");
            pagingByMove = true;
            return;
        }
        switch (msg.what) { //3d curl
            case 0: //click to start curling
                startValueAnimation(msg.arg1 - (int) pressDownX);
                break;

            case 1: //curling is over
                break;

            case 2: //finger is down
                curlDownMsgSending = false;
                break;

            case 3: //finger is moved
                if (pagingByMove)
                    return;
                if (forceDisableCurlEvent)
                    return;
                if (!sentCurlTouchEvent && !enoughTurnMove(msg.arg1, pressDownX, true)) //v1.9.4
                    return;
                if (!gotCurlImage) {
                    pageDirection = msg.arg1 > pressDownX ? PAGE_UP : PAGE_DOWN;
                    if (ignoreFirstOrLastPageFlip(pageDirection, true))
                        return;
                    gotCurlImage = true;

//					if (!sentCurlTouchEvent && isOppositeTouch(msg.arg1, pressDownX)) {
//						pagingByMove = true;
//						pageScroll(pageDirection);
//					} else {
                    valueDragManually = true;
                    if (!getValueShot(true)) {
                        curlDelayedMsg = createCurlMsg(3, hMotionEvent);
                        return;
                    }
                    setValueAnimationPos((int) (hMotionEvent.getX() - pressDownX), true, false);
                    handleValueMessage(createCurlMsg(3, hMotionEvent));
//					}
                } else {
                    if (forceDelayFlipAnimation || curlDelayedMsg != null)
                        return;
                    sentCurlTouchEvent = true;
                    setValueAnimationPos((int) (hMotionEvent.getX() - pressDownX), true, false);
                }
                break;

            case 4: //finger is up
                if (pagingByMove || forceDisableCurlEvent) {
                    return;
                }

                if (!gotCurlImage) {
                    gotCurlImage = true;
                    if (Math.abs(mCurlFlingDirection) > 600)
                        pageDirection = mCurlFlingDirection > 0 ? PAGE_UP : PAGE_DOWN;
                    else if (enoughTurnMove(msg.arg1, pressDownX, true))
                        pageDirection = msg.arg1 > pressDownX ? PAGE_UP : PAGE_DOWN;
                    else
                        pageDirection = getCurlClickDirection(hMotionEvent);

                    if (pageDirection == 0)
                        return;
                    if (ignoreFirstOrLastPageFlip(pageDirection, true))
                        return;
                    if (ignorePdfFlingAction())
                        return;
                    if (!getValueShot(true)) {
                        curlDelayedMsg = createValueStartMsg();
                    } else
                        handleValueMessage(createValueStartMsg());
                } else {
                    if (curlDelayedMsg != null) {
                        handler.sendMessageDelayed(handler.obtainMessage(DELAY_CURL3D_TOUCH_UP, msg.what, 0), 10);
                        return;
                    }
                    if (valueCacheOk) {
                        MotionEvent e2 = MotionEvent.obtain(hMotionEvent);
                        e2.setLocation(getTouchX(e2), e2.getY());
                        if ((mCurlFlingDirection > 200 && pageDirection == PAGE_DOWN) || (mCurlFlingDirection < -200 && pageDirection == PAGE_UP)) {
                            valueScrollBack = true;
                        }
                        handleValueMessage(createCurlMsg(0, hMotionEvent));
                    } else
                        flippingAnimationTime = 0;
                }
                gotCurlImage = false;
                break;
        }
    }

    private Message createValueStartMsg() {
        MotionEvent e = hDownEvent == null ? MotionEvent.obtain(0, 0, 0, 0, 0, 0)
                : MotionEvent.obtain(hDownEvent);
        e.setLocation(pressDownX, e.getY());
        return createCurlMsg(0, hDownEvent);
    }

    private boolean getValueShot(boolean front) {
        if (front)
            curlDelayedMsg = null;

        if (!front && forceDelayFlipAnimation) {
            handler.sendEmptyMessageDelayed(DELAY_CURL3D_SHOT2, 10);
            return false;
        }

        try {
            if (front) {
                preValueAnimation();
                pageScroll(pageDirection, true, false);
                saveLastPostion(false);
                showReadProgress(pageDirection == PAGE_DOWN ? 1 : -1);
                return getValueShot(false);
            } else if (curlDelayedMsg != null) {
                Message delayedMsg = curlDelayedMsg;
                curlDelayedMsg = null;
                handleValueMessage(delayedMsg);
            }
            return true;
        } catch (Exception e) {
            A.error(e);
        }
        return false;
    }

    private boolean enoughTurnMove(float nowX, float downX, boolean checkOpposite) {
        boolean opposite = !checkOpposite ? false : isOppositeTouch(nowX, downX);
        int value = opposite ? A.d(20) : A.d(8);
        return Math.abs(nowX - downX) > value;
    }

    private boolean isOppositeTouch(float nowX, float downX) {
        return nowX > baseFrame.getWidth() / 2 ? nowX > downX : nowX < downX;
    }

    private boolean ignoreFirstOrLastPageFlip(int direction, boolean showTip) {
        boolean result;
        if (direction == PAGE_DOWN)
            result = isEndOfBook();
        else
            result = isBeginOfBook();
        if (result && showTip)
            T.showToastText(this, A.fanti(direction == PAGE_DOWN ? "已到最后一页" : "已是第一页"));
        return result;
    }

    private boolean isBeginOfBook() {
        if (isPdf()) {
            if (pdfGetCurrPageNo() == 0) {
                if (pdf.textReflow) {
                    return txtScroll.getScrollY() == 0;
                } else if (Global.def_view == 0) {
                    PDFPosition p = pdf.pdfView.viewGetPos();
                    return p.page_y == 0;
                } else return true;
            } else
                return false;
        } else
            return txtScroll.getScrollY() == 0 && (getBookType() == A.FILE_TXT || (A.lastChapter == 0 && A.lastSplitIndex == 0));
//		return isPdf() ? (pdfGetCurrPageNo() == 0 && (!pdf.textReflow || txtScroll.getScrollY() == 0))
//				: (txtScroll.getScrollY() == 0 && (getBookType() == A.FILE_TXT || (A.lastChapter == 0 && A.lastSplitIndex == 0)));
    }

    private boolean ignorePdfFlingAction() {
        return (ignorePdfCurl(hMotionEvent)) && Math.abs(getTouchX(hMotionEvent) - pressDownX) > A.d(8);
    }

    private boolean isCurlClick;
    private boolean curlDownMsgSending;
    private boolean pagingByMove, sentCurlTouchEvent;
    private boolean gotCurlImage;
    private boolean forceDisableCurlEvent;
    private float continueMoveX;

    private boolean doOnCurlTouch(View v, MotionEvent event) {
        if (inPreShow)
            return false;
        if (layoutVisible)
            return false;
        if (A.isSpeaking)
            return false;
        if (!A.isFlipCurl() && !isValueFlip())
            return false;
        if (isPdf() && pdf.textReflow)
            return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isCurlClick = false;
            if (flippingAnimation() || waitingForCahingShots())
                return true;

            gotCurlImage = false;
            pagingByMove = false;
            sentCurlTouchEvent = false;
            hideScrollbarProgress();
            forceDisableCurlEvent = false;
            isCurlClick = true;
            if (isMiddleTap(event))
                isCurlClick = false;
            if (!isCurlClick)
                return false;

            mCurlFlingDirection = 0;
            hideDotViews();
            curlDownMsgSending = true;
            handleCurlMessage(createCurlMsg(2, event));
        }

        if (A.moveStart)
            return false;

        if (pagingByMove && event.getAction() == MotionEvent.ACTION_MOVE) {//allow continue moving to turn page again
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (!gotCurlImage)
                if ((A.adjustBrightness && do_AdjustBrightness_Event(event)) || (A.adjustFontSizeAtSlide && do_AdjustFontSize_Event(event))) {
                    isCurlClick = false;
                    setCurlViewINVISIBLE();
                    forceDisableCurlEvent = true;
                    return true;
                }
            if (!gotCurlImage) {
                if (dotVisible())
                    return true;
                if (isTouchInEdge(event))
                    return true;
            }
            if (ignorePdfCurl(event))
                return false;

            if (!isCurlClick) {
                if (isMiddleTap(event) || pagingByMove) {
                    float xMove = Math.abs(getTouchX(event) - pressDownX);
                    float yMove = Math.abs(event.getY() - pressDownY);
                    if (xMove > yMove && enoughTurnMove(getTouchX(event), pressDownX, true)) {//v1.9.7
                        pagingByMove = true;
                        pageDirection = getTouchX(event) > pressDownX ? PAGE_UP : PAGE_DOWN;
                        pageScroll(pageDirection);
                        return true;
                    }
                }
                return !isMiddleTap(event);
            }
            Message m = createCurlMsg(3, event);
            if ((A.isFlipCurl()) && !curlDownMsgSending)
                handleCurlMessage(m);
            else
                curlHandlerSendMessage(m);
        }

        if (!ignorePdfCurl(event)) {
            if (mCurlDetector == null)
                mCurlDetector = new GestureDetector(new MyCurlGesture());
            mCurlDetector.onTouchEvent(event);
        }

        if (event.getAction() == MotionEvent.ACTION_UP && !forceDisableCurlEvent) {
            if (do_Gesture_Event(event) && swipeGuesture) {
                if (gotCurlImage)
                    onFlipCurlingEnd.Finished(false);
                return true;
            }

            if (gotCurlImage || mTouchTimes <= 10) {
                if (gotCurlImage || pressTime == -1 || SystemClock.elapsedRealtime() - pressTime < A.ONTOUCH_IGNORETIME) {
                    boolean isUrlClick = false;
                    if (!gotCurlImage) {
                        isUrlClick = isUrlClick(event, true);
                        float xMove = Math.abs(getTouchX(event) - pressDownX);
                        float yMove = Math.abs(event.getY() - pressDownY);
                        if (dotVisible())
                            return true;
                        if (clickForBookmark(event))
                            return true;
                        if (isMiddleTap(event) && yMove < 12 && xMove < 12) {
                            if (!isUrlClick)
                                inverseLayoutVisible(false);
                            return true;
                        }
                    }
                    if (gotCurlImage || (isCurlClick && !isUrlClick)) {
                        Message m = createCurlMsg(4, event);
                        if ((A.isFlipCurl()) && !curlDownMsgSending)
                            handleCurlMessage(m);
                        else
                            curlHandlerSendMessage(m);
                        return true;
                    }
                }
            }
            return !isMiddleTap(event);
        }

        return true;
    }

    private boolean isHoriValueFlip() {
        return A.isFlipNone() && A.isHoriValueFlip();
    }

    private boolean isValueFlip() { // use new ValueAnimation
        if (!A.isFlipHorizShift())
            return false;
        if (isPdf())
            return false;
        if (A.dualPageEnabled())
            return false;
        return true;
    }

    void setValueAnimationPos(int value, boolean verify, boolean post) {
//		A.log("setShift:"+value+(verify?" ##verify##":""));
        preValueAnimation();
        if (verify && ((pageDirection == PAGE_DOWN && value > 0) || (pageDirection == PAGE_UP && value < 0))) {
            pressDownX = hMotionEvent.getX();
            value = 0;
//			return;
        }
        value = Math.abs(value);
        int w = baseFrame.getWidth();
        int h = baseFrame.getHeight();

        if (value > 0)
            setContentLayDisabled(false);

        int flip_animation = A.isFlipNone() ? A.hori_fling_animation : A.flip_animation;
        if (flip_animation == A.FLIP_HORIZONTAL) {
            if (pageDirection == PAGE_DOWN) {
                ScrollView2.setClipPos(0, w - value, pageDirection);
                scrollCache.setShift(-value, 0, post);
                txtScroll.setShift(w - value, 0, post);
            } else {
                ScrollView2.setClipPos(value, w, pageDirection);
                scrollCache.setShift(value, 0, post);
                txtScroll.setShift(value - w, 0, post);
            }
        }

        if (flip_animation == A.FLIP_SHIFT_HORIZONTAL) {
            if (pageDirection == PAGE_DOWN) {
                ScrollView2.setClipPos(0, w - value, pageDirection);
                scrollCache.setShift(-value, 0, post);
                txtScroll.setShift(0, 0, post);
            } else {
                ScrollView2.setClipPos(value, w, pageDirection);
                scrollCache.setShift(0, 0, post);
                txtScroll.setShift(value - w, 0, post);
            }
        }
    }

    private void setContentLayDisabled(boolean disabled) {
//		if (contentLay.disableDraw != disabled){
//			contentLay.disableDraw = disabled;
//			if (!disabled)
//				contentLay.invalidate();
//		}
        if (txtScroll.disableDraw != disabled) {
            txtScroll.disableDraw = disabled;
        }
    }

    boolean valueScrollBack;
    boolean valueCacheOk;
    int valueChapter;

    private void preValueAnimation() {
        if (!valueCacheOk) {
//			A.log("preValueAnimation");
            txtCache.alone = false;
            if (txtCache.getText() != txtView.getText()) {
                scrollCache.setPadding(txtScroll.getPaddingLeft(), txtScroll.getPaddingTop(), txtScroll.getPaddingRight(), txtScroll.getPaddingBottom());
//			    A.setBackgroundImage(scrollCache);
                A.setTextFont(txtCache, false);
                A.setTxtViewTypeface(txtCache);
                A.setLineSpace(txtCache);
                A.setFontSpace(txtCache);
                txtCache.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtView.getTextSize());
                txtCache.setText(txtView.getText());
                valueChapter = A.lastChapter;
            }

            fixBottomForScrollTo(txtCache, txtScroll.getScrollY());
            scrollCache.scrollTo(0, txtScroll.getScrollY());
            scrollCache.setShift(0, 0, false);
            scrollCache.setVisibility(View.VISIBLE);

            setContentLayDisabled(true);
            handler.removeMessages(HIDE_TXTCACHE);
            handler.sendEmptyMessageDelayed(HIDE_TXTCACHE, 1500);
            valueScrollBack = false;
            valueCacheOk = true;
        }
    }

    private void hideTxtCacheHandler() {
        if (valueCacheOk || scrollCache.getVisibility() == View.VISIBLE) {
            handler.removeMessages(HIDE_TXTCACHE);
            if (flippingAnimationTime == 0 && lastTouchAction == MotionEvent.ACTION_UP) {
                A.log("========force hide txtCache==========");
                scrollCache.setVisibility(View.INVISIBLE);
                valueCacheOk = false;
                setContentLayDisabled(false);
            } else
                handler.sendEmptyMessageDelayed(HIDE_TXTCACHE, 1500);
        }
    }

    boolean valueDragManually;
    int valueExecuteCount;

    void startValueAnimation(int value) {
        value = Math.abs(value);
        if (value < A.dr(1f)) {
            value = 0;
            if (valueDragManually) {
                A.log("---------------onAnimationEnd(2)-----------------");
                valueScrollBack = true;
                doValueAnimationEnd();
                return;
            }
        }

        int w = baseFrame.getWidth();
        long duration = 600 - A.flipSpeed * 10;
        if (value != 0)
            duration = (long) (valueScrollBack ? duration * value / w : duration * (w - value) / w);
        if (duration < 100)
            duration = 100;
//		A.log("startValueAnimation: "+value+" -> "+w+" duration:"+duration);

        if (flippingAnimationTime > 0) {
            A.log("===========valueAnimation already start=========");
            return;
        }

        handler.removeMessages(HIDE_TXTCACHE);
        preValueAnimation();
        valueExecuteCount = 0;
        flippingAnimationTime = SystemClock.elapsedRealtime();

        valueStart = value;
        valueEnd = valueScrollBack ? 0 : w;
        valueDuration = duration;

        scrollCache.recordDrawCount = true;
        scrollCache.drawCount = 0;
//		scrollCache.lastDrawTime = txtScroll.lastDrawTime = SystemClock.elapsedRealtime();

        if (disableShiftWithTimer)
            shiftAnimateWithValue();
        else
            shiftAnimateWithTimer();
    }

    int valueStart, valueEnd;
    long valueDuration;
    boolean disableShiftWithTimer = true;

    private void shiftAnimateWithValue() {
        ValueAnimator a = ValueAnimator.ofInt(valueStart, valueEnd);
        a.setDuration(valueDuration);
        ValueAnimator.setFrameDelay(15); //todo: useless, why
        a.setInterpolator(new DecelerateInterpolator());
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                valueExecuteCount++;
                int value = (int) animation.getAnimatedValue();
//				A.log("onAnimationUpdate:"+value);
//				A.log(valueExecuteCount+"/"+(SystemClock.elapsedRealtime()-txtScroll.lastDrawTime));
                txtScroll.lastDrawTime = SystemClock.elapsedRealtime();
                if (value != 0)
                    setValueAnimationPos(value, false, false);
            }
        });
        a.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//				A.log("onAnimationStart");
                if (txtCache.getHeight() < txtView.getHeight() * 3 / 2)
                    A.setViewBottom(txtCache, txtView.getHeight() * 2);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                scrollCache.recordDrawCount = false;
//				A.log("#ValueAnimatinoEnd, taskCount:" + valueExecuteCount + " fs:" + (1000*valueExecuteCount/valueDuration)
//						+ " || viewDraw: " + scrollCache.drawCount + " fs:" + (1000*scrollCache.drawCount/valueDuration));
                doValueAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        a.start();
    }

    long fsCount, fsFailed;

    private void shiftAnimateWithTimer() {
        new Timer().schedule(new TimerTask() {
            int failed, lastDraw;
            long lastFailedTime;

            @Override
            public void run() {
                int drawCount = scrollCache.drawCount - lastDraw;
                lastDraw = scrollCache.drawCount;
                failed = drawCount > 0 ? 0 : failed + 1;
                if (failed == 1)
                    lastFailedTime = SystemClock.elapsedRealtime();
                if (failed > 5) {
                    A.log("============= no onDraw() execute ============");
                    flippingAnimationTime += (SystemClock.elapsedRealtime() - lastFailedTime + 20);
                    failed = 0;
                }

                valueExecuteCount++;
//				A.log(valueExecuteCount+"/"+(SystemClock.elapsedRealtime()-txtScroll.lastDrawTime));
//				txtScroll.lastDrawTime = SystemClock.elapsedRealtime();
                float elapsed = SystemClock.elapsedRealtime() - flippingAnimationTime;
                if (elapsed >= valueDuration) {
                    scrollCache.recordDrawCount = false;
                    long fs = 1000 * scrollCache.drawCount / valueDuration;
//					A.log("*TimerAnimationEnd, taskCount:" + valueExecuteCount + " fs:" + (1000*valueExecuteCount/valueDuration)
//							+ " | viewDraw: " + scrollCache.drawCount + " fs:" + fs);

					/*fsCount++;
					if (fs < 45)
						fsFailed++;
					if ((fsCount > 3 && fsFailed == fsCount) || (fsCount > 10 && fsFailed > fsCount/2))
						disableShiftWithTimer = true; //todo: switch to ValueAnimator?
					if (fsCount > 20) {
						fsCount = fsFailed = 0;
					}*/

                    handler.sendEmptyMessage(VALUE_ANIMATION_END);
//					doValueAnimationEnd();
                    cancel();
                } else {
                    int l = valueEnd - valueStart;
                    double frameStep = pageDirection != PAGE_DOWN ? 3 : 3;
                    float off = l - (l * (float) Math.pow((valueDuration - elapsed), frameStep) / (float) Math.pow(valueDuration, frameStep));
                    int value = (int) (valueStart + off);
                    setValueAnimationPos(value, false, true);
                }
            }
        }, 0, 15);
    }

    private void doValueAnimationEnd() {
        valueCacheOk = false;
        valueDragManually = false;
        flippingAnimationTime = 0;
        txtScroll.setShift(0, 0, false);
        ScrollView2.setClipPos(0, 0, 0);
        setContentLayDisabled(false);

        if (valueScrollBack) {
            valueScrollBack = false;
            txtViewValueScrollBack();
        } else {
            scrollCache.setVisibility(View.INVISIBLE);
            contentLay.postInvalidate();
            handler.removeMessages(HIDE_TXTCACHE);
        }
    }

    private void txtViewValueScrollBack() {
/*		txtView.setText(txtCache.getText());
//		A.setViewBottom(txtView, txtCache.getHeight() + A.getPageHeight());
		txtScrollTo(scrollCache.getScrollY());*/
        pageScroll(-pageDirection, true, false);
        showReadProgress(pageDirection == PAGE_DOWN ? 1 : -1);
        handler.sendEmptyMessageDelayed(HIDE_TXTCACHE, 50);
    }

    private boolean ignorePdfCurl(MotionEvent event) {
        return isPdf() && (!A.pdf_scoll_lock || event.getPointerCount() > 1);
    }

    public float getTouchX(MotionEvent event) {
        return event.getX() + additionalLeft();
    }

    private Message createCurlMsg(int what, MotionEvent event) {
        Message m = new Message();
        m.what = what;
        if (event != null) {
            m.arg1 = (int) getTouchX(event);
            m.arg2 = (int) event.getY();
        }
        return m;
    }

    private int getCurlClickDirection(MotionEvent event) {
        int action;
        switch (getClickArea(event)) {
            case A.CLICK_TOP:
                action = A.doTapScreenTop;
                break;
            case A.CLICK_BOTTOM:
                action = A.doTapScreenBottom;
                break;
            case A.CLICK_LEFT:
                action = A.doTapScreenLeft;
                break;
            case A.CLICK_RIGHT:
                action = A.doTapScreenRight;
                break;
            default:
                return PAGE_DOWN;
        }
        if (action == A.DO_NONE)
            return 0;
        else
            return action == A.DO_PAGE_UP ? PAGE_UP : PAGE_DOWN;
    }

    ////------------------------------------

    private Handler flip_handler;

    private Handler get_flip_handler() {
        if (flip_handler == null)
            flip_handler = new Handler() {
                public void handleMessage(Message msg) {
                    try {
                        showPageAnimation(msg.what == PAGE_DOWN);
                        if (!forceDelayFlipAnimation || A.hori_fling_animation < 3)
                            tmpHorizaontalScroll = false;
                        showSeekBarProgress();
                        showReadProgress(A.isFlipCurl() || isValueFlip() ? 0 : msg.what == PAGE_DOWN ? 1 : -1);
                        if (A.isFlipNone() && !showStatusbar())
                            handler.sendEmptyMessageDelayed(SHOW_SCROLLBAR, 50);
                    } catch (Exception e) {
                        A.error(e);
                    }
                }
            };
        return flip_handler;
    }

    ;

    public long flippingAnimationTime;

    private boolean flippingAnimation() {
        if (flippingAnimationTime > 0) {
            if (SystemClock.elapsedRealtime() - flippingAnimationTime > 2000)
                flippingAnimationTime = 0;
            return true;
        }
        return false;
    }

    protected boolean showPageAnimation(boolean isPageDown) {
        if (keyDownCount > 1)
            return false;
        if (flippingAnimation())
            return false;
        if (forceDelayFlipAnimation && flipView.getVisibility() == View.VISIBLE) {
            handler.removeMessages(DELAY_FLIP_CLICK);
            handler.sendEmptyMessageDelayed(DELAY_FLIP_CLICK, 10);
            return false;
        }

        if (valueCacheOk)
            return false;
        if (inPreShow)
            return false;
        if (flipView.bm1 == null)
            return false;

        if (forceTiltSpeed == 0) {
            boolean go = true;
            if (!tmpHorizaontalScroll) {
                if (A.isInAutoScroll || A.isSpeaking || A.flip_animation < 3)
                    go = false;
            } else if ((flipView.getVisibility() != View.VISIBLE && txtLayS.getVisibility() != View.VISIBLE)
                    || (A.flip_animation < 3 && A.hori_fling_animation < 3)) {
                go = false;
            }
            if (!go) {
                setFlipViewINVISIBLE(true);
                return false;
            }
        }

//		showReadProgress(pageDirection == PAGE_DOWN ? 1 : -1);
        int hori_fling_animation = A.hori_fling_animation;
        if (isPdf() && A.pdf_scoll_lock && Global.def_view == 3 && hori_fling_animation == A.FLIP_VERTICAL)
            hori_fling_animation = A.FLIP_HORIZONTAL;
        int flip_type = tmpHorizaontalScroll && A.isFlipNone() ? hori_fling_animation : A.flip_animation;
        if (flip_type == A.FLIP_NONE && forceTiltSpeed != 0)
            flip_type = A.FLIP_VERTICAL;
        if (flip_type < 3) {
            setFlipViewINVISIBLE(true);
            return false;
        }

        tmpHorizaontalScroll = false;
        flippingAnimationTime = SystemClock.elapsedRealtime();
        contentLay.setAnimationState(true);
        setTiltPaused(true);
        flipView.setTag(txtScroll.getScrollY());
        lastFlipScrollY = getFlipScrollY();
        showReadProgress(0);
        tmpFlipShot2 = getPageShot(true, true);
        flipView.setBitmap2(tmpFlipShot2);

        flipView.startAnimation(isPageDown, flip_type, new A.AfterFlipCurl() {
            public void Finished(boolean success) {
//				A.saveMemoryLog("clearShots(1):");
                scrollBlock.setVisibility(View.GONE);
                setTiltPaused(false);
                forceTiltSpeed = 0;
                flippingAnimationTime = 0;
                if (!updateForFitHardwareAccelerate())
                    contentLay.postInvalidate();
//				A.saveMemoryLog("clearShots(2):");
            }
        });

        return true;
    }

    protected void setTiltPaused(boolean paused) {
        if (shakeSensorLisener != null)
            shakeSensorLisener.tilt_paused = paused;
    }

    protected void showBookByPercent(int progress) {
        try {
//			A.moveStart = false;
            switch (getBookType()) {
                case A.FILE_TXT:
                    showTxtByPosition((A.txtLength() * progress / 1000), null);
                    break;
                case A.FILE_HTML:
                    if (A.noSplitHtmls())
                        txtScrollTo(txtView.getRealHeight() * progress / 1000);
                    else {
                        int l = htmlText.length();
                        int p = (int) ((long) l * (long) progress / 1000);
                        int i = p / A.maxHtmlChapterSize;
                        if (i < A.splitHtmls.size() && A.splitHtmls.size() > 0 && i >= 0) {
                            A.lastSplitIndex = i;
                            htmlSrc = A.splitHtmls.get(i);
                            htmlSrc = A.chineseJianFanConvert(htmlSrc);
                            txtViewSetText(MyHtml.fromHtml(htmlSrc, createHtmlBookImageGetter(), -1));
                            int y = p % A.maxHtmlChapterSize;
                            txtScrollHandler.sendMessageDelayed(txtScrollHandler.obtainMessage(2, y), 100);
                        }
                    }
                    break;
                case A.FILE_EBOOK:
                    if (isWebBook()) {
                        A.lastChapter = A.ebook.getChapters().size() * progress / 1000;
                        if (A.lastChapter > A.ebook.getChapters().size() - 1)
                            A.lastChapter = A.ebook.getChapters().size() - 1;
                        showEBookByPosition(A.lastChapter, 0, 0, true);
                    } else {
                        long p = getEBookTotalSize() * (progress - 1) / 1000;
                        long l = 0;
                        for (int i = 0; i < A.ebook.getChapters().size(); i++) {
                            int pure_len = A.ebook.getChapters().get(i).pure_text_length;
                            long size = pure_len > 0 ? pure_len : A.ebook.getChapters().get(i).size;
                            if (size <= 0)
                                size = 1;
                            l += size;
                            if (l > p || i == A.ebook.getChapters().size() - 1) {
                                long tp = size - (l - p);
                                //把字符数估算到文件大小的相应位置
                                if (A.ebook.isHtml() && pure_len < 0)
                                    tp = T.html2Text(A.ebook.getChapterText(i)).length() * tp / size;
                                if (progress == 1000)
                                    A.lastPosition = l;
                                else
                                    A.lastPosition = tp % A.maxHtmlChapterSize;
                                if (A.lastChapter != i) {
                                    ebook_inLoading = true;
                                    A.lastChapter = i;
                                    A.lastSplitIndex = tp < A.maxHtmlChapterSize ? 0 : (int) (tp / A.maxHtmlChapterSize);
                                    if (isBigChapter(i))
                                        createProgressDlg(A.ebook.getChapters().get(i).name);
                                    reloadBook();
                                } else {
                                    int iSplit = getSplitIndex(tp);
                                    boolean reload = !A.noSplitHtmls() || (iSplit != A.lastSplitIndex);
                                    if (reload) {
                                        A.lastSplitIndex = iSplit;
                                        createProgressDlg(A.ebook.getChapters().get(i).name + " (" + (iSplit + 1)
                                                + (!A.noSplitHtmls() ? "/" + A.splitHtmls.size() : "") + ")");
                                        reloadBook();
                                    } else
                                        showEBookByPosition(A.lastChapter, iSplit, A.lastPosition, reload);
                                }
                                return;
                            }
                        }
                    }
                    break;
            }

        } catch (Exception e) {
            A.error(e);
        }
        ebook_inLoading = false;
    }

    private int getSplitIndex(long tp) {
        int count = 0;
        for (int i = 0; i < A.splitHtmls.size(); i++) {
            count += A.splitHtmls.get(i).length();
            if (count >= tp)
                return i;
        }
        return 0;
    }

    private MyImageGetter createHtmlBookImageGetter() {
        imageGetter = new MyImageGetter() {
            public Drawable getDrawable(String source, boolean original) {
                String imageFile = T.getFilePath(A.lastFile) + "/" + source;
                if (T.isFile(imageFile))
                    try {
                        Drawable d = A.getFileDrawable(new File(imageFile), A.NORMAL_SIZE, A.LOW_QUALITY);
                        int w1 = d.getIntrinsicWidth();
                        int w2 = txtView.getWidth();
                        int h1 = d.getIntrinsicHeight();
                        if (w1 > w2 / 2)
                            d.setBounds(w2 > w1 ? (w2 - w1) / 2 : 0, 0, w2 > w1 ? (w2 - w1) / 2 + w1 : w2, w1 > w2 ? h1 * w2 / w1 : h1);
                        else
                            d.setBounds(0, 0, w1, h1);
                        return A.getDisplayDrawable(d);
                    } catch (OutOfMemoryError e) {
                        A.error(e);
                        System.gc();
                    } catch (Exception e) {
                        A.error(e);
                    }
                return null;
            }

            public Rect getDrawableBounds(String source, boolean original) {
                Drawable d = getDrawable(source, false);
                return d != null ? d.getBounds() : null;
            }
        };
        return imageGetter;
    }

    private boolean ebookPageUp = false, ebookChapterDown = false;
    private MyImageGetter imageGetter;
    public String htmlSrc;

    protected void showEBookByPosition(int lastChapter, int lastSplitIndex, long lastPosition, boolean reLoad) {
        try {
            if (A.ebook.getChapters().size() == 0)
                return;

            ebook_inLoading = true;
            pausePixelAutoScroll();
            if (ebookChapterDown) {
                ebookChapterDown = false;
                ebookPageUp = false;
                lastPosition = 0;
            }

            if (lastChapter < 0)
                lastChapter = 0;
            if (lastChapter > A.ebook.getChapters().size() - 1)
                lastChapter = A.ebook.getChapters().size() - 1;
            A.lastChapter = lastChapter;
            A.lastSplitIndex = lastSplitIndex;
            A.lastPosition = lastPosition;

            BaseEBook.Chapter chapter = A.ebook.getChapters().get(A.lastChapter);
            int len = A.ebook.getChapterText(A.lastChapter).length();
            if (A.lastPosition > len && len > A.maxHtmlChapterSize)
                A.lastSplitIndex = len / A.maxHtmlChapterSize;

            if (inWebReading) {
                showWebReading();
                showTipForYouHuaReading();
                return;
            }

            if (reLoad) {
                if (A.ebook.isHtml() && A.useWebView) {
                    generateChapterText(0); //get htmlScr
                    showInWebView(htmlSrc);
                    return;
                }
                if (hasNextChapterText(false, null))
                    return;
                clearTxtView();
                generateChapterText(0);
            }

            if (!isOnlineDownloadTag(A.lastChapter, txtView.getText2(), true)) {
                if (ebookPageUp) {
                    ebookPageUp = false;
                    disableTxtViewDraw();
                    txtScrollHandler.sendEmptyMessageDelayed(1, 50);
                } else
                    goToEBookLastPosition();
            }

        } catch (Throwable e) {
            A.error(e);
        } finally {
            changeChapterTime = SystemClock.elapsedRealtime();
            updateProgressStatus();
            hideProgressDlg();
            hide_ebook_cover();
//			if (!isCoverShow && flipView.getVisibility() == View.VISIBLE)
//				flipView.doFinishAnimation();
            resetFlipCache();
            ;
//			ebook_inLoading = false;
//			System.gc();
        }
    }

    private void generateChapterText(int type) {
//		long t = SystemClock.elapsedRealtime();
        ArrayList<String> htmls = new ArrayList<String>();
        int id = type == 1 ? preNextChapterId : type == -1 ? oldPriorChapterId : A.lastChapter;
        int splitIndex = type == 1 ? preNextChapterSplit : type == -1 ? oldPriorChapterSplit : A.lastSplitIndex;

        String html = A.ebook.getChapterText(id);
        if (A.chapterEndPrompt && id == A.ebook.getChapters().size() - 1)
            try {
                html += (A.ebook.isHtml() ? "<br><br><center>" + A.theEndText() + "</center><br><br>" : "\n\n" + A.theEndText() + "\n\n");
            } catch (OutOfMemoryError e) {
                A.error(e);
            }

        if (A.ebook.isHtml()) {
            if (!A.useWebView)
                html = A.adjustChapterHtml(html, htmls);
            if (htmls.size() > 0) {
                if ((type == -1 && oldPriorChapterSplit == -1) || ebookPageUp || splitIndex >= htmls.size())
                    splitIndex = htmls.size() - 1;
                html = htmls.get(splitIndex);
            } else
                splitIndex = 0;

            html = A.ebook.dealSplitHtml(id, splitIndex, htmls.size(), html);
            html = A.chineseJianFanConvert(html);
            imageGetter = A.ebook.getMyImageGetter();
            if (type == 1) {
                savePreNextChapterText(id, MyHtml.fromHtml(html, imageGetter, id), true);
            } else if (type == -1) {
                oldPriorChapterText = MyHtml.fromHtml(html, imageGetter, id);
            } else if (!A.useWebView) {
                htmlSrc = html;
                txtViewSetText(MyHtml.fromHtml(html, imageGetter, id));
                if (locate_to_search_result)
                    A.lastPosition = getSearchResultLocation();
            }

        } else {
            html = A.adjustChapterHtml(html, htmls);
            if (htmls.size() > 0) {
                if ((type == -1 && oldPriorChapterSplit == -1) || ebookPageUp || splitIndex >= htmls.size())
                    splitIndex = htmls.size() - 1;
                html = htmls.get(splitIndex);
            } else
                splitIndex = 0;
            html = A.chineseJianFanConvert(html);
            if (type == 1) {
                savePreNextChapterText(id, html, true);
            } else if (type == -1) {
                oldPriorChapterText = html;
            } else if (!A.useWebView) {
                txtViewSetText(html);
            }
        }

        if (type == 1) {
            preNextHtmlSrc = html;
            preNextChapterId = id;
            preNextChapterSplit = splitIndex;
            preNextSplitHtmls = htmls;
        } else if (type == -1) {
            oldPriorHtmlSrc = html;
            oldPriorChapterId = id;
            oldPriorChapterSplit = splitIndex;
            oldPriorSplitHtmls = htmls;
        } else {
            htmlSrc = html;
            A.lastChapter = id;
            A.lastSplitIndex = splitIndex;
            A.splitHtmls = htmls;
        }
//		A.log("----------------@chapter load time:" + (SystemClock.elapsedRealtime() - t));
    }

    private void pausePixelAutoScroll() {
        if (A.isInAutoScroll && (getScrollMode() == A.SCROLL_PIXEL || getScrollMode() == A.SCROLL_LINE)) {
            do_AutoScroll(false, false);
            A.isInAutoScroll = true;
        }
    }

    private int getScrollMode() {
        return isPdf() ? A.SCROLL_ROLLING_PIXEL : A.autoScrollMode;
    }

    private boolean isRollingScroll() {
        return getScrollMode() == A.SCROLL_ROLLING_PIXEL || getScrollMode() == A.SCROLL_ROLLING_LINE;
    }

    private long delayTime() {
        return 50; //todo: if less than 50, will cause verifiyBottomFixed() error, why?
//		if (Build.VERSION.SDK_INT < 21)
//			return 50;
////		if (dualPageEnabled())
////			return 50;
//		if (A.screenRealWidth != null && A.screenRealHeight != null)
//			if (baseFrame.getWidth() < A.screenRealWidth || baseFrame.getWidth() < A.screenRealHeight)
//				return 50;
//		return 10;
    }

    private void goToEBookLastPosition() {
        ebook_inLoading = true;
        disableTxtViewDraw();

//		A.log("------1*" + txtView.getWidth() + " " + txtView2.getWidth() + " tv2/sv2-visible:"+txtView2.getVisibility()+"/"+txtScroll2.getVisibility());
        getAndroid22Handler().sendEmptyMessageDelayed(0, delayTime());
//		if (dualPageEnabled()) {
//			android22HandlerSendMessage(-1, false);
//		}else {
//			int y = txtView.getLineTop2(lo.getLineForOffset((int) A.lastPosition));
//			txtScrollTo(y);
//			android22HandlerSendMessage(y, false);
//		}
    }

    long goToLastTime;
    Handler android22Handle;

    Handler getAndroid22Handler() {
        if (android22Handle == null)
            android22Handle = new Handler() {
                @Override
                public void handleMessage(Message msg) {
//					if (!A.isFlipNone())
//						A.setViewBottom(txtView, txtView.getHeight() + txtScroll.getHeight()*2);

                    if (txtView.getLayout() == null) {
                        fixBrokenTextView(true);
                        if (!isPaused)
                            handler.postDelayed(ActivityTxt.this, 200);
                        return;
                    }
                    if (txtView.layoutState == 1) { //almost useless (1)
                        A.log("**************SoftHyphenStaticLayout tWorking, delay 100************");
                        getAndroid22Handler().sendEmptyMessageDelayed(0, 100);
                        return;
                    }
                    if (dualPageEnabled()) { //almost useless (2)
                        if (txtView2.getWidth() == 0 || (txtView.getText().length() > 0 && txtView.getHeight() == 0)) {
                            A.log("********error txtView.getHeight():" + txtView.getHeight());
                            fixBrokenTextView(true);
                            if (!isPaused)
                                getAndroid22Handler().sendEmptyMessageDelayed(0, 200);
                            return;
                        }
                        fixBottomForScrollTo(txtView, txtView.getRealHeight());
                    }

//					A.log("------2*" + txtView.getWidth() + " " + txtView2.getWidth() + ", pos: "+A.lastPosition);
                    int y = txtView.getLineTop2(txtView.getLayout().getLineForOffset((int) A.lastPosition));
                    goToLastTime = SystemClock.elapsedRealtime();

                    txtScrollTo(y);
                    enableTxtViewDraw();
                    ebook_inLoading = false;
                    forceDelayFlipAnimation = false;

                    handler.sendEmptyMessageDelayed(PRE_LOAD_NEXT_CHAPTER, justOpenBookAnim() ? 1000 : 100);
                    createCachePageShotsHandler(justOpenBookAnim() ? 1300 : 300);

                    continueSpeakOrAutoScroll();
                    forceUpdateForFitHardwareAccelerate(50);
                }
            };

        return android22Handle;
    }

    private boolean justOpenBookAnim() {
        return A.openBookAnim && System.currentTimeMillis() - onCreateTime < 3000;
    }

    Handler pixelScrollHandler;

    private void continueSpeakOrAutoScroll() {
        if (A.isSpeaking)
            speakCurrentPage(-100);

        if (A.isInAutoScroll && (getScrollMode() == A.SCROLL_ROLLING_PIXEL || getScrollMode() == A.SCROLL_ROLLING_LINE)) {
            if (scrollImage != null) {
                scrollImage.setPage1Bm(getPageShot(true, true));
                scrollImage.invalidate();
            }
        }

        if (A.isInAutoScroll && (getScrollMode() == A.SCROLL_PIXEL || getScrollMode() == A.SCROLL_LINE)) {
            if (A.autoScrollSpeed > 10) {
                T.showToastText(ActivityTxt.this, "3", 0);
                if (pixelScrollHandler == null)
                    pixelScrollHandler = new Handler() {
                        public void handleMessage(Message msg) {
                            if (A.isInAutoScroll) {
                                if (msg.what == 3) {
                                    T.hideToast();
                                    do_AutoScroll(true, false);
                                } else
                                    T.showToastText(ActivityTxt.this, "" + (3 - msg.what));
                            }
                        }
                    };
                pixelScrollHandler.removeCallbacksAndMessages(null);
                pixelScrollHandler.sendEmptyMessageDelayed(1, 1000);
                pixelScrollHandler.sendEmptyMessageDelayed(2, 2000);
                pixelScrollHandler.sendEmptyMessageDelayed(3, 3000);
            } else
                do_AutoScroll(true, false);
        }
    }

    protected void showTxtByPosition(long position, String veri) {
        if (getBookType() != A.FILE_TXT)
            return;
        int p = (int) A.getTxtDisplayPos(position, true);
        try {
            if ((A.getTxts().size() > 0) && (A.lastBlockIndex >= 0) && (A.lastBlockIndex < A.getTxts().size())) {
                String text = A.getBlocksText(A.lastBlockIndex);
                text = A.chineseJianFanConvert(text);

                if (inPreShow) {
                    inPreShow = false;
                    if (text.equals(txtView.getText().toString()))
                        return;
                    saveLastPostion(false);
                    position = A.lastPosition;
                }
                txtViewSetText(text);

                if (veri != null && text.length() > p + veri.length()) { //todo: fix unknown position error that caused p != p in pageScroll()
                    String cur = text.substring(p, p + veri.length());
                    if (!cur.equals(veri)) {
                        A.log("showTxtByPosition veri error, cur [" + cur + "], should be [" + veri + "]");
                        int i1 = text.lastIndexOf(veri, p);
                        if (i1 != -1 && p - i1 > 200) i1 = -1;
                        int i2 = text.indexOf(veri, p);
                        if (i2 - p > 200) i2 = -1;
                        if (i1 != -1 && i2 != -1) {
                            p = p - i1 < i2 - p ? i1 : i2;
                        } else {
                            if (i1 != -1) p = i1;
                            else if (i2 != -1) p = i2;
                        }
                    }
                }

                if ((p > text.length() - 1))
                    p = text.length() - 1;

                MyLayout layout = txtView.getLayout();
                if (isTxtScrollReady && layout != null) { //isTxtScrollReady isn't ready in android2.2, first time run to here
                    txtScrollNoDelay(p);
                } else {
                    txtFileFinalScrollAtStartup(p);
                }
            }
        } catch (Exception e) {
            A.error(e);
        }
    }

    private void txtFileFinalScrollAtStartup(long p) {
//		A.log("---txtFileFinalScrollAtStartup 1, lineCount:" + txtView.getLineCount() + " tvH: "+txtView.getHeight()+" txtLen:"+txtView.getText().length());
        if (p > 0)
            txtView.disableDraw = txtView2.disableDraw = true;
        checkStatusBar();
        new Handler() {
            public void handleMessage(Message msg) {
//				A.log("---txtFileFinalScrollAtStartup 2: " + txtView.getLineCount() + " tvH: "+txtView.getHeight());
                handler.sendMessageDelayed(handler.obtainMessage(SCROLL_TXT_AGAIN, msg.what, 0),
                        fixBrokenTextView(false) ? 50 : delayTime());
            }
        }.sendEmptyMessageDelayed((int) p, fixBrokenTextView(false) ? 50 : 0);
    }

    private boolean fixBrokenTextView(boolean forceReload) {
        boolean broken = txtViewTextBroken();
        if (broken || forceReload) {
            A.log("************txtView broken, lineCount:" + txtView.getLineCount() + " tvH: " + txtView.getHeight() + " txtLen:" + txtView.getText().length() + ", lastPosition:" + A.lastPosition);
            CharSequence text = txtView.getText();
            clearTxtView();
            txtViewSetText(text);
        }
        return broken;
    }

    private boolean txtViewTextBroken() {
        if (txtView.getText() == null)
            return false;
        if (txtView.getText().length() > 0 && txtView.getLineCount() == 0)
            return true;
        if (txtView.getText().length() > 0 && txtView.getLineCount() == 1) {
            int i = txtView.getText2().indexOf("\n");
            if (i != -1) {
                i = txtView.getText2().indexOf("\n", i + 1);
                if (i != -1) {
                    A.log("#############txtView deep broken, txtView.getLineCount() == 1 only############" + ", lastPosition:" + A.lastPosition);
                    return true;
                }
            }
        }
        return false;
    }

    private int seekBarStartPos;
    OnSeekBarChangeListener seekBarChanged = new OnSeekBarChangeListener() {
        public void onStopTrackingTouch(SeekBar seekBar) {
            T.hideToast();
            nav_return.setVisibility(View.VISIBLE);
            if (getBookType() == A.FILE_EBOOK && !ebook_inLoading &&
                    (seekBarStartPos != getSeekBarProgress(seekBar)
                            || (getSeekBarProgress(seekBar) == 0 && A.lastPosition != 0))) {
                ebook_inLoading = true;
                showBookByPercent(getSeekBarProgress(seekBar));
                hideTopLay();
            }
            updateBarIcons();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            if (nav_return.getTag() == null) {
                nav_return.setTag(1);
                saveLinkBackInfo(true);
            }
            seekBarStartPos = getSeekBarProgress(seekBar);
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (isPdf()) {
                if (fromUser)
                    goToPdfPage(progress, true);//!A.pdf_dual_page || !A.isLandscape());
                return;
            }

            if (progress < 0)
                progress = 0;
            if (progress > 1000)
                progress = 1000;
            if (layoutVisible) {
                float p = (float) progress * 100 / 1000;
                if (fromUser) {
                    if (isWebBook()) {
						/*//android.os.DeadObjectException: Failed to dispatch window animation state change.
						int i = A.ebook.getChapters().size() * progress / 1000;
						if (i > A.ebook.getChapters().size() - 1)
							i = A.ebook.getChapters().size() - 1;
						T.showToastText(ActivityTxt.this, A.ebook.getChapters().get(i).name, 0, Gravity.BOTTOM);*/
                    } else
                        percentView.setText(p + "%");
                } else
                    percentView.setText(getPercentStr2());
            }

            if (fromUser && getBookLength() > 0 && web == null && (getBookType() == A.FILE_TXT || getBookType() == A.FILE_HTML)) {
                showBookByPercent(progress);
                checkStatusBar();
                setFlipViewINVISIBLE(true);
                resetFlipCache();
            }

//			if (fromUser)
//				forceNotSaveZeroPosition = false;

            currentPage = -1;
        }

        private void goToPdfPage(int progress, boolean updatePage) {
            if (m_doc == null || m_doc == null)
                return;
            int p = pdfValidatePageNumber(progress);
            percentView.setText("" + (p + 1) + "/" + m_doc.GetPageCount());
            if (updatePage) {
                A.lastPosition = p;
                pdfGotoPage((int) A.lastPosition, true);
                updateProgressStatus();
            }
        }
    };

    private void adjustTxtViewText() { //需要时重新调整txtView内容以实现平滑滚动
        try {

            switch (getBookType()) {
                case A.FILE_TXT:
                    int y = txtScroll.getScrollY();
                    int h = txtView.getRealHeight();
                    int lh = txtView.getLineHeight();
                    int sh = A.getPageHeight();

                    if (A.isInAutoScroll && A.landscape2PageMode && A.isLandscape())
                        h = txtView.getLineTop(txtView.getLineCount()) + txtView.getLineHeight();

                    int offset = -1;
                    if ((y < lh / 2) && (A.lastBlockIndex > 1)) { //只剩半行,重新加载
                        offset = 0;
                    } else if ((sh + y > h - lh - lh / 2 - 1) && (A.lastBlockIndex < A.getTxts().size() - 2))
                        offset = 1; //z 最后一行半的位置开始重新加载(最后一行显示是不完整的,不能显示出来)
                    //z v1.1.5.3 改成两行半
                    if (offset == 0) { //move up
                        int i = (A.lastBlockIndex == 0) ? 0 : A.lastBlockIndex - 1;
                        A.lastPosition = A.getPriorTxtLength(i) + 5;
                        showTxtByPosition(A.lastPosition, null);
                    }

                    if (offset == 1) { //move down
                        if (txtView.getLayout() == null)
                            return;//v1.2.21
                        A.log("----load next txt block-----");
                        int i = A.lastBlockIndex;
                        int j = 0;
                        if (i == 0)
                            j++;
                        i += j;
                        String text = A.chineseJianFanConvert(A.getTxts2(i + 0) + A.getTxts2(i + 1) + A.getTxts2(i + 2));
                        txtViewSetText(text);
                        int line = txtView.getLayout().getLineForOffset(A.getTxts2(i + 0).length() + A.getTxts2(i + 1).length() - 1);
                        if (line - sh / lh - 1 > 0)
                            line = line - sh / lh - 1;
                        int ny = txtView.getLineTop2(line);
                        txtScrollTo(ny + 2);
                        A.lastBlockIndex = i + 1 + j;
                    }
                    break;

                case A.FILE_EBOOK:
                    break;

                case A.FILE_HTML:
                    break;
            }

        } catch (Exception e) {
            A.error(e);
        }
    }

    private float pressDownY = 0;
    public float pressDownX = 0;
    private boolean pressDown = false;
    private boolean hasMarginAction;
    public boolean longTimeTapEvent = false, footnoteLongTimeTapEvent = false;
    private boolean longTimeTapEventSent = false;
    public boolean longTimeTapEventDown = false;
    private int lastTouchAction;

    public boolean onTouch(View v, MotionEvent event) {
        userAction = true;
        recordUserActiveTime();
        lastTouchAction = event.getAction();
        if (lastTouchAction == MotionEvent.ACTION_UP) {
            setScreenAwakeTime = 0;
            checkScreenAwake();
        }
        if (A.isFlipNone() && flippingAnimation())
            return true;

        try {
            saveEvent(event);
            saveBrightMoveSpeed(event);
            A.touchingView = v;
            boolean webOrPdf = (web != null || inWebReading || isPdf());
            boolean isAutoState = isAutoState();
            if (stopTouchEvent(v, event))
                return true;
            if (!isAutoState && !webOrPdf && multiTouchForFontSize(event))
                return true;
            if (web == null && isHighlightEvent(v, event))
                return true;
            if (!isAutoState && web == null && !inWebReading && doOnCurlTouch(v, event)) {
                pdfAfterOnTouch(event);
                return true;
            }

            float xMove = Math.abs(getTouchX(event) - pressDownX);
            float yMove = Math.abs(event.getY() - pressDownY);
            switch (lastTouchAction) {
                case MotionEvent.ACTION_MOVE:
                    if (autoScrollPaused && !pressDown_move_for_speed)
                        return false;
                    if (!inPreShow && SystemClock.elapsedRealtime() - yFlingTime > 500) { //touch on screen, onTouch>3?
                        if (A.adjustBrightness) //tap-move屏幕左边缘调整亮度
                            if (do_AdjustBrightness_Event(event))
                                return true;
                        if (A.adjustFontSizeAtSlide) //tap-move屏幕左边缘调整font
                            if (do_AdjustFontSize_Event(event))
                                return true;
                        if (!layoutVisible && mTouchTimes > 10) //显示滚动位置点
                            showScrollbarProgress(true);
                    }
                    if (pagingByMove)
                        return true;
                    if (!isAutoState && !webOrPdf && isShiftGesture(xMove, yMove, event))
                        return true;

                    checkMovingToChangeChapter(v, event, webOrPdf, isAutoState, xMove, yMove);
                    break;

                case MotionEvent.ACTION_UP:
                    if ((v instanceof ScrollView) && do_Gesture_Event(event))
                        break;
                    if (dotVisible())
                        break;

                    boolean movingChangedChapter = false;
                    xFlingTime = 0;
                    if (mTouchTimes <= 20 && yMove < A.d(10) && xMove < A.d(10) && SystemClock.elapsedRealtime() - yFlingTime > 500) { //while start scroll in a second
                        int ignoreTime = web != null ? 200 : A.ONTOUCH_IGNORETIME;
                        if (pressTime == -1 || SystemClock.elapsedRealtime() - pressTime < ignoreTime) {//long-tap time
                            xFlingTime = SystemClock.elapsedRealtime(); //v1.3.9
                            do_TapUp_Event(event);
                            if (web != null)
                                web.clickTime = SystemClock.elapsedRealtime();
                        }
                    } else if (preparedMovingChangeChapter) {
                        tmpHorizaontalScroll = true;
                        movingChangedChapter = true;
                        pageScroll(noScrollDist > 0 ? PAGE_UP : PAGE_DOWN);
                        A.moveStart = true;
                    }
                    yFlingTime = 0;
                    mTouchTimes = 0;
                    priorMovingY = priorMovingEventY = movingStopEventY = -1;
                    showMovingChangeChapterState(false);
                    if (movingChangedChapter)
                        return true;
                    break;
            }

            if (!isAutoState && !webOrPdf) {
                initFling();
                if (mGestureDetector.onTouchEvent(event))
                    return true;
                else if (lastTouchAction == MotionEvent.ACTION_MOVE) {
                    if (isDisableMove())
                        return true;
                    if (isDisableMovedFlips() && !isMiddleTap(event))
                        return true;
                    if (!A.moveStart && xMove > yMove * 0.7) //swipe horizontal, don't vertical scroll
                        return true;
                    if (xFlingTime != 0) { //v1.3.9 ignore small tap movement
                        if (yMove < A.d(5))
                            return true;
                        xFlingTime = 0;
                    }
                    A.moveStart = true;
                    yBeforePageDown.clear();
                    if (yMove > A.d(5))
                        saveMoveReadingStatistics(event);
                }
            } else if (isPdf()) {
                if (A.pdf_scoll_lock) {
                    initFling();
                    if (mGestureDetector.onTouchEvent(event))
                        return true;
                }
                pdfAfterOnTouch(event);
            }

            if (lastTouchAction == MotionEvent.ACTION_UP && (v instanceof ScrollView) && (!layoutVisible))
                showScrollbarProgress(false);

        } catch (Throwable e) {//v1.9.4 Throwable includes all errors
            A.error(e);
            return true;
        }

        return false;
    }

    int moveReadingY;

    private void saveMoveReadingStatistics(MotionEvent event) {
        if (txtScroll.getScrollY() - moveReadingY >= A.getPageHeight()) {
            moveReadingY = txtScroll.getScrollY();
            statistics_add();
        }
    }

    private float priorMovingY, priorMovingEventY, movingStopEventY, noScrollDist;
    private boolean preparedMovingChangeChapter;

    private void checkMovingToChangeChapter(View v, MotionEvent event, boolean webOrPdf, boolean isAutoState, float xMove, float yMove) {
        if (A.disableMove)
            return;
        boolean pdfReflow = isPdf() && pdf != null && pdf.textReflow;
        if (!pdfReflow)
            if (hasMarginAction || !A.isFlipNone() || isAutoState || !(v instanceof ScrollView) || web != null || dualPageEnabled()
                    || dotVisible() || (getBookType() != A.FILE_EBOOK && getBookType() != A.FILE_HTML)) {
                showMovingChangeChapterState(false);
                return;
            }

        int y = txtScroll.getScrollY(); //for moving up/down to change chapter
        if (priorMovingY == -1) {
            priorMovingY = y;
            priorMovingEventY = event.getY();
        } else if (wheelVisible || (yMove > xMove && Math.abs(event.getY() - priorMovingEventY) > A.d(2))) {
            priorMovingEventY = event.getY();
            if (wheelVisible || priorMovingY == y) {
                if (movingStopEventY == -1) {
                    movingStopEventY = event.getY();
                } else {
                    float dist = (event.getY() - movingStopEventY);
                    if (Math.abs(dist) > A.d(61))
                        movingStopEventY = event.getY() - A.d(dist > 0 ? 61 : -61);
                    if ((dist > 0 && noScrollDist < 0) || (dist < 0 && noScrollDist > 0)) {
                        showMovingChangeChapterState(false);
                        movingStopEventY = -1;
                        noScrollDist = 0;
                    } else
                        noScrollDist = dist;
                }
            } else {
                priorMovingY = y;
                movingStopEventY = -1;
                noScrollDist = 0;
            }
        }

        if (wheelVisible || (noScrollDist > A.d(8) && txtScroll.getScrollY() < A.d(2) ||
                (noScrollDist < -A.d(8) && txtScroll.getScrollY() > txtView.getHeight() - A.getPageHeight() - A.d(2))))
            showMovingChangeChapterState(true);
    }

    //	ProgressWheel wheel;
    boolean wheelVisible;

    private void showMovingChangeChapterState(boolean visible) {
        if (visible && ((noScrollDist < 0 && isEndOfBook()) || (noScrollDist > 0 && isBeginOfBook())))
            visible = false;

        preparedMovingChangeChapter = visible;
        if (visible) {
//			if (wheel == null) {
//				wheel = new ProgressWheel(this);
//				baseFrame.addView(wheel);
//				wheel.setCircleColor(0);
//				wheel.setContourColor(0);
//				wheel.setRimColor(0);
//				wheel.startSpinning();
//			}
//			wheel.setBarColor(A.getAlphaColor(A.fontColor, -150));
//			FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) wheel.getLayoutParams();
//			fl.width = fl.height = A.d(40);
//			fl.gravity = Gravity.CENTER_HORIZONTAL;
//			fl.topMargin = noScrollDist>0? A.d(10) : baseFrame.getHeight() - fl.height - (showStatusbar() ? statusLay.getHeight() : 0) - A.d(10);

            int l = (int) Math.abs(noScrollDist * 6 / A.getDensity());
            preparedMovingChangeChapter = l >= 360;

//			wheel.setBarLength(l>360? 360 : l);
//			wheel.barPaint2.setColor(A.isNightState()? 0xffffffff: 0xff000000);
//			wheel.bringToFront();
//			wheel.setVisibility(View.VISIBLE);
            wheelVisible = true;
            if (preparedMovingChangeChapter && noScrollDist > 0)
                prePriorChapter();
        } else {
            wheelVisible = false;
//			if (wheel != null && wheel.getVisibility() == View.VISIBLE)
//				wheel.setVisibility(View.GONE);
        }
    }

    private void saveEvent(MotionEvent event) {
        if (hMotionEvent != null)
            hMotionEvent.recycle();
        hMotionEvent = MotionEvent.obtain(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (hDownEvent != null)
                hDownEvent.recycle();
            hDownEvent = MotionEvent.obtain(event);
        }
    }

    private boolean isShiftGesture(float xMove, float yMove, MotionEvent event) {
        if (dotVisible() || SystemClock.elapsedRealtime() - pageScrollTime < 500)
            return false;
        if (A.isFlipHorizShift() && xMove >= yMove && xMove > A.d(20) && yMove < A.d(40)) {
            pagingByMove = true;
            pageScroll(getTouchX(event) - pressDownX < 0 ? PAGE_DOWN : PAGE_UP);
            return true;
        }
        if (A.isFlipVerticalShift()) {
            if (isMiddleTap(hDownEvent))
                return false;
            if (xMove <= yMove && yMove > A.d(20) && xMove < A.d(40)) {
                pagingByMove = true;
                pageScroll(event.getY() - pressDownY < 0 ? PAGE_DOWN : PAGE_UP);
            }
            return true;
        }
        return false;
    }

    private boolean isMiddleTap(MotionEvent event) {
        if (event == null)
            event = hMotionEvent;
        int w = baseFrame.getWidth();
        int h = baseFrame.getHeight();
        float x = getTouchX(event);
        float y = event.getY();
        return (x > w / 3 && x < w * 2 / 3 && y > h / 3 && y < h * 2 / 3)
                && (pressDownX > w / 3 && pressDownX < w * 2 / 3 && pressDownY > h / 3 && pressDownY < h * 2 / 3);
    }

    private boolean isDisableMovedFlips() {
        return A.flip_animation == A.FLIP_HORIZONTAL || A.flip_animation == A.FLIP_SHIFT_HORIZONTAL;
    }

    private boolean isHighlightEvent(View v, MotionEvent event) {
        if ((v == pdf || v instanceof ScrollView) && A.doLongTap != A.DO_NONE)
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!longTimeTapEventSent && !longTimeTapEventDown) {
                        longTimeTapEvent = true;
                        longTimeTapEventDown = true;
                        int delay = (int) (1000 * A.longTapInterval);//A.LONG_TAP_TIME;
                        if (isTouchInEdge(event))//边沿长按时间加倍
                            delay = delay * 15 / 10;
                        handler.sendEmptyMessageDelayed(LONG_TIME_TAP_EVENT, delay);
                        longTimeTapEventSent = true;
                    }
                    if (hideDotViews()) {
                        pressDown = false;
                        return true;
                    }
                    pressDown = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (stopLongTap(event)) {
                        longTimeTapEvent = false;
                        hideDotViews();
                    }
                    if (!pressDown)
                        return true;
                    break;
                case MotionEvent.ACTION_UP:
                    longTimeTapEvent = false;
                    longTimeTapEventDown = false;
                    if (!pressDown) {
                        xFlingTime = SystemClock.elapsedRealtime(); //v1.3.11
                        return true;
                    }
                    pressDown = false;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    A.log("MotionEvent.ACTION_CANCEL");
                    longTimeTapEvent = false;
                    break;
            }
        return false;
    }

    private boolean stopLongTap(MotionEvent event) {
        if (flippingAnimationTime > 0)
            return true;
        return (isPdf() && event.getPointerCount() > 1) || (Math.abs(event.getY() - pressDownY) > A.d(10))
                || (Math.abs(getTouchX(event) - pressDownX) > A.d(10));
    }

    public boolean dragToSelectText, moveStart2;

    private boolean stopTouchEvent(View v, MotionEvent event) {
        if (isPdf()) {
            if (pdfAnnotStart || SystemClock.elapsedRealtime() - annotEndTime < 500)
                return true;
        }
        if (isPdf() && PDFReader.innerLinkRecord != -1 && event.getAction() == MotionEvent.ACTION_UP) {
            PDFReader.innerLinkRecord = -1;
            return true;
        }

        if (A.isInAutoScroll || v == txtLayS) {
            auto_scroll_touch(event);
        }

        if (layoutVisible) {
            inverseLayoutVisible(true);
            return true;
        }
        if (A.isSpeaking) {
            if (tts_panel_visible) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showTtsPanel(false);
                    if (tts_paused)
                        restore_speak();
                }
                return true;
            } else
                speak_touch(event);
        }

//		if (hideSearchPanel())
//		    return true;

        if (bottomLay != null && bottomLay.getVisibility() == View.VISIBLE) {
            showBottomBar(-1);
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pagingByMove = false;
                setTiltPaused(true);
                moveStart2 = A.moveStart;
                A.moveStart = false;
                swipeGuesture = false;
                mTouchTimes = 0;
                continueMoveX = 0;
                pressTime = SystemClock.elapsedRealtime();
                pressDownX = getTouchX(event);
                pressDownY = event.getY();
                priorMovingY = priorMovingEventY = movingStopEventY = -1;
                preparedMovingChangeChapter = false;
                birghtnessPos = -1;
                fontsizePos = -1;
                hasMarginAction = false;
                dragToSelectText = false;
                releaseMagnifier();
                break;
            case MotionEvent.ACTION_MOVE:
                if (autoScrollPaused && !pressDown_move_for_speed) {
                    if (isRollingScroll()) {
                        scrollPos = (int) event.getY();
                        scrollImage.topLine = scrollPos;
                        scrollImage.invalidate();
                    }
                    return isRollingScroll();
                }
                mTouchTimes++;
                if (A.pdfStatus == STATUS.sta_sel)
                    select_text_state = true;
                if (select_text_state) {
                    if (A.pdfStatus != STATUS.sta_sel) {
                        select_move_down(event.getY());
                        selectText(true);
                    }
                    if (Math.abs(getTouchX(event) - pressDownX) > A.d(24) || Math.abs(event.getY() - pressDownY) > A.d(16)) {
                        if (A.directHighlight)
                            dragToSelectText = true;
                    }
                    if (A.showMagnifier) {
                        contentLay.magnifierStart = true;
                        hBar.drawMagnifier(event);
                    }
                    return true;
                }
                if (isAutoState())
                    return true;
                if (!isDisableMove())
                    adjustTxtViewText();
                break;
            case MotionEvent.ACTION_UP:
                setTiltPaused(false);
                releaseMagnifier();
                select_text_state = false;
                if (dragToSelectText) {
                    dragToSelectText = false;
                    longTimeTapEventDown = false;
                    doHighlight2(null, -1, -1, true);
                    A.lastFileAnnotUpdated = true;
                    return true;
                }
                if (brightnessSetted) {
                    brightnessSetted = false;
                    setLeds();
                }
                break;
        }

        return false;
    }

    private void releaseMagnifier() {
        if (!T.isRecycled(hBar.bm))
            hBar.stopMagnifier();
        if (contentLay.magnifierStart) {
            contentLay.magnifierStart = false;
            contentLay.releaseCache();
            updateForFitHardwareAccelerate();
        }
    }

    long select_scroll_time;

    private void select_move_down(float y) {
//		if (dualPageEnabled() || SystemClock.elapsedRealtime() - select_scroll_time < 100)
        if (SystemClock.elapsedRealtime() - select_scroll_time < 100)
            return;

        select_scroll_time = SystemClock.elapsedRealtime();
        int lh = txtView.getLineHeight();
        if (A.getScreenHeight2() - y < lh * 1.5) {
            if (lastLineDisplayed())
                return;

            if (txtView.getHeight() - txtScroll.getScrollY() > lh) {
                txtScrollTo(txtScroll.getScrollY() + lh);
                showReadProgress(0);
                if (!select_text_state) {
                    View tmp = dot == dot1 ? dot2 : dot1;
                    tmp.layout(tmp.getLeft(), tmp.getTop() - lh, tmp.getRight(), tmp.getBottom() - lh);
                }
                resetFlipCache();
                ;
            }
        } else {
            if (y < lh) {
                if (txtScroll.getScrollY() > lh) {
                    txtScrollTo(txtScroll.getScrollY() - lh);
                    showReadProgress(0);
                    if (!select_text_state) {
                        View tmp = dot == dot1 ? dot2 : dot1;
                        tmp.layout(tmp.getLeft(), tmp.getTop() + lh, tmp.getRight(), tmp.getBottom() + lh);
                    }
                    resetFlipCache();
                    ;
                }
            }
        }
    }

    private int getRealLineCount() {
//		return txtView.getLineCount() - fixedLineCount;
        return txtView.getRealLineCount();
    }

    private int pressDown_scroll_value;
    private boolean pressDown_move_for_speed;

    private void auto_scroll_touch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                pressDownY = event.getY();
                pressDownX = event.getX();
                pressDown_scroll_value = A.autoScrollSpeed;
                pressDown_move_for_speed = event.getPointerCount() < 2;
                if (!pressDown_move_for_speed)
                    autoScrollPaused = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (dotVisible())
                    return;
                if (pressDown_move_for_speed)
                    if (event.getPointerCount() >= 2) {//one finger for volume, two for speed
                        pressDown_move_for_speed = false;
                    }
                float moveX = Math.abs(event.getX() - pressDownX);
                float moveY = Math.abs(event.getY() - pressDownY);
                if (moveY > moveX) {
                    int step = (int) (event.getY() - pressDownY) / A.d(20);
                    if (pressDown_move_for_speed) {
                        if (step != 0 || pressDown_scroll_value != A.autoScrollSpeed) {
                            A.autoScrollSpeed = pressDown_scroll_value + step;
                            if (A.autoScrollSpeed < 0)
                                A.autoScrollSpeed = 0;
                            if (A.autoScrollSpeed > 100)
                                A.autoScrollSpeed = 100;
                            T.showToastText(this, getString(R.string.scroll_speed) + " " + (100 - A.autoScrollSpeed) + "%");
                            resetPixelScrollSpeed();
                        }
                    } else {
                        autoScrollPaused = true;
                    }
                } else if (moveX > A.d(80) && pressDown_move_for_speed) {
                    do_AutoScroll(false);
                    pressDown = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!pressDown_move_for_speed)
                    autoScrollPaused = false;
                break;
        }
    }

    private int pressDown_speak_value;
    private boolean pressDown_move_for_volume;
    private int pressDown_speed_speed;

    private void speak_touch(MotionEvent event) {
        if (tts_volume == null)
            return;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                pressDownY = event.getY();
                pressDownX = event.getX();
                pressDown_speak_value = tts_volume.getProgress();
                forceSpeakPos = -1;
                pressDown_move_for_volume = true;
                A.log("pressDown_speak_volume: " + pressDown_speak_value);
                break;
            case MotionEvent.ACTION_MOVE:
                if (dotVisible())
                    return;
                if (pressDown_move_for_volume && forceSpeakPos == -1)
                    if (event.getPointerCount() >= 2) {//one finger for volume, two for speed
                        pressDown_move_for_volume = false;
                        pressDown_speak_value = A.tts_speed; //tts_speed.getProgress();
                        pressDown_speed_speed = A.tts_speed;
                    }
                float moveX = Math.abs(event.getX() - pressDownX);
                float moveY = Math.abs(event.getY() - pressDownY);
                if (moveY > moveX || forceSpeakPos != -1) {
                    int step = (int) (event.getY() - pressDownY) / A.d(40);

                    if (pressDown_move_for_volume) {
                        if (tts_stopped || Math.abs(event.getY() - pressDownY) > A.d(8)) {
                            if (!tts_stopped) {
                                tts_stopped = true;
                                tts_stop();
                            }
                            int touchPos = getTouchPos();
                            int i = getSpeakLineIndex(touchPos);
                            if (i != -1) {
                                forceSpeakPos = touchPos;
                                TtsLine sl = speakLines.get(i);
                                highlightText(lastStartOfSpeakText + sl.start, lastStartOfSpeakText + sl.end);
                            }
                        }
						/*if (step != 0 || tts_volume.getProgress() != pressDown_speak_value) {
							int value = pressDown_speak_value - step;
							if (value < 1)
								value = 1;
							if (value > tts_volume.getMax())
								value = tts_volume.getMax();
							tts_volume.setProgress(value);
							T.showToastText(this, getString(R.string.tts_volume) + " " + value);
							((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
						}*/
                    } else {
                        if (step != 0 || A.tts_speed != pressDown_speak_value) {
                            int value = pressDown_speak_value - step;
                            if (value < 1)
                                value = 1;
                            if (value > maxTtsSpeed())
                                value = maxTtsSpeed();
                            tts_speed_setProgress(value);
                            pressDown_speed_speed = value;
                            T.showToastText(this, getString(R.string.tts_speed) + " " + value);
                        }
                    }
                } else if (moveX > A.d(80) && pressDown_move_for_volume) {
                    stop_speak();
                    pressDown = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (forceSpeakPos != -1) {
                    speakFingerPoint();
                } else if (!pressDown_move_for_volume && pressDown_speed_speed != A.tts_speed) {
                    A.tts_speed = pressDown_speed_speed;
                    restore_speak();
                }
                break;
        }
    }

    private void tts_speed_setProgress(int value) {
        if (value > 30)
            value = 30 + (value - 30) / 2;
        tts_speed.setProgress(value);
    }

    protected int get_tts_speed_progress(int progress) {
        return progress > 30 ? progress = 30 + (progress - 30) * 2 : progress > 0 ? progress : 1;
    }

    private int maxTtsSpeed() {
        return tts_speed.getMax() + 10;
    }

    //	long multi_change_font_time=-1;
    double multi_distance;
    boolean multi_touching;

    double getDistanceOfTwoPoint(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private boolean multiTouchForFontSize(MotionEvent event) {
        if (!A.mult_touch && web == null)
            return false;
        if (isPdf())
            return false;
        if (isTouchInEdge(event))
            return false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (multi_touching && event.getPointerCount() >= 2) {
                    setCurlViewINVISIBLE();
                    setFlipViewINVISIBLE(true);

                    if (multi_distance == -1) {
                        multi_distance = getDistanceOfTwoPoint(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    } else {
                        double distance2 = getDistanceOfTwoPoint(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                        if (web == null) {
                            float adjust = A.fontSize + (int) (distance2 - multi_distance) / A.d(20);
                            if (A.fontSize != adjust && adjust > 5 && adjust < 200) {
                                A.fontSize = event.getPointerCount() <= 2 ? adjust : A.fontSize + (adjust - A.fontSize) / 10;
                                if (A.fontSize > A.maxFontSize)
                                    A.fontSize = A.maxFontSize;
                                if (A.fontSize < A.minFontSize)
                                    A.fontSize = A.minFontSize;
                                setFontSize();
                                checkStatusBar();
                                multi_distance = distance2;
                                T.showToastText(this, (new DecimalFormat("0.0").format(A.fontSize)));
                            }

                        } else {
                            int adjust = (int) (distance2 - multi_distance) / A.d(20);
                            if (Math.abs(adjust) > 0) {
                                A.lastWebFontSize = A.lastWebFontSize + adjust;
                                if (A.lastWebFontSize > 72)
                                    A.lastWebFontSize = 72;
                                if (A.lastWebFontSize < 1)
                                    A.lastWebFontSize = 1;
                                setFontSize();
                                multi_distance = distance2;
                                T.showToastText(this, (new DecimalFormat("0.0").format(A.lastWebFontSize)));
                            }
                        }
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (multi_touching) {
                    multi_touching = false;
                    multi_distance = -1;
                    return true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() >= 2) {
                    multi_touching = true;
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean isTouchInEdge(MotionEvent event) {
        return (A.adjustBrightness && isWipeInEdge(A.brightness_edge, event)) ||
                (A.adjustFontSizeAtSlide && isWipeInEdge(A.fontsize_edge, event));
    }

    public boolean isWipeInEdge(int edge, MotionEvent event) {
        int cw = getEdgeSize();
        int w = baseFrame.getWidth();
        int h = baseFrame.getHeight();
        float x = getTouchX(event);
        float y = event.getY();
        switch (edge) {
            case A.EDGE_LEFT:
                return x < cw && pressDownX < cw;
            case A.EDGE_RIGHT:
                return x > w - cw && pressDownX > w - cw;
            case A.EDGE_TOP:
                return y < cw && pressDownY < cw;
            default:
                return y > h - cw && pressDownY > h - cw;
        }
    }

    private int getEdgeSize() {
        return A.d(A.isTablet ? (A.isLargeTablet ? 60 : 50) : 40);
    }

    private float birghtnessPos = -1, fontsizePos = -1;

    private float edgeMoveLength(int edge, MotionEvent event) {
        return (edge == A.EDGE_LEFT || edge == A.EDGE_RIGHT) ? event.getY() - pressDownY : getTouchX(event) - pressDownX;
    }

    private float edgeMovePos(int edge, MotionEvent event) {
        return (edge == A.EDGE_LEFT || edge == A.EDGE_RIGHT) ? event.getY() : getTouchX(event);
    }

    private int edgeLength(int edge) {
        return (edge == A.EDGE_LEFT || edge == A.EDGE_RIGHT) ? baseFrame.getHeight() : baseFrame.getWidth();
    }

    private boolean do_AdjustBrightness_Event(MotionEvent event) {
        if (isWipeInEdge(A.brightness_edge, event)) {
            float pos = edgeMovePos(A.brightness_edge, event);
            if (birghtnessPos == -1) {
                birghtnessPos = pos;
            } else {
                float adjust = birghtnessPos - pos;
                float adjust2 = Math.abs(adjust);
                float interval = A.df(20);
                if (adjust2 > interval) {
                    if (A.brightnessValue == -100) //第一次使用, 付给初始值0.5
                        A.brightnessValue = getSystemBrightness();//50;
                    if (velSpeed > 10000)
                        velSpeed = 10000;
                    float rate = adjust > 0 ? 500f : 400f;
                    int step = (int) ((1f + velSpeed / rate) * adjust2 / interval);

                    int value = A.brightnessValue + (adjust > 0 ? step : -step);
                    if (A.brightnessValue > 2 && value < 2) {
                        value = 2;
                    } else if (A.brightnessValue > 1 && value < 1) {
                        value = 1;
                    } else if (A.brightnessValue > 0 && value < 0) {
                        value = 0;
                    } else if (A.brightnessValue <= 0 && adjust < 0 && step > 1) {
                        step = 1;
                        value = A.brightnessValue - step;
                    }

                    A.brightnessValue = value;
                    birghtnessPos = pos;
                    A.log("*adjust:" + adjust + ", step:" + step + ", velSpeed: " + velSpeed + ", brightness:" + A.brightnessValue + ", rate:" + rate);

                    setScreenBrightness(A.brightnessValue, true);
//					resetFlipCache();
                    hasMarginAction = true;

//					if (!A.adjustBrightnessTipped) {
//						A.adjustBrightnessTipped = true;
//						T.showAlertText(this, getString(R.string.tip) + " (" + getString(R.string.miscellaneous) + ")",
//								getString(R.string.swpie_edge_brightness));
//					}
                    return true;
                }
            }
            if (!A.isFlipCurl() && !isValueFlip())
                return true;
        } else
            birghtnessPos = -1;

        return false;
    }

    private int getSystemBrightness() {
        try {
            int cur = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            cur = 100 * cur / 255;

            if (Build.VERSION.SDK_INT < 29) {
                int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                if (h > 8 && h < 19 && cur < 30)
                    cur += 15; //todo: 实际获取亮度经常是太低, 屏幕黑得严重
                else if (h > 21 || h < 6)
                    cur -= 0;
                else if (cur < 30)
                    cur += 5;
            }
            return cur;
        } catch (Exception e) {
            A.error(e);
            return 50;
        }
    }

    private float velSpeed;
    VelocityTracker velTracker;

    private void saveBrightMoveSpeed(MotionEvent event) {
        if (isWipeInEdge(A.brightness_edge, event)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    velSpeed = 0;
                    initVelTracker();
                    velTracker.addMovement(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    initVelTracker();
                    velTracker.addMovement(event);
                    velTracker.computeCurrentVelocity(1000);
                    velSpeed = Math.abs(A.brightness_edge < 2 ? velTracker.getYVelocity() : velTracker.getXVelocity());
//					A.log("Y velocity(2): " + velSpeed);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    releaseVelTracker();
                    break;
            }
        } else if (velTracker != null && event.getAction() == MotionEvent.ACTION_UP) {
            releaseVelTracker();
        }
    }

    private void releaseVelTracker() {
        if (velTracker != null) {
            velTracker.clear();
            velTracker.recycle();
            velTracker = null;
        }
        velSpeed = 0;
    }

    private void initVelTracker() {
        if (velTracker == null)
            velTracker = VelocityTracker.obtain();
    }

    public void resetFlipCache() {
        resetFlipCache(false, 0);
    }

    public void resetFlipCache(boolean resetCachShot, int delay) {
        resetFlipCache(resetCachShot, delay, true);
    }

    public void resetFlipCache(boolean resetCachShot, int delay, boolean hideCurl3dLay) {
        lastFlipScrollY = -1;
        if (curl3dCover != null)
            curl3dCover.setTag(null);
        if (resetCachShot)
            createCachePageShotsHandler(delay);
        else
            T.recycle(tmpFlipShot3);
        if (hideCurl3dLay && curl3d != null && curl3dLay.getVisibility() == View.VISIBLE)
            setCurl3dVisibility(false);
        if (getBookType() == A.FILE_TXT && !T.isRecycled(tmpFlipShot3))
            T.recycle(tmpFlipShot3);
        if (A.isFlipHorizShift())
            txtCache.setText("");
    }

    private boolean do_AdjustFontSize_Event(MotionEvent event) {
        if (isWipeInEdge(A.fontsize_edge, event)) {
            float pos = edgeMovePos(A.fontsize_edge, event);

            if (fontsizePos == -1)
                fontsizePos = pos;
            else {
                float adjust = fontsizePos - pos;
                if (Math.abs(adjust) > A.d(40)) {
                    resetPageCount();

                    if (isPdfNoflow()) {
                        pdfZoomRatio(adjust > 0 ? 0.1f : -0.1f);
                    } else if (web == null) {
                        float level = event.getPointerCount() >= 2 ? 0.1f : 1f;
                        A.fontSize = A.fontSize + (adjust > 0 ? level : -level);
                        if (A.fontSize > A.maxFontSize)
                            A.fontSize = A.maxFontSize;
                        if (A.fontSize < A.minFontSize)
                            A.fontSize = A.minFontSize;
                        T.showToastText(this, (new DecimalFormat("0.0").format(A.fontSize)));
                        setFontSize();
                    } else {
                        webViewZoom(adjust > 0);
                    }
                    fontsizePos = pos;
                    hasMarginAction = true;
                    return true;
                }
            }
            if (!A.isFlipCurl() && !isValueFlip())
                return true;
        } else
            fontsizePos = -1;

        return false;
    }

    long lastTapUpTime = -1;
    private boolean autoScrollPaused;

    private void do_TapUp_Event(MotionEvent event) {
        if (hide_ebook_cover())
            return;
        if (layoutVisible) {
            inverseLayoutVisible(true);
            return;
        }

        if (lastTapUpTime != -1)
            if (SystemClock.elapsedRealtime() - lastTapUpTime < 100)//防止屏幕出现连续错误事件
                return;
        lastTapUpTime = SystemClock.elapsedRealtime();

        if (A.isInAutoScroll) {
            autoScrollPaused = !autoScrollPaused;
            if (autoScrollPaused)
                T.showToastText(this, getString(R.string.pause));
            return;
        }

        if (A.isSpeaking) {
            do_tts_buttons(tts_play);
            if (tts_paused)
                T.showToastText(this, getString(R.string.pause));
            return;
        }

        if (inWebReading) {
            if (isMiddleTap(event))
                inverseLayoutVisible(false);
            return;
        }

        if (!isUrlClick(event, true)) {
            if (clickForBookmark(event))
                return;
            switch (getClickArea(event)) {
                case A.CLICK_TOP:
                    if (layoutVisible)
                        inverseLayoutVisible(true);
                    if (!A.disableClick || A.flip_animation != A.FLIP_NONE)
                        doTouchDown(A.CLICK_TOP);
                    break;
                case A.CLICK_BOTTOM:
                    if (layoutVisible)
                        inverseLayoutVisible(true);
                    if (!A.disableClick || A.flip_animation != A.FLIP_NONE)
                        doTouchDown(A.CLICK_BOTTOM);
                    break;
                case A.CLICK_LEFT:
                    if (layoutVisible)
                        inverseLayoutVisible(true);
                    if (!A.disableClick || A.flip_animation != A.FLIP_NONE)
                        doTouchDown(A.CLICK_LEFT);
                    break;
                case A.CLICK_RIGHT:
                    if (layoutVisible)
                        inverseLayoutVisible(true);
                    if (!A.disableClick || A.flip_animation != A.FLIP_NONE)
                        doTouchDown(A.CLICK_RIGHT);
                    break;
                case A.CLICK_MIDDLE:
                    boolean interrupt = false;
                    if (isSingleTapForTextSelection()) {
                        do_text_select(true);
                        if (pdf == null || pdf.textReflow)
                            interrupt = dotVisible();
                    }
                    if (!interrupt)
                        inverseLayoutVisible(false);
                    break;
            }
        }

        return;
    }

    private int getClickArea(MotionEvent event) {
        int clickArea = A.clickArea(event);
        if (A.wholeScreenPageDown && clickArea != A.CLICK_MIDDLE)
            clickArea = A.CLICK_RIGHT;
        return clickArea;
    }

    private boolean clickForBookmark(MotionEvent event) {
        if (getTouchX(event) > baseFrame.getWidth() - A.d(40) && event.getY() < A.d(40)) {
            createBookmark(null, false, true);
            resetFlipCache();
            return true;
        }
        return false;
    }

    private boolean isSingleTapForTextSelection() {
        return A.doTapScreenTop == A.DO_SELECT_TEXT || A.doTapScreenBottom == A.DO_SELECT_TEXT || A.doTapScreenLeft == A.DO_SELECT_TEXT
                || A.doTapScreenRight == A.DO_SELECT_TEXT;
    }


    private boolean isVisualBoomarkClick(MotionEvent event) {
        if (contentLay.visualBookmarks.size() == 0)
            return false;
        int h = txtView.getLineHeight() * 80 / 100;
        int w = h * 50 / 70;
        float x = getTouchX(event);
        float y = event.getY();
        for (final Bookmark bm : contentLay.visualBookmarks) {
            if (x > contentLay.getWidth() - w - A.d(1) && y > bm.drawY2 && y < bm.drawY2 + h) {
                String[] items = new String[2];
                items[0] = A.getStringArrayItem(R.array.catalog_popup_menu, 1);
                items[1] = A.getStringArrayItem(R.array.one_file_bookmark, 1);
                String title = bm.name;
                if (title.indexOf(") ") != -1)
                    title = title.substring(title.indexOf(") ") + 2, title.length());

                new MyDialog.Builder(this)
                        .setTitle(title)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        new PrefEditBookmark(ActivityTxt.this, bm.name, new PrefEditBookmark.OnAfterEdit() {
                                            public void AfterEdit(String result, Integer editColor) {
                                                bm.name = result;
                                                bm.color = editColor;
                                                BookDb.removeBookmark(bm);
                                                BookDb.addBookmark(bm);
                                                A.lastFileAnnotUpdated = true;
                                                if (!updateForFitHardwareAccelerate())
                                                    contentLay.postInvalidate();
                                            }
                                        }, bm.color);
                                        break;

                                    case 1:
                                        int id = A.getBookmarksId(A.lastFile);
                                        if (id != -1) {
                                            A.getBookmarks().get(id).remove(bm);
                                            BookDb.removeBookmark(bm);
                                            A.lastFileAnnotUpdated = true;
                                            if (!updateForFitHardwareAccelerate())
                                                contentLay.postInvalidate();
                                        }
                                        break;
                                }
                            }
                        })
                        .show();
                return true;
            }
        }
        return false;
    }

    private boolean isImageClick(MotionEvent event, boolean forMediaOnly, boolean execute) {
        if (isPdf())
            return false;
        if (web != null)
            return false;
        if (!isHtmlContent())
            return false;

        try {
            ScrollView sv = A.touchingView == txtScroll2 ? txtScroll2 : txtScroll;
            MRTextView tv = getTouchTxtView();
            int y = sv.getScrollY();
            int pl = sv.getPaddingLeft();
            int pt = sv.getPaddingTop();
            float hx = event.getX() - pl;
            int hy = (int) event.getY() + y - pt;

            MyLayout lo = tv.getLayout();
            if (lo == null)
                return false;
            int line = lo.getLineForVertical(hy);
            int j1 = lo.getLineStart(line);
            int j2 = lo.getLineVisibleEnd(line);

            int floatWidth = lo.getLineFloat(line);
            if (floatWidth != 0) {
                MyFloatSpan sp = lo.getLineFloatSp(line);
                MRTextView.MarginF margins = tv.getCssMargins(line);
                float fx = -1;
                if (floatWidth > 0) {
                    if (hx < margins.left)
                        fx = hx;
                } else if (floatWidth < 0) {
                    if (hx > tv.getWidth() - margins.right)
                        fx = hx - (tv.getWidth() - margins.right);
                }
                if (fx != -1 && !sp.isDropcap) {
                    if (sp.float_text == null || hy < sp.float_sl_v) {
                        return dealImageSpan(sp.imageSpan, forMediaOnly, execute);
                    } else if (execute) {
                        float fy = hy - sp.float_sl_v;
                        int fline = sp.float_sl.getLineForVertical((int) fy);
                        int fstart = sp.float_sl.getOffsetForHorizontal(fline, fx);
                        int fend = fstart + 2;
                        fstart = fstart - 2;
                        if (fstart < 0) fstart = 0;
                        if (fend > sp.float_text.length()) fend = sp.float_text.length();
                        MyUrlSpan[] spans = ((Spanned) sp.float_text).getSpans(fstart, fend, MyUrlSpan.class);
                        if (spans.length > 0) {
                            return executeUrl((Spanned) sp.float_text, spans[0], spans[0].getURL());
                        } else {
                            copyToClipboard(sp.float_text.toString());
                            return true;
                        }
                    }
                }
            }

            int offset = tv.getLineOffset(line, hx);
            if (offset == -1)
                return false;
            if (j2 > j1) {
                Spanned spanStr = null;
                if (tv.getText() instanceof SpannedString)
                    spanStr = (SpannedString) tv.getText();
                if (tv.getText() instanceof SpannableStringBuilder)
                    spanStr = (SpannableStringBuilder) tv.getText();
                if (spanStr != null) {
                    Object[] spans = spanStr.getSpans(j1, j2, Object.class);
                    String lineText = tv.getLineText(line).replace("\n", "").replace(A.INDENT_CHAR + "", "").trim();
                    for (Object o : spans) {
                        if (o instanceof com.flyersoft.staticlayout.MyImageSpan) {
                            MyImageSpan sp = (MyImageSpan) o;
                            int w = sp.getDrawable().getBounds().width();
                            if (w > baseFrame.getWidth() / 2 || lineText.length() == 1)
                                return dealImageSpan(sp, forMediaOnly, execute);
                        }
                    }
                    if (j1 < offset || j2 > offset) {
                        if (j1 < offset)
                            j1 = offset - 1;
                        if (j2 > offset)
                            j2 = offset + 1;
                        spans = spanStr.getSpans(j1, j2, Object.class);
                        for (Object o : spans) {
                            if (o instanceof com.flyersoft.staticlayout.MyImageSpan)
                                return dealImageSpan((MyImageSpan) o, forMediaOnly, execute);
                        }
                    }
                }
            }
        } catch (Exception e) {
            A.error(e);
        }
        return false;
    }

    private boolean dealImageSpan(MyImageSpan sp, boolean forMediaOnly, boolean execute) {
        if (isMediaSpan(sp, forMediaOnly))
            return true;
        if (forMediaOnly)
            return false;
        if (execute)
            startImageGallery(sp);
        return true;
    }

    private boolean isMediaSpan(MyImageSpan sp, boolean playMedia) {
        String source = sp.getSource();
        if (source.equals("#audio#null") || source.startsWith("#video#null")) {
            T.showToastText(this, "null");
            return true;
        }
        if (source.startsWith("#audio#")) {
            if (playMedia) {
                if (source.startsWith("#audio#http") || source.startsWith("#audio#rtsp")) {
                    playMp3(source.substring(7));
                } else {
                    A.ebook.getDrawableFromSource(source, 0);
                    String cacheFile = A.book_cache + "/" + T.getFilename(A.lastFile) + "/" + T.getFilename(source).replace("#audio#", "");
                    if (T.isFile(cacheFile))
                        playMp3(cacheFile);
                }
            }
            return true;
        } else if (source.startsWith("#video#")) {
            if (playMedia) {
                if (source.startsWith("#video#http")) {
                    playVideo(source.substring(7));
                } else {
                    A.ebook.getDrawableFromSource(source, 0);
                    String cacheFile = A.book_cache + "/" + T.getFilename(A.lastFile) + "/" + T.getFilename(source).replace("#video#", "");
                    if (T.isFile(cacheFile))
                        playVideo(cacheFile);
                }
            }
            return true;
        } else
            return false;
    }

    private boolean isUrlClick(MotionEvent event, boolean checkHighlight) {
        urlFootnote = null;
        checkUrlFootnote = true;
        if (isPdfNoflow())
            return false;
        if (web != null)
            return false;
        if (A.currentChapterWithImageOnly())
            return false;
        if (hasMediaTag && isImageClick(event, true, true))
            return true;
        if (isHtmlContent())
            try {
                ScrollView sv = A.touchingView == txtScroll2 ? txtScroll2 : txtScroll;
                MRTextView tv = getTouchTxtView();

                Spanned spanned = tv.getSpanned();
                if (spanned == null)
                    return false;

                int y = sv.getScrollY();
                int pl = sv.getPaddingLeft();
                int pt = sv.getPaddingTop();
                float hx = event.getX() - pl;
                int hy = (int) event.getY() + y - pt;

                MyLayout lo = tv.getLayout();
                if (lo == null)
                    return false;

                if (hy > tv.getHeight())
                    return false;

                int line = lo.getLineForVertical(hy);
                if (tv.lastIgnoreLine() > 0 && line >= tv.lastIgnoreLine())
                    return false;
                if (isTableZoomIcon(tv, spanned, line, hx))
                    return true;

                int offset = tv.getLineOffset(line, hx);
                if (offset == -1)
                    return false;

                int j1 = lo.getLineStart(line);
                int j2 = lo.getLineVisibleEnd(line);
                if (j2 == j1)
                    return false;

                if (offset == j1) {
                    float realx = tv.getTextX(line, j1);
                    if (hx < realx - A.d(20))
                        return false;
                }
                if (offset >= j2) {  // j2 is next line's begin
                    if (offset == j2)
                        offset = j2 - 1;
                    int w = tv.getWidth();
                    if (j2 - j1 == 1 && tv.getTextX(line, j1) < w / 3 && hx > w / 3)
                        return false;
                    if (hx > tv.getTextX(line, j2) + A.d(20))
                        return false;
                    if (j2 - j1 == 1 && spanned.charAt(j1) == '\uFFFC')
                        if (hx < w / 3 || hx > w * 2 / 3) //for single image or super char, must click middle
                            if (tv.getLineHeight(line) > tv.getLineHeight(line) * 2)
                                return false;
                }

                int start = offset;
                if (hx > A.d(16)) start = txtView.getLineOffset(line, hx - A.d(16));
                else if (j1 < start) start -= 2;
                if (start < j1) start = j1;

                int end = offset;
                if (hx < txtView.getWidth() - A.d(16))
                    end = txtView.getLineOffset(line, hx + A.d(16));
                else if (j2 > offset) end = offset + 2;
                if (end > j2) end = j2;

//				MyUrlSpan[] spans = spanned.getSpans(start, end, MyUrlSpan.class);
                MyUrlSpan o = getUrlSpan(start, end, offset, spanned);
                if (o != null) {
                    o.clicked = true;
                    String url = o.getURL();
                    if (executeUrl(spanned, o, url))
                        return true;
                }
            } catch (Exception e) {
                A.error(e);
            }

        if (checkHighlight && isNoteHighlightClick(event))
            return true;

        return false;
    }

    private MyUrlSpan getUrlSpan(int start, int end, int offset, Spanned spanned) {
        MyUrlSpan[] spans = spanned.getSpans(start, end, MyUrlSpan.class);
        MyUrlSpan o = null;
        for (MyUrlSpan sp : spans) {
            if (o == null) {
                o = sp;
            } else {
                int off1 = Math.abs(offset - spanned.getSpanStart(o));
                int off2 = Math.abs(offset - spanned.getSpanStart(sp));
                if (off2 < off1)
                    o = sp;
            }
        }
        return o;
    }

    private boolean executeUrl(Spanned spanned, Object o, String url) {
        if (T.isNull(url))
            return false;
        if (A.ebook != null && url.startsWith("***")) { //duoKan popup footnote, from MyHtml.StartImg()
            footnoteStr = url.substring(3);
            showEpub3Footnote();
            return true;
        } else if (A.ebook != null && url.startsWith("?")) {//fb2
            return dealFb2Link(url);
        } else if (!url.startsWith("@@")) {
            String title = spanned.subSequence(spanned.getSpanStart(o), spanned.getSpanEnd(o)).toString().trim();
            openUrlLink(url, title, 50);
            return true;
        }
        return false;
    }

    private boolean isTableZoomIcon(MRTextView tv, Spanned spanStr, int line, float hx) {
        int i1 = line > 0 ? line - 1 : 0;
        int i2 = line < tv.getRealLineCount() - 1 ? line + 1 : line;
        int start = txtView.getLayout().getLineStart(i1);
        int end = txtView.getLayout().getLineVisibleEnd(i2);
        MyTableSpan[] sps = spanStr.getSpans(start, end, MyTableSpan.class);

        for (MyTableSpan sp : sps)
            if (sp.html != null && sp.renderTime > 0) {
                int tl = txtView.getLayout().getLineForOffset(sp.spStart);
                if ((line == tl || line == tl - 1)
                        && hx > sp.r - A.d(48) && hx < sp.r) {
                    new PrefHtmlViewer(this, sp.html).show();
                    return true;
                }
            }
        return false;
    }

    private boolean dealFb2Link(String url) {
        if (url.startsWith("??")) {
            String tag = url.substring(2, url.length());
            try {
                for (int i = A.ebook.getChapters().size() - 1; i >= 0; i--) {
                    String html = A.ebook.getChapterText(i);
                    int j = html.indexOf("id=\"" + tag + "\"");
                    if (j != -1)
                        j = html.lastIndexOf("<", j);
                    if (j != -1) {
                        html = html.substring(0, j);
                        int p = MyHtml.fromHtml(html, imageGetter, A.lastChapter).toString().length();

                        saveLinkBackInfo(true);
                        saveChapterTextToPriorCache(A.lastChapter, A.lastSplitIndex, htmlSrc);
                        boolean same = A.lastChapter == i;
                        A.lastChapter = i;
                        A.lastPosition = p;
                        if (same) {
                            MyLayout lo = txtView.getLayout();
                            int y = lo.getLineTop(lo.getLineForOffset((int) A.lastPosition));
                            txtScrollTo(y);
                        } else
                            showEBookByPosition(A.lastChapter, 0, A.lastPosition, true);
                        linkBackSetVisible();
                        resetFlipCache();
                        ;
                        hideProgressDlg();

                    }
                }
            } catch (OutOfMemoryError e) {
                A.error(e);
            } catch (Exception e) {
                A.error(e);
            }
            return true;
        } else {
            BaseEBook.FootNote fn = A.ebook.getFootNote(url.substring(1, url.length()));
            getTouchTxtView().invalidate();
            if (fn != null) {
                showFB2Footnote(fn);
                return true;
            }
        }
        return false;
    }

    private void doMobiLink(String url, String title) {
        url = url.substring(5);
        while (url.startsWith("0"))
            url = url.substring(1);
        try {
            int p = Integer.valueOf(url);
            int count = 0, chapterSize = A.ebook.getChapters().size();
            for (int i = 0; i < chapterSize; i++) {
                count += A.ebook.getChapters().get(i).size;
                if (count >= p) {
                    int cIndex = A.lastChapter;
                    int splitIndex = A.lastSplitIndex;
                    int tmpPosition = 0;

                    if (count == p) {
                        if (i < chapterSize - 1) {
                            cIndex = i + 1;
                            splitIndex = 0;
                            tmpPosition = 0;
                        } else
                            return;
                    } else {
                        boolean done = false;
                        if (p > count - 200 && i < chapterSize - 1) {
                            String s = A.ebook.getChapterText(i);
                            if (A.chapterEndPrompt && s.indexOf(BaseEBook.CHAPTER_END_HTMLHINT1) != -1)
                                s = s.substring(0, s.indexOf(BaseEBook.CHAPTER_END_HTMLHINT1));
                            int off = p - (count - s.length());
                            if (s.length() > off) {
                                s = s.substring(off);
                                int j = s.indexOf(">");
                                int k = s.indexOf("<");
                                if ((j != -1 && k == -1) || (j == -1 && k != -1)) {
                                    done = true;
                                } else {
                                    if (k > j)
                                        s = s.substring(k);
                                    s = Html.fromHtml(s).toString().trim();
                                    if (s.length() == 0)
                                        done = true;
                                }
                            }
                        }
                        if (done) {
                            cIndex = i + 1;
                            splitIndex = 0;
                            tmpPosition = 0;
                        } else {
                            String s = A.ebook.getChapterText(i);
                            cIndex = i;
                            int t = (int) (A.ebook.getChapters().get(i).size - (count - p));
                            t = (int) ((long) s.length() * t / A.ebook.getChapters().get(i).size);
                            if (t < A.maxHtmlChapterSize) {
                                splitIndex = 0;
                            } else {
                                ArrayList<String> htmls = new ArrayList<String>();
                                A.createSplitHtmls(A.ebook.getChapterText(i), htmls);
                                long count2 = 0;
                                for (int i2 = 0; i2 < htmls.size(); i2++) {
                                    count2 += htmls.get(i2).length();
                                    if (count2 > t) {
                                        splitIndex = i2;
                                        s = htmls.get(i2);
                                        t = t - (int) (count2 - s.length());
                                        break;
                                    }
                                }
                            }

                            title = title.trim();
                            int t2 = s.lastIndexOf(title, t);
                            int t3 = s.indexOf(title, t);
                            A.log("---------------t2:" + t2 + " t:" + t + " t3:" + t3 + "  (t-t2):" + (t - t2) + "  (t3-t):" + (t3 - t));
                            if (t3 == -1) {
                                if (t2 != -1 && (t - t2) < 300)
                                    t = t2;
                            } else if (t3 - t < 150) {
                                t = t3;
                            } else if (t3 > 1000 && (t2 != -1 && (t - t2) < 300))
                                t = t2;

                            int idPos = s.lastIndexOf("<", t);
                            String tagStr = "(<a href=\"@@\">#</a>)";
                            StringBuilder sb = new StringBuilder();
                            sb.append(s.substring(0, idPos));
                            sb.append(tagStr);
                            sb.append(s.substring(idPos));
                            Spanned spanned = MyHtml.fromHtml(sb.toString(), imageGetter, i);
                            MyUrlSpan[] spans = spanned.getSpans(0, spanned.length(), MyUrlSpan.class);
                            for (MyUrlSpan o : spans) {
                                if (o.getURL().equals("@@")) {
                                    tmpPosition = spanned.getSpanStart(o);
                                    break;
                                }
                            }
                        }
                    }

                    saveLastPostion(false);
                    saveChapterTextToPriorCache(A.lastChapter, A.lastSplitIndex, htmlSrc);
                    saveLinkBackInfo(true);
                    if (A.lastChapter == cIndex && A.lastSplitIndex == splitIndex) {
                        int y = txtView.getLineTop2(txtView.getLayout().getLineForOffset(tmpPosition));
                        txtScrollTo(y);
                    } else {
                        showEBookByPosition(cIndex, splitIndex, tmpPosition, true);
                    }
                    linkBackSetVisible();
                    break;
                }
            }
        } catch (Exception e) {
            A.error(e);
        }
    }

    private void showFB2Footnote(final FootNote fn) {
        footnoteStr = "<h3>" + fn.title + "</h3>" + fn.html;
        showEpub3Footnote();
    }

    private MRTextView getTouchTxtView() {
        return A.touchingView == txtScroll2 ? txtView2 : txtView;
    }

    private void startImageGallery(MyImageSpan gallerySp) {
        if (gallerySp == null)
            return;

        String src = gallerySp.getSource();
        if (A.isHttpUrl(src)) {
            String cache = A.generateUrlImageCacheName(src);
            if (T.isFile(cache)) {
                Intent i = new Intent(this, PicGalleryShow.class);
                i.putExtra("imageFile", cache);
                startActivity(i);
            }
            return;
        }

        Intent i = new Intent(this, PicGalleryShow.class);
        if (A.ebook != null)
            i.putExtra("ebookImage", src);
        else
            i.putExtra("imageFile", T.getFilePath(A.lastFile) + "/" + src); //Html files
        startActivity(i);
    }

    //-----------open url link functions:-----------------------------
    private void openUrlLink(final String url, final String title, long delay) {
        if (isWebImage(url, title))
            return;
        if (isVipLink(url))
            return;
        if (isEpub3Footnote(url, title))
            return;

        createProgressDlg(title);
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    getTouchTxtView().invalidate();
                    openUrlLinkHandler(url, title);
                } catch (OutOfMemoryError e) {
                    A.error(e);
                } catch (Exception e) {
                    A.error(e);
                }
            }
        }.sendEmptyMessageDelayed(0, delay);
    }

    private void openUrlLinkHandler(final String url, final String title) {
        if (url.startsWith("@mobi") && A.ebook != null) {
            doMobiLink(url, title);
            hideProgressDlg();
            return;
        }
        if (A.isHttpUrl(url)) {
            T.openUrl(ActivityTxt.this, url);
            hideProgressDlg();
            return;
        }

        LINK_BACK lb_tag = saveLinkBackInfo(true);
        cPos = 0;
        int tmpLastSplitIndex = A.lastSplitIndex;
        int tmpLastChapter = A.lastChapter;
        String tmpHtmlSrc = htmlSrc;
//		A.logTime = SystemClock.elapsedRealtime();
        A.log("1:");
        ArrayList<String> oldSplitHtmls = (ArrayList<String>) A.splitHtmls.clone();
        ArrayList<String> oldPreNextSplitHtmls = preNextSplitHtmls == null ? null : A.splitHtmls == preNextSplitHtmls ? oldSplitHtmls
                : (ArrayList<String>) preNextSplitHtmls.clone();
        if (getBookType() == A.FILE_HTML) {
            if (url.startsWith("#")) {
                if (openHtmlFileWithTag(url.substring(1), false)) {
                    linkBackSetVisible();
                    hideProgressDlg();
                    return;
                }
            } else {
                int tagIndex = url.indexOf("#");
                String newFile = T.getFilePath(A.lastFile) + "/" + (tagIndex == -1 ? url : url.substring(0, tagIndex));
                if (T.isFile(newFile)) {
                    A.lastFile = newFile;
                    if (tagIndex != -1) {
                        htmlSrc = T.getFileText(A.lastFile);
                        openHtmlFileWithTag(url.substring(tagIndex + 1), true);
                    } else {
                        htmlText = "";
                        reloadBook();
                    }
                    linkBackSetVisible();
                    hideProgressDlg();
                    return;
                }
            }
        } else { //ebook
            String aUrl = Uri.decode(url);
            int cIndex = -1;
            int idIndex = aUrl.indexOf("#"); //has tag
            if (idIndex == -1) {
                String file = getExactFileInEpub(aUrl);
                for (int i = 0; i < A.ebook.getChapters().size(); i++) {
                    BaseEBook.Chapter c = A.ebook.getChapters().get(i);
                    if (c.filename != null && c.filename.equals(file)) {
                        cIndex = i;
                        break;
                    }
                }
            }

            if (cIndex == -1 && aUrl.indexOf("./") != -1 && idIndex == -1) { //chm
                String aUrl2 = aUrl.substring(aUrl.lastIndexOf("/") + 1);
                for (int i = 0; i < A.ebook.getChapters().size(); i++) {
                    BaseEBook.Chapter c = A.ebook.getChapters().get(i);
                    if (c.filename != null && (c.filename.equals(aUrl2) || c.filename.endsWith("/" + aUrl2))) {
                        cIndex = i;
                        break;
                    }
                }
            }

            if (cIndex == -1) {
                if (idIndex == 0) { //v1.5.1 in the same file
                    int tagIndex = -1;
                    int split = tmpLastSplitIndex;
                    boolean inSplit = false;
                    String html = htmlSrc;
                    String id_str = aUrl.substring(idIndex + 1);
                    String tag1 = "id=\"" + id_str + "\"";
                    String tag12 = "id = \"" + id_str + "\"";
                    String tag2 = "name=\"" + id_str + "\"";
                    String tag22 = "name = \"" + id_str + "\"";
                    int start = (int) (html.length() * A.lastPosition / txtView.getText().length());
                    start = start * 9 / 10; //v1.9.4
                    if (A.noSplitHtmls()) {
                        tagIndex = getTagIndexFromHtml(html, tag1, tag12, tag2, tag22, start);
                        if (tagIndex == -1)
                            tagIndex = getTagIndexFromHtml(html, tag1, tag12, tag2, tag22, 0);
                    } else {
                        tagIndex = getTagIndexFromHtml(html, tag1, tag12, tag2, tag22, start);
                        if (tagIndex == -1)
                            for (int i = tmpLastSplitIndex + 1; i < A.splitHtmls.size(); i++) {
                                tagIndex = getTagIndexFromHtml(A.splitHtmls.get(i), tag1, tag12, tag2, tag22, 0);
                                if (tagIndex != -1) {
                                    split = i;
                                    inSplit = true;
                                    break;
                                }
                            }
                        if (tagIndex == -1)
                            for (int i = tmpLastSplitIndex; i >= 0; i--) {
                                tagIndex = getTagIndexFromHtml(A.splitHtmls.get(i), tag1, tag12, tag2, tag22, 0);
                                if (tagIndex != -1) {
                                    split = i;
                                    inSplit = true;
                                    break;
                                }
                            }
                    }

                    if (tagIndex == -1) {//v1.8.3
                        Chapter c1 = A.ebook.getChapters().get(A.lastChapter);
                        String filename = c1.filename;

                        //v4.6 fast locate in same file, very-big.mobi
                        for (int i = 0; i < A.ebook.getChapters().size(); i++)
                            if (i != A.lastChapter) {
                                BaseEBook.Chapter c2 = A.ebook.getChapters().get(i);
                                if (id_str.equals(c2.id_Tag) && c2.filename.equals(filename)) {
                                    ArrayList<String> list = new ArrayList<>();
                                    String src = A.adjustChapterHtml(A.ebook.getChapterText(i), list);
                                    if (list.size() == 0) {
                                        tagIndex = getTagIndexFromHtml(src, tag1, tag12, tag2, tag22, 0);
                                    } else {
                                        for (int j = 0; j < list.size(); j++) {
                                            src = list.get(j);
                                            tagIndex = getTagIndexFromHtml(src, tag1, tag12, tag2, tag22, 0);
                                            if (tagIndex != -1) {
                                                split = j;
                                                break;
                                            }
                                        }
                                    }
                                    if (tagIndex != -1) {
                                        html = src;
                                        A.lastChapter = i;
                                        break;
                                    }
                                }
                            }

                        if (tagIndex == -1)
                            for (int i = 0; i < A.ebook.getChapters().size(); i++)
                                if (i != A.lastChapter) {
                                    BaseEBook.Chapter c2 = A.ebook.getChapters().get(i);
                                    boolean hasSameFile = c2.filename.equals(filename);
                                    if (!hasSameFile && c1.usedFiles.size() > 1)
                                        for (String s : c1.usedFiles)
                                            if (s != filename && c2.filename.equals(s)) {
                                                hasSameFile = true;
                                                break;
                                            }
                                    if (!hasSameFile && c2.usedFiles.size() > 1)
                                        for (String s : c2.usedFiles)
                                            if (s.equals(filename)) {
                                                hasSameFile = true;
                                                break;
                                            }
                                    if (hasSameFile) {
                                        ArrayList<String> list = new ArrayList<>();
                                        String src = A.adjustChapterHtml(A.ebook.getChapterText(i), list);
                                        if (list.size() == 0) {
                                            tagIndex = getTagIndexFromHtml(src, tag1, tag12, tag2, tag22, 0);
                                        } else {
                                            for (int j = 0; j < list.size(); j++) {
                                                src = list.get(j);
                                                tagIndex = getTagIndexFromHtml(src, tag1, tag12, tag2, tag22, 0);
                                                if (tagIndex != -1) {
                                                    split = j;
                                                    break;
                                                }
                                            }
                                        }
                                        if (tagIndex != -1) {
                                            html = src;
                                            A.lastChapter = i;
                                            break;
                                        }
                                    }
                                }
                    }
                    if (tagIndex != -1) {
                        String source = !inSplit ? html : A.splitHtmls.get(split);
                        getCIndex(0, null, id_str, aUrl, null, source, tagIndex);
                        cIndex = A.lastChapter;
                        A.lastSplitIndex = split;
                    }
                } else if (idIndex > 0) {
                    String file = aUrl.substring(0, idIndex);
                    file = getExactFileInEpub(file);
                    String id_str = aUrl.substring(idIndex + 1);
                    for (int i = 0; i < A.ebook.getChapters().size(); i++) {
                        cIndex = existTag(aUrl, file, id_str, i);
                        if (cIndex != -1)
                            break;
                    }
                }
            }
            if (cIndex == -1) {
                String file = idIndex == -1 ? aUrl : aUrl.substring(0, idIndex);
                file = getExactFileInEpub(file);
                for (int i = 0; i < A.ebook.getChapters().size(); i++) {
                    if (inUsedFiles(file, A.ebook.getChapters().get(i))) {
                        cIndex = i;
                        String fileTag = "<a name=" + T.getFilename(aUrl) + ">";
                        String html;
                        ArrayList<String> list = new ArrayList<String>();
                        if (i == tmpLastChapter) {
                            list = A.splitHtmls;
                            html = list.size() > 0 ? list.get(0) : htmlSrc;
                        } else {
                            html = A.ebook.getChapterText(i);
                            html = A.adjustChapterHtml(html, list);
                        }

//						A.moveStart = true; //PAGE_BREAK begin at usedFiles content
                        if (list.size() == 0) {
                            int k = html.indexOf(fileTag);
                            if (k != -1)
                                cPos = MyHtml.fromHtml(html.substring(0, k), imageGetter, i).length() + 1;
                        } else {
                            for (int j = 0; j < list.size(); j++) {
                                html = list.get(j);
                                int k = html.indexOf(fileTag);
                                if (k != -1) {
                                    cPos = MyHtml.fromHtml(html.substring(0, k), imageGetter, i).length() + 1;
                                    A.lastSplitIndex = j;
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }

            if (cIndex >= 0) {
                if (urlFootnote != null) {
                    A.lastChapter = tmpLastChapter;
                    A.lastSplitIndex = tmpLastSplitIndex;
                    A.splitHtmls = oldSplitHtmls;
                    htmlSrc = tmpHtmlSrc;
                    preNextSplitHtmls = oldPreNextSplitHtmls;
                    hideProgressDlg();
                    link_backs.remove(lb_tag);
                    showUrlFootnote(url, title, cIndex);
                    return;
                }

                saveLastPostion(false);
                A.splitHtmls = oldSplitHtmls;
                saveChapterTextToPriorCache(A.lastChapter, tmpLastSplitIndex, tmpHtmlSrc);
                boolean same = tmpLastChapter == cIndex && tmpLastSplitIndex == A.lastSplitIndex;
                A.lastChapter = cIndex;
                A.lastPosition = cPos;
                if (A.lastPosition == 0) {
                    ebookChapterDown = true;
                    reloadBook();
                } else {
//					if (A.lastPosition > 100)
//						A.lastPosition -= (A.isTablet? 100 : 80); //todo: better display, and fix not accurate error?
                    if (same) {
                        MyLayout lo = txtView.getLayout();
                        int y = txtView.getLineTop2(lo.getLineForOffset((int) A.lastPosition));
                        txtScrollTo(y);
                    } else {
                        showEBookByPosition(A.lastChapter, A.lastSplitIndex, A.lastPosition, true);
                    }
                }
                linkBackSetVisible();
                hideProgressDlg();
                updateProgressStatus();
                A.log("3:");
                return;
            }
        }

        if (A.ebook != null) {
            String aUrl = Uri.decode(url);
            if (aUrl.indexOf("#") != -1)
                aUrl = aUrl.substring(0, aUrl.indexOf("#"));
            String singleFileText = A.ebook.getSingleFileText(aUrl);
            if (singleFileText != null && singleFileText.length() > 0) {
                linkBackSetVisible();
                htmlSrc = T.deleteHtmlStyle(T.getHtmlBody(singleFileText));
                htmlSrc = A.adjustChapterHtml(htmlSrc);
                htmlSrc = A.chineseJianFanConvert(htmlSrc);
                txtViewSetText(MyHtml.fromHtml(htmlSrc, imageGetter, A.lastChapter));
                txtScrollByDelay(cPos);
                hideProgressDlg();
                return;
            }
        }
        A.lastSplitIndex = tmpLastSplitIndex;
        hideProgressDlg();
        T.showToastText(ActivityTxt.this, "\"" + url + "\" " + getString(R.string.link_not_found));
    }

    private String getExactFileInEpub(String filename) {
        if (A.ebook != null && A.ebook instanceof Epub) {
            MyZip_Base.FileInfo_In_Zip item = ((Epub) A.ebook).getFileItem(filename);
            if (item != null)
                filename = item.filename;
        }
        return filename;
    }

    private void showUrlFootnote(final String url, final String title, int cIndex) {
        MyDialog.Builder dlg = new MyDialog.Builder(ActivityTxt.this);
        handler.sendEmptyMessageDelayed(GPU_SHADOW, 50);
        dlg.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                A.setSystemUiVisibility(true);
                eraseGPUShadow(0);
            }
        });

        TextView tv = new TextView(this);
        tv.setTextSize(A.fontSize > 19 ? A.fontSize - 2 : A.fontSize);
        tv.setPadding(A.d(4), A.d(4), A.d(4), A.d(4));
        tv.setText(Html.fromHtml(urlFootnote, A.ebook.getImageGetter(), null));

        if (title.length() > 1 || (title.length() == 1 && title.charAt(0) != '\uFFFC'))
            dlg.setTitle(title);

        dlg.setView(tv)
                .setPositiveButton(R.string.open_note, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkUrlFootnote = false;
                        urlFootnote = null;
                        new Handler() {
                            public void handleMessage(Message msg) {
                                openUrlLink(url, title, 50);
                            }
                        }.sendEmptyMessageDelayed(0, 50);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(true)
                .show();
    }

    private int getTagIndexFromHtml(String html, String tag1, String tag12, String tag2, String tag22, int start) {
        int result = html.indexOf(tag1, start);
        if (result == -1)
            result = html.indexOf(tag12, start);
        if (result == -1)
            result = html.indexOf(tag2, start);
        if (result == -1)
            result = html.indexOf(tag22, start);
        return result;
    }

    public void linkBackSetVisible() {
        linkBackIv.setImageResource(R.drawable.linkback_button);
        linkBackIv.setVisibility(View.VISIBLE);
    }

    private int existTag(String aUrl, String file, String id_str, int i) {
        BaseEBook.Chapter c = A.ebook.getChapters().get(i);
        if (file.equals("") || (c.filename != null &&
                (c.filename.equals(file) || c.filename.endsWith("/" + file) || inUsedFiles(file, c)))) {
            return getCIndex(i, T.getFilename(file), id_str, aUrl, c, null, 0);
        }
        return -1;
    }

    private boolean inUsedFiles(String file, Chapter c) {
        String name = T.getFilename(file);
        for (String usedFile : c.usedFiles)
            if (usedFile.equals(name) || usedFile.endsWith("/" + name))
                return true;
        return false;
    }

    private int getCIndex(int i, String file, String id_str, String aUrl, Chapter c, String htmlContent, int tagIndex) { //v1.5.1 for id_tag in the same file: href="#10"
        int cIndex = -1;
        int tmpCIndex = i;
        String idStr = "id=\"" + id_str + "\"";
        String html;

        boolean forceReturnFileHtml = false;
        if (htmlContent == null) { //in the same fiile
            String chapterText = A.ebook.getChapterText(tmpCIndex);
            chapterText = chapterText.replace("\n\"" + id_str, "\"" + id_str);
            if (!c.additionalText.equals(BaseEBook.ADITIONAL_ERROR_TAG)) {
                html = getTagHtml(chapterText, file, idStr);
                if (html == null) {
                    idStr = "name=\"" + id_str + "\"";
                    html = getTagHtml(chapterText, file, idStr);
                }
                if (html == null && i > 0) {
                    if (!A.ebook.getChapters().get(i - 1).filename.equals(c.filename)) {
                        tmpCIndex--;
                        html = getTagHtml(chapterText, file, idStr);
                        if (html == null) {
                            idStr = "id=\"" + id_str + "\"";
                            html = getTagHtml(chapterText, file, idStr);
                        }
                    }
                }
            } else {
                html = chapterText;
                forceReturnFileHtml = true;
            }
        } else {
            html = htmlContent.replace("\n\"" + id_str, "\"" + id_str);
        }

        if (html != null) {
            int idPos = -1;
            if (forceReturnFileHtml) {//v1.9.6 out of memory error when loading huge additional content
                html = A.ebook.getSingleFileText(file);
                idPos = html.indexOf(idStr);
                cPos = 0;
                if (idPos == -1)
                    return -100;
            } else if (htmlContent == null) {
                int fileIndex = html.indexOf("<a name=" + file + ">");//v1.3.13 fix incorrect link by file tag
                if (fileIndex != -1)
                    idPos = html.indexOf(idStr, fileIndex);
                if (idPos == -1)
                    idPos = html.indexOf(idStr);
            } else
                idPos = tagIndex;

            if (checkUrlFootnote && isUrlLikeFootnote(html, idStr, idPos)) //v2.2.2
                return i;

            cIndex = i;
            if (tmpCIndex == i) {
                try {
//					if (html.indexOf(MyHtml.PAGE_BREAK) != -1)
//						A.moveStart = true; //todo: don't hide page content which may owned to this link

                    idPos = html.lastIndexOf("<", idPos);
                    Spanned spanned = MyHtml.fromHtml(html.substring(0, idPos), imageGetter,
                            htmlContent == null ? i : A.lastChapter);
                    cPos = spanned.length() + 1;

                    if (forceReturnFileHtml)
                        return -100;
                } catch (Throwable e) {
                    A.error(e);
                }
            }
        }
        return cIndex; // -100, ignore next procedure, get single file text directly
    }

    private String footnoteStr;

    private boolean isEpub3Footnote(String url, String title) {
        if (url.contains("#"))
            url = url.substring(url.indexOf("#"));
        if (!url.startsWith("#") || htmlSrc.indexOf("\"noteref\"") == -1)
            return false;

        footnoteStr = null;
        String html = htmlSrc;
        if (!A.noSplitHtmls() && A.lastSplitIndex < A.splitHtmls.size() - 1)
            try {
                for (int i = A.lastSplitIndex + 1; i < A.splitHtmls.size(); i++)
                    html = html + A.splitHtmls.get(i);
            } catch (OutOfMemoryError e) {
                A.error(e);
            }

        int start = 0;
        while (true) {
            int i = html.indexOf(" href=\"" + url + "\"", start);
            if (i == -1)
                i = html.indexOf(" href = \"" + url + "\"", start);

            if (i == -1) { //../Text/Section002_p2.xhtml#note1
                i = html.indexOf(url + "\"", start);
                if (i != -1) {
                    int h = html.lastIndexOf(" href=\"", i);
                    int i1 = html.lastIndexOf("<", i);
                    if (h == -1 || h < i1 || (i - h) > 120)
                        break;
                }
            }

            if (i == -1)
                break;
            int i1 = html.lastIndexOf("<", i);
            int i2 = html.indexOf(">", i);
            if (i1 != -1 && i2 != -1 && i2 - i1 < 200) {
                String s = html.substring(i1, i2);
                if (s.indexOf("noteref") != -1) {
                    int i3 = html.indexOf(" id=\"" + url.substring(1) + "\"", i2);
                    if (i3 == -1)
                        i3 = html.indexOf(" id = \"" + url.substring(1) + "\"", i2);
                    if (i3 == -1)
                        i3 = html.indexOf(" id=\"" + url.substring(1) + "\"", start);
                    if (i3 != -1) {
                        i1 = html.lastIndexOf("<aside ", i3);
                        if (i3 - i1 < 1000) {
                            int exact = html.substring(i2 + 1, i2 + 100).replace((char) 160, ' ').indexOf(title.replace((char) 160, ' '));
                            i2 = html.indexOf("</aside>", i3);
                            if (i2 != -1) {

                                int j;
                                for (j = url.length() - 1; j > 0; j--)
                                    if (!T.charIsNumber(url.charAt(j)))
                                        break;
                                int j2 = html.indexOf(" id=\"" + url.substring(1, j + 1), i3 + 10);
                                if (j2 != -1 && j2 < i2) {
                                    int e = html.indexOf(" id=\"" + url.substring(1), i3 + 10);
                                    if (j2 != e) {
                                        int j3 = html.lastIndexOf("<", j2);
                                        if (j3 > i3)
                                            i2 = j3;
                                    }
                                }

                                i1 = html.indexOf(">", i3);
                                if (footnoteStr == null)
                                    footnoteStr = html.substring(i1 + 1, i2);
                                start = i2;
                                if (exact != -1 && exact < 10) {
                                    footnoteStr = html.substring(i1 + 1, i2);
                                    break;
                                }

                            }
                        }
                    }
                }
            }
            if (start < i)
                start = i + 100;
        }

        if (footnoteStr != null) {
            hideProgressDlg();
            footnoteStr = footnoteStr.trim().replace("\u200F^", "^"); //todo temp solution, ^ is RTL char
            footnoteStr = (!"￼".equals(title) ? "<h3>" + title + "</h3>" : "") + footnoteStr;
            showEpub3Footnote();
            return true;
        } else
            return false;
    }

    private Dialog footnoteDlg;
    private MyDialog.Builder footnoteDlg2;

    private void showEpub3Footnote() {
        getTouchTxtView().invalidate();

        if (footnoteStr.indexOf("<ruby") != -1) {
            WebView wb = new WebView(this);
            int c = A.backgroundColor;
            if (A.useBackgroundImage && A.savedBackgroundDrwable != null)
                c = T.getDrawableAboutColor(A.savedBackgroundDrwable);
            String background = "background-color:" + T.color2Html(c) + ";";
            String css = !A.isNightState() ? "" : MRBookView.CSS
                    .replace("%BACKGROUND", background).replace("%COLOR", T.color2Html(A.fontColor)).replace("%LEFT", "" + 20)
                    .replace("%RIGHT", "" + 0).replace("%TOP", "" + 0).replace("%BOTTOM", "" + 0);
            wb.loadDataWithBaseURL("", "<html>" + css + "<body>" + footnoteStr + "<body></html>", "text/html", "UTF-8", null);
            new MyDialog.Builder(this).setView(wb)
                    .setPositiveButton(R.string.ok, null)
                    .show();
            return;
        }

        CharSequence text = Html.fromHtml(footnoteStr, A.ebook.getImageGetter(), null);
        text = A.deleteLineBreaks(text);
        TextView tv = new TextView(this);
        tv.setId(R.id.childId);
        tv.setText(text);
        tv.setTextSize(A.fontSize);
        tv.setPadding(A.d(28), A.d(28), A.d(28), A.d(28));
        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new LayoutParams(-1, -2));
        A.setTextViewWithTheme(tv);

        if (A.useBackgroundImage && A.savedBackgroundDrwable != null) {
            int c = T.getDrawableAboutColor(A.savedBackgroundDrwable);
            tv.setBackgroundColor(c);
            sv.setBackgroundColor(c);
        } else {
            A.setBackgroundImage(tv);
            A.setBackgroundImage(sv);
        }
        sv.addView(tv, new LayoutParams(-1, -2));

        handler.sendEmptyMessageDelayed(GPU_SHADOW, 50);
        footnoteDlg = new Dialog(this, R.style.dialog_fullscreen);
        footnoteDlg.setContentView(sv);
        footnoteDlg.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                A.setSystemUiVisibility(true);
                eraseGPUShadow(0);
            }
        });
        footnoteDlg.show();

        if (A.isMultiWindow())
            sv.setVerticalScrollBarEnabled(false);
        sv.setOnTouchListener(new OnTouchListener() {
            long downTime;
            float downX, downY;

            public boolean onTouch(View v, MotionEvent event) {
                try {
                    A.log("*action:" + event.getAction());
                    ScrollView sv = (ScrollView) v;
                    if (sv.getScrollY() > 0)
                        eraseGPUShadow(0);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        downTime = SystemClock.elapsedRealtime();
                        downX = event.getX();
                        downY = event.getY();
                        if (A.isMultiWindow())
                            eraseGPUShadow(2000);
                        footnoteLongTimeTapEvent = true;
                        handler.removeMessages(FOOTNOTE_LONG_TIME_TAP_EVENT);
                        handler.sendMessageDelayed(handler.obtainMessage(FOOTNOTE_LONG_TIME_TAP_EVENT, 1, 1), 400);
                    }
                    if (event.getAction() == MotionEvent.ACTION_MOVE)
                        if ((Math.abs(event.getY() - downY) > A.d(10)) || (Math.abs(getTouchX(event) - downX) > A.d(10)))
                            footnoteLongTimeTapEvent = false;
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        handler.sendEmptyMessageDelayed(GPU_SHADOW, 3000);
                        if (SystemClock.elapsedRealtime() - downTime > 400)
                            return false;
                        footnoteLongTimeTapEvent = false;
                        TextView t = (TextView) v.findViewById(R.id.childId);
                        Layout lo = t.getLayout();
                        int hx = (int) event.getX() - t.getPaddingLeft();
                        int hy = (int) event.getY() + sv.getScrollY() - t.getPaddingTop();
                        int line = lo.getLineForVertical(hy);
                        int offset = lo.getOffsetForHorizontal(line, hx);
                        int j1 = lo.getLineStart(line);
                        int j2 = lo.getLineEnd(line);
                        if (offset >= j1 && offset < j2 && j1 != j2) {
                            Spanned spanStr = (Spanned) t.getText();
                            if (j1 < offset) j1 = offset - 1;
                            if (j2 > offset) j2 = offset + 1;
                            Object[] spans = spanStr.getSpans(j1, j2, Object.class);
                            for (Object o : spans) {
                                if (o instanceof URLSpan || o instanceof MyUrlSpan) {
                                    String url = o instanceof URLSpan ? ((URLSpan) o).getURL() : ((MyUrlSpan) o).getURL();
                                    String title = "";
                                    int i = footnoteStr.indexOf("<a href=\"" + url + "\">");
                                    if (i != -1) {
                                        i = footnoteStr.indexOf(">", i);
                                        title = footnoteStr.substring(i + 1, footnoteStr.indexOf("<", i + 1));
                                    }
                                    A.log("title:#" + title + "#url:" + url);

                                    if (footnoteDlg != null)
                                        footnoteDlg.dismiss();
                                    footnoteDlg = null;
                                    if (footnoteDlg2 != null)
                                        footnoteDlg2.dismiss();
                                    footnoteDlg2 = null;

                                    if (A.isHttpUrl(url)) {
                                        T.openUrl(ActivityTxt.this, url);
                                    } else {
                                        if (url.startsWith("#"))
                                            eraseGPUShadow(50);
                                        if (url.startsWith("?") && A.ebook != null) {//fb2
                                            String tag = url.substring(1, url.length());
                                            BaseEBook.FootNote fn = A.ebook.getFootNote(tag);
                                            if (fn == null && tag.startsWith("?"))
                                                fn = A.ebook.getFootNote(tag.substring(1));
                                            if (fn != null) {
                                                showFB2Footnote(fn);
                                                return false;
                                            }
                                        }
                                        openUrlLink(url, title, 50);
                                    }
                                    break;
                                }
                            }
                            A.log("text click: " + t.getText().toString().substring(j1, j2) + "#" +
                                    t.getText().toString().substring(offset - 4, offset + 4));
                        }
                    }
                } catch (Exception e) {
                    A.error(e);
                }
                return false;
            }
        });
    }

    protected void copyFootnoteToClipboard(Message msg) {
        String s = msg.arg1 == 1 ? footnoteStr : urlFootnote;
        if (msg.obj != null && msg.obj instanceof FootNote)
            s = ((FootNote) msg.obj).html;
        if (s == null)
            return;
        if (s.startsWith("<h3>"))
            s = s.substring(s.indexOf("</h3>") + 5);
        s = Html.fromHtml(s).toString();
        while (s.indexOf("\n\n") != -1)
            s = s.replace("\n\n", "\n");
        while (s.length() > 0 && s.charAt(0) == '\n')
            s = s.substring(1);
        while (s.length() > 0 && s.charAt(s.length() - 1) == '\n')
            s = s.substring(0, s.length() - 1);
        copyToClipboard(s);
    }

    private String urlFootnote;
    private boolean checkUrlFootnote, isFootnoteEpub;

    private boolean isUrlLikeFootnote(String html, String idStr, int idPos) {
        urlFootnote = null;
        if (html == null || idStr == null)
            return false;

        String idStr2 = idStr.endsWith("\"") ? idStr.substring(0, idStr.length() - 1) : idStr;
        String tag = null;
        for (int i = idStr2.length() - 1; i > 3; i--) { //id="citationsource5"
            char c = idStr2.charAt(i);
            if (i == idStr2.length() - 1) {
                if (!T.charIsNumber(c))
                    return false;
            } else if (!T.charIsNumber(c)) {
                tag = idStr2.substring(0, i + 1);
                break;
            }
        }

        if (tag != null) {
            boolean ok = true;
            if (!isFootnoteEpub) {
                int count = 0;
                int start = 0;
                while (true) {
                    int i = html.indexOf(tag, start);
                    if (i != -1) {
                        count++;
                        if (count == 2)
                            break;
                        start = i + tag.length();
                    } else
                        break;
                }
                ok = count >= 2; //condition (1): at least 2 related id tag
            }

            int i1 = html.indexOf(idStr, idPos);
            if (i1 == -1)
                return false;
            if (!ok && html.length() - i1 > 1500)
                return false;

            int i2 = html.indexOf(tag, i1 + idStr.length());
            if (i2 == -1)
                i2 = html.length();
            if (i2 - i1 > 2000) {
                int i3 = html.indexOf("id=\"", i1 + idStr.length());
                if (i3 != -1 && i3 < i2)
                    i2 = i3;
            }
            if (i2 - i1 < (isFootnoteEpub ? 10000 : 2000)) { //condition (2): foot note text not so long
                i1 = html.lastIndexOf("<", i1);
                char c = i1 == 0 ? ' ' : html.charAt(i1 - 1);
                if (c == '(' || c == '{' || c == '[') {
                    i1--;
                } else {
                    int i3 = html.lastIndexOf("(", i1);
                    if (i3 != -1 && i1 - i3 < 50)
                        if (T.html2Text(html.substring(i3, i1)).length() < 10)
                            i1 = i3;
                }

                if (tag.indexOf("<") == -1)
                    i2 = html.lastIndexOf("<", i2);
                int i3 = html.lastIndexOf("<a href", i2);
                if (i3 > i1 + 10 && i2 - i3 < 100)
                    if (T.html2Text(html.substring(i3, i2)).length() < 30)
                        if (T.html2Text(html.substring(i1, i3)).length() > 0)
                            i2 = i3;

                String s = html.substring(i1, i2);
                while (true) {
                    if (s.length() == 0)
                        return false;
                    c = s.charAt(s.length() - 1);
                    if (c == ' ' || c == '—' || c == '(' || c == '{' || c == '[')
                        s = s.substring(0, s.length() - 1);
                    else
                        break;
                }
                i3 = s.lastIndexOf("(");
                if (i3 != -1 && s.length() - i3 < 50)
                    if (T.html2Text(s.substring(i3)).length() < 10)
                        s = s.substring(0, i3);
                if (T.html2Text(s).trim().replace("" + (char) 160, "").length() == 0)
                    return false;

                urlFootnote = s;
                isFootnoteEpub = true;
                return true;
            }
        }
        return false;
    }

    int cPos = 0;

    private String getTagHtml(String html, String file, String idStr) {
        int idPos = -1;
        int fileIndex = html.indexOf("<a name=" + file + ">");//v1.3.13 fix incorrect link by file tag
        if (fileIndex != -1)
            idPos = html.indexOf(idStr, fileIndex);
        if (idPos == -1)
            idPos = html.indexOf(idStr);

        if (html != null && idStr != null && idPos != -1) {
            htmlSrc = A.adjustChapterHtml(html);
            if (!A.noSplitHtmls()) {
                int start = 0, l = 0;
                if (fileIndex != -1) {
                    for (int i = 0; i < A.splitHtmls.size(); i++) {
                        l += A.splitHtmls.get(i).length();
                        if (l > idPos) {
                            start = i;
                            break;
                        }
                    }
                }

                for (int i = start; i < A.splitHtmls.size(); i++)
                    if (A.splitHtmls.get(i).indexOf(idStr) != -1) {
                        A.lastSplitIndex = i;
                        return A.splitHtmls.get(i);
                    }
            } else
                A.lastSplitIndex = 0;
            return htmlSrc;
        }

        return null;
    }

    private boolean openHtmlFileWithTag(final String tagUrl, boolean reload) {
        if (reload)
            htmlSrc = A.adjustChapterHtml(htmlSrc);

        if (A.noSplitHtmls()) {
            if (openHtmlFileWithTag2(htmlSrc, 0, tagUrl))
                return true;
        } else {
            for (int i = 0; i < A.splitHtmls.size(); i++)
                if (openHtmlFileWithTag2(A.splitHtmls.get(i), i, tagUrl))
                    return true;
        }

        if (reload)
            txtViewSetText(MyHtml.fromHtml(htmlSrc, imageGetter, -1));

        return false;
    }

    private boolean openHtmlFileWithTag2(String html, int splitIndex, String tagUrl) {
        String tag = "@@" + tagUrl;
        String tagStr = "(<a href=\"" + tag + "\">#</a>)";

        String tagStr2 = "<a name=\"" + tagUrl + "\"></a>";
        if (html.indexOf(tagStr2) == -1) {
            tagStr2 = "<a name='" + tagUrl + "'></a>";
            if (html.indexOf(tagStr2) == -1) {
                tagStr2 = "<a name=" + tagUrl + "></a>";
                if (html.indexOf(tagStr2) == -1) {
                    tagStr2 = "<a name=\"" + tagUrl + "\">";
                    if (html.indexOf(tagStr2) == -1) {
                        tagStr2 = "<a name=" + tagUrl + ">";
                        if (html.indexOf(tagStr2) == -1) {
                            tagStr2 = "<a id='" + tagUrl + "'></a>";
                            if (html.indexOf(tagStr2) == -1) {
                                tagStr2 = "<a id=\"" + tagUrl + "\"></a>";
                                if (html.indexOf(tagStr2) == -1) {
                                    tagStr2 = "<a id=" + tagUrl + "></a>";
                                    if (html.indexOf(tagStr2) == -1) {
                                        tagStr2 = "<a id=\"" + tagUrl + "\">";
                                        if (html.indexOf(tagStr2) == -1) {
                                            tagStr2 = "<a id=" + tagUrl + ">";
                                            if (html.indexOf(tagStr2) == -1)
                                                return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        html = html.replace(tagStr2, tagStr);
        Spanned spanned = MyHtml.fromHtml(html, imageGetter, -1);
        MyUrlSpan[] spans = spanned.getSpans(0, spanned.length(), MyUrlSpan.class);
        for (MyUrlSpan o : spans) {
            if (o.getURL().equals(tag)) {
                htmlSrc = html;
                txtViewSetText(spanned);
                A.lastSplitIndex = splitIndex;
                txtFileFinalScrollAtStartup(spanned.getSpanStart(o));
                return true;
            }
        }
        return false;
    }

    ////--------------------------------------------------------------------------

    private boolean isNoteHighlightClick(MotionEvent event) {
        if (isPdfNoflow())
            return false;
        if (A.getHighlights().size() == 0)
            return false;
        if (getTouchX(event) > contentLay.getWidth() - txtScroll.getPaddingRight())
            return false;

        ScrollView sv = A.touchingView == txtScroll2 ? txtScroll2 : txtScroll;
        MRTextView tv = getTouchTxtView();

        int y = sv.getScrollY();
        int pl = sv.getPaddingLeft();
        int pt = sv.getPaddingTop();
        float hx = event.getX() - pl;
        int hy = (int) event.getY() + y - pt;
        MyLayout lo = tv.getLayout();
        if (lo == null)
            return false;
        int line = lo.getLineForVertical(hy);
        int offset = tv.getLineOffset(line, hx);
        if (offset == -1)
            return false;

        if (offset < tv.getText().length() && tv.getText().charAt(offset) == '\n')
            return false;

        boolean isTxt = getBookType() == A.FILE_TXT;

        long A1 = offset;
        if (isTxt) {
            A1 = A.getTxtRealPos(offset);
            if (A1 == -1)
                return false; //error to count the real position
        }

        preNoteInfo = A.getPreHighlight(offset);
        if (preNoteInfo != null) {
            if (A.touchingView == txtScroll2 && txtView2.getVisibility() != View.VISIBLE)
                return false;
            boolean noteClick = false;
            int start = (int) (getBookType() == A.FILE_TXT ? offset - (A1 - preNoteInfo.lastPosition) : preNoteInfo.lastPosition);
            if (preNoteInfo.note.length() > 0) {
                float startX = tv.getTextX(line, start);
                if (Math.abs(startX - hx) < A.d(26)) {
                    noteClick = true;
                    do_edit_note(preNoteInfo);
                }
            }
            if (!noteClick) {
                highlightText(true, start, start + preNoteInfo.highlightLength);
            }
            return true;
        }

        return false;
    }

    private boolean swipeGuesture;

    private boolean do_Gesture_Event(MotionEvent event) {
        if (web != null)
            return false;
        if (dot1 != null && dot1.getVisibility() == View.VISIBLE)
            return false;

        int w = A.getScreenWidth2();
        int destLength = w / 2 < 400 ? w / 2 : 400;
        if (Math.abs(event.getY() - pressDownY) < 120) {
            //(1)swipe left to right
            if (getTouchX(event) - pressDownX > destLength) {
                if (doHorizontalSwipe(true))
                    return true;
            }
            //(2)swipe right to left
            if (getTouchX(event) - pressDownX < -destLength) {
                if (doHorizontalSwipe(false))
                    return true;
            }
        }

        w = baseFrame.getHeight();
        destLength = w / 2 < 400 ? w / 2 : 400;
        if (Math.abs(getTouchX(event) - pressDownX) < 120) {
            //(3)swipe top to bottom
            if (event.getY() - pressDownY > destLength) {
                if (A.doSwipeTopToBottom != A.DO_NONE) {
                    xFlingTime = 1;
                    swipeGuesture = true;
                }
                if (doEvent(A.doSwipeTopToBottom))
                    return true;
            }
            //(4)swipe bottom to top
            if (event.getY() - pressDownY < -destLength) {
                if (A.doSwipeBottomToTop != A.DO_NONE) {
                    xFlingTime = 1;
                    swipeGuesture = true;
                }
                if (doEvent(A.doSwipeBottomToTop))
                    return true;
            }
        }

        return false;
    }

    private float pressDownY2 = 0;
    private float pressDownX2 = 0;
    private boolean pressDown2 = false;

    @Override
    // OnTouchEvent和OnTouch是不同的 //z好像要加入这个, 要不txt为空时OnTouch事件没有触发???
    public boolean onTouchEvent(MotionEvent event) {
        try {

            if (multiTouchForFontSize(event))
                return true;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressDownX2 = getTouchX(event);
                    pressDownY2 = event.getY();
                    pressDown2 = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if ((Math.abs(event.getY() - pressDownY2) > 10) || (Math.abs(getTouchX(event) - pressDownX2) > 10)) {
                        longTimeTapEvent = false;
                        pressDown2 = false;
                        hideDotViews();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (dotVisible())
                        break;
                    if (pressDown2)
                        do_TapUp_Event(event);
                    pressDown2 = false;
                    break;
            }

        } catch (Exception e) {
            A.error(e);
            return true;
        }

        return true;
    }

    private int mTouchTimes = 0;
    long pressTime = -1;

    private GestureDetector mGestureDetector;
    //	MotionEvent touchEvent;
    VelocityTracker mVT;
    int mMaximumVelocity;
    float mDeceleration;
    long xFlingTime = 1, yFlingTime = 0;
    private boolean flingInited = false;

    private void initFling() {
        if (flingInited)
            return;
        flingInited = true;

        mGestureDetector = new GestureDetector(new MySimpleGesture());
        if (mVT == null)
            mVT = VelocityTracker.obtain();
        mMaximumVelocity = ViewConfiguration.get(ActivityTxt.this).getScaledMaximumFlingVelocity();
        float ppi = this.getResources().getDisplayMetrics().density * 160.0f; //mDeceleration参考代码从Scroller.pas来的
        mDeceleration = SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.37f // inch/meter
                * ppi // pixels per inch
                * ViewConfiguration.getScrollFriction();
    }

    private boolean tmpHorizaontalScroll = false;

    private boolean acceptHorizontalFling(VelocityTracker vt, float velocityX, float velocityY) {
        if (!A.allow_scroll_horizontally)
            return false;
        if (swipeGuesture)
            return false;

        float x = Math.abs(mVT.getXVelocity());
        float y = Math.abs(mVT.getYVelocity());
        if (x == 0)
            x = Math.abs(velocityX);
        if (y == 0)
            y = Math.abs(velocityY);

        float xMove = Math.abs(getTouchX(hMotionEvent) - pressDownX);
        float yMove = Math.abs(hMotionEvent.getY() - pressDownY);
        if (A.isFlipVerticalShift()) {
            if (isMiddleTap(hDownEvent))
                return false;
            if (pagingByMove)
                return true;
            if (y > 600 || x > 600) {
                if (xMove <= yMove && xMove < A.d(40)) {
                    xFlingTime = SystemClock.elapsedRealtime();
                    flingPageScroll(velocityY < 0, 0);
                } else if (xMove > yMove && yMove < A.d(40)) {
                    xFlingTime = SystemClock.elapsedRealtime();
                    flingPageScroll(velocityX < 0, 0);
                }
                return true;
            }
        } else {
            if (x > 600 && x > y) {
                if (pagingByMove)
                    return true;
                if (xMove >= yMove && yMove < A.d(40)) {
                    xFlingTime = SystemClock.elapsedRealtime();
                    flingPageScroll(velocityX < 0, 0);
                }
                return true;
            }
        }

        return false;
    }

    float forceTiltSpeed;

    public void flingPageScroll(boolean pageDown, float tiltSpeed) {
        if (web != null) {
            if (pageDown)
                webViewPageDown();
            else
                webViewPageUp();
            return;
        }
        forceTiltSpeed = tiltSpeed;
        if (tiltSpeed == 0) {
            tmpHorizaontalScroll = true;
        }
        if (SystemClock.elapsedRealtime() - pageScrollTime > 500)
            pageScroll(pageDown ? PAGE_DOWN : PAGE_UP);
    }

    class MySimpleGesture extends SimpleOnGestureListener {
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (inPreShow)
                return false;
            if (isPdf())
                return false;
            if (dotVisible())
                return false;
            if (A.isSpeaking || A.isInAutoScroll)
                return false;

            if (A.touchingView instanceof ScrollView)
                try {
                    mVT.addMovement(hMotionEvent); //必须要加上这个mVT.addMovement(e1)才能生效, 计算是基于2个event的?
                    mVT.addMovement(e1);
                    mVT.computeCurrentVelocity(1000, mMaximumVelocity);

                    if (acceptHorizontalFling(mVT, velocityX, velocityY))
                        return true;
                    if (isDisableMove())
                        return true;
                    if (isDisableMovedFlips() && !isMiddleTap(e1))
                        return true;

                    currentPage = -1;
                    longTimeTapEvent = false;
                    hideDotViews();

                    if (getBookType() != A.FILE_TXT) {
                        updateProgressStatus();
                        return false; //use default action
                    }

                    double flingY = mVT.getYVelocity();
                    if (flingY == 0) //android 3.0+
                        flingY = velocityY;

                    int distance = (int) ((flingY * flingY) / (2 * mDeceleration));
                    if (Math.abs(flingY) < 5)
                        return false; //忽略小于5的fling

                    int y = txtScroll.getScrollY();
                    int h = txtView.getRealHeight();
                    int lh = txtView.getLineHeight();

                    MyLayout layout = txtView.getLayout();
                    if (layout == null)
                        return false;

                    if (distance >= h / 2) {// *2/3 distance大于两倍block长度的fling, 修改为最大只能滚动两个block的量; 要扩大最长滚动距离, 加大A.fixedBlockLength缺省值即可
                        distance = h / 2;
                        double newFling = Math.sqrt(distance * 2 * mDeceleration);
                        flingY = (flingY > 0) ? newFling : -newFling;
                    }

                    if ((flingY > 0) && (A.lastBlockIndex > 2)) { //fling up  - move up
                        if (y - flingY < lh) { //fling后剩余空间小于一行,开始重新加载
                            int curLine = layout.getLineForVertical(y);
                            int curPos = layout.getLineStart(curLine);

                            int i = A.lastBlockIndex;
                            int l0 = A.getTxts2(i - 1).length();
                            int newPos = (curPos > l0) ? (curPos - l0) : curPos;
                            if (curPos < l0)
                                i--;

                            String text = A.chineseJianFanConvert(A.getTxts2(i - 2) + A.getTxts2(i - 1) + A.getTxts2(i));
                            txtViewSetText(text);
                            newPos = A.getTxts2(i - 2).length() + A.getTxts2(i - 1).length() + newPos - 5;
                            int line = txtView.getLayout().getLineForOffset(newPos);
                            int ny = txtView.getLineTop2(line);
                            txtScrollTo(ny);
                            A.lastBlockIndex = i - 1;
                        }
                    } else { //fling down   -- move down
                        if ((flingY < 0) && (A.lastBlockIndex < A.getTxts().size() - 2) && (A.lastBlockIndex > 0)) {
                            if (y - flingY > h - lh) { //fling后剩余空间小于一行,开始重新加载
                                flingY = flingY * 9 / 10;//flingY *= 9/10; this must be 0 (9/10=0)
                                int curLine = layout.getLineForVertical(y);
                                int curPos = layout.getLineStart(curLine);

                                int i = A.lastBlockIndex;
                                int l0 = A.getTxts2(i - 1).length();
                                int l1 = A.getTxts2(i).length();
                                int newPos = (curPos > l0 + l1) ? (curPos - l0 - l1) : (curPos - l0);
                                if (curPos > l0 + l1)
                                    i++;

                                String text = A.chineseJianFanConvert(A.getTxts2(i) + A.getTxts2(i + 1) + A.getTxts2(i + 2));
                                txtViewSetText(text);
                                int line = txtView.getLayout().getLineForOffset(newPos + 5);
                                int ny = txtView.getLineTop2(line);
                                txtScrollTo(ny);
                                A.lastBlockIndex = i + 1;
                            }
                        }
                    }

                    yFlingTime = SystemClock.elapsedRealtime();
                    txtScroll.fling(-(int) flingY);
                    int delay = (int) Math.abs(velocityY);
                    if (delay > 1500 && !showStatusbar())
                        handler.sendEmptyMessageDelayed(SHOW_SCROLLBAR, 3200 * delay / (delay + 2500));
                    updateProgressStatus();
                    return true;

                } catch (Exception e) {
                    A.error(e);
                }
            return false;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }
    }

    private GestureDetector mCurlDetector;
    private float mCurlFlingDirection;

    class MyCurlGesture extends SimpleOnGestureListener {
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mCurlFlingDirection = velocityX;//>0? PAGE_UP : PAGE_DOWN;
            return false;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }
    }

    private boolean isPressScreenStateButton = false;

    public void onClick(View v) {
        if (disableFunctionsInWebView(v))
            return;

        if (v == downloadIv) {
            do_download_chapters();
        }

        if (v == urlTv) {
            T.openUrl(this, A.ebook.getChapters().get(A.lastChapter).url);
        }

        if (v == siteTv) {
//			T.showAlertText(this, "已提交投诉", A.getAssetFileText("投诉指引.txt"));
            do_switch_source();
        }

        if (v == backLay)
            do_back_to_book_stack();

        if (v == titleTextView)
            do_book_info();

        if (v == onlineSwitch)
            switchWebReading(!inWebReading);

        if (v == percentView)
            do_go_percent();

        if (v == statusLeftPanel)
            doEvent(A.statusClickLeft);

        if (v == statusMiddle || v == statusMiddle21 || v == statusMiddle22)
            doEvent(A.statusClickMiddle);

        if (v == statusRight)
            doEvent(A.statusClickRight);

        //-------------------------
        if (v == b_orientation) {
            doScreenOrientation();
        }

        if (v == b_daynight) {
            if (!A.mainNightTheme && askEnableAutoDay())
                return;
            doDayNight();
        }

        if (v == b_storeUrl) {
            switchToStoreUrl();
        }

        if (v == b_toShelf) {
            do_add_favorite();
            inverseLayoutVisible(true);
        }

        if (v == b_read) {
            z.curChapterText = txtView.getText2();
            z.showShuPing(this, getBookName(), getAuthor(), getCurChapter(), A.getBookThumbFile(A.lastFile));
        }

        if (v == b_write) {
            z.curChapterText = txtView.getText2();
            z.addSubShuPing(this, getBookName(), getAuthor(), getCurChapter(), A.getBookThumbFile(A.lastFile));
        }

        if (v == daynightLay) {
            inverseLayoutVisible(true);
        }

        if (v == b_chapter) {
            inverseLayoutVisible(true);
            do_show_chapters(0);
        }

        if (v == chromeIv) {
            doChromeButton(false);
        }

        if (v == b_brightness) {
            do_brightness();
        }

        if (v == b_speak) {
            do_speak();
        }

        if (v == b_autoscroll) {
            do_AutoScrollAsk();
        }

        if (v == b_bookmark) {
            do_bookmark();
        }

        if (v == menuB) {
//			showOptionsMenu(menuB);
            doMenuB();
        }

        if (v == b_search) {
            do_search();
        }

        if (v == b_fontsize) {
            do_font_size();
        }
        if (v == b_tilt) {
            do_tilt();
        }

        if (v == b_option) {
//			do_options_menu(v);
            do_bottom_more();
        }

        if (v == linkBackIv) {
            doLinkBack();
        }
        if (v == pdfPenIv) {
            inverseLayoutVisible(true);
            pdfShowAnnotLay();
        }

        if (v == nav_return) {
            navigateBackLinks();
            inverseLayoutVisible(true);
        }

        if (v == ttsIv) {
            if (tts_panel == null)
                initTtsPanel();
            showTtsPanel(true);
        }

        if (v == tips_ok)
            hideTipsPanel();

        if (v == search_list) {
            showPrefSearch(null);
        }

        if (v == search_close) {
            hideSearchPanel();
        }

        if (v == search_prior) {
            if (!T.isNull(PrefSearch.results) && PrefSearch.lastClickIndex > 0) {
                PrefSearch.lastClickIndex--;
                doPrefSearchResult(PrefSearch.lastClickIndex, false);
            }
        }

        if (v == search_next) {
            if (!T.isNull(PrefSearch.results) && PrefSearch.lastClickIndex < PrefSearch.results.size() - 1) {
                PrefSearch.lastClickIndex++;
                doPrefSearchResult(PrefSearch.lastClickIndex, false);
            }
        }

        if (v == search_return) {
            doLinkBack();
            hideSearchPanel();
        }
    }

    boolean startWithWebReading, youHuaReadingTipped;

    private void doMenuB() {
        CharSequence[] items = isWebBook() ?
                new CharSequence[]{getString(R.string.huanyuan), getString(R.string.jinghua), getString(R.string.tihuan), getString(R.string.book_info), A.fanti("分享本书")} : A.ebook == null ?
                new CharSequence[]{getString(R.string.jinghua), getString(R.string.tihuan), getString(R.string.text_encoding), getString(R.string.book_info), A.fanti("分享本书")} :
                new CharSequence[]{getString(R.string.jinghua), getString(R.string.tihuan), getString(R.string.book_info), A.fanti("分享本书")};

        int[] colors = new int[items.length];
        for (int i = 0; i < colors.length; i++)
            colors[i] = A.getAlphaColor(bottomBackgroundColor, A.mainNightTheme ? -30 : -15);
        for (int i = 0; i < items.length; i++)
            items[i] = Html.fromHtml("<font color=\"#dddddd\">" + items[i].toString() + "</font>");

        new MyDialog(this, menuB, items, null, 0, new MyDialog.MenuItemClick() {
            public void onClick(int which) {
                inverseLayoutVisible(true);
                if (isWebBook()) {
                    if (which == 0)
                        do_switch_source();
                    if (which == 1)
                        WB.set_purify(ActivityTxt.this, txtView.getText2(), A.ebook.getChapters().get(A.lastChapter).url);
                    if (which == 2)
                        WB.set_replacement(ActivityTxt.this);
                    if (which == 3)
                        do_book_info();
                    if (which == 4)
                        doShareReadState(false);
                } else if (A.ebook != null) {
                    if (which == 0)
                        WB.set_purify(ActivityTxt.this, txtView.getText2(), null);
                    if (which == 1)
                        WB.set_replacement(ActivityTxt.this);
                    if (which == 2)
                        do_book_info();
                    if (which == 3)
                        doShareReadState(false);
                } else { //txt, html
                    if (which == 0)
                        WB.set_purify(ActivityTxt.this, txtView.getText2(), null);
                    if (which == 1)
                        WB.set_replacement(ActivityTxt.this);
                    if (which == 2)
                        selectTextEncode();
                    if (which == 3)
                        do_book_info();
                    if (which == 4)
                        doShareReadState(false);
                }

            }
        }, colors, null, true).show(A.d(40), -A.d(40));
    }

    boolean inWebReading;

    private void switchWebReading(boolean useWeb) {
        A.forceYouHua = false;
        inWebReading = useWeb;
        if (inWebReading) {
            showWebReading();
            txtViewSetText("");
        } else {
            hideWebReading();
            hideProgressDlg();
            preNextChapterText = oldPriorChapterText = null;
            reloadBook();
        }
        inverseLayoutVisible(true);
    }

    private void selectTextEncode() {
        String encode = A.fileEncoding != null ? A.fileEncoding : A.textEncode;
        int id;
        if (encode.equals("") || encode.equals(A.CHARSET_AUTO))
            id = 0;
        else for (id = 1; id < A.getTextEncodes().length; id++)
            if (A.getTextEncodes()[id].toString().equals(encode))
                break;
        final int selected = id;
        new MyDialog.Builder(this).setTitle(R.string.text_encoding).setSingleChoiceItems(A.getTextEncodes(), selected, null)
                .setPositiveButton(R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lv = ((AlertDialog) dialog).getListView();
                        int i = lv.getCheckedItemPosition();
                        if (selected != i) {
                            String encode = i == 0 ? A.CHARSET_AUTO : A.getTextEncodes()[i].toString();
                            if (A.encodingError(ActivityTxt.this, encode))
                                return;
                            A.textEncode = encode;
                            getSharedPreferences(A.ENCODING_FILE, 0).edit().putString(A.lastFile, encode).commit();
                            getSharedPreferences(A.OPTIONS_FILE, 0).edit().putString("textEncode", encode).commit();
                            refreshTxtRender();
                        }
                    }
                }).setNegativeButton(R.string.cancel, null).show();
    }

    boolean adjustedThemeAtStartup;

    void adjustNightTheme() {
        if (A.shelfThemeChanged) {
            A.shelfThemeChanged = false;
            int nightHash = A.getThemeHash(A.NIGHT_THEME);
            int curHash = A.getCurThemeHash();
            int tempHash = A.getThemeHash(A.TEMP_THEME);
            if (!A.mainNightTheme) {
                if (curHash == nightHash) {
                    if (tempHash != -1 && tempHash != nightHash)
                        loadTheme(A.TEMP_THEME, A.loadThemeWithColorOnly);
                    else
                        loadTheme(A.DAY_THEME, A.loadThemeWithColorOnly);
                    if (A.brightnessValue != -100)
                        if (A.autoThemeDayBrightness == -100 || A.brightnessValue < A.autoThemeDayBrightness)
                            setScreenBrightness(A.autoThemeDayBrightness, false);
                    adjustedThemeAtStartup = true;
                }
            } else {
                if (curHash != nightHash) {
                    if (curHash != nightHash)
                        A.saveTheme(A.TEMP_THEME, true);
                    A.autoThemeDayBrightness = A.brightnessValue;
                    if (A.autoThemeNightWithBright) {
                        A.brightnessValue = A.autoThemeNightBrightness;
                        setScreenBrightness(A.brightnessValue, false);
                    }
                    loadTheme(A.NIGHT_THEME, A.loadThemeWithColorOnly);
                    adjustedThemeAtStartup = true;
                }
            }
        }
    }

    private void doDayNight() {
        boolean oldMainNightTheme = A.mainNightTheme;
        resetFlipCache();
        if (isPdfNoflow()) {
            pdfRemoveThumb();
            A.pdf_theme = A.pdf_theme == 0 ? 1 : 0;
            pdfUpdateView(true);
        } else {
//            cycleThemes(false);
//            setLeds();

            int nightHash = A.getThemeHash(A.NIGHT_THEME);
            int tempHash = A.getThemeHash(A.TEMP_THEME);
            int curHash = A.getCurThemeHash();
            int br = A.brightnessValue;

            if (!A.mainNightTheme) {
                if (curHash != nightHash)
                    A.saveTheme(A.TEMP_THEME, true);
                A.autoThemeDayBrightness = br;
                if (A.autoThemeNightWithBright) {
                    A.brightnessValue = A.autoThemeNightBrightness;
                    setScreenBrightness(A.brightnessValue, false);
                }
                loadTheme(A.NIGHT_THEME, A.loadThemeWithColorOnly);
            } else {
                A.brightnessValue = A.autoThemeDayBrightness;
                setScreenBrightness(A.brightnessValue, false);
                if (tempHash != -1 && tempHash != nightHash)
                    loadTheme(A.TEMP_THEME, A.loadThemeWithColorOnly);
                else
                    loadTheme(A.DAY_THEME, A.loadThemeWithColorOnly);
            }

            if (web != null)
                reloadWebView();

        }
        A.setMainNightTheme(!A.mainNightTheme);
        A.txtThemeChanged = true;//oldMainNightTheme != A.mainNightTheme;

//		if (readWeb != null && A.mainNightTheme)
//			S.setNightMode(readWeb, A.mainNightTheme);
//		inverseLayoutVisible(true);
        setNightModeIndicator();
        z.notifyThemeChanged();
    }

    private boolean askEnableAutoDay() {
        if (!A.askEnableAutoDay)
            return false;
        ScrollView layout = (ScrollView) LayoutInflater.from(this).inflate(R.layout.do_event_confirm, null);
        final TextView tv = (TextView) layout.findViewById(R.id.ofTextView);
        final CheckBox cb = (CheckBox) layout.findViewById(R.id.ofNeverAskAgain);
        cb.setChecked(true);
        tv.setText(Html.fromHtml(A.fanti("切换到夜间模式后, 是否启用\"天亮后自动关闭夜间模式\"?" +
                "<br><br><small><font color=\"#888888\">(可在\"亮度调节->配置夜间模式\"重设)</font></small>")));
        new MyDialog.Builder(this)
                .setView(layout)
                .setPositiveButton(A.fanti("启用"), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        A.askEnableAutoDay = !cb.isChecked();
                        A.autoThemeDay = true;
                        doDayNight();
                    }
                }).setNegativeButton(A.fanti("不启用"), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                A.askEnableAutoDay = !cb.isChecked();
                A.autoThemeDay = false;
                doDayNight();
            }
        }).setCancelable(false).show();
        return true;
    }

    private void doScreenOrientation() {
//		if (android.os.Build.VERSION.SDK_INT == 26) {
//			T.showAlertText(this, A.fanti("请注意"),
//					A.fanti("Android8.0系统的用户，如需横竖屏切换，请在系统通知栏里把屏幕方向设置为自动旋转。"));
//			return;
//		}

        saveLastPostion(true);
        generateNextScreenOrientation();
        if (A.landscape2PageMode)
            justEnabledDualPage = true;
        restartReaderToTxt();

/*		if (A.immersive_fullscreen && !dualPageEnabled())
            handler.sendEmptyMessageDelayed(CHECK_DUAL_PAGE, 500);
		isPressScreenStateButton = true;
		saveLastPostion(true);
		generateNextScreenOrientation();
//		if (A.isFlipCurl()){// && A.immersive_fullscreen && A.sysHasNavBar()) {
//            restartReaderToTxt();
//        } else {
            setScreenOrientation();
            statusHandlerSendMessage(1, 100);
            pdfRemoveThumb();
//        }*/
    }

    private void cycleThemes(boolean saveTempThemeOnly) {
        int dayHash = A.getThemeHash(A.DAY_THEME);
        int nightHash = A.getThemeHash(A.NIGHT_THEME);
        int curHash = A.getCurThemeHash();

        if (saveTempThemeOnly) {
            if (curHash != dayHash && curHash != nightHash)
                A.saveTheme(A.TEMP_THEME, true);
            return;
        }

        if (curHash != dayHash && curHash != nightHash) {
            A.saveTheme(A.TEMP_THEME, true);
            loadTheme(A.NIGHT_THEME, A.loadThemeWithColorOnly);
        } else if (curHash == nightHash) {
            loadTheme(A.DAY_THEME, A.loadThemeWithColorOnly);
        } else if (curHash == dayHash) {
            int tempHash = A.getThemeHash(A.TEMP_THEME);
            if (tempHash != dayHash && tempHash != nightHash)
                loadTheme(A.TEMP_THEME, A.loadThemeWithColorOnly);
            else
                loadTheme(A.NIGHT_THEME, A.loadThemeWithColorOnly);
        }
        setFlipViewINVISIBLE(true);
    }

    private void do_tilt() {
        A.tilt_turn_page = !A.tilt_turn_page;
        T.showToastText(this, "\"" + getString(R.string.tilt_turn_page) + "\" "
                + getString(A.tilt_turn_page ? R.string.ok : R.string.cancel));
        if (shakeSensorLisener != null)
            shakeSensorLisener.tilt_turn_page = A.tilt_turn_page;
        registerHardwares();
        inverseLayoutVisible(true);
    }

    LINK_BACK tmpPositionInfo;

    public LINK_BACK saveLinkBackInfo(boolean addToList) {
        saveLastPostion(false);
        try {
            String title;
            if (isPdf()) {
                if (PDFReader.innerLinkRecord >= 0) {
                    A.lastPosition = PDFReader.innerLinkRecord;
//				PDFReader.innerLinkRecord = -1;
                } else
                    A.lastPosition = pdfGetCurrPageNo();
                title = getString(R.string.page) + " " + (A.lastPosition + 1);// + "/" + m_doc.GetPageCount();
            } else {
                if (txtView.getText2().length() == 0)
                    return null;
                int line = txtView.getLayout().getLineForVertical(txtScroll.getScrollY());
                int offset = txtView.getLayout().getLineStart(line);
                if (offset == 0 && isWebBook())
                    offset = A.ebook.getChapters().get(A.lastChapter).name.length() + 1;
                String bookmark = txtView.getText2().substring(offset).replace("\n", "").replace(A.INDENT_CHAR, ' ').trim() + "...";
                int max = A.isAsiaLanguage ? 15 : 20;
                if (bookmark.length() > max)
                    bookmark = bookmark.substring(0, max);
                title = (isWebBook() ? "" : "(" + getPercentStr2() + ") ") + bookmark;
            }
            for (LINK_BACK item : link_backs)
                if (item.title.equals(title)) {
                    link_backs.remove(item);
                    break;
                }
            tmpPositionInfo = new LINK_BACK(title, A.lastFile, A.lastChapter, A.lastSplitIndex, A.lastPosition);
            if (addToList) {
                link_backs.add(0, tmpPositionInfo);
                return tmpPositionInfo;
            }
        } catch (Throwable e) {
            A.error(e);
        }
        return null;
    }

    public class LINK_BACK {
        String title;
        String backFile;
        int backChapter;
        int backSplitIndex;
        long backPosition;

        public LINK_BACK(String title, String backFile, int backChapter, int backSplitIndex, long backPosition) {
            this.title = title;
            this.backFile = backFile;
            this.backChapter = backChapter;
            this.backSplitIndex = backSplitIndex;
            this.backPosition = backPosition;
        }
    }

    private ArrayList<LINK_BACK> link_backs = new ArrayList<LINK_BACK>();

    private void doLinkBack() {
        if (link_backs.size() == 0)
            return;
        LINK_BACK back = link_backs.get(0);
        link_backs.remove(0);
        linkBackIv.setVisibility(View.INVISIBLE);
        goToBackLink(back);
    }

    private void goToBackLink(LINK_BACK back) {
        if (isPdf()) {
            pdfGotoPage((int) back.backPosition, true);
        } else {
            if (getBookType() == A.FILE_HTML && !A.lastFile.equals(back.backFile))
                htmlText = "";
            A.lastFile = back.backFile;
            A.lastChapter = back.backChapter;
            A.lastSplitIndex = back.backSplitIndex;
            A.lastPosition = back.backPosition;
            if (!hasPriorChapterText(null)) {
                createProgressDlg("");
                reloadBook();
            }
        }
        updateProgressStatus();
    }

    private boolean navigateBackLinks() {
        if (link_backs.size() == 0)
            return false;
        CharSequence[] items = new CharSequence[link_backs.size()];
        for (int i = 0; i < link_backs.size(); i++) {
            if (isWebBook()) {
                String cname = A.ebook.getChapters().get(link_backs.get(i).backChapter).name.trim();
                if (cname.contains(" "))
                    cname = cname.substring(0, cname.indexOf(" "));
                String title = link_backs.get(i).title;
//				cacheUrls[i] = "#html#" + "<small><font color=\"#888888\">(" + cname + ") </font>" + title + "</small>";
                items[i] = Html.fromHtml("<small><font color=\"#888888\">(返回" + cname + ") </font></small>" + title + "");
            } else
                items[i] = link_backs.get(i).title;
        }
//		items[items.length - 2] = "-";
//		items[items.length - 1] = getString(R.string.exit);
//		int[] colors = new int[items.length];
//		colors[colors.length-1] = A.isNightState()? 0x66222222 : 0x66777777;

//		new MyDialog.Builder(this).setItems(cacheUrls, new OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				if (which == link_backs.size() + 1) {
//					if (!forceExitRestart())
//						doFinish();
//				}
//				if (which < link_backs.size()) {
//					LINK_BACK back = link_backs.get(which);
//					saveLinkBackInfo(true);
//					hideLinkBackButton();
//					goToBackLink(back);
//				}
//			}
//		}).show();

        new MyDialog(this, null, items, null, 0, new MyDialog.MenuItemClick() {
            public void onClick(int which) {
//				if (which == link_backs.size() + 1) {
//					if (!forceExitRestart())
//						doFinish();
//				}
                if (which < link_backs.size()) {
                    LINK_BACK back = link_backs.get(which);
                    saveLinkBackInfo(true);
                    hideLinkBackButton();
                    goToBackLink(back);
                }
            }
        }, null, null, false).show();

        return true;
    }

    public void hideLinkBackButton() {
        if (linkBackIv != null && linkBackIv.getVisibility() == View.VISIBLE)
            linkBackIv.setVisibility(View.INVISIBLE);
    }

    private void do_go_percent() {
        final EditText et = new EditText(this);
        //		et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        String s = isPdf() ? "" + (pdfGetCurrPageNo() + 1) : (topLay.getVisibility() == View.VISIBLE ? percentView.getText().toString()
                : getPercentStr2());
        et.setSingleLine();
        et.setText(s);

        new MyDialog.Builder(this).setTitle(isPdf() ? "1 - " + m_doc.GetPageCount() : "0.0% - 100%").setView(et)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (web == null)
                            try {
                                String s = et.getText().toString().replace("%", "");
                                if (isPdf()) {
                                    int i = Integer.valueOf(s);
                                    if (i > 0 && i <= m_doc.GetPageCount())
                                        pdfGotoPage(i, true);
                                    updateProgressStatus();
                                } else {
                                    float p = Float.valueOf(s) * 10;
                                    if (p >= 0 && p <= 1000) {
                                        showBookByPercent((int) p);
                                        updateProgressStatus();
                                    }
                                }
                                inverseLayoutVisible(true);
                            } catch (Exception e) {
                            }
                    }
                }).setNegativeButton(R.string.cancel, null).show();
    }

    //---------------tts begin------------
    private int lastEndOfSpeakText = -1;
    private int lastStartOfSpeakText = -1;
    private int speakTextLength = -1;
    private boolean speak_in_background;
    private String SPEAK_PUNCT1 = ",.;?\":(){}[]!，。；＂”“：？（）、！\n";
    private ArrayList<TtsLine> speakLines;

    private ArrayList<TtsLine> getSpeakText() {
        getSpeakHandler(); //init here, can't create handler inside speaking thread
        if (isPdfNoflow())
            return pdfGetTtsLines();

        MyLayout l = txtView.getLayout();
        if (l == null)
            return null;
        saveLastPostion(true);

//		A.log("------->text length:"+txtView.getText2().length()+" max:"+TextToSpeech.getMaxSpeechInputLength());
        int startLine = l.getLineForVertical(txtScroll.getScrollY());
        int endLine = getSpeakPageLastLine(startLine, -1);
        if (!A.backgroundTtsOption && isPaused && getBookType() != A.FILE_TXT) {
            endLine = getRealLineCount() - 1;
            preNextChapter(false);
            speak_in_background = true;
        } else
            speak_in_background = false;

        int i1 = l.getLineStart(startLine);
        int i2 = l.getLineEnd(endLine);
        int t1 = i1, t2 = i2;

        String all = txtView.getText2();
        if (isWebBook() && (isOnlineDownloadTag(A.lastChapter, all, false) || all.length() == 0))
            return null;

        try {
            int pre = -1;
            if (i1 < 10) //v1.2.13自动切换下一章后第一页没有需要接上的
                lastEndOfSpeakText = -1;

            if (lastEndOfSpeakText != -1 && lastEndOfSpeakText < i2) {
                pre = i1;
                i1 = lastEndOfSpeakText;
                if (SPEAK_PUNCT1.indexOf(all.charAt(i1)) != -1)
                    i1++;
            }

            if (i2 < all.length() - 20) //v1.2.13 最后一页不截断
                while (i2 > i1 + 15 && SPEAK_PUNCT1.indexOf(all.charAt(i2 - 1)) == -1)
                    i2--;
            lastEndOfSpeakText = i2;
            lastStartOfSpeakText = i1;
            String text = all.substring(i1, i2);
            if (pre > i1) {
                String preText = all.substring(i1, pre).trim();
                if (preText.length() > 0 && !isPaused) { //v1.3.11?
                    txtScrollNoDelay(i1);
                    int startLine2 = l.getLineForOffset(i1);
                    if (startLine2 < startLine) {
                        endLine = getSpeakPageLastLine(startLine, startLine2);
                        i2 = l.getLineEnd(endLine);
                        if (i2 < all.length() - 20) //v1.2.13 最后一页不截断
                            while (i2 > i1 + 15 && SPEAK_PUNCT1.indexOf(all.charAt(i2 - 1)) == -1)
                                i2--;
                        lastEndOfSpeakText = i2;
                        text = all.substring(i1, i2);
                    }
                }
            }

            setFlipViewINVISIBLE(true);
            speakTextLength = text.replace("\n", "").replace(" ", "").length();
            return A.getTtsLines(text, false, speak_in_background, lastStartOfSpeakText);
        } catch (Exception e) {
            T.showToastText(this, "#ERROR#" + A.errorMsg(e), 1);
            A.error(e);
            lastEndOfSpeakText = -1;
            lastStartOfSpeakText = t1;
            String text = all.substring(t1, t2);
            speakTextLength = text.replace("\n", "").replace(" ", "").length();
            return A.getTtsLines(text, false, speak_in_background, lastStartOfSpeakText);
        }
    }

    private int getSpeakPageLastLine(int startLine, int curLine) {
        int endLine = A.getLastDisplayLine(txtScroll, curLine);
        if (endLine < startLine) {
            int pageLineCount = getPageLineCount();
            endLine = pageLineCount + startLine - 1;
            if (endLine > txtView.getLineCount() - 1)
                endLine = txtView.getLineCount() - 1;
        }
        return endLine;
    }

    int stopTtsFromCalling;
    long tts_start_time;

//	class TeleListener extends PhoneStateListener {
//		@Override
//		public void onCallStateChanged(int state, String incomingNumber) {
//			A.log("calling state:" + state);
//			super.onCallStateChanged(state, incomingNumber);
//			try {
//				switch (state) {
//				case TelephonyManager.CALL_STATE_IDLE:
//					if (Build.VERSION.SDK_INT < 21 && A.getROMInfo().indexOf("htc") != -1) { //incompatible with htc?
//						return; //when tts start, it always execute this
//					} else if (stopTtsFromCalling == 1) {
//						stopTtsFromCalling = 2;
//						restore_speak();
//					}
//					break;
//				case TelephonyManager.CALL_STATE_RINGING:
//					if (SystemClock.elapsedRealtime() - tts_start_time < 1000) //v2.1.1
//						return;
//					if (Build.VERSION.SDK_INT < 21 && A.getROMInfo().indexOf("htc") != -1) {
//						if (A.isSpeaking)
//							stop_speak();
//					} else if (A.isSpeaking && stopTtsFromCalling != 1 && !tts_paused) {
//						stopTtsFromCalling = 1;
//						do_tts_buttons(tts_play, false);
//					}
//					break;
//				case TelephonyManager.CALL_STATE_OFFHOOK:
//					break;
//				}
//			} catch (Throwable e) {
//				A.error(e);
//			}
//		}
//	}

    private TextToSpeech tts;
    private String ttsTextFromClipboard;
    private OnInitListener onInitTTS = new OnInitListener() {
        public void onInit(int status) {
            A.log("*TTS onInit:" + status);
            if (status == TextToSpeech.SUCCESS) {
                tts_stopped = false;
//				try {
//					TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//					mTelephonyMgr.listen(new TeleListener(), PhoneStateListener.LISTEN_CALL_STATE);
//				} catch (Exception e) {
////					A.error(e);
//				}
                try {
                    if (ttsTextFromClipboard != null) {
                        String text = ttsTextFromClipboard;
                        ttsTextFromClipboard = null;
                        tts.setSpeechRate((float) A.tts_speed / 10);
                        tts.setPitch((float) A.tts_pitch / 10);
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                        tts_start_time = SystemClock.elapsedRealtime();
                        return;
                    }

                    if (isPdf()) {
                        if (dotVisible() && pdf.selectedText != null && pdf.selectedText.length() > 0) {
                            inverseLayoutVisible(true);
                            hideDotSelectors();
                            tts.setSpeechRate((float) A.tts_speed / 10);
                            tts.setPitch((float) A.tts_pitch / 10);
                            tts.speak(pdf.selectedText, TextToSpeech.QUEUE_FLUSH, null);
                            tts_start_time = SystemClock.elapsedRealtime();
                        } else
                            startTTSProc();
                        return;
                    }

                    MRTextView tv = txtView.hStart != -1 ? txtView : txtView2.hStart != -1 ? txtView2 : null;
//					if (tv != null && !ttsFromAdjustSpeed) {
                    if (tv != null) {
                        String selected = tv.getText().subSequence(tv.hStart, tv.hEnd).toString();
                        inverseLayoutVisible(true);
                        tts.setSpeechRate((float) A.tts_speed / 10);
                        tts.setPitch((float) A.tts_pitch / 10);
                        tts.speak(selected, TextToSpeech.QUEUE_FLUSH, null);
                        tts_start_time = SystemClock.elapsedRealtime();
                    } else if (dualPageEnabled()) {
                        A.touchingView = null;
                        A.isSpeaking = true;
                        checkDualPageMode();
                        new Handler() {
                            public void handleMessage(Message msg) { //delay proc
                                txtView.assumeLayout();
                                txtScrollNoDelay(msg.what);
                                startTTSProc();
                            }
                        }.sendEmptyMessage(txtView.getCurPosition());
                    } else
                        startTTSProc();
                } catch (Exception e) {
                    A.error(e);
                }

            } else {
                T.showAlertText(ActivityTxt.this, getString(R.string.error), getString(R.string.tts_init_failed));
            }
        }

        private void startTTSProc() {
            lastEndOfSpeakText = -1;
            speakLines = getSpeakText();
            if (speakLines != null && tts != null) {
                A.isSpeaking = true;
                registerAudioFocus();
                show_tts_icon();
                inverseLayoutVisible(true);
//				tts.setSpeechRate((float) A.tts_speed/10);
//				tts.setPitch((float) A.tts_pitch/10);
                tts_utteranceId = -1;
                setSpeakQueue(tts_utteranceId + 1);
                setStopTtsHandler();
                showTtsOptionsButton();
            } else
                A.isSpeaking = false;
        }
    };


    private int tts_utteranceId, force_utteranceId = -2, tts_screen_off_pos = -1;
    private OnUtteranceCompletedListener tts_listener;
    private UtteranceProgressListener tts_progress_listener;

    private void setSpeakOnUtteranceComplete() {
        if (Build.VERSION.SDK_INT >= 15) {
            if (tts_progress_listener == null)
                tts_progress_listener = new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        A.log("#tts_onStart: " + utteranceId);
                        if (!isPdf() || isPdfReflow()) {
                            TtsLine sl = speakLines.get(Integer.valueOf(utteranceId));
                            highlightText(lastStartOfSpeakText + sl.start, lastStartOfSpeakText + sl.end);
                        }
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        A.log("#tts_onDone: " + utteranceId);
                        doOnUtteranceComplted(utteranceId, false);
                    }

                    @Override
                    public void onError(String utteranceId) {
                        A.log("###TTS ERROR### id: " + utteranceId);
                    }
                };
            tts.setOnUtteranceProgressListener(tts_progress_listener);
        } else {
            if (tts_listener == null)
                tts_listener = new OnUtteranceCompletedListener() {
                    public void onUtteranceCompleted(String utteranceId) {
                        doOnUtteranceComplted(utteranceId, true);
                    }
                };
            if (tts != null)
                tts.setOnUtteranceCompletedListener(tts_listener);
        }
    }

    private void doOnUtteranceComplted(String utteranceId, boolean doHighlight) {
        tts_screen_off_pos = -1;
        if (utteranceId == null)
            return;
        PDFPage.delForceSel(false);
        if (!A.isSpeaking)
            return;
        if (tts_stopped) {
            A.log("-----------setSpeakOnUtteranceComplete, tts_stopped=true");
            return;
        }

        tts_utteranceId = Integer.valueOf(utteranceId);
        if (tts_utteranceId < speakLines.size() - 1) {
            TtsLine sl = speakLines.get(tts_utteranceId + 1);
            if (!A.backgroundTtsOption && isPaused && !isPdf())
                tts_screen_off_pos = lastStartOfSpeakText + sl.start;
            if (!isPdf() || isPdfReflow()) {
                A.log(tts_utteranceId + "/" + speakLines.size() + "->" + sl.s + "##");
                if (doHighlight)
                    highlightText(lastStartOfSpeakText + sl.start, lastStartOfSpeakText + sl.end);

                if (speak_in_background && !isPaused && txtView.hStart >= 0 && txtView.getLayout() != null) {
                    int line = txtView.getLayout().getLineForOffset(txtView.hStart);
                    int y1 = txtView.getLineTop3(line);
                    line = txtView.getLayout().getLineForOffset(txtView.hEnd);
                    int y2 = txtView.getLineTop3(line);
                    if (y1 < txtScroll.getScrollY() || y2 > txtScroll.getScrollY() + A.getPageHeight() - txtView.getLineHeight()) {
                        txtScrollByDelay(txtView.hStart);
                        updateProgressStatus();
                    }
                }
                if (speak_in_background && isPaused) { //v3.5.4
                    getSharedPreferences(A.POSITION_FILE, 0).edit().putString(A.lastFile.toLowerCase(),
                            A.lastChapter + "@" + A.lastSplitIndex + "#" + (lastStartOfSpeakText + sl.start)
                                    + ":" + getPercentStr2()).commit();
                }
            } else {
                pdf.selectedPara = null;
                PDFPage.setForceSel(sl.start, sl.end);
                pdf.postInvalidate();
                getSpeakHandler().sendEmptyMessageDelayed(CHECK_PDF_SPEAKING_POS, 100);
            }

        } else {
            if (speak_in_background) {
                tts_screen_off_pos = -1;
                handler.sendMessage(handler.obtainMessage(PAGE_DOWN_TO_NEXT_CHAPTER,
                        txtView.getLineTop2(txtView.getLineCount()), 0));
            } else
                speakNextPage();
        }
    }

    private void setSpeakQueue(int fromId) {
        try {
            audio_pause_from_listener = false;
            if (speakLines == null || tts == null)
                return;
            if (force_utteranceId != -2) {
                fromId = force_utteranceId + 1;
                force_utteranceId = -2;
            }

            A.log("tts_utteranceId 2*:" + tts_utteranceId + "/" + fromId + " size:" + speakLines.size());
            setSpeakOnUtteranceComplete();
            tts_stopped = false;
            if (tts_from_last_line) {
                tts_from_last_line = false;
                fromId = speakLines.size() - 1;
            }
            tts_utteranceId = fromId - 1;

            tts.setSpeechRate((float) A.tts_speed / 10);
            tts.setPitch((float) A.tts_pitch / 10);

            for (int i = fromId; i < speakLines.size(); i++) {
                TtsLine sl = speakLines.get(i);
                if (i == fromId && (A.tts_divide != A.TTS_BY_PAGE)) {
                    highlightText(lastStartOfSpeakText + sl.start, lastStartOfSpeakText + sl.end);
                }
                HashMap<String, String> speakParams = new HashMap<String, String>();
                speakParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, String.valueOf(i));
                tts.speak(sl.s, TextToSpeech.QUEUE_ADD, speakParams);
                if (A.tts_interval_enable && A.tts_interval_time > 0)
                    tts.playSilence(A.tts_interval_time, TextToSpeech.QUEUE_ADD, null);
                tts_start_time = SystemClock.elapsedRealtime();
            }
            if (speakLines.size() == 0) {
                speakNextPage();
            }
        } catch (Exception e) {
            A.error(e);
            T.showToastText(this, A.errorMsg(e));
        }
    }

    private final int SPEAK_PAGE_DOWN = 0;
    private final int SPEAK_PAGE_UP = 1;
    private final int SPEAK_CHECK_PAGE = 2;
    private final int STOP_SPEAK = 3;
    private final int CHECK_PDF_SPEAKING_POS = 4;
    private final int RESTORE_SPEAKING = 5;
    private Handler speakHandler;

    private Handler getSpeakHandler() { //can't create handler inside speaking thread
        if (speakHandler == null)
            speakHandler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == CHECK_PDF_SPEAKING_POS && pdf != null && pdf.selectedPara != null) {
                        int y = 0, x = 0;
                        int h = baseFrame.getHeight() - A.d(2) - (tts_panel.getVisibility() == View.VISIBLE ? tts_panel.getHeight() : 0);
                        if (pdf.selectedPara != null) {
                            if (pdf.selectedPara.y2 > h) {
                                y = (int) pdf.selectedPara.y1 - A.d(5);
                            } else if (pdf.selectedPara.y1 < 0) {
                                y = (int) pdf.selectedPara.y1;
                            }
                            if (pdf.selectedPara.x1 < 0 || pdf.selectedPara.x1 > baseFrame.getWidth() - A.d(2)) {
                                x = (int) pdf.selectedPara.x1 - A.d(3);
                            }
                        }
                        PDFPosition p = pdf.pdfView.viewGetPos();
                        if (y != 0 || x != 0) {
                            if (y != 0)
                                p.page_y += y;
                            if (x != 0)
                                p.page_x += x;
                            pdf.pdfView.viewGoto(p);
                            pdf.postInvalidate();
                        }
                    }

                    if (msg.what == SPEAK_PAGE_DOWN) {
                        if (isPdfNoflow()) {
                            speakPdfText(true);
                        } else {
                            do_page_down();
                        }
                    }

                    if (msg.what == SPEAK_PAGE_UP) {
                        if (isPdfNoflow()) {
                            speakPdfText(false);
                        } else
                            do_page_up();
                    }

                    if (msg.what == SPEAK_CHECK_PAGE) {
                        try {
                            int y1 = (Integer) msg.obj;
                            int y2 = txtScroll.getScrollY();
                            if (Math.abs(y2 - y1) > 5) {
                                speakLines = getSpeakText();
                                if (speakLines != null && tts != null) {
                                    if (speakTextLength < 2)
                                        speakNextPage();
                                    else {
                                        setSpeakQueue(0);
                                    }
                                } else
                                    stop_speak();
                            } else if (isEndOfBook())
                                stop_speak();
                        } catch (Exception e) {
                            stop_speak();
                            A.error(e);
                        }
                    }

                    if (msg.what == STOP_SPEAK) {
                        if (A.tts_stop_enable && A.isSpeaking)
                            stop_speak();
                    }

                    if (msg.what == RESTORE_SPEAKING && speakLines != null) {
                        if (tts_utteranceId > speakLines.size() - 2)
                            tts_utteranceId = -1;
                        if (isPdfNoflow() && tts_utteranceId != -1) {
                            TtsLine sl = speakLines.get(tts_utteranceId + 1);
                            pdf.selectedPara = null;
                            PDFPage.setForceSel(sl.start, sl.end);
                            pdf.postInvalidate();
                            getSpeakHandler().sendEmptyMessageDelayed(CHECK_PDF_SPEAKING_POS, 100);
                        }
                        setSpeakQueue(tts_utteranceId + 1);
                    }
                }

                private void speakPdfText(boolean pageDown) {
                    if ((pageDown && pdfGetCurrPageNo() == m_doc.GetPageCount() - 1) || (!pageDown && pdfGetCurrPageNo() == 0))
                        stop_speak();
                    else {
                        while (true) {
                            pdfPageScroll(pageDown);
                            String text = pdf.pdfView.viewGetCurPageText();
                            if (!T.isNull(text) || pdfGetCurrPageNo() >= m_doc.GetPageCount() - 1)
                                break;
                        }
                        speakLines = getSpeakText();
                        setSpeakQueue(0);
                    }
                }
            };
        return speakHandler;
    }

    private boolean force_tts_stop, force_tts_not_stop;

    private void setStopTtsHandler() {
        if ((A.tts_stop_enable && !force_tts_not_stop) || force_tts_stop) {
            getSpeakHandler().removeMessages(STOP_SPEAK);
            getSpeakHandler().sendMessageDelayed(getSpeakHandler().obtainMessage(STOP_SPEAK), A.tts_stop_time * 60 * 1000);
            T.showToastText(ActivityTxt.this, "已设置" + A.tts_stop_time + "分钟后自动停止朗读");
        }
    }

    private void speakCurrentPage(int y) { //for TextView only
        if (y == -100)
            lastEndOfSpeakText = -1;
        getSpeakHandler().sendMessageDelayed(getSpeakHandler().obtainMessage(SPEAK_CHECK_PAGE, y), 50);
    }

    private void speakNextPage() {
        try {
            int y1 = txtScroll.getScrollY();
            getSpeakHandler().removeMessages(SPEAK_PAGE_DOWN);
            getSpeakHandler().sendMessageDelayed(getSpeakHandler().obtainMessage(SPEAK_PAGE_DOWN, y1), 100);
        } catch (Exception e) {
            A.error(e);
        }
    }

    private void speakPriorPage() {
        try {
            final int y1 = txtScroll.getScrollY();
            getSpeakHandler().removeMessages(SPEAK_PAGE_UP);
            getSpeakHandler().sendMessageDelayed(getSpeakHandler().obtainMessage(SPEAK_PAGE_UP, y1), 100);
        } catch (Exception e) {
            A.error(e);
        }
    }

    private MyDialog.Builder ttsAlertDlg;

    private void do_speak() {
        if (!shareValidteOK(4))
            return;
        force_tts_stop = force_tts_not_stop = false;
        if (web != null)
            return;
        if (A.isSpeaking) {
            stop_speak();
            return;
        }
        kill_tts(false);

        MRTextView tv = txtView.hStart != -1 ? txtView : txtView2.hStart != -1 ? txtView2 : null;
        boolean askForTts = A.askForTts && (tv == null || A.firstTimeUseTts);
        if ((askForTts) && txtView.hStart == -1) {
            if (ttsAlertDlg != null)
                return;
            if (A.firstTimeUseTts) {
                A.firstTimeUseTts = false;
                T.openTtsOptions(ActivityTxt.this);
            }

            ScrollView layout = (ScrollView) LayoutInflater.from(this).inflate(R.layout.tts_options, null);
            final CheckBox askCheck = (CheckBox) layout.findViewById(R.id.ofNeverAskAgain);
            final CheckBox stopCheck = (CheckBox) layout.findViewById(R.id.tts_stop_check);
            final EditText stopEt = (EditText) layout.findViewById(R.id.tts_stop_time);
            final Spinner sp = (Spinner) layout.findViewById(R.id.pmTtsDivides);
            askCheck.setChecked(true);

            if (A.tts_stop_enable) {
                stopCheck.setChecked(true);
                askCheck.setEnabled(false);
            }
            stopCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        askCheck.setChecked(false);
                        askCheck.setEnabled(false);
                    } else
                        askCheck.setEnabled(true);
                }
            });
            stopEt.setText("" + A.tts_stop_time);
            layout.findViewById(R.id.pmSetTTS).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    T.openTtsOptions(ActivityTxt.this);
                }
            });

            sp.setSelection(A.tts_divide);
            ttsAlertDlg = new MyDialog.Builder(this);
            ttsAlertDlg.setView(layout)
                    .setPositiveButton(R.string.start_speak_button, new OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            A.askForTts = !askCheck.isChecked();
                            initTTS();
                            ttsAlertDlg = null;
                        }
                    }).setNegativeButton(R.string.cancel, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    A.askForTts = !askCheck.isChecked();
                    ttsAlertDlg = null;
                }
            }).setCancelable(false).show();
        } else {
            A.log("*TTS start 1");
            initTTS();
        }
    }

    private void initTTS() {
        registerHeadset();
        tts = new TextToSpeech(ActivityTxt.this, onInitTTS);
        addToBatteryIgnore();
//		new InitTTSAsync().execute();  //if addLay.addView for AdMob, AsyncTask won't execute
    }
//	public class InitTTSAsync extends AsyncTask<Void, Long, Boolean> {
//		public InitTTSAsync() {
//			A.log("#InitTTSAsync");
//		}
//		@Override
//		protected Boolean doInBackground(Void... params) {
//			A.log("#doInBackground");
//			return true;
//		}
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			A.log("#onPreExecute");
//		}
//		@Override
//		protected void onPostExecute(Boolean result) {
//			A.log("#onPostExecute:" + result);
//			tts = new TextToSpeech(ActivityTxt.this, onInitTTS);
//		}
//	}

    public void stop_speak() {
        stop_speak(true);
    }

    public void stop_speak(boolean showTip) {
        if (A.doLongTap == A.DO_SPEAK) {
            if (SystemClock.elapsedRealtime() - tts_start_time < 3000)
                return;
        }

        A.isSpeaking = false;
        PDFPage.delForceSel(true);
        bookBackgroundStyle = -1;
        yBeforePageDown.clear();
        if (dualPageEnabled()) {
            saveLastPostion(true);
            restartReaderToTxt();
			/*txtView.nullLayouts();
			txtView2.nullLayouts();
			clearTxtView();
			checkDualPageMode();
			new Handler() {
				public void handleMessage(Message msg) {
					isTxtScrollReady = false;
					txtLay.requestLayout();
					reloadBook();
				}
			}.sendEmptyMessageDelayed(0, 200);*/
        }

        if (isPaused && txtView.hStart != -1) {
            txtScrollByDelay(txtView.hStart);
            updateProgressStatus();
        } else
            txtView.hStart = -1;

        lastEndOfSpeakText = -1;
        showTtsPanel(false);
        hide_tts_icon();
        hideTipsPanel();

        kill_tts(showTip);
        unregisterAudioFocus();
    }

    private void kill_tts(boolean showTip) {
        if (tts != null) {
            try {
                if (showTip)
                    T.showToastText(this, getString(R.string.tts_stop));
                tts_stopped = true;
                tts_stop();
                tts.shutdown();
            } catch (Exception e) {
                A.error(e);
            }
            tts = null;
        }
    }

    Notification tts_nofitication;

    private void show_tts_icon() {
        if (!A.isSpeaking)
            return;
        try {
            if (tts_nofitication == null) {
                if (Build.VERSION.SDK_INT >= 16) {
                    final RemoteViews rview = new RemoteViews(getPackageName(), R.layout.tts_notification);

                    Notification.Builder builder = new Notification.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ttsb3)
                            .setOngoing(true)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText("TTS");
                    A.setNotificationChannel(builder);
                    tts_nofitication = builder.build();

    				/*tts_nofitication = new Notification();
    				tts_nofitication.icon = R.drawable.ttsb3;
    				tts_nofitication.flags |= Notification.FLAG_ONGOING_EVENT;*/
                    tts_nofitication.contentView = rview;

                    final String TTS_STOP = "mr.tts.stop";
                    final String TTS_PAUSE = "mr.tts.pause";
                    final String TTS_PLAY = "mr.tts.play";
                    final String TTS_TO_READER = "mr.tts.to.reader";

                    Drawable d = A.getBookCover(A.lastFile);
                    if (d != null)
                        rview.setImageViewBitmap(R.id.tts_lay, T.drawableToBitmap(d));

                    rview.setOnClickPendingIntent(R.id.tts_stop,
                            PendingIntent.getBroadcast(this, 0, new Intent(TTS_STOP), PendingIntent.FLAG_UPDATE_CURRENT));
                    rview.setOnClickPendingIntent(R.id.tts_pause,
                            PendingIntent.getBroadcast(this, 0, new Intent(TTS_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT));
                    rview.setOnClickPendingIntent(R.id.tts_play,
                            PendingIntent.getBroadcast(this, 0, new Intent(TTS_PLAY), PendingIntent.FLAG_UPDATE_CURRENT));
                    rview.setOnClickPendingIntent(R.id.tts_lay,
                            PendingIntent.getBroadcast(this, 0, new Intent(TTS_TO_READER), PendingIntent.FLAG_UPDATE_CURRENT));

                    IntentFilter filter = new IntentFilter();
                    filter.addAction(TTS_STOP);
                    filter.addAction(TTS_PAUSE);
                    filter.addAction(TTS_PLAY);
                    filter.addAction(TTS_TO_READER);
                    tts_receiver = new BroadcastReceiver() {
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();
                            if (action.equals(TTS_STOP)) {
                                do_tts_buttons(tts_stop, false);
                            }
                            if (action.equals(TTS_PAUSE)) {
                                if (!tts_paused)
                                    pauseTTS();
                            }
                            if (action.equals(TTS_PLAY)) {
                                if (tts_paused)
                                    restore_speak();
                            }
                            if (action.equals(TTS_TO_READER)) {
                                Intent i = getPackageManager().getLaunchIntentForPackage(getPackageName());
                                if (i != null) {
                                    i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.setPackage(null);
                                    startActivity(i);
                                }
                            }
                        }
                    };
                    registerReceiver(tts_receiver, filter);

                } else {
                    Intent i = new Intent(getApplicationContext(), ActivityMain.class);
                    i.setAction("android.intent.action.MAIN");
                    i.addCategory("android.intent.category.LAUNCHER");
                    PendingIntent p = PendingIntent.getActivity(this, 0, i, 0);

                    //				tts_nofitication = new Notification(R.drawable.ttsb3, getString(R.string.app_name), System.currentTimeMillis());
                    //				tts_nofitication.flags |= Notification.FLAG_ONGOING_EVENT;
                    //				tts_nofitication.setLatestEventInfo(this, getString(R.string.app_name), "TTS", p);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        tts_nofitication = new Notification(R.drawable.ttsb3, getString(R.string.app_name), System.currentTimeMillis());
                        tts_nofitication.flags |= Notification.FLAG_ONGOING_EVENT;
                        try {
                            Method m = tts_nofitication.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                            m.invoke(tts_nofitication, getApplicationContext(), getString(R.string.app_name), "TTS", p);
                        } catch (Throwable e) {
                            A.error(e);
                        }
                    } else {
                        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ttsb3)
                                .setOngoing(true)
                                .setContentIntent(p)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText("TTS");
                        A.setNotificationChannel(builder);
                        tts_nofitication = builder.build();
                    }
                }
            }

            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(A.SEEKBOOKS_OPEN_BROADCAST.hashCode(), tts_nofitication);
        } catch (Exception e) {
            A.error(e);
        }
    }

    BroadcastReceiver tts_receiver;

    public void hide_tts_icon() {
        try {
            if (tts_receiver != null) {
                unregisterReceiver(tts_receiver);
                tts_receiver = null;
            }
            if (tts_nofitication != null) {
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(A.SEEKBOOKS_OPEN_BROADCAST.hashCode());
                tts_nofitication = null;
            }
        } catch (Exception e) {
            A.error(e);
        }
    }

    View search_panel, search_prior, search_next, search_list, search_close, search_return;
    TextView seearchCountTv;
    EditText searchEt;

    public void showSearchPanel() {
        if (search_panel == null) {
            search_panel = ((ViewStub) findViewById(R.id.searchStub)).inflate();
            search_list = search_panel.findViewById(R.id.listB);
            search_close = search_panel.findViewById(R.id.closeB);
            search_prior = search_panel.findViewById(R.id.priorB);
            search_next = search_panel.findViewById(R.id.nextB);
            search_return = search_panel.findViewById(R.id.nav_return);
            seearchCountTv = search_panel.findViewById(R.id.count);
            searchEt = search_panel.findViewById(R.id.keyEdit);
//            searchEt.setSingleLine();
//            searchEt.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
//            searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    if (actionId == EditorInfo.IME_ACTION_SEARCH){
//                        String key = searchEt.getText().toString();
//                        if (!T.isNull(key))
//                            showPrefSearch(!key.equals(A.lastSearchKey) ? key : null);
//                    }
//                    return false;
//                }
//            });
            searchEt.setVisibility(View.INVISIBLE);

            search_list.setOnClickListener(this);
            search_close.setOnClickListener(this);
            search_prior.setOnClickListener(this);
            search_next.setOnClickListener(this);
            search_return.setOnClickListener(this);
        }
        searchEt.setText(A.lastSearchKey);
        search_panel.setVisibility(View.VISIBLE);
        seearchCountTv.setText((PrefSearch.lastClickIndex + 1) + "/" + PrefSearch.results.size());
        A.setSystemUiVisibility(true);
    }

    View tips_panel, tips_ok;
    CheckBox tips_confirm;
    ImageView tips_image;
    private final int TIP_TYPE_TTS = 0;
    private final int TIP_TYPE_AUTOSCROLL = 1;
    private int tips_type;

    private boolean showTipsPanel(int type) {
        if (type == TIP_TYPE_TTS && !A.askTtsTip)
            return false;
        if (type == TIP_TYPE_AUTOSCROLL && !A.askAutoScrollTip)
            return false;
        if (tips_panel == null) {
            tips_panel = ((ViewStub) findViewById(R.id.tipStub)).inflate();
            tips_ok = tips_panel.findViewById(R.id.okB);
            tips_ok.setOnClickListener(this);
            tips_confirm = (CheckBox) tips_panel.findViewById(R.id.ofNeverAskAgain);
            tips_image = (ImageView) tips_panel.findViewById(R.id.imageView1);
            if (A.fitCutout == 1 && A.isCutoutScreen() && A.getCutoutBarHeight() > 0)
                tips_panel.setPadding(0, A.getCutoutBarHeight(), 0, 0);
        }
        tips_confirm.setChecked(false);
        if (type == TIP_TYPE_TTS)
            tips_image.setImageDrawable(A.getDrawableFromAsset(getAssets(), "tips_tts.png", A.NORMAL_QUALITY));
        if (type == TIP_TYPE_AUTOSCROLL)
            tips_image.setImageDrawable(A.getDrawableFromAsset(getAssets(), "tips_autoscroll.png", A.NORMAL_QUALITY));
        tips_type = type;
        tips_panel.setVisibility(View.VISIBLE);
        return true;
    }

    private boolean hideTipsPanel() {
        if (tips_panel != null && tips_panel.getVisibility() == View.VISIBLE) {
            tips_panel.setVisibility(View.GONE);
            tips_image.setImageDrawable(null);
            if (tips_confirm.isChecked()) {
                if (tips_type == TIP_TYPE_TTS)
                    A.askTtsTip = false;
                if (tips_type == TIP_TYPE_AUTOSCROLL)
                    A.askAutoScrollTip = false;
            }
            return true;
        }
        return false;
    }

    //---------------------tts panel------------------

    public View tts_panel, tts_stop, tts_prior, tts_play, tts_next, tts_options, tts_page_up, tts_page_down;
    TextView tts_reset;
    SeekBar tts_speed, tts_volume, tts_pitch;
    boolean tts_panel_visible, tts_paused, tts_from_last_line, tts_stopped;
    private View.OnClickListener ttsClickEvent;
    private OnSeekBarChangeListener ttsSeekBarEvent;

    private void initTtsPanel() {
        tts_panel = ((ViewStub) findViewById(R.id.viewStub1)).inflate();
        ttsClickEvent = new View.OnClickListener() {
            public void onClick(View v) {
                do_tts_buttons(v);
            }
        };
        ttsSeekBarEvent = new OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar == tts_speed || seekBar == tts_pitch) {
                    int progress = seekBar.getProgress();
                    if (seekBar == tts_speed)
                        A.tts_speed = get_tts_speed_progress(progress);
                    if (seekBar == tts_pitch)
                        A.tts_pitch = progress > 0 ? progress : 1;
                    setTtsResetVisibile();
                    restore_speak();
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (seekBar == tts_speed)
                        progress = get_tts_speed_progress(progress);
                    T.showToastText(ActivityTxt.this, "" + progress);
                    keepTtsPanelVisible();
                    if (seekBar == tts_volume)
                        ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), 0);
                }
            }
        };
        tts_page_up = tts_panel.findViewById(R.id.tts_page_up);
        tts_page_up.setOnClickListener(ttsClickEvent);
        tts_page_down = tts_panel.findViewById(R.id.tts_page_down);
        tts_page_down.setOnClickListener(ttsClickEvent);
        tts_stop = tts_panel.findViewById(R.id.tts_stop);
        tts_stop.setOnClickListener(ttsClickEvent);
        tts_prior = tts_panel.findViewById(R.id.tts_prior);
        tts_prior.setOnClickListener(ttsClickEvent);
        tts_play = tts_panel.findViewById(R.id.tts_play);
        tts_play.setOnClickListener(ttsClickEvent);
        tts_next = tts_panel.findViewById(R.id.tts_next);
        tts_next.setOnClickListener(ttsClickEvent);
        tts_reset = (TextView) tts_panel.findViewById(R.id.resetTv);
        tts_reset.setOnClickListener(ttsClickEvent);
        tts_reset.setText(getString(R.string.reset).replace(":", ""));
        setTtsResetVisibile();
        tts_options = tts_panel.findViewById(R.id.tts_options);
        tts_options.setOnClickListener(ttsClickEvent);
        tts_speed = (SeekBar) tts_panel.findViewById(R.id.tts_speed);
        tts_speed.setOnSeekBarChangeListener(ttsSeekBarEvent);
        tts_speed_setProgress(A.tts_speed);
        tts_pitch = (SeekBar) tts_panel.findViewById(R.id.tts_pitch);
        tts_pitch.setOnSeekBarChangeListener(ttsSeekBarEvent);
        tts_pitch.setProgress(A.tts_pitch);
        tts_volume = (SeekBar) tts_panel.findViewById(R.id.tts_volume);
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        tts_volume.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        tts_volume.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
        tts_volume.setOnSeekBarChangeListener(ttsSeekBarEvent);

//		if (A.vdf(A.getScreenWidth()) < 360) { // 480*800, small phone
//			tts_page_up.setVisibility(View.GONE);
//			tts_page_down.setVisibility(View.GONE);
//		}
    }

    private void setTtsResetVisibile() {
        tts_reset.setVisibility(A.tts_speed == 10 && A.tts_pitch == 10 ? View.GONE : View.VISIBLE);
    }

    private void do_tts_buttons(View v) {
        do_tts_buttons(v, true);
    }

    public void do_tts_buttons(View v, boolean showPanel) {
        audio_pause_from_listener = false;

        if (showPanel)
            keepTtsPanelVisible();

        if (v == tts_page_up) {
            switchTtsPlayPause(true);
            tts_stopped = true;
            tts_stop();
            lastEndOfSpeakText = -1;
            speakPriorPage();
        }
        if (v == tts_page_down) {
            switchTtsPlayPause(true);
            tts_stopped = true;
            tts_stop();
            lastEndOfSpeakText = -1;
            speakNextPage();
        }
        if (v == tts_stop) {
            stop_speak();
        }

        if (v == tts_prior) {
            switchTtsPlayPause(true);
            A.log("tts_utteranceId 1:" + tts_utteranceId);
            if (tts_utteranceId > -1) {// && speakLines.size()>tts_utteranceId+1){
                tts_utteranceId--;
                force_utteranceId = tts_utteranceId;
                restore_speak();
            } else {
                tts_from_last_line = true;
                tts_stopped = true;
                tts_stop();
                speakPriorPage();
            }
        }

        if (v == tts_play) {
            if (tts_paused) {
                restore_speak();
            } else {
                pauseTTS();
            }
        }

        if (v == tts_next) {
            switchTtsPlayPause(true);
            if (speakLines != null && tts_utteranceId < speakLines.size() - 2) {
                tts_utteranceId++;
                force_utteranceId = tts_utteranceId;
                restore_speak();
            } else {
                tts_stopped = true;
                if (tts != null) {
                    tts_stop();
                    speakNextPage();
                }
            }
        }

        if (v == tts_options) {
            showBottomTtsOptions();
//			T.openTtsOptions(this);
        }

        if (v == tts_reset) {
            A.tts_speed = 10;
            tts_speed_setProgress(10);
            A.tts_pitch = 10;
            tts_pitch.setProgress(10);
            setTtsResetVisibile();
            if (A.isSpeaking)
                restore_speak();
        }
    }

    private void pauseTTS() {
        if (tts != null) {
            switchTtsPlayPause(false);
            tts_stopped = true;
            tts_stop();
            if (tts_panel.getVisibility() != View.VISIBLE)
                showTtsPanel(true, true);
        }
    }

    int forceSpeakPos;

    void speakFingerPoint() {
//		highlightText(lastStartOfSpeakText + sl.start, lastStartOfSpeakText + sl.end);
        int i = getSpeakLineIndex(forceSpeakPos);
        if (i != -1) {
            force_utteranceId = i - 1;
        }
        restore_speak();
    }

    private int getSpeakLineIndex(int pos) {
        if (pos != -1 && speakLines != null) {
            for (int i = 0; i < speakLines.size(); i++) {
                TtsLine sl = speakLines.get(i);
                int start = lastStartOfSpeakText + sl.start;
                int end = lastStartOfSpeakText + sl.end;
                if (pos >= start && pos < end)
                    return i;
            }
        }
        return -1;
    }

    private void tts_stop() {
        try {
            tts.stop();
        } catch (Exception e) {
            A.error(e);
        }
    }

    private void restore_speak() {
        try {
            tts_stopped = true;
            tts_stop();
//			setSpeakQueue(tts_utteranceId + 1); //this has bug in Android 6
            getSpeakHandler().removeMessages(RESTORE_SPEAKING);
            getSpeakHandler().sendEmptyMessageDelayed(RESTORE_SPEAKING, 100);

            switchTtsPlayPause(true);
        } catch (Exception e) {
            A.error(e);
            stop_speak();
            T.showToastText(this, A.errorMsg(e));
        }
    }

    private void switchTtsPlayPause(boolean playState) {
        if (playState) {
            tts_paused = false;
            if (tts_play != null)
                ((ImageView) tts_play).setImageResource(R.drawable.tts_pause);
        } else {
            tts_paused = true;
            if (tts_play != null)
                ((ImageView) tts_play).setImageResource(R.drawable.tts_play);
        }
    }

    protected void keepTtsPanelVisible() {
        if (tts_panel == null)
            return;
        ttsButtonPressed = true;
        tts_panel.setAnimation(null);
        tts_panel.setVisibility(View.VISIBLE);
        ttsIv.setVisibility(View.GONE);
        tts_panel_visible = true;
    }

    private void showTtsPanel(boolean visible) {
        showTtsPanel(visible, true);
    }

    private void showTtsPanel(boolean visible, boolean animate) {
        if (tts_panel != null) {
            if (!visible)
                if (showTipsPanel(TIP_TYPE_TTS))
                    animate = false;
            tts_panel_visible = visible;
            tts_panel.setVisibility(visible ? View.VISIBLE : View.GONE);
//			tts_panel.setBackgroundColor(A.isNightState() ? 0xAA666666 : 0xCCFFFFFF);
            if (A.isSpeaking) {
                int h = baseFrame.getHeight();
                Animation animation1 = visible ? new TranslateAnimation(0, 0, baseFrame.getHeight(), baseFrame.getHeight() - h)
                        : new TranslateAnimation(0, 0, baseFrame.getHeight() - h, baseFrame.getHeight());
                animation1.setDuration(400);
                tts_panel.startAnimation(animation1);

                if (animate) {
                    Animation animation2 = visible ? new AlphaAnimation(1.0f, 0.1f) : new AlphaAnimation(0.1f, 1.0f);
                    animation2.setDuration(visible ? 300 : 800);
                    ttsIv.startAnimation(animation2);
                }
            }
        }
        ttsIv.setVisibility(A.isSpeaking && !visible ? View.VISIBLE : View.GONE);
    }

    private boolean ttsButtonPressed;

    private void showTtsOptionsButton() {
        //		ttsIv.setVisibility(View.VISIBLE);
        if (tts_panel == null)
            initTtsPanel();
        else
            tts_volume.setProgress(((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC));

        switchTtsPlayPause(true);
        showTtsPanel(true, false);
        ttsButtonPressed = false;
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (!ttsButtonPressed && tts_panel_visible)
                    showTtsPanel(false);
            }
        }.sendEmptyMessageDelayed(0, 4000);
    }

    ////---------------tts end------------

    @Override
    protected void onStop() {
        A.log("-txt:stop");
        super.onStop();
        if (isOnExiting) {
            T.deleteFile(A.txtPauseOnlyTagFile);
        } else
            T.saveFileText(A.txtPauseOnlyTagFile, "" + System.currentTimeMillis());
    }

    @Override
    protected void onDestroy() {
        A.saveMemoryLog("-txt:destroy");
        T.recycle(flipView.bm1);
        T.recycle(flipView.bm2);
        T.recycle(tmpFlipShot1);
        T.recycle(tmpFlipShot2);

        if (contentLay != null)
            contentLay.releaseCache();
        if (flipView != null) {
            flipView.setBitmap1(null);
            flipView.setBitmap2(null);
        }

        bookType = -1;
        if (mp3 != null)
            mp3.stop();
        unregisterHeadset();
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
            batteryReceiver = null;
            battery = null;
        }
        super.onDestroy();
        T.deleteFile(A.txtPauseOnlyTagFile); // if ActTxt is killed by system, has onStop() only  without onDestroy()

        if (!fromOuterApp && T.isNull(ActivityMain.selfPref))
            //double-back key, will execute ActivitMain.onDestroy firstly, then ActivityTxt.onDestroy
            if (SystemClock.elapsedRealtime() - ActivityMain.backExitTime > 1000) {
                A.forceRebootToMain = true;
                A.SaveOptions(this);
                startActivity(new Intent(this, ActivityMain.class));
            }
    }

    private void clearMemoryOnExit() {
        if (A.isSpeaking)
            stop_speak();
        if (web != null)
            web.resetZoom();
        bookType = -1;
        selfPref = null;
//		A.bookmarksList = null;
        A.baseFrame = null;
        A.txtScroll = null;
        A.txtScroll2 = null;
        A.scrollCache = null;
        A.txtView = null;
        A.txtView2 = null;
        A.txtCache = null;
        A.splitHtmls = new ArrayList<String>();
        A.clearTxts2();
        A.clearTxts();

        A.savedBackgroundDrwable = null;
        if (curl3d != null)
            curl3d.clearAllShots();

        if (isPdf() && pdf != null) {
            if (pdfThumb != null && (Integer) pdfThumb.getTag() == Global.def_view)
                pdfThumb.thumbClose();
            pdf.set_thumb(null);
            pdf.close();
            if (m_doc != null)
                m_doc.Close();
            Global.RemoveTmp();
        }

        if (fromShuku)
            WB.notifyRecentListChanged(A.lastFile);
    }

    //-----------for auto scroll:-------------
    private Timer scrollTimer;
    private int scrollPos, pdfVerticalOffset;
    private boolean justStartScroll;
    ScrollImage scrollImage;

    private void createRollingScrollLayout() { //for smooth scroll
        txtLayS.setVisibility(View.VISIBLE);
        if (scrollImage == null) {
            scrollImage = new ScrollImage(this);
            txtLayS.addView(scrollImage, new FrameLayout.LayoutParams(-1, -1));
        }
        txtLayS.setOnTouchListener(this);
    }

    private void do_AutoScrollAsk() {
        if (A.askForScrollEvent) {
            ScrollView layout = (ScrollView) LayoutInflater.from(this).inflate(R.layout.scroll_event_confirm, null);
            final CheckBox checkBox = (CheckBox) layout.findViewById(R.id.ofNeverAskAgain);
            final SeekBar sb = (SeekBar) layout.findViewById(R.id.pmScrollSpeed);
            sb.setProgress(100 - A.autoScrollSpeed);
            final Spinner sp = (Spinner) layout.findViewById(R.id.pmScrollMode);
            sp.setSelection(A.autoScrollMode);
            if (isPdf())
                layout.findViewById(R.id.modeLay).setVisibility(View.GONE);

            new MyDialog.Builder(this).setTitle(getString(R.string.auto_scroll_control)).setView(layout)
                    .setPositiveButton(R.string.ok, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            A.askForScrollEvent = !checkBox.isChecked();
                            A.autoScrollMode = sp.getSelectedItemPosition();
                            A.autoScrollSpeed = 100 - sb.getProgress();
                            do_AutoScroll(true);
                        }
                    }).show();
        } else
            do_AutoScroll(true);
    }

    private void do_AutoScroll(boolean start) {
        if (!shareValidteOK(3))
            return;
        if (web != null)
            return;
        do_AutoScroll(start, true);
    }

    private void do_AutoScroll(boolean start, boolean showHint) {
        if (web != null)
            return;
        if (start) {
            saveStatisticsInfo();
        } else {
            read_words = 0;
            statisticsTime = System.currentTimeMillis();
        }
        autoScrollPaused = false;
        if (start) {
            showTipsPanel(TIP_TYPE_AUTOSCROLL);
            if (!showHint || !resetPositionFrom2PageModeBeforeAutoScroll())
                startAutoScrollEvent();
            inverseLayoutVisible(true);
            baseFrame.setKeepScreenOn(true);
            A.isInAutoScroll = true;
        } else {
            yBeforePageDown.clear();
            A.isInAutoScroll = false;
            if (!isPdf()) {
                bookBackgroundStyle = -1;
                if (showHint && dualPageEnabled()) {
                    saveLastPostion(true);
                    restartReaderToTxt();
					/*handler.removeMessages(AUTO_SCROLL_MESSAGE);
					txtView.nullLayouts();
					txtView2.nullLayouts();
					clearTxtView();
					checkDualPageMode();
					new Handler() {
						public void handleMessage(Message msg) {
							isTxtScrollReady = false;
							txtLay.requestLayout();
							reloadBook();
						}
					}.sendEmptyMessageDelayed(0, 200);*/
                }
            }
            if (scrollTimer != null)
                scrollTimer.cancel();
            txtLayS.setVisibility(View.GONE);
            if (scrollImage != null) {
                T.recycle(scrollImage.page1Bm);
                T.recycle(scrollImage.page2Bm);
            }
            if (scrollImage != null)
                T.recycle(scrollImage.shadowBm);
            if (txtLineS != null)
                txtLineS.setVisibility(View.GONE);
            if (showHint)
                T.showToastText(this, getString(R.string.scroll_cancel));
            baseFrame.setKeepScreenOn(A.keepScreenAwake);
            hideTipsPanel();
            if (!isPdf() && !dualPageEnabled())
                statusHandlerSendMessage(0, 0);
            pixel_scroll_ignore = 0;
        }
    }

    private void startAutoScrollEvent() {
        A.isInAutoScroll = true;
        switch (getScrollMode()) {
            case A.SCROLL_ROLLING_PIXEL:
            case A.SCROLL_ROLLING_LINE:
                createRollingScrollLayout();
                if (isPdf())
                    pdfVerticalOffset = pdfVerticalParaMargin();
                scrollPos = isPdf() ? pdfVerticalOffset : txtScroll.getPaddingTop();
                break;
            case A.SCROLL_PIXEL:
            case A.SCROLL_LINE:
                if (A.ebook != null && txtView.getRealLineCount() > 2) {
                    int l = A.getPageHeight() / txtView.getLineHeight(txtView.getRealLineCount() - 1);
                    if (txtView.appendLineCount < l / 4)
                        txtView.appendEmptyLines(l / 4 - txtView.appendLineCount);
                }
                break;
            case A.SCROLL_PAGE:
                if (txtLineS == null) {
                    txtLineS = new View(this);
                    txtLineS.setBackgroundColor(0xAA666666);
                    baseFrame.addView(txtLineS, new FrameLayout.LayoutParams(-1, 1));
                }
                txtLineS.setVisibility(View.VISIBLE);
                break;
        }

        autoScrollTimesCount = 0;
        scroll_event_time = SystemClock.elapsedRealtime();
        justStartScroll = true;
        scrollTimer = new Timer();
        scrollTimer.schedule(new AutoScrollTask(), 0, getAutoScrollInterval());
    }

    private boolean resetPositionFrom2PageModeBeforeAutoScroll() {
        if (dualPageEnabled())
            try {
                A.isInAutoScroll = true;
                checkDualPageMode();
                new Handler() {
                    public void handleMessage(Message msg) { //delay proc
                        txtView.assumeLayout();
                        txtScrollNoDelay(msg.what);
                        startAutoScrollEvent();
                    }
                }.sendEmptyMessage(txtView.getCurPosition());
                return true;
            } catch (Exception e) {
                A.error(e);
            }
        return false;
    }

    private int getAutoScrollInterval() {
        int l = A.autoScrollSpeed;
        int interval = 100;
        if (getScrollMode() == A.SCROLL_ROLLING_PIXEL) {
            int added = 150;
            int base = 25;
            if (l >= 50)
                interval = (int) (base + 15 + (l - 50) * added / 50);
            else {
                int step = A.autoScrollSpeed % 5 == 0 ? 5 : l % 5;
                interval = getPixelScrollSpace() <= 4 ? base - 5 + step * 3 : base;
            }
            interval = interval * 100 / (100 + 2 * (l < 50 ? (50 - l) : 0));
        } else if (getScrollMode() == A.SCROLL_ROLLING_LINE) //v3.3.2
            interval = interval * 100 / (100 + 2 * (l < 50 ? (50 - l) : 0));
        else if (getScrollMode() == A.SCROLL_PIXEL) //v3.3.2
            interval = 4;

        A.log("----interval:" + interval);
        return interval;
    }

    private int getPixelScrollSpace() {
        int l = A.autoScrollSpeed;
        l = l >= 50 ? 1 : (55 - l) / 5;
        return l;
    }

    private int scrollInterval = -1;

    private void resetPixelScrollSpeed() {
        if ((getScrollMode() == A.SCROLL_PIXEL || getScrollMode() == A.SCROLL_ROLLING_PIXEL) && scrollTimer != null) {
            int interval = getAutoScrollInterval();
            if (interval != scrollInterval) {
                scrollInterval = interval;
                if (scrollTimer != null)
                    scrollTimer.cancel();
                scrollTimer = new Timer();
                scrollTimer.schedule(new AutoScrollTask(), 10, interval);
            }
        }
    }

    private int autoScrollTimesCount;

    public class AutoScrollTask extends TimerTask {
        @Override
        public void run() {
            if (!A.isInAutoScroll) {
                this.cancel();
                return;
            }

            if (autoScrollPaused)
                return;

            autoScrollTimesCount++;
            switch (getScrollMode()) {
                case A.SCROLL_ROLLING_PIXEL:
                    handler.sendEmptyMessage(AUTO_SCROLL_MESSAGE);
                    break;
                case A.SCROLL_ROLLING_LINE:
                    if (autoScrollTimesCount * 4 >= A.autoScrollSpeed) {
                        autoScrollTimesCount = 0;
                        handler.sendEmptyMessage(AUTO_SCROLL_MESSAGE);
                    }
                    break;
                case A.SCROLL_PIXEL:
                    handler.sendEmptyMessage(AUTO_SCROLL_MESSAGE);
                    break;
                case A.SCROLL_LINE:
                    if (autoScrollTimesCount * 5 >= A.autoScrollSpeed) {
                        autoScrollTimesCount = 0;
                        handler.sendEmptyMessage(AUTO_SCROLL_MESSAGE);
                    }
                    break;
                case A.SCROLL_PAGE:
                    handler.sendEmptyMessage(AUTO_SCROLL_MESSAGE);
                    break;
            }
        }
    }

    private long scroll_event_time, pixel_scroll_ignore;

    private void do_autoscroll_message() {
        if (autoScrollPaused)
            return;

        boolean show_scroll_progress = false;
        if (SystemClock.elapsedRealtime() - scroll_event_time > 2500) {
            scroll_event_time = SystemClock.elapsedRealtime();
            show_scroll_progress = true;
        }

        int l = A.autoScrollSpeed;
        switch (getScrollMode()) {
            case A.SCROLL_ROLLING_PIXEL:
            case A.SCROLL_ROLLING_LINE:
                boolean isRollingLine = getScrollMode() == A.SCROLL_ROLLING_LINE;
                if (justStartScroll) {
                    autoScrollTurnPage(PAGE_DOWN, false);
                    justStartScroll = false;
                    scrollImage.topLine = 0;
                }
                if (isRollingLine) {
                    int line = txtView.getLayout().getLineForVertical(txtScroll.getScrollY() + scrollImage.topLine + 1);
//					scrollPos = txtView.getLineTop(line + 1);
                    int lh = txtView.getLineHeight(line);
                    scrollPos += (lh > txtView.getLineHeight() * 2 ? txtView.getLineHeight() : lh);
                } else
                    scrollPos += getPixelScrollSpace() * 55 / (55 - (l < 50 ? (50 - l) : 0));
                int bottomOffset = isPdf() ? pdfVerticalOffset : showStatusbar() ? statusLay.getHeight() : 0;
                if (scrollPos > baseFrame.getHeight() - bottomOffset) {
                    if (isEndOfBook())
                        do_AutoScroll(false);
                    else
                        autoScrollTurnPage(PAGE_DOWN, true);
                }

                scrollImage.topLine = scrollPos;
                scrollImage.invalidate();
                break;

            case A.SCROLL_PIXEL:
                if (++pixel_scroll_ignore > l / (l < 20 ? 2 : l < 30 ? 3 : l < 40 ? 4 : 5)) {
                    pixel_scroll_ignore = 0;
                    adjustTxtViewText();
                    int y1 = txtScroll.getScrollY();
                    boolean forceToNext = false;
                    if (A.ebook != null && A.landscape2PageMode && A.isLandscape())
                        if (y1 + A.getPageHeight() > txtView.getLineTop3(txtView.getLineCount()) + 5 * txtView.getLineHeight())
                            forceToNext = true;
                    if (!forceToNext)
                        txtScroll.scrollBy(0, l < 40 ? (50 - l) * 5 / (50 - (40 - l)) : 1);
                    pixelAutoScrollLastLineHint(y1);
                }
                if (show_scroll_progress)
                    showReadProgress(0);
                break;

            case A.SCROLL_LINE:
                adjustTxtViewText();
                int y2 = txtScroll.getScrollY();
                int line = txtView.getLayout().getLineForVertical(txtScroll.getScrollY() + A.getPageHeight() + 1);
                int lh = txtView.getLineHeight(line);
                txtScroll.scrollBy(0, lh > txtView.getLineHeight() * 2 ? txtView.getLineHeight() : lh);
                pixelAutoScrollLastLineHint(y2);
                if (show_scroll_progress)
                    showReadProgress(0);
                break;

            case A.SCROLL_PAGE:
                float speed = (l + 20) / 10;
                speed = (speed * speed / (speed + 5)) * 30;
                speed = speed * 100 / (100 + 2 * (l < 50 ? (50 - l) : 0));

                if (autoScrollTimesCount >= speed) {
                    autoScrollTimesCount = 0;
                    if (isEndOfBook())
                        do_AutoScroll(false);
                    else
                        pageScroll(PAGE_DOWN);
                }

                int w = (int) (15 + A.getScreenWidth2() * autoScrollTimesCount / (speed));
                if (autoScrollTimesCount % 2 == 0 && txtLineS != null)
                    txtLineS.layout(0, 0, w, A.d(2));
                break;
        }
    }

    private long pixelAutoScrollToBottomTime, lastPixelTipNumber;

    private void pixelAutoScrollLastLineHint(int y) {
        if (y == txtScroll.getScrollY()) {
            if (isEndOfBook()) {
                do_AutoScroll(false);
            } else if (getBookType() == A.FILE_EBOOK) {
                if (pixelAutoScrollToBottomTime == 0) {
                    pixelAutoScrollToBottomTime = SystemClock.elapsedRealtime();
                } else {
                    long interval = 1 + SystemClock.elapsedRealtime() - pixelAutoScrollToBottomTime;
                    if (A.autoScrollSpeed <= 10 || interval > 3000) {
                        pixelAutoScrollToBottomTime = 0;
                        T.hideToast();
                        pageScroll(PAGE_DOWN);
                    } else {
                        long i = (4000 - interval) / 1000;
                        if (i != lastPixelTipNumber) {
                            lastPixelTipNumber = i;
                            T.showToastText(this, "" + i);
                        }
                    }
                }
            } else
                pageScroll(PAGE_DOWN);
        } else
            pixelAutoScrollToBottomTime = 0;
    }

    private void autoScrollTurnPage(int direction, boolean resetScrollPos) {
        switch (getScrollMode()) {
            case A.SCROLL_ROLLING_PIXEL:
            case A.SCROLL_ROLLING_LINE:
                if (scrollImage == null)
                    return;
                if (resetScrollPos) {
                    boolean isRollingLine = getScrollMode() == A.SCROLL_ROLLING_LINE;
                    scrollPos = isPdf() ? pdfVerticalOffset : txtScroll.getPaddingTop() + (isRollingLine ? txtView.getLineHeight() : 0);
                }
                scrollImage.setVisibility(View.INVISIBLE);
                boolean hideTips = (tips_panel != null && tips_panel.getVisibility() == View.VISIBLE);
                if (hideTips)
                    tips_panel.setVisibility(View.INVISIBLE);
                scrollImage.setPage2Bm(getPageShot(true, true));
                scrollImage.topLine = 0;
                scrollImage.setVisibility(View.VISIBLE);
                if (hideTips)
                    tips_panel.setVisibility(View.VISIBLE);
                scrollImage.invalidate();
                int i = A.lastChapter;
                pageScroll(direction);
                if (i == A.lastChapter)
                    scrollImage.setPage1Bm(getPageShot(true, true));
                scrollImage.invalidate();
                break;

            case A.SCROLL_PIXEL:
            case A.SCROLL_LINE:
                pageScroll(direction);
                break;

            case A.SCROLL_PAGE:
                autoScrollTimesCount = 0;
                pageScroll(direction);
                break;
        }
    }

    ////-------------auto scroll end------------

    private boolean restart_after_sync_finish;

    private boolean forceExitRestart() {
        if (A.tmpOutOfMemoryTag || A.isLowMemory(30) || A.isLowestMemory()) { //z: v2.2.5 hyphenation works stable now?
            restartReaderToMain();
            return true;
        }

        return false;
    }

    void do_back_to_book_stack() {
        if (forceExitRestart())
            return;
        doFinish();
    }

    boolean isOnExiting, dntSavePositionAgain;

    private void doFinish() {
        saveLastPostion(true);
        dntSavePositionAgain = true;

        ActivityMain.openBookView = null;
        if (A.openBookAnim) {
            if (curl3d != null) {
//				curl3d.clearAllShots();
//				curl3dLay.removeView(curl3d);
//				curl3d = null;
                curl3d.setVisibility(View.GONE);
                curl3dLay.setVisibility(View.GONE);
            }
            Drawable d = A.getBookCover(A.lastFile);
            if (d != null) {
                inverseLayoutVisible(true);
                flipView.isCoverShow = true;
                flipView.setBitmap1(T.drawableToBitmap(d));
                flipView.setBitmap2(null);
                flipView.setImageDrawable(null);
                flipView.startAnimation(false, -100, new A.AfterFlipCurl() {
                    @Override
                    public void Finished(boolean success) {
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                doFinishEvent();
                            }
                        }.sendEmptyMessageDelayed(0, 60);
                    }
                });
                return;
            } else
                contentLay.saveShot();
        }
        doFinishEvent();
    }

    private void doFinishEvent() {
        contentLay.finished = true;
        saveEBookName();
        isOnExiting = true;
        selfPref = null;
        A.typefaceCache = null;
        DownloadTaskBrowser.destroyWebView(cacheWB);
        DownloadTaskBrowser.destroyWebView(storeWeb);
        z.taskReadeTime("" + A.statistics_time);
        z.taskReadeWords("" + A.statistics_words);
        if (A.openBookAnim && actTransArrive && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            finishAfterTransition();
        else
            finish();
    }

    public void terminate_reader() {
        try {
            if (!T.isNull(ActivityMain.selfPref))
                ActivityMain.selfPref.finish();
            ActivityMain.selfPref = null;
            saveLastPostion(true);
            A.forceRebootToTxt = A.forceRebootToMain = false;
            T.deleteFile(A.txtPauseOnlyTagFile);
            A.SaveOptions(this);
            finish();
            selfPref = null;
//			if (A.syncType != A.LOCAL)
//				if (A.uploadCloudPositionFile()){
//					doFinish();
//					return;
//				}
            System.exit(0);
        } catch (Exception e) {
            A.error(e);
        }
    }

    private void saveEBookName() {
        BookInfo bi = BookDb.getBook(A.lastFile);
        if (bi == null) {
            String bookName = null, author = "", description = "", category = "";
            if (getBookType() == A.FILE_EBOOK && A.ebook != null) {
                bookName = A.ebook.getBookName();
                author = A.ebook.getAuthor();
                description = A.ebook.description;
                category = T.stringList2Text(A.ebook.categories);
            }
            if (getBookType() == A.FILE_PDF && m_doc != null) {
                bookName = m_doc.GetMeta("Title");
                author = m_doc.GetMeta("Author");
            }
            if (getBookType() == A.FILE_TXT) {
                String s = T.getOnlyFilename(A.lastFile);
                int i = s.indexOf(" - ");
                if (i > 0) {
                    bookName = s.substring(0, i).trim();
                    author = s.substring(i + 3).trim();
                }
            }
            if (bookName != null) {
                bi = new BookInfo(
                        bookName,
                        A.lastFile,
                        author,
                        description,
                        category,
                        "",
                        "",
                        "" + System.currentTimeMillis(),
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                );
                BookDb.insertBookToTemp(bi);
            }
        }
    }

    private boolean brightnessSetted = false;

    public void setScreenBrightness(int brightnessValue, boolean showHint) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();

        if (brightnessValue == -100) {
            lp.screenBrightness = -1;
            A.brightnessValue = brightnessValue;
        } else {
            if (brightnessValue > 100)
                brightnessValue = 100;
            if (brightnessValue < -50)
                brightnessValue = -50;

            if (showHint)
                T.showToastText(this, (brightnessValue + "%"), 0, Gravity.CENTER);

            A.brightnessValue = brightnessValue;
            showShadeCoverOnView();

            float value = brightnessValue > 0 ? brightnessValue : 1;
            lp.screenBrightness = value / 100;
        }
        brightnessSetted = true;
        getWindow().setAttributes(lp);
    }

    private void showShadeCoverOnView() {
        if (shadeView == null)
            return;
        if (A.brightnessValue > 0 && !A.bluelightEnable) {
            if (shadeView.getVisibility() != View.GONE) {
                shadeView.setVisibility(View.GONE);
            }
            return;
        }
//      i*180/80, 200-i*190/80, 180-i*170/80, 60-i*60/80
        int i = A.bluelightOpacity;
        int a = A.bluelightEnable ? i * 180 / 80 + getShadeAlph() : getShadeAlph();
        if (i > 80)
            i = 80;
        int r = A.bluelightEnable ? 200 - i * 190 / 80 : 0;
        int g = A.bluelightEnable ? 180 - i * 170 / 80 : 0;
        int b = A.bluelightEnable ? 60 - i * 60 / 80 : 0;
        if (a > 230)
            a = 230;
        int color = Color.argb(a, r, g, b);
        shadeView.setBackgroundColor(color);
        shadeView.setVisibility(View.VISIBLE);
    }

    public int getShadeAlph() {
        if (A.brightnessValue > 0 || A.brightnessValue == -100)
            return 0;
        int value = 1 - A.brightnessValue;
        if (value > 50)
            value = 50;
        return value * 4;
    }

    public void setLeds() {
        if ((A.adjustLed || A.adjustNightLed) && A.brightnessValue != -100) {
            setLedValue(-1, true); //set = 0 twice will make led ligtht
            if ((A.adjustLed && A.brightnessValue <= A.disableLedValue) || (A.adjustNightLed && A.lastTheme.equals(A.NIGHT_THEME))) {
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        setLedValue(0, true);
                    }
                }.sendEmptyMessage(0);
            }
        } else
            setLedValue(-1, false);
    }

    boolean LedChanged;

    //
    public void setLedValue(float value, boolean force) {
        if (Build.VERSION.SDK_INT < 8) //only Android 2.2 or higher
            return;
        if (!LedChanged && !force)
            return;

        try {
            LedChanged = true;
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            Field buttonBrightness = lp.getClass().getField("buttonBrightness");
            buttonBrightness.set(lp, value);//0 off -1 light: lp.buttonBrightness = value;
            getWindow().setAttributes(lp);
        } catch (Exception e) {
            LedChanged = false;
            A.error(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void do_page_up() {
        if (webViewPageUp())
            return;
        if (A.isInAutoScroll)
            return;

        if (A.flip_animation > 2)
            prePriorChapterForPageUp();
        pageScroll(PAGE_UP);
    }

    public void do_page_down() {
        if (webViewPageDown())
            return;
        if (A.isInAutoScroll)
            return;
        pageScroll(PAGE_DOWN);
    }

    void do_open_file(String nextFile) {
        if (!T.isFile(nextFile)) {
            T.showToastText(this, nextFile + getString(R.string.not_exists));
            return;
        }

        if (!A.lastFile.equals(nextFile))
            saveLastPostion(true);
        A.lastFile = nextFile;
        if (pdf != null)
            restartReaderToTxt();
        A.clearTxts();
        clearTxtView();
        clearpreNextCache();
        initExtras(false);
        if (!isPdf())
            A.loadVisualOptions(false);
        reloadBook();
    }

    public void restartReaderToMain() {
        restartWhenLowMemory(100, true, 0);
    }

    public void restartReaderToTxt() {
        A.SaveOptions(this);
        restartReaderIntent();
//		restartWhenLowMemory(100, false, 0);
    }

    void do_prior_file() {
        int index = A.getFileList().indexOf(A.lastFile);
        if (index > 1) {

            final String nextFile = A.getFileList().get(index - 1);
            T.showToastText(ActivityTxt.this, T.getFilename(nextFile), 1);
            do_open_file(nextFile);

        } else
            T.showToastText(this, getString(R.string.no_prior_file), 1);
    }

    void do_next_file() {
        int index = A.getFileList().indexOf(A.lastFile);
        if (index < A.getFileList().size() - 1) {

            String nextFile = A.getFileList().get(index + 1);
            T.showToastText(ActivityTxt.this, T.getFilename(nextFile), 1);
            do_open_file(nextFile);

        } else
            T.showToastText(this, getString(R.string.no_next_file), 1);
    }

    private void do_font_size() {
        showBottomBar(1);
    }

    private void do_brightness() {
        showBottomBar(2);
        setBrightnessSKValue();
        bluelightSK.setProgress(A.bluelightOpacity);
    }

    void do_search() {
        inverseLayoutVisible(true);
        if (isPdf()) {
            if (pdf.textReflow)
                pdfTextReflowSwitch(true);
            pdfShowSearchLay();
        } else
            showPrefSearch(null);
    }

    void showPrefSearch(String key) {
        hideDotViews();
        if (webViewSearch())
            return;
        if (isPdf()) {
            if (pdf.textReflow)
                pdfTextReflowSwitch(true);
            pdfShowSearchLay();
            pdfSearchEdit.setText(key);
            return;
        }

        new PrefSearch(ActivityTxt.this, new PrefSearch.OnClickResult() {
            public void onClick(int resultId) {
                doPrefSearchResult(resultId, true);
            }
        }, key).show();
    }

    private void doPrefSearchResult(int resultId, boolean showDialogAndSaveLinkBack) {
        if (PrefSearch.results == null)
            return;
        if (showDialogAndSaveLinkBack) {
            if (link_backs != null)
                link_backs.clear();
            saveLinkBackInfo(true);
        }
        hideDotViews();
//		linkBackSetVisible();
        search_result = PrefSearch.results.get(resultId);
        PrefSearch.SearchResult sr = search_result;
        boolean sameHtmlSrc = A.lastChapter == sr.chapter && A.lastSplitIndex == sr.splitIndex;
        A.lastChapter = sr.chapter;
        A.lastSplitIndex = sr.splitIndex;
//				A.lastPosition = sr.position;
        int l = getDisplayTextLength(txtView, txtScroll); //v2.3.3
        A.lastPosition = sr.start > l / 4 ? sr.start - l / 4 : sr.start > 80 ? sr.start - 80 : sr.start;

        switch (getBookType()) {
            case A.FILE_TXT:
//				long t1 = txtView.getLayout().getLineStart(txtView.getLayout().getLineForVertical(txtScroll.getScrollY()));
//				int end = A.getLastDisplayLine(isPage2Visible() ? txtScroll2 : txtScroll, -1);
//				long t2 = txtView.getLayout().getLineEnd(end);
//				t1 = A.getTxtRealPos(t1);
//				t2 = A.getTxtRealPos(t2);
//				if (sr.start >= t1 && sr.end < t2) {
//					txtView.hStart = txtView2.hStart = (int) A.getTxtDisplayPos(sr.start, false);
//					txtView.hEnd = txtView2.hEnd = (int) A.getTxtDisplayPos(sr.end, false);
//					txtView.postInvalidate();
//					txtView2.postInvalidate();
//				} else { //todo: c209.txt 搜索"她", 第47/48可以, 49/50不能定位
                reloadBook();
                txtView.hStart = (int) A.getTxtDisplayPos(sr.start, false);
                txtView.hEnd = (int) A.getTxtDisplayPos(sr.end, false);
//				}
                break;
            case A.FILE_HTML:
            case A.FILE_EBOOK:
                if (sameHtmlSrc) {
                    txtView.hStart = txtView2.hStart = sr.start;
                    txtView.hEnd = txtView2.hEnd = sr.end;
                    int p1 = txtView.getLayout().getLineStart(txtView.getLayout().getLineForVertical(txtScroll.getScrollY()));
                    int last = A.getLastDisplayLine(isPage2Visible() ? txtScroll2 : txtScroll, -1);
                    int p2 = txtView.getLayout().getLineEnd(last);
                    if (txtView.hStart >= p1 && txtView.hEnd < p2) {
                        txtView.postInvalidate();
                        txtView2.postInvalidate();
                    } else
                        txtScrollNoDelay(getSearchResultLocation());
                } else {
                    if (showDialogAndSaveLinkBack)
                        createProgressDlg(Html.fromHtml(sr.html).toString());
                    locate_to_search_result = true;
                    reloadBook();
                    txtView.hStart = sr.start;
                    txtView.hEnd = sr.end;
                }
                break;
        }

        updateProgressStatus();
        if (PrefSearch.results.size() > 1)
            showSearchPanel();
    }

    boolean locate_to_search_result;
    PrefSearch.SearchResult search_result;

    protected int getSearchResultLocation() {
        locate_to_search_result = false;
        if (search_result == null)
            return (int) A.lastPosition;
//		String text = txtView.getText2();
//		String key = A.lastSearchKey;
//		if (!A.searchCaseSensitive) {
//			text = text.toLowerCase();
//			key = key.toLowerCase();
//		}
//		int pos = search_result.start;
//		int i = -1;
//		int i1 = text.indexOf(key, pos);
//		if (i1 != pos) {
//			int i2 = text.lastIndexOf(key, pos);
//			i = i2 == -1 || (i1 != -1 && i1 - pos < (pos - i2) * 2) ? i1 : i2;
//		}
//		if (i != -1 && i != pos && Math.abs(i - pos) < 1000) {
//			txtView.hStart = i;
//			txtView.hEnd = txtView.hStart + key.length();
//			return i > 80 ? i - 80 : i;
//		} else {
        if (txtView.hStart == -1) {
            txtView.hStart = search_result.start;
            txtView.hEnd = search_result.end;
        }
        return (int) A.lastPosition;
//		}
    }

    PrefChapters chapterDlg;

    void do_show_chapters(int tab) { //0 chapters, 1 bookmarks, 2 notes
        try {
            String errorMsg = null;
            switch (getBookType()) {
                case A.FILE_TXT:
                case A.FILE_HTML:
                    if (A.getTxtChapters().size() == 0)
                        errorMsg = getString(R.string.no_chapter_found);
                    break;
                case A.FILE_EBOOK:
                    if (A.ebook.getChapters().size() == 0)
                        errorMsg = getString(R.string.no_chapter_found);
                    break;
                case A.FILE_PDF:
                    pdfGetToc();
                    if (pdfToc.size() == 0)
                        errorMsg = getString(R.string.no_chapter_found);
                    break;
            }
            if (tab == 0 && errorMsg != null) {
                T.showToastText(this, errorMsg);
//				return;
            }

            selfPref = this;
            chapterDlg = new PrefChapters(ActivityTxt.this, tab, new PrefChapters.OnChangeChapter() {
                public void onGetChapter(int chapterId, int splitIndex, long position, boolean fromChapterList) {
                    if (A.lastChapter > 0)
                        saveLinkBackInfo(true);
                    if (fromChapterList && isPdf()) {
                        A.lastPosition = pdfToc.get(chapterId).pageNumber;
                        pdfGotoPage((int) A.lastPosition, true);
                        showReadProgress(0);
                        return;
                    }

//					forceNotSaveZeroPosition = false;
                    A.lastSplitIndex = splitIndex;
                    switch (getBookType()) {
                        case A.FILE_TXT:
                        case A.FILE_HTML:
                            A.lastPosition = fromChapterList ?
                                    A.getTxtChapters().get(chapterId).getDisplayPosition(chapterId) : position;
                            reloadBook();
                            checkStatusBar();
                            resetFlipCache();
                            break;
                        case A.FILE_EBOOK:
                            if (oldPriorSplitHtmls != null && oldPriorSplitHtmls.size() > 1)
                                oldPriorChapterText = null;
                            A.lastChapter = chapterId;
                            A.lastPosition = position;
                            clearTxtView();
                            A.SaveOptions(ActivityTxt.this);
                            if (isBigChapter(A.lastChapter))
                                createProgressDlg(A.ebook.getChapters().get(A.lastChapter).name);
                            ebookPageUp = false;
                            reloadBook();
                            break;
                    }
                    if (A.immersive_fullscreen && dualPageEnabled())
                        handler.sendEmptyMessageDelayed(CHECK_DUAL_PAGE, 500);
                }
            }, false);

            chapterDlg.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    A.setSystemUiVisibility(true);
                    //hide_ebook_cover();
                    chapterDlg = null;
                }
            });
            chapterDlg.show();
            A.moveStart = false;

        } catch (Exception e) {
            A.error(e);
        }
    }

    public void createBookmark(String bookmark, boolean createNewAfterDeleteOld, boolean ask) {
        int id = A.getBookmarksId(A.lastFile);
        final Bookmarks bms = id == -1 ? new Bookmarks(A.lastFile) : A.getBookmarks().get(id);
        if (id == -1)
            A.getBookmarks().add(bms);

        if (isPdf())
            A.lastPosition = pdfGetCurrPageNo();
        saveLastPostion(false);
        boolean exist = false;
        for (Bookmark bm : bms.list)
            if (bm.position == A.lastPosition && (isPdf() || (bm.chapter == A.lastChapter && bm.splitIndex == A.lastSplitIndex))) {
                bms.remove(bm);
                exist = true;
                break;
            }

        if (!exist || createNewAfterDeleteOld) {
            if (bookmark == null)
                bookmark = "(" + createBookmarkProgressText() + ") " + generateBookmarkName();
            if (ask && A.bookmarkManually) {
                new PrefEditBookmark(this, bookmark, new PrefEditBookmark.OnAfterEdit() {
                    public void AfterEdit(String result, Integer editColor) {
                        createBookmark2(bms, result);
                        if (!updateForFitHardwareAccelerate())
                            contentLay.postInvalidate();
                    }
                }, null);
            } else
                createBookmark2(bms, bookmark);
        }

        A.lastFileAnnotUpdated = true;
        if (!updateForFitHardwareAccelerate())
            contentLay.postInvalidate();
    }

    public String createBookmarkProgressText() {
        return isWebBook() ? A.ebook.getChapters().get(A.lastChapter).name
                : isPdf() ? (A.lastPosition + 1) + "/" + m_doc.GetPageCount() : getPercentStr2();
    }

    private void createBookmark2(Bookmarks bms, String bookmark) {
        Bookmark bm = new Bookmark(A.lastFile, bookmark, A.lastChapter, A.lastSplitIndex, A.lastPosition,
                System.currentTimeMillis(), A.getBookmarkColor());
        bms.list.add(bm);
        BookDb.addBookmark(bm);
        T.showToastText(this, getString(R.string.add_bookmark), bookmark, 0);
    }

    private void forceUpdateForFitHardwareAccelerate(int delay) {
        if (A.fitHardwareAccelerated)
            handler.sendEmptyMessageDelayed(FORCEFITHARDWAREACCELERATE, delay);
    }

    private boolean updateForFitHardwareAccelerate() {
        if (A.fitHardwareAccelerated) {
            if (pdf != null) {
                pdf.invalidate();
            } else {
                if (!isPaused && isAutoRotate() && txtViewTextBroken()) { //auto-sensor rare error
                    A.log("******txtView.getText().length() > 0 && txtView.getLineCount() == 0******", true);
                    clearTxtView();
                    reloadBookOnError();
                    ignoreSavePosTime = SystemClock.elapsedRealtime();
                    T.showToastText(this, getString(R.string.loading) + "...");
                    handler.sendEmptyMessageDelayed(FORCEFITHARDWAREACCELERATE, 1000);
                    return false;
                }

                txtView.invalidate();
                txtView2.invalidate();
            }
            contentLay.postInvalidate();
            return true;
        }
        return false;
    }

    void do_bookmark() {
		/*if (web == null)
			try {
				selfPref = this;
				saveLinkBackInfo(false);
				Intent i = new Intent(this, BookMarkAct.class);
				i.putExtra("bookFile", A.lastFile);

				saveLastPostion(true);
				String bookmark = generateBookmarkName();
				i.putExtra("bookmark", bookmark);
				i.putExtra("position", A.lastPosition);
				i.putExtra("chapter", A.lastChapter);
				i.putExtra("splitIndex", A.lastSplitIndex);
				i.putExtra("progress", createBookmarkProgressText());

				inverseLayoutVisible(true);
				A.lastFileFromBookmark = null;
				startActivityForResult(i, 1);
			} catch (Exception e) {
				A.error(e);
			}*/
        do_show_chapters(1);
    }

    public String generateBookmarkName() {
        String bookmark;
        int addLength = A.isChinese ? 38 : 78;
        if (isPdf()) {
            bookmark = pdfCreateReflowTextForShow(pdfGetCurrPageNo()).trim();
            if (bookmark.length() > addLength)
                bookmark = bookmark.substring(0, addLength);
            if (bookmark.length() == 0)
                bookmark = "" + A.lastPosition;
        } else {
            int line = txtView.getLayout().getLineForVertical(txtScroll.getScrollY());
            int offset = txtView.getLayout().getLineStart(line);
            if (offset == 0 && isWebBook())
                offset = A.ebook.getChapters().get(A.lastChapter).name.length() + 1;
            bookmark = txtView.getText2();
            if (bookmark.length() > offset + addLength) {
                bookmark = bookmark.substring(offset, offset + addLength);
                int i = bookmark.lastIndexOf(" ");
                if (bookmark.length() - i < 8)
                    bookmark = bookmark.substring(0, i);
            } else if (bookmark.length() < 3)
                bookmark = T.dateTimeToStr(System.currentTimeMillis());
            else
                bookmark = bookmark.substring(offset, bookmark.length());
        }
        bookmark = bookmark.replace("\n", " ").replace(A.INDENT_CHAR, ' ').trim() + "...";
        return bookmark;
    }

    public long getBookLength() {
        try {
            switch (getBookType()) {
                case A.FILE_HTML:
                    return web != null ? web.getContentHeight() : htmlText.length();
                case A.FILE_EBOOK:
                    return getEBookTotalSize();
                case A.FILE_PDF:
                    return m_doc.GetPageCount();
                default:
                    return A.txtLength();
            }
        } catch (Exception e) {
            A.error(e);
            return 1;
        }
    }

    private long getEBookTotalSize() { //v1.9.4
        int textSize = BaseEBook.getBookCharCountIfComplete(-1);
        return textSize > 0 ? textSize : A.ebook.getTotalSize();
    }

    private long getCurrentPosition() {
        try {
            switch (getBookType()) {
                case A.FILE_HTML:
                    if (web != null)
                        return web.getScrollY();
                    if (A.noSplitHtmls())
                        return (long) htmlSrc.length() * (long) txtScroll.getScrollY() / txtView.getRealHeight();
                    else {
                        long d = (long) htmlSrc.length() * (long) txtScroll.getScrollY() / txtView.getRealHeight();
                        return A.lastSplitIndex * A.maxHtmlChapterSize + d;
                    }
                case A.FILE_EBOOK:
                    if (web != null)
                        return BaseEBook.getPriorTextLength2(A.lastChapter) + web.getScrollY();
                    int bookSize = BaseEBook.getBookCharCountIfComplete(-1);
                    if (bookSize > 0) { //v1.9.5
                        Chapter c = A.ebook.getChapters().get(A.lastChapter);
                        int bookLeft = BaseEBook.getBookCharCountIfComplete(A.lastChapter);
                        int line = txtView.getLayout().getLineForVertical(txtScroll.getScrollY());
                        long chapterSize = txtView.getText().length();
                        if (chapterSize == 0)
                            chapterSize = 1;
                        long chapterLeft = chapterSize - txtView.getLayout().getLineStart(line);
                        if (!A.noSplitHtmls()) {
                            if (A.lastChapter == 0 && A.lastSplitIndex == 0 && A.lastPosition == 0)
                                return 0;
                            long left = 0, total = 0;
                            for (int i = 0; i < A.splitHtmls.size(); i++) {
                                total += A.splitHtmls.get(i).length();
                                if (i > A.lastSplitIndex)
                                    left += A.splitHtmls.get(i).length();
                            }
                            chapterSize = total;
                            chapterLeft += left;
                        }
                        long result = (bookSize - bookLeft) - c.pure_text_length * chapterLeft / chapterSize;
                        return result > 0 ? result : 0;
                    }

                    long tp = A.lastPosition;
                    if (!A.noSplitHtmls()) {
                        long d = (long) txtView.getText().length() * (long) txtScroll.getScrollY() / txtView.getRealHeight();
                        tp = A.lastSplitIndex * A.maxHtmlChapterSize + d;
                    } else if (A.ebook.isHtml()) {
                        if (txtView.getText().length() != 0)
                            tp = A.ebook.getChapters().get(A.lastChapter).size * tp / txtView.getText().length();
                    }
                    return BaseEBook.getPriorTextLength2(A.lastChapter) + tp;
                default:
                    return A.lastPosition;
            }
        } catch (OutOfMemoryError e) {
            A.error(e);
        } catch (Exception e) {
            A.error(e);
        }
        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == StoreWebView.STORE_LOGIN || requestCode == StoreWebView.STORE_LOGIN2) {
                if (requestCode == StoreWebView.STORE_LOGIN && readWeb != null)
                    readWeb.returnLoginToken();
                W.initAccount(false);
                if (W.accountOk()) {
                    T.deleteFile(W.getUserPicCacheFile());
                    ActivityMain.reloadStoreAfterLogin();
                    if (requestCode == StoreWebView.STORE_LOGIN2 && !T.isNull(yueWenUrlBeforeLogin))
                        openVipBrowser(yueWenUrlBeforeLogin);
                }
                return;
            }

            if (requestCode == 1) { //open file from bookmark
                if (A.lastFileFromBookmark != null) {
                    if (A.lastFileFromBookmark.equals(A.lastFile)) {
                        if (tmpPositionInfo != null && link_backs.indexOf(tmpPositionInfo) == -1)
                            link_backs.add(0, tmpPositionInfo);
                        switch (getBookType()) {
                            case A.FILE_TXT:
                                showTxtByPosition(A.lastPosition, null);
                                updateProgressStatus();
                                break;

                            case A.FILE_HTML:
                                if (A.noSplitHtmls()) {
                                    int line = txtView.getLayout().getLineForOffset((int) A.lastPosition);
                                    int y = txtView.getLineTop2(line);
                                    txtScrollTo(y);
                                    updateProgressStatus();
                                } else
                                    do_open_file2(A.lastFileFromBookmark);

                                break;

                            case A.FILE_EBOOK:
                                isTxtScrollReady = false;
                                ebookPageUp = false;
                                showEBookByPosition(A.lastChapter, A.lastSplitIndex, A.lastPosition, true);
                                isTxtScrollReady = true;
                                break;

                            case A.FILE_PDF:
                                pdfGotoPage((int) A.lastPosition, true);
                                if (isPdfReflow() && A.lastSplitIndex == 10000)
                                    txtScrollByDelay(A.lastChapter);
                                updateProgressStatus();
                                break;
                        }
                    } else
                        do_open_file2(A.lastFileFromBookmark);
                } else if (getBookType() == A.FILE_TXT && dualPageEnabled())
                    showTxtByPosition(A.lastPosition, null);

                resetFlipCache();
            }

            if (requestCode == 123) { //after show ClickTip
//				if (A.noMenuKey)
//					T.showAlertText(this, getString(R.string.tip), getString(R.string.no_menu_key));
            }

            if (requestCode == 124) { //from play video
                isPaused = false;
//				contentLay.refreshDrawableState();
                contentLay.postInvalidate();
            }

            if (requestCode == 125) {
                checkOptionsChanged();
            }

            if (requestCode == 126 && isWebBook()) {
                reloadCurWebChapter();
            }

            if (requestCode == 127) {
                if (A.isTablet && A.landscape2PageMode
                        && A.trySplashAdTime > System.currentTimeMillis() - 10000) {
                    A.log("******splashAd in dualPageMode: restart intent******");
//					handler.sendEmptyMessageDelayed(RESTART_INTENT_HANDLER, 100);
                    restartReaderIntent();
                }
            }

        } catch (Exception e) {
            A.error(e);
        }
    }

    private void do_open_file2(String filename) {
        Message msg = new Message();
        msg.obj = filename;
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                do_open_file((String) msg.obj);
            }
        }.sendMessage(msg);
    }

    void do_book_info() {
        hideProgressDlg();
        boolean wb = A.isWebBook();

        try {
            String text;

            if (isPdf()) {
                String author = m_doc.GetMeta("Author");
                text = "<big><b>" + A.getBookName() + "</b>" + (author == null || author.length() == 0 ? "" : " - " + author) + "</big><br><br><b>"
                        + getString(R.string.filename) + ":</b>  " + T.getFilename(A.lastFile) + "<br><b>" + getString(R.string.location)
                        + ":</b>  " + T.getFilePath(A.lastFile) + "<br><b>" + getString(R.string.file_size)
                        + ":</b>  " + Formatter.formatFileSize(this, new File(A.lastFile).length()) + "<br><b>" + getString(R.string.total_pages)
                        + ":</b>  " + m_doc.GetPageCount() + "<br><b>" + getString(R.string.current_page) + ":</b>  " + (pdfGetCurrPageNo() + 1)
                        + "<br><b>Creator:</b>  " + m_doc.GetMeta("Creator") + "<br><b>Subject:</b>  " + m_doc.GetMeta("Subject")
                        + "<br><b>Keywords:</b>  " + m_doc.GetMeta("Keywords") + "<br><b>Producer:</b>  " + m_doc.GetMeta("Producer")
                        + "<br><b>Version:</b>  " + m_doc.GetMeta("ver");
            } else {
                long l = getBookLength();
                long total, current;
                if ((getBookType() == A.FILE_EBOOK) || (getBookType() == A.FILE_TXT && A.getTxts().size() > 3)) {
                    current = getAboutCurrentPage(0);
                    total = getTotalPages() / 100000;
                    if (total == 0)
                        total = 1;
                } else {
                    total = txtView.getRealHeight() / A.getPageHeight() + 1;
                    current = total * A.lastPosition / l + 1;
                }

                String pages = total < 10 ? "" :
                        "<br><b>" + getString(R.string.total_pages)
                                + ":</b>  " + total + "<br><b>" + getString(R.string.current_page) + ":</b>  ";

                text = wb ? "" :
                        "<b>" + getString(R.string.filename) + ":</b>  " + T.getFilename(A.lastFile) + "<br><b>" + getString(R.string.location)
                                + ":</b>  " + T.getFilePath(A.lastFile) + "<br><b>" + getString(R.string.file_size) + ":</b>  "
                                + Formatter.formatFileSize(this, new File(A.lastFile).length())
                                + pages
                                + current;

                switch (getBookType()) {
                    case A.FILE_TXT:
                    case A.FILE_HTML:
                        if (A.getTxtChapters().size() > 0) {
                            int id = A.getTxtChapterId(A.lastPosition, null, null);
                            text = text + "<br><b>" + getString(R.string.total_chapters) + ":</b> " + A.getTxtChapters().size() + "<br><b>"
                                    + getString(R.string._current_chapter) + ":</b> " + (id + 1) + "<br>"
                                    + A.getTxtChapters().get(id).chapter_trim;
                        }
                        break;
                    case A.FILE_EBOOK:
                        String serial = BookDb.createSerialText(T.stringList2Text(A.ebook.categories));
                        String author = A.ebook.getAuthor();
                        text = "<big><b>" + A.ebook.getBookName() + "</b>"
                                + (serial != null ? "<br>" + serial + (author.equals("") ? "" : "<br>" + author) : (author.equals("") ? "" : " - " + author))
                                + "</big>" + (A.ebook.description != null && !A.ebook.description.equals("") ? "<br><br><i>" + A.ebook.description + "</i>" : "")
                                + "<br><br>" + text + "<br><b>" + getString(R.string.total_chapters) + ":</b> " + A.ebook.getChapters().size()
                                + "<br><b>" + getString(R.string._current_chapter) + ":</b> " + (A.lastChapter + 1) + ", "
                                + A.ebook.getChapters().get(A.lastChapter).name;
                        break;
                }
            }

            String time = "0";
            String speed = "0";
            saveStatisticsInfo();
            BookDb.ReadStatistics r = BookDb.getSavedStatistics(A.lastFile);
            if (r.usedTime > 0 && r.readWords > 0)
                try {
//					time = "" + new java.text.DecimalFormat("0.0").format((float) r.usedTime / 60 / 60 / 1000);
                    time = T.formatInterval(r.usedTime);
                    float minutes = (float) r.usedTime / 60 / 1000;
                    if (minutes > 0)
                        speed = "" + new java.text.DecimalFormat("0").format((r.readWords / minutes));
                } catch (Exception e) {
                    A.error(e);
                }

            String readDate = BookDb.getReadDate2(A.lastFile, r);
            info_text = text + "<br><b>" + getString(R.string.read_hour) + ":</b> " + time + "<br><b>"
                    + getString(R.string.read_speed) + ": </b>"
                    + speed + getString(R.string.read_speed2) + "<br>"
                    + (readDate != null ? "<b>" + getString(R.string.date_read_history) + "</b><br>" + readDate : "")
                    + (wb ? "<b>书源: </b>" + A.ebook.online_site.name() : "<b>" + getString(R.string.chars_in_book) + ":</b>  ");
            info_sv = LayoutInflater.from(this).inflate(R.layout.book_info, null);

            ((TextView) info_sv.findViewById(R.id.rdmTextView01)).setText(Html.fromHtml(info_text));
            ImageView iv = (ImageView) info_sv.findViewById(R.id.imageView1);
            Drawable d = A.getBookCover(A.lastFile);
            if (d != null) {
                iv.setImageDrawable(d);
                iv.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent i = new Intent(ActivityTxt.this, PicGalleryShow.class);
                        if (A.ebook != null && A.ebook.getCoverFile() != null)
                            i.putExtra("ebookImage", A.ebook.getCoverFile());
                        else {
                            i.putExtra("imageFile", A.getBookCoverFile(A.lastFile));
                            i.putExtra("singPicOnly", true);
                        }
                        startActivity(i);
                    }
                });
            } else
                iv.setVisibility(View.GONE);

            //-----------word count(1)------

            int textSize = -1;
            if (!wb)
                switch (getBookType()) {
                    case A.FILE_TXT:
                        textSize = BaseEBook.getTxtUnReadWordCount(new AfterGetUnReadWords() {
                            public void refresh(int wordCount) {
                                handler.sendEmptyMessage(BOOK_INFO_COUNT_DONE);
                            }
                        }, true);
                        break;
                    case A.FILE_HTML:
                        do_book_info_count_done();
                        break;
                    case A.FILE_EBOOK:
                        textSize = BaseEBook.getUnReadWordCountFrom(A.ebook, 0, new AfterGetUnReadWords() {
                            public void refresh(int wordCount) {
                                handler.sendEmptyMessage(BOOK_INFO_COUNT_DONE);
                            }
                        }, true);
                        break;
                    case A.FILE_PDF:
                        textSize = BaseEBook.getPdfUnReadWordCoun(new AfterGetUnReadWords() {
                            public void refresh(int wordCount) {
                                handler.sendEmptyMessage(BOOK_INFO_COUNT_DONE);
                            }
                        }, true);
                        break;
                }
            else
                info_sv.findViewById(R.id.progressBar3).setVisibility(View.GONE);
            if (textSize > 0)
                do_book_info_count_done();

            //-----------word count(2)------

            A.setDialogNightState(info_sv);
            MyDialog.Builder dlg = new MyDialog.Builder(this);
            dlg.setTitle(getString(R.string.book_info)).setView(info_sv);
            dlg.setPositiveButton(R.string.jiarushujia, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    do_add_favorite();
                }
            });
            if (isWebBook())
                dlg.setNegativeButton(A.getFinishBooks().contains(A.lastFile) ? "取消完本标记" : "标记为已完本",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (A.getFinishBooks().contains(A.lastFile))
                                    A.deleteFinishBook(A.lastFile);
                                else
                                    A.addFinishBook(A.lastFile);
                            }
                        });
            dlg.setNeutralButton(getString(R.string.more) + "... ", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    final String[] items = new String[3];
                    items[0] = getString(R.string.fenxiangbenshujindu);
                    items[1] = getString(R.string.qingkongbenshutongji);
                    items[2] = getString(isWebBook() ? R.string.qingkonghuanchun : R.string.send_file);

                    new MyDialog.Builder(ActivityTxt.this).setItems(items, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0)
                                doShareReadState(true);
                            if (which == 1) {
                                BookDb.deleteStatistics(A.lastFile);
                                statisticsTime = System.currentTimeMillis();
                                showReadProgress(0);
                            }
                            if (which == 2) {
                                if (isWebBook()) {
                                    clearBookCache();
                                } else
                                    A.sendFile(ActivityTxt.this, A.lastFile);
                            }
                        }
                    }).show();
                }
            });
            dlg.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    info_sv = null;
                    info_text = null;
                }
            });
            dlg.show();
            inverseLayoutVisible(true);
        } catch (Throwable e) {
            System.gc();
            A.error(e);
        }
    }

    private void clearBookCache() {
        int size1 = T.getFolderSize(T.getFilePath(A.lastFile));
        WB.clearBookCache(A.lastFile);
        int size2 = T.getFolderSize(T.getFilePath(A.lastFile));
        if (size1 > size2)
            T.showAlertText(ActivityTxt.this, getString(R.string.yiqingkongbingshifang) + "\n\n " + Formatter.formatFileSize(ActivityTxt.this, (size1 - size2)));
    }

    private View info_sv;
    private String info_text;

    private void do_book_info_count_done() {
        if (isFinishing() || info_sv == null || info_text == null)
            return;

        try {
            long size = getBookLength();
            long word_count = BaseEBook.getBookWordCountIfComplete();
            String size_text = pdf == null ? NumberFormat.getInstance().format(size) //Formatter.formatFileSize(this, size)
                    : ""; //in pdf, this is page count

            if (word_count > 0) {
                size_text = T.buildString(size_text, "<br><b>", getString(R.string.words_in_book), ":</b>  ",
                        NumberFormat.getInstance().format(word_count));
                //Formatter.formatFileSize(this, word_count));

                int book_left = getBookLeftWords();
                int chapter_left = getChapterLeftWords();
                if (chapter_left > 0)
                    book_left += chapter_left;

                if (book_left > 0) {
                    int speed = getReadingSpeed();
                    int min = book_left / speed;
                    int book_hour = min / 60;
                    int book_min = min % 60;

                    String chapterLeft = A.ebook != null && chapter_left > 0 ? "<br><br>" +
                            String.format(getString(R.string.remaining_time_chapter) + "", chapter_left / speed) : "<br>";
                    size_text = T.buildString(size_text, "<i>", chapterLeft, "<br>",
                            String.format(getString(R.string.remaining_time_book), book_hour, book_min), "</i>");
                }
            }

            ((TextView) info_sv.findViewById(R.id.rdmTextView01)).setText(Html.fromHtml(info_text + size_text));

        } catch (Throwable e) {
            System.gc();
            A.error(e);
        }

        info_sv.findViewById(R.id.progressBar3).setVisibility(View.GONE);
    }

    protected void do_add_favorite() {
        BookInfo bi = BookDb.getBook(A.lastFile);
        if (bi == null)
            bi = BookDb.createBookInfoFromFile(BookDb.DEFAULT_FAV, A.lastFile, false);
        else if (bi.favorite.equals(""))
            bi.favorite = BookDb.DEFAULT_FAV;

        if (bi != null) {
            PrefEditBook dlg = new PrefEditBook(ActivityTxt.this, new OnBookEdited() {
                public void onSaveBookInfo(boolean shelfChanged) {
                    A.setSystemUiVisibility(true);
                }
            }, false, bi);
            dlg.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    A.setSystemUiVisibility(true);
                }
            });
            dlg.show();
        }
    }

    void do_options_menu(View v) {
        showOptionsMenu(v);
    }

    void do_change_theme() {
        if (!A.isInAutoScroll)
            showThemeWindow();
    }

    void do_disable() {
    }

    void do_none() {
    }

    boolean doKeyDown(int keyCode) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (doEvent(A.doDPadUp))
                    return true;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (doEvent(A.doDPadDown))
                    return true;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (doEvent(A.doDPadLeft))
                    return true;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (doEvent(A.doDPadRight))
                    return true;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (doEvent(A.doDPadCenter))
                    return true;
                break;
            case KeyEvent.KEYCODE_SEARCH:
                if (A.isSpeaking && A.doSearchKey != A.DO_SPEAK) { //v1.2.17
                    showTtsPanel(true);
                    return true;
                }
                if (doEvent(A.doSearchKey))
                    return true;
                break;
            case KeyEvent.KEYCODE_CAMERA:
                if (doEvent(A.doCameraKey))
                    return true;
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_PAGE_UP:
            case 105:
                if (!layoutVisible) {
                    if (A.noMenuKey && A.fullscreen && (A.doVolumeKeyUp == A.DO_PAGE_UP || A.doVolumeKeyUp == A.DO_PAGE_DOWN))
                        hideSystemUi();
                    if (A.doVolumeKeyUp == A.DO_BRIGHTNESS) {
                        setScreenBrightness(A.brightnessValue + 1, true);
                        return true;
                    }
                    if (doEvent(A.doVolumeKeyUp))
                        return true;
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case 106:
                if (!layoutVisible) {
                    if (A.noMenuKey && A.fullscreen && (A.doVolumeKeyDown == A.DO_PAGE_DOWN || A.doVolumeKeyDown == A.DO_PAGE_UP))
                        hideSystemUi();
                    if (A.doVolumeKeyUp == A.DO_BRIGHTNESS) {
                        setScreenBrightness(A.brightnessValue - 1, true);
                        return true;
                    }
                    if (doEvent(A.doVolumeKeyDown))
                        return true;
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                if (!A.isInAutoScroll && !A.isSpeaking) {
                    boolean result = false;
//					if (navigateBackLinks())
//						result = true;
                    if (doEvent(A.doBackKey))
                        result = true;
                    if (result)
                        return true;
                }
                break;
            case KeyEvent.KEYCODE_HOME:
                if (doEvent(A.doHomeKey))
                    return true;
                break;
            case KeyEvent.KEYCODE_MENU:
                if (doEvent(A.doMenuKey))
                    return true;
                break;
        }

        return false;
    }

    private void adjustVolume(boolean up) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int cur = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int step = max / 15;
        if (step < 1)
            step = 1;
        if (up)
            cur += step;
        else
            cur -= step;
        if (cur < 0)
            cur = 0;
        if (cur > max)
            cur = max;
        am.setStreamVolume(AudioManager.STREAM_MUSIC, cur, 0);
        T.showToastText(ActivityTxt.this, "翻页音量: " + cur + "/" + max, 0, Gravity.CENTER);
    }

    void doTouchDown(int id) {
        switch (id) {
            case A.CLICK_TOP:
                doEvent(A.doTapScreenTop);
                break;
            case A.CLICK_BOTTOM:
                doEvent(A.doTapScreenBottom);
                break;
            case A.CLICK_LEFT:
                doEvent(A.doTapScreenLeft);
                break;
            case A.CLICK_RIGHT:
                doEvent(A.doTapScreenRight);
                break;
        }
    }

    public boolean doEvent2(int action) { //long-tap event
        if (action == A.DO_BOOKMARK)
            createBookmark(null, false, true);
        else
            doEvent(action);
        return true;
    }

    public boolean doEvent(int action) {
        if (action == A.DO_NONE)
            return false;

        switch (action) {
            case A.DO_PAGE_UP:
                do_page_up();
                break;
            case A.DO_PAGE_DOWN:
                do_page_down();
                break;
            case A.DO_PRIOR_FILE:
                do_prior_file();
                break;
            case A.DO_NEXT_FILE:
                do_next_file();
                break;
            case A.DO_SEARCH:
                do_search();
                break;
            case A.DO_AUTOSCROLL:
                do_AutoScroll(!A.isInAutoScroll);
                break;
            case A.DO_SELECT_TEXT:
                do_text_select(isSingleTapForTextSelection());
                break;
            case A.DO_EXIT_READER:
                terminate_reader();
                break;
            case A.DO_BACK_TO_BOOK_STACK:
                do_back_to_book_stack();
                break;
            case A.DO_BOOKMARK:
                do_bookmark();
                break;
            case A.DO_BOOK_STATE:
                do_book_info();
                break;
            case A.DO_NAVIGATE_MENU:
                showMoreOptionsMenu(null);
                break;
            case A.DO_OPTIONS_MENU:
                do_options_menu(null);
                break;
            case A.DO_RANDOM_THEME:
                do_change_theme();
                break;
            case A.DO_FONT_SIZE:
                do_font_size();
                break;
            case A.DO_PRIOR_CHAPTER:
                do_prior_chapter();
                break;
            case A.DO_NEXT_CHAPTER:
                do_next_chapter();
                break;
            case A.DO_CHAPTERS:
                do_show_chapters(0);
                break;
            case A.DO_SPEAK:
                do_speak();
                break;
            case A.DO_BRIGHTNESS:
                do_brightness();
                break;
            case A.DO_VISUAL:
                showVisualOptions();
                break;
            case A.DO_CONTROL:
                showControlOptions();
                break;
            case A.DO_MISC:
                showMiscOptions();
                break;
        }

        return true;
    }

    @SuppressWarnings("WrongConstant")
    void setScreenOrientation() {
//		if (android.os.Build.VERSION.SDK_INT != 26)
        setRequestedOrientation(A.getScreenOrientation(A.screenState));
    }

    private boolean isAutoRotate() {
        return A.screenState == 0;
//		return A.screenState == 0 || android.os.Build.VERSION.SDK_INT == 26;
    }


    private void generateNextScreenOrientation() {
        int o = getResources().getConfiguration().orientation;
        if (Build.VERSION.SDK_INT >= 9) {
            A.screenState = o == 2 ? 6 : 5;
        } else
            A.screenState = o == 2 ? 2 : 1;
    }

    //-----------------headset----------------
    boolean headsetRegistered = false;
    OpenFile_Receiver headsetReceiver;
    ComponentName mediaButtonReciver;

    private void registerHeadset() {
        if (headsetRegistered)
            return;
        headsetRegistered = true;
        if (A.doHeadsetKey == A.DO_NONE && A.doMediaPlayPause == A.DO_NONE && A.doMediaPlayNext == A.DO_NONE && A.doMediaPlayPrevious == A.DO_NONE)
            return;

        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            ComponentName mediaButtonReciver = new ComponentName(getPackageName(), OpenFile_Receiver.class.getName());
            am.registerMediaButtonEventReceiver(mediaButtonReciver);
            if (Build.VERSION.SDK_INT >= 14) {
                headsetReceiver = new OpenFile_Receiver();
                registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
            }
        } catch (Throwable e) {
            A.error(e);
        }
    }

    private void unregisterHeadset() {
        if (!headsetRegistered)
            return;
        headsetRegistered = false;
        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.unregisterMediaButtonEventReceiver(mediaButtonReciver);
            if (headsetReceiver != null)
                unregisterReceiver(headsetReceiver);
        } catch (Exception e) {
            A.error(e);
        }
    }


    AudioManager.OnAudioFocusChangeListener audioChangeListener;
    boolean audio_focus_lossed, audio_pause_from_listener;

    private void registerAudioFocus() {
        if (A.disableAudioFocus)
            return;

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio_focus_lossed = audio_pause_from_listener = false;
        if (audioChangeListener == null)
            audioChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (A.disableAudioFocus)
                        return;

                    A.log("##onAudioFocusChange: " + focusChange);
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
//						if (A.isSpeaking){
//							audio_focus_lossed = true;
//							((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, tts_volume.getProgress()*2/3, 0);
//						}
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        if (A.isSpeaking && !tts_paused) {
                            do_tts_buttons(tts_play, false);
                            audio_pause_from_listener = true;
                        }
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        if (tts_paused && audio_pause_from_listener)
                            do_tts_buttons(tts_play, false);
//						if (A.isSpeaking && audio_focus_lossed){
//							audio_focus_lossed = false;
//							((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, tts_volume.getProgress(), 0);
//						}
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        if (A.isSpeaking && !tts_paused) {
                            do_tts_buttons(tts_play, false);
                            audio_pause_from_listener = true;
                        }
                    }
                }
            };
        int result = am.requestAudioFocus(audioChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        A.log("##requestAudioFocus:" + result);
    }

    private void unregisterAudioFocus() {
        if (audioChangeListener != null) {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(audioChangeListener);
            audioChangeListener = null;
        }
    }

    private Handler receiveHandler;

    public Handler getReceiveHandler() {
        if (receiveHandler == null)
            receiveHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Integer key = (Integer) msg.obj;
                    A.log("received key:" + key + ", msg.waht:" + msg.what);

                    if (msg.what == 1) {//BlueTooth headset keys
                        switch (key) {
                            case KeyEvent.KEYCODE_HEADSETHOOK: //Play/pause: 85 Forward: 87 Backward: 88
                                if (A.doHeadsetKey == A.DO_SPEAK) {
                                    if (A.isSpeaking)
                                        do_tts_buttons(tts_play, false);
                                    else
                                        do_speak();
                                } else
                                    doEvent2(A.doHeadsetKey);
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            case KeyEvent.KEYCODE_MEDIA_PLAY:
                                if (A.doMediaPlayPause == A.DO_SPEAK) {
                                    if (A.isSpeaking)
                                        do_tts_buttons(tts_play, false);
                                    else
                                        do_speak();
                                } else
                                    doEvent2(A.doMediaPlayPause);
                                break;

                            case KeyEvent.KEYCODE_MEDIA_NEXT:
                                if (A.isSpeaking)
                                    do_tts_buttons(tts_next, false);
                                else
                                    doEvent2(A.doMediaPlayNext);
                                break;

                            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                                if (A.isSpeaking)
                                    do_tts_buttons(tts_prior, false);
                                else
                                    doEvent2(A.doMediaPlayPrevious);
                                break;
                        }
                    }

                    if (msg.what == 2) {//HeadsetPlugReceiveer
                        if (key == 0) //2: plug in, 0: plug out
                            if (A.isSpeaking) {
                                if (stopTtsFromCalling == 2) //when end of a call, it also activate this event
                                    stopTtsFromCalling = 0;
                                else if (System.currentTimeMillis() - resumeTime > 3000 && System.currentTimeMillis() - pausedTime > 2000)
                                    stop_speak();
                            }
                    }
                }
            };
        return receiveHandler;
    }

    //-------------shake begin--------
    private long registerHardwaresTime = -1;

    private void registerHardwares() {
//		if (registerHardwaresTime == -1) {
//			new Thread() {
//				@Override
//				public void run() {
//					try {
//						registerHardwaresTime = SystemClock.elapsedRealtime();
//						registerHeadset();
//						registerShakeSensor();
//						registerHardwaresTime = -1;
//						headsetRegistered = true;
//						if (A.tilt_turn_page) {
//							if (A.askForTiltAction)
//								handler.sendEmptyMessageDelayed(LOOP_EVENT_CHECK, 100);
//							else
//								shakeSensorLisener.tilt_turn_page = true;
//						}
//					} catch (Exception e) {
//						A.error(e);
//					}
//				}
//			}.start();
//		} else {//error in register sensor
//			if (SystemClock.elapsedRealtime() - registerHardwaresTime > 2000) {
//				headsetRegistered = false;
//				A.doShakePhone = A.DO_NONE;
//				A.SaveOptions(this);
//			}
//		}
    }

    public void registerShakeSensor() {
        if (A.doShakePhone == A.DO_NONE && !A.tilt_turn_page)
            return;
        if (sensorMgr == null)
            sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorMgr.registerListener(getShakeSensorLisener(), sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterShakeSensor() {
        if (sensorMgr != null)
            sensorMgr.unregisterListener(getShakeSensorLisener());
    }

    public MyShakeSensorListener shakeSensorLisener;
    private SensorManager sensorMgr;

    private MyShakeSensorListener getShakeSensorLisener() {
        if (shakeSensorLisener == null)
            shakeSensorLisener = new MyShakeSensorListener();
        return shakeSensorLisener;
    }

    private boolean doHorizontalSwipe(final boolean fromLeftToRight) {
        if ((fromLeftToRight && A.doSwipeLeftToRight != A.DO_NONE) || (!fromLeftToRight && A.doSwipeRightToLeft != A.DO_NONE)) {
            xFlingTime = 1;
            swipeGuesture = true;
        }

        if ((A.askForSwipeEvent) && (A.doSwipeLeftToRight == A.DO_NEXT_FILE) && (A.doSwipeRightToLeft == A.DO_PRIOR_FILE)) {

            String hint;
            if (fromLeftToRight)
                hint = getString(R.string.swipe_hint_1);
            else
                hint = getString(R.string.swipe_hint_2);

            ScrollView layout = (ScrollView) LayoutInflater.from(this).inflate(R.layout.do_event_confirm, null);
            final TextView textView = (TextView) layout.findViewById(R.id.ofTextView);
            final CheckBox checkBox = (CheckBox) layout.findViewById(R.id.ofNeverAskAgain);
            textView.setText(hint);

            new MyDialog.Builder(this).setTitle(getString(R.string.swipe_gesture)).setView(layout)
                    .setPositiveButton(R.string.ok, new OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            A.askForSwipeEvent = !checkBox.isChecked();
                            if (fromLeftToRight)
                                do_next_file();
                            else
                                do_prior_file();
                        }
                    }).setNegativeButton(R.string.cancel, new OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    A.askForSwipeEvent = !checkBox.isChecked();
                    if (!A.askForSwipeEvent) {
                        A.doSwipeLeftToRight = A.DO_NONE;
                        A.doSwipeRightToLeft = A.DO_NONE;
                    }
                }
            }).show();

            return true;

        } else {
            if (fromLeftToRight)
                return doEvent(A.doSwipeLeftToRight);
            else
                return doEvent(A.doSwipeRightToLeft);
        }

    }

    private int topLayPaddingTop = -1;

    private void setFullscreen(boolean topLayHeightOnly) {
        setStatusBarHeight();
        int l = 0, r = 0, b = 0;
        if (topLayHeightOnly) {
            if (topLayPaddingTop == -1)
                topLayPaddingTop = topLay.getPaddingTop();
            b = topLay.getPaddingBottom();
            l = topLay.getPaddingLeft();
            r = topLay.getPaddingRight();
        }

        if (A.fullscreen) {
            if (!topLayHeightOnly) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                topLaySetPadding(l, topLayPaddingTop + sysbar_height, r, b);
            }
            if (A.fullscreenWithStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//				getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else {
            if (!topLayHeightOnly) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else
//				topLaySetPadding(l, topLayPaddingTop + (A.isCutoutScreen()? sysbar_height : 0), r, b);
                topLaySetPadding(l, topLayPaddingTop, r, b);
        }
    }

    private void topLaySetPadding(int l, int t, int r, int b) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View bar = topLay.findViewById(R.id.notificationBar);
            if (A.fitCutout != 1 && A.isCutoutScreen() && A.fullscreen) {
                bar.setVisibility(View.GONE);
            } else {
                bar.getLayoutParams().height = t;
                bar.setVisibility(View.VISIBLE);
            }
            topLay.setPadding(l, 0, r, b);
        } else
            topLay.setPadding(l, t, r, b);
    }

    public int sysbar_height;
    private boolean sysbarChecked = false;

    private void setStatusBarHeight() {
        if (sysbarChecked)
            return;
        if (A.sysbar_height > 0)
            sysbar_height = A.sysbar_height;

        try {
            Rect r = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            if (r.top > 0) {
                A.sysbar_height = sysbar_height = r.top;
                sysbarChecked = true;
//				A.log("-----------notification bar height: " + r.top);
            } else if (sysbar_height == 0 && !T.isNull(ActivityMain.selfPref)) {
                ActivityMain.selfPref.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                if (r.top > 0)
                    A.sysbar_height = sysbar_height = r.top;
            }
        } catch (Exception e) {
            A.error(e);
        }
    }

    private void createProgressDlg(String info) {
        createProgressDlg(getString(R.string.loading), info);
    }

    private ProgressDialog dissmissDlg;

    private void createProgressDlg(String title, String info) {
        createProgressDlg(title, info, true);
    }

    private void createProgressDlg(String title, String info, boolean useNew) {
        if (!useNew && progressDlg != null) {
            progressDlg.setTitle(title);
            progressDlg.setMessage(info);
            return;
        }
//		showProgressIndicator();
        if (progressDlg != null) {
            dissmissDlg = progressDlg;
            progressDlg.dismiss();
        }
        T.hideToast();
        if (A.isInAutoScroll)
            return;
        progressDlg = A.createProgressDialog(this, title, info, true, true);
        progressDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                        || keyCode == KeyEvent.KEYCODE_PAGE_UP || keyCode == KeyEvent.KEYCODE_PAGE_DOWN
                        || keyCode == 105 || keyCode == 106)
                    return true;
                return false;
            }
        });
        progressDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                A.setSystemUiVisibility(true);
                if (progressDlg == dissmissDlg)
                    progressDlg = null;
            }
        });
    }

    public void hideProgressDlg() {
//		if (A.isFlipCurl() && isCoverShow)
//			return;
        ebook_inLoading = false;
        if (online_caching)
            return;
        if (progressDlg != null) {
            dissmissDlg = progressDlg;
            progressDlg.dismiss();
        }
        progressDlg = null;
        if (progressIndicator != null)
            progressIndicator.setVisibility(View.GONE);
    }

//	public boolean progressDlgShowVisible(){
//		if (progressDlg != null)
//			return true;
//		if (progressIndicator != null && progressIndicator.getVisibility()==View.VISIBLE)
//			return true;
//		return false;
//	}

    ProgressBar progressIndicator;

    public void showProgressIndicator() {
        try {
            createProgressIndicator();
            progressIndicator.bringToFront();
            progressIndicator.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            A.error(e);
        }
    }

    private void createProgressIndicator() {
        if (progressIndicator == null) {
            progressIndicator = new ProgressBar(this);
            baseFrame.addView(progressIndicator);
            FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) progressIndicator.getLayoutParams();
            fl.width = A.d(30);
            fl.height = A.d(30);
            fl.gravity = Gravity.CENTER;// Gravity.BOTTOM | Gravity.RIGHT;
            fl.bottomMargin = A.d(10);
            fl.rightMargin = A.d(10);
        }
    }

    long remind1StartTime;
    MyDialog.Builder reminderDlg;

    boolean reminderJustShowed() {
//		return reminderDlg != null;
        boolean result = System.currentTimeMillis() - remind1StartTime < T.minute(10);
//		if (result)
//			remind1StartTime = 0;
        return result;
    }

    private boolean checkRemindEvent() {
        if (isAutoState()) {
            A.invokeRemind1Time = SystemClock.elapsedRealtime();
            return false;
        }
        if (A.remind1) {
            if (SystemClock.elapsedRealtime() - A.invokeRemind1Time > A.remind1Time * 60 * 1000) {
                longTimeTapEvent = false;
                A.invokeRemind1Time = SystemClock.elapsedRealtime();
                if (isPaused)
                    return false;

                View v = LayoutInflater.from(this).inflate(R.layout.health_remind, null);
                ((TextView) v.findViewById(R.id.tipTv)).setText(A.fanti("(提醒间隔为" + A.remind1Time + "分钟, 可在阅读设置里调整)"));
                final TextView timeTv = (TextView) v.findViewById(R.id.timeTv);
                timeTv.setText("" + 60);
                if (!A.isNightState())
                    A.forceDayTextColors(v);

                reminderDlg = new MyDialog.Builder(this);
                reminderDlg.setView(v).setCancelable(false).show();

				/*Handler timeHandler = new Handler() {
					public void handleMessage(Message msg) {
						A.invokeRemind1Time = SystemClock.elapsedRealtime();
						timeTv.setText("" + msg.what);
						msg.what--;
						if (msg.what >= 0)
							sendEmptyMessageDelayed(msg.what, 1000);
						else
							reminder.dismiss();
					}
				};
				timeHandler.sendEmptyMessage(60);*/

                remind1StartTime = System.currentTimeMillis();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    A.invokeRemind1Time = SystemClock.elapsedRealtime();
                                    long elapsed = (System.currentTimeMillis() - remind1StartTime) / 1000;
                                    if (elapsed >= 60) {
                                        if (reminderDlg != null) {
                                            reminderDlg.dismiss();
                                            reminderDlg = null;
                                        }
                                        cancel();
                                    } else if (timeTv != null)
                                        timeTv.setText("" + (60 - elapsed));
                                } catch (Exception e) {
                                    A.error(e);
                                }
                            }
                        });
                    }
                }, 10, 100);

                return true;
            }
        }

        if (A.remind2) {
            if (isPaused)
                return false;
            if (!A.invokeRemind2) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), A.remind2TimeHour,
                        A.remind2TimeMinute, 00);
                long invokeTime = calendar.getTimeInMillis();
                long nowTime = System.currentTimeMillis();
                if (((nowTime - invokeTime) / 60 / 1000 < 15) && (nowTime > invokeTime)) {
                    longTimeTapEvent = false;
                    A.invokeRemind2 = true;
                    new MyDialog.Builder(this).setTitle(T.time()).setMessage(A.remind2Text).setPositiveButton(R.string.ok, null).show();
                    return true;
                }
            }
        }
        return false;
    }

    //-------reading progress float bar-----
    public View statusLay, statusLayHori, remainingLay, statusLeftPanel, status2Page;//, rpLay;
    private TextView statusLeft, statusLeft2, statusRight, statusMiddle, statusMiddle21, statusMiddle22, remainingLeft, remainingRight;

    private void initReadProgressViews() {
        statusLay = findViewById(R.id.txtStatusLay);
        remainingLay = findViewById(R.id.remainingLay);
        statusLayHori = findViewById(R.id.txtStatusLayHori);
        statusLeftPanel = findViewById(R.id.statusLeftPanel);
        status2Page = findViewById(R.id.status2Page);
        statusLeft = (TextView) findViewById(R.id.statusLeft);
        statusLeft2 = (TextView) findViewById(R.id.statusLeft2);
        statusRight = (TextView) findViewById(R.id.statusRight);
        statusMiddle = (TextView) findViewById(R.id.statusMiddle);
        statusMiddle21 = (TextView) findViewById(R.id.statusMiddle21);
        statusMiddle22 = (TextView) findViewById(R.id.statusMiddle22);
        remainingLeft = (TextView) findViewById(R.id.remainingLeft);
        remainingRight = (TextView) findViewById(R.id.remainingRight);
        statusLeftPanel.setOnClickListener(this);
        statusRight.setOnClickListener(this);
        statusMiddle.setOnClickListener(this);
        statusMiddle21.setOnClickListener(this);
        statusMiddle22.setOnClickListener(this);
        statusLeftPanel.setOnLongClickListener(this);
        statusRight.setOnLongClickListener(this);
        statusMiddle.setOnLongClickListener(this);
        statusMiddle21.setOnLongClickListener(this);
        statusMiddle22.setOnLongClickListener(this);
        setStatusBarProperties(false);
    }

    private long totalPages = -1;
    private long currentPage = -1;
    private long lastPageCountCheckSize = -1;

    private long getTotalPages() { //要乘于100000 - 为了四舍五入后能精确定位
        try {
            if (getBookType() == A.FILE_EBOOK) {
                if (A.ebook.getChapters().size() > 0) {
                    int l = txtView.getText().length();
                    if (l - lastPageCountCheckSize > 100) {
                        lastPageCountCheckSize = l;
                        double pages = (double) txtView.getRealHeight() / A.getPageHeight();
                        pages = pages * getEBookTotalSize() / A.ebook.getChapters().get(A.lastChapter).size;
                        totalPages = (long) (pages * 100000);
                    }
                }
            } else {
                if (totalPages < 100000 && txtView.getRealHeight() > baseFrame.getHeight()) { //v1.3.8 some tablet delay, so add this check
                    if (getBookType() == A.FILE_TXT && A.getTxts().size() > 3)
                        totalPages = (A.getTxts().size() * ((((long) txtView.getRealHeight() * 100000) / A.getPageHeight()) / 3));
                    else {
                        double t = 100000 * txtView.getRealHeight() / A.getPageHeight();
                        totalPages = (long) (A.noSplitHtmls() ? t : (htmlText.length() / txtView.getText().length()) * t);
                    }
                }
            }
        } catch (Exception e) {
            A.error(e);
            totalPages = 100000;
        }
        return totalPages > 100000 ? totalPages : 100000; //at lease be 1 page
    }

    private long getAboutCurrentPage(int pageAdd) {
        if (currentPage == -1) {
            long l = getBookLength();
            if (l == 0)
                l = 1;
            switch (getBookType()) {
                case A.FILE_TXT:
                    currentPage = getTotalPages() * A.lastPosition / l / 100000 + 1;
                    break;
                case A.FILE_HTML:
                    currentPage = getTotalPages() * (A.noSplitHtmls() ? A.lastPosition : A.maxHtmlChapterSize * A.lastSplitIndex + A.lastPosition) / l
                            / 100000 + 1;
                    break;
                case A.FILE_EBOOK:
                    int h = txtView.getRealHeight();
                    if (h > 0) {
                        int cTotal = h / A.getPageHeight() + 1;
                        int cCurrent = cTotal * (txtScroll.getScrollY() + A.getPageHeight() / 5) / h + 1;
                        currentPage = getTotalPages() * (BaseEBook.getPriorTextLength2(A.lastChapter)) / l / 100000 + cCurrent;
                    } else
                        currentPage = 1;
                    break;
            }
        }

        currentPage = currentPage + pageAdd;
        if (currentPage < 1)
            currentPage = 1;
        if (currentPage > getTotalPages() / 100000)
            currentPage = getTotalPages() / 100000;

        return currentPage;
    }

    private void fixBottomForScrollTo(MRTextView tv, int toY) {
        if (toY == 0)
            return;
        int svH = A.getPageHeight();
        int tvH = tv.getHeight();
        if (tvH - toY < svH) {
            int off = svH - (tvH - toY);
            int lh = tv.getLineHeight();
            int count = off / lh;
            if (off % lh > 0)
                count++;
            tv.appendEmptyLines(count);
        }
    }

    public boolean checkedBottomHalfLine;

    public void checkBottomHalfLineHandler() {
        checkTxtLayCutoutMagin();
        checkedBottomHalfLine = true;
        int barHeight = showStatusbar() ? statusLay.getHeight() - statusLay.getPaddingTop() : 0;
        int b = A.getBottomMargin() + barHeight;
        txtScrollSetPadding(txtScroll.getPaddingLeft(), A.getTopMargin(), txtScroll.getPaddingRight(), b);
    }

    private void checkTxtLayCutoutMagin() {
        if (A.fullscreen && A.isLandscape() && A.fitCutout == 1 && A.isCutoutScreen()) {
            int r = getWindowManager().getDefaultDisplay().getRotation();
            MarginLayoutParams lp = (MarginLayoutParams) txtLay.getLayoutParams();
            lp.leftMargin = r == Surface.ROTATION_90 ? A.getCutoutBarHeight() : 0;
            lp.rightMargin = r == Surface.ROTATION_270 ? A.getCutoutBarHeight() : 0;
            if (A.pageStyle2PageMode && pdf == null) {
                lp = (MarginLayoutParams) dualPageBackground.getLayoutParams();
                lp.leftMargin = r == Surface.ROTATION_90 ? A.getCutoutBarHeight() : 0;
                lp.rightMargin = r == Surface.ROTATION_270 ? A.getCutoutBarHeight() : 0;
            }
        }
    }

    public void checkBottomHalfLine() {
        handler.sendEmptyMessage(CHECK_BOTTOM_HALF_LINE);
    }

    private void checkStatusBar() {
        if (showStatusbar() && web == null) {
            statusLay.setVisibility(View.VISIBLE);
            setStatusBarProperties(true);
            updateProgressStatus();
            if (isPdf()) {
                pdfFullStatusBar = null;
                pdfShowFullStatusBarInfo();
            }
        } else
            statusLay.setVisibility(View.GONE);
        checkBottomHalfLine(); //v1.2.5 顺便放在这里一起检查
    }

    private void updateProgressStatus() {
        statusHandlerSendMessage(100, 200); //延时更新, chapter切换时html一般没加载完
    }

    public void setStatusBarProperties(boolean colorOnly) {
        int color = A.statusCustomizeFont ? A.statusFontColor : //v1.8.11
                isPdf() ? (A.isWhiteFont(A.pdf_back_color) ? 0xff222222 : 0xffcccccc) : A.fontColor;
//		if (A.brightnessValue < 1) {
//			boolean white = isPdf() ? !A.isWhiteFont(A.pdf_back_color) : A.isWhiteFont(A.fontColor);
//			int alpha = white ? -getShadeAlph() : 0;
//			statusLeft.setTextColor(A.getAlphaColor(color, alpha < -50 ? alpha : -50));
//			color = A.getAlphaColor(color, alpha);
//		} else
        color = A.getAlphaColor(color, -70);
        statusLeft.setTextColor(color);
        statusLeft2.setTextColor(color);
        statusMiddle.setTextColor(color);
        statusMiddle21.setTextColor(color);
        statusMiddle22.setTextColor(color);
        statusRight.setTextColor(color);

        statusLayHori.setVisibility(A.showStatusbar || status2Page.getVisibility() == View.VISIBLE
                || (showRemaingTime() && A.remaingTimeInStatusBar) ? View.VISIBLE : View.GONE);
        remainingLay.setVisibility(showRemaingTime() && !A.remaingTimeInStatusBar && status2Page.getVisibility() == View.GONE ? View.VISIBLE
                : View.GONE);
        remainingLeft.setTextColor(color);
        remainingRight.setTextColor(color);

        if (!colorOnly) {
            statusLeft.setTextSize(A.statusFontSize);
            statusLeft2.setTextSize(A.statusFontSize);
            statusMiddle.setTextSize(A.statusFontSize);
            statusRight.setTextSize(A.statusFontSize);

            int m = A.statusMargin > A.d(30) ? A.d(30) : A.statusMargin;
            int t = 0; //A.statusMargin;
            statusMiddle21.setPadding(m, t, m, A.statusMargin);
            statusMiddle22.setPadding(m, t, m, A.statusMargin);
            int l1 = A.statusMargin - A.d(2);
            statusLeftPanel.setPadding(l1 > 0 ? l1 : 0, t, m, A.statusMargin);
            statusMiddle.setPadding(A.d(30) - m, t, A.d(30) - m, A.statusMargin);
            int r2 = A.statusMargin + A.d(2);
            statusRight.setPadding(m, t, r2, A.statusMargin);

            remainingLeft.setTextSize(A.statusFontSize);
            remainingRight.setTextSize(A.statusFontSize);
            remainingLay.setPadding(A.statusMargin, t, A.statusMargin, A.statusMargin / 2);

            int bold = Typeface.NORMAL;//Typeface.BOLD;
            if (A.statusCustomizeFont) {
                statusLeft.setTextColor(A.getAlphaColor(A.statusFontColor, -50));
                statusLeft2.setTextColor(A.statusFontColor);
                statusMiddle.setTextColor(A.statusFontColor);
                statusRight.setTextColor(A.statusFontColor);
                statusLeft.setTypeface(A.getTypeFace(A.statusFontName, bold));
                statusLeft2.setTypeface(A.getTypeFace(A.statusFontName, bold));
                statusMiddle.setTypeface(A.getTypeFace(A.statusFontName, Typeface.NORMAL));
                statusRight.setTypeface(A.getTypeFace(A.statusFontName, bold));

                remainingLeft.setTypeface(A.getTypeFace(A.statusFontName, Typeface.NORMAL));
                remainingRight.setTypeface(A.getTypeFace(A.statusFontName, Typeface.NORMAL));
            } else {
                statusLeft.setTypeface(A.getTypeFace("sans-serif", bold));
                statusLeft2.setTypeface(A.getTypeFace("sans-serif", bold));
                statusMiddle.setTypeface(A.getTypeFace("sans-serif", Typeface.NORMAL));
                statusRight.setTypeface(A.getTypeFace("sans-serif", bold));

                remainingLeft.setTypeface(A.getTypeFace("sans-serif", bold));
                remainingRight.setTypeface(A.getTypeFace("sans-serif", bold));
            }

            statusLeftPanel.setClickable(A.statusClickLeft != A.DO_NONE);
            statusMiddle.setClickable(A.statusClickMiddle != A.DO_NONE);
            statusMiddle21.setClickable(A.statusClickMiddle != A.DO_NONE);
            statusMiddle22.setClickable(A.statusClickMiddle != A.DO_NONE);
            statusRight.setClickable(A.statusClickRight != A.DO_NONE);
            statusLeftPanel.setLongClickable(A.statusClickLeft2 != A.DO_NONE);
            statusMiddle.setLongClickable(A.statusClickMiddle2 != A.DO_NONE);
            statusMiddle21.setLongClickable(A.statusClickMiddle2 != A.DO_NONE);
            statusMiddle22.setLongClickable(A.statusClickMiddle2 != A.DO_NONE);
            statusRight.setLongClickable(A.statusClickRight2 != A.DO_NONE);
        }

        setStatusLayBackgroundColor();
        if (A.isFullScreenPhone() && A.fullscreen && A.immersive_fullscreen) {
            LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) statusLeftPanel.getLayoutParams();
            LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) statusRight.getLayoutParams();
            lp1.leftMargin = lp2.rightMargin = A.d(10);
        }
    }

    public void setStatusLayBackgroundColor() {
        if (status2Page.getVisibility() == View.VISIBLE) {
            statusLayHori.setBackgroundDrawable(null);
            statusLeft.setBackgroundDrawable(null);
            setStatusLayTextColorFor2PageMode();
        } else {
//			if (isPdf())
            statusLayHori.setBackgroundDrawable(null);
//			else
//				statusLayHori.setBackgroundColor(0xffff0000);//A.statusBackColor);
        }
        boolean white = isPdf() ? !A.isWhiteFont(A.pdf_back_color) : A.isWhiteFont(A.fontColor);
//		statusLeft.setBackgroundResource(white ? R.drawable.battery2 : R.drawable.battery);
        ((BatteryTextView) statusLeft).setBatteryIcon(!white);
    }

    private boolean isPureBlackBackground() {
        return !A.useBackgroundImage && A.backgroundColor == -16777216;
    }

    private Handler statusHandler;

    private void statusHandlerSendMessage(int what, int delay) {
        if (statusHandler == null) {
            statusHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (isFinishing())
                        return;
                    try {
                        if (msg.what == 0) {
                            showReadProgress(0);
                            if (!ebook_inLoading && !isPaused && msg.arg1 > 0)
                                saveLastPostion(true, true);
                        }
                        if (msg.what == 100) {
                            if (!isPdf() && getBookType() != A.FILE_TXT && txtView.getRealHeight() < baseFrame.getHeight() * 3)
                                preNextChapter(false);
                            showReadProgress(0);
                        }
                        if (msg.what == 1) {
                            A.setTxtScrollPadding(txtScroll);
                            checkStatusBar();
                        }
                    } catch (Exception e) {
                        A.error(e);
                    }
                }
            };
        }
        statusHandler.removeMessages(what);
//		statusHandler.sendEmptyMessageDelayed(what, delay);
        statusHandler.sendMessageDelayed(statusHandler.obtainMessage(what, delay, 0), delay);
    }

    private long lastCheckProgressTime = -1;
    HashMap<Integer, Integer> cachedSplitsPageCount, cachedSplitsPageNum;
    long total, current;

    public void showReadProgress(int pageAdd) {
        if (inCloudSyncing)
            return;
        if (!showStatusbar())
            return;
        if (isPaused)
            return;
        if (web != null)
            return;
        if (SystemClock.elapsedRealtime() - lastCheckProgressTime < 100)
            return;

        pageAdd *= txtKeepPage(0);
        lastCheckProgressTime = SystemClock.elapsedRealtime();
        saveLastPostion(false);
        updateBarTime();

        String text = "", progress = "";
        total = -1;
        current = -1;
        switch (getBookType()) {
            case A.FILE_TXT:
                text = getTxtChapterName();
                if (!inPreShow) {// && text.equals(T.getFilename(A.lastFile))) {
                    total = getTotalPages() / 100000;
                    current = getAboutCurrentPage(pageAdd);
                    if (current > total)
                        current = total;
                    progress = " (" + current + "/" + total + ")";
                    text += progress;
                }
                break;
            case A.FILE_HTML:
            case A.FILE_EBOOK:
                if (getBookType() == A.FILE_HTML) {
                    text = T.getFilename(A.lastFile);
                } else if (A.ebook != null && A.ebook.isInited()) {
                    text = A.ebook.getChapters().get(A.lastChapter).name;
                } else
                    return;

                int h = txtView.getRealHeight();
//			h -= A.oneLineTagHeight(); //v1.9.7
                int tmp = h / A.getPageHeight();
                h += (tmp > 3 ? tmp * A.oneLineTagHeight() / 3 : tmp > 2 ? A.oneLineTagHeight() / 2 : 0); //v3.4.4 should add?
                if (h < 1)
                    h = 1;

                total = h / A.getPageHeight() + 1;
                current = total * (txtScroll.getScrollY() + A.getPageHeight() / 5) / h + 1;

                if (!A.noSplitHtmls()) {
                    if (A.lastSplitIndex > A.splitHtmls.size() - 1)
                        A.lastSplitIndex = A.splitHtmls.size() - 1;
                    int l = A.splitHtmls.get(A.lastSplitIndex).length();
                    int priorTotal = -1;

                    if (cachedSplitsPageCount == null) {
                        cachedSplitsPageCount = new HashMap<Integer, Integer>();
                        cachedSplitsPageNum = new HashMap<Integer, Integer>();
                    }
                    cachedSplitsPageCount.put(A.lastSplitIndex, (int) total);
                    if (A.lastSplitIndex > 0 && cachedSplitsPageCount.containsKey(A.lastSplitIndex - 1)) {
                        total = cachedSplitsPageCount.get(A.lastSplitIndex - 1);
                        l = A.splitHtmls.get(A.lastSplitIndex - 1).length();
                        if (cachedSplitsPageNum.containsKey(A.lastSplitIndex - 1))
                            priorTotal = cachedSplitsPageNum.get(A.lastSplitIndex - 1);
                    }

                    current = current + (priorTotal > 0 ? priorTotal : total * stringArrayLength(A.splitHtmls, A.lastSplitIndex) / l);
                    total = total * stringArrayLength(A.splitHtmls, A.splitHtmls.size()) / l;

                    if (lastLineDisplayed())
                        cachedSplitsPageNum.put(A.lastSplitIndex, (int) (current
                                + (dualPageEnabled() && txtView2.getVisibility() == View.VISIBLE ? 1 : 0)));
                }
                if (current > total || (A.noSplitHtmls() && !dualPageEnabled() && lastLineDisplayed()))
                    current = total;

                progress = " (" + current + "/" + total + ")";
                text += progress;

                break;
            case A.FILE_PDF:
                if (pdf == null || pdf.pdfView == null) //v1.8.8 error here, why?
                    return;
                pdfShowFullStatusBarInfo();
                pdfGetToc();
                if (pdfToc.size() > 0 && A.lastChapter < pdfToc.size())
                    text = pdfToc.get(A.lastChapter).name;
                else
                    text = A.getBookName();
                break;
        }

        int book_left = -1;
        if (showRemaingTime()) {
            book_left = getBookLeftWords();
        }

        if (isWebBook())
            statusRight.setText((A.lastChapter + 1) + "/" + A.ebook.getChapters().size() + "章");
        else if (isPdf())
            statusRight.setText((pdf.textReflow ? A.lastPosition + 1 : pdfGetCurrPageNo() + 1) + "/" + m_doc.GetPageCount());
        else
            statusRight.setText(inPreShow ? "" : getPercentStr2());

        if (status2Page.getVisibility() == View.VISIBLE) {
            if (total != -1) {
                statusMiddle21.setText(current + "/" + total);
                statusMiddle21.setTag(" (" + current + "/" + total + ")");
                String text2 = current + 1 > total ? "" : (current + 1) + "/" + total;
                statusMiddle22.setText(text2);
                statusMiddle22.setTag(text2.length() > 0 ? " (" + text2 + ")" : "");
            } else {
                statusMiddle21.setText("");
                statusMiddle22.setText("");
            }
            statusMiddle.setText("");
        } else {
            restStatusMiddlePadding();
            statusMiddle.setText(text);
            statusMiddle.setTag(isPdf() ? "" : " (" + current + "/" + total + ")");
        }

        if (!isWebBook())
            refresh_ebook_remaining_time(book_left, false);
        if (!isPaused)
            statusHandlerSendMessage(0, (A.fullscreen && A.fullscreenWithStatus ? 30 : 60) * 1000);//每分钟更新一次
    }

    A.TxTChapter curTxtChapter;

    private String getTxtChapterName() {
        if (A.chapters == null || A.chapters.size() < 3)
            return T.getFilename(A.lastFile);
        String lineText = txtView.getLineText(txtView.getLayout().getLineForVertical(txtScroll.getScrollY()));
        int id = A.getTxtChapterId(A.lastPosition, curTxtChapter, lineText);
        curTxtChapter = A.getTxtChapters().get(id);
        String title = curTxtChapter.chapter_trim;
        title = PrefChapters.cleanChapterTitle(title);
        return title;
    }

    private int getBookLeftWords() {
        switch (getBookType()) {
            case A.FILE_TXT:
                if (!inPreShow)
                    return BaseEBook.getTxtUnReadWordCount(new AfterGetUnReadWords() {
                        public void refresh(int wordCount) {
                            handler.sendMessage(handler.obtainMessage(REFRESH_EBOOK_REMAINING_TIME, wordCount, 0));
                        }
                    }, false);
                break;
            case A.FILE_HTML:
                if (total > 0 && current > 0)
                    return (int) (getHtmlFileWordCount() * (total - current) / total);
                break;
            case A.FILE_EBOOK:
                return BaseEBook.getUnReadWordCountFrom(A.ebook, A.lastChapter, new AfterGetUnReadWords() {
                    public void refresh(int wordCount) {
                        handler.sendMessage(handler.obtainMessage(REFRESH_EBOOK_REMAINING_TIME, wordCount, 0));
                    }
                }, false);
            case A.FILE_PDF:
                return BaseEBook.getPdfUnReadWordCoun(new AfterGetUnReadWords() {
                    public void refresh(int wordCount) {
                        handler.sendMessage(handler.obtainMessage(REFRESH_EBOOK_REMAINING_TIME, wordCount, 0));
                    }
                }, false);
        }
        return -1;
    }

    protected void refresh_ebook_remaining_time(int book_left, boolean refreshTotalOnly) {
        if (!showRemaingTime())
            return;
        if (refreshTotalOnly) {
            statusRight.setText(getPercentStr2());
            return;
        }
        if (book_left == -1 && A.ebook != null)
            statusRight.setText("");

        try {
            int chapter_left = getChapterLeftWords();
            if (chapter_left == -1 && book_left == -1)
                return;

            int chapter_min = 0, book_hour = 0, book_min = 0;
            int speed = getReadingSpeed();
            if (book_left != -1) {
                if (chapter_left != -1)
                    book_left += chapter_left;
                int min = book_left / speed;
                book_hour = min / 60;
                book_min = min % 60;
            }
            if (chapter_left != -1 && getBookType() == A.FILE_EBOOK && A.ebook != null && A.ebook.getChapters().size() == 1)
                chapter_left = -1;
            if (chapter_left != -1)
                chapter_min = chapter_left / speed;

            if (status2Page.getVisibility() == View.VISIBLE) {
                statusMiddle21
                        .setText(String.format(getString(R.string.remaining_time_chapter) + "", chapter_min) + (String) statusMiddle21.getTag());
                statusMiddle22.setText((book_left == -1 ? "..." : String.format(getString(R.string.remaining_time_book), book_hour, book_min))
                        + (String) statusMiddle22.getTag());
                statusMiddle.setText("");
            } else {
                if (A.remaingTimeInStatusBar) {
                    String text;
                    if (book_left == -1)
                        text = String.format(getString(R.string.remaining_time_chapter) + "", chapter_min);
                    else if (chapter_left == -1)
                        text = String.format(getString(R.string.remaining_time_book), book_hour, book_min);
                    else
                        text = String.format(getString(R.string.remaining_time_format), chapter_min, book_hour, book_min);

                    String tmp = text + (String) statusMiddle.getTag();
                    if (Layout.getDesiredWidth(tmp, statusMiddle.getPaint()) < statusMiddle.getWidth() - A.d(4))
                        statusMiddle.setText(tmp);
                    else
                        statusMiddle.setText(text);
                } else {
                    String left = "", right = "";
                    if (chapter_left != -1)
                        left = String.format(getString(R.string.remaining_time_chapter) + "", chapter_min);
                    if (book_left != -1)
                        right = String.format(getString(R.string.remaining_time_book), book_hour, book_min);
                    remainingLeft.setText(left);
                    remainingRight.setText(right);
                }
            }
        } catch (OutOfMemoryError e) {
            A.error(e);
        } catch (Exception e) {
            A.error(e);
        }
    }

    private int getChapterLeftWords() {
        switch (getBookType()) {
            case A.FILE_TXT:
                return -1;
            case A.FILE_EBOOK:
                MyLayout lo = txtView.getLayout();
                if (lo == null)
                    return -1;
                int y = txtScroll.getScrollY();
                int line = lo.getLineForVertical(y);
                int p = lo.getLineStart(line);
                String text_left = txtView.getText2().substring(p, txtView.getText().length());
                if (getBookType() == A.FILE_EBOOK) {
                    if (!A.noSplitHtmls() && A.lastSplitIndex < A.splitHtmls.size() - 1) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = A.lastSplitIndex + 1; i < A.splitHtmls.size(); i++)
                            sb.append(A.splitHtmls.get(i));
                        String additional = A.ebook.isHtml() ? T.html2Text(sb.toString()) : sb.toString();
                        text_left = text_left + additional;
                    }
                }
                return T.getWordsCount(text_left, A.isAsiaLanguage);
            case A.FILE_HTML:
                break;
            case A.FILE_PDF:
                break;
        }
        return -1;
    }

    private void restStatusMiddlePadding() {
        if (showRemaingTime() && A.remaingTimeInStatusBar) {
            if (statusMiddle.getPaddingLeft() == A.d(30)) {
                statusMiddle.setPadding(A.d(2), 0, A.d(2), 0);
                statusMiddle.setEllipsize(TruncateAt.END);
            }
        } else {
            if (statusMiddle.getPaddingLeft() == A.d(2)) {
                statusMiddle.setPadding(A.d(30), 0, A.d(30), 0);
                statusMiddle.setEllipsize(TruncateAt.MIDDLE);
            }
        }
    }

    private int getReadingSpeed() {// words per minute
        BookDb.ReadStatistics r = BookDb.getSavedStatistics(A.lastFile);
        int minimal = 10 * 60 * 1000; // half hour
        if (System.currentTimeMillis() - statisticsTime > minimal)
            saveStatisticsInfo();

        if (r.usedTime > minimal) {
            float minutes = (float) r.usedTime / 60 / 1000;
            return (int) (r.readWords / minutes);
        } else if (A.statistics_time > minimal) {
            float minutes = (float) A.statistics_time / 60 / 1000;
            return (int) (A.statistics_words / minutes);
        } else
            return A.isAsiaLanguage ? 400 : 220;
    }

    private int htmlFileWordCount, htmlFileCharCount;

    public int getHtmlFileWordCount() {
        if (htmlFileWordCount == 0) {
            String text = T.html2Text(htmlText);
            htmlFileCharCount = text.length();
            htmlFileWordCount = T.getWordsCount(text, A.isAsiaLanguage);
        }
        return htmlFileWordCount;
    }

    public int getHtmlFileCharCount() {
        if (htmlFileCharCount == 0) {
            String text = T.html2Text(htmlText);
            htmlFileCharCount = text.length();
            htmlFileWordCount = T.getWordsCount(text, A.isAsiaLanguage);
        }
        return htmlFileCharCount;
    }

    private String getPercentStr2() {
        if (isPdf())
            return T.getPercentStr(pdfGetCurrPageNo() + 1, m_doc.GetPageCount());
        else if (isEndOfBook())
            return "100%";
        else if (getBookType() == A.FILE_TXT)
            return T.getPercentStr2(getCurrentPosition(), getBookLength());
        else
            return T.getPercentStr(getCurrentPosition(), getBookLength());
    }

    private boolean isEndOfBook() {
        int y = txtScroll.getScrollY();
        int tvH = txtView.getRealHeight();
        int svH = A.getPageHeight();
        int lh = txtView.getLineHeight();
        switch (getBookType()) {
            case A.FILE_TXT:
                if (A.lastBlockIndex >= A.getTxts().size() - 2 && y + svH * txtKeepPage(0) > tvH - lh * 9 / 10)
                    return true;
                break;
            case A.FILE_HTML:
                if ((A.splitHtmls.size() == 0 || A.lastSplitIndex == A.splitHtmls.size() - 1) && y + svH * txtKeepPage(0) > tvH - lh * 9 / 10)
                    return true;
                break;
            case A.FILE_EBOOK:
                if (A.ebook != null && A.lastChapter == A.ebook.getChapters().size() - 1
                        && (A.splitHtmls.size() == 0 || A.lastSplitIndex == A.splitHtmls.size() - 1))
                    if (lastLineDisplayed())
                        return true;
                break;
            case A.FILE_PDF:
                if (pdfGetCurrPageNo() == m_doc.GetPageCount() - 1) {
                    if (pdf.textReflow)
                        return lastLineDisplayed();
                    else {
                        PDFPosition p = pdf.pdfView.viewGetPos();
                        float ph = pdfPageParaHeight(p.page);
                        float h = pdf.getHeight() - A.d(10);
                        return p.page_y >= ph - h - A.d(10);
                    }
                }
//				if (pdfGetCurrPageNo() == m_doc.GetPageCount() - 1 &&
//						(!pdf.textReflow || lastLineDisplayed()))
//					return true;
                break;
        }
        return false;
    }

    private boolean lastLineDisplayed() {
        int line;

        if (dualPageEnabled()) {
            if (!isPage2Visible())
                return true;
            if (txtScroll2.getScrollY() + A.getPageHeight() >= txtView.getRealHeight())
                return true;
            line = A.getLastDisplayLine(txtScroll2, -1);
        } else {
//			if (txtScroll.getScrollY() + A.getPageHeight() >= txtView.getRealHeight()) //todo: incorrect, if below has lastTopBorder or PageBreak
//				return true;
            if (A.currentChapterWithImageOnly())
                return true;
            line = A.getLastDisplayLine(txtScroll, -1);
            if (line == getRealLineCount() - 1 && txtView.lineHasImage(line)
                    && txtScroll.getScrollY() + A.getPageHeight() < txtView.getLineTop(line + 1))
                return false;
        }

        if (line >= getRealLineCount() - 1)
            return true;

        if (txtView.getLayout().getLineFloat(line) != 0) {
            MyFloatSpan floatSp = txtView.getLayout().getLineFloatSp(line);
            ScrollView sv = dualPageEnabled() ? txtScroll2 : txtScroll;
            if (floatSp != null && floatSp.float_v + floatSp.float_height - A.d(10)
                    > sv.getScrollY() + A.getPageHeight())
                return false;
        }

        if (line >= getRealLineCount() - 3) {
            int i1 = txtView.getLayout().getLineStart(line + 1);
            String s = txtView.getText2().substring(i1, txtView.getText2().length());
            if (A.isEmtpyText(s, 0, s.length()))
                return true;
        }

        return false;
    }

    private long stringArrayLength(ArrayList<String> list, int index) {
        long result = 0;
        for (int i = 0; i < index; i++)
            result += list.get(i).length();
        return result;
    }

    private void updateBarTime() {
        if (!showStatusbar())
            return;
        try {
            if (isPdf() && !pdfShowFullStatusBarInfo())
                return;
            statusLeft2.setText(T.time(false, A.use12Hour, A.getLocale()));
            if (battery == null) {
                String battery2 = T.getBattery();
                updateBatteryInfo(battery2);
                if (battery2 == null)
                    registerBattery();
            } else
                updateBatteryInfo(battery);
        } catch (Exception e) {
            A.error(e);
        }
    }

    private void updateBatteryInfo(String battery1) {
        if (battery1 != null && battery1.length() > 0) {
            statusLeft.setVisibility(View.VISIBLE);
            statusLeft.setText(battery1.trim());
        } else if (statusLeft.getVisibility() == View.VISIBLE)
            statusLeft.setVisibility(View.GONE);
    }

    //-------reading progress float bar end-----

    private void error_and_exit(String errMsg, boolean clearLastFile) {
        if (clearLastFile)
            A.lastFile = "";
        if (errMsg == null) {
            if (A.ebook != null && A.ebook.errMsg != null)
                errMsg = A.ebook.errMsg;
            else
                errMsg = getString(R.string.invalid_file);
        }

        final boolean oom = errMsg.indexOf(".OutOfMemoryError") != -1;
        new MyDialog.Builder(ActivityTxt.this).setTitle(getString(R.string.error)).setMessage(errMsg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (oom)
                            restartReaderToMain();
                        else
                            doFinish();
                    }
                }).setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                if (oom)
                    restartReaderToMain();
                else
                    doFinish();
            }
        }).show();
    }

    private void setFlipViewINVISIBLE(boolean clear) {
        if (flipView.getVisibility() != View.GONE) {
            flipView.setVisibility(View.GONE);
            if (clear)
                flipView.setImageDrawable(null);
            contentLay.setAnimationState(false);
        }
    }

    private void setCurlViewINVISIBLE() {
        if (curl3d != null && curl3dLay.getVisibility() == View.VISIBLE)
            setCurl3dVisibility(false);
    }

    //-------------highlight text------------
    public HighlightLay hBar;
    TextView hCopy, hHighlight, hNote, hDict, hMore;
    public ColorTemplate colorTemplate;
    public DotImageView dot1, dot2;
    View dot, hBar2;
    ImageView hPen;
    int hLastX, hLastY;
    MotionEvent hMotionEvent, hDownEvent;

    private void initHighlightViews() {
        hBar = (HighlightLay) findViewById(R.id.HighlightBar);
        dot1 = (DotImageView) findViewById(R.id.dot1);
        dot2 = (DotImageView) findViewById(R.id.dot2);
        hPen = (ImageView) findViewById(R.id.penIv);
        hCopy = (TextView) findViewById(R.id.copyTv);
        hHighlight = (TextView) findViewById(R.id.highlightTv);
        hNote = (TextView) findViewById(R.id.noteTv);
        hDict = (TextView) findViewById(R.id.dictTv);
        hMore = (TextView) findViewById(R.id.shareTv);
        colorTemplate = (ColorTemplate) findViewById(R.id.colorTemplate);
        hBar2 = findViewById(R.id.HighlightBar2);

        hCopy.getPaint().setFakeBoldText(true);
        hHighlight.getPaint().setFakeBoldText(true);
        hNote.getPaint().setFakeBoldText(true);
        hDict.getPaint().setFakeBoldText(true);
        hMore.getPaint().setFakeBoldText(true);

        hPen.setOnClickListener(hOnClick);
        hCopy.setOnClickListener(hOnClick);
        hHighlight.setOnClickListener(hOnClick);
        hNote.setOnClickListener(hOnClick);
        hDict.setOnClickListener(hOnClick);
        hMore.setOnClickListener(hOnClick);

        dot1.setOnTouchListener(hOnTouch);
        dot2.setOnTouchListener(hOnTouch);
        setHPenVisible(isPdf() ? View.VISIBLE : View.GONE);

        hHighlight.setOnLongClickListener(this);
        hDict.setOnLongClickListener(this);
        colorTemplate.setVisibility(A.showColorTemplate ? View.VISIBLE : View.GONE);
        if (A.showColorTemplate)
            initColorTemplateEvents();
    }

    public void initColorTemplateEvents() {
        colorTemplate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean top = colorTemplate.isClickTop();
                int index = colorTemplate.getClicked();
                if (top && index == 4) {
//					T.showAlertText(ActivityTxt.this, getString(R.string.tip), getString(R.string.show_highlight_template_tip));
                    T.showToastText(ActivityTxt.this, getString(R.string.show_highlight_template_tip), 1);
//					A.showColorTemplate = false;
//					colorTemplate.setVisibility(View.GONE);
//					updateHBarHeight(A.d(70));
                    return;
                }

                if (pdf == null || pdf.textReflow) {
                    if (top) {
                        A.highlightMode = index;
                        colorTemplate.postInvalidate();
                    }
                    doHighlight2(A.touchingView == txtScroll2 ? txtView2 : txtView,
                            top ? index : -1, colorTemplate.getClickedColor(), !top);
                } else {
                    if (top && isClickOnAnnot())
                        return;
                    if (top)
                        A.highlightMode = index;
                    pdfDoHighlight2(colorTemplate.getClickedColor());
                }
            }
        });
        colorTemplate.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (colorTemplate.isClickTop() && colorTemplate.getClicked() == 4)
                    return true;
                new ColorDialog(ActivityTxt.this, getString(R.string.highlight_color), true,
                        colorTemplate.getLongClickedColor(), new ColorDialog.OnSaveColor() {
                    public void getColor(int color) {
                        if (colorTemplate.isClickTop()) {
                            switch (colorTemplate.getClicked()) {
                                case 0:
                                    if (isPdf())
                                        A.pdf_highlight_color = color;
                                    else
                                        A.highlight_color1 = color;
                                    break;
                                case 1:
                                    A.underline_color = color;
                                    break;
                                case 2:
                                    A.strikethrough_color = color;
                                    break;
                                case 3:
                                    A.squiggly_color = color;
                                    break;
                                case 4:
                                    return;
                            }
                        } else {
                            switch (colorTemplate.getClicked()) {
                                case 0:
                                    if (isPdf())
                                        A.pdf_highlight_color = color;
                                    else
                                        A.highlight_color1 = color;
                                    break;
                                case 1:
                                    A.highlight_color2 = color;
                                    break;
                                case 2:
                                    A.highlight_color3 = color;
                                    break;
                                case 3:
                                    A.highlight_color4 = color;
                                    break;
                                case 4:
                                    if (A.highlightMode == 3)
                                        A.squiggly_color = color;
                                    else if (A.highlightMode == 2)
                                        A.strikethrough_color = color;
                                    else
                                        A.underline_color = color;
                                    break;
                            }
                        }
                        colorTemplate.postInvalidate();
                    }
                }).show();
                return true;
            }
        });
    }

    public void updateHBarHeight(int topOff) {
        int p = hBar.getHeight() - hBar2.getHeight();
        if (p > A.d(70))
            p = p - A.d(70);
        if (A.showColorTemplate) {
            hBar.layout(hBar.getLeft(), hBar.getTop() + topOff, hBar.getRight(),
                    hBar.getTop() + hBar2.getHeight() + A.d(70) + p + topOff);
        } else
            hBar.layout(hBar.getLeft(), hBar.getTop() + topOff, hBar.getRight(),
                    hBar.getTop() + hBar2.getHeight() + p + topOff);
        hBar.requestLayout();
    }

    View.OnTouchListener hOnTouch = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            showSelectBar();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dot = (v == dot1 || v == dot2) ? v : null;
                    if (dot != null) {
                        hLastX = (int) event.getRawX() - dot.getLeft();
                        hLastY = (int) event.getRawY() - dot.getTop();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (dot != null) {
                        select_move_down(dot.getTop());
                        int l = (int) event.getRawX() - hLastX;
                        int t = (int) event.getRawY() - hLastY;
                        dot.layout(l, t, l + dot.getWidth(), t + dot.getHeight());
                        dot.postInvalidate();
                        preNoteInfo = null;
                        highlightText(false, -1, -1);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (dot != null) {
                        highlightText(true, -1, -1);
                        dot = null;
                    }
                    break;
            }

            return true;
        }
    };

    View.OnClickListener hOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            if (v == hHighlight) {
                doHighlight(false);
            }

            if (v == hNote) {
                do_add_note();
            }

            if (v == hPen) {
                if (isClickOnAnnot()) { //delete button
                    if (isPdfNoflow()) {
                        pdf.annotRemove();
                        A.pdfAnnotUpdated = true;
                        hideDotViews();
                        pdfSaveAnnotsForTime();
                    } else {
                        removeCurrentNote(preNoteInfo);
                    }
                } else {
                    if (isPdfNoflow()) {
                        if (m_doc.CanSave()) {
                            hideDotViews();
                            pdfShowAnnotLay();
                        } else
                            pdfShowFailedAnnotInfo(getString(R.string.pdf_cannot_modify));
                    }
                }
            }

            String s;
            if (isPdfNoflow()) {
                s = pdf.selectedText;
                if (s == null && preNoteInfo == null)
                    return; //--------------
            } else
                s = getTouchTxtView().selectedText;

            if (v == hCopy && s != null) {
                copyToClipboard(s);
                hideDotViews();
            }

            if (s != null)
                s = s.trim();
            if (v == hDict) {
                showSelectBar();
                if (A.translateInited)
                    doDictButton(false);
                else
                    customizeDict(ActivityTxt.this, true);
            }

            if (v == hMore && s != null) {
                showDictMoreMenu(s);
            }

        }

    };

    private void copyToClipboard(String s) {
        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(s);
            T.showToastText(this, getString(R.string.copy_to_clipboard), s.replace("\n", "<br>"), 0);
        } catch (Exception e) {
            A.error(e);
        }
    }

    private void showSelectBar() {
        cancelFade = true;
//		hBar.setAnimation(null);
//		dot1.setAnimation(null);
//		dot2.setAnimation(null);
        showhBar();
        A.viewFadeVisible(dot1, View.VISIBLE);
        A.viewFadeVisible(dot2, View.VISIBLE);
    }


    public boolean checkhBarWidth, checkhBarWidth2;
    float hBarFontSize = -1;

    public void showhBar() {
        if (!checkhBarWidth) {
            checkhBarWidth = true;
            float w2 = hMore.getPaint().measureText(hMore.getText().toString()) + hMore.getPaddingLeft() + hMore.getPaddingRight() - A.d(1);
            if (hMore.getWidth() < w2) {
                if (hBarFontSize == -1)
                    hBarFontSize = hMore.getTextSize() / A.getDensity(); // tv.getTextSize() = tv.setTextSize * density
                float fs = hBarFontSize - 1;
                hCopy.setTextSize(fs);
                hHighlight.setTextSize(fs);
                hNote.setTextSize(fs);
                hDict.setTextSize(fs);
                hMore.setTextSize(fs);

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        int hw = hCopy.getWidth() + hHighlight.getWidth() + hNote.getWidth() + hDict.getWidth() + hMore.getWidth()
                                + A.d(4) * 4 + A.d(6) * 2; //A.d(6) is hbar.9.png padding
                        if (isPdfNoflow() || hPen.getVisibility() == View.VISIBLE)
                            hw += hPenWidth();
                        int l = hw > baseFrame.getWidth() ? 0 : (baseFrame.getWidth() - hw) / 2;
                        hBar.layout(l, hBar.getTop(), l + hw, hBar.getBottom());

                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                float w2 = hMore.getPaint().measureText(hMore.getText().toString()) + hMore.getPaddingLeft() + hMore.getPaddingRight() - A.d(1);
                                if (hMore.getWidth() < w2) {
                                    hBarFontSize--;
                                    if (hBarFontSize > 10) {
                                        checkhBarWidth = false;
                                        showhBar();
                                        return;
                                    }
                                }
                                setHBarVisible(true);
                            }
                        }.sendEmptyMessage(0);
                    }
                }.sendEmptyMessage(0);
            } else
                setHBarVisible(true);
        } else
            setHBarVisible(true);
        if (A.showColorTemplate)
            colorTemplate.postInvalidate();
//		if (hPen.getVisibility()!=View.VISIBLE && hPenWidth()>0 && hCopy.getLeft()>hPenWidth()/2)
//			updateHBarHandler();
    }

    private void setHBarVisible(boolean visible) {
        A.moveStart = moveStart2;
        if (visible) {
            hBar.setBackgroundResource(A.isNightState() ? R.drawable.hbar : R.drawable.hbar2);
            int c = A.isNightState() ? 0xffeeeeee : 0xff616161;
            hCopy.setTextColor(c);
            hHighlight.setTextColor(c);
            hNote.setTextColor(c);
            hDict.setTextColor(c);
            hMore.setTextColor(c);

            if (hPen.getVisibility() == View.GONE && hCopy.getLeft() > 0)
                hBar2.requestLayout();
        }

        A.viewFadeVisible(hBar, visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void setHPenImage(boolean trash) {
        if (trash) {
            hPen.setImageResource(A.isNightState() ? R.drawable.trash : R.drawable.trash2);
            hPen.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    T.showToastText(ActivityTxt.this, getString(R.string.delete));
                    return false;
                }
            });
        } else {
            hPen.setImageResource(R.drawable.pen);
            hPen.setOnLongClickListener(null);
        }
    }

    private boolean cancelFade = false;

    private void fadeSelectBar() {
		/*cancelFade = false;
		Animation animation1 = new AlphaAnimation(1.0f, 0.6f);
		animation1.setDuration(A.dict_index == 0 ? 4000 : 2500);
		animation1.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				if (cancelFade)
					return;
				hideDotSelectors();
				getTouchTxtView().postInvalidate();
				setHBarVisible(false);
				dot1.setVisibility(View.INVISIBLE);
				dot2.setVisibility(View.INVISIBLE);
			}
		});
		hBar.startAnimation(animation1);
		dot1.startAnimation(animation1);
		dot2.startAnimation(animation1);*/
    }

    private void doHighlight(boolean fromLongTap) {
        if (isPdfNoflow()) {
            pdfDoHighlight(fromLongTap);
            return;
        }

        final MRTextView tv = A.touchingView == txtScroll2 ? txtView2 : txtView;
        if (tv.hasHighlight()) {
            if (preNoteInfo == null && fromLongTap) {
                doHighlight2(tv, -1, -1, true);
            } else {
                int mode = preNoteInfo == null ? -1 : preNoteInfo.underline ? 1 : preNoteInfo.strikethrough ? 2
                        : preNoteInfo.squiggly ? 3 : 0;
                new PrefSelectHighlight(this, new PrefSelectHighlight.OnSelectColor() {
                    public void selectColor(int mode, boolean delete) {
                        if (delete)
                            removeCurrentNote(preNoteInfo);
                        else
                            doHighlight2(tv, mode, -1, true);
                    }
                }, false, mode, tv.selectedText).show();
            }
        }
    }

    private void doHighlight2(MRTextView tv, int mode, int forceColor, boolean hideDots) {
        if (isPdfNoflow()) {
            pdfDoHighlight2(-1);
            return;
        }
        if (tv == null)
            tv = A.touchingView == txtScroll2 ? txtView2 : txtView;
        if (mode == -1)
            mode = A.highlightMode;
        NoteInfo note = preNoteInfo != null ? preNoteInfo : createDotNoteInfo();
        String tag1 = "" + note.underline + note.strikethrough + note.squiggly + note.highlightColor;
        note.underline = mode == 1;
        note.strikethrough = mode == 2;
        note.squiggly = mode == 3;
        if (pdf != null) {
            note.lastChapter = (int) A.lastPosition;
            note.lastSplitIndex = -1;
        }

        if (forceColor == -1) {
            note.highlightColor = A.highlight_color1;
            if (note.underline)
                note.highlightColor = A.underline_color;
            if (note.strikethrough)
                note.highlightColor = A.strikethrough_color;
            if (note.squiggly)
                note.highlightColor = A.squiggly_color;
        } else
            note.highlightColor = forceColor;

        if (preNoteInfo != null) {
            String tag2 = "" + note.underline + note.strikethrough + note.squiggly + note.highlightColor;
            if (!tag1.equals(tag2))
                if (!A.updateNote(note))
                    A.refreshNotes();
        } else
            A.addNote(note);

        if (hideDots) {
            hideDotViews();
        } else
            preNoteInfo = note;
        tv.hStart = -1;
        tv.postInvalidate();
        resetFlipCache();
        ;
    }

    private NoteInfo createDotNoteInfo() {
        MRTextView tv = getTouchTxtView();
        long lastPosition = getBookType() == A.FILE_TXT ? A.getTxtRealPos(tv.hStart) : tv.hStart;
        return new NoteInfo(0, A.getBookName(), A.lastFile, A.lastChapter, A.lastSplitIndex, lastPosition, tv.hEnd - tv.hStart, A.highlight_color1,
                System.currentTimeMillis(), "", "", tv.selectedText, false, false, "");
    }

    private void removeCurrentNote(NoteInfo note) {
        if (note == null)
            return;
        if (!A.removeNote(note))
            A.refreshNotes();
        MRTextView tv = getTouchTxtView();
        tv.hStart = -1;
        hideDotViews();
        if (!T.isNull(note.original))
            A.deleteHighlightAllItem(note.original);
        tv.postInvalidate();
        resetFlipCache();
        ;
    }

    protected void do_add_note() {
        if (isPdfNoflow()) {
            pdfDoAnnotation();
            return;
        }

        final NoteInfo fromNote = preNoteInfo != null ? preNoteInfo : createDotNoteInfo();
        if (pdf != null) {
            fromNote.lastChapter = (int) A.lastPosition;
            fromNote.lastSplitIndex = -1;
        }
        Dialog dlg = new PrefEditNote(ActivityTxt.this, fromNote, false, new PrefEditNote.OnAfterEditNote() {
            public void AfterEditNote(int result, String text) {
                if (result == PrefEditNote.SAVE) {
                    A.addNote(fromNote);
                    hideDotViews();
                    MRTextView tv = getTouchTxtView();
                    tv.hStart = -1;
                    tv.postInvalidate();
                    createCachePageShotsHandler(400);
                }
            }
        });
//		dlg.setCancelable(false);
        dlg.show();
    }

    protected void do_edit_note(final NoteInfo note) {
        final String tag1 = note.note + note.highlightColor + note.strikethrough + note.squiggly + note.underline;
        new PrefEditNote(ActivityTxt.this, note, true, new PrefEditNote.OnAfterEditNote() {
            public void AfterEditNote(int result, String text) {
                if (result == PrefEditNote.SAVE) {
                    String tag2 = note.note + note.highlightColor + note.strikethrough + note.squiggly + note.underline;
                    if (!tag1.equals(tag2))
                        A.updateNote(note);
                } else if (result == PrefEditNote.DELETE) {
                    removeCurrentNote(note);
                }
                contentLay.postInvalidate();
            }
        }).show();
    }

    private void doDictButton(boolean longTap) {
        try {
            String s = isPdfNoflow() ? pdf.selectedText.trim() : getTouchTxtView().selectedText.replace(A.INDENT_CHAR, ' ').trim();
            lookupWord(s, longTap);
        } catch (Exception e) {
            A.error(e);
        }
    }

    private void lookupWord(String word, boolean longTap) {
        if (T.isNull(word))
            return;
//		getSharedPreferences(A.DICT_HISTORY_FILE, 0).edit().putString(word, "").commit();
        openDictUrl(A.getDictUrl(longTap ? A.dict_index2 : A.dict_index), word);
    }

    protected void showDictHistory() {
        SharedPreferences sp = getSharedPreferences(A.DICT_HISTORY_FILE, 0);
        final String[] list = new String[sp.getAll().size()];
        int i = 0;
        for (String key : sp.getAll().keySet()) {
            list[i++] = key;
        }

        new MyDialog.Builder(ActivityTxt.this).setTitle(R.string.dict_history).setItems(list, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                openDictUrl(A.getDictUrl(A.dict_index), list[which]);
            }
        }).setPositiveButton(R.string.clear_dict_history, new OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                getSharedPreferences(A.DICT_HISTORY_FILE, 0).edit().clear().commit();
            }
        }).setNegativeButton(R.string.cancel, null).show();
    }

    protected void openDictUrl(String url, String s) {
        pdfSetImmersiveMode(true);
        if (url.equals("Lingvo")) {
            try {
                fadeSelectBar();
                Intent i = new Intent("com.abbyy.mobile.lingvo.intent.action.TRANSLATE");
                i.putExtra("com.abbyy.mobile.lingvo.intent.extra.TEXT", s);
                if (highlightY != -1) {
                    int offset = A.isLandscape() ? 5 - txtView.getLineHeight() : 5;
                    if (highlightY > getResources().getDisplayMetrics().heightPixels / 2) {
                        i.putExtra("com.abbyy.mobile.lingvo.intent.extra.HEIGHT", highlightY - txtView.getLineHeight() - offset);
                        i.putExtra("com.abbyy.mobile.lingvo.intent.extra.EXTRA_GRAVITY", Gravity.TOP);
                    } else {
                        i.putExtra("com.abbyy.mobile.lingvo.intent.extra.HEIGHT", getResources().getDisplayMetrics().heightPixels - highlightY
                                - txtView.getLineHeight() - offset);
                        i.putExtra("com.abbyy.mobile.lingvo.intent.extra.EXTRA_GRAVITY", Gravity.BOTTOM);
                    }
                }
                i.putExtra("com.abbyy.mobile.lingvo.intent.extra.MARGIN_LEFT", 4);
                i.putExtra("com.abbyy.mobile.lingvo.intent.extra.MARGIN_RIGHT", 4);
                i.putExtra("com.abbyy.mobile.lingvo.intent.extra.MARGIN_TOP", 4);
                i.putExtra("com.abbyy.mobile.lingvo.intent.extra.MARGIN_BOTTOM", 4);
                startActivity(i);
            } catch (Exception e) {
                T.openAppInMarket(this, "com.abbyy.mobile.lingvo.market");
            }
        }
        if (url.equals("FreeDictionary")) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setClassName("org.freedictionary", "org.freedictionary.MainActivity");
                i.setData(Uri.parse(s));
//				i.setData(Uri.parse("thefreedictionary://search/"+s));
                startActivity(i);
            } catch (Exception e) {
                T.openAppInMarket(this, "org.freedictionary");
            }
        } else if (url.equals("Leo")) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setClassName("org.leo.android.dict", "org.leo.android.dict.LeoDict");
                i.setData(Uri.parse(s));
                startActivity(i);
            } catch (Exception e) {
                T.openAppInMarket(this, "org.leo.android.dict");
            }

        } else if (url.equals("YunCi")) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setAction("com.yunci.search");
                i.putExtra("EXTRA_QUERY", s);
                i.putExtra("EXTRA_GRAVITY", highlightY < getResources().getDisplayMetrics().heightPixels / 2 ? Gravity.BOTTOM : Gravity.TOP);//设置搜索界面的位置-默认置顶（Gravity.TOP）;
                startActivity(i);
            } catch (Exception e) {
                T.openUrl(this, "http://www.yunci4.com/");
            }

        } else if (url.equals("ColorDict3")) {
            try {
                fadeSelectBar();
                Intent i = new Intent("colordict.intent.action.SEARCH");
                i.putExtra("EXTRA_QUERY", s); //Search Query
                if (highlightY != -1) {
                    int offset = A.isLandscape() ? 5 - txtView.getLineHeight() : 5;//A.isHighResolution?0:-5
                    if (highlightY > getResources().getDisplayMetrics().heightPixels / 2) {
                        i.putExtra("EXTRA_HEIGHT", highlightY - txtView.getLineHeight() - offset); //400pixel, if you don't specify, fill_parent"
                        i.putExtra("EXTRA_GRAVITY", Gravity.TOP);
                    } else {
                        i.putExtra("EXTRA_HEIGHT", getResources().getDisplayMetrics().heightPixels - highlightY - txtView.getLineHeight() - offset); //400pixel, if you don't specify, fill_parent"
                        i.putExtra("EXTRA_GRAVITY", Gravity.BOTTOM);
                    }
                }
                i.putExtra("EXTRA_MARGIN_LEFT", 4);
                i.putExtra("EXTRA_MARGIN_RIGHT", 4);
                i.putExtra("EXTRA_MARGIN_TOP", 4);
                i.putExtra("EXTRA_MARGIN_BOTTOM", 4);
                startActivity(i);
            } catch (Exception e) {
                if (A.isLandscape()) {
                    new MyDialog.Builder(this).setMessage(Html.fromHtml(A.fanti(
                            "是否安装这个离线词典? <br><br>安装完成后请自行在词典应用里<b>设置允许横屏显示词典.</b>")))
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!T.openAppInMarket(ActivityTxt.this, "com.eusoft.eudic"))
                                        T.showAlertText(ActivityTxt.this, getString(R.string.error), getString(R.string.market_not_install));
                                }
                            }).setNegativeButton(R.string.cancel, null).show();
                } else
                    openMarketForDict("com.eusoft.eudic");
//					openMarketForDict("com.qianyan.eudic");
            }

        } else if (url.equals("ColorDict")) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setClassName("com.socialnmobile.colordict", "com.socialnmobile.colordict.activity.Main");
                i.setData(Uri.parse(s));
                startActivity(i);
            } catch (Exception e) {
                openMarketForDict("com.socialnmobile.colordict");
            }

        } else if (url.equals("Fora")) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setAction("com.ngc.fora.action.LOOKUP");
                i.putExtra("HEADWORD", s);
                startActivity(i);
            } catch (Exception e) {
                openMarketForDict("com.ngc.fora");
            }

        } else if (url.equals("youdao")) {
            try {
                String pkg = "com.youdao.dict";
                Intent intent = new Intent(Intent.ACTION_PROCESS_TEXT);
                intent.setComponent(new ComponentName(pkg, "com.youdao.dict.activity.ProcessTextActivity"));
                intent.putExtra(Intent.EXTRA_PROCESS_TEXT, s);
                startActivity(intent);
            } catch (Exception e) {
                openMarketForDict("com.youdao.dict");
            }

        } else if (url.equals("XinHua")) {
            try {
                Intent i = new Intent();
                i.setClassName("hugh.android.app.zidian", "hugh.android.app.zidian.ZiDian");
                i.putExtra("zi", s);
                startActivity(i);
            } catch (Exception e) {
                openMarketForDict("hugh.android.app.zidian");
            }

        } else {
            if (url.equals("Google"))
                url = A.DICTIONARY_URL;
            if (url.equals("Customized"))
                url = A.my_dict_url;
            if (url.equals(A.TRANSLATION_URL))
                if (callGoogleTranslateApp(s))
                    return;
            try {
                url = url.replace("%s", Uri.encode(s)).replace("<SL>", A.sourceLanguage).replace("<TL>", A.destLanguage);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (Exception e) {
                A.error(e);
            }
        }
    }

    private void openMarketForDict(final String packageName) {
        new MyDialog.Builder(this).setMessage(R.string.dict_install).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (!T.openAppInMarket(ActivityTxt.this, packageName))
                    T.showAlertText(ActivityTxt.this, getString(R.string.error), getString(R.string.market_not_install));
            }
        }).setNegativeButton(R.string.cancel, null).show();
    }

    public void customizeDict(final Context context, final boolean showMenu) {
        ScrollView layout = (ScrollView) LayoutInflater.from(context).inflate(R.layout.dictionary_select, null);
        final Spinner dict_sp = (Spinner) layout.findViewById(R.id.cnDictSp);
        final Spinner dict_sp2 = (Spinner) layout.findViewById(R.id.cnDictSp2);
        final CheckBox cb = (CheckBox) layout.findViewById(R.id.openDict);
        final CheckBox cb2 = (CheckBox) layout.findViewById(R.id.oneChar);
        ((TextView) layout.findViewById(R.id.tv1)).setText(Html.fromHtml(getString(R.string.dictionary) + "1 <small>(" +
                getString(R.string.single_tap_dict) + ")</small>"));
        ((TextView) layout.findViewById(R.id.tv2)).setText(Html.fromHtml(getString(R.string.dictionary) + "2 <small>(" +
                getString(R.string.long_tap_dict) + ")</small>"));
//		if (isPdf())
//			cb.setVisibility(View.GONE);

        cb2.setChecked(A.oneCharDict);
        cb2.setVisibility(A.isAsiaLanguage ? View.VISIBLE : View.GONE);
        cb.setChecked(A.openDictDirect);


        final String[] items = getResources().getStringArray(R.array.dict_list);
        ArrayAdapter<String> actionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dict_sp.setAdapter(actionAdapter);
        dict_sp2.setAdapter(actionAdapter);

        A.dict_index = A.dict_index == -1 ? 0 : A.dict_index >= items.length - 1 ? 0 : A.dict_index;
        A.dict_index2 = A.dict_index2 == -1 ? 0 : A.dict_index2 >= items.length - 1 ? 0 : A.dict_index2;
        dict_sp.setSelection(A.dict_index);
        dict_sp2.setSelection(A.dict_index2);

        new Handler() { //delay enable, or it'll execute at once
            public void handleMessage(Message msg) {
                OnItemSelectedListener selectDictEvent = new OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == items.length - 1) {
                            final EditText et = new EditText(context);
                            et.setText(A.my_dict_url);
                            et.setSingleLine(true);
                            new MyDialog.Builder(context).setTitle(getString(R.string.my_dict_hint)).setView(et)
                                    .setPositiveButton(R.string.ok, new OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            A.my_dict_url = et.getText().toString();
                                        }
                                    }).show();
                        }

                        if (A.isNightState()) {
                            for (int i = 0; i < parent.getChildCount(); i++)
                                if (parent.getChildAt(i) instanceof TextView)
                                    ((TextView) parent.getChildAt(i)).setTextColor(0xffdddddd);
                        }
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                };
                dict_sp.setOnItemSelectedListener(selectDictEvent);
                dict_sp2.setOnItemSelectedListener(selectDictEvent);
            }
        }.sendEmptyMessage(0);

        A.setSpinnerListStyle(layout);
        MyDialog.Builder dlg = new MyDialog.Builder(context);
        dlg.setTitle(getString(R.string.customize_dictionary)).setView(layout)
                .setPositiveButton(R.string.ok, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//						A.askHighlightType = askCb.isChecked();
                        A.dict_index = dict_sp.getSelectedItemPosition();
                        A.dict_index2 = dict_sp2.getSelectedItemPosition();

                        A.openDictDirect = cb.isChecked();
                        A.oneCharDict = cb2.isChecked();
                        A.translateInited = true;
                        if (showMenu)
                            doDictButton(false);
                    }
                }).setNegativeButton(R.string.cancel, null);
        if (A.isNightState()) //bug: night state won't show customized url dialog, why?
            dlg.setHideKeyboard(false);
        dlg.show();
    }

    private void highlightText(int start, int end) {
        highlightText(txtView, start, end);
        if (dualPageEnabled())
            highlightText(txtView2, start, end);
        if (!isPaused && A.fitHardwareAccelerated)
            contentLay.postInvalidate();

        if (!A.isSpeaking && start >= 0 && start < txtView.getText().length()) {
            char c = txtView.getText().charAt(start);
            A.log("[" + c + "]ASCII:" + (0 + c) + " CharacterType:" + Character.getType(c) + " UnicodeBlock:" + Character.UnicodeBlock.of(c));
        }
    }

    private void highlightText(MRTextView tv, int start, int end) {
        if (!A.isSpeaking) {
            end = adjustLastHighlightPosition(tv, end);
            if (start > end)
                start = end;
        }
        tv.hStart = start;
        tv.hEnd = end;
        if (!isPaused)
            tv.postInvalidate();
    }

    private int adjustLastHighlightPosition(MRTextView tv, int end) {
        if (isPdf()) { //text reflow mode
//			String s = tv.getText().toString();
//			if (end > s.length() - 1)
//				end = s.length() - 1;
            return end;
        } else if (getBookType() != A.FILE_EBOOK)
            return end;

        int maxLine = getRealLineCount() - 1;
        if (A.chapterEndPrompt && !A.trimBlankSpace) {
            int tmp = tv.getText2().indexOf(A.getChapterEndText());
            if (tmp > 0)
                maxLine = tv.getLayout().getLineForOffset(tmp) - 1;
        }
        int line = tv.getLayout().getLineForOffset(end);
        if (line > 0 && line > maxLine) {
            end = tv.getLayout().getLineVisibleEnd(maxLine);
        }
        return end;
    }

    public int additionalLeft() {
        if (A.touchingView == txtScroll2)
            return txtScroll.getWidth() + txtLay.getPaddingLeft();
        else if (dualPageEnabled())
            return txtLay.getPaddingLeft();
        else
            return 0;
    }

    private int additionalLeft(MRTextView tv) {
        if (tv == txtView2)
            return txtScroll.getWidth() + txtLay.getPaddingLeft();
        else if (dualPageEnabled())
            return txtLay.getPaddingLeft();
        else
            return 0;
    }

    private int highlightY = -1;

    private void highlightText(boolean setDotPosition, int offset1, int offset2) {
        if (isPdfNoflow()) {
            pdfHighlightText(setDotPosition, true);
            return;
        }

        MyLayout lo = txtView.getLayout();
        if (lo == null)
            return;

        MRTextView tv1, tv2;
        if (offset1 == -1 && dualPageEnabled()) {
            tv1 = dot1.getLeft() + dot1.getWidth() / 2 > baseFrame.getWidth() / 2 ? txtView2 : txtView;
            tv2 = dot2.getLeft() + dot1.getWidth() / 2 > baseFrame.getWidth() / 2 ? txtView2 : txtView;
        } else {
            if (dualPageEnabled()) {
                tv1 = offsetInTxtScroll2(offset1) ? txtView2 : txtView;
                tv2 = offsetInTxtScroll2(offset2) ? txtView2 : txtView;
            } else
                tv1 = tv2 = txtView;
        }
        ScrollView sv1 = tv1 == txtView2 ? txtScroll2 : txtScroll;
        ScrollView sv2 = tv2 == txtView2 ? txtScroll2 : txtScroll;

        int lh = tv1.getLineHeight();
        int sy1 = sv1.getScrollY();
        int sy2 = sv2.getScrollY();
        int pl = sv1.getPaddingLeft();
        int pt = sv1.getPaddingTop();

        int line1, line2;
        if (offset1 == -1 || offset2 == -1) {
            line1 = lo.getLineForVertical(sy1 + dot1.getTop() - lh / 3 - pt);
            line2 = lo.getLineForVertical(sy2 + dot2.getTop() - lh / 3 - pt);
            offset1 = tv1.getLineOffset(line1, dot1.getLeft() - additionalLeft(tv1) + dot1.getWidth() / 2 - pl);
            offset2 = tv2.getLineOffset(line2, dot2.getLeft() - additionalLeft(tv2) + dot1.getWidth() / 2 - pl);
            if (offset1 == -1 || offset2 == -1)
                return;
        }
        offset1 = adjustLastHighlightPosition(tv1, offset1);
        offset2 = adjustLastHighlightPosition(tv2, offset2);
        line1 = lo.getLineForOffset(offset1);
        line2 = lo.getLineForOffset(offset2 - 1);

        int textLength = tv1.getText().length();
        tv1.hStart = tv1.hEnd = tv2.hStart = tv2.hEnd = -1;
        if (offset1 >= 0 && offset2 >= 0 && offset1 != offset2 && offset1 < textLength && offset2 <= textLength) {

            boolean inverse = offset1 > offset2;
            highlightText(inverse ? offset2 : offset1, inverse ? offset1 : offset2);
            if (tv1.hStart > tv1.hEnd) {
                A.log("ERROR tv1.hStart-End:" + tv1.hStart + "/" + tv1.hEnd);
                return;
            }
            txtView.selectedText = txtView2.selectedText = txtView.getText2().substring(tv1.hStart, tv1.hEnd);
            if (setDotPosition) {
                int dw = dot1.getWidth();
                int dh = dot1.getHeight();

                int l1 = (int) tv1.getTextX(line1, offset1);
                int t1 = pt + lo.getLineBottom(line1) - sy1 - 3;
                highlightY = t1 - lh / 2;

                int l2 = (int) tv2.getTextX(line2, offset2);
                int t2 = pt + lo.getLineBottom(line2) - sy2 - 3;

                if (tv1.getLayout().getParagraphDirection(line1) == MyLayout.DIR_RIGHT_TO_LEFT) {
                    int l3 = l1;
                    if (tv1.selfDrawLine(line1)) {
                        l1 = l2 + pl - dw / 2 + additionalLeft(tv2);
                        l2 = l3 + pl - dw / 2 + additionalLeft(tv1);
                    } else {
                        l1 = tv1.getWidth2() - l2 + pl - dw / 2 + additionalLeft(tv2);
                        l2 = tv1.getWidth2() - l3 + pl - dw / 2 + additionalLeft(tv1);
                    }
                    l3 = t1;
                    t1 = t2;
                    t2 = l3;
                } else {
                    l1 += pl - dw / 2 + additionalLeft(tv1);
                    l2 += pl - dw / 2 + additionalLeft(tv2);
                }

                dot1.layout(l1, t1, l1 + dw, t1 + dh);
                dot2.layout(l2, t2, l2 + dw, t2 + dh);

                layoutHBar();
                if (preNoteInfo != null) {
                    hPen.setTag(1);
                    setHPenImage(true);
                    setHPenVisible(View.VISIBLE);
                    new Handler() {
                        public void handleMessage(Message msg) {
                            int l = hBar.getLeft();
                            int r = hBar.getRight() + hPenWidth();
                            if (r > baseFrame.getWidth()) {
                                int off = r - baseFrame.getWidth() + A.d(2);
                                l -= off;
                                r -= off;
                            }
                            if (l < 0) {
                                l = 0;
                                r = hBar.getWidth() + hPenWidth();
                            }
                            hBar.layout(l, hBar.getTop(), r, hBar.getBottom());
                            hBar.requestLayout();
                            if (!checkhBarWidth2) {
                                checkhBarWidth2 = true;
                                checkhBarWidth = false;
                                showhBar();
                            }
                        }
                    }.sendEmptyMessage(0);
                }
//				else if (hPenWidth()>0 && hCopy.getLeft()>hPenWidth()) //rare issue, need to fix
//					updateHBarHandler();
                showDotViews();
            } else {
                setHBarVisible(false);
            }
        }
    }

    private void setHPenVisible(int v) {
//		if (hPen.getVisibility()!=v)
//			updateHBarHandler();
        hPen.setVisibility(v);
        findViewById(R.id.penSplit).setVisibility(v);
    }

//	int colorPlateHeight;
//	private void updateHBarHandler() {
//		if (colorTemplate.getVisibility() == View.VISIBLE && colorTemplate.getHeight() > 0)
//			colorPlateHeight = hBar.getHeight() - hBar2.getHeight();
//
//		new Handler() {
//			public void handleMessage(Message msg) {
//				updateHBarHeight(0);
//				hBar2.requestLayout();
//			}
//		}.sendEmptyMessageDelayed(0, 50);
//	}

    private boolean offsetInTxtScroll2(int offset) {
        MyLayout lo = txtView2.getLayout();
        if (lo == null)
            return false;
        if (txtView2.getVisibility() != View.VISIBLE)
            return false;
        int l = lo.getLineForOffset(offset);
        int l1 = lo.getLineForVertical(txtScroll2.getScrollY());
        int l2 = A.getLastDisplayLine(txtScroll2, l1);
        return l >= l1 && l <= l2;
    }

    public void layoutHBar() {
        int t, t1 = dot1.getTop(), t2 = dot2.getTop();
        if (t1 > t2) {
            t = t2;
            t2 = t1;
            t1 = t;
        }
        int dh = dot1.getHeight();
        int h = hBar.getHeight();
        int lh = txtView.getLineHeight();
        if (t1 > h + lh + A.d(20)) {
            t = t1 - (h + lh + A.d(15));
        } else {
            if (baseFrame.getHeight() - t2 - dh > h - 24)
                t = t2 + dh;
            else
                t = t1 + dh + (t2 - t1 - dh - h) / 2 - dh / 5;
        }
        int w = hBar.getWidth();
        int hPenW = -1;
        if (!isPdfNoflow() && hPen.getVisibility() == View.VISIBLE) {
            hPenW = hPenWidth();
            w = w - hPenW;
        }

        int l = getHBarLeftFromDot();
        if (dot == null)
            dot = dot1.getTop() > dot2.getTop() ? dot1 : dot2;
        if (t < A.d(2) || t > baseFrame.getHeight() - h - A.d(1))
            t = (A.getScreenHeight2() - h) / 2;
        if ((t < t1 && t + h > t1) || (t < t2 && t + h > t2)) { //v2.6.4
            int l1 = dot1.getLeft(), l2 = dot2.getLeft();
            if (l1 > l2) {
                int x = l2;
                l2 = l1;
                l1 = x;
            }
            if (l2 - l1 < w + dot1.getWidth()) {
                if (l1 > w)
                    l = l1 - w;
                else if (l2 < baseFrame.getWidth() - w - dot1.getWidth())
                    l = l2 + dot1.getWidth();
                else if (t1 > baseFrame.getHeight() - t2)
                    t = A.d(2);
                else
                    t = baseFrame.getHeight() - h - A.d(1);
            }
        }
        hBar.layout(l, t, l + w, t + h);
        if (hPenW != -1 && hPenWidth() > hPenW) {
            int add = hPenWidth() - hPenW;
            hBar.layout(l, t, l + w - add, t + h);
        }

        hPen.setTag(0);
        setHPenImage(false);
        setHPenVisible(isPdfNoflow() ? View.VISIBLE : View.GONE);

        showhBar();
    }

    private int hPenWidth() {
        return hPen.getWidth() + A.d(4) + 1;
    }

    private int getHBarLeftFromDot() {
        int i1 = dot1.getLeft() > dot2.getLeft() ? dot2.getLeft() : dot1.getLeft();
        int i2 = dot1.getLeft() > dot2.getLeft() ? dot1.getLeft() : dot2.getLeft();
        int i = i1 + dot1.getWidth() / 2 + (i2 - i1) / 2;
        int w = hBar.getWidth();
        int sw = A.getScreenWidth2();
        if (i + w / 2 < sw && i - w / 2 > 5)
            return i - w / 2;
        else if (i + w / 2 < sw)
            return 5;
        else if (i - w / 2 > 5)
            return sw - w - 5;
        else
            return sw;
    }

    private void showDotViews() {
        A.viewFadeVisible(dot1, View.VISIBLE);
        A.viewFadeVisible(dot2, View.VISIBLE);
    }

    private boolean hideDotViews() {
        if (txtView == null)
            return false;
        txtView.hStart = -1;
        txtView2.hStart = -1;
        highlightY = -1;
        if (hBar == null || dot1 == null || dot2 == null)
            return false;
        if (hBar.getVisibility() != View.VISIBLE && !dotVisible())
            return false;

        txtView.postInvalidate();
        txtView2.postInvalidate();
        xFlingTime = SystemClock.elapsedRealtime(); //v1.3.11
        longTimeTapEvent = false;
//		hBar.setAnimation(null);
//		dot1.setAnimation(null);
//		dot2.setAnimation(null);
        A.viewFadeVisible(dot1, View.INVISIBLE);
        A.viewFadeVisible(dot2, View.INVISIBLE);
        setHBarVisible(false);
        if (isPdf() && pdf != null) {
            pdf.recordDotsAct(null);
            pdf.pdfView.viewEnableTextSelection(false);
            PDFPage.delForceSel(true);
        }
        return true;
    }

    private void hideDotSelectors() {
        MRTextView tv = getTouchTxtView();
        int hStart = tv.hStart;
        int hEnd = tv.hEnd;
        hideDotViews();
        tv.hStart = hStart;
        tv.hEnd = hEnd;
    }

    private void doLongTimeTapEvent() {
        A.log("doLongTimeTapEvent");
        A.moveStart = moveStart2;
        if (web != null)
            return;
        if (A.mult_touch && hMotionEvent.getPointerCount() >= 2)
            return;
        if (isVisualBoomarkClick(hMotionEvent))
            return;
        if (isImageClick(hMotionEvent, false, true))
            return;
        if (isNoteHighlightClick(hMotionEvent))
            return;
        if (isUrlClick(hMotionEvent, false))
            return;

        showMovingChangeChapterState(false);
        if (A.doLongTap == A.DO_SELECT_TEXT) {
            selectText(false);
            if (A.showMagnifier)
                hBar.drawMagnifier(hMotionEvent);
            lookupWordAutomatically();
        } else
            doEvent(A.doLongTap);
    }

    private void lookupWordAutomatically() {
        if (A.openDictDirect && dotVisible()) {
            if (isPdfNoflow()) {
                if (!T.isNull(pdf.selectedText)) {
                    lookupWord(pdf.selectedText.replace(A.INDENT_CHAR, ' ').trim(), false);
                    hBar.stopMagnifier();
                }
            } else if (!T.isNull(getTouchTxtView().selectedText))
                lookupWord(getTouchTxtView().selectedText.replace(A.INDENT_CHAR, ' ').trim(), false);
        }
    }

    public boolean dotVisible() {
        return dot1 != null && dot1.getVisibility() == View.VISIBLE;
    }

    public NoteInfo preNoteInfo = null;
    boolean select_text_state;
    int first_select_pos;

    int getTouchPos() {
        if (isPdfNoflow())
            return -1;
        ScrollView sv = A.touchingView == txtScroll2 ? txtScroll2 : txtScroll;
        MRTextView tv = getTouchTxtView();
        float setX = sv.getWidth() / 2;
        float setY = sv.getHeight() / 2;
        if (hMotionEvent != null)
            try {
                setX = hMotionEvent.getX();
                setY = hMotionEvent.getY();
            } catch (Exception e) {
                A.error(e);
                return -1;
            }

        int y = sv.getScrollY();
        int pl = sv.getPaddingLeft();
        int pt = sv.getPaddingTop();
        int hy = (int) setY + y - pt;
        float hx = setX - pl;

        MyLayout lo = tv.getLayout();
        if (lo == null)
            return -1;
        int line = lo.getLineForVertical(hy);
        if (line > getRealLineCount() - 1)
            return -1;
        int offset = tv.getLineOffset(line, hx);
        return offset;
    }

    private void selectText(boolean endWithTouch) {
        if (isPdfNoflow()) {
            pdfSelectText(true);
            return;
        }

        ScrollView sv = A.touchingView == txtScroll2 ? txtScroll2 : txtScroll;
        MRTextView tv = getTouchTxtView();

        float setX = sv.getWidth() / 2;
        float setY = sv.getHeight() / 2;
        if (hMotionEvent != null)
            try {
                setX = hMotionEvent.getX();
                setY = hMotionEvent.getY();
            } catch (Exception e) {
                A.error(e);
                return;
            }

        int y = sv.getScrollY();
        int pl = sv.getPaddingLeft();
        int pt = sv.getPaddingTop();
        int hy = (int) setY + y - pt;
        float hx = setX - pl;

        MyLayout lo = tv.getLayout();
        if (lo == null)
            return;
        int line = lo.getLineForVertical(hy);
        if (line > getRealLineCount() - 1)
            return;
        int offset = tv.getLineOffset(line, hx);
        if (offset == -1)
            return;
        int j1 = lo.getLineStart(line);
        int j2 = lo.getLineVisibleEnd(line);
        if (offset >= j1 && offset < j2 && j1 != j2) {
            preNoteInfo = A.getPreHighlight(offset);
            if (preNoteInfo != null) {
                int start = (int) (getBookType() == A.FILE_TXT ? offset - (A.getTxtRealPos(offset) - preNoteInfo.lastPosition)
                        : preNoteInfo.lastPosition);
                highlightText(true, start, start + preNoteInfo.highlightLength);

            } else {
                int start, end;
                if (!endWithTouch && (A.oneCharDict && Character.getType(tv.getText().charAt(offset)) == Character.OTHER_LETTER)) {
                    start = offset - 1;
                    end = offset + 1;
                    j1 = 0;
                    first_select_pos = start + j1 + 1;
                } else {
                    String s = tv.getText2().substring(j1, j2);
                    int pos = offset - j1;
                    while (pos > 0 && A.PUNCTUATIONS.indexOf(s.charAt(pos)) != -1)
                        pos--;
                    start = -1;
                    end = s.length();
                    if (!endWithTouch) {
                        for (int i = pos - 1; i >= 0; i--)
                            if (isSplitChar(s.charAt(i))) {
                                start = i;
                                break;
                            }
                        first_select_pos = start + j1 + 1;
                    }
                    for (int i = pos + 1; i < s.length(); i++)
                        if (isSplitChar(s.charAt(i))) {
                            end = i;
                            break;
                        }
                }
                select_text_state = true;
                if (endWithTouch)
                    highlightText(true, first_select_pos, end + j1);
                else
                    highlightText(true, start + j1 + 1, end + j1);
            }
        }
    }

    private boolean isSplitChar(char c) {
        return A.PUNCTUATIONS.indexOf(c) != -1 || (A.oneCharDict && Character.getType(c) == Character.OTHER_LETTER);
    }

    protected void do_text_select(boolean lookup) {
        if (web != null)
            web.selectAndCopyText();
        else {
            inverseLayoutVisible(true);
            selectText(false);
            if (lookup)
                lookupWordAutomatically();
        }
    }

    ////---------------------------------------

    public void setFontSize() {
        if (txtView.getSpanned() != null &&
                txtView.getSpanned().getSpans(0, txtView.getText().length(), MyFloatSpan.class).length > 0) {
            refreshTxtRender();
            return;
        }
//		setFontSizeHandler();
        int delay = 200;
        handler.removeMessages(SET_FONT_SIZE);
        handler.sendEmptyMessageDelayed(SET_FONT_SIZE, delay);
    }

    private void setFontSizeHandler() {
        if (web != null) {
            updateWebViewFontSize(false);
            return;
        }

        MyLayout layout = txtView.getLayout();
        if (layout == null)
            return;
        saveLastPostion(false);
        int y = txtScroll.getScrollY();
        int line = layout.getLineForVertical(y);
        int p = layout.getLineStart(line);
        txtViewSetTextSize(A.fontSize);
        txtScrollByDelay(p);
        statusHandlerSendMessage(1, 200);
        resetFlipCache();
    }

    private void txtScrollByDelay(int p) {
        handler.removeMessages(SCROLL_NO_DELAY);
        handler.sendMessageDelayed(handler.obtainMessage(SCROLL_NO_DELAY, p, 0), 100);
    }

    private void txtScrollNoDelay(int p) {
        MyLayout layout = txtView.getLayout();
        if (layout != null) {
            int line = layout.getLineForOffset(p);
            int y = txtView.getLineTop2(line);
            txtScrollTo(y);
        }
    }

    //-----------------webView----------------
    public MRBookView web;
    int inversedWebLastY = -1, inversedWebLastChapter = -1;

    private void doChromeButton(boolean fromLongTap) {
        if (fromLongTap && web != null && !T.isNull(htmlSrc)) { //for test only, view-source
//			web.loadData(htmlSrc, "text/plain", "UTF-8");
            String s = htmlSrc + "\n@\n" + MRBookView.ebook_css;
            web.loadDataWithBaseURL("", "<pre>" +
                    s.replace("<", "&lt;").replace(">", "&gt;").replace("\n@\n", "<br><hr><br>")//.replace("\n", "<br>")
                    + "</code>", "text/html", "UTF-8", null);
            return;
        }

        if (isPdf())
            pdfTextReflowSwitch(true);
        else
            doWebViewSwitch(true);
    }

    private void doWebViewSwitch(boolean reverse) {
        if (reverse)
            A.useWebView = !A.useWebView;
        if (chromeIv != null)
            chromeIv.setImageResource(A.useWebView ? R.drawable.mrbookview2 : R.drawable.chromebookview2);
        if (A.useWebView) {
            txtViewSetText("");
//			showInWebView(htmlSrc); //this won't include css in html
            reloadBook();
        } else if (web != null) {
            inversedWebLastChapter = A.lastChapter;
            inversedWebLastY = web.getScrollY();
            removeWebView();
            preNextChapterText = oldPriorChapterText = null;
            reloadBook();
        }

        inverseLayoutVisible(true);
    }

    private void showInWebView(String html) {
        statusLay.setVisibility(View.GONE);

        createWebView();
        if (!isTxtScrollReady)
            web.lastY = web.lastX = A.lastWebY;
        if (inversedWebLastY != -1 && inversedWebLastChapter == A.lastChapter) {
            web.lastY = inversedWebLastY;
            inversedWebLastY = -1;
            inversedWebLastChapter = -1;
        }

        web.clearHistory();
        if (isWebBook() && A.ebook.getChapters().get(A.lastChapter).url != null) {
//			web.loadUrl(A.ebook.getChapters().get(A.lastChapter).url);
            loadWebUrl(web, A.ebook.online_site, A.ebook.getChapters().get(A.lastChapter).url);
            return;
        }

        MRBookView.ebook_css = null;
        if (getBookType() == A.FILE_HTML) {
            web.loadUrl("file://" + A.lastFile);
            return;
        }
        if (!isHtmlContent())
            return;

        if (A.ebook.getChapters().size() == 0)
            return;
        if (A.lastChapter >= A.ebook.getChapters().size())
            A.lastChapter = A.ebook.getChapters().size() - 1;
        Chapter c = A.ebook.getChapters().get(A.lastChapter);
        MRBookView.ebook_css = c.css_str;
        WebSettings settings = web.getSettings();

//		settings.setUseWideViewPort(true);
        if (c.id_Tag == null && c.filename != null && c.filename.length() > 0 && !c.filename.endsWith(".xml") && !c.filename.endsWith(".xhtml")
                && c.usedFiles.size() <= 1) { //v1.3.7

            if (settings != null && !settings.getDefaultTextEncodingName().equals(A.fileEncoding))
                settings.setDefaultTextEncodingName(A.fileEncoding);
            web.loadPage(A.lastFile, c.filename);

        } else {

            T.saveFileText(A.tmp_out_file, "<html>" + html + "</html>");
            if (settings != null && !settings.getDefaultTextEncodingName().equals("UTF-8"))
                settings.setDefaultTextEncodingName("UTF-8");
            web.loadPage(A.lastFile, A.tmp_out_file);

        }
        showProgressIndicator();
        web.clearHistory();
    }

    public void webViewZoom(boolean in) {
        if (web == null)
            return;
        if (in)
            A.lastWebFontSize++;
        else
            A.lastWebFontSize--;
        updateWebViewFontSize(true);
    }

    public void updateWebViewFontSize(boolean showToast) {
        if (web == null)
            return;
        if (A.lastWebFontSize < 1)
            A.lastWebFontSize = 1;
        if (A.lastWebFontSize > 72)
            A.lastWebFontSize = 72;
        web.getSettings().setDefaultFontSize(A.lastWebFontSize);
        if (showToast)
            T.showToastText(this, "网页字号调整: " + A.lastWebFontSize, 0, Gravity.CENTER);
        A.log("------new defaultFontSize:" + A.lastWebFontSize);
    }

    private boolean disableFunctionsInWebView(View v) {
        boolean disable = web != null && (v == b_autoscroll || v == b_bookmark || v == b_speak || v == b_search);
        if (disable) {
            new MyDialog.Builder(this).setMessage(R.string.preview_disable_hint)
                    .setPositiveButton(R.string.preview_turn_off, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            doChromeButton(false);
                        }
                    }).setNegativeButton("Cancel", null).show();
        }
        return disable;
    }

    private void createWebView() {
        if (web != null)
            return;

        web = new MRBookView(this);
//		A.disableGPUView(web);
        web.setFocusableInTouchMode(false); //for long-tap to open link
        setWebViewVisual();
        web.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        web.getSettings().setDefaultTextEncodingName(A.fileEncoding);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

//		if (A.lastWebScale!=-1) { //use css font-size instead?
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
//			if (A.lastWebScale > 200)
//				A.lastWebScale = 200 + (A.lastWebScale-200)/3;
//			web.setInitialScale(A.lastWebScale);
//		}
        updateWebViewFontSize(false);

        baseFrame.addView(web, new FrameLayout.LayoutParams(-1, -1));
        web.bringToFront();
        initTopBottomLay(true);
        topLay.bringToFront();
        bottomLay.bringToFront();
        shadeView.bringToFront();
    }

    private void setWebViewVisual() {
        String background = "";
        if (A.useBackgroundImage) {
            web.setBackgroundColor(0x000000);
        } else
            background = "background-color:" + T.color2Html(A.backgroundColor) + ";";

        MRBookView.css = //"<meta name=\"viewport\" content=\"width="+baseFrame.getWidth()+", initial-scale=1\">"+
                MRBookView.CSS
                        //.replace("%SIZE", ""+A.fontSize)
                        .replace("%BACKGROUND", background).replace("%COLOR", T.color2Html(A.fontColor)).replace("%LEFT", "" + A.vd(A.leftMargin))
                        .replace("%RIGHT", "" + A.vd(A.rightMargin)).replace("%TOP", "" + A.vd(A.topMargin)).replace("%BOTTOM", "" + A.vd(A.bottomMargin));
    }

    private void reloadWebView() {
        A.lastWebY = inversedWebLastY = web.getScrollY();
        removeWebView();
        statusLay.setVisibility(View.GONE);
        createWebView();
        web.lastY = A.lastWebY;
        showInWebView(htmlSrc);
//		setWebViewVisual();
//		web.lastY = web.getScrollY();
//		web.loadUrl(web.lastUrl);
    }

    private void removeWebView() {
        if (web == null)
            return;
        web.setVisibility(View.GONE);//scrollbar won't disappear by only destroy()
        baseFrame.removeView(web);
        web.destroy();
        web = null;
        if (showStatusbar())
            statusLay.setVisibility(View.VISIBLE);
    }

    private void setChromeVisibility(boolean visible) {
        if (pdfPenIv != null)
            pdfPenIv.setVisibility(A.pdf_annot_button && isPdfNoflow() ? View.VISIBLE : View.GONE);
        if (chromeIv == null)
            return;
        boolean show = (isPdf() && A.pdf_text_button)
                || ((web != null || A.showChromeButton || A.getFileType() == A.FILE_CHM || A.getFileType() == A.FILE_HTML) && visible && isHtmlContent());
        if (isWebBook() || !show) {
            chromeIv.setVisibility(View.GONE);
            return;
        }
        chromeIv.setImageResource(isPdf() ? pdf.textReflow ? R.drawable.mrbookview2 : R.drawable.text_reflow : A.useWebView ? R.drawable.mrbookview2
                : R.drawable.chromebookview2);
        chromeIv.setVisibility(View.VISIBLE);
    }

    private boolean isWebViewKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && web != null && web.canGoBack())
            return true;
        if (keyCode == KeyEvent.KEYCODE_BACK && inWebReading && readWeb != null && readWeb.canGoBack())
            return true;
        return false;
    }

    private boolean isWebViewKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && web != null && web.canGoBack()) {
            web.goBack();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && inWebReading && readWeb != null) {
            if (readWeb.canGoBack()) {
                readWeb.goBack();
                return true;
            }
//			else if (!startWithWebReading)
//				switchWebReading(false);
        }
        return false;
    }

    private int WEB_SCROLL_OFF = A.d(5);

    private boolean webViewPageDown() {
        if (web != null) {
            final int x = web.getScrollX();
            final int y = web.getScrollY();
            if (A.verticalAlignment) {
                if (x <= 0) {
                    if (A.ebook != null && A.lastChapter < A.ebook.getChapters().size() - 1) {
                        web.lastX = 0;
                        do_next_chapter();
                    }
                } else
                    web.scrollTo(x - (web.getWidth() - WEB_SCROLL_OFF), y);
            } else {
//				web.pageDown(false);
                if (y + web.getHeight() >= web.getContentHeight2()) {
                    if (A.ebook != null && A.lastChapter < A.ebook.getChapters().size() - 1) {
                        web.lastY = 0;
                        do_next_chapter();
                    }
                } else
                    web.scrollTo(x, y + web.getHeight() - WEB_SCROLL_OFF);
            }
            return true;
        }
        return false;
    }

    private boolean webViewPageUp() {
        if (web != null) {
            final int x = web.getScrollX();
            final int y = web.getScrollY();
            if (A.verticalAlignment) {
                if (x + web.getWidth() >= web.getContentWidth2()) {
                    if (A.ebook != null && A.lastChapter > 0) {
                        web.lastX = -1;
                        do_prior_chapter();
                    } else if (web.canGoBack())
                        web.goBack();
                } else
                    web.scrollTo(x + (web.getWidth() - WEB_SCROLL_OFF), y);
            } else {
//				web.pageUp(false);
                if (y <= 0) {
                    if (A.ebook != null && A.lastChapter > 0) {
                        web.lastY = -1;
                        do_prior_chapter();
                    } else if (web.canGoBack())
                        web.goBack();
                } else
                    web.scrollTo(x, y - (web.getHeight() - WEB_SCROLL_OFF));
            }
            return true;
        }
        return false;
    }

    private boolean webViewSearch() {
        if (web != null) {
            return true;
        }
        return false;
    }

    private void resetPageCount() {
        currentPage = -1;
        totalPages = -1;
    }

    private void showDictMoreMenu(final String s) {
        String[] items = new String[]{"分享文字", "朗读文字", "搜索本书", "搜索网络", "词典选项"};
        MyDialog menu = new MyDialog(this, hMore, items, new MyDialog.MenuItemClick() {
            public void onClick(int which) {
                switch (which) {
                    case 0:
                        String text = s + ("\n\n(" + A.getAppNameAndVer() + ", 《" + A.getBookName() + "》)");
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_page) + ": " + A.getBookName());
                        intent.putExtra(Intent.EXTRA_TEXT, text);
                        startActivity(Intent.createChooser(intent, ""));
                        break;
                    case 1:
                        do_speak();
                        break;
                    case 2:
                        showPrefSearch(s);
                        break;
                    case 3:
//						openDictUrl(A.BAIDUE_URL, s);
                        Intent intent2 = new Intent(Intent.ACTION_WEB_SEARCH);
                        intent2.putExtra(SearchManager.QUERY, s);
                        startActivity(intent2);
                        break;
                    case 4:
                        customizeDict(ActivityTxt.this, false);
                        break;
                }
            }
        });
//		menu.setBuilderAnchor(root);
        menu.setRightAnimateOnly().showOverflow(hMore);
    }

    private boolean callGoogleTranslateApp(String text) {
        if (!googleTranslateInstalled())
            return false;

        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.apps.translate");
            Uri uri = new Uri.Builder()
                    .scheme("http")
                    .authority("translate.google.com")
                    .path("/m/translate")
                    .appendQueryParameter("q", text)
                    .appendQueryParameter("tl", A.destLanguage) // target language
                    .appendQueryParameter("sl", A.sourceLanguage) // source language
                    .build();
            intent.setData(uri);
            startActivity(intent);
        } catch (Exception e) {
            A.error(e);
//			T.showAlertText(this, getString(R.string.error), A.errorMsg(e));
            return false;
        }

        return true;
    }

    private boolean googleTranslateInstalled() {
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.translate", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    //-------------------2-page mode--------------
    private boolean checkDualPageMode() {
        boolean enable = dualPageEnabled();
        if (!enable) {
            if (txtScroll2.getVisibility() != View.GONE) {
                txtView2.setText("");
                txtScroll2.setVisibility(View.GONE);
                dualPageBackground.setBackgroundDrawable(null);
                dualPageBackground.setVisibility(View.GONE);
            }
        } else {
            txtScroll2.setVisibility(View.VISIBLE);
            setBookStyleBackground();
            if (!A.pageStyle2PageMode && dualPageBackground.getBackground() != null) {
                dualPageBackground.setBackgroundDrawable(null);
                dualPageBackground.setVisibility(View.GONE);
            }
        }
        return enable;
    }

    public int bookBackgroundStyle = -2;

    public void setBookStyleBackground() {
        if (!A.pageStyle2PageMode) {
            if (dualPageBackground.getVisibility() != View.GONE)
                dualPageBackground.setVisibility(View.GONE);
            return;
        }
        int dest = getDualDrawableRes();
        if (bookBackgroundStyle != dest) {
            bookBackgroundStyle = dest;
            if (dest == -1)
                dualPageBackground.setBackgroundDrawable(null);
            else
                dualPageBackground.setBackgroundResource(dest);
            if (dualPageBackground.getVisibility() != View.VISIBLE)
                dualPageBackground.setVisibility(View.VISIBLE);
        }
    }

    private int getDualDrawableRes() {
        return isPureBlackBackground() ? -1 : A.isWhiteFont(A.fontColor) ? R.drawable.bookstyle_night : R.drawable.bookstyle;
    }

    public boolean dualPageEnabled() {
        return A.dualPageEnabled() && web == null && !isPdf();
    }

    private void setTxtView2Visible() {
        if (A.getLastDisplayLine(txtScroll, -1) >= getRealLineCount() - 1) {
            if (txtView2.getVisibility() != View.INVISIBLE)
                txtView2.setVisibility(View.INVISIBLE);
        } else {
            int y = getDualPageNextY(txtScroll.getScrollY());
            txtScroll2ScrollTo(y);
        }
    }

    private void txtScroll2ScrollTo(int y) {
//		A.log("--------" + txtView.getWidth() + " " + txtView2.getWidth() +", y:"+y+", elapseTime:"+(SystemClock.elapsedRealtime() - goToLastTime));
        if (txtView2.getVisibility() != View.VISIBLE)
            txtView2.setVisibility(View.VISIBLE);
        if (txtScroll2.getScrollY() != y) {
            fixBottomForScrollTo(txtView, y);
            txtScroll2.scrollTo(0, y);
            if (SystemClock.elapsedRealtime() - goToLastTime < 100) {
                verifiyBottomFixed(txtScroll.getScrollY(), txtView.getHeight());
                updateProgressStatus();
            }
        }
    }

    Handler veryifiHandler;

    private void verifiyBottomFixed(int scrollY, int height) {
        if (veryifiHandler == null) {
            veryifiHandler = new Handler() {
                public void handleMessage(Message msg) {
                    if (txtScroll.getScrollY() != msg.arg1 || txtView.getHeight() < msg.arg2) {
                        A.log("*verifiyBottomFixed, txtView.height: " + txtView.getHeight() + ", should be: " + msg.arg2);
                        txtScrollTo(msg.arg1);
                    }
                }
            };
        }
        veryifiHandler.sendMessageDelayed(veryifiHandler.obtainMessage(0, scrollY, height), 100);
    }

    private int getDualPageNextY(int curY) {
        int curLine = txtView.getLayout().getLineForVertical(curY);
        int lastLine = A.getLastDisplayLine(txtScroll, -1);
        if (!txtView.isNormalImageLine(lastLine)) // if is image line but already full showed, return false too
            lastLine++;
        if (lastLine == curLine && curLine < txtView.getRealLineCount())
            lastLine++;
        int nextY = txtView.getLineTop2(lastLine);
        return nextY;
    }

    public int txtKeepPage(int add) {
        return dualPageEnabled() ? 2 + add : 1;
    }

    private boolean isDisableMove() {
//		return A.disableMove;
        return A.disableMove || A.flip_animation > 0 || dualPageEnabled();
    }

    private boolean isPage2Visible() {
        return txtScroll2.getVisibility() == View.VISIBLE && txtView2.getVisibility() == View.VISIBLE;
    }

    boolean hasMediaTag;

    private void txtViewSetText(CharSequence text) {
//		A.log("------* setText");
        yBeforePageDown.clear();
        if (A.missedFonts != null)
            A.missedFonts.clear();

        setCssBodyStyles();
        if (A.isJapanese && A.getFileType() == A.FILE_TXT)
            text = JpRubyTxt.dealJpRubyTxt(text);
        txtView.setText(text);

        if (A.getFileType() == A.FILE_EPUB && htmlSrc != null) { //for epub3
            hasMediaTag = htmlSrc.indexOf("<audio ") != -1 || htmlSrc.indexOf("<video ") != -1;
            showCssMissedFontsTip();
        } else
            hasMediaTag = false;

        contentLay.lastDrawY1 = -1;
        if (dualPageEnabled()) {
            txtView2.setVisibility(View.INVISIBLE);
            txtView2.setText(txtView.getText());
        }
        checkDualPageMode();

        if (updateOnlineChapter(A.lastChapter, txtView.getText2(), false)) {
            online_caching = true;
            createProgressDlg("", A.ebook.getChapters().get(A.lastChapter).name + "\n" + getString(R.string.downloading_content));
            clearTxtView();
        }
    }

    private String cssBodyBackgrondImage;
    private Float cssFontSize;

    private void setCssBodyStyles() {
        MRTextView.global_alignment = 0;
        if (A.ebook == null || A.lastChapter > A.ebook.getChapters().size() - 1)
            return;

        Chapter c = A.ebook.getChapters().get(A.lastChapter);
        CSS css = c.css;
        CSS.Style bodyStyle = MyHtml.getClassStyle("body", c.css_body_id, c.css_body_class, c, null, null);
        if (c.css_body_style != null) {
            bodyStyle = new CSS.Style(bodyStyle);
            bodyStyle.scanPropertyForStyle(c.css_body_style);
        }

        String cssFont = null;
        Float fontSize = null;
        if (bodyStyle != null) {
            if (bodyStyle.align > 1)
                MRTextView.global_alignment = bodyStyle.align;
            if (bodyStyle.fontFace != null) {
                cssFont = bodyStyle.fontFace;
                if (A.forceCssFontName == null || !A.forceCssFontName.equals(cssFont)) {
                    A.forceCssFontName = cssFont;
                    clearTxtView();
                    A.setTxtViewTypeface();
                }
            }
            if (bodyStyle.fontSize > 0 && bodyStyle.fontSize != 1f) {
                fontSize = bodyStyle.fontSize;
            }
        }

        if (fontSize != null) {
            if (fontSize != cssFontSize) {
                clearTxtView();
                cssFontSize = fontSize;
                txtViewSetTextSize(A.fontSize);
            }
        } else if (cssFontSize != null) {
            cssFontSize = null;
            clearTxtView();
            txtViewSetTextSize(A.fontSize);
        }

        if (cssFont == null && A.forceCssFontName != null) {
            A.forceCssFontName = null;
            clearTxtView();
            A.setTxtViewTypeface();
        }

        if (css == null || bodyStyle == null || bodyStyle.backgroundImage == null
                || !chapterHasOnlySingleFile(c)) { //todo, bug: Background-Image for only one page (kehuan.epub)
            if (cssBodyBackgrondImage != null) {
                cssBodyBackgrondImage = null;
                txtScroll.setBackgroundDrawable(null);
                txtScroll2.setBackgroundDrawable(null);
            }
            return;
        }

        String s = bodyStyle.backgroundImage;
        if (s == cssBodyBackgrondImage)
            return;

        int i1 = s.indexOf("("), i2 = s.indexOf(")");
        if (i1 != -1 && i2 > i1) {
            String img = s.substring(i1 + 1, i2);
            Drawable d = A.ebook.getDrawableFromSource(img, 0);
            if (d != null) {
                String property = bodyStyle.css_text;
                boolean repeat = property == null || !property.contains("no-repeat");
                boolean contain = property != null && property.contains("contain");
                boolean cover = property != null && property.contains("cover");

                int dw = d.getIntrinsicWidth(), dh = d.getIntrinsicHeight();
                if (dw > 0 && dh > 0) {
                    if (contain) {
                        int w = txtScroll.getWidth(), h = txtScroll.getHeight();
                        if (dh / h > dw / w)
                            d = T.zoomDrawable(A.getContext().getResources(), d, dw * h / dh, h);
                        else
                            d = T.zoomDrawable(A.getContext().getResources(), d, w, dh * w / dw);
                    }
                }

                if (!cover && repeat && (d instanceof BitmapDrawable)) {
                    ((BitmapDrawable) d).setTileModeX(android.graphics.Shader.TileMode.REPEAT);
                    ((BitmapDrawable) d).setTileModeY(android.graphics.Shader.TileMode.REPEAT);
                }

                if (!repeat && !contain && !cover) {
                    String position = CSS.propertyTagValue(property, "background-position", false);
                    String size = CSS.propertyTagValue(property, "background-size", false);
                    if (position != null && size != null && (position.contains("bottom") || position.contains("right")))
                        d = null;
                }

                cssBodyBackgrondImage = s;
                txtScroll.setBackgroundDrawable(d);
                txtScroll2.setBackgroundDrawable(d);
            }
        }
    }

    private boolean chapterHasOnlySingleFile(Chapter c) {
        if (c.usedFiles.size() == 0)
            return true;
        if (c.usedFiles.size() == 1) {
            String f1 = c.filename;
            String f2 = c.usedFiles.get(0);
            return f1.endsWith(f2) || f2.endsWith((f1));
        }
        return false;
    }

    private MyDialog.Builder cssFontDlg;

    private boolean showCssMissedFontsTip() {
        final ArrayList<String> fonts = isPdf() ? pdfMissedFonts : A.missedFonts;
        if (fonts != null && fonts.size() > 0) {
            try {
                StringBuilder sb = new StringBuilder();
                for (String name : fonts)
                    sb.append(" <a href=\"" + name + "\">" + name + ".ttf" + "</a>  ");
                String html = "<b>" + (isPdf() ? "PDF " + getString(R.string.button_options)
                        : "精排版选项") + " -> "
                        + (isPdf() ? getString(R.string.pdf_missed_font_hint)
                        : getString(R.string.epub_embedded_fonts)) + "</b><br><br>"
                        + getString(R.string.epub_embedded_fonts_missied, "<b>" + sb.toString()
                        + "</b><br>", "\"" + A.outerFontsFolder + "\"");

                if (cssFontDlg != null)
                    cssFontDlg.dismiss();

                cssFontDlg = new MyDialog.Builder(ActivityTxt.this);
                handler.sendEmptyMessageDelayed(GPU_SHADOW, 50);
                cssFontDlg.setOnDismissListener(new OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        A.setSystemUiVisibility(true);
                        eraseGPUShadow(0);
                    }
                });

                TextView tv = new TextView(this);
                tv.setTextSize(A.fontSize > 19 ? A.fontSize - 2 : A.fontSize);
                tv.setPadding(A.d(4), A.d(4), A.d(4), A.d(4));
//				tv.setText(Html.fromHtml(html));
                T.setTextViewLinkClickable(tv, html, new T.OnUrlClick() {
                    public void onClick(String url) {
                        downloadFont(url, fonts.size() > 1);
                    }
                });

                cssFontDlg.setView(tv)
                        .setPositiveButton(R.string.open_note, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (fonts != null && fonts.size() > 0)
                                    downloadFont(fonts.get(0), false);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
//						.setCancelable(false)
                        .show();

                return true;
            } catch (Exception e) {
                A.error(e);
            }
        }
        return false;
    }

    private void downloadFont(final String font, final boolean restartTip) {
        String key = font.replace("\"", "").replace("'", "").trim()
                + (A.isChinese ? " 字体下载" : " ttf download");
        new PrefDownloadCover(ActivityTxt.this, new PrefDownloadCover.OnSaveImage() {
            public void onGetImageFile(final String saveTo, Drawable d) {
                if (saveTo != null) {
                    if (T.getOnlyFilename(saveTo).toLowerCase().equals(font.toLowerCase())) {
                        restartForFont();
                    } else {
                        String s = "\"<b>" + T.getFilename(saveTo) + "</b>\" " + getString(R.string.rename_file)
                                + " \"<b>" + font + T.getFileExt(saveTo) + "</b>\"?";
                        new MyDialog.Builder(ActivityTxt.this)
                                .setMessage(Html.fromHtml(s))
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.ok, new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (T.renameFile(saveTo, T.getFilePath(saveTo) + "/" + font + T.getFileExt(saveTo), true))
                                            restartForFont();
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, null)
                                .show();

                    }
                }
            }

            private void restartForFont() {
                if (restartTip) {
                    new MyDialog.Builder(ActivityTxt.this)
//							.setTitle(R.string.confirmation)
                            .setMessage(R.string.use_css_font_now)
//							.setCancelable(false)
                            .setPositiveButton(android.R.string.ok, new OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    saveLastPostion(true);
                                    restartReaderToTxt();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                } else {
                    saveLastPostion(true);
                    restartReaderToTxt();
                }
            }
        }, null, key, 2, false).show();
    }

    private void restoreAppendedBottomHandler(final boolean restore) {
        /* check setLayoutTime in MyTextView.OnMeasure instead */

        if (A.isInAutoScroll)
            return;
        if (!A.immersive_fullscreen || isPdf() || web != null)
            return;
        if (!A.sysHasNavBar())
            return;

//		if (dualPageEnabled() && lastLineDisplayed()) {
        if ((restore || dualPageEnabled()) && lastLineDisplayed()) { //v5.2.2
            disableTxtViewDraw();
            new Handler() {
                public void handleMessage(Message msg) {
                    txtScrollTo(msg.what);
                    enableTxtViewDraw();
                }
            }.sendEmptyMessageDelayed(txtScroll.getScrollY(), 100);
        }
    }

    private void txtScrollTo(int y) {
        initViewParams();
        if (txtScroll.getScrollY() != y) {
            fixBottomForScrollTo(txtView, y);
            txtScroll.scrollTo(0, y);
            contentLay.lastDrawY1 = -1;
        }
        if (checkDualPageMode())
            setTxtView2Visible();
    }

    private void txtScrollSetPadding(int left, int top, int right, int bottom) {
        if (dualPageEnabled()) {
            if (A.pageStyle2PageMode) {
                int m1 = A.minDualPageMargin();
                if (left < m1)
                    left = m1;
            }
            right = left;

            int m2 = A.isTablet ? 12 : A.d(8);
            if (paddingDifferent(txtLay, m2, 0, m2, 0))
                txtLay.setPadding(m2, 0, m2, 0);
            if (paddingDifferent(txtScroll, left, top, right, bottom)) {
                setStatusBarPadding(m2, left, bottom);
                txtScroll.setPadding(left, top, right, bottom);
                txtScroll2.setPadding(left, top, right, bottom);
            }
        } else {
            if (paddingDifferent(txtLay, 0, 0, 0, 0)) {
                txtLay.setPadding(0, 0, 0, 0);
                setStatusBarPadding(0, 0, 0);
            }
            if (paddingDifferent(txtScroll, left, top, right, bottom))
                txtScroll.setPadding(left, top, right, bottom);
        }
    }

    private void setStatusBarPadding(int m2, int left, int bottom) {
        if (showStatusbar()) {
            if (m2 == 0) {
                status2Page.setVisibility(View.GONE);
                statusLay.setPadding(0, 0, 0, 0);
                setStatusBarProperties(false);
            } else {
                int p1 = m2 + left * 12 / 10;
                int b = bottom / 2 - statusLay.getHeight() * 3 / 10;
                if (b < A.d(5))
                    b = A.d(5);
                if (A.isHighResulotionTablet())
                    b = b - A.d(4);
                int l = p1 - A.d(3);
                statusLay.setPadding(l > 0 ? l : 0, 0, p1 + A.d(1), b);
                statusLayHori.setBackgroundDrawable(null);
                statusLeft.setBackgroundDrawable(null);
                status2Page.setVisibility(View.VISIBLE);
                remainingLay.setVisibility(View.GONE);
                setStatusLayTextColorFor2PageMode();
            }
        }
    }

    public void setStatusLayTextColorFor2PageMode() {
        int color = A.fontColor;
        color = Color.argb(200, Color.red(color), Color.green(color), Color.blue(color));
        statusMiddle21.setTextColor(color);
        statusMiddle22.setTextColor(color);
        statusRight.setTextColor(color);
        statusLeft.setTextColor(color);
        statusLeft2.setTextColor(color);
        int size = A.statusFontSize;
        statusMiddle21.setTextSize(size);
        statusMiddle22.setTextSize(size);
        statusRight.setTextSize(size - 1);
        statusLeft.setTextSize(size - 1);
        statusLeft2.setTextSize(size - 1);

        Typeface typeFace = A.getTypeFace(A.statusCustomizeFont ? A.statusFontName : "sans-serif", Typeface.NORMAL);
        statusMiddle21.setTypeface(typeFace);
        statusMiddle22.setTypeface(typeFace);
        statusLeft.setTypeface(typeFace);
        statusLeft2.setTypeface(typeFace);
        statusRight.setTypeface(typeFace);
    }

    private boolean paddingDifferent(View sv, int left, int top, int right, int bottom) {
        return sv.getPaddingLeft() != left || sv.getPaddingTop() != top || sv.getPaddingRight() != right || sv.getPaddingBottom() != bottom;
    }

    //	private boolean init2PagePadding = false;
    private void checkLandscape2PagePadding() {
        if (dualPageEnabled()) {
//			init2PagePadding = true;
            A.setTxtScrollPadding(txtScroll); //after set txtScroll2 to visible, txtScroll1 padding will be changed
            txtScrollSetPadding(txtScroll.getPaddingLeft(), txtScroll.getPaddingTop(), txtScroll.getPaddingRight(), txtScroll.getPaddingTop());
//			contentLay.requestLayout();
        }
    }

    private void txtViewSetTextSize(float size) {
        if (cssFontSize != null)
            size = cssFontSize * size;
        txtView.setTextSize(size);
        txtView2.setTextSize(size);
//		checkDualPageMode(true, -1);
        yBeforePageDown.clear();
        forceNoCurlCache = true;
    }

    private void loadTheme(String name, boolean forceOnlyColor) {
        boolean oldMainNightTheme = A.mainNightTheme;
        resetFlipCache();
        if (isPdfNoflow()) {
            pdfRemoveThumb();
            A.pdf_theme = name.equals(A.DAY_THEME) ? 1 : 0;
            pdfUpdateView(true);
            setStatusBarProperties(true);
        } else {
            boolean lastLineDisplayed = resumeTime > 0 && getBookType() == A.FILE_EBOOK && lastLineDisplayed();
            if (lastLineDisplayed)
                saveLastPostion(true);

            MyLayout layout = txtView.getLayout();
            int p = -1;
            if (!forceOnlyColor)
                forceOnlyColor = A.themeOnlyColorDiffToCur(name);
            if (!forceOnlyColor && layout != null) {
                int y = txtScroll.getScrollY();
                int line = layout.getLineForVertical(y);
                p = layout.getLineStart(line);
            }
            A.loadTheme(name, forceOnlyColor);
            forceNoCurlCache = true;

            if (!forceOnlyColor)
                checkStatusBar();
            else
                setStatusBarProperties(true);
            if (p != -1)
                txtScrollByDelay(p);

            if (checkDualPageMode())
                setTxtView2Visible();
            ;

            if (lastLineDisplayed) { //last line error, reset
                new Handler() {
                    public void handleMessage(Message msg) {
                        txtViewSetText(txtView.getText());
                        goToEBookLastPosition();
                    }
                }.sendEmptyMessage(0);
            }
        }
//		if (isPdf() || A.lastTheme.equals(A.DAY_THEME) || A.lastTheme.equals(A.NIGHT_THEME))
//			A.mainNightTheme = A.isNightState(false);
//		A.mainThemeChanged = oldMainNightTheme != A.mainNightTheme;
    }

    public boolean initClickTip(final boolean forceShow) {
        return false;
//		if (!forceShow && A.showedReaderClickTip)
//			return false;
//		A.showedReaderClickTip = true;
//
//		new Handler() {
//			public void handleMessage(Message msg) {
//				try {
//					ClickTip.drawable = new BitmapDrawable(getResources(), getPageShot(false, false));
//					startActivityForResult(new Intent(ActivityTxt.this, ClickTip.class), forceShow? 0 : 123);
//				} catch (Exception e) {
//					A.error(e);
//				}
//			}
//		}.sendEmptyMessage(0);
//		return true;
    }

    public Bitmap getPageShot(boolean hideStatusBar, boolean highQuality) {
        Bitmap bm = null;
        try {
            if (flipView.getVisibility() == View.VISIBLE || (curl3d != null && curl3d.getVisibility() == View.VISIBLE))
                if (txtView.getHeight() < A.getPageHeight() && pdf == null && !A.dualPageEnabled())
                    txtView.setForceHeight(A.getPageHeight()); //v2.3.5

            bm = Bitmap.createBitmap(baseFrame.getWidth(), baseFrame.getHeight(), highQuality ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas c = new Canvas(bm);
//			DisplayListCanvas c2 = new DisplayListCanvas(bm);
            A.setBackgroundImage(c);
            if (dualPageBackground.getVisibility() == View.VISIBLE)
                dualPageBackground.draw(c);
            contentLay.draw(c);
        } catch (OutOfMemoryError e) {
            A.error(e);
        }
        return bm;
    }

    private int last_statistics_Y = -1;
    private int last_statistics_Chapter;
    private int read_words;
    private long last_statistics_add_time;

    private void statistics_add() {
        if (A.isInAutoScroll)
            return;
        if (pageDirection != PAGE_DOWN)
            return;
        if (last_statistics_Y != -1 && last_statistics_Y == txtScroll.getScrollY() && last_statistics_Chapter == A.lastChapter)
            return;
        if (SystemClock.elapsedRealtime() - last_statistics_add_time > 1000) {//page read time must > 1s
            last_statistics_Y = txtScroll.getScrollY();
            last_statistics_Chapter = A.lastChapter;
            A.statistics_pages = A.statistics_pages + (isPage2Visible() ? 2 : 1);
            try {
                int count = getDisplayTextWords();
                A.statistics_words += count;
                read_words += count;
            } catch (Exception e) {
                A.error(e);
            }
        }
        last_statistics_add_time = SystemClock.elapsedRealtime();
    }

    private int getDisplayTextWords() {
        MyLayout layout = txtView.getLayout();
        if (layout == null || layout.getLineCount() == 0)
            return 0;

        int y = txtScroll.getScrollY();
        int line = layout.getLineForVertical(y);
        int p1 = layout.getLineStart(line);
        line = A.getLastDisplayLine(isPage2Visible() ? txtScroll2 : txtScroll, -1); //v2.3.5
        if (line > txtView.getRealLineCount() - 1)
            line = txtView.getRealLineCount() - 1;
        int p2 = layout.getLineVisibleEnd(line);
        String text = txtView.getText().subSequence(p1, p2).toString();
        int wordCount = T.getWordsCount(text, A.isAsiaLanguage);
        return wordCount;
    }

    private int getDisplayTextLength(MRTextView txtView, ScrollView txtScroll) {
        MyLayout layout = txtView.getLayout();
        int y = txtScroll.getScrollY();
        int line = layout.getLineForVertical(y);
        int p1 = layout.getLineStart(line);
        line = A.getLastDisplayLine(txtScroll, -1);
        int p2 = layout.getLineVisibleEnd(line);
        return p2 - p1;
    }

    public boolean onLongClick(final View v) {
        if (v == daynightLay) {
            if (A.debug)
                autoCollectBookMetas(true, false);
        }

        if (v == hHighlight) {
            doHighlight(true);
            return true;
        }

        if (v == hDict) {
            showSelectBar();
            if (A.translateInited)
                doDictButton(true);
            else
                customizeDict(ActivityTxt.this, true);
            return true;
        }

        if (v == statusLeftPanel)
            return doEvent2(A.statusClickLeft2);

        if (v == statusMiddle || v == statusMiddle21 || v == statusMiddle22)
            return doEvent2(A.statusClickMiddle2);

        if (v == statusRight)
            return doEvent2(A.statusClickRight2);

        if (v == chromeIv) {
            if (web != null) {
                doChromeButton(true);
                return true;
            } else
                return false;
        }

        return false;
    }

    //---------------------PDF----------------
    public boolean isPdf() {
        return getBookType() == A.FILE_PDF;
    }

    public boolean isPdfReflow() {
        return pdf != null && pdf.textReflow;
    }

    public boolean isPdfNoflow() {
        return pdf != null && !pdf.textReflow;
    }

    View pdfSearchLay, pdfTopLay, pdfBottomLay;
    ImageView antSelected, antNote, antFreeText, antInk, antLine, antArrow, antRect, antEllipse, antCancel,
            apCancel, apThickness, apColor, apFillColor, apNote, pdfLockIv;
    ClearableEditText pdfSearchEdit;
    String pdfSearchString;
    PDFThumbView2 pdfThumb;
    FrameLayout pdfBaseFrame;

    public void pdfRemoveThumb() {
        if (isPdf() && pdf != null && pdfThumb != null) {
            inverseLayoutVisible(true);
            try {
                pdf.set_thumb(null);
                pdfThumb.setVisibility(View.GONE);
                if ((Integer) pdfThumb.getTag() == Global.def_view)
                    pdfThumb.thumbClose();
                pdfBaseFrame.removeView(pdfThumb);
            } catch (Exception e) {
                A.error(e);
            }
            pdfThumb = null;
        }
    }

    private void pdfShowThumb(boolean show) {
        if (!show) {
            if (pdfThumb != null && pdfThumb.getVisibility() == View.VISIBLE)
                pdfThumb.setVisibility(View.GONE);
            return;
        }

        if (!A.pdf_show_thumb || !isPdf() || pdf == null || pdf.textReflow) {
            if (pdfThumb != null)
                pdfThumb.setVisibility(View.GONE);
            return;
        }
        if (pdfThumb == null) {
            if (pdf == null || pdf.pdfView == null)
                return;
            pdfThumb = new PDFThumbView2(this, null);
            pdfThumb.setTag(Global.def_view);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1, A.d(A.isTablet ? 150 : 100));
            lp.bottomMargin = bottomLay.getHeight();
            lp.gravity = Gravity.BOTTOM;
            pdfBaseFrame.addView(pdfThumb, lp);
            pdfThumb.thumbOpen(m_doc, new PDFViewThumb2.PDFThumbListener() {
                public void OnPageClicked(int pageno) {
                    pdfGotoPage(pageno, true);
                    percentView.setText("" + (pageno + 1) + "/" + m_doc.GetPageCount());
                }
            });
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    pdfShowThumb(true);
                }
            }.sendEmptyMessage(0);
            return;
        }
        pdfThumb.setVisibility(View.VISIBLE);
        pdfThumb.thumbGotoPage(pdfGetCurrPageNo());
        pdf.set_thumb(pdfThumb);
    }

    private void pdfInitLay() {
        if (pdfLay == null) {
            pdfLay = ((ViewStub) findViewById(R.id.pdfStub)).inflate();
            pdfBaseFrame = (FrameLayout) pdfLay.findViewById(R.id.base);
            pdf = (PDFReader) pdfLay.findViewById(R.id.PDFView);
            pdfSearchLay = pdfLay.findViewById(R.id.searchLay);
            pdfTopLay = pdfLay.findViewById(R.id.pdfTop);
            pdfBottomLay = pdfLay.findViewById(R.id.pdfBottom);
            antNote = (ImageView) pdfLay.findViewById(R.id.imageView1);
            antFreeText = (ImageView) pdfLay.findViewById(R.id.imageView2);
            antFreeText.setVisibility(View.GONE);

            antInk = (ImageView) pdfLay.findViewById(R.id.imageView3);
            antLine = (ImageView) pdfLay.findViewById(R.id.imageView4);
            antArrow = (ImageView) pdfLay.findViewById(R.id.imageView5);
            antRect = (ImageView) pdfLay.findViewById(R.id.imageView6);
            antEllipse = (ImageView) pdfLay.findViewById(R.id.imageView7);
            antCancel = (ImageView) pdfLay.findViewById(R.id.imageView8);
            apCancel = (ImageView) pdfLay.findViewById(R.id.ImageView01);
            apThickness = (ImageView) pdfLay.findViewById(R.id.ImageView02);
            apColor = (ImageView) pdfLay.findViewById(R.id.ImageView03);
            apFillColor = (ImageView) pdfLay.findViewById(R.id.ImageView04);
            apNote = (ImageView) pdfLay.findViewById(R.id.ImageView05);
            pdfTopLay.setVisibility(View.GONE);
            pdfBottomLay.setVisibility(View.GONE);
            pdfSearchEdit = (ClearableEditText) findViewById(R.id.keyEdit);
            pdfHideSearchLay();
            pdfInitAnnotButtons();

            if (A.sysHasNavBar()) {
                pdfSearchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    public void onFocusChange(View view, boolean hasFocus) {
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                A.setSystemUiVisibility(true);
                            }
                        }, 400);
                    }
                });
//				pdfSearchEdit.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                pdfSearchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        A.setSystemUiVisibility(true);
//						if (actionId == EditorInfo.IME_ACTION_SEARCH)
//							onClick(searchB);
                        return false;
                    }
                });
            }

            pdfLockIv = (ImageView) pdfLay.findViewById(R.id.pdfLockIv);
            pdfLockIv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    A.pdf_scoll_lock = !A.pdf_scoll_lock;
                    pdf.pdfView.viewLockSide(A.pdf_scoll_lock, pdf.getContext());
                    inverseLayoutVisible(true);
                }
            });
//			pdfLockIv.setOnLongClickListener(new OnLongClickListener() {
//				public boolean onLongClick(View v) {
//					T.showToastText(ActivityTxt.this, getString(A.pdf_scoll_lock? R.string.pdf_moving_unlocked : R.string.pdf_moving_locked));
//					return false;
//				}
//			});
        }
    }

    private void pdfShowLockIcon(boolean visible) {
        if (pdfLockIv == null)
            return;
        if (pdf.textReflow)
            visible = false;
        if (visible) {
            pdfLockIv.setImageResource(A.pdf_scoll_lock ? R.drawable.pdf_unlock : R.drawable.pdf_lock);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) pdfLockIv.getLayoutParams();
            lp.bottomMargin = bottomLay.getHeight();
            if (A.pdf_show_thumb && pdfThumb != null) {
                if (pdfThumb.getHeight() == 0) {
                    new Handler() {
                        public void handleMessage(Message msg) {
                            pdfShowLockIcon(true);
                        }
                    }.sendEmptyMessage(0);
                    return;
                }
                lp.bottomMargin += pdfThumb.getHeight();
            }
            lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            lp.bottomMargin -= A.d(8);
        }
        pdfLockIv.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    View.OnClickListener onPdfAnnotButtonClick;

    private void pdfInitAnnotButtons() {
        if (onPdfAnnotButtonClick != null)
            return;
        onPdfAnnotButtonClick = new View.OnClickListener() {
            public void onClick(View v) {
                hideDotViews();
                if (v.getId() == R.id.optionB) {
                    pdfSetImmersiveMode(true);
                    LinearLayout ll = (LinearLayout) LayoutInflater.from(ActivityTxt.this).inflate(R.layout.search_options, null);
                    ll.findViewById(R.id.forwardcb).setVisibility(View.GONE);
                    ll.findViewById(R.id.TextView02).setVisibility(View.GONE);
                    final CheckBox caseCb = (CheckBox) ll.findViewById(R.id.casecb);
                    final CheckBox wholeCb = (CheckBox) ll.findViewById(R.id.wholecb);
                    caseCb.setChecked(A.searchCaseSensitive);
//					wholeCb.setChecked(A.searchWholeWord);
                    wholeCb.setVisibility(View.GONE);
                    new MyDialog.Builder(ActivityTxt.this).setTitle(R.string.search_options).setView(ll)
                            .setPositiveButton(R.string.ok, new OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    A.searchCaseSensitive = caseCb.isChecked();
//									A.searchWholeWord = wholeCb.isChecked();
                                    pdfSearchString = null;
                                    pdfSetImmersiveMode(false);
                                }
                            }).setNegativeButton(R.string.cancel, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            pdfSetImmersiveMode(false);
                        }
                    }).setOnCancelListener(new OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            pdfSetImmersiveMode(false);
                        }
                    }).show();

                }

                if (v.getId() == R.id.priorB) {
                    pdfSearchText(false);
                }
                if (v.getId() == R.id.nextB) {
                    pdfSearchText(true);
                }

                pdfSaveAnnotsForTime();
                if (v == antNote) {
                    if (antSelected == antInk && A.pdfStatus == STATUS.sta_ink)
                        pdf.annotEnd();
                    antSelected = antNote;
                    Global.tmpNoteText = null;
                    pdf.annotNote();
                    pdfShowAnnotLay();
                }
                if (v == antFreeText) {
                    if (antSelected == antInk && A.pdfStatus == STATUS.sta_ink)
                        pdf.annotEnd();
                    pdfSaveAnnotsForSecurity();
                    antSelected = antFreeText;
                    Global.tmpNoteText = null;
                    pdf.annotFreeText();
                    pdfShowAnnotLay();
                }
                if (v == antInk) {
                    if (antSelected == antInk && A.pdfStatus == STATUS.sta_ink)
                        pdf.annotEnd();
                    antSelected = antInk;
                    pdf.annotInk();
                    pdfShowAnnotLay();
                }
                if (v == antLine) {
                    if (antSelected == antInk && A.pdfStatus == STATUS.sta_ink)
                        pdf.annotEnd();
                    antSelected = antLine;
                    pdf.annotLine();
                    pdfShowAnnotLay();
                }
                if (v == antArrow) {
                    if (antSelected == antInk && A.pdfStatus == STATUS.sta_ink)
                        pdf.annotEnd();
                    antSelected = antArrow;
                    pdf.annotArrow();
                    pdfShowAnnotLay();
                }
                if (v == antRect) {
                    if (antSelected == antInk && A.pdfStatus == STATUS.sta_ink)
                        pdf.annotEnd();
                    antSelected = antRect;
                    pdf.annotRect();
                    pdfShowAnnotLay();
                }
                if (v == antEllipse) {
                    if (antSelected == antInk && A.pdfStatus == STATUS.sta_ink)
                        pdf.annotEnd();
                    antSelected = antEllipse;
                    pdf.annotEllipse();
                    pdfShowAnnotLay();
                }
                if (v == antCancel) {
                    if (antSelected == antInk && A.pdfStatus == STATUS.sta_ink)
                        pdf.annotEnd();
                    A.pdfStatus = STATUS.sta_none;
                    antSelected = null;
                    Global.tmpNoteText = null;
                    pdfHideAnnotLay(true, true);
                }

                if (v == apCancel) {
                    if (pdf.pdfView.getSelectedAnnot() != null) {
                        A.pdfStatus = STATUS.sta_annot;
                        pdf.annotRemove();
                        A.pdfAnnotUpdated = true;
                    }
                    if (antSelected == antInk && A.pdfStatus == STATUS.sta_ink)
                        pdf.annotEnd();
                    A.pdfStatus = STATUS.sta_none;
                    antSelected = null;
                    Global.tmpNoteText = null;
                    if (pdfTopLay.getVisibility() == View.VISIBLE)
                        pdfShowAnnotLay();
                    else
                        pdfBottomLay.setVisibility(View.GONE);
                }
                if (v == apThickness) {
                    Annotation annot = pdf.pdfView.getSelectedAnnot();
                    String[] items = new String[20];
                    int cur = (int) (annot == null ? (antSelected == antFreeText ? Global.freeTextSize : Global.inkWidth) : annot.GetStrokeWidth());
                    for (int i = 0; i < items.length; i++) {
                        int value = i + (antSelected == antFreeText ? 11 : 1);
                        items[i] = (value == cur ? MyDialog.OFFTAG : "") + value;
                    }
                    pdfSetImmersiveMode(true);
                    new MyDialog.Builder(ActivityTxt.this).setTitle(antSelected == antFreeText ? R.string.font_size : R.string.pdf_pen_width)
                            .setItems(items, new OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (pdf.pdfView.getSelectedAnnot() != null) {
                                        pdf.pdfView.getSelectedAnnot().SetStrokeWidth(which + 1);
                                        pdf.pdfView.refreshCurPage();
                                        A.pdfAnnotUpdated = true;
                                    } else if (antSelected == antFreeText) {
                                        Global.freeTextSize = which + 11;
                                    } else {
                                        if (A.pdfStatus == STATUS.sta_ink) {
                                            onPdfAnnotButtonClick.onClick(antInk);
                                        }
                                        Global.inkWidth = which + 1;
                                    }
                                    pdfSetImmersiveMode(false);
                                }
                            }).setOnCancelListener(new OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            pdfSetImmersiveMode(false);
                        }
                    }).show();
                }
                if (v == apColor) {
                    pdfSetImmersiveMode(true);
                    Annotation annot = pdf.pdfView.getSelectedAnnot();
                    int c = annot == null ? Global.inkColor : annot.GetStrokeColor();
                    ColorDialog dlg = new ColorDialog(ActivityTxt.this, getString(R.string.highlight_color), true, c, new ColorDialog.OnSaveColor() {
                        public void getColor(int color) {
                            Global.inkColor = color;
                            if (pdf.pdfView.getSelectedAnnot() != null) {
                                pdf.pdfView.getSelectedAnnot().SetStrokeColor(color);
                                pdf.pdfView.refreshCurPage();
                                A.pdfAnnotUpdated = true;
                            } else {
                                if (A.pdfStatus == STATUS.sta_ink) {
                                    onPdfAnnotButtonClick.onClick(antInk);
                                }
                            }
                        }
                    });
                    dlg.setOnDismissListener(new OnDismissListener() {
                        public void onDismiss(DialogInterface dialog) {
                            A.setSystemUiVisibility(true);
                            pdfSetImmersiveMode(false);
                        }
                    });
                    dlg.show();
                }
                if (v == apFillColor) {
                    pdfSetImmersiveMode(true);
                    Annotation annot = pdf.pdfView.getSelectedAnnot();
                    int c = annot == null ? Global.fillColor : annot.GetFillColor();
                    ColorDialog dlg = new ColorDialog(ActivityTxt.this, getString(R.string.fill_color), true, c, new ColorDialog.OnSaveColor() {
                        public void getColor(int color) {
                            Global.fillColor = color;
                            if (pdf.pdfView.getSelectedAnnot() != null) {
                                pdf.pdfView.getSelectedAnnot().SetFillColor(color);
                                pdf.pdfView.refreshCurPage();
                                A.pdfAnnotUpdated = true;
                            }
                        }
                    });
                    dlg.setOnDismissListener(new OnDismissListener() {
                        public void onDismiss(DialogInterface dialog) {
                            A.setSystemUiVisibility(true);
                            pdfSetImmersiveMode(false);
                        }
                    });
                    dlg.show();
                }
                if (v == apNote) {
                    pdfSetImmersiveMode(true);
                    final EditText et = new EditText(ActivityTxt.this);
                    Annotation annot = pdf.pdfView.getSelectedAnnot();
                    et.setText(annot == null ? (Global.tmpNoteText == null ? "" : Global.tmpNoteText) : annot.GetType() == 3 ? annot.GetEditText()
                            : annot.GetPopupText());
                    new MyDialog.Builder(ActivityTxt.this).setView(et)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String text = et.getText().toString();
                                    Annotation annot = pdf.pdfView.getSelectedAnnot();
                                    if (annot != null) {
                                        if (annot.GetType() == 3) {
//										pdf.setFreeText(annot, text);
                                            annot.SetEditText(text);
                                            pdf.pdfView.refreshCurPage();
                                        } else
                                            annot.SetPopupText(text);
                                        A.pdfAnnotUpdated = true;
                                    } else if (text.length() > 0) {
                                        Global.tmpNoteText = text;
                                    } else
                                        Global.tmpNoteText = null;
                                }
                            }).setNegativeButton(R.string.cancel, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            pdfSetImmersiveMode(false);
                        }
                    }).setOnCancelListener(new OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            pdfSetImmersiveMode(false);
                        }
                    }).show();
                }
            }
        };

        pdfLay.findViewById(R.id.optionB).setOnClickListener(onPdfAnnotButtonClick);
        pdfLay.findViewById(R.id.priorB).setOnClickListener(onPdfAnnotButtonClick);
        pdfLay.findViewById(R.id.nextB).setOnClickListener(onPdfAnnotButtonClick);
        antNote.setOnClickListener(onPdfAnnotButtonClick);
        antFreeText.setOnClickListener(onPdfAnnotButtonClick);
        antInk.setOnClickListener(onPdfAnnotButtonClick);
        antLine.setOnClickListener(onPdfAnnotButtonClick);
        antArrow.setOnClickListener(onPdfAnnotButtonClick);
        antRect.setOnClickListener(onPdfAnnotButtonClick);
        antEllipse.setOnClickListener(onPdfAnnotButtonClick);
        antCancel.setOnClickListener(onPdfAnnotButtonClick);
        apCancel.setOnClickListener(onPdfAnnotButtonClick);
        apThickness.setOnClickListener(onPdfAnnotButtonClick);
        apColor.setOnClickListener(onPdfAnnotButtonClick);
        apFillColor.setOnClickListener(onPdfAnnotButtonClick);
        apNote.setOnClickListener(onPdfAnnotButtonClick);
        antSelected = null;
    }

    private void pdfShowAnnotLay() {
//		pdf.annotEnd();
        pdfTopLay.setVisibility(View.VISIBLE);
        pdfSetAnnotButtonBackground();
        pdfBottomLay.setVisibility(antSelected == null || antSelected == antNote ? View.GONE : View.VISIBLE);
        apCancel.setImageResource(R.drawable.pdf_ok);
        apThickness.setVisibility(antSelected == antNote ? View.GONE : View.VISIBLE);
        apColor.setVisibility(antSelected == antNote ? View.GONE : View.VISIBLE);
        apFillColor.setVisibility(antSelected != antRect && antSelected != antEllipse ? View.GONE : View.VISIBLE);
        apNote.setVisibility(antSelected == antFreeText ? View.GONE : View.VISIBLE);
    }

    private void pdfSetAnnotButtonBackground() {
        antNote.setBackgroundResource(R.drawable.my_list_selector);
        antFreeText.setBackgroundResource(R.drawable.my_list_selector);
        antInk.setBackgroundResource(R.drawable.my_list_selector);
        antLine.setBackgroundResource(R.drawable.my_list_selector);
        antArrow.setBackgroundResource(R.drawable.my_list_selector);
        antRect.setBackgroundResource(R.drawable.my_list_selector);
        antEllipse.setBackgroundResource(R.drawable.my_list_selector);
        antNote.setPadding(A.d(6), A.d(6), A.d(6), A.d(6));
        antFreeText.setPadding(A.d(6), A.d(6), A.d(6), A.d(6));
        antInk.setPadding(A.d(6), A.d(6), A.d(6), A.d(6));
        antLine.setPadding(A.d(6), A.d(6), A.d(6), A.d(6));
        antArrow.setPadding(A.d(6), A.d(6), A.d(6), A.d(6));
        antRect.setPadding(A.d(6), A.d(6), A.d(6), A.d(6));
        antEllipse.setPadding(A.d(6), A.d(6), A.d(6), A.d(6));
        if (antSelected == antNote)
            antNote.setBackgroundColor(0xee888888);
        if (antSelected == antFreeText)
            antFreeText.setBackgroundColor(0xee888888);
        if (antSelected == antInk)
            antInk.setBackgroundColor(0xee888888);
        if (antSelected == antLine)
            antLine.setBackgroundColor(0xee888888);
        if (antSelected == antArrow)
            antArrow.setBackgroundColor(0xee888888);
        if (antSelected == antRect)
            antRect.setBackgroundColor(0xee888888);
        if (antSelected == antEllipse)
            antEllipse.setBackgroundColor(0xee888888);
    }

    protected boolean pdfHideAnnotLay(boolean hideTop, boolean hideBottom) {
        if (isPdf()) {
            boolean ok = false;
            if (hideBottom && pdfBottomLay != null && pdfBottomLay.getVisibility() == View.VISIBLE) {
                pdfBottomLay.setVisibility(View.GONE);
                ok = true;
            }
            if (hideTop && pdfTopLay != null && pdfTopLay.getVisibility() == View.VISIBLE) {
                pdfTopLay.setVisibility(View.GONE);
                ok = true;
            }
            return ok;
        }
        return false;
    }

    private void pdfSearchText(boolean forward) {
        A.lastSearchKey = pdfSearchEdit.getText().toString();
        if (pdfSearchString != null && A.lastSearchKey.compareTo(pdfSearchString) == 0) {
            pdf.find(forward ? 1 : -1);
        } else if (A.lastSearchKey.length() > 0) {
            pdfSearchString = A.lastSearchKey;
            pdf.findStart(A.lastSearchKey, A.searchCaseSensitive, A.searchWholeWord);
            pdf.find(forward ? 1 : -1);
        }
    }

    private void pdfHideLay() {
        if (pdfLay != null)
            pdfLay.setVisibility(View.GONE);
    }

    private void pdfShowSearchLay() {
        if (pdfSearchLay != null) {
            inverseLayoutVisible(true);
            statusLay.setVisibility(View.GONE);
            pdfSearchLay.setVisibility(View.VISIBLE);
            pdfSearchEdit.setText(A.lastSearchKey);
        }
    }

    private boolean pdfHideSearchLay() {
        if (pdfSearchLay != null && pdfSearchLay.getVisibility() == View.VISIBLE) {
            pdfSearchLay.setVisibility(View.GONE);
            if (showStatusbar())
                statusLay.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    private boolean pdfInited;

    protected void pdfOpen(String filename) {
        if (Global.isPriorInited(this)) {
            pdfInited = Global.Init(this, A.lastFile);
            pdfOpen2(filename);
        } else {
            createProgressDlg("", getString(R.string.pdf_init));
            new Thread() {
                @Override
                public void run() {
                    pdfInited = Global.Init(ActivityTxt.this, A.lastFile);
                    handler.sendEmptyMessage(SHOW_PDF2);
                }
            }.start();
        }
    }

    private void pdfOpen2(String filename) {
        if (!pdfInited) {
            A.SaveOptions(this);
            new MyDialog.Builder(this).setTitle("PDF").setMessage(getString(R.string.pdf_init_error, Global.PDF_VER))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (!T.openAppInMarket(ActivityTxt.this, "com.flyersoft.plugin_pdf")) {
                                error_and_exit(getString(R.string.market_not_install), true);
                            } else
                                doFinish();
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    doFinish();
                }
            }).setOnCancelListener(new OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    doFinish();
                }
            }).show();
        } else {
            A.SaveOptions(this);
            pdfInitLay();
            txtLay.setVisibility(View.GONE);
            removeWebView();
            pdfOpen3(filename);
        }
    }

    public Document m_doc;
    public String pdf_title;

    private void pdfOpen3(String filename) {
        if (m_doc != null) {
            //m_doc.Close(); // pdfView has inner bug
            restartReaderToTxt();
            return;
        }

        filename = BaseCompressor.getZRCacheFile(filename);
        m_doc = new Document();
        String upassword = "";
        SharedPreferences sp = getSharedPreferences("pdf", 0);
        if (sp.contains(filename + "s")) {
            upassword = A.getDecryptText(sp.getString(filename + "u", ""));
            if (!sp.getBoolean(filename + "s", false))
                sp.edit().remove(filename + "s").commit();
        }

        int ret = m_doc.Open(filename, upassword);
        String errorMsg = null;
        switch (ret) {
            case -1: //errorMsg = "Need input password.";
                pdfCheckPassword(filename);
                return;
            case -2:
                errorMsg = "Unknown encryption.";
                break;
            case -3:
                errorMsg = "Damaged or invalid format.";
                break;
            case -10:
                errorMsg = "Access denied or invalid file path.";
                break;
            case 0:
                break;// succeeded, and continue
            default:
                errorMsg = "Unknown error";
                break;
        }

        A.pdfAnnotUpdated = false;
        if (errorMsg != null) {
            error_and_exit(errorMsg, true);
        } else {
            pdfFontDelegate();
            hideProgressDlg();
            pdfSetProperties(false);
            pdf.open(m_doc, filename);
            pdf.pdfView.viewEnableTextSelection(false);
            pdf.setViewListener(pdf);
            pdf.setAnnotListener(pdfOnAnnoListener());
            Global.selColor = pdf.RGB2PDfColor(A.highlight_color1, 80, -40);
            pdf_title = m_doc.GetMeta("Title");
            A.lastChapter = A.lastSplitIndex = 0;

            boolean clickTipShowed = initClickTip(false);
            if (A.lastPosition == 0) {
                pdfGetToc();
                if (!clickTipShowed && !ignoreChapterListAtBegin && pdfToc.size() > 3)
                    do_show_chapters(0);
            } else
                pdfGotoPage((int) A.lastPosition, false);

            pdfLoadLastPosition(Global.def_view == 0 ? 200 : 0);
            loadNotes();
            checkStatusBar();
        }
    }

    private ArrayList<String> pdfMissedFonts;

    private void pdfFontDelegate() { //note: can test in pro version only
        m_doc.SetFontDel(new Document.PDFFontDelegate() {
            public String GetExtFont(String collection, String fname, int flag, int[] ret_flags) {
                A.log("-------->pdfFontDelegate:" + fname); //v2.5.3
                if (!A.pdf_font_hint) //v2.5.4
                    return null;
                if (pdfMissedFonts == null)
                    pdfMissedFonts = new ArrayList<String>();
                if (pdfMissedFonts.indexOf(fname) != -1)
                    return null;

                SharedPreferences sp = getSharedPreferences(A.lastFile.replace("/", "") + "_font", 0);
                if (sp.contains(fname)) {
                    A.log("*****ERROR: pdf font already added to delete sp:" + fname);
                    return null;
                }

                pdfMissedFonts.add(fname);
                handler.removeMessages(PDF_MISS_FONT);
                handler.sendEmptyMessageDelayed(PDF_MISS_FONT, 1000);
                return null;
            }
        });
    }

    private void pdfShowMissFontTip() {
        if (pdfMissedFonts == null || pdfMissedFonts.size() == 0)
            return;
        SharedPreferences sp = getSharedPreferences(A.lastFile.replace("/", "") + "_font", 0);
        boolean restart = false;
        for (String fname : pdfMissedFonts) {
            String font = A.outerFontsFolder + "/" + fname + ".ttf";
            if (T.isFile(font))
                restart = true;
            sp.edit().putString(fname, "").commit();
        }

        if (restart) {
            saveLastPostion(true);
            restartReaderToTxt();
        } else {
            showCssMissedFontsTip();
        }
    }

    private int pdfCheckWordCountState; //0: uncheck, 1: allow, 2: forbit

    private boolean pdfAllowCheckWordCount() {
        if (pdfCheckWordCountState == 0) {
            pdfCheckWordCountState = getSharedPreferences("pdf_words_failed", 0).contains(A.lastFile) ? 2 : 1;

        }
        return pdfCheckWordCountState == 1;
    }

    private boolean showRemaingTime() {
        if (isWebBook())
            return false;
        if (isPdf() && (!pdfAllowCheckWordCount() || !pdfShowFullStatusBarInfo()))
            return false;
        return A.showRemainingTime;
    }

    private void pdfSetProperties(boolean updateView) {
        if (!isPdf() || pdf == null)
            return;
        int old_def_view = Global.def_view;
        setGlobalValue();
        if (updateView && old_def_view != Global.def_view) {
            pdfUpdateView(false);
        }
    }

    private void pdfCheckPassword(final String filename) {
        View v = LayoutInflater.from(this).inflate(R.layout.pdf_password, null);
        final EditText uEdit = (EditText) v.findViewById(R.id.ueit);
        final CheckBox cb = (CheckBox) v.findViewById(R.id.saveCheck);
        new MyDialog.Builder(this).setTitle("Need input password").setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String up = uEdit.getText().toString();
                        int ret = m_doc.Open(filename, up);
                        if (ret == 0) {
                            getSharedPreferences("pdf", 0).edit().putString(filename + "u", A.getEncryptText(up))
                                    .putBoolean(filename + "s", cb.isChecked()).commit();
                            restartReaderToTxt();
                        } else
                            error_and_exit("Invalidated password.", false);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                doFinish();
            }
        }).setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                doFinish();
            }
        }).show();
    }

    private boolean pdfPageScroll(boolean pageDown) {
        if (!isPdf())
            return false;
        linkBackIv.setVisibility(View.INVISIBLE);

        int off = A.pdf_dual_page && A.isLandscape() ? 2 : 1;
        if (pdf.textReflow) {
            int y = txtScroll.getScrollY();
            int tvH = txtView.getRealHeight();
            int svH = A.getPageHeight();
            if ((!pageDown && y == 0) || (pageDown && (lastLineDisplayed() || y + svH >= tvH - txtView.getLineHeight() * 95 / 100))) {
                A.lastPosition = pdfValidatePageNumber((int) A.lastPosition + (pageDown ? off : -off));
                yBeforePageDown.clear();
                clearTxtView();
                txtView.setText(pdfCreateReflowTextForShow(A.lastPosition));
//				if (pageDown)
                txtScrollByDelay(0);
//				else
//					txtScrollHandler.sendEmptyMessageDelayed(1, 50);
            } else {
                int line = getNextPageLine2(pageDown, false);
                int y2 = txtView.getLineTop(line);
                txtScrollTo(y2);
            }
            if (A.isSpeaking) {
                handler.sendEmptyMessageDelayed(SPEAK_PDF_TEXT_REFLOW, 50);
            }
        } else {
            PDFPosition p = pdf.pdfView.viewGetPos();
            if (Global.def_view == 0) {
                pdfVerticalPageScroll(pageDown, p);
            } else {
                pdfResetPageXY(p);
                p.page = pdfValidatePageNumber(p.page + (pageDown ? off : -off));
                pdf.pdfView.viewGoto(p);
            }
            A.lastPosition = p.page;
        }

        showReadProgress(0);
        if (pageDown)
            pdfSaveReadStatistics();

        return true;
    }

    private void pdfVerticalPageScroll(boolean pageDown, PDFPosition p) {
        float ph = pdfPageParaHeight(p.page);
        float h = pdf.getHeight() - A.d(10);
        if (pageDown) {
            if (A.isSpeaking || p.page_y + h > ph) {
                if (p.page == m_doc.GetPageCount() - 1) {
                    p.page_y = (int) (ph - h);
                } else {
                    p.page = pdfValidatePageNumber(p.page + 1);
                    if (A.isSpeaking)
                        p.page_y = 0;
                    else
                        p.page_y = (int) (h - (ph - p.page_y) - 4);//4 is the gap height
                }
            } else {
                p.page_y += (int) h;
            }
        } else {
            if (p.page_y < h) {
                if (p.page == 0) {
                    p.page_y = 0;
                } else {
                    p.page -= 1;
                    p.page_y = (int) (ph - (h - p.page_y) + 4);//4 is the gap height
                }
            } else {
                p.page_y -= (int) h;
            }
        }
        pdf.pdfView.viewGoto(p);
        pdf.postInvalidate();
    }

    private void pdfResetPageXY(PDFPosition p) {
        if (A.pdf_scoll_lock)
            return;
        if (p.page_y == 0) {
            float ph = pdfPageParaHeight(p.page);
            int h = pdf.getHeight();
            if (h > ph)
                p.page_y = -(int) ((h - ph) / 2) - 1;
        }
        if (p.page_x == 0) {
            float pw = pdfPageParaWidth(p.page);
            int w = pdf.getWidth();
            if (w > pw)
                p.page_x = -(int) ((w - pw) / 2);
        }
    }

    private ArrayList<Integer> pdfStatisticsPages;

    private void pdfSaveReadStatistics() {
        try {
            if (pdfStatisticsPages == null)
                pdfStatisticsPages = new ArrayList<Integer>();
            if (pdf.turnedPages.indexOf((int) A.lastPosition) == -1)
                pdf.turnedPages.add((int) A.lastPosition);
            for (int i : pdf.turnedPages)
                if (pdfStatisticsPages.indexOf(i) == -1) {
                    pdfStatisticsPages.add(i);
                    A.statistics_pages++;
                    String text = pdfGetPageText(i);
                    if (text.length() > 0)
                        pdfSaveReadStatistics2(text);
                }
            pdf.turnedPages.clear();
        } catch (Exception e) {
            A.error(e);
        }
    }

    public String pdfGetPageText(int i) {
        try {
            Page page = m_doc.GetPage(i);
            page.ObjsStart();
            String text = page.ObjsGetString(0, page.ObjsGetCharCount());
            if (text == null)
                return "";
            return text;
        } catch (Exception e) {
            A.error(e);
            return "";
        }
    }

    private void pdfSaveReadStatistics2(String text) {
        int wordsCount = T.getWordsCount(text, A.isAsiaLanguage);
        A.statistics_words += wordsCount;
        read_words += wordsCount;
    }

//	class PdfOutline {
//		int pageNumber;
//		String title;
//
//		public PdfOutline(int outline, String title) {
//			this.pageNumber = outline;
//			this.title = title.trim();
//		}
//	}

    public boolean pdfTreeTOC;
    public ArrayList<TocChapter> pdfToc;

    private void pdfGetToc() {
        if (pdfToc == null) {
            pdfToc = new ArrayList<TocChapter>();
            SharedPreferences sp = getSharedPreferences("pdf_toc_failed", 0);
            if (sp.contains(A.lastFile))
                return;

            sp.edit().putBoolean(A.lastFile, true).commit();
            pdfAddTocTree(m_doc.GetOutlines(), 0); //app may crash here
            sp.edit().remove(A.lastFile).commit();

            if (pdfTreeTOC)
                for (int j = 0; j < pdfToc.size(); j++)
                    pdfToc.get(j).indent++;
        }

        A.lastPosition = pdfGetCurrPageNo();
        for (int i = 0; i < pdfToc.size(); i++)
            if (A.lastPosition >= pdfToc.get(i).pageNumber) {
                A.lastChapter = i;
                if (A.lastPosition == pdfToc.get(i).pageNumber)
                    break;
            } else
                break;

    }

    private void pdfAddTocTree(Outline o, int indent) {
        while (o != null) {
            Outline child = o.GetChild();
            pdfToc.add(new TocChapter(o.GetTitle(), pdfToc.size(), indent, child != null, true, o.GetDest(), null));
            if (child != null) {
                pdfTreeTOC = true;
                pdfAddTocTree(child, indent + 1);
            }
            o = o.GetNext();
        }
    }

    void pdfZoomRatio(float add) {
        if (pdf == null || pdf.pdfView == null)
            return;
        float ratio;
        ratio = pdf.pdfView.viewGetRatio() + add;
        PDFPosition p = pdf.pdfView.viewGetPos();
        pdf.pdfView.viewSetRatio(ratio, p.page_x, p.page_y, false);
    }

    private void pdfSelectText(boolean showSelector) {
        if (hMotionEvent == null)
            return;
        int x = (int) hMotionEvent.getX();
        int y = (int) hMotionEvent.getY();
        int dw = dot1.getWidth();
        int dh = dot1.getHeight();
        dot1.layout(x - dw / 2, y, x - dw / 2 + dw, y + dh);
        dot2.layout(x - dw / 2, y, x - dw / 2 + dw, y + dh);
        //		dot2.layout(x+dw/2, y, x+dw/2+dw, y+dh);
        hHighlight.setTag(0);
        if (showSelector) {
            showDotViews();
            pdf.recordDotsAct(this);
        }
        pdfHighlightText(true, showSelector);
    }

    private void pdfHighlightText(boolean showHBar, boolean showHighlight) {
        int off = A.d(5);
        int dw = dot1.getWidth();
        int x1 = dot1.getLeft() + dw / 2;
        int y1 = dot1.getTop() - off;
        int x2 = dot2.getLeft() + dw / 2;
        int y2 = dot2.getTop() - off;

        if (showHighlight) {
            if (x1 != x2)
                A.pdfHighlightFromDot = true;
            pdfHighlight(x1, y1, x2, y2);
        }
        if (showHBar)
            layoutHBar();
        else
            setHBarVisible(false);
    }

    private void pdfHighlight(int x1, int y1, int x2, int y2) {
//		Global.selColor = pdf.RGB2PDfColor(A.pdf_highlight_color, 80, -40);
        try {
            if (Global.def_view == 3) {
                PDFPosition p = pdf.pdfView.viewGetPos();
                x1 += p.page_x;
                x2 += p.page_x;
            }
            pdf.pdfView.viewSetSel(x1, y1, x2, y2);
        } catch (Exception e) {
            A.error(e);
        }
    }

    public void pdfAfterOnTouch(MotionEvent event) {
    }

    Boolean pdfFullStatusBar;

    public void pdfShowFullStatusBarInfoHandler() {
        if (showStatusbar()) {
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    pdfShowFullStatusBarInfo();
                }
            }.sendEmptyMessage(0);
        }
    }

    private void pdfUpdateStatusBarColor(boolean full) {
        if (pdfFullStatusBar == null || pdfFullStatusBar != full) {
            A.log("*pdfCheckStatusBar--------");
            pdfFullStatusBar = full;
            if (full) {
                statusRight.setBackgroundDrawable(null);
                statusRight.setPadding(0, 0, A.d(4), 0);
                setStatusBarProperties(true);
                updateBarTime();
            } else {
                statusRight.setBackgroundColor(0xaa000000);
                statusRight.setTextColor(0xffffffff);
                statusRight.setPadding(A.d(4), 0, A.d(4), 0);
            }
            int visible = full ? View.VISIBLE : View.INVISIBLE;
            remainingRight.setVisibility(visible);
            statusLeft.setVisibility(visible);
            statusLeft2.setVisibility(visible);
            statusMiddle.setVisibility(visible);
        }
    }

    public boolean pdfShowFullStatusBarInfo() {
        boolean full = pdf.textReflow ? true : Global.def_view != 0 &&
                (A.getScreenHeight2() - pdfPageParaHeight(-1)) > statusLay.getHeight() * 2;
        if (showStatusbar())
            pdfUpdateStatusBarColor(full);
        return full;
    }

//	private float pdfPageWidth(int pageno) {
//		if (pageno == -1)
//			pageno = pdfGetCurrPageNo();
//		return m_doc.GetPageWidth(pageno) * pdf.pdfView.viewGetRatio();
//	}
//
//	private float pdfPageHeight(int pageno) {//content height in a pdf page
//		if (pageno == -1)
//			pageno = pdfGetCurrPageNo();
//		return m_doc.GetPageHeight(pageno) * pdf.pdfView.viewGetRatio();
//	}

    private float pdfPageParaWidth(int pageno) {
        if (pageno == -1)
            pageno = pdfGetCurrPageNo();
        return m_doc.GetPageWidth(pageno) * pdf.getParaRatio();
    }

    private float pdfPageParaHeight(int pageno) {//real height of a pdf page
        if (pageno == -1)
            pageno = pdfGetCurrPageNo();
        return m_doc.GetPageHeight(pageno) * pdf.getParaRatio();
    }

    private int pdfVerticalParaMargin() {
        int h = baseFrame.getHeight();
        float ph = pdfPageParaHeight(-1);
        return ph >= h ? 0 : (int) (h - ph) / 2;
    }

    public void pdfUpdateView(boolean updateRatio) {
        setGlobalValue();
        //		float ratio = Global.def_view==0? pdf.pdfView.viewGetRatio() : pdf.getParaRatio();
        float ratio = pdf.pdfView.viewGetRatio();
        PDFPosition p = pdf.pdfView.viewGetPos();
        pdf.set_viewer(Global.def_view);
        if (updateRatio)
            pdfResetRatio(p, ratio);
    }

    private void setGlobalValue() {
        Global.render_mode = A.pdf_render_quality == 0 ? 2 : (A.pdf_render_quality == 1 ? 1 : 0);
        if (A.pdf_dual_page && A.isLandscape())
            Global.def_view = 4;
        else
            Global.def_view = A.pdf_view_mode == 0 ? (A.isLandscape() ? 0 : 3) : (A.pdf_view_mode == 1 ? 3 : 0);
    }

    private void pdfSaveLastPosition() {
        if (!dntSavePositionAgain && !isOnExiting)
            try {
                float ratio = pdf.pdfView.viewGetRatio();
                PDFPosition p = pdf.pdfView.viewGetPos();
                if (!pdf.textReflow)
                    A.lastPosition = p.page;
                String value = "" + ratio + "#" + p.page_x + ":" + p.page_y + "@" + A.pdf_scoll_lock;
                getSharedPreferences(A.POSITION_FILE, 0).edit()
                        .putString(A.lastFile.toLowerCase(), "" + A.lastPosition + ":" + getPercentStr2())
                        .putString(A.lastFile.toLowerCase() + "pdf", value).commit();
            } catch (Exception e) {
                A.error(e);
            }
    }

    private void pdfLoadLastPosition(int delay) {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String s = getSharedPreferences(A.POSITION_FILE, 0).getString(A.lastFile.toLowerCase() + "pdf", "");
                if (s.length() > 0)
                    try {
                        ignoreChapterListAtBegin = true;
                        PDFPosition p = pdf.pdfView.viewGetPos();
                        if (p.page != A.lastPosition) {//v2.3.3
                            A.log("*********incorrect pdf load page, why:" + A.lastPosition + " - " + p.page);
                            p.page = (int) A.lastPosition;
                        }
                        float ratio = Float.valueOf(s.substring(0, s.indexOf("#")));
                        p.page_x = Integer.valueOf(s.substring(s.indexOf("#") + 1, s.indexOf(":")));
                        p.page_y = Integer.valueOf(s.substring(s.indexOf(":") + 1, s.indexOf("@")));
                        pdfResetRatio(p, ratio);
                        if (A.pdf_scoll_lock)
                            A.pdf_scoll_lock = s.indexOf("true") > 0;
                        if (A.pdf_scoll_lock)
                            pdf.pdfView.viewLockSide(A.pdf_scoll_lock, pdf.getContext());
                    } catch (Exception e) {
                        A.error(e);
                    }
                else
                    A.pdf_scoll_lock = false;
            }
        }.sendEmptyMessageDelayed(0, delay);
    }

    private void pdfResetRatio(PDFPosition p, float ratio) {
        pdfResetPageXY(p);
        pdf.pdfView.viewSetRatio(ratio, p.page_x, p.page_y, false);
        pdf.pdfView.viewGoto(p);
    }

    private void pdfTextReflowSwitch(boolean reverse) {
        if (reverse)
            pdf.textReflow = !pdf.textReflow;
        pdfLockIv.setVisibility(pdf.textReflow ? View.GONE : View.VISIBLE);
        chromeIv.setImageResource(pdf.textReflow ? R.drawable.mrbookview2 : R.drawable.text_reflow);
        inverseLayoutVisible(true);

        if (pdf.textReflow) {
            pdf.setVisibility(View.GONE);
            A.loadVisualOptions(false);
            txtLay.setVisibility(View.VISIBLE);
            txtView.setText(pdfCreateReflowTextForShow(A.lastPosition));
            txtScrollByDelay(0);
        } else {
            pdf.setVisibility(View.VISIBLE);
            txtLay.setVisibility(View.GONE);
            pdf.pdfView.viewGotoPage((int) A.lastPosition);
        }
        checkStatusBar();
    }

    private String pdfCreateReflowTextForShow(long i) {
        String text = pdfGetPageText((int) i);
        text = A.formatPdfReflowText(text, m_doc.GetPage((int) i));
        if (!A.isSpeaking)
            text = text + " <" + (i + 1) + "/" + m_doc.GetPageCount() + ">";
        return text;
    }

    protected int pdfValidatePageNumber(int progress) {
        if (progress >= m_doc.GetPageCount())
            progress = m_doc.GetPageCount() - 1;
        if (progress < 0)
            progress = 0;
        return progress;
    }

    protected void pdfGotoPage(int i, boolean sameRatio) {
        try {
            i = pdfValidatePageNumber(i);
            if (pdf.textReflow)
                txtView.setText(pdfCreateReflowTextForShow(i));
            else {
                if (sameRatio) {
                    PDFPosition p = pdf.pdfView.viewGetPos();
                    pdfResetPageXY(p);
                    p.page = i;
                    pdf.pdfView.viewGoto(p);
                } else
                    pdf.pdfView.viewGotoPage(i);
            }
            A.lastPosition = i;
        } catch (Exception e) {
            A.error(e);
        }
    }

    public int pdfCurPageNo;

    public int pdfGetCurrPageNo() {
        if (pdf == null || pdf.textReflow || pdf.pdfView == null)
            pdfCurPageNo = (int) A.lastPosition;
        else
            pdfCurPageNo = pdf.pdfView.viewGetCurPageNo();

        if (pdfCurPageNo == -1)
            return (int) A.lastPosition;
        return pdfCurPageNo;
    }

    private void pdfCheckBookcover() {
        String destFile = BaseCompressor.getZRCacheFile(A.lastFile);
//		String coverFile = A.download_cache_path + "/" + T.getFilename(destFile) + A.COVER_TAG;
        String thumbFile = A.download_cache_path + "/" + T.getFilename(destFile) + A.THUMB_TAG;
        if (!T.isFile(thumbFile)) {
            long t = System.currentTimeMillis();
            Bitmap bm = Global.createBitmapOfPage(m_doc, 0, null);
            A.generateBookCovers(bm, destFile, false);
            A.log("extract cover time:" + (System.currentTimeMillis() - t));
        }
    }

    private void pdfDoAnnotation() {
        if (!pdfAnnotStart && pdf.selectedText == null)
            return;
        if (!pdfCanSave())
            return;

        pdfSetImmersiveMode(true);
        pdf.lockResize(true);
        new PrefEditNote(ActivityTxt.this, null, pdfAnnotStart && !T.isNull(pdf.annotGetText()), new PrefEditNote.OnAfterEditNote() {
            public void AfterEditNote(int result, final String text) {
                if (text.length() == 0 || !m_doc.CanSave())
                    result = PrefEditNote.CANCEL;
                try {
                    switch (result) {
                        case PrefEditNote.SAVE:
                            if (!pdfAnnotStart) {
                                if (!pdf.pdfView.annotSetMarkup(0)) {
                                    pdfShowFailedAnnotInfo("Failed to add annotation!");
                                    return;
                                }
                                Page page = m_doc.GetPage(pdfGetCurrPageNo());
                                page.ObjsStart();
                                A.log("*page:" + page + " annotCount:" + page.GetAnnotCount());
                                if (page.GetAnnotCount() > 0) {
                                    Annotation a = page.GetAnnot(page.GetAnnotCount() - 1);
                                    a.SetPopupText(text);
                                    pdfSaveHighlightNote((int) A.lastPosition, pdf.selectedText, text, 0);
                                } else
                                    A.log("#Error pdf note# GetAnnotCount()==0");
                            } else {
                                if (!pdf.annotGetText().equals(text))
                                    pdf.annotSetText(text);
                            }
                            pdf.annotEnd();
                            A.pdfAnnotUpdated = true;
                            A.lastFileAnnotUpdated = true;
                            break;
                        case PrefEditNote.DELETE:
                            pdf.annotRemove();
                            A.pdfAnnotUpdated = true;
                            hideDotViews();
                            break;
                        case PrefEditNote.CANCEL:
                            pdf.annotEnd();
                            break;
                    }
                } catch (Exception e) {
                    A.error(e);
                }
                pdf.lockResize(false);
                hideDotViews();
                pdfSaveAnnotsForTime();
                pdfSetImmersiveMode(false);
            }
        }).show();
    }

    protected void pdfShowFailedAnnotInfo(String text) {
        if (A.isKitkatExtSdcardFile(A.lastFile)) {
            CharSequence s = Html.fromHtml("<b>" + text + "</b><br><br>" +
                    getString(R.string.copy_pdf_file_to_edit, "<b><font color=\"#FF0000\">/sdcard/Books/Pdf</font></b>"));
            new MyDialog.Builder(this)
                    .setMessage(s)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String dest = "/sdcard/Books/Pdf/" + T.getFilename(A.lastFile);
                            File destFile = new File(dest);
                            if (!destFile.isFile() || destFile.length() != T.getFileSize(A.lastFile)) {
                                T.copyFile(A.lastFile, dest, true);
                            }
                            if (T.isFile(dest)) {
                                saveLastPostion(true);
                                A.deleteHistory(A.lastFile);
                                A.lastFile = dest;
                                A.lastPath = T.getFilePath(dest);
                                saveLastPostion(true);
                                restartReaderToTxt();
                            }
                        }
                    }).setNegativeButton(android.R.string.no, null)
                    .show();
        } else
            T.showToastText(this, text);
    }

    private boolean pdfCanSave() {
        if (!m_doc.CanSave()) {
            pdfShowFailedAnnotInfo(getString(R.string.pdf_cannot_modify));
            return false;
        }
        return true;
    }

    private void pdfDoHighlight(boolean fromLongTap) {
        if (!isClickOnAnnot() && pdf.selectedText == null)
            return;
        if (!pdfCanSave())
            return;

        pdfSetImmersiveMode(true);
        pdf.lockResize(true);
        if (!isClickOnAnnot() && fromLongTap) {
            pdfDoHighlight2(-1);
            pdf.annotEnd();
            pdf.lockResize(false);
            A.lastFileAnnotUpdated = true;
        } else {
            int mode = -1;
            if (isClickOnAnnot()) {
                Annotation annot = pdf.pdfView.getSelectedAnnot();
                if (annot != null) {
                    if (annot.GetType() == 9)
                        mode = 0;
                    else if (annot.GetType() == 10)
                        mode = 1;
                    else if (annot.GetType() == 12)
                        mode = 2;
                    else if (annot.GetType() == 11)
                        mode = 3;
                }
            }

            PrefSelectHighlight h = new PrefSelectHighlight(this, new PrefSelectHighlight.OnSelectColor() {
                public void selectColor(int mode, boolean delete) {
                    if (delete) {
                        pdf.annotRemove();
                        A.pdfAnnotUpdated = true;
                        hideDotViews();
                        pdfSaveAnnotsForTime();
                    } else
                        pdfDoHighlight2(-1);
                    A.lastFileAnnotUpdated = true;
                }
            }, true, mode, null);
            h.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    pdf.annotEnd();
                    pdf.lockResize(false);
                    pdfSetImmersiveMode(false);
                }
            });
            h.show();
        }
    }

    private boolean pdfDoHighlight2(int forceColor) {
        boolean result = false;
        try {
            if (isClickOnAnnot()) {
                Annotation annot = pdf.pdfView.getSelectedAnnot();
                if (annot != null) {
                    if (annot.GetType() == 9) //highlight
                        annot.SetFillColor(Page.fixHighlightColor(forceColor != -1 ? forceColor : A.pdf_highlight_color));
                    else if (annot.GetType() == 10)
                        annot.SetStrokeColor(forceColor != -1 ? forceColor : A.underline_color);
                    else if (annot.GetType() == 12)
                        annot.SetStrokeColor(forceColor != -1 ? forceColor : A.strikethrough_color);
                    else if (annot.GetType() == 11)
                        annot.SetStrokeColor(forceColor != -1 ? forceColor : A.squiggly_color);
                    pdf.pdfView.refreshCurPage();
                    result = true;
                }
            } else {
                boolean ret = false;
                int savedColor = -1;
                if (forceColor != -1) {
                    if (A.highlightMode == 0) {
                        savedColor = A.pdf_highlight_color;
                        A.pdf_highlight_color = forceColor;
                    } else if (A.highlightMode == 1) {
                        savedColor = A.underline_color;
                        A.underline_color = forceColor;
                    } else if (A.highlightMode == 2) {
                        savedColor = A.strikethrough_color;
                        A.strikethrough_color = forceColor;
                    } else if (A.highlightMode == 3) {
                        savedColor = A.squiggly_color;
                        A.squiggly_color = forceColor;
                    }
                }

                if (A.highlightMode == 0)
                    ret = pdf.pdfView.annotSetMarkup(0);
                else if (A.highlightMode == 1)
                    ret = pdf.pdfView.annotSetMarkup(1);
                else if (A.highlightMode == 2)
                    ret = pdf.pdfView.annotSetMarkup(2);
                else if (A.highlightMode == 3)
                    ret = pdf.pdfView.annotSetMarkup(4);

                if (!ret) {
                    pdfShowFailedAnnotInfo("Failed to add highlight!");
                } else {
                    if (pdfCanSave()) {
                        pdfSaveHighlightNote((int) A.lastPosition, pdf.selectedText, "", A.highlightMode);
                        A.pdfAnnotUpdated = true;
                        result = true;
                    }
                }

                if (forceColor != -1) {
                    if (A.highlightMode == 0) {
                        A.pdf_highlight_color = savedColor;
                    } else if (A.highlightMode == 1) {
                        A.underline_color = savedColor;
                    } else if (A.highlightMode == 2) {
                        A.strikethrough_color = savedColor;
                    } else if (A.highlightMode == 3) {
                        A.squiggly_color = savedColor;
                    }
                }
            }

            pdfSaveAnnotsForTime();
        } catch (Exception e) {
            A.error(e);
        }
        hideDotViews();
        return result;
    }

    private void pdfSetImmersiveMode(boolean immersive) {
        if (A.immersive_fullscreen && pdf != null)
            pdf.forbid_immersive_mode = immersive;
    }

    protected void pdfSaveHighlightNote(int pageno, String original, String noteText, int type) {
        NoteInfo note = new NoteInfo(0, A.getBookName(), A.lastFile, Page.note_start,
                Page.note_end, //v1.9.5 lastChapter=start, lastSplitIndex=end
                pageno, Page.note_end - Page.note_start,
                type == 0 ? A.pdf_highlight_color : type == 1 ? A.underline_color : type == 2 ? A.strikethrough_color : A.squiggly_color,
                System.currentTimeMillis(), "", noteText, original, type == 1, type == 2, type == 3 ? "1" : "");
        A.addNote(note);
    }

    public boolean pdfAnnotStart;
    private int pdf_sel_index;
    long annotEndTime;

    private PDFAnnotListener pdfOnAnnoListener() {
        return new PDFAnnotListener() {
            public void onAnnotUpdate() {
                Log.i("MR2", "--annot update");
//				pdf.annotUpdated = true;
                hideDotViews();
            }

            public void onAnnotEnd() {
                Log.i("MR2", "--annot end");
                annotEndTime = SystemClock.elapsedRealtime();
                pdfAnnotStart = false;
                antSelected = null;
                A.pdfStatus = STATUS.sta_none;
                if (pdfTopLay.getVisibility() == View.VISIBLE)
                    pdfShowAnnotLay();
                pdfHideAnnotLay(false, true);
                hideDotViews();
            }

            public void onAnnotDragStart(boolean has_goto, boolean has_popup) {
                pdfAnnotStart = true;
                inverseLayoutVisible(true);
                Annotation annot = pdf.pdfView.getSelectedAnnot();
                int type = annot.GetType();
                boolean ok = type == 1 || type == 3 || type == 4 || type == 5 || type == 6 || type == 15;
                if (ok) {
                    apNote.setVisibility(View.VISIBLE);
                    apThickness.setVisibility(type != 1 && type != 3 ? View.VISIBLE : View.GONE);
                    apColor.setVisibility(type != 1 && type != 3 ? View.VISIBLE : View.GONE);
                    apFillColor.setVisibility(type == 5 || type == 6 ? View.VISIBLE : View.GONE);
                    apCancel.setImageResource(R.drawable.trash);
                    pdfBottomLay.setVisibility(View.VISIBLE);
                    if (pdfTopLay.getVisibility() == View.VISIBLE) {
                        antSelected = null;
//						A.pdfStatus = STATUS.sta_annot;
                        pdfSetAnnotButtonBackground();
                    }
                    if (!T.isNull(annot.GetPopupText()))
                        onPdfAnnotButtonClick.onClick(apNote);
                    return;
                }

                Log.i("MR2", "--annot drag start");
                if (!T.isNull(pdf.annotGetText())) {
                    pdfDoAnnotation();
                } else {
                    pdfSelectText(false);
//					hHighlight.setTag(1);
//					hHighlight.setText(getString(R.string.delete));
                    hPen.setTag(1);
                    setHPenImage(true);

                    int color = 0x88aaaaaa;
                    hCopy.setTextColor(color);
                    hDict.setTextColor(color);
                    hMore.setTextColor(color);
//					hHighlight.setTextColor(color);
                }
            }

            public void onAnnotEditBox(int type, String val, float text_size, float left, float top, float right, float bottom) {
                pdfSaveAnnotsForSecurity();
                if (PDFReader.isFreeTextAnnot(pdf.pdfView.getSelectedAnnot())) {
                    onAnnotDragStart(false, false);
                    return;
                }

                pdfAnnotStart = true;
                pdf.lockResize(true);
                final EditText et = new EditText(ActivityTxt.this);
                et.setText(val);
                new MyDialog.Builder(ActivityTxt.this).setView(et).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pdf.annotSetEditText(et.getText().toString());
                        A.pdfAnnotUpdated = true;
                        pdf.lockResize(false);
                        pdfAnnotStart = false;
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pdf.lockResize(false);
                        pdfAnnotStart = false;
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        pdf.lockResize(false);
                        pdfAnnotStart = false;
                    }
                }).show();
            }

            public void onAnnotComboBox(int sel, String[] opts, float arg2, float arg3, float arg4, float arg5) {
                pdfAnnotStart = true;
                pdf.lockResize(true);
                pdf_sel_index = sel;
                new MyDialog.Builder(ActivityTxt.this).setSingleChoiceItems(opts, sel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pdf_sel_index = which;
                    }
                }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pdf.annotSetChoice(pdf_sel_index);
                        A.pdfAnnotUpdated = true;
                        pdf.lockResize(false);
                        pdfAnnotStart = false;
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pdf.lockResize(false);
                        pdfAnnotStart = false;
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        pdf.lockResize(false);
                        pdfAnnotStart = false;
                    }
                }).show();
            }
        };
    }

    private long pdfLastSaveTime;

    private void pdfSaveAnnotsForTime() {
        if (!A.pdfAnnotUpdated || pdf == null || m_doc == null)
            return;
        if (pdfLastSaveTime == 0)
            pdfLastSaveTime = SystemClock.elapsedRealtime();
        if (SystemClock.elapsedRealtime() - pdfLastSaveTime < 5 * 60000)
            return;
        A.log("****SAVE PDF DOCUMENT****2");
        A.pdfAnnotUpdated = false;
        m_doc.Save();
        pdfLastSaveTime = SystemClock.elapsedRealtime();
    }

    private void pdfSaveAnnotsForSecurity() { //freeText annot may crash in other CPUs
        if (!A.pdfAnnotUpdated || pdf == null || m_doc == null)
            return;
        if (Build.CPU_ABI.equals("armeabi-v7a"))
            return;
        A.log("****SAVE PDF DOCUMENT****3");
        A.pdfAnnotUpdated = false;
        m_doc.Save();
        pdfLastSaveTime = SystemClock.elapsedRealtime();
    }

    private ArrayList<TtsLine> pdfGetTtsLines() {
        if (pdf.textReflow) {
            pdfTextReflowSwitch(true);
//			String text = pdfCreateReflowTextForShow(pdfGetCurrPageNo());
//			ArrayList<TtsLine> ttsLines = A.getTtsLines(text, false);
//			if (ttsLines.size() == 0)
//				return null;
//			return ttsLines;
        }

        String text = pdf.pdfView.viewGetCurPageText();
        if (T.isNull(text))
            return null;

        try {
            Page page = m_doc.GetPage(pdfGetCurrPageNo());
            page.ObjsStart();
            int index = 0;
            while (true) {
                int r = text.indexOf("\r", index);
                if (r == -1)
                    break;

                float[] v1 = new float[4];
                page.ObjsGetCharRect(r - 1, v1);
                if (text.length() <= r + 2)
                    break;

                float[] v2 = new float[4];
                page.ObjsGetCharRect(r + 2, v2);
                float h1 = v1[3] - v1[1];
                float h2 = v2[3] - v2[1];

                if (Math.abs(h1 - h2) > 1) {
                    //A.log("*"+"("+page.ObjsGetString(r-1, r)+":"+h1+" "+page.ObjsGetString(r+2,r+3)+":"+h2+")"+line);
                    text = text.substring(0, r) + "✔" + text.substring(r + 1);
                }

                index = r + 2;
            }
        } catch (Exception e) {
            A.error(e);
        }

        ArrayList<TtsLine> ttsLines = A.getTtsLines(text, true, false, 0);
        if (ttsLines.size() == 0)
            return null;
        TtsLine sl = ttsLines.get(0);
        pdf.selectedPara = null;
        PDFPage.setForceSel(sl.start, sl.end);
        pdf.postInvalidate();
        getSpeakHandler().sendEmptyMessageDelayed(CHECK_PDF_SPEAKING_POS, 100);
        return ttsLines;
    }

    ////----------------PDF END----------------

    private int preNextChapterId = -1;
    private int preNextChapterSplit;
    private boolean preNextInProgress, prePriorInProgress;
    private CharSequence preNextChapterText;
    private ArrayList<String> preNextSplitHtmls;
    private String preNextHtmlSrc;
//	private int preNextFixedLineCount;

    private void preNextChapter(boolean checkY) { //thread
        if (preNextInProgress)
            return;
        if (checkY && txtScroll.getScrollY() < txtView.getRealHeight() / 3)
            return;

        int chapterId = A.lastChapter;
        int splitIndex = A.lastSplitIndex;
        if (!A.noSplitHtmls() && A.lastSplitIndex < A.splitHtmls.size() - 1)
            splitIndex++;
        else {
            splitIndex = 0;
            chapterId++;
            if (getBookType() == A.FILE_HTML || isPdf())
                return;
        }

        if (!A.ebook.isInited())
            return;
        if (A.ebook != null && chapterId > A.ebook.getChapters().size() - 1)
            return;
        if (preNextChapterText != null && preNextChapterId == chapterId && preNextChapterSplit == splitIndex)
            return;

        preNextChapterId = chapterId;
        preNextChapterSplit = splitIndex;
        preNextChapterText = null;
        Thread thread = new Thread() {
            @Override
            public void run() {
                preNextInProgress = true;
                try {
                    if (isFinishing())
                        return;
                    if (preNextChapterSplit > 0) {
                        imageGetter = A.ebook == null ? createHtmlBookImageGetter() : A.ebook.getMyImageGetter();
                        preNextHtmlSrc = A.chineseJianFanConvert(A.splitHtmls.get(preNextChapterSplit));
                        savePreNextChapterText(preNextChapterId, MyHtml.fromHtml(preNextHtmlSrc, imageGetter, preNextChapterId), true);
                        preNextSplitHtmls = A.splitHtmls;
                    } else {
                        generateChapterText(1);
                        if (A.ebook.online_site != null && A.ebook.online_site.ok())
                            updateOnlineChapter(preNextChapterId, preNextHtmlSrc, true);
                    }
                } catch (Exception e) {
                    A.error(e);
                } finally {
                    preNextInProgress = false;
                }
            }
        };
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private void savePreNextChapterText(int chapterId, CharSequence text, boolean createCurlCache) {
        preNextChapterText = text;
        if (createCurlCache && A.isFlipCurl() && !dualPageEnabled() && A.ebook != null) {
            if (text.length() > 100 || !isOnlineDownloadTag(chapterId, text.toString(), true)) {
                handler.removeMessages(CACHE_NEXT_CHAPTER_CURL);
                handler.removeMessages(CACHE_NEXT_CHAPTER_CURL2);
                handler.sendEmptyMessageDelayed(CACHE_NEXT_CHAPTER_CURL, 100);
            }
        }
    }

    public MRTextView txtCache;
    public ScrollView2 scrollCache;
    Bitmap txtCacheBm;
    int txtCacheChapter, txtCacheSplit;
    int txtCacheChapter2, txtCacheSplit2;

    private void createNextChapterCurlCache() {
        if (delayHideCurl) {
            handler.sendEmptyMessageDelayed(CACHE_NEXT_CHAPTER_CURL, 100);
            return;
        }
        if (preNextHtmlSrc == null)
            return;
        if (txtCacheChapter == preNextChapterId && txtCacheSplit == preNextChapterSplit && !T.isRecycled(txtCacheBm)) {
            A.log("=======already has txtCacheBm: " + preNextChapterId);
            return;
        }
        try {
            if (chapterHasChangedStyle(preNextChapterId))
                return;

            txtCacheChapter2 = preNextChapterId;
            txtCacheSplit2 = preNextChapterSplit;

            scrollCache.setPadding(txtScroll.getPaddingLeft(), txtScroll.getPaddingTop(), txtScroll.getPaddingRight(), txtScroll.getPaddingBottom());
            txtCache.alone = true;
            txtCache.maxLines = getPageLineCount() * 2;
            A.setTextFont(txtCache, false);
            A.setTxtViewTypeface(txtCache);
            A.setLineSpace(txtCache);
            A.setFontSpace(txtCache);
            //		txtCache.setTextSize(A.vdf(txtView.getTextSize()));
            txtCache.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtView.getTextSize());

            txtCache.setText(MyHtml.fromHtml(preNextHtmlSrc, imageGetter, preNextChapterId));
            txtCache.lastChapter = preNextChapterId;
            txtCache.lastSplitIndex = preNextChapterSplit;
            T.recycle(txtCacheBm);
            handler.sendEmptyMessageDelayed(CACHE_NEXT_CHAPTER_CURL2, 10);
        } catch (Exception e) {
            A.error(e);
        }
    }

    private boolean chapterHasChangedStyle(int id) {
        if (A.currentChapterWithImageOnly(id))
            return true;

        Chapter c = A.ebook.getChapters().get(id);
        CSS.Style bodyStyle = MyHtml.getClassStyle("body", c.css_body_id, c.css_body_class, c, null, null);
        if (c.css_body_style != null) {
            bodyStyle = new CSS.Style(bodyStyle);
            bodyStyle.scanPropertyForStyle(c.css_body_style);
        }
        if (bodyStyle == null)
            return false;

        if (bodyStyle.align > 1 && bodyStyle.align != MRTextView.global_alignment)
            return true;
        if (bodyStyle.fontFace != null)
            if (A.forceCssFontName == null || !A.forceCssFontName.equals(bodyStyle.fontFace))
                return true;
        if (bodyStyle.fontSize > 0 && bodyStyle.fontSize != 1f && bodyStyle.fontSize != cssFontSize)
            return true;

        String s = bodyStyle.backgroundImage;
        if (T.isNull(s) || s == cssBodyBackgrondImage)
            return false;

        int i1 = s.indexOf("("), i2 = s.indexOf(")");
        if (i1 != -1 && i2 > i1) {
            String img = s.substring(i1 + 1, i2);
            Drawable d = A.ebook.getDrawableFromSource(img, 0);
            if (d != null)
                return true;
        }
        return false;
    }

    private void createNextChapterCurlCache2() {
        if (txtCacheChapter2 == preNextChapterId && txtCacheSplit2 == preNextChapterSplit)
            try {
                forceNoCurlCache = false;
                if (tmpFlipShot3 != txtCacheBm)
                    T.recycle(txtCacheBm);
                txtCacheBm = Bitmap.createBitmap(baseFrame.getWidth(), baseFrame.getHeight(), HighQuality3D() ? Config.ARGB_8888 : Config.RGB_565);
                Canvas c = new Canvas(txtCacheBm);
                A.setBackgroundImage(c);
                scrollCache.draw(c);
//			if (showStatusbar()) {
//				c.save();
//				c.translate(0, statusLay.getTop());
//				statusLay.draw(c);
//				c.restore();
//			}
//			A.log("#"+preNextChapterId + " : " + txtCacheBm.toString());
                txtCacheChapter = preNextChapterId;
                txtCacheSplit = preNextChapterSplit;
                txtCache.setText("");
            } catch (Throwable e) {
                A.error(e);
                if (tmpFlipShot3 != txtCacheBm)
                    T.recycle(txtCacheBm);
            }
        else if (tmpFlipShot3 != txtCacheBm)
            T.recycle(txtCacheBm);
    }

/*	private boolean createTxtCacheView() {
		if (scrollCache == null) {
			scrollCache = new ScrollView(this);
			scrollCache.setVerticalScrollBarEnabled(false);
			scrollCache.setHorizontalScrollBarEnabled(false);
			scrollCache.setFadingEdgeLength(0);;
			baseFrame.addView(scrollCache, txtScroll.getWidth(), txtScroll.getHeight());
			txtCache = new MRTextView(this);
			txtCache.alone = true;
			txtCache.maxLines = getPageLineCount() * 2;
			scrollCache.addView(txtCache, -1, -2);
			scrollCache.setVisibility(View.INVISIBLE);
			return true;
		}
		return false;
	}*/

    public boolean forceNoCurlCache;

    boolean curlCacheAvailable() {
        return !forceNoCurlCache && !T.isRecycled(txtCacheBm);
    }

    //-----------------------------

    private boolean hasNextChapterText(boolean toChapterBegin, String progressTitle) {
        if (!preNextInProgress && preNextChapterText != null && preNextChapterId == A.lastChapter && preNextChapterSplit == A.lastSplitIndex) {
            A.lastChapter = preNextChapterId;
            A.lastSplitIndex = preNextChapterSplit;
            A.splitHtmls = preNextSplitHtmls;
            htmlSrc = preNextHtmlSrc;

            if (A.isFlipCurl() && !A.isInAutoScroll && toChapterBegin && curlBackTime == 0
                    && A.lastChapter == txtCacheChapter && A.lastSplitIndex == txtCacheSplit && curlCacheAvailable()) {
                tmpFlipShot3 = txtCacheBm;
                delayHideCurl = true;
//				A.log("delayHideCurl start");
                new Handler() {
                    public void handleMessage(Message msg) {
                        goNextCacheChapter(true);
                    }
                }.sendEmptyMessageDelayed(0, 500);
            } else
                goNextCacheChapter(toChapterBegin);
            return true;
        }
        return false;
    }

    boolean delayHideCurl, delayHideCurl2;

    private void goNextCacheChapter(boolean toChapterBegin) {
        if (SystemClock.elapsedRealtime() - curlBackTime > 2000) {
            disableTxtViewDraw();
            clearTxtView();
        }
        txtViewSetText(preNextChapterText);
        delayHideCurl = false;
//		A.log("delayHideCurl end");
        if (toChapterBegin)
            A.lastPosition = 0;

        pausePixelAutoScroll();
        ebook_inLoading = true;
        txtScrollToLastPostion();
        forceDelayFlipAnimation = true;

        new Handler() {
            public void handleMessage(Message msg) {
                txtScrollToLastPostion();
                if (SystemClock.elapsedRealtime() - curlBackTime > 2000 || txtView.disableDraw)
                    enableTxtViewDraw();

//				A.log("goNextCacheChapter end");
                if (delayHideCurl2) {
                    delayHideCurl2 = false;
                    handleCurlMessage(createCurlMsg(1, null));
                }
                if (curlBackTime > 0) {
                    T.recycle(tmpFlipShot1);
                    T.recycle(tmpFlipShot2);
                    T.recycle(tmpFlipShot3);
                }
                curlBackTime = 0;
                ebook_inLoading = false;
                forceDelayFlipAnimation = false;

                statusHandlerSendMessage(100, 0);
                statusHandlerSendMessage(100, 100); //confirm it
                continueSpeakOrAutoScroll();
                handler.sendEmptyMessageDelayed(PRE_LOAD_NEXT_CHAPTER, 100);
            }
        }.sendEmptyMessageDelayed(0, 0);
    }

    private void txtScrollToLastPostion() {
        if (txtView.getLayout() != null)
            txtScrollTo(txtView.getLineTop2(txtView.getLayout().getLineForOffset((int) A.lastPosition)));
    }

    private void prePriorChapterForPageUp() {
        if ((A.ebook != null || !A.noSplitHtmls()) && !isBeginOfBook() && web == null && txtScroll.getScrollY() == 0) //v3.5.1
            prePriorChapter();
    }

    private void prePriorChapter() { //no thread
        int chapterId = A.lastChapter;
        int splitIndex = A.lastSplitIndex;
        if (!A.noSplitHtmls() && A.lastSplitIndex > 0)
            splitIndex--;
        else {
            splitIndex = -1;
            chapterId--;
            if (getBookType() == A.FILE_HTML || isPdf())
                return;
        }
        if (A.ebook != null && chapterId < 0)
            return;
        if (oldPriorChapterText != null && oldPriorChapterId == chapterId &&
                (oldPriorChapterSplit == splitIndex || splitIndex == -1))
            return;

        oldPriorChapterId = chapterId;
        oldPriorChapterSplit = splitIndex;
        oldPriorChapterText = null;

        if (oldPriorChapterSplit != -1) {
            imageGetter = A.ebook == null ? createHtmlBookImageGetter() : A.ebook.getMyImageGetter();
            oldPriorHtmlSrc = A.chineseJianFanConvert(A.splitHtmls.get(oldPriorChapterSplit));
            oldPriorChapterText = MyHtml.fromHtml(oldPriorHtmlSrc, imageGetter, oldPriorChapterId);
            oldPriorSplitHtmls = A.splitHtmls;
        } else {
            generateChapterText(-1);
        }
        oldPriorChapterY = -1;
//		oldPriorChapterP = -1;
    }

    private boolean hasPriorChapterText(String progressTitle) {
        if (oldPriorChapterText != null && oldPriorChapterId == A.lastChapter &&
                (oldPriorChapterSplit == A.lastSplitIndex || A.lastSplitIndex == -1)) {
            if (A.lastSplitIndex == -1)
                A.lastSplitIndex = oldPriorChapterSplit;
            if (A.lastSplitIndex == -1)
                A.lastSplitIndex = 0;
//			if (!isPaused && A.slowRomText(preNextChapterText)) {
//				if (progressTitle == null)
//					progressTitle = "...";
//				createProgressDlg(progressTitle);
//				new Handler() {
//					public void handleMessage(Message msg) {
//						goPriorCacheChapter();
//						hideProgressDlg();
//					}
//				}.sendEmptyMessage(0);
//			} else
            goPriorCacheChapter();
            return true;
        }
        return false;
    }

    private void goPriorCacheChapter() {
        disableTxtViewDraw();
        clearTxtView();
        A.splitHtmls = oldPriorSplitHtmls;
        htmlSrc = oldPriorHtmlSrc;
        txtViewSetText(oldPriorChapterText);
        forceDelayFlipAnimation = true;
//		if (oldPriorChapterY != -1)
//			txtScrollTo(oldPriorChapterY); // if cur chapter large then prior, this will extend appended bottom

        new Handler() {
            public void handleMessage(Message msg) {
                if (oldPriorChapterY != -1) {
                    enableTxtViewDraw();
                    txtScrollTo(oldPriorChapterY);
                    forceDelayFlipAnimation = false;
                } else
                    txtScrollHandler.sendEmptyMessageDelayed(1, 50);

                if (A.isFlipCurl()) {
                    resetFlipCache(false, 0, false);
                    if (curlBackTime > 0) {
                        setCurl3dVisibility(false);
                        setFlipViewINVISIBLE(true);
                    }
                } else
                    forceUpdateForFitHardwareAccelerate(50);
                updateProgressStatus();
                curlBackTime = 0;
            }
        }.sendEmptyMessage(0);
    }

    private void disableTxtViewDraw() {
        txtView.disableDraw = txtView2.disableDraw = true;
    }

    private boolean enableTxtViewDraw() {
        boolean currentDisabled = txtView.disableDraw;
        txtView.disableDraw = txtView2.disableDraw = false;
//		if (valueCacheOk && contentLay.disableDraw)
//			return false;

        if (currentDisabled) {
//			contentLay.postInvalidate();
            txtView.postInvalidate();
            txtView2.postInvalidate();
            return true;
        }
        if (txtScroll.getScrollY() == 0)
            txtView.postInvalidate();
        return false;
    }

    private int oldPriorChapterId = -1;
    private int oldPriorChapterSplit;
    private int oldPriorChapterY;
    //	private int oldPriorChapterP;
    private CharSequence oldPriorChapterText;
    private ArrayList<String> oldPriorSplitHtmls;
    private String oldPriorHtmlSrc;

    private void saveChapterTextToPriorCache(int lastChapter, int lastSplitIndex, String tmpHtmlSrc) {
        if (htmlSrc == null)
            return;
        oldPriorChapterId = lastChapter;
        oldPriorChapterSplit = lastSplitIndex;
        oldPriorChapterText = txtView.getText();
        oldPriorSplitHtmls = (ArrayList<String>) A.splitHtmls.clone();
        oldPriorHtmlSrc = tmpHtmlSrc;
        oldPriorChapterY = txtScroll.getScrollY();
    }

    private void saveChapterTextToNextCache(int lastChapter, int lastSplitIndex) {
        if (htmlSrc == null)
            return;
        savePreNextChapterText(lastChapter, txtView.getText(), false);
        preNextChapterId = lastChapter;
        preNextChapterSplit = lastSplitIndex;
        preNextSplitHtmls = (ArrayList<String>) A.splitHtmls.clone();
        preNextHtmlSrc = htmlSrc;

        if (A.isFlipCurl() && A.ebook != null) {
            txtCacheChapter = preNextChapterId;
            txtCacheSplit = preNextChapterSplit;
            txtCacheBm = getPageShot(false, HighQuality3D());
            A.log("*" + preNextChapterId + " : " + txtCacheBm.toString());
        }
    }

    private void clearpreNextCache() {
        oldPriorChapterText = null;
        preNextChapterText = null;
    }

    private void playPageSound() {
        if (A.isSpeaking)
            return;
        if (A.isInAutoScroll)
            return;
        if (!A.pageSound)
            return;
        if (mp3PlayState == 1)
            return;
        A.playPageSound(this);
    }

    String battery;
    BroadcastReceiver batteryReceiver;

    public void registerBattery() {
        if (isFinishing())
            return;
        battery = "";
        batteryReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                battery = "" + level;
                updateBarTime();
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }


    ////----------------------------

    private void doShareReadState(boolean readState) {
        A.BookState book = A.getBookState(A.lastFile);
        if (readState) {
            saveStatisticsInfo();
            book.progress = getPercentStr2();
        }
        shareBook(this, book, A.ebook, readState);
        A.trackEvent("doShare", "share_book", "", 1);
    }

    public static void shareBook(Activity act, A.BookState book) {
        shareBook(act, book, null, false);
    }

    public static void shareBook(Activity act, A.BookState book, BaseEBook ebook, boolean readState) {
        if (readState) {
            BookDb.ReadStatistics r = BookDb.getSavedStatistics(book.filename);
            if (r.usedTime > 0 && r.readWords > 0)
                try {
//					book.time = "" + new java.text.DecimalFormat("0.0").format((float) r.usedTime / 60 / 60 / 1000);
                    book.time = T.formatInterval(r.usedTime);
                    float minutes = (float) r.usedTime / 60 / 1000;
                    if (minutes > 0)
                        book.speed = "" + new java.text.DecimalFormat("0").format((r.readWords / minutes));
                } catch (Exception e) {
                    A.error(e);
                }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("我正在阅读");
        String author = "";
        if (!T.isNull(book.author))
            book.author = "(作者: " + book.author + ")";
        sb.append("《" + book.name + "》" + book.author);
        boolean isWebBook = A.isWebBook(book.filename);
        if (!isWebBook && readState && !T.isNull(book.progress))
            sb.append(", 已看完" + book.progress);
        sb.append("\n");
        if (readState) {
            sb.append(act.getString(R.string.read_hour) + ": " + book.time + "\n");
            sb.append(act.getString(R.string.read_speed) + ": " + book.speed + act.getString(R.string.read_speed2) + "\n");
        }
        if (isWebBook)
            try {
                if (ebook != null) {
                    sb.append(ebook.getChapters().get(A.lastChapter).name);
                    sb.append("\n书源\"" + ebook.online_site.name() + "\", 地址:\n");
                    sb.append(ebook.getChapters().get(A.lastChapter).url + "\n");
                } else {
                    String siteTag = WBpub.getFileText(book.filename);
                    String path = T.getFilePath(book.filename);
                    ArrayList<String> sources = T.text2StringList(WBpub.getFileText(path + "/.sources"));
                    String siteName = siteTag;
                    String url = null;
                    for (int i = 0; i < sources.size(); i++) {
                        String s = sources.get(i);
                        if (s.contains("*" + siteTag + "#")) {
                            siteName = s.substring(0, s.indexOf("*"));
                            url = s.substring(s.indexOf("#") + 1);
                            break;
                        }
                    }
                    sb.append("书源\"" + siteName + "\", 地址:\n");
                    sb.append(url + "\n");
                }
            } catch (Exception e) {
                A.error(e);
            }

        sb.append("\n(复制这条信息, 打开搜书大师可直接阅读: http://soushu.site)");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享我的阅读" + ": " + A.getBookName());
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        A.log(sb.toString());
        act.startActivity(Intent.createChooser(intent, ""));
    }

    private void hideSystemUi() {
        if (Build.VERSION.SDK_INT >= 15 && !A.immersive_fullscreen)
            try {
//				Method m = View.class.getMethod("setSystemUiVisibility", new Class[] { int.class });
//				m.invoke(baseFrame, new Object[] { 1 }); //View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                baseFrame.setSystemUiVisibility(1);
            } catch (Throwable e) {
                A.error(e);
            }
    }

    public boolean showStatusbar() {
        return A.showStatusbar || showRemaingTime();
    }

    private boolean askedForTilt;

    private void checkTiltEvent() {
    }

    //-----------------video player--------------
    public void playVideo(String file) {
        isPaused = true;
        contentLay.invalidate();
        Intent intent = new Intent(this, MediaAct.class);
        intent.putExtra("video", file);
        startActivityForResult(intent, 124);
    }

    //-----------------mp3 player--------------
    View mp3Lay;
    ImageView mp3Play, mp3Stop;
    TextView mp3Eclapsed, mp3Total;
    SeekBar mp3Seek;
    MediaPlayer mp3;
    public int mp3PlayState; //0:not ready 1: play 2: paused
    public String mp3_file;

    public void playMp3(String file) {
        A.log("play mp3:" + file);
        mp3_file = file;
        initMp3Lay();
        mp3Lay.setVisibility(View.VISIBLE);
        mp3StartButton();
        contentLay.postInvalidate();
    }

    void initMp3Lay() {
        mp3PlayState = 0;
        if (mp3Lay != null) {
            mp3Eclapsed.setText("00:00");
            mp3Total.setText("00:00");
            mp3Play.setImageResource(R.drawable.tts_play);
            mp3.stop();
            return;
        }
        mp3Lay = ((ViewStub) findViewById(R.id.mp3Stub)).inflate();
        mp3Play = (ImageView) mp3Lay.findViewById(R.id.imageView1);
        mp3Stop = (ImageView) mp3Lay.findViewById(R.id.imageView2);
        mp3Eclapsed = (TextView) mp3Lay.findViewById(R.id.time1);
        mp3Total = (TextView) mp3Lay.findViewById(R.id.time2);
        mp3Seek = (SeekBar) mp3Lay.findViewById(R.id.seekBar1);
//		mp3Seek.setThumb(getResources().getDrawable(R.drawable.empty));
        mp3Seek.setThumb(null);
        mp3 = new MediaPlayer();
        mp3Play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mp3PlayState == 0) {
                    mp3StartButton();
                } else if (mp3PlayState == 1) {
                    mp3.pause();
                    mp3PlayState = 2;
                } else {
                    mp3.start();
                    mp3PlayState = 1;
                }
                mp3Play.setImageResource(mp3PlayState != 2 ? R.drawable.tts_pause : R.drawable.tts_play);
            }
        });
        mp3Stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mp3StopButton();
            }
        });
        mp3Seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp3.seekTo(seekBar.getProgress());
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            long lastSetTime;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Time time = new Time();
                    time.set(seekBar.getProgress());
                    mp3Eclapsed.setText(time.format("%M:%S"));
                    if (SystemClock.elapsedRealtime() - lastSetTime > 800) {
                        lastSetTime = SystemClock.elapsedRealtime();
                        mp3.seekTo(seekBar.getProgress());
                    }
                    getMp3Handler().sendEmptyMessageDelayed(0, 1000);
                }
            }
        });
        mp3.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp3StopButton();
            }
        });
    }

    private void mp3StartButton() {
        try {
            mp3.reset();
            mp3.setDataSource(mp3_file);
            mp3.prepare();
            mp3.start();
            mp3Seek.setMax(mp3.getDuration());
            mp3Seek.setProgress(0);
            mp3PlayState = 1;
            Time time = new Time();
            time.set(mp3.getDuration());
            mp3Total.setText(time.format("%M:%S"));
            mp3Play.setImageResource(R.drawable.tts_pause);
            getMp3Handler().sendEmptyMessageDelayed(0, 1000);

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mp3Lay.getLayoutParams();
            lp.leftMargin = lp.rightMargin = A.getScreenWidth2() / (A.isLandscape() ? 4 : 6);
        } catch (Exception e) {
            A.error(e);
        }
    }

    private void mp3StopButton() {
        mp3.stop();
        mp3PlayState = 0;
        mp3Seek.setProgress(0);
        mp3Lay.setVisibility(View.GONE);
        contentLay.postInvalidate();
    }

    Handler mp3Handler;

    Handler getMp3Handler() {
        if (mp3Handler == null)
            mp3Handler = new Handler() {
                public void handleMessage(Message msg) {
                    if (mp3 == null || mp3PlayState != 1)
                        return;
                    mp3Seek.setMax(mp3.getDuration());
                    mp3Seek.setProgress(mp3.getCurrentPosition());
                    Time time = new Time();
                    time.set(mp3.getCurrentPosition());
                    mp3Eclapsed.setText(time.format("%M:%S"));
                    time.set(mp3.getDuration());
                    mp3Total.setText(time.format("%M:%S"));
                    if (mp3PlayState == 1)
                        mp3Handler.sendEmptyMessageDelayed(0, 1000);
                }
            };
        mp3Handler.removeMessages(0);
        return mp3Handler;
    }

    public boolean isClickOnAnnot() {
        return hPen.getTag() != null && (Integer) hPen.getTag() == 1;
    }

    //------------------------------------------
    public static void sync_finish_message() {
        if (!T.isNull(selfPref)) {
            selfPref.handler.sendEmptyMessage(ActivityTxt.SYNC_FINISHED);
        }
    }

    public static void show_received_message(String s) {
        if (!T.isNull(selfPref)) {
            Handler h = ActivityTxt.selfPref.handler;
            h.sendMessage(h.obtainMessage(ActivityTxt.RESTART_CONFIRM_MESSAGE, s));
        }
    }

    ////--------------mp3 player------------------

    private void init3dHandler() {
        A.log("*init3dHandler");
        if (curl3d != null) {
            handler.sendEmptyMessage(INIT_CURL3D_RENDER);
        } else {
            handler.sendEmptyMessageDelayed(INIT_CURL3D, 50);
        }
    }

    private void setAutoTheme() {
        if (adjustedThemeAtStartup) {
            adjustedThemeAtStartup = false;
            return;
        }
        if (!A.autoTheme)
            return;
        if (!A.autoThemeDay && !A.autoThemeNight)
            return;
//		if (A.lastReadTime == 0 || System.currentTimeMillis() - A.lastReadTime < 10*60*1000)
//			return;
//		handler.sendEmptyMessageDelayed(SET_AUTO_THEME, 0);
        setAutoThemeHandler();
    }

    private void setAutoThemeHandler() {
        long nowTime = System.currentTimeMillis();
        int dayHour = A.autoThemeDayTime / 100;
        int dayEndHour = 17;
        int nightHour = A.autoThemeNightTime / 100;
        int nightEndHour = 5;

        Calendar calendar = Calendar.getInstance();
        int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                dayHour, A.autoThemeDayTime % 100, 00);
        long dayTime = calendar.getTimeInMillis();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                dayEndHour, 0, 00);
        long dayEnd = calendar.getTimeInMillis();

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                nightHour, A.autoThemeNightTime % 100, 00);
        long nightTime = calendar.getTimeInMillis();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                nightEndHour, 0, 00);
        long nightEnd = calendar.getTimeInMillis();

        calendar.setTime(new Date(A.lastReadTime));
        int lastHour = calendar.get(Calendar.HOUR_OF_DAY);
        int type = 0; //1: day; -1: night

        if (A.autoThemeDay)
            if (nowTime > dayTime && nowTime < dayEnd && (lastHour < nightEndHour || lastHour > dayEndHour))
                type = 1;

        if (type == 0 && A.autoThemeNight) {
            if (nightHour > dayEndHour && nightHour < 24) {
                if (nowTime > nightTime || (nowTime < nightEnd))
                    type = -1;
            } else if (nightHour < nightEndHour) {
                if (nowHour >= nightHour && nowTime < nightEnd) {
                    calendar = Calendar.getInstance();
                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                            A.autoThemeNightTime / 100, A.autoThemeNightTime % 100, 00);
                    if (nowTime > calendar.getTimeInMillis())
                        type = -1;
                }
            }
        }

        if (type != 0) {
            boolean load = !isPdf() &&
                    ((type == 1 && A.lastTheme.equals(A.NIGHT_THEME)) || (type == -1 && !A.lastTheme.equals(A.NIGHT_THEME)));

            if (load) {
                inverseLayoutVisible(true);
//				cycleThemes(true);
                loadTheme(type == 1 ? A.TEMP_THEME : A.NIGHT_THEME, true); //todo: 这个跟moonreader不同
                A.setMainNightTheme(type == -1);
                A.txtThemeChanged = true;
                z.notifyThemeChanged();
            }

            if (A.brightnessValue != -100) {
                if (type == 1 && (A.autoThemeDayBrightness == -100 || A.brightnessValue < A.autoThemeDayBrightness))
                    setScreenBrightness(A.autoThemeDayBrightness, false);
                if (type == -1 && (A.autoThemeNightBrightness == -100 || A.brightnessValue > A.autoThemeNightBrightness))
                    setScreenBrightness(A.autoThemeNightBrightness, false);
            }

            if (load) {
                checkStatusBar();
                setLeds();
            }
            if (web != null)
                reloadWebView();
        }
    }


    public void eraseGPUShadow(int delay) {
        A.baseFrame.setBackgroundDrawable(null);
        if (delay == 0) {
            A.setBackgroundImage(A.baseFrame);
        } else {
            new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == 50)
                        A.baseFrame.setBackgroundDrawable(null);
                    A.setBackgroundImage(A.baseFrame);
                }
            }.sendEmptyMessageDelayed(delay, delay);
        }
    }

    private boolean isAutoState() {
        return A.isSpeaking || A.isInAutoScroll;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {

    }


    private long activeTime;

    private void recordUserActiveTime() {
        if (A.isSpeaking || A.isInAutoScroll) {
            activeTime = 0;
            return;
        }
        if (activeTime == 0) {
            activeTime = System.currentTimeMillis();
            return;
        }
        long passed = System.currentTimeMillis() - activeTime;
        activeTime = System.currentTimeMillis();
        if (passed > T.minute(10)) { //idle time, shouldn't be included to statistics
            statisticsTime = System.currentTimeMillis() - T.minute(1); //take all idle time as 1 minute only
        }
    }

    private Handler restartHandler;

    public void refreshTxtRender() {
        if (restartHandler == null) {
            restartHandler = new Handler() {
                public void handleMessage(Message msg) {
                    try {
                        saveLastPostion(true);
                        A.clearTxts();
                        clearTxtView();
                        A.checkTextViewProperties();
                        txtViewSetTextSize(A.fontSize);
                        htmlText = ""; //v1.2.23 fix html encoding error
                        reloadBook();
                    } catch (Exception e) {
                        A.error(e);
                    }
                }
            };
        }

        restartHandler.removeMessages(0);
        restartHandler.sendEmptyMessageDelayed(0, 250);
    }

    //*//----------------------------

    private boolean updateOnlineChapter(int id, String text, boolean checkEmpty) {
        if (!isOnlineDownloadTag(id, text, checkEmpty))
            return false;

        if (!A.ebook.online_site.ok()) {
            String path = T.getFilePath(A.lastFile);
            final ArrayList<String> sources = T.text2StringList(WBpub.getFileText(path + "/.sources"));
            if (sources.size() < 2) {
                new MyDialog.Builder(this)
                        .setMessage(A.fanti("本书所用书源\"" + A.ebook.online_site.name() + "\"已失效, 请重新搜索."))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                WB.doWebSearchWithSelect(ActivityTxt.this, T.getOnlyFilename(A.lastFile));
                            }
                        }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
            } else {
                T.showToastText(this, A.fanti("本书所用书源\"" + A.ebook.online_site.name() + "\"已失效, 请切换书源或重新搜索."), 1);
                do_switch_source();
            }
            return false;
        }

        String url = A.ebook.getChapters().get(id).url;
        if (T.isNull(url))
            return false;

        if (A.ebook.online_site.webReadLevel > 1) {
            txtViewSetText("");
            if (id == A.lastChapter)
                new MyDialog.Builder(this).setTitle(R.string.tip).setMessage("\n该书源不提供优化阅读服务, 是否打开网页版?")
                        .setPositiveButton("打开网页版", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                T.openUrl(ActivityTxt.this, A.ebook.getChapters().get(A.lastChapter).url);
                            }
                        }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
            return false;
        }

        if (lastCacheItem != null && lastCacheItem.chapterId == id) {
            if (id == A.lastChapter && isJavascriptContent() && cacheWB != null)
                cacheWB.reload();
            return true;
        }

        int cacheId = cachingUrlsId(url);
        if (id == A.lastChapter) {
            if (cacheId != -1)
                cacheUrls.remove(cacheId);
            cacheUrls.add(0, new WBpub.WebCacheItem(url, A.ebook.online_site.userAgent, A.ebook.getChapters().get(id).name, id, false));
            if (isJavascriptContent() && lastCacheItem != null) {
                cacheUrls.add(1, lastCacheItem);
                lastCacheItem = null;
            }
            cachingChapter();
        } else if (cacheId == -1) {
            cacheUrls.add(0, new WBpub.WebCacheItem(url, A.ebook.online_site.userAgent, A.ebook.getChapters().get(id).name, id, false));
            int delayed = 200 + T.myRandom(200);
            if (isJavascriptContent() && A.ebook.online_site.contentDelay > 200)
                delayed = A.ebook.online_site.contentDelay + T.myRandom(A.ebook.online_site.contentDelay);
            handler.sendEmptyMessageDelayed(CACHING_TASK, delayed);
        } else if (cacheId > 0)
            Collections.swap(cacheUrls, 0, cacheId);
        return true;
    }

    private boolean isJavascriptContent() {
        return A.ebook.online_site.javascriptContent ||
                (A.ebook.online_site.primary() && S.getForceWebSites().contains(A.ebook.online_site.siteTag));
    }

    public boolean isWebBook() {
        return A.ebook != null && A.ebook.online_site != null;
    }

    private boolean isOnlineDownloadTag(int chapterId, String text, boolean checkEmpty) {
        if (isWebBook()) {
            if (text.equals(getString(R.string.downloading_content)))
                return true;
            if (A.textJian2Fan && text.equals(A.chineseJianFanConvert(getString(R.string.downloading_content))))
                return true;
            if (checkEmpty && text.length() == 0 && A.ebook.getChapters().get(chapterId).name.length() > 0)
                return true;
        }
        return false;
    }

    ArrayList<WBpub.WebCacheItem> cacheUrls = new ArrayList<>();
    WBpub.WebCacheItem lastCacheItem;
    boolean online_caching;

    private int cachingUrlsId(String url) {
        if (cacheUrls == null)
            cacheUrls = new ArrayList<>();
        for (int i = 0; i < cacheUrls.size(); i++)
            if (cacheUrls.get(i) != null && url.equals(cacheUrls.get(i).url))
                return i;
        return -1;
    }

    DownloadTaskBrowser.CacheWebView cacheWB;

    private void cachingChapter() {
        if (cacheUrls.size() == 0 || lastCacheItem != null) {  //only download one at same time
//			if (isJavascriptContent() && cacheUrls.size() == 0)
//				stopCacheWebView(); //todo: 不能在这里清空, 因为cacheWeb.delay可能还没执行完毕, 会导致无法获取章节
            return;
        }
        lastCacheItem = cacheUrls.get(0);
        cacheUrls.remove(0);
        showCachingProgress(lastCacheItem);

        DownloadTask.Result result = new DownloadTask.Result();
        result.callback = new DownloadTask.Callback() {
            @Override
            public void onFinish(DownloadTask.Result result) {
                handler.sendMessage(handler.obtainMessage(CACHE_FINISHED, result));
            }

            @Override
            public void onProgress(DownloadTask.Result result, int progress, int total) {

            }

            @Override
            public void onCancel() {
                hideProgressDlg();
                cacheUrls.clear();
                if (inDownloading || (lastCacheItem != null && lastCacheItem.chapterId == A.lastChapter))
                    showChapterDownloadError(null);
                lastCacheItem = null;
                online_caching = false;
            }

            @Override
            public void onError(DownloadTask.Result result, String err) {
                handler.sendMessage(handler.obtainMessage(CACHE_ERROR, result));
            }
        };

        DownloadTaskBrowser.CacheWebView wb = null;
        if (isJavascriptContent()) {
            if (cacheWB == null || cacheWB.destroyed) {
                cacheWB = DownloadTaskBrowser.createDownloadWebView();
                cacheWB.delayed = A.ebook.online_site.contentDelay == 0 ? 200 : A.ebook.online_site.contentDelay;
            }
            wb = cacheWB;
            wb.stop = false;
            wb.timeout = A.ebook.online_site.contentTimeout;
            reload_urls.remove(lastCacheItem.url);
        }

        DownloadTask.download(wb, result, lastCacheItem.url, null, A.ebook.online_site.userAgent);
    }

    private void stopCacheWebView() {
        if (cacheWB != null) {
            cacheWB.stop = true;
            S.clearWebView(cacheWB);
        }
    }

    private void doCacheFinished(DownloadTask.Result result) {
        online_caching = false;
        if (isFinishing() || !isWebBook()) {
            hideProgressDlg();
            return;
        }

        if (!result.noJavascript && cacheWB != null && cacheWB.stop)
            return;

        A.ebook.online_site.last_url = result.final_url;
        String content = S.getChapterContent(result.html, A.ebook.online_site);
        boolean ok = true;

        if (lastCacheItem != null) {
            ok = false;
            int id = lastCacheItem.chapterId;
            Chapter c = A.ebook.getChapters().get(id);

            if (isJavascriptContent() && badContent(lastCacheItem.name, content)) {
                increaseCacheWBdelayed(true);
                if (DownloadTaskBrowser.reloadCacheWB(cacheWB, result.url, reload_urls, 250)) {
                    if (A.lastChapter == id)
                        createProgressDlg("", c.name + "\n" + getString(R.string.downloading_content));
                    return;
                }
            }

            if (S.isVipChapter(c.name) || !T.isNull(content)) {
                c.filename = WBpub.saveChapterContent(A.ebook.online_id, A.ebook.getAuthor(), A.ebook.online_site, id, c.name, content, c.url, lastCacheItem.appendOnly);

                if (!lastCacheItem.loadedUrls.contains(result.url))
                    lastCacheItem.loadedUrls.add(result.url);
                String stopUrl = id < A.ebook.getChapters().size() - 1 ? A.ebook.getChapters().get(id + 1).url : null;
                String nextContentUrl = S.getNextContentUrl(result.html, A.ebook.online_site, lastCacheItem.loadedUrls, stopUrl);
                if (!T.isNull(nextContentUrl)) { //v13.6 章节分页, 读取下一页
                    WBpub.WebCacheItem next = new WBpub.WebCacheItem(nextContentUrl, A.ebook.online_site.userAgent, A.ebook.getChapters().get(id).name, id, true);
                    next.loadedUrls = lastCacheItem.loadedUrls;
                    cacheUrls.add(0, next);
                    lastCacheItem = null;
                    handler.sendEmptyMessageDelayed(CACHING_TASK, T.myRandom(200));
                    return;
                }

                c.text = BaseEBook.UN_LOAD_TAG;
                c.text = A.ebook.getChapterText(id);
                c.size = c.text.length();

                if (A.lastChapter == id) {
                    if (preNextChapterId == A.lastChapter)
                        preNextChapterText = null;
                    reloadBook();
                } else if (preNextChapterId == id)
                    new Thread() {
                        @Override
                        public void run() {
                            generateChapterText(1);
                        }
                    }.start();

                if (cacheUrls.size() > 0) {
                    int delayed = 100 + T.myRandom(200);
                    if (isJavascriptContent() && A.ebook.online_site.contentDelay > 200)
                        delayed = A.ebook.online_site.contentDelay + T.myRandom(A.ebook.online_site.contentDelay);
                    handler.sendEmptyMessageDelayed(CACHING_TASK, delayed);
                } else if (isJavascriptContent())
                    handler.sendEmptyMessageDelayed(CACHING_TASK_END, 0);

                if (S.isVipChapter(c.name) && T.isNull(content) && A.lastChapter == id)
                    showTipForFailedVipChapter();

                lastCacheItem = null;
                ok = true;
            }

            if (!ok && A.lastChapter == id) {
                hideProgressDlg();
                showChapterDownloadError(null);
            }
        }

        checkDownloadFinished(!ok);
//		WB.saveCache(A.ebook.online_site, result, content, lastCacheItem==null? null : lastCacheItem.name);
        if (!inDownloading)
            hideProgressDlg();

    }

    private void increaseCacheWBdelayed(boolean step) {
        if (cacheWB != null && cacheWB.delayed < 5000) {
            cacheWB.delayed = (step ? cacheWB.delayed + 1000 : cacheWB.delayed * 2);
            if (cacheWB.delayed > 5000)
                cacheWB.delayed = 5000;
        }
    }

    private boolean badContent(String cName, String content) {
        if (S.isVipChapter(cName))
            return false;
        if (content.length() < 80)
            return true;
        if (content.length() < 800) {
            int min = WB.goodChapterTitle(cName) ? 100 : 50;
            if (Html.fromHtml(content).toString().length() < min)
                return true;
        }
        return false;
    }

    private void doCacheError(DownloadTask.Result result) {
        hideProgressDlg();
        cacheUrls.clear();
        if (inDownloading || (lastCacheItem != null && lastCacheItem.chapterId == A.lastChapter))
            showChapterDownloadError(result.err);
        lastCacheItem = null;
        online_caching = false;
    }

    private void showChapterDownloadError(String err) {
        inDownloading = false;
        if (!A.isNetworkConnecting()) {
            T.showAlertText(this, getString(R.string.huoquzhangjieshibai));
            return;
        }
        CharSequence info = Html.fromHtml(A.fanti("<br>请五秒后重试, 或使用原版浏览, 或切换到其它书源<br>") +
                (!T.isNull(err) ? "<br><small><font color=\"#888888\">" + err + "</font></small><br>" : ""));
        new MyDialog.Builder(this).setTitle(getString(R.string.weinenghuoquzhangjie)).setMessage(info)
                .setPositiveButton(getString(R.string.chongshi), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        increaseCacheWBdelayed(true);
                        reloadCurWebChapter();
                    }
                }).setNegativeButton(A.fanti("原版浏览"), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//						T.openUrl(ActivityTxt.this, A.ebook.getChapters().get(A.lastChapter).url);
                showWebReading();
                T.showToastText(ActivityTxt.this, Html.fromHtml(A.fanti(
                        "<small><b>提示:</b> 1.点工具栏右上的\"原版/优化\"可切换阅读模式; 2.点击工具栏网址可用外部浏览器打开</small>")), 1);
            }
        }).setNeutralButton(getString(R.string.qiehuanshuyuan), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                do_switch_source();
            }
        }).show();
    }

    private void reloadCurWebChapter() {
        lastCacheItem = null;
        txtView.setText(getString(R.string.downloading_content));
        if (updateOnlineChapter(A.lastChapter, getString(R.string.downloading_content), false)) {
            online_caching = true;
            createProgressDlg("", A.ebook.getChapters().get(A.lastChapter).name + "\n" + getString(R.string.downloading_content));
            clearTxtView();
        }
    }

    private void do_switch_source() {
        inverseLayoutVisible(true);
        ActivityMain.doSelectSource(this, handler, A.lastFile, new ActivityMain.OnSelectSource() {
            @Override
            public void afterSelect(String siteTag, String url) {
                switchToSource(siteTag, url, false);
            }
        });
    }

    public void switchToSource(String siteTag, String url, boolean forUpdateCur) {
        if (T.isNull(siteTag))
            return;
        if (!forUpdateCur && siteTag.equals(A.ebook.online_site.siteTag))
            return;
        if (!A.isNetworkConnecting()) {
            T.showAlertText(this, getString(R.string.qinglianjiewangluo));
            return;
        }
        getSourceTask(siteTag, url, true, forUpdateCur, true, null);
    }

    S.WebBook switchTaskBook;
    String switchTaskSiteTag;

    private void getSourceTask(final String siteTag, String url, boolean first, final boolean forUpdateCur,
                               final boolean showTip, S.WebBook savedBook) {
        switchTaskSiteTag = siteTag;
        S.BookSite site = S.getSite(siteTag, true);
        if (site == null || !site.ok()) {
            T.showToastText(this, getString(R.string.wuxiaoshuyuan) + siteTag);
            return;
        }

        site.working = true;
        site.firstExecute = first;
        switchTaskBook = savedBook != null ? savedBook : new S.WebBook();
        switchTaskBook.site = site;
        switchTaskBook.name = A.ebook.getBookName();
        switchTaskBook.author = A.ebook.getAuthor();
        switchTaskBook.description = A.ebook.description;
        switchTaskBook.url = url;
        if (showTip)
            createProgressDlg(forUpdateCur ? site.name() + "·" + A.getBookName() : getString(R.string.qiehuanshuyuan), getString(R.string.gengxinzhangjieliebiaozhong), false);

        DownloadTask.Result result = new DownloadTask.Result();
        result.callback = new DownloadTask.Callback() {
            @Override
            public void onFinish(DownloadTask.Result result) {
                handler.sendMessage(handler.obtainMessage(SWITCH_FINISHED, forUpdateCur ? 1 : 0, showTip ? 1 : 0, result));
            }

            @Override
            public void onProgress(DownloadTask.Result result, int progress, int total) {

            }

            @Override
            public void onCancel() {
                hideProgressDlg();
                if (showTip && A.isNetworkConnecting())
                    T.showToastText(ActivityTxt.this, getString(R.string.get_content_failed));
            }

            @Override
            public void onError(DownloadTask.Result result, String err) {
                if (showTip)
                    handler.sendMessage(handler.obtainMessage(SWITCH_ERROR, result));
            }
        };

        DownloadTaskBrowser.CacheWebView wb = null;
        if (site.javascriptTOC) {
            if (cacheWB == null || cacheWB.destroyed) {
                cacheWB = DownloadTaskBrowser.createDownloadWebView();
            }
            wb = cacheWB;
            wb.stop = false;
            wb.delayed = site.TOCDelay == 0 ? 200 : site.TOCDelay;
        }

        DownloadTask.download(wb, result, url, null, site.userAgent);
    }

    private void doSwitchSourceFinished(DownloadTask.Result result, boolean forUpdateCur, boolean showTip) {
        if (isOnExiting || isFinishing() || !isWebBook()) {
            hideProgressDlg();
            return;
        }

        switchTaskBook.site.last_url = result.final_url;
        if (switchTaskBook.site instanceof S.LinkSite)
            switchTaskBook.site.firstExecute = false;
        if (T.isNull(switchTaskBook.description) && !T.isNull(A.ebook.description))
            switchTaskBook.description = A.ebook.description;
        ArrayList<S.WebChapter> chapters = S.getChapterList(result.html, switchTaskBook.site);

        if (chapters.size() == 1 && chapters.get(0).name.equals(S.CHAPTER_URL_TAG)
                && !chapters.get(0).url.equals(result.url)) {
            getSourceTask(switchTaskSiteTag, chapters.get(0).url, false, forUpdateCur, showTip, switchTaskBook);
            return;
        }

        if (chapters.size() == 0 && switchTaskBook.chapters.size() == 0 && switchTaskBook.site.javascriptTOC)
            if (DownloadTaskBrowser.reloadCacheWB(cacheWB, result.url, reload_urls, 200))
                return;

        if (!S.sameChapterList(switchTaskBook.chapters, chapters))
            switchTaskBook.chapters.addAll(chapters);
        chapters = switchTaskBook.chapters;

        if (!T.isNull(switchTaskBook.site.nextChapterTag)) {
            switchTaskBook.loadedUrls.add(result.url);
            String nextUrl = S.getNextChapterUrl(result.html, switchTaskBook.site, switchTaskBook.loadedUrls);
            if (!T.isNull(nextUrl)) {
                A.log("*hasNextChapterUrl: " + nextUrl, chapters.size());
                getSourceTask(switchTaskSiteTag, nextUrl, false, forUpdateCur, showTip, switchTaskBook);
                return;
            }
            S.deleteDuplicatedChapters(chapters); //第二页里的第一页url可能跟原第一页的不同, 重复了
        }

        hideProgressDlg();
        if (chapters.size() > 0) {
            if (forUpdateCur) {
                int updates = chapters.size() - A.ebook.getChapters().size();
                if (updates < 0) {
                    if (chapters.size() > 1 && showTip) {
                        T.showAlertText(this, A.fanti("章节列表已修复, 原章节数" + A.ebook.getChapters().size() + ", 新章节数" + chapters.size()));
                        WBpub.saveBook(switchTaskBook, false);
                        if (A.lastChapter > 0)
                            A.lastChapter--;
                        if (A.lastChapter > chapters.size() - 1)
                            A.lastChapter = chapters.size() - 1;
                        A.loadEBook(A.lastFile);
                        handler.postDelayed(this, 10);
                    }
                } else if (updates == 0) {
                    int size = chapters.size();
                    if (!chapters.get(size - 1).name.equals(A.ebook.getChapters().get(size - 1).name)) {
                        WBpub.saveBook(switchTaskBook, false);
                        A.loadEBook(A.lastFile);
                        T.showToastText(this, A.fanti("章节列表已更新"));
                    } else if (showTip)
                        T.showToastText(this, getString(R.string.meiyouxinzhangjie));
                } else if (updates > 0) {
                    ActivityMain.chapterChanged = true;
                    WBpub.saveBook(switchTaskBook, false);
                    A.loadEBook(A.lastFile);
                    showReadProgress(0);
                    if (showTip)
                        T.showAlertText(this, getString(R.string.xinzeng) + updates + getString(R.string.xinzheng2) + chapters.get(chapters.size() - 1).name);
                    else
                        T.showToastText(this, getString(R.string.xinzeng) + updates + getString(R.string.xinzheng2) + chapters.get(chapters.size() - 1).name, 1);
                }
            } else {
                if (chapters.size() > A.lastChapter) {
                    switchToNewSource(chapters);
                } else {
                    new MyDialog.Builder(this).setMessage(Html.fromHtml(A.fanti("当前章节是<b>\"" + A.ebook.getChapters().get(A.lastChapter).name
                            + "\"</b>, 所选书源<b>\"" + switchTaskBook.site.name() + "\"</b>章节数总共<b>只有" + chapters.size() + "章</b>, 是否仍切换过去?")))
                            .setPositiveButton(R.string.ok, new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switchToNewSource(switchTaskBook.chapters);
                                }
                            }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
                }
            }
        } else {
            if (forUpdateCur) {
                if (showTip)
                    T.showToastText(this, getString(R.string.gengxinshibai));
            } else
                T.showAlertText(this, getString(R.string.huanyuan1) + switchTaskBook.site.name() + getString(R.string.huanyuan2));
        }
    }

    private void switchToNewSource(ArrayList<S.WebChapter> chapters) {
        int last = WB.getLastChapter(A.lastChapter, A.ebook.getChapters().get(A.lastChapter).name, chapters);
        if (last != A.lastChapter) {
            A.log("last chapter: " + A.lastChapter + ", new last chapter: " + last);
            A.lastChapter = last;
            saveLastPostion(true);
        }
        WBpub.saveBook(switchTaskBook, true);
        restartReaderToTxt();
    }

    private HashMap<String, Integer> reload_urls = new HashMap<>();

    private void doSwitchSourceError(DownloadTask.Result result) {
        hideProgressDlg();
        if (A.isNetworkConnecting())
            T.showToastText(ActivityTxt.this, getString(R.string.get_content_failed) + "\n" + result.err);
    }

    //-----------------------

    public void do_download_chapters() {
        inverseLayoutVisible(true);
        final String[] items = new String[]{getString(R.string.gengxinzhangjieliebiao), getString(R.string.chongxinxiazaibenzhang),
                getString(R.string.xiazaihousanshizhang),
                A.fanti("下载后面全部章节"),
                A.fanti("下载全部"),
                A.fanti("查看全本下载链接" + (A.ebook.online_site.downloadable ? "" : "(未知)"))};
        new MyDialog.Builder(this).setTitle(getString(R.string.download))
                .setSingleChoiceItems(items, 2, null)
                .setPositiveButton(R.string.ok, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lv = ((AlertDialog) dialog).getListView();
                        final int pos = lv.getCheckedItemPosition();
                        if (pos == 0) {
                            updateWebBookChapters(true);
                        } else if (pos == 1) {
                            updateOnlineChapter(A.lastChapter, getString(R.string.downloading_content), false);
                            online_caching = true;
                            createProgressDlg("", A.ebook.getChapters().get(A.lastChapter).name + "\n" + getString(R.string.downloading_content));
                            clearTxtView();
                        } else if (pos < 5) {
                            final int count = pos == 2 ? 30 : pos == 3 ? 0 : -1;
                            if (isJavascriptContent()) {
                                new MyDialog.Builder(ActivityTxt.this)//.setTitle("提示")
                                        .setMessage(Html.fromHtml(getString(R.string.bunenghuancheng)
                                        ))//+"<br><small><font color=\"#888888\">(如发现无效或重复章节内容, 可清空缓存, 重新读取; 或切换到其它书源)</font></small>"))
                                        .setPositiveButton(R.string.ok, new OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                downloadCaches(count);
                                            }
                                        }).setNegativeButton(R.string.cancel, null).show();
                            } else
                                downloadCaches(count);
                        } else {
                            String url = WB.getWebBookUrl(A.lastFile, A.ebook.online_site.siteTag);
                            if (T.isNull(url))
                                url = A.ebook.getChapters().get(A.lastChapter).url;
                            String bookName = A.ebook.getBookName() + (!T.isNull(A.ebook.getAuthor()) ? " - " + A.ebook.getAuthor() : "");
                            BrowserAct.loadDownloadPage(ActivityTxt.this, A.ebook.online_site, url, null,
                                    bookName, A.fanti("如有书籍下载链接, 可点击下载"));
                        }
                    }
                })
                .setNeutralButton(getString(R.string.qingkonghuanchun), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearBookCache();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    boolean inDownloading;

    private void downloadCaches(int count) {
        ArrayList<Integer> list = new ArrayList<>();
        int size = A.ebook.getChapters().size();

        if (count == -1) { //全部
            for (int i = A.lastChapter + 2; i < size; i++) //already cache next chapter
                if (!T.isFile(A.ebook.getChapters().get(i).filename))
                    list.add(i);
            for (int i = 0; i < A.lastChapter; i++)
                if (!T.isFile(A.ebook.getChapters().get(i).filename))
                    list.add(i);
        } else if (count == 0) { //后面全部
            for (int i = A.lastChapter + 2; i < size; i++) //already cache next chapter
                if (!T.isFile(A.ebook.getChapters().get(i).filename))
                    list.add(i);
        } else {
            for (int i = A.lastChapter + 2; i < size; i++) {
                if (--count == 0)
                    break;
                if (!T.isFile(A.ebook.getChapters().get(i).filename))
                    list.add(i);
            }
        }

        if (list.size() == 0) {
            T.showAlertText(this, getString(R.string.zhangjieyixiazai));
            return;
        }

        cacheUrls.clear();
        for (int id : list)
            cacheUrls.add(new WBpub.WebCacheItem(A.ebook.getChapters().get(id).url, A.ebook.online_site.userAgent, A.ebook.getChapters().get(id).name, id, false));

        inDownloading = true;
        handler.sendEmptyMessageDelayed(CACHING_TASK, 0);

        lastCachingTitle = Html.fromHtml(A.ebook.getChapters().get(list.get(0)).name
                + "<br><br><small><font color=\"#888888\">" + getString(R.string.paiduizhong) + cacheUrls.size() + "</font></small>");
        createCachingDialog();
    }

    private void createCachingDialog() {
        if (progressDlg != null) {
            dissmissDlg = progressDlg;
            progressDlg.dismiss();
        }
        progressDlg = !A.isNightState() ? new ProgressDialog(this) : new ProgressDialog(this, R.style.MyProgressDialogDark);
        progressDlg.setTitle(getString(R.string.zhengzaixiazai));
        progressDlg.setMessage(T.isNull(lastCachingTitle) ? "" : lastCachingTitle);
        progressDlg.setCancelable(false);
        progressDlg.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.houtiaxiazai),
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        A.setSystemUiVisibility(true);
                        progressDlg = null;
                    }
                });
        progressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.tingzhixiazai),
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        A.setSystemUiVisibility(true);
                        inDownloading = false;
                        cacheUrls.clear();
                        progressDlg = null;
                        showReadProgress(0);
                    }
                });
        progressDlg.show();
    }

    CharSequence lastCachingTitle;

    private void showCachingProgress(WBpub.WebCacheItem item) {
        if (!inDownloading)
            return;
        if (progressDlg != null) {
            lastCachingTitle = Html.fromHtml(A.ebook.getChapters().get(item.chapterId).name
                    + "<br><br><small><font color=\"#888888\">" + getString(R.string.paiduizhong) + cacheUrls.size() + "</font></small>");
            progressDlg.setMessage(lastCachingTitle);
        } else if (showStatusbar() && statusMiddle != null && !dualPageEnabled())
            statusMiddle.setText(getString(R.string.zhengzaixiazai) + "(" + cacheUrls.size() + "): " + A.ebook.getChapters().get(item.chapterId).name);
    }

    void checkDownloadFinished(boolean error) {
        if (!inDownloading)
            return;
        if (isFinishing() || !isWebBook())
            return;
        if (error || cacheUrls.size() == 0) {
            inDownloading = false;
            lastCachingTitle = null;
            cacheUrls.clear();
            hideProgressDlg();
            if (error) {
                T.showAlertText(this, getString(R.string.qingqiehuanshuyuan));
            } else {
                int size = T.getFolderSize(T.getFilePath(A.lastFile));
                T.showAlertText(this, getString(R.string.xiazaiwanchengkongjian) + Formatter.formatFileSize(this, size));
            }
            showReadProgress(0);
        }
    }

    private boolean stayInCacheing() {
        if (!inDownloading)
            return false;

        if (isJavascriptContent()) {
            new MyDialog.Builder(this).setMessage(A.fanti("\n正在下载章节, 是否退出? \n(所用书源不支持后台缓存)\n"))
                    .setPositiveButton(A.fanti("直接退出"), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            cacheUrls.clear();
                            do_back_to_book_stack();
                        }
                    })
                    .setNegativeButton(A.fanti("查看进度"), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            createCachingDialog();
                        }
                    })
//					.setNeutralButton(R.string.cancel, null)
                    .show();
        } else {
            new MyDialog.Builder(this).setMessage(A.fanti("\n正在下载章节, 退出后是否继续在后台缓存?\n"))
                    .setPositiveButton(A.fanti("继续缓存"), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (lastCacheItem != null)
                                cacheUrls.add(0, lastCacheItem);
                            ChapterDownloadService.downloadChapters(ActivityTxt.this, (WBpub) A.ebook, cacheUrls);
                            cacheUrls.clear();
                            do_back_to_book_stack();
                        }
                    })
                    .setNegativeButton(A.fanti("停止缓存"), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            cacheUrls.clear();
                            do_back_to_book_stack();
                        }
                    })
//					.setNeutralButton(A.fanti("查看进度"), new OnClickListener() {
//						public void onClick(DialogInterface dialog, int which) {
//							createCachingDialog();
//						}
//					})
                    .show();
        }

        return true;
    }

    //----------------------

    int marginTipCount = 0;

    protected void adjustMargin(int dir) { // left 0 right 1 top 2 bottom 3
        try {
            A.setMarginLimitation();
            handler.removeMessages(ActivityTxt.SET_MARGIN);
            int delay = 200;
            handler.sendEmptyMessageDelayed(SET_MARGIN, delay);

            if ((dir == 0 && A.leftMargin == 0) || (dir == 1 && A.rightMargin == 0)) {
                if (A.ebook != null && marginTipCount < 2) {
                    boolean hasMargin = false;
                    int line = txtView.getLayout().getLineForVertical(txtScroll.getScrollY());
                    if (txtView.getCssMargins(line).left > 0 || txtView.getCssMargins(line).right > 0) {
                        hasMargin = true;
                    } else {
                        int line2 = txtView.getLayout().getLineForVertical(txtScroll.getScrollY() + A.getPageHeight() / 2);
                        if (line2 != line) {
                            if (txtView.getCssMargins(line2).left > 0 || txtView.getCssMargins(line2).right > 0)
                                hasMargin = true;
                        }
                    }
                    if (hasMargin) {
                        marginTipCount++;
                        T.showAlertText(this, getString(R.string.statubar_margin),
                                Html.fromHtml(getString(R.string.css_margin_tip) + "<br><br>\"<b>"
                                        + "设置" + " -> 排版 -> 精排版书籍选项" + "</b>\" -> \"<b>"
                                        + getString(R.string.disable_css) + "</b>\" -> \"<b>"
                                        + A.getStringArrayItem(R.array.disable_css_styles, 6) + "</b>\"")
                        );
                    }
                }
            }
        } catch (Exception e) {
            A.error(e);
        }
    }

    //----------------------


    private void refreshFontStyle() {
        A.setTxtViewTypeface();
        clearCurlCache();
    }

    public void clearCurlCache() {
        resetFlipCache();
        forceNoCurlCache = true;
    }

    private void fontSizeAdd(int add) {
        if (isPdfNoflow()) {
            pdfZoomRatio(add > 0 ? 0.1f : -01.f);
            return;
        }
        if (web != null) {
            webViewZoom(add > 0);
            return;
        }
        resetPageCount();
        A.fontSize += add;
        if (A.fontSize < A.minFontSize)
            A.fontSize = A.minFontSize;
        if (A.fontSize > A.maxFontSize)
            A.fontSize = A.maxFontSize;
        T.showToastText(this, "" + (int) (A.fontSize), 0, Gravity.CENTER);
        setFontSize();
        if (fontSizeTv != null)
            fontSizeTv.setText("" + (int) (A.fontSize));
    }

    private void setTypesetId(int id) { // 1 narrow, 2 normal, 3 wide, 4 none
        A.lineSpace = id == 1 ? 0 : id == 2 ? 3 : id == 3 ? 10 : 0;
        A.paragraphSpace = id == 1 ? 0 : id == 2 ? 7 : id == 3 ? 10 : 10;
        A.setFontSpace(A.txtView);
        A.setFontSpace(A.txtView2);
        A.setLineSpace(A.txtView);
        A.setLineSpace(A.txtView2);
        clearCurlCache();
        A.typesetId = id;
        updateTypesetIndicator();
        hideTopLay();
    }

    private void updateTypesetIndicator() {
        if (fontPanel != null) {
            ((RoundImage) fontPanel.findViewById(R.id.typeset1)).setChecked(A.typesetId == 1);
            ((RoundImage) fontPanel.findViewById(R.id.typeset2)).setChecked(A.typesetId == 2);
            ((RoundImage) fontPanel.findViewById(R.id.typeset3)).setChecked(A.typesetId == 3);
            ((RoundButton) fontPanel.findViewById(R.id.typeset4)).setChecked(A.typesetId == 4);
            ((RoundButton) fontPanel.findViewById(R.id.moreTypeset)).setChecked(A.typesetId == 5);
        }
        /*if (spacePanel != null){
            ((RoundImage) spacePanel.findViewById(R.id.typeset21)).setChecked(A.typesetId == 1);
            ((RoundImage) spacePanel.findViewById(R.id.typeset22)).setChecked(A.typesetId == 2);
            ((RoundImage) spacePanel.findViewById(R.id.typeset23)).setChecked(A.typesetId == 3);
            ((RoundButton) spacePanel.findViewById(R.id.typeset24)).setChecked(A.typesetId == 4);
        }*/
    }

    private void setThemeId(int id) {
        loadTheme("" + id, A.loadThemeWithColorOnly);
        A.themeId = id;
        updateThemeIndicator();
        hideTopLay();
    }

    private void updateThemeIndicator() {
        if (fontPanel == null)
            return;
        theme1.setChecked(A.themeId == 1);
        theme2.setChecked(A.themeId == 2);
        theme3.setChecked(A.themeId == 3);
        theme4.setChecked(A.themeId == 4);
        ((RoundButton) bottomLay.findViewById(R.id.moreTheme)).setChecked(A.themeId == 5);
    }

    private void updateFlipIndicator() {
        if (fontPanel == null)
            return;
        flip1.setChecked(A.flip_animation == A.FLIP_NONE && !A.disableMove);
        flip2.setChecked(A.flip_animation == A.FLIP_HORIZONTAL || A.flip_animation == A.FLIP_SHIFT_HORIZONTAL);
        flip3.setChecked(A.flip_animation == A.FLIP_CURL3D_G || A.flip_animation == A.FLIP_CURL3D);
        moreFlip.setChecked(!flip1.isChecked() && !flip2.isChecked() && !flip3.isChecked());
    }

    private void updateThemeImage() {
        if (fontPanel == null)
            return;
        A.PageTheme pt = A.getThemeList().get(A.getThemeId("1"));
        theme1.setRoundImageDrawable(!pt.pUseBackgroundImage ? new ColorDrawable(pt.pBackgroundColor) :
                A.getImagesDrawable(A.getBackgroundImages(false), pt.pBackgroundImage, A.SMALL_SIZE, A.LOW_QUALITY));
        pt = A.getThemeList().get(A.getThemeId("2"));
        theme2.setRoundImageDrawable(!pt.pUseBackgroundImage ? new ColorDrawable(pt.pBackgroundColor) :
                A.getImagesDrawable(A.getBackgroundImages(false), pt.pBackgroundImage, A.SMALL_SIZE, A.LOW_QUALITY));
        pt = A.getThemeList().get(A.getThemeId("3"));
        theme3.setRoundImageDrawable(!pt.pUseBackgroundImage ? new ColorDrawable(pt.pBackgroundColor) :
                A.getImagesDrawable(A.getBackgroundImages(false), pt.pBackgroundImage, A.SMALL_SIZE, A.LOW_QUALITY));
        pt = A.getThemeList().get(A.getThemeId("4"));
        theme4.setRoundImageDrawable(!pt.pUseBackgroundImage ? new ColorDrawable(pt.pBackgroundColor) :
                A.getImagesDrawable(A.getBackgroundImages(false), pt.pBackgroundImage, A.SMALL_SIZE, A.LOW_QUALITY));
    }

    private void updateColorPanel() {
        if (colorPanel == null)
            return;
        ((RoundButton) colorPanel.findViewById(R.id.fontColor)).setSolidColor(A.fontColor);
        ((RoundImage) colorPanel.findViewById(R.id.backgroundImage)).setRoundImageDrawable(
                A.getImagesDrawable(A.getBackgroundImages(false), A.backgroundImage, A.SMALL_SIZE, A.LOW_QUALITY));
        ((RoundButton) colorPanel.findViewById(R.id.backgroundColor)).setSolidColor(A.backgroundColor);
        ((ImageView) colorPanel.findViewById(R.id.backgroundColorSelect)).setImageResource(!A.useBackgroundImage ? R.drawable.indicator2 : R.drawable.indicator3);
        ((ImageView) colorPanel.findViewById(R.id.backgroundImageSelect)).setImageResource(A.useBackgroundImage ? R.drawable.indicator2 : R.drawable.indicator3);

        boolean themeSaved = false;
        if (curIsTheme("1")) themeSaved = true;
        else if (curIsTheme("2")) themeSaved = true;
        else if (curIsTheme("3")) themeSaved = true;
        else if (curIsTheme("4")) themeSaved = true;
        ((RoundButton) colorPanel.findViewById(R.id.saveTheme)).setChecked(themeSaved);
        setNightModeIndicator();
    }

    private boolean curIsTheme(String name) {
        A.PageTheme pt = A.getThemeList().get(A.getThemeId(name));
        if (pt.pUseBackgroundImage == A.useBackgroundImage && pt.pFontColor == A.fontColor)
            if (A.useBackgroundImage)
                return pt.pBackgroundImage.equals(A.backgroundImage);
            else
                return pt.pBackgroundColor == A.backgroundColor;
        return false;
    }

    private void updateNightPanel() {
        if (nightPanel == null)
            return;
        A.PageTheme pt = A.getNightTheme();
        ((RoundButton) nightPanel.findViewById(R.id.nfontColor)).setSolidColor(pt.pFontColor);
        ((RoundImage) nightPanel.findViewById(R.id.nbackgroundImage)).setRoundImageDrawable(
                A.getImagesDrawable(A.getBackgroundImages(false), pt.pBackgroundImage, A.SMALL_SIZE, A.LOW_QUALITY));
        ((RoundButton) nightPanel.findViewById(R.id.nbackgroundColor)).setSolidColor(pt.pBackgroundColor);
        ((ImageView) nightPanel.findViewById(R.id.nbackgroundColorSelect)).setImageResource(!pt.pUseBackgroundImage ? R.drawable.indicator2 : R.drawable.indicator3);
        ((ImageView) nightPanel.findViewById(R.id.nbackgroundImageSelect)).setImageResource(pt.pUseBackgroundImage ? R.drawable.indicator2 : R.drawable.indicator3);
        ((ImageView) nightPanel.findViewById(R.id.nbrightIndicator)).setImageResource(A.autoThemeNightWithBright ? R.drawable.indicator2 : R.drawable.indicator3);
        ((TextView) nightPanel.findViewById(R.id.nbrightTv)).setText(A.autoThemeNightBrightness + "%");
        ((RoundButton) nightPanel.findViewById(R.id.nautoCancelNight)).setChecked(A.autoThemeDay);
    }

    private boolean setFontColor(int color) {
        if (color != A.fontColor) {
            A.fontColor = color;
            txtView.setTextColor(color);
            txtView2.setTextColor(color);
            if (showStatusbar() && !A.statusCustomizeFont)
                setStatusBarProperties(true);
            clearCurlCache();
            if (A.fontShadow) {
                A.setTxtViewShadow(txtView);
                A.setTxtViewShadow(txtView2);
                txtView.postInvalidate();
                txtView2.postInvalidate();
            }
            return true;
        }
        return false;
    }

    private void checkFontColor(Drawable d, int c) {
        setFontColor(getDrawbleFontColor(d, c));
    }

    public static int getDrawbleFontColor(Drawable d, int c) {
        int c1 = T.getColorValue(d == null ? c : T.getDrawableAboutColor(d));
        int c2 = T.getColorValue(A.fontColor);
        int color = A.fontColor;
        if (c1 < 126 && c2 < 126) {
            color = 0xfff4f4f4;
        } else if (c1 > 126 && c2 > 126) {
            color = 0xff333333;
        }
        return color;
    }

    private int saveToThemeId = -1;

    private void saveToTheme() {
        String[] items = new String[]{"位置一", "位置二", "位置三", "位置四"};
        new MyDialog.Builder(ActivityTxt.this).setTitle("保存到")
                .setSingleChoiceItems(items, saveToThemeId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveToThemeId = which;
                    }
                }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (saveToThemeId != -1) {
                    A.saveTheme("" + (saveToThemeId + 1), true);
                    updateThemeImage();
                }
            }
        }).setNegativeButton(R.string.cancel, null).show();
    }

    private void exportTheme() {
        View layout = LayoutInflater.from(this).inflate(R.layout.typesetting_export, null);
        final EditText et = layout.findViewById(R.id.path);
        final CheckBox cb1 = layout.findViewById(R.id.cb1);
        final CheckBox cb2 = layout.findViewById(R.id.cb2);

        layout.findViewById(R.id.pathTv).setVisibility(View.GONE);
        String add = !A.fontName.contains(".") ? ".ttf" : "";
        String ttfFile = A.outerFontsFolder + "/" + A.fontName + add;
        if (T.isFile(ttfFile))
            cb1.setText(cb1.getText().toString() + "\n(" + A.fontName + add + " " + A.formateSize(T.getFileSize(ttfFile)) + ")");

        new MyDialog.Builder(this)
                .setTitle(A.fanti("导出配色方案"))
                .setView(layout)
                .setPositiveButton(A.fanti("导出"), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String filename = et.getText().toString();
                        if (exportThemeOK(filename, cb1.isChecked(), cb2.isChecked()))
                            T.showToastText(ActivityTxt.this, A.fanti("已导出到" + filename));
                        else
                            T.showToastText(ActivityTxt.this, A.fanti("导出失败"));
                    }
                }).setNegativeButton(A.fanti("导出并分享"), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String filename = et.getText().toString();
                if (exportThemeOK(filename, cb1.isChecked(), cb2.isChecked()))
                    A.sendFile(ActivityTxt.this, filename);
            }
        }).show();
    }

    private boolean exportThemeOK(String filename, boolean includeFont, boolean includeMargins) {
        StringBuilder sb = new StringBuilder();
        sb.append((A.useBackgroundImage ? "*pic" : "*color") + "\n");
        sb.append((A.useBackgroundImage ? A.backgroundImage : A.backgroundColor) + "\n");
        sb.append(A.fontColor + "\n");
        if (includeFont) {
            sb.append("*font\n");
            sb.append(A.fontName + "\n");
            sb.append(A.fontAnti + "\n");
            sb.append(A.fontBold + "\n");
            sb.append(A.fontItalic + "\n");
            sb.append(A.fontShadow + "\n");
            sb.append(A.fontUnderline + "\n");
            sb.append(A.dashUnderline + "\n");
        }
        if (includeMargins) {
            sb.append("*margin\n");
            sb.append(A.lineSpace + "\n");
            sb.append(A.paragraphSpace + "\n");
            sb.append(A.fontSpace + "\n");
            sb.append(A.leftMargin + "\n");
            sb.append(A.rightMargin + "\n");
            sb.append(A.topMargin + "\n");
            sb.append(A.bottomMargin + "\n");
        }

        String setFile = A.book_cache + "/typeset";
        if (!T.saveFileText(setFile, sb.toString()))
            return false;

        try {
            ZipOutputStream outZip = new ZipOutputStream(T.getFileOutputStream(filename));
            MyZip_Java.addToZip(outZip, setFile, T.getFilename(setFile));
            if (A.useBackgroundImage && A.backgroundImage.startsWith("/") && T.isFile(A.backgroundImage))
                MyZip_Java.addToZip(outZip, A.backgroundImage, "pic" + T.getFileExt(A.backgroundImage));
            String add = !A.fontName.contains(".") ? ".ttf" : "";
            String ttfFile = A.outerFontsFolder + "/" + A.fontName + add;
            if (includeFont && T.isFile(ttfFile))
                MyZip_Java.addToZip(outZip, ttfFile, "font.ttf");
            outZip.finish();
            outZip.close();
            return true;
        } catch (Exception e) {
            A.error(e);
        }
        return false;
    }

    private void importTheme() {
        View layout = LayoutInflater.from(this).inflate(R.layout.typesetting_export, null);
        final TextView tv = layout.findViewById(R.id.pathTv);
        final CheckBox cb1 = layout.findViewById(R.id.cb1);
        final CheckBox cb2 = layout.findViewById(R.id.cb2);

        layout.findViewById(R.id.path).setVisibility(View.GONE);
        layout.findViewById(R.id.tip).setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        setImportCheckedVisible(tv.getText().toString(), cb1, cb2);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PrefFileSelector(ActivityTxt.this, A.fanti("选择配色文件"),
                        T.getFilePath(tv.getText().toString()), new PrefFileSelector.OnGetFile() {
                    @Override
                    public void onGetFile(String filename) {
                        tv.setText(filename);
                        setImportCheckedVisible(tv.getText().toString(), cb1, cb2);
                    }
                }, "*.ssdsps").show();
            }
        });

        new MyDialog.Builder(this)
                .setTitle(A.fanti("导入配色方案"))
                .setView(layout)
                .setPositiveButton(A.fanti("导入"), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String filename = tv.getText().toString();
                        if (!T.isFile((filename)))
                            T.showAlertText(ActivityTxt.this, A.fanti("文件不存在"));
                        else if (importThemeOK(filename, cb1.isChecked(), cb2.isChecked()))
                            new MyDialog.Builder(ActivityTxt.this).setMessage(A.fanti("\n已成功导入"))
                                    .setPositiveButton(R.string.ok, new OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            restartReaderIntent();
                                        }
                                    }).setCancelable(false).show();
                        else
                            T.showAlertText(ActivityTxt.this, A.fanti("导入失败, 配置文件可能已损坏"));
                    }
                }).setNegativeButton(A.fanti("取消"), null).show();
    }

    private void setImportCheckedVisible(String filename, CheckBox cb1, CheckBox cb2) {
        cb1.setChecked(true);
        cb1.setEnabled(true);
        cb2.setChecked(true);
        cb2.setEnabled(true);
        if (T.isFile(filename))
            try {
                MyAntZip zipper = new MyAntZip(filename);
                ArrayList<String> fileList = zipper.getAllList();
                if (fileList.contains("typeset")) {
                    InputStream is = zipper.getFileStream("typeset");
                    ArrayList<String> opList = T.text2StringList(T.inputStream2String(is));
                    if (!opList.contains("*font")) {
                        cb1.setChecked(false);
                        cb1.setEnabled(false);
                    }
                    if (!opList.contains("*margin")) {
                        cb2.setChecked(false);
                        cb2.setEnabled(false);
                    }
                }
            } catch (Exception e) {
                A.error(e);
            }
    }

    private boolean importThemeOK(String filename, boolean includeFont, boolean includeMargins) {
        try {
            MyAntZip zipper = new MyAntZip(filename);
            ArrayList<String> fileList = zipper.getAllList();
            if (fileList.contains("typeset")) {
                InputStream is = zipper.getFileStream("typeset");
                ArrayList<String> opList = T.text2StringList(T.inputStream2String(is));

                String pic = null;
                if (opList.get(0).equals("*pic")) {
                    A.useBackgroundImage = true;
                    pic = opList.get(1);
                    String zipFile = "pic" + T.getFileExt(pic);
                    if (fileList.contains(zipFile)) {
                        String saveTo = A.outerImagesFolder + "/" + T.getFilename(pic);
                        zipper.saveToFile(zipFile, saveTo);
                        A.backgroundImage = saveTo;
                    } else
                        A.backgroundImage = pic;
                } else {
                    A.useBackgroundImage = false;
                    A.backgroundColor = T.string2Int(opList.get(1));
                }

                A.fontColor = T.string2Int(opList.get(2));
                if (includeFont && opList.contains("*font")) {
                    int i = opList.indexOf("*font");
                    A.fontName = opList.get(i + 1);
                    A.fontAnti = opList.get(i + 2).equals("true");
                    A.fontBold = opList.get(i + 3).equals("true");
                    A.fontItalic = opList.get(i + 4).equals("true");
                    A.fontShadow = opList.get(i + 5).equals("true");
                    A.fontUnderline = opList.get(i + 6).equals("true");
                    A.dashUnderline = opList.get(i + 7).equals("true");
                    if (fileList.contains("font.ttf")) {
                        String add = !A.fontName.contains(".") ? ".ttf" : "";
                        zipper.saveToFile("font.ttf",
                                A.outerFontsFolder + "/" + A.fontName + add);
                    }
                }

                if (includeMargins && opList.contains("*margin")) {
                    int i = opList.indexOf("*margin");
                    A.lineSpace = T.string2Int(opList.get(i + 1));
                    A.paragraphSpace = T.string2Int(opList.get(i + 2));
                    A.fontSpace = T.string2Int(opList.get(i + 3));
                    A.leftMargin = T.string2Int(opList.get(i + 4));
                    A.rightMargin = T.string2Int(opList.get(i + 5));
                    A.topMargin = T.string2Int(opList.get(i + 6));
                    A.bottomMargin = T.string2Int(opList.get(i + 7));
                }
                return true;
            }
        } catch (Exception e) {
            A.error(e);
        }
        return false;
    }

    private void clearCachedWebViews() {
        for (int i = baseFrame.getChildCount() - 1; i >= 0; i--) {
            View v = baseFrame.getChildAt(i);
            if (v instanceof WebView)
                if (v != web && v != cacheWB) {
                    WebView w = (WebView) v;
                    S.clearWebView(w);
                    baseFrame.removeView(w);
                    w.destroy();
                }
        }
    }

    public void updateWebBookChapters(boolean showTip) {
        if (isFinishing() || !isWebBook())
            return;
        String url = WB.getWebBookUrl(A.lastFile, A.ebook.online_site.siteTag);
        if (!T.isNull(url))
            getSourceTask(A.ebook.online_site.siteTag, url, true, true, showTip, null);
        if (showTip)
            T.showToastText(this, A.fanti("提示: 可在设置里启用自动检查新章节功能"), 1);
        A.bookUpateRecord(A.lastFile);
    }

    private boolean isVipLink(final String url) {
        if (isWebBook() && S.isVipChapter(A.ebook.getChapters().get(A.lastChapter).name))
            if (htmlSrc.contains(WBpub.VIP_CHAPTER_TAG1) && htmlSrc.contains(WBpub.VIP_CHAPTER_TAG2)) {
                if (A.ebook.online_site == S.store) {
                    openVipBrowser(url);
                } else {
                    String[] items = new String[]{getString(R.string.zaisoushuliyd), getString(R.string.shiyongwaibuliulan)};
                    new MyDialog.Builder(this).setTitle(getString(R.string.qingxuanze)).setSingleChoiceItems(items, 0, null)
                            .setPositiveButton(R.string.ok, new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ListView lv = ((AlertDialog) dialog).getListView();
                                    if (lv.getCheckedItemPosition() == 0) {
                                        openVipBrowser(url);
                                    } else {
                                        T.openUrl(ActivityTxt.this, url);
                                    }
                                }
                            }).setNegativeButton(R.string.cancel, null)
                            .setNeutralButton(getString(R.string.youhuaydshuoming), new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(ActivityTxt.this, BrowserAct.class);
                                    i.putExtra("filename", "各平台VIP章节阅读指引");
                                    startActivity(i);
                                }
                            }).show();
                }
                return true;
            }
        return false;
    }

    String yueWenUrlBeforeLogin;

    public void openVipBrowser(String url) {
        if (A.noNetworkAlert(this))
            return;

        if (ThirdConfig.isYueWenUrl(url) && !W.accountOk()) {
            yueWenUrlBeforeLogin = url;
            W.showLogin(this, StoreWebView.STORE_LOGIN2);
            return;
        }

        S.addForceWebSite(A.ebook.online_site.siteTag);
        for (int i = A.lastChapter + 1; i < A.ebook.getChapters().size(); i++)
            A.ebook.getChapters().get(i).text = getString(R.string.downloading_content);
        clearCurlCache();
        preNextChapterText = null;

        Intent i = new Intent(ActivityTxt.this, BrowserAct.class);
        i.putExtra("url", url);
        i.putExtra("siteTag", A.ebook.online_site.siteTag);
        i.putExtra("title", ThirdConfig.isYueWenUrl(url) ? A.fanti("请选择\"自动订阅\"") : getString(R.string.denglupingtaitishi));
        startActivityForResult(i, 126);
    }

    private boolean isWebImage(final String url, String title) {
        if (A.isHttpUrl(url) && title.equals(getString(R.string.download_url_image))) {
            createProgressDlg(getString(R.string.download_url_image), url);
            new Thread() {
                @Override
                public void run() {
                    try {
                        URL m = new URL(url);
                        InputStream is = (InputStream) m.getContent();
                        Drawable d = Drawable.createFromStream(is, "src");
                        if (d != null) {
                            T.drawableToFile(d, A.generateUrlImageCacheName(url));
                            handler.sendEmptyMessage(DOWNLOAD_WEB_IMAGE_FINISHED);
                        }
                    } catch (Throwable e) {
                        A.error(e);
                        handler.sendMessage(handler.obtainMessage(DOWNLOAD_WEB_IMAGE_FAILED, A.errorMsg(e)));
                    }
                }
            }.start();
            return true;
        }
        return false;
    }

    //-------------------

    private boolean shareValidteOK(final int type) { //1: font 2: margin 3: autoscroll 4: TTS
        return true;
		/*final String valid_file = A.xml_files_folder + "/" + (type == 1 || type == 2? "shareok_font" : "shareok_tts");
		if (T.isFile(valid_file))
			return true;

		String s = type == 1? "需要开启自定义字体和自定义排版功能吗？\n\n分享一次搜书大师到您的QQ群或微信群即可开通，谢谢您的帮助！" :
				type == 2? "需要开启自定义字体和自定义排版功能吗？\n\n分享一次搜书大师到您的QQ群或微信群即可开通，谢谢您的帮助！" :
						type == 3? "需要开启朗读和自动翻页功能吗？\n\n分享一次搜书大师到您的QQ群或微信群即可开通，谢谢您的帮助！" :
								type == 4? "需要开启朗读和自动翻页功能吗？\n\n分享一次搜书大师到您的QQ群或微信群即可开通，谢谢您的帮助！" :
										"";
		new MyDialog.Builder(this).setMessage(s)
				.setPositiveButton("开通", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AboutAct.doSocialShare(ActivityTxt.this, null, false, 0);
						T.saveFileText(valid_file, "" + System.currentTimeMillis());
						A.trackEvent("doShare", "from_func", "func" + type, 1);
					}
				})
				.setNegativeButton("不用了", null).setCancelable(false).show();

		return false;*/
    }

    StoreWebView readWeb;
    boolean firstLoadUrl;

    private void showWebReading() {
//		A.useWebReading = true;
        inWebReading = true;
        A.addWebReading(A.ebook.online_site.siteTag);
        if (readWeb == null) {
            readWeb = new StoreWebView(this, new StoreWebView.StoreWebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    A.log("#url : " + url);
                    if (S.openAppInWebView(ActivityTxt.this, url, true))
                        return true;
                    if (url.endsWith(".txt")) {
                        BrowserAct.downloadBook(ActivityTxt.this, handler, progressIndicator, url, null, null);
                        return true;
                    }
                    if (firstLoadUrl)
                        view.clearHistory();
                    if (ThirdConfig.isYueWenUrl(url))
                        return super.shouldOverrideUrlLoading(view, url);
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    A.log("#onPageStarted : " + url);
                    showProgressIndicator();
                    if (firstLoadUrl)
                        view.clearHistory();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    A.log("#onPageFinished : " + url);
                    hideProgressDlg();
                    if (firstLoadUrl) {
                        firstLoadUrl = false;
                        view.clearHistory();
                    }
                }
            }, S.getChromeClient(A.ebook.online_site), handler);

//			S.setWebViewSettings(readWeb);
//			readWeb.setWebViewClient(new MyViewClient());
//			readWeb.setWebChromeClient(S.getChromeClient(A.ebook.online_site));
            readWeb.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    A.log("*onDownloadStart : " + url);
                    createProgressIndicator();
                    progressIndicator.setVisibility(View.GONE);
                    BrowserAct.downloadBook(ActivityTxt.this, handler, progressIndicator, url, null, null);
                }
            });

            readWeb.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    boolean touchDisabled = false;
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        pressDownX = event.getX();
                        if (isTouchInEdge(event))// || isMiddleTap(event))
                            touchDisabled = true;
                    }
                    ActivityTxt.this.onTouch(readWeb, event);
                    if (event.getAction() == MotionEvent.ACTION_UP)
                        touchDisabled = false;
                    if (touchDisabled)
                        return true;
                    return false;
                }
            });

            baseFrame.addView(readWeb, new FrameLayout.LayoutParams(-1, -1));
            readWeb.bringToFront();
            initTopBottomLay(true);
            topLay.bringToFront();
            bottomLay.bringToFront();
            shadeView.bringToFront();
        }
        firstLoadUrl = true;
        readWeb.clearHistory();
        readWeb.setVisibility(View.VISIBLE);
        loadWebUrl(readWeb, A.ebook.online_site, A.ebook.getChapters().get(A.lastChapter).url);
    }

    MyDialog.Builder adSiteAlertDlg;

    private void loadWebUrl(final WebView readWeb, final S.BookSite site, final String url) {
        if (site != null && site.hasAd && !S.ignoreAdTip(site.siteTag)) {
            ScrollView layout = (ScrollView) LayoutInflater.from(this).inflate(R.layout.do_event_confirm, null);
            TextView tv = layout.findViewById(R.id.ofTextView);
            final CheckBox cb = layout.findViewById(R.id.ofNeverAskAgain);
            tv.setText(A.fanti("该书源网页可能含有不当广告, 广告并非来自搜书大师, 搜书大师亦无法预知广告内容, 请谨慎点击.\n\n请确认是否仍要打开该网页?"));
            cb.setChecked(false);
            cb.setText(A.fanti("不再对这个网站提出警示"));
            if (adSiteAlertDlg != null)
                adSiteAlertDlg.dismiss();
            adSiteAlertDlg = new MyDialog.Builder(this);
            adSiteAlertDlg.setTitle("广告提示(" + site.name() + ")")
                    .setView(layout)
                    .setPositiveButton(A.fanti("用浏览器打开"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (cb.isChecked())
                                S.addToIgnoreAdTipSites(site.siteTag);
                            readWeb.loadUrl(url);
                            adSiteAlertDlg = null;
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (cb.isChecked())
                        S.addToIgnoreAdTipSites(site.siteTag);
                    switchWebReading(false);
                    adSiteAlertDlg = null;
                }
            }).setCancelable(false).show();
        } else
            readWeb.loadUrl(url);
    }

    private void hideWebReading() {
//		A.useWebReading = false;
        A.deleteWebReading(A.ebook.online_site.siteTag);
        if (readWeb != null) {
            readWeb.stopLoading();
            readWeb.clearHistory();
            readWeb.setVisibility(View.GONE);
        }
    }

/*
	class MyViewClient extends android.webkit.WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, final String url) {
			A.log("#url : " + url);
			if (S.openAppInWebView(ActivityTxt.this, url))
				return true;
			if (url.endsWith(".txt")) {
				BrowserAct.downloadBook(ActivityTxt.this, handler, progressIndicator, url, null, null);
				return true;
			}
			if (firstLoadUrl)
				readWeb.clearHistory();
            view.loadUrl(url);
            return true;
		}
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			A.log("#onPageStarted : " + url);
			showProgressIndicator();
			if (firstLoadUrl)
				readWeb.clearHistory();
		}
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			A.log("#onPageFinished : " + url);
			hideProgressDlg();
			if (firstLoadUrl){
				firstLoadUrl = false;
				readWeb.clearHistory();
			}
		}
	}
*/

    private void showTipForFailedVipChapter() {
        if (!A.askForWebReading)
            return;

        if (A.ebook.online_site == S.store) {
            new MyDialog.Builder(this).setMessage("\n当前VIP章节优化阅读暂未成功, 请再次尝试")
                    .setPositiveButton(R.string.ok, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openVipBrowser(A.ebook.online_site.last_url);
                        }
                    }).setNegativeButton(A.fanti("原版浏览"), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    showWebReading();
                }
            }).show();
            return;
        }

        ScrollView layout = (ScrollView) LayoutInflater.from(this).inflate(R.layout.do_event_confirm, null);
        final TextView tv = (TextView) layout.findViewById(R.id.ofTextView);
        final CheckBox cb = (CheckBox) layout.findViewById(R.id.ofNeverAskAgain);
        tv.setText(Html.fromHtml(A.fanti("当前VIP章节优化阅读未成功, 建议切换到原版浏览. " +
                "<br><br><small><font color=\"#888888\"><b>提示:</b> 1.使用您在该平台的账号登录并订阅该VIP章节后, 可再切换回优化阅读模式<br>2.点工具栏右上的\"原版/优化\"可切换阅读模式</font></small>")));
        new MyDialog.Builder(this)
//				.setTitle("")
                .setView(layout)
                .setPositiveButton(A.fanti("原版浏览"), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        A.askForWebReading = !cb.isChecked();
                        showWebReading();
                    }
                }).setNegativeButton(R.string.cancel, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                A.askForWebReading = !cb.isChecked();
            }
        }).show();
    }

    private void showTipForYouHuaReading() {
        if (!startWithWebReading)
            return;
        if (!A.askForYouHua)
            return;
        if (youHuaReadingTipped)
            return;
        youHuaReadingTipped = true;

        ScrollView layout = (ScrollView) LayoutInflater.from(this).inflate(R.layout.do_event_confirm, null);
        final TextView tv = (TextView) layout.findViewById(R.id.ofTextView);
        final CheckBox cb = (CheckBox) layout.findViewById(R.id.ofNeverAskAgain);
        tv.setText(Html.fromHtml(A.fanti("现在是原版浏览模式, 是否尝试切换到优化阅读模式(重排文本以适应手机阅读)?" +
                "<br><br><small><font color=\"#888888\"><b>提示:</b> 1.点工具栏右上的\"原版/优化\"可切换阅读模式; <br>2.点击工具栏网址可用外部浏览器打开</font></small>")));
        new MyDialog.Builder(this)
//				.setTitle("")
                .setView(layout)
                .setPositiveButton(A.fanti("优化阅读"), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        A.askForYouHua = !cb.isChecked();
                        switchWebReading(false);
                        if (adSiteAlertDlg != null)
                            adSiteAlertDlg.dismiss();
                    }
                }).setNegativeButton(R.string.cancel, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                A.askForYouHua = !cb.isChecked();
            }
        }).show();
    }

    boolean showStoreButton() {
        if (A.storeLevel > 1)
            return false;
        if (S.store == null)
            return false;
        if (!isWebBook())
            return false;
        if (A.ebook.online_site == S.store)
            return false;
        if (!T.isFile(getStoreUrlFile()))
            return false;
        if (T.isNull(T.getFileText(getStoreUrlFile())))
            return false;
//		b_storeUrl.setText("  " + (A.ebook.online_site.primary() ? S.store.name() : "正版阅读") + "  ");
        b_storeUrl.setText("" + S.store.name() + "");
        return true;
    }

    String storeUrlBookFile;
    DownloadTaskBrowser.CacheWebView storeWeb;

    void cacheStoreUrl() {
        if (A.storeLevel > 2)
            return;
        if (S.store == null)
            return;
        if (!isWebBook())
            return;
        if (A.ebook.online_site == S.store)
            return;
        if (!A.isNetworkConnecting())
            return;
        if (T.isFile(getStoreUrlFile())) {
            if (T.isNull(T.getFileText(getStoreUrlFile()))) {
                if (System.currentTimeMillis() - T.getFileTime(getStoreUrlFile()) < T.day(10))
                    return;
            } else
                return;
        }

        if (A.lastFile.equals(storeUrlBookFile))
            return;
        storeUrlBookFile = A.lastFile;

        S.BookSite site = S.store;
        String key = S.encode(A.ebook.getBookName(), site.keyEncode);
        String url = site.searchUrl.replace("%s", key);
        String post = !T.isNull(site.postParams) ? site.postParams.replace("%s", key) : null;

        DownloadTask.Result result = new DownloadTask.Result();
        result.callback = new DownloadTask.Callback() {
            @Override
            public void onFinish(DownloadTask.Result result) {
                if (isFinishing())
                    return;
                result.site = S.store;
                handler.sendMessage(handler.obtainMessage(CACHE_STORE_URL_FINISH, result));
            }

            @Override
            public void onProgress(DownloadTask.Result result, int progress, int total) {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(DownloadTask.Result result, String err) {
                A.log("cacheStoreUrl error: " + err);
            }
        };

        if (storeWeb == null || storeWeb.destroyed)
            storeWeb = DownloadTaskBrowser.createDownloadWebView();
        storeWeb.delayed = site.searchDelay;
        storeWeb.stop = false;
        DownloadTask.download(storeWeb, result, true, 0, url, post, site.searchAgent);
    }

    private void doCacheStoreUrlFinished(DownloadTask.Result result) {
        if (storeWeb.stop)
            return;
        result.site.last_url = result.final_url;
        ArrayList<S.WebBook> items = S.getBookList(result.html, result.site);
        if (items.size() > 5)
            T.saveFileText(getStoreUrlFile(), ""); //先做个没有这本书的标记(如果没有这本书, 10天后再继续检查, 不用每次都查)
        A.log("doCacheStoreUrlFinished, books: " + items.size());

        for (S.WebBook item : items) {
            if (A.ebook.getBookName().equals(item.name) && A.ebook.getAuthor().equals(item.author)) {
                A.log("=====found it======= " + item.url);
                T.saveFileText(getStoreUrlFile(), item.url);
                WBpub.addSiteToSources(A.lastFile, item);
                break;
            }
        }
        storeWeb.stop = true;
        S.clearWebView(storeWeb);
    }

    private String getStoreUrlFile() {
        return S.getStoreUrlFile(A.ebook.getBookName(), A.ebook.getAuthor());
    }

    private void switchToStoreUrl() {
        if (!A.ebook.online_site.primary() && A.yueWenReadTip) {
            ScrollView layout = (ScrollView) LayoutInflater.from(this).inflate(R.layout.do_event_confirm, null);
            final TextView tv = (TextView) layout.findViewById(R.id.ofTextView);
            final CheckBox cb = (CheckBox) layout.findViewById(R.id.ofNeverAskAgain);
            cb.setChecked(true);
            tv.setTextSize(15);
            tv.setText(Html.fromHtml(A.fanti("切换到阅文书城阅读正版, 无错字更新快, 更可有效支持您喜欢的作者.")));
            new MyDialog.Builder(this)
                    .setView(layout)
                    .setPositiveButton(R.string.ok, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            A.yueWenReadTip = !cb.isChecked();
                            switchToStoreUrl2();
                        }
                    }).setNegativeButton(R.string.cancel, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    A.askEnableAutoDay = !cb.isChecked();
                    A.autoThemeDay = false;
                }
            }).setCancelable(false).show();
        } else
            switchToStoreUrl2();
    }

    private void switchToStoreUrl2() {
        switchToSource(S.store.siteTag, T.getFileText(getStoreUrlFile()), false);
    }

    long txtCachePos = -1;
    int txtCachePos2;

    private void saveTxtFileCache() {
        if (getBookType() != A.FILE_TXT)
            return;
        if (txtCachePos == A.lastPosition)
            return;
        MyLayout lo = txtView.getLayout();
        if (lo == null || !isTxtScrollReady)
            return;
        txtCachePos = A.lastPosition;
        txtCachePos2 = lo.getLineStart(lo.getLineForVertical(txtScroll.getScrollY()));
        String s = txtCachePos + "|" + txtCachePos2 + "@" + A.lastBlockIndex + "#"
                + txtView.getText().toString();
        if (s.length() < 1000)
            return;
        String filename = A.book_cache + "/" + A.lastFile.hashCode() + ".tc";
        T.saveFileText(filename, s);
    }

    private String getTxtFileCache() {
        String filename = A.book_cache + "/" + A.lastFile.hashCode() + ".tc";
        if (!T.isFile(filename))
            return null;
        String s = T.getFileText(filename);
        if (s.length() < 1000)
            return null;
        int i1 = s.indexOf("|");
        int i2 = s.indexOf("@");
        int i3 = s.indexOf("#");
        if (i1 == -1 || i2 < i1 || i3 < i2)
            return null;
        try {
            long cachePos = Long.valueOf(s.substring(0, i1));
            if (cachePos != A.lastPosition)
                return null;
            txtCachePos2 = Integer.valueOf(s.substring(i1 + 1, i2));
            A.lastBlockIndex = Integer.valueOf(s.substring(i2 + 1, i3));
            s = s.substring(i3 + 1);
            return s;
        } catch (Exception e) {
            A.error(e);
        }
        T.deleteFile(filename);
        return null;
    }

    private void addToBatteryIgnore() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        if (!A.askBatteryIgnore)
            return;

        try {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm.isIgnoringBatteryOptimizations(packageName))
                return;
            ScrollView layout = (ScrollView) LayoutInflater.from(this).inflate(R.layout.do_event_confirm, null);
            final TextView tv = layout.findViewById(R.id.ofTextView);
            final CheckBox cb = layout.findViewById(R.id.ofNeverAskAgain);
            cb.setChecked(false);
            tv.setText(Html.fromHtml(A.fanti("如果需要阅读器在息屏后继续朗读, 请在系统<b>电池优化</b>里, 把搜书大师加入到<b>白名单</b>中.")));
            new MyDialog.Builder(this)
                    .setTitle(R.string.tip)
                    .setView(layout)
                    .setPositiveButton(A.fanti("手动设置"), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            A.askBatteryIgnore = !cb.isChecked();
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                            startActivity(intent);
                        }
                    }).setNegativeButton(A.fanti("下次再说"), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    A.askBatteryIgnore = !cb.isChecked();
                }
            }).setCancelable(false).show();
        } catch (Exception e) {
            A.error(e);
        }
    }

    //-----------------------------------

    int bookCommentCount;
    boolean updatedBookHot;
    boolean hasShuPingPage;

    private String getBookName() {
        if (A.ebook != null)
            return A.ebook.getBookName();
        if (getBookType() == A.FILE_TXT) {
            String s = T.getOnlyFilename(A.lastFile);
            int i = s.indexOf(" - ");
            if (i > 0)
                return s.substring(0, i).trim();
        }
        return T.getOnlyFilename(A.lastFile);
    }

    private String getAuthor() {
        if (A.ebook != null)
            return A.ebook.getAuthor();
        if (getBookType() == A.FILE_TXT) {
            String s = T.getOnlyFilename(A.lastFile);
            int i = s.indexOf(" - ");
            if (i > 0)
                return s.substring(i + 3).trim();
        }
        return "";
    }

    private String getCurChapter() {
        if (A.ebook != null)
            return A.ebook.getChapters().get(A.lastChapter).name;
        if (getBookType() == A.FILE_TXT)
            if (!T.isNull(A.chapters))
                return getTxtChapterName();
        return "";
    }

    boolean commentCollected;

    void getBookCommentCount() {
        if (hasShuPingPage)
            z.getBookCommentCount(getBookName(), getAuthor(), new z.ResultInterface() {
                @Override
                public void done(boolean success, Object object) {
                    if (success && (object instanceof Integer)) {
                        bookCommentCount = (Integer) object;
                        A.log(getBookName() + " ---------- comments: " + bookCommentCount);
                        if (!commentCollected && bookCommentCount < z.LESS_COMMENT_COUNT) {
                            commentCollected = true;
                            autoCollectBookMetas(false, false);
                        } else if (z.shouldUpdateCollectTags(z.getTagsCollectedTime(getBookName(), getAuthor()))) {
                            autoCollectBookMetas(false, true);
                        }
                        showCommentCount();
                        if (!updatedBookHot) {
                            updatedBookHot = true;
                            z.uploadHot(getBookName(), getAuthor());
                        }
                    }
                }
            });
    }

    private void showCommentCount() {
//		if (bookCommentCount > 99)
//			bookCommentCount = 99;
        if (bookCommentCount < 0)
            bookCommentCount = 0;
        if (commentTv != null)
            commentTv.setText("" + bookCommentCount + (bookCommentCount < 10 ? " " : ""));
    }

    void autoCollectBookMetas(final boolean forceTestUpdate, final boolean collectTagsOnly) {
        if (!hasShuPingPage)
            return;

        //非调试状态, 三天内尝试过采集书评的书籍不再采集
        if (AutoCollect.alreadyCollectedRecently(getBookName(), getAuthor()))
            return;

        z.getBookMeta(getBookName(), getAuthor(), new z.ResultInterface() {
            @Override
            public void done(boolean success, Object obj) {
                A.log(obj);
                if (!success)
                    return;
                z.WWBookMeta book = (z.WWBookMeta) obj;
                if (forceTestUpdate) {
                    book.parentLay = baseFrame;
                    book.forTest = true;
                }
                book.onUpdateBookResult = new z.ResultInterface() {
                    @Override
                    public void done(boolean success, Object obj) {
                        if (success) {
                            z.WWBookMeta book = (z.WWBookMeta) obj;
                            if (book.comments.size() > 0)
                                handler.sendEmptyMessageDelayed(GET_BOOK_COMMENT, 100);
                            if (!T.isNull(book.coverUrl) && T.isNull(A.getBookCoverFile(A.lastFile)))
                                T.saveCoverUrlToFile(book.coverUrl, A.getCoverFileTag(A.lastFile) + A.NETCOVER_TAG, null);
                        }
                    }
                };
                if (book.original == null) {
                    book.original = new BookAndDiscuss();
                    book.original.setBookName(getBookName());
                    book.original.setBookAuthor(getAuthor());
                }
                if (collectTagsOnly) {
                    book.collectTagsOnly = true;
                    if (!z.shouldCollectBookTags(book.original))
                        return;
                }
                AutoCollect.autoCollect(book);
            }
        });
    }

    long tastReadRecord;
    static boolean task20, task40, task60;

    private void doTaskRead() {
        if (!AccountData.isLoaded())
            return;
//		if (A.taskReadCount >= 6) //todo: 暂时只执行两天, 然后取消
//			return;
        AccountData.readTaskShowToast = A.taskReadCount < 6;
        if (isPaused)
            return;
        if (A.taskReadDay != T.getTodayNumber()) {
            A.taskReadDay = T.getTodayNumber();
            A.taskReadTime = 0;
            tastReadRecord = System.currentTimeMillis();
            task20 = task40 = task60 = false;
        }
        if (tastReadRecord > 0) {
            A.taskReadTime += (System.currentTimeMillis() - tastReadRecord);
            if (!task20 && A.taskReadTime >= T.minute(20) && A.taskReadTime < T.minute(22)) {
                task20 = true;
                z.taskReade("20");
                A.taskReadCount++;
            }
            if (!task40 && A.taskReadTime >= T.minute(40) && A.taskReadTime < T.minute(42)) {
                task40 = true;
                z.taskReade("40");
                A.taskReadCount++;
            }
            if (!task60 && A.taskReadTime >= T.minute(60) && A.taskReadTime < T.minute(62)) {
                task60 = true;
                z.taskReade("60");
                A.taskReadCount++;
            }
        }
        tastReadRecord = System.currentTimeMillis();
    }

}
