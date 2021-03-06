package com.yusufcakmak.exomediasources

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_extractor_media_source.*

class LoopingMediaSourceActivity : AppCompatActivity() {

    private lateinit var player: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory

    private val trackSelectionFactory = AdaptiveTrackSelection.Factory()
    private var trackSelector: DefaultTrackSelector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_looping_media_source)
    }

    private fun initializePlayer() {
        trackSelector = DefaultTrackSelector(trackSelectionFactory)
        mediaDataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"))

        val dataSpec = DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.himym))
        val rawDataSource = RawResourceDataSource(this)
        rawDataSource.open(dataSpec)

        val mediaSource = ExtractorMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(rawDataSource.uri)
        // startPositionUs The start position within {@code mediaSource}'s window at which to start
        //  providing samples, in microseconds.
        val clippingMediaSource = ClippingMediaSource(mediaSource, 0, 3000000)
        val loopingMediaSource = LoopingMediaSource(clippingMediaSource)

/*        val mediaSource = ExtractorMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"))*/

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)

        with(player) {
            prepare(loopingMediaSource, false, false)
            playWhenReady = true
        }

        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        playerView.player = player
        playerView.requestFocus()
    }

    private fun releasePlayer() {
        player.release()
        trackSelector = null
    }

    public override fun onStart() {
        super.onStart()

        if (Util.SDK_INT > 23) initializePlayer()
    }

    public override fun onResume() {
        super.onResume()

        if (Util.SDK_INT <= 23) initializePlayer()
    }

    public override fun onPause() {
        super.onPause()

        if (Util.SDK_INT <= 23) releasePlayer()
    }

    public override fun onStop() {
        super.onStop()

        if (Util.SDK_INT > 23) releasePlayer()
    }
}