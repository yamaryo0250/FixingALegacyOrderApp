package ryo.myappcompany.fixingalegacyorderapp.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ryo.myappcompany.fixingalegacyorderapp.R
import ryo.myappcompany.fixingalegacyorderapp.viewmodel.FlashSaleViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FlashSaleActivity : AppCompatActivity() {

    companion object {
        val TAG: String = FlashSaleActivity::class.java.simpleName
    }

    private val viewModel: FlashSaleViewModel by viewModels()

    private lateinit var tvProductName: TextView
    private lateinit var tvStock: TextView
    private lateinit var btnBuy: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_sale)

        tvProductName = findViewById(R.id.tvProductName)
        tvStock = findViewById(R.id.tvStock)
        btnBuy = findViewById(R.id.btnBuy)
        progressBar = findViewById(R.id.progressBar)

        btnBuy.setOnClickListener {
            Log.d(TAG, "button clicked.")
            viewModel.onBuyClicked()
        }

        // UIを状態クラスで管理
        settingUiState()
    }

    /**
     * UIの状態クラスを設定
     *
     * UI表示内容をFlowで管理するための設定(collect等)
     */
    private fun settingUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                        tvProductName.text = state.productName
                        tvStock.text =
                            getString(R.string.msg_stock_quantity, state.stock.toString())

                        btnBuy.isEnabled = state.stock > 0 && !state.isLoading
                    }
                }

                launch {
                    viewModel.purchaseEvent.collect { event ->
                        Toast.makeText(this@FlashSaleActivity, getString(event.message), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
