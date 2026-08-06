package ryo.myappcompany.fixingalegacyorderapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import ryo.myappcompany.fixingalegacyorderapp.R
import ryo.myappcompany.fixingalegacyorderapp.viewmodel.FlashSaleViewModel
import kotlinx.coroutines.launch

class FlashSaleActivity : AppCompatActivity() {

    private lateinit var viewModel: FlashSaleViewModel

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

        viewModel = ViewModelProvider(this)[FlashSaleViewModel::class.java]

        btnBuy.setOnClickListener {
            viewModel.onBuyClicked()
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                tvProductName.text = state.productName
                tvStock.text = "残り在庫: ${state.stock}個"

                btnBuy.isEnabled = state.stock > 0

                if (state.message.isNotEmpty()) {
                    Toast.makeText(this@FlashSaleActivity, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
