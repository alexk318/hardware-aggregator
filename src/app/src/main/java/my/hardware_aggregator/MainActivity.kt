package my.hardware_aggregator

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import kotlinx.coroutines.flow.collect

import com.google.android.material.switchmaterial.SwitchMaterial

import my.hardware_aggregator.db.DBHelper
import my.hardware_aggregator.main.MainViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)

        val intent = Intent(this, ResultActivity::class.java)

        val buttonGetDataFromParser: Button = findViewById(R.id.button)
        val buttonGetDataFromDB: Button = findViewById(R.id.button2)

        val spinnerHardware: Spinner = findViewById(R.id.spinnerHardware)

        val switchShop: SwitchMaterial = findViewById(R.id.switchShop)
        val switchForcecom: SwitchMaterial = findViewById(R.id.switchTechnodom)
        val switchTomas : SwitchMaterial = findViewById(R.id.switchTomas)

        val dbManagerShop = DBHelper(applicationContext).DBManagerShop()
        val dbManagerForcecom = DBHelper(applicationContext).DBManagerForcecom()
        val dbManagerTomas = DBHelper(applicationContext).DBManagerTomas()

        buttonGetDataFromParser.setOnClickListener {
            viewModel.loadProductsFromParser(spinnerHardware.selectedItem.toString(),
                switchShop.isChecked,
                switchForcecom.isChecked,
                switchTomas.isChecked
            )
        }

        buttonGetDataFromDB.setOnClickListener {
            viewModel.getProductsFromDB(
                applicationContext,
                spinnerHardware.selectedItem.toString(),
            )
        }

        lifecycleScope.launchWhenStarted {

            viewModel.state.collect { event ->
                when (event) {

                    is MainViewModel.Event.SuccessParser -> {
                        val dataResponse = event.dataResponse

                        intent.putExtra("productsShop", dataResponse.shop)
                        intent.putExtra("productsForcecom", dataResponse.forcecom)
                        intent.putExtra("productsTomas", dataResponse.tomas)

                        if (switchShop.isChecked) {
                            dbManagerShop.deleteSpecificProducts(spinnerHardware.selectedItem.toString())
                            dbManagerShop.insertSpecificProductsFromList(spinnerHardware.selectedItem.toString(), dataResponse.shop)
                        }

                        if (switchForcecom.isChecked) {
                            dbManagerForcecom.deleteSpecificProducts(spinnerHardware.selectedItem.toString())
                            dbManagerForcecom.insertSpecificProductsFromList(spinnerHardware.selectedItem.toString(), dataResponse.forcecom)

                        }
                        if (switchTomas.isChecked) {
                            dbManagerTomas.deleteSpecificProducts(spinnerHardware.selectedItem.toString())
                            dbManagerTomas.insertSpecificProductsFromList(spinnerHardware.selectedItem.toString(), dataResponse.tomas)
                        }

                        buttonGetDataFromParser.isEnabled = true
                        buttonGetDataFromDB.isEnabled = true

                        startActivity(intent)
                    }

                    is MainViewModel.Event.SuccessDB -> {
                        val dataResponse = event.dataResponse

                        intent.putExtra("productsShop", dataResponse.shop)
                        intent.putExtra("productsForcecom", dataResponse.forcecom)
                        intent.putExtra("productsTomas", dataResponse.tomas)

                        buttonGetDataFromParser.isEnabled = true
                        buttonGetDataFromDB.isEnabled = true

                        startActivity(intent)
                    }

                    is MainViewModel.Event.Loading -> {
                        buttonGetDataFromParser.isEnabled = false
                        buttonGetDataFromDB.isEnabled = false
                    }

                    is MainViewModel.Event.Error -> {
                        Toast.makeText(applicationContext, event.message, Toast.LENGTH_SHORT).show()

                        buttonGetDataFromParser.isEnabled = true
                        buttonGetDataFromDB.isEnabled = true
                    }

                    else -> Unit
                }
            }

        }

    }
}