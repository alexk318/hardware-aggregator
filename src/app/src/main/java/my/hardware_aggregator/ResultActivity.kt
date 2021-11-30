package my.hardware_aggregator

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import my.hardware_aggregator.data.models.Product
import kotlin.collections.ArrayList

class ResultActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        setContentView(R.layout.activity_result)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        @Suppress("UNCHECKED_CAST")
        val productsShop = intent.getSerializableExtra("productsShop") as ArrayList<Product>
        var productsShopCopy = productsShop

        @Suppress("UNCHECKED_CAST")
        val productsForcecom = intent.getSerializableExtra("productsForcecom") as ArrayList<Product>
        var productsForcecomCopy = productsForcecom

        @Suppress("UNCHECKED_CAST")
        val productsTomas = intent.getSerializableExtra("productsTomas") as ArrayList<Product>
        var productsTomasCopy = productsTomas

        val productsStorePack = arrayListOf<ArrayList<Product>>(ArrayList(), ArrayList(), ArrayList())

        fun getCurrentProducts(): ArrayList<Product> {
            productsStorePack[0] = productsShopCopy
            productsStorePack[1] = productsForcecomCopy
            productsStorePack[2] = productsTomasCopy

            val allProducts = ArrayList<Product>()
            for (productsStore in productsStorePack) {
                allProducts.addAll(productsStore)
            }

            return ArrayList(allProducts.toList().sortedByDescending { it.cost })

        }


        val adapter = CustomRecyclerAdapter(getCurrentProducts(), getCurrentProducts(), this)
        recyclerView.adapter = adapter

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.navView)

        val inputSearch = navView.menu.findItem(R.id.inputSearch).actionView as EditText

        val checkBoxShop = navView.menu.findItem(R.id.checkBoxShop).actionView as CheckBox
        val checkBoxForcecom = navView.menu.findItem(R.id.checkBoxForcecom).actionView as CheckBox
        val checkBoxTomas = navView.menu.findItem(R.id.checkBoxTomas).actionView as CheckBox

        if (productsShop.size == 0) {
            checkBoxShop.isClickable = false
            checkBoxShop.isChecked = false
            checkBoxShop.setTextColor(Color.parseColor("#d3d3d3"))
        } else {
            checkBoxShop.text = getString(R.string.checkbox_active_shop_title, productsShop.size)
        }

        if (productsForcecom.size == 0) {
            checkBoxForcecom.isClickable = false
            checkBoxForcecom.isChecked = false
            checkBoxForcecom.setTextColor(Color.parseColor("#d3d3d3"))
        } else {
            checkBoxForcecom.text = getString(R.string.checkbox_active_forcecom_title, productsForcecom.size)
        }

        if (productsTomas.size == 0) {
            checkBoxTomas.isClickable = false
            checkBoxTomas.isChecked = false
            checkBoxTomas.setTextColor(Color.parseColor("#d3d3d3"))
        } else {
            checkBoxTomas.text = getString(R.string.checkbox_active_tomas_title, productsTomas.size)
        }

        val switchCostSort = navView.menu.findItem(R.id.switchCostSort).actionView as SwitchMaterial

        switchCostSort.setOnCheckedChangeListener {_, _ ->
            adapter.reverse()
        }

        checkBoxShop.setOnCheckedChangeListener { _, isChecked ->
            productsShopCopy = if (isChecked) { productsShop } else { ArrayList() }

            val currentProducts = getCurrentProducts()
            if (switchCostSort.isChecked) { currentProducts.reverse() }

            inputSearch.text.clear()

            adapter.setNewValues(currentProducts)
        }

        checkBoxForcecom.setOnCheckedChangeListener { _, isChecked ->
            productsForcecomCopy = if (isChecked) { productsForcecom } else { ArrayList() }

            val currentProducts = getCurrentProducts()
            if (switchCostSort.isChecked) { currentProducts.reverse() }

            inputSearch.text.clear()

            adapter.setNewValues(currentProducts)
        }

        checkBoxTomas.setOnCheckedChangeListener { _, isChecked ->
            productsTomasCopy = if (isChecked) { productsTomas } else { ArrayList() }

            val currentProducts = getCurrentProducts()
            if (switchCostSort.isChecked) { currentProducts.reverse() }

            inputSearch.text.clear()

            adapter.setNewValues(currentProducts)
        }

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        inputSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {adapter.filter.filter(p0)}
            override fun afterTextChanged(p0: Editable?) {}  })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


}
