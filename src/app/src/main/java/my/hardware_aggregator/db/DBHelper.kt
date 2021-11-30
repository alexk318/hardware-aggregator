package my.hardware_aggregator.db

import android.content.Context
import android.content.ContentValues


import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import my.hardware_aggregator.data.models.Product

class DBHelper(context: Context) : SQLiteOpenHelper(context, "Parser.db", null, 1) {

    override fun onCreate(DB: SQLiteDatabase) {
        DB.execSQL("create Table ProductShop(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT, description TEXT, cost INT, image_url TEXT, url TEXT, hardware_type TEXT )")
        DB.execSQL("create Table ProductForcecom(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT, description TEXT, cost INT, image_url TEXT, url TEXT, hardware_type TEXT )")
        DB.execSQL("create Table ProductTomas(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT, description TEXT, cost INT, image_url TEXT, url TEXT, hardware_type TEXT )")
    }

    override fun onUpgrade(DB: SQLiteDatabase, p1: Int, p2: Int) {
        DB.execSQL("drop Table if exists ProductShop")
        DB.execSQL("drop Table if exists ProductForcecom")
        DB.execSQL("drop Table if exists ProductTomas")
    }


    abstract inner class DBManager {
        abstract var table: String

        private val db: SQLiteDatabase = this@DBHelper.writableDatabase

        fun getSpecificProducts(hardwareType: String): ArrayList<Product> {
            val products = ArrayList<Product>()

            val cursor = db.rawQuery("SELECT * FROM $table WHERE hardware_type='${hardwareType}'", null)

            while (cursor.moveToNext()) {
                products.add(Product(cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5)))
            }

            cursor.close()

            return products

        }

        fun insertSpecificProductsFromList(hardwareType: String, products: List<Product>?) {
            if (products != null) {
                for (product in products) {

                    val contentValues = ContentValues()

                    if (product.image_url != null) {
                        contentValues.put("image_url", product.image_url)
                    } else {
                        contentValues.put("image_url", "")
                    }

                    if (product.title != null) {
                        contentValues.put("title", product.title)
                    } else {
                        contentValues.put("title", "")
                    }

                    if (product.description != null) {
                        contentValues.put("description", product.description)
                    } else {
                        contentValues.put("description", "")
                    }

                    if (product.cost != null) {
                        contentValues.put("cost", product.cost)
                    } else {
                        contentValues.put("cost", "")
                    }

                    if (product.url != null) {
                        contentValues.put("url", product.url)
                    } else {
                        contentValues.put("url", "")
                    }

                    contentValues.put("hardware_type", hardwareType)

                    db.insert(table, null, contentValues)
                }
            }
        }

        fun deleteSpecificProducts(hardwareType: String) {
            db.delete(table, "hardware_type='${hardwareType}'", null)
        }

    }

    inner class DBManagerShop : DBManager() { override var table = "ProductShop" }
    inner class DBManagerForcecom : DBManager() { override var table = "ProductForcecom" }
    inner class DBManagerTomas : DBManager() { override var table = "ProductTomas" }
}