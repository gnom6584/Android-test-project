package com.example.testproject

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.testproject.databinding.ActivityMainBinding

private const val TOOLS_HIDE_ANIMATION_DURATION= 300L

fun debug(value: Any){
    Log.d("DEBUG", value.toString())
}

private val PETS = mutableListOf(
    PetModel("Кеша", PARROT_IMAGE_URL, 0),
    PetModel("Рекс", DOG_IMAGE_URL, 0),
    PetModel("Ти рекс", RAPTOR_IMAGE_URL, 0),
    PetModel("Просто кот", CAT_IMAGE_URL, 0),
    PetModel("Просто ёж", EJJIC_IMAGE_URL, 0)
)

fun openNative(currentActivity: Activity){
    currentActivity.startActivity(Intent(currentActivity, MainActivity::class.java))
}

class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding by lazy { DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)}
    val myTouchHelper by lazy{ MyItemTouchHelper()}

    fun startBuyActivity(){
        openUrlInWebViewActivity(this, MARKET_URL)
    }

    fun initActionDropButtons(){
        val baseColorRes = ContextCompat.getColor(baseContext, R.color.colorDragDropButtonBase)
        binding.apply {
            with(cancelButton) {
                baseColor = baseColorRes
                outlineColor = ContextCompat.getColor(baseContext, R.color.colorCancelCrossButton)
                onCompleteListeners.add{
                    myTouchHelper.removeFlag = true
                    Toast.makeText(baseContext, "Remove!", Toast.LENGTH_SHORT).show()
                }
                Glide
                    .with(this)
                    .load(CANCEL_BUTTON_URL)
                    .into(binding.cancelImage)
            }
            with(buyButton){
                baseColor = baseColorRes
                outlineColor = ContextCompat.getColor(baseContext, R.color.colorBuyButton)
                onCompleteListeners.add{
                    startBuyActivity()
                    Toast.makeText(baseContext, "Buy!", Toast.LENGTH_SHORT).show()
                }
                Glide
                    .with(this)
                    .load(BUY_BUTTON_URL)
                    .into(binding.buyImage)
            }
            with(likeButton) {
                baseColor = baseColorRes
                outlineColor = ContextCompat.getColor(baseContext, R.color.colorLikeButton)
                onCompleteListeners.add{
                    myTouchHelper.doWithSelectedHolder{
                        (it as PetsRecyclerAdapter.ViewHolder).dataSample?.likes =
                            it.dataSample?.likes!! + 1
                        it.binding?.likesText?.text = it.dataSample?.likes.toString()
                    }
                    Toast.makeText(baseContext, "Like!", Toast.LENGTH_SHORT).show()
                }
                Glide
                    .with(this)
                    .load(LIKE_BUTTON_URL)
                    .into(binding.likeImage)
            }
        }
    }

    private fun initActionBar(){
        Glide.with(this).asDrawable().load(APPLICATION_ICON_URL)
            .into(object : CustomTarget<Drawable?>() {
                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    if(supportActionBar != null) {
                        with(supportActionBar!!) {
                            setDisplayShowHomeEnabled(true)
                            setDisplayUseLogoEnabled(true)
                            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
                            setCustomView(R.layout.action_bar)
                            (customView as ViewGroup).getChildAt(0).background = resource
                        }
                    }
                }
            })
    }

    fun initRecycler(adapter: PetsRecyclerAdapter){
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
            isNestedScrollingEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActionBar()

        val adapter = PetsRecyclerAdapter(PETS)

        initRecycler(adapter)

        val itemHelper = ItemTouchHelper(myTouchHelper)
        itemHelper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.itemAnimator = DefaultItemAnimator()

        initActionDropButtons()

        myTouchHelper.onDragListener.add {
            with(binding) {
                buttonsContainer.animate().alpha(if (it) 1.0f else 0.0f).setDuration(TOOLS_HIDE_ANIMATION_DURATION).start()//похуй
                textView.animate().alpha(if (it) 0.0f else 1.0f).setDuration(TOOLS_HIDE_ANIMATION_DURATION).start()
            }

        }

        adapter.removeListeners.add {
            with(binding) {
                if (adapter.itemCount > 1) {
                    textView.text = getString(R.string.selectThePet)
                }
                else {
                    textView.text = getString(R.string.NoPets)
                }
            }
        }

        with(binding.root.viewTreeObserver) {
            addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    setMarginAndSnapHelper()
                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    fun setMarginAndSnapHelper(){
        binding.recyclerView.addItemDecoration(MyMarginItemDecoration(0.25f))
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if(myTouchHelper.drag) {
            binding.apply {
                cancelButton.receiveTouch(ev)
                buyButton.receiveTouch(ev)
                likeButton.receiveTouch(ev)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
