package idn.faza.happyplace

import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import idn.faza.Adapter.HappyPlacesAdapter
import idn.faza.Database.DatabaseHandler
import idn.faza.model.HappyPlacesModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    companion object{
        private const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        val EXTRA_PLACE_DETAIL = "extra_place_details"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab_add_happy_place.setOnClickListener {
           val intent = Intent(this@MainActivity,
               AddHappyPlaceActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE) // disini coba
        }

        getHappyPlaceListFromLocalDB()
    }

    private fun getHappyPlaceListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val getHappyPlaceList = dbHandler.getHappyPlaceList()

        if (getHappyPlaceList.size > 0) {
            rv_happy_palace_list.visibility = View.VISIBLE
            tv_no_records.visibility = View.GONE
      setupHappyPlaceRecyclerView(getHappyPlaceList)
        }else{
            rv_happy_palace_list.visibility = View.GONE
            tv_no_records.visibility = View.VISIBLE
        }
    }

    private fun setupHappyPlaceRecyclerView(happyPlaceList: ArrayList<HappyPlacesModel>) {
        rv_happy_palace_list.layoutManager = LinearLayoutManager(this)
        rv_happy_palace_list.setHasFixedSize(true)

        val placesAdapter = HappyPlacesAdapter(this, happyPlaceList)
        rv_happy_palace_list.adapter = placesAdapter

        placesAdapter.setOnClickListener(object : HappyPlacesAdapter.OnClickListener{
            override fun onClick(position: Int, data: HappyPlacesModel) {
                val intent = Intent(this@MainActivity, HappyDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAIL, data) // mengirim data ke DetailActivity
                startActivity(intent)
            }
        })
        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_happy_palace_list.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(
                    this@MainActivity,
                    viewHolder.adapterPosition,
                    ADD_PLACE_ACTIVITY_REQUEST_CODE
                )

            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rv_happy_palace_list)

        val deleteSwipeHandler = object : AddSwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_happy_palace_list.adapter as HappyPlacesAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                getHappyPlaceListFromLocalDB()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rv_happy_palace_list)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                getHappyPlaceListFromLocalDB()
                } else {
                Log.e("Activity", "Canceled or Back Pressed")
            }
        }
    }
}